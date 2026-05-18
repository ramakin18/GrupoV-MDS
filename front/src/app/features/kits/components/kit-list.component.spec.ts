import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { KitListComponent } from './kit-list.component';
import { KIT_SERVICE_TOKEN, IKitService } from '@core/services/kit.service.interface';
import { PRODUCT_SERVICE_TOKEN, IProductService } from '@core/services/product.service.interface';

describe('KitListComponent', () => {
  let component: KitListComponent;
  let fixture: ComponentFixture<KitListComponent>;
  let mockKitService: IKitService;
  let mockProductService: IProductService;

  const mockKits = [
    { idKit: 1, nombre: 'Kit A', descripcion: 'Desc A', precio: 500, activo: true, stock: 3, productos: [{ idProducto: 1, nombreProducto: 'P1', cantidad: 2 }] },
    { idKit: 2, nombre: 'Kit B', descripcion: 'Desc B', precio: 300, activo: true, stock: 0, productos: [{ idProducto: 2, nombreProducto: 'P2', cantidad: 1 }] }
  ];

  const mockProducts = [
    { idProducto: 1, nombreProducto: 'P1', descripcion: 'D1', precio: 100, stockDisponible: 10, stockMinimo: 2, borrado: false },
    { idProducto: 2, nombreProducto: 'P2', descripcion: 'D2', precio: 200, stockDisponible: 5, stockMinimo: 2, borrado: false }
  ];

  beforeEach(async () => {
    mockKitService = {
      getAll: vi.fn().mockReturnValue(of(mockKits)),
      getById: vi.fn(),
      create: vi.fn(),
      update: vi.fn(),
      delete: vi.fn()
    };

    mockProductService = {
      getAll: vi.fn().mockReturnValue(of(mockProducts)),
      create: vi.fn(), getById: vi.fn(), update: vi.fn(), delete: vi.fn()
    };

    TestBed.configureTestingModule({
      imports: [KitListComponent],
      providers: [
        provideRouter([]),
        { provide: KIT_SERVICE_TOKEN, useValue: mockKitService },
        { provide: PRODUCT_SERVICE_TOKEN, useValue: mockProductService }
      ]
    });

    fixture = TestBed.createComponent(KitListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load kits on init', () => {
    expect(component.kits.length).toBe(2);
    expect(component.kitCount).toBe(2);
  });

  it('should load active products on init', () => {
    expect(component.products.length).toBe(2);
  });

  it('isLowStock should detect zero stock kits', () => {
    expect(component.isLowStock(mockKits[0])).toBe(false);
    expect(component.isLowStock(mockKits[1])).toBe(true);
  });

  it('getStockLabel should return correct text', () => {
    expect(component.getStockLabel(3)).toBe('3 kit(s)');
    expect(component.getStockLabel(0)).toBe('Sin stock');
    expect(component.getStockLabel(-1)).toBe('Sin stock');
  });

  it('openCreateForm should reset form fields', () => {
    component.openCreateForm();
    expect(component.showForm).toBe(true);
    expect(component.formNombre).toBe('');
    expect(component.editingKitId).toBeNull();
  });

  it('openEditForm should populate form with kit data', () => {
    component.openEditForm(mockKits[0]);
    expect(component.showForm).toBe(true);
    expect(component.formNombre).toBe('Kit A');
    expect(component.editingKitId).toBe(1);
  });

  it('cancelForm should hide form', () => {
    component.openCreateForm();
    component.cancelForm();
    expect(component.showForm).toBe(false);
  });

  it('saveKit should validate required fields', () => {
    component.openCreateForm();
    component.saveKit();
    expect(component.errorMessage).toBe('El nombre es obligatorio.');
  });

  it('saveKit should validate description', () => {
    component.openCreateForm();
    component.formNombre = 'Kit X';
    component.saveKit();
    expect(component.errorMessage).toBe('La descripcion es obligatoria.');
  });

  it('saveKit should validate price', () => {
    component.openCreateForm();
    component.formNombre = 'Kit X';
    component.formDescripcion = 'Desc';
    component.saveKit();
    expect(component.errorMessage).toBe('El precio debe ser mayor a 0.');
  });

  it('saveKit should validate at least one product', () => {
    component.openCreateForm();
    component.formNombre = 'Kit X';
    component.formDescripcion = 'Desc';
    component.formPrecio = 100;
    component.saveKit();
    expect(component.errorMessage).toBe('Debe seleccionar al menos un producto.');
  });

  it('saveKit should validate kit must have >1 product or quantity >1', () => {
    component.openCreateForm();
    component.formNombre = 'Kit X';
    component.formDescripcion = 'Desc';
    component.formPrecio = 100;
    component.formProductos = [{ idProducto: 1, nombreProducto: 'P1', cantidad: 1 }];
    component.saveKit();
    expect(component.errorMessage).toContain('mas de un producto');
  });

  it('saveKit should call create when not editing', () => {
    vi.mocked(mockKitService.create).mockReturnValue(of(mockKits[0]));
    component.openCreateForm();
    component.formNombre = 'Kit X';
    component.formDescripcion = 'Desc';
    component.formPrecio = 100;
    component.formProductos = [{ idProducto: 1, nombreProducto: 'P1', cantidad: 2 }];
    component.saveKit();
    expect(mockKitService.create).toHaveBeenCalled();
  });

  it('saveKit should call update when editing', () => {
    vi.mocked(mockKitService.update).mockReturnValue(of(mockKits[0]));
    component.openEditForm(mockKits[0]);
    component.formPrecio = 600;
    component.saveKit();
    expect(mockKitService.update).toHaveBeenCalledWith(1, expect.objectContaining({ precio: 600 }));
  });

  it('deleteKit should call service', () => {
    vi.mocked(mockKitService.delete).mockReturnValue(of(void 0));
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    component.deleteKit(mockKits[0]);
    expect(mockKitService.delete).toHaveBeenCalledWith(1);
  });

  it('availableProducts should exclude selected', () => {
    component.openCreateForm();
    component.formProductos = [{ idProducto: 1, nombreProducto: 'P1', cantidad: 1 }];
    expect(component.availableProducts.length).toBe(1);
  });
});
