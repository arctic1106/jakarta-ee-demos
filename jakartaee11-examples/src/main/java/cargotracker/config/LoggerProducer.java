package cargotracker.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

import java.util.logging.Logger;

@ApplicationScoped
public class LoggerProducer {

    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(extractLoggerName(injectionPoint));
    }

    private String extractLoggerName(InjectionPoint injectionPoint) {
        if (injectionPoint.getBean() == null) return injectionPoint.getMember().getDeclaringClass().getName();
        if (injectionPoint.getBean().getName() == null) return injectionPoint.getBean().getBeanClass().getName();
        return injectionPoint.getBean().getName();
    }
}