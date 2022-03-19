package com.github.sparkzxl.spi;

import com.github.sparkzxl.spi.fixture.JdbcSPI;
import com.github.sparkzxl.spi.fixture.MysqlSPI;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class ExtensionLoaderTest {

    /**
     * Test spi.
     */
    @Test
    public void testSPI() {
        JdbcSPI jdbcSPI = ExtensionLoader.getExtensionLoader(JdbcSPI.class).getJoin("mysql");
        System.out.println(StringUtils.equals(jdbcSPI.getClass().getName(), MysqlSPI.class.getName()));
    }

}
