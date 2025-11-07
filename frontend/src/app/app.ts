import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Auth } from './auth/services/auth';
import { NgxMeteorsComponent } from '@omnedia/ngx-meteors';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, NgxMeteorsComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App {
  title = 'Bank of Manhattan';

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