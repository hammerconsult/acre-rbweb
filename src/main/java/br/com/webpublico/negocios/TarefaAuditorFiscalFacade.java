package br.com.webpublico.negocios;

import br.com.webpublico.entidades.SecretariaFiscalizacao;
import br.com.webpublico.entidades.TarefaAuditorFiscal;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 04/08/14
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class TarefaAuditorFiscalFacade extends AbstractFacade<TarefaAuditorFiscal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TarefaAuditorFiscalFacade() {
        super(TarefaAuditorFiscal.class);
    }

    public Integer getProximoCodigoPorSecretaria(SecretariaFiscalizacao secretariaFiscalizacao) {
        Integer retorno = 0;
        Query q = em.createNativeQuery(" select max(codigo) codigo from tarefaauditorfiscal where secretariafiscalizacao_id = :secretaria ");
        q.setParameter("secretaria", secretariaFiscalizacao.getId());
        q.setMaxResults(1);
        if (q.getResultList() != null && q.getResultList().size() > 0 && q.getSingleResult() != null) {
            retorno = ((BigDecimal) q.getSingleResult()).intValue();
        }
        return retorno + 1;
    }

    public List<TarefaAuditorFiscal> listarTarefaPorCodigoOuDescricaoReduzida(SecretariaFiscalizacao secretariaFiscalizacao, String parte) {
        if (secretariaFiscalizacao == null) {
            return new ArrayList();
        }
        Query q = em.createNativeQuery(" select * from tarefaauditorfiscal where secretariafiscalizacao_id = " + secretariaFiscalizacao.getId() +
                "and ((cast(codigo as varchar(20))= '" + parte.trim() + "') or (descricaoReduzida like '%" + parte.trim() + "%'))", TarefaAuditorFiscal.class);
        return q.getResultList();
    }

    public List<TarefaAuditorFiscal> listarTarefaOrdenandoPorCodigo(SecretariaFiscalizacao secretariaFiscalizacao) {
        if (secretariaFiscalizacao == null) {
            return new ArrayList();
        }
        Query q = em.createNativeQuery(" select * from tarefaauditorfiscal where secretariafiscalizacao_id = " + secretariaFiscalizacao.getId()
                + " and situacao = 'ATIVO' "
                + " order by codigo ", TarefaAuditorFiscal.class);
        return q.getResultList();
    }
}
