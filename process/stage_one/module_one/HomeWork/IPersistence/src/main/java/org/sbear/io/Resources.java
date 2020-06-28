package org.sbear.io;

import java.io.InputStream;

/**
 * @author xxyWi
 */
public class Resources {
    public static InputStream getResourceAsSteam(String path) {
        InputStream resourceAsStream = Resources.class.getClassLoader().getResourceAsStream(path);
        return resourceAsStream;
    }
}
