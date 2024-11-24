/*
 * Copyright 2024 - Charles Dabadie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jleblanc64.libcustom;

import org.apache.commons.lang3.StringUtils;

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

    public static boolean isBigger(String a, String b) {
        int countA = StringUtils.countMatches(a, ".");
        int countB = StringUtils.countMatches(b, ".");

        int countDot = Math.max(countA, countB);
        for (int i = 0; i < countDot - countA; i++)
            a += ".0";

        for (int i = 0; i < countDot - countB; i++)
            b += ".0";

        var splitA = a.split("\\.");
        var splitB = b.split("\\.");

        for (int i = 0; i < splitA.length; i++) {
            var intA = Integer.parseInt(splitA[i]);
            var intB = Integer.parseInt(splitB[i]);

            if (intA > intB)
                return true;
            else if (intA < intB)
                return false;
        }

            return false;
    }
}
