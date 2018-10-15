package com.jiangli.doc.mybatis

import com.zhihuishu.teacher.commons.client.tools.mybatis.MethodImplUtil
import com.zhihuishu.teacher.commons.client.utils.*
import org.apache.commons.io.IOUtils
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.sql.DriverManager


fun main(args: Array<String>) {
    ///////////////START-OF-CONFIG//////////////////
//    val TBL_NAME = "TBL_OPEN_ADVERTISEMENT"
//    val PKG="com.zhihuishu.aries.operation"
//    val DATABASE = "db_aries_operation"
//    val TBL_NAME = "TBL_DAILY_PUSH"
//    val JAVA_NAME = "DailyPush"
//    val DESC = "每日推送"

//    val PKG="com.zhihuishu.aries.course"
//    val DATABASE = "db_aries_course"
//    val TBL_NAME = "TBL_RELEVANCE_COURSE"
//    val JAVA_NAME = "CourseRelevance"
//    val DESC = "课程权限"

//    val PKG="com.zhihuishu.aries.run"
//    val DATABASE = "db_aries_run"
//    val TBL_NAME = "TBL_RECRUIT_ASSISTANT"
//    val JAVA_NAME = "RecruitAssistant"
//    val DESC = "招生辅导员关系"

//    val PKG="com.zhihuishu.aries.interaction"
//    val DATABASE = "db_aries_interaction"
//    val TBL_NAME = "TBL_CHAPTER_SORT"
//    val JAVA_NAME = "ChapterSort"
//    val DESC = "章节序号冗余表"


//    val PKG="com.zhihuishu.army.db"
//    val DATABASE = "DB_ARMY_STUDY"
//    val TBL_NAME = "ARMY_COURSE"
//    val JAVA_NAME = "ArmyCourse"
//    val DESC = "自定义课程表"

//    val PKG="com.zhihuishu.army.db"
//    val DATABASE = "DB_ARMY_STUDY"
//    val TBL_NAME = "ARMY_USER_COURSE"
//    val JAVA_NAME = "ArmyUserCourse"
//    val DESC = "自定义选课表"

//    val PKG="com.zhihuishu.army.db"
//    val DATABASE = "DB_ARMY_STUDY"
//    val TBL_NAME = "ARMY_STUDENT_STUDY_RECORD"
//    val JAVA_NAME = "ArmyStudentStudyRecord"
//    val DESC = "自定义课程学习进度表"


//    val PKG="com.zhihuishu.aries.teachmanage"
//    val DATABASE = "db_aries_teachmanage"
//    val ONE_TO_MANYID = "COURSE_ID" // 一对多id，可为空
//    val TBL_NAME = "TM_USER_ROLE"
//    val JAVA_NAME = "TmUserRole"
//    val DESC = "管理员用户表"

    //文件名称
    val PKG="com.zhihuishu.aries.liveRoom.datasource"
    //数据库名称
    val DATABASE = "db_aries_live"
    //表名称
    val TBL_NAME = "LIVE_COMPANY"
    //Java实体类名称
    val JAVA_NAME = "LiveCompany"
    //表说明
    val DESC = "直播任务所属企业"

    val OUTPUTPATH = "E:\\0.test\\1.mybatis_tools"

    //文件名称
//    val PKG="com.zhihuishu.aries.liveRoom.datasource"
//    //数据库名称
//    val DATABASE = "db_aries_live"
//    //表名称
//    val TBL_NAME = "LIVE_REPLAY"
//    //Java实体类名称
//    val JAVA_NAME = "LiveReplay"
//    //表说明
//    val DESC = "直播任务收流地址对象"
//    val OUTPUTPATH = "C:\\Users\\Jiangli\\Desktop\\outpath"


    //aries
    val DB_URL = "jdbc:mysql://192.168.222.8:3306?user=root&password=ablejava"

    //zhihuishu
//    val DB_URL = "jdbc:mysql://192.168.9.223:3306?user=root&password=ablejava"

    ///////////////END-OF-CONFIG//////////////////
    //下面别看了  具体逻辑
    //fixed
    Class.forName("com.mysql.jdbc.Driver")
    val connection = DriverManager.getConnection(DB_URL)

    val metaData = connection.metaData

    //列信息
    val list = mutableListOf<JavaField>()
    val colRs = metaData.getColumns(DATABASE, "%", TBL_NAME, "%")
    while (colRs.next()) {
        val columnName = colRs.getString("COLUMN_NAME")
        val columnType = colRs.getString("TYPE_NAME")
        val datasize = colRs.getInt("COLUMN_SIZE")
        val digits = colRs.getInt("DECIMAL_DIGITS")
        val nullable = colRs.getInt("NULLABLE") //1:可
        val message = colRs.getString("REMARKS")
        val columnDef  = colRs.getString("COLUMN_DEF") // 该列的默认值 当值在单引号内时应被解释为一个字符串
        println("$columnName $columnType $datasize $digits $nullable $columnDef")
//        println(message)
//        println(colRs.getString("IS_AUTOINCREMENT"))

        val javaField = JavaField(columnName, columnType)
        javaField.remark = message ?: ""
        javaField.nullable = nullable == 1
        javaField.defaultValue = columnDef

        calcField(javaField)
        list.add(javaField)
    }

    //表主键
    val pkRs = metaData.getPrimaryKeys(DATABASE, null, TBL_NAME)
    pkRs.next()
    val pkColName = pkRs.getString("COLUMN_NAME")
    list.filter { it.columnName==pkColName }.forEach { it.isPk=true }
//    println(pkColName)
//    println(list)

    //remove
    FileUtil.deleteUnderDir(OUTPUTPATH)

    //pojo
    val modelName = JAVA_NAME
    val modelPkg = "${PKG}.model"
    val modelCls = "$modelPkg.$modelName"
    generateFile(generateCls(modelPkg,"$DESC model", modelName,list,IMPORT_COMMON_DTO),OUTPUTPATH,"model","$modelName.java")
    val dtoName = "${JAVA_NAME}Dto"
    val dtoPkg = "${PKG}.dto"
    val dtoCls = "$dtoPkg.$dtoName"
    generateFile(generateCls(dtoPkg,"$DESC dto", dtoName,list,IMPORT_COMMON_DTO),OUTPUTPATH,"dto","$dtoName.java")
    val openDtoName = "${JAVA_NAME}OpenDto"
    val openDtoPkg = "${PKG}.openapi.dto"
    val openDtoCls = "$openDtoPkg.$openDtoName"
    generateFile(generateCls(openDtoPkg,"$DESC open dto", openDtoName,list, IMPORT_COMMON_DTO,FIELD_SERIAL, IMPL_SERIAL),OUTPUTPATH,"openapi","dto","$openDtoName.java")

    //interface
    //mapper
    val mapperClsName = "${JAVA_NAME}Mapper"
    val mapperPkg = "${PKG}.mapper"
    val mapperCls = "$mapperPkg.$mapperClsName"
    generateFile(
            generateInterface(
                    mapperPkg,
                    DESC,
                    mapperClsName,
                    false,
                    modelName,
                    pend(IMPORT_MAPPER,modelCls), ANNO_MAPPER
            ),
            OUTPUTPATH,"mapper","$mapperClsName.java"
    )


    //service
    val serviceClsName = "${JAVA_NAME}Service"
    val servicePkg = "${PKG}.service"
    val serviceCls = "$servicePkg.$serviceClsName"
    generateFile(
            generateInterface(
                    servicePkg,
                    DESC,
                    serviceClsName,
                    true,
                    dtoName,
                    pend(IMPORT_SERVICE,dtoCls)),
            OUTPUTPATH,"service","$serviceClsName.java"
    )


    //openapi
    val openServiceClsName = "${JAVA_NAME}OpenService"
    val openapiPkg = "${PKG}.openapi"
    val openapiCls = "$openapiPkg.$openServiceClsName"
    generateFile(
            generateInterface(
                    openapiPkg,
                    DESC,
                    openServiceClsName,
                    true,
                    openDtoName,
                    pend(IMPORT_OPENAPI,openDtoCls)
            ),
            OUTPUTPATH,"openapi","$openServiceClsName.java"
    )

    //impl
    //dao
    generateFile(
            generateMapperXml(TBL_NAME,PKG,JAVA_NAME,list),
            OUTPUTPATH,
            "${getCamelSplitName(JAVA_NAME)}.xml"
    )

    //service impl
    val serviceImplPkg = "${PKG}.service.impl"
    val serviceSuperCls = "BaseServiceImpl<$modelName,$dtoName>"
    val serviceImplMap = mapOf(
            "space" to SPACE
            ,"variableName" to nameToCamel(dtoName)
            ,"description" to DESC
            ,"scope" to "public"
            ,"objType" to dtoName
    )
    generateFile(
            generateCls(
                serviceImplPkg,
                "$DESC Service实现",
                "${serviceClsName}Impl",
                null,
                pend(IMPORT_SERVICE_IMPL,serviceCls,modelCls,dtoCls,mapperCls), pend(mutableListOf(),autowiredField(mapperClsName)),
                arrayListOf(serviceClsName),
                serviceSuperCls,
                arrayListOf("@Service"),
                pend(mutableListOf(),generateBaseImplMethod(modelName,dtoName),MethodImplUtil.resolveImpl(serviceImplMap,modelName,nameToCamel(mapperClsName)))
            ),
            OUTPUTPATH,"service","impl","${serviceClsName}Impl.java"
    )

    //openapi impl
    val openserviceImplPkg = "${PKG}.openapi.impl"
    val openserviceSuperCls = "BaseServiceImpl<$dtoName,$openDtoName>"
    val openserviceImplMap = mapOf(
            "space" to SPACE
            ,"variableName" to nameToCamel(openDtoName)
            ,"description" to DESC
            ,"scope" to "public"
            ,"objType" to openDtoName
    )
    generateFile(
            generateCls(
                    openserviceImplPkg,
                    "$DESC OpenService实现",
                    "${openServiceClsName}Impl",
                    null,
                    pend(IMPORT_OPENAPI_IMPL,openapiCls,dtoCls,openDtoCls,serviceCls),
                    pend(mutableListOf(),autowiredField(serviceClsName)), arrayListOf(openServiceClsName),
                    openserviceSuperCls,
                    arrayListOf(
                            """@org.springframework.stereotype.Component("${openServiceClsName}Impl")""",
                            """@com.alibaba.dubbo.config.annotation.Service(interfaceClass = $openServiceClsName.class,version = "1.0.0")"""
                    ),
                    pend(mutableListOf(),generateBaseImplMethod(dtoName,openDtoName),MethodImplUtil.resolveImpl(openserviceImplMap,dtoName,nameToCamel(serviceClsName)))
            ),
            OUTPUTPATH,
            "openapi","impl","${openServiceClsName}Impl.java"
    )

    //BaseServiceImpl.java
    val classPathFile = PathUtil.getSRCFileRelative(BaseServiceImpl::class.java, "BaseServiceImpl.java")
    generateFile(
            IOUtils.toString(FileInputStream(classPathFile)),
            OUTPUTPATH,
            "BaseServiceImpl.java"
    )



    //test
    //mapper
    val mapperTestMap = mapOf(
            "space" to SPACE
            ,"description" to DESC
    )
    generateFile(
            generateCls(
                    mapperPkg,
                    "$DESC mapper 测试用例",
                    "${mapperClsName}Test",
                    null,
                    pendNew(IMPORT_TEST_COMMON,modelCls,mapperCls), pend(mutableListOf(),autowiredField(mapperClsName)),
                    null,
                    "BaseTest",
                    null,
                    pend(mutableListOf(),MethodImplUtil.resolveTest(mapperTestMap,modelName,nameToCamel(mapperClsName),list))
            ),
            OUTPUTPATH,"test","mapper","${mapperClsName}Test.java"
    )
    //service
    val serviceTestMap = mapOf(
            "space" to SPACE
            ,"description" to DESC
    )
    generateFile(
            generateCls(
                    servicePkg,
                    "$DESC service 测试用例",
                    "${serviceClsName}Test",
                    null,
                    pendNew(IMPORT_TEST_COMMON,dtoCls,serviceCls), pend(mutableListOf(),autowiredField(serviceClsName)),
                    null,
                    "BaseTest",
                    null,
                    pend(mutableListOf(),MethodImplUtil.resolveTest(serviceTestMap,dtoName,nameToCamel(serviceClsName),list))
            ),
            OUTPUTPATH,"test","service","${serviceClsName}Test.java"
    )
    //openapi
    val openapiTestMap = mapOf(
            "space" to SPACE
            ,"description" to DESC
    )
    generateFile(
            generateCls(
                    openapiPkg,
                    "$DESC open service 测试用例",
                    "${openServiceClsName}Test",
                    null,
                    pendNew(IMPORT_TEST_COMMON,openDtoCls,openapiCls), pend(mutableListOf(),autowiredField(openServiceClsName)),
                    null,
                    "BaseTest",
                    null,
                    pend(mutableListOf(),MethodImplUtil.resolveTest(openapiTestMap,openDtoName,nameToCamel(openServiceClsName),list))
            ),
            OUTPUTPATH,"test","openapi","${openServiceClsName}Test.java"
    )

}

fun generateBaseImplMethod(serviceClsName: String, openServiceName: String): String {
    val methodList =  """
    @Override
    public Class<$serviceClsName> getDtoClass() {
        return $serviceClsName.class;
    }

    @Override
    public Class<$openServiceName> getOpenDtoClass() {
        return $openServiceName.class;
    }
"""
    return methodList
}

/**
 *
 *
 * out/dao/mapper,xml,model,test
 * out/service/service,dto,test
 * out/openapi/openservice,opendto,test
 *
 *
 * @author Jiangli
 * @date 2018/3/1 9:32
 */
data class JavaField(val columnName: String,val columType: String) {
     var initVal: Any?=null
    var isPk: Boolean=false
     lateinit var remark: String
     lateinit var fieldName: String
     lateinit var fieldCls: String
    var nullable: Boolean=false
      var defaultValue: String?=null
    var fieldClsImport: String?=null //import

    override fun toString(): String {
        return "JavaField(columnName='$columnName', columType='$columType', initVal=$initVal, isPk=$isPk, remark='$remark', fieldName='$fieldName', fieldCls='$fieldCls', nullable=$nullable, defaultValue='$defaultValue', fieldClsImport=$fieldClsImport)"
    }

}

fun calcField( f:JavaField) {
    val columType = f.columType

    f.fieldCls = when (columType.trim()) {
        "INT" -> "Long"
        "INT UNSIGNED" -> "Long"
        "TINYINT" -> "Integer"
        "BIGINT" -> "Long"
        "BIT" -> "Integer"
        "SMALLINT" -> "Integer"
        "VARCHAR" -> "String"
        "TIMESTAMP" -> "Date"
        "DATETIME" -> "Date"
        else -> throw Exception("未识别的type $columType")
    }
    f.fieldClsImport = when (columType) {
        "TIMESTAMP" -> "java.util.Date"
        else -> null
    }

    f.fieldName = colNameToCamel(f.columnName)

}

fun colNameToCamel(f:String):String {
    var lowerCase = f.toLowerCase()

    while (lowerCase.contains("_")) {
        val first = lowerCase.indexOfFirst { it == '_' }
        lowerCase=lowerCase.substring(0,first) + lowerCase.get(first+1).toUpperCase() + lowerCase.substring(first+2)
    }
    return lowerCase
}
fun nameToCamel(f:String):String {
    return f[0].toLowerCase()+ f.substring(1)
}
fun nameToMethod(f:String):String {
    return f[0].toUpperCase()+ f.substring(1)
}
fun generateFieldSet(indent: String, varName: String, field: JavaField): String {
    val value=
            if(field.isPk) "1L"
                else
                    when(field.fieldCls){
                        "Long"->"${Rnd.getRandomNum(100,1000000)}L"
                        "Integer"->"${Rnd.getRandomNum(1,4)}"
                        "String"->""""abcd""""
                        "Date"->"""new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("${DateUtil.getCurrentDate()} 00:00:00");"""
                        else -> ""
                    }

    return """${indent}${varName}.${generatePrefixMethod("set",field.fieldName)}($value);"""
}
fun generatePrefixMethod(prefix: String, fieldName: String): String {
    return """${prefix}${nameToMethod(fieldName)}"""
}

fun generateFieldsSetExclude(indent:String,varName:String,fields: MutableList<JavaField>, vararg exList:String): String {
    val sb = StringBuilder()

    val filteredList = fields.filter { !exList.contains(it.fieldName) }
    filteredList.forEachIndexed { index, it ->
        sb.append(generateFieldSet(indent,varName,it))
        if(index!=filteredList.lastIndex)
            sb.append("\r\n")
    }

    return sb.toString()
}

fun generateFile(body:String, vararg path:String) {
    val sb = StringBuilder()
    path.forEach {
        sb.append(it)
        sb.append(System.getProperty("file.separator"))
    }
    sb.deleteCharAt(sb.lastIndex)

    PathUtil.ensureFilePath(sb.toString())

    IOUtils.write(body,FileOutputStream(sb.toString()))
}

fun dbFieldsExists(fields: List<JavaField>, dbFieldName: String): Boolean {
    return fields.any {
        it.columnName == dbFieldName
    }
}
