package io.github.jleblanc64.libcustom;

import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static io.github.jleblanc64.libcustom.functional.Functor.tryF;

public class LibVersion {
    public static int extractVersion(Class<?> referenceClass) {
        return tryF(() -> extractVersionUnsafe(referenceClass)).orElse(null);
    }

    private static int extractVersionUnsafe(Class<?> referenceClass) throws IOException {
        var p = referenceClass.getProtectionDomain();
        var c = p.getCodeSource();
        var l = c.getLocation();

        try (var os = l.openStream();
             var jis = new JarInputStream(os)) {
            var props = readPomProperties(jis, referenceClass);
            var version = props.getProperty("version");
            return byteBuddyVersionToInt(version);
        }
    }

    private static Properties readPomProperties(JarInputStream jarInputStream, Class<?> referenceClass) {
        try {
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                String entryName = jarEntry.getName();
                if (entryName.startsWith("META-INF")
                        && entryName.endsWith("pom.properties")) {

                    Properties properties = new Properties();
                    ClassLoader classLoader = referenceClass.getClassLoader();
                    properties.load(classLoader.getResourceAsStream(entryName));
                    return properties;
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public static int byteBuddyVersionToInt(String version) {
        return Integer.parseInt(version.replaceAll("\\.", ""));
    }
}
