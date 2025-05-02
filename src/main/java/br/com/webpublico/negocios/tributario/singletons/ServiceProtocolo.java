package br.com.webpublico.negocios.tributario.singletons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Daniel Franco
 * @since 21/01/2015 12:32
 */
public class ServiceProtocolo
//        implements Service<Integer>
{

    private static final Logger logger = LoggerFactory.getLogger(ServiceProtocolo.class);
//    public static final ServiceName SINGLETON_SERVICE_NAME = ServiceName.JBOSS.append("webpublico", "singleton", "protocolo");

    /**
     * A flag whether the service is started.
     */
    public ServiceProtocolo(Integer numero) {
        this.numero = numero;
    }

    private final AtomicBoolean started = new AtomicBoolean(false);

    private Integer numero = null;

//    private final InjectedValue<ServerEnvironment> env = new InjectedValue<ServerEnvironment>();
//
//    public Injector<ServerEnvironment> getEnvInjector() {
//        return this.env;
//    }

    /**
     * @return the name of the server node
     */
    public Integer getValue() throws IllegalStateException, IllegalArgumentException {
        if (!started.get()) {
            throw new IllegalStateException("O Service [" + this.getClass().getName() + "] Não está Inicializado");
        }
        System.out.println("vai passar aqui... ");
        System.out.println("numero -----------> " + this.numero);
        return ++this.numero;
    }

//    public void start(StartContext arg0) throws StartException {
//        if (!started.compareAndSet(false, true)) {
//            throw new StartException("O Service ]" + this.getClass().getName() + "] já está inicializado");
//        }
//        LOGGER.debug("Iniciando service [" + this.getClass().getName() + "]");
//    }
//
//    public void stop(StopContext arg0) {
//        if (!started.compareAndSet(true, false)) {
//            LOGGER.warning("O Service [" + this.getClass().getName() + "] não está Inicializado");
//        } else {
//            LOGGER.debug("Interrompendo Service [" + this.getClass().getName() + "]");
//        }
//    }
}
