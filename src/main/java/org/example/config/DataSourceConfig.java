package org.example.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.MailSessionDefinition;

@MailSessionDefinition(
    name = "java:app/mail/tiendaMailSession",
    host = "smtp.serviciodecorreo.es",      // CAMBIAR por el host real (ej: smtp.gmail.com o mail.debatesysolidaridad.org)
    user = "administracion@debatesysolidaridad.org",     // REQUERIDO: Tu dirección de correo o usuario SMTP
    password = "Makoki1", // REQUERIDO: Tu contraseña o clave de aplicación
    transportProtocol = "smtp",
    properties = {
        "mail.smtp.port=587",           // Puerto común para STARTTLS: 587. Para SSL/TLS: 465.
        "mail.smtp.auth=true",          // Activar si el servidor requiere usuario y contraseña
        "mail.smtp.starttls.enable=true", // Recomendado para seguridad
        "mail.smtp.ssl.trust=*",        // Opcional: confiar en todos los certificados (usar con precaución)
        "mail.smtp.timeout=5000",
        "mail.smtp.connectiontimeout=5000"
    }
)
@DataSourceDefinition(
    name = "java:app/jdbc/tiendaDebatesDS",
    className = "com.mysql.cj.jdbc.MysqlDataSource",
    url = "jdbc:mysql://127.0.0.1:3306/tiendadebates",
    user = "anticapitalistas",
    password = "Luxemburgo15@",
    properties = {
        "useSSL=false",
        "allowPublicKeyRetrieval=true",
        "serverTimezone=UTC",
        "characterEncoding=UTF-8",
        "useUnicode=true"
    }
)
@ApplicationScoped
public class DataSourceConfig {
}
