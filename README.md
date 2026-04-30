# Boarding House Canvassing System
### MSU-IIT — BSIT 1st Year Project

A java app where students can browse and canvass boarding houses near the university. Landlords can list and manage their properties. Everything runs locally — no internet needed.

---

## What the app does

**For Students**
- Browse all boarding house listings
- Filter by area, room type, price, and availability
- View full details — photos, room types, inclusions, rules
- Leave star ratings and reviews
- Save listings to favorites

**For Landlords**
- Create a listing with a username and 4-digit PIN
- Manage boarding house info, room types, and photos
- Upload and delete photos directly from the app
- Change your PIN anytime

---

## How to run it

Running the JAR (if provided)**
1. Extract the zip file — keep all folders together, do not move files around
2. Double-click `BoardingHouseApp.jar`, or open a terminal inside the folder and run:
```
java -jar BoardingHouseApp.jar
```

> The `data/` and `photos/` folders are created automatically the first time you use the app.

---

## Important notes

- **Keep the folder together.** Photos are stored locally inside the `photos/` folder. If you move just the JAR without the rest of the folder, photos will not load.
- **Don't forget your PIN.** Each listing has its own 4-digit PIN. If you forget it, you'll need to manually open `data/boardinghouses.csv` and find the `ownerPin` column to recover it.
- **Don't delete the data, photo, and PNG folder.** All listings, reviews, and favorites are stored in the `data/` folder. Deleting it resets everything.

---

## Project structure

```
src/bhsystem/project/
├── backend/          ← data models and file management (don't touch unless fixing a bug)
│   ├── BoardingHouse.java
│   ├── RoomType.java
│   ├── Review.java
│   ├── DataManager.java
│   ├── PhotoManager.java
│   └── SlugGenerator.java
└── frontend/         ← all screens and UI
    ├── App.java                  ← entry point, run this
    ├── AppColors.java            ← color palette
    ├── ModeSelectionScreen.java
    ├── MainListingWallScreen.java
    ├── FullDetailsScreen.java
    ├── LandlordHomeScreen.java
    ├── CreateListingScreen.java
    ├── ManageListingScreen.java
    ├── PhotoManagerDialog.java
    ├── PhotoViewerDialog.java
    ├── AddReviewDialog.java
    ├── PinEntryDialog.java
    └── EditRoomTypeDialog.java
```

---

## Tech used

| | |
|---|---|
| Language | Java |
| UI | Java Swing |
| Data storage | CSV files |
| Photos | Local folder, relative paths |

---

## Known limitations

- No internet or sync — everything is local to the machine running the app
- No PIN recovery — forgotten PINs must be manually fixed in `data/boardinghouses.csv`
- App must always be run from inside its extracted folder so file paths work correctly

---

*Developed by BSIT 1st Year students — MSU-IIT*
