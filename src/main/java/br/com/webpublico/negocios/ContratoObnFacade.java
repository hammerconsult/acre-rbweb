package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.ContratoObn;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeObn;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by israeleriston on 19/05/16.
 */
@Stateless
public class ContratoObnFacade extends AbstractFacade<ContratoObn> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    /**
     * Deve ser executado no construtor da subclasse para indicar qual é a
     * classe da entidade
     *
     * @param classe Classe da entidade
     */
    public ContratoObnFacade() {
        super(ContratoObn.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ContratoObn recuperar(Object id) {
        ContratoObn c = em.find(ContratoObn.class, id);
        for (UnidadeObn unidadeObn : c.getUnidades()) {
            unidadeObn.getUnidadeOrganizacional().getHierarquiasOrganizacionais().size();
        }

        return c;
    }

    public List<ContratoObn> completarContratoPorBanco(String filter, Banco ban) {
        String sql = " select contrato.* from CONTRATOOBN contrato " +
                "inner join BANCOOBN ban on contrato.BANCOOBN_ID = ban.ID " +
                "where ban.NUMERODOBANCO = :numeroBanco and contrato.numerocontrato like :filter ";
        Query q = em.createNativeQuery(sql, ContratoObn.class);
        q.setParameter("numeroBanco", ban.getNumeroBanco());
        q.setParameter("filter", "%" + filter.trim().toLowerCase() + "%");

        return q.getResultList().isEmpty() ? Lists.newArrayList() : q.getResultList();
    }


    public List<HierarquiaOrganizacional> buscarHierarquiaPorContratoAndBanco(String filter, ContratoObn contrato, Banco banco) {
        String sql = " SELECT DISTINCT ho.* " +
                " FROM CONTRATOOBN contrato " +
                "  INNER JOIN BANCOOBN ban ON contrato.BANCOOBN_ID = ban.ID " +
                "  INNER JOIN UNIDADEOBN uni ON contrato.ID = uni.CONTRATOOBN_ID " +
                "  INNER JOIN UNIDADEORGANIZACIONAL uo ON uni.UNIDADEORGANIZACIONAL_ID = uo.ID " +
                "  INNER JOIN HIERARQUIAORGANIZACIONAL ho ON uo.ID = ho.SUBORDINADA_ID " +
                " WHERE (lower(uo.DESCRICAO) LIKE :filter  OR ho.CODIGONO LIKE :filter) " +
                " AND ho.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA' " +
                " AND contrato.NUMEROCONTRATO = :numeroContrato and ho.indicedono = 3 " +
                " AND ban.numerodobanco = :banco " +
                " AND :data BETWEEN ho.iniciovigencia and COALESCE(ho.fimvigencia,:data)";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("numeroContrato", contrato.getNumeroContrato());
        q.setParameter("filter", "%" + filter.toLowerCase().trim() + "%");
        q.setParameter("banco", banco.getNumeroBanco());
        q.setParameter("data", sistemaFacade.getDataOperacao());

        return q.getResultList().isEmpty() ? new ArrayList<>() : q.getResultList();
    }


    public ContratoObn buscarContratoPorNumeroContratoAndBanco(String numeroContrato, Banco ban){
        String sql = " select contrato.* from CONTRATOOBN contrato " +
                "inner join BANCOOBN bancoobn on contrato.BANCOOBN_ID = bancoobn.ID " +
                "inner join BANCO ban on ban.NUMEROBANCO = bancoobn.NUMERODOBANCO " +
                "where contrato.NUMEROCONTRATO = :numerocontrato and ban.NUMEROBANCO = :numerobanco";
        Query q = em.createNativeQuery(sql,ContratoObn.class);
        q.setParameter("numerocontrato", numeroContrato);
        q.setParameter("numerobanco",  ban.getNumeroBanco());

        return (ContratoObn) (q.getResultList().isEmpty() ? new ContratoObn() : q.getResultList().get(0));
    }

}
