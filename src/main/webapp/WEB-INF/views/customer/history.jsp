<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Transaction History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center">
        <h3>Transaction History for Account <span id="accountNumber" class="text-primary"></span></h3>
        <a href="/customer/dashboard" class="btn btn-secondary">&laquo; Back to Dashboard</a>
    </div>
    <hr>
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Timestamp</th>
                <th>Type</th>
                <th>Description</th>
                <th>Amount</th>
                <th>Balance After</th>
            </tr>
        </thead>
        <tbody id="history-table-body">
            <!-- Rows will be injected here by JavaScript -->
        </tbody>
    </table>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            window.location.href = '/auth/login';
            return;
        }

        // Get account number from URL query parameter
        const urlParams = new URLSearchParams(window.location.search);
        const accountNumber = urlParams.get('accountNumber');
        document.getElementById('accountNumber').textContent = accountNumber;

        fetch(`/api/transactions/history/${accountNumber}`, {
            method: 'GET',
            headers: {'Authorization': 'Bearer ' + token}
        })
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch transaction history.');
            return response.json();
        })
        .then(transactions => {
            const tableBody = document.getElementById('history-table-body');
            if (transactions.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No transactions found.</td></tr>';
                return;
            }
            transactions.forEach(tx => {
                const amountClass = tx.transactionType === 'DEPOSIT' ? 'text-success' : 'text-danger';
                const row = `
                    <tr>
                        <td>${new Date(tx.timestamp).toLocaleString()}</td>
                        <td>${tx.transactionType}</td>
                        <td>${tx.description}</td>
                        <td class="${amountClass}">₹${tx.amount.toFixed(2)}</td>
                        <td>₹${tx.balanceAfter.toFixed(2)}</td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            });
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('history-table-body').innerHTML = '<tr><td colspan="5" class="text-center text-danger">Could not load history.</td></tr>';
        });
    });
</script>
</body>
</html>