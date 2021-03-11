package prism.akash;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import prism.akash.container.BaseData;
import prism.akash.controller.BaseController;
import prism.akash.tools.http.HTTPTool;
import prism.akash.tools.reids.RedisTool;

/**
 * 多并发线程测试
 * TODO  秒杀示例
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AkashApplicationTests {

    @Autowired
    RedisTool redisTool;

    @Autowired
    BaseController baseController;

    @Autowired
    HTTPTool httpTool;

    // 使用SQL引擎进行数据查询
    //    @Test
    public void testPrism(){
        System.out.println(selectEngine("80272d292a99479890717a04df11e900"));
        //TODO 当前引擎仅做测试使用
        System.out.println(selectEnginePage("5f0ee5c127c24961997a1be205499d0c"));
    }

    //基本接口的增删改查
    public void base() {
        String tid = "57c4b85014b4447dbf21b0c6abcfe9f2";
        String result = add(tid);
        JSONObject jo = JSONObject.parseObject(result);
        String id = jo.getString("result");
        System.out.println("add : " + id);
        System.out.println(selectOne(tid, id));
        System.out.println("upd : " + upd(tid, id));
        System.out.println(selectOne(tid, id));
        System.out.println("softDelete : " + deleteDataSoft(tid, id));
        System.out.println(selectOne(tid, id));
//        System.out.println("DeleteError : " + deleteData(tid,JSON.toJSONString("11-\"dasdas11@111")));
        System.out.println("Delete : " + deleteData(tid, id));
        System.out.println(selectOne(tid, id));
    }

//    @Test
    public void menu_Test(){
        BaseData execute = new BaseData();
        execute.put("name", "测试");
        execute.put("code", "test");
        execute.put("is_parent", 1);
        execute.put("is_lock", 0);
        execute.put("pid", -1);
        execute.put("order_number", 0);
        System.out.println(JSON.toJSONString(execute));
        System.out.println(baseController.executeUnify("menu", "addMenuNode", "", JSON.toJSONString(execute)));
    }


    //数据引擎查询数据
    public String selectEngine(String id) {
        BaseData execute = new BaseData();
        execute.put("executeData", JSON.toJSONString(execute));
        return baseController.executeUnify("", "select", id, JSON.toJSONString(execute));
    }

    //数据引擎查询分页数据
    public String selectEnginePage(String id) {
        BaseData execute = new BaseData();
        execute.put("like_code", "select%");
        execute.put("like_name", "select%");
        return baseController.executeUnify("", "selectPage", id, JSON.toJSONString(execute));
    }

    //数据暴力删除
    public String deleteData(String tid, String id) {
        BaseData execute = new BaseData();
        execute.put("id", id);
        return baseController.executeUnify("", "deleteData", tid, JSON.toJSONString(execute));
    }

    //数据软删除
    public String deleteDataSoft(String tid, String id) {
        BaseData execute = new BaseData();
        execute.put("id", id);
        return baseController.executeUnify("", "deleteDataSoft", tid, JSON.toJSONString(execute));
    }

    //查询单条数据
    public String selectOne(String tid, String id) {
        BaseData  execute = new BaseData();
        execute.put("id", id);
        return baseController.executeUnify("", "selectByOne", tid, JSON.toJSONString(execute));
    }


    //更新菜单
    public String upd(String tid, String id) {
        BaseData  execute = new BaseData();
        execute.put("name","测试22P");
        execute.put("code", "ZZytest'哈哈1-1");
        execute.put("is_parent",1);
        execute.put("is_lock",0);
        execute.put("pid",-1);
        execute.put("note",-1);
        execute.put("order_number",0);
        execute.put("version",1);
        execute.put("id", id);
        return baseController.executeUnify("", "updateData", tid, JSON.toJSONString(execute));
    }

    //新增菜单
    public String add(String tid) {
        BaseData  execute = new BaseData();
        execute.put("name","测试");
        execute.put("code","test");
        execute.put("is_parent",1);
        execute.put("is_lock",0);
        execute.put("pid",-1);
        execute.put("order_number",0);
        return baseController.executeUnify("base", "insertData", tid, JSON.toJSONString(execute));
    }

    /**
     * TODO 本示例模拟演示了100个用户竞争10件商品的秒杀逻辑
     * @throws Throwable
     */
//    @Test
    public void testThreadJunit() throws Throwable {
        //设置基础库存数量
        redisTool.set("kucun","10",600000);

        //并发数量
        int runnerSize = 50;

        //Runner数组，相当于并发多少个。
//        TestRunnable[] trs = new TestRunnable[runnerSize];
//
//        for (int i = 0; i < runnerSize; i++) {
//            trs[i] = new ThreadA();
//        }
//
//        // 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
//        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//        // 开发并发执行数组里定义的内容
//        mttr.runTestRunnables();
    }

//    private class ThreadA extends TestRunnable {
//        @Override
//        public void runTest() throws Throwable {
//            String value =  redisTool.getOnLock("kucun");
//            int other = Integer.parseInt(value) - 1;
//            System.out.println(new Date());
//            //库存抢占
//            update("kucun",other);
//        }
//    }

    //Test:数据更新多线程测试
    //---->测试目标:无数据抢占情况出现
    public void update(String key,int other) throws Exception {
       if (other > -1){
           String value = redisTool.getOnLock("kucun");
           int otherNew = Integer.parseInt(value);
           if (otherNew > other) {
               redisTool.set(key, other + "", 600000);
               System.out.println("当前剩余库存：" + select("kucun"));
           } else {
               System.out.println("手慢了一步，被抢走啦~");
           }
       }else{
           System.out.println("抢占失败，库存不足");
       }
    }

    //Test:数据查询多线程测试
    //---->测试目标:可以准确返回指定查询数据
    public String select(String key) throws Exception {
        return redisTool.get(key);
    }
}
