```markdown
# ✈️ Airline Reservation System - REST API Documentation

This documentation outlines all available REST API endpoints for managing reservations, tickets, and payments in the Airline Reservation System.

---

## 📌 Reservation Endpoints

### 1. Get All Reservations
- **URL:** `/api/reservations`
- **Method:** `GET`
- **Description:** Retrieves a list of all reservations.
- **Response:** Array of Reservation objects

<details>
<summary>Sample Response</summary>

```json
[
  {
    "id": "67fe7179d5c9775fb64b64cd",
    "firstName": "John",
    "lastName": "Doe",
    "numPassengers": 2,
    "classType": "Business",
    "phoneNumber": "1234567890",
    "time": "10:30:00",
    "date": "2025-05-15",
    "ticketNumber": "TKT123456",
    "details": "Window seat preferred"
  },
  {
    "id": "67fe7179d5c9775fb64b64cf",
    "firstName": "Jane",
    "lastName": "Smith",
    "numPassengers": 1,
    "classType": "Economy",
    "phoneNumber": "9876543210",
    "time": "14:45:00",
    "date": "2025-05-20",
    "ticketNumber": null,
    "details": "No preferences"
  }
]
```
</details>

---

### 2. Get Reservation by ID
- **URL:** `/api/reservations/{id}`
- **Method:** `GET`
- **Description:** Retrieves a reservation by its ID.

<details>
<summary>Sample Response</summary>

```json
{
  "id": "67fe7179d5c9775fb64b64cd",
  "firstName": "John",
  "lastName": "Doe",
  "numPassengers": 2,
  "classType": "Business",
  "phoneNumber": "1234567890",
  "time": "10:30:00",
  "date": "2025-05-15",
  "ticketNumber": "TKT123456",
  "details": "Window seat preferred"
}
```
</details>

---

### 3. Create New Reservation
- **URL:** `/api/reservations`
- **Method:** `POST`
- **Description:** Creates a new reservation.
- **Request Body:** Reservation object (without `id`)

<details>
<summary>Sample Request & Response</summary>

**Request:**
```json
{
  "firstName": "Michael",
  "lastName": "Johnson",
  "numPassengers": 3,
  "classType": "First",
  "phoneNumber": "5551234567",
  "time": "08:15:00",
  "date": "2025-06-10",
  "details": "Aisle seat preferred"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation created successfully",
  "reservation": {
    "id": "67fe7179d5c9775fb64b64cg",
    "firstName": "Michael",
    "lastName": "Johnson",
    "numPassengers": 3,
    "classType": "First",
    "phoneNumber": "5551234567",
    "time": "08:15:00",
    "date": "2025-06-10",
    "ticketNumber": null,
    "details": "Aisle seat preferred"
  }
}
```
</details>

---

### 4. Update Existing Reservation
- **URL:** `/api/reservations/{id}`
- **Method:** `PUT`
- **Description:** Updates an existing reservation by ID.

<details>
<summary>Sample Request & Response</summary>

**Request:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "numPassengers": 3,
  "classType": "First",
  "phoneNumber": "1234567890",
  "time": "11:30:00",
  "date": "2025-05-16",
  "details": "Window seat and special meal"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation updated successfully",
  "reservation": {
    "id": "67fe7179d5c9775fb64b64cd",
    "firstName": "John",
    "lastName": "Doe",
    "numPassengers": 3,
    "classType": "First",
    "phoneNumber": "1234567890",
    "time": "11:30:00",
    "date": "2025-05-16",
    "ticketNumber": "TKT123456",
    "details": "Window seat and special meal"
  }
}
```
</details>

---

### 5. Delete Reservation
- **URL:** `/api/reservations/{id}`
- **Method:** `DELETE`
- **Description:** Deletes a reservation by ID.

```json
{
  "success": true,
  "message": "Reservation deleted successfully"
}
```

---

## 🎟️ Ticket Endpoints

### 1. Get All Tickets
- **URL:** `/api/tickets`
- **Method:** `GET`
- **Description:** Retrieves all airline tickets.

### 2. Get Ticket by ID
- **URL:** `/api/tickets/{id}`
- **Method:** `GET`
- **Description:** Retrieves a ticket and its associated reservation by ID.

### 3. Get Ticket Summary
- **URL:** `/api/tickets/{id}/summary`
- **Method:** `GET`
- **Description:** Returns ticket, reservation, payment, and summary information for a given ticket ID.

---

## 💳 Payment Endpoints

### 1. Get All Payments
- **URL:** `/api/payments`
- **Method:** `GET`
- **Description:** Returns a list of payments (using DTOs to exclude sensitive data).

### 2. Get Payment by ID
- **URL:** `/api/payments/{id}`
- **Method:** `GET`
- **Description:** Retrieves a payment by its ID (DTO).

### 3. Get Payment for a Reservation
- **URL:** `/api/payments/reservation/{reservationId}`
- **Method:** `GET`
- **Description:** Returns payment info calculated for the specified reservation.

### 4. Process a Payment
- **URL:** `/api/payments/process`
- **Method:** `POST`
- **Description:** Processes a payment, creates a ticket, and returns full info.

<details>
<summary>Sample Request & Response</summary>

**Request:**
```json
{
  "reservationId": "67ff094f9410385741165817",
  "cardHolderName": "John Doe",
  "cardNumber": "1234567890123456",
  "expiryDate": "12/12",
  "cvv": "123",
  "date": "2025-04-15",
  "amount": 1200.0
}
```

**Response:**
```json
{
  "success": true,
  "ticket": {
    "number": "TKT123456",
    "price": 1200.0,
    "details": "Flight details for John Doe",
    "reservationId": "67fe7179d5c9775fb64b64cd",
    "paymentId": "67fe7179d5c9775fb64b65cd"
  },
  "payment": {
    "id": "67fe7179d5c9775fb64b65cd",
    "amount": 1200.0,
    "date": "2025-04-15",
    "reservationId": "67fe7179d5c9775fb64b64cd"
  },
  "message": "Payment processed successfully"
}
```
</details>
