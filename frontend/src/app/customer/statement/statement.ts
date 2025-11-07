// Path: src/app/customer/statement/statement.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AccountService } from '../services/account';
import { MatSnackBar } from '@angular/material/snack-bar';

// --- Material & Ngb Imports ---
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon'; // Import for the icon

@Component({
  selector: 'app-statement',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule
  ],
  templateUrl: './statement.html',
  styleUrls: ['./statement.scss']
})
export class Statement implements OnInit {
  statementForm: FormGroup;
  accountNumber: string | null = null;

  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private accountService = inject(AccountService);
  private snackBar = inject(MatSnackBar);

  constructor() {
    const today = new Date();
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(today.getDate() - 30);

    this.statementForm = this.fb.group({
      startDate: [thirtyDaysAgo],
      endDate: [today]
    });
  }

  ngOnInit(): void {
    this.accountNumber = this.route.snapshot.queryParamMap.get('accountNumber');
  }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  downloadStatement(): void {
    if (this.statementForm.invalid || !this.accountNumber) {
      return;
    }

    const formValues = this.statementForm.value;
    const requestBody = {
      accountNumber: this.accountNumber,
      startDate: this.formatDate(formValues.startDate),
      endDate: this.formatDate(formValues.endDate)
    };

    this.accountService.downloadStatement(requestBody).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        
        const a = document.createElement('a');
        a.href = url;
        
        const username = localStorage.getItem('username') || 'user';
        const currentDate = this.formatDate(new Date());
        a.download = `statement-${username}-${this.accountNumber}-${currentDate}.pdf`;
        
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        this.snackBar.open('Statement downloaded successfully!', 'Close', { duration: 3000 });
      },
      error: (err) => {
        this.snackBar.open('Failed to download statement. Please try again.', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}