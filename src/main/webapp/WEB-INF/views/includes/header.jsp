<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Bank of Trust</a>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <span class="navbar-text me-3">
                        Welcome, <strong id="header-username"></strong>!
                    </span>
                </li>
                <li class="nav-item">
                    <button id="logout-button" class="btn btn-light">Logout</button>
                </li>
            </ul>
        </div>
    </div>
</nav>

<script>
    // This script should be on every protected page.
    document.addEventListener('DOMContentLoaded', function() {
        const token = localStorage.getItem('jwtToken');
        const username = localStorage.getItem('username');

        // Back Button Security: If no token exists, force logout.
        if (!token) {
            window.location.replace('/auth/login?reason=session_expired');
            return;
        }

        // Populate header
        if (username) {
            document.getElementById('header-username').textContent = username;
        }

        // Logout functionality
        const logoutButton = document.getElementById('logout-button');
        if (logoutButton) {
            logoutButton.addEventListener('click', function() {
                localStorage.clear();
                window.location.replace('/auth/login?reason=logout_success');
            });
        }
    });
</script>