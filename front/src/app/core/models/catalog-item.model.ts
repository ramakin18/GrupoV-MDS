export interface CatalogProductItem {
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
  imagenUrl?: string;
}

export interface CatalogItem {
  type: 'product' | 'kit';
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  imagenUrl?: string;
  productos?: CatalogProductItem[];
  imagenesCollage?: string[];
  promedioPuntuacion?: number;
  cantidadResenas?: number;
}
