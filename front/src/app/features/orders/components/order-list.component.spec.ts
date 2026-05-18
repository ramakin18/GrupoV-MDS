import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { OrderListComponent } from './order-list.component';
import { ORDER_SERVICE_TOKEN, IOrderService } from '@core/services/order.service.interface';

describe('OrderListComponent', () => {
  let component: OrderListComponent;
  let fixture: ComponentFixture<OrderListComponent>;
  let mockOrderService: IOrderService;

  const mockOrders = [
    { idPedido: 1, clienteId: 1, nombreCliente: 'Juan', apellidoCliente: 'Perez', emailCliente: 'j@t.com', fecha: '2024-01-01', situacion: 'RESERVADO' as const, formaPago: 'EFECTIVO', total: 500, domicilioEnvio: { pais: 'AR', provincia: 'BA', localidad: 'CABA', calle: 'C', numero: '1' }, detalles: [] },
    { idPedido: 2, clienteId: 1, nombreCliente: 'Juan', apellidoCliente: 'Perez', emailCliente: 'j@t.com', fecha: '2024-01-02', situacion: 'ENTREGADO' as const, formaPago: 'EFECTIVO', total: 300, domicilioEnvio: { pais: 'AR', provincia: 'BA', localidad: 'CABA', calle: 'C', numero: '1' }, detalles: [] }
  ];

  beforeEach(async () => {
    mockOrderService = {
      getAll: vi.fn().mockReturnValue(of(mockOrders)),
      getById: vi.fn(), getPendingDelivery: vi.fn(),
      create: vi.fn(), updateSituacion: vi.fn(), cancelar: vi.fn()
    };

    TestBed.configureTestingModule({
      imports: [OrderListComponent],
      providers: [
        provideRouter([]),
        { provide: ORDER_SERVICE_TOKEN, useValue: mockOrderService }
      ]
    });

    fixture = TestBed.createComponent(OrderListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load orders on init', () => {
    expect(component.orders.length).toBe(2);
  });

  it('filteredOrders should return all when TODOS', () => {
    expect(component.filteredOrders.length).toBe(2);
  });

  it('filteredOrders should filter by status', () => {
    component.selectedStatus = 'ENTREGADO';
    expect(component.filteredOrders.length).toBe(1);
    expect(component.filteredOrders[0].situacion).toBe('ENTREGADO');
  });

  it('getStatusLabel should return correct label', () => {
    expect(component.getStatusLabel('RESERVADO')).toBe('Reservado');
    expect(component.getStatusLabel('ENTREGADO')).toBe('Entregado');
  });

  it('canCancel should return true for cancellable statuses', () => {
    expect(component.canCancel('RESERVADO')).toBe(true);
    expect(component.canCancel('ENTREGADO')).toBe(false);
    expect(component.canCancel('CANCELADO')).toBe(false);
  });

  it('getAllowedTransitions should return valid transitions', () => {
    expect(component.getAllowedTransitions('RESERVADO')).toEqual(['PENDIENTE', 'CANCELADO']);
    expect(component.getAllowedTransitions('ENTREGADO')).toEqual([]);
  });

  it('updateStatus should call service', () => {
    vi.mocked(mockOrderService.updateSituacion).mockReturnValue(of(mockOrders[0]));
    const event = { target: { value: 'PENDIENTE' } } as unknown as Event;
    component.updateStatus(event, mockOrders[0]);
    expect(mockOrderService.updateSituacion).toHaveBeenCalledWith(1, 'PENDIENTE');
  });

  it('requestCancel should set cancel state', () => {
    component.requestCancel(mockOrders[0]);
    expect(component.orderToCancel).toEqual(mockOrders[0]);
    expect(component.cancelMotivo).toBe('');
  });

  it('cancelCancel should clear cancel state', () => {
    component.requestCancel(mockOrders[0]);
    component.cancelCancel();
    expect(component.orderToCancel).toBeNull();
  });

  it('confirmCancel should call cancelar', () => {
    vi.mocked(mockOrderService.cancelar).mockReturnValue(of(mockOrders[0]));
    component.requestCancel(mockOrders[0]);
    component.cancelMotivo = 'Cliente solicito cancelacion';
    component.confirmCancel();
    expect(mockOrderService.cancelar).toHaveBeenCalledWith(1, 'Cliente solicito cancelacion');
  });

  it('should show error on load failure', () => {
    vi.mocked(mockOrderService.getAll).mockReturnValue(throwError(() => new Error('fail')));
    component.loadOrders();
    expect(component.errorMessage).toBe('Error al cargar pedidos');
  });
});
