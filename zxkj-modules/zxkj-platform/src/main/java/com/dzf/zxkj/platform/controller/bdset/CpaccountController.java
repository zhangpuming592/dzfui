package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/bdset/gl_cpacckmact")
public class CpaccountController {
    @Autowired
    private ICpaccountService cpaccountService;

    @GetMapping("/queryByPz")
    public ReturnData queryByPz() {
        Grid grid = new Grid();
        try {
            YntCpaccountVO[] vos = cpaccountService
                    .queryAccountByPz(SystemUtil.getLoginCorpId());
            if (vos != null && vos.length > 0) {
                grid.setRows(Arrays.asList(vos));
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
        } catch (Exception e) {
            // TODO: 2019/10/21
        }
        return ReturnData.ok().data(grid);
    }
    @PostMapping("queryAccountRule")
    public ReturnData<Grid> queryAccountRule(@MultiRequestBody CorpVO corpVO) {
        Grid<String> json = new Grid();
        try {
            String codeRule = cpaccountService.queryAccountRule(corpVO.getPk_corp());
            json.setMsg(codeRule);
            json.setSuccess(true);
        } catch (BusinessException e) {
            json.setMsg(e.getMessage());
            json.setSuccess(false);
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryByPkCorp")
    public ReturnData<Grid> queryByPkCorp(String pk_corp) {
        Grid grid = new Grid();
        try {
            YntCpaccountVO[] vos = cpaccountService
                    .queryAccountByPz(pk_corp);
            if (vos != null && vos.length > 0) {
                grid.setRows(Arrays.asList(vos));
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
        } catch (Exception e) {
            // TODO: 2019/10/21
        }
        return ReturnData.ok().data(grid);
    }
}
