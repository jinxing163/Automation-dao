package com.zhihuishu.teacher.commons.jtest.core.group;


import com.zhihuishu.teacher.commons.jtest.core.group.invoker.GroupInvoker;

/**
 * @author Jiangli
 * @date 2017/12/28 9:31
 */
public interface GroupInf {
    GroupInvoker[] splitGroup(String[] params);
}
