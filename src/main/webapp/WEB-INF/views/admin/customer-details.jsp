<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Customer Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<jsp:include page="../includes/header.jsp" />
<div class="container mt-4">
    <h3>Accounts for Customer: <span id="username" class="text-primary"></span></h3>
    <a href="/admin/dashboard">&laquo; Back to Admin Dashboard</a>
    <hr>
    <div id="accounts-list" class="row"></div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const token = localStorage.getItem('jwtToken');
        const urlParams = new URLSearchParams(window.location.search);
        const userId = urlParams.get('userId');
        const username = urlParams.get('username');

        document.getElementById('username').textContent = username;

        fetch('/api/admin/customers/' + userId + '/accounts', {
            headers: { 'Authorization': 'Bearer ' + token }
        })
        .then(res => res.json())
        .then(accounts => {
            const accountsListDiv = document.getElementById('accounts-list');
            accountsListDiv.innerHTML = '';
            if (accounts.length === 0) {
                accountsListDiv.innerHTML = '<p>This customer has no accounts.</p>';
            }
            accounts.forEach(account => {
                // THE FIX: Replaced template literal with standard string concatenation
                accountsListDiv.innerHTML +=
                    '<div class="col-md-6 mb-3">' +
                    '    <div class="card">' +
                    '        <div class="card-header d-flex justify-content-between">' +
                    '            <h5 class="card-title mb-0">' + account.accountType + '</h5>' +
                    '            <span class="badge bg-info text-dark">' + account.status + '</span>' +
                    '        </div>' +
                    '        <div class="card-body">' +
                    '            <p><strong>Number:</strong> ' + account.accountNumber + '</p>' +
                    '            <p><strong>Balance:</strong> ₹' + account.balance.toFixed(2) + '</p>' +
                    '            <a href="/admin/history?accountNumber=' + account.accountNumber + '" class="btn btn-sm btn-secondary">View Transactions</a>' +
                    '        </div>' +
                    '    </div>' +
                    '</div>';
            });
        });
    });
</script>
</body>
</html>