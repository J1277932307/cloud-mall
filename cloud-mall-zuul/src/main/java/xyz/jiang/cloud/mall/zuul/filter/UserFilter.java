package xyz.jiang.cloud.mall.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import xyz.jiang.cloud.mall.common.common.Constant;
import xyz.jiang.cloud.mall.user.model.pojo.User;
import xyz.jiang.cloud.mall.zuul.feign.UserFeignClient;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Program: cloud-mall
 * @Classname UserFilter
 * @Description: TODO
 * @Author: JiangKan
 * @Create: 2020-11-22
 **/

@Component
public class UserFilter extends ZuulFilter {


    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();
        if (requestURI.contains("images") || requestURI.contains("pay")) {
            return false;
        }
        if (requestURI.contains("cart") || requestURI.contains("order")) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //进行登录检验
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request  = context.getRequest();
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute(Constant.MALL_USER);
        if (user == null) {
            context.setSendZuulResponse(false);
            context.setResponseBody("{\n"
                    + "    \"status\": 10007,\n"
                    + "    \"msg\": \"NEED_LOGIN\",\n"
                    + "    \"data\": null\n"
                    + "}");
            context.setResponseStatusCode(200);
        }


        return null;
    }
}
