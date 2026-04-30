package bhsystem.project.backend;

//Represents a single student review left on a boarding house listing.

public class Review {
    // Fields
    //Unique slug ID. e.g. "sta_elena_bh_001_rev_001". Set by bhsystem.project.backend.DataManager.
    private String id;
    //Slug of the parent bhsystem.project.backend.BoardingHouse this review belong
    private String boardingHouseId;
    //Star rating given by the student. Must be between 1 and 5 inclusive
    private int starRating;
    //The date the review was posted
    private String datePosted;
    //The written comment from the student.
    private String comment;

    // Constructors
    //Creation constructor.

    public Review(int starRating, String datePosted, String comment) {
        this.id              = "";
        this.boardingHouseId = "";
        this.starRating      = starRating;
        this.datePosted      = datePosted;
        this.comment         = comment;
    }

    //Full constructor

    public Review(String id, String boardingHouseId,
                  int starRating, String datePosted, String comment) {
        this.id              = id;
        this.boardingHouseId = boardingHouseId;
        this.starRating      = starRating;
        this.datePosted      = datePosted;
        this.comment         = comment;
    }

    // Getters

    public String getId()              { return id; }
    public String getBoardingHouseId() { return boardingHouseId; }
    public int    getStarRating()      { return starRating; }
    public String getDatePosted()      { return datePosted; }
    public String getComment()         { return comment; }

    // Setters
    // Called by bhsystem.project.backend.DataManager.assignSlugAndAdd() after the slug is generated.

    public void setId(String id)                           { this.id = id; }

    //Called by bhsystem.project.backend.DataManager.assignSlugAndAdd() to link this review to itsbparent boarding house before saving.
    public void setBoardingHouseId(String boardingHouseId) {
        this.boardingHouseId = boardingHouseId;
    }

    //Allows the student to correct the star rating before the review is saved

    public void setStarRating(int starRating) { this.starRating = starRating; }

    //Allows the student to correct the comment before the review is saved

    public void setComment(String comment)    { this.comment = comment; }

    // Utility - Returns a simple readable summary of this review.

   /* @Override
    public String toString() {
        return "[bhsystem.project.backend.Review " + id
                + " | " + starRating + " stars"
                + " | " + datePosted
                + " | " + comment + "]";
    }*/
}
