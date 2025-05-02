package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ConfiguracaoPerfilApp;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.ConfiguracaoTituloSistemaFacade;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonArquivosInterface implements Serializable {

    private final int PRIMEIRO = 0;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConfiguracaoTituloSistemaFacade configuracaoTituloSistemaFacade;
    private Arquivo logoLogin;
    private Arquivo logoTopo;
    private ConfiguracaoPerfilApp configuracaoPerfil;


    @Lock(LockType.READ)
    public Arquivo getLogoLogin() {
        if(logoLogin == null){
            logoLogin = arquivoFacade.recuperaUltimaLogo();
        }
        return logoLogin;
    }

    @Lock(LockType.READ)
    public Arquivo getLogoTopo() {
        if(logoTopo == null){
            logoTopo = arquivoFacade.recuperaUltimaLogoTopo();
        }
        return logoTopo;
    }

    @Lock(LockType.READ)
    public ConfiguracaoPerfilApp getConfiguracaoPerfil() {
        if(configuracaoPerfil == null){
            configuracaoPerfil = configuracaoTituloSistemaFacade.recuperaConfiguracaoPerfilApp();
        }
        return configuracaoPerfil;
    }

    public void limpaConfiguracaoPerfil() {
        configuracaoPerfil = configuracaoTituloSistemaFacade.recuperaConfiguracaoPerfilApp();
    }

    public void limpaLogoTopo() {
        logoTopo = arquivoFacade.recuperaUltimaLogoTopo();
    }

    public void limpaLogoLogin() {
        logoLogin = arquivoFacade.recuperaUltimaLogo();
    }
}
