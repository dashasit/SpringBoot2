package com.intelligrated.generic.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligrated.crypto.Crypto;
import com.intelligrated.crypto.exceptions.CryptoException;
import com.intelligrated.generic.impl.GateWay;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
/**
 * Created by sachin.subhedar on 01/19/2017.
 * <p>
 * Copyright (c) 2001-2016 Intelligrated [https://www.intelligrated.com/]
 * <p>
 * The  information  contained  herein  is  the  confidential  and  proprietary
 * information of Intelligrated.  This information is protected,  among others,
 * by the patent,  copyright,  trademark,  and trade secret laws of  the United
 * States and its several states.  Any use,  copying, or reverse engineering is
 * strictly prohibited. This software has been developed at private expense and
 * accordingly,  if used under Government  contract,  the use,  reproduction or
 * disclosure  of  this  information  is subject to  the restrictions set forth
 * under the  contract between  Intelligrated  and its customer.  By viewing or
 * receiving this information, you consent to the foregoing.
 */

@Configuration
@ComponentScan(basePackages = "com.intelligrated.generic.*")
@EnableTransactionManagement
@PropertySource("file:/${INTELLIGRATED_HOME}/common/config/database.properties")
@PropertySource("file:/${INTELLIGRATED_HOME}/common/config/generic.properties")
public class GeneralConfiguration {

    @Value("${dataSource.driverClass}")
    private String driverClassName;
    @Value("${dataSource.jdbcUrl}")
    private String jdbcUrl;
    @Value("${dataSource.user}")
    private String user;
	@Value("${dataSource.encryptedPassword}")
	private String encryptedPassword;
    @Value("${dataSource.minPoolSize}")
    private int minPoolSize;
    @Value("${dataSource.maxPoolSize}")
    private int maxPoolSize;
    @Value("${hibernate.dialect}")
    private String hibernateDialect;
    @Value("${hibernate.show_sql}")
    private String hibernateShowSql;
    @Value("${hibernate.format_sql}")
    private String hibernateFormatSql;
	@Value("${dataSource.testConnectionOnCheckin:false}")
	private boolean testConnectionOnCheckin;
	@Value("${dataSource.testConnectionOnCheckout:false}")
	private boolean testConnectionOnCheckout;
	@Value("${dataSource.preferredTestQuery:}")
	private String preferredTestQuery;


	private final Logger logger = LogManager.getLogger();

    @Autowired
    Environment env;

	@Autowired
	Crypto crypto;

    @Bean(name = "dataSource")
    public DataSource getDataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClassName);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(user);
		try {
			dataSource.setPassword(crypto.decrypt(encryptedPassword));
			//IMPORTANT: do NOT log the decrypted password
		} catch (CryptoException cryptoException) {
			logger.error("Could not decrypt DB password {}. Exception {}", encryptedPassword, cryptoException);
			//TODO: send event to Alarm and Events and generate notification
		}        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setMaxPoolSize(maxPoolSize);
        return dataSource;
    }

    @Bean(name = "gateWay")
    public GateWay getGateWay() {
        return new GateWay();
    }

    @Bean(name = "objectMapper")
    public ObjectMapper getObjectMapper() {
		return new HibernateAwareObjectMapper();
	}

	@Autowired
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		logger.info("restTemplate bean @LoadBalanced and @Autowired");
		final RestTemplate restTemplate = new RestTemplate();

		// find and replace Jackson message converter with our own
		for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
			final HttpMessageConverter<?> httpMessageConverter = restTemplate.getMessageConverters().get(i);
			if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
				restTemplate.getMessageConverters().set(i, converter());
			}
		}
		return restTemplate;
	}

	@Bean
	public MappingJackson2HttpMessageConverter converter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

		converter.setObjectMapper(new HibernateAwareObjectMapper());

		return converter;
	}

	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) throws PropertyVetoException {
		
		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);

		sessionBuilder.scanPackages("com.intelligrated.generic.entities");
		sessionBuilder.addProperties(getHibernateProperties());

        return sessionBuilder.buildSessionFactory();
	}

	@Autowired
	@Bean(name="transactionManager")
	public JpaTransactionManager getTransactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	@Autowired
	@Bean(name="jpaVendorAdapter")
	public HibernateJpaVendorAdapter getHibernateJpaVendorAdapter(DataSource dataSource){
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		try {
			BeanUtils.populate(hibernateJpaVendorAdapter, BeanUtils.describe(dataSource));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
		    logger.error("Failed while trying to Autowire jpaVendorAdapter {}",e);
			e.printStackTrace();
		}
        return hibernateJpaVendorAdapter;
	}

	@Autowired
	@Bean(name="persistenceUnitManager")
	public PersistenceUnitManager getPersistenceUnitManager(DataSource dataSource , HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
		DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
		persistenceUnitManager.setDefaultDataSource(dataSource);
		persistenceUnitManager.setPackagesToScan("com.intelligrated.generic.entities");
		return persistenceUnitManager;
	}

	@Autowired
	@Bean(name="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactory(DataSource dataSource, HibernateJpaVendorAdapter
			jpaVendorAdapter, PersistenceUnitManager persistenceUnitManager) {
		EntityManagerFactoryBuilder entityManagerFactoryBuilder = new EntityManagerFactoryBuilder(jpaVendorAdapter,
				getJpaProperties(), persistenceUnitManager);
		return entityManagerFactoryBuilder
				.dataSource(dataSource)
				.packages("com.intelligrated.generic.entities")
				.properties(getJpaProperties())
				.build();
	}

	@Bean
	 public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	 return new PropertySourcesPlaceholderConfigurer();
	 }

	@Autowired
	@Bean
	public Logger getLogger() {
		return LogManager.getLogger();
	}

	private Properties getHibernateProperties() {
		Properties properties = new Properties();

		properties.put("jadira.usertype.autoRegisterUserTypes", "true");
		properties.put("jadira.usertype.databaseZone", "UTC");
		properties.put("jadira.usertype.javaZone", "UTC");

		properties.put("hibernate.show_sql", hibernateShowSql);
		properties.put("hibernate.format_sql", hibernateFormatSql);
		properties.put("hibernate.dialect", hibernateDialect);

		return properties;
	}

	private Map<String, ?> getJpaProperties() {
		Map<String, String> jpaProperty = new HashMap<>();
		Properties hibernateProperties = getHibernateProperties();
		for(String propertyName : hibernateProperties.stringPropertyNames()) {
			jpaProperty.put(propertyName, hibernateProperties.getProperty(propertyName));
		}
		return jpaProperty;
	}
}
