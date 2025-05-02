package br.com.webpublico.agendamentotarefas;


import br.com.webpublico.negocios.tributario.services.ServiceAgendamentoTarefa;
import br.com.webpublico.seguranca.MensagemTodosUsuariosService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class SchedulerBuilder implements ServletContextListener {

    protected static final Logger logger = LoggerFactory.getLogger(SchedulerBuilder.class);
    @Autowired
    protected ServiceAgendamentoTarefa serviceAgendamentoTarefa;
    @Autowired
    protected MensagemTodosUsuariosService mensagemTodosUsuariosService;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            addContextoSpring();
            iniciaScheduler();
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
        }
    }

    private void addContextoSpring() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    private void iniciaScheduler() throws SchedulerException {
        serviceAgendamentoTarefa.agendaTarefas();
        mensagemTodosUsuariosService.agendaTarefas();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


}
