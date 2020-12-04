package xyz.jiang.cloud.mall.categoryproduct.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.jiang.cloud.mall.categoryproduct.model.VO.CategoryVO;
import xyz.jiang.cloud.mall.categoryproduct.model.pojo.Category;
import xyz.jiang.cloud.mall.categoryproduct.model.request.AddCategoryReq;
import xyz.jiang.cloud.mall.categoryproduct.model.request.UpdateCategoryReq;
import xyz.jiang.cloud.mall.categoryproduct.service.CategoryService;
import xyz.jiang.cloud.mall.common.common.ApiRestResponse;
import xyz.jiang.cloud.mall.common.exception.MallExceptionEnum;


import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * @Program: mall
 * @Classname CategoryController
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-10-07
 **/
@Controller
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @ApiOperation("后台添加目录")
    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryReq addCategoryReq) {

        categoryService.add(addCategoryReq);
        return ApiRestResponse.success();

    }

    @ApiOperation("后台更新目录")
    @PostMapping("/admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(HttpSession session, @Valid @RequestBody UpdateCategoryReq updateCategoryReq) {

        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReq,category);
        categoryService.update(category);
        return ApiRestResponse.success();

    }

    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("管理员获取后台目录列表")
    @PostMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("用户获取后台目录列表")
    @PostMapping("/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer() {
        List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOS);
    }

}
