<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Customer Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center">
        <h2>Welcome, <span id="username"></span>!</h2>
        <a href="/customer/create-account" class="btn btn-primary">Create New Account</a>
    </div>
    <hr>
    <h4>Your Accounts</h4>
    <div id="accounts-list" class="row">
        <!-- Account cards will be dynamically inserted here -->
    </div>
</div>

<script>
    // Immediately check for token and fetch data on page load
    document.addEventListener('DOMContentLoaded', function() {
        const token = localStorage.getItem('jwtToken');
        const username = localStorage.getItem('username');

        if (!token) {
            // If no token, redirect to login
            window.location.href = '/auth/login';
            return;
        }

        document.getElementById('username').textContent = username;

        // Fetch user's accounts from the backend
        fetch('/api/accounts/my-accounts', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch accounts.');
            }
            return response.json();
        })
        .then(accounts => {
            const accountsListDiv = document.getElementById('accounts-list');
            if (accounts.length === 0) {
                accountsListDiv.innerHTML = '<p>You have no accounts yet. Click the button above to create one!</p>';
                return;
            }
            
            // Clear any existing content
            accountsListDiv.innerHTML = '';

            accounts.forEach(account => {
                // --- THIS IS THE CORRECTED PART ---
                // Using traditional string concatenation with '+' to avoid JSP EL conflict.
                var card = '<div class="col-md-6 mb-3">' +
                           '    <div class="card">' +
                           '        <div class="card-header d-flex justify-content-between">' +
                           '            <h5 class="card-title mb-0">' + account.accountType + ' Account</h5>' +
                           '            <span class="badge bg-info">' + account.status + '</span>' +
                           '        </div>' +
                           '        <div class="card-body">' +
                           '            <p class="card-text"><strong>Account Number:</strong> ' + account.accountNumber + '</p>' +
                           '            <p class="card-text"><strong>Balance:</strong> ₹' + account.balance.toFixed(2) + '</p>';
                
                // Conditionally add the button
                if (account.status === 'APPROVED') {
                    card += '<a href="/customer/history?accountNumber=' + account.accountNumber + '" class="btn btn-sm btn-outline-primary">View History</a>';
                }

                card += '        </div>' +
                        '    </div>' +
                        '</div>';
                
                accountsListDiv.innerHTML += card;
            });
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('accounts-list').innerHTML = '<p class="text-danger">Could not load account information.</p>';
        });
    });
</script>
</body>
</html>