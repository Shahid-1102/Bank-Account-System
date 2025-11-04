<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Customer Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .card-body .btn { margin-top: 5px; } /* For better button spacing */
    </style>
</head>
<body>
<jsp:include page="../includes/header.jsp" />

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3>Your Accounts</h3>
        <a href="/customer/create-account" class="btn btn-primary">Create New Account</a>
    </div>
    <div id="accounts-list" class="row">
        <!-- Account cards will be dynamically inserted here -->
    </div>
</div>

<!-- MODALS -->

<!-- Deposit/Withdraw/Transfer Modal -->
<div class="modal fade" id="transactionModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="transactionModalTitle"></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div id="tx-response-message" class="alert" style="display: none;"></div>
                <form id="transactionForm">
                    <input type="hidden" id="tx-account-number">
                    <input type="hidden" id="tx-type">
                    <div class="mb-3">
                        <label for="tx-amount" class="form-label">Amount</label>
                        <input type="number" class="form-control" id="tx-amount" min="100" required>
                    </div>
                    <div class="mb-3" id="transfer-to-account-group" style="display: none;">
                        <label for="tx-to-account" class="form-label">To Account Number</label>
                        <input type="text" class="form-control" id="tx-to-account">
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Submit</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Mini Statement Modal -->
<div class="modal fade" id="miniStatementModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Mini Statement for <span id="mini-stmt-account"></span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <table class="table table-sm">
                    <thead><tr><th>Date</th><th>Description</th><th>Type</th><th>Amount</th><th>Balance After</th></tr></thead>
                    <tbody id="mini-stmt-table-body"></tbody>
                </table>
            </div>
        </div>
    </div>
</div>


<!-- JAVASCRIPT -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    let transactionModal;
    let miniStatementModal;

    document.addEventListener('DOMContentLoaded', function() {
        transactionModal = new bootstrap.Modal(document.getElementById('transactionModal'), {});
        miniStatementModal = new bootstrap.Modal(document.getElementById('miniStatementModal'), {});
        fetchAccounts(); // Initial fetch
    });

    function fetchAccounts() {
        const token = localStorage.getItem('jwtToken');
        const accountsListDiv = document.getElementById('accounts-list');
        accountsListDiv.innerHTML = '<p>Loading accounts...</p>';

        fetch('/api/accounts/my-accounts', {
            method: 'GET',
            headers: { 'Authorization': 'Bearer ' + token }
        })
        .then(response => response.json())
        .then(accounts => {
            accountsListDiv.innerHTML = ''; // Clear loading message
            if (accounts.length === 0) {
                accountsListDiv.innerHTML = '<p>You have no accounts yet. Click the button above to create one!</p>';
                return;
            }

            accounts.forEach(account => {
                var buttonsHtml = '';
                if (account.status === 'APPROVED') {
                    // --- THIS IS THE CORRECTED LINE ---
                    buttonsHtml =
                        '<button class="btn btn-sm btn-success me-2" onclick="openTransactionModal(\'deposit\', \'' + account.accountNumber + '\')">Deposit</button>' +
                        '<button class="btn btn-sm btn-warning me-2" onclick="openTransactionModal(\'withdraw\', \'' + account.accountNumber + '\')">Withdraw</button>' +
                        '<button class="btn btn-sm btn-info me-2" onclick="openTransactionModal(\'transfer\', \'' + account.accountNumber + '\')">Transfer</button>' +
                        '<a href="/customer/statement?accountNumber=' + account.accountNumber + '" class="btn btn-sm btn-outline-secondary me-2">Generate Statement</a>' + // RESTORED BUTTON
                        '<button class="btn btn-sm btn-outline-dark" onclick="openMiniStatement(\'' + account.accountNumber + '\')">Mini Statement</button>';
                }
				
				var remarksHtml = '';
				            if (account.status === 'REJECTED' && account.adminRemarks) {
				                remarksHtml = '<p class="card-text text-danger"><strong>Reason:</strong> ' + account.adminRemarks + '</p>';
				            }

                var card = '<div class="col-md-6 mb-3">' +
                   '    <div class="card">' +
                   '        <div class="card-header d-flex justify-content-between">' +
                   '            <h5 class="card-title mb-0">' + account.accountType + ' Account</h5>' +
                   '            <span class="badge bg-info text-dark">' + account.status + '</span>' +
                   '        </div>' +
                   '        <div class="card-body">' +
                   '            <p class="card-text"><strong>Account Number:</strong> ' + account.accountNumber + '</p>' +
                   '            <p class="card-text"><strong>Balance:</strong> ₹' + account.balance.toFixed(2) + '</p>' +
				   				remarksHtml +
                   '            <div>' + buttonsHtml + '</div>' +
                   '        </div>' +
                   '    </div>' +
                   '</div>';

                accountsListDiv.innerHTML += card;
            });
        });
    }

    function openTransactionModal(type, accountNumber) {
        document.getElementById('transactionForm').reset();
        document.getElementById('tx-response-message').style.display = 'none';
        document.getElementById('tx-account-number').value = accountNumber;
        document.getElementById('tx-type').value = type;

        const title = document.getElementById('transactionModalTitle');
        const toAccountGroup = document.getElementById('transfer-to-account-group');
        const toAccountInput = document.getElementById('tx-to-account');

        if (type === 'deposit') {
            title.textContent = 'Deposit to ' + accountNumber;
            toAccountGroup.style.display = 'none';
            toAccountInput.required = false;
        } else if (type === 'withdraw') {
            title.textContent = 'Withdraw from ' + accountNumber;
            toAccountGroup.style.display = 'none';
            toAccountInput.required = false;
        } else if (type === 'transfer') {
            title.textContent = 'Transfer from ' + accountNumber;
            toAccountGroup.style.display = 'block';
            toAccountInput.required = true;
        }
        transactionModal.show();
    }

    document.getElementById('transactionForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const type = document.getElementById('tx-type').value;
        const amount = parseFloat(document.getElementById('tx-amount').value);
        const fromAccount = document.getElementById('tx-account-number').value;
        const toAccount = document.getElementById('tx-to-account').value;
        const messageDiv = document.getElementById('tx-response-message');
        let url = '/api/transactions/' + type;
        let body = (type === 'transfer') ? { fromAccountNumber: fromAccount, toAccountNumber: toAccount, amount: amount } : { accountNumber: fromAccount, amount: amount };

        fetch(url, {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken'), 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        })
        .then(response => {
             if (!response.ok) { return response.text().then(text => { throw new Error(text || 'Transaction failed') }); }
             return response.json();
        })
        .then(data => {
            messageDiv.className = 'alert alert-success';
            messageDiv.textContent = 'Transaction successful!';
            messageDiv.style.display = 'block';
            fetchAccounts(); // Re-fetch all accounts to update balances
            setTimeout(() => { transactionModal.hide(); }, 2000);
        })
        .catch(error => {
            messageDiv.className = 'alert alert-danger';
            messageDiv.textContent = 'Error: ' + error.message;
            messageDiv.style.display = 'block';
        });
    });

    function openMiniStatement(accountNumber) {
        document.getElementById('mini-stmt-account').textContent = accountNumber;
        const tableBody = document.getElementById('mini-stmt-table-body');
        tableBody.innerHTML = '<tr><td colspan="5">Loading...</td></tr>';
        
        // Ensure this endpoint exists and is mapped correctly in your backend controllers
        const miniStatementUrl = '/api/transactions/mini-statement/' + accountNumber;

        fetch(miniStatementUrl, {
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
        })
        .then(res => {
            if(!res.ok) { return res.text().then(text => { throw new Error(text) }) }
            return res.json();
        })
        .then(transactions => {
            tableBody.innerHTML = '';
            if (transactions.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No transactions found.</td></tr>';
            } else {
                transactions.forEach(tx => {
                    const amountClass = tx.transactionType === 'DEPOSIT' ? 'text-success' : 'text-danger';
                    tableBody.innerHTML += '<tr>' +
                        '<td>' + new Date(tx.timestamp).toLocaleDateString() + '</td>' +
                        '<td>' + tx.description + '</td>' +
                        '<td>' + tx.transactionType + '</td>' +
                        '<td class="' + amountClass + '">₹' + tx.amount.toFixed(2) + '</td>' +
                        '<td>₹' + tx.balanceAfter.toFixed(2) + '</td>' +
                        '</tr>';
                });
            }
            miniStatementModal.show();
        })
        .catch(err => {
            tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Error: ' + err.message + '</td></tr>';
            miniStatementModal.show();
        });
    }
</script>
</body>
</html>