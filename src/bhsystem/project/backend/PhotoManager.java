package bhsystem.project.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

//Handles all file system operations related to photos.

public class PhotoManager {
    private static final String PHOTOS_ROOT = "photos";

    // Public methods
    //Copies a photo selected by the landlord into the boarding house's subfolder inside the app's photos/ directory.

    public static String copyBoardingHousePhoto(File sourceFile,
                                                String boardingHouseId) throws IOException {
        String subfolderName = boardingHouseId;
        return copyPhotoToSubfolder(sourceFile, subfolderName);
    }

    //Copies a photo selected by the landlord into the room type subfolder inside the app's photos/ directory.
    public static String copyRoomTypePhoto(File sourceFile,
                                           String roomTypeId) throws IOException {
        String subfolderName = roomTypeId;
        return copyPhotoToSubfolder(sourceFile, subfolderName);
    }

    //Resolves a relative photo path stored in the model into an absolutebFile object that the GUI can use to load and display the image.

    public static File resolvePhotoFile(String relativePath) {
        String appRoot = System.getProperty("user.dir");
        return new File(appRoot + File.separator + relativePath.replace("/", File.separator));
    }

    //Deletes all photos belonging to a boarding house by removing its entire subfolder from the photos/ directory.
    public static void deleteBoardingHousePhotos(String boardingHouseId) {
        File subFolder = resolveSubfolder(boardingHouseId);
        deleteDirectoryRecursively(subFolder);
    }

    // Deletes all photos belonging to a specific room type by removing its subfolder from the photos/ directory.

    public static void deleteRoomTypePhotos(String roomTypeId) {
        File subFolder = resolveSubfolder(roomTypeId);
        deleteDirectoryRecursively(subFolder);
    }

    //Deletes a single photo file from disk given its relative path.
    public static void deletePhoto(String relativePath) {
        File file = resolvePhotoFile(relativePath);
        if (file.exists()) {
            file.delete();
        }
    }
    //Returns true if the given relative photo path points to a file that actually exists on disk.

    public static boolean photoExists(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }
        File file = resolvePhotoFile(relativePath);
        return file.exists() && file.isFile();
    }

    //Removes any photo paths from the given list that no longer point to existing files on disk. Returns the cleaned-up list.

    public static ArrayList<String> pruneDeadPaths(ArrayList<String> photoPaths) {
        ArrayList<String> valid = new ArrayList<>();
        for (String path : photoPaths) {
            if (photoExists(path)) {
                valid.add(path);
            }
        }
        return valid;
    }

    // Private helpers
    private static String copyPhotoToSubfolder(File sourceFile,
                                               String subfolderName) throws IOException {
        // Step 1: resolve the absolute destination folder
        File destFolder = resolveSubfolder(subfolderName);

        // Step 2: create the folder hierarchy if needed
        if (!destFolder.exists()) {
            destFolder.mkdirs();  // creates photos/ and the subfolder in one call
        }

        // Step 3: determine a safe filename with no collision
        String safeFileName = resolveFileName(sourceFile.getName(), destFolder);

        // Step 4: copy the file
        File destFile = new File(destFolder, safeFileName);
        copyFile(sourceFile, destFile);

        // Step 5: build and return the relative path using forward slashes
        return PHOTOS_ROOT + "/" + subfolderName + "/" + safeFileName;
    }
    private static File resolveSubfolder(String subfolderName) {
        String appRoot = System.getProperty("user.dir");
        return new File(appRoot + File.separator + PHOTOS_ROOT + File.separator + subfolderName);
    }

    private static String resolveFileName(String originalName, File destFolder) {
        File candidate = new File(destFolder, originalName);
        if (!candidate.exists()) {
            return originalName;
        }

        String base;
        String ext;
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            base = originalName.substring(0, dotIndex);
            ext  = originalName.substring(dotIndex);   // includes the dot
        } else {
            base = originalName;
            ext  = "";
        }

        int counter = 1;
        while (true) {
            String newName = base + "_" + counter + ext;  // e.g. cover_1.jpg
            candidate = new File(destFolder, newName);
            if (!candidate.exists()) {
                return newName;
            }
            counter++;
        }
    }

    private static void copyFile(File source, File destination) throws IOException {
        InputStream  in  = null;
        OutputStream out = null;
        try {
            in  = new FileInputStream(source);
            out = new FileOutputStream(destination);
            byte[] buffer = new byte[8192];  // 8KB chunks
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            // Close both streams even if an exception occurred.
            // Each close is in its own try block so a failure on one
            // does not prevent the other from closing.
            if (in != null) {
                try { in.close(); } catch (IOException ignored) {}
            }
            if (out != null) {
                try { out.close(); } catch (IOException ignored) {}
            }
        }
    }

    private static void deleteDirectoryRecursively(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }
        File[] contents = directory.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursively(file);  // recurse into subdirectories
                } else {
                    file.delete();                     // delete individual file
                }
            }
        }
        directory.delete();  // now delete the empty directory itself
    }
}