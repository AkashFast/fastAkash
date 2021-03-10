package prism.akash.dataInteraction;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import prism.akash.container.BaseData;

import java.util.List;

@Mapper
@Component
public interface BaseInteraction {

    @Update("${bd.executeSql}")
    int execute(@Param("bd")BaseData bd);

    @Select("${bd.select}")
    List<BaseData> select(@Param("bd")BaseData bd);

    @Select("${bd.totalSql}")
    Integer selectNums(@Param("bd")BaseData bd);
}
