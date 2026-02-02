# 📱 دليل بناء NetBoxy VPN عبر Termux

## 🎯 بناء تطبيق Android VPN مباشرة من هاتفك!

---

## 📋 المتطلبات:

- هاتف Android (4GB RAM أو أكثر يفضل)
- مساحة فارغة: ~2GB
- اتصال إنترنت جيد
- صبر (أول مرة تأخذ وقت 😊)

---

## 🚀 الخطوة 1: تثبيت Termux

### ⚠️ مهم جداً: لا تحمل من Google Play!

**حمّل من F-Droid فقط:**

1. اذهب إلى: https://f-droid.org/en/packages/com.termux/
2. أو حمّل F-Droid أولاً من: https://f-droid.org
3. ثم ابحث عن **Termux** وثبّته

**أو حمّل APK مباشرة:**
- https://github.com/termux/termux-app/releases/latest
- حمّل ملف: `termux-app_vX.X.X+github-debug_universal.apk`

---

## 🔧 الخطوة 2: إعداد Termux

### افتح Termux وأدخل الأوامر التالية:

```bash
# 1. تحديث الحزم
pkg update -y && pkg upgrade -y

# 2. منح صلاحية الوصول للتخزين
termux-setup-storage
```

**سيطلب منك إذن - اضغط "السماح"!**

انتظر حتى تكتمل... (دقيقة أو دقيقتين)

---

## 📦 الخطوة 3: تثبيت الأدوات المطلوبة

```bash
# تثبيت Java 17
pkg install openjdk-17 -y

# تثبيت Gradle
pkg install gradle -y

# تثبيت أدوات إضافية
pkg install git wget unzip -y

# التحقق من التثبيت
java -version
gradle -v
```

**يجب أن ترى:**
- Java version 17.x.x
- Gradle 8.x

---

## 📥 الخطوة 4: تحضير المشروع

### الطريقة 1: من ملف ZIP (الأسهل)

```bash
# انتقل إلى مجلد التحميلات
cd /storage/emulated/0/Download

# إذا كان الملف موجود هنا
ls -la NetBoxy*

# فك الضغط
unzip NetBoxyVPN-GitHub.zip -d ~/

# انتقل للمشروع
cd ~/NetBoxyVPN
```

### الطريقة 2: من GitHub

```bash
# استنساخ المشروع
cd ~
git clone https://github.com/YOUR_USERNAME/NetBoxyVPN.git

# انتقل للمشروع
cd NetBoxyVPN
```

---

## 🔨 الخطوة 5: بناء APK

```bash
# تأكد أنك في مجلد المشروع
pwd
# يجب أن يظهر: /data/data/com.termux/files/home/NetBoxyVPN

# منح صلاحية التنفيذ
chmod +x gradlew

# بناء APK Debug (للتجربة)
./gradlew assembleDebug

# أو بناء Release
./gradlew assembleRelease
```

### ⏱️ الوقت المتوقع:
- **أول مرة:** 15-30 دقيقة (تحميل dependencies)
- **المرات التالية:** 3-5 دقائق فقط!

---

## 📂 الخطوة 6: العثور على APK

بعد اكتمال البناء:

```bash
# نسخ APK لمجلد التحميلات
cp app/build/outputs/apk/debug/app-debug.apk /storage/emulated/0/Download/NetBoxy.apk

# أو عرض المسار
ls -lh app/build/outputs/apk/debug/
```

**ستجد الملف في:**
```
/storage/emulated/0/Download/NetBoxy.apk
```

---

## 📱 الخطوة 7: تثبيت APK

1. افتح مدير الملفات
2. اذهب إلى مجلد **التحميلات** (Downloads)
3. ابحث عن `NetBoxy.apk`
4. اضغط عليه
5. اضغط **تثبيت**
6. إذا طلب إذن، فعّل "تثبيت من مصادر غير معروفة"

---

## 🎨 تخصيص المشروع

### تعديل ملف index.html:

```bash
# استخدم محرر nano
nano app/src/main/assets/index.html

# أو محرر vi
vi app/src/main/assets/index.html
```

**للحفظ في nano:**
- `Ctrl + X`
- `Y`
- `Enter`

**بعد التعديل، أعد البناء:**
```bash
./gradlew assembleDebug
```

### تغيير اسم التطبيق:

```bash
nano app/src/main/res/values/strings.xml
```

غيّر:
```xml
<string name="app_name">NetBoxy</string>
```

### تغيير Package Name:

```bash
nano app/build.gradle
```

غيّر:
```gradle
applicationId "com.netboxy.vpn"
```

---

## 🔄 سكريبت سريع للبناء

احفظ هذا السكريبت لتسهيل العملية:

```bash
# إنشاء سكريبت بناء سريع
cat > ~/build-netboxy.sh << 'EOF'
#!/data/data/com.termux/files/usr/bin/bash

echo "🚀 بناء NetBoxy VPN..."

cd ~/NetBoxyVPN

echo "🔨 تنظيف المشروع..."
./gradlew clean

echo "📦 بناء APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ نجح البناء!"
    
    # نسخ إلى التحميلات
    cp app/build/outputs/apk/debug/app-debug.apk /storage/emulated/0/Download/NetBoxy-$(date +%Y%m%d-%H%M%S).apk
    
    echo "📱 تم حفظ APK في مجلد التحميلات!"
    ls -lh /storage/emulated/0/Download/NetBoxy*.apk | tail -1
else
    echo "❌ فشل البناء!"
    exit 1
fi
EOF

# منح صلاحية التنفيذ
chmod +x ~/build-netboxy.sh
```

**للاستخدام:**
```bash
~/build-netboxy.sh
```

---

## ⚡ أوامر مفيدة

### تنظيف المشروع:
```bash
./gradlew clean
```

### بناء سريع:
```bash
./gradlew assembleDebug --offline
```

### حذف cache إذا واجهت مشاكل:
```bash
rm -rf ~/.gradle/caches/
./gradlew clean assembleDebug
```

### التحقق من الأخطاء:
```bash
./gradlew assembleDebug --stacktrace --info
```

---

## 🐛 حل المشاكل الشائعة

### ❌ المشكلة: "Permission denied"
```bash
chmod +x gradlew
```

### ❌ المشكلة: "Out of memory"
أضف إلى `gradle.properties`:
```bash
echo "org.gradle.jvmargs=-Xmx1024m" >> gradle.properties
```

### ❌ المشكلة: "SDK not found"
```bash
# Gradle سيحمل SDK تلقائياً
./gradlew assembleDebug
```

### ❌ المشكلة: "BUILD FAILED"
```bash
# اقرأ الأخطاء بدقة
./gradlew assembleDebug --stacktrace

# أو نظف وأعد البناء
./gradlew clean
./gradlew assembleDebug
```

### ❌ المشكلة: Termux بطيء جداً
- أغلق التطبيقات الأخرى
- استخدم WiFi بدل 4G
- أعد تشغيل الهاتف
- تأكد من مساحة كافية

---

## 🎯 نصائح للأداء الأفضل

### 1. استخدم Gradle Daemon:
```bash
echo "org.gradle.daemon=true" >> gradle.properties
```

### 2. بناء موازي:
```bash
echo "org.gradle.parallel=true" >> gradle.properties
```

### 3. تفعيل Cache:
```bash
echo "org.gradle.caching=true" >> gradle.properties
```

### 4. تحديد عدد العمال:
```bash
echo "org.gradle.workers.max=2" >> gradle.properties
```

---

## 📊 مراقبة التقدم

### عرض حجم APK:
```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

### عرض سجل البناء:
```bash
cat build.log
```

### مراقبة استخدام المساحة:
```bash
du -sh ~/.gradle
du -sh ~/NetBoxyVPN
```

---

## 🔐 توقيع APK (اختياري)

### إنشاء Keystore:
```bash
keytool -genkey -v -keystore ~/netboxy.keystore \
  -alias netboxy -keyalg RSA -keysize 2048 -validity 10000
```

### توقيع APK:
```bash
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore ~/netboxy.keystore \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  netboxy
```

---

## 📱 تطبيقات مساعدة في Termux

### Termux:API (للمزيد من الميزات):
```bash
pkg install termux-api -y
```

### محرر نصوص أفضل:
```bash
pkg install nano vim -y
```

### أدوات تطوير إضافية:
```bash
pkg install python nodejs -y
```

---

## ✅ Checklist سريع

- [ ] تثبيت Termux من F-Droid
- [ ] منح صلاحية التخزين
- [ ] تثبيت Java 17
- [ ] تثبيت Gradle
- [ ] فك ضغط المشروع
- [ ] chmod +x gradlew
- [ ] ./gradlew assembleDebug
- [ ] نسخ APK للتحميلات
- [ ] تثبيت APK
- [ ] تجربة التطبيق!

---

## 🎓 أوامر Termux مفيدة

```bash
# تحديث جميع الحزم
pkg upgrade -y

# تنظيف Cache
pkg clean

# البحث عن حزمة
pkg search اسم_الحزمة

# إلغاء تثبيت حزمة
pkg uninstall اسم_الحزمة

# عرض الحزم المثبتة
pkg list-installed

# الخروج من Termux
exit
```

---

## 🚀 بناء سريع بأمر واحد!

بعد أول مرة، استخدم:

```bash
cd ~/NetBoxyVPN && ./gradlew clean assembleDebug && cp app/build/outputs/apk/debug/app-debug.apk /storage/emulated/0/Download/NetBoxy-NEW.apk && echo "✅ تم! ابحث عن NetBoxy-NEW.apk في التحميلات"
```

---

## 💡 نصيحة أخيرة

**شغّل الهاتف على الشاحن أثناء البناء!** ⚡

البناء يستهلك بطارية، خاصة في أول مرة.

---

## 🎉 مبروك!

الآن أنت قادر على:
- ✅ بناء APK من هاتفك مباشرة
- ✅ تعديل الكود وإعادة البناء
- ✅ إنشاء تطبيقات Android بدون كمبيوتر!

---

## 📞 الدعم

إذا واجهت مشاكل:
1. اقرأ الأخطاء في السجل
2. ابحث عن الخطأ في Google
3. تأكد من تثبيت كل الحزم صحيحاً

---

**استمتع ببناء التطبيقات من هاتفك! 🚀**
