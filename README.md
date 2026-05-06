# Boarding House Canvassing System 

A Java desktop app where students can browse and canvass boarding houses near the university. Landlords can list and manage their properties. Everything runs locally — no internet needed.

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

### Steps
1. Clone the repository
```
git clone https://github.com/your-username/BHSystem.git
```
2. Open the project in IntelliJ — **File → Open** and select the `BHSystem` folder
3. Make sure the `resources/` folder is marked as a Resources Root — right-click it → **Mark Directory as → Resources Root**
4. Run `App.java` inside `src/bhsystem/project/frontend/`

> The `data/` and `photos/` folders are created automatically the first time you run the app.

---

## Project structure

```
BHSystem/
├── resources/
│   └── png_icons/            ← all icon PNGs used throughout the app
├── src/bhsystem/project/
│   ├── backend/              ← data models and file management
│   │   ├── BoardingHouse.java
│   │   ├── RoomType.java
│   │   ├── Review.java
│   │   ├── DataManager.java
│   │   ├── PhotoManager.java
│   │   └── SlugGenerator.java
│   └── frontend/             ← all screens and UI
│       ├── App.java                  ← entry point, run this
│       ├── AppColors.java            ← color palette
│       ├── PNGLoader.java            ← utility for loading and scaling PNG icons
│       ├── ModeSelectionScreen.java
│       ├── MainListingWallScreen.java
│       ├── FullDetailsScreen.java
│       ├── LandlordHomeScreen.java
│       ├── CreateListingScreen.java
│       ├── ManageListingScreen.java
│       ├── PhotoManagerDialog.java
│       ├── PhotoViewerDialog.java
│       ├── AddReviewDialog.java
│       ├── PinEntryDialog.java
│       └── EditRoomTypeDialog.java
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
- App must always be run from inside its folder so relative file paths work correctly

---
