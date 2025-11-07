import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AccountService } from '../services/account';

// --- Material Imports ---
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-create-account',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatIconModule,
  ],
  templateUrl: './create-account.html',
  styleUrls: ['./create-account.scss'],
})
export class CreateAccount {
  createAccountForm: FormGroup;

  private fb = inject(FormBuilder);
  private accountService = inject(AccountService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  constructor() {
    this.createAccountForm = this.fb.group({
      accountType: ['', [Validators.required]],
      initialDeposit: [1000, [Validators.required, Validators.min(1000)]],
    });
  }

  onSubmit(): void {
    if (this.createAccountForm.invalid) {
      this.createAccountForm.markAllAsTouched();
      return;
    }

    this.accountService.createAccount(this.createAccountForm.value).subscribe({
      next: () => {
        this.snackBar.open(
          'Account application submitted successfully! It is now pending approval.',
          'Close',
          {
            duration: 5000,
          }
        );
        this.router.navigate(['/customer/dashboard']);
      },
      error: (err) => {
        let errorMessage = 'An unknown error occurred.';
        if (typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.error && err.error.message) {
          errorMessage = err.error.message;
        }
        this.snackBar.open(`Error: ${errorMessage}`, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar'],
        });
      },
    });
  }
}
