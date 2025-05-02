/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Iterator;
import java.util.List;

@Stateless
public class DisponibilidadeFacade extends AbstractFacade<Disponibilidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExtincaoCargoFacade extincaoCargoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    protected static final Integer CODIGO_SITUACAO_FUNCIONAL_DISPONIBILIDADE = 12;
    protected static final Integer CODIGO_PROVIMENTO_DISPONIBILIDADE = 71;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Disponibilidade recuperar(Object id) {
        Disponibilidade d = super.recuperar(id);    //To change body of overridden methods use File | Settings | File Templates.
        d.getItensDiponibilidade().size();
        return d;
    }

    public DisponibilidadeFacade() {
        super(Disponibilidade.class);
    }

    public List<ContratoFP> recuperarContratosVigentesNosCargosDaExtincao(ExtincaoCargo ec) {
        List<Cargo> cargos = extincaoCargoFacade.recuperarCargosDaExtincao(ec);
        String hql = "select cfp from ContratoFP cfp" +
                " where cfp.cargo in :cargos" +
                "   and :dataExtincao between cfp.inicioVigencia and coalesce(cfp.finalVigencia, :dataExtincao)" +
                "order by cfp.matriculaFP.matricula, cfp.numero";
        Query q = em.createQuery(hql);
        q.setParameter("cargos", cargos);
        q.setParameter("dataExtincao", ec.getDataExtincao());
        List<ContratoFP> contratos = q.getResultList();
        return contratos;
    }

    public void definirProvimento(ItemDisponibilidade id) {
        if (id.getProvimentoFP() != null && id.getContratoFP().getProvimentoFP() != null && id.getProvimentoFP().equals(id.getContratoFP().getProvimentoFP())) {
            ContratoFP c = em.find(ContratoFP.class, id.getContratoFP().getId());
            c.getSituacaoFuncionals().size();
            id.setContratoFP(c);

            ProvimentoFP pAntigo = id.getProvimentoFP();
            ProvimentoFP pNovo = provimentoFPFacade.recuperarProvimentoAnterior(pAntigo);
            id.getContratoFP().setProvimentoFP(pNovo);
            id.setProvimentoFP(null);

            Integer i = id.getContratoFP().getSituacaoFuncionals().indexOf(id.getSituacaoContratoFP());
            try {
                id.getContratoFP().getSituacaoFuncionals().get(i + 1).setFinalVigencia(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            id.getContratoFP().getSituacaoFuncionals().remove(id.getSituacaoContratoFP());
            id.setSituacaoContratoFP(null);
            id.setContratoFP(em.merge(id.getContratoFP()));
            em.merge(id);
            pAntigo = em.merge(pAntigo);
            em.remove(pAntigo);
        }
    }

    private Disponibilidade recarregarContratosDosItensSelecionados(Disponibilidade d) {
        for (ItemDisponibilidade id : d.getItensDiponibilidade()) {
            ContratoFP c = em.find(ContratoFP.class, id.getContratoFP().getId());
            c.getSituacaoFuncionals().size();
            id.setContratoFP(c);
        }
        return d;
    }

    public Disponibilidade removerItensNaoMarcados(Disponibilidade d) {
        for (Iterator<ItemDisponibilidade> it = d.getItensDiponibilidade().iterator(); it.hasNext(); ) {
            ItemDisponibilidade id = it.next();
            if (!id.isSelecionadoEmLista()) {
                definirProvimento(id);
                it.remove();
            }
        }
        return d;
    }

    public void salvarGeral(Disponibilidade d) {
        d = removerItensNaoMarcados(d);
        d = recarregarContratosDosItensSelecionados(d);
        d = colocarContratosEmSituacaoFuncionalDeDisponibilidade(d);
        d = criarProvimentoDeDisponibilidade(d);
        em.merge(d);
    }

    @Override
    public void salvar(Disponibilidade entity) {
        salvarGeral(entity);
    }

    @Override
    public void salvarNovo(Disponibilidade entity) {
        salvarGeral(entity);
    }

    private Disponibilidade colocarContratosEmSituacaoFuncionalDeDisponibilidade(Disponibilidade d) {
        for (ItemDisponibilidade id : d.getItensDiponibilidade()) {
            SituacaoContratoFP sc = contratoFPFacade.colocarContratoFPEmSituacaoFuncional(id.getContratoFP(), id.getDataDisponibilidade(), Long.parseLong("" + CODIGO_SITUACAO_FUNCIONAL_DISPONIBILIDADE));
            id.setSituacaoContratoFP(sc);
        }
        return d;
    }

    private Disponibilidade criarProvimentoDeDisponibilidade(Disponibilidade d) {
        for (ItemDisponibilidade id : d.getItensDiponibilidade()) {
            ProvimentoFP p = contratoFPFacade.adicionarProvimentoNoContrato(id.getContratoFP(), id.getDataDisponibilidade(), null, CODIGO_PROVIMENTO_DISPONIBILIDADE);
            id.setProvimentoFP(p);
        }
        return d;
    }
}
