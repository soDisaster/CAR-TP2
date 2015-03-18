package com.example.config;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.example.rs.JaxRsApiApplication;
import com.rest.RessourcesFichiers;

@Configuration
public class AppConfig {	
	@Bean( destroyMethod = "shutdown" )
	public SpringBus cxf() {
		return new SpringBus();
	}
	
	@Bean @DependsOn( "cxf" )
	public Server jaxRsServer() throws UnknownHostException, IOException {
		JAXRSServerFactoryBean factory = RuntimeDelegate.getInstance().createEndpoint( jaxRsApiApplication(), JAXRSServerFactoryBean.class );
		
		List<Object> serviceBeans = new ArrayList<Object>();
		serviceBeans.add(new RessourcesFichiers());
		
		factory.setServiceBeans(serviceBeans);
		factory.setAddress( "/" + factory.getAddress() );
		factory.setProviders( Arrays.< Object >asList( jsonProvider() ) );
		return factory.create();
	}
	
	@Bean 
	public JaxRsApiApplication jaxRsApiApplication() {
		return new JaxRsApiApplication();
	}
		
	@Bean
	public JacksonJsonProvider jsonProvider() {
		return new JacksonJsonProvider();
	}
}
