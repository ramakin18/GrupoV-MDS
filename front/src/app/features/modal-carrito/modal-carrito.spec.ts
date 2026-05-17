import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalCarritoComponent } from './modal-carrito';

describe('ModalCarritoComponent', () => {
  let component: ModalCarritoComponent;
  let fixture: ComponentFixture<ModalCarritoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalCarritoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalCarritoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
