/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.enums.TipoOperacaoORC;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author venon
 */
@Stateless
public class ReservaFonteDespesaOrcFacade extends AbstractFacade<ReservaFonteDespesaOrc> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LiberacaoFonteDespesaORCFacade liberacaoFonteDespesaORCFacade;
//    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
//    private SistemaControlador sistemaControlador;

    public ReservaFonteDespesaOrcFacade() {
        super(ReservaFonteDespesaOrc.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Método que gera a Reserva de Dotação
     *
     * @param valor       notNull
     * @param fonte       notNull
     * @param origem      notNull
     * @param tipoCredito notNull
     * @return ReservaFonteDespesaOrc
     */
    public ReservaFonteDespesaOrc gerarReserva(BigDecimal valor, FonteDespesaORC fonte, OrigemReservaFonte origem, TipoOperacaoORC tipoCredito, UnidadeOrganizacional unidadeOrganizacional, String numeroOrigem, String historico) {
        try {
            ReservaFonteDespesaOrc reserva = new ReservaFonteDespesaOrc();
            reserva.setDataReserva(sistemaFacade.getDataOperacao());
            reserva.setFonteDespesaORC(fonte);
            reserva.setValor(valor);
            reserva.setOrigemReservaFonte(origem);
            em.persist(reserva);

            gerarSaldoFonteDespesaOrc(fonte, origem, tipoCredito, unidadeOrganizacional, reserva, numeroOrigem, historico);


            return reserva;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível gerar reserva de dotação. Detalhes do erro: " + e.getMessage());
        }
    }

    private void gerarSaldoFonteDespesaOrc(FonteDespesaORC fonte, OrigemReservaFonte origem, TipoOperacaoORC tipoCredito, UnidadeOrganizacional unidadeOrganizacional, ReservaFonteDespesaOrc reserva, String numeroOrigem, String historico) {
        if (OrigemReservaFonte.LICITACAO.equals(origem)) {
            gerarSaldoFonteDespesaOrcPorOperacaoOrc(fonte, tipoCredito, unidadeOrganizacional, reserva, OperacaoORC.RESERVADO_POR_LICITACAO, numeroOrigem, historico);
        } else {
            gerarSaldoFonteDespesaOrcPorOperacaoOrc(fonte, tipoCredito, unidadeOrganizacional, reserva, OperacaoORC.RESERVADO, numeroOrigem, historico);
        }
    }

    private void gerarSaldoFonteDespesaOrcPorOperacaoOrc(FonteDespesaORC fonte, TipoOperacaoORC tipoCredito, UnidadeOrganizacional unidade, ReservaFonteDespesaOrc reserva, OperacaoORC operacaoORC, String numeroOrigem, String historico) {
        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
            fonte,
            operacaoORC,
            tipoCredito,
            reserva.getValor(),
            reserva.getDataReserva(),
            unidade,
            reserva.getId().toString(),
            reserva.getClass().getSimpleName(),
            numeroOrigem,
            historico);
        saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
    }

    public void gerarReservaSolicitacaoEmpenho(SolicitacaoEmpenho a, OrigemReservaFonte origem, String numeroOrigem) {
        try {
            gerarReserva(a.getValor(), a.getFonteDespesaORC(), origem, TipoOperacaoORC.NORMAL, a.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(), numeroOrigem, a.getComplementoHistorico());
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível gerar reserva de dotação. Detalhes do erro: " + e.getMessage());
        }
    }

    public List<ReservaFonteDespesaOrc> recuperaReservasPorAnulacaoOrc(AnulacaoORC a) {
        String sql = "SELECT re.* FROM reservafontedespesaorc "
            + "re INNER JOIN anulacaoreserva ar ON re.id = ar.reservafontedespesaorc_id "
            + "WHERE ar.anulacaoorc_id = :param";
        Query q = em.createNativeQuery(sql, ReservaFonteDespesaOrc.class);
        q.setParameter("param", a.getId());
        return q.getResultList();
    }

    public ReservaFonteDespesaOrc recuperaReservaPorAnulacaoOrc(AnulacaoORC a) {
        String sql = "SELECT re.* FROM reservafontedespesaorc "
            + "re INNER JOIN anulacaoreserva ar ON re.id = ar.reservafontedespesaorc_id "
            + "WHERE ar.anulacaoorc_id = :param ORDER BY re.id DESC";
        Query q = em.createNativeQuery(sql, ReservaFonteDespesaOrc.class);
        q.setParameter("param", a.getId());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (ReservaFonteDespesaOrc) q.getResultList().get(0);
    }

    public ReservaFonteDespesaOrc recuperaReservaPorReservaDotacao(ReservaDotacao rd) {
        String sql = "SELECT re.* FROM reservafontedespesaorc re "
            + "INNER JOIN reservadotacaoreservafonte rd ON re.id = rd.reservafontedespesaorc_id "
            + "WHERE rd.reservadotacao_id = :param";
        Query q = em.createNativeQuery(sql, ReservaFonteDespesaOrc.class);
        q.setParameter("param", rd.getId());
        return (ReservaFonteDespesaOrc) q.getSingleResult();
    }
}
