import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ReportApiService, StockMinimoReporteItem } from '../services/report-api.service';
import { finalize, timeout } from 'rxjs';

@Component({
  selector: 'app-minimum-stock-report',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './minimum-stock-report.component.html',
  styleUrls: ['./minimum-stock-report.component.css']
})
export class MinimumStockReportComponent implements OnInit {
  loading = false;
  error = '';
  reportData: StockMinimoReporteItem[] = [];

  constructor(private readonly reportService: ReportApiService) {}

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport(): void {
    this.loading = true;
    this.error = '';

    this.reportService.getStockMinimo()
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

  isBelowMinimum(item: StockMinimoReporteItem): boolean {
    return item.stockActual < item.stockMinimo;
  }
}
