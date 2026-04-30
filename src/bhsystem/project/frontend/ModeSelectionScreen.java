package bhsystem.project.frontend;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ModeSelectionScreen extends JPanel {

    public ModeSelectionScreen() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BACKGROUND);

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    // HEADER — maroon banner with school branding
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel();
        header.setBackground(AppColors.PRIMARY);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(48, 32, 40, 32));

        // Paw icon
        JLabel iconLabel = new JLabel("🐾", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // App title
        JLabel titleLabel = new JLabel("BH Canvassing", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(AppColors.PRIMARY_TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Accent underline bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(AppColors.ACCENT);
        accentBar.setMaximumSize(new Dimension(80, 4));
        accentBar.setPreferredSize(new Dimension(80, 4));
        accentBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // School name
        JLabel schoolLabel = new JLabel("MSU-IIT", SwingConstants.CENTER);
        schoolLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        schoolLabel.setForeground(new Color(0xFF, 0xFF, 0xFF, 180));
        schoolLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(iconLabel);
        header.add(Box.createVerticalStrut(12));
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(accentBar);
        header.add(Box.createVerticalStrut(10));
        header.add(schoolLabel);

        return header;
    }

    // CENTER — welcome text + mode selection buttons
    private JPanel buildCenterPanel() {
        JPanel center = new JPanel();
        center.setBackground(AppColors.BACKGROUND);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(48, 40, 48, 40));

        // Welcome heading
        JLabel welcomeLabel = new JLabel("Welcome!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(AppColors.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Instruction text
        JLabel instructLabel = new JLabel(
                "<html><div style='text-align:center;'>Select mode<br></div></html>",
                SwingConstants.CENTER
        );
        instructLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructLabel.setForeground(AppColors.TEXT_MUTED);
        instructLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spacer
        center.add(welcomeLabel);
        center.add(Box.createVerticalStrut(8));
        center.add(instructLabel);
        center.add(Box.createVerticalStrut(40));

        // Student button
        JButton studentBtn = buildModeButton(
                "I'm a Student",
                "Browse and canvass boarding houses",
                AppColors.PRIMARY,
                AppColors.PRIMARY_TEXT,
                AppColors.PRIMARY_DARK
        );
        studentBtn.addActionListener(e -> App.navigate(App.CARD_MAIN_LISTING_WALL));
        studentBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(studentBtn);
        center.add(Box.createVerticalStrut(16));

        // Landlord button — outlined style
        JButton landlordBtn = buildModeButton(
                "I'm a Landlord",
                "Manage your boarding house listings",
                AppColors.SURFACE,
                AppColors.PRIMARY,
                new Color(0xF0, 0xE8, 0xE8)
        );
        landlordBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.PRIMARY, 2, true),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));
        landlordBtn.addActionListener(e -> App.navigate(App.CARD_LANDLORD_HOME));
        landlordBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(landlordBtn);

        return center;
    }

    // FOOTER
    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel();
        footer.setBackground(AppColors.BACKGROUND);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 16, 20, 16));

        JLabel tagline = new JLabel("Influencing the Future", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tagline.setForeground(AppColors.TEXT_MUTED);

        footer.add(tagline);
        return footer;
    }

    // Helper — builds a styled mode button with a title and subtitle line
    private JButton buildModeButton(String title, String subtitle,
                                    Color bgColor, Color fgColor, Color hoverColor) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // HTML label for two-line button text
        btn.setText("<html>"
                + "<div style='text-align:center;'>"
                + "<b style='font-size:13pt;'>" + title + "</b><br>"
                + "<span style='font-size:9pt;'>" + subtitle + "</span>"
                + "</div>"
                + "</html>");

        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        btn.setPreferredSize(new Dimension(320, 80));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
                btn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
                btn.repaint();
            }
        });

        return btn;
    }
}
