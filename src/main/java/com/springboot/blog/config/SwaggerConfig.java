package com.springboot.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;



@Configuration
@EnableSwagger2
public class SwaggerConfig  
{
	// 헤더의 Authorization 을 통해 JWT를 얻을 수 있다.
	public static final String AUTHORIZATION_HEADER= "Authorization";
	
	private ApiKey apiKey() {
		return new ApiKey("JWT", AUTHORIZATION_HEADER,"header");
	}
	
	private ApiInfo apiInfo()
	{
		String description = "Spring Boot Blog REST API Documentation";
		return new ApiInfoBuilder()
				.title("Spring Boot Blog REST APIs")
				.description(description)
				.version("1")
				.termsOfServiceUrl("Terms of service")
				.contact(new Contact("javapp","/javapp.tistory.com","javapp@naver.com"))
				.license("License of API")
				.licenseUrl("API license URL")
				.extensions(Collections.emptyList())
				.build();
	}
	
	@Bean
	public Docket api()
	{
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.securityContexts(Arrays.asList(securityContext())) //jwt 권한 설정
				.securitySchemes(Arrays.asList(apiKey())) //jwt 권한 설정
				.select() // ApiSelectorBuilder 를 생성
				.apis(RequestHandlerSelectors.any()) //api 스펙이 작성되어 있는 패키지 지정
				.paths(PathSelectors.any()) // apis()로 선택되어진 api 중 특정 path 조건에 맞는 api들을 다시 필터링하여 문서화 
				.build();
	}
	
    private SecurityContext securityContext(){
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth(){
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
	
}
