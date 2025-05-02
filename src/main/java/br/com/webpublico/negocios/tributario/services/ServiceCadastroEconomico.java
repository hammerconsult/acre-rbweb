package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.Pessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;

@Scope("request")
@Service
public class ServiceCadastroEconomico implements DisposableBean {

    protected static final Logger logger = LoggerFactory.getLogger(ServiceCadastroEconomicoAfterDestroy.class);
    private Pessoa pessoa;

    public ServiceCadastroEconomico() {
    }

    public void realizarOperacoes() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        ServiceCadastroEconomicoAfterDestroy serviceCadastroEconomicoAfterDestroy = (ServiceCadastroEconomicoAfterDestroy) ap.getBean("serviceCadastroEconomicoAfterDestroy");
        serviceCadastroEconomicoAfterDestroy.alterarDadosCmc(pessoa);
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public void destroy() throws Exception {
        realizarOperacoes();
    }
}
