export interface Review {
    id: number;
    puntuacion: number;
    descripcion: string;
    fechaCreacion: string;
    nombreUsuario: string;
    eliminado: boolean;
    productoId?: number;
    kitId?: number;
}

export interface ReviewCreateDto {
    puntuacion: number;
    descripcion: string;
    usuarioId: number;
    productoId?: number;
    kitId?: number;
}
