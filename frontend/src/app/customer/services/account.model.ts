export interface Account {
  id: number;
  accountNumber: string;
  accountType: 'SAVINGS' | 'CURRENT' | 'SALARY';
  balance: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  adminRemarks?: string;
}