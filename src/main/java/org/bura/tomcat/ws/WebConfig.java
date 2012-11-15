package org.bura.tomcat.ws;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


@Configuration
@EnableWebMvc
@ImportResource("classpath*:META-INF/spring/spring-security-context.xml")
@ComponentScan("org.bura.tomcat.ws.component")
public class WebConfig extends WebMvcConfigurerAdapter {

	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(templateEngine());
		thymeleafViewResolver.setCharacterEncoding("UTF-8");

		return thymeleafViewResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
		springTemplateEngine.setTemplateResolver(servletContextTemplateResolver());

		return springTemplateEngine;
	}

	@Bean
	public ServletContextTemplateResolver servletContextTemplateResolver() {
		ServletContextTemplateResolver servletContextTemplateResolver = new ServletContextTemplateResolver();
		servletContextTemplateResolver.setPrefix("/WEB-INF/view/");
		servletContextTemplateResolver.setCacheable(false);
		servletContextTemplateResolver.setCharacterEncoding("UTF-8");
		servletContextTemplateResolver.setTemplateMode("HTML5");

		return servletContextTemplateResolver;
	}

}
