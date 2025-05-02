package br.com.webpublico.nfse.facades;

import br.com.webpublico.nfse.domain.CodigoTributacaoMunicipal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class CodigoTributacaoMunicipalFacade extends AbstractFacade<CodigoTributacaoMunicipal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CodigoTributacaoMunicipalFacade() {
        super(CodigoTributacaoMunicipal.class);
    }

    @Override
    public void preSave(CodigoTributacaoMunicipal entidade) {
        super.preSave(entidade);
        entidade.realizarValidacoes();
        CodigoTributacaoMunicipal aliquotaConflitante = buscarAliquotaConflitante(entidade);
        if (aliquotaConflitante != null) {
            throw new ValidacaoException("Existe uma alíquota cadastrada para o código de tributação informado, " +
                " por favor encerre a vigência dessa alíquota e tente novamente. Código de Tributação Municipal: " +
                aliquotaConflitante.getCodigo());
        }
    }

    public CodigoTributacaoMunicipal buscarAliquotaConflitante(CodigoTributacaoMunicipal codigoTributacaoMunicipal) {
        String sql = " select ctm.* " +
            "            from codigotributacaomunicipal ctm " +
            " where ctm.codigotributacao_id = :idcodigotributacao " +
            "   and ( (:iniciovigencia between ctm.iniciovigencia and coalesce(ctm.fimvigencia, current_date)) or " +
            "         (:fimvigencia between ctm.iniciovigencia and coalesce(ctm.fimvigencia, current_date)) or " +
            "         (:iniciovigencia < ctm.iniciovigencia and :fimvigencia > coalesce(ctm.fimvigencia, current_date)) ) ";
        if (codigoTributacaoMunicipal.getId() != null) {
            sql += " and ctm.id != :id ";
        }
        Query query = em.createNativeQuery(sql, CodigoTributacaoMunicipal.class);
        query.setParameter("idcodigotributacao", codigoTributacaoMunicipal.getCodigoTributacao().getId());
        query.setParameter("iniciovigencia", codigoTributacaoMunicipal.getInicioVigencia());
        query.setParameter("fimvigencia", codigoTributacaoMunicipal.getFimVigencia() != null ?
            codigoTributacaoMunicipal.getFimVigencia() : new Date());
        if (codigoTributacaoMunicipal.getId() != null) {
            query.setParameter("id", codigoTributacaoMunicipal.getId());
        }
        query.setMaxResults(1);
        List resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return (CodigoTributacaoMunicipal) resultList.get(0);
        }
        return null;
    }
}
