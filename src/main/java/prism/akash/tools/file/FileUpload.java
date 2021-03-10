package prism.akash.tools.file;

import prism.akash.container.BaseData;

import java.util.List;
import java.util.Map;

public interface FileUpload {

    /**
     * 上传并解析Excel内数据「数据库标化处理」
     * -- * TODO 仅支持简单数据表的解析及处理！
     *
     * @param fileUrl      excel文件地址
     * @param bd           对应的附加信息（指定主表信息）
     * @param isGenerateId 是否需要生成主键编号
     *                     @param patchNum 批量处理，每次处理多少条
     * @return
     * @throws Exception
     */
    Map<String, String> importExcel(String fileUrl, BaseData bd, boolean isGenerateId, int patchNum) throws Exception;

    /**
     * 上传并解析指定Excel的数据「非标化数据」
     *
     * @param fileUrl   excel文件地址
     * @param tableHead 表头转换集合
     * @return
     * @throws Exception
     */
    List<BaseData> importExcelData(String fileUrl, Map<String, String> tableHead) throws Exception;
}
