package bhsystem.project.backend;

import java.util.ArrayList;

 //Represents one category of room offered by a boarding house. A single boarding house can have multiple RoomType entries

public class RoomType {

    // Fields
    //Unique slug Also used as the photo subfolder name for this room type.
    private String id;
    //Slug of the parent bhsystem.project.backend.BoardingHouse this room type belongs to.
    private String boardingHouseId;
    //Display name of the room type as entered by the landlord
    private String typeName;
    //Maximum number of occupants t
    private int capacity;
    //Monthly rental price
    private double monthlyRent;
    //Total number of rooms
    private int totalRooms;
    //Number of rooms of this type currently available.
    private int availableRooms;
    // inclusions
    private ArrayList<String> inclusions;
    //Relative paths to photos for this specific room type.
    private ArrayList<String> photoPaths;

    // Constructors
    public RoomType(String typeName, int capacity, double monthlyRent,
                    int totalRooms, int availableRooms,
                    ArrayList<String> inclusions, ArrayList<String> photoPaths) {
        this.id              = "";
        this.boardingHouseId = "";
        this.typeName        = typeName;
        this.capacity        = capacity;
        this.monthlyRent     = monthlyRent;
        this.totalRooms      = totalRooms;
        this.availableRooms  = availableRooms;
        this.inclusions      = inclusions;
        this.photoPaths      = photoPaths;
    }

    //Full constructor.
    public RoomType(String id, String boardingHouseId,
                    String typeName, int capacity, double monthlyRent,
                    int totalRooms, int availableRooms,
                    ArrayList<String> inclusions, ArrayList<String> photoPaths) {
        this.id              = id;
        this.boardingHouseId = boardingHouseId;
        this.typeName        = typeName;
        this.capacity        = capacity;
        this.monthlyRent     = monthlyRent;
        this.totalRooms      = totalRooms;
        this.availableRooms  = availableRooms;
        this.inclusions      = inclusions;
        this.photoPaths      = photoPaths;
    }

    // Getters
    public String            getId()             { return id; }
    public String            getBoardingHouseId() { return boardingHouseId; }
    public String            getTypeName()       { return typeName; }
    public int               getCapacity()       { return capacity; }
    public double            getMonthlyRent()    { return monthlyRent; }
    public int               getTotalRooms()     { return totalRooms; }
    public int               getAvailableRooms() { return availableRooms; }
    public ArrayList<String> getInclusions()     { return inclusions; }
    public ArrayList<String> getPhotoPaths()     { return photoPaths; }

    // Setters
    public void setId(String id)                           { this.id = id; }

    //Called by bhsystem.project.backend.DataManager.assignSlugAndAdd() to link this room type to its parent boarding house before saving.
    public void setBoardingHouseId(String boardingHouseId) {
        this.boardingHouseId = boardingHouseId;
    }

    //Updated by the landlord via the Manage Listing screen.
    public void setTypeName(String typeName)           { this.typeName = typeName; }

    //Updated by the landlord if the room capacity changes.
    public void setCapacity(int capacity)              { this.capacity = capacity; }

    //Updated by the landlord when adjusting the rental price.
    public void setMonthlyRent(double monthlyRent)     { this.monthlyRent = monthlyRent; }

    //Updated by the landlord when the total room count changes.
    public void setTotalRooms(int totalRooms)          { this.totalRooms = totalRooms; }

    //Updated by the landlord when a room is rented out or becomes free again
    public void setAvailableRooms(int availableRooms)  { this.availableRooms = availableRooms; }

    //Replaces the full inclusions list
    public void setInclusions(ArrayList<String> inclusions) {
        this.inclusions = inclusions;
    }

    //Replaces the full photo paths list.
    public void setPhotoPaths(ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
    }
    // Convenience methods
    //Returns true if at least one room of this type is currently available.
    public boolean isAvailable() {
        return availableRooms > 0;
    }

    //Returns the relative path of the first photo in the photoPaths list
    public String getCoverPhotoPath() {
        if (photoPaths == null || photoPaths.isEmpty()) {
            return null;
        }
        return photoPaths.get(0);
    }

    public void addPhotoPath(String path) {
        photoPaths.add(path);
    }

    //Removes a single photo path from the photoPaths list by value.
    public void removePhotoPath(String path) {
        photoPaths.remove(path);
    }

   /* @Override
    public String toString() {
        return "[bhsystem.project.backend.RoomType " + id
                + " | " + typeName
                + " | cap:" + capacity
                + " | \u20b1" + monthlyRent
                + " | " + availableRooms + "/" + totalRooms + " available]";
    }*/
}
