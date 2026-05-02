import { Routes } from '@angular/router';
import { ProductListComponent } from './features/products/components/product-list.component';
import { ClientRegistrationComponent } from './features/clients/components/client-registration.component';

export const routes: Routes = [
  { path: '', component: ProductListComponent, pathMatch: 'full' },
  { path: 'products', component: ProductListComponent },
  { path: 'register', component: ClientRegistrationComponent },
  { path: '**', redirectTo: '' }
];
