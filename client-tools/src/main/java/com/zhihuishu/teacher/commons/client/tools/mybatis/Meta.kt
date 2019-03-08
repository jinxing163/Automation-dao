package com.zhihuishu.teacher.commons.client.tools.mybatis

import com.jiangli.doc.mybatis.*
import org.springframework.util.PropertyPlaceholderHelper
import java.util.*

/**
 *
 *
 * @author Jiangli
 * @date 2018/7/9 16:44
 */
class MMethod{
     var indent: String?=null //前置空格
     var name: String?=null //方法名
     var nameCN: String?=null //方法中文名
     var variableName: String?=null //变量名
     var body: String?=null //方法体

    constructor(){

    }


    constructor(indent: String?, name: String?, nameCN: String?, variableName: String?, body: String?) {
        this.indent = indent
        this.name = name
        this.nameCN = nameCN
        this.variableName = variableName
        this.body = body
    }


}


object MethodImplUtil{
    fun funcList(): List<MMethod> {
        val list = mutableListOf<MMethod>()

        list.add(MMethod("${'$'}{space}","save","新增","${'$'}{variableName}","""
${'$'}{_this_indent}/**
${'$'}{_this_indent}* ${'$'}{description} ${'$'}{_this_nameCN}
${'$'}{_this_indent}*/${'$'}{annotation}
${'$'}{_this_indent}${'$'}{scope} void ${'$'}{_this_name}(${'$'}{objType} ${'$'}{_this_variableName})${'$'}{impl.${'$'}{_this_name}:{}}
"""))




        list.add(MMethod("${'$'}{space}","delete","删除","${'$'}{variableName}","""
${'$'}{_this_indent}/**
${'$'}{_this_indent}* ${'$'}{description} ${'$'}{_this_nameCN}
${'$'}{_this_indent}*/${'$'}{annotation}
${'$'}{_this_indent}${'$'}{scope} void ${'$'}{_this_name}(${'$'}{objType} ${'$'}{_this_variableName})${'$'}{impl.${'$'}{_this_name}:{}}
"""))

        list.add(MMethod("${'$'}{space}","update","更新","${'$'}{variableName}","""
${'$'}{_this_indent}/**
${'$'}{_this_indent}* ${'$'}{description} ${'$'}{_this_nameCN}
${'$'}{_this_indent}*/${'$'}{annotation}
${'$'}{_this_indent}${'$'}{scope} void ${'$'}{_this_name}(${'$'}{objType} ${'$'}{_this_variableName})${'$'}{impl.${'$'}{_this_name}:{}}
"""))

        list.add(MMethod("${'$'}{space}","get","查询单个","${'$'}{variableName}Id","""
${'$'}{_this_indent}/**
${'$'}{_this_indent}* ${'$'}{description} ${'$'}{_this_nameCN}
${'$'}{_this_indent}*/${'$'}{annotation}
${'$'}{_this_indent}${'$'}{scope} ${'$'}{objType} ${'$'}{_this_name}(${'$'}{idType} ${'$'}{_this_variableName})${'$'}{impl.${'$'}{_this_name}:{}}
"""))

//        是否有查课程id
//        list.add(MMethod("${'$'}{space}","list","一对多查询列表","courseId","""
//${'$'}{_this_indent}/**
//${'$'}{_this_indent}* ${'$'}{description} ${'$'}{_this_nameCN}
//${'$'}{_this_indent}*/${'$'}{annotation}
//${'$'}{_this_indent}${'$'}{scope} List<${'$'}{objType}> ${'$'}{_this_name}(${'$'}{idType} ${'$'}{_this_variableName})${'$'}{impl.${'$'}{_this_name}:{}}
//"""))

        list.add(MMethod("${'$'}{space}","listOfIds","根据id查列表","${'$'}{variableName}Ids","""
${'$'}{_this_indent}/**
${'$'}{_this_indent}* ${'$'}{description} ${'$'}{_this_nameCN}
${'$'}{_this_indent}*/${'$'}{annotation}
${'$'}{_this_indent}${'$'}{scope} List<${'$'}{objType}> ${'$'}{_this_name}(${'$'}{paramAnno:}List<${'$'}{idType}> ${'$'}{_this_variableName})${'$'}{impl.${'$'}{_this_name}:{}}
"""))

        list.add(MMethod("${'$'}{space}","listAll","查询全部","${'$'}{variableName}Ids","""
${'$'}{_this_indent}/**
${'$'}{_this_indent}* ${'$'}{description} ${'$'}{_this_nameCN}
${'$'}{_this_indent}*/${'$'}{annotation}
${'$'}{_this_indent}${'$'}{scope} List<${'$'}{objType}> ${'$'}{_this_name}()${'$'}{impl.${'$'}{_this_name}:{}}
"""))

        return list
    }

    fun resolveInterface(map:Map<out Any,out Any>): String? {
        val  paramMap = mutableMapOf<Any,Any>()
        val lineEnd = ";"
        paramMap.put("impl.save", lineEnd)
        paramMap.put("impl.delete", lineEnd)
        paramMap.put("impl.update", lineEnd)
        paramMap.put("impl.get", lineEnd)
        paramMap.put("impl.list", lineEnd)
        paramMap.put("impl.listOfIds", lineEnd)
        paramMap.put("impl.listAll", lineEnd)
//
        paramMap.putAll(map)

        return resolve(paramMap)
    }

    fun resolveImpl(map:Map<out Any,out Any>, dtoClsName:String, serviceName:String): String {
        val  paramMap = mutableMapOf<Any,Any>()
        paramMap.put("annotation", "\r\n${'$'}{space}@Override")

        val dblSpace = """${'$'}{space}${'$'}{space}"""

        paramMap.put("impl.save", """{
$dblSpace$dtoClsName dto=  transToDto(${'$'}{variableName});
${'$'}{space}${'$'}{space}$serviceName.save(dto);
${'$'}{space}}""")

        paramMap.put("impl.delete", """{
$dblSpace$dtoClsName dto=  transToDto(${'$'}{variableName});
${'$'}{space}${'$'}{space}$serviceName.delete(dto);
${'$'}{space}}""")

        paramMap.put("impl.update", """{
$dblSpace$dtoClsName dto=  transToDto(${'$'}{variableName});
${'$'}{space}${'$'}{space}$serviceName.update(dto);
${'$'}{space}}""")

        paramMap.put("impl.get", """{
${dblSpace}return transToOpen($serviceName.get(${'$'}{variableName}Id));
${'$'}{space}}""")

        paramMap.put("impl.list", """{
${dblSpace}return transToOpen($serviceName.list(courseId));
${'$'}{space}}""")

        paramMap.put("impl.listOfIds", """{
${dblSpace}return transToOpen($serviceName.listOfIds(${'$'}{variableName}Ids));
${'$'}{space}}""")

        paramMap.put("impl.listAll", """{
${dblSpace}return transToOpen($serviceName.listAll());
${'$'}{space}}""")

        paramMap.putAll(map)

        return resolve(paramMap)
    }

    fun resolveTest(map: Map<out Any, out Any>, dtoClsName: String, serviceName: String, fields: MutableList<JavaField>): String {
        val  paramMap = mutableMapOf<Any,Any>()
        paramMap.put("annotation", "\r\n${'$'}{space}@Test")

        val dblSpace = """${'$'}{space}${'$'}{space}"""
        val containsDeletePerson: Boolean = dbFieldsExists(fields,"DELETE_PERSON")
        val containsCreatePerson: Boolean = dbFieldsExists(fields,"CREATE_PERSON")
        val setDeletePerson = if(containsDeletePerson) """${dblSpace}dto.setDeletePerson(900L);""" else ""
        val idField = fields.first { it.isPk }.fieldName

        val varName = "dto"
        val saveSetStmt = generateFieldsSetExclude(dblSpace, varName,fields,idField, colNameToCamel("IS_DELETED"), colNameToCamel("CREATE_TIME"), colNameToCamel("UPDATE_TIME"),  colNameToCamel("DELETE_PERSON"))
        val updateSetStmt = generateFieldsSetExclude(dblSpace, varName,fields,colNameToCamel("IS_DELETED"), colNameToCamel("CREATE_TIME"), colNameToCamel("UPDATE_TIME"), colNameToCamel("CREATE_PERSON"), colNameToCamel("DELETE_PERSON"))
        val setIdStmt = generateFieldSet(dblSpace, varName,fields.first { it.isPk })
        paramMap.put("impl.save", """{
$dblSpace$dtoClsName dto=  new $dtoClsName();
$saveSetStmt
${dblSpace}$serviceName.save(dto);
${'$'}{space}}""")


        paramMap.put("impl.delete", """{
$dblSpace$dtoClsName dto=  new $dtoClsName();
${setIdStmt}
$setDeletePerson
${dblSpace}$serviceName.delete(dto);
${'$'}{space}}""")

        paramMap.put("impl.update", """{
$dblSpace$dtoClsName dto=  new $dtoClsName();
${updateSetStmt}
${dblSpace}$serviceName.update(dto);
${'$'}{space}}""")

        paramMap.put("impl.get", """{
$dblSpace$dtoClsName dto=  $serviceName.get(1L);
${dblSpace}System.out.println(dto);
${'$'}{space}}""")

        paramMap.put("impl.listOfIds", """{
${dblSpace}List<$dtoClsName> list=  $serviceName.listOfIds(Arrays.asList(1L,2L,3L,4L));
${dblSpace}System.out.println(list);
${'$'}{space}}""")

        paramMap.put("impl.listAll", """{
${dblSpace}List<$dtoClsName> list=  $serviceName.listAll();
${dblSpace}System.out.println(list);
${'$'}{space}}""")

        paramMap.putAll(map)

        val x =  PropertyPlaceholderHelper("\${","}",":",true)
        val props=Properties()
        props.putAll(paramMap)
        val sb = StringBuilder()
        funcList().forEach {
            val obj = it

            obj.javaClass.declaredFields.forEach {
                if (it.name != "body") {
                    it.isAccessible = true
                    props.put("_this_${it.name}",it.get(obj))
                }
            }

            val realBody  = x.replacePlaceholders("""
${'$'}{_this_indent}//${'$'}{description} 测试-${'$'}{_this_nameCN}${'$'}{annotation}
${'$'}{_this_indent}public void test_${'$'}{_this_name}()${'$'}{impl.${'$'}{_this_name}:{}}
""", props)
            sb.append(realBody)
            sb.append("\r\n")
        }

        return sb.toString()
    }



    fun resolve(map:Map<Any,Any>): String {
        val x =  PropertyPlaceholderHelper("\${","}",":",true)
        val props=Properties()

        val  defaultMap = mutableMapOf<Any,Any>()
        defaultMap.put("space", "")
        defaultMap.put("description", "")
        defaultMap.put("scope", "")
        defaultMap.put("objType", "")
        defaultMap.put("variableName", "")
        defaultMap.put("idType", "Long")
        defaultMap.put("annotation", "")

        props.putAll(defaultMap)
        props.putAll(map)

        val sb = StringBuilder()
        funcList().forEach {
            val obj = it

            obj.javaClass.declaredFields.forEach {
                if (it.name != "body") {
                    it.isAccessible = true
                    props.put("_this_${it.name}",it.get(obj))
                }
            }

            val realBody  = x.replacePlaceholders(obj.body, props)
            sb.append(realBody)
            sb.append("\r\n")
        }

        return sb.toString()
    }
}