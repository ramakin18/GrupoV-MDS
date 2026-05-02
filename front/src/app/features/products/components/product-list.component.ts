import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { IProductService } from '../../core/services/product.service.interface';
import { Product, ProductCreateDto } from '../../core/models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css'
})
export class ProductListComponent implements OnInit {
  products$: Observable<Product[]> | null = null;
  productForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor(
    private readonly productService: IProductService,
    private readonly fb: FormBuilder
  ) {
    this.productForm = this.fb.group({
      sku: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(5)]],
      price: [0, [Validators.required, Validators.min(0.01)]],
      stock: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.products$ = this.productService.getAll();
    this.products$.subscribe({
      error: (error) => {
        this.errorMessage = 'Error al cargar productos';
        console.error('Error loading products:', error);
        this.isLoading = false;
      },
      complete: () => { this.isLoading = false; }
    });
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const productData: ProductCreateDto = this.productForm.value;

    this.productService.create(productData).subscribe({
      next: () => {
        this.productForm.reset({ sku: '', description: '', price: 0, stock: 0 });
        this.loadProducts();
      },
      error: (error) => {
        this.errorMessage = error?.error?.message || 'Error al crear producto';
        console.error('Error creating product:', error);
        this.isLoading = false;
      }
    });
  }
}
