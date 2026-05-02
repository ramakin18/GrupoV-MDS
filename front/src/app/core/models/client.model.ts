export interface Client {
  id?: number;
  nombre: string;
  apellido: string;
  email: string;
  contrasena: string;
  pais: string;
  provincia: string;
  localidad: string;
  calle: string;
  numero: string;
  piso?: string;
  departamento?: string;
  rol?: string;
}

export type ClientCreateDto = Omit<Client, 'id'>;
