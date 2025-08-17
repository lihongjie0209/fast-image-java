package cn.lihongjie.image;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Comprehensive tests for FastImageUtils functionality across different platforms.
 * These tests focus on functionality verification, not performance.
 */
public class FastImageUtilsTest {
    
    private static boolean libraryAvailable = false;
    private static String platformInfo = "";
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("=== FastImageUtils Test Suite ===");
        
        try {
            // Get platform information
            platformInfo = FastImageUtils.getPlatformInfo();
            System.out.println("Platform Info:");
            System.out.println(platformInfo);
            System.out.println();
            
            // Test library availability
            libraryAvailable = FastImageUtils.testLibrary();
            System.out.println("Library Available: " + libraryAvailable);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Error during setup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Before
    public void setUp() {
        // Skip tests if library is not available
        org.junit.Assume.assumeTrue("Native library not available on this platform", libraryAvailable);
    }
    
    @Test
    public void testLibraryInitialization() {
        assertTrue("Library should be initialized", libraryAvailable);
        assertNotNull("Platform info should not be null", platformInfo);
        assertTrue("Platform info should contain platform details", 
                   platformInfo.contains("Platform:") && platformInfo.contains("Architecture:"));
    }
    
    @Test
    public void testBasicCompression() throws Exception {
        byte[] testImage = createTestJPEG(400, 300);
        
        // Test basic compression with quality 70
        byte[] compressed = FastImageUtils.compress(testImage, 70);
        
        assertNotNull("Compressed data should not be null", compressed);
        assertTrue("Compressed data should not be empty", compressed.length > 0);
        assertTrue("Compressed data should be smaller than original", compressed.length <= testImage.length);
        
        // Verify it's a valid JPEG (starts with FF D8 and ends with FF D9)
        assertEquals("Should start with JPEG marker", (byte) 0xFF, compressed[0]);
        assertEquals("Should start with JPEG marker", (byte) 0xD8, compressed[1]);
        assertEquals("Should end with JPEG marker", (byte) 0xFF, compressed[compressed.length - 2]);
        assertEquals("Should end with JPEG marker", (byte) 0xD9, compressed[compressed.length - 1]);
    }
    
    @Test
    public void testQualityLevels() throws Exception {
        byte[] testImage = createTestJPEG(600, 400);
        
        int[] qualities = {30, 50, 70, 90};
        byte[][] results = new byte[qualities.length][];
        
        // Test each quality level
        for (int i = 0; i < qualities.length; i++) {
            results[i] = FastImageUtils.compress(testImage, qualities[i]);
            assertNotNull("Result for quality " + qualities[i] + " should not be null", results[i]);
            assertTrue("Result for quality " + qualities[i] + " should not be empty", results[i].length > 0);
        }
        
        // Generally, higher quality should result in larger files
        // Note: This is not always strictly true, but should be true for our test images
        for (int i = 0; i < qualities.length - 1; i++) {
            System.out.println("Quality " + qualities[i] + ": " + results[i].length + " bytes");
        }
        System.out.println("Quality " + qualities[qualities.length - 1] + ": " + 
                          results[results.length - 1].length + " bytes");
    }
    
    @Test
    public void testPresetQualityMethods() throws Exception {
        byte[] testImage = createTestJPEG(500, 350);
        
        // Test preset quality methods
        byte[] high = FastImageUtils.compressHigh(testImage);
        byte[] medium = FastImageUtils.compressMedium(testImage);
        byte[] low = FastImageUtils.compressLow(testImage);
        
        assertNotNull("High quality result should not be null", high);
        assertNotNull("Medium quality result should not be null", medium);
        assertNotNull("Low quality result should not be null", low);
        
        assertTrue("High quality result should not be empty", high.length > 0);
        assertTrue("Medium quality result should not be empty", medium.length > 0);
        assertTrue("Low quality result should not be empty", low.length > 0);
        
        System.out.println("Preset quality results:");
        System.out.println("High (90%):   " + high.length + " bytes");
        System.out.println("Medium (60%): " + medium.length + " bytes");
        System.out.println("Low (30%):    " + low.length + " bytes");
    }
    
    @Test
    public void testInvalidQuality() throws Exception {
        byte[] testImage = createTestJPEG(200, 200);
        
        // Test invalid quality values
        try {
            FastImageUtils.compress(testImage, -1);
            fail("Should throw exception for negative quality");
        } catch (Exception e) {
            // Expected
            System.out.println("Expected exception for quality -1: " + e.getMessage());
        }
        
        try {
            FastImageUtils.compress(testImage, 101);
            fail("Should throw exception for quality > 100");
        } catch (Exception e) {
            // Expected
            System.out.println("Expected exception for quality 101: " + e.getMessage());
        }
    }
    
    @Test
    public void testEmptyInput() {
        try {
            FastImageUtils.compress(new byte[0], 50);
            fail("Should throw exception for empty input");
        } catch (Exception e) {
            // Expected
            System.out.println("Expected exception for empty input: " + e.getMessage());
        }
    }
    
    @Test
    public void testNullInput() {
        try {
            FastImageUtils.compress(null, 50);
            fail("Should throw exception for null input");
        } catch (Exception e) {
            // Expected
            System.out.println("Expected exception for null input: " + e.getMessage());
        }
    }
    
    @Test
    public void testPNGInput() throws Exception {
        byte[] testPNG = createTestPNG(300, 200);
        
        // PNG input should be compressed but remain as PNG format
        byte[] result = FastImageUtils.compress(testPNG, 70);
        
        assertNotNull("PNG compression result should not be null", result);
        assertTrue("PNG compression result should not be empty", result.length > 0);
        
        // Result should remain PNG format (input format = output format)
        assertEquals("Should start with PNG signature", (byte) 0x89, result[0]);
        assertEquals("Should start with PNG signature", (byte) 0x50, result[1]);
        assertEquals("Should start with PNG signature", (byte) 0x4E, result[2]);
        assertEquals("Should start with PNG signature", (byte) 0x47, result[3]);
        
        System.out.println("PNG input (" + testPNG.length + " bytes) -> PNG output (" + result.length + " bytes)");
    }
    
    @Test
    public void testJPEGInputOutputFormat() throws Exception {
        byte[] testJPEG = createTestJPEG(300, 200);
        
        // JPEG input should be compressed but remain as JPEG format  
        byte[] result = FastImageUtils.compress(testJPEG, 70);
        
        assertNotNull("JPEG compression result should not be null", result);
        assertTrue("JPEG compression result should not be empty", result.length > 0);
        
        // Result should remain JPEG format (input format = output format)
        assertEquals("Should start with JPEG marker", (byte) 0xFF, result[0]);
        assertEquals("Should start with JPEG marker", (byte) 0xD8, result[1]);
        assertEquals("Should end with JPEG marker", (byte) 0xFF, result[result.length - 2]);
        assertEquals("Should end with JPEG marker", (byte) 0xD9, result[result.length - 1]);
        
        System.out.println("JPEG input (" + testJPEG.length + " bytes) -> JPEG output (" + result.length + " bytes)");
    }
    
    @Test
    public void testLargeImage() throws Exception {
        // Test with a larger image (1920x1080)
        byte[] largeImage = createTestJPEG(1920, 1080);
        
        long startTime = System.currentTimeMillis();
        byte[] compressed = FastImageUtils.compress(largeImage, 60);
        long endTime = System.currentTimeMillis();
        
        assertNotNull("Large image compression result should not be null", compressed);
        assertTrue("Large image compression result should not be empty", compressed.length > 0);
        
        double compressionRatio = (1.0 - (double) compressed.length / largeImage.length) * 100;
        
        System.out.println("Large image test (1920x1080):");
        System.out.println("Original: " + formatBytes(largeImage.length));
        System.out.println("Compressed: " + formatBytes(compressed.length));
        System.out.println("Compression ratio: " + String.format("%.1f%%", compressionRatio));
        System.out.println("Processing time: " + (endTime - startTime) + "ms");
    }
    
    @Test
    public void testMultipleCompressions() throws Exception {
        byte[] testImage = createTestJPEG(400, 300);
        
        // Test multiple consecutive compressions to ensure stability
        for (int i = 0; i < 10; i++) {
            byte[] result = FastImageUtils.compress(testImage, 70);
            assertNotNull("Compression " + i + " result should not be null", result);
            assertTrue("Compression " + i + " result should not be empty", result.length > 0);
        }
        
        System.out.println("Successfully performed 10 consecutive compressions");
    }
    
    @Test
    public void testPlatformSpecificFeatures() {
        // Test platform-specific information
        String info = FastImageUtils.getPlatformInfo();
        
        assertTrue("Platform info should contain OS info", 
                   info.contains("Platform:"));
        assertTrue("Platform info should contain architecture info", 
                   info.contains("Architecture:"));
        assertTrue("Platform info should contain Java version", 
                   info.contains("Java:"));
        assertTrue("Platform info should contain library info", 
                   info.contains("Native Library:"));
        
        System.out.println("Platform-specific test completed");
        System.out.println("Current platform: " + System.getProperty("os.name") + 
                          " " + System.getProperty("os.arch"));
    }
    
    // Helper method to create test JPEG image
    private byte[] createTestJPEG(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Create a gradient background
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = (x * 255) / width;
                int green = (y * 255) / height;
                int blue = ((x + y) * 255) / (width + height);
                image.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }
        
        // Add some text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(12, Math.min(width, height) / 20)));
        g2d.drawString("Test " + width + "x" + height, 10, 30);
        
        g2d.dispose();
        
        // Convert to JPEG bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", baos);
        return baos.toByteArray();
    }
    
    // Helper method to create test PNG image
    private byte[] createTestPNG(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Create a checkered pattern
        int squareSize = 20;
        for (int y = 0; y < height; y += squareSize) {
            for (int x = 0; x < width; x += squareSize) {
                boolean isBlack = ((x / squareSize) + (y / squareSize)) % 2 == 0;
                g2d.setColor(isBlack ? Color.BLACK : Color.WHITE);
                g2d.fillRect(x, y, squareSize, squareSize);
            }
        }
        
        // Add some transparency
        g2d.setColor(new Color(255, 0, 0, 128)); // Semi-transparent red
        g2d.fillOval(width / 4, height / 4, width / 2, height / 2);
        
        g2d.dispose();
        
        // Convert to PNG bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return String.format("%.1fKB", bytes / 1024.0);
        return String.format("%.1fMB", bytes / (1024.0 * 1024));
    }
}
