/*
 * Codigo gerado automaticamente em Tue Apr 12 17:35:39 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.UnidadePortal;
import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.ResponsavelUnidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ResponsavelUnidadeFacade extends AbstractFacade<ResponsavelUnidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ResponsavelUnidadeFacade() {
        super(ResponsavelUnidade.class);
    }

    public List<ResponsavelUnidade> responsaveisDaUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        String hql = "from ResponsavelUnidade r " +
            " where r.unidadeOrganizacional = :unidadeOrganizacional";

        Query q = em.createQuery(hql);
        q.setParameter("unidadeOrganizacional", unidadeOrganizacional);
        return q.getResultList();
    }

    public List<ResponsavelUnidade> responsaveisDaUnidadePorEntidade(Entidade entidade) {
        String hql = "from ResponsavelUnidade r " +
            " where r.unidadeOrganizacional.entidade = :entidade";

        Query q = em.createQuery(hql);
        q.setParameter("entidade", entidade);
        return q.getResultList();
    }

    @Override
    public ResponsavelUnidade recuperar(Object id) {
        ResponsavelUnidade obj =  super.recuperar(id);
        Hibernate.initialize(obj.getAgenda());
        if (obj.getDetentorArquivoComposicao() != null && !obj.getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
            Hibernate.initialize(obj.getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao arquivoComposicao : obj.getDetentorArquivoComposicao().getArquivosComposicao()) {
                if (arquivoComposicao.getArquivo() != null) {
                    Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
                }
            }
        }
        if (obj.getDetentorArquivoComposicao() != null && obj.getDetentorArquivoComposicao().getArquivoComposicao() != null &&
            obj.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo() != null) {
            Hibernate.initialize(obj.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes());
        }
        return obj;
    }

    @Override
    public void salvar(ResponsavelUnidade entity) {
        super.salvar(entity);
        portalTransparenciaNovoFacade.salvarPortal(new UnidadePortal(entity.getUnidadeOrganizacional()));
    }

    @Override
    public void salvarNovo(ResponsavelUnidade entity) {
        super.salvarNovo(entity);
        portalTransparenciaNovoFacade.salvarPortal(new UnidadePortal(entity.getUnidadeOrganizacional()));
    }
}
