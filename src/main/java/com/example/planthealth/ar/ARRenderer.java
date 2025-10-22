/*
package com.example.planthealth.ar;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

public class ARRenderer {
*/
/**
 * Generates a 3D-looking AR box image for plant health.
 * 
 * @param plantName Plant name
 * @param status    Healthy/Diseased/Warning
 * @return Base64 PNG image
 *//*
    public static String generatePlantHealthImage(String plantName, String status) {
     try {
         int width = 400;
         int height = 400;
         BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g2d = image.createGraphics();
    
         // Smooth graphics
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
         // Background
         g2d.setColor(new Color(245, 245, 245));
         g2d.fillRect(0, 0, width, height);
    
         // Box color based on status
         Color boxColor;
         switch (status.toLowerCase()) {
             case "healthy":
                 boxColor = Color.GREEN;
                 break;
             case "warning":
                 boxColor = Color.YELLOW;
                 break;
             default:
                 boxColor = Color.RED;
                 break;
         }
    
         // 3D Box points
         int x = 120, y = 120, size = 160, depth = 40;
    
         // Front face
         g2d.setColor(boxColor.darker());
         g2d.fillRect(x, y, size, size);
    
         // Top face (polygon)
         Path2D topFace = new Path2D.Double();
         topFace.moveTo(x, y);
         topFace.lineTo(x + depth, y - depth);
         topFace.lineTo(x + size + depth, y - depth);
         topFace.lineTo(x + size, y);
         topFace.closePath();
         g2d.setColor(boxColor.brighter());
         g2d.fill(topFace);
    
         // Side face (polygon)
         Path2D sideFace = new Path2D.Double();
         sideFace.moveTo(x + size, y);
         sideFace.lineTo(x + size + depth, y - depth);
         sideFace.lineTo(x + size + depth, y + size - depth);
         sideFace.lineTo(x + size, y + size);
         sideFace.closePath();
         g2d.setColor(boxColor);
         g2d.fill(sideFace);
    
         // Shadow for depth
         g2d.setColor(new Color(0, 0, 0, 50));
         g2d.fillOval(x + size / 2, y + size + 20, 100, 20);
    
         // Plant name text
         g2d.setColor(Color.BLACK);
         g2d.setFont(new Font("Arial", Font.BOLD, 18));
         g2d.drawString(plantName + " - " + status, 100, 380);
    
         g2d.dispose();
    
         // Convert to Base64
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ImageIO.write(image, "png", baos);
         byte[] bytes = baos.toByteArray();
         return Base64.getEncoder().encodeToString(bytes);
    
     } catch (Exception e) {
         e.printStackTrace();
         return null;
     }
    }
    }
    */

package com.example.planthealth.ar;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;

public class ARRenderer {

    /**
     * Practical AR: Overlay health information directly on the plant image
     */
    public static String generatePlantHealthImage(String plantName, String status, String originalImagePath) {
        try {
            // Load the ACTUAL plant image that user uploaded
            BufferedImage plantImage = ImageIO.read(new File(originalImagePath));
            Graphics2D g2d = plantImage.createGraphics();

            // Enable smooth graphics
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Add health status overlay
            drawHealthOverlay(g2d, plantImage.getWidth(), plantImage.getHeight(), status, plantName);

            // Add problem areas if not healthy
            if (!"Healthy".equalsIgnoreCase(status)) {
                drawProblemAreas(g2d, plantImage.getWidth(), plantImage.getHeight(), status);
            }

            g2d.dispose();

            // Convert to base64 for HTML display
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(plantImage, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            System.err.println("Error generating practical AR: " + e.getMessage());
            // Fallback: return the original image
            return getOriginalImageAsBase64(originalImagePath);
        }
    }

    private static void drawHealthOverlay(Graphics2D g2d, int width, int height, String status, String plantName) {
        // Status banner at top
        Color statusColor = getStatusColor(status);
        g2d.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 180));
        g2d.fillRect(0, 0, width, 60);

        // Status text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String statusText = getStatusEmoji(status) + " " + plantName + " - " + status.toUpperCase();
        g2d.drawString(statusText, 20, 35);

        // Care tips at bottom
        drawCareTips(g2d, width, height, status);
    }

    private static void drawProblemAreas(Graphics2D g2d, int width, int height, String status) {
        Color problemColor = getStatusColor(status);
        g2d.setColor(problemColor);
        g2d.setStroke(new BasicStroke(3));

        // Draw attention areas (in real app, these would come from ML analysis)
        int centerX = width / 2;
        int centerY = height / 2;

        // Circle potential problem areas
        g2d.drawOval(centerX - 80, centerY - 60, 60, 60);
        g2d.drawOval(centerX + 20, centerY - 30, 50, 50);
        g2d.drawOval(centerX - 40, centerY + 40, 70, 50);

        // Add attention markers
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("🔍 Check this area", centerX - 100, centerY - 70);
        g2d.drawString("⚠️ Possible issue", centerX + 30, centerY - 40);
        g2d.drawString("📌 Inspect closely", centerX - 50, centerY + 100);
    }

    private static void drawCareTips(Graphics2D g2d, int width, int height, String status) {
        int tipY = height - 120;

        // Semi-transparent background for tips
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(10, tipY, width - 20, 110);

        // Tips content
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("💡 Immediate Care Tips:", 20, tipY + 25);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        if ("Diseased".equalsIgnoreCase(status)) {
            g2d.drawString("• Isolate from other plants", 30, tipY + 50);
            g2d.drawString("• Remove affected leaves carefully", 30, tipY + 70);
            g2d.drawString("• Apply appropriate treatment", 30, tipY + 90);
            g2d.drawString("• Monitor daily for changes", 30, tipY + 110);
        } else if ("Warning".equalsIgnoreCase(status)) {
            g2d.drawString("• Increase monitoring frequency", 30, tipY + 50);
            g2d.drawString("• Check watering schedule", 30, tipY + 70);
            g2d.drawString("• Ensure proper sunlight", 30, tipY + 90);
            g2d.drawString("• Consider soil nutrients", 30, tipY + 110);
        } else {
            g2d.drawString("• Continue current care routine", 30, tipY + 50);
            g2d.drawString("• Maintain consistent watering", 30, tipY + 70);
            g2d.drawString("• Provide adequate sunlight", 30, tipY + 90);
            g2d.drawString("• Fertilize as needed", 30, tipY + 110);
        }
    }

    private static Color getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "healthy":
                return Color.GREEN;
            case "warning":
                return Color.ORANGE;
            case "diseased":
                return Color.RED;
            default:
                return Color.GRAY;
        }
    }

    private static String getStatusEmoji(String status) {
        switch (status.toLowerCase()) {
            case "healthy":
                return "✅";
            case "warning":
                return "⚠️";
            case "diseased":
                return "🚨";
            default:
                return "❓";
        }
    }

    private static String getOriginalImageAsBase64(String imagePath) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }
}