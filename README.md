# LibraryManagementSystem

### 📚📖✨

## Overview

**LibraryManagementSystem** is an academic project created as part of the coursework for the subject **Network and Distributed Programming** at the Faculty of Electrical Engineering, University of Banja Luka (Elektrotehnički fakultet). This project was developed individually in May 2024. The system simulates the operations of an online library, enabling efficient management of books, members, reservations, and supplier communications. 📖📚✨

## Features

### Library Application 📘📗📙

- **Member Management:**

  - View all members.

  - Add, edit, delete, approve, or block membership accounts.

- **Book Management:**

  - Perform CRUD (Create, Read, Update, Delete) operations on books stored in a Redis database.

- **Reservation Management:**

  - View and approve/decline book reservations made by members.

- **Supplier Communication:**

  - Order new books from suppliers using classic socket communication. 📚🖥️✨

### Member Application 📖🖊️✨

- **Authentication:**

  - User registration with personal details (name, address, email, username, password).

  - Account activation requires librarian approval.

  - Login after account activation.

- **Book Operations:**

  - View and search the catalog of books.

  - Download selected books as a ZIP file via email.

  - View book details, including the title, cover image, and first 100 lines of text.

- **Communication:**

  - Interact with other members via a secure chat application using sockets. ✉️📚✨

### Supplier Application 📦📑📕

- **Book Offering:**

  - Upload book details (title, author, publication date, language, cover image, and text file).

  - Books are retrieved from Project Gutenberg using links stored in a `linkovi.txt` file.

- **Order Processing:**

  - Receive book orders from the library.

  - Approve or reject orders.

  - Generate invoices and send them to the accounting service using RMI. 💼📘✨

## Technologies Used 🚀📱💻

- **Frontend:** JavaFX for GUI applications.

- **Backend:** RESTful API for library and member functionalities.

- **Database:** Redis for storing book information.

- **Communication Protocols:**

  - REST API for member registration and book-related operations.

  - Multicast for member suggestions and announcements.

  - Sockets for supplier communication.

  - Secure sockets for communication between members.

  - RMI for communication between the supplier and accounting service.

- **Data Storage:** XML for member accounts, serialized files for invoices.

- **Utilities:** Logger for exception handling, properties files for configuration. 🛠️📊✨

## Academic Context 🏫📘📜

This project is an individual assignment developed to demonstrate practical skills in networked and distributed systems programming. It integrates concepts such as RESTful APIs, socket communication, RMI, and GUI development to create a functional and interactive simulation of an online library system. 🎓📚✨

---

**Faculty of Electrical Engineering**  📖📘✨

University of Banja Luka  📚🎓✨

Subject: Network and Distributed Programming  ✨📜📚

May 2024

