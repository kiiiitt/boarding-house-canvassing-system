package bhsystem.project.frontend;
import bhsystem.project.backend.PhotoManager;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

//for viewing photos one at a time.
public class PhotoViewerDialog extends JDialog {

    private static final int PHOTO_AREA_WIDTH  = 400;
    private static final int PHOTO_AREA_HEIGHT = 300;

    private final ArrayList<String> photoPaths;
    private int currentIndex = 0;

    // Components that update on navigation
    private JLabel  photoLabel;
    private JLabel  counterLabel;
    private JButton prevBtn;
    private JButton nextBtn;

    public PhotoViewerDialog(Frame parent, ArrayList<String> photoPaths, String title) {
        super(parent, title + " — Photos", true); // modal = true

        this.photoPaths = (photoPaths != null) ? photoPaths : new ArrayList<>();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BACKGROUND);

        add(buildTitleBar(title), BorderLayout.NORTH);
        add(buildPhotoArea(),     BorderLayout.CENTER);
        add(buildControlBar(),    BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);

        // Load first photo
        loadPhoto(0);
    }


    // TITLE BAR

    private JPanel buildTitleBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel(title + " — Photos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(AppColors.PRIMARY_TEXT);
/*
        JButton closeBtn = new JButton("x");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeBtn.setForeground(AppColors.PRIMARY_TEXT);
        closeBtn.setOpaque(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
*/
        bar.add(titleLabel, BorderLayout.CENTER);
      //  bar.add(closeBtn,   BorderLayout.EAST);



        // Gold accent line
        JPanel accent = new JPanel();
        accent.setBackground(AppColors.ACCENT);
        accent.setPreferredSize(new Dimension(0, 3));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppColors.PRIMARY);
        wrapper.add(bar,    BorderLayout.CENTER);
        wrapper.add(accent, BorderLayout.SOUTH);

        return wrapper;
    }




    // PHOTO AREA — displays the current photo, scaled to fit
    private JPanel buildPhotoArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(new Color(0x1C, 0x10, 0x10));
        area.setPreferredSize(new Dimension(PHOTO_AREA_WIDTH, PHOTO_AREA_HEIGHT));
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setForeground(Color.WHITE);
        photoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        area.add(photoLabel, BorderLayout.CENTER);
        return area;
    }

    // CONTROL BAR — prev/next buttons + counter + close
    private JPanel buildControlBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(AppColors.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Prev button
        prevBtn = buildNavButton("← Prev");
        prevBtn.addActionListener(e -> navigate(-1));

        // Counter label
        counterLabel = new JLabel("", SwingConstants.CENTER);
        counterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        counterLabel.setForeground(AppColors.TEXT_MUTED);

        // Next button
        nextBtn = buildNavButton("Next →");
        nextBtn.addActionListener(e -> navigate(1));

        // Close button
        JButton closeBtn = buildPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());

        // Layout: [Prev]  [counter]  [Next]   [Close]
        JPanel navGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        navGroup.setBackground(AppColors.SURFACE);
        navGroup.add(prevBtn);
        navGroup.add(counterLabel);
        navGroup.add(nextBtn);

        bar.add(navGroup,  BorderLayout.CENTER);
        bar.add(closeBtn,  BorderLayout.EAST);

        return bar;
    }

    // NAVIGATION — moves to the photo at currentIndex + direction

    private void navigate(int direction) {
        if (photoPaths.isEmpty()) return;
        currentIndex = (currentIndex + direction + photoPaths.size()) % photoPaths.size();
        loadPhoto(currentIndex);
    }

    // LOAD PHOTO — resolves path, scales image, updates label and counter
    private void loadPhoto(int index) {
        if (photoPaths.isEmpty()) {
            photoLabel.setIcon(null);
            photoLabel.setText("No photos available.");
            counterLabel.setText("0 of 0");
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
            return;
        }

        // Update counter
        counterLabel.setText((index + 1) + " of " + photoPaths.size());

        // Enable/disable nav buttons
        boolean multiple = photoPaths.size() > 1;
        prevBtn.setEnabled(multiple);
        nextBtn.setEnabled(multiple);

        // Resolve path via PhotoManager
        String relativePath = photoPaths.get(index);
        File photoFile = PhotoManager.resolvePhotoFile(relativePath);

        if (photoFile == null || !photoFile.exists()) {
            photoLabel.setIcon(null);
            photoLabel.setText("Photo not found: " + relativePath);
            return;
        }

        // Load and scale image
        try {
            ImageIcon raw = new ImageIcon(photoFile.getAbsolutePath());
            Image scaled = scaleToFit(
                    raw.getImage(),
                    PHOTO_AREA_WIDTH  - 16,
                    PHOTO_AREA_HEIGHT - 16
            );
            photoLabel.setIcon(new ImageIcon(scaled));
            photoLabel.setText("");
        } catch (Exception ex) {
            photoLabel.setIcon(null);
            photoLabel.setText("Could not load photo.");
        }
    }

    // SCALE TO FIT — preserves aspect ratio within maxW x maxH bounds

    private Image scaleToFit(Image original, int maxW, int maxH) {
        int origW = original.getWidth(null);
        int origH = original.getHeight(null);

        if (origW <= 0 || origH <= 0) return original;

        double scaleW = (double) maxW / origW;
        double scaleH = (double) maxH / origH;
        double scale  = Math.min(scaleW, scaleH);

        int newW = (int) (origW * scale);
        int newH = (int) (origH * scale);

        // Use BufferedImage for smooth scaling
        BufferedImage scaled = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(original, 0, 0, newW, newH, null);
        g2.dispose();

        return scaled;
    }

    // Helpers
    private JButton buildNavButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? getBackground() : AppColors.BORDER);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(AppColors.SURFACE);
        btn.setForeground(AppColors.TEXT_PRIMARY);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) { btn.setBackground(AppColors.BACKGROUND); btn.repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(AppColors.SURFACE); btn.repaint();
            }
        });

        return btn;
    }

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(AppColors.PRIMARY);
        btn.setForeground(AppColors.PRIMARY_TEXT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 34));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(AppColors.PRIMARY_DARK); btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(AppColors.PRIMARY); btn.repaint();
            }
        });

        return btn;
    }
}
