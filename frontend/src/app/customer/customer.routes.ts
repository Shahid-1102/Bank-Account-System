import { Routes } from '@angular/router';
import { Customer } from '../layouts/customer/customer';
import { Dashboard } from './dashboard/dashboard';
import { Statement } from './statement/statement';
import { CreateAccount } from './create-account/create-account';
import { Profile } from './profile/profile';

export const CUSTOMER_ROUTES: Routes = [
  {
    path: '',
    component: Customer,
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'statement', component: Statement },
      { path: 'create-account', component: CreateAccount },
      { path: 'profile', component: Profile },
      // Future routes like 'profile', 'history' will go here
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];