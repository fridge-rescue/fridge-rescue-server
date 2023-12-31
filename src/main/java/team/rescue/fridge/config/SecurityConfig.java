package team.rescue.fridge.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import team.rescue.fridge.handler.AuthenticationExceptionHandler;
import team.rescue.fridge.handler.AuthorizationExceptionHandler;
import team.rescue.fridge.oauth.handler.OAuth2FailureHandler;
import team.rescue.fridge.oauth.handler.OAuth2SuccessHandler;
import team.rescue.fridge.oauth.service.CustomOAuth2UserService;
import team.rescue.fridge.security.JwtAuthenticationFilter;
import team.rescue.fridge.security.JwtAuthorizationFilter;
import team.rescue.fridge.security.MemberDetailService;
import team.rescue.fridge.security.RedisUtil;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2UserService oAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;
	private final RedisUtil redisUtil;
	private final PasswordEncoder passwordEncoder;
	private final MemberDetailService memberDetailService;
	private final ObjectMapper objectMapper;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement((sessionManagement) ->
						sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.oauth2Login(oauth2Login ->
						oauth2Login.userInfoEndpoint(userInfoEndpointConfig ->
										userInfoEndpointConfig.userService(oAuth2UserService))
								.successHandler(oAuth2SuccessHandler)
								.failureHandler(oAuth2FailureHandler))
				.headers((headerConfig) ->
						headerConfig.frameOptions(FrameOptionsConfig::disable)
				) // h2-console 화면을 사용하기 위해 iframe 비허용 처리
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS
				.logout(withDefaults())
				.authorizeHttpRequests((authorizeRequests) ->
								authorizeRequests
										.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
										.requestMatchers(PathRequest.toH2Console()).permitAll() // h2 콘솔
										.requestMatchers("/", "/login/**").permitAll()  // 메인 화면, 로그인 화면, url path가 구체적으로 나오면 다시 수정.
//                .requestMatchers("/post/**").hasRole(Role.USER.name)  // post관련 요청은 User role만. 나중에 Role을 추가하면 추가하기로.
										.anyRequest().authenticated()
				);
		http.apply(new CustomSecurityFilterManager());

		// exceptionHandling 추가
		http
				.exceptionHandling(exceptionHandler -> {
					exceptionHandler.authenticationEntryPoint(
							new AuthenticationExceptionHandler()); // 인증 실패(401)
					exceptionHandler.accessDeniedHandler(
							new AuthorizationExceptionHandler()); // 인가(권한) 오류(403)
				});

		return http.build();
	}

	CorsConfigurationSource corsConfigurationSource() {
		log.debug("[+] corsConfigurationSource 등록");

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));  // 나중에 프론트 출처만 허용해야함
		configuration.setAllowedMethods(List.of("*"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);  //클라이언트가 쿠키나 인증 관련 헤더를 사용하여 요청을 할 수 있도록 허용

		// 모든 주소요청에 적용
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	AuthenticationManager authenticationManager() {
		log.debug("[+] AuthenticationManager 등록");
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(memberDetailService);
		provider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(provider);
	}

	public class CustomSecurityFilterManager
			extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
		@Override
		public void configure(HttpSecurity builder) throws Exception {
			AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
			builder.addFilter(new JwtAuthenticationFilter(authenticationManager, objectMapper, redisUtil));
			builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
			super.configure(builder);
		}
	}
}
