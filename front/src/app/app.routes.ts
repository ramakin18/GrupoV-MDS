import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/components/home.component';
import { ProductListComponent } from './features/products/components/product-list.component';
import { ClientRegistrationComponent } from './features/clients/components/client-registration.component';
import { ClientLoginComponent } from './features/clients/components/client-login.component';
import { OrderListComponent } from './features/orders/components/order-list.component';
import { PendingDeliveryComponent } from './features/orders/components/pending-delivery.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'products', redirectTo: 'products/usuario', pathMatch: 'full' },
  { path: 'products/:role', component: ProductListComponent },
  { path: 'orders', component: OrderListComponent },
  { path: 'orders/pendientes', component: PendingDeliveryComponent },
  { path: 'register', component: ClientRegistrationComponent },
  { path: 'login', component: ClientLoginComponent },
  { path: '**', redirectTo: '' }
];
