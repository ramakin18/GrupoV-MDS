export interface Client {
  id?: number;
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  pais: string;
  provincia: string;
  localidad: string;
  calle: string;
  altura: string;
  piso?: string;
  departamento?: string;
}

export type ClientCreateDto = Omit<Client, 'id'>;
