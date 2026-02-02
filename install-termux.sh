#!/data/data/com.termux/files/usr/bin/bash

# 🚀 سكريبت تثبيت NetBoxy VPN على Termux
# النسخة: 1.0

echo "╔════════════════════════════════════════╗"
echo "║   🚀 NetBoxy VPN - Termux Installer   ║"
echo "╚════════════════════════════════════════╝"
echo ""

# ألوان للنصوص
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# دالة للطباعة بالألوان
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# التحقق من Termux
if [ ! -d "/data/data/com.termux" ]; then
    print_error "هذا السكريبت يعمل على Termux فقط!"
    exit 1
fi

print_info "بدء التثبيت..."
echo ""

# 1. تحديث الحزم
print_info "خطوة 1/6: تحديث الحزم..."
pkg update -y > /dev/null 2>&1
pkg upgrade -y > /dev/null 2>&1
print_success "تم تحديث الحزم"

# 2. تثبيت Java
print_info "خطوة 2/6: تثبيت Java 17..."
if ! command -v java &> /dev/null; then
    pkg install openjdk-17 -y > /dev/null 2>&1
    print_success "تم تثبيت Java 17"
else
    print_success "Java مثبت مسبقاً"
fi

# 3. تثبيت Gradle
print_info "خطوة 3/6: تثبيت Gradle..."
if ! command -v gradle &> /dev/null; then
    pkg install gradle -y > /dev/null 2>&1
    print_success "تم تثبيت Gradle"
else
    print_success "Gradle مثبت مسبقاً"
fi

# 4. تثبيت أدوات إضافية
print_info "خطوة 4/6: تثبيت الأدوات الإضافية..."
pkg install git wget unzip -y > /dev/null 2>&1
print_success "تم تثبيت الأدوات"

# 5. طلب صلاحية التخزين
print_info "خطوة 5/6: طلب صلاحية الوصول للتخزين..."
termux-setup-storage
sleep 2
print_success "تم منح الصلاحيات"

# 6. إنشاء مجلد المشاريع
print_info "خطوة 6/6: إنشاء مجلد المشاريع..."
mkdir -p ~/projects
print_success "تم إنشاء مجلد المشاريع"

echo ""
print_success "اكتمل التثبيت بنجاح! 🎉"
echo ""

# عرض الإصدارات
echo "════════════════════════════════════════"
echo "📦 الحزم المثبتة:"
echo "════════════════════════════════════════"
java -version 2>&1 | head -1
gradle -v | head -1
echo "════════════════════════════════════════"
echo ""

# تعليمات الاستخدام
print_info "الخطوات التالية:"
echo ""
echo "1️⃣  انسخ مجلد NetBoxyVPN إلى:"
echo "   /storage/emulated/0/Download/"
echo ""
echo "2️⃣  ثم نفذ الأوامر التالية:"
echo "   cd /storage/emulated/0/Download"
echo "   unzip NetBoxyVPN-GitHub.zip -d ~/"
echo "   cd ~/NetBoxyVPN"
echo "   chmod +x gradlew"
echo "   ./gradlew assembleDebug"
echo ""
echo "3️⃣  أو استخدم السكريبت السريع:"
echo "   bash ~/build-netboxy.sh"
echo ""

# إنشاء سكريبت بناء سريع
print_info "إنشاء سكريبت بناء سريع..."

cat > ~/build-netboxy.sh << 'SCRIPT_EOF'
#!/data/data/com.termux/files/usr/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}════════════════════════════════════════${NC}"
echo -e "${BLUE}   🚀 بناء NetBoxy VPN${NC}"
echo -e "${BLUE}════════════════════════════════════════${NC}"
echo ""

if [ ! -d ~/NetBoxyVPN ]; then
    echo -e "${RED}❌ مجلد المشروع غير موجود!${NC}"
    echo ""
    echo "يرجى نسخ المشروع أولاً:"
    echo "  cd /storage/emulated/0/Download"
    echo "  unzip NetBoxyVPN-GitHub.zip -d ~/"
    exit 1
fi

cd ~/NetBoxyVPN

echo -e "${BLUE}🧹 تنظيف المشروع...${NC}"
./gradlew clean > /dev/null 2>&1

echo -e "${BLUE}🔨 بناء APK...${NC}"
echo "⏱️  قد يستغرق هذا بضع دقائق..."
echo ""

./gradlew assembleDebug --console=plain

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}════════════════════════════════════════${NC}"
    echo -e "${GREEN}   ✅ نجح البناء!${NC}"
    echo -e "${GREEN}════════════════════════════════════════${NC}"
    
    TIMESTAMP=$(date +%Y%m%d-%H%M%S)
    OUTPUT_NAME="NetBoxy-${TIMESTAMP}.apk"
    
    cp app/build/outputs/apk/debug/app-debug.apk "/storage/emulated/0/Download/${OUTPUT_NAME}"
    
    echo ""
    echo -e "${GREEN}📱 تم حفظ APK:${NC}"
    echo "   /storage/emulated/0/Download/${OUTPUT_NAME}"
    echo ""
    
    APK_SIZE=$(ls -lh "/storage/emulated/0/Download/${OUTPUT_NAME}" | awk '{print $5}')
    echo -e "${BLUE}📊 حجم الملف: ${APK_SIZE}${NC}"
    echo ""
    echo -e "${GREEN}🎉 يمكنك الآن تثبيت التطبيق!${NC}"
else
    echo ""
    echo -e "${RED}════════════════════════════════════════${NC}"
    echo -e "${RED}   ❌ فشل البناء!${NC}"
    echo -e "${RED}════════════════════════════════════════${NC}"
    echo ""
    echo "للمزيد من التفاصيل نفذ:"
    echo "  ./gradlew assembleDebug --stacktrace"
    exit 1
fi
SCRIPT_EOF

chmod +x ~/build-netboxy.sh
print_success "تم إنشاء سكريبت البناء السريع"

echo ""
print_success "جميع الأدوات جاهزة! 🎊"
echo ""
print_warning "تذكر: البناء لأول مرة قد يستغرق 15-30 دقيقة"
print_info "المرات التالية ستكون أسرع (3-5 دقائق فقط)"
echo ""
