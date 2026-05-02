import { Routes } from '@angular/router';
import { ProductComponent_Prueba } from './prueba/product_prueba.component';

export const routes: Routes = [
  { path: 'prueba', component: ProductComponent_Prueba },
  { path: '', redirectTo: '/prueba', pathMatch: 'full' } 
];