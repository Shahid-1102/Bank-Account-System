import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Auth } from './auth/services/auth';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App {
  title = 'frontend';

  private authService = inject(Auth);
  private router = inject(Router);

  constructor() {
    window.addEventListener('pageshow', (event) => {
      if (event.persisted && !this.authService.isLoggedIn()) {
        this.router.navigate(['/auth/login'], { replaceUrl: true });
      }
    });
  }
}