export interface KitProductReference {
  idProducto: number;
  cantidad: number;
}

export interface CartItem {
  idProducto: number;
  nombreProducto: string;
  precio: number;
  cantidad: number;
  stockDisponible: number;
  tipo?: 'product' | 'kit';
  kitId?: number;
  kitProductos?: KitProductReference[];
}
export interface CartOperationResult {
  success: boolean;
  message?: string;
}

export interface CartValidationItemRequest {
  idProducto: number;
  cantidad: number;
  precioUnitario?: number;
}

export interface CartValidationRequest {
  items: CartValidationItemRequest[];
}

export interface CartValidationItemResponse {
  idProducto: number;
  nombreProducto: string;
  precioUnitario: number;
  cantidad: number;
  subtotal: number;
}

export interface CartValidationResponse {
  items: CartValidationItemResponse[];
  total: number;
}
