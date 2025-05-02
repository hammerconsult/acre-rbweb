package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.negocios.SistemaFacade;
//import org.jboss.as.clustering.singleton.SingletonService;
//import org.jboss.as.server.CurrentServiceContainer;
//import org.jboss.as.server.ServerEnvironment;
//import org.jboss.as.server.ServerEnvironmentService;
//import org.jboss.msc.service.AbstractServiceListener;
//import org.jboss.msc.service.ServiceController;
//import org.jboss.msc.service.ServiceController.Transition;
//import org.jboss.msc.service.ServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumSet;

/**
* @author Daniel Franco
* @since 21/01/2015 12:54
*/
@Singleton
public class ServiceProtocoloSingleton {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProtocoloSingleton.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    private boolean ativo = false;
    private Integer numero;

    public Integer getNumeroAtualizando() {
        return ++this.numero;
    }

    private Integer atualizaNumero() {
        return ((BigDecimal) em.createNativeQuery("SELECT coalesce(max(numero),0) FROM Processo WHERE ano = :ano AND protocolo = 1")
                .setParameter("ano", sistemaFacade.getExercicioCorrente().getAno())
                .getSingleResult()).intValue();
    }

    /**
     * Create the Service and wait until it is started.<br/>
     * Will log a message if the service will not start in 10sec.
     */
    public void startup() {
        if (ativo) {
            //System.out.println("Já ativo, ignorando");
            return;
        }
        System.out.println("Inicializando ServiceProtocoloSingleton");
        this.numero = atualizaNumero();
        ServiceProtocolo service = new ServiceProtocolo(numero);

//        SingletonService<Integer> singleton = new SingletonService<>(service, ServiceProtocolo.SINGLETON_SERVICE_NAME);
        // if there is a node where the Singleton should deployed the election policy might set,
        // otherwise the JGroups coordinator will start it
        //singleton.setElectionPolicy(new PreferredSingletonElectionPolicy(new NamePreference("node2/cluster"), new SimpleSingletonElectionPolicy()));

        //System.out.println("CurrentServiceContainer.getServiceContainer() ---> " + CurrentServiceContainer.getServiceContainer());
//        ServiceController<Integer> controller = singleton.build(CurrentServiceContainer.getServiceContainer()).addDependency(ServerEnvironmentService.SERVICE_NAME, ServerEnvironment.class, service.getEnvInjector()).install();
//        controller.setMode(ServiceController.Mode.ACTIVE);
        try {
//            wait(controller, EnumSet.of(ServiceController.State.DOWN, ServiceController.State.STARTING), ServiceController.State.UP);
            //System.out.println("ServiceProtocoloSingleton iniciou o Service ServiceProtocolo");
            ativo = true;
        } catch (IllegalStateException e) {
            //System.out.println("Singleton Service " + ServiceProtocolo.SINGLETON_SERVICE_NAME + " não inicializado, você tem certeza de estar num cluster HA?");
        }
    }

    /**
     * Remove the service during undeploy or shutdown
     */
    @PreDestroy
    protected void destroy() {
        System.out.println("ServiceProtocoloSingleton será destruído");
//        ServiceController<?> controller = CurrentServiceContainer.getServiceContainer().getRequiredService(ServiceProtocolo.SINGLETON_SERVICE_NAME);
//        controller.setMode(ServiceController.Mode.REMOVE);
        try {
//            wait(controller, EnumSet.of(ServiceController.State.UP, ServiceController.State.STOPPING, ServiceController.State.DOWN), ServiceController.State.REMOVED);
        } catch (IllegalStateException e) {
//            System.out.println("Singleton Service " + ServiceProtocolo.SINGLETON_SERVICE_NAME + " não interrompido corretamente");
        }
    }

//    private static <T> void wait(ServiceController<T> controller, Collection<ServiceController.State> expectedStates, ServiceController.State targetState) {
//        if (controller.getState() != targetState) {
//            ServiceListener<T> listener = new NotifyingServiceListener<T>();
//            controller.addListener(listener);
//            try {
//                synchronized (controller) {
//                    int maxRetry = 2;
//                    while (expectedStates.contains(controller.getState()) && maxRetry > 0) {
//                        LOGGER.debug("Service controller state is {}, waiting for transition to {}", new Object[] {controller.getState(), targetState});
//                        controller.wait(5000);
//                        maxRetry--;
//                    }
//                }
//            } catch (InterruptedException e) {
//                LOGGER.warn("Wait on startup is interrupted!");
//                Thread.currentThread().interrupt();
//            }
//            controller.removeListener(listener);
//            ServiceController.State state = controller.getState();
//            LOGGER.debug("Service controller state is now {}",state);
//            if (state != targetState) {
//                throw new IllegalStateException(String.format("Failed to wait for state to transition to %s.  Current state is %s", targetState, state), controller.getStartException());
//            }
//        }
//    }
//
//    private static class NotifyingServiceListener<T> extends AbstractServiceListener<T> {
//        @Override
//        public void transition(ServiceController<? extends T> controller, Transition transition) {
//            synchronized (controller) {
//                controller.notify();
//            }
//        }
//    }
}
