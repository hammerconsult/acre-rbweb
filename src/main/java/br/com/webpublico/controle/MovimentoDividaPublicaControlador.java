/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigMovDividaPublica;
import br.com.webpublico.entidades.DividaPublica;
import br.com.webpublico.entidades.MovimentoDividaPublica;
import br.com.webpublico.entidades.SaldoDividaPublica;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.MovimentoDividaPublicaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean(name = "movimentoDividaPublicaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-movimento-divida-publica", pattern = "/movimento-divida-publica/novo/", viewId = "/faces/financeiro/orcamentario/dividapublica/movimentodividapublica/edita.xhtml"),
    @URLMapping(id = "edita-movimento-divida-publica", pattern = "/movimento-divida-publica/editar/#{movimentoDividaPublicaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/movimentodividapublica/edita.xhtml"),
    @URLMapping(id = "listar-movimento-divida-publica", pattern = "/movimento-divida-publica/listar/", viewId = "/faces/financeiro/orcamentario/dividapublica/movimentodividapublica/lista.xhtml"),
    @URLMapping(id = "ver-movimento-divida-publica", pattern = "/movimento-divida-publica/ver/#{movimentoDividaPublicaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/movimentodividapublica/visualizar.xhtml")
})
public class MovimentoDividaPublicaControlador extends PrettyControlador<MovimentoDividaPublica> implements Serializable, CRUD {

    @EJB
    private MovimentoDividaPublicaFacade movimentoDividaPublicaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public MovimentoDividaPublicaControlador() {
        super(MovimentoDividaPublica.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return movimentoDividaPublicaFacade;
    }

    public List<DividaPublica> completarDividaPublica(String parte) {
        return movimentoDividaPublicaFacade.getDividaPublicaFacade().buscarDividasPublicasPorUsuario(parte.trim(), sistemaControlador.getUsuarioCorrente(), sistemaControlador.getDataOperacao(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public List<SelectItem> buscarContasDeDestinacao() {
        if (selecionado.getDividaPublica() != null) {
            return Util.getListSelectItem(movimentoDividaPublicaFacade.getDividaPublicaFacade().buscarContasDeDestinacaoPorDividaPublica(selecionado.getDividaPublica(), TipoValidacao.MOVIMENTO_DIVIDA_PUBLICA, sistemaControlador.getExercicioCorrente(), null));
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (OperacaoMovimentoDividaPublica operacao : OperacaoMovimentoDividaPublica.values()) {
            if (!(OperacaoMovimentoDividaPublica.PAGAMENTO_AMORTIZACAO.equals(operacao)
                || OperacaoMovimentoDividaPublica.RECEITA_OPERACAO_CREDITO.equals(operacao)
                || OperacaoMovimentoDividaPublica.EMPENHO.equals(operacao))) {
                retorno.add(new SelectItem(operacao, operacao.getDescricao()));
            }
        }
        return retorno;
    }

    public List<SelectItem> getTiposDeLancamento() {
        return Util.getListSelectItemSemCampoVazio(TipoLancamento.values(), false);
    }

    @URLAction(mappingId = "novo-movimento-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setData(sistemaControlador.getDataOperacao());
        selecionado.setValor(BigDecimal.ZERO);
        selecionado.setSaldo(BigDecimal.ZERO);
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        if (movimentoDividaPublicaFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", movimentoDividaPublicaFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-movimento-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "edita-movimento-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            if (isOperacaoNovo()) {
                validarCampos();
                movimentoDividaPublicaFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(selecionado.getUnidadeOrganizacionalAdm(), selecionado.getUnidadeOrganizacional(), selecionado.getData());
                movimentoDividaPublicaFacade.salvarNovo(selecionado);
            } else {
                Util.validarCampos(selecionado);
                movimentoDividaPublicaFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public String atribuirEvento() {
        try {
            selecionado.setEventoContabil(null);
            if (selecionado.getTipoLancamento() != null && selecionado.getOperacaoMovimentoDividaPublica() != null && selecionado.getDividaPublica() != null) {
                ConfigMovDividaPublica configMovDividaPublica = movimentoDividaPublicaFacade.getConfigMovDividaPublicaFacade().recuperaConfiguracaoExistente(selecionado);
                selecionado.setEventoContabil(configMovDividaPublica.getEventoContabil());
                return selecionado.getEventoContabil().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    public String alteraLabelDividaPublica() {
        if (selecionado.getDividaPublica().getNaturezaDividaPublica() != null) {
            if (NaturezaDividaPublica.OPERACAO_CREDITO.equals(selecionado.getDividaPublica().getNaturezaDividaPublica())) {
                return "Operação de Crédito";
            } else if (NaturezaDividaPublica.PRECATORIO.equals(selecionado.getDividaPublica().getNaturezaDividaPublica())) {
                return "Precatórios";
            } else {
                return "Dívida Pública";
            }
        }
        return "";
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero (0).");
        }
        ve.lancarException();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/movimento-divida-publica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void selecionarDividaPublica(ActionEvent evento) {
        selecionado.setDividaPublica(((DividaPublica) evento.getComponent().getAttributes().get("objeto")));
    }

    public void atualizarFonteDeRecursos() {
        if (selecionado.getContaDeDestinacao() != null) {
            selecionado.setFonteDeRecursos(selecionado.getContaDeDestinacao().getFonteDeRecursos());
        }
    }

    public BigDecimal recuperarSaldoDividaPublicaLongoPrazo() {
        return recuperarSaldoDividaPublica(Intervalo.LONGO_PRAZO);
    }

    public BigDecimal recuperarSaldoDividaPublicaCurtoPrazo() {
        return recuperarSaldoDividaPublica(Intervalo.CURTO_PRAZO);
    }

    public BigDecimal recuperarSaldoTotalDividaPublica() {
        return recuperarSaldoDividaPublicaCurtoPrazo().add(recuperarSaldoDividaPublicaLongoPrazo());
    }

    private BigDecimal recuperarSaldoDividaPublica(Intervalo intervalo) {
        BigDecimal saldo = BigDecimal.ZERO;
        try {
            if (selecionado.getDividaPublica() != null
                && selecionado.getUnidadeOrganizacional() != null
                && selecionado.getFonteDeRecursos() != null
                && selecionado.getData() != null) {
                SaldoDividaPublica saldoDivida = movimentoDividaPublicaFacade.getSaldoDividaPublicaFacade().recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(
                    selecionado.getData(),
                    selecionado.getUnidadeOrganizacional(),
                    selecionado.getDividaPublica(),
                    intervalo,
                    selecionado.getContaDeDestinacao());
                if (saldoDivida != null && saldoDivida.getId() != null) {
                    saldo = saldoDivida.getSaldoReal();
                }
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        return saldo;
    }
}
