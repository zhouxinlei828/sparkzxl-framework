package com.github.sparkzxl.spi.fixture;

import com.github.sparkzxl.spi.Join;

@Join
public class MysqlSPI implements JdbcSPI {

    @Override
    public String getClassName() {
        return "mysql";
    }
}
