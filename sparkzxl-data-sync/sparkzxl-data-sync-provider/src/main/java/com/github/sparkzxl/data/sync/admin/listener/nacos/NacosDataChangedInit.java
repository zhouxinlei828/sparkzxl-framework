package com.github.sparkzxl.data.sync.admin.listener.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.data.sync.admin.listener.AbstractDataChangedInit;
import com.github.sparkzxl.data.sync.common.constant.NacosPathConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * description: NacosDataChangedInit
 *
 * @author zhouxinlei
 * @since 2022-09-05 16:27:08
 */
public class NacosDataChangedInit extends AbstractDataChangedInit {

    private static final Logger logger = LoggerFactory.getLogger(NacosDataChangedInit.class);

    private final ConfigService configService;

    public NacosDataChangedInit(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    protected boolean notExist() {
        return dataIdNotExist(NacosPathConstants.META_DATA_ID);
    }

    private boolean dataIdNotExist(final String dataId) {
        try {
            return Objects.isNull(
                    configService.getConfig(dataId,
                            NacosPathConstants.GROUP,
                            NacosPathConstants.DEFAULT_TIME_OUT));
        } catch (NacosException e) {
            logger.error("Get data from nacos error.", e);
            throw new BizException(e.getMessage());
        }
    }

}
