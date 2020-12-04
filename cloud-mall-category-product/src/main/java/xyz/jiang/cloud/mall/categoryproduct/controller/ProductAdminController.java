package xyz.jiang.cloud.mall.categoryproduct.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.jiang.cloud.mall.categoryproduct.common.ProductConstant;
import xyz.jiang.cloud.mall.categoryproduct.model.pojo.Product;
import xyz.jiang.cloud.mall.categoryproduct.model.request.AddProductReq;
import xyz.jiang.cloud.mall.categoryproduct.model.request.UpdateProductReq;
import xyz.jiang.cloud.mall.categoryproduct.service.ProductService;
import xyz.jiang.cloud.mall.common.common.ApiRestResponse;
import xyz.jiang.cloud.mall.common.common.Constant;
import xyz.jiang.cloud.mall.common.exception.MallException;
import xyz.jiang.cloud.mall.common.exception.MallExceptionEnum;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * @Program: mall
 * @Classname ProductAdminController
 * @Description: 后台商品管理Controller
 * @Author: JiangKan
 * @Create: 2020-10-11
 **/
@RestController
public class ProductAdminController {

    @Resource
    private ProductService productService;

    @Value("${file.upload.ip}")
    String ip;

    @Value("{file.upload.port}")
    Integer port;

    @PostMapping("/admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq){
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }



    @PostMapping("admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称uuid
        UUID uuid = UUID.randomUUID();
        String newFileName =  uuid.toString() +suffixName;
        File fileDirectory = new File(ProductConstant.FILE_UPLOAD_DIR);
        File destFile = new File(ProductConstant.FILE_UPLOAD_DIR + newFileName);
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new MallException(MallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL() + "")) + "/category-product/images/"+newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(MallExceptionEnum.UPLOAD_FAILED);
        }
    }

    private URI getHost(URI uri) {
        System.out.println(uri.toString());
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), ip, port, null, null,null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @ApiOperation("后台更新商品")
    @PostMapping("/admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestParam UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq,product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    public ApiRestResponse deleteProduct(Integer id) {
        productService.delete(id);

        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品批量上下架")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表接口")
    @PostMapping("/admin/product/list")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo  = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

}
