package com.zhihuishu.teacher.commons.client.tools.mybatis

import org.junit.jupiter.api.Test

/**
 * @author Jiangli
 * @date 2018/7/9 16:57
 */
internal class MethodImplUtilTest {

    @Test
    fun resolve() {
        println(MethodImplUtil.resolve(mapOf(
//                "SPACE" to "xx"
        )))
    }

    @Test
    fun resolveImpl() {
        println(MethodImplUtil.resolveImpl(mapOf(
//                "SPACE" to "xx"
        ),"XXDto","XXService"))
    }
}