import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Auth, Profile as ProfileModel} from '../../auth/services/auth';

// --- Material Imports ---
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatListModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})
export class Profile implements OnInit {
  profile: ProfileModel | null = null;
  isLoading = true;

  private authService = inject(Auth);

  ngOnInit(): void {
    this.authService.getMyProfile().subscribe(data => {
      this.profile = data;
      this.isLoading = false;
    });
  }
}