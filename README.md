# Bank Account Management System

This is a complete full-stack banking application developed as a proof-of-concept. The system allows customers to register, manage their bank accounts, and perform transactions, all governed by an admin approval workflow. The project was built using a modern technology stack featuring a Spring Boot backend and an Angular frontend.

## Live Demo (Placeholder)

[Link to your deployed application, if available. You can use services like Vercel for the frontend and Render/Heroku for the backend.]

---

## Features

### Customer Features
- **Authentication:** Secure user registration and JWT-based login.
- **Account Management:** Apply for new bank accounts (Savings, Current, Salary).
- **Dashboard:** View all accounts with their current status (Pending, Approved, Rejected) and balance.
- **Transactions:** Perform Deposit, Withdraw, and Transfer operations through interactive modals.
- **Statements:**
  - View a "Mini Statement" of the last 10 transactions.
  - Generate and download a full transaction statement as a PDF for a custom date range.
- **Profile:** View personal details.

### Admin Features
- **Secure Dashboard:** Role-protected admin panel with system statistics.
- **Approval Workflow:** View a paginated list of all pending account applications.
- **Account Actions:** Approve or Reject account applications with a mandatory reason for rejection.
- **Customer Management:**
  - View a paginated and searchable list of all registered customers.
  - View all accounts associated with a specific customer.
  - View the complete, paginated transaction history for any customer account.
- **Admin Creation:** A logged-in admin can register new admin users.

### Security Features
- **JWT-Based Authentication:** Stateless and secure API.
- **Password Encryption:** Passwords are encrypted using BCrypt.
- **Role-Based Access Control (RBAC):** Clear separation between Customer and Admin privileges using Spring Security.
- **Route Guards:** Angular route guards protect frontend pages from unauthorized access.
- **HTTP Interceptor:** Automatically attaches JWT tokens to outgoing API requests.

---

## Technical Architecture

The project follows a modern, decoupled architecture.

### Backend (Spring Boot)
The backend is built using a classic 4-layer architecture to ensure a clean separation of concerns:
1.  **Controller Layer:** Handles HTTP requests and API endpoints (`@RestController`).
2.  **Service Layer:** Contains all business logic (e.g., transaction validation, account limits) in a POJO-based model.
3.  **Repository Layer:** Manages all database operations using Spring Data JPA (`@Repository`).
4.  **Model Layer:** Defines the data structure with JPA entities (`@Entity`) and Data Transfer Objects (DTOs).

### Frontend (Angular)
The frontend is a modern Single-Page Application (SPA) built with a standalone component architecture:
- **Core:** Standalone components, services, and function-based guards/interceptors.
- **Routing:** Lazy-loaded feature modules (`Auth`, `Customer`, `Admin`) for optimal performance.
- **UI:** A combination of **Angular Material** for high-quality components and **ng-bootstrap** for modals and other utilities.

---

## Technology Stack

| Category      | Technology                                       |
|---------------|--------------------------------------------------|
| **Backend**   | Spring Boot 3.x, Spring Security, Spring Data JPA, Java 17+ |
| **Frontend**  | Angular 17+, TypeScript, SCSS                      |
| **UI Libraries**| Angular Material, ng-bootstrap, Bootstrap 5        |
| **Database**  | MySQL                                            |
| **Build Tools** | Maven (Backend), Angular CLI (Frontend)          |
| **PDF Generation** | iText, Thymeleaf                               |

---

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

- **Java JDK 17+**
- **Maven 3.2+**
- **MySQL Server**
- **Node.js 18.x or higher**
- **Angular CLI** (install globally: `npm install -g @angular/cli`)

### 1. Backend Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/bank-account-system.git
    cd bank-account-system
    ```

2.  **Create the Database:**
    - Open your MySQL client and run the following command to create the database schema:
      ```sql
      CREATE DATABASE bank_system;
      ```

3.  **Configure Application Properties:**
    - Navigate to `src/main/resources/application.properties`.
    - Update the following properties with your MySQL username and password:
      ```properties
      spring.datasource.username=your_mysql_username
      spring.datasource.password=your_mysql_password
      ```

4.  **Run the Backend:**
    - Open the project in your favorite IDE (like STS, IntelliJ, or VS Code).
    - Run the `BankAccountSystemApplication.java` file.
    - The backend will start on `http://localhost:8080`. The tables will be created automatically.

### 2. Frontend Setup

1.  **Navigate to the frontend directory:**
    ```bash
    cd frontend
    ```

2.  **Install dependencies:**
    This will download all the required packages for the Angular application.
    ```bash
    npm install
    ```

3.  **Run the Frontend Development Server:**
    ```bash
    npm start
    ```
    - The application will open on `http://localhost:4200`. The `proxy.conf.json` file is pre-configured to forward all API requests to the backend, so you won't face any CORS issues.

---

## How to Use the Application

1.  Navigate to `http://localhost:4200`.
2.  **Register a Customer:** Use the registration form to create a new customer account (e.g., username: `johndoe`).
3.  **Register an Admin:** Use the same form, but select the "ADMIN" role (or use the dedicated admin creation feature later) (e.g., username: `adminuser`).
4.  **Log in as the admin** (`adminuser`) to approve the customer's account application.
5.  **Log in as the customer** (`johndoe`) to use the application features.

Enjoy your modern banking application!