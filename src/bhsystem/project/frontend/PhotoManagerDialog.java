package bhsystem.project.frontend;
import bhsystem.project.backend.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//A JDialog for managing photos on a boarding house listing or room type.
public class PhotoManagerDialog extends JDialog {

    private final ArrayList<String>       photoPaths;    // direct reference to model list
    private final String                  ownerId;       // bh.getId() or room.getId()
    private final boolean                 isBoardingHouse;
    private final ArrayList<BoardingHouse> masterList;
    private final Runnable                onChanged;     // called after any add/delete
    private JPanel photoListPanel;

    // Constructor
    public PhotoManagerDialog(Frame parent, String title,
                              ArrayList<String> photoPaths,
                              String ownerId, boolean isBoardingHouse,
                              ArrayList<BoardingHouse> masterList,
                              Runnable onChanged) {
        super(parent, title, true);
        this.photoPaths      = photoPaths;
        this.ownerId         = ownerId;
        this.isBoardingHouse = isBoardingHouse;
        this.masterList      = masterList;
        this.onChanged       = onChanged;

        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BACKGROUND);
        setResizable(false);

        add(buildTitleBar(title), BorderLayout.NORTH);
        add(buildPhotoListArea(), BorderLayout.CENTER);
        add(buildBottomBar(),     BorderLayout.SOUTH);

        setMinimumSize(new Dimension(380, 300));
        setPreferredSize(new Dimension(420, 480));
        pack();
        setLocationRelativeTo(parent);
    }

    // TITLE BAR
    private JPanel buildTitleBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(AppColors.PRIMARY_TEXT);
        bar.add(lbl, BorderLayout.CENTER);

        JPanel accent = new JPanel();
        accent.setBackground(AppColors.ACCENT);
        accent.setPreferredSize(new Dimension(0, 3));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppColors.PRIMARY);
        wrapper.add(bar,    BorderLayout.CENTER);
        wrapper.add(accent, BorderLayout.SOUTH);
        return wrapper;
    }

    // PHOTO LIST AREA — scrollable, rebuilt after every add/delete
    private JScrollPane buildPhotoListArea() {
        photoListPanel = new JPanel();
        photoListPanel.setLayout(new BoxLayout(photoListPanel, BoxLayout.Y_AXIS));
        photoListPanel.setBackground(AppColors.BACKGROUND);
        photoListPanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        rebuildPhotoList();

        JScrollPane scrollPane = new JScrollPane(photoListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(AppColors.BACKGROUND);
        scrollPane.getViewport().setBackground(AppColors.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void rebuildPhotoList() {
        photoListPanel.removeAll();
        if (photoPaths == null || photoPaths.isEmpty()) {
            JLabel empty = new JLabel("No photos uploaded yet.", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            empty.setForeground(AppColors.TEXT_MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            photoListPanel.add(Box.createVerticalStrut(24));
            photoListPanel.add(empty);
        } else {
            for (String path : new ArrayList<>(photoPaths)) {
                photoListPanel.add(buildPhotoRow(path));
                photoListPanel.add(Box.createVerticalStrut(8));
            }
        }

        photoListPanel.add(Box.createVerticalGlue());
        photoListPanel.revalidate();
        photoListPanel.repaint();
    }

    // PHOTO ROW — thumbnail + filename + delete button
    private JPanel buildPhotoRow(String relativePath) {
        JPanel row = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AppColors.BORDER, 10),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        //Thumbnail
        JLabel thumb = new JLabel("", SwingConstants.CENTER);
        thumb.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        thumb.setPreferredSize(new Dimension(60, 60));
        thumb.setBackground(new Color(0xE8, 0xD5, 0xD5));
        thumb.setOpaque(true);
        thumb.setBorder(BorderFactory.createLineBorder(AppColors.BORDER, 1));

        File photoFile = PhotoManager.resolvePhotoFile(relativePath);
        if (photoFile != null && photoFile.exists()) {
            ImageIcon raw    = new ImageIcon(photoFile.getAbsolutePath());
            Image     scaled = raw.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            thumb.setIcon(new ImageIcon(scaled));
            thumb.setText("");
        }

        //Filename label
        String filename = relativePath.contains("/")
                ? relativePath.substring(relativePath.lastIndexOf('/') + 1)
                : relativePath;
        JLabel fileLabel = new JLabel(filename);
        fileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fileLabel.setForeground(AppColors.TEXT_MUTED);

        // delete button
        JButton deleteBtn = new JButton(" Delete");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        deleteBtn.setForeground(AppColors.ERROR);
        deleteBtn.setBackground(AppColors.SURFACE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.ERROR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> onDeletePhoto(relativePath));

        row.add(thumb,      BorderLayout.WEST);
        row.add(fileLabel,  BorderLayout.CENTER);
        row.add(deleteBtn,  BorderLayout.EAST);
        return row;
    }

    // BOTTOM BAR — Upload More + Close
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JButton uploadBtn = buildAccentButton("  Upload More");
        uploadBtn.setIcon(PNGLoader.getScaledIcon("camera.png", 13));
        uploadBtn.addActionListener(e -> onUploadPhotos());

        JButton closeBtn = buildOutlineButton("Close");
        closeBtn.addActionListener(e -> dispose());

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(closeBtn);
        btnGroup.add(uploadBtn);

        bar.add(btnGroup, BorderLayout.EAST);
        return bar;
    }

    // ACTIONS
    //Confirms with the user, deletes the file from disk, removes from model,
    //saves to CSV, fires onChanged callback, and rebuilds the photo list.
    private void onDeletePhoto(String relativePath) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this photo?\nThis cannot be undone.",
                "Delete Photo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        PhotoManager.deletePhoto(relativePath);   // deletes file from disk
        photoPaths.remove(relativePath);          // removes from model list
        DataManager.saveAll(masterList);          // persists change to CSV

        if (onChanged != null) onChanged.run();   // update count label in ManageListingScreen
        rebuildPhotoList();
    }

    //adds paths to model, saves to CSV, fires onChanged, rebuilds list.
    private void onUploadPhotos() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Photo(s)");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Image files (jpg, jpeg, png, gif)", "jpg", "jpeg", "png", "gif"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(true);

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File[] selected = chooser.getSelectedFiles();
        int added = 0;

        for (File file : selected) {
            try {
                String relativePath = isBoardingHouse
                        ? PhotoManager.copyBoardingHousePhoto(file, ownerId)  // (File, String)
                        : PhotoManager.copyRoomTypePhoto(file, ownerId);       // (File, String)
                photoPaths.add(relativePath);
                added++;
            } catch (IOException ex) {
                System.err.println("PhotoManagerDialog: failed to copy " + file.getName()
                        + " — " + ex.getMessage());
            }
        }

        if (added > 0) {
            DataManager.saveAll(masterList);
            if (onChanged != null) onChanged.run();
            rebuildPhotoList();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not copy the selected photos. Check that the files are readable.",
                    "Upload Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Button builders
    private JButton buildAccentButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(AppColors.ACCENT); btn.setForeground(AppColors.ACCENT_TEXT);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(AppColors.ACCENT_LIGHT); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(AppColors.ACCENT);       btn.repaint(); }
        });
        return btn;
    }

    private JButton buildOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(AppColors.SURFACE); btn.setForeground(AppColors.TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }


    // RoundedBorder
    private static class RoundedBorder implements Border {
        private final Color color; private final int radius;
        RoundedBorder(Color color, int radius) { this.color = color; this.radius = radius; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, w - 1, h - 1, radius, radius));
            g2.dispose();
        }
        @Override public Insets  getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
        @Override public boolean isBorderOpaque() { return false; }
    }
}