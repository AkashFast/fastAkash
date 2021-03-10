package prism.akash.tools.druid;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import prism.akash.container.BaseData;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Druid数据库资源管理类
 * TODO : 系统·通用工具
 *
 * @author HaoNan Yan
 */
@Component
public class InitDataBase {

    private final Logger logger = LoggerFactory.getLogger(InitDataBase.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driver-class-name}")
    private String DriverClassName;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 获取数据连接池
     *
     * @return
     */
    private DruidDataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(DriverClassName);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(20);
        //连接泄漏监测
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(30);
        //配置获取连接等待超时的时间
        dataSource.setMaxWait(20000);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(20000);
        //防止过期
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        return dataSource;
    }

    /**
     * 获取数据库表及字段信息
     *
     * @param code  指定需要同步的数据表code,未指定则查询全表
     * @param base: 是否同步系统底层数据表
     * @return
     */
    public List<BaseData> getDataBase(String code, boolean base) {
        List<BaseData> tableArray = new ArrayList<>();
        boolean existCode = code.isEmpty() || code == null;
        String[] codeList = code.split(",");
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            DatabaseMetaData dbMetaData = con.getMetaData();
            //TODO : 获取目前使用的数据库名称
            String dataBase = url.split(":")[3].split("/")[1].split("\\?")[0];
            ResultSet rs = dbMetaData.getTables(dataBase.toLowerCase(), null, null, new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                //如果传入了CODE值，则对CODE值进行逐一匹配
                if (!existCode) {
                    for (String c : codeList) {
                        if (!c.isEmpty()) {
                            //1.如果base为true,则判断code是否具备系统表属性「sys_/cr_」
                            if (c.equals(tableName))
                                existCode = true;
                            break;
                        }
                    }
                }
                if (existCode) {
                    if (!base && (tableName.indexOf("sys_") > -1 || tableName.indexOf("cr_") > -1)){
                        //TODO 如果满足以上两个条件，则不对系统初始基础表进行
                    }else{
                        BaseData table = new BaseData();
                        table.put("code", tableName);
                        table.put("name", rs.getString("REMARKS"));
                        ResultSet rsColimns = dbMetaData.getColumns(dataBase, null, rs.getString("TABLE_NAME"), "%");
                        BaseData colimns = new BaseData();
                        while (rsColimns.next()) {
                            String cname = rsColimns.getString("COLUMN_NAME");
                            colimns.put(cname, rsColimns.getString("REMARKS") + "||" + rsColimns.getString("TYPE_NAME") + "||" + rsColimns.getString("COLUMN_SIZE"));
                        }
                        table.put("colimns", colimns);
                        tableArray.add(table);
                    }
                }
            }
            con.close();
        } catch (SQLException e) {
            logger.error("DataSource getConnection Error -> " + e.getMessage() + " / " + e.getCause().getMessage());
        } finally {
            //TODO : 关闭清空连接,等待GC回收
            con = null;
        }
        return tableArray;
    }

}
