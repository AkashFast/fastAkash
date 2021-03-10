package prism.akash.tools;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * 系统 数值类计算 科学计数
 * TODO : 系统·数值计算
 *
 * @author HaoNan Yan
 */
public class NumberKit {

    NumberFormat numberFormat = NumberFormat.getInstance();

    /**
     * @param digits       精确到小数点后几位
     * @param useGroup     是否需要分组处理 如1300->1,300
     * @param roundingMode 是否启用四舍五入
     */
    public NumberKit(int digits, boolean useGroup, boolean roundingMode) {
        numberFormat.setRoundingMode(roundingMode ? RoundingMode.HALF_UP : RoundingMode.HALF_EVEN);
        numberFormat.setGroupingUsed(useGroup);
        numberFormat.setMaximumFractionDigits(digits);
    }

    public String formatPercentage(float result) {
        return numberFormat.format(result);
    }

}
