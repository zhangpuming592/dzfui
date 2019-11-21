package com.dzf.zxkj.report.excel.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.MuiltSheetAndTitleExceport;
import com.dzf.zxkj.excel.param.TitleColumnExcelport;
import com.dzf.zxkj.excel.param.UnitExceport;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 发生额余额表导出配置
 *
 * @author zhangj
 *
 */
public class KmzzExcelField extends MuiltSheetAndTitleExceport<KmZzVO> implements UnitExceport {
    private KmZzVO[] vos = null;

    private List<KmZzVO[]> allsheetvos = null;

    private String nodename = null;

    private String pk_currency;//币种

    private String currencyname;//币种

    private String[] periods = null;

    private String[] allsheetname = null;

    private String qj = null;

    private String now = DZFDate.getDate(new Date()).toString();

    private String creator = null;

    private String corpname = null;

    public KmzzExcelField(String nodename, String pk_currency, String currencyname, String qj, String corpname) {
        this.nodename = nodename;
        this.pk_currency = pk_currency;
        this.currencyname = currencyname;
        this.qj = qj;
        this.corpname = corpname;
    }

    public KmzzExcelField(String nodename, String pk_currency, String currencyname, String[] periods, String[] allsheetname, String qj, String corpname) {
        this.nodename = nodename;
        this.pk_currency = pk_currency;
        this.currencyname = currencyname;
        this.periods = periods;
        this.allsheetname = allsheetname;
        this.qj = qj;
        this.corpname = corpname;
    }

    private Fieldelement[] getFileWbs() {
        List<Fieldelement> list = new ArrayList<Fieldelement>();
        list.add( new Fieldelement("kmbm", "科目编码", false, 0, true));
        list.add(new Fieldelement("km", "科目名称", false, 0, true,80,false));
        list.add(new Fieldelement("kmbm", "科目编码", false, 0, true));
        list.add(new Fieldelement("zy", "摘要", false, 0, true));
        Fieldelement qcelement = new Fieldelement("", "借方金额", true, 2, true, 1, 2);
        qcelement.setChilds(new Fieldelement[] {
                new Fieldelement("ybjf", "原币", true, 2, true),
                new Fieldelement("jf", "本位币", true, 2, true)});
        list.add(qcelement);
        Fieldelement bqelement = new Fieldelement("", "贷方金额", true, 2, true, 1, 2);
        bqelement.setChilds(new Fieldelement[] {
                new Fieldelement("ybdf", "原币", true, 2, true),
                new Fieldelement("df", "本位币", true, 2, true)});
        list.add(bqelement);
        Fieldelement bnelement = new Fieldelement("", "余额", true, 2, true, 1, 3);
        bnelement.setChilds(new Fieldelement[] {
                new Fieldelement("fx", "方向", false, 0, true),
                new Fieldelement("ybye", "原币", true, 2, true),
                new Fieldelement("ye", "本位币", true, 2, true) });
        list.add(bnelement);
        return list.toArray(new Fieldelement[0]);
    }

    private Fieldelement[] getFileNoWbs() {
        List<Fieldelement> list = new ArrayList<Fieldelement>();
        list.add( new Fieldelement("kmbm", "科目编码", false, 0, true));
        list.add(new Fieldelement("km", "科目名称", false, 0, true,80,false));
        list.add(new Fieldelement("period", "期间", false, 0, true));
        list.add(new Fieldelement("zy", "摘要", false, 0, true));
        list.add(new Fieldelement("jf", "借方", true, 2, true));
        list.add(new Fieldelement("df", "贷方", true, 2, true));
        list.add(new Fieldelement("fx", "方向", false, 0, true));
        list.add(new Fieldelement("ye", "余额", true, 2, true));
        return list.toArray(new Fieldelement[0]);
    }

    @Override
    public String getExcelport2007Name() {
        return nodename + "("+corpname+")_" + now + ".xlsx";
    }

    @Override
    public String getExcelport2003Name() {
        return nodename + "("+corpname+")_" + now + ".xls";
    }

    @Override
    public String getExceportHeadName() {
        return nodename;
    }

    @Override
    public String getSheetName() {
        return nodename;
    }

    @Override
    public KmZzVO[] getData() {
        return vos;
    }

    @Override
    public Fieldelement[] getFieldInfo() {
        if (!StringUtil.isEmpty(pk_currency) && !pk_currency.equals(IGlobalConstants.RMB_currency_id)) {
            return getFileWbs();
        } else {
            return getFileNoWbs();
        }
    }


    @Override
    public String getQj() {
        return qj;
    }

    @Override
    public String getCreateSheetDate() {
        return now;
    }

    @Override
    public String getCreateor() {
        return creator;
    }

    @Override
    public String getCorpName() {
        return corpname;
    }


    @Override
    public boolean[] isShowTitDetail() {
        return new boolean[] { true, true, true };
    }

    @Override
    public List<KmZzVO[]> getAllSheetData() {
        return allsheetvos;
    }

    @Override
    public String[] getAllSheetName() {
        return allsheetname;
    }

    public List<KmZzVO[]> getAllsheetzcvos() {
        return allsheetvos;
    }

    public void setAllsheetzcvos(List<KmZzVO[]> allsheetzcvos) {
        this.allsheetvos = allsheetzcvos;
    }

    @Override
    public String[] getAllPeriod() {
        return periods;
    }

    @Override
    public List<TitleColumnExcelport> getHeadColumns() {
        List<TitleColumnExcelport> lists = new ArrayList<TitleColumnExcelport>();
        return lists;
    }

    @Override
    public TitleColumnExcelport getTitleColumns() {
        TitleColumnExcelport column1 = new TitleColumnExcelport(1, getSheetName(), HorizontalAlignment.RIGHT);
        return column1;
    }

    @Override
    public String getDw() {
        if (StringUtil.isEmpty(currencyname)) {
            return "元";
        } else {
            return currencyname;
        }
    }


}