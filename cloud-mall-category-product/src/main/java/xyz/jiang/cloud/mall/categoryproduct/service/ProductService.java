package xyz.jiang.cloud.mall.categoryproduct.service;

import com.github.pagehelper.PageInfo;
import xyz.jiang.cloud.mall.categoryproduct.model.pojo.Product;
import xyz.jiang.cloud.mall.categoryproduct.model.request.AddProductReq;
import xyz.jiang.cloud.mall.categoryproduct.model.request.ProductListReq;

/**
 * @Program: mall
 * @Classname ProductService
 * @Description: 商品服务
 * @Author: JiangKan
 * @Create: 2020-10-11
 **/
public interface ProductService {
    public void add(AddProductReq addProductReq);

    void update(Product updateProduct);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);

    public void updateStock(Integer productId, Integer stock);
}
