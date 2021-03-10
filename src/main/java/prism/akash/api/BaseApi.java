package prism.akash.api;

import prism.akash.container.BaseData;
import prism.akash.container.sqlEngine.sqlEngine;

import java.util.List;
import java.util.Map;

public interface BaseApi {

    /**
     * 数据查询 - 无分页
     * @param id
     * @param executeData
     * @return
     */
    List<BaseData> select(String id,String executeData);

    /**
     * 数据查询  - 带分页
     * @param id
     * @param executeData
     * @return
     */
    Map<String,Object> selectPage(String id,String executeData);


    /**
     * 数据查询  - 带分页
     * TODO 本方法仅提供给schema使用
     * @param sqlEngine
     * @return
     */
    Map<String,Object> selectPageBase(sqlEngine sqlEngine);


    /**
     * 数据引擎执行 - 增删改
     * @param id            数据引擎ID
     * @param executeData   sql引擎所需的参数数据对象
     * @return
     */
    int execute(String id,String executeData);

    /**
     * 根据ID查询单条数据（请注意，只能查询state为1，即正常的数据）
     * @param id            数据引擎ID
     * @param executeData   sql引擎所需的参数数据对象
     *                                  id :     待查询数据id
     *                                  fields : 指定查询的字段，没有为全部
     * @return
     */
    BaseData selectByOne(String id,String executeData);

    /**
     * 数据新增
     * @param id            数据表ID
     * @param executeData   需要执行新增的数据对象
     * @return
     *              uuid : 成功
     *              -1   : 参数字段不匹配
     *              -2   : 数据表不存在
     *              -5   : 失败
     */
    String insertData(String id,String executeData);


    /**
     * 数据更新
     * @param id            数据表ID
     * @param executeData   需要执行更新的数据对象（需要指定需更新的数据ID，Version）
     * @return
     *               1   : 成功
     *              -1   : 参数字段不匹配
     *              -2   : 数据表不存在
     *              -3   : 数据版本不匹配
     *              -8   ：数据不存在
     *              -9   ：入参数据有误（缺少版本号 updVersion）
     *               0   ：失败（数据锁定:is_lock状态）
     */
    int updateData(String id,String executeData);

    /**
     * 数据删除
     * @param id            数据表ID
     * @param executeData   需要执行删除的数据对象（需要指定需更新的数据ID，Version）
     * @return
     *           0 - 失败（数据锁定:is_lock状态）
     *           1 - 成功
     *           -1- 参数不匹配（没有id)
     *           -2- 不存在表
     */
    int deleteData(String id,String executeData);

    //TODO 内部接口  Start

    /**
     * 数据查询
     * @param sqlEngine
     * @return  返回结果集
     */
    List<BaseData> selectBase(sqlEngine sqlEngine);

    /**
     * 增删改
     * @param sqlEngine
     * @return  执行成功条数
     */
    int execute(sqlEngine sqlEngine);

    //TODO 内部接口 End
}
