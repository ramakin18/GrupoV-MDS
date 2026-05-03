import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable, catchError, finalize, of, shareReplay } from 'rxjs';
import { PRODUCT_SERVICE_TOKEN, IProductService } from '@core/services/product.service.interface';
import { Product, ProductCreateDto, ProductFilters, ProductViewRole } from '@core/models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  products$: Observable<Product[]> = of([]);
  productForm: FormGroup;
  filterForm: FormGroup;
  role: ProductViewRole = 'USUARIO';
  isLoading = false;
  errorMessage = '';

  constructor(
    @Inject(PRODUCT_SERVICE_TOKEN) private readonly productService: IProductService,
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    this.productForm = this.fb.group({
      nombreProducto: ['', [Validators.required, Validators.minLength(4)]],
      descripcion: ['', [Validators.required, Validators.minLength(5)]],
      precio: [0, [Validators.required, Validators.min(0.01)]],
      stockDisponible: [0, [Validators.required, Validators.min(0)]]
    });
    this.filterForm = this.fb.group({
      nombre: [''],
      precio: [null, [Validators.min(0)]],
      stock: [null, [Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.role = this.normalizeRole(params.get('role'));
      this.loadProducts();
    });
  }

  get isAdmin(): boolean {
    return this.role === 'ADMIN';
  }

  get pageTitle(): string {
    return this.isAdmin ? 'Vista ADMIN de Productos' : 'Vista USUARIO de Productos';
  }

  get pageSubtitle(): string {
    return this.isAdmin
      ? 'Visualiza y administra todos los productos registrados'
      : 'Visualiza los productos activos disponibles';
  }

  get emptyMessage(): string {
    return this.isAdmin ? 'No hay productos registrados' : 'No hay productos activos para mostrar';
  }

  get emptyHint(): string {
    return this.isAdmin
      ? 'Usa el formulario de arriba para agregar tu primer producto'
      : 'Cuando el administrador registre productos activos, apareceran aca';
  }

  loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.products$ = this.productService.getAll(this.role, this.getFilters()).pipe(
      catchError((error: unknown) => {
        this.errorMessage = 'Error al cargar productos';
        console.error('Error loading products:', error);
        return of([]);
      }),
      finalize(() => {
        this.isLoading = false;
      }),
      shareReplay({ bufferSize: 1, refCount: true })
    );
  }

  applyFilters(): void {
    if (this.filterForm.invalid) {
      this.filterForm.markAllAsTouched();
      return;
    }

    this.loadProducts();
  }

  clearFilters(): void {
    this.filterForm.reset({
      nombre: '',
      precio: null,
      stock: null
    });
    this.loadProducts();
  }

  onSubmit(): void {
    if (!this.isAdmin) {
      return;
    }

    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    const productData: ProductCreateDto = this.productForm.value;

    this.productService.create(productData).subscribe({
      next: () => {
        this.productForm.reset({
          nombreProducto: '',
          descripcion: '',
          precio: 0,
          stockDisponible: 0
        });
        this.loadProducts();
      },
      error: (error: any) => {
        this.errorMessage = error?.error?.message || 'Error al crear producto';
        console.error('Error creating product:', error);
        this.isLoading = false;
      }
    });
  }

  goHome(): void {
    this.router.navigate(['/']);
  }

  private normalizeRole(role: string | null): ProductViewRole {
    return role?.toUpperCase() === 'ADMIN' ? 'ADMIN' : 'USUARIO';
  }

  private getFilters(): ProductFilters {
    const rawFilters = this.filterForm.value;

    return {
      nombre: rawFilters.nombre,
      precio: rawFilters.precio,
      stock: rawFilters.stock
    };
  }
}
