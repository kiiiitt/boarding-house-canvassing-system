package bhsystem.project.frontend;

import bhsystem.project.backend.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class FullDetailsScreen extends JPanel {

    private final BoardingHouse bh;
    private JButton favoriteBtn;

    // Reviews panel kept as field so it can be rebuilt after adding a review
    private JPanel reviewsSectionPanel;
    private JPanel scrollContentPanel;

    public FullDetailsScreen(BoardingHouse bh) {
        this.bh = bh;
        setLayout(new BorderLayout());
        setBackground(AppColors.BACKGROUND);

        if (bh == null) {
            add(buildNullState(), BorderLayout.CENTER);
            return;
        }

        add(buildHeaderBar(),     BorderLayout.NORTH);
        add(buildScrollContent(), BorderLayout.CENTER);
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
        backBtn.addActionListener(e -> App.navigate(App.CARD_MAIN_LISTING_WALL));

        favoriteBtn = buildTextButton(
                bh.isFavorited() ? "♥ Saved" : "♡ Save",
                bh.isFavorited() ? AppColors.ACCENT : AppColors.PRIMARY_TEXT
        );
        favoriteBtn.addActionListener(e -> toggleFavorite());

        topRow.add(backBtn,     BorderLayout.WEST);
        topRow.add(favoriteBtn, BorderLayout.EAST);

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

    // SCROLLABLE CONTENT
    private JScrollPane buildScrollContent() {
        scrollContentPanel = new JPanel();
        scrollContentPanel.setLayout(new BoxLayout(scrollContentPanel, BoxLayout.Y_AXIS));
        scrollContentPanel.setBackground(AppColors.BACKGROUND);
        scrollContentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 32, 16));

        scrollContentPanel.add(buildHeroSection());
        scrollContentPanel.add(Box.createVerticalStrut(16));
        scrollContentPanel.add(buildSectionDivider("ROOM TYPES"));
        scrollContentPanel.add(Box.createVerticalStrut(10));
        scrollContentPanel.add(buildRoomTypesSection());
        scrollContentPanel.add(Box.createVerticalStrut(16));
        scrollContentPanel.add(buildSectionDivider("INCLUSIONS & RULES"));
        scrollContentPanel.add(Box.createVerticalStrut(10));
        scrollContentPanel.add(buildInclusionsRulesSection());
        scrollContentPanel.add(Box.createVerticalStrut(16));
        scrollContentPanel.add(buildSectionDivider("REVIEWS"));
        scrollContentPanel.add(Box.createVerticalStrut(10));

        reviewsSectionPanel = buildReviewsSection();
        scrollContentPanel.add(reviewsSectionPanel);

        scrollContentPanel.add(Box.createVerticalStrut(16));
        scrollContentPanel.add(buildSectionDivider("CONTACT"));
        scrollContentPanel.add(Box.createVerticalStrut(10));
        scrollContentPanel.add(buildContactSection());

        JScrollPane scrollPane = new JScrollPane(scrollContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(AppColors.BACKGROUND);
        scrollPane.getViewport().setBackground(AppColors.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    // HERO
    private JPanel buildHeroSection() {
        JPanel hero = buildSurfaceCard(14);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(bh.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel locationLabel = PNGLoader.createIconLabel("  Brgy. " + bh.getBarangay() + " · " + bh.getAddress(), "pin_.png", 12);
        locationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        locationLabel.setForeground(AppColors.TEXT_MUTED);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        metaRow.setOpaque(false);
        metaRow.setAlignmentX(Component.LEFT_ALIGNMENT);



        double rating   = bh.getOverallRating();
        int    revCount = bh.getReviewCount();

        JPanel ratingLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        ratingLabel.setOpaque(false);

        int filled = (int) Math.round(rating);
        for (int i = 1; i <= 5; i++) {
            JLabel star = new JLabel();
            star.setIcon(PNGLoader.getScaledIcon(i <= filled ? "star_trus.png" : "star_false.png", 9));
            ratingLabel.add(star);
        }

        JLabel ratingNumber = new JLabel("  " + String.format("%.1f", rating)
                + "  (" + revCount + " review" + (revCount != 1 ? "s" : "") + ")");
        ratingNumber.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ratingNumber.setForeground(AppColors.ACCENT);
        ratingLabel.add(ratingNumber);

        metaRow.add(ratingLabel);
        metaRow.add(buildAvailabilityBadge(bh.hasAvailableRooms()));



        // View Boarding House Photos
        JButton viewPhotosLink = buildLinkButton("  View Boarding House Photos");
        viewPhotosLink.setIcon(PNGLoader.getScaledIcon("camera.png", 13));
        viewPhotosLink.addActionListener(e -> {
            ArrayList<String> paths = bh.getPhotoPaths();
            if (paths == null || paths.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No photos uploaded for this boarding house yet.",
                        "No Photos", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new PhotoViewerDialog(App.getFrame(), paths, bh.getName()).setVisible(true);
        });
        viewPhotosLink.setAlignmentX(Component.LEFT_ALIGNMENT);

        hero.add(nameLabel);
        hero.add(Box.createVerticalStrut(4));
        hero.add(locationLabel);
        hero.add(Box.createVerticalStrut(8));
        hero.add(metaRow);
        hero.add(Box.createVerticalStrut(10));
        hero.add(viewPhotosLink);

        return hero;
    }

    //  ROOM TYPES
    private JPanel buildRoomTypesSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(AppColors.BACKGROUND);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        ArrayList<RoomType> rooms = bh.getRoomTypes();
        if (rooms == null || rooms.isEmpty()) {
            section.add(buildMutedLabel("No room types added yet."));
        } else {
            for (RoomType room : rooms) {
                section.add(buildRoomTypeCard(room));
                section.add(Box.createVerticalStrut(10));
            }
        }
        return section;
    }

    private JPanel buildRoomTypeCard(RoomType room) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AppColors.BORDER, 12),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accentBar = new JPanel();
        accentBar.setBackground(AppColors.PRIMARY);
        accentBar.setPreferredSize(new Dimension(4, 0));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(room.getTypeName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);

        JPanel availLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        availLabel.setOpaque(false);

        JLabel rentLabel = new JLabel("₱" + String.format("%,.0f", room.getMonthlyRent()) + "/mo   ");
        rentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rentLabel.setForeground(AppColors.TEXT_MUTED);

        String availText = room.isAvailable()
                ? "  " + room.getAvailableRooms() + " available" : "  Full";
        JLabel availStatusLabel = new JLabel(availText);
        availStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        availStatusLabel.setForeground(AppColors.TEXT_MUTED);
        availStatusLabel.setIcon(PNGLoader.getScaledIcon(room.isAvailable() ? "green.png" : "red.png", 10));

        availLabel.add(rentLabel);
        availLabel.add(availStatusLabel);

        String inclText = (room.getInclusions() != null && !room.getInclusions().isEmpty())
                ? String.join("  ·  ", room.getInclusions()) : "No inclusions listed";
        JLabel inclLabel = new JLabel(inclText);
        inclLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        inclLabel.setForeground(AppColors.TEXT_MUTED);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(availLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(inclLabel);
        // View Photos link per room — opens PhotoViewerDialog
        JButton photosLink = buildLinkButton("  Photos");
        photosLink.setIcon(PNGLoader.getScaledIcon("camera.png", 13));
        photosLink.addActionListener(e -> {
            ArrayList<String> paths = room.getPhotoPaths();
            if (paths == null || paths.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No photos uploaded for \"" + room.getTypeName() + "\" yet.",
                        "No Photos", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new PhotoViewerDialog(App.getFrame(), paths, room.getTypeName()).setVisible(true);
        });
        card.add(accentBar,  BorderLayout.WEST);
        card.add(infoPanel,  BorderLayout.CENTER);
        card.add(photosLink, BorderLayout.EAST);
        return card;
    }

    // INCLUSIONS & RULES
    private JPanel buildInclusionsRulesSection() {
        JPanel section = buildSurfaceCard(12);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        section.add(buildSubSectionLabel("Inclusions"));
        section.add(Box.createVerticalStrut(6));

        JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        chipsPanel.setOpaque(false);
        chipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ArrayList<String> inclusions = bh.getInclusions();
        if (inclusions == null || inclusions.isEmpty()) {
            chipsPanel.add(buildMutedLabel("None listed"));
        } else {
            for (String incl : inclusions) chipsPanel.add(buildInclusionChip(incl));
        }
        section.add(chipsPanel);
        section.add(Box.createVerticalStrut(12));
        section.add(buildSubSectionLabel("House Rules"));
        section.add(Box.createVerticalStrut(6));
        ArrayList<String> rules = bh.getRules();
        if (rules == null || rules.isEmpty()) {
            section.add(buildMutedLabel("No rules listed."));
        } else {
            for (String rule : rules) {
                JLabel ruleLabel = new JLabel("•  " + rule);
                ruleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                ruleLabel.setForeground(AppColors.TEXT_PRIMARY);
                ruleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                section.add(ruleLabel);
                section.add(Box.createVerticalStrut(3));
            }
        }
        return section;
    }




    /* This is for the review summary*/
    private JPanel buildReviewsSection() {
        JPanel section = buildSurfaceCard(12);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        // Summary + Add Review row
        JPanel summaryRow = new JPanel(new BorderLayout());
        summaryRow.setOpaque(false);
        summaryRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel ratingLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        ratingLabel.setOpaque(false);

        int filled = (int) Math.round(bh.getOverallRating());
        for (int i = 1; i <= 5; i++) {
            JLabel star = new JLabel();
            star.setIcon(PNGLoader.getScaledIcon(i <= filled ? "star_trus.png" : "star_false.png", 10));
            ratingLabel.add(star);
        }

        JLabel ratingNumber = new JLabel(" " + String.format("%.1f", bh.getOverallRating()));
        ratingNumber.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ratingNumber.setForeground(AppColors.ACCENT);
        ratingLabel.add(ratingNumber);

        JButton addReviewBtn = buildSmallPrimaryButton("+ Review");
        addReviewBtn.addActionListener(e -> {
            new AddReviewDialog(App.getFrame(), bh, App.getMasterList()).setVisible(true);
            rebuildReviewsSection();
        });
        summaryRow.add(ratingLabel, BorderLayout.WEST);
        summaryRow.add(addReviewBtn, BorderLayout.EAST);
        section.add(summaryRow);
        section.add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.DIVIDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sep);
        section.add(Box.createVerticalStrut(10));

        ArrayList<Review> reviews = bh.getReviews();
        if (reviews == null || reviews.isEmpty()) {
            section.add(buildMutedLabel("No reviews yet. Be the first!"));
        } else {
            for (Review review : reviews) {
                section.add(buildReviewEntry(review));
                section.add(Box.createVerticalStrut(10));
            }
        }

        return section;
    }




    private void rebuildReviewsSection() {
        int index = getComponentIndex(scrollContentPanel, reviewsSectionPanel);
        if (index >= 0) {
            scrollContentPanel.remove(reviewsSectionPanel);
            reviewsSectionPanel = buildReviewsSection();
            scrollContentPanel.add(reviewsSectionPanel, index);
            scrollContentPanel.revalidate();
            scrollContentPanel.repaint();
        }
    }
    private int getComponentIndex(Container container, Component target) {
        for (int i = 0; i < container.getComponentCount(); i++) {
            if (container.getComponent(i) == target) return i;
        }
        return -1;
    }
    private JPanel buildReviewEntry(Review review) {
        JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.Y_AXIS));
        entry.setOpaque(false);
        entry.setAlignmentX(Component.LEFT_ALIGNMENT);
        entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel starsLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starsLabel.setOpaque(false);
        for (int i = 1; i <= review.getStarRating(); i++) {
            JLabel star = new JLabel();
            star.setIcon(PNGLoader.getScaledIcon("star_trus.png", 9));
            starsLabel.add(star);
        }

        JLabel dateLabel = new JLabel(review.getDatePosted());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(AppColors.TEXT_MUTED);

        topRow.add(starsLabel, BorderLayout.WEST);
        topRow.add(dateLabel,  BorderLayout.EAST);

        JLabel commentLabel = new JLabel(
                "<html><div style='width:260px;'>" + review.getComment() + "</div></html>"
        );
        commentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commentLabel.setForeground(AppColors.TEXT_PRIMARY);
        commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        entry.add(topRow);
        entry.add(Box.createVerticalStrut(3));
        entry.add(commentLabel);
        return entry;
    }

    // CONTACT
    private JPanel buildContactSection() {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AppColors.PRIMARY, 12),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = PNGLoader.createIconLabel("  " + bh.getContactName(), "person.png", 14);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);

        JLabel numberLabel = PNGLoader.createIconLabel("  " + bh.getContactNumber(), "phone.png", 13);
        numberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        numberLabel.setForeground(AppColors.TEXT_MUTED);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(numberLabel);

        String email = bh.getContactEmail();
        if (email != null && !email.isEmpty()) {
            JLabel emailLabel = PNGLoader.createIconLabel("  " + email, "mail.png", 13);
            emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            emailLabel.setForeground(AppColors.TEXT_MUTED);
            infoPanel.add(Box.createVerticalStrut(4));
            infoPanel.add(emailLabel);
        }

        String facebook = bh.getContactFacebook();
        if (facebook != null && !facebook.isEmpty()) {
            JLabel fbLabel = PNGLoader.createIconLabel("  " + facebook, "facebook.png", 13);
            fbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fbLabel.setForeground(AppColors.TEXT_MUTED);
            infoPanel.add(Box.createVerticalStrut(4));
            infoPanel.add(fbLabel);
        }

        // copy number feature
        JButton copyBtn = buildSmallPrimaryButton("Copy Number");
        copyBtn.addActionListener(e -> {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(
                            new java.awt.datatransfer.StringSelection(bh.getContactNumber()), null);
            copyBtn.setText("Copied!");
            Timer timer = new Timer(1800, ev -> copyBtn.setText("Copy Number"));
            timer.setRepeats(false);
            timer.start();
        });

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(copyBtn,   BorderLayout.EAST);
        return card;
    }

    // FAVORITE TOGGLE
    private void toggleFavorite() {
        bh.setFavorited(!bh.isFavorited());
        DataManager.saveFavorites(App.getMasterList());
        favoriteBtn.setText(bh.isFavorited() ? "♥ Saved" : "♡ Save");
        favoriteBtn.setForeground(bh.isFavorited() ? AppColors.ACCENT : AppColors.PRIMARY_TEXT);
        favoriteBtn.repaint();
    }

    // NULL STATE
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

    // Shared helpers
    private JPanel buildSurfaceCard(int radius) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AppColors.BORDER, radius),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    private JPanel buildSectionDivider(String title) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(AppColors.BACKGROUND);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(AppColors.PRIMARY);
        JSeparator line = new JSeparator();
        line.setForeground(AppColors.DIVIDER);
        row.add(label, BorderLayout.WEST);
        row.add(line,  BorderLayout.CENTER);
        return row;
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
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(AppColors.BADGE_TEXT);
        badge.setBackground(available ? AppColors.BADGE_AVAILABLE : AppColors.BADGE_FULL);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return badge;
    }

    private JLabel buildInclusionChip(String text) {
        JLabel chip = new JLabel("  " + text + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x8B, 0x1A, 0x1A, 20));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setOpaque(false);
        chip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chip.setForeground(AppColors.PRIMARY);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.PRIMARY, 1),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        return chip;
    }

    private JLabel buildSubSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(AppColors.PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel buildMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        label.setForeground(AppColors.TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton buildLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(AppColors.PRIMARY);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder());
        return btn;
    }

    private JButton buildSmallPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        btn.setPreferredSize(new Dimension(110, 34));
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

    private JButton buildTextButton(String text, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(fgColor);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return btn;
    }

    // RoundedBorder
    private static class RoundedBorder implements Border {
        private final Color color;
        private final int   radius;

        RoundedBorder(Color color, int radius) {
            this.color  = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, w - 1, h - 1, radius, radius));
            g2.dispose();
        }

        @Override public Insets  getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
        @Override public boolean isBorderOpaque() { return false; }
    }
}