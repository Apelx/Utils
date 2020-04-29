package cn.apelx.tool.list;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 集合Utils
 * @Author lx
 * @Date 2019/12/9 14:53
 */
public class ListUtils {

    /**
     * 拆分单个List为指定 SIZE 的 List
     *
     * @param list
     * @param size 子集集合SIZE
     */
    public static List<List<String>> splitListString(List<String> list, int size) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<List<String>> result = new ArrayList();
        if (list.size() <= size) {
            result.add(list);
        } else {
            int fromIndex = 0;
            while (fromIndex < list.size()) {
                int toIndex = fromIndex + size;
                if (toIndex > list.size()) {
                    toIndex = list.size();
                }
                List<String> subList = list.subList(fromIndex, toIndex);
                result.add(subList);
                fromIndex += size;
            }
        }
        return result;
    }

    /**
     * 拆分单个List为指定 SIZE 的 List
     *
     * @param list
     * @param size 子集集合SIZE
     */
    public static List<List> splitList(List list, int size) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<List> result = new ArrayList();
        if (list.size() <= size) {
            result.add(list);
        } else {
            int fromIndex = 0;
            while (fromIndex < list.size()) {
                int toIndex = fromIndex + size;
                if (toIndex > list.size()) {
                    toIndex = list.size();
                }
                List subList = list.subList(fromIndex, toIndex);
                result.add(subList);
                fromIndex += size;
            }
        }
        return result;
    }
}
