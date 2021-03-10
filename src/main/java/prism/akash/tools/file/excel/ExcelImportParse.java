package prism.akash.tools.file.excel;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import prism.akash.container.BaseData;
import prism.akash.tools.file.ExcelImport;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel灵活导出工具实现类
 *
 * @author HaoNan Yan
 */
@Service("excelImportParse")
public class ExcelImportParse implements ExcelImport {

    @Override
    public ModelAndView importExcel(List<BaseData> pageData,
                                    BaseData params) {
        List<BaseData> PageDataList = new ArrayList<BaseData>();
        pageData.forEach((PageData) -> PageDataList.add(PageData));
        ModelMap model = new ModelMap();
        ExcelView v = new ExcelView();
        model.put("dataSet", PageDataList);
        model.put("cellName", params.getString("cellName"));
        model.put("cellParseName", params.getString("cellParseName"));
        model.put("parseCellValue", params.get("parseCellValue") != null ? params.get("parseCellValue") : new BaseData());
        model.put("fileName", params.getString("fileName"));
        return new ModelAndView(v, model);
    }

}
