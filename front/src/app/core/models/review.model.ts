export interface Review {
    id: number;
    puntuacion: number;
    descripcion: string;
    fechaCreacion: string;
    nombreUsuario: string;
}

export interface ReviewCreateDto {
    puntuacion: number;
    descripcion: string;
    usuario: { id: number };
    producto: { idProducto: number };
}