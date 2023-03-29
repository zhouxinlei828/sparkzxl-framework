package com.github.sparkzxl.gateway.plugin.dubbo.handler;

import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.gateway.common.Singleton;
import com.github.sparkzxl.gateway.common.constant.enums.FilterEnum;
import com.github.sparkzxl.gateway.common.entity.FilterData;
import com.github.sparkzxl.gateway.plugin.dubbo.config.DubboRegisterConfig;
import com.github.sparkzxl.gateway.plugin.handler.FilterDataHandler;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 抽象dubbo插件处理
 *
 * @author zhouxinlei
 * @since 2022-08-15 14:59:21
 */
public abstract class AbstractDubboFilterDataHandler implements FilterDataHandler {

    @Override
    public void handlerFilter(FilterData filterData) {
        if (Objects.nonNull(filterData) && Boolean.TRUE.equals(filterData.isEnabled())) {
            String dataConfig = filterData.getConfig();
            if (StringUtils.isEmpty(dataConfig)) {
                return;
            }
            DubboRegisterConfig dubboRegisterConfig = JsonUtils.getJson().toJavaObject(dataConfig, DubboRegisterConfig.class);
            DubboRegisterConfig exist = Singleton.INSTANCE.get(DubboRegisterConfig.class);
            if (Objects.isNull(dubboRegisterConfig)) {
                return;
            }
            if (Objects.isNull(exist) || !dubboRegisterConfig.equals(exist)) {
                // If it is null, initialize it
                this.initConfigCache(dubboRegisterConfig);
            }
            Singleton.INSTANCE.single(DubboRegisterConfig.class, dubboRegisterConfig);
        }
    }

    /**
     * 初始化 dubbo 注册配置信息
     *
     * @param dubboRegisterConfig dubbo 注册配置
     */
    protected abstract void initConfigCache(DubboRegisterConfig dubboRegisterConfig);

    @Override
    public String filterNamed() {
        return FilterEnum.DUBBO.getName();
    }
}

