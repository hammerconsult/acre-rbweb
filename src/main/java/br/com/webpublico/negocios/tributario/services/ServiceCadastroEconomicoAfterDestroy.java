package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Scope("prototype")
@Service
public class ServiceCadastroEconomicoAfterDestroy implements DisposableBean {

    protected static final Logger logger = LoggerFactory.getLogger(ServiceCadastroEconomicoAfterDestroy.class);
    private CadastroEconomicoFacade cadastroEconomicoFacade;

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        if (cadastroEconomicoFacade == null) {
            cadastroEconomicoFacade = (CadastroEconomicoFacade) Util.getFacadeViaLookup("java:module/CadastroEconomicoFacade");
        }
        return cadastroEconomicoFacade;
    }

    public void alterarDadosCmc(Pessoa pessoa) {
        if (pessoa == null) return;
        CadastroEconomico cadastroEconomico = getCadastroEconomicoFacade().buscarCadastroEconomicoAtivoPorPessoa(pessoa);
        cadastroEconomico = getCadastroEconomicoFacade().recuperar(cadastroEconomico.getId());
        if (cadastroEconomico != null) {
            getCadastroEconomicoFacade().atualizarLocalizacaoDoCadastroEconomicoComEnderecoPrincipalPessoa(cadastroEconomico,
                pessoa);
            if (pessoa instanceof PessoaJuridica) {
                PessoaJuridica pessoaJuridica = (PessoaJuridica) pessoa;
                getCadastroEconomicoFacade().atualizarCnaesDoCadastroEconomicoComOsCnaesDaPessoaJuridica(cadastroEconomico,
                    pessoaJuridica);
                getCadastroEconomicoFacade().atualizarSociedadeDoCadastroEconomicoComSociedadeDaPessoaJuridica(cadastroEconomico,
                    pessoaJuridica);
            }
            getCadastroEconomicoFacade().salvar(cadastroEconomico);
        }
    }

    @PreDestroy
    @Override
    public void destroy() throws Exception {
        SistemaFacade.removerSistemaServiceParaThread();
    }
}
