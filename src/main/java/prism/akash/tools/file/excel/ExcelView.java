package prism.akash.tools.file.excel;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import prism.akash.container.BaseData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ExcelView extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        List<BaseData> mt = (List<BaseData>) model.get("dataSet");
        BaseData parseCellValue = (BaseData)model.get("parseCellValue");
        Sheet sheet = workbook.createSheet(model.get("fileName").toString());
        //处理Excel表头字体样式(Cell)
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12); //字体高度
        font.setFontName("Microsoft YaHei"); //字体
        font.setBold(true); //字体加粗

        //处理Excel表头样式(Cell)
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER); //水平布局：居中
        cellStyle.setWrapText(true);

        String[] cellName = model.get("cellName").toString().split(",");
        String[] cellParseName = model.get("cellParseName").toString().split(",");

        //判断是否有内容
        if (mt.size() != 0) {
            //获取字段列表（需和mapper中保持一致，若用到了as字段，则填写as字段的值即可,支持中文）
            sheet.setDefaultColumnWidth(cellName.length);
            Row header = sheet.createRow(0);
            for (int i = 0; i < cellParseName.length; i++) {
                header.createCell(i).setCellValue(cellParseName[i]);
                header.getCell(i).setCellStyle(cellStyle);
            }

            for (int i = 0; i < mt.size(); i++) {
                Row row = sheet.createRow(i + 1);
                BaseData pd = mt.get(i);
                //循环CellName获取值
                for (int j = 0; j < cellName.length; j++) {
                    Cell cell = row.createCell(j);
                    if (j < cellName.length) {
                        Object o = pd.get(cellName[j].trim());
                        // TODO 需要字段转义
                        cell.setCellValue(o == null ? "" : parseFormat(parseCellValue,cellName[j].trim(),o.toString()));
                    } else {
                        cell.setCellValue("");
                    }
                    cell.setCellStyle(cellStyle);
                }
            }

            for (int i = 0; i <= cellName.length; i++) {
                // 调整每一列宽度
                sheet.autoSizeColumn((short) i);
                // 解决自动设置列宽中文失效的问题
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
            }
        } else {
            Row header = sheet.createRow(0);
            for (int i = 0; i < cellParseName.length; i++) {
                header.createCell(i).setCellValue(cellParseName[i]);
                header.getCell(i).setCellStyle(cellStyle);
            }
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        OutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        outputStream.flush();
        outputStream.close();
    }

    /**
     * 内部方法:对指定的导出字段进行格式化处理
     * @param formatData        格式化数据对象
     * @param keyCode           指定的输出字段key
     * @param value             原输出值
     * @return
     */
    private String parseFormat(BaseData formatData, String keyCode, String value) {
        Object formatObj = formatData.get(keyCode);
        if (formatObj != null) {
            BaseData format = (BaseData) formatObj;
            Iterator<Map.Entry<String, String>> entries = format.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry<String, String> entry = entries.next();
                if (value.equals(entry.getKey())){
                    return entry.getValue();
                }
            }
            return value;
        } else {
            return value;
        }
    }
}
