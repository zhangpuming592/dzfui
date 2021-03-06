package com.dzf.zxkj.app.utils;

import com.dzf.zxkj.app.config.SecretKeyConfig;
import com.dzf.zxkj.common.utils.RC4;
import com.dzf.zxkj.common.utils.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *  公司信息加密解密工具类
 */
@Component
public class SecretCodeUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static String enCode(String value) throws RuntimeException {
        if (StringUtil.isEmpty(value))
            return value;
        String key = null;
        try {
            System.out.println(applicationContext);
            key = com.dzf.zxkj.common.utils.RC4.encry_RC4_string(value, applicationContext.getBean(SecretKeyConfig.class).getDefaultKey());
        } catch (Exception e) {
            key = value;
        }
        return key;
    }

    public static String deCode(String pvalue) throws RuntimeException {
        if (StringUtil.isEmpty(pvalue))
            return pvalue;
        String key = null;
        try {
            key = RC4.decry_RC4(pvalue, applicationContext.getBean(SecretKeyConfig.class).getDefaultKey());
        } catch (Exception e) {
            key = pvalue;
        }
        if (StringUtil.isEmpty(key))
            key = pvalue;
        return key;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
