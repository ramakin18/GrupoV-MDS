import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { routes } from './app.routes';
import { ProductApiService } from './features/products/services/product-api.service';
import { ClientApiService } from './features/clients/services/client-api.service';
import { OrderApiService } from './features/orders/services/order-api.service';
import { KitApiService } from './features/kits/services/kit-api.service';
import { PRODUCT_SERVICE_TOKEN } from './core/services/product.service.interface';
import { CLIENT_SERVICE_TOKEN } from './core/services/client.service.interface';
import { ORDER_SERVICE_TOKEN } from './core/services/order.service.interface';
import { KIT_SERVICE_TOKEN } from './core/services/kit.service.interface';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withFetch()),
    { provide: PRODUCT_SERVICE_TOKEN, useClass: ProductApiService },
    { provide: CLIENT_SERVICE_TOKEN, useClass: ClientApiService },
    { provide: ORDER_SERVICE_TOKEN, useClass: OrderApiService },
    { provide: KIT_SERVICE_TOKEN, useClass: KitApiService }
  ]
};