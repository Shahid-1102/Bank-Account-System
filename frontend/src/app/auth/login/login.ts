import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../services/auth';

// ng-bootstrap and other imports
import { NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxShinyTextComponent } from "@omnedia/ngx-shiny-text";
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    NgbAlertModule,
    NgxShinyTextComponent,
    MatButtonModule
],
  templateUrl: './login.html',
  styleUrls: ['./login.scss', '../auth.scss']
})
export class Login {
  loginForm: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: Auth,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    this.errorMessage = null;
    if (this.loginForm.invalid) {
      return;
    }

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        if (response.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/customer']);
        }
      },
      error: (err) => {
        this.errorMessage = 'Login Failed: Invalid username or password.';
      }
    });
  }
}