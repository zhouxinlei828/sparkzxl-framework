package com.github.sparkzxl.alarm.provider;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONArray;
import com.github.sparkzxl.alarm.autoconfigure.TemplateConfig;
import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.support.AlarmErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: 文件告警模板加载
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class FileAlarmTemplateProvider extends BaseAlarmTemplateProvider {

    private final static String HTTP = "http";
    private final static String HTTPS = "https";

    private final Map<String, AlarmTemplate> configTemplateMap = new HashMap<>();

    private final TemplateConfig templateConfig;

    @Override
    AlarmTemplate getAlarmTemplate(String templateId) {
        AlarmTemplate alarmTemplate = configTemplateMap.get(templateId);
        if (ObjectUtils.isEmpty(alarmTemplate)) {
            String templatePath = templateConfig.getTemplatePath();
            String templatePathStr;
            if (StringUtils.startsWithIgnoreCase(templatePath, HTTP) || StringUtils.startsWithIgnoreCase(templatePath, HTTPS)) {
                URL url = URLUtil.url(templatePath);
                File file = FileUtil.file(url);
                FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
                templatePathStr = fileReader.readString();
            } else {
                templatePathStr = ResourceUtil.readUtf8Str(templatePath);
            }
            List<AlarmTemplate> alarmTemplateList = JSONArray.parseArray(templatePathStr, AlarmTemplate.class);
            Map<String, AlarmTemplate> templateMap = alarmTemplateList.stream().collect(Collectors.toMap(AlarmTemplate::getTemplateId, k -> k));
            configTemplateMap.putAll(templateMap);
            alarmTemplate = templateMap.get(templateId);
            if (ObjectUtils.isEmpty(alarmTemplate)) {
                throw new AlarmException(AlarmErrorCodeEnum.TEMPLATE_NOT_FOUND);
            }
        }
        return alarmTemplate;
    }
}
