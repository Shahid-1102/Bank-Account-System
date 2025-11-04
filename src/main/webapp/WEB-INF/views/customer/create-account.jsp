<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create New Account</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h3>Apply for a New Bank Account</h3>
                </div>
                <div class="card-body">
                    <div id="responseMessage" class="alert" style="display: none;"></div>
                    <form id="createAccountForm">
                        <div class="mb-3">
                            <label for="accountType" class="form-label">Account Type</label>
                            <select class="form-select" id="accountType" required>
                                <option value="SAVINGS">Savings</option>
                                <option value="CURRENT">Current</option>
                                <option value="SALARY">Salary</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="initialDeposit" class="form-label">Initial Deposit Amount</label>
                            <input type="number" class="form-control" id="initialDeposit" min="1000" required>
                            <div class="form-text">Minimum deposit is ₹1000.</div>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Submit Application</button>
                    </form>
                     <div class="mt-3">
                        <a href="/customer/dashboard">&laquo; Back to Dashboard</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.getElementById('createAccountForm').addEventListener('submit', function(event) {
        event.preventDefault();
        
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            window.location.href = '/auth/login';
            return;
        }

        const accountData = {
            accountType: document.getElementById('accountType').value,
            initialDeposit: parseFloat(document.getElementById('initialDeposit').value)
        };
        
        const messageDiv = document.getElementById('responseMessage');

        fetch('/api/accounts/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(accountData)
        })
        .then(async response => {
            const bodyText = await response.text();
            if (!response.ok) {
                 throw new Error(bodyText || `Error: ${response.status}`);
            }
            return bodyText;
        })
        .then(data => {
            messageDiv.textContent = 'Your account application has been submitted successfully! It is now pending approval.';
            messageDiv.className = 'alert alert-success';
            messageDiv.style.display = 'block';
            document.getElementById('createAccountForm').reset();
        })
        .catch(error => {
            messageDiv.textContent = `Error: ${error.message}`;
            messageDiv.className = 'alert alert-danger';
            messageDiv.style.display = 'block';
        });
    });
</script>
</body>
</html>