package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AjusteDepositoEstornoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by mga on 24/08/2017.
 */
@ManagedBean(name = "ajusteDepositoEstornoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-estorno-ajuste-deposito", pattern = "/estorno-ajuste-deposito/novo/", viewId = "/faces/financeiro/extraorcamentario/ajustedepositoestorno/edita.xhtml"),
    @URLMapping(id = "edita-estorno-ajuste-deposito", pattern = "/estorno-ajuste-deposito/editar/#{ajusteDepositoEstornoControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/ajustedepositoestorno/edita.xhtml"),
    @URLMapping(id = "listar-estorno-ajuste-deposito", pattern = "/estorno-ajuste-deposito/listar/", viewId = "/faces/financeiro/extraorcamentario/ajustedepositoestorno/lista.xhtml"),
    @URLMapping(id = "ver-estorno-ajuste-deposito", pattern = "/estorno-ajuste-deposito/ver/#{ajusteDepositoEstornoControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/ajustedepositoestorno/visualizar.xhtml")
})
public class AjusteDepositoEstornoControlador extends PrettyControlador<AjusteDepositoEstorno> implements Serializable, CRUD {

    @EJB
    private AjusteDepositoEstornoFacade facade;
    private List<ReceitaExtra> receitasExtrasSelecionadas;
    private List<ReceitaExtra> receitasExtras;

    public AjusteDepositoEstornoControlador() {
        super(AjusteDepositoEstorno.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-ajuste-deposito/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-estorno-ajuste-deposito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataEstorno(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        if (facade.getSingletonReprocessamento().isCalculando()) {
            FacesUtil.addWarn("Aviso!", facade.getSingletonReprocessamento().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "edita-estorno-ajuste-deposito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        receitasExtrasSelecionadas = Lists.newArrayList();
        recuperarReceitasDoAjuste();
    }

    @URLAction(mappingId = "ver-estorno-ajuste-deposito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarReceitasDoAjuste();
    }


    public boolean isRegistroEditavel() {
        return isOperacaoEditar();
    }

    public void definirAjuste() {
        selecionado.setEventoContabil(null);
        try {
            if (selecionado.getAjusteDeposito() != null) {
                definirEventoAndRecuperarReceita();
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void definirEventoAndRecuperarReceita() {
        EventoContabil eventoContabil = facade.recuperarEventoContabil(selecionado);
        if (eventoContabil != null) {
            selecionado.setEventoContabil(eventoContabil);
        }
        selecionado.setHistorico(selecionado.getAjusteDeposito().getHistorico());
        selecionado.setValor(BigDecimal.ZERO);
        recuperarReceitasDoAjuste();
        if (receitasExtrasSelecionadas == null) {
            receitasExtrasSelecionadas = Lists.newArrayList();
        } else {
            receitasExtrasSelecionadas.clear();
        }
        if (selecionado.getAjusteDeposito().isAjusteDiminutivo()) {
            selecionarReceita(receitasExtras.get(0));
        }
    }

    private void recuperarReceitasDoAjuste() {
        receitasExtras = facade.getAjusteDepositoFacade().buscarReceitaExtraPorAjuste(selecionado.getAjusteDeposito());
    }

    public void selecionarAjuste(ActionEvent event) {
        selecionado.setAjusteDeposito((AjusteDeposito) event.getComponent().getAttributes().get("objeto"));
        if (selecionado.getAjusteDeposito() != null) {
            definirEventoAndRecuperarReceita();
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarCampos();
            if (isOperacaoNovo()) {
                selecionado = facade.salvarNovo(selecionado, receitasExtrasSelecionadas);
                redirecionarParaVer();
            } else {
                facade.salvar(selecionado);
                redireciona();
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (isOperacaoNovo() && selecionado.getValor().compareTo(selecionado.getAjusteDeposito().getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b>"
                + Util.formataValor(selecionado.getValor()) + "</b> é maior que o saldo de <b>" + Util.formataValor(selecionado.getAjusteDeposito().getSaldo())
                + "</b> disponível para o ajuste em depósito do tipo: "
                + selecionado.getAjusteDeposito().getTipoAjuste().getDescricao() + ".");
        }
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor deve ser maior que zero (0).");
        }
        if (selecionado.getEventoContabil() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Nenhum evento contábil encontrado para este lançamento!");
        }
        ve.lancarException();
    }

    public String iconeTodos() {
        if (receitasExtrasSelecionadas.size() == receitasExtras.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String icone(ReceitaExtra receitaExtra) {
        if (receitasExtrasSelecionadas.contains(receitaExtra)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }


    public void selecionarTodasReceitas() {
        if (receitasExtrasSelecionadas.size() == receitasExtras.size()) {
            receitasExtrasSelecionadas.removeAll(receitasExtras);
        } else {
            for (ReceitaExtra receitaExtra : receitasExtras) {
                selecionarReceita(receitaExtra);
            }
        }
        selecionado.setValor(getValorTotalReceitaSelecionadas());
    }

    public void selecionarReceita(ReceitaExtra receitaExtra) {
        if (receitasExtrasSelecionadas.contains(receitaExtra)) {
            receitasExtrasSelecionadas.remove(receitaExtra);
        } else {
            receitasExtrasSelecionadas.add(receitaExtra);
        }
        selecionado.setValor(getValorTotalReceitaSelecionadas());
    }

    public void limparCamposAjuste() {
        selecionado.setAjusteDeposito(null);
        selecionado.setEventoContabil(null);
        receitasExtras = Lists.newArrayList();
        receitasExtrasSelecionadas = Lists.newArrayList();
        selecionado.setValor(BigDecimal.ZERO);
        selecionado.setHistorico(null);
    }

    private BigDecimal getValorTotalReceitaSelecionadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ReceitaExtra rec : receitasExtrasSelecionadas) {
            total = total.add(rec.getValor());
        }
        return total;
    }

    public BigDecimal getValorTotalReceitas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ReceitaExtra rec : receitasExtras) {
            total = total.add(rec.getValor());
        }
        return total;
    }

    public BigDecimal getSaldoTotalReceitas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ReceitaExtra rec : receitasExtras) {
            total = total.add(rec.getSaldo());
        }
        return total;
    }

    public boolean isReceitaAjusteDiminutivo(ReceitaExtra receitaExtra) {
        return isEstornoAjusteDiminutivo() || receitaExtra != null && receitaExtra.isReceitaAberta();
    }

    public boolean isEstornoAjusteDiminutivo() {
        return selecionado.getAjusteDeposito() != null && selecionado.getAjusteDeposito().isAjusteDiminutivo();
    }

    public boolean isReceitaEstornada(ReceitaExtra receitaExtra) {
        return !isEstornoAjusteDiminutivo() && receitaExtra != null && receitaExtra.isReceitaAberta();
    }

    public List<AjusteDeposito> completarAjusteDeposito(String parte) {
        return facade.getAjusteDepositoFacade().buscarAjusteDepositoPorUnidade(parte.trim(), selecionado.getUnidadeOrganizacional());
    }

    public ParametroEvento getParametroEvento() {
        return facade.getParametroEvento(selecionado);
    }

    public List<ReceitaExtra> getReceitasExtrasSelecionadas() {
        return receitasExtrasSelecionadas;
    }

    public void setReceitasExtrasSelecionadas(List<ReceitaExtra> receitasExtrasSelecionadas) {
        this.receitasExtrasSelecionadas = receitasExtrasSelecionadas;
    }

    public List<ReceitaExtra> getReceitasExtras() {
        return receitasExtras;
    }

    public void setReceitasExtras(List<ReceitaExtra> receitasExtras) {
        this.receitasExtras = receitasExtras;
    }
}
