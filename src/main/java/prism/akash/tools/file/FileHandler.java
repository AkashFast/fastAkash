package prism.akash.tools.file;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class FileHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //TODO : 获取系统默认的文件存储主路径
    @Value("${akashConfig.defaultFilePath}")
    public String defaultFilePath;

    /**
     * 多图片多文件上传
     * @param file          图片及文件的集合
     * @param request       由Controller带回HttpRequest对象
     * @param filePath      文件的上传根路径
     * @return
     */
    public String uploadFiledList(MultipartFile[] file,
                                    HttpServletRequest request,
                                    String filePath) throws Exception{
        String separator =  File.separator;
        List imgList=new ArrayList();
        String fp = defaultFilePath + filePath;
        if (file!=null && file.length>0) {
            for (int i = 0; i < file.length; i++) {
                String fileName = file[i].getOriginalFilename();
                if (fileName != null && fileName != "") {
                    byte[] bytes = file[i].getBytes();

                    //获取文件后缀名称
                    String fileF  =  file[i].getContentType();
                    //生成新的文件名称
                    String filePathing = fileF.contains("jpg")||fileF.contains("png")||
                            fileF.contains("jpeg")||fileF.contains("gif") ? "imgFile" :
                            fileF.contains("pdf")|| fileF.contains("text/plain")|| fileF.contains(".document")|| fileF.contains("x-zip")||fileF.contains(".sheet")||fileF.contains(".ms-excel") ? "docFile" : "otherFile";

                    String fileZ = filePathing.equals("imgFile") ? fileF.split("/")[1] :
                            filePathing.equals("docFile") && fileF.contains(".document") ? "docx" :
                                    filePathing.equals("docFile") && fileF.contains(".sheet") ? "xlsx" :
                                            filePathing.equals("docFile") && fileF.contains(".ms-excel") ? "xls" :
                                                    filePathing.equals("docFile") && fileF.contains(".pdf") ? "pdf" :
                                                            filePathing.equals("docFile") && fileF.contains("text/plain") ? "txt":
                                                                    filePathing.equals("docFile") && fileF.contains("x-zip") ? "zip" :
                                                                            fileF.contains("audio/wav") ? "wav" :
                                                                                    fileF.contains("audio/mpeg") ? "mp3" :"";

                    if (!fileZ.equals("")) {
                        filePath = defaultFilePath + filePath + separator + filePathing;
                        File file1 = new File(filePath, "");
                        if (!file1.exists()) {
                            file1.setWritable(true, false);
                            file1.mkdirs();
                        }
                        try {
                            fileName = new Date().getTime() + "_" + new Random().nextInt(1000) + "." + fileZ;
                            BufferedOutputStream buffStream =
                                    new BufferedOutputStream(new FileOutputStream(new File(filePath + separator + fileName)));
                            buffStream.write(bytes);
                            buffStream.close();
                            imgList.add(new SimpleDateFormat("yyyyMMdd").format(new Date())
                                    + separator + filePathing + separator + fileName);
                        } catch (Exception e) {
                            logger.error("fileHandler:uploadList: ->  error : " + e.getMessage() + " / " + e.getCause().getMessage());
                        }
                        filePath = fp;
                    }
                }
            }
        }
        return JSON.toJSONString(imgList);
    }

    /**
     * 根据文件名称获取指定的文件流信息
     * @param response
     * @param fileName   文件名称
     * @throws IOException
     */
    public void getFile(HttpServletResponse response, String fileName) throws IOException {
        ServletOutputStream out = null;
        FileInputStream ips = null;
        try {
            //获取图片存放路径
            String imgPath = defaultFilePath + File.separator +  fileName;
            ips = new FileInputStream(new File(imgPath));
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            response.setContentType("multipart/form-data");
            out = response.getOutputStream();
            //读取文件流
            int len = 0;
            byte[] buffer = new byte[1024 * 10];
            while ((len = ips.read(buffer)) != -1){
                out.write(buffer,0,len);
            }
            out.flush();
        }catch (Exception e){
            logger.error("fileHandler:getFileList: ->  error : " + e.getMessage() + " / " + e.getCause().getMessage());
        }finally {
            out.close();
            ips.close();
        }
    }
}
