<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h3>Admin Dashboard</h3>
    <hr>
    <div class="row" id="stats-cards">
        <!-- Stats will be loaded here -->
    </div>
    <div class="mt-4">
        <h4>Pending Account Requests</h4>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Customer Name</th>
                    <th>Account Type</th>
                    <th>Initial Deposit</th>
                    <th>Applied On</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="pending-accounts-table">
                <!-- Pending accounts will be loaded here -->
            </tbody>
        </table>
    </div>
</div>

<script>
    const token = localStorage.getItem('jwtToken');
    if (!token || localStorage.getItem('role') !== 'ADMIN') {
        window.location.href = '/auth/login';
    }

    // Function to handle approve/reject actions
    function processAccount(accountId, action) {
        let url = '/api/admin/accounts/' + action + '/' + accountId;
        let method = 'PUT';
        let body = {};

        if (action === 'reject') {
            const reason = prompt("Please enter the reason for rejection:");
            if (!reason) return; // User cancelled
            body = { remarks: reason };
        }

        fetch(url, {
            method: method,
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: action === 'reject' ? JSON.stringify(body) : null
        })
        .then(response => {
            if (!response.ok) throw new Error('Failed to ' + action + ' account.');
            document.getElementById('row-' + accountId).remove();
            fetchStats(); 
        })
        .catch(error => alert(error.message));
    }

    // Function to fetch stats
    function fetchStats() {
        fetch('/api/admin/dashboard/stats', { headers: { 'Authorization': 'Bearer ' + token }})
        .then(res => res.json())
        .then(stats => {
            // CORRECTED: Using string concatenation instead of template literals
            document.getElementById('stats-cards').innerHTML =
                '<div class="col-md-3"><div class="card text-white bg-primary p-3"><h5>Total Accounts</h5><h2>' + stats.totalAccounts + '</h2></div></div>' +
                '<div class="col-md-3"><div class="card text-white bg-warning p-3"><h5>Pending</h5><h2>' + stats.pendingAccounts + '</h2></div></div>' +
                '<div class="col-md-3"><div class="card text-white bg-success p-3"><h5>Approved</h5><h2>' + stats.approvedAccounts + '</h2></div></div>' +
                '<div class="col-md-3"><div class="card text-white bg-danger p-3"><h5>Rejected</h5><h2>' + stats.rejectedAccounts + '</h2></div></div>';
        });
    }

    document.addEventListener('DOMContentLoaded', function() {
        fetchStats();

        fetch('/api/admin/accounts/pending', { headers: { 'Authorization': 'Bearer ' + token }})
        .then(res => res.json())
        .then(accounts => {
            const tableBody = document.getElementById('pending-accounts-table');
            tableBody.innerHTML = '';
            if (accounts.length === 0) {
                 tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No pending requests.</td></tr>';
                 return;
            }
            accounts.forEach(acc => {
                // CORRECTED: Using string concatenation instead of template literals
                tableBody.innerHTML +=
                    '<tr id="row-' + acc.id + '">' +
                    '    <td>' + acc.userFullName + '</td>' +
                    '    <td>' + acc.accountType + '</td>' +
                    '    <td>₹' + acc.balance.toFixed(2) + '</td>' +
                    '    <td>' + new Date(acc.createdAt).toLocaleDateString() + '</td>' +
                    '    <td>' +
                    '        <button class="btn btn-sm btn-success" onclick="processAccount(' + acc.id + ', \'approve\')">Approve</button> ' +
                    '        <button class="btn btn-sm btn-danger" onclick="processAccount(' + acc.id + ', \'reject\')">Reject</button>' +
                    '    </td>' +
                    '</tr>';
            });
        });
    });
</script>
</body>
</html>