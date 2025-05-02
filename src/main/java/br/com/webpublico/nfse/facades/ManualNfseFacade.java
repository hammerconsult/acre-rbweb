package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ManualNfse;
import br.com.webpublico.nfse.domain.dtos.ManualDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by william on 29/08/17.
 */
@Stateless
public class ManualNfseFacade extends AbstractFacade<ManualNfse> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoManualFacade tipoManualFacade;
    @EJB
    private PesquisaGenericaNfseFacade pesquisaGenericaNfseFacade;

    public ManualNfseFacade() {
        super(ManualNfse.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasOrdemAdicionada(ManualNfse entity) {
        String sql = "select * from manualnfse where ordem = :ordem";
        if (entity.getId() != null) {
            sql += " and id <> :id";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("ordem", entity.getOrdem());
        if (entity.getId() != null) {
            q.setParameter("id", entity.getId());
        }
        return !q.getResultList().isEmpty();
    }

    @Override
    public ManualNfse recuperar(Object id) {
        ManualNfse manualNfse = super.recuperar(id);
        if (manualNfse.getDetentorArquivoComposicao() != null &&
            manualNfse.getDetentorArquivoComposicao().getArquivoComposicao() != null &&
            manualNfse.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo() != null) {
            manualNfse.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes().size();
        }
        return manualNfse;
    }

    public TipoManualFacade getTipoManualFacade() {
        return tipoManualFacade;
    }

    public List<ManualNfse> buscarManualParaExibicao() {
        String hql = " select m from ManualNfse m " +
            "  inner join m.tipoManual tm " +
            " where m.habilitarExibicao =:verdadeiro " +
            "   and tm.habilitarExibicao =:verdadeiro " +
            " order by tm.ordem, m.ordem ";
        Query q = em.createQuery(hql);
        q.setParameter("verdadeiro", true);
        return q.getResultList();
    }

    public Page<ManualDTO> pesquisar(Pageable pageable, String searchFields, String query) throws UnsupportedEncodingException {
        Page<ManualNfse> manuais = pesquisaGenericaNfseFacade.pesquisar(pageable, "ManualNfse", searchFields, query);
        return new PageImpl(ManualNfse.toListManualNfseDTO(manuais.getContent()), pageable, manuais.getTotalElements());
    }

    public ManualNfse salvarRetornando(ManualNfse manualNfse) {
        return em.merge(manualNfse);
    }

    public List<ManualNfse> buscarManualPorTag(String tag) {
        if (Strings.isNullOrEmpty(tag)) {
            return Lists.newArrayList();
        }
        String hql = " select m from ManualNfse m " +
            "  inner join m.tipoManual tm " +
            " where m.habilitarExibicao =:verdadeiro " +
            "   and tm.habilitarExibicao =:verdadeiro " +
            "   and lower(m.tags) like :tags " +
            " order by tm.ordem, m.ordem ";
        Query q = em.createQuery(hql);
        q.setParameter("verdadeiro", true);
        q.setParameter("tags", "%" + tag.toLowerCase() + "%");
        return q.getResultList();
    }
}
