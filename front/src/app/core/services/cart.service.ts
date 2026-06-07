import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { Kit } from '../models/kit.model';
import {
  CartItem,
  CartOperationResult,
  CartValidationItemRequest,
  CartValidationRequest,
  CartValidationResponse,
  KitProductReference
} from '../models/cart-item.model';
import { environment } from '@environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  // Clave usada para guardar y recuperar el carrito del navegador.
  private readonly storageKey = 'shopping_cart';
  // URL del backend que valida que los productos sigan activos y con stock.
  private readonly apiUrl = `${environment.apiUrl}/api/carrito`;
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

  // Agrega un kit completo como un solo item con su precio personalizado
  addKit(kit: Kit): CartOperationResult {
    if (!kit.idKit) {
      return { success: false, message: 'El kit no tiene un ID valido.' };
    }

    if (!kit.activo) {
      return { success: false, message: 'El kit no esta disponible.' };
    }

    if (kit.stock <= 0) {
      return { success: false, message: 'No hay stock disponible para este kit.' };
    }

    const existingItem = this.items.find(item => item.kitId === kit.idKit && item.tipo === 'kit');

    if (existingItem) {
      if (existingItem.cantidad >= (kit.stock ?? 0)) {
        return { success: false, message: 'No hay mas stock disponible para este kit.' };
      }
      this.items = this.items.map(item =>
        item.kitId === kit.idKit && item.tipo === 'kit'
          ? { ...item, cantidad: item.cantidad + 1 }
          : item
      );
      this.saveToLocalStorage();
      return { success: true, message: 'Kit agregado al carrito.' };
    }

    const kitProductos: KitProductReference[] = (kit.productos || []).map(kp => ({
      idProducto: kp.idProducto,
      cantidad: kp.cantidad
    }));

    this.items = [
      ...this.items,
      {
        idProducto: 0,
        nombreProducto: kit.nombre,
        precio: kit.precio,
        cantidad: 1,
        stockDisponible: kit.stock ?? 0,
        tipo: 'kit',
        kitId: kit.idKit,
        kitProductos
      }
    ];
    this.saveToLocalStorage();

    return { success: true, message: 'Kit agregado al carrito.' };
  }

  // Expande items tipo kit a sus productos individuales para crear el pedido
  expandKits(items: CartItem[]): { idProducto: number; cantidad: number; precioUnitario?: number }[] {
    const result: { idProducto: number; cantidad: number; precioUnitario?: number }[] = [];

    for (const item of items) {
      const kitProductos = item.kitProductos;
      if (item.tipo === 'kit' && kitProductos) {
        const totalKitCantidad = kitProductos.reduce((sum, p) => sum + p.cantidad, 0);

        for (const kp of kitProductos) {
          const precioUnitario = item.precio / totalKitCantidad;
          for (let i = 0; i < item.cantidad; i++) {
            result.push({
              idProducto: kp.idProducto,
              cantidad: kp.cantidad,
              precioUnitario
            });
          }
        }
      } else {
        result.push({
          idProducto: item.idProducto,
          cantidad: item.cantidad
        });
      }
    }

    return result;
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

  // Envia items al backend para validar stock y calcular total.
  validateCartWithBackend(expandedItems?: CartValidationItemRequest[]): Observable<CartValidationResponse> {
    const request: CartValidationRequest = {
      items: expandedItems
        ? expandedItems.map(item => ({
            idProducto: item.idProducto,
            cantidad: item.cantidad,
            ...(item.precioUnitario != null ? { precioUnitario: item.precioUnitario } : {})
          }))
        : this.items.map(item => ({
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
