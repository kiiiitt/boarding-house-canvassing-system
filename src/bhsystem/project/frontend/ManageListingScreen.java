package bhsystem.project.frontend;

import bhsystem.project.backend.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ManageListingScreen extends JPanel {

    private final BoardingHouse            bh;
    private final ArrayList<BoardingHouse> masterList;

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

    private JPanel roomTypesListPanel;
    private JPanel scrollContentPanel;
    private JLabel bhPhotoCountLabel;

    public ManageListingScreen(BoardingHouse bh, ArrayList<BoardingHouse> masterList) {
        this.bh         = bh;
        this.masterList = masterList;
        setLayout(new BorderLayout());
        setBackground(AppColors.BACKGROUND);

        if (bh == null) {
            add(buildNullState(), BorderLayout.CENTER);
            return;
        }

        add(buildHeaderBar(),     BorderLayout.NORTH);
        add(buildScrollContent(), BorderLayout.CENTER);
        add(buildSaveBar(),       BorderLayout.SOUTH);
    }

    // HEADER BAR

    private JPanel buildHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(AppColors.PRIMARY);
        topRow.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        JButton backBtn = buildTextButton("← Back", AppColors.PRIMARY_TEXT);
        backBtn.addActionListener(e -> {
            App.refreshLandlordHome();
            App.navigate(App.CARD_LANDLORD_HOME);
        });
        JLabel titleLabel = new JLabel(bh.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
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

    // SCROLLABLE CONTENT

    private JScrollPane buildScrollContent() {
        scrollContentPanel = new JPanel();
        scrollContentPanel.setLayout(new BoxLayout(scrollContentPanel, BoxLayout.Y_AXIS));
        scrollContentPanel.setBackground(AppColors.BACKGROUND);
        scrollContentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 24, 16));

        scrollContentPanel.add(buildSectionLabel("BOARDING HOUSE INFO"));
        scrollContentPanel.add(Box.createVerticalStrut(10));
        scrollContentPanel.add(buildInfoSection());
        scrollContentPanel.add(Box.createVerticalStrut(16));
        scrollContentPanel.add(buildSectionLabel("BOARDING HOUSE PHOTOS"));
        scrollContentPanel.add(Box.createVerticalStrut(10));
        scrollContentPanel.add(buildBHPhotosSection());
        scrollContentPanel.add(Box.createVerticalStrut(16));
        scrollContentPanel.add(buildSectionLabel("ROOM TYPES"));
        scrollContentPanel.add(Box.createVerticalStrut(10));
        roomTypesListPanel = buildRoomTypesList();
        scrollContentPanel.add(roomTypesListPanel);
        scrollContentPanel.add(Box.createVerticalStrut(16));
        scrollContentPanel.add(buildSectionLabel("SECURITY"));
        scrollContentPanel.add(Box.createVerticalStrut(10));
        scrollContentPanel.add(buildSecuritySection());
        scrollContentPanel.add(Box.createVerticalStrut(16));
        scrollContentPanel.add(buildSectionLabel(""));
        scrollContentPanel.add(Box.createVerticalStrut(10));
        scrollContentPanel.add(buildDangerSection());

        JScrollPane scroll = new JScrollPane(scrollContentPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(AppColors.BACKGROUND);
        scroll.getViewport().setBackground(AppColors.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // INFO SECTION

    private JPanel buildInfoSection() {
        JPanel section = buildSurfaceCard(12);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        nameField            = buildTextField(bh.getName());
        barangayField        = buildTextField(bh.getBarangay());
        addressField         = buildTextField(bh.getAddress());
        contactNameField     = buildTextField(bh.getContactName());
        contactNumberField   = buildTextField(bh.getContactNumber());
        contactEmailField    = buildTextField(bh.getContactEmail() != null ? bh.getContactEmail() : "");
        contactFacebookField = buildTextField(bh.getContactFacebook() != null ? bh.getContactFacebook() : "");
        listingUsernameField = buildTextField(bh.getListingUsername() != null ? bh.getListingUsername() : "");

        section.add(buildFieldRow("Boarding House Name", nameField));      section.add(Box.createVerticalStrut(10));
        section.add(buildFieldRow("Barangay", barangayField));             section.add(Box.createVerticalStrut(10));
        section.add(buildFieldRow("Full Address", addressField));          section.add(Box.createVerticalStrut(10));
        section.add(buildFieldRow("Contact Name", contactNameField));      section.add(Box.createVerticalStrut(10));
        section.add(buildFieldRow("Contact Number", contactNumberField));  section.add(Box.createVerticalStrut(10));
        section.add(buildFieldRow("Contact Email  (optional)", contactEmailField));    section.add(Box.createVerticalStrut(10));
        section.add(buildFieldRow("Facebook  (optional)", contactFacebookField)); section.add(Box.createVerticalStrut(10));
        section.add(buildFieldRow("Listing Username", listingUsernameField)); section.add(Box.createVerticalStrut(10));

        String inclText = (bh.getInclusions() != null) ? String.join("\n", bh.getInclusions()) : "";
        inclusionsArea = buildTextArea(inclText);
        section.add(buildTextAreaRow("Inclusions  (one per line)", inclusionsArea));
        section.add(Box.createVerticalStrut(10));

        String rulesText = (bh.getRules() != null) ? String.join("\n", bh.getRules()) : "";
        rulesArea = buildTextArea(rulesText);
        section.add(buildTextAreaRow("House Rules  (one per line)", rulesArea));
        return section;
    }

    // BH PHOTOS SECTION

    private JPanel buildBHPhotosSection() {
        JPanel section = buildSurfaceCard(12);
        section.setLayout(new BorderLayout(12, 0));
        int count = (bh.getPhotoPaths() != null) ? bh.getPhotoPaths().size() : 0;
        bhPhotoCountLabel = new JLabel(count + " photo" + (count != 1 ? "s" : "") + " uploaded");
        bhPhotoCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bhPhotoCountLabel.setForeground(AppColors.TEXT_MUTED);
        // Opens PhotoManagerDialog — upload and delete


        JButton manageBtn = buildAccentButton("  Manage Photos");
        manageBtn.setIcon(PNGLoader.getScaledIcon("camera.png", 13));
        manageBtn.setPreferredSize(new Dimension(140, 28)); // adjust width/height to your liking
        manageBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        manageBtn.addActionListener(e -> {
            String dialogTitle = bh.getName() + " — Photos";
            new PhotoManagerDialog(
                    App.getFrame(),
                    dialogTitle,
                    bh.getPhotoPaths(),      // direct reference — changes apply to model immediately
                    bh.getId(),
                    true,                    // isBoardingHouse = true
                    masterList,
                    () -> {                  // onChanged: update count label after add/delete
                        int total = bh.getPhotoPaths() != null ? bh.getPhotoPaths().size() : 0;
                        bhPhotoCountLabel.setText(total + " photo" + (total != 1 ? "s" : "") + " uploaded");
                        bhPhotoCountLabel.repaint();
                    }
            ).setVisible(true);
        });
        section.add(bhPhotoCountLabel, BorderLayout.CENTER);
        section.add(manageBtn,         BorderLayout.EAST);
        return section;
    }

    // ROOM TYPES LIST

    private JPanel buildRoomTypesList() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppColors.BACKGROUND);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        ArrayList<RoomType> rooms = bh.getRoomTypes();
        if (rooms != null && !rooms.isEmpty()) {
            for (RoomType room : rooms) {
                panel.add(buildRoomTypeRow(room));
                panel.add(Box.createVerticalStrut(8));
            }
        } else {
            JLabel empty = new JLabel("No room types yet. Add one below.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            empty.setForeground(AppColors.TEXT_MUTED);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(empty);
            panel.add(Box.createVerticalStrut(8));
        }

        JButton addBtn = buildOutlineButton("+ Add Room Type");
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.addActionListener(e ->
                new EditRoomTypeDialog(App.getFrame(), bh, null, masterList,
                        this::rebuildRoomTypesList).setVisible(true));
        panel.add(addBtn);
        return panel;
    }

    private JPanel buildRoomTypeRow(RoomType room) {
        JPanel row = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose(); super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AppColors.BORDER, 10),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel nameLabel = new JLabel(room.getTypeName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);
        JLabel detailLabel = new JLabel("₱" + String.format("%,.0f", room.getMonthlyRent())
                + "/mo  ·  " + room.getAvailableRooms() + " / " + room.getTotalRooms() + " available");
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailLabel.setForeground(AppColors.TEXT_MUTED);
        info.add(nameLabel);
        info.add(Box.createVerticalStrut(2));
        info.add(detailLabel);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnGroup.setOpaque(false);
        JButton editBtn = buildSmallPrimaryButton("Edit");
        editBtn.addActionListener(e ->
                new EditRoomTypeDialog(App.getFrame(), bh, room, masterList,
                        this::rebuildRoomTypesList).setVisible(true));


        JButton photoBtn = buildAccentButton("  Manage Photos");
        photoBtn.setIcon(PNGLoader.getScaledIcon("camera.png", 13));
        photoBtn.setPreferredSize(new Dimension(140, 28)); // adjust width/height to your liking
        photoBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));


        photoBtn.addActionListener(e -> {
            String dialogTitle = room.getTypeName() + " — Photos";
            new PhotoManagerDialog(
                    App.getFrame(),
                    dialogTitle,
                    room.getPhotoPaths(),    // direct reference — changes apply to model immediately
                    room.getId(),
                    false,                   // isBoardingHouse = false
                    masterList,
                    null                     // no count label to update for room types
            ).setVisible(true);
        });
        btnGroup.add(editBtn);
        btnGroup.add(photoBtn);

        JButton deleteRoomBtn = buildSmallDeleteButton("Delete");
        deleteRoomBtn.addActionListener(e -> onDeleteRoomType(room));
        btnGroup.add(deleteRoomBtn);

        row.add(info,     BorderLayout.CENTER);
        row.add(btnGroup, BorderLayout.EAST);
        return row;
    }

    private void rebuildRoomTypesList() {
        int index = getComponentIndex(scrollContentPanel, roomTypesListPanel);
        if (index >= 0) {
            scrollContentPanel.remove(roomTypesListPanel);
            roomTypesListPanel = buildRoomTypesList();
            scrollContentPanel.add(roomTypesListPanel, index);
            scrollContentPanel.revalidate();
            scrollContentPanel.repaint();
        }
    }

    // ROOM TYPE DELETION OPTION

    /**
     * Confirms, then deletes the room type's photos from disk,
     * removes it from the BH model, saves CSV, and rebuilds the room types list.
     */
    private void onDeleteRoomType(RoomType room) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete room type \"" + room.getTypeName() + "\"?\n" +
                        "All photos for this room type will also be deleted.\n" +
                        "This cannot be undone.",
                "Delete Room Type",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        PhotoManager.deleteRoomTypePhotos(room.getId()); // delete photos from disk
        bh.removeRoomType(room);                         // remove from model
        DataManager.saveAll(masterList);                 // persist to CSV
        rebuildRoomTypesList();                          // refresh UI
    }

    // BOARDING HOUSE DELETION

    /**
     * Asks for double confirmation, then:
     *  1. Deletes all room type photos from disk
     *  2. Deletes all BH-level photos from disk
     *  3. Removes the BH from the master list
     *  4. Saves CSV
     *  5. Refreshes landlord home and navigates back
     */
    private void onDeleteListing() {
        // First confirmation
        int first = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \"" + bh.getName() + "\"?\n" +
                        "This will permanently delete the listing, all room types,\n" +
                        "all reviews, and all photos.",
                "Delete Listing",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (first != JOptionPane.YES_OPTION) return;

        // Second confirmation — extra safety for destructive action
        int second = JOptionPane.showConfirmDialog(
                this,
                "This cannot be undone. Delete \"" + bh.getName() + "\" permanently?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE
        );
        if (second != JOptionPane.YES_OPTION) return;

        // Delete all room type photos from disk
        for (RoomType room : bh.getRoomTypes()) {
            PhotoManager.deleteRoomTypePhotos(room.getId());
        }

        // Delete BH-level photos from disk
        PhotoManager.deleteBoardingHousePhotos(bh.getId());

        // Remove from master list and save
        masterList.remove(bh);
        DataManager.saveAll(masterList);

        // Navigate back to landlord home
        App.refreshLandlordHome();
        App.navigate(App.CARD_LANDLORD_HOME);
    }

    // delete entire listing option

    private JPanel buildDangerSection() {
        JPanel section = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose(); super.paintComponent(g);
            }
        };
        section.setOpaque(false);
        section.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AppColors.ERROR, 12),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel warningLabel = new JLabel(
                "<html>Permanently deletes this listing, all room types, and all photos.</html>"
        );
        warningLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        warningLabel.setForeground(AppColors.TEXT_MUTED);

        JButton deleteBtn = new JButton("Delete Listing") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose(); super.paintComponent(g);
            }
        };
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.setBackground(AppColors.ERROR);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setOpaque(false); deleteBtn.setContentAreaFilled(false);
        deleteBtn.setFocusPainted(false); deleteBtn.setBorderPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.setPreferredSize(new Dimension(120, 36));
        deleteBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                deleteBtn.setBackground(new Color(0x9B, 0x29, 0x1B)); deleteBtn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                deleteBtn.setBackground(AppColors.ERROR); deleteBtn.repaint();
            }
        });
        deleteBtn.addActionListener(e -> onDeleteListing());

        section.add(warningLabel, BorderLayout.CENTER);
        section.add(deleteBtn,    BorderLayout.EAST);
        return section;
    }

    // SECURITY SECTION

    private JPanel buildSecuritySection() {
        JPanel section = buildSurfaceCard(12);
        section.setLayout(new BorderLayout());
        JLabel pinLabel = new JLabel("4-digit PIN protects access to this listing.");
        pinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pinLabel.setForeground(AppColors.TEXT_MUTED);
        JButton changePinBtn = buildSmallPrimaryButton("Change PIN");
        changePinBtn.addActionListener(e -> showChangePinDialog());
        section.add(pinLabel,     BorderLayout.CENTER);
        section.add(changePinBtn, BorderLayout.EAST);
        return section;
    }

    // SAVE BAR

    private JPanel buildSaveBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JButton saveBtn = new JButton("Save Changes") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose(); super.paintComponent(g);
            }
        };
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(AppColors.PRIMARY); saveBtn.setForeground(AppColors.PRIMARY_TEXT);
        saveBtn.setOpaque(false); saveBtn.setContentAreaFilled(false);
        saveBtn.setFocusPainted(false); saveBtn.setBorderPainted(false);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.setPreferredSize(new Dimension(0, 48));
        saveBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { saveBtn.setBackground(AppColors.PRIMARY_DARK); saveBtn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { saveBtn.setBackground(AppColors.PRIMARY);      saveBtn.repaint(); }
        });
        saveBtn.addActionListener(e -> onSaveChanges());
        bar.add(saveBtn, BorderLayout.CENTER);
        return bar;
    }

    // ACTIONS
     //Reads all fields, validates, then saves to the bh object and writes to disk.

    private void onSaveChanges() {
        String name     = nameField.getText().trim();
        String brgy     = barangayField.getText().trim();
        String addr     = addressField.getText().trim();
        String cName    = contactNameField.getText().trim();
        String cNum     = contactNumberField.getText().trim();
        String cEmail   = contactEmailField.getText().trim();      // optional, no required check
        String cFB      = contactFacebookField.getText().trim();   // optional, no required check
        String username = listingUsernameField.getText().trim();

        // Required field check
        if (name.isEmpty() || brgy.isEmpty() || addr.isEmpty() || cName.isEmpty() || cNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields before saving.",
                    "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Listing username is required.",
                    "Missing Username", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Username uniqueness check
        for (BoardingHouse existing : masterList) {
            if (existing != bh && existing.getListingUsername().equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this,
                        "Username \"" + username + "\" is already used by another listing.",
                        "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Apply changes to model
        bh.setName(name);
        bh.setBarangay(brgy);
        bh.setAddress(addr);
        bh.setContactName(cName);
        bh.setContactNumber(cNum);
        bh.setContactEmail(cEmail);
        bh.setContactFacebook(cFB);
        bh.setListingUsername(username);
        bh.setInclusions(parseLines(inclusionsArea.getText()));
        bh.setRules(parseLines(rulesArea.getText()));

        DataManager.saveAll(masterList);
        JOptionPane.showMessageDialog(this, "Changes saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showChangePinDialog() {
        JDialog dialog = new JDialog(App.getFrame(), "Change PIN", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(AppColors.BACKGROUND);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(AppColors.PRIMARY);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = new JLabel("Change PIN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(AppColors.PRIMARY_TEXT);
        titleBar.add(title, BorderLayout.CENTER);
        JPanel accent = new JPanel();
        accent.setBackground(AppColors.ACCENT);
        accent.setPreferredSize(new Dimension(0, 3));
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(AppColors.PRIMARY);
        titleWrapper.add(titleBar, BorderLayout.CENTER);
        titleWrapper.add(accent,   BorderLayout.SOUTH);
        dialog.add(titleWrapper, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BACKGROUND);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));
        JPasswordField[] newPin     = new JPasswordField[4];
        JPasswordField[] confirmPin = new JPasswordField[4];
        JLabel errLbl = new JLabel(" ");
        errLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errLbl.setForeground(AppColors.ERROR);
        errLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(buildChangePinRow("New PIN", newPin));
        form.add(Box.createVerticalStrut(10));
        form.add(buildChangePinRow("Confirm PIN", confirmPin));
        form.add(Box.createVerticalStrut(10));
        form.add(errLbl);
        dialog.add(form, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnBar.setBackground(AppColors.SURFACE);
        btnBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER));
        JButton cancelBtn = buildOutlineButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        JButton saveBtn = buildSmallPrimaryButton("Update PIN");
        saveBtn.addActionListener(e -> {
            String np = collectPin(newPin);
            String cp = collectPin(confirmPin);
            if (np.length() < 4)  { errLbl.setText("Please enter all 4 digits."); return; }
            if (!np.equals(cp))   { errLbl.setText("PINs do not match.");          return; }
            bh.setOwnerPin(np);
            DataManager.saveAll(masterList);
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "PIN updated successfully.", "PIN Changed", JOptionPane.INFORMATION_MESSAGE);
        });
        btnBar.add(cancelBtn);
        btnBar.add(saveBtn);
        dialog.add(btnBar, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(320, 0));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel buildChangePinRow(String label, JPasswordField[] boxes) {
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
            final int idx = i;
            JPasswordField box = new JPasswordField(1);
            box.setFont(new Font("Segoe UI", Font.BOLD, 18));
            box.setHorizontalAlignment(SwingConstants.CENTER);
            box.setPreferredSize(new Dimension(46, 50));
            box.setBackground(AppColors.SURFACE);
            box.setForeground(AppColors.TEXT_PRIMARY);
            box.setEchoChar('●');
            box.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.BORDER, 2),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)));
            box.addKeyListener(new KeyAdapter() {
                @Override public void keyTyped(KeyEvent e) {
                    if (!Character.isDigit(e.getKeyChar())) { e.consume(); return; }
                    SwingUtilities.invokeLater(() -> {
                        String t = new String(box.getPassword());
                        if (t.length() > 1) box.setText(String.valueOf(t.charAt(t.length() - 1)));
                        if (idx < 3) boxes[idx + 1].requestFocusInWindow();
                    });
                }
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                            && new String(box.getPassword()).isEmpty() && idx > 0) {
                        boxes[idx - 1].setText("");
                        boxes[idx - 1].requestFocusInWindow();
                    }
                }
            });
            boxes[i] = box;
            boxRow.add(box);
        }
        row.add(lbl);
        row.add(Box.createVerticalStrut(6));
        row.add(boxRow);
        return row;
    }

    // Utilities

    private ArrayList<String> parseLines(String raw) {
        return Arrays.stream(raw.split("\n"))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String collectPin(JPasswordField[] boxes) {
        StringBuilder sb = new StringBuilder();
        for (JPasswordField b : boxes) sb.append(new String(b.getPassword()));
        return sb.toString();
    }

    private int getComponentIndex(Container container, Component target) {
        for (int i = 0; i < container.getComponentCount(); i++) {
            if (container.getComponent(i) == target) return i;
        }
        return -1;
    }

    private JPanel buildNullState() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppColors.BACKGROUND);
        JLabel label = new JLabel("No listing selected.", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        label.setForeground(AppColors.TEXT_MUTED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalGlue());
        panel.add(label);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // Builder helpers

    private JPanel buildSurfaceCard(int radius) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AppColors.BORDER, radius),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

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

    private JPanel buildFieldRow(String label, JTextField field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(AppColors.SURFACE);
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
        row.setBackground(AppColors.SURFACE);
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

    private JButton buildSmallPrimaryButton(String text) {
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
        btn.setBackground(AppColors.PRIMARY); btn.setForeground(AppColors.PRIMARY_TEXT);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 34));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(AppColors.PRIMARY_DARK); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(AppColors.PRIMARY);      btn.repaint(); }
        });
        return btn;
    }

    private JButton buildSmallOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(AppColors.SURFACE); btn.setForeground(AppColors.PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.PRIMARY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(44, 34));
        return btn;
    }

    private JButton buildSmallDeleteButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(AppColors.SURFACE); btn.setForeground(AppColors.ERROR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.ERROR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70, 34));
        return btn;
    }

    private JButton buildOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(AppColors.SURFACE); btn.setForeground(AppColors.TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 38));
        return btn;
    }

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