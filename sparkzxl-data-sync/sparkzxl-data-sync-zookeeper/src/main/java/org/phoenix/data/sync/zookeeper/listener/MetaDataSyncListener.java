package org.phoenix.data.sync.zookeeper.listener;

import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.data.sync.common.entity.MetaData;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.springframework.util.AntPathMatcher;

/**
 * description: 元数据同步监听
 *
 * @author zhouxinlei
 * @since 2022-09-09 11:07:30
 */
public class MetaDataSyncListener extends AbstractDataSyncListener {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    @Override
    protected void event(TreeCacheEvent.Type type, String path, ChildData data) {
        // if not uri register path, return.
        String configGroup = configGroup() + "/*";
        if (!MATCHER.match(configGroup, path)) {
            return;
        }
        if (type.equals(TreeCacheEvent.Type.NODE_REMOVED)) {
            final String realPath = path.substring(configGroup().length() + 1);
            String[] dataArray = StringUtils.split(realPath, StrPool.DASH);
            MetaData metaData = new MetaData();
            try {
                metaData.setUnionId(URLDecoder.decode(realPath, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected String getType() {
        return ConfigGroupEnum.META_DATA.getCode();
    }
}
