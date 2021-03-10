package prism.akash.tools.oauth;

import org.springframework.stereotype.Component;
import prism.akash.tools.StringKit;

/**
 * AccessTool
 * TODO : 系统 · 安全控制
 *
 * @author HaoNan Yan
 */
@Component
public class AccessTool {

    /**
     * 入参安全校验
     * TODO  主要作用于Controller层,对用户上传的数据进行安全过滤校验
     *
     * @param params ...待校验参数
     * @return
     */
    public Boolean accessParamCheck(String... params) {
        Boolean accessPass = true;
        if (params.length > 0) {
            //循环获取待校验的数据
            for (String param : params) {
                //STRING  TODO 仅支持英文、数字及下划线「 _ 」
                accessPass = StringKit.isSpecialChar(param);
                if (accessPass){
                    break;
                }
            }
        }
        return accessPass;
    }
}
