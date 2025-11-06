// Path: src/app/auth/register/register.ts

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../services/auth';

// ng-bootstrap and other imports
import { NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';

// Custom Validator: Must be defined outside the class or as a static method.
function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password');
  const confirmPassword = control.get('confirmPassword');

  // If controls haven't been initialized, don't validate yet
  if (!password || !confirmPassword) {
    return null;
  }

  // If they don't match, return an error object
  return password.value === confirmPassword.value ? null : { passwordMismatch: true };
}


@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    NgbAlertModule
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class Register {
  registerForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: Auth,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      fullName: ['', [Validators.required]],
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=]).*$/)
      ]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: passwordMatchValidator }); // Apply the custom validator to the entire form group
  }

  onSubmit(): void {
    this.errorMessage = null;
    this.successMessage = null;

    if (this.registerForm.invalid) {
      // Mark all fields as touched to show errors
      this.registerForm.markAllAsTouched();
      return;
    }

    // Exclude 'confirmPassword' from the data sent to the backend
    const { confirmPassword, ...userData } = this.registerForm.value;

    this.authService.register(userData).subscribe({
      next: (response) => {
        this.successMessage = 'Registration Successful! Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/auth/login']);
        }, 2000); // Wait 2 seconds before redirecting
      },
      error: (err) => {
        // The error object from HttpClient contains the text response in err.error
        this.errorMessage = `Registration Failed: ${err.error}`;
      }
    });
  }
}