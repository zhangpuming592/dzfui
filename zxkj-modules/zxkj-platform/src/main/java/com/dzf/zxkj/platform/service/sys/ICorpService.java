package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface ICorpService {
    CorpVO queryByPk(String pk_corp) throws DZFWarpException;
}