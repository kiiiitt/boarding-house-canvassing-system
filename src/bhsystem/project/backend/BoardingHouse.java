package bhsystem.project.backend;
import java.util.ArrayList;
public class BoardingHouse {

    // Fields

    private String id;
    private String name;
    private String barangay;
    private String address;
    private String contactName;
    private String contactNumber;

    //Optional contact email.
    private String contactEmail;

    //Optional Facebook page or profile name.
    private String contactFacebook;

     //Unique username set by landlord during listing creation.

    private String listingUsername;
    private String ownerPin;
    private ArrayList<String> rules;
    private ArrayList<String> inclusions;
    private ArrayList<String> photoPaths;
    private ArrayList<RoomType> roomTypes;
    private ArrayList<Review> reviews;
    private boolean isFavorited;

    // Constructors

    public BoardingHouse(String name, String barangay, String address,
                         String contactName, String contactNumber,
                         String contactEmail, String contactFacebook,
                         String listingUsername, String ownerPin,
                         ArrayList<String> rules, ArrayList<String> inclusions,
                         ArrayList<String> photoPaths) {
        this.id              = "";
        this.name            = name;
        this.barangay        = barangay;
        this.address         = address;
        this.contactName     = contactName;
        this.contactNumber   = contactNumber;
        this.contactEmail    = contactEmail    != null ? contactEmail    : "";
        this.contactFacebook = contactFacebook != null ? contactFacebook : "";
        this.listingUsername = listingUsername != null ? listingUsername : "";
        this.ownerPin        = ownerPin;
        this.rules           = rules;
        this.inclusions      = inclusions;
        this.photoPaths      = photoPaths;
        this.roomTypes       = new ArrayList<>();
        this.reviews         = new ArrayList<>();
        this.isFavorited     = false;
    }

     //Full constructor
    public BoardingHouse(String id, String name, String barangay, String address,
                         String contactName, String contactNumber,
                         String contactEmail, String contactFacebook,
                         String listingUsername, String ownerPin,
                         ArrayList<String> rules, ArrayList<String> inclusions,
                         ArrayList<String> photoPaths) {
        this.id              = id;
        this.name            = name;
        this.barangay        = barangay;
        this.address         = address;
        this.contactName     = contactName;
        this.contactNumber   = contactNumber;
        this.contactEmail    = contactEmail    != null ? contactEmail    : "";
        this.contactFacebook = contactFacebook != null ? contactFacebook : "";
        this.listingUsername = listingUsername != null ? listingUsername : "";
        this.ownerPin        = ownerPin;
        this.rules           = rules;
        this.inclusions      = inclusions;
        this.photoPaths      = photoPaths;
        this.roomTypes       = new ArrayList<>();
        this.reviews         = new ArrayList<>();
        this.isFavorited     = false;
    }

    // Getters

    public String              getId()              { return id; }
    public String              getName()            { return name; }
    public String              getBarangay()        { return barangay; }
    public String              getAddress()         { return address; }
    public String              getContactName()     { return contactName; }
    public String              getContactNumber()   { return contactNumber; }
    public String              getContactEmail()    { return contactEmail; }
    public String              getContactFacebook() { return contactFacebook; }
    public String              getListingUsername() { return listingUsername; }
    public String              getOwnerPin()        { return ownerPin; }
    public ArrayList<String>   getRules()           { return rules; }
    public ArrayList<String>   getInclusions()      { return inclusions; }
    public ArrayList<String>   getPhotoPaths()      { return photoPaths; }
    public ArrayList<RoomType> getRoomTypes()       { return roomTypes; }
    public ArrayList<Review>   getReviews()         { return reviews; }
    public boolean             isFavorited()        { return isFavorited; }

    // Setters

    //Set only by DataManager
    public void setId(String id)                           { this.id = id; }
    public void setName(String name)                       { this.name = name; }
    public void setBarangay(String barangay)               { this.barangay = barangay; }
    public void setAddress(String address)                 { this.address = address; }
    public void setContactName(String contactName)         { this.contactName = contactName; }
    public void setContactNumber(String contactNumber)     { this.contactNumber = contactNumber; }
    public void setContactEmail(String contactEmail)       { this.contactEmail = contactEmail != null ? contactEmail : ""; }
    public void setContactFacebook(String contactFacebook) { this.contactFacebook = contactFacebook != null ? contactFacebook : ""; }
    public void setListingUsername(String listingUsername)  { this.listingUsername = listingUsername != null ? listingUsername : ""; }
    public void setOwnerPin(String ownerPin)               { this.ownerPin = ownerPin; }
    public void setRules(ArrayList<String> rules)          { this.rules = rules; }
    public void setInclusions(ArrayList<String> inclusions){ this.inclusions = inclusions; }
    public void setPhotoPaths(ArrayList<String> photoPaths){ this.photoPaths = photoPaths; }
    public void setRoomTypes(ArrayList<RoomType> roomTypes){ this.roomTypes = roomTypes; }
    public void setReviews(ArrayList<Review> reviews)      { this.reviews = reviews; }
    public void setFavorited(boolean favorited)            { this.isFavorited = favorited; }

    // Computations

    //Returns average star rating from all reviews. Returns 0.0 if no reviews.
    public double getOverallRating() {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        int total = 0;
        for (Review review : reviews) total += review.getStarRating();
        return (double) total / reviews.size();
    }

    // Returns the number of reviews posted.
    public int getReviewCount() {
        if (reviews == null) return 0;
        return reviews.size();
    }

    // Convenience methods (unchanged from original)

    public boolean hasAvailableRooms() {
        if (roomTypes == null || roomTypes.isEmpty()) return false;
        for (RoomType rt : roomTypes) {
            if (rt.isAvailable()) return true;
        }
        return false;
    }

    public boolean hasPriceInRange(double minPrice, double maxPrice) {
        if (roomTypes == null || roomTypes.isEmpty()) return false;
        for (RoomType rt : roomTypes) {
            double rent = rt.getMonthlyRent();
            if (rent >= minPrice && rent <= maxPrice) return true;
        }
        return false;
    }

    public boolean hasRoomWithCapacity(int capacity) {
        if (roomTypes == null || roomTypes.isEmpty()) return false;
        for (RoomType rt : roomTypes) {
            if (rt.getCapacity() == capacity) return true;
        }
        return false;
    }

    // Returns first photo path (cover photo), or null if no photos uploaded.
    public String getCoverPhotoPath() {
        if (photoPaths == null || photoPaths.isEmpty()) return null;
        return photoPaths.get(0);
    }

    public void addPhotoPath(String path)    { photoPaths.add(path); }
    public void removePhotoPath(String path) { photoPaths.remove(path); }
    public void addRoomType(RoomType rt)     { roomTypes.add(rt); }
    public void removeRoomType(RoomType rt)  { roomTypes.remove(rt); }
    public void addReview(Review review)     { reviews.add(review); }

    //Returns true if inputPin exactly matches ownerPin.
    public boolean checkPin(String inputPin) {
        return this.ownerPin.equals(inputPin);
    }

   /* @Override
    public String toString() {
        return "[BoardingHouse " + id
                + " | " + name
                + " | @" + listingUsername
                + " | Brgy. " + barangay
                + " | " + (roomTypes != null ? roomTypes.size() : 0) + " room types"
                + " | " + (reviews != null ? reviews.size() : 0) + " reviews"
                + " | rating: " + String.format("%.1f", getOverallRating()) + "]";
    }*/
}