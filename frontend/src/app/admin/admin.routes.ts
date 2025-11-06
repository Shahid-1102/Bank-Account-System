// Path: src/app/admin/admin.routes.ts
import { Routes } from '@angular/router';
import { Admin } from '../layouts/admin/admin';
import { Dashboard } from './dashboard/dashboard';
import { CustomerDetails } from './customer-details/customer-details';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: Admin,
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'customers/:id', component: CustomerDetails },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];