package com.zhihuishu.teacher.commons.jtest.core.test;

import com.zhihuishu.teacher.commons.jtest.core.RepeatFixedDuration;
import com.zhihuishu.teacher.commons.jtest.core.StatisticsJunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Administrator
 *
 *         CreatedTime  2016/9/1 0001 16:38
 */
@RunWith(StatisticsJunitRunner.class)
public class JunitTest {
    private Class<Object> aClass = Object.class;

    @Test
    public void logTest() {
        new Object();
    }

    @RepeatFixedDuration
    @Test
    public void xxNew() {
        new Object();
    }

    //比new略慢
    @RepeatFixedDuration
    @Test
    public void xxNew2() {
        try {
            aClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @RepeatFixedDuration
    @Test
    public void xxts() {
       System.currentTimeMillis();
    }

    @RepeatFixedDuration
    @Test
    public void ndgts() {
       new Date().getTime();
    }


    //取ts慢很多
    @RepeatFixedDuration
    @Test
    public void cggts() {
       Calendar.getInstance().getTimeInMillis();
    }


    @RepeatFixedDuration
    @Test
    public void formatTimeBySdf() {
        String yyyymmdd = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        System.out.println(yyyymmdd);
    }

    //比SimpleDateFormat快
    @RepeatFixedDuration
    @Test
    public void formatTimeByCalendar() {
        Calendar instance = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(instance.get(Calendar.YEAR));
        sb.append(instance.get(Calendar.MONTH)+1);
        sb.append(instance.get(Calendar.DATE));
        String s = sb.toString();
//        System.out.println(s);
    }



}
