package br.com.webpublico.negocios;


import br.com.webpublico.entidades.AmparoLegal;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.enums.LeiLicitacao;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AmparoLegalFacade extends AbstractFacade<AmparoLegal> {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;

    public AmparoLegalFacade() {
        super(AmparoLegal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean existeCodigo(Long id, Long codigo) {
        StringBuilder sql = new StringBuilder("select codigo from amparolegal where codigo = :codigo");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("codigo", codigo);
        if (id != null) {
            q.setParameter("id", id);
        }
        return !q.getResultList().isEmpty();
    }

    public List<LeiLicitacao> buscarLeiLicitacaoAmparoLegalVigente() {
        String hql = " select distinct al.leiLicitacao from AmparoLegal al "
            + " where (to_date(:dataOperacao,'dd/mm/yyyy') between al.inicioVigencia and coalesce(al.fimVigencia, to_date(:dataOperacao,'dd/mm/yyyy')))";
        Query consulta = em.createQuery(hql, LeiLicitacao.class);
        consulta.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return consulta.getResultList();
    }

    public List<AmparoLegal> buscarPorLeiAndModalidade(String parte, LeiLicitacao leiLicitacao, ModalidadeLicitacao modalidade) {
        String sql = " select distinct al.* from amparolegal al " +
            "          where (lower(al.nome) like :param ) " +
            "            and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(al.iniciovigencia) and coalesce(trunc(al.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "            and al.leilicitacao = :tipoLeiLicitacao ";
        sql += modalidade != null ? " and al.modalidadelicitacao = :modalidade " : "";
        sql += "       order by codigo ";
        Query q = em.createNativeQuery(sql, AmparoLegal.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoLeiLicitacao", leiLicitacao.name());
        if (modalidade != null) {
            q.setParameter("modalidade", modalidade.name());
        }
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<ModalidadeLicitacao> buscarModalidadesPorLei(LeiLicitacao leiLicitacao) {
        String hql = " select distinct al.modalidadeLicitacao from AmparoLegal al " +
            "          where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(al.inicioVigencia) and coalesce(trunc(al.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "            and al.leiLicitacao = :leiLicitacao " ;
        Query q = em.createQuery(hql, ModalidadeLicitacao.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("leiLicitacao", leiLicitacao);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public AmparoLegal buscarAmparoLegalPorLicitacao(Licitacao licitacao) {
        String sql = " select al.* from amparolegal al " +
                "    inner join solicitacaomaterial sm on sm.amparolegal_id = al.id " +
                "    inner join processodecompra pdc on sm.id = pdc.solicitacaomaterial_id " +
                "    inner join licitacao lic on pdc.id = lic.processodecompra_id " +
                " where lic.id = :idLicitacao  " ;
        Query q = em.createNativeQuery(sql, AmparoLegal.class);
        q.setParameter("idLicitacao", licitacao.getId());
        try {
            return (AmparoLegal) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
