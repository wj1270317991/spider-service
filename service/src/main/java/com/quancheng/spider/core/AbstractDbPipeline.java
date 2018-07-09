package com.quancheng.spider.core;

import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-06
 **/
public abstract class AbstractDbPipeline<T> implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        List<T> list = resultItems.get(PageEnum.RESULT_ITEMS_KEY.name());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(this::save);
        }
    }

    public abstract int save(T t);
}
