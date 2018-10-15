package com.zhihuishu.teacher.commons.client.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Jiangli
 * @date 2018/7/9 15:23
 */
class NameUtilTest {

    @Test
    void getCamelSplitName() {
        //System.out.println(NameUtil.getCamelSplitName("ArmyStudentStudyRecord"));
        cmp("ArmyStudentStudyRecord", "army_student_study_record");
        cmp("AAABC", "a_a_a_b_c");
        cmp("C", "c");
        cmp("c", "c");
    }

    private void cmp(String p1, String p2) {
        String camelSplitName = NameUtil.getCamelSplitName(p1);
        Assertions.assertTrue(camelSplitName.equals(p2),"result:"+camelSplitName +" expected:"+p2);
    }
}