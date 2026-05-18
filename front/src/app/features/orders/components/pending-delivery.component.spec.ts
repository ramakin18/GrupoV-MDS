import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { PendingDeliveryComponent } from './pending-delivery.component';
import { ORDER_SERVICE_TOKEN, IOrderService } from '@core/services/order.service.interface';

describe('PendingDeliveryComponent', () => {
  let component: PendingDeliveryComponent;
  let fixture: ComponentFixture<PendingDeliveryComponent>;
  let mockOrderService: IOrderService;

  const mockOrders = [
    { idPedido: 1, clienteId: 1, nombreCliente: 'Juan', apellidoCliente: 'Perez', emailCliente: 'j@t.com', fecha: '2024-01-01', situacion: 'RESERVADO' as const, formaPago: 'EFECTIVO', total: 500, domicilioEnvio: { pais: 'Argentina', provincia: 'Buenos Aires', localidad: 'CABA', calle: 'Av Siempre Viva', numero: '123', piso: '2', departamento: 'A' }, detalles: [] },
    { idPedido: 2, clienteId: 1, nombreCliente: 'Maria', apellidoCliente: 'Lopez', emailCliente: 'm@t.com', fecha: '2024-01-02', situacion: 'PENDIENTE' as const, formaPago: 'EFECTIVO', total: 300, domicilioEnvio: { pais: 'Argentina', provincia: 'BA', localidad: 'CABA', calle: 'Calle Falsa', numero: '456' }, detalles: [] }
  ];

  beforeEach(async () => {
    mockOrderService = {
      getAll: vi.fn(), getById: vi.fn(),
      getPendingDelivery: vi.fn().mockReturnValue(of(mockOrders)),
      create: vi.fn(), updateSituacion: vi.fn(), cancelar: vi.fn()
    };

    TestBed.configureTestingModule({
      imports: [PendingDeliveryComponent],
      providers: [
        provideRouter([]),
        { provide: ORDER_SERVICE_TOKEN, useValue: mockOrderService }
      ]
    });

    fixture = TestBed.createComponent(PendingDeliveryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load pending deliveries on init', () => {
    expect(component.orders.length).toBe(2);
  });

  it('estadoLabel should return correct labels', () => {
    expect(component.estadoLabel('RESERVADO')).toBe('Pendiente');
    expect(component.estadoLabel('PENDIENTE')).toBe('En preparacion');
    expect(component.estadoLabel('LISTO')).toBe('Listo');
  });

  it('estadoBadgeClass should return correct classes', () => {
    expect(component.estadoBadgeClass('RESERVADO')).toBe('badge badge-warning');
    expect(component.estadoBadgeClass('PENDIENTE')).toBe('badge badge-info');
    expect(component.estadoBadgeClass('LISTO')).toBe('badge');
  });

  it('domicilioCompleto should format address', () => {
    const d = mockOrders[0].domicilioEnvio;
    const result = component.domicilioCompleto(d);
    expect(result).toContain('Av Siempre Viva');
    expect(result).toContain('Piso 2');
    expect(result).toContain('Depto A');
  });

  it('domicilioCompleto should handle missing optional fields', () => {
    const d = mockOrders[1].domicilioEnvio;
    const result = component.domicilioCompleto(d);
    expect(result).not.toContain('Piso');
  });

  it('clienteNombre should concat name and surname', () => {
    expect(component.clienteNombre(mockOrders[0])).toBe('Juan Perez');
  });

  it('markAsReady should update status to LISTO', () => {
    vi.mocked(mockOrderService.updateSituacion).mockReturnValue(of(mockOrders[0]));
    component.markAsReady(mockOrders[0]);
    expect(mockOrderService.updateSituacion).toHaveBeenCalledWith(1, 'LISTO');
  });

  it('openCancelModal should set state', () => {
    component.openCancelModal(mockOrders[0]);
    expect(component.showCancelModal).toBe(true);
    expect(component.cancelOrder).toEqual(mockOrders[0]);
  });

  it('closeCancelModal should clear state', () => {
    component.openCancelModal(mockOrders[0]);
    component.closeCancelModal();
    expect(component.showCancelModal).toBe(false);
    expect(component.cancelOrder).toBeNull();
  });

  it('confirmCancel should call cancelar', () => {
    vi.mocked(mockOrderService.cancelar).mockReturnValue(of(mockOrders[0]));
    component.openCancelModal(mockOrders[0]);
    component.cancelMotivo = 'Razon';
    component.confirmCancel();
    expect(mockOrderService.cancelar).toHaveBeenCalledWith(1, 'Razon');
  });

  it('should show error on load failure', () => {
    vi.mocked(mockOrderService.getPendingDelivery).mockReturnValue(throwError(() => new Error('fail')));
    component.loadPendingDeliveries();
    expect(component.errorMessage).toBe('Error al cargar pedidos pendientes');
  });
});
