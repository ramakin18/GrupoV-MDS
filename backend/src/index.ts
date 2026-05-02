import express, { Request, Response } from 'express';
import cors from 'cors';
// Para usar cors se requerira instalar cors con "npm install cors" y "npm install -D @types/cors"
import { getAll, create } from './main/java/feature/controllers/product.controller';

const app = express();
const port = 3000;

// Filtros de entrada
app.use(cors());         
// Nos permite que Angular (u otro origen) se conecte sin ser bloqueado
app.use(express.json()); 
// Transforma el texto plano que llega de internet a un objeto JSON

// Endpoints
// Ruta raíz para probar que el navegador que el servidor responde
app.get('/', (_req: Request, res: Response) => {
  res.send('API de Productos - Backend funcionando!');
});

// Rutas de nuestro negocio conectadas a los Controladores
app.get('/api/products', getAll);
app.post('/api/products', create);

//Arranque del Servidor
app.listen(port, () => {
  console.log(`Servidor corriendo en http://localhost:${port}`);
});