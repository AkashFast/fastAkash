package prism.akash.container.converter;

import prism.akash.container.BaseData;

/**
 * 逻辑引擎初始化数据
 */
public class ConverterData {

    //标识：当前引擎是否已存在
    private Boolean exist = false;

    //标识：当前执行逻辑版本号
    private Integer version = 0;

    //标识：当前执行逻辑标准序列值
    private Integer sort = 0;

    //标识：当前子查询执行逻辑标准序列值
    private Integer childSort = 0;

    //标识：当前引擎唯一编号
    private String engineId;

    //标识：当前子查询逻辑编号
    private String childId;

    //标识：错误信息
    private String errorMsg;

    //标识：数据执行结果
    private BaseData execute;

    public BaseData getExecute() {
        return execute;
    }

    public void setExecute(BaseData execute) {
        // TODO : 敏感信息屏蔽
        execute.remove("queryTable");
        execute.remove("fromSql");
        execute.remove("appointColumn");
        this.execute = execute;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public Boolean getExist() {
        return exist;
    }

    public void setExist(Boolean exist) {
        this.exist = exist;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getChildSort() {
        return childSort;
    }

    public void setChildSort(Integer childSort) {
        this.childSort = childSort;
    }

    public String getEngineId() {
        return engineId;
    }

    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}
