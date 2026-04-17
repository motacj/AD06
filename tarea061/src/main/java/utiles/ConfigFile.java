package utiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
/**
 * Yo soy la clase encargada de leer archivos de configuración.
 *
 * Mi papel es muy importante aunque no se vea en pantalla,
 * porque gracias a mí otras clases pueden obtener valores externos,
 * como por ejemplo la configuración de MongoDB.
 *
 * Dicho fácil:
 * yo leo el archivo,
 * convierto su contenido en propiedades
 * y se lo entrego a quien lo necesite.
 */
public class ConfigFile {

    // Logger para registrar mensajes de informacion, advertencia y error relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para el registro de mensajes.
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ConfigFile.class);
    private final String filename;
    /**
    * Aquí yo guardo el nombre del archivo de configuración que tendré que leer.
    *
    * @param filename nombre del archivo de propiedades que quiero cargar.
    */
    public ConfigFile(String filename) {
        this.filename = filename;
    }
    /**
     * Aquí yo intento leer el archivo de configuración indicado y cargar sus propiedades.
     *
     * Si encuentro el archivo, lo abro, lo leo y devuelvo sus valores.
     * Si no lo encuentro o hay un problema de lectura,
     * registro el error y devuelvo null.
     *
     * @return un objeto Properties con la configuración cargada, o null si falla la lectura.
     */
    public Properties readPropertiesFiles() {
        Properties properties = null;
        try (InputStream input = ConfigFile.class.getClassLoader().getResourceAsStream(this.filename)) {

            if (input == null) {
                logger.error("No se encontro el archivo de configuracion: {}", this.filename);
                return null;
            }

            properties = new Properties();
            logger.info("Archivo de configuracion cargado correctamente: {}", this.filename);
            properties.load(input);
            return properties;

        } catch (IOException e) {
            logger.error("Error al leer el archivo de configuracion: " + e.getMessage());
            return null;
        }
        
    }
}
