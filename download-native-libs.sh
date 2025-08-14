#!/bin/bash

# Fast Image Java - Native Libraries Download Script
# 从Rust项目的GitHub Release下载最新的原生库

set -e

RUST_REPO="lihongjie0209/fast-image"
NATIVE_DIR="src/main/resources/native"
DOWNLOAD_DIR="$NATIVE_DIR"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Fast Image Java - Native Libraries Downloader${NC}"
echo "=================================================="

# 解析参数
VERSION="latest"
if [ $# -gt 0 ]; then
    VERSION="$1"
fi

echo -e "${YELLOW}📦 Target Version: $VERSION${NC}"

# 检查必要工具
command -v curl >/dev/null 2>&1 || { echo -e "${RED}❌ curl is required but not installed.${NC}" >&2; exit 1; }
command -v jq >/dev/null 2>&1 || { echo -e "${RED}❌ jq is required but not installed.${NC}" >&2; exit 1; }

# 获取版本信息
if [ "$VERSION" = "latest" ]; then
    echo -e "${BLUE}🔍 Fetching latest release info...${NC}"
    VERSION=$(curl -s "https://api.github.com/repos/$RUST_REPO/releases/latest" | jq -r .tag_name)
    if [ "$VERSION" = "null" ] || [ -z "$VERSION" ]; then
        echo -e "${RED}❌ Failed to fetch latest version${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}✅ Using version: $VERSION${NC}"

# 创建目录
echo -e "${BLUE}📁 Creating native libraries directory...${NC}"
mkdir -p "$DOWNLOAD_DIR"

# 定义所有平台的原生库
declare -a PLATFORMS=(
    "fast_image-windows-x86_64.dll"
    "fast_image-windows-aarch64.dll"
    "libfast_image-linux-x86_64.so"
    "libfast_image-linux-aarch64.so"
    "libfast_image-macos-x86_64.dylib"
    "libfast_image-macos-aarch64.dylib"
)

# 下载计数器
downloaded=0
failed=0

echo -e "${BLUE}⬇️  Downloading native libraries...${NC}"

# 下载每个平台的库
for platform in "${PLATFORMS[@]}"; do
    echo -n "  📥 $platform ... "
    
    if curl -L -f -s \
        -H "Accept: application/octet-stream" \
        -o "$DOWNLOAD_DIR/$platform" \
        "https://github.com/$RUST_REPO/releases/download/$VERSION/$platform" 2>/dev/null; then
        
        # 检查文件大小
        size=$(wc -c < "$DOWNLOAD_DIR/$platform" 2>/dev/null || echo "0")
        if [ "$size" -gt 1000 ]; then  # 至少1KB
            echo -e "${GREEN}✅ ($(numfmt --to=iec $size))${NC}"
            ((downloaded++))
        else
            echo -e "${RED}❌ (file too small)${NC}"
            rm -f "$DOWNLOAD_DIR/$platform"
            ((failed++))
        fi
    else
        echo -e "${RED}❌ (download failed)${NC}"
        ((failed++))
    fi
done

echo ""
echo "📊 Download Summary:"
echo "  ✅ Successfully downloaded: $downloaded files"
echo "  ❌ Failed downloads: $failed files"

# 显示下载的文件
if [ $downloaded -gt 0 ]; then
    echo ""
    echo -e "${GREEN}📁 Downloaded files:${NC}"
    find "$DOWNLOAD_DIR" -type f \( -name "*.dll" -o -name "*.so" -o -name "*.dylib" \) -exec ls -lh {} \; | \
        awk '{printf "  %s %s (%s)\n", $9, $NF, $5}'
fi

# 验证当前平台的库
echo ""
echo -e "${BLUE}🔍 Verifying platform compatibility...${NC}"

case "$(uname -s)" in
    CYGWIN*|MINGW*|MSYS*)
        if [ -f "$DOWNLOAD_DIR/fast_image-windows-x86_64.dll" ]; then
            echo -e "${GREEN}✅ Windows x64 library available${NC}"
        else
            echo -e "${YELLOW}⚠️  Windows x64 library missing${NC}"
        fi
        ;;
    Linux*)
        if [ -f "$DOWNLOAD_DIR/libfast_image-linux-x86_64.so" ]; then
            echo -e "${GREEN}✅ Linux x64 library available${NC}"
        else
            echo -e "${YELLOW}⚠️  Linux x64 library missing${NC}"
        fi
        ;;
    Darwin*)
        if [ -f "$DOWNLOAD_DIR/libfast_image-macos-x86_64.dylib" ]; then
            echo -e "${GREEN}✅ macOS Intel library available${NC}"
        else
            echo -e "${YELLOW}⚠️  macOS Intel library missing${NC}"
        fi
        if [ -f "$DOWNLOAD_DIR/libfast_image-macos-aarch64.dylib" ]; then
            echo -e "${GREEN}✅ macOS Apple Silicon library available${NC}"
        else
            echo -e "${YELLOW}⚠️  macOS Apple Silicon library missing${NC}"
        fi
        ;;
    *)
        echo -e "${YELLOW}⚠️  Unknown platform: $(uname -s)${NC}"
        ;;
esac

if [ $downloaded -eq 0 ]; then
    echo ""
    echo -e "${RED}❌ No libraries were downloaded successfully!${NC}"
    echo "Please check:"
    echo "  1. Internet connection"
    echo "  2. GitHub repository: https://github.com/$RUST_REPO"
    echo "  3. Release version: $VERSION"
    exit 1
fi

echo ""
echo -e "${GREEN}🎉 Native libraries download completed!${NC}"
echo ""
echo "Next steps:"
echo "  1. Run tests: mvn test"
echo "  2. Run benchmark: mvn exec:java@benchmark"
echo "  3. Build JAR: mvn package"

exit 0
