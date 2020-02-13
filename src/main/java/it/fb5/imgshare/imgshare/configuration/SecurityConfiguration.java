package it.fb5.imgshare.imgshare.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import it.fb5.imgshare.imgshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserRepository userRepository;

    @Autowired
    public SecurityConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.headers()
                .frameOptions().sameOrigin();

        http.authorizeRequests()
                .antMatchers("/upload", "/me/**", "/a/create", "/a/*/edit").authenticated()
                .antMatchers("/login").permitAll()
                .anyRequest().permitAll();

        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login?error=true");

        http.logout()
                .logoutSuccessUrl("/");

        http.formLogin(withDefaults()).httpBasic(withDefaults());
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return userInfo -> new SecurityPrincipal(userInfo.matches(
                "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$")
                ?
                this.userRepository.findByEmail(userInfo).orElseThrow(() -> {
                    throw new UsernameNotFoundException("User not found");
                }) :
                this.userRepository.findByUsername(userInfo).orElseThrow(() -> {
                    throw new UsernameNotFoundException("User not found");
                }));
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        var handler = new SavedRequestAwareAuthenticationSuccessHandler();

        handler.setUseReferer(true);
        handler.setTargetUrlParameter("redirect");
        handler.setDefaultTargetUrl("/");

        return handler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
