import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/components/home.component';
import { ProductListComponent } from './features/products/components/product-list.component';
import { ClientRegistrationComponent } from './features/clients/components/client-registration.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'products', redirectTo: 'products/usuario', pathMatch: 'full' },
  { path: 'products/:role', component: ProductListComponent },
  { path: 'register', component: ClientRegistrationComponent },
  { path: '**', redirectTo: '' }
];
