import { Component, OnInit } from '@angular/core';
import { ReportApiService } from '../services/report-api.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common'; // <--- IMPORTANTE


@Component({
  selector: 'app-best-sellers-report',
  standalone: true, // Esto confirma que es un componente independiente
  imports: [
    CommonModule, // Esto habilita *ngFor y *ngIf
    FormsModule   // Esto habilita [(ngModel)] y [ngValue]
  ],
  templateUrl: './best-sellers-report.component.html',
  styleUrls: ['./best-sellers-report.component.css']
})
export class BestSellersReportComponent implements OnInit {
  loading: boolean = false;
  reportData: any[] = [];
  error: string = "";

  selectedMes: number | null = null;
  selectedAnio: number | null = null;
  appliedMes: number | null = null;
  appliedAnio: number | null = null;

  meses = [
    { name: 'Enero', value: 1 }, { name: 'Febrero', value: 2 }, { name: 'Marzo', value: 3 },
    { name: 'Abril', value: 4 }, { name: 'Mayo', value: 5 }, { name: 'Junio', value: 6 }
  ];
  anios = [2024, 2025, 2026];

  constructor(private reportService: ReportApiService) {}

  ngOnInit(): void {}

  onFilter(): void {
    if (this.selectedMes !== null && this.selectedAnio !== null) {
      this.appliedMes = this.selectedMes;
      this.appliedAnio = this.selectedAnio;
      this.loadReport();
    } else {
      this.error = "Por favor, selecciona un mes y un año válidos.";
    }
  }

  onClear(): void {
    this.selectedMes = null;
    this.selectedAnio = null;
    this.appliedMes = null;
    this.appliedAnio = null;
    this.reportData = [];
    this.error = "";
  }

  loadReport(): void {
    this.loading = true;
    this.reportService.getProductosMasVendidos(this.appliedMes, this.appliedAnio).subscribe({
      next: (data: any) => {
        // Asignamos directamente los datos
        this.reportData = Array.isArray(data) ? data : [];
        this.loading = false;
      },
      error: (err: any) => {
        this.error = "Error al conectar con el servidor.";
        console.error(err);
        this.loading = false;
      }
    });
  }
}
