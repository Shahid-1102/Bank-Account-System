import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { AdminService } from '../services/admin';
import { Account } from '../../customer/services/account.model';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NgbModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-customer-details',
  standalone: true,
  imports: [
    CommonModule, RouterModule, MatCardModule, MatButtonModule,
    MatProgressSpinnerModule, NgbModalModule, MatPaginatorModule, MatIconModule
  ],
  templateUrl: './customer-details.html',
  styleUrls: ['./customer-details.scss']
})
export class CustomerDetails implements OnInit {
  isLoading = true;
  accounts: Account[] = [];
  userId: number | null = null;
  
  // For the history modal
  historyData: any[] = [];
  historyTotalElements = 0;
  isLoadingHistory = false;
  selectedAccountForHistory: string = '';

  @ViewChild('historyPaginator') historyPaginator!: MatPaginator;

  private route = inject(ActivatedRoute);
  private adminService = inject(AdminService);
  private modalService = inject(NgbModal);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.userId = +id;
      this.adminService.getCustomerAccounts(this.userId).subscribe(data => {
        this.accounts = data;
        this.isLoading = false;
      });
    }
  }

  openHistoryModal(modalContent: any, accountNumber: string): void {
    this.selectedAccountForHistory = accountNumber;
    this.modalService.open(modalContent, { centered: true, size: 'xl' }).result.then(() => {
      this.historyPaginator.pageIndex = 0;
    }, () => {});
    setTimeout(() => {
      this.loadHistory();
      this.historyPaginator.page.subscribe(() => this.loadHistory());
    }, 100);
  }

  loadHistory(): void {
    this.isLoadingHistory = true;
    const page = this.historyPaginator?.pageIndex ?? 0;
    const size = this.historyPaginator?.pageSize ?? 10;

    this.adminService.getAccountHistory(this.selectedAccountForHistory, page, size).subscribe(pageData => {
      this.historyData = pageData.content;
      this.historyTotalElements = pageData.totalElements;
      this.isLoadingHistory = false;
    });
  }
}