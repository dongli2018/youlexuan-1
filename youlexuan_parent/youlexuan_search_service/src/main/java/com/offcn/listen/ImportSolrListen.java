package com.offcn.listen;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Arrays;
import java.util.List;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.listen</p>
 * <p>ClassName:ImportSolrListen</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/12/2
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Component
public class ImportSolrListen implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage text = (TextMessage) message;
            String listStr = text.getText();
            List<TbItem> list = JSON.parseArray(listStr, TbItem.class);
            itemSearchService.importDate(list);
            System.out.println(">>>>消息导入成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
