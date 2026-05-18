import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Injectable } from '@angular/core';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { ProductListComponent } from './product-list.component';
import { PRODUCT_SERVICE_TOKEN, IProductService } from '@core/services/product.service.interface';
import { CartService } from '@core/services/cart.service';
import { AuthService } from '@core/services/auth.service';
import { HttpClient } from '@angular/common/http';

@Injectable()
class MockAuthService {
  private _user: any = null;
  get currentUser() { return this._user; }
  get isLoggedIn() { return this._user !== null; }
  login(u: any) { this._user = u; }
  logout() { this._user = null; }
}

describe('ProductListComponent', () => {
  let component: ProductListComponent;
  let fixture: ComponentFixture<ProductListComponent>;
  let mockProductService: IProductService;
  let mockAuthService: MockAuthService;
  let mockCartService: CartService;

  const mockProducts = [
    { idProducto: 1, nombreProducto: 'P1', descripcion: 'D1', precio: 100, stockDisponible: 10, stockMinimo: 3, borrado: false },
    { idProducto: 2, nombreProducto: 'P2', descripcion: 'D2', precio: 200, stockDisponible: 1, stockMinimo: 5, borrado: false }
  ];

  beforeEach(async () => {
    mockProductService = {
      getAll: vi.fn().mockReturnValue(of(mockProducts)),
      create: vi.fn(),
      getById: vi.fn(),
      update: vi.fn(),
      delete: vi.fn()
    };

    TestBed.configureTestingModule({
      imports: [ProductListComponent],
      providers: [
        provideRouter([]),
        { provide: PRODUCT_SERVICE_TOKEN, useValue: mockProductService },
        { provide: AuthService, useClass: MockAuthService },
        { provide: HttpClient, useValue: { post: vi.fn() } }
      ]
    });

    fixture = TestBed.createComponent(ProductListComponent);
    component = fixture.componentInstance;
    mockAuthService = TestBed.inject(AuthService) as unknown as MockAuthService;
    mockCartService = TestBed.inject(CartService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load products on init', () => {
    expect(component.products.length).toBe(2);
  });

  it('isAdmin should return false when not logged in', () => {
    expect(component.isAdmin).toBe(false);
  });

  it('isAdmin should return true for ADMIN user', () => {
    mockAuthService.login({ id: 1, nombre: 'A', apellido: 'B', email: 'a@b.com', domicilio: { pais: 'AR', provincia: 'BA', localidad: 'CABA', calle: 'C', numero: '1' }, rol: 'ADMIN' });
    expect(component.isAdmin).toBe(true);
  });

  it('isAdmin should return false for CLIENTE user', () => {
    mockAuthService.login({ id: 2, nombre: 'U', apellido: 'X', email: 'u@x.com', domicilio: { pais: 'AR', provincia: 'BA', localidad: 'CABA', calle: 'C', numero: '2' }, rol: 'CLIENTE' });
    expect(component.isAdmin).toBe(false);
  });

  it('hayProductosEscasos should detect low stock', () => {
    expect(component.hayProductosEscasos).toBe(true);
  });

  it('displayedProducts should filter low stock when mostrarEscasos is true', () => {
    component.mostrarEscasos = true;
    expect(component.displayedProducts.length).toBe(1);
    expect(component.displayedProducts[0].nombreProducto).toBe('P2');
  });

  it('toggleEscasos should toggle flag', () => {
    expect(component.mostrarEscasos).toBe(false);
    component.toggleEscasos();
    expect(component.mostrarEscasos).toBe(true);
  });

  it('adjustStock should update stockDelta', () => {
    const row = component.products[0];
    component.adjustStock(row, 5);
    expect(row.stockDelta).toBe(5);
    expect(row.stockDisponible).toBe(15);
  });

  it('adjustStock should not go below 0', () => {
    const row = component.products[0];
    component.adjustStock(row, -20);
    expect(row.stockDelta).toBe(0);
  });

  it('validatePrice should clamp negative values', () => {
    const row = component.products[0];
    row.precio = -50;
    component.validatePrice(row);
    expect(row.precio).toBe(100);
  });

  it('addToCart should add product', () => {
    component.addToCart(mockProducts[0]);
    expect(mockCartService.getItems().length).toBe(1);
  });

  it('should show error on load failure', () => {
    vi.mocked(mockProductService.getAll).mockReturnValue(throwError(() => new Error('fail')));
    component.loadProducts();
    expect(component.errorMessage).toBe('Error al cargar productos');
  });
});
