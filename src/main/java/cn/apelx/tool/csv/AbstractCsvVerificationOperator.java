package cn.apelx.tool.csv;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

/**
 * CSV 验证基类
 *
 * @author lx
 * @since 2019/11/11 15:57
 */
public abstract class AbstractCsvVerificationOperator {

    /**
     * 验证 Csv是否复核业务逻辑
     *
     * @param records
     * @return
     */
    public abstract void verify(List<CSVRecord> records);
}
