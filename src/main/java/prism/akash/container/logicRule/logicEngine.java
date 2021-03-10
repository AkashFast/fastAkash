package prism.akash.container.logicRule;

import prism.akash.container.BaseData;

import java.io.Serializable;

public class logicEngine implements Serializable {

    private static final long serialVersionUID = 1L;

    BaseData logic = null;

    public logicEngine(){
        logic = new BaseData();
    }

    /**
     * 初始化构建逻辑执行引擎
     * @param backType  设置返回值类型
     * @param back      设置返回值名称
     * @return
     */
    public logicEngine build(String backType,String back){

        return this;
    }
}
