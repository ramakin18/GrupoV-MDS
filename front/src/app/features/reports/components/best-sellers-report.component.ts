import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize, timeout } from 'rxjs';
import { ProductoMasVendido, ReportApiService } from '../services/report-api.service';

@Component({
  selector: 'app-best-sellers-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './best-sellers-report.component.html',
  styleUrls: ['./best-sellers-report.component.css']
})
export class BestSellersReportComponent implements OnInit {
  loading = false;
  reportData: ProductoMasVendido[] = [];
  error = '';

  selectedMes: number | null = null;
  selectedAnio: number | null = null;
  selectedDia: number | null = null;
  appliedMes: number | null = null;
  appliedAnio: number | null = null;
  appliedDia: number | null = null;

  meses = [
    { name: 'Enero', value: 1 },
    { name: 'Febrero', value: 2 },
    { name: 'Marzo', value: 3 },
    { name: 'Abril', value: 4 },
    { name: 'Mayo', value: 5 },
    { name: 'Junio', value: 6 },
    { name: 'Julio', value: 7 },
    { name: 'Agosto', value: 8 },
    { name: 'Septiembre', value: 9 },
    { name: 'Octubre', value: 10 },
    { name: 'Noviembre', value: 11 },
    { name: 'Diciembre', value: 12 }
  ];
  anios = [2024, 2025, 2026];
  dias = Array.from({ length: 31 }, (_, index) => index + 1);

  constructor(private readonly reportService: ReportApiService) {}

  ngOnInit(): void {
    this.loadReport();
  }

  onFilter(): void {
    if (this.selectedDia !== null && (this.selectedMes === null || this.selectedAnio === null)) {
      this.error = 'Para filtrar por dia selecciona mes y anio.';
      this.reportData = [];
      return;
    }

    this.appliedMes = this.selectedMes;
    this.appliedAnio = this.selectedAnio;
    this.appliedDia = this.selectedDia;
    this.loadReport();
  }

  onClear(): void {
    this.selectedMes = null;
    this.selectedAnio = null;
    this.selectedDia = null;
    this.appliedMes = null;
    this.appliedAnio = null;
    this.appliedDia = null;
    this.reportData = [];
    this.error = '';
  }

  loadReport(): void {
    this.loading = true;
    this.error = '';

    this.reportService.getProductosMasVendidos(this.appliedMes, this.appliedAnio, this.appliedDia)
      .pipe(
        timeout(15000),
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: data => {
          this.reportData = Array.isArray(data) ? data : [];
        },
        error: err => {
          this.reportData = [];
          this.error = err?.error?.message || 'No se pudo generar el reporte.';
          console.error(err);
        }
      });
  }
}
