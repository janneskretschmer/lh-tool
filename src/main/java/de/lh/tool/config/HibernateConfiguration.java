package de.lh.tool.config;

import java.util.Properties;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("de.lh.tool.repository")
@PropertySource(value = { "classpath:credentials.properties", "classpath:hibernate.properties" })
public class HibernateConfiguration {

	@Autowired
	private Environment environment;

	@Bean(initMethod = "migrate")
	public Flyway flyway() {
		Flyway flyway = new Flyway();
		flyway.setDataSource(getDataSource());
		flyway.setLocations("classpath:dbscripts/incremental");
		return flyway;
	}

	@Bean(name = "entityManagerFactory")
	@DependsOn("flyway")
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(getJpaVendorAdapter());
		localContainerEntityManagerFactoryBean.setDataSource(getDataSource());
		localContainerEntityManagerFactoryBean.setPersistenceUnitName("jpaPersistenceUnit");
		localContainerEntityManagerFactoryBean.setPackagesToScan("de.lh.tool");
		localContainerEntityManagerFactoryBean.setJpaProperties(getHibernateProperties());
		return localContainerEntityManagerFactoryBean;
	}

	@Bean
	public JpaVendorAdapter getJpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager getPlatformTransactionManager() {
		return new JpaTransactionManager(getEntityManagerFactoryBean().getObject());
	}

	@Bean
	public DataSource getDataSource() {
		TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
		dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
		dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
		return dataSource;
	}

	private Properties getHibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
		properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
		properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
		properties.put("spring.jpa.hibernate.ddl-auto", false);
		return properties;
	}

}