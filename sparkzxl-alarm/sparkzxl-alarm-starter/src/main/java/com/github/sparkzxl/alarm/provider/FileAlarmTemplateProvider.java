package com.github.sparkzxl.alarm.provider;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.URLUtil;
import com.github.sparkzxl.alarm.autoconfigure.TemplateConfig;
import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.support.AlarmErrorCodeEnum;
import com.github.sparkzxl.core.json.JsonUtils;
import com.google.common.collect.Maps;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * description: 文件告警模板加载
 *
 * @author zhouxinlei
 */
public class FileAlarmTemplateProvider extends BaseAlarmTemplateProvider {

    private final static String HTTP = "http";
    private final static String HTTPS = "https";

    private final TemplateConfig templateConfig;
    private final Map<String, AlarmTemplate> configTemplateMap;

    public FileAlarmTemplateProvider(TemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
        this.configTemplateMap = Maps.newHashMap();
    }

    @Override
    AlarmTemplate getAlarmTemplate(String templateId) {
        AlarmTemplate alarmTemplate = configTemplateMap.get(templateId);
        if (ObjectUtils.isEmpty(alarmTemplate)) {
            String templatePath = templateConfig.getTemplatePath();
            String templateContent;
            if (StringUtils.startsWithIgnoreCase(templatePath, HTTP) || StringUtils.startsWithIgnoreCase(templatePath, HTTPS)) {
                URL url = URLUtil.url(templatePath);
                File file = FileUtil.file(url);
                FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
                templateContent = fileReader.readString();
            } else {
                templateContent = ResourceUtil.readUtf8Str(templatePath);
            }
            List<AlarmTemplate> alarmTemplateList = JsonUtils.getJson().toJavaList(templateContent, AlarmTemplate.class);
            Map<String, AlarmTemplate> templateMap = alarmTemplateList.stream()
                    .collect(Collectors.toMap(AlarmTemplate::getTemplateId, k -> k));
            configTemplateMap.putAll(templateMap);
            alarmTemplate = templateMap.get(templateId);
            if (ObjectUtils.isEmpty(alarmTemplate)) {
                throw new AlarmException(AlarmErrorCodeEnum.TEMPLATE_NOT_FOUND);
            }
        }
        return alarmTemplate;
    }
}
