package com.mowforth.fructose;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;

import java.lang.reflect.Field;
import java.util.*;

/**
 * CHANGEME
 */
public abstract class ConfigModule extends AbstractModule {

    private final Config config;
    private final Map<Class<?>, Transformer<?>> transformers;

    public ConfigModule(Config configuration) {
        this.config = ConfigFactory.load().withFallback(configuration);
        this.transformers = new HashMap<Class<?>, Transformer<?>>();
        transformers.putAll(Transformers.COMMON);
    }

    /**
     * TODO document
     */
    protected abstract void setupConfiguration();

    protected <T> void registerTransformer(Class<T> klass, Transformer<T> fn) {
        transformers.put(klass, fn);
    }

    /**
     * Add configuration metadata.
     * @param klass class containing configuration metadata
     */
    protected void registerConfigKeys(Class<? extends Object> klass) {
        Field[] fields = klass.getFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class)) {

                ValueType valueType = field.getAnnotation(ValueType.class);

                Class type;
                Class<? extends Collection> ctype;
                String key = null;

                try {
                    key = (String)field.get(null);
                } catch (IllegalAccessException ex) {
                    String x = "";
                }

                // Fail on provision, not on lookup
                if (!config.hasPath(key)) {
                    continue;
                }

                if (valueType != null) {
                    type = valueType.value();
                    bind(type)
                            .annotatedWith(Names.named(key))
                            .toInstance(buildObject(type, config.getString(key)));
//                } else if (listValueType != null) {
//                    type = listValueType.value();
//
//                    final List list = buildList(type, key);
//                    Provider provider = new Provider<Object>() {
//                        @Override
//                        public Object get() {
//                            return list;
//                        }
//                    };
//                    bind(TypeLiteral.get(Types.listOf(type)))
//                            .annotatedWith(Names.named(key))
//                            .toProvider(provider);
//                } else if (setValueType != null) {
//                    type = setValueType.value();
//                    final Set set = buildSet(type, key);
//                    Provider provider = new Provider<Object>() {
//                        @Override
//                        public Object get() {
//                            return set;
//                        }
//                    };
//                    bind(TypeLiteral.get(Types.setOf(type)))
//                            .annotatedWith(Names.named(key))
//                            .toProvider(provider);
                } else {
                    ConfigValue val = config.getValue(key);
                    if (val.valueType() == ConfigValueType.STRING) {
                        bind(String.class)
                                .annotatedWith(Names.named(key))
                                .toInstance(config.getString(key));
                    } else if (val.valueType() == ConfigValueType.NUMBER) {
                        bind(Integer.class)
                                .annotatedWith(Names.named(key))
                                .toInstance(config.getInt(key));
                    } else if (val.valueType() == ConfigValueType.BOOLEAN) {
                        bind(Boolean.class)
                                .annotatedWith(Names.named(key))
                                .toInstance(config.getBoolean(key));
                    } else if (val.valueType() == ConfigValueType.LIST) {
                        List list = config.getAnyRefList(key);
                        if (list.get(0) instanceof String) {
                            bind(new TypeLiteral<List<String>>() {
                            })
                                    .annotatedWith(Names.named(key))
                                    .toInstance(config.getStringList(key));
                        } else if (list.get(0) instanceof Integer) {
                            bind(new TypeLiteral<List<Integer>>() {
                            })
                                    .annotatedWith(Names.named(key))
                                    .toInstance(config.getIntList(key));
                        } else {
                            bind(List.class)
                                    .annotatedWith(Names.named(key))
                                    .toInstance(config.getAnyRefList(key));
                        }
                    }
                }
            }
        }
    }

    private <T> T buildObject(Class<? extends T> type, Object raw) {
        Transformer transformer = transformers.get(type);
        Preconditions.checkNotNull(transformer, "Couldn't find a way to convert " + type.toString());
        try {
            return (T)transformer.call(raw);
        } catch (Exception e) {
            Throwables.propagate(e);
            return null;
        }
    }

    private <T> List<T> buildList(Class<T> type, String key) {
        List rawList = config.getAnyRefList(key);
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Object rawObject : rawList) {
            builder.add(buildObject(type, rawObject));
        }
        return builder.build();
    }

    private <T> Set<T> buildSet(Class<T> type, String key) {
        List rawList = config.getAnyRefList(key);
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (Object rawObject : rawList) {
            builder.add(buildObject(type, rawObject));
        }
        return builder.build();
    }

    @Override
    protected void configure() {
        setupConfiguration();
    }
}
