import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';

import { ModalCarritoComponent } from './modal-carrito';
import { CartService } from '@core/services/cart.service';
import { AuthService } from '@core/services/auth.service';
import { ORDER_SERVICE_TOKEN, IOrderService } from '@core/services/order.service.interface';
import { of } from 'rxjs';

describe('ModalCarritoComponent', () => {
  let component: ModalCarritoComponent;
  let fixture: ComponentFixture<ModalCarritoComponent>;
  let mockOrderService: IOrderService;

  beforeEach(async () => {
    mockOrderService = {
      getAll: vi.fn(), getById: vi.fn(), getPendingDelivery: vi.fn(),
      create: vi.fn().mockReturnValue(of({ idPedido: 1 })),
      updateSituacion: vi.fn(), cancelar: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ModalCarritoComponent],
      providers: [
        { provide: HttpClient, useValue: { post: vi.fn().mockReturnValue(of({ valido: true, total: 0, items: [] })) } },
        { provide: ORDER_SERVICE_TOKEN, useValue: mockOrderService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ModalCarritoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
