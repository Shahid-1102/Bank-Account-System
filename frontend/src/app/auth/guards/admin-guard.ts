import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';
import { MatSnackBar } from '@angular/material/snack-bar';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(Auth);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  if (authService.isLoggedIn() && authService.getUserRole() === 'ADMIN') {
    return true; // User is logged in AND is an admin, allow access
  } else {
    // Show an error message and redirect
    snackBar.open('Access Denied: You do not have permission to view this page.', 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
    router.navigate(['/auth/login']);
    return false;
  }
};