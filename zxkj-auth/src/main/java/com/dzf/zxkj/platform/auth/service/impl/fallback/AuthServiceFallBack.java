package com.dzf.zxkj.platform.auth.service.impl.fallback;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class AuthServiceFallBack {
    public static List<String> getPkCorpByUserId(String userid, Throwable throwable) {
        log.error("[鉴权中心-获取用户["+userid+"]下公司列表] - [熔断] :{}", throwable.getStackTrace());
        return new ArrayList();
    }

    public static Set<String> getAllPermission(Throwable throwable) {
        log.error("[鉴权中心-获取权限列表] - [熔断] :{}", throwable.getStackTrace());
        return new HashSet();
    }

    public static Set<String> getPermisssionByUseridAndPkCorp(String userid, String pk_corp, Throwable throwable) {
        log.error("[鉴权中心-获取用户["+userid+":"+pk_corp+"]权限列表] - [熔断] :{}", throwable.getStackTrace());
        return new HashSet();
    }
}