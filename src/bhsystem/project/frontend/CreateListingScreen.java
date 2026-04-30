package bhsystem.project.frontend;

import bhsystem.project.backend.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CreateListingScreen extends JPanel {

    private final ArrayList<BoardingHouse> masterList;

    // Form fields
    private JTextField nameField;
    private JTextField barangayField;
    private JTextField addressField;
    private JTextField contactNameField;
    private JTextField contactNumberField;
    private JTextField contactEmailField;     // optional
    private JTextField contactFacebookField;  // optional
    private JTextField listingUsernameField;  // required, unique
    private JTextArea  inclusionsArea;
    private JTextArea  rulesArea;

    private final JPasswordField[] pinBoxes     = new JPasswordField[4];
    private final JPasswordField[] confirmBoxes = new JPasswordField[4];

    private JLabel errorLabel;

    public CreateListingScreen(ArrayList<BoardingHouse> masterList) {
        this.masterList = masterList;
        setLayout(new BorderLayout());
        setBackground(AppColors.BACKGROUND);
        add(buildHeaderBar(),  BorderLayout.NORTH);
        add(buildScrollForm(), BorderLayout.CENTER);
        add(buildBottomBar(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(AppColors.PRIMARY);
        topRow.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        JButton backBtn = buildTextButton("← Back", AppColors.PRIMARY_TEXT);
        backBtn.addActionListener(e -> App.navigate(App.CARD_LANDLORD_HOME));
        JLabel titleLabel = new JLabel("Create New Listing", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(AppColors.PRIMARY_TEXT);
        topRow.add(backBtn,    BorderLayout.WEST);
        topRow.add(titleLabel, BorderLayout.CENTER);
        JPanel accent = new JPanel();
        accent.setBackground(AppColors.ACCENT);
        accent.setPreferredSize(new Dimension(0, 3));
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppColors.PRIMARY);
        wrapper.add(topRow,  BorderLayout.CENTER);
        wrapper.add(accent,  BorderLayout.SOUTH);
        header.add(wrapper, BorderLayout.CENTER);
        return header;
    }

    private JScrollPane buildScrollForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BACKGROUND);
        form.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        //Boarding House Info
        form.add(buildSectionLabel("BOARDING HOUSE INFO"));
        form.add(Box.createVerticalStrut(10));

        nameField          = buildTextField("");
        barangayField      = buildTextField("");
        addressField       = buildTextField("");
        contactNameField   = buildTextField("");
        contactNumberField = buildTextField("");
        contactEmailField  = buildTextField("");
        contactFacebookField = buildTextField("");

        form.add(buildFieldRow("Boarding House Name *", nameField));         form.add(Box.createVerticalStrut(10));
        form.add(buildFieldRow("Barangay *", barangayField));                form.add(Box.createVerticalStrut(10));
        form.add(buildFieldRow("Full Address *", addressField));             form.add(Box.createVerticalStrut(10));
        form.add(buildFieldRow("Contact Name *", contactNameField));         form.add(Box.createVerticalStrut(10));
        form.add(buildFieldRow("Contact Number *", contactNumberField));     form.add(Box.createVerticalStrut(10));
        form.add(buildFieldRow("Contact Email  (optional)", contactEmailField));    form.add(Box.createVerticalStrut(10));
        form.add(buildFieldRow("Facebook  (optional)", contactFacebookField)); form.add(Box.createVerticalStrut(16));

        //Inclusions & Rules
        form.add(buildSectionLabel("INCLUSIONS & RULES"));
        form.add(Box.createVerticalStrut(10));
        inclusionsArea = buildTextArea("WiFi\nWater\nElectricity");
        form.add(buildTextAreaRow("Inclusions  (one per line)", inclusionsArea));
        form.add(Box.createVerticalStrut(10));
        rulesArea = buildTextArea("");
        form.add(buildTextAreaRow("House Rules  (one per line)", rulesArea));
        form.add(Box.createVerticalStrut(16));

        //Listing Access
        form.add(buildSectionLabel("LISTING ACCESS"));
        form.add(Box.createVerticalStrut(4));
        JLabel usernameHint = new JLabel("Your username lets you find your listing on the landlord screen.");
        usernameHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        usernameHint.setForeground(AppColors.TEXT_MUTED);
        usernameHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(usernameHint);
        form.add(Box.createVerticalStrut(10));

        listingUsernameField = buildTextField("");
        form.add(buildFieldRow("Listing Username *", listingUsernameField));
        form.add(Box.createVerticalStrut(16));

        //PIN
        form.add(buildSectionLabel("SET YOUR PIN"));
        form.add(Box.createVerticalStrut(4));
        JLabel pinHint = new JLabel("You'll use this PIN together with your username to access this listing.");
        pinHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pinHint.setForeground(AppColors.TEXT_MUTED);
        pinHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(pinHint);
        form.add(Box.createVerticalStrut(12));
        form.add(buildPinRow("Set PIN", pinBoxes));
        form.add(Box.createVerticalStrut(10));
        form.add(buildPinRow("Confirm PIN", confirmBoxes));
        form.add(Box.createVerticalStrut(16));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(AppColors.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(AppColors.BACKGROUND);
        scroll.getViewport().setBackground(AppColors.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JButton createBtn = new JButton("Create Listing") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose(); super.paintComponent(g);
            }
        };
        createBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createBtn.setBackground(AppColors.PRIMARY); createBtn.setForeground(AppColors.PRIMARY_TEXT);
        createBtn.setOpaque(false); createBtn.setContentAreaFilled(false);
        createBtn.setFocusPainted(false); createBtn.setBorderPainted(false);
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createBtn.setPreferredSize(new Dimension(0, 48));
        createBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { createBtn.setBackground(AppColors.PRIMARY_DARK); createBtn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { createBtn.setBackground(AppColors.PRIMARY);      createBtn.repaint(); }
        });
        createBtn.addActionListener(e -> onCreateListing());
        bar.add(createBtn, BorderLayout.CENTER);
        return bar;
    }

    private void onCreateListing() {
        String name            = nameField.getText().trim();
        String barangay        = barangayField.getText().trim();
        String address         = addressField.getText().trim();
        String contactName     = contactNameField.getText().trim();
        String contactNumber   = contactNumberField.getText().trim();
        String contactEmail    = contactEmailField.getText().trim();      // optional, no required check
        String contactFacebook = contactFacebookField.getText().trim();  // optional, no required check
        String username        = listingUsernameField.getText().trim();

        // Required field validation
        if (name.isEmpty())          { showError("Boarding house name is required."); return; }
        if (barangay.isEmpty())      { showError("Barangay is required.");            return; }
        if (address.isEmpty())       { showError("Address is required.");             return; }
        if (contactName.isEmpty())   { showError("Contact name is required.");        return; }
        if (contactNumber.isEmpty()) { showError("Contact number is required.");      return; }
        if (username.isEmpty())      { showError("Listing username is required.");    return; }

        // Username check
        for (BoardingHouse existing : masterList) {
            if (existing.getListingUsername().equalsIgnoreCase(username)) {
                showError("That username is already taken. Choose a different one.");
                return;
            }
        }

        // PIN validation
        String pin        = collectPin(pinBoxes);
        String confirmPin = collectPin(confirmBoxes);
        if (pin.length() < 4)        { showError("Please enter all 4 digits for your PIN."); return; }
        if (!pin.equals(confirmPin)) { showError("PINs do not match. Please re-enter.");     return; }

        ArrayList<String> inclusions = parseLines(inclusionsArea.getText());
        ArrayList<String> rules      = parseLines(rulesArea.getText());

        BoardingHouse newBH = new BoardingHouse(
                name, barangay, address,
                contactName, contactNumber,
                contactEmail, contactFacebook, username,
                pin, rules, inclusions, new ArrayList<>()
        );

        DataManager.assignSlugAndAdd(newBH, masterList);
        App.refreshLandlordHome();
        App.showManageListing(newBH);
    }

    // PIN row builder
    private JPanel buildPinRow(String label, JPasswordField[] boxes) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(AppColors.BACKGROUND);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppColors.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel boxRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        boxRow.setBackground(AppColors.BACKGROUND);
        boxRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < 4; i++) {
            JPasswordField box = buildPinDigitBox(boxes, i);
            boxes[i] = box;
            boxRow.add(box);
        }
        row.add(lbl);
        row.add(Box.createVerticalStrut(6));
        row.add(boxRow);
        return row;
    }

    private JPasswordField buildPinDigitBox(JPasswordField[] group, int index) {
        JPasswordField box = new JPasswordField(1);
        box.setFont(new Font("Segoe UI", Font.BOLD, 20));
        box.setHorizontalAlignment(SwingConstants.CENTER);
        box.setPreferredSize(new Dimension(48, 52));
        box.setBackground(AppColors.SURFACE);
        box.setForeground(AppColors.TEXT_PRIMARY);
        box.setCaretColor(AppColors.PRIMARY);
        box.setEchoChar('●');
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER, 2),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        box.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.PRIMARY, 2), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
            }
            @Override public void focusLost(FocusEvent e) {
                box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER, 2), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
            }
        });
        box.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) { e.consume(); return; }
                SwingUtilities.invokeLater(() -> {
                    String t = new String(box.getPassword());
                    if (t.length() > 1) box.setText(String.valueOf(t.charAt(t.length() - 1)));
                    if (index < 3) group[index + 1].requestFocusInWindow();
                    clearError();
                });
            }
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                        && new String(box.getPassword()).isEmpty() && index > 0) {
                    group[index - 1].setText("");
                    group[index - 1].requestFocusInWindow();
                }
            }
        });
        return box;
    }


    // Helpers
    private String collectPin(JPasswordField[] boxes) {
        StringBuilder sb = new StringBuilder();
        for (JPasswordField b : boxes) sb.append(new String(b.getPassword()));
        return sb.toString();
    }

    private ArrayList<String> parseLines(String raw) {
        return Arrays.stream(raw.split("\n"))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void showError(String msg) { errorLabel.setText(msg); errorLabel.repaint(); }
    private void clearError()          { errorLabel.setText(" "); }

    private JPanel buildSectionLabel(String text) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(AppColors.BACKGROUND);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(AppColors.PRIMARY);
        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.DIVIDER);
        row.add(lbl, BorderLayout.WEST);
        row.add(sep, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildFieldRow(String label, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(AppColors.BACKGROUND);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppColors.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(lbl); row.add(Box.createVerticalStrut(4)); row.add(field);
        return row;
    }

    private JPanel buildTextAreaRow(String label, JTextArea area) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(AppColors.BACKGROUND);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppColors.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.add(lbl); row.add(Box.createVerticalStrut(4)); row.add(scroll);
        return row;
    }

    private JTextField buildTextField(String value) {
        JTextField field = new JTextField(value);
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

    private JTextArea buildTextArea(String value) {
        JTextArea area = new JTextArea(value, 3, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setForeground(AppColors.TEXT_PRIMARY);
        area.setBackground(AppColors.SURFACE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return area;
    }

    private JButton buildTextButton(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(fg);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 12));
        return btn;
    }
}