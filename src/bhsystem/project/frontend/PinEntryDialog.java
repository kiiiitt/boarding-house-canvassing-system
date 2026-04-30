package bhsystem.project.frontend;
import bhsystem.project.backend.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class PinEntryDialog extends JDialog {

    private final BoardingHouse bh;
    private final JPasswordField[] digitBoxes = new JPasswordField[4];
    private JLabel errorLabel;

    public PinEntryDialog(Frame parent, BoardingHouse bh) {
        super(parent, "Enter PIN", true);
        this.bh = bh;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BACKGROUND);

        add(buildTitleBar(),  BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonBar(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(320, 0));
        setLocationRelativeTo(parent);

        // Auto focus first digit box on open
        SwingUtilities.invokeLater(() -> digitBoxes[0].requestFocusInWindow());
    }

    // TITLE BAR
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("🔒  " + bh.getName(), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(AppColors.PRIMARY_TEXT);

        JPanel accent = new JPanel();
        accent.setBackground(AppColors.ACCENT);
        accent.setPreferredSize(new Dimension(0, 3));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppColors.PRIMARY);
        wrapper.add(bar,    BorderLayout.CENTER);
        wrapper.add(accent, BorderLayout.SOUTH);

        return wrapper;
    }

    // FORM PANEL — instruction text + 4 digit boxes + error label
    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BACKGROUND);
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

        JLabel instruction = new JLabel("Enter the 4-digit PIN for this listing.", SwingConstants.CENTER);
        instruction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instruction.setForeground(AppColors.TEXT_MUTED);
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4 digit boxes row
        JPanel boxesRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        boxesRow.setBackground(AppColors.BACKGROUND);
        boxesRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 0; i < 4; i++) {
            JPasswordField box = buildDigitBox(i);
            digitBoxes[i] = box;
            boxesRow.add(box);
        }

        // Error label
        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(AppColors.ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(instruction);
        form.add(Box.createVerticalStrut(20));
        form.add(boxesRow);
        form.add(Box.createVerticalStrut(10));
        form.add(errorLabel);

        return form;
    }

    // BUTTON BAR — Cancel + Confirm
    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bar.setBackground(AppColors.SURFACE);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER));

        JButton cancelBtn = buildOutlineButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton confirmBtn = buildPrimaryButton("Confirm");
        confirmBtn.addActionListener(e -> onConfirm());

        // Also confirm on Enter from last digit box
        digitBoxes[3].addActionListener(e -> onConfirm());

        bar.add(cancelBtn);
        bar.add(confirmBtn);
        return bar;
    }

    // BUILD DIGIT BOX
    private JPasswordField buildDigitBox(int index) {
        JPasswordField box = new JPasswordField(1);
        box.setFont(new Font("Segoe UI", Font.BOLD, 22));
        box.setHorizontalAlignment(SwingConstants.CENTER);
        box.setPreferredSize(new Dimension(52, 56));
        box.setMaximumSize(new Dimension(52, 56));
        box.setBackground(AppColors.SURFACE);
        box.setForeground(AppColors.TEXT_PRIMARY);
        box.setCaretColor(AppColors.PRIMARY);
        box.setEchoChar('●');
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER, 2),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        // Highlight border on focus
        box.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                box.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColors.PRIMARY, 2),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                box.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColors.BORDER, 2),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }
        });

   // Auto-advance to next box
        box.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                // Replace any existing content and move forward
                SwingUtilities.invokeLater(() -> {
                    String text = new String(box.getPassword());
                    if (text.length() > 1) {
                        box.setText(String.valueOf(text.charAt(text.length() - 1)));
                    }
                    if (index < 3) {
                        digitBoxes[index + 1].requestFocusInWindow();
                    }
                    clearError();
                });
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (new String(box.getPassword()).isEmpty() && index > 0) {
                        digitBoxes[index - 1].setText("");
                        digitBoxes[index - 1].requestFocusInWindow();
                    }
                }
            }
        });

        return box;
    }

    // CONFIRM — collect PIN and validate
    private void onConfirm() {
        StringBuilder pin = new StringBuilder();
        for (JPasswordField box : digitBoxes) {
            pin.append(new String(box.getPassword()));
        }

        if (pin.length() < 4) {
            showError("Please enter all 4 digits.");
            return;
        }

        if (bh.checkPin(pin.toString())) {
            dispose();
            App.showManageListing(bh);
        } else {
            showError("Incorrect PIN. Please try again.");
            clearBoxes();
            digitBoxes[0].requestFocusInWindow();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.repaint();
    }

    private void clearError() {
        errorLabel.setText(" ");
    }

    private void clearBoxes() {
        for (JPasswordField box : digitBoxes) box.setText("");
    }

    // Helpers
    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(AppColors.PRIMARY);
        btn.setForeground(AppColors.PRIMARY_TEXT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 38));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(AppColors.PRIMARY_DARK); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(AppColors.PRIMARY);      btn.repaint(); }
        });
        return btn;
    }

    private JButton buildOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(AppColors.SURFACE);
        btn.setForeground(AppColors.TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 38));
        return btn;
    }
}