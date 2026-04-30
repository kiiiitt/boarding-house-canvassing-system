package bhsystem.project.frontend;
import bhsystem.project.backend.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
public class App {

    // Card name final variable
    public static final String CARD_MODE_SELECTION    = "ModeSelection";
    public static final String CARD_LANDLORD_HOME     = "LandlordHome";
    public static final String CARD_MAIN_LISTING_WALL = "MainListingWall";
    public static final String CARD_FULL_DETAILS      = "FullDetails";
    public static final String CARD_CREATE_LISTING    = "CreateListing";   // Phase 4
    public static final String CARD_MANAGE_LISTING    = "ManageListing";   // Phase 4

    // Shared state
    private static ArrayList<BoardingHouse> masterList;
    private static CardLayout cardLayout;
    private static JPanel     rootPanel;
    private static JFrame     frame;

    // Screen references
    private static MainListingWallScreen mainListingWallScreen;
    private static LandlordHomeScreen    landlordHomeScreen;
    private static FullDetailsScreen     fullDetailsScreen;
    private static ManageListingScreen   manageListingScreen;   // Phase 4
    private static CreateListingScreen   createListingScreen;   // Phase 4

    // Entry
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::launch);
    }

    // Launch sequence
    private static void launch() {
        masterList = DataManager.loadAll();

        frame = new JFrame("Boarding House Canvassing System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 780);
        frame.setMinimumSize(new Dimension(420, 680));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(AppColors.BACKGROUND);

        // ILISAN KUNG NAA NAY FINAL ICON
        java.net.URL iconUrl = App.class.getResource("app_icon.png");
        if (iconUrl != null) frame.setIconImage(new ImageIcon(iconUrl).getImage());

        cardLayout = new CardLayout();
        rootPanel  = new JPanel(cardLayout);
        rootPanel.setBackground(AppColors.BACKGROUND);

        buildAndRegisterScreens();
        cardLayout.show(rootPanel, CARD_MODE_SELECTION);

        frame.add(rootPanel);
        frame.setVisible(true);
    }

    // Build and register all screens

    private static void buildAndRegisterScreens() {
        ModeSelectionScreen modeSelectionScreen = new ModeSelectionScreen();
        rootPanel.add(modeSelectionScreen, CARD_MODE_SELECTION);

        landlordHomeScreen = new LandlordHomeScreen(masterList);
        rootPanel.add(landlordHomeScreen, CARD_LANDLORD_HOME);

        mainListingWallScreen = new MainListingWallScreen(masterList);
        rootPanel.add(mainListingWallScreen, CARD_MAIN_LISTING_WALL);

        fullDetailsScreen = new FullDetailsScreen(null);
        rootPanel.add(fullDetailsScreen, CARD_FULL_DETAILS);

        // Phase 4 screens — built empty at startup, rebuilt on demand
        createListingScreen = new CreateListingScreen(masterList);
        rootPanel.add(createListingScreen, CARD_CREATE_LISTING);

        manageListingScreen = new ManageListingScreen(null, masterList);
        rootPanel.add(manageListingScreen, CARD_MANAGE_LISTING);
    }

    // Navigation of the UI

    public static void navigate(String cardName) {
        cardLayout.show(rootPanel, cardName);
    }

    public static void showFullDetails(BoardingHouse boardingHouse) {
        fullDetailsScreen = new FullDetailsScreen(boardingHouse);
        rootPanel.add(fullDetailsScreen, CARD_FULL_DETAILS);
        cardLayout.show(rootPanel, CARD_FULL_DETAILS);
    }

    public static void showManageListing(BoardingHouse boardingHouse) {
        manageListingScreen = new ManageListingScreen(boardingHouse, masterList);
        rootPanel.add(manageListingScreen, CARD_MANAGE_LISTING);
        cardLayout.show(rootPanel, CARD_MANAGE_LISTING);
    }

    public static void showCreateListing() {
        createListingScreen = new CreateListingScreen(masterList);
        rootPanel.add(createListingScreen, CARD_CREATE_LISTING);
        cardLayout.show(rootPanel, CARD_CREATE_LISTING);
    }

    // Refresh methods — rebuild screens after data changes
    public static void refreshListingWall() {
        mainListingWallScreen = new MainListingWallScreen(masterList);
        rootPanel.add(mainListingWallScreen, CARD_MAIN_LISTING_WALL);
        cardLayout.show(rootPanel, CARD_MAIN_LISTING_WALL);
    }

    public static void refreshLandlordHome() {
        landlordHomeScreen = new LandlordHomeScreen(masterList);
        rootPanel.add(landlordHomeScreen, CARD_LANDLORD_HOME);
    }

    // Getters
    public static ArrayList<BoardingHouse> getMasterList() { return masterList; }
    public static JFrame                   getFrame()      { return frame;      }
}