package bhsystem.project.frontend;
import bhsystem.project.backend.*;
import com.sun.tools.javac.Main;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MainListingWallScreen extends JPanel {

    private final ArrayList<BoardingHouse> masterList;

    // Filters
    private String  selectedBarangay = "All Areas";
    private int     selectedCapacity = 0;
    private double  minPrice         = 0;
    private double  maxPrice         = Double.MAX_VALUE;
    private boolean availableOnly    = false;

    // Tab
    private boolean showingFavorites = false;

    // Panels rebuilt on filter/tab change
    private JPanel      listingsContainer;
    private JScrollPane scrollPane;

    // Tab buttons — kept as fields to update active style
    private JButton listingsTabBtn;
    private JButton favoritesTabBtn;

    public MainListingWallScreen(ArrayList<BoardingHouse> masterList) {
        this.masterList = masterList;
        setLayout(new BorderLayout());
        setBackground(AppColors.BACKGROUND);

        add(buildHeaderBar(),   BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildTabBar(),      BorderLayout.SOUTH);
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
        backBtn.addActionListener(e -> App.navigate(App.CARD_MODE_SELECTION));

        JLabel titleLabel = new JLabel("Find a Boarding House", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(AppColors.PRIMARY_TEXT);

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
    // MAIN CONTENT
    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(AppColors.BACKGROUND);
        content.add(buildFilterBar(),  BorderLayout.NORTH);
        content.add(buildScrollPane(), BorderLayout.CENTER);
        return content;
    }
    // FILTER BAR
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel();
        bar.setBackground(AppColors.SURFACE);
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Row 1: Area + Capacity
        JPanel row1 = new JPanel(new GridLayout(1, 2, 10, 0));
        row1.setBackground(AppColors.SURFACE);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        String[] barangays = buildBarangayOptions();
        JComboBox<String> areaCombo = buildComboBox(barangays);
        areaCombo.addActionListener(e -> {
            selectedBarangay = (String) areaCombo.getSelectedItem();
            refreshListings();
        });

        String[] capacities = {"Any Capacity", "Solo (1 Person)", "2 Person", "4 Person"};
        JComboBox<String> capacityCombo = buildComboBox(capacities);
        capacityCombo.addActionListener(e -> {
            switch (capacityCombo.getSelectedIndex()) {
                case 1 -> selectedCapacity = 1;
                case 2 -> selectedCapacity = 2;
                case 3 -> selectedCapacity = 4;
                default -> selectedCapacity = 0;
            }
            refreshListings();
        });

        row1.add(areaCombo);
        row1.add(capacityCombo);

        // Row 2: Price range — with Min/Max placeholder text
        JPanel row2 = new JPanel(new BorderLayout());
        row2.setBackground(AppColors.SURFACE);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel priceContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        priceContainer.setBackground(AppColors.SURFACE);

        JLabel priceLabel = new JLabel("Price ₱");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceLabel.setForeground(AppColors.TEXT_MUTED);

        JTextField minField = buildPriceField("Min");
        minField.setText("Min");
        minField.setForeground(AppColors.TEXT_MUTED);
        minField.setPreferredSize(new Dimension(70, 32));

        JTextField maxField = buildPriceField("Max");
        maxField.setText("Max");
        maxField.setForeground(AppColors.TEXT_MUTED);
        maxField.setPreferredSize(new Dimension(70, 32));

        JLabel dashLabel = new JLabel(" - ", SwingConstants.CENTER);
        dashLabel.setForeground(AppColors.TEXT_MUTED);

        priceContainer.add(priceLabel);
        priceContainer.add(minField);
        priceContainer.add(dashLabel);
        priceContainer.add(maxField);
        row2.add(priceContainer, BorderLayout.EAST);

        // Placeholder
        FocusAdapter priceListener = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                if (field.getText().equals("Min") || field.getText().equals("Max")) {
                    field.setText("");
                    field.setForeground(AppColors.TEXT_PRIMARY);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                if (field.getText().trim().isEmpty()) {
                    field.setText(field == minField ? "Min" : "Max");
                    field.setForeground(AppColors.TEXT_MUTED);
                }
                applyPriceFilter(minField, maxField);
            }
        };
        minField.addFocusListener(priceListener);
        maxField.addFocusListener(priceListener);

        ActionListener priceEnterListener = e -> applyPriceFilter(minField, maxField);
        minField.addActionListener(priceEnterListener);
        maxField.addActionListener(priceEnterListener);

        // Row 3: Available only
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row3.setBackground(AppColors.SURFACE);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JCheckBox availCheckBox = new JCheckBox("Show available only");
        availCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        availCheckBox.setForeground(AppColors.TEXT_MUTED);
        availCheckBox.setBackground(AppColors.SURFACE);
        availCheckBox.setFocusPainted(false);
        availCheckBox.addActionListener(e -> {
            availableOnly = availCheckBox.isSelected();
            refreshListings();
        });
        row3.add(availCheckBox);
        bar.add(row1);
        bar.add(Box.createVerticalStrut(8));
        bar.add(row2);
        bar.add(Box.createVerticalStrut(6));
        bar.add(row3);
        return bar;
    }
    // SCROLL PANE
    private JScrollPane buildScrollPane() {
        listingsContainer = new JPanel();
        listingsContainer.setLayout(new BoxLayout(listingsContainer, BoxLayout.Y_AXIS));
        listingsContainer.setBackground(AppColors.BACKGROUND);
        listingsContainer.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        populateListings();

        scrollPane = new JScrollPane(listingsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(AppColors.BACKGROUND);
        scrollPane.getViewport().setBackground(AppColors.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }
    // POPULATE LISTINGS
    private void populateListings() {
        listingsContainer.removeAll();

        ArrayList<BoardingHouse> source = showingFavorites
                ? masterList.stream()
                .filter(BoardingHouse::isFavorited)
                .collect(Collectors.toCollection(ArrayList::new))
                : masterList;

        ArrayList<BoardingHouse> filtered = applyFilters(source);

        if (filtered.isEmpty()) {
            listingsContainer.add(buildEmptyState());
        } else {
            for (BoardingHouse bh : filtered) {
                listingsContainer.add(buildListingCard(bh));
                listingsContainer.add(Box.createVerticalStrut(12));
            }
        }
        listingsContainer.add(Box.createVerticalGlue());
        listingsContainer.revalidate();
        listingsContainer.repaint();
    }
    // FILTER LOGIC
    private ArrayList<BoardingHouse> applyFilters(ArrayList<BoardingHouse> source) {
        ArrayList<BoardingHouse> result = new ArrayList<>();
        for (BoardingHouse bh : source) {
            // Hide listings with no room types — landlord must add at least one room type
            // before the listing becomes visible to students
            if (bh.getRoomTypes() == null || bh.getRoomTypes().isEmpty()) continue;
            if (!selectedBarangay.equals("All Areas")
                    && !bh.getBarangay().equalsIgnoreCase(selectedBarangay)) continue;
            if (selectedCapacity != 0
                    && !bh.hasRoomWithCapacity(selectedCapacity)) continue;
            if ((minPrice > 0 || maxPrice < Double.MAX_VALUE)
                    && !bh.hasPriceInRange(minPrice, maxPrice)) continue;
            if (availableOnly && !bh.hasAvailableRooms()) continue;
            result.add(bh);
        }
        return result;
    }

    // BRIEF LISTING CARD
    private JPanel buildListingCard(BoardingHouse bh) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AppColors.BORDER, 14),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 116));  // increased from 100
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Cover photo
        JPanel photoBox = new JPanel(new BorderLayout());
        photoBox.setPreferredSize(new Dimension(72, 72));
        photoBox.setBackground(new Color(0xE8, 0xD5, 0xD5));
        photoBox.setBorder(BorderFactory.createLineBorder(AppColors.BORDER, 1));

        JLabel photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        String coverPath = bh.getCoverPhotoPath();
        if (coverPath != null) {
            File photoFile = PhotoManager.resolvePhotoFile(coverPath);
            if (photoFile != null && photoFile.exists()) {
                ImageIcon raw = new ImageIcon(photoFile.getAbsolutePath());
                Image scaled = raw.getImage().getScaledInstance(72, 72, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
                photoLabel.setText("");
            }
        }
        photoBox.add(photoLabel, BorderLayout.CENTER);
        //Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(bh.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);
        /*
         * USED TO LOAD PNG USING PNGLOADER CLASS-----------------------------------------------------
         */
        // Just an icon (for buttons, frames)
        PNGLoader.getScaledIcon("pin_.png", 16);
        // Icon + text label in one line
        JLabel barangayLabel = PNGLoader.createIconLabel("Brgy. " + bh.getBarangay(), "pin_.png", 16);
        barangayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        barangayLabel.setForeground(AppColors.TEXT_MUTED);
        /*
         * ------------------------------------------------------------------------------------------
         */

        // Rating + availability row
        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        metaRow.setOpaque(false);

        int filled = (int) Math.round(bh.getOverallRating());
        for (int i = 1; i <= 5; i++) {
            JLabel star = new JLabel();
            star.setIcon(PNGLoader.getScaledIcon(i <= filled ? "star_trus.png" : "star_false.png", 9));
            metaRow.add(star);
        }

        JLabel ratingLabel = new JLabel(" " + String.format("%.1f", bh.getOverallRating()));
        ratingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ratingLabel.setForeground(AppColors.ACCENT);

        JLabel badgeLabel = buildAvailabilityBadge(bh.hasAvailableRooms());

        metaRow.add(ratingLabel);
        metaRow.add(badgeLabel);

        // Contact row — shows name and number
        // Keep your row setup exactly the same
        JPanel contactRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contactRow.setOpaque(false);

        /*
         * USED TO LOAD PNG USING PNGLOADER CLASS-------------------------------------------------------
         */
        // 1. Create the Person Label
        JLabel personLabel = PNGLoader.createIconLabel(bh.getContactName() + "     ", "person.png", 11);
        personLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        personLabel.setForeground(AppColors.TEXT_MUTED);

        // 2. Create the Phone Label
        JLabel phoneLabel = PNGLoader.createIconLabel(bh.getContactNumber(), "phone.png", 11);
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        phoneLabel.setForeground(AppColors.TEXT_MUTED);

        // 3. Add BOTH labels to the row, one after the other
        contactRow.add(personLabel);
        contactRow.add(phoneLabel);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(barangayLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(metaRow);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(contactRow);
        /*
         * ---------------------------------------------------------------------------
         */


        /*
         * USED TO LOAD PNG USING PNGLOADER CLASS-----------------------------------------------
         */
        //Favorite button (EAST)
        // Clicking this will add the listing on students favorites.
        JButton favBtn = new JButton();
        favBtn.setIcon(PNGLoader.getScaledIcon(bh.isFavorited() ? "hearttrue.png" : "heart.png", 12));
        favBtn.setForeground(bh.isFavorited() ? AppColors.ACCENT : AppColors.TEXT_MUTED);
        /*
         * --------------------------------------------------------------------------------------
         */
        favBtn.setOpaque(false);
        favBtn.setContentAreaFilled(false);
        favBtn.setFocusPainted(false);
        favBtn.setBorderPainted(false);
        favBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        favBtn.setPreferredSize(new Dimension(36, 36));
        favBtn.addActionListener(e -> {
            bh.setFavorited(!bh.isFavorited());
            DataManager.saveFavorites(masterList);
            refreshListings();  // rebuilds cards with updated heart state
        });

        card.add(photoBox, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(favBtn, BorderLayout.EAST);
        // Click card body if will open the Full Details screen
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                App.showFullDetails(bh);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(AppColors.PRIMARY, 14),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(AppColors.BORDER, 14),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
                card.repaint();
            }
        });
        return card;
    }

    // EMPTY STATE
    private JPanel buildEmptyState() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppColors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(60, 32, 60, 32));

        String icon = showingFavorites ? "" : "";
        String msg  = showingFavorites ? "No favorites yet"    : "No listings found";
        String sub  = showingFavorites ? "Tap ♡ on a listing to save it here."
                : "Try adjusting your filters.";
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(AppColors.TEXT_MUTED);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLabel = new JLabel(msg, SwingConstants.CENTER);
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        msgLabel.setForeground(AppColors.TEXT_PRIMARY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel(sub, SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(AppColors.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(msgLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subLabel);
        return panel;
    }
    // BOTTOM TAB BAR
    private JPanel buildTabBar() {
        JPanel tabBar = new JPanel(new GridLayout(1, 2));
        tabBar.setBackground(AppColors.SURFACE);
        tabBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER));
        tabBar.setPreferredSize(new Dimension(0, 56));

        listingsTabBtn  = buildTabButton("🏠  Listings",  true);
        favoritesTabBtn = buildTabButton("♡  Favorites", false);

        listingsTabBtn.addActionListener(e -> {
            showingFavorites = false;
            setActiveTab(listingsTabBtn, favoritesTabBtn);
            refreshListings();
        });
        favoritesTabBtn.addActionListener(e -> {
            showingFavorites = true;
            setActiveTab(favoritesTabBtn, listingsTabBtn);
            refreshListings();
        });
        tabBar.add(listingsTabBtn);
        tabBar.add(favoritesTabBtn);
        return tabBar;
    }
    // Helpers
    private void refreshListings() {
        populateListings();
        if (scrollPane != null) scrollPane.getVerticalScrollBar().setValue(0);
    }
    private void applyPriceFilter(JTextField minField, JTextField maxField) {
        try {
            String minText = minField.getText().trim();
            String maxText = maxField.getText().trim();
            // Treat placeholder text "Min" / "Max" as empty — no filter applied
            minPrice = (minText.isEmpty() || minText.equals("Min")) ? 0               : Double.parseDouble(minText);
            maxPrice = (maxText.isEmpty() || maxText.equals("Max")) ? Double.MAX_VALUE : Double.parseDouble(maxText);
        } catch (NumberFormatException ex) {
            minPrice = 0;
            maxPrice = Double.MAX_VALUE;
        }
        refreshListings();
    }
    private String[] buildBarangayOptions() {
        ArrayList<String> options = new ArrayList<>();
        options.add("All Areas");
        for (BoardingHouse bh : masterList) {
            if (!options.contains(bh.getBarangay())) options.add(bh.getBarangay());
        }
        return options.toArray(new String[0]);
    }
    private JPanel buildStarPanel(double rating) {
        int filled = (int) Math.round(rating);
        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starPanel.setOpaque(false);

        for (int i = 1; i <= 5; i++) {
            JLabel star = new JLabel();
            star.setIcon(PNGLoader.getScaledIcon(i <= filled ? "star_trus.png" : "star_false.png", 7));
            starPanel.add(star);
        }
        return starPanel;
    }
    private JLabel buildAvailabilityBadge(boolean available) {
        JLabel badge = new JLabel(available ? " Available " : " Full ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(AppColors.BADGE_TEXT);
        badge.setBackground(available ? AppColors.BADGE_AVAILABLE : AppColors.BADGE_FULL);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return badge;
    }
    private JComboBox<String> buildComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(AppColors.SURFACE);
        combo.setForeground(AppColors.TEXT_PRIMARY);
        combo.setFocusable(false);
        return combo;
    }
    private JTextField buildPriceField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setForeground(AppColors.TEXT_PRIMARY);
        field.setBackground(AppColors.SURFACE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        field.setToolTipText(placeholder);
        return field;
    }
    private JButton buildTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyTabStyle(btn, active);
        return btn;
    }
    private void applyTabStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(AppColors.PRIMARY);
            btn.setForeground(AppColors.PRIMARY_TEXT);
        } else {
            btn.setBackground(AppColors.SURFACE);
            btn.setForeground(AppColors.TEXT_MUTED);
        }
    }
    private void setActiveTab(JButton active, JButton inactive) {
        applyTabStyle(active, true);
        applyTabStyle(inactive, false);
        active.repaint();
        inactive.repaint();
    }
    private JButton buildTextButton(String text, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(fgColor);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 12));
        return btn;
    }
    private static class RoundedBorder implements Border {
        private final Color color;
        private final int   radius;

        RoundedBorder(Color color, int radius) {
            this.color  = color;
            this.radius = radius;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
        @Override
        public boolean isBorderOpaque() { return false; }
    }
}