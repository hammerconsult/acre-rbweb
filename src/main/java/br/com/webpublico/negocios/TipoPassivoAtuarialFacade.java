/*
 * Codigo gerado automaticamente em Fri Jul 13 10:46:12 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.enums.TipoOperacaoAtuarial;
import br.com.webpublico.entidades.TipoPassivoAtuarial;
import br.com.webpublico.enums.TipoPlano;
import br.com.webpublico.enums.TipoProvisao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TipoPassivoAtuarialFacade extends AbstractFacade<TipoPassivoAtuarial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoPassivoAtuarialFacade() {
        super(TipoPassivoAtuarial.class);
    }

    public String getUltimoNumero() {
        String sql = "SELECT max(to_number(codigo))+1 AS ultimoNumero FROM TIPOPASSIVOATUARIAL";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    @Override
    public TipoPassivoAtuarial recuperar(Object id) {
        TipoPassivoAtuarial tpa = em.find(TipoPassivoAtuarial.class, id);
        tpa.getTipoOperacaoAtuarial().size();
        tpa.getTipoPlano().size();
        tpa.getTipoProvisao().size();
        return tpa;
    }

    public List<TipoPassivoAtuarial> getTipoPassivoPorOperacaoPlanoProvisao(TipoOperacaoAtuarial tipoOperacaoAtuarial, TipoPlano tipoPlano, TipoProvisao tipoProvisao) {
        String sql = "SELECT tpa.* FROM TipoPassivoAtuarial tpa "
                + "INNER JOIN TIPOPASSIVO_TIPOOPERACAO tipoOperacao ON tipoOperacao.tipopassivoatuarial_id = tpa.id "
                + "INNER JOIN TIPOPASSIVO_TIPOPLANO tipoPlano ON tipoPlano.tipopassivoatuarial_id = tpa.id "
                + "INNER JOIN TIPOPASSIVO_TIPOPROVISAO tipoProvisao ON tipoProvisao.tipopassivoatuarial_id = tpa.id "
                + "WHERE tipooperacao.tipooperacaoatuarial IN (:tipoOperacaoAtuarial) "
                + "AND tipoplano.tipoplano IN (:tipoPlano) "
                + "AND tipoprovisao.tipoprovisao IN (:tipoProvisao)";
        Query q = em.createNativeQuery(sql, TipoPassivoAtuarial.class);
        q.setParameter("tipoOperacaoAtuarial", tipoOperacaoAtuarial.name());
        q.setParameter("tipoPlano", tipoPlano.name());
        q.setParameter("tipoProvisao", tipoProvisao.name());
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<TipoPassivoAtuarial>();
    }

}
