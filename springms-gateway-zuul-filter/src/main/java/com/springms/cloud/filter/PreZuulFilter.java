package com.springms.cloud.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * zuul 的过滤器。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/26
 *
 */
public class PreZuulFilter extends ZuulFilter{

    private static final Logger Logger = LoggerFactory.getLogger(PreZuulFilter.class);

    /**
     * 前置过滤器。
     *
     * 但是在 zuul 中定义了四种不同生命周期的过滤器类型：
     *
     *      1、pre：可以在请求被路由之前调用；
     *
     *      2、route：在路由请求时候被调用；
     *
     *      3、post：在route和error过滤器之后被调用；
     *
     *      4、error：处理请求时发生错误时被调用；
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤的优先级，数字越大，优先级越低。
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 1;
    }

    /**
     * 是否执行该过滤器。
     *
     * true：说明需要过滤；
     *
     * false：说明不要过滤；
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return false;
    }

    /**
     * 过滤器的具体逻辑。
     *
     * @return
     */
    @Override
    public Object run() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String host = request.getRemoteHost();
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("                    请求的host:{}                          ", host);
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Logger.info("<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return null;
    }
}
