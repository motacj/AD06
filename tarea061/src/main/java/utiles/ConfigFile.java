package utiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

public class ConfigFile {

    // Logger para registrar mensajes de informacion, advertencia y error relacionados con la conexion a MongoDB. Se utiliza la biblioteca SLF4J para el registro de mensajes.
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ConfigFile.class);
    private final String filename;

    public ConfigFile(String filename) {
        this.filename = filename;
    }

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
