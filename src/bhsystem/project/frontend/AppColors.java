package bhsystem.project.frontend;

import java.awt.Color;

// color palette for the system.

public class AppColors {


    // PRIMARY
    public static final Color PRIMARY          = new Color(0x8B, 0x1A, 0x1A); // #8B1A1A
    public static final Color PRIMARY_DARK     = new Color(0x6E, 0x0E, 0x0E); // #6E0E0E  — hover/pressed state
    public static final Color PRIMARY_TEXT     = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF  — text on PRIMARY bg

    // SECONDARY / ACCENT — Rich Gold (mascot fur, logo ring)
    public static final Color ACCENT           = new Color(0xD4, 0x92, 0x0A); // #D4920A
    public static final Color ACCENT_LIGHT     = new Color(0xE8, 0xA0, 0x20); // #E8A020  — lighter highlight
    public static final Color ACCENT_TEXT      = new Color(0x1C, 0x10, 0x10); // #1C1010  — text on ACCENT bg

    // BACKGROUNDS
    public static final Color BACKGROUND       = new Color(0xFA, 0xF6, 0xF0); // #FAF6F0  — warm off-white
    public static final Color SURFACE          = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF  — cards, dialogs, forms

    // TEXT
    public static final Color TEXT_PRIMARY     = new Color(0x1C, 0x10, 0x10); // #1C1010  — titles, body text
    public static final Color TEXT_MUTED       = new Color(0x6B, 0x50, 0x50); // #6B5050  — subtitles, barangay, labels


    // STATUS
    public static final Color BADGE_AVAILABLE  = new Color(0x2E, 0x7D, 0x52); // #2E7D52  — "Available" green
    public static final Color BADGE_FULL       = new Color(0xA9, 0x32, 0x26); // #A93226  — "Full" red
    public static final Color BADGE_TEXT       = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF  — text inside badges

    // ERROR / DANGER
    // Used for: wrong PIN message, validation errors
    public static final Color ERROR            = new Color(0xC0, 0x39, 0x2B); // #C0392B

    // BORDERS & DIVIDERS
    // Used for: card borders, separator lines, input field borders
    public static final Color BORDER           = new Color(0xE0, 0xD5, 0xD5); // #E0D5D5  — subtle warm border
    public static final Color DIVIDER          = new Color(0xD0, 0xC8, 0xC8); // #D0C8C8  — section dividers

    // Prevent instantiation
    private AppColors() {}
}
