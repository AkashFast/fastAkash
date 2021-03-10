package prism.akash.schema.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import prism.akash.schema.BaseSchema;
import prism.akash.tools.annocation.Access;
import prism.akash.tools.annocation.Schema;
import prism.akash.tools.annocation.checked.AccessType;
import prism.akash.tools.file.FileHandler;
import prism.akash.tools.file.FileUpload;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@Schema(code = "file", name = "系统文件管理")
public class fileSchema extends BaseSchema {

    private final Logger logger = LoggerFactory.getLogger(fileSchema.class);

    @Autowired
    FileHandler fileHandler;

    @Autowired
    FileUpload fileUpload;


    /**
     * 上传文件
     *
     * @param file 文件流对象
     * @return
     */
    @Access({AccessType.UPLOAD})
    public String upLoadFile(MultipartFile[] file) {
        String files = "";
        //文件处理
        String ffile = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (null != file && file.length != 0) {
            try {
                files = fileHandler.uploadFiledList(file, null, "" + ffile);
            } catch (Exception e) {
                logger.error("FileController:upLoad:Exception -> " + e.getMessage() + " / " + e.getCause().getMessage());
            }
        }
        return files;
    }

    /**
     * 数据文件下载
     * @param response
     * @param fileName  请求下载的文件名称
     */
    @Access({AccessType.DOWN})
    public void getFile(HttpServletResponse response, String fileName) {
        try {
            fileHandler.getFile(response, fileName);
        } catch (IOException e) {
            logger.error("FileController:getFile:IOException -> " + e.getMessage() + " / " + e.getCause().getMessage());
        }
    }

}
