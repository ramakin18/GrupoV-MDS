import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProductService_Prueba {
    private url = 'http://localhost:3000/api/products';

    constructor(private http: HttpClient) { }

    get() {
        return this.http.get<any[]>(this.url);
    }

    post(data: any) {
        return this.http.post(this.url, data);
    }
}