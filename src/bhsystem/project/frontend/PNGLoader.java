package bhsystem.project.frontend; // Adjust to your actual package
import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PNGLoader {

    /**
     * Loads and scales an icon by filename (square).
     * Example: PNGLoader.getScaledIcon("pin_.png", 16);
     */
    public static ImageIcon getScaledIcon(String filename, int size) {
        return getScaledIcon(filename, size, size);
    }

    /**
     * Loads and scales an icon by filename (custom width and height).
     * Example: PNGLoader.getScaledIcon("pin_.png", 16, 20);
     */
    public static ImageIcon getScaledIcon(String filename, int width, int height) {
        URL imgUrl = Thread.currentThread().getContextClassLoader()
                .getResource("png_icons/" + filename);
        if (imgUrl != null) {
            Image scaled = new ImageIcon(imgUrl).getImage()
                    .getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } else {
            System.err.println("Image not found" + filename);
            return null;
        }
    }

    /**
     * Creates a JLabel with an icon and text side by side.
     * Example: PNGLoader.createIconLabel("Brgy. Tibanga", "pin_.png", 16);
     */
    public static JLabel createIconLabel(String text, String filename, int size) {
        JLabel label = new JLabel(text);
        ImageIcon icon = getScaledIcon(filename, size);
        if (icon != null) {
            label.setIcon(icon);
            label.setIconTextGap(6);
            label.setHorizontalTextPosition(JLabel.RIGHT);
            label.setVerticalTextPosition(JLabel.CENTER);
        }
        return label;
    }
}
