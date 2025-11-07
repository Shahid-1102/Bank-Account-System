import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService } from '../services/account';
import { Account } from '../services/account.model';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

// --- Material & Ngb Imports ---
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NgbModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    NgbModalModule
    ,
    RouterLink
],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard implements OnInit {
  accounts: Account[] = [];
  isLoading = true;
  error: string | null = null;
  
  transactionForm: FormGroup;
  miniStatementData: any[] = [];
  selectedAccountForTx: string = '';

  private accountService = inject(AccountService);
  private fb = inject(FormBuilder);
  private snackBar = inject(MatSnackBar);
  private modalService = inject(NgbModal);

  constructor() {
    this.transactionForm = this.fb.group({
      type: [''], 
      amount: [null, [Validators.required, Validators.min(100)]],
      toAccountNumber: ['']
    });
  }

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.isLoading = true;
    this.accountService.getMyAccounts().subscribe({
      next: (data) => {
        this.accounts = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = "Failed to load account data.";
        this.isLoading = false;
      }
    });
  }

  openTransactionModal(modalContent: any, type: 'deposit' | 'withdraw' | 'transfer', accountNumber: string) {
    this.selectedAccountForTx = accountNumber;
    this.transactionForm.reset({ type: type });
    
    const toAccountControl = this.transactionForm.get('toAccountNumber');
    if (type === 'transfer') {
      toAccountControl?.setValidators([Validators.required]);
    } else {
      toAccountControl?.clearValidators();
    }
    toAccountControl?.updateValueAndValidity();
    
    this.modalService.open(modalContent, { centered: true });
  }

  submitTransaction(): void {
    if (this.transactionForm.invalid) {
      return;
    }

    const { type, amount, toAccountNumber } = this.transactionForm.value;
    let transactionObservable: Observable<any>;

    switch (type) {
      case 'deposit':
        transactionObservable = this.accountService.deposit(this.selectedAccountForTx, amount);
        break;
      case 'withdraw':
        transactionObservable = this.accountService.withdraw(this.selectedAccountForTx, amount);
        break;
      case 'transfer':
        transactionObservable = this.accountService.transfer(this.selectedAccountForTx, toAccountNumber, amount);
        break;
      default:
        return;
    }

    transactionObservable.subscribe({
      next: () => {
        this.snackBar.open('Transaction Successful!', 'Close', { duration: 3000 });
        this.modalService.dismissAll();
        this.loadAccounts(); // Refresh account data
      },
      error: (err) => {
        this.snackBar.open(`Error: ${err.error.message || 'Transaction failed.'}`, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  openMiniStatementModal(modalContent: any, accountNumber: string) {
    this.selectedAccountForTx = accountNumber;
    this.miniStatementData = [];
    this.modalService.open(modalContent, { centered: true, size: 'lg' });

    this.accountService.getMiniStatement(accountNumber).subscribe({
      next: (data) => {
        this.miniStatementData = data;
      },
      error: (err) => {
        this.miniStatementData = [{ error: err.error.message || 'Could not load statement.' }];
      }
    });
  }
}