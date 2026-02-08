package manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {
    private final Properties properties;

    public PropertiesManager(String propFileName) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(propFileName);
        this.properties = new Properties();
        this.properties.load(is);
    }

    public String getProperty(String propName) {
        return this.properties.getProperty(propName);
    }
}
