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
public class AdminFilter extends ZuulFilter {

    @Resource
    private UserFeignClient userFeignClient;
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
        if (requestURI.contains("adminLogin")) {
            return false;
        }
        if (requestURI.contains("admin")) {
            return true;
        }

        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //检验是否是管理员
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request  = context.getRequest();
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute(Constant.MALL_USER);
        System.out.println(user.getUsername());
        if (user == null) {
            context.setSendZuulResponse(false);
            context.setResponseBody("{\n"
                    + "    \"status\": 10007,\n"
                    + "    \"msg\": \"NEED_LOGIN\",\n"
                    + "    \"data\": null\n"
                    + "}");
            context.setResponseStatusCode(200);
        }

        //检验是否是管理员
        Boolean adminRole = userFeignClient.checkAdminRole(user);
        if (!adminRole) {
                context.setSendZuulResponse(false);
                context.setResponseBody("{\n"
                        + "    \"status\": 10009,\n"
                        + "    \"msg\": \"NEED_ADMIN\",\n"
                        + "    \"data\": null\n"
                        + "}");
        }
        return null;
    }
}
