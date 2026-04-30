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

public class EditRoomTypeDialog extends JDialog {

    private final BoardingHouse            bh;
    private final RoomType                 existingRoom;
    private final ArrayList<BoardingHouse> masterList;
    private final Runnable                 onSaveCallback;
    private final boolean                  isNew;

    private JTextField        nameField;
    private JComboBox<String> capacityCombo;
    private JTextField        rentField;
    private JTextField        totalRoomsField;
    private JTextField        availRoomsField;
    private JTextArea         inclusionsArea;
    private JLabel            errorLabel;

    public EditRoomTypeDialog(Frame parent, BoardingHouse bh, RoomType existingRoom,
                              ArrayList<BoardingHouse> masterList, Runnable onSaveCallback) {
        super(parent, existingRoom == null ? "Add Room Type" : "Edit Room Type", true);
        this.bh             = bh;
        this.existingRoom   = existingRoom;
        this.masterList     = masterList;
        this.onSaveCallback = onSaveCallback;
        this.isNew          = (existingRoom == null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BACKGROUND);
        add(buildTitleBar(),  BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonBar(), BorderLayout.SOUTH);
        pack();
        setMinimumSize(new Dimension(360, 0));
        setLocationRelativeTo(parent);
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel(isNew ? "Add Room Type" : "Edit — " + existingRoom.getTypeName(), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(AppColors.PRIMARY_TEXT);
        JPanel accent = new JPanel();
        accent.setBackground(AppColors.ACCENT);
        accent.setPreferredSize(new Dimension(0, 3));
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppColors.PRIMARY);
        wrapper.add(bar,    BorderLayout.CENTER);
        wrapper.add(accent, BorderLayout.SOUTH);
        bar.add(title, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BACKGROUND);
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

        nameField = buildTextField(isNew ? "" : existingRoom.getTypeName());
        form.add(buildFieldRow("Room Type Name", nameField));
        form.add(Box.createVerticalStrut(10));

        String[] caps = {"1 Person (Solo)", "2 Persons", "4 Persons"};
        capacityCombo = new JComboBox<>(caps);
        capacityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        capacityCombo.setBackground(AppColors.SURFACE);
        if (!isNew) {
            switch (existingRoom.getCapacity()) {
                case 2  -> capacityCombo.setSelectedIndex(1);
                case 4  -> capacityCombo.setSelectedIndex(2);
                default -> capacityCombo.setSelectedIndex(0);
            }
        }
        form.add(buildFieldRow("Capacity", capacityCombo));
        form.add(Box.createVerticalStrut(10));

        rentField = buildTextField(isNew ? "" : String.valueOf((int) existingRoom.getMonthlyRent()));
        form.add(buildFieldRow("Monthly Rent (₱)", rentField));
        form.add(Box.createVerticalStrut(10));

        totalRoomsField = buildTextField(isNew ? "" : String.valueOf(existingRoom.getTotalRooms()));
        form.add(buildFieldRow("Total Rooms", totalRoomsField));
        form.add(Box.createVerticalStrut(10));

        availRoomsField = buildTextField(isNew ? "" : String.valueOf(existingRoom.getAvailableRooms()));
        form.add(buildFieldRow("Available Rooms", availRoomsField));
        form.add(Box.createVerticalStrut(10));

        String existingIncl = (!isNew && existingRoom.getInclusions() != null)
                ? String.join("\n", existingRoom.getInclusions()) : "";
        inclusionsArea = new JTextArea(existingIncl, 3, 20);
        inclusionsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inclusionsArea.setForeground(AppColors.TEXT_PRIMARY);
        inclusionsArea.setBackground(AppColors.SURFACE);
        inclusionsArea.setLineWrap(true);
        inclusionsArea.setWrapStyleWord(true);
        inclusionsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane inclScroll = new JScrollPane(inclusionsArea);
        inclScroll.setBorder(BorderFactory.createEmptyBorder());
        inclScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        inclScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel inclNote = new JLabel("One inclusion per line  e.g. WiFi");
        inclNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        inclNote.setForeground(AppColors.TEXT_MUTED);
        inclNote.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(buildLabelOnly("Inclusions"));
        form.add(Box.createVerticalStrut(4));
        form.add(inclScroll);
        form.add(Box.createVerticalStrut(2));
        form.add(inclNote);
        form.add(Box.createVerticalStrut(10));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(AppColors.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);
        return form;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bar.setBackground(AppColors.SURFACE);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER));
        JButton cancelBtn = buildOutlineButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        JButton saveBtn = buildPrimaryButton(isNew ? "Add Room Type" : "Save Changes");
        saveBtn.addActionListener(e -> onSave());
        bar.add(cancelBtn);
        bar.add(saveBtn);
        return bar;
    }

    private void onSave() {
        String name   = nameField.getText().trim();
        String rentS  = rentField.getText().trim();
        String totalS = totalRoomsField.getText().trim();
        String availS = availRoomsField.getText().trim();

        if (name.isEmpty())   { showError("Room type name is required.");  return; }
        if (rentS.isEmpty())  { showError("Monthly rent is required.");    return; }
        if (totalS.isEmpty()) { showError("Total rooms is required.");     return; }
        if (availS.isEmpty()) { showError("Available rooms is required."); return; }

        double rent; int total, avail;
        try {
            rent  = Double.parseDouble(rentS);
            total = Integer.parseInt(totalS);
            avail = Integer.parseInt(availS);
        } catch (NumberFormatException ex) {
            showError("Rent, total rooms, and available rooms must be numbers.");
            return;
        }

        if (avail > total) { showError("Available rooms cannot exceed total rooms."); return; }

        int capacity = switch (capacityCombo.getSelectedIndex()) {
            case 1  -> 2;
            case 2  -> 4;
            default -> 1;
        };

        ArrayList<String> inclusions = Arrays.stream(inclusionsArea.getText().split("\n"))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

        if (isNew) {
            RoomType newRoom = new RoomType(name, capacity, rent, total, avail, inclusions, new ArrayList<>());
            DataManager.assignSlugAndAdd(newRoom, bh, masterList);
        } else {
            existingRoom.setTypeName(name);
            existingRoom.setCapacity(capacity);
            existingRoom.setMonthlyRent(rent);
            existingRoom.setTotalRooms(total);
            existingRoom.setAvailableRooms(avail);
            existingRoom.setInclusions(inclusions);
            DataManager.saveAll(masterList);
        }

        dispose();
        if (onSaveCallback != null) onSaveCallback.run();
    }

    private void showError(String msg) { errorLabel.setText(msg); errorLabel.repaint(); }

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

    private JLabel buildLabelOnly(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppColors.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
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

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(AppColors.PRIMARY); btn.setForeground(AppColors.PRIMARY_TEXT);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 38));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(AppColors.PRIMARY_DARK); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(AppColors.PRIMARY);      btn.repaint(); }
        });
        return btn;
    }

    private JButton buildOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(AppColors.SURFACE); btn.setForeground(AppColors.TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER, 1), BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 38));
        return btn;
    }
}
