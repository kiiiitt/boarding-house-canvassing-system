package bhsystem.project.backend;

import java.util.ArrayList;

//Responsible for creating all unique string IDs (slugs) used across the app. Slugs serve as primary keys in the CSV files and as photo subfolder names.

public class SlugGenerator {
    // Public slug generation methods
    //Generates a unique slug for a new bhsystem.project.backend.BoardingHouse.

    public static String generateBoardingHouseSlug(String name, ArrayList<String> existingSlugs) {
        String base = toBoardingHouseSlugBase(name);
        String counter = nextCounter(base, existingSlugs);
        return base + "_" + counter;
    }

    //Generates a unique slug for a new bhsystem.project.backend.RoomType.
    public static String generateRoomTypeSlug(String boardingHouseId, String typeName,
                                              ArrayList<String> existingSlugs) {
        String typeBase = toSlugBase(typeName);
        String prefix = boardingHouseId + "_" + typeBase;
        String counter = nextCounter(prefix, existingSlugs);
        return prefix + "_" + counter;
    }

    //Generates a unique slug for a new bhsystem.project.backend.Review.

    public static String generateReviewSlug(String boardingHouseId,
                                            ArrayList<String> existingSlugs) {
        String prefix = boardingHouseId + "_rev";
        String counter = nextCounter(prefix, existingSlugs);
        return prefix + "_" + counter;
    }

    // Private helpers

    //Converts a boarding house name into a URL-safe slug base.
    private static String toBoardingHouseSlugBase(String name) {
        String s = name.toLowerCase();
        s = s.replace("boarding house", "bh");  // abbreviate before stripping
        return toSlugBase(s);
    }

    //Generic slug base converter used for room type names and as them final clean-up step inside toBoardingHouseSlugBase.
    private static String toSlugBase(String text) {
        String s = text.toLowerCase();
        s = s.replaceAll("[^a-z0-9 ]", " ");  // keep only letters, digits, spaces
        s = s.trim();
        s = s.replaceAll("\\s+", "_");         // spaces to single underscore
        s = s.replaceAll("_+", "_");           // collapse double underscores
        s = s.replaceAll("^_|_$", "");         // strip leading/trailing underscores
        return s;
    }

    //Finds the next available three-digit counter for a given slug prefix.
    private static String nextCounter(String prefix, ArrayList<String> existingSlugs) {
        String searchPrefix = prefix + "_";
        int highest = 0;

        for (String slug : existingSlugs) {
            if (slug.startsWith(searchPrefix)) {
                // Extract the portion after the prefix
                String remainder = slug.substring(searchPrefix.length());

                // We only want slugs where the remainder IS exactly three digits
                if (remainder.matches("\\d{3}")) {
                    int num = Integer.parseInt(remainder);
                    if (num > highest) {
                        highest = num;
                    }
                }
            }
        }

        // Return the next number after the highest found, zero-padded to 3 digits
        return String.format("%03d", highest + 1);
    }
}
