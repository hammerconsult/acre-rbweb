package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ArquivoEconsig;
import br.com.webpublico.entidades.ArquivoEconsigItens;
import br.com.webpublico.entidades.LancamentoFP;
import br.com.webpublico.entidadesauxiliares.ImportaMovimentoFinanceiro;
import br.com.webpublico.enums.Mes;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 10/10/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ArquivoEconsigFacade extends AbstractFacade<ArquivoEconsig> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ImportaArquivoEconsigAsyncFacade importaArquivoEconsigAsyncFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private MiddleRHFacade middleRHFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ArquivoEconsigFacade() {
        super(ArquivoEconsig.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void processaArquivo(ImportaMovimentoFinanceiro arquivo) throws FileNotFoundException, IOException {
        importaArquivoEconsigAsyncFacade.processarArquivo(arquivo, sistemaFacade.getDataOperacao());
    }

    @Override
    public void remover(ArquivoEconsig entity) {
        entity = recuperarTodos(entity.getId());
        for (ArquivoEconsigItens item : entity.getArquivoEconsigItens()) {
            LancamentoFP lancamentoFP = item.getLancamentoFP();
            item.setLancamentoFP(null);
            em.merge(item);
            lancamentoFPFacade.remover(lancamentoFP);
        }
        super.remover(entity);
    }

    public void removerMotherFocker(ArquivoEconsig entity) {
        entity = recuperar(entity.getId());
        entity.getArquivoEconsigItens().size();
        for (ArquivoEconsigItens item : entity.getArquivoEconsigItens()) {
            LancamentoFP lancamentoFP = item.getLancamentoFP();
//            item.setLancamentoFP(null);
//            em.merge(item);
            if (lancamentoFP != null) {
                lancamentoFP = em.find(LancamentoFP.class, lancamentoFP.getId());
                lancamentoFPFacade.remover(lancamentoFP);
            } else {
                //System.out.println("opaa");
            }

        }
        super.remover(entity);
    }

    @Override
    public ArquivoEconsig recuperar(Object id) {
        ArquivoEconsig ae = em.find(ArquivoEconsig.class, id);
        ae.getErrosEconsig().size();
        return ae;
    }


    public ArquivoEconsig recuperarTodos(Object id) {
        ArquivoEconsig ae = em.find(ArquivoEconsig.class, id);
        ae.getArquivoEconsigItens().size();
        ae.getErrosEconsig().size();
        return ae;
    }

    public List<ArquivoEconsig> buscarByMonthAndYear(Mes mes, Integer ano) {
        String hql = "from ArquivoEconsig a where a.mes = :mes and a.ano = :ano";
        Query q = em.createQuery(hql);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    public void mergeItem(ArquivoEconsigItens item) {
        em.merge(item);
    }

    //    @TransactionTimeout(value = 3, unit = TimeUnit.HOURS)
    public void salvar(ArquivoEconsig selecionado, ImportaMovimentoFinanceiro importaMovimentoFinanceiro, Integer totalRejeitados) {
        selecionado.setAno(importaMovimentoFinanceiro.getAno());
        selecionado.setMes(Mes.getMesToInt(importaMovimentoFinanceiro.getMes()));
        selecionado.setDataRegistro(sistemaFacade.getDataOperacao());
        selecionado.setTotal(importaMovimentoFinanceiro.getContadorTotal());
        selecionado.setQuantidadeOk(importaMovimentoFinanceiro.getContadorTotal() - totalRejeitados);
        selecionado.setQuantidadeRejeitados(totalRejeitados);

        for (ArquivoEconsigItens itens : selecionado.getArquivoEconsigItens()) {
            if (itens.getLancamentoFP().getId() != null) {
            } else {
                em.persist(itens.getLancamentoFP());
            }
        }
        em.merge(selecionado);
    }

    public boolean existeArquivoProcessado(Mes mes, Integer ano) {
        Query q = em.createQuery("from ArquivoEconsig a where a.mes = :mes and a.ano = :ano");
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList().isEmpty();
    }

    public ArquivoEconsigItens recuperaEconsigItensPorLancamentoFP(LancamentoFP lancamento) {
        Query q = this.em.createQuery("from ArquivoEconsigItens  econ where econ.lancamentoFP = :lanc");
        q.setParameter("lanc", lancamento);
        if (!q.getResultList().isEmpty()) {
            return (ArquivoEconsigItens) q.getSingleResult();
        }
         return null;
    }

    public ArquivoEconsigItens recuperarEconsigLancamento(LancamentoFP lancamento) {
        return (ArquivoEconsigItens) em.createQuery("from ArquivoEconsigItens aei where aei.lancamentoFP = :lanc").setParameter("lanc", lancamento).getSingleResult();
    }
}
