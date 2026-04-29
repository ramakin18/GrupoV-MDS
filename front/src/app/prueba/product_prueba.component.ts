import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService_Prueba } from './product_prueba.service';

@Component({
    selector: 'app-product-prueba',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './product_prueba.component.html'
})
export class ProductComponent_Prueba implements OnInit {
    lista: any[] = [];
    nuevo = { sku: '', description: '', price: 0, stock: 0 };
    mensajeError = '';

    constructor(private api: ProductService_Prueba) { }

    ngOnInit() {
        this.cargar();
    }

    cargar() {
        this.api.get().subscribe(res => this.lista = res);
    }

    enviar() {
        this.mensajeError = ''; // Limpia errores anteriores
        this.api.post(this.nuevo).subscribe({
            next: () => {
                this.cargar();
                this.nuevo = { sku: '', description: '', price: 0, stock: 0 };
            },
            error: (err) => {
                // Atrapa los mensajes del backend (400 o 409)
                this.mensajeError = err.error.message || "Error de conexión";
            }
        });
    }
} 