package bookinfo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SpringDocConfiguration {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(
            new Info()
                .title("백엔드 실습 과제에서 정의한 REST API")
                .description("스프링 부트 기반의 도서 정보 REST API")
                .version("v1.0")
            );
    }
}
