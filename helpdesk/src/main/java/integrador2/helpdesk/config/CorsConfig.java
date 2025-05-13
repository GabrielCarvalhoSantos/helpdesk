package integrador2.helpdesk.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    @Value("${frontend.origin}")          // valor resolvido na etapa 1
    private String frontendOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")        // todas as rotas da API
                .allowedOrigins(frontendOrigin)      // ex.: http://localhost:3000
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);             // envia cookie/JWT se precisar
    }
}
