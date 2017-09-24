package com.springms.cloud.feign;

import com.springms.cloud.entity.User;
import com.springms.config.TestFeignCustomConfiguration;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 用户Http请求的客户端，FeignClient 注解地方采用了自定义的配置。
 *
 * 注解FeignClient的传参：表示的是注册到 Eureka 服务上的模块名称。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/9/24
 *
 */
@FeignClient(name = "springms-provider-user", configuration = TestFeignCustomConfiguration.class, fallback = UserFeignCustomClientFallback.class)
public interface UserFeignCustomClient {

    /**
     * 这里的注解 RequestLine、Param 是 Feign 的配置新的注解，详细请参考链接：https://github.com/OpenFeign/feign
     *
     * @param id
     * @return
     */
    @RequestLine("GET /simple/{id}")
    public User findById(@Param("id") Long id);
}


/****************************************************************************************
 参考代码如下：

    interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    static class Contributor {
        String login;
        int contributions;
    }

    public static void main(String... args) {
        GitHub github = Feign.builder().decoder(new GsonDecoder()).target(GitHub.class, "https://api.github.com");

        // Fetch and print a list of the contributors to this library.
        List<Contributor> contributors = github.contributors("OpenFeign", "feign");
        for (Contributor contributor : contributors) {
            System.out.println(contributor.login + " (" + contributor.contributions + ")");
        }
    }
 ****************************************************************************************/