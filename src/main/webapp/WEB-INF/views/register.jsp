<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h3>Register New User</h3>
                </div>
                <div class="card-body">
                    <div id="successMessage" class="alert alert-success" style="display: none;"></div>
                    <div id="errorMessage" class="alert alert-danger" style="display: none;"></div>

                    <form id="registerForm">
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" class="form-control" id="username" required>
                        </div>
                        <div class="mb-3">
                            <label for="fullName" class="form-label">Full Name</label>
                            <input type="text" class="form-control" id="fullName" required>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" required>
                            <div class="form-text">Must be at least 8 characters, with a number, letter, and special character.</div>
                        </div>
                        <div class="mb-3">
                            <label for="role" class="form-label">Role</label>
                            <select class="form-select" id="role">
                                <option value="CUSTOMER" selected>Customer</option>
                                <option value="ADMIN">Admin</option>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Register</button>
                    </form>
                    <div class="mt-3 text-center">
                        <p>Already have an account? <a href="/auth/login">Login here</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.getElementById('registerForm').addEventListener('submit', function(event) {
        // 1. Prevent the browser's default form submission behavior
        event.preventDefault();

        // 2. Get references to the message divs
        const successMessageDiv = document.getElementById('successMessage');
        const errorMessageDiv = document.getElementById('errorMessage');
        
        // Hide previous messages
        successMessageDiv.style.display = 'none';
        errorMessageDiv.style.display = 'none';

        // 3. Read the values from the form inputs
        const registerData = {
            username: document.getElementById('username').value,
            fullName: document.getElementById('fullName').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            role: document.getElementById('role').value
        };

        // 4. Use the Fetch API to send the data to our backend REST endpoint
        fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(registerData),
        })
        .then(async response => {
            const responseBody = await response.text(); // Read body as text first
            if (!response.ok) {
                // If the server responded with an error, use the body as the error message
                throw new Error(responseBody || 'Registration failed with status: ' + response.status);
            }
            return responseBody;
        })
        .then(data => {
            // 5. Handle a successful response
            console.log('Success:', data);
            successMessageDiv.textContent = data + " You will be redirected to the login page shortly.";
            successMessageDiv.style.display = 'block';
            document.getElementById('registerForm').reset(); // Clear the form

            // Redirect to login page after a short delay
            setTimeout(() => {
                window.location.href = '/auth/login';
            }, 3000); // 3-second delay
        })
        .catch(error => {
            // 6. Handle any errors (network error or error from the server)
            console.error('Error:', error);
            errorMessageDiv.textContent = error.message;
            errorMessageDiv.style.display = 'block';
        });
    });
</script>

</body>
</html>