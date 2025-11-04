<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card">
                <div class="card-header">
                    <h3>Bank Account System - Login</h3>
                </div>
                <div class="card-body">
                    <div id="errorMessage" class="alert alert-danger" style="display: none;"></div>
                    <form id="loginForm">
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" class="form-control" id="username" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Login</button>
                    </form>
                    <div class="mt-3 text-center">
                        <p>Don't have an account? <a href="/auth/register">Register here</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.getElementById('loginForm').addEventListener('submit', function (event) {
        // Prevent the default form submission which reloads the page
        event.preventDefault();

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const errorMessageDiv = document.getElementById('errorMessage');

        // Hide previous error messages
        errorMessageDiv.style.display = 'none';

        // Create the request body as a JavaScript object
        const loginData = {
            username: username,
            password: password
        };

        // Use the Fetch API to send a POST request to our backend
        fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        })
        .then(response => {
            if (!response.ok) {
                // If response is not 2xx, throw an error to be caught below
                throw new Error('Invalid username or password.');
            }
            return response.json(); // Parse the JSON from the response
        })
        .then(data => {
            // On successful login, data will contain the token
            console.log('Login successful:', data);

            // Store the token in localStorage to use for subsequent API calls
            localStorage.setItem('jwtToken', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('role', data.role);
            
            // Redirect to a dashboard page (we will create this later)
            // Based on role, you can redirect to different pages
            if (data.role === 'ADMIN') {
                 window.location.href = '/admin/dashboard'; // To be created
            } else {
                 window.location.href = '/customer/dashboard'; // To be created
            }
        })
        .catch(error => {
            // Display error message to the user
            console.error('Login failed:', error);
            errorMessageDiv.textContent = error.message;
            errorMessageDiv.style.display = 'block';
        });
    });
</script>

</body>
</html>