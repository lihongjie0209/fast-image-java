package cn.lihongjie.image.unit;

import cn.lihongjie.image.FastImageUtils;
import cn.lihongjie.image.util.TestImageLoader;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for cross-platform compatibility
 * 
 * These tests verify that the native library loads correctly
 * and platform detection works as expected.
 */
public class CrossPlatformUnitTest {

    @BeforeClass
    public static void ensureNativeLibraryLoaded() {
        // Verify that the native library is available
        String platformInfo = FastImageUtils.getPlatformInfo();
        assertNotNull("Platform info should not be null", platformInfo);
        assertTrue("Platform info should indicate initialization", 
                  platformInfo.contains("Initialized: true"));
    }

    @Test
    public void testPlatformDetection() {
        String platformInfo = FastImageUtils.getPlatformInfo();
        assertNotNull("Platform info should not be null", platformInfo);
        
        // Should contain expected platform information
        assertTrue("Should contain platform information", 
                  platformInfo.contains("Platform:"));
        assertTrue("Should contain architecture information", 
                  platformInfo.contains("Architecture:"));
        assertTrue("Should contain Java version", 
                  platformInfo.contains("Java:"));
        assertTrue("Should contain native library information", 
                  platformInfo.contains("Native Library:"));
    }

    @Test
    public void testLibraryInitializationStatus() {
        String platformInfo = FastImageUtils.getPlatformInfo();
        assertTrue("Library should be initialized in unit tests", 
                  platformInfo.contains("Initialized: true"));
    }

    @Test
    public void testLibraryFunctionality() {
        boolean isWorking = FastImageUtils.testLibrary();
        assertTrue("Native library should be working", isWorking);
    }

    @Test
    public void testSupportedPlatforms() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        
        // The library should work on supported platforms
        boolean isSupportedPlatform = 
            osName.contains("windows") || 
            osName.contains("linux") || 
            osName.contains("mac") || 
            osName.contains("darwin");
        
        assertTrue("Should be running on a supported platform", isSupportedPlatform);
        
        // Architecture should be supported
        boolean isSupportedArch = 
            osArch.contains("64") || 
            osArch.contains("aarch") || 
            osArch.contains("arm");
        
        assertTrue("Should be running on a supported architecture", isSupportedArch);
    }

    @Test
    public void testBasicOperationsWork() throws Exception {
        // Test that basic operations work on this platform
        byte[] testData = TestImageLoader.loadSmallPngImage();
        
        // Test compression
        byte[] compressed = FastImageUtils.compress(testData, 50);
        assertNotNull("Compression should work on this platform", compressed);
        
        // Test rotation
        byte[] rotated = FastImageUtils.rotate(testData, 90);
        assertNotNull("Rotation should work on this platform", rotated);
    }

    @Test
    public void testMultipleImageFormats() throws Exception {
        // Test that multiple image formats work on this platform
        
        // PNG
        byte[] pngData = TestImageLoader.loadSmallPngImage();
        byte[] compressedPng = FastImageUtils.compress(pngData, 70);
        byte[] rotatedPng = FastImageUtils.rotate90(pngData);
        assertNotNull("PNG compression should work", compressedPng);
        assertNotNull("PNG rotation should work", rotatedPng);
        
        // JPEG
        byte[] jpegData = TestImageLoader.loadSmallJpegImage();
        byte[] compressedJpeg = FastImageUtils.compress(jpegData, 70);
        byte[] rotatedJpeg = FastImageUtils.rotate90(jpegData);
        assertNotNull("JPEG compression should work", compressedJpeg);
        assertNotNull("JPEG rotation should work", rotatedJpeg);
    }
}
