package hexlet.code.config.rollbar;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "hexlet.code"
})
public class RollbarConfig {

    // Добавляем токен через переменные окружения
    @Value("${rollbar_token:}")
    private String rollbarToken;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public Rollbar rollbar() {

        Rollbar rollbar = new Rollbar(getRollbarConfigs(rollbarToken));

        return rollbar;
    }

    private Config getRollbarConfigs(String accessToken) {

        return RollbarSpringConfigBuilder.withAccessToken(accessToken)
                .environment("development")
                .enabled(activeProfile.equals("prod"))
                .build();
    }

}
