package com.quancheng.spider.impl;

import com.quancheng.spider.dao.MerchantMapper;
import com.quancheng.spider.dataobject.Merchant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class MerchantDaoTest {
    //    @Autowired
    @MockBean
    private MerchantMapper merchantMapper;

    @Test
    public void save() {
        Merchant merchant = new Merchant();
        merchant.setMerchantId("SH0004");
        merchant.setName("JSON");
        merchant.setAddress("addddd");
        merchant.setRecommendedDishes("weore");
        merchant.setFeaturedDishes("htp//////so.jpg");
        int result = merchantMapper.insert(merchant);
        Assert.assertTrue(result > 0);
    }
}
