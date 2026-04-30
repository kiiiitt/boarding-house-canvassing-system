package bhsystem.project.frontend;
import bhsystem.project.backend.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddReviewDialog extends JDialog {

    private final BoardingHouse            bh;
    private final ArrayList<BoardingHouse> masterList;

    private int selectedRating = 0;
    private final JButton[] starBtns = new JButton[5];
    private JTextArea commentArea;
    private JLabel   errorLabel;

    public AddReviewDialog(Frame parent, BoardingHouse bh,
                           ArrayList<BoardingHouse> masterList) {
        super(parent, "Add a Review", true);
        this.bh         = bh;
        this.masterList = masterList;
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
        JLabel titleLabel = new JLabel("Review: " + bh.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(AppColors.PRIMARY_TEXT);
        JPanel accent = new JPanel();
        accent.setBackground(AppColors.ACCENT);
        accent.setPreferredSize(new Dimension(0, 3));
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppColors.PRIMARY);
        wrapper.add(bar,    BorderLayout.CENTER);
        wrapper.add(accent, BorderLayout.SOUTH);
        bar.add(titleLabel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BACKGROUND);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));

        JLabel ratingLabel = new JLabel("Your Rating");
        ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ratingLabel.setForeground(AppColors.TEXT_PRIMARY);
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel starsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        starsRow.setBackground(AppColors.BACKGROUND);
        starsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < 5; i++) {
            final int sv = i + 1;
            JButton star = buildStarButton();
            star.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { highlightStars(sv, true);            }
                @Override public void mouseExited(MouseEvent e)  { highlightStars(selectedRating, false);}
                @Override public void mouseClicked(MouseEvent e) { selectedRating = sv; highlightStars(sv, false); clearError(); }
            });
            starBtns[i] = star;
            starsRow.add(star);
        }

        JLabel ratingHint = new JLabel("Tap a star to rate");
        ratingHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        ratingHint.setForeground(AppColors.TEXT_MUTED);
        ratingHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel commentLabel = new JLabel("Your Comment");
        commentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        commentLabel.setForeground(AppColors.TEXT_PRIMARY);
        commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        commentArea = new JTextArea(4, 28);
        commentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commentArea.setForeground(AppColors.TEXT_PRIMARY);
        commentArea.setBackground(AppColors.SURFACE);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setBorder(BorderFactory.createEmptyBorder());
        commentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        commentScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(AppColors.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(ratingLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(starsRow);
        form.add(Box.createVerticalStrut(4));
        form.add(ratingHint);
        form.add(Box.createVerticalStrut(16));
        form.add(commentLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(commentScroll);
        form.add(Box.createVerticalStrut(8));
        form.add(errorLabel);
        return form;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bar.setBackground(AppColors.SURFACE);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER));
        JButton cancelBtn = buildOutlineButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        JButton submitBtn = buildPrimaryButton("Submit Review");
        submitBtn.addActionListener(e -> onSubmit());
        bar.add(cancelBtn);
        bar.add(submitBtn);
        return bar;
    }

    private void onSubmit() {
        String comment = commentArea.getText().trim();
        if (selectedRating == 0) { showError("Please select a star rating."); return; }
        if (comment.isEmpty())   { showError("Please enter a comment.");       return; }
        // FIXED: Review(int starRating, String datePosted, String comment)
        String datePosted = new SimpleDateFormat("MMMM d yyyy").format(new Date());
        Review review = new Review(selectedRating, datePosted, comment);
        DataManager.assignSlugAndAdd(review, bh, masterList);
        dispose();
    }

    private void highlightStars(int upTo, boolean isHover) {
        for (int i = 0; i < 5; i++) {
            boolean filled = i < upTo;
            starBtns[i].setText(filled ? "★" : "☆");
            starBtns[i].setForeground(filled ? (isHover ? AppColors.ACCENT_LIGHT : AppColors.ACCENT) : AppColors.BORDER);
            starBtns[i].repaint();
        }
    }

    private void showError(String msg) { errorLabel.setText(msg); errorLabel.repaint(); }
    private void clearError()          { errorLabel.setText(" "); errorLabel.repaint(); }

    private JButton buildStarButton() {
        JButton btn = new JButton("☆");
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        btn.setForeground(AppColors.BORDER);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 2, 0, 2));
        return btn;
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
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 38));
        return btn;
    }
}
