package prism.akash.tools.http.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

/**
 * HttpRequest
 * TODO : 系统 · HTTP请求
 *
 * @author HaoNan Yan
 */
@Component
public class HttpRequest implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    /**
     * GET请求
     *
     * @param url    请求地址
     * @param param  参数
     * @param header 自定义的header请求头
     * @return
     */
    public String sendGet(String url, Map<String, String> param, Map<String, String> header) {
        String result = "";
        BufferedReader in = null;
        StringBuffer urlParam = new StringBuffer(url);
        //在参数对象存在时,对数据进行处理
        if (param.size() > 0) {
            urlParam.append("?");
            Iterator<Map.Entry<String, String>> entries = param.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                urlParam.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            urlParam.deleteCharAt(urlParam.length() - 1);
        }
        try {
            URL realUrl = new URL(urlParam.toString());
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置请求头通用属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            //在自定义请求头不为空时,进行数据追加
            if (header.size() > 0) {
                Iterator<Map.Entry<String, String>> entriesHeader = header.entrySet().iterator();
                while (entriesHeader.hasNext()) {
                    Map.Entry<String, String> entryHeader = entriesHeader.next();
                    connection.setRequestProperty(entryHeader.getKey(), entryHeader.getValue());
                }
            }

            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (MalformedURLException e) {
            logger.error("HttpRequest:sendGet:MalformedURLException -> " + e.getMessage() + " / " + e.getCause().getMessage());
        } catch (IOException e) {
            logger.error("HttpRequest:sendGet:IOException -> " + e.getMessage() + " / " + e.getCause().getMessage());
        } finally {
            // 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                logger.error("HttpRequest:sendGet:Exception -> " + e2.getMessage() + " / " + e2.getCause().getMessage());
            }
        }
        return result;
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url         发送请求的 URL
     * @param param       请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param contentType 请求的数据类型，如果需要使用application/json则传入「json」即可
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, String param, String contentType) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            if (!contentType.isEmpty()){
                conn.setRequestProperty("Content-Type", contentType.equals("json") ? "application/json; charset=utf-8" : contentType);
            }
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("ScannerSchemaTool:发送 POST 请求出现异常！: " + e.getMessage() + " / " + e.getCause().getMessage());
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error("ScannerSchemaTool:IO流异常！: " + ex.getMessage() + " / " + ex.getCause().getMessage());
            }
        }
        return result;
    }
}
