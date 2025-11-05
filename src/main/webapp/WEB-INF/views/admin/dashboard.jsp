<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<jsp:include page="../includes/header.jsp" />
<div class="container mt-4">
	<div class="d-flex justify-content-between align-items-center mb-4">
	        <h3 class="mb-0">Admin Dashboard</h3>
	        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#adminRegisterModal">
	            Register New Admin
	        </button>
	    </div>
	<div class="row" id="stats-cards"></div>

    <div class="mt-4">
        <h4>Pending Account Requests</h4>
        <table class="table table-striped table-hover">
            <thead><tr><th>Customer Name</th><th>Account Type</th><th>Deposit</th><th>Applied On</th><th>Actions</th></tr></thead>
            <tbody id="pending-accounts-table"></tbody>
        </table>
        <div id="pending-pagination" class="d-flex justify-content-end"></div>
    </div>
    
    <div class="mt-5">
        <h4>Customer Management</h4>
        <div class="mb-3"><input type="text" id="customer-search" class="form-control" placeholder="Search by username..."></div>
        <table class="table table-hover">
            <thead><tr><th>ID</th><th>Full Name</th><th>Username</th><th>Email</th><th>Actions</th></tr></thead>
            <tbody id="customers-table"></tbody>
        </table>
        <div id="customer-pagination" class="d-flex justify-content-end"></div>
    </div>
</div>

<!-- Admin Registration Modal -->
<div class="modal fade" id="adminRegisterModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Register New Admin User</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div id="admin-reg-message" class="alert" style="display: none;"></div>
                <form id="adminRegisterForm">
                    <div class="mb-3"><label for="admin-username" class="form-label">Username</label><input type="text" class="form-control" id="admin-username" required></div>
                    <div class="mb-3"><label for="admin-fullName" class="form-label">Full Name</label><input type="text" class="form-control" id="admin-fullName" required></div>
                    <div class="mb-3"><label for="admin-email" class="form-label">Email</label><input type="email" class="form-control" id="admin-email" required></div>
                    <div class="mb-3"><label for="admin-password" class="form-label">Password</label><input type="password" class="form-control" id="admin-password" required></div>
                    <button type="submit" class="btn btn-primary w-100">Register Admin</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // These functions can be defined globally
    function processAccount(accountId, action) {
        const token = localStorage.getItem('jwtToken');
        let url = '/api/admin/accounts/' + action + '/' + accountId;
        let body = {};
        if (action === 'reject') {
            const reason = prompt("Please enter the reason for rejection:");
            if (!reason) return;
            body = { remarks: reason };
        }
        fetch(url, {
            method: 'PUT',
            headers: { 'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json' },
            body: action === 'reject' ? JSON.stringify(body) : null
        }).then(response => {
            if (!response.ok) throw new Error('Failed to ' + action + ' account.');
            fetchPendingAccounts(0);
            fetchStats(); 
        }).catch(error => alert(error.message));
    }

    function fetchStats() {
        const token = localStorage.getItem('jwtToken');
        fetch('/api/admin/dashboard/stats', { headers: { 'Authorization': 'Bearer ' + token }})
        .then(res => res.json())
        .then(stats => {
            document.getElementById('stats-cards').innerHTML =
                '<div class="col-md-3"><div class="card text-white bg-primary p-3"><h5>Total Accounts</h5><h2>' + stats.totalAccounts + '</h2></div></div>' +
                '<div class="col-md-3"><div class="card text-white bg-warning p-3"><h5>Pending</h5><h2>' + stats.pendingAccounts + '</h2></div></div>' +
                '<div class="col-md-3"><div class="card text-white bg-success p-3"><h5>Approved</h5><h2>' + stats.approvedAccounts + '</h2></div></div>' +
                '<div class="col-md-3"><div class="card text-white bg-danger p-3"><h5>Rejected</h5><h2>' + stats.rejectedAccounts + '</h2></div></div>';
        });
    }

    function fetchPendingAccounts(page) {
        const token = localStorage.getItem('jwtToken');
        const tableBody = document.getElementById('pending-accounts-table');
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center">Loading...</td></tr>';
        
        // CORRECTED URL
        const url = '/api/admin/accounts/pending?page=' + page + '&size=10';
        fetch(url, { headers: { 'Authorization': 'Bearer ' + token }})
        .then(res => res.json()).then(pageData => {
            tableBody.innerHTML = '';
            if (pageData && pageData.content && pageData.content.length > 0) {
                pageData.content.forEach(acc => {
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
            } else { tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No pending requests.</td></tr>'; }
            renderPagination('pending-pagination', pageData, fetchPendingAccounts, '');
        }).catch(err => {
            tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Failed to load data.</td></tr>';
        });
    }

    function fetchCustomers(page, query) {
        const token = localStorage.getItem('jwtToken');
        const customersTable = document.getElementById('customers-table');
        customersTable.innerHTML = '<tr><td colspan="5" class="text-center">Loading...</td></tr>';

        // CORRECTED URL
        const url = '/api/admin/customers?page=' + page + '&size=10&query=' + query;
        fetch(url, { headers: { 'Authorization': 'Bearer ' + token }})
        .then(res => res.json()).then(pageData => {
            customersTable.innerHTML = '';
            if (pageData && pageData.content && pageData.content.length > 0) {
                pageData.content.forEach(user => {
                    customersTable.innerHTML +=
                        '<tr>' +
                        '    <td>' + user.id + '</td>' +
                        '    <td>' + user.fullName + '</td>' +
                        '    <td>' + user.username + '</td>' +
                        '    <td>' + user.email + '</td>' +
                        '    <td><a href="/admin/customer-details?userId=' + user.id + '&username=' + user.username + '" class="btn btn-sm btn-primary">View Accounts</a></td>' +
                        '</tr>';
                });
            } else { customersTable.innerHTML = '<tr><td colspan="5" class="text-center">No customers found.</td></tr>'; }
            renderPagination('customer-pagination', pageData, fetchCustomers, query);
        }).catch(err => {
            customersTable.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Failed to load data.</td></tr>';
        });
    }

    function renderPagination(containerId, pageData, fetchFunction, query) {
        const container = document.getElementById(containerId);
        container.innerHTML = '';
        if (!pageData || pageData.totalPages <= 1) return;

        const isFirst = pageData.first;
        const isLast = pageData.last;
        const currentPage = pageData.number;

        const prevButton = document.createElement('button');
        prevButton.className = 'btn btn-outline-secondary me-2';
        prevButton.innerText = 'Previous';
        if (isFirst) prevButton.disabled = true;
        prevButton.onclick = () => fetchFunction(currentPage - 1, query);

        const nextButton = document.createElement('button');
        nextButton.className = 'btn btn-outline-secondary ms-2';
        nextButton.innerText = 'Next';
        if (isLast) nextButton.disabled = true;
        nextButton.onclick = () => fetchFunction(currentPage + 1, query);

        const pageInfo = document.createElement('span');
        pageInfo.className = 'align-self-center';
        // CORRECTED TEXT
        pageInfo.innerText = 'Page ' + (currentPage + 1) + ' of ' + pageData.totalPages;

        container.appendChild(prevButton);
        container.appendChild(pageInfo);
        container.appendChild(nextButton);
    }

    document.addEventListener('DOMContentLoaded', function() {
        const token = localStorage.getItem('jwtToken');
        if (!token || localStorage.getItem('role') !== 'ADMIN') { window.location.replace('/auth/login'); return; }
        
        const customerSearchInput = document.getElementById('customer-search');
        
        fetchStats();
        fetchPendingAccounts(0);
        fetchCustomers(0, '');

        let timeout = null;
        customerSearchInput.addEventListener('input', (event) => {
            clearTimeout(timeout);
            const queryValue = event.target.value;
            timeout = setTimeout(() => {
                fetchCustomers(0, queryValue);
            }, 300);
        });
    });
	
	
	document.getElementById('adminRegisterForm').addEventListener('submit', function(e) {
	    e.preventDefault();
	    const messageDiv = document.getElementById('admin-reg-message');
	    const token = localStorage.getItem('jwtToken');

	    const adminData = {
	        username: document.getElementById('admin-username').value,
	        fullName: document.getElementById('admin-fullName').value,
	        email: document.getElementById('admin-email').value,
	        password: document.getElementById('admin-password').value
	    };

	    fetch('/api/admin/create-admin', {
	        method: 'POST',
	        headers: { 'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json' },
	        body: JSON.stringify(adminData)
	    })
	    .then(async response => {
	        const text = await response.text();
	        if (!response.ok) throw new Error(text);
	        return text;
	    })
	    .then(data => {
	        messageDiv.className = 'alert alert-success';
	        messageDiv.textContent = data;
	        messageDiv.style.display = 'block';
	        document.getElementById('adminRegisterForm').reset();
	    })
	    .catch(error => {
	        messageDiv.className = 'alert alert-danger';
	        messageDiv.textContent = 'Error: ' + error.message;
	        messageDiv.style.display = 'block';
	    });
	});
</script>
</body>
</html>