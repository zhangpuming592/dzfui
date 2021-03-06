package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.YwHdVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwbb.YwHdExcelField;
import com.dzf.zxkj.report.service.cwbb.IYwHdReport;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("gl_rep_ywhdbact")
@Slf4j
public class YwHdController  extends ReportBaseController {

    @Autowired
    private IYwHdReport gl_rep_ywhdserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO){
        Grid grid = new Grid();

        QueryParamVO queryParamvo = getQueryParamVO(queryvo,corpVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{queryParamvo.getPk_corp()},null);
            String begindate = DateUtils.getPeriod(queryParamvo.getBegindate1());
            String ishajz = "N";
            if(queryParamvo.getIshasjz()!=null && queryParamvo.getIshasjz().booleanValue()){
                ishajz = "Y";
            }
            queryParamvo.setQjq(begindate);
            queryParamvo.setQjz(begindate);
            queryParamvo.setIshasjz(new DZFBoolean(ishajz));
            queryParamvo.setIshassh(DZFBoolean.TRUE);
            queryParamvo.setXswyewfs(DZFBoolean.FALSE);

            //开始日期应该在建账日期前
            corpVO = zxkjPlatformService.queryCorpByPk(queryParamvo.getPk_corp());
            checkPowerDate(queryParamvo,corpVO);

            YwHdVO[] ywvos =  gl_rep_ywhdserv.queryYwHdValues(queryParamvo);

            if(ywvos==null || ywvos.length ==0){
                grid.setMsg("当前数据为空!");
            }else{
                grid.setMsg("查询成功");
            }
            grid.setTotal((long) (ywvos == null ? 0 : ywvos.length));
            grid.setRows(ywvos == null ? new ArrayList<YwHdVO>() : Arrays.asList(ywvos));
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<YwHdVO>());
            printErrorLog(grid, e, "查询失败！");
        }
        //日志记录接口
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,
                "业务活动表查询:"+queryParamvo.getBegindate1().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    //导出Excel
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryParamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response){
        // 校验
        checkSecurityData(null, new String[]{queryParamvo.getPk_corp()},null);
        YwHdVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(),YwHdVO[].class);
        String gs=  excelExportVO.getCorpName();
        String qj=  excelExportVO.getTitleperiod();

        Excelexport2003<YwHdVO> lxs = new Excelexport2003<YwHdVO>();
        YwHdExcelField yhd = new YwHdExcelField();
        yhd.setZeroshownull(!queryParamvo.getBshowzero().booleanValue());
        yhd.setYwhdvos(listVo);
        yhd.setQj(qj);
        yhd.setCreator(userVO.getCuserid());
        yhd.setCorpName(gs);

        //日志记录接口
        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,
                "业务活动表导出:"+qj, ISysConstants.SYS_2);
        baseExcelExport(response,lxs,yhd);

    }

    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        try {
            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            QueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), QueryParamVO.class);
            // 校验
            checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String strlist = printParamVO.getList();
            if(StringUtil.isEmpty(strlist)){
                return;
            }

            YwHdVO[] bodyvos = JsonUtils.deserialize(strlist, YwHdVO[].class);

            List<ColumnCellAttr> columncellattrlist = new ArrayList<ColumnCellAttr>();

            columncellattrlist.add(new ColumnCellAttr("项目",null,null,2,"xm",6));
            columncellattrlist.add(new ColumnCellAttr("行次",null,null,2,"hs",1));
            columncellattrlist.add(new ColumnCellAttr("本月数",null,3,null,null,0));
            columncellattrlist.add( new ColumnCellAttr("本年累计数",null,3,null,null,0));
            columncellattrlist.add(new ColumnCellAttr(" 非限定性",null,null,null,"monfxdx",1));
            columncellattrlist.add(new ColumnCellAttr(" 限定性",null,null,null,"monxdx",1));
            columncellattrlist.add(new ColumnCellAttr(" 合计",null,null,null,"monhj",1));
            columncellattrlist.add(new ColumnCellAttr(" 非限定性",null,null,null,"yearfxdx",1));
            columncellattrlist.add(new ColumnCellAttr(" 限定性",null,null,null,"yearxdx",1));
            columncellattrlist.add(new ColumnCellAttr(" 合计",null,null,null,"yearhj",1));

            //初始化表头
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间",printParamVO.getTitleperiod());
            tmap.put("单位", "元");
            if(pmap.get("pageOrt").equals("Y")){
                printReporUtil.setIscross(DZFBoolean.TRUE);//是否横向
                printReporUtil.setLineheight(18f);//设置行高
            } else{
                printReporUtil.setIscross(DZFBoolean.FALSE);//是否横向
            }

            if(pmap.get("type").equals("2")){//B5显示12f
                printReporUtil.setLineheight(12f);//设置行高
            }
            printReporUtil.setBshowzero(queryparamvo.getBshowzero());
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));//设置表头字体
            //初始化表体列编码和列名称
            printReporUtil.printReport(bodyvos,"业 务 活 动 表", columncellattrlist,18,pmap.get("type"),pmap,tmap);
            //日志记录接口
            writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT,
                    "业务活动表打印:"+printParamVO.getTitleperiod(), ISysConstants.SYS_2);
        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
    }

}
