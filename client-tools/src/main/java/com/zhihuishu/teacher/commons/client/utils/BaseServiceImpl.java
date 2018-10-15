package com.zhihuishu.teacher.commons.client.utils;

import com.zhihuishu.toolkit.helper.BeanHelper;
import com.zhihuishu.toolkit.jedis.template.JedisExecutor;
import com.zhihuishu.toolkit.jedis.template.JedisTemplate;
import com.zhihuishu.toolkit.log.LoggerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 基础类
 */
@Service
public abstract class BaseServiceImpl<DTO,OPENDTO>  {

    /** 日志组件 */
    protected LoggerTemplate log = LoggerTemplate.getLogger(this.getClass());

    @Autowired
    protected JedisExecutor jedisExecutor;

    @Autowired
    protected JedisTemplate jedisTemplate ;

    public static boolean isValidId(Long id) {
        return id!=null && id > 0;
    }

    public static String invalidToNullForStr(Long l) {
        if (isValidId(l)) {
            return l.toString();
        }
        return null;
    }

    public static Boolean intToBoolean(Integer str) {
        if (str == null) {
            return false;
        }
        return str.equals(1);
    }

    public static String str(Object str) {
        if (str == null) {
            return null;
        }
        return str.toString();
    }
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    public static boolean isNotEmpty(Collection str) {
        return !isEmpty(str);
    }
    public static boolean isEmpty(String str) {
        return str == null || str.toString().length()==0;
    }
    public static String emptyToNull(String str) {
        if(isEmpty(str))
            return null;
        else
            return str;
    }
    public static boolean isEmpty(Object obj) {
        return obj == null ;
    }

    public static boolean isEmpty(Collection str) {
        return str == null || str.size() == 0;
    }
    public static <T> T returnLast(T... arr) {
        if (arr!=null) {
            for (int i = arr.length-1; i >=0; i--) {
                if (arr[i]!=null) {
                    return arr[i];
                }
            }
        }
        return null;
    }

    public static String ns(String message) {
        return message + " 不能为空!";
    }
    public static void error(String message) {
        throw new IllegalArgumentException(message);
    }
    public static void error(String code,String message) {
        throw new IllegalArgumentException(message+" 错误码:"+code);
    }
    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
    public static void assertTrue(boolean object, String message) {
        if (!object) {
            throw new IllegalArgumentException(message);
        }
    }
    public static void assertFalse(boolean object, String message) {
        if (object) {
            throw new IllegalArgumentException(message);
        }
    }
    public static void assertStringSize(String str,int size, String message) {
        if (str != null && str.length() > size) {
            throw new IllegalArgumentException(String.format(message,size));
        }
    }
    public static void assertCollectionNotNull(Collection list,String message) {
        if (list == null || list.size() == 0) {
            throw new IllegalArgumentException(message);
        }
    }



    public  static <T,R> List<R> transList(Function<T,R> function, Collection<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        List<R> ret = new ArrayList<>(list.size());
        list.forEach(t -> ret.add(function.apply(t)));
        return ret;
    }

    public Integer ensurePageIndex(Integer pageIndex, Integer pageSize, long totalRecords) {
        if (pageIndex == null) {
            return 0;
        }

        if (pageIndex < 0) {
            return 0;
        }

        int totalPage = (int) (totalRecords/pageSize);
        if (totalRecords%pageSize > 0) {
            totalPage += 1;
        }

        if (pageIndex > totalPage) {
            return totalPage;
        }

        return pageIndex;
    }

    public Integer ensurePageSize(Integer pageSize, int from, int to) {
        if (pageSize == null) {
            return from;
        }
        if (pageSize < from) {
            return from;
        }
        if (pageSize > to) {
            return to;
        }

        return pageSize;
    }

    public <K,V> void complete(Map<K, List<V>> ret, List<K> assisUserIds) {
        if (assisUserIds!=null && ret !=null) {
            assisUserIds.forEach(k -> {
                List<V> vs = ret.get(k);
                if (vs == null) {
                    ret.put(k, new ArrayList<>());
                }
            });
        }
    }
    public <K,V> void completeObj(Map<K, V> ret, List<K> assisUserIds) {
        if (assisUserIds!=null && ret !=null) {
            assisUserIds.forEach(k -> {
                V vs = ret.get(k);
                if (vs == null) {
                    ret.put(k, null);
                }
            });
        }
    }

    //互转方法 start
    public Class<DTO> getDtoClass(){
        throw new UnsupportedOperationException("必须先返回dto class!");
    }
    public Class<OPENDTO> getOpenDtoClass(){
        throw new UnsupportedOperationException("必须先返回open dto class!");
    }

    //// open -> dto
    public  DTO transToDto(OPENDTO opendto) {
        DTO ret = BeanHelper.transTo(opendto, getDtoClass());
        return ret;
    }
    public  List<DTO> transToDto(List<OPENDTO> list) {
        return transList(opendto -> transToDto(opendto),list);
    }

    //// dto -> open
    public  OPENDTO transToOpen(DTO dto) {
        OPENDTO ret = BeanHelper.transTo(dto, getOpenDtoClass());
        return ret;
    }
    public  List<OPENDTO> transToOpen(List<DTO> list) {
        return transList(opendto -> transToOpen(opendto),list);
    }
    //互转方法 end
}
