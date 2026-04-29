import { Request, Response } from 'express';
import { ProductServiceImpl } from '../services/impl/domain/product.service.impl';

const productService = new ProductServiceImpl();

export const getAll = (_req: Request, res: Response) => {
    const products = productService.listAll();
    return res.json(products);
};

export const create = (req: Request, res: Response) => {
    try {
        // ESte consol.log nos ayudara a comparar resultados durante la ejecucion y detectar errores.
        console.log("Datos que llegaron desde Angular:", req.body)

        const { sku, description, price, stock } = req.body;

        // Validamos el SKU y la descripción como datos obligatorios.
        if (!sku || !description) {
            return res.status(400).json({ message: "El SKU y la descripción son obligatorios." });
        }

        // Con precioValido sabremos si el numero posee mas de dos numeros posteriores a la coma.
        const precioValido = /^\d+(\.\d{1,2})?$/.test(price.toString());
        // Validamos que todo precio debe ser igual o mayor a 0 (igual por si el usuario quisiera hacer alguna prueba u oferta).
        if (price < 0 || !precioValido) {
            return res.status(400).json({ message: "El precio debe ser 0 o mayor y contener solo dos decimales." });
        }

        // Validamos que el sea mayor o igual a 0. Tambien con el o (||) comprobamos que stock sea un int.
        if (stock < 0 || !Number.isInteger(stock)) {
            return res.status(400).json({ message: "El stock no puede ser negativo." });
        }

        // Creamos un service para validadr el error desde aqui
        const result = productService.create(req.body);

        // Si no hubo error, devolvemos el éxito
        return res.status(201).json(result);
        
    // Este catch atrapara el error de service si es que ocurre.
    } catch (error: any) {

        return res.status(409).json({ message: error.message });
    }

}