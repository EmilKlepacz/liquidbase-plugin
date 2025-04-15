package liquidbase.plugin.utils;

import java.io.*;

public class PluginResourcesUtils {
    public static InputStream getResourceAsStream(String resourceName) {
        return PluginResourcesUtils.class.getClassLoader().getResourceAsStream(resourceName);
    }

    public static void copyResourceToFile(String resourceName, String outputPath) throws IOException {
        InputStream inputStream = PluginResourcesUtils.getResourceAsStream(resourceName);
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new IOException("Error copying resource to file: " + e.getMessage());
            }
        } else {
            throw new FileNotFoundException("Resource not found: " + resourceName);
        }
    }
}
