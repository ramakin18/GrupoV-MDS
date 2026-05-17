import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, finalize, shareReplay } from 'rxjs/operators';
import { IProductService, PRODUCT_SERVICE_TOKEN } from '../../../core/services/product.service.interface';
import { Product, ProductFilters, ProductViewRole } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  // 1. Variables de estado
  products$: Observable<Product[]> = of([]);
  filterForm: FormGroup;
  productForm: FormGroup; // Para el alta de productos si sos admin
  role: ProductViewRole = 'USUARIO';
  isLoading = false;
  errorMessage = '';

  constructor(
    @Inject(PRODUCT_SERVICE_TOKEN) private readonly productService: IProductService,
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    // Inicializamos formularios
    this.filterForm = this.fb.group({
      nombre: [''],
      precio: [null, [Validators.min(0)]],
      stock: [null, [Validators.min(0)]]
    });

    this.productForm = this.fb.group({
      nombreProducto: ['', [Validators.required, Validators.minLength(4)]],
      descripcion: ['', [Validators.required, Validators.minLength(5)]],
      precio: [0, [Validators.required, Validators.min(0.01)]],
      stockDisponible: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    // Escuchamos el rol de la URL y cargamos productos
    this.route.paramMap.subscribe(params => {
      const roleParam = params.get('role');
      this.role = roleParam?.toUpperCase() === 'ADMIN' ? 'ADMIN' : 'USUARIO';
      this.loadProducts();
    });
  }

  // Getters para el HTML
  get isAdmin(): boolean {
    return this.role === 'ADMIN';
  }

  get pageTitle(): string {
    return this.isAdmin ? 'Vista ADMIN de Productos' : 'Vista USUARIO de Productos';
  }

  get pageSubtitle(): string {
    return this.isAdmin ? 'Visualiza y administra el inventario' : 'Visualiza los productos activos disponibles';
  }

  get emptyMessage(): string {
    return 'No se encontraron productos con estos filtros.';
  }

  get emptyHint(): string {
    return this.isAdmin ? 'Intentá ajustar los filtros de búsqueda.' : 'Cuando el administrador registre productos, aparecerán aquí.';
  }

  // Lógica principal
  loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.products$ = this.productService.getAll(this.role, this.getFilters()).pipe(
      catchError((error) => {
        this.errorMessage = 'Error al cargar los productos. Verifique la conexión con el servidor.';
        console.error('Error loading products:', error);
        return of([]);
      }),
      finalize(() => this.isLoading = false),
      shareReplay({ bufferSize: 1, refCount: true })
    );
  }

  applyFilters(): void {
    if (this.filterForm.valid) {
      this.loadProducts();
    } else {
      this.filterForm.markAllAsTouched();
    }
  }

  clearFilters(): void {
    this.filterForm.reset({
      nombre: '',
      precio: null,
      stock: null
    });
    this.loadProducts();
  }

  private getFilters(): ProductFilters {
    const rawValues = this.filterForm.value;
    return {
      nombre: rawValues.nombre || '',
      precio: rawValues.precio,
      stock: rawValues.stock
    };
  }
  onSubmit(): void {
    if (this.productForm.valid) {
      this.isLoading = true;
      const nuevoProducto = this.productForm.value;

      this.productService.create(nuevoProducto).subscribe({
        next: () => {
          this.productForm.reset();
          this.loadProducts(); // Recargamos la lista para ver el nuevo
          this.isLoading = false;
          alert('Producto creado con éxito');
        },
        error: (err) => {
          this.errorMessage = 'Error al crear el producto';
          this.isLoading = false;
        }
      });
    } else {
      this.productForm.markAllAsTouched();
    }
  }
}
