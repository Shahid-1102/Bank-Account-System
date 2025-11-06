// Path: src/app/admin/services/admin.ts
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Account } from '../../customer/services/account.model';


export interface AdminStats {
  totalAccounts: number;
  pendingAccounts: number;
  approvedAccounts: number;
  rejectedAccounts: number;
}

export interface PendingAccount {
  id: number;
  userFullName: string;
  accountType: string;
  balance: number;
  createdAt: string;
}

export interface Customer {
  id: number;
  fullName: string;
  username: string;
  email: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}


@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);

  getDashboardStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>('/api/admin/dashboard/stats');
  }

  getPendingAccounts(page: number, size: number): Observable<PagedResponse<PendingAccount>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResponse<PendingAccount>>('/api/admin/accounts/pending', { params });
  }
  
  approveAccount(accountId: number): Observable<any> {
    return this.http.put(`/api/admin/accounts/approve/${accountId}`, {});
  }

  rejectAccount(accountId: number, reason: string): Observable<any> {
    return this.http.put(`/api/admin/accounts/reject/${accountId}`, { remarks: reason });
  }

  searchCustomers(query: string, page: number, size: number): Observable<PagedResponse<Customer>> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResponse<Customer>>('/api/admin/customers', { params });
  }

  getCustomerAccounts(userId: number): Observable<Account[]> {
    return this.http.get<Account[]>(`/api/admin/customers/${userId}/accounts`);
  }

  // getAccountHistory(accountNumber: string): Observable<any[]> {
  //   return this.http.get<any[]>(`/api/admin/accounts/history/${accountNumber}`);
  // }

  getAccountHistory(accountNumber: string, page: number, size: number): Observable<PagedResponse<any>> {
    const params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString());
        
    return this.http.get<PagedResponse<any>>(`/api/admin/accounts/history/${accountNumber}`, { params });
}
}