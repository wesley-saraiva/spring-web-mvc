package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private ImplementacaoUserDetailsService implementacaoUserDetailsService;

    @Override // Configura as solicitações de acessso http
    protected void configure(HttpSecurity http) throws Exception {
	http.csrf().disable() // Desativa as configurações padrão de memoria.
		.authorizeRequests() // Permitir restringir acessos
		.antMatchers(HttpMethod.GET, "/").permitAll() // Qualquer usuario acessa o sistema
		.antMatchers("**/materialize/**").permitAll().antMatchers(HttpMethod.GET, "/cadastropessoa")
		.hasAnyRole("ADMIN").anyRequest().authenticated().and().formLogin().permitAll() // permite qualquer
												// usuario
		.loginPage("/login").defaultSuccessUrl("/cadastropessoa").failureUrl("/login?error=true").and().logout()
		.logoutSuccessUrl("/login")
		// Mapeia URL de Logout e invalida usuario autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
    }

    @Override // Cria autenticação do usuario com banco de dados ou em memoria
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

	auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());

//		auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
//		.withUser("wesley")
//		.password("$2a$10$9i/ll/GMZoXEbpbOZtnnTOk8ISzhjK.FxprxswVOWfUCsfjs9vl4i")
//		.roles("ADMIN");
    }

    @Override

    public void configure(WebSecurity web) throws Exception {

	web.ignoring().antMatchers("/materialize/**");

    }

}
