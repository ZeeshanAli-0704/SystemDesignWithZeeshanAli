#
## 🔵 1. **Movie**

Represents information about a movie.

| Field            | Type         | Description                      |
| ---------------- | ------------ | -------------------------------- |
| **MovieID (PK)** | int          | Unique identifier for each movie |
| Title            | varchar(256) | Title of the movie               |
| Description      | varchar(512) | Summary of the movie             |
| Duration         | datetime     | Length of the movie              |
| Language         | varchar(16)  | Language of the movie            |
| ReleaseDate      | datetime     | Release date                     |
| Country          | varchar(64)  | Country of origin                |
| Genre            | varchar(20)  | Genre (e.g., action, drama)      |

🔁 **One Movie can have multiple Shows**.

---

### 🔵 2. **Show**

Represents a particular showtime of a movie in a specific hall.

| Field                 | Type     | Description                    |
| --------------------- | -------- | ------------------------------ |
| **ShowID (PK)**       | int      | Unique identifier              |
| Date                  | datetime | Date of the show               |
| StartTime             | datetime | Start time                     |
| EndTime               | datetime | End time                       |
| **CinemaHallID (FK)** | int      | Which hall it's being shown in |
| **MovieID (FK)**      | int      | Which movie is being shown     |

🔁 **Each Show belongs to one Movie and one Hall**, but a movie can have many shows.

---

### 🔵 3. **Booking**

A customer booking one or more seats for a specific show.

| Field              | Type       | Description                     |
| ------------------ | ---------- | ------------------------------- |
| **BookingID (PK)** | int        | Unique identifier               |
| NumberOfSeats      | int        | How many seats were booked      |
| Timestamp          | datetime   | When the booking was made       |
| Status             | int (enum) | Status: booked, cancelled, etc. |
| **UserID (FK)**    | int        | Who made the booking            |
| **ShowID (FK)**    | int        | For which show                  |

🔁 **One Booking belongs to one User and one Show**.

---

### 🔵 4. **User**

Represents a person using the system to book tickets.

| Field           | Type        | Description                    |
| --------------- | ----------- | ------------------------------ |
| **UserID (PK)** | int         | Unique identifier              |
| Name            | varchar(64) | User's name                    |
| Password        | varchar(20) | Login password (likely hashed) |
| Email           | varchar(64) | Email address                  |
| Phone           | varchar(16) | Phone number                   |

🔁 **One User can make multiple Bookings**.

---

### 🔵 5. **Payment**

Represents the payment made for a booking.

| Field               | Type       | Description                        |
| ------------------- | ---------- | ---------------------------------- |
| **PaymentID (PK)**  | int        | Unique identifier                  |
| Amount              | number     | Amount paid                        |
| Timestamp           | datetime   | When the payment was made          |
| DiscountCouponID    | int        | (Optional) coupon applied          |
| RemoteTransactionID | int        | External payment gateway reference |
| PaymentMethod       | int (enum) | Enum like: Card, UPI, Wallet, etc. |
| **BookingID (FK)**  | int        | Linked booking                     |

🔁 **Each Payment is linked to one Booking**.

---

### 🔵 6. **Show\_Seat**

Represents a seat reserved for a particular show.

| Field                 | Type       | Description                      |
| --------------------- | ---------- | -------------------------------- |
| **ShowSeatID (PK)**   | int        | Unique identifier                |
| Status                | int (enum) | Enum (available, booked, etc.)   |
| Price                 | number     | Dynamic price per seat           |
| **CinemaSeatID (FK)** | int        | Link to base seat                |
| **ShowID (FK)**       | int        | For which show                   |
| **BookingID (FK)**    | int        | Which booking owns it (nullable) |

🔁 **A Show\_Seat is derived from a Cinema\_Seat and linked to a Booking and Show**.

---

### 🔵 7. **Cinema\_Seat**

Represents a physical seat in a hall.

| Field                 | Type       | Description                   |
| --------------------- | ---------- | ----------------------------- |
| **CinemaSeatID (PK)** | int        | Unique ID for the seat        |
| SeatNumber            | int        | Seat number                   |
| Type                  | int (enum) | Enum: regular, recliner, etc. |
| **CinemaHallID (FK)** | int        | In which hall                 |

🔁 **Each seat belongs to a Cinema Hall**.

---

### 🔵 8. **Cinema\_Hall**

Represents an individual hall inside a cinema.

| Field                 | Type        | Description                |
| --------------------- | ----------- | -------------------------- |
| **CinemaHallID (PK)** | int         | Unique ID                  |
| Name                  | varchar(64) | Hall name                  |
| TotalSeats            | int         | Number of seats            |
| **CinemaID (FK)**     | int         | Which cinema it belongs to |

🔁 **One Cinema can have many Halls**.

---

### 🔵 9. **Cinema**

Represents a cinema (like PVR, INOX).

| Field             | Type        | Description            |
| ----------------- | ----------- | ---------------------- |
| **CinemaID (PK)** | int         | Unique ID              |
| Name              | varchar(64) | Cinema name            |
| TotalCinemaHalls  | int         | Number of halls inside |
| **CityID (FK)**   | int         | Location city          |

🔁 **A city can have multiple Cinemas**.

---

### 🔵 10. **City**

Location metadata for the cinema.

| Field           | Type        | Description    |
| --------------- | ----------- | -------------- |
| **CityID (PK)** | int         | Unique city ID |
| Name            | varchar(64) | City name      |
| State           | varchar(64) | State          |
| ZipCode         | varchar(16) | Postal code    |

---

### 🔁 Summary of Relationships:

* **Movie ↔ Show**: One-to-many
* **Show ↔ Booking**: One-to-many
* **User ↔ Booking**: One-to-many
* **Booking ↔ Payment**: One-to-one
* **Show ↔ Show\_Seat**: One-to-many
* **Show\_Seat ↔ Booking**: Many-to-one (nullable)
* **Cinema ↔ Cinema\_Hall**: One-to-many
* **Cinema\_Hall ↔ Cinema\_Seat**: One-to-many
* **City ↔ Cinema**: One-to-many
* **Cinema\_Hall ↔ Show**: One-to-many

---

### ✅ Use Case Example (Real Flow):

1. A user logs in and selects a **city**.
2. System shows **cinemas** in that city.
3. User selects a **cinema**, chooses a **movie** and a **showtime**.
4. Based on selected **show**, the system displays **available seats** from `Show_Seat`.
5. User selects seats → makes a **booking** → payment is made and logged.
6. `Show_Seat.status` is updated to **booked** and `BookingID` is attached.

---




