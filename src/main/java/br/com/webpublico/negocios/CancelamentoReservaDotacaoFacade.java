package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CancelamentoReservaDotacao;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.enums.SituacaoSolicitacaoCancelamentoReservaDotacao;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 09/06/14
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CancelamentoReservaDotacaoFacade extends AbstractFacade<CancelamentoReservaDotacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private SolicitacaoCancelamentoReservaDotacaoFacade solicitacaoCancelamentoReservaDotacaoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CancelamentoReservaDotacaoFacade() {
        super(CancelamentoReservaDotacao.class);
    }

    @Override
    public void salvarNovo(CancelamentoReservaDotacao entity) {
        entity.setNumero(singletonGeradorCodigo.getProximoCodigo(entity.getClass(), "numero"));

        OperacaoORC operacaoORC = getOperacaoOrc(entity);
        BigDecimal valor = entity.getSolicitacao().getValor();

        if (validarValor(valor, operacaoORC, entity)) {
            super.salvarNovo(entity);
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                entity.getSolicitacao().getFonteDespesaORC(),
                operacaoORC,
                TipoOperacaoORC.ESTORNO,
                valor,
                entity.getData(),
                entity.getSolicitacao().getUnidadeOrganizacional(),
                entity.getId().toString(),
                entity.getClass().getSimpleName(),
                entity.getNumero().toString(),
                "Cancelamento de Reserva de Dotação da Solicitação: " + entity.getSolicitacao().getNumero() + " de " + DataUtil.getDataFormatada(entity.getSolicitacao().getData()));
            saldoFonteDespesaORCFacade.gerarSaldoOrcamentarioSemRealizarValidacao(vo);

            entity.getSolicitacao().setSituacao(SituacaoSolicitacaoCancelamentoReservaDotacao.APROVADA);
            em.merge(entity.getSolicitacao());
        }
    }

    private boolean validarValor(BigDecimal valor, OperacaoORC operacaoORC, CancelamentoReservaDotacao entity) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExcecaoNegocioGenerica("O Saldo da Operação <b> " + operacaoORC.toString() + "</b> já está menor ou igual a ZERO.");
        }
        if (OperacaoORC.RESERVADO_POR_LICITACAO.equals(operacaoORC)) {
            saldoFonteDespesaORCFacade.validarValorColunaIndividual(
                valor,
                entity.getSolicitacao().getFonteDespesaORC(),
                entity.getData(),
                entity.getSolicitacao().getUnidadeOrganizacional(),
                operacaoORC);
        }
        return true;
    }


    private OperacaoORC getOperacaoOrc(CancelamentoReservaDotacao entity) {
        OperacaoORC operacaoORC = null;
        if (entity.getSolicitacao().getOrigemReservaFonte().equals(OrigemReservaFonte.LICITACAO)) {
            return OperacaoORC.RESERVADO_POR_LICITACAO;
        } else {
            return OperacaoORC.RESERVADO;
        }
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public SolicitacaoCancelamentoReservaDotacaoFacade getSolicitacaoCancelamentoReservaDotacaoFacade() {
        return solicitacaoCancelamentoReservaDotacaoFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }
}
