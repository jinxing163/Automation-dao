package com.zhihuishu.teacher.commons.client.tools.mybatis

import com.jiangli.doc.mybatis.generateBaseImplMethod
import com.jiangli.doc.mybatis.getCamelSplitName

/**
 *
 *
 * @author Jiangli
 * @date 2018/4/8 14:55
 */
fun main(args: Array<String>) {
    val s = StringBuilder()
    val method = generateBaseImplMethod("A", "B")
    println(method)
//    s.append(method)
    s.append("${method}\r\n")
    println(s)


    println(getCamelSplitName("ArmyStudentStudyRecord"))

}