
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../services/auth';

import { NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxShinyTextComponent } from "@omnedia/ngx-shiny-text";
import { MatButtonModule } from '@angular/material/button';

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password');
  const confirmPassword = control.get('confirmPassword');

  if (!password || !confirmPassword) {
    return null;
  }

  return password.value === confirmPassword.value ? null : { passwordMismatch: true };
}


@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    NgbAlertModule,
    NgxShinyTextComponent,
    MatButtonModule 
],
  templateUrl: './register.html',
  styleUrls: ['./register.scss', '../auth.scss']
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
    }, { validators: passwordMatchValidator });
  }

  onSubmit(): void {
    this.errorMessage = null;
    this.successMessage = null;

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { confirmPassword, ...userData } = this.registerForm.value;

    this.authService.register(userData).subscribe({
      next: (response) => {
        this.successMessage = 'Registration Successful! Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/auth/login']);
        }, 2000);
      },
      error: (err) => {
        this.errorMessage = `Registration Failed: ${err.error}`;
      }
    });
  }
}