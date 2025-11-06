// Path: src/app/customer/services/account.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Account } from './account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(private http: HttpClient) { }

  getMyAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>('/api/accounts/my-accounts');
  }

  deposit(accountNumber: string, amount: number): Observable<any> {
    return this.http.post('/api/transactions/deposit', { accountNumber, amount });
  }

  withdraw(accountNumber: string, amount: number): Observable<any> {
    return this.http.post('/api/transactions/withdraw', { accountNumber, amount });
  }

  transfer(fromAccountNumber: string, toAccountNumber: string, amount: number): Observable<any> {
    return this.http.post('/api/transactions/transfer', { fromAccountNumber, toAccountNumber, amount });
  }

  getMiniStatement(accountNumber: string): Observable<any> {
    return this.http.get(`/api/transactions/mini-statement/${accountNumber}`);
  }

  downloadStatement(requestBody: any): Observable<Blob> {
    return this.http.post('/api/reports/download-statement', requestBody, {
      responseType: 'blob' // <-- This is crucial to handle the PDF file response
    });
  }

  createAccount(accountData: { accountType: string, initialDeposit: number }): Observable<any> {
    return this.http.post('/api/accounts/create', accountData);
  }
}