package com.github.sparkzxl.spi;

import java.util.Optional;

/**
 * SpiExtensionFactory.
 *
 * @author zhouxinlei
 */
@Join
public class SpiExtensionFactory implements ExtensionFactory {

    @Override
    public <T> T getExtension(final String key, final Class<T> clazz) {
        return Optional.ofNullable(clazz)
                .filter(Class::isInterface)
                .filter(cls -> cls.isAnnotationPresent(SPI.class))
                .map(ExtensionLoader::getExtensionLoader)
                .map(ExtensionLoader::getDefaultJoin)
                .orElse(null);
    }
}
