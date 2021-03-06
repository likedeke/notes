package com.like.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author like
 * @email 980650920@qq.com
 * @since 2021-03-28 16:48
 */
@Configuration
public class SecurityConfigWithUserDetailsService extends WebSecurityConfigurerAdapter {

    @Resource
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    /**
     * 持续的令牌库 remember me
     * @return {@link PersistentTokenRepository}
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
    /**
     * 配置 使用自定义的service
     * @param auth 身份验证
     * @throws Exception 异常
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 配置 登录页面 以及 允许哪些请求可以不用通过登录就可以访问
     * @param http http
     * @throws Exception 异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().accessDeniedPage("/noAuth.html");  // 没有权限访问 跳转的一个url
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/index").permitAll();  // 退出访问url 成功跳转页面

        http.formLogin()
            .loginPage("/login.html")                              // 自定义登录的页面
            .loginProcessingUrl("/user/login")                    // 登录信息提交到哪个controller 具体逻辑不用管
            .defaultSuccessUrl("/loginSuccess.html").permitAll()             // 登录成功只有，跳转路径
            .and().authorizeRequests()
            .antMatchers("/", "/noauth").permitAll() // 访问这些路径不需要认证
            .antMatchers("/adminOnly").hasAuthority("admin")
            .antMatchers("/adminAndRole").hasAnyAuthority("admin,role")  // 使用多个权限
            .antMatchers("/producer").hasRole("producer")  // 是producer 这个角色才可以访问
            .antMatchers("/gamer").hasRole("gamer")  // 是gamer 这个角色才可以访问
            .anyRequest().authenticated()
            .and().rememberMe().tokenRepository(persistentTokenRepository()).tokenValiditySeconds(60).userDetailsService(userDetailsService)
            .and().csrf().disable();             // 关闭csrf 防护

    }

    /**
     * 在ioc中添加密码编码器
     * @return {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
