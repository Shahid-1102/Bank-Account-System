// Path: src/app/layouts/customer/customer.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Auth } from '../../auth/services/auth';

// --- Material Imports for a Professional Layout ---
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './customer.html',
  styleUrls: ['./customer.scss']
})
export class Customer {
  private authService = inject(Auth);

  public username: string | null = null;

  constructor() {
    this.username = localStorage.getItem('username');
  }

  logout(): void {
    this.authService.logout();
  }
}