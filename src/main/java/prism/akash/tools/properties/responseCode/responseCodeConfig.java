package prism.akash.tools.properties.responseCode;

/**
 * 请求响应码·基本项配置
 */
public class responseCodeConfig {

    /**
     * 使用响应码换取指定的相应信息
     *
     * @param code
     * @return
     */
    public static String formatCode(String code) {
        String result = "⚠ 操作失败：未知错误，请联系管理员";
        // 新增
        if (code.length() == 32) {
            result = code;
        } else {
            if (code.equals("1")) {
                result = "操作执行成功";
            }
            if (code.equals("0")) {
                result = "⚠ 操作失败：程序执行异常，请联系管理员";
            }
            if (code.equals("-1")) {
                result = "⚠ 操作失败：关键参数字段有误（可操作字段不存在或「id」参数未填写）";
            }
            if (code.equals("-2")) {
                result = "⚠ 操作失败：待操作数据表不存在";
            }
            if (code.equals("-3")) {
                result = "⚠ 操作失败：当前数据版本已过期";
            }
            if (code.equals("-8") || code.equals("") || code.isEmpty()) {
                result = "⚠ 操作失败：待操作数据不存在";
            }
            if (code.equals("-9")) {
                result = "⚠ 操作失败：缺少「version」字段";
            }
            if (code.equals("-5")) {
                result = "⚠ 操作失败：请求方法时缺少关键字段";
            }
            if (code.equals("-99")) {
                result = "⚠ 操作失败：移除节点失败，请删除子级节点后重试";
            }
            if (code.equals("UR1")) {
                result = "⚠ 操作失败：用户「USER」不存在";
            }
            if (code.equals("UR2")) {
                result = "⚠ 操作失败：权限「ROLE」未定义";
            }
            if (code.equals("UR3")) {
                result = "⚠ 操作失败：菜单「MENU」未定义";
            }
            if (code.equals("UR4")) {
                result = "⚠ 操作失败：数据源「TABLES」未定义";
            }
            if (code.equals("CR1")) {
                result = "⚠ 操作失败：数据唯一码「CODE」已被使用";
            }
            if (code.equals("NAC")) {
                result = "⚠ 操作失败：无数据访问权限";
            }
        }
        return result;
    }

}
