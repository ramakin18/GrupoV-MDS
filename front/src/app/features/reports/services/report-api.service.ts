import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable, map } from "rxjs";
import { environment } from "../../../../environments/environment";

export interface ProductoMasVendido {
  nombreProducto: string;
  cantidadVendida: number;
}

@Injectable({
  providedIn: "root"
})
export class ReportApiService {
  private apiUrl = `${environment.apiUrl}/api/reportes`;

  constructor(private http: HttpClient) {}

  // En report-api.service.ts

  getProductosMasVendidos(mes: number | null, anio: number | null): Observable<any> {
      let params = new HttpParams();
      if (mes !== null) params = params.set("mes", mes.toString());
      if (anio !== null) params = params.set("anio", anio.toString());

      return this.http.get<any>(`${this.apiUrl}/productos-mas-vendidos`, { params });
  }
}
