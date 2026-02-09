package com.example.Recu_RA3.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@EnableJdbcHttpSession(
        maxInactiveIntervalInSeconds = 1800 // 30 minutos de inactividad antes de cerrar sesión
)
public class SessionConfig {

}