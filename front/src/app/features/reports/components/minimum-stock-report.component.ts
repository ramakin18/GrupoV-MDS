import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ReportApiService, StockMinimoReporteItem } from '../services/report-api.service';
import { finalize, timeout } from 'rxjs';

@Component({
  selector: 'app-minimum-stock-report',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './minimum-stock-report.component.html',
  styleUrls: ['./minimum-stock-report.component.css']
})
export class MinimumStockReportComponent implements OnInit {
  loading = false;
  error = '';
  reportData: StockMinimoReporteItem[] = [];

  constructor(
    private readonly reportService: ReportApiService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport(): void {
    this.loading = true;
    this.error = '';

    this.reportService.getStockMinimo()
      .pipe(
        timeout(15000),
        finalize(() => {
          this.loading = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: data => {
          this.reportData = Array.isArray(data) ? data : [];
          this.cdr.markForCheck();
        },
        error: err => {
          this.reportData = [];
          this.error = err?.error?.message || 'No se pudo generar el reporte.';
          console.error(err);
          this.cdr.markForCheck();
        }
      });
  }

  isBelowMinimum(item: StockMinimoReporteItem): boolean {
    return item.stockActual < item.stockMinimo;
  }
}
