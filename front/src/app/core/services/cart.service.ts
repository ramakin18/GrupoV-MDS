import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import {
  CartItem,
  CartOperationResult,
  CartValidationRequest,
  CartValidationResponse
} from '../models/cart-item.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  // Clave usada para guardar y recuperar el carrito del navegador.
  private readonly storageKey = 'shopping_cart';
  // URL del backend que valida que los productos sigan activos y con stock.
  private readonly apiUrl = 'http://localhost:8081/api/carrito';
  // Estado en memoria del carrito mientras la app esta abierta.
  private items: CartItem[] = [];

  constructor(private readonly http: HttpClient) {
    // Al crear el servicio, intenta recuperar un carrito guardado previamente.
    this.loadFromLocalStorage();
  }

  // Entrega los productos actuales del carrito para mostrarlos en el modal.
  getItems(): CartItem[] {
    return this.items;
  }

  // Agrega un producto nuevo o aumenta su cantidad si ya existe en el carrito.
  addProduct(product: Product): CartOperationResult {
    // Estas validaciones evitan agregar productos invalidos, borrados o sin stock.
    if (!product.idProducto) {
      return {
        success: false,
        message: 'El producto no tiene un ID valido.'
      };
    }

    if (product.borrado) {
      return {
        success: false,
        message: 'El producto no esta disponible.'
      };
    }

    if (product.stockDisponible <= 0) {
      return {
        success: false,
        message: 'No hay stock disponible para este producto.'
      };
    }

    const existingItem = this.findItem(product.idProducto);

    // Si el producto ya estaba cargado, reutiliza la logica de sumar cantidad.
    if (existingItem) {
      return this.increaseQuantity(product.idProducto);
    }

    // Si no existia, lo agrega con cantidad inicial 1.
    this.items = [
      ...this.items,
      {
        idProducto: product.idProducto,
        nombreProducto: product.nombreProducto,
        precio: product.precio,
        cantidad: 1,
        stockDisponible: product.stockDisponible
      }
    ];
    this.saveToLocalStorage();

    return {
      success: true,
      message: 'Producto agregado al carrito.'
    };
  }

  // Elimina un producto completo del carrito.
  removeProduct(idProducto: number): void {
    this.items = this.items.filter(item => item.idProducto !== idProducto);
    this.saveToLocalStorage();
  }

  // Suma una unidad, siempre que no supere el stock disponible.
  increaseQuantity(idProducto: number): CartOperationResult {
    const item = this.findItem(idProducto);

    if (!item) {
      return {
        success: false,
        message: 'El producto no existe en el carrito.'
      };
    }

    if (item.cantidad >= item.stockDisponible) {
      return {
        success: false,
        message: 'No hay mas stock disponible para este producto.'
      };
    }

    this.items = this.items.map(currentItem =>
      currentItem.idProducto === idProducto
        ? { ...currentItem, cantidad: currentItem.cantidad + 1 }
        : currentItem
    );
    this.saveToLocalStorage();

    return {
      success: true,
      message: 'Cantidad actualizada.'
    };
  }

  // Resta una unidad; si queda en cero, elimina el producto.
  decreaseQuantity(idProducto: number): void {
    const item = this.findItem(idProducto);

    if (!item) {
      return;
    }

    if (item.cantidad <= 1) {
      this.removeProduct(idProducto);
      return;
    }

    this.items = this.items.map(currentItem =>
      currentItem.idProducto === idProducto
        ? { ...currentItem, cantidad: currentItem.cantidad - 1 }
        : currentItem
    );
    this.saveToLocalStorage();
  }

  // Permite setear una cantidad exacta, util si mas adelante agregas un input numerico.
  updateQuantity(idProducto: number, cantidad: number): CartOperationResult {
    const item = this.findItem(idProducto);

    if (!item) {
      return {
        success: false,
        message: 'El producto no existe en el carrito.'
      };
    }

    if (cantidad <= 0) {
      this.removeProduct(idProducto);
      return {
        success: true,
        message: 'Producto eliminado del carrito.'
      };
    }

    if (cantidad > item.stockDisponible) {
      return {
        success: false,
        message: 'La cantidad supera el stock disponible.'
      };
    }

    this.items = this.items.map(currentItem =>
      currentItem.idProducto === idProducto
        ? { ...currentItem, cantidad }
        : currentItem
    );
    this.saveToLocalStorage();

    return {
      success: true,
      message: 'Cantidad actualizada.'
    };
  }

  // Calcula el importe total del carrito.
  getTotal(): number {
    return this.items.reduce((total, item) => total + item.precio * item.cantidad, 0);
  }

  // Suma todas las unidades para mostrar el badge del icono del carrito.
  getTotalQuantity(): number {
    return this.items.reduce((total, item) => total + item.cantidad, 0);
  }

  // Vacia el carrito y actualiza localStorage.
  clearCart(): void {
    this.items = [];
    this.saveToLocalStorage();
  }

  // Valida contra el stock que ya esta cargado en el frontend.
  validateStock(): CartOperationResult {
    const invalidItem = this.items.find(item => item.cantidad > item.stockDisponible);

    if (invalidItem) {
      return {
        success: false,
        message: `El producto ${invalidItem.nombreProducto} supera el stock disponible.`
      };
    }

    return {
      success: true
    };
  }

  // Envia solo idProducto y cantidad al backend para validar contra la base real.
  validateCartWithBackend(): Observable<CartValidationResponse> {
    const request: CartValidationRequest = {
      items: this.items.map(item => ({
        idProducto: item.idProducto,
        cantidad: item.cantidad
      }))
    };

    return this.http.post<CartValidationResponse>(`${this.apiUrl}/validar`, request);
  }

  // Busca un producto dentro del carrito por su id.
  private findItem(idProducto: number): CartItem | undefined {
    return this.items.find(item => item.idProducto === idProducto);
  }

  // Persiste el carrito para que no se pierda al recargar la pagina.
  private saveToLocalStorage(): void {
    localStorage.setItem(this.storageKey, JSON.stringify(this.items));
  }

  // Recupera el carrito guardado y descarta datos corruptos si localStorage falla.
  private loadFromLocalStorage(): void {
    const savedCart = localStorage.getItem(this.storageKey);

    if (!savedCart) {
      this.items = [];
      return;
    }

    try {
      const parsedCart = JSON.parse(savedCart) as CartItem[];
      this.items = Array.isArray(parsedCart) ? parsedCart : [];
    } catch {
      this.items = [];
      localStorage.removeItem(this.storageKey);
    }
  }
}
