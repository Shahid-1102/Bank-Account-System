<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Generate Statement</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h3>Generate Account Statement</h3>
                    <h5>Account: <span id="accountNumber" class="text-primary"></span></h5>
                </div>
                <div class="card-body">
                    <div id="errorMessage" class="alert alert-danger" style="display: none;"></div>
                    <form id="statementForm">
                        <div class="mb-3">
                            <label for="startDate" class="form-label">Start Date</label>
                            <input type="date" class="form-control" id="startDate" required>
                        </div>
                        <div class="mb-3">
                            <label for="endDate" class="form-label">End Date</label>
                            <input type="date" class="form-control" id="endDate" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Download Statement (PDF)</button>
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
    // Helper function to format a Date object into 'YYYY-MM-DD' string
    function toYYYYMMDD(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    document.addEventListener('DOMContentLoaded', function() {
        const urlParams = new URLSearchParams(window.location.search);
        const accountNumber = urlParams.get('accountNumber');
        document.getElementById('accountNumber').textContent = accountNumber;

        const endDateInput = document.getElementById('endDate');
        const startDateInput = document.getElementById('startDate');
        
        const today = new Date();
        endDateInput.value = toYYYYMMDD(today);
        
        const thirtyDaysAgo = new Date();
        thirtyDaysAgo.setDate(today.getDate() - 30);
        startDateInput.value = toYYYYMMDD(thirtyDaysAgo);
    });

    document.getElementById('statementForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            window.location.href = '/auth/login';
            return;
        }

        const accountNumber = document.getElementById('accountNumber').textContent;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        const errorMessageDiv = document.getElementById('errorMessage');
        errorMessageDiv.style.display = 'none';

        if (!startDate || !endDate) {
            errorMessageDiv.textContent = 'Error: Both Start Date and End Date are required.';
            errorMessageDiv.style.display = 'block';
            return;
        }

        const downloadUrl = '/api/reports/download-statement'; // URL is now fixed
        
        // Create the request body object
        const requestBody = {
            accountNumber: accountNumber,
            startDate: startDate,
            endDate: endDate
        };

        // Use POST and send a JSON body
        fetch(downloadUrl, {
            method: 'POST', // CHANGED
            headers: { 
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json' // CHANGED
            },
            body: JSON.stringify(requestBody) // CHANGED
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text || 'Failed to download PDF.') });
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = `statement-${accountNumber}-${startDate}.pdf`;
            document.body.appendChild(a);
a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        })
        .catch(error => {
            console.error('Error downloading statement:', error);
            errorMessageDiv.textContent = 'Error: ' + error.message;
            errorMessageDiv.style.display = 'block';
        });
    });
</script>
</body>
</html>