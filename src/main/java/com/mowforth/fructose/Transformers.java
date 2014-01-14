package com.mowforth.fructose;

import com.google.common.base.Preconditions;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Transformers for common value types.
 *
 * <p>These are loaded by {@link ConfigModule} before
 * any attempt to read a configuration.</p>
 */
public class Transformers {

    private Transformers() {};

    public static final Map<Class<?>, Transformer<?>> COMMON =
            new HashMap<Class<?>, Transformer<?>>();

    static {
        COMMON.put(InetSocketAddress.class, new Transformer<InetSocketAddress>() {
            @Override
            public InetSocketAddress call(Object raw) throws Exception {
                if (raw instanceof String) {
                    String rawAddress = (String) raw;
                    String[] parts = rawAddress.split(":");
                    Preconditions.checkArgument(parts.length == 2);
                    return new InetSocketAddress(InetAddress.getByName(parts[0]), Integer.valueOf(parts[1]));
                } else if (raw instanceof List) {
                    List list = (List) raw;
                    Preconditions.checkArgument(list.size() == 2);
                    InetAddress remote = InetAddress.getByName((String)list.get(0));
                    int port = (Integer)list.get(1);
                    return new InetSocketAddress(remote, port);
                } else {
                    throw new IllegalArgumentException("Malformed InetSocketAddress");
                }
            }
        });
        COMMON.put(InetAddress.class, new Transformer<InetAddress>() {
            @Override
            public InetAddress call(Object raw) throws Exception {
                Preconditions.checkArgument(raw instanceof String);
                return InetAddress.getByName((String) raw);
            }
        });
        COMMON.put(UUID.class, new Transformer<UUID>() {
            @Override
            public UUID call(Object raw) throws Exception {
                Preconditions.checkArgument(raw instanceof String);
                return UUID.fromString((String) raw);
            }
        });
        COMMON.put(Path.class, new Transformer<Path>() {
            @Override
            public Path call(Object raw) throws Exception {
                Preconditions.checkArgument(raw instanceof String);
                Path path = Paths.get((String) raw);
                Preconditions.checkArgument(Files.exists(path));
                return path;
            }
        });
        COMMON.put(URL.class, new Transformer<URL>() {
            @Override
            public URL call(Object raw) throws Exception {
                Preconditions.checkArgument(raw instanceof String);
                return new URL((String) raw);
            }
        });
    }
}
