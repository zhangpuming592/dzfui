package com.dzf.zxkj.platform.controller.message;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.message.MsgSysVO;
import com.dzf.zxkj.platform.model.message.MsgTypeVO;
import com.dzf.zxkj.platform.service.message.IMessageService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/msg/message")
@Slf4j
public class MessageController {
    @Autowired
    private IMessageService msgServiceImpl;

    @GetMapping("/getAdminMsg")
    public ReturnData getAdminMsg(String sreceive) {
        Json json = new Json();
        List<MsgAdminVO> messages = msgServiceImpl.queryLatestAdminMsg(SystemUtil.getLoginUserId(), sreceive, 5);
        if (messages != null && messages.size() > 0) {
            String curDate = new DZFDate().toString();
            for (MsgAdminVO msg : messages) {
                msg.setVsendtime(msg.getVsenddate());
                msg.setVsenddate(formatDate(curDate, msg.getVsenddate()));
            }
        }
        json.setSuccess(true);
        json.setRows(messages);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getMsgTypes")
    public ReturnData getMsgTypes(@RequestParam String sreceive) {
        Json json = new Json();
        List<MsgTypeVO> types = msgServiceImpl.queryMsgType(SystemUtil.getLoginUserId(), sreceive);
        json.setRows(types);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/updateMsgStatus")
    public ReturnData updateMsgStatus(@RequestBody MsgAdminVO[] msgVos) {
        Json json = new Json();
        msgServiceImpl.updateAdminRead(msgVos);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/query")
    public ReturnData query(@RequestBody MsgAdminVO data) {
        Grid grid = new Grid();
        data.setCuserid(SystemUtil.getLoginUserId());
        MsgAdminVO[] msgs = msgServiceImpl.query(data.getCuserid(), data);
        grid.setTotal((long) msgs.length);
        msgs = pagingData(msgs, data.getPage(), data.getRows());
        grid.setRows(Arrays.asList(msgs));
        grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    @GetMapping("/getSysMsg")
    public ReturnData getSysMsg(@RequestParam String sreceive) {
        Json json = new Json();
        List<MsgSysVO> msgs = msgServiceImpl.queryLatestSysMsg(sreceive, 5);
        if (msgs != null && msgs.size() > 0) {
            String curDate = new DZFDate().toString();
            for (MsgSysVO msg : msgs) {
                msg.setVsenddate(formatDate(curDate, msg.getVsenddate()));
            }
        }
        json.setRows(msgs);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/checkNewMessage")
    public ReturnData checkNewMessage() {
        Json json = new Json();
        int num = msgServiceImpl.queryUnreadMsgNum(SystemUtil.getLoginUserId());
        json.setRows(num);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/delete")
    public ReturnData delete(@RequestBody MsgAdminVO[] vos) {
        Json json = new Json();
        msgServiceImpl.deleteAdminMsg(vos);
        json.setSuccess(true);
        json.setMsg("删除成功");
        return ReturnData.ok().data(json);
    }

    private String formatDate(String curDate, String date) {
        if (date != null) {
            String curYear = curDate.substring(0, 4);
            if (date.length() > 10 && curDate.equals(date.substring(0, 10))) {
                date = date.substring(11);
            } else if (curYear.equals(date.substring(0, 4))) {
                date = date.substring(5, 10);
            } else {
                date = date.substring(0, 10);
            }
        }
        return date;
    }

    private MsgAdminVO[] pagingData(MsgAdminVO[] msgs, int page, int rows) {
        int beginIndex = rows * (page - 1);
        int endIndex = rows * page;
        if (endIndex >= msgs.length) {
            endIndex = msgs.length;
        }
        msgs = Arrays.copyOfRange(msgs, beginIndex, endIndex);
        return msgs;
    }

    @GetMapping("/getSysMsgByDate")
    public ReturnData getSysMsgByDate(@RequestParam String sreceive) {
        Json json = new Json();
        List<MsgSysVO> msgs = msgServiceImpl.querySysMsgByDate(sreceive, new DZFDate());
        if (msgs != null && msgs.size() > 0) {
            String curDate = new DZFDate().toString();
            for (MsgSysVO msg : msgs) {
                msg.setVsenddate(formatDate(curDate, msg.getVsenddate()));
            }
        }
        json.setRows(msgs);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }
}