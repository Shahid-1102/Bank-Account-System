<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="includes/public_header.jsp" />

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h3 class="card-title text-center mb-4">Create Your Customer Account</h3>
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
                            <div class="form-text">Must be 8+ characters, with a number, letter, and special character.</div>
                        </div>
                        
                        <%-- ROLE DROPDOWN IS NOW REMOVED --%>

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
        event.preventDefault();
        const successMessageDiv = document.getElementById('successMessage');
        const errorMessageDiv = document.getElementById('errorMessage');
        successMessageDiv.style.display = 'none';
        errorMessageDiv.style.display = 'none';

        const registerData = {
            username: document.getElementById('username').value,
            fullName: document.getElementById('fullName').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        };

        fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(registerData),
        })
        .then(async response => {
            const responseBody = await response.text();
            if (!response.ok) { throw new Error(responseBody || 'Registration failed'); }
            return responseBody;
        })
        .then(data => {
            successMessageDiv.textContent = data + " Redirecting to login...";
            successMessageDiv.style.display = 'block';
            document.getElementById('registerForm').reset();
            setTimeout(() => { window.location.href = '/auth/login'; }, 3000);
        })
        .catch(error => {
            errorMessageDiv.textContent = error.message;
            errorMessageDiv.style.display = 'block';
        });
    });
</script>

</body>
</html>