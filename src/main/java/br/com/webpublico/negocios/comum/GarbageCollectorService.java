package br.com.webpublico.negocios.comum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class GarbageCollectorService {
    public static final String ATIVAR_OTIMIZACAO_EXPERIMENTAL = "ATIVAR_OTIMIZACAO_EXPERIMENTAL";
    private static final Logger logger = LoggerFactory.getLogger(GarbageCollectorService.class);


    public static void startGarbageColletor() {
        startGarbageColletor(true);
    }


    public static void startGarbageColletor(boolean verificarFlagOtimizacao) {
        if (isUnlockedLabs(verificarFlagOtimizacao)) {
            logger.debug("Executando garbage collector");
            System.gc();
        }
    }

    public static boolean isUnlockedLabs(boolean isEnvironmentEnable) {
        if (isEnvironmentEnable) {
            String webpublicoExperimental = System.getenv(ATIVAR_OTIMIZACAO_EXPERIMENTAL);
            if (webpublicoExperimental == null) {
                return false;
            }
            return Boolean.parseBoolean(webpublicoExperimental);
        } else {
            return true;
        }

    }

    public static void updateEnviroment(boolean value) {
        setEnv("ATIVAR_OTIMIZACAO_EXPERIMENTAL", value + "");
    }

    public static void setEnv(String key, String value) {
        try {
            Map<String, String> env = System.getenv();
            Class<?> cl = env.getClass();
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> writableEnv = (Map<String, String>) field.get(env);
            writableEnv.put(key, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set environment variable", e);
        }
    }
}
