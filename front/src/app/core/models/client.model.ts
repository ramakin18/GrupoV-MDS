export interface DomicilioEnvio {
  pais: string;
  provincia: string;
  localidad: string;
  calle: string;
  numero: string;
  piso?: string;
  departamento?: string;
}

export interface Client {
  id?: number;
  nombre: string;
  apellido: string;
  email: string;
  domicilio: DomicilioEnvio;
  rol?: string;
}

export interface ClientCreateDto extends Omit<Client, 'id'> {
  contrasena: string;
  rol: string;
}

export interface ClientLoginDto {
  email: string;
  contrasena: string;
}
