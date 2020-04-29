package cn.apelx.tool.csv;


import cn.apelx.tool.constant.CsvConstant;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * csv文件工具类
 *
 * @author zym
 * @date 2019-10-15
 */
public class CsvUtils {

    private static final Logger logger = LoggerFactory.getLogger(CsvUtils.class);

    /**
     * 解析csv文件
     *
     * @param filePath             文件路径
     * @param parserOperator       Csv解析操作类
     * @param verificationOperator Csv验证操作类
     * @param recordSize           分页步长
     * @param headers              自定义列头（根据传入的列头名称顺序与文件内容顺序对应，传入自定义列头可根据列头名称取值）
     * @param isGBK                是否是GBK编码
     * @param numberLimit          导入条数限制
     * @return
     */
    private static int parserCsv(String filePath, AbstractCsvParserOperator parserOperator, AbstractCsvVerificationOperator verificationOperator,
                                 int recordSize, String[] headers, boolean isGBK, Integer numberLimit) {
        int totalCount = 0;
        List<CSVRecord> recordList = new ArrayList<>();
        Iterator<CSVRecord> records = null;
        //创建CSVFormat
        CSVFormat format = null;
        if (headers != null && headers.length > 0) {
            format = CSVFormat.RFC4180.withHeader(headers);
        } else {
            format = CSVFormat.RFC4180;
        }
        CSVParser parser = null;
        Reader reader = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            String charsetName = "UTF-8";
            if (isGBK) {
                charsetName = "GBK";
            }
            reader = new InputStreamReader(fileInputStream, charsetName);
            //创建CSVParser对象
            parser = new CSVParser(reader, format);
            records = parser.iterator();
            if (parserOperator != null) {
                while (records.hasNext()) {
                    CSVRecord record = records.next();
                    if (record.getRecordNumber() > 1) {
                        recordList.add(record);
                        totalCount++;
                    }
                    // CSV文件内容条数超限
                    if (numberLimit != null && totalCount > numberLimit) {
                        throw new RuntimeException("CSV data overrun. limit " + numberLimit);
                    }
                    // 达到分页数量，分页操作
                    if (recordList.size() == recordSize) {
                        if (verificationOperator != null) {
                            verificationOperator.verify(recordList);
                        }
                        parserOperator.operateRecord(recordList);
                        recordList = new ArrayList<>();
                    }
                }
                if (recordList.size() > 0) {
                    if (verificationOperator != null) {
                        verificationOperator.verify(recordList);
                    }
                    parserOperator.operateRecord(recordList);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("File [" + filePath + "] is not found ");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("File [" + filePath + "] throw exception " + e.toString());
        } finally {
            closeParserCsvIO(parser, reader, fileInputStream);
        }
        return totalCount;
    }

    /**
     * 解析csv文件（自定义列头）
     *
     * @param filePath       文件路径
     * @param parserOperator Csv解析操作类
     * @param recordSize     分页步长
     * @param numberLimit    导入条数限制
     * @return
     */
    public static void parserCsvWithHeader(String filePath, AbstractCsvParserOperator parserOperator, int recordSize,
                                           String[] headers, boolean isGbk, Integer numberLimit) {
        parserCsv(filePath, parserOperator, null, recordSize, headers, isGbk, numberLimit);
    }

    /**
     * 解析csv文件（无自定义列头）
     *
     * @param filePath       文件路径
     * @param parserOperator Csv解析操作类
     * @param recordSize     分页步长
     * @param numberLimit    导入条数限制
     * @return
     */
    public static int parserCsvWithoutHeader(String filePath, AbstractCsvParserOperator parserOperator, int recordSize,
                                             boolean isGbk, Integer numberLimit) {
        return parserCsv(filePath, parserOperator, null, recordSize, null, isGbk, numberLimit);
    }


    /**
     * 解析csv文件（自定义列头）支持验证
     *
     * @param filePath             文件路径
     * @param parserOperator       CSV解析操作类
     * @param verificationOperator CSV验证操作类
     * @param recordSize           分页步长
     * @param numberLimit          导入条数限制
     * @return
     */
    public static void parserCsvWithHeaderSupportVerify(String filePath, AbstractCsvParserOperator parserOperator,
                                                        AbstractCsvVerificationOperator verificationOperator, int recordSize,
                                                        String[] headers, boolean isGbk, Integer numberLimit) {
        parserCsv(filePath, parserOperator, verificationOperator, recordSize, headers, isGbk, numberLimit);
    }

    /**
     * 解析csv文件（无自定义列头） 支持验证
     *
     * @param filePath             文件路径
     * @param parserOperator       CSV解析操作类
     * @param verificationOperator CSV验证操作类
     * @param recordSize           分页步长
     * @param numberLimit          导入条数限制
     * @return
     */
    public static int parserCsvWithoutHeaderSupportVerify(String filePath, AbstractCsvParserOperator parserOperator,
                                                          AbstractCsvVerificationOperator verificationOperator,
                                                          int recordSize, boolean isGbk, Integer numberLimit) {
        return parserCsv(filePath, parserOperator, verificationOperator, recordSize, null, isGbk, numberLimit);
    }

    /**
     * 关闭解析csv文件使用的流对象
     *
     * @param parser
     * @param reader
     * @param fileInputStream
     */
    private static void closeParserCsvIO(CSVParser parser, Reader reader, FileInputStream fileInputStream) {
        try {
            if (parser != null) {
                parser.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否为csv类型文件（是否以.csv结尾）
     *
     * @param fileName
     * @return
     */
    public static boolean isCsvFile(String fileName) {
        if (StringUtils.isNotEmpty(fileName)) {
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (StringUtils.isNotEmpty(suffix) && CsvConstant.FILE_TYPE_CSV.equals(suffix)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
