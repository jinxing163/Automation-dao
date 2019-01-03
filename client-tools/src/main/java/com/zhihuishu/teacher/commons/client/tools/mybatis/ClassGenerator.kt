package com.jiangli.doc.mybatis

import com.zhihuishu.teacher.commons.client.tools.mybatis.MethodImplUtil
import com.zhihuishu.teacher.commons.client.utils.NameUtil
import java.lang.StringBuilder

val IMPORT_COMMON_DTO = arrayListOf("java.util.Date")

val FIELD_SERIAL = arrayListOf("private static final long serialVersionUID = 1L", "")
val IMPL_SERIAL = arrayListOf("java.io.Serializable")

val ANNO_MAPPER = arrayListOf("@Repository")

val IMPORT_MAPPER = arrayListOf("java.util.List","org.apache.ibatis.annotations.Param","org.springframework.stereotype.Repository")
val IMPORT_SERVICE = arrayListOf("java.util.List")
val IMPORT_OPENAPI = arrayListOf("java.util.List")

val IMPORT_SERVICE_IMPL = arrayListOf("java.util.List","java.util.Date","org.springframework.beans.factory.annotation.Autowired","org.springframework.stereotype.Service")
val IMPORT_OPENAPI_IMPL = arrayListOf("java.util.List","java.util.Date","org.springframework.beans.factory.annotation.Autowired")

val IMPORT_TEST_COMMON = arrayListOf("org.junit.Test","org.springframework.beans.factory.annotation.Autowired","org.junit.runner.RunWith","org.springframework.boot.test.context.SpringBootTest","com.zhihuishu.aries.BaseTest","java.util.Arrays","java.util.List")

val SPACE = "    "

fun getCamelSplitName(name: String):String {
    return  NameUtil.getCamelSplitName(name)
}

fun pend(list: MutableList<String>, vararg str: String):List<String> {
    str.forEach {
        list.add(it)
    }
    return list
}
fun pendNew(list: MutableList<String>, vararg str: String):List<String> {
    var allList = mutableListOf<String>()
    allList.addAll(list)

    return pend(allList,*str)
}
fun autowiredField(clsName: String):String {
    val varName = nameToCamel(clsName)

    return "@Autowired\r\n${SPACE}private $clsName $varName"
}

fun generateCls(pkg:String,desc:String,clsName:String,fields:List<JavaField>?,extraImports:List<String>?= arrayListOf(),extraField:List<String>?= arrayListOf(),implClses:List<String>?= arrayListOf(),superClsName:String?=null,extraAnnos:List<String>?= arrayListOf(),extraMethods:List<String>?= arrayListOf()):String {
    val fieldList =  StringBuilder()
    val importList = StringBuilder()
    val implClsList = StringBuilder()
    val extendsCls = StringBuilder()
    val methodsList = StringBuilder()
    val annoList =  StringBuilder()
    val totalImport = mutableSetOf<String>()

    //import
    extraImports?.forEach { totalImport.add(it) }
    totalImport.forEach {
        importList.append("import $it;\r\n")
    }

    //注解
    extraAnnos?.forEach {
        annoList.append("\r\n${it}")
    }

    //超类
    superClsName?.let{
        extendsCls.append("""extends $it""")
    }

    //实现类
    implClses?.let {
        if (implClses.isNotEmpty()) {
            implClsList.append("implements ")

            it.forEachIndexed { index, s ->
                implClsList.append(s)
                if (index != it.lastIndex) {
                    implClsList.append(",")
                }
            }
        }
    }

    //custom fields first
    extraField?.forEach {
        fieldList.append("${SPACE}${it}${if (it.isNotEmpty()) ";" else ""}\r\n")
    }

    //类属性注释 (字段注释)
    fields?.forEach {
        fieldList.append("${SPACE}/**${it.remark}*/\r\n")
        fieldList.append("${SPACE}private ${it.fieldCls} ${it.fieldName};\r\n")

        it.fieldClsImport?.let {
            totalImport.add(it)
        }
    }


    //所有方法
    val totalMethods = mutableListOf<String>()

    //getter & setter
    fields?.let {
        fields.forEach {
            val setter = """
    public void set${NameUtil.getCapitalName(it.fieldName)}(${it.fieldCls} ${it.fieldName}) {
        this.${it.fieldName} = ${it.fieldName};
    }

    public ${it.fieldCls} get${NameUtil.getCapitalName(it.fieldName)}() {
        return this.${it.fieldName};
    }
"""
            totalMethods.add(setter)
        }
    }

    //toString
    fields?.let {
        val toStrPrefix="""
    @Override
    public String toString() {
        return "$clsName{" +
"""
        val toStrSuffix="""
                '}';
    }
"""
        var sb = StringBuilder()
        fields.forEachIndexed{
            idx,it->
                sb.append("""                "${if(idx!=0) "," else ""}${it.fieldName}=" + ${it.fieldName} + """)
                sb.append("\r\n")
        }
        totalMethods.add("$toStrPrefix$sb$toStrSuffix")
    }

    if (extraMethods != null) {
        totalMethods.addAll(extraMethods)
    }

    totalMethods?.let{
        it.forEach {
            methodsList.append("${it}\r\n")
        }
    }

    return """
package $pkg;
$importList

/**
 * $desc
 */$annoList
public class $clsName $extendsCls $implClsList{
$fieldList

$methodsList
}
"""
}

fun appendComment(sb:StringBuilder,txt:String) {
    sb.append("${SPACE}/**\r\n")
    sb.append("${SPACE} * $txt\r\n")
    sb.append("${SPACE} */\r\n")
}
fun appendEnter(sb:StringBuilder) {
    sb.append("${SPACE}\r\n")
}

fun generateInterface(pkg:String,desc:String,clsName:String,useWrap:Boolean?=false,objName:String,extraImports:List<String>?= arrayListOf(),extraAnnos:List<String>?= arrayListOf(),implClses:List<String>?= arrayListOf()):String {
    val annoList =  StringBuilder()
    val importList = StringBuilder()
    val methodsList =  StringBuilder()
    val implClsList = StringBuilder()
    val imported = hashSetOf<String>()

    extraAnnos?.forEach {
        annoList.append("\r\n${it}")
    }

    extraImports?.forEach {
        if (!imported.contains(it)) {
            importList.append("import $it;\r\n")
            imported.add(it)
        }
    }



//    extraMethods?.forEach {
        val variableName = nameToCamel(objName)
        val mapOf = mutableMapOf(
                "space" to SPACE
                ,"variableName" to variableName
                ,"description" to desc
                ,"scope" to ""
                ,"objType" to objName
        )

    //dao
    if (!useWrap!!) {
        mapOf.put("paramAnno","@Param(\"${'$'}{_this_variableName}\") ")
    }

    val method = MethodImplUtil.resolveInterface(mapOf)


    implClses?.let {
        if (implClsList.isNotEmpty()) {
            implClsList.append("extends ")

            it.forEachIndexed { index, s ->
                implClsList.append(s)
                if (index != it.lastIndex) {
                    implClsList.append(",")
                }
            }
        }
    }

    return """
package $pkg;
$importList

/**
 * $desc
 */$annoList
public interface $clsName $implClsList{
$method
}
"""
}

fun generateMapperXml(tableName:String,pkg:String,javaName:String,fields:List<JavaField>):String{
    val includes = fields.joinToString(",\r\n") { javaField -> SPACE +SPACE + javaField.columnName }
    val variableName = nameToCamel(javaName)
    val containsDeletePerson: Boolean = dbFieldsExists(fields,"DELETE_PERSON")
    val deleteSetStmt = if (containsDeletePerson) """<if test="deletePerson != null">DELETE_PERSON = #{deletePerson}, </if>""" else ""

    val idField = fields.first { it.isPk }.fieldName
    val idColumn = fields.first { it.isPk }.columnName

    fun mustInput(f:JavaField):Boolean{
        return !f.nullable && f.defaultValue == null
    }

    val updateList = StringBuilder()
    fields.filter { !it.isPk } .forEach {
//        if(mustInput(it)){
            updateList.append("\r\n$SPACE$SPACE$SPACE<if test=\"${it.fieldName} != null\">${it.columnName}= #{${it.fieldName}}, </if>")
//        } else {
//            updateList.append("\r\n$SPACE$SPACE$SPACE<if test=\"${it.fieldName} != null\">${it.columnName}= #{${it.fieldName}}, </if>")
//        }
    }

    val saveColList = StringBuilder()
    val saveValList = StringBuilder()
    val batchSaveValList = StringBuilder()
    val max_idx =   fields.filter { !it.isPk }.lastIndex

    fields.sortedBy {mustInput(it)}.filter { !it.isPk } .forEachIndexed { idx, it ->
        var suffix = if(idx == max_idx) "" else ","

//        println("$idx / ${fields.lastIndex}")

        if(mustInput(it)){
            saveColList.append("\r\n$SPACE$SPACE$SPACE${it.columnName}${suffix} ")
            saveValList.append("\r\n$SPACE$SPACE$SPACE#{${it.fieldName}}${suffix} ")
            batchSaveValList.append("\r\n$SPACE$SPACE$SPACE#{item.${it.fieldName}}${suffix} ")
        } else {
            saveColList.append("\r\n$SPACE$SPACE$SPACE<if test=\"${it.fieldName} != null\">${it.columnName}${suffix} </if>")
            saveValList.append("\r\n$SPACE$SPACE$SPACE<if test=\"${it.fieldName} != null\">#{${it.fieldName}}${suffix} </if>")
            batchSaveValList.append("\r\n$SPACE$SPACE$SPACE<if test=\"item.${it.fieldName} != null\">#{item.${it.fieldName}}${suffix} </if>")
        }
    }

//    println(fields.sortedBy {mustInput(it)})
//    println(fields)

    return """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${pkg}.mapper.${javaName}Mapper">

    <!-- 表字段 -->
    <sql id="fields">
$includes
    </sql>



    <!-- 根据id查列表 -->
    <select id="listOfIds" resultType="${pkg}.model.${javaName}">
        SELECT <include refid="fields"/>  FROM $tableName WHERE IS_DELETED=0
        AND $idColumn in
        <foreach collection="${variableName}Ids" index="index" item="item"
                 open="(" separator="," close=")">
            #{item}
        </foreach>
        ORDER BY CREATE_TIME DESC
    </select>

    <!-- 查单个 -->
    <select id="get"  resultType="${pkg}.model.${javaName}">
        SELECT <include refid="fields"/>  FROM $tableName WHERE IS_DELETED=0  AND $idColumn = #{${variableName}Id}
    </select>

    <!-- 删除 -->
    <update id="delete" parameterType="${pkg}.model.${javaName}">
        UPDATE $tableName
        <set>
            ${deleteSetStmt}
            IS_DELETED = 1
        </set>
        WHERE $idColumn=#{$idField} AND IS_DELETED=0
    </update>

    <!-- 修改 -->
    <update id="update" parameterType="${pkg}.model.${javaName}">
        UPDATE $tableName
        <set>$updateList
            UPDATE_TIME = CURRENT_TIMESTAMP
        </set>
        WHERE $idColumn=#{$idField} AND IS_DELETED=0
    </update>

    <!-- 保存 -->
    <insert id="save" parameterType="${pkg}.model.${javaName}" keyProperty="$idField" useGeneratedKeys="true">
        INSERT INTO $tableName($saveColList
        ) values ($saveValList
        )
    </insert>

    <!-- 批量保存 -->
    <insert id="batchSave" parameterType="${pkg}.model.${javaName}" keyProperty="id" useGeneratedKeys="true">
        INSERT INTO $tableName($saveColList
        ) values
        <foreach collection="activityLotteryDtoIds" index="index" item="item"
                 separator=",">
            ($batchSaveValList)
        </foreach>

    </insert>

     <!-- 查全部 -->
    <select id="listAll" resultType="${pkg}.model.${javaName}">
        SELECT <include refid="fields"/>  FROM $tableName WHERE IS_DELETED=0
        ORDER BY CREATE_TIME DESC
    </select>
</mapper>
"""

//    <!-- 查列表 -->
//    <select id="list" resultType="${pkg}.model.${javaName}">
//    SELECT <include refid="fields"/>  FROM $tableName WHERE IS_DELETED=0  AND COURSE_ID = #{courseId}
//    ORDER BY CREATE_TIME DESC
//    </select>


}
