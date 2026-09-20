package sv.udb.cafedonbosco.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import sv.udb.cafedonbosco.exception.AppException;
import sv.udb.cafedonbosco.util.JsonUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centraliza el manejo de errores de todos los servlets: una AppException
 * ya trae su codigo HTTP y un mensaje seguro para el cliente; cualquier
 * otro error se registra en el log del servidor y se responde con un
 * mensaje generico, sin exponer trazas ni detalles internos.
 */
public abstract class BaseServlet extends HttpServlet {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    protected void manejarError(HttpServletResponse response, Exception excepcion) throws IOException {
        if (excepcion instanceof AppException appException) {
            if (appException.getCause() != null) {
                logger.log(Level.WARNING, "AppException con causa: " + appException.getMessage(), appException.getCause());
            }
            JsonUtil.error(response, appException.getCodigoHttp(), appException.getMessage());
            return;
        }
        logger.log(Level.SEVERE, "Error inesperado en el servlet", excepcion);
        JsonUtil.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Ocurrio un error inesperado. Intenta de nuevo mas tarde.");
    }
}
