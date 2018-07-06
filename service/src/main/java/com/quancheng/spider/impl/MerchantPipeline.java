package com.quancheng.spider.impl;

import com.quancheng.spider.core.AbstractDbPipeline;
import com.quancheng.spider.dao.MerchantMapper;
import com.quancheng.spider.dataobject.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public int save(Merchant merchant) {
        return merchantMapper.insert(merchant);
    }
}
