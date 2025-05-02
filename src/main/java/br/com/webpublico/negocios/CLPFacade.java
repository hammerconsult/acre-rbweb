/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@Stateless
public class CLPFacade extends AbstractFacade<CLP> {

    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ClpHistoricoContabilFacade clpHistoricoContabilFacade;
    @EJB
    private TipoContaAuxiliarFacade tipoContaAuxiliarFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private TagOccFacade cLPTagFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CLPFacade() {
        super(CLP.class);
    }

    @Override
    public CLP recuperar(Object id) {
        CLP c = getEntityManager().find(CLP.class, id);
        c.getLcps().size();
        return c;
    }

    public boolean validaCodigo(CLP cLPConfiguracao, Exercicio exercicio) {
        String sql = "SELECT * FROM CLP "
            + "WHERE codigo= :parte AND exercicio_id = :exercicio AND fimvigencia IS null";
        Query q = em.createNativeQuery(sql, CLP.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("parte", cLPConfiguracao.getCodigo().trim());
        List<CLP> lista = q.getResultList();
        if (lista.contains(cLPConfiguracao)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<CLP> listaFiltrandoClpConfiguracaoVigentes(String parte, Date data) {
        String sql = "SELECT CLP.* "
            + "FROM CLP CLP "
            + "WHERE ((LOWER(CLP.CODIGO) LIKE :param) OR (LOWER(CLP.NOME) LIKE :param))"
            + "  AND (trunc(fimvigencia) >= to_date(:data, 'dd/mm/yyyy') OR fimvigencia IS null) ";
        Query q = em.createNativeQuery(sql, CLP.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(data));
        return q.getResultList();
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ClpHistoricoContabilFacade getClpHistoricoContabilFacade() {
        return clpHistoricoContabilFacade;
    }

    public TagOccFacade getcLPTagFacade() {
        return cLPTagFacade;
    }

    public TipoContaAuxiliarFacade getTipoContaAuxiliarFacade() {
        return tipoContaAuxiliarFacade;
    }

    public boolean validaLancamentosContabeis(CLP clP) {
        String sql = "SELECT lc.* FROM lancamentocontabil lc"
            + " INNER JOIN lcp lcp ON lcp.id = lc.lcp_id"
            + " WHERE lcp.clp_id = :id";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("id", clP.getId());
        try {
            if (consulta.getResultList().isEmpty()) {
                return true;
            }
        } catch (NoResultException e) {
        }
        return false;
    }

    public void validaCodigoNomeVigenciaMesmoData(CLP clpConf, Exercicio exercicioCorrente) {

        String sql = "SELECT * FROM CLP "
            + "WHERE codigo= :codigo "
            + "AND nome = :nome "
            + "AND to_date(:data, 'dd/mm/yyyy') between trunc(iniciovigencia) AND coalesce(trunc(fimvigencia), to_date(:data, 'dd/mm/yyyy')) "
            + "AND exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql, CLP.class);
        q.setParameter("exercicio", exercicioCorrente.getId());
        q.setParameter("codigo", clpConf.getCodigo().trim());
        q.setParameter("nome", clpConf.getNome().trim());
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        q.setMaxResults(1);
        try {
            List<CLP> lista = q.getResultList();
            if (!lista.isEmpty()) {
                if (clpConf.getId() != null) {
                    if (!lista.get(0).equals(clpConf)) {
                        throw new ExcecaoNegocioGenerica("Possui uma Configuração de CLP vigente cadastrada com o mesmo código, descrição e a data.");
                    }
                } else {
                    throw new ExcecaoNegocioGenerica("Possui uma Configuração de CLP vigente cadastrada com o mesmo código, descrição e a data.");
                }
            }
        } catch (NoResultException e) {
        }
    }

    public void meuSalvar(CLP clpSelecionado, CLP clpNaoAlterada) {
        verificaAlteracoesDoEvento(clpSelecionado, clpNaoAlterada);
        if (clpSelecionado.getId() == null) {
            clpSelecionado.setExercicio(((SistemaControlador) Util.getControladorPeloNome("sistemaControlador")).getExercicioCorrente());
            salvarNovo(clpSelecionado);
        } else {
            salvar(clpSelecionado);
        }
    }

    private void verificaAlteracoesDoEvento(CLP clpSelecionado, CLP clpNaoAlterada) {

        if (clpSelecionado.getId() == null) {
            return;
        }
        boolean alteroEvento = false;

        if (!clpNaoAlterada.getInicioVigencia().equals(clpSelecionado.getInicioVigencia())) {
            alteroEvento = true;
        }
        for (LCP lcp : clpNaoAlterada.getLcps()) {
            if (!clpSelecionado.getLcps().contains(lcp)) {
                alteroEvento = true;
            } else {
                try {
                    LCP lcpDoSelecinado = clpSelecionado.getLcps().get(0);
                    if (lcp.meuHashCode() != lcpDoSelecinado.meuHashCode()) {
                        alteroEvento = true;
                    }
                } catch (Exception e) {
                }
            }
        }

        if (alteroEvento) {
            List<EventoContabil> eventos = recuperaEventosDaCLP(clpSelecionado);
            for (EventoContabil eventoContabil : eventos) {
                eventoContabilFacade.geraEventosReprocessar(eventoContabil, clpSelecionado.getId(), CLP.class.getSimpleName());
            }
        }
    }

    private List<EventoContabil> recuperaEventosDaCLP(CLP clpSelecionado) {
        String sql = "SELECT DISTINCT e.* FROM eventocontabil e"
            + " INNER JOIN itemeventoclp item ON e.id = item.eventocontabil_id"
            + " INNER JOIN clp ON item.clp_id = clp.id"
            + " WHERE clp.id = :clp";
        Query consulta = em.createNativeQuery(sql, EventoContabil.class);
        consulta.setParameter("clp", clpSelecionado.getId());
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<EventoContabil> validaVinculoComEventoContabil(CLP clP) {
        String sql = "SELECT e.* FROM EVENTOCONTABIL E"
            + "      INNER JOIN ITEMEVENTOCLP IE ON E.ID = IE.EVENTOCONTABIL_ID"
            + "      INNER JOIN CLP ON IE.CLP_ID = CLP.ID"
            + "           WHERE clp.id = :clp";

        Query consulta = em.createNativeQuery(sql, EventoContabil.class);
        consulta.setParameter("clp", clP.getId());
        try {
            return consulta.getResultList();
        } catch (NoResultException e) {
        }
        return new ArrayList<>();
    }

    public List<LCP> buscarLCPs(Conta conta, Date data) {
        Query consulta = em.createNativeQuery("select clp.* from CLP clp "
            + " inner join lcp lcp on clp.id = lcp.clp_id"
            + " where ("
            + "        lcp.contaCredito_id = :conta or"
            + "        lcp.contaCreditoInterEstado_id = :conta or"
            + "        lcp.contaCreditoInterMunicipal_id = :conta or"
            + "        lcp.contaCreditoInterUniao_id = :conta or"
            + "        lcp.contaCreditoIntra_id = :conta or"
            + "        lcp.contaDebito_id = :conta or"
            + "        lcp.contaDebitoInterEstado_id = :conta or"
            + "        lcp.contaDebitoInterMunicipal_id = :conta or"
            + "        lcp.contaDebitoInterUniao_id = :conta or"
            + "        lcp.contaDebitoIntra_id = :conta)" +
            " and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(clp.inicioVigencia) and coalesce(trunc(clp.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ", CLP.class);
        try {
            consulta.setParameter("conta", conta.getId());
            consulta.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
            consulta.setMaxResults(1);
            CLP clpRecuperada = (CLP) consulta.getSingleResult();
            return clpRecuperada.getLcps();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<CLP> buscarCLPComContasPorEventoContabil(EventoContabil eventoContabil, Date data) {
        if (eventoContabil == null || eventoContabil.getId() == null) return Lists.newArrayList();
        String sql = "select c.* " +
            " from eventocontabil eve " +
            "    inner join itemeventoclp i on eve.id = i.eventocontabil_id " +
            "    inner join clp c on i.clp_id = c.id " +
            " where eve.id = :eventoContabil" +
            "   and to_date(:dataoperacao,'dd/mm/yyyy') between c.iniciovigencia and coalesce(c.fimvigencia,to_date(:dataoperacao,'dd/mm/yyyy'))";
        Query consulta = em.createNativeQuery(sql, CLP.class);
        consulta.setParameter("eventoContabil", eventoContabil.getId());
        consulta.setParameter("dataoperacao", DataUtil.getDataFormatada(data));
        return consulta.getResultList();
    }

    public boolean isCLPUtilizadaEmEventoContabilQueNaoForDeAjusteManual(CLP clp) {
        if (clp == null || clp.getId() == null) return false;
        String sql = "select eve.id " +
            " from eventocontabil eve " +
            "    inner join itemeventoclp i on eve.id = i.eventocontabil_id " +
            " where i.clp_id = :idClp " +
            "   and eve.tipoeventocontabil <> :ajusteContabilManual ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idClp", clp.getId());
        q.setParameter("ajusteContabilManual", TipoEventoContabil.AJUSTE_CONTABIL_MANUAL.name());
        return !q.getResultList().isEmpty();
    }
}
