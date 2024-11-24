package io.github.jleblanc64.libcustom;

import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static io.github.jleblanc64.libcustom.functional.Functor.tryF;

public class LibVersion {
    public static String extractVersion(Class<?> referenceClass) {
        return tryF(() -> extractVersionUnsafe(referenceClass)).orElse(null);
    }

    private static String extractVersionUnsafe(Class<?> referenceClass) throws IOException {
        var p = referenceClass.getProtectionDomain();
        var c = p.getCodeSource();
        var l = c.getLocation();

        try (var os = l.openStream();
             var jis = new JarInputStream(os)) {

            var props = readPomProperties(jis, referenceClass);
            return props.getProperty("version");
        }
    }

    private static Properties readPomProperties(JarInputStream jarInputStream, Class<?> referenceClass) throws IOException {
        JarEntry jarEntry;
        while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            var entryName = jarEntry.getName();
            if (entryName.startsWith("META-INF") && entryName.endsWith("pom.properties")) {

                var props = new Properties();
                var cl = referenceClass.getClassLoader();
                props.load(cl.getResourceAsStream(entryName));
                return props;
            }
        }

        return null;
    }

    public static int byteBuddyVersionToInt(String version) {
        return Integer.parseInt(version.replaceAll("\\.", ""));
    }
}
