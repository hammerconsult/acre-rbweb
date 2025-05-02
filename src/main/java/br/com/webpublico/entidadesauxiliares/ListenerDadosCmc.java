package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.negocios.tributario.services.ServiceCadastroEconomico;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.persistence.PostUpdate;

public class ListenerDadosCmc {

    @PostUpdate
    public void atualizarDadosCmc(Pessoa pessoa) {
        ServiceCadastroEconomico service = getServiceCadastroEconomico();
        if (service != null) {
            service.setPessoa(pessoa);
        }
    }

    private ServiceCadastroEconomico getServiceCadastroEconomico() {
        try {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            return (ServiceCadastroEconomico) ap.getBean("serviceCadastroEconomico");
        } catch (BeanCreationException e) {
            return null;
        }
    }

}
