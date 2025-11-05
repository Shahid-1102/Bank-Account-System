<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light"> <%-- Add a light background for contrast --%>

<jsp:include page="includes/public_header.jsp" />

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-sm"> <%-- Add a subtle shadow --%>
                <div class="card-body p-4">
                    <h3 class="card-title text-center mb-4">Login to Your Account</h3>
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
    // JavaScript remains the same
    document.getElementById('loginForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const errorMessageDiv = document.getElementById('errorMessage');
        errorMessageDiv.style.display = 'none';
        const loginData = { username: username, password: password };

        fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginData)
        })
        .then(response => {
            if (!response.ok) { throw new Error('Invalid username or password.'); }
            return response.json();
        })
        .then(data => {
            localStorage.setItem('jwtToken', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('role', data.role);
            
            if (data.role === 'ADMIN') {
                 window.location.href = '/admin/dashboard';
            } else {
                 window.location.href = '/customer/dashboard';
            }
        })
        .catch(error => {
            errorMessageDiv.textContent = error.message;
            errorMessageDiv.style.display = 'block';
        });
    });
</script>

</body>
</html>