package com.netboxy.vpn

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class VpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var vpnJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        var isRunning = false
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "NetBoxyVPN"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val configJson = intent?.getStringExtra("config")
        
        if (configJson != null) {
            try {
                val config = Gson().fromJson(configJson, VlessConfig::class.java)
                startVpn(config)
            } catch (e: Exception) {
                e.printStackTrace()
                stopSelf()
            }
        }
        
        return START_STICKY
    }

    private fun startVpn(config: VlessConfig) {
        try {
            // Create VPN interface
            val builder = Builder()
            builder.setSession("NetBoxy VPN")
            builder.addAddress("10.0.0.2", 24)
            builder.addRoute("0.0.0.0", 0)
            builder.addDnsServer("8.8.8.8")
            builder.addDnsServer("8.8.4.4")
            
            // Set MTU
            builder.setMtu(1500)
            
            // Establish VPN
            vpnInterface = builder.establish()
            
            if (vpnInterface != null) {
                isRunning = true
                startForeground(NOTIFICATION_ID, createNotification("Connected to ${config.server}"))
                
                // Start packet forwarding
                vpnJob = scope.launch {
                    forwardPackets(config)
                }
            } else {
                stopSelf()
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private suspend fun forwardPackets(config: VlessConfig) {
        try {
            val vpnInput = FileInputStream(vpnInterface!!.fileDescriptor)
            val vpnOutput = FileOutputStream(vpnInterface!!.fileDescriptor)
            
            // Create connection to VPN server
            val tunnel = DatagramChannel.open()
            tunnel.connect(InetSocketAddress(config.server, config.port.toInt()))
            
            val packet = ByteBuffer.allocate(32767)
            
            while (isRunning && !Thread.currentThread().isInterrupted) {
                packet.clear()
                
                // Read from VPN interface
                val length = vpnInput.read(packet.array())
                if (length > 0) {
                    packet.limit(length)
                    
                    // Forward to VPN server (simplified - real implementation needs VLESS protocol)
                    tunnel.write(packet)
                    
                    // Read response
                    packet.clear()
                    tunnel.read(packet)
                    
                    // Write back to VPN interface
                    if (packet.position() > 0) {
                        vpnOutput.write(packet.array(), 0, packet.position())
                    }
                }
            }
            
            tunnel.close()
            vpnInput.close()
            vpnOutput.close()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NetBoxy VPN Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for NetBoxy VPN connection status"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(message: String): android.app.Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NetBoxy VPN")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_vpn)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        
        vpnJob?.cancel()
        vpnInterface?.close()
        vpnInterface = null
        
        scope.cancel()
        
        stopForeground(true)
    }

    override fun onRevoke() {
        super.onRevoke()
        stopSelf()
    }
}
