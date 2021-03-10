package prism.akash.tools.asyncInit;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import prism.akash.api.BaseApi;
import prism.akash.container.BaseData;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同步信息初始化
 *                --  每天凌晨00:30分扫描全库表及字段进行数据同步整合
 */
@Component
public class AsyncInitData {


}
