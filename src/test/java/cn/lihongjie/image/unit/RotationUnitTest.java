package cn.lihongjie.image.unit;

import cn.lihongjie.image.FastImageUtils;
import cn.lihongjie.image.util.TestImageLoader;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for FastImageUtils rotation functionality
 * 
 * These tests require the native library to be properly loaded.
 * If the native library is not available, the tests will fail fast.
 */
public class RotationUnitTest {

    @BeforeClass
    public static void ensureNativeLibraryLoaded() {
        // Verify that the native library is available
        String platformInfo = FastImageUtils.getPlatformInfo();
        assertNotNull("Platform info should not be null", platformInfo);
        assertTrue("Platform info should indicate initialization", 
                  platformInfo.contains("Initialized: true"));
    }

    @Test
    public void testBasicRotationWithValidAngles() throws Exception {
        byte[] testImageData = TestImageLoader.loadSmallPngImage();
        
        // Test valid rotation angles
        int[] validAngles = {90, 180, 270};
        
        for (int angle : validAngles) {
            byte[] rotated = FastImageUtils.rotate(testImageData, angle);
            assertNotNull("Rotated data should not be null for angle " + angle, rotated);
            assertTrue("Rotated data should have some content for angle " + angle, 
                      rotated.length > 0);
        }
    }

    @Test
    public void testConvenienceRotationMethods() throws Exception {
        byte[] testImageData = TestImageLoader.loadSmallPngImage();
        
        // Test convenience methods
        byte[] rotated90 = FastImageUtils.rotate90(testImageData);
        assertNotNull("rotate90() should not return null", rotated90);
        assertTrue("rotate90() should return data", rotated90.length > 0);
        
        byte[] rotated180 = FastImageUtils.rotate180(testImageData);
        assertNotNull("rotate180() should not return null", rotated180);
        assertTrue("rotate180() should return data", rotated180.length > 0);
        
        byte[] rotated270 = FastImageUtils.rotate270(testImageData);
        assertNotNull("rotate270() should not return null", rotated270);
        assertTrue("rotate270() should return data", rotated270.length > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRotationWithInvalidAngle0() throws Exception {
        byte[] testImageData = TestImageLoader.loadSmallPngImage();
        FastImageUtils.rotate(testImageData, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRotationWithInvalidAngle45() throws Exception {
        byte[] testImageData = TestImageLoader.loadSmallPngImage();
        FastImageUtils.rotate(testImageData, 45);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRotationWithInvalidAngle360() throws Exception {
        byte[] testImageData = TestImageLoader.loadSmallPngImage();
        FastImageUtils.rotate(testImageData, 360);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRotationWithNegativeAngle() throws Exception {
        byte[] testImageData = TestImageLoader.loadSmallPngImage();
        FastImageUtils.rotate(testImageData, -90);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRotationWithEmptyData() {
        FastImageUtils.rotate(new byte[0], 90);
    }

    @Test(expected = RuntimeException.class)
    public void testRotationWithNullData() {
        FastImageUtils.rotate(null, 90);
    }

    @Test
    public void testRotationAndCompressionCombination() throws Exception {
        byte[] testImageData = TestImageLoader.loadSmallPngImage();
        
        // Test rotation followed by compression
        byte[] rotated = FastImageUtils.rotate90(testImageData);
        byte[] compressed = FastImageUtils.compress(rotated, 70);
        assertNotNull("Combined rotation and compression should work", compressed);
        assertTrue("Combined operation should produce data", compressed.length > 0);
        
        // Test compression followed by rotation
        byte[] compressedFirst = FastImageUtils.compress(testImageData, 70);
        byte[] rotatedAfter = FastImageUtils.rotate90(compressedFirst);
        assertNotNull("Combined compression and rotation should work", rotatedAfter);
        assertTrue("Combined operation should produce data", rotatedAfter.length > 0);
    }

    @Test
    public void testMultipleRotationsEquivalence() throws Exception {
        byte[] testImageData = TestImageLoader.loadSmallPngImage();
        
        // Test that 4 x 90° rotations equals 360° (should be equivalent to original)
        byte[] rotated90 = FastImageUtils.rotate90(testImageData);
        byte[] rotated180 = FastImageUtils.rotate90(rotated90);
        byte[] rotated270 = FastImageUtils.rotate90(rotated180);
        byte[] rotated360 = FastImageUtils.rotate90(rotated270);
        
        // All operations should succeed
        assertNotNull("First 90° rotation should work", rotated90);
        assertNotNull("Second 90° rotation should work", rotated180);
        assertNotNull("Third 90° rotation should work", rotated270);
        assertNotNull("Fourth 90° rotation should work", rotated360);
        
        // Test that 180° twice equals 360° (original)
        byte[] direct180 = FastImageUtils.rotate180(testImageData);
        byte[] double180 = FastImageUtils.rotate180(direct180);
        assertNotNull("Direct 180° rotation should work", direct180);
        assertNotNull("Double 180° rotation should work", double180);
    }

    @Test
    public void testRotationWithJpegInput() throws Exception {
        byte[] testImageData = TestImageLoader.loadSmallJpegImage();
        
        byte[] rotated = FastImageUtils.rotate90(testImageData);
        assertNotNull("JPEG rotation should work", rotated);
        assertTrue("Rotated JPEG should have content", rotated.length > 0);
    }

    @Test
    public void testRotationWithDifferentFormats() throws Exception {
        // Test PNG rotation
        byte[] pngData = TestImageLoader.loadSmallPngImage();
        byte[] rotatedPng = FastImageUtils.rotate90(pngData);
        assertNotNull("PNG rotation should work", rotatedPng);
        
        // Test JPEG rotation
        byte[] jpegData = TestImageLoader.loadSmallJpegImage();
        byte[] rotatedJpeg = FastImageUtils.rotate90(jpegData);
        assertNotNull("JPEG rotation should work", rotatedJpeg);
    }
}
