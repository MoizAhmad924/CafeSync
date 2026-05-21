# CafeSync — Project Report

This document describes the CafeSync POS project: architecture, data models, persistence, GUI screens and user flows, analytics logic, utilities, and notable implementation details. Reading this should allow a developer or reviewer to understand the core logic and how the pieces interact.

---

**Table of Contents**
- Overview
- Project Structure
- Data Models
- Persistence / Repositories (CSV format)
- GUI Screens & User Flows
- Analytics & Reporting Logic
- Utilities & Security
- Enums & Constants
- Data files
- Running the app
- Design decisions & assumptions
- Potential improvements

---

**Overview**

CafeSync is a small desktop Java Swing POS (Point Of Sale) application with two primary roles: `CASHIER` and `MANAGER`.

- Cashiers use the POS screen to browse menu items, build a cart, and complete payments (creating orders).
- Managers can manage the menu (CRUD on menu items) and view analytics (KPIs, charts, top selling items, order status breakdown).

Persistence is implemented using CSV files under the `data/` folder: `menu_items.csv`, `orders.csv`, `users.csv`.

---

**Project Structure (high-level)**

- `src/app/Main.java` — application entry point; sets up sample menu item and launches `LoginScreen`.
- `src/models/` — domain models: `MenuItem`, `Order`, `OrderItem`, `User`.
- `src/repository/` — CSV-based repositories: `MenuRepository`, `OrderRepository`, `UserRepository` implementing `interfaces/CSVRepository<T>`.
- `src/gui/` — Swing-based UI screens: `LoginScreen`, `POSScreen`, `PaymentScreen`, `MenuManagement`, `OrderManagementScreen`, `AnalyticsScreen`.
- `src/analytics/AnalyticsManager.java` — statistics and KPIs derived from orders.
- `src/utils/HashUtility.java` — SHA-256 hashing helper used for user passwords.
- `data/` — CSV data files used by repositories.

---

**Data Models**

1) `MenuItem`
- Fields: `itemName`, `menuItemID`, `price`, `description`, `category`, `imageUrl`, `isAvailable`, `preparationTime`, `servingSize`.
- ID generation: default constructor uses `"MI" + System.currentTimeMillis()` to create a unique-ish ID. There is an alternative constructor that accepts an explicit `menuItemID` (used for edits and seeded data).
- Validation: name, description, category, imageUrl non-null and non-empty; price, prep time, serving size non-negative.

2) `OrderItem`
- Fields: `MenuItem menuItem`, `int quantity`, `double subTotal`.
- Quantity mutators update `subTotal` based on `menuItem.getPrice()`.

3) `Order`
- Fields: `orderDateTime`, `orderDate`, `orderTime`, `orderID`, `List<OrderItem> orderItems`, `totalPrice`, `OrderType orderType`, `userName`, `customerName`, `customerContact`, `deliveryAddress`, `orderStatus`.
- Constructors:
  - Primary constructor takes `userName`, `orderType`, `customerName`, `customerContact`, `deliveryAddress`, `orderItems`. It sets date/time and order ID automatically and initializes `totalPrice` and `orderStatus`.
  - CSV-backed constructor accepts explicit `orderID` and `orderDateTime` strings for deserialization.
- Order item management: `addOrderItem(MenuItem)` increments quantity if item exists; `removeOrderItem`, `decrementOrderItem` adjust totals.
- Status transitions: `cancelOrder()`, `completeOrder()`, `outForDelivery()`, `prepareOrder()` which set `orderStatus` to appropriate `OrderStatus` enum value.

4) `User`
- Fields: `name`, `cnic`, `dateOfBirth`, `phoneNo`, `address`, `userRole`, `username`, `password`.
- Validation: CNIC format `\d{5}-\d{7}-\d{1}`, phone is 11 digits, username minimum length and no spaces.
- Password handling: `setPassword` hashes using SHA-256 via `HashUtility.hashWithSHA256`; `verifyPassword` hashes the login attempt and compares.

---

**Persistence / Repositories**

All repositories implement `interfaces.CSVRepository<T>` with the usual CRUD methods: `save`, `findById`, `findAll`, `update`, `delete`.

General CSV patterns used:
- Each repository defines `FILE_PATH` (e.g., `data/menu_items.csv`) and a `HEADER` line.
- Serialization: object → CSV string (comma-separated). For composite fields (lists) a custom string format is used.
- Deserialization: CSV line → object, with parsing of numeric values and enum lookups (`Category.valueOf(...)`, etc.).
- `save()` appends to CSV and writes header if file is empty.
- `findAll()` reads the file (skips header if present), deserializes each line into model objects.
- `update()` and `delete()` load all, modify the list in-memory, then `rewriteAll()` which overwrites the CSV file with the header plus all entries.

Repository-specific details:

- `MenuRepository`
  - CSV columns: `itemName,menuItemID,price,description,category,imageUrl,preparationTime,servingSize`
  - `serialize(MenuItem)` and `deserialize(String line)` map fields directly.

- `OrderRepository`
  - CSV columns: `orderID, Date, userName, orderType, customerName, customerContact, deliveryAddress, orderItems, totalPrice, orderStatus`
  - Order items are serialized into a single CSV column using `menuItemID:quantity;` separators (semicolon-separated items; colon between ID and quantity). Example: `MI1600000000000:2;MI1600000000020:1;`
  - During deserialization, repository looks up `MenuItem` instances by ID using `MenuRepository.findById` so `menu_items.csv` must contain those IDs.
  - `findOrdersInRange(LocalDate start, LocalDate end)` filters orders by `orderDate` inclusive.

- `UserRepository`
  - CSV columns: `name,cnic,dob,phone,address,role,username,password`
  - Password column stores the hashed password string (SHA-256 hex).

Notes / Caveats:
- CSV parsing uses simple `String.split(",")` which can fail if any fields contain commas. The code assumes data does not include embedded commas.
- Header writing/rewrite overwrites the entire file — concurrent writes are not handled.

---

**GUI Screens & User Flows**

1) `LoginScreen`
- Fields: `usernameField`, `passwordField`, `roleComboBox`.
- Backend: looks up user via `UserRepository.findById(enteredUsername)`.
- Password verification: uses `User.verifyPassword` which uses `HashUtility`.
- Role-check: ensures stored `user.getUserRole()` equals selected role.
- On success:
  - `Role.CASHIER` → opens `POSScreen(user)` (closes login window).
  - `Role.MANAGER` → opens `MenuManagement(user)`.

2) `POSScreen` (Cashier flow)
- Displays menu items in a 3-column grid using `MenuRepository.findAll()`.
- Each menu card: image (or placeholder), name, description, prep/serving meta, price, `+ Add to Cart` button.
- Cart panel (right): lists `OrderItem` rows with quantity controls (+, -, X), subtotal, total.
- Cart operations: `addToCart`, `changeQty`, `removeFromCart`, `clearCart`, `refreshCart`.
- Confirm Payment button opens `PaymentScreen(orderItems, this)` and hides POS window.
- Also has header buttons: `Logout` and `Orders` (opens `OrderManagementScreen`).

3) `PaymentScreen`
- Builds an `Order` with: cashier username (from parent POS), selected `OrderType` (TAKEAWAY/DELIVERY), customer info, delivery address (visible only when `DELIVERY` chosen), selected payment method.
- On `Confirm & Pay`:
  - Creates `Order` and `orderRepo.save(newOrder)` which appends to `data/orders.csv`.
  - Shows confirmation dialog with order details and returns to a fresh `POSScreen` instance for the cashier.

4) `MenuManagement` (Manager flow)
- Shows existing menu items in a grid and provides a form (right pane) to add or edit items.
- Add item: validates fields (non-empty, numeric fields parse correctly), creates `MenuItem` and calls `menuRepo.save(item)`, and appends to the UI.
- Edit: populates form with selected item, toggles `editMode`, updates via `menuRepo.update(item)`.
- Delete: prompts confirmation and calls `menuRepo.delete(item.getID())`.

5) `OrderManagementScreen` (Manager view for orders)
- Shows a table-like list of orders with columns: `Order ID, Date, Time, Customer, Contact, Type, Items, Total, Status, Actions`.
- Filters:
  - Date Range: `TODAY`, `THIS_WEEK`, `THIS_MONTH` — computes start/end accordingly.
  - Status: filter by `OrderStatus` (PENDING, PREPARING, OUT_FOR_DELIVERY, COMPLETED, CANCELLED).
- Actions vary by status: Accept/Cancel (PENDING), Send Out/Complete/Cancel (PREPARING), Complete/Cancel (OUT_FOR_DELIVERY), no actions for final states.
- Actions update `Order` status in-memory then call `OrderRepo.update(order)` to persist change.

6) `AnalyticsScreen` (Manager)
- Uses `AnalyticsManager` to compute KPIs and chart data for selected `DateRange`.
- Visualizations: small, custom-painted bar charts for revenue by day and orders by hour, KPI cards for revenue, orders, avg order value, top item, peak hour, and tables for top selling items and order status breakdown.

---

**Analytics & Reporting Logic (AnalyticsManager)**

Key methods and logic:

- `findOrdersInRange(DateRange range, boolean withLastPeriod)`
  - Computes `startDate` and `endDate` for `TODAY`, `THIS_WEEK`, `THIS_MONTH`.
  - If `withLastPeriod` is true, computes previous period start/end (day/week/month offset) and returns both lists.

- `getTotalRevenue(DateRange range)` — sums `order.getTotalPrice()` for current range.
- `getTotalOrders(DateRange range)` — count of orders in range.
- `getAverageOrderValue(DateRange range)` — revenue / total orders (safe-guard zero).
- `getRevenueChangePercent(DateRange range)` — computes percent change vs last period (0 if lastPeriod revenue is zero).
- `getOrdersCountChange(DateRange range)` — difference in order counts between current and last period.
- `getAvgOrderChangePercent(DateRange range)` — percent change in average order value vs last period.

- `getPeakHourLabel(DateRange range)`
  - Builds hourly counts via `getOrdersByHour` and finds hour with max orders.
  - Formats the hour range into 12-hour AM/PM format (e.g., "12 PM – 1 PM").

- `getRevenueByDay(DateRange range)` and `getRevenueByDayLabels` — produce arrays for visualizing revenue across days depending on range.

- `getTopSellingItems(DateRange range, int limit)`
  - Algorithm: collects all menu items, builds an array of unit counts aligned to menu item indices by scanning orders in range and counting quantities per menuItemID; then selects top `limit` indices by repeated max extraction.
  - Returns a 2D string array with columns `[name, category, units, revenueStr]`.

- `getOrderCountByStatus(DateRange range, String[] statuses)` — counts orders by `OrderStatus` for a breakdown.

Notes:
- The top-selling algorithm uses an index-based approach (array of counts) relying on `MenuRepository.findAll()` ordering; it is O(N*M) where N = number of ordered items and M = menu size, which is acceptable for small datasets but could be optimized with maps.

---

**Utilities & Security**

- `HashUtility.hashWithSHA256(String)` — returns SHA-256 hex string of input text. Used by `User.setPassword` and `User.verifyPassword`.
- Passwords are stored hashed in `users.csv`. The app does not use salts or PBKDF2 — consider adding salt and a stronger password hashing mechanism in production.

---

**Enums & Constants**

- `Category`: APPETIZER, MAIN_COURSE, DIPS, BEVERAGE
- `OrderStatus`: PENDING, PREPARING, OUT_FOR_DELIVERY, COMPLETED, CANCELLED
- `OrderType`: TAKEAWAY, DELIVERY
- `PaymentMethod`: CASH, CARD, QR_PAYMENT, JAZZCASH
- `Role`: CASHIER, MANAGER
- `DateRange`: TODAY, THIS_WEEK, THIS_MONTH

These are heavily used across UI / repository / analytics logic.

---

**Data files**

- `data/menu_items.csv` — CSV for menu items (header: itemName,menuItemID,price,description,category,imageUrl,preparationTime,servingSize)
- `data/orders.csv` — CSV for orders (header: orderID, Date, userName, orderType, customerName, customerContact, deliveryAddress, orderItems, totalPrice, orderStatus)
- `data/users.csv` — CSV for users (header: name,cnic,dob,phone,address,role,username,password)

Important: order deserialization depends on menu items being present (OrderRepository resolves menuItemIDs against `MenuRepository`).

---

**Running the app**

From the workspace root, compile and run the Java application in a standard Java toolchain. The project uses plain Java (no external build manifest is included in the repository snapshot).

Example (if using `javac`/`java`):

```bash
javac -d out -sourcepath src $(find src -name "*.java")
java -cp out app.Main
```

Or use the provided VS Code task `java (buildArtifact): CafeSync` to build a jar and run.

Files under `data/` must be writable by the running process.

---

**Design decisions & assumptions**

- Storage: CSV chosen for simplicity. Assumes small dataset, single-process access. No concurrency control.
- IDs: menu/order IDs use timestamp prefixes. Collisions are unlikely but possible under very fast concurrent creation.
- CSV parsing uses `split(",")` and does not escape commas in text fields. The UI and input validation aim to avoid commas in fields.
- Password security: SHA-256 hashing without salt; acceptable for demo but not production.
- UI: Java Swing; custom components and painting used for charts and badges.
- Validation: model classes perform defensive validation and throw `IllegalArgumentException` when invalid input supplied.

---

**Potential improvements**

- Replace CSV with a lightweight embedded DB (SQLite or H2) to handle escaping, queries, and concurrency.
- Use salted PBKDF2 / bcrypt / Argon2 for password storage.
- Improve CSV parsing to handle quoted fields or use a CSV library (OpenCSV).
- Use Map-based indexing for analytics top-sellers to improve performance (HashMap<menuId, units>).
- Add unit tests for parsing, serialization, and analytics computations.
- Add input sanitization for fields that may include commas or special characters.
- Consider separating UI logic from persistence via service layer and interfaces for easier testing.

---

If you'd like, I can:
- generate a class-level sequence diagram or mermaid flow for the main user flows;
- convert CSV repositories to use a proper CSV library or SQLite;
- add unit tests for `AnalyticsManager` and repositories.

Report generated by GitHub Copilot.
