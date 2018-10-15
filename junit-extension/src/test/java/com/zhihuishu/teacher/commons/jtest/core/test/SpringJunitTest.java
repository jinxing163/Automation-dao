package com.zhihuishu.teacher.commons.jtest.core.test;

import com.zhihuishu.teacher.commons.jtest.core.RepeatFixedDuration;
import com.zhihuishu.teacher.commons.jtest.core.StatisticsSpringJunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Jiangli
 *
 *         CreatedTime  2016/4/26 0026 17:21
 */
@RunWith(StatisticsSpringJunitRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public class SpringJunitTest   {
    @Autowired
    private ApplicationContext applicationContext;


    @RepeatFixedDuration()
    @Test
    public void test_new() {
        new Object();
    }

    @Test
    public void test_spring() {
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            System.out.println(name);
        }
    }


}
