package xyz.jiang.cloud.mall.categoryproduct.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Program: cloud-mall
 * @Classname ProductConstant
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-11-29
 **/
@Component
public class ProductConstant {
    //图片文件上传目录
    public static String FILE_UPLOAD_DIR;
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir){
        FILE_UPLOAD_DIR = fileUploadDir;
    }
}
