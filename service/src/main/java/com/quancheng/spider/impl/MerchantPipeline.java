package com.quancheng.spider.impl;

import com.quancheng.spider.core.AbstractDbPipeline;
import com.quancheng.spider.core.PageEnum;
import com.quancheng.spider.dao.MerchantMapper;
import com.quancheng.spider.dataobject.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;

import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-06
 **/
@Component
public class MerchantPipeline extends AbstractDbPipeline<Merchant> {
    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public List<Merchant> getItems(ResultItems resultItems) {
        return resultItems.get(PageEnum.RESULT_MERCHAANT_KEY.name());
    }

    @Override
    public int save(Merchant merchant) {
        return merchantMapper.insert(merchant);
    }
}
