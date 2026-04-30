package bhsystem.project.frontend;

import bhsystem.project.backend.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class LandlordHomeScreen extends JPanel {
    private final ArrayList<BoardingHouse> masterList;
    public LandlordHomeScreen(ArrayList<BoardingHouse> masterList) {
        this.masterList = masterList;
        setLayout(new BorderLayout());
        setBackground(AppColors.BACKGROUND);
        add(buildHeaderBar(),   BorderLayout.NORTH);
        add(buildCenterContent(), BorderLayout.CENTER);
        add(buildBottomBar(),   BorderLayout.SOUTH);
    }

    // HEADER BAR

    private JPanel buildHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JButton backBtn = buildTextButton("← Back", AppColors.PRIMARY_TEXT);
        backBtn.addActionListener(e -> App.navigate(App.CARD_MODE_SELECTION));

        JLabel titleLabel = new JLabel("Landlord Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(AppColors.PRIMARY_TEXT);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(AppColors.PRIMARY);
        topRow.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        topRow.add(backBtn,    BorderLayout.WEST);
        topRow.add(titleLabel, BorderLayout.CENTER);

        JPanel accentLine = new JPanel();
        accentLine.setBackground(AppColors.ACCENT);
        accentLine.setPreferredSize(new Dimension(0, 3));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppColors.PRIMARY);
        wrapper.add(topRow,     BorderLayout.CENTER);
        wrapper.add(accentLine, BorderLayout.SOUTH);

        header.add(wrapper, BorderLayout.CENTER);
        return header;
    }

    // CENTER CONTENT — empty state or login form depends on data

    private JPanel buildCenterContent() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(AppColors.BACKGROUND);

        if (masterList == null || masterList.isEmpty()) {
            center.add(buildEmptyState(), BorderLayout.CENTER);
        } else {
            center.add(buildLoginForm(), BorderLayout.CENTER);
        }

        return center;
    }

    // EMPTY STATE - default if no data yet

    private JPanel buildEmptyState() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppColors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(60, 32, 60, 32));

        JLabel iconLabel = new JLabel("🏠", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLabel = new JLabel("No listings yet", SwingConstants.CENTER);
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        msgLabel.setForeground(AppColors.TEXT_PRIMARY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel(
                "<html><div style='text-align:center;'>Tap \"+ Create New Listing\"<br>to add your first boarding house.</div></html>",
                SwingConstants.CENTER
        );
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(AppColors.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(12));
        panel.add(msgLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subLabel);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // LOGIN FORM — username + PIN entry

    private JPanel buildLoginForm() {
        // Outer wrapper
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(AppColors.BACKGROUND);

        // Surface card
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AppColors.BORDER, 16),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        // Section title
        JLabel sectionLbl = new JLabel("ACCESS YOUR LISTING");
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sectionLbl.setForeground(AppColors.PRIMARY);
        sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.DIVIDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username field
        JLabel usernameLbl = new JLabel("Listing Username");
        usernameLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLbl.setForeground(AppColors.TEXT_PRIMARY);
        usernameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = buildTextField();
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // PIN field (single password field, max 4 digits)
        JLabel pinLbl = new JLabel("4-Digit PIN");
        pinLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pinLbl.setForeground(AppColors.TEXT_PRIMARY);
        pinLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField pinField = buildPinField();
        pinField.setAlignmentX(Component.LEFT_ALIGNMENT);
        pinField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // Error label
        JLabel errorLbl = new JLabel(" ");
        errorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLbl.setForeground(AppColors.ERROR);
        errorLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Open Listing button
        JButton openBtn = buildPrimaryButton("Open Listing");
        openBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        openBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        openBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String pin      = new String(pinField.getPassword()).trim();

            if (username.isEmpty()) { errorLbl.setText("Please enter your listing username."); return; }
            if (pin.isEmpty())      { errorLbl.setText("Please enter your PIN.");               return; }

            // Find listing by username (case-insensitive)
            BoardingHouse match = null;
            for (BoardingHouse bh : masterList) {
                if (bh.getListingUsername().equalsIgnoreCase(username)) {
                    match = bh;
                    break;
                }
            }

            if (match == null) {
                errorLbl.setText("No listing found with that username.");
                return;
            }

            if (!match.checkPin(pin)) {
                errorLbl.setText("Incorrect PIN. Please try again.");
                pinField.setText("");
                return;
            }

            // Correct — open the listing
            errorLbl.setText(" ");
            App.showManageListing(match);
        });

        // Allow pressing Enter on PIN field to submit
        pinField.addActionListener(e -> openBtn.doClick());

        card.add(sectionLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(sep);
        card.add(Box.createVerticalStrut(16));
        card.add(usernameLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(12));
        card.add(pinLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(pinField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLbl);
        card.add(Box.createVerticalStrut(16));
        card.add(openBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(24, 24, 24, 24);
        gbc.weightx = 1.0;
        wrapper.add(card, gbc);

        return wrapper;
    }

    // BOTTOM BAR — Create New Listing

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JButton createBtn = new JButton("+ Create New Listing") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose(); super.paintComponent(g);
            }
        };
        createBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createBtn.setBackground(AppColors.ACCENT);
        createBtn.setForeground(AppColors.ACCENT_TEXT);
        createBtn.setOpaque(false); createBtn.setContentAreaFilled(false);
        createBtn.setFocusPainted(false); createBtn.setBorderPainted(false);
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createBtn.setPreferredSize(new Dimension(0, 48));
        createBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { createBtn.setBackground(AppColors.ACCENT_LIGHT); createBtn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { createBtn.setBackground(AppColors.ACCENT);       createBtn.repaint(); }
        });
        createBtn.addActionListener(e -> App.showCreateListing());
        bar.add(createBtn, BorderLayout.CENTER);
        return bar;
    }

    // Field builders

    private JTextField buildTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(AppColors.TEXT_PRIMARY);
        field.setBackground(AppColors.SURFACE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.PRIMARY), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            }
        });
        return field;
    }


     //Builds a PIN password field that only accepts up to 4 numeric digits.

    private JPasswordField buildPinField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.BOLD, 16));
        field.setEchoChar('●');
        field.setForeground(AppColors.TEXT_PRIMARY);
        field.setBackground(AppColors.SURFACE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.PRIMARY), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            }
        });

        // Restrict to max 4 numeric digits
        ((PlainDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) return;
                String digits = string.replaceAll("[^0-9]", "");
                if (fb.getDocument().getLength() + digits.length() <= 4) {
                    super.insertString(fb, offset, digits, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) return;
                String digits = text.replaceAll("[^0-9]", "");
                if (fb.getDocument().getLength() - length + digits.length() <= 4) {
                    super.replace(fb, offset, length, digits, attrs);
                }
            }
        });

        return field;
    }

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(AppColors.PRIMARY); btn.setForeground(AppColors.PRIMARY_TEXT);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 48));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(AppColors.PRIMARY_DARK); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(AppColors.PRIMARY);      btn.repaint(); }
        });
        return btn;
    }

    private JButton buildTextButton(String text, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(fgColor);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 12));
        return btn;
    }

    private static class RoundedBorder implements Border {
        private final Color color;
        private final int   radius;
        RoundedBorder(Color color, int radius) { this.color = color; this.radius = radius; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }
        @Override public Insets  getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
        @Override public boolean isBorderOpaque() { return false; }
    }
}