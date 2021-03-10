package prism.akash.controller.file;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import prism.akash.controller.proxy.BaseProxy;
import prism.akash.tools.StringKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 文件管理相关接口
 *       TODO : 系统·文件管理 （上传 / 下载 / 处理）
 * @author HaoNan Yan
 */
@RestController
public class FileController extends BaseProxy implements Serializable {

    /**
     * 文件上传
     *
     * @param request
     * @param file
     * @param data
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/upFile",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String upLoad(
            HttpServletRequest request,
            @RequestParam(value = "file", required = false) MultipartFile[] file,
            @RequestParam(value = "data", required = false, defaultValue = "{}") String data
    ) {
        return invokeUpLoad(file, StringKit.parseBaseData(data));
    }


    /**
     * 获取图片及文件流
     *
     * @param response
     * @param fileName
     * @throws IOException
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/getFile",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    public void getFile(
            HttpServletResponse response,
            @RequestParam(value = "fileName", required = false) String fileName,
            @RequestParam(value = "data", required = false, defaultValue = "{}") String data
    ) {
        invokeDownLoad(response, fileName, StringKit.parseBaseData(data));
    }
}
