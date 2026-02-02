# 📱 الدليل السريع - NetBoxy VPN على Termux

## ⚡ 3 خطوات فقط!

---

### 🔰 الخطوة 1: تثبيت Termux

**حمّل من هنا فقط:**
```
🔗 https://f-droid.org/en/packages/com.termux/
```

❌ **لا تحمل من Google Play Store!**

---

### 🔧 الخطوة 2: تشغيل السكريبت التلقائي

بعد فتح Termux، انسخ والصق الأوامر التالية واحد تلو الآخر:

```bash
# تحديث الحزم
pkg update -y && pkg upgrade -y

# منح صلاحية التخزين (اضغط السماح!)
termux-setup-storage

# تثبيت الأدوات الأساسية
pkg install openjdk-17 gradle git wget unzip -y

# انتقل لمجلد التحميلات
cd /storage/emulated/0/Download

# فك ضغط المشروع
unzip NetBoxyVPN-GitHub.zip -d ~/

# انتقل للمشروع
cd ~/NetBoxyVPN

# منح الصلاحيات
chmod +x gradlew
chmod +x install-termux.sh

# بناء APK!
./gradlew assembleDebug
```

---

### 📱 الخطوة 3: تثبيت APK

بعد اكتمال البناء:

```bash
# نسخ APK لمجلد التحميلات
cp app/build/outputs/apk/debug/app-debug.apk /storage/emulated/0/Download/NetBoxy.apk
```

ثم:
1. افتح مدير الملفات
2. اذهب لمجلد "التحميلات"
3. ابحث عن `NetBoxy.apk`
4. اضغط عليه → تثبيت
5. استمتع! 🎉

---

## 🚀 للبناء مرة أخرى (سريع!)

```bash
cd ~/NetBoxyVPN
./gradlew clean assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk /storage/emulated/0/Download/NetBoxy-NEW.apk
```

---

## 📋 أوامر مفيدة

### عرض مسار APK:
```bash
ls -lh app/build/outputs/apk/debug/
```

### تنظيف المشروع:
```bash
./gradlew clean
```

### بناء سريع (بدون إنترنت):
```bash
./gradlew assembleDebug --offline
```

---

## ⏱️ الوقت المتوقع:

- **التثبيت الأول:** 10 دقائق
- **البناء الأول:** 20-30 دقيقة
- **بناء تالي:** 3-5 دقائق فقط!

---

## ⚠️ نصائح مهمة:

1. ✅ استخدم WiFi (أسرع من 4G)
2. ✅ شغّل الهاتف على الشاحن
3. ✅ أغلق التطبيقات الأخرى
4. ✅ تأكد من مساحة فارغة 2GB+

---

## 🐛 حل المشاكل:

### ❌ "Permission denied"
```bash
chmod +x gradlew
```

### ❌ "BUILD FAILED"
```bash
./gradlew clean
./gradlew assembleDebug --stacktrace
```

### ❌ بطء شديد
- أعد تشغيل الهاتف
- استخدم WiFi
- احذف cache:
```bash
rm -rf ~/.gradle/caches/
```

---

## 📞 روابط مفيدة:

- **Termux Wiki:** https://wiki.termux.com
- **F-Droid:** https://f-droid.org
- **Gradle Docs:** https://gradle.org

---

## ✅ Checklist:

- [ ] تثبيت Termux من F-Droid
- [ ] تشغيل `pkg update`
- [ ] منح صلاحية التخزين
- [ ] تثبيت Java & Gradle
- [ ] فك ضغط المشروع
- [ ] `chmod +x gradlew`
- [ ] `./gradlew assembleDebug`
- [ ] نسخ APK للتحميلات
- [ ] تثبيت وتشغيل!

---

🎉 **مبروك! أنت الآن مطور Android بدون كمبيوتر!**
