package prism.akash.tools.date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
public class dateParse {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 格式化时间parse
     * @param format   格式化
     * @param date     时间
     * @return Date
     * @throws ParseException
     */
    public Date parseDate(String format, String date) {
        Date ndate = new Date();
        try {
            ndate = new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            logger.error("dateParse.parseDate -> ParseException 类型转换异常");
        }
        return ndate;
    }

    /**
     * 格式化时间format
     * @param format   格式化
     * @param date     时间
     * @return String
     */
    public String formatDate(String format,Date date){
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 获取时间差
     *
     * @param date
     * @param date2
     * @return
     */
    public Long getDiffDay(String date, String date2) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long days = null;
        try {
            Date currentTime = dateFormat.parse(date2);
            Date pastTime = dateFormat.parse(date);
            long diff = currentTime.getTime() - pastTime.getTime();
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            logger.error("dateParse.getDiffDay -> ParseException 类型转换异常");
        }
        return days;
    }

    /**
     * 日期增减「天/分钟」
     *
     * @param formatStr
     * @param dates
     * @param type  0-分钟 1-天
     * @return
     * @throws ParseException
     */
    public String addTime(String formatStr, String dates, int add,int type) {
        Date date = null;//取时间
        date = parseDate(formatStr, dates);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(type == 0 ? calendar.MINUTE  : calendar.DATE, add);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime();   //这个时间就是日期往后推一天的结果
        return formatDate(formatStr, date);
    }

    /**
     * 获取指定日期是周几
     *
     * @param date
     * @return
     */
    public int getWeek(String date) {
        int week = 0;
        Date today = null;
        today = parseDate("yyyy-MM-dd", date);
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        week = c.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public  String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }
}
