package com.github.sparkzxl.signature.utils;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import com.github.sparkzxl.core.json.JsonUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SM3SignUtil {

    /**
     * 签名
     *
     * @param map    参数Map
     * @param secret 秘钥
     * @return String
     */
    public static String sign(Map<String, Object> map, String secret) {
        TreeMap<String, Object> treeMap = new TreeMap<>(map);
        String sortParam = "";
        for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
            String mapKey = entry.getKey();
            if (!"sign".equals(mapKey) && ObjectUtils.isNotEmpty(entry.getValue())) {
                if (StringUtils.isEmpty(sortParam)) {
                    sortParam = mapKey + "=" + entry.getValue();
                } else {
                    sortParam += "&" + mapKey + "=" + entry.getValue();
                }
            }
        }
        //sign签名
        sortParam += "&appSecret=" + secret;
        System.out.println("sortParam：" + sortParam);
        return SM3Util.createSign(sortParam);
    }

    /**
     * 验证签名
     *
     * @param map    请求参数
     * @param secret 秘钥
     * @param sign   签名
     * @return boolean
     */
    public static boolean verifySign(Map<String, Object> map, String secret, String sign) {
        TreeMap<String, Object> treeMap = new TreeMap<>(map);
        String signValue = sign(treeMap, secret);
        return sign.equals(signValue);
    }


    public static void main(String[] args) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("socialCreditCode", "24427218FX8FQTDMBE");
        params.put("enterpriseName", "测试企业");
        params.put("contactName", "茅全勇");
        params.put("contactPhone", "13202630638");
        List<Map<String, Object>> mapList = Lists.newArrayList();
        Map<String, Object> fileMap0 = Maps.newHashMap();
        fileMap0.put("fileId", 1809035302853783552L);
        fileMap0.put("fileName", "测试文件.pdf");
        fileMap0.put("fileUrl", "http://172.16.200.202:9000/nmg/dev/330300/2024/01/22/8e95100dab334115a6e7027c44393b99.pdf");
        mapList.add(fileMap0);
        Map<String, Object> fileMap1 = Maps.newHashMap();
        fileMap1.put("fileId", 1749318552873357313L);
        fileMap1.put("fileName", "cs.pdf");
        fileMap1.put("fileUrl", "http://172.16.200.202:9000/nmg/dev/330300/2024/01/22/26f2f9855c0141d5965df7cad62d24e5.pdf");
        mapList.add(fileMap1);
        params.put("files", mapList);
        Map<String, Object> finalParams = Maps.newHashMap();
        finalParams.put("appKey", "mbnfq0dwgzltcilw");
        finalParams.put("timestamp", System.currentTimeMillis());
        finalParams.put("nonce", IdUtil.fastSimpleUUID());
        finalParams.put("dataInfo", params);
        System.out.println("加签参数：" + JsonUtils.getJson().toJsonPretty(finalParams));
        String secret = IdUtil.fastSimpleUUID();
        String sign = sign(finalParams, secret);
        System.out.println("sign:" + sign);
        boolean verified = verifySign(finalParams, secret, sign);
        System.out.println("sign verify result:" + verified);
    }
}
