/*
 * Codigo gerado automaticamente em Tue Mar 27 16:21:22 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DiariaDeCampo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PropostaConcessaoDiaria;
import br.com.webpublico.util.Persistencia;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class DiariaDeCampoFacade extends AbstractFacade<DiariaDeCampo> {

    private static final Logger logger = LoggerFactory.getLogger(DiariaDeCampoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(DiariaDeCampo entity) {
        getEntityManager().merge(entity);
    }

    @Override
    public void salvarNovo(DiariaDeCampo entity) {
        try {
            getEntityManager().persist(entity);

        } catch (Exception e) {
            if (e instanceof SQLGrammarException) {
                SQLGrammarException ex = (SQLGrammarException) e;
                String msg = ex.getMessage() + " -  " + ex.getSQLException().getMessage();
                logger.warn(msg, ex);
            } else if (e instanceof JDBCConnectionException) {
                JDBCConnectionException ex = (JDBCConnectionException) e;
                String msg = ex.getMessage() + " -  " + ex.getSQLException().getMessage();
                logger.warn(msg, ex);
            } else if (e instanceof EntityExistsException) {
                EntityExistsException ex = (EntityExistsException) e;
                String msg = ex.getMessage();
                logger.warn(msg, ex);
            } else {
                String msg = e.getMessage();
                logger.warn(msg, e);

            }
        }
    }

    public DiariaDeCampoFacade() {
        super(DiariaDeCampo.class);
    }

    public PropostaConcessaoDiariaFacade getPropostaConcessaoDiariaFacade() {
        return propostaConcessaoDiariaFacade;
    }

    public Integer getCodigoSequencial(Exercicio exerc) {

        Query q = getEntityManager().createNativeQuery("SELECT coalesce(max(to_number(d.codigo))+1,1) FROM DIARIADECAMPO d WHERE d.id IN( SELECT dp.DIARIACAMPO_ID FROM DIARIACAMPO_PROPOSTA dp INNER JOIN PROPOSTACONCESSAODIARIA prop ON prop.ID= dp.PROPOSTA_ID AND prop.EXERCICIO_ID=:exerc )");
        q.setParameter("exerc", exerc);
        BigDecimal b = (BigDecimal) q.getSingleResult();
        Integer toReturn = b.intValue();

        return toReturn;
    }

    @Override
    public DiariaDeCampo recarregar(DiariaDeCampo entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        Query q = getEntityManager().createQuery("from DiariaDeCampo d left join fetch d.porpostasConcoesDiarias where d=:param ");
        q.setParameter("param", entity);

        return (DiariaDeCampo) q.getSingleResult();
    }

    public List<PropostaConcessaoDiaria> propostaDisponiveis(Exercicio exerc) {
        List toReturn;

        Query q = getEntityManager().createNativeQuery("SELECT p.* FROM propostaconcessaodiaria p WHERE p.aprovado=0 AND  p.EXERCICIO_ID=:exerc AND  p.id NOT IN(SELECT x.proposta_id FROM diariacampo_proposta x INNER JOIN PROPOSTACONCESSAODIARIA p ON p.ID = x.PROPOSTA_ID AND p.EXERCICIO_ID=:exerc ) ", PropostaConcessaoDiaria.class);
        q.setParameter("exerc", exerc.getId());
        toReturn = q.getResultList();


        return toReturn;
    }

    @Override
    public void remover(DiariaDeCampo entity) {
        entity = recarregar(entity);
        List<PropostaConcessaoDiaria> listaP = entity.getPorpostasConcoesDiarias();
        entity.getPorpostasConcoesDiarias().removeAll(listaP);
        entity = getEntityManager().merge(entity);
        getEntityManager().remove(entity);
    }

    public List<PropostaConcessaoDiaria> listaDiariaDeCampo() {
        String sql = "SELECT * FROM PROPOSTACONCESSAODIARIA WHERE TIPOPROPOSTA = 'CONCESSAO_DIARIACAMPO' ORDER BY id DESC";
        Query q = getEntityManager().createNativeQuery(sql, PropostaConcessaoDiaria.class);
        return q.getResultList();
    }
}
