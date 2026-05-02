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
    nuevo: any = { sku: '', description: '', price: 0, stock: 0 };

    // Inyectamos el servicio
    constructor(private api: ProductService_Prueba) {}

    // Esto ejecuta la carga de datos automáticamente apenas entras a la página
    ngOnInit() {
        this.cargar();
    }

    // Función robusta para traer los datos
    cargar() {
        this.api.get().subscribe({
            next: (datos: any) => {
                this.lista = datos;
            },
            error: (err: any) => {
                console.error('Error al cargar productos:', err);
            }
        });
    }

    // Función robusta para enviar, con limpieza automática y recarga
    enviar() {
        this.api.post(this.nuevo).subscribe({
            next: (respuesta: any) => {
                
                // Recargamos la tabla al instante
                this.cargar(); 
                
                // Limpiamos los inputs del HTML
                this.nuevo = { sku: '', description: '', price: 0, stock: 0 };
            },
            error: (err: any) => {
                console.error('El backend rechazó los datos:', err.error.message);
                alert('Error: ' + err.error.message);
            }
        });
    }
}