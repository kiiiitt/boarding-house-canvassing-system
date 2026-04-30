package bhsystem.project.backend;
import bhsystem.project.frontend.MainListingWallScreen;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DataManager {

    // File path constants
    private static final File DATA_DIR = new File("data");
    private static final File BH_FILE  = new File(DATA_DIR, "boardinghouses.csv");
    private static final File RT_FILE  = new File(DATA_DIR, "roomtypes.csv");
    private static final File REV_FILE = new File(DATA_DIR, "reviews.csv");
    private static final File FAV_FILE = new File(DATA_DIR, "favorites.csv");

    //CSV headers —
    private static final String BH_HEADER =
            "id,name,barangay,address,contactName,contactNumber," +
                    "contactEmail,contactFacebook,listingUsername," +
                    "ownerPin,rules,inclusions,photoPaths";

    private static final String RT_HEADER =
            "id,boardingHouseId,typeName,capacity,monthlyRent," +
                    "totalRooms,availableRooms,inclusions,photoPaths";

    private static final String REV_HEADER =
            "id,boardingHouseId,starRating,datePosted,comment";

    //Split limits
    private static final int BH_COLS  = 13;   // was 10
    private static final int RT_COLS  = 9;
    private static final int REV_COLS = 5;

    // load, save, create neede csv files for teh data of the app
    // Loads all data from the four CSV files and assembles the master list.

    public static ArrayList<BoardingHouse> loadAll() {
        ArrayList<BoardingHouse> masterList = loadBoardingHouses();
        attachRoomTypes(masterList);
        attachReviews(masterList);
        applyFavorites(masterList);
        pruneAllPhotoPaths(masterList);
        return masterList;
    }

    // Writes the entire current state of the master list to all four CSV files.

    public static void saveAll(ArrayList<BoardingHouse> masterList) {
        ensureDataFolder();
        saveBoardingHouses(masterList);
        saveRoomTypes(masterList);
        saveReviews(masterList);
        saveFavorites(masterList);
    }

    //Rewrites only favorites.csv.

    public static void saveFavorites(ArrayList<BoardingHouse> masterList) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(FAV_FILE));
            for (BoardingHouse bh : masterList) {
                if (bh.isFavorited()) {
                    writer.println(bh.getId());
                }
            }
        } catch (IOException e) {
            System.err.println("DataManager: failed to write favorites.csv — " + e.getMessage());
        } finally {
            if (writer != null) writer.close();
        }
    }

    // assignSlugAndAdd — BoardingHouse
    // Creates a new boarding house record: generates slug, assigns ID, adds to master list, saves to disk.

    public static void assignSlugAndAdd(BoardingHouse bh,
                                        ArrayList<BoardingHouse> masterList) {
        ArrayList<String> existingSlugs = extractBoardingHouseSlugs(masterList);
        String slug = SlugGenerator.generateBoardingHouseSlug(bh.getName(), existingSlugs);
        bh.setId(slug);
        masterList.add(bh);
        saveAll(masterList);
    }

    // assignSlugAndAdd RoomType
    // Creates a new room type record: generates slug, assigns IDs, links to parent BH, saves to disk.

    public static void assignSlugAndAdd(RoomType rt, BoardingHouse bh,
                                        ArrayList<BoardingHouse> masterList) {
        ArrayList<String> existingSlugs = extractRoomTypeSlugs(masterList);
        String slug = SlugGenerator.generateRoomTypeSlug(bh.getId(), rt.getTypeName(), existingSlugs);
        rt.setId(slug);
        rt.setBoardingHouseId(bh.getId());
        bh.addRoomType(rt);
        saveAll(masterList);
    }

    // assignSlugAndAdd — Review
    // Creates a new review record: generates slug, assigns IDs, links to parent BH, saves to disk.
    public static void assignSlugAndAdd(Review review, BoardingHouse bh,
                                        ArrayList<BoardingHouse> masterList) {
        ArrayList<String> existingSlugs = extractReviewSlugs(masterList);
        String slug = SlugGenerator.generateReviewSlug(bh.getId(), existingSlugs);
        review.setId(slug);
        review.setBoardingHouseId(bh.getId());
        bh.addReview(review);
        saveAll(masterList);
    }

    // PRIVATE — load helpers

    // Reads boardinghouses.csv and returns one BoardingHouse per data row.
    private static ArrayList<BoardingHouse> loadBoardingHouses() {
        ArrayList<BoardingHouse> list = new ArrayList<>();
        if (!BH_FILE.exists()) return list;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(BH_FILE));
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] p = line.split(",", BH_COLS);
                if (p.length < BH_COLS) continue; // skip malformed rows silently

                String            id              = p[0].trim();
                String            name            = p[1].trim();
                String            barangay        = p[2].trim();
                String            address         = p[3].trim();
                String            contactName     = p[4].trim();
                String            contactNumber   = p[5].trim();
                String            contactEmail    = p[6].trim();
                String            contactFacebook = p[7].trim();
                String            listingUsername = p[8].trim();
                String            ownerPin        = p[9].trim();
                ArrayList<String> rules           = splitPipe(p[10].trim());
                ArrayList<String> inclusions      = splitPipe(p[11].trim());
                ArrayList<String> photoPaths      = splitPipe(p[12].trim());

                BoardingHouse bh = new BoardingHouse(
                        id, name, barangay, address,
                        contactName, contactNumber,
                        contactEmail, contactFacebook, listingUsername,
                        ownerPin, rules, inclusions, photoPaths
                );
                list.add(bh);
            }
        } catch (IOException e) {
            System.err.println("DataManager: failed to read boardinghouses.csv — " + e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
        return list;
    }

    //  Reads roomtypes.csv and attaches each RoomType to its parent BoardingHouse.
    private static void attachRoomTypes(ArrayList<BoardingHouse> masterList) {
        if (!RT_FILE.exists()) return;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(RT_FILE));
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] p = line.split(",", RT_COLS);
                if (p.length < RT_COLS) continue;

                String            id              = p[0].trim();
                String            boardingHouseId = p[1].trim();
                String            typeName        = p[2].trim();
                int               capacity        = Integer.parseInt(p[3].trim());
                double            monthlyRent     = Double.parseDouble(p[4].trim());
                int               totalRooms      = Integer.parseInt(p[5].trim());
                int               availableRooms  = Integer.parseInt(p[6].trim());
                ArrayList<String> inclusions      = splitPipe(p[7].trim());
                ArrayList<String> photoPaths      = splitPipe(p[8].trim());

                RoomType rt = new RoomType(
                        id, boardingHouseId, typeName,
                        capacity, monthlyRent,
                        totalRooms, availableRooms,
                        inclusions, photoPaths
                );

                BoardingHouse parent = findById(masterList, boardingHouseId);
                if (parent != null) parent.addRoomType(rt);
            }
        } catch (IOException e) {
            System.err.println("DataManager: failed to read roomtypes.csv — " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("DataManager: malformed number in roomtypes.csv — " + e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
    }

    // Reads reviews.csv and attaches each Review to its parent BoardingHouse.
    private static void attachReviews(ArrayList<BoardingHouse> masterList) {
        if (!REV_FILE.exists()) return;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(REV_FILE));
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] p = line.split(",", REV_COLS);
                if (p.length < REV_COLS) continue;

                String id              = p[0].trim();
                String boardingHouseId = p[1].trim();
                int    starRating      = Integer.parseInt(p[2].trim());
                String datePosted      = p[3].trim();
                String comment         = p[4];

                Review review = new Review(id, boardingHouseId, starRating, datePosted, comment);

                BoardingHouse parent = findById(masterList, boardingHouseId);
                if (parent != null) parent.addReview(review);
            }
        } catch (IOException e) {
            System.err.println("DataManager: failed to read reviews.csv — " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("DataManager: malformed number in reviews.csv — " + e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
    }

    // reads favorites.csv and marks matching BoardingHouse objects as favorite adds to fav dashboard.
    private static void applyFavorites(ArrayList<BoardingHouse> masterList) {
        if (!FAV_FILE.exists()) return;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(FAV_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                BoardingHouse bh = findById(masterList, line);
                if (bh != null) bh.setFavorited(true);
            }
        } catch (IOException e) {
            System.err.println("DataManager: failed to read favorites.csv — " + e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
    }

    //prune All Photo Paths
    private static void pruneAllPhotoPaths(ArrayList<BoardingHouse> masterList) {
        boolean anyPruned = false;

        for (BoardingHouse bh : masterList) {
            ArrayList<String> cleaned = PhotoManager.pruneDeadPaths(bh.getPhotoPaths());
            if (cleaned.size() != bh.getPhotoPaths().size()) {
                bh.setPhotoPaths(cleaned);
                anyPruned = true;
            }
            for (RoomType rt : bh.getRoomTypes()) {
                ArrayList<String> cleanedRt = PhotoManager.pruneDeadPaths(rt.getPhotoPaths());
                if (cleanedRt.size() != rt.getPhotoPaths().size()) {
                    rt.setPhotoPaths(cleanedRt);
                    anyPruned = true;
                }
            }
        }

        if (anyPruned) saveAll(masterList);
    }

    // PRIVATE — save helpers
     //Writes boardinghouses.csv

    private static void saveBoardingHouses(ArrayList<BoardingHouse> masterList) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(BH_FILE));
            writer.println(BH_HEADER);
            for (BoardingHouse bh : masterList) {
                writer.println(
                        bh.getId()              + "," +
                                bh.getName()            + "," +
                                bh.getBarangay()        + "," +
                                bh.getAddress()         + "," +
                                bh.getContactName()     + "," +
                                bh.getContactNumber()   + "," +
                                bh.getContactEmail()    + "," +
                                bh.getContactFacebook() + "," +
                                bh.getListingUsername() + "," +
                                bh.getOwnerPin()        + "," +
                                joinPipe(bh.getRules())       + "," +
                                joinPipe(bh.getInclusions())  + "," +
                                joinPipe(bh.getPhotoPaths())
                );
            }
        } catch (IOException e) {
            System.err.println("DataManager: failed to write boardinghouses.csv — " + e.getMessage());
        } finally {
            if (writer != null) writer.close();
        }
    }

     // Writes roomtypes.csv

    private static void saveRoomTypes(ArrayList<BoardingHouse> masterList) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(RT_FILE));
            writer.println(RT_HEADER);
            for (BoardingHouse bh : masterList) {
                for (RoomType rt : bh.getRoomTypes()) {
                    writer.println(
                            rt.getId()              + "," +
                                    rt.getBoardingHouseId() + "," +
                                    rt.getTypeName()        + "," +
                                    rt.getCapacity()        + "," +
                                    rt.getMonthlyRent()     + "," +
                                    rt.getTotalRooms()      + "," +
                                    rt.getAvailableRooms()  + "," +
                                    joinPipe(rt.getInclusions()) + "," +
                                    joinPipe(rt.getPhotoPaths())
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("DataManager: failed to write roomtypes.csv — " + e.getMessage());
        } finally {
            if (writer != null) writer.close();
        }
    }

    // Writes reviews.csv

    private static void saveReviews(ArrayList<BoardingHouse> masterList) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(REV_FILE));
            writer.println(REV_HEADER);
            for (BoardingHouse bh : masterList) {
                for (Review review : bh.getReviews()) {
                    writer.println(
                            review.getId()              + "," +
                                    review.getBoardingHouseId() + "," +
                                    review.getStarRating()      + "," +
                                    review.getDatePosted()      + "," +
                                    review.getComment()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("DataManager: failed to write reviews.csv — " + e.getMessage());
        } finally {
            if (writer != null) writer.close();
        }
    }

    // PRIVATE — slug extraction helpers

    private static ArrayList<String> extractBoardingHouseSlugs(ArrayList<BoardingHouse> masterList) {
        ArrayList<String> slugs = new ArrayList<>();
        for (BoardingHouse bh : masterList) slugs.add(bh.getId());
        return slugs;
    }

    private static ArrayList<String> extractRoomTypeSlugs(ArrayList<BoardingHouse> masterList) {
        ArrayList<String> slugs = new ArrayList<>();
        for (BoardingHouse bh : masterList)
            for (RoomType rt : bh.getRoomTypes()) slugs.add(rt.getId());
        return slugs;
    }

    private static ArrayList<String> extractReviewSlugs(ArrayList<BoardingHouse> masterList) {
        ArrayList<String> slugs = new ArrayList<>();
        for (BoardingHouse bh : masterList)
            for (Review review : bh.getReviews()) slugs.add(review.getId());
        return slugs;
    }

    // PRIVATE — general helpers

    private static BoardingHouse findById(ArrayList<BoardingHouse> masterList, String id) {
        for (BoardingHouse bh : masterList) {
            if (bh.getId().equals(id)) return bh;
        }
        return null;
    }

    private static void ensureDataFolder() {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
    }

    private static String joinPipe(ArrayList<String> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append("|");
        }
        return sb.toString();
    }

    private static ArrayList<String> splitPipe(String cell) {
        ArrayList<String> list = new ArrayList<>();
        if (cell == null || cell.trim().isEmpty()) return list;
        String[] parts = cell.split("\\|");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        return list;
    }
}
