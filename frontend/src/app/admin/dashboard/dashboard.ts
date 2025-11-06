// Path: src/app/admin/dashboard/dashboard.ts
import { Component, OnInit, ViewChild, AfterViewInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService, AdminStats, PendingAccount, Customer } from '../services/admin';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';

// --- Material Imports for a Rich UI ---
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, MatCardModule, MatProgressSpinnerModule, MatTableModule,
    MatPaginatorModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    RouterLink
],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard implements OnInit, AfterViewInit {
  // Services
  private adminService = inject(AdminService);
  private snackBar = inject(MatSnackBar);
  
  // Data properties
  stats: AdminStats | null = null;
  isLoadingStats = true;

  // Pending Accounts Table
  pendingDisplayedColumns: string[] = ['customerName', 'accountType', 'deposit', 'appliedOn', 'actions'];
  pendingDataSource: PendingAccount[] = [];
  pendingTotalElements = 0;
  isLoadingPending = true;

  // Customer Management Table
  customerDisplayedColumns: string[] = ['id', 'fullName', 'username', 'email', 'actions'];
  customerDataSource: Customer[] = [];
  customerTotalElements = 0;
  isLoadingCustomers = true;
  private searchSubject = new Subject<string>();

  @ViewChild('pendingPaginator') pendingPaginator!: MatPaginator;
  @ViewChild('customerPaginator') customerPaginator!: MatPaginator;
  
  ngOnInit(): void {
    this.loadStats();
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(searchQuery => {
      this.customerPaginator.pageIndex = 0;
      this.loadCustomers(searchQuery);
    });
  }

  ngAfterViewInit(): void {
    // Load initial data once paginators are available
    this.loadPendingAccounts();
    this.loadCustomers();
    
    // Subscribe to paginator events
    this.pendingPaginator.page.pipe(tap(() => this.loadPendingAccounts())).subscribe();
    this.customerPaginator.page.pipe(tap(() => this.loadCustomers())).subscribe();
  }

  loadStats(): void {
    this.isLoadingStats = true;
    this.adminService.getDashboardStats().subscribe(data => {
      this.stats = data;
      this.isLoadingStats = false;
    });
  }

  loadPendingAccounts(): void {
    this.isLoadingPending = true;
    this.adminService.getPendingAccounts(this.pendingPaginator?.pageIndex ?? 0, 5).subscribe(pageData => {
      this.pendingDataSource = pageData.content;
      this.pendingTotalElements = pageData.totalElements;
      this.isLoadingPending = false;
    });
  }

  loadCustomers(query: string = (document.getElementById('customer-search') as HTMLInputElement)?.value || ''): void {
    this.isLoadingCustomers = true;
    this.adminService.searchCustomers(query, this.customerPaginator?.pageIndex ?? 0, 10).subscribe(pageData => {
      this.customerDataSource = pageData.content;
      this.customerTotalElements = pageData.totalElements;
      this.isLoadingCustomers = false;
    });
  }

  onSearch(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.searchSubject.next(filterValue.trim().toLowerCase());
  }

  approve(accountId: number): void {
    this.adminService.approveAccount(accountId).subscribe(() => {
      this.snackBar.open('Account approved successfully!', 'Close', { duration: 3000 });
      this.refreshData();
    });
  }

  reject(accountId: number): void {
    const reason = prompt('Please enter the reason for rejection (this will be visible to the customer):');
    if (reason && reason.trim()) {
      this.adminService.rejectAccount(accountId, reason).subscribe(() => {
        this.snackBar.open('Account rejected successfully!', 'Close', { duration: 3000 });
        this.refreshData();
      });
    }
  }
  
  refreshData(): void {
    this.loadStats();
    this.loadPendingAccounts();
  }
}