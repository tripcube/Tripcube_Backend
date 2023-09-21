package uos.ac.kr.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfig {
    // http://localhost:8080/swagger-ui/index.html

    private static final String REFERENCE = "Authorization 헤더 값";
    @Bean
    public Docket swagger() {
        return new Docket(DocumentationType.SWAGGER_2)
                .forCodeGeneration(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("uos.ac.kr.controllers"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .apiInfo(apiInfo())
                .enable(true)
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(securityScheme()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("테스트 API 타이틀")
                .description("테스트 API 상세소개 및 사용법 등")
                .contact(new Contact("mile", "milenote.tistory.com", "skn@futurenuri.com"))
                .version("1.0")
                .build();
    }

    private SecurityContext securityContext() {
        return springfox.documentation
                .spi.service.contexts
                .SecurityContext
                .builder()
                .securityReferences(defaultAuth())
                .operationSelector(operationContext -> true)
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = new AuthorizationScope("global", "accessEverything");
        return List.of(new SecurityReference(REFERENCE, authorizationScopes));
    }

    private ApiKey securityScheme() {
        String targetHeader = "Authorization";
        return new ApiKey(REFERENCE, targetHeader, "header");
    }

}
