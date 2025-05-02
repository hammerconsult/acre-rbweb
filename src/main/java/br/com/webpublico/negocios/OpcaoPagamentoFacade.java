/*
 * Codigo gerado automaticamente em Tue Dec 06 11:27:39 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoParcela;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class OpcaoPagamentoFacade extends AbstractFacade<OpcaoPagamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private ConfiguracaoAcrescimosFacade configuracaoAcrescimosFacade;
    @EJB
    private FeriadoFacade feriadoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OpcaoPagamentoFacade() {
        super(OpcaoPagamento.class);
    }

    @Override
    public OpcaoPagamento recuperar(Object id) {
        OpcaoPagamento op = em.find(OpcaoPagamento.class, id);
        Hibernate.initialize(op.getParcelas());
        Hibernate.initialize(op.getDescontos());
        return op;
    }

    @Override
    public void salvar(OpcaoPagamento entity) {
        getEntityManager().merge(entity);
    }

    @Override
    public void salvarNovo(OpcaoPagamento entity) {
        getEntityManager().persist(entity);
    }

    @Override
    public void remover(OpcaoPagamento entity) {
        if (entity.getId() != null) {
            entity = em.find(OpcaoPagamento.class, entity.getId());
            for (Parcela p : entity.getParcelas()) {
                em.remove(p);
            }
            for (DescontoOpcaoPagamento d : entity.getDescontos()) {
                em.remove(d);
            }
            em.remove(entity);
        }
    }

    public boolean emUso(OpcaoPagamento opcaoPagamento) {
        Query q = em.createNativeQuery("SELECT count(d.id) FROM Divida d "
            + " INNER JOIN opcaopagamentodivida opd ON opd.divida_id = d.id AND opd.opcaopagamento_id = :opcaoID");
        q.setParameter("opcaoID", opcaoPagamento.getId());
        BigDecimal i = (BigDecimal) q.getSingleResult();
        return i.compareTo(BigDecimal.ZERO) > 0;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public ConfiguracaoAcrescimosFacade getConfiguracaoAcrescimosFacade() {
        return configuracaoAcrescimosFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public List<OpcaoPagamentoDivida> retornaOpcoesDaDividaNoExercicio(Divida divida, Exercicio exercicio) {
        String hql = "select op from OpcaoPagamentoDivida op" +
            " where op.divida = :divida" +
            "  and :ano between extract(year from op.inicioVigencia) and extract(year from op.finalVigencia)";
        Query q = em.createQuery(hql);
        q.setParameter("divida", divida);
        q.setParameter("ano", exercicio.getAno());
        return q.getResultList();
    }

    public List<OpcaoPagamento> buscarOpcaoPagamentoFixaPorExercicio(Exercicio exercicio) {
        String sql = " select distinct op.* " +
            "          from opcaopagamento op " +
            "          where op.tipoparcela = :tipo " +
            "          and extract (year from op.dataverificacaodebito) = :ano";
        Query q = em.createNativeQuery(sql, OpcaoPagamento.class);
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("tipo", TipoParcela.FIXA.name());
        return q.getResultList();
    }

    public List<OpcaoPagamentoDivida> buscarOpcaoPagamentoDivida(OpcaoPagamento opcaoPagamento) {
        String sql = " select op.* from opcaopagamentodivida op where OPCAOPAGAMENTO_ID = :opcaoPagamento";
        Query q = em.createNativeQuery(sql, OpcaoPagamentoDivida.class);
        q.setParameter("opcaoPagamento", opcaoPagamento.getId());
        return q.getResultList();
    }

    public OpcaoPagamento salvarOpcaoPagamento(OpcaoPagamento op) {
        return em.merge(op);
    }
}
