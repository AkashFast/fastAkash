package prism.akash.tools.file.excel;

import com.alibaba.fastjson.JSON;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import prism.akash.api.BaseApi;
import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.engineEnum.conditionType;
import prism.akash.container.sqlEngine.engineEnum.groupType;
import prism.akash.container.sqlEngine.engineEnum.joinType;
import prism.akash.container.sqlEngine.engineEnum.queryType;
import prism.akash.container.sqlEngine.sqlEngine;
import prism.akash.tools.StringKit;
import prism.akash.tools.date.dateParse;
import prism.akash.tools.file.FileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExcelUpLoadParse implements FileUpload, Serializable {

    private static final Logger log = LoggerFactory.getLogger(ExcelUpLoadParse.class);

    //TODO : 获取系统默认的文件存储主路径
    @Value("${akashConfig.defaultFilePath}")
    public String defaultFilePath;

    @Autowired
    BaseApi baseApi;

    @Autowired
    dateParse dateParse;

    //HSSText  Excel内部信息字段处理
    private Object getHSSTextString(Row row, int colNum) {
        Cell cell = row.getCell(colNum);
        String cellValue = "";
        if (null != cell) {
            CellType cellType = cell.getCellTypeEnum();
            if (cellType == CellType.NUMERIC) {
                // 对数据进行二次解析
                short format = cell.getCellStyle().getDataFormat();
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = null;
                    if (format == 20 || format == 32) {
                        sdf = new SimpleDateFormat("HH:mm");
                    } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                        // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                        double value = cell.getNumericCellValue();
                        Date date = org.apache.poi.ss.usermodel.DateUtil
                                .getJavaDate(value);
                        cellValue = sdf.format(date);
                    } else {// 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }
                    try {
                        cellValue = sdf.format(cell.getDateCellValue());// 日期
                    } catch (Exception e) {
                        log.info("Excel Parse Error ：NUMERIC 单元格数据格式有误!");
                    } finally {
                        sdf = null;
                    }
                } else {
                    BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
                    // 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
                    cellValue = bd.toPlainString();
                }
                return cellValue;
            } else if (cellType == CellType.STRING) {
                return cell.getStringCellValue().trim();
            } else if (cellType == CellType.BLANK) {
                return "";
            } else if (cellType == CellType.ERROR) {
                return "";
            } else if (cellType == CellType.FORMULA) {
                try {
                    cellValue = cell.getStringCellValue();
                    if (cellValue.length() > 1) {
                        boolean exist = (cellValue.startsWith("\"") && cellValue.endsWith("\""));
                        //如果两端存在双引号则移除
                        if (exist) {
                            cellValue = cellValue.substring(1, cellValue.length() - 1);
                        }
                    }
                    return cellValue;
                } catch (IllegalStateException e) {
                    return cell.getNumericCellValue();
                }
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    @Override
    public Map<String, String> importExcel(String fileUrl, BaseData bd, boolean isGenerateId, int patchNum) throws Exception {
        Map<String, String> result = new HashMap<>();
        result.put("result", "数据录入成功");

        if (bd.get("execute") != null) {
            String execute = bd.getString("execute");
            // TODO : 获取数据源字段
            List<BaseData> columns = baseApi.selectBase(new sqlEngine()
                    .execute("cr_field", "c")
                    .joinBuild("cr_tables", "t", joinType.L)
                    .joinColunm("c", "tid", "id").joinFin()
                    .queryBuild(queryType.and, "t", "@code", conditionType.EQ, groupType.DEF, execute)
                    .selectFin(""));
            // TODO : 获取全表字段
            StringBuffer cols = new StringBuffer();
            for (BaseData col : columns) {
                cols.append(",").append(col.get("code"));
            }
            String colAppend = cols.deleteCharAt(0).toString();
            // TODO : 获取Excel缓存数据
            FileInputStream in = new FileInputStream(fileUrl);
            Workbook wk = StreamingReader.builder()
                    .rowCacheSize(1000)  //缓存到内存中的行数，默认是10
                    .bufferSize(10240)  //读取资源时，缓存到内存的字节大小，默认是1024
                    .open(in);  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
            Sheet sheet = wk.getSheetAt(0);
            // TODO : 获取表头
            Row head = null;
            List<BaseData> addDatas = new ArrayList<>();
            // TODO ： 设定执行条数及批次
            int executeNum = 0;
            int countSum = 1;
            // TODO : 对缓存数据进行遍历
            for (Row row : sheet) {
                if(row.getRowNum() == 0){
                    head = row;
                }else{
                    BaseData addData = new BaseData();
                    if (isGenerateId) {
                        addData.put("id",  StringKit.getUUID());
                    }
                    // TODO : 字段定向匹配
                    for (int j = 0; j < head.getPhysicalNumberOfCells(); j++) {
                        if (!getHSSTextString(row, j).toString().equals("")) {
                            for (BaseData col : columns) {
                                if (getHSSTextString(head, j).equals(col.getString("name"))) {
                                    // TODO : 类型匹配转换
                                    String type = col.getString("type");
                                    Object res = getHSSTextString(row, j);
                                    if (type.equals("int")) {
                                        res = Integer.parseInt(res + "");
                                    } else if (type.equals("double")) {
                                        res = Double.parseDouble(res + "");
                                    } else {
                                        res = res.toString();
                                    }
                                    addData.put(col.getString("code"), res);
                                }
                            }
                        }
                    }
                    addDatas.add(addData);
                }
                executeNum++;
                // TODO : 批量数据处理
                if (executeNum == countSum*patchNum) {
                    int res = baseApi.execute(new sqlEngine().execute(execute, execute).
                            insertFetchPush(JSON.toJSONString(addDatas), colAppend).insertFin(""));
                    log.info("保存成功：" + res + ",当前数据保存节点:" + row.getRowNum() + ",数据批次为:" + countSum);
                    addDatas.clear();
                    countSum++;
                }
            }
            // TODO : 余量数据处理
            int res = baseApi.execute(new sqlEngine().execute(execute, execute).
                    insertFetchPush(JSON.toJSONString(addDatas), colAppend).insertFin(""));
            log.info("余量数据保存成功：" + res + ",当前数据保存节点:" + executeNum + ",数据批次为:" + countSum);
            addDatas.clear();
            // 执行结束后关闭wk
            wk.close();
        }else {
            result.put("result",
                    "⚠:未指定数据目标源表！");
            return result;
        }
        return result;
    }

    @Override
    public List<BaseData> importExcelData(String fileUrl, Map<String, String> tableHead) throws Exception {
        List<BaseData> addDatas = new ArrayList<>();
        // TODO : 获取Excel缓存数据
        FileInputStream in = new FileInputStream(defaultFilePath + File.separator + fileUrl);
        Workbook wk = StreamingReader.builder()
                .rowCacheSize(1000)  //缓存到内存中的行数，默认是10
                .bufferSize(10240)  //读取资源时，缓存到内存的字节大小，默认是1024
                .open(in);  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
        Sheet sheet = wk.getSheetAt(0);
        // TODO : 获取表头
        Row head = null;
        // TODO : 对缓存数据进行遍历
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                head = row;
            } else {
                BaseData addData = new BaseData();
                for (int j = 0; j < head.getPhysicalNumberOfCells(); j++) {
                    if (!getHSSTextString(row, j).toString().equals("")) {
                        for (Map.Entry<String, String> entry : tableHead.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (getHSSTextString(head, j).equals(value)) {
                                addData.put(key, getHSSTextString(row, j));
                            }
                        }
                    }
                }
                addDatas.add(addData);
            }
        }
        // 执行结束后关闭wk
        wk.close();
        return addDatas;
    }
}
