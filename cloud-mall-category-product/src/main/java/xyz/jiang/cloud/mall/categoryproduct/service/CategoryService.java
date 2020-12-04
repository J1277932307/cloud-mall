package xyz.jiang.cloud.mall.categoryproduct.service;

import com.github.pagehelper.PageInfo;
import xyz.jiang.cloud.mall.categoryproduct.model.VO.CategoryVO;
import xyz.jiang.cloud.mall.categoryproduct.model.pojo.Category;
import xyz.jiang.cloud.mall.categoryproduct.model.request.AddCategoryReq;


import java.util.List;

/**
 * 描述： 分类目录 Service
*/
public interface CategoryService {
    public void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listCategoryForCustomer(Integer parentId);
}
