import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';
import { ProductApiService } from './features/products/services/product-api.service';
import { ClientApiService } from './features/clients/services/client-api.service';
import { IProductService } from './core/services/product.service.interface';
import { IClientService } from './core/services/client.service.interface';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    { provide: IProductService, useClass: ProductApiService },
    { provide: IClientService, useClass: ClientApiService }
  ]
};