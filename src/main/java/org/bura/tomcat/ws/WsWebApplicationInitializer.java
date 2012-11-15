package org.bura.tomcat.ws;


import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;


public class WsWebApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) throws ServletException {
		// spring
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(WebConfig.class);
		container.addListener(new ContextLoaderListener(rootContext));

		// spring security
		FilterRegistration fr = container.addFilter("securityFilter", new DelegatingFilterProxy(
				"springSecurityFilterChain"));
		fr.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "*");
		container.addListener(new HttpSessionEventPublisher());

		// websockets
		ServletRegistration.Dynamic ws = container.addServlet("ws", new DispatcherWebsocketServlet(rootContext));
		ws.setLoadOnStartup(1);
		ws.addMapping("/ws/*");

		// mvc
		ServletRegistration.Dynamic mvc = container.addServlet("mvc", new DispatcherServlet(rootContext));
		mvc.setLoadOnStartup(2);
		mvc.addMapping("/*");
	}

}
