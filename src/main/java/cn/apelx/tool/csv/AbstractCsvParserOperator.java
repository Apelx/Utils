package cn.apelx.tool.csv;

import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * CSV 解析基类
 *
 * @author lx
 * @date 2019-10-15
 */

public abstract class AbstractCsvParserOperator {

    protected List dataList = new ArrayList(16);

    /**
     * 操作csv记录
     *
     * @param records
     */
    public abstract void operateRecord(List<CSVRecord> records);

    public List getDataList() {
        return dataList;
    }
}
