<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin - Transaction History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<jsp:include page="../includes/header.jsp" />
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center">
        <h3>Transaction History for Account <span id="accountNumber" class="text-primary"></span></h3>
        <a href="javascript:history.back()" class="btn btn-secondary">&laquo; Back to Customer Details</a>
    </div>
    <hr>
    <table class="table table-striped">
        <thead><tr><th>Timestamp</th><th>Description</th><th>Type</th><th>Amount</th><th>Balance After</th></tr></thead>
        <tbody id="history-table-body"></tbody>
    </table>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const token = localStorage.getItem('jwtToken');
        const urlParams = new URLSearchParams(window.location.search);
        const accountNumber = urlParams.get('accountNumber');
        document.getElementById('accountNumber').textContent = accountNumber;

        fetch('/api/admin/accounts/history/' + accountNumber, {
            method: 'GET',
            headers: {'Authorization': 'Bearer ' + token}
        })
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch transaction history.');
            return response.json();
        })
        .then(transactions => {
            const tableBody = document.getElementById('history-table-body');
            tableBody.innerHTML = '';
            if (transactions.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No transactions found.</td></tr>';
                return;
            }
            transactions.forEach(tx => {
                const amountClass = tx.transactionType === 'DEPOSIT' ? 'text-success' : 'text-danger';
                // THE FIX: Replaced template literal with standard string concatenation
                tableBody.innerHTML +=
                    '<tr>' +
                    '    <td>' + new Date(tx.timestamp).toLocaleString() + '</td>' +
                    '    <td>' + tx.description + '</td>' +
                    '    <td>' + tx.transactionType + '</td>' +
                    '    <td class="' + amountClass + '">₹' + tx.amount.toFixed(2) + '</td>' +
                    '    <td>₹' + tx.balanceAfter.toFixed(2) + '</td>' +
                    '</tr>';
            });
        })
        .catch(error => {
            console.error('Error:', error);
            tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Could not load history.</td></tr>';
        });
    });
</script>
</body>
</html>