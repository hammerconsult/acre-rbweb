package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContaContabil;
import br.com.webpublico.entidades.TransferenciaSaldoContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TransferenciaSaldoContaAuxiliarFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-transferencia-de-saldo-conta-auxiliar", pattern = "/transferencia-de-saldo-conta-auxiliar/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/transferenciasaldocontaauxiliar/edita.xhtml"),
    @URLMapping(id = "editar-transferencia-de-saldo-conta-auxiliar", pattern = "/transferencia-de-saldo-conta-auxiliar/editar/#{transferenciaSaldoContaAuxiliarControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/transferenciasaldocontaauxiliar/edita.xhtml"),
    @URLMapping(id = "listar-transferencia-de-saldo-conta-auxiliar", pattern = "/transferencia-de-saldo-conta-auxiliar/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/transferenciasaldocontaauxiliar/lista.xhtml"),
    @URLMapping(id = "ver-transferencia-de-saldo-conta-auxiliar", pattern = "/transferencia-de-saldo-conta-auxiliar/ver/#{transferenciaSaldoContaAuxiliarControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/transferenciasaldocontaauxiliar/visualizar.xhtml"),
})
public class TransferenciaSaldoContaAuxiliarControlador extends PrettyControlador<TransferenciaSaldoContaAuxiliar> implements Serializable, CRUD {

    @EJB
    private TransferenciaSaldoContaAuxiliarFacade facade;
    private BigDecimal saldoAuxiliarDetalhadoCredito;
    private BigDecimal saldoAuxiliarDetalhadoDebito;
    private BigDecimal saldoAuxiliarCredito;
    private BigDecimal saldoAuxiliarDebito;

    public TransferenciaSaldoContaAuxiliarControlador() {
        super(TransferenciaSaldoContaAuxiliar.class);
    }

    @URLAction(mappingId = "novo-transferencia-de-saldo-conta-auxiliar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        inicializarSaldoCredito();
        inicializarSaldoDebito();
    }

    private void inicializarSaldoDebito() {
        saldoAuxiliarDetalhadoDebito = BigDecimal.ZERO;
        saldoAuxiliarDebito = BigDecimal.ZERO;
    }

    private void inicializarSaldoCredito() {
        saldoAuxiliarDetalhadoCredito = BigDecimal.ZERO;
        saldoAuxiliarCredito = BigDecimal.ZERO;
    }

    @URLAction(mappingId = "editar-transferencia-de-saldo-conta-auxiliar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        atualizarSaldoDebito();
        atualizarSaldoCredito();
    }

    @URLAction(mappingId = "ver-transferencia-de-saldo-conta-auxiliar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<ContaAuxiliarDetalhada> completarContasAuxiliaresDetalhadas(String filtro) {
        if (selecionado.getContaContabil() != null) {
            return facade.getContaFacade().buscarContasAuxiliaresDetalhadasPorCodigoOrDescricao(filtro, facade.getSistemaFacade().getExercicioCorrente(), selecionado.getUnidadeOrganizacional(), selecionado.getContaContabil());
        }
        return Lists.newArrayList();
    }

    public List<ContaContabil> completarContasContabeis(String filtro) {
        return facade.getContaFacade().buscarContasContabeisPorCodigoOrDescricaoAndExercicio(filtro.trim(), facade.getSistemaFacade().getExercicioCorrente());
    }

    public void atribuirContaAuxiliarDebitoAndSaldoAtual() {
        if (selecionado.getContaAuxiliarDetalhadaDeb() != null) {
            selecionado.setContaAuxiliarDebito(facade.buscarContaAuxiliarPorContaAuxiliarDetalhada(selecionado.getContaAuxiliarDetalhadaDeb()));
            atualizarSaldoDebito();
        } else {
            selecionado.setContaAuxiliarDebito(null);
            inicializarSaldoDebito();
        }
    }

    private void atualizarSaldoDebito() {
        saldoAuxiliarDetalhadoDebito = facade.buscarSaldoContabilAtual(selecionado.getContaAuxiliarDetalhadaDeb(), selecionado.getUnidadeOrganizacional());
        saldoAuxiliarDebito = facade.buscarSaldoContabilAtual(selecionado.getContaAuxiliarDebito(), selecionado.getUnidadeOrganizacional());
    }

    public void atribuirContaAuxiliarCreditoAndSaldoAtual() {
        if (selecionado.getContaAuxiliarDetalhadaCred() != null) {
            selecionado.setContaAuxiliarCredito(facade.buscarContaAuxiliarPorContaAuxiliarDetalhada(selecionado.getContaAuxiliarDetalhadaCred()));
            atualizarSaldoCredito();
        } else {
            selecionado.setContaAuxiliarCredito(null);
            inicializarSaldoCredito();
        }
    }

    private void atualizarSaldoCredito() {
        saldoAuxiliarDetalhadoCredito = facade.buscarSaldoContabilAtual(selecionado.getContaAuxiliarDetalhadaCred(), selecionado.getUnidadeOrganizacional());
        saldoAuxiliarCredito = facade.buscarSaldoContabilAtual(selecionado.getContaAuxiliarCredito(), selecionado.getUnidadeOrganizacional());
    }

    public void limparContasAuxiliares() {
        selecionado.setContaAuxiliarCredito(null);
        selecionado.setContaAuxiliarDebito(null);
        selecionado.setContaAuxiliarDetalhadaCred(null);
        selecionado.setContaAuxiliarDetalhadaDeb(null);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);

            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void validarCampos() {
        selecionado.validarCamposObrigatorios();
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        if (selecionado.getContaAuxiliarDetalhadaCred().equals(selecionado.getContaAuxiliarDetalhadaDeb())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione contas auxiliares diferentes para efetuar a transferência.");
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor deve ser maior que 0 (zero)!");
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/transferencia-de-saldo-conta-auxiliar/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String getSaldoAuxiliarDetalhadoCreditoAsString() {
        return getSaldoAsString(saldoAuxiliarDetalhadoCredito);
    }

    private String getSaldoAsString(BigDecimal saldo) {
        return saldo.compareTo(BigDecimal.ZERO) >= 0 ? Util.formataValor(saldo) + " - C" : Util.formataValor(saldo.multiply(new BigDecimal("-1"))) + " - D";
    }

    public String getSaldoAuxiliarDetalhadoDebitoAsString() {
        return getSaldoAsString(saldoAuxiliarDetalhadoDebito);
    }

    public String getSaldoAuxiliarCreditoAsString() {
        return getSaldoAsString(saldoAuxiliarCredito);
    }

    public String getSaldoAuxiliarDebitoAsString() {
        return getSaldoAsString(saldoAuxiliarDebito);
    }
}
