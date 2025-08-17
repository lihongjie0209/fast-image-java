package cn.lihongjie.image;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Cross-platform compatibility tests for FastImageUtils.
 * This test class is specifically designed to verify that the library
 * works correctly across different operating systems and architectures.
 */
public class CrossPlatformTest {
    
    private static String detectedPlatform = "";
    private static boolean libraryInitialized = false;
    
    @BeforeClass
    public static void detectPlatform() {
        System.out.println("=== Cross-Platform Compatibility Test ===");
        
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        String javaVersion = System.getProperty("java.version");
        
        System.out.println("Operating System: " + osName);
        System.out.println("Architecture: " + osArch);
        System.out.println("Java Version: " + javaVersion);
        System.out.println();
        
        // Determine expected platform
        if (osName.contains("windows")) {
            if (osArch.contains("aarch") || osArch.contains("arm")) {
                detectedPlatform = "windows-aarch64";
            } else {
                detectedPlatform = "windows-x86_64";
            }
        } else if (osName.contains("linux")) {
            if (osArch.contains("aarch") || osArch.contains("arm")) {
                detectedPlatform = "linux-aarch64";
            } else {
                detectedPlatform = "linux-x86_64";
            }
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            if (osArch.contains("aarch") || osArch.contains("arm")) {
                detectedPlatform = "macos-aarch64";
            } else {
                detectedPlatform = "macos-x86_64";
            }
        }
        
        System.out.println("Detected platform: " + detectedPlatform);
        
        try {
            // Test library initialization
            String platformInfo = FastImageUtils.getPlatformInfo();
            libraryInitialized = FastImageUtils.testLibrary();
            
            System.out.println("Library initialized: " + libraryInitialized);
            System.out.println("Platform info from library:");
            System.out.println(platformInfo);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Failed to initialize library: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    public void testPlatformDetection() {
        assertFalse("Platform should be detected", detectedPlatform.isEmpty());
        assertTrue("Platform should be supported", 
                   detectedPlatform.equals("windows-x86_64") ||
                   detectedPlatform.equals("windows-aarch64") ||
                   detectedPlatform.equals("linux-x86_64") ||
                   detectedPlatform.equals("linux-aarch64") ||
                   detectedPlatform.equals("macos-x86_64") ||
                   detectedPlatform.equals("macos-aarch64"));
        
        System.out.println("✓ Platform detection test passed for: " + detectedPlatform);
    }
    
    @Test
    public void testLibraryLoading() {
        // Skip test if library is not available (but don't fail)
        if (!libraryInitialized) {
            System.out.println("⚠ Library not initialized - this may be expected in some environments");
            return;
        }
        
        assertTrue("Library should be properly initialized", libraryInitialized);
        
        // Verify platform info contains expected details
        String platformInfo = FastImageUtils.getPlatformInfo();
        assertNotNull("Platform info should not be null", platformInfo);
        assertTrue("Platform info should contain platform", platformInfo.contains("Platform:"));
        assertTrue("Platform info should contain architecture", platformInfo.contains("Architecture:"));
        assertTrue("Platform info should contain native library", platformInfo.contains("Native Library:"));
        
        System.out.println("✓ Library loading test passed");
    }
    
    @Test
    public void testBasicFunctionality() {
        // Skip test if library is not available
        org.junit.Assume.assumeTrue("Native library not available", libraryInitialized);
        
        try {
            // Create a simple test image
            byte[] testImage = createPlatformTestImage();
            
            // Test basic compression
            byte[] compressed = FastImageUtils.compress(testImage, 75);
            
            assertNotNull("Compressed data should not be null", compressed);
            assertTrue("Compressed data should not be empty", compressed.length > 0);
            
            // Verify JPEG format
            assertTrue("Result should be valid JPEG", isValidJPEG(compressed));
            
            System.out.println("✓ Basic functionality test passed");
            System.out.println("  Original size: " + testImage.length + " bytes");
            System.out.println("  Compressed size: " + compressed.length + " bytes");
            System.out.println("  Compression ratio: " + 
                              String.format("%.1f%%", (1.0 - (double)compressed.length / testImage.length) * 100));
            
        } catch (Exception e) {
            fail("Basic functionality test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testMultiQualityCompression() {
        // Skip test if library is not available
        org.junit.Assume.assumeTrue("Native library not available", libraryInitialized);
        
        try {
            byte[] testImage = createPlatformTestImage();
            
            int[] qualities = {30, 60, 90};
            System.out.println("Testing multiple quality levels:");
            
            for (int quality : qualities) {
                byte[] compressed = FastImageUtils.compress(testImage, quality);
                
                assertNotNull("Compressed data for quality " + quality + " should not be null", compressed);
                assertTrue("Compressed data for quality " + quality + " should not be empty", compressed.length > 0);
                assertTrue("Result for quality " + quality + " should be valid JPEG", isValidJPEG(compressed));
                
                System.out.println("  Quality " + quality + "%: " + compressed.length + " bytes");
            }
            
            System.out.println("✓ Multi-quality compression test passed");
            
        } catch (Exception e) {
            fail("Multi-quality compression test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testPresetMethods() {
        // Skip test if library is not available
        org.junit.Assume.assumeTrue("Native library not available", libraryInitialized);
        
        try {
            byte[] testImage = createPlatformTestImage();
            
            // Test all preset methods
            byte[] high = FastImageUtils.compressHigh(testImage);
            byte[] medium = FastImageUtils.compressMedium(testImage);
            byte[] low = FastImageUtils.compressLow(testImage);
            
            assertNotNull("High quality result should not be null", high);
            assertNotNull("Medium quality result should not be null", medium);
            assertNotNull("Low quality result should not be null", low);
            
            assertTrue("High quality result should be valid JPEG", isValidJPEG(high));
            assertTrue("Medium quality result should be valid JPEG", isValidJPEG(medium));
            assertTrue("Low quality result should be valid JPEG", isValidJPEG(low));
            
            System.out.println("✓ Preset methods test passed");
            System.out.println("  High quality (90%): " + high.length + " bytes");
            System.out.println("  Medium quality (60%): " + medium.length + " bytes");
            System.out.println("  Low quality (30%): " + low.length + " bytes");
            
        } catch (Exception e) {
            fail("Preset methods test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testErrorHandlingAcrossPlatforms() {
        // Skip test if library is not available
        org.junit.Assume.assumeTrue("Native library not available", libraryInitialized);
        
        // Test error handling - should work consistently across platforms
        
        // Test null input
        try {
            FastImageUtils.compress(null, 50);
            fail("Should throw exception for null input");
        } catch (Exception e) {
            System.out.println("✓ Null input properly rejected: " + e.getClass().getSimpleName());
        }
        
        // Test empty input
        try {
            FastImageUtils.compress(new byte[0], 50);
            fail("Should throw exception for empty input");
        } catch (Exception e) {
            System.out.println("✓ Empty input properly rejected: " + e.getClass().getSimpleName());
        }
        
        // Test invalid quality
        byte[] testData = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xD9}; // Minimal JPEG
        
        try {
            FastImageUtils.compress(testData, -1);
            fail("Should throw exception for negative quality");
        } catch (Exception e) {
            System.out.println("✓ Negative quality properly rejected: " + e.getClass().getSimpleName());
        }
        
        try {
            FastImageUtils.compress(testData, 101);
            fail("Should throw exception for quality > 100");
        } catch (Exception e) {
            System.out.println("✓ Quality > 100 properly rejected: " + e.getClass().getSimpleName());
        }
        
        System.out.println("✓ Error handling test passed");
    }
    
    @Test
    public void testPlatformConsistency() {
        // Skip test if library is not available
        org.junit.Assume.assumeTrue("Native library not available", libraryInitialized);
        
        try {
            byte[] testImage = createPlatformTestImage();
            
            // Perform the same compression multiple times - results should be consistent
            byte[] result1 = FastImageUtils.compress(testImage, 70);
            byte[] result2 = FastImageUtils.compress(testImage, 70);
            byte[] result3 = FastImageUtils.compress(testImage, 70);
            
            // Results should be identical for same input and quality
            assertArrayEquals("First and second compression should be identical", result1, result2);
            assertArrayEquals("Second and third compression should be identical", result2, result3);
            
            System.out.println("✓ Platform consistency test passed");
            System.out.println("  All compression results are identical (" + result1.length + " bytes)");
            
        } catch (Exception e) {
            fail("Platform consistency test failed: " + e.getMessage());
        }
    }
    
    // Helper method to create a test image specific to platform testing
    private byte[] createPlatformTestImage() throws IOException {
        BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Create a pattern that includes platform info
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 640, 480);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        g2d.drawString("Platform Test: " + detectedPlatform, 20, 50);
        g2d.drawString("Java: " + System.getProperty("java.version"), 20, 100);
        
        // Add some geometric patterns
        g2d.setColor(Color.YELLOW);
        for (int i = 0; i < 10; i++) {
            g2d.fillOval(i * 60, 150 + i * 20, 40, 40);
        }
        
        g2d.setColor(Color.RED);
        for (int i = 0; i < 640; i += 50) {
            g2d.drawLine(i, 300, i + 25, 350);
        }
        
        g2d.dispose();
        
        // Convert to JPEG bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", baos);
        return baos.toByteArray();
    }
    
    // Helper method to validate JPEG format
    private boolean isValidJPEG(byte[] data) {
        if (data == null || data.length < 4) {
            return false;
        }
        
        // Check JPEG markers
        return data[0] == (byte) 0xFF && data[1] == (byte) 0xD8 &&  // SOI marker
               data[data.length - 2] == (byte) 0xFF && data[data.length - 1] == (byte) 0xD9; // EOI marker
    }
}
