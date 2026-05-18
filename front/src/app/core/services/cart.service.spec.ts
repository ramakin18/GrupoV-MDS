import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { CartService } from './cart.service';
import { Product } from '../models/product.model';

describe('CartService', () => {
  let service: CartService;
  let http: HttpClient;

  const mockProduct: Product = {
    idProducto: 1, nombreProducto: 'Test', descripcion: 'Desc',
    precio: 100, stockDisponible: 10, stockMinimo: 2
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [{ provide: HttpClient, useValue: { post: vi.fn() } }]
    });
    service = TestBed.inject(CartService);
    http = TestBed.inject(HttpClient);
  });

  it('should start with empty cart', () => {
    expect(service.getItems()).toEqual([]);
    expect(service.getTotal()).toBe(0);
    expect(service.getTotalQuantity()).toBe(0);
  });

  it('addProduct should add new item', () => {
    const result = service.addProduct(mockProduct);
    expect(result.success).toBe(true);
    expect(service.getItems().length).toBe(1);
    expect(service.getItems()[0].cantidad).toBe(1);
  });

  it('addProduct should reject product without id', () => {
    const result = service.addProduct({ ...mockProduct, idProducto: undefined });
    expect(result.success).toBe(false);
  });

  it('addProduct should reject deleted product', () => {
    const result = service.addProduct({ ...mockProduct, borrado: true });
    expect(result.success).toBe(false);
  });

  it('addProduct should reject product with no stock', () => {
    const result = service.addProduct({ ...mockProduct, stockDisponible: 0 });
    expect(result.success).toBe(false);
  });

  it('addProduct should increase quantity for existing item', () => {
    service.addProduct(mockProduct);
    const result = service.addProduct(mockProduct);
    expect(result.success).toBe(true);
    expect(service.getItems()[0].cantidad).toBe(2);
  });

  it('increaseQuantity should fail when stock exceeded', () => {
    service.addProduct(mockProduct);
    const item = service.getItems()[0];
    item.cantidad = 10;
    const result = service.increaseQuantity(1);
    expect(result.success).toBe(false);
  });

  it('removeProduct should remove item', () => {
    service.addProduct(mockProduct);
    service.removeProduct(1);
    expect(service.getItems()).toEqual([]);
  });

  it('decreaseQuantity should remove item when quantity becomes 0', () => {
    service.addProduct(mockProduct);
    service.decreaseQuantity(1);
    expect(service.getItems()).toEqual([]);
  });

  it('decreaseQuantity should reduce quantity', () => {
    service.addProduct(mockProduct);
    service.addProduct(mockProduct);
    service.decreaseQuantity(1);
    expect(service.getItems()[0].cantidad).toBe(1);
  });

  it('updateQuantity should set exact quantity', () => {
    service.addProduct(mockProduct);
    service.updateQuantity(1, 5);
    expect(service.getItems()[0].cantidad).toBe(5);
  });

  it('updateQuantity should remove item when quantity <= 0', () => {
    service.addProduct(mockProduct);
    service.updateQuantity(1, 0);
    expect(service.getItems()).toEqual([]);
  });

  it('updateQuantity should fail when exceeding stock', () => {
    service.addProduct(mockProduct);
    const result = service.updateQuantity(1, 99);
    expect(result.success).toBe(false);
  });

  it('getTotal should calculate correctly', () => {
    service.addProduct(mockProduct);
    service.addProduct(mockProduct);
    expect(service.getTotal()).toBe(200);
  });

  it('getTotalQuantity should sum all quantities', () => {
    service.addProduct(mockProduct);
    service.addProduct(mockProduct);
    service.addProduct({ ...mockProduct, idProducto: 2 });
    expect(service.getTotalQuantity()).toBe(3);
  });

  it('clearCart should empty the cart', () => {
    service.addProduct(mockProduct);
    service.clearCart();
    expect(service.getItems()).toEqual([]);
    expect(service.getTotal()).toBe(0);
  });

  it('validateStock should pass when items are within stock limits', () => {
    service.addProduct(mockProduct);
    const result = service.validateStock();
    expect(result.success).toBe(true);
  });

  it('validateStock should detect overstock', () => {
    service.addProduct(mockProduct);
    service.getItems()[0].cantidad = 99;
    const result = service.validateStock();
    expect(result.success).toBe(false);
  });

  it('validateCartWithBackend should call backend', () => {
    service.addProduct(mockProduct);
    vi.mocked(http.post).mockReturnValue(of({ valido: true, total: 100, items: [] }));
    service.validateCartWithBackend().subscribe();
    expect(http.post).toHaveBeenCalled();
  });
});
