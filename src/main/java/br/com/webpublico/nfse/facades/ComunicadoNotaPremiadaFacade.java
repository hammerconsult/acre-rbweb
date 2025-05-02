package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.UsuarioNotaPremiadaFacade;
import br.com.webpublico.nfse.domain.ComunicadoNotaPremiada;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ComunicadoNotaPremiadaFacade extends AbstractFacade<ComunicadoNotaPremiada> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UsuarioNotaPremiadaFacade usuarioNotaPremiadaFacade;

    public ComunicadoNotaPremiadaFacade() {
        super(ComunicadoNotaPremiada.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ComunicadoNotaPremiada recuperar(Object id) {
        ComunicadoNotaPremiada comunicadoNotaPremiada = super.recuperar(id);
        if (comunicadoNotaPremiada.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(comunicadoNotaPremiada.getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao arquivoComposicao : comunicadoNotaPremiada.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
            }
        }
        return comunicadoNotaPremiada;
    }
}
