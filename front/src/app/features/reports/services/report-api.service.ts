import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";

export interface ProductoMasVendido {
  nombreProducto: string;
  cantidadVendida: number;
}

export interface StockMinimoReporteItem {
  codigo: string;
  nombre: string;
  stockActual: number;
  stockMinimo: number;
}

@Injectable({
  providedIn: "root"
})
export class ReportApiService {
  private apiUrl = `${environment.apiUrl}/api/reportes`;

  constructor(private http: HttpClient) {}

  getProductosMasVendidos(mes: number | null, anio: number | null, dia: number | null = null): Observable<ProductoMasVendido[]> {
      let params = new HttpParams();
      if (mes !== null) params = params.set("mes", mes.toString());
      if (anio !== null) params = params.set("anio", anio.toString());
      if (dia !== null) params = params.set("dia", dia.toString());

      return this.http.get<ProductoMasVendido[]>(`${this.apiUrl}/productos-mas-vendidos`, { params });
  }

  getStockMinimo(): Observable<StockMinimoReporteItem[]> {
    return this.http.get<StockMinimoReporteItem[]>(`${this.apiUrl}/stock-minimo`);
  }
}
