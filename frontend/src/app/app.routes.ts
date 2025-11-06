// Path: src/app/app.routes.ts

import { Routes } from '@angular/router';
import { authGuard } from './auth/guards/auth-guard';
import { adminGuard } from './auth/guards/admin-guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
  },

  {
    path: 'customer',
    canActivate: [authGuard], 
    loadChildren: () => import('./customer/customer.routes').then(m => m.CUSTOMER_ROUTES)
  },

  {
    path: 'admin',
    canActivate: [authGuard, adminGuard], 
    loadChildren: () => import('./admin/admin.routes').then(m => m.ADMIN_ROUTES)
  },

  { 
    path: '', 
    redirectTo: '/auth/login', 
    pathMatch: 'full' 
  },

  { 
    path: '**', 
    redirectTo: '/auth/login' 
  }
];