package lejendary.oauth2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Leijendekker
 *         <p>
 *         </code>PropertiesConfiguration</code> class will generate the properties files for you.
 *         run the <code>initialize()</code> function to check if the files in </code>PROPERTY_FILES</code> are already created,
 *         if not, this class will create those files.
 *         <p>
 *         Simply add the property files in the main application's <code>@PropertySources</code> annotation
 */

public class PropertiesConfiguration {

    private final static Logger log = LoggerFactory.getLogger(PropertiesConfiguration.class);
    private final static String[] PROPERTY_FILES = {"application.properties", "database.properties"};
    private final static String separator = File.separator;
    private final static String directoryPath = separator + "opt" + separator + "config" + separator + "oauth2";

    public static void initialize() {
        // Check if the configuration file is existing.
        // If the file is not yet visible in the configuration folder, the application will create the file.
        Resource[] resources = propertyResources();
        File directoryFile;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            log.info("Checking configuration file(s)...");
            directoryFile = new File(directoryPath);

            if (!directoryFile.exists()) {
                log.info("Configuration folder does not exists. Creating configuration folder...");

                boolean isCreated = directoryFile.mkdirs();

                if (isCreated) log.info("Created the configuration folder: {}", directoryFile.getAbsolutePath());
                else log.warn("Unable to create the configuration folder");
            }

            for (Resource resource : resources) {
                inputStream = resource.getInputStream();
                File outputFile = new File(directoryPath + separator + resource.getFilename());
                if (!outputFile.exists()) {
                    fileOutputStream = new FileOutputStream(outputFile);

                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);

                    fileOutputStream.write(buffer);

                    log.info("Created the configuration file: {}", resource.getFilename());
                }
            }
        } catch (IOException e) {
            log.warn("Cannot create configuration file: {}", e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.warn("Unable to close configuration file input stream: {}", e.getMessage());
            }

            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                log.warn("Unable to close configuration file output stream: {}", e.getMessage());
            }
        }
    }

    private static Resource[] propertyResources() {
        List<Resource> resourceList = new ArrayList<>();
        Resource[] resources = new Resource[PROPERTY_FILES.length];
        for (String propertyFile : PROPERTY_FILES) {
            resourceList.add(new ClassPathResource("properties/" + propertyFile));
        }

        return resourceList.toArray(resources);
    }

}
