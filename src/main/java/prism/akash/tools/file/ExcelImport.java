package prism.akash.tools.file;

import org.springframework.web.servlet.ModelAndView;
import prism.akash.container.BaseData;

import java.util.List;

/**
 * Excel灵活导出工具类
 *
 * @author HaoNan Yan
 */
public interface ExcelImport {


    /**
     * Import Excel Tool
     * Excel灵活导出工具类「单sheet」
     *
     * @param pageData  需要导出的实体集合
     * @param params
     *                  cellName            字段值
     *                  cellParseName       特殊字段格式化
     *                  fileName            Excel文件名称
     * @return 导出数据流[input Stream]
     */
    ModelAndView importExcel(List<BaseData> pageData, BaseData params);

}
