/*
 * Codigo gerado automaticamente em Wed Apr 04 13:59:50 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConvenioDespesaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "convenioDespesaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-convenio-despesa", pattern = "/convenio-despesa/novo/", viewId = "/faces/financeiro/convenios/despesa/conveniodespesa/edita.xhtml"),
    @URLMapping(id = "editar-convenio-despesa", pattern = "/convenio-despesa/editar/#{convenioDespesaControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/conveniodespesa/edita.xhtml"),
    @URLMapping(id = "ver-convenio-despesa", pattern = "/convenio-despesa/ver/#{convenioDespesaControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/conveniodespesa/visualizar.xhtml"),
    @URLMapping(id = "listar-convenio-despesa", pattern = "/convenio-despesa/listar/", viewId = "/faces/financeiro/convenios/despesa/conveniodespesa/lista.xhtml")
})
public class ConvenioDespesaControlador extends PrettyControlador<ConvenioDespesa> implements Serializable, CRUD {

    @EJB
    private ConvenioDespesaFacade convenioDespesaFacade;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterFonteDespesaORC;
    private DespesaExercConvenio despesaExercConvenioSelecionado;
    private PrestacaoContas prestacaoContas;
    private ConvenioDespesaLiberacao convenioDespesaLiberacoes;
    private TramiteConvenioDesp tramiteConvenioDespSelecionado;
    private DespesaORC despesaORC;
    private PlanoAplicacao planoAplicacaoSelecionado;
    private PlanoAplicacaoCategDesp categoriaDespesaSelecionada;
    private PlanoAplicacaoContaDesp elementoDespesaSelecionado;
    private CronogramaDesembolso cronogramaDesembolsoSelecionado;
    private Interveniente intervenienteSelecionado;
    private AndamentoConvenio andamentoConvenio;
    private Aditivos aditivos;
    private List<Interveniente> intervenientes;
    private List<AndamentoConvenio> listaAndamentos;
    private List<AndamentoConvenio> andamentosRemovidos;
    private List<TramiteConvenioDesp> listaTramites;
    private Boolean pertenceEmenda;

    public ConvenioDespesaControlador() {
        super(ConvenioDespesa.class);
    }

    public ConvenioDespesaFacade getFacade() {
        return convenioDespesaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return convenioDespesaFacade;
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    public void salvar() {
        adicionaAndamentosAoSelecionado();
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                selecionado = convenioDespesaFacade.salvarNovoConvenio(selecionado, listaAndamentos);
                FacesUtil.addOperacaoRealizada(" Registro " + selecionado + " salvo com sucesso.");
            } else {
                selecionado = convenioDespesaFacade.salvar(selecionado, listaAndamentos, andamentosRemovidos);
                FacesUtil.addOperacaoRealizada(" Registro " + selecionado + " alterado com sucesso.");
            }
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        validarValores();
        verificarAoSalvarDataVigencia();
        verificarAoSalvarDataPeriodiciade();
        ValidacaoException ve = new ValidacaoException();
        for (DespesaExercConvenio despesa : selecionado.getDespesaExercConvenios()) {
            if (despesa.getId() != null && !despesa.getGerarSolicitacaoEmpenho() && convenioDespesaFacade.hasSolicitacaoEmpenho(despesa)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma solicitação de empenho para a dotação " + despesa.getFonteDespesaORC() + " portanto não é possível desmarcar a opção 'Gerar Solicitação de Empenho'.");
            }
        }
        if (selecionado.getDetentorArquivoComposicao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Arquivo anexo é obrigatório!");
        }
        if (pertenceEmenda && selecionado.getEmenda() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Emenda deve ser informado.");
        }
        ve.lancarException();
    }

    public List<TipoContaDespesa> getTipoContaDespesa() {
        return Arrays.asList(TipoContaDespesa.CONVENIO_DESPESA);
    }

    private void validarValores() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValorConvenio().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Valor do Convênio deve ser maior que zero (0).");
        }
        if (selecionado.getValorRepasse().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Valor de Repasse não pode ser negativo. ");
        }
        if (selecionado.getValorContrapartida().compareTo(selecionado.getValorConvenio()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Valor de Contrapartida não deve ser superior ao Valor do Convênio!");
        }
        ve.lancarException();
    }

    private void calculaValorFinalConvenioPorAditivo(AditivosConvenioDespesa acd) {
        selecionado.setValorConvenioAditivado(selecionado.getValorConvenioAditivado().subtract(acd.getAditivos().getValorAditivo()));
        selecionado.setValorContrapartidaAditivada(selecionado.getValorContrapartidaAditivada().subtract(acd.getAditivos().getValorContrapartida()));
        selecionado.setValorRepasseAditivado(selecionado.getValorConvenioAditivado().subtract(selecionado.getValorContrapartidaAditivada()));
    }

    public BigDecimal atribuirValorFinalAoConvenio(AditivosConvenioDespesa adr) {
        BigDecimal total = BigDecimal.ZERO;
        selecionado.setValorConvenioAditivado(selecionado.getValorConvenioAditivado().add(adr.getAditivos().getValorAditivo()));
        selecionado.setValorContrapartidaAditivada(selecionado.getValorContrapartidaAditivada().add(adr.getAditivos().getValorContrapartida()));
        selecionado.setValorRepasseAditivado(selecionado.getValorConvenioAditivado().subtract(selecionado.getValorContrapartidaAditivada()));
        selecionado.setDataVigenciaFinal(adr.getAditivos().getDataFimVigencia());
        return total;
    }

    public BigDecimal calcularTotalDespesaOrcamentarias() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (DespesaExercConvenio despesaExercConvenio : selecionado.getDespesaExercConvenios()) {
            total = total.add(despesaExercConvenio.getValor());
        }
        return total;
    }

    public BigDecimal calcularTotalLiberadoContrapartida() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ConvenioDespesaLiberacao convenioDespesaLiberacao : selecionado.getConvenioDespesaLiberacoes()) {
            total = total.add(convenioDespesaLiberacao.getValorLiberadoContrapartida());
        }
        return total;
    }

    public BigDecimal calcularTotalLiberadoConcedente() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ConvenioDespesaLiberacao convenioDespesaLiberacao : selecionado.getConvenioDespesaLiberacoes()) {
            total = total.add(convenioDespesaLiberacao.getValorLiberadoConcedente());
        }
        return total;
    }

    public BigDecimal calcularTotalConvenioCronogramaDesembolso() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (CronogramaDesembolso cronogramaDesembolso : selecionado.getCronogramaDesembolsos()) {
            total = total.add(cronogramaDesembolso.getValorConvenioCronograma());
        }
        return total;
    }

    public BigDecimal calcularTotalContrapartidaCronogramaDesembolso() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (CronogramaDesembolso cronogramaDesembolso : selecionado.getCronogramaDesembolsos()) {
            total = total.add(cronogramaDesembolso.getValorConvenioContapartida());
        }
        return total;
    }

    public BigDecimal calcularTotalContrapartidaPlanoTrabalho() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (PlanoAplicacao planoAplicacao : selecionado.getPlanoAplicacoes()) {
            total = total.add(planoAplicacao.getValorContrapartidaPlano());
        }
        return total;
    }

    public BigDecimal calcularTotalConvenioPlanoTrabalho() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (PlanoAplicacao planoAplicacao : selecionado.getPlanoAplicacoes()) {
            total = total.add(planoAplicacao.getValorConvenioPlano());
        }
        return total;
    }

    public BigDecimal calcularTotalCategoriaDespesa() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (PlanoAplicacaoCategDesp cat : planoAplicacaoSelecionado.getListaCategoriaDespesas()) {
            total = total.add(cat.getValor());
        }
        return total;
    }

    public BigDecimal calcularTotalCategoriaDespesaNoPlanoTrabalho(PlanoAplicacao plano) {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (PlanoAplicacaoCategDesp cat : plano.getListaCategoriaDespesas()) {
            total = total.add(cat.getValor());
        }
        return total;
    }

    public BigDecimal calcularTotalElementoDespesa() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (PlanoAplicacaoContaDesp elemento : planoAplicacaoSelecionado.getListaElementosDespesa()) {
            total = total.add(elemento.getValor());
        }
        return total;
    }

    public BigDecimal calcularTotalElementoDespesaNoPlanoTrabalho(PlanoAplicacao plano) {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (PlanoAplicacaoContaDesp elemento : plano.getListaElementosDespesa()) {
            total = total.add(elemento.getValor());
        }
        return total;
    }

    public BigDecimal calcularTotalAditivo() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (AditivosConvenioDespesa aditivo : selecionado.getAditivosConvenioDespesas()) {
            total = total.add(aditivo.getAditivos().getValorAditivo());
        }
        return total;
    }

    public BigDecimal calcularTotalAditivoContrapartida() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (AditivosConvenioDespesa aditivo : selecionado.getAditivosConvenioDespesas()) {
            total = total.add(aditivo.getAditivos().getValorContrapartida());
        }
        return total;
    }

    public BigDecimal getSomaEstornoPagamentos() {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (PagamentoEstorno pe : getListaPagamentoEstorno()) {
            estornos = estornos.add(pe.getValor());
        }
        return estornos;
    }

    public BigDecimal getSomaPagamentos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (Pagamento p : getListaPagamento()) {
            valor = valor.add(p.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaLiquidacoes() {
        BigDecimal liq = new BigDecimal(BigInteger.ZERO);
        for (Liquidacao l : getListaLiquidacao()) {
            liq = liq.add(l.getValor());
        }
        return liq;
    }

    public BigDecimal getSomaEstornoLiquidacoes() {
        BigDecimal liq = new BigDecimal(BigInteger.ZERO);
        for (LiquidacaoEstorno le : getListaLiquidacaoEstorno()) {
            liq = liq.add(le.getValor());
        }
        return liq;
    }

    public BigDecimal getSomaEmpenhos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (Empenho e : getListaEmpenhos()) {
            valor = valor.add(e.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaEstornoEmpenhos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (EmpenhoEstorno ee : getListaEmpenhoEstorno()) {
            valor = valor.add(ee.getValor());
        }
        return valor;
    }

    public void removerInterveniente(ActionEvent evt) {
        ConvenioDespInterveniente cri = (ConvenioDespInterveniente) evt.getComponent().getAttributes().get("removeInter");
        ConvenioDespesa cd = (selecionado);
        cd.getConvenioDespIntervenientes().remove(cri);
    }

    public void removerLiberacoes(ActionEvent evt) {
        ConvenioDespesaLiberacao cdl = (ConvenioDespesaLiberacao) evt.getComponent().getAttributes().get("removeLiberacao");
        selecionado.getConvenioDespesaLiberacoes().remove(cdl);
    }

    public void removeStatusPrestacaoContas(ActionEvent evt) {
        PrestacaoContas pc = (PrestacaoContas) evt.getComponent().getAttributes().get("removeStatusPrestacao");
        selecionado.getPrestacaoContas().remove(pc);
    }

    public void adicionaAditivos() {
        if (validaAditivo()) {
            AditivosConvenioDespesa acd = new AditivosConvenioDespesa();
            acd.setConvenioDespesa(selecionado);
            acd.setAditivos(aditivos);
            atribuirValorFinalAoConvenio(acd);
            selecionado.getAditivosConvenioDespesas().add(acd);
            aditivos = new Aditivos();
        }
    }

    public void editarAditivo(AditivosConvenioDespesa acd) {
        aditivos = acd.getAditivos();
        selecionado.getAditivosConvenioDespesas().remove(acd);
    }

    public void removerAditivos(ActionEvent evt) {
        AditivosConvenioDespesa acr = (AditivosConvenioDespesa) evt.getComponent().getAttributes().get("removeAditi");
        calculaValorFinalConvenioPorAditivo(acr);
        selecionado.getAditivosConvenioDespesas().remove(acr);
    }

    public Boolean validaAditivo() {
        Boolean toReturn = true;
        if (aditivos.getNumero() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Número é obrigatório para adicionar.");
            toReturn = false;
        }
        if (aditivos.getDescricao().trim().equals("")) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Descrição é obrigatório para adicionar.");
            toReturn = false;
        }
        if (aditivos.getValorAditivo().compareTo(BigDecimal.ZERO) < 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório", "  O campo Valor Aditivo deve ser maior ou igual a 0"));
            toReturn = false;
        }
        if (aditivos.getValorContrapartida().compareTo(BigDecimal.ZERO) < 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatóio ", " O campo Valor da Contrapartida deve ser maior ou igual a 0"));
            toReturn = false;
        }
        for (AditivosConvenioDespesa ad : selecionado.getAditivosConvenioDespesas()) {
            if (ad.getAditivos().getNumero().equals(aditivos.getNumero())) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe um Aditivo adicionado com o número: " + aditivos.getNumero());
                toReturn = false;
            }
        }
        return toReturn;
    }

    public void adicionaInterveniente() {
        ConvenioDespesa cd = (selecionado);
        if (intervenienteSelecionado != null) {
            if (intervenienteSelecionado.getId() != null) {
                if (podeAdicionarIntervinente(intervenienteSelecionado)) {
                    ConvenioDespInterveniente cdi = new ConvenioDespInterveniente();
                    cdi.setConvenioDespesa(cd);
                    cdi.setInterveniente(intervenienteSelecionado);
                    cd.getConvenioDespIntervenientes().add(cdi);
                    intervenienteSelecionado = new Interveniente();
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Interveniente " + intervenienteSelecionado + " já foi adicionado na Lista.");
                }
            }
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Selecione um Interveniente para adicionar na Lista.");
        }
    }

    private boolean podeAdicionarIntervinente(Interveniente intervenienteSelecionado) {
        for (ConvenioDespInterveniente convenioDespInterveniente : selecionado.getConvenioDespIntervenientes()) {
            if (convenioDespInterveniente.getInterveniente().equals(intervenienteSelecionado)) {
                return false;
            }
        }
        return true;
    }

    public void removerAndamento(ActionEvent evt) {
        AndamentoConvenio ac = (AndamentoConvenio) evt.getComponent().getAttributes().get("removeAnda");
        listaAndamentos.remove(ac);
    }

    public void adicionaAndamento() {
        if (podeAdicionarAndamento(andamentoConvenio)) {
            listaAndamentos.add(andamentoConvenio);
            andamentoConvenio = new AndamentoConvenio();
        }
    }

    private boolean podeAdicionarAndamento(AndamentoConvenio andamentoConvenio) {
        boolean valido = true;
        if (andamentoConvenio.getDataAndamento() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Data deve ser informado para adicionar.");
            valido = false;
        }
        if (andamentoConvenio.getInterveniente() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Interviniente deve ser informado para adicionar.");
            valido = false;
        }
        if (andamentoConvenio.getTipoInterveniente() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Tipo de Interveniente deve ser informado para adicionar.");
            valido = false;
        }
        return valido;
    }

    public void adicionaCronograma() {
        if (cronogramaDesembolsoSelecionado.getDataCronograma() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Data deve ser informado para adicionar.");
            return;
        }
        if (cronogramaDesembolsoSelecionado.getValorConvenioCronograma() != null) {
            if (cronogramaDesembolsoSelecionado.getValorConvenioCronograma().compareTo(BigDecimal.ZERO) < 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor Convênio Cronograma deve ser maior que zero (0).");
                return;
            }
        }
        if (cronogramaDesembolsoSelecionado.getValorConvenioContapartida() != null) {
            if (cronogramaDesembolsoSelecionado.getValorConvenioContapartida().compareTo(BigDecimal.ZERO) < 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor Contrapartida Cronograma  deve ser maior que zero (0).");
                return;
            }
        }
        ConvenioDespesa dp = (selecionado);
        cronogramaDesembolsoSelecionado.setConvenioDespesa(dp);
        dp.getCronogramaDesembolsos().add(cronogramaDesembolsoSelecionado);
        cronogramaDesembolsoSelecionado = new CronogramaDesembolso();
    }

    public void adicionaPlanoAplicacao() {
        if (validaPlanoTrabalho()) {
            ConvenioDespesa dp = (selecionado);
            planoAplicacaoSelecionado.setConvenioDespesa(dp);
            dp.getPlanoAplicacoes().add(planoAplicacaoSelecionado);
            planoAplicacaoSelecionado = new PlanoAplicacao();
            categoriaDespesaSelecionada = new PlanoAplicacaoCategDesp();
            elementoDespesaSelecionado = new PlanoAplicacaoContaDesp();
        }
    }

    public void adicionarCategoriaDespesa() {
        if (validaCategoriaDespesa()) {
            ConvenioDespesa dp = (selecionado);
            categoriaDespesaSelecionada.setPlanoAplicacao(planoAplicacaoSelecionado);
            planoAplicacaoSelecionado.getListaCategoriaDespesas().add(categoriaDespesaSelecionada);
            categoriaDespesaSelecionada = new PlanoAplicacaoCategDesp();
        }
    }

    public Boolean validaCategoriaDespesa() {
        if (categoriaDespesaSelecionada.getCategoriaDespesa() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Categoria de Despesa deve ser informado para adicionar.");
            return false;
        }
        if (categoriaDespesaSelecionada.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor deve ser maior que zero(0).");
            return false;
        }
        return true;
    }

    public void adicionarElementoDespesa() {
        if (validarElementoDespesa()) {
            ConvenioDespesa dp = (selecionado);
            elementoDespesaSelecionado.setPlanoAplicacao(planoAplicacaoSelecionado);
            planoAplicacaoSelecionado.getListaElementosDespesa().add(elementoDespesaSelecionado);
            elementoDespesaSelecionado = new PlanoAplicacaoContaDesp();
        }
    }

    public Boolean validarElementoDespesa() {
        if (elementoDespesaSelecionado.getElementoDespesa() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Elemento de Despesa deve ser informado para adicionar.");
            return false;
        }
        if (elementoDespesaSelecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor deve ser maior que zero(0).");
            return false;
        }
        return true;
    }

    public Boolean validaPlanoTrabalho() {
        if (planoAplicacaoSelecionado.getListaCategoriaDespesas().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Deve ser informado uma Categoria de Despesa clicando no botão 'Selecionar Categoria de Despesa'.");
            return false;
        }
//        if (planoAplicacaoSelecionado.getListaElementosDespesa().isEmpty()) {
//            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Deve ser informado uma Elemento de Despesa clicando no botão 'Selecionar Elemento de Despesa'.");
//            return false;
//        }
        if (planoAplicacaoSelecionado.getValorConvenioPlano().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor deve ser maior que zero(0).");
            return false;
        }
        for (PlanoAplicacao planoAplicacao : selecionado.getPlanoAplicacoes()) {
            if (planoAplicacao.getNumero().equals(planoAplicacaoSelecionado.getNumero())) {
                FacesUtil.addOperacaoNaoPermitida(" Já existe um Plano de Trabalho adicionado com o número: " + planoAplicacaoSelecionado.getNumero() + ".");
                return false;
            }
        }
        return true;
    }

    public void editarPlanoAplicacao(PlanoAplicacao plano) {
        planoAplicacaoSelecionado = plano;
        selecionado.getPlanoAplicacoes().remove(plano);
    }

    public void editarCategoriaDespesa(PlanoAplicacaoCategDesp planoAplicacaoCategDesp) {
        categoriaDespesaSelecionada = planoAplicacaoCategDesp;
        planoAplicacaoSelecionado.getListaCategoriaDespesas().remove(planoAplicacaoCategDesp);
    }

    public void editarElementoDespesa(PlanoAplicacaoContaDesp planoAplicacaoElemDesp) {
        elementoDespesaSelecionado = planoAplicacaoElemDesp;
        planoAplicacaoSelecionado.getListaElementosDespesa().remove(planoAplicacaoElemDesp);
    }

    public void adicionaDespesa() {
        ConvenioDespesa dp = (selecionado);
        if (despesaORC == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Dotação Orçamentária deve ser informado para adicionar. ");
        }
        if (despesaExercConvenioSelecionado.getFonteDespesaORC() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Fonte de Recurso deve ser informado para adicionar. ");
        } else {
            if (podeAdicionarContaDespesa(despesaORC)) {
                despesaExercConvenioSelecionado.setDespesaORC(despesaORC);
                despesaExercConvenioSelecionado.setExercicio(despesaORC.getExercicio());
                despesaExercConvenioSelecionado.setConvenioDespesa(dp);
                dp.getDespesaExercConvenios().add(despesaExercConvenioSelecionado);
                despesaExercConvenioSelecionado = new DespesaExercConvenio();
                despesaORC = null;
            } else {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Conta de Despesa " + despesaORC.getProvisaoPPADespesa().getContaDeDespesa() + " já foi adicionada na lista.");
            }
        }
    }

    private boolean podeAdicionarContaDespesa(DespesaORC despesaORCSelecionada) {
        for (DespesaExercConvenio despesaExercConvenio : selecionado.getDespesaExercConvenios()) {
            if (despesaExercConvenio.getDespesaORC().equals(despesaORCSelecionada)) {
                return false;
            }
        }
        return true;
    }

    public void adicionaOcorrencia() {
        ConvenioDespesa dp = (selecionado);
        if (tramiteConvenioDespSelecionado.getDataTramite() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "  O campo Data deve ser informado para adicionar.");
        }
        if (tramiteConvenioDespSelecionado.getOcorrenciaConvenioDesp() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Ocorrência deve ser informado para adicionar.");
        } else {
            if (podeAdicionarOcorrencia(tramiteConvenioDespSelecionado)) {
                tramiteConvenioDespSelecionado.setConvenioDespesa(dp);
                dp.getTramites().add(tramiteConvenioDespSelecionado);
                tramiteConvenioDespSelecionado = new TramiteConvenioDesp();
            } else {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Ocorrência " + tramiteConvenioDespSelecionado.getOcorrenciaConvenioDesp() + " já foi adicionado na Lista.");
            }
        }
    }

    public void editarOcorrencia(TramiteConvenioDesp tramite) {
        tramiteConvenioDespSelecionado = tramite;
        selecionado.getTramites().remove(tramite);
    }

    public void editarDotacaoOrcamentario(DespesaExercConvenio despesaExercConvenio) {
        despesaExercConvenioSelecionado = despesaExercConvenio;
        despesaORC = despesaExercConvenioSelecionado.getDespesaORC();
        selecionado.getDespesaExercConvenios().remove(despesaExercConvenio);
    }

    public void editarRecursoLiberado(ConvenioDespesaLiberacao convenioDespesaLib) {
        convenioDespesaLiberacoes = convenioDespesaLib;
        selecionado.getConvenioDespesaLiberacoes().remove(convenioDespesaLib);
    }

    private boolean podeAdicionarOcorrencia(TramiteConvenioDesp tramiteConvenioDespSelecionado) {
        for (TramiteConvenioDesp tramiteConvenioDesp : selecionado.getTramites()) {
            if (tramiteConvenioDesp.getOcorrenciaConvenioDesp().equals(tramiteConvenioDespSelecionado.getOcorrenciaConvenioDesp())) {
                return false;
            }
        }
        return true;
    }

    public void removeCronograma(ActionEvent evt) {
        ConvenioDespesa dp = (selecionado);
        CronogramaDesembolso cro = (CronogramaDesembolso) evt.getComponent().getAttributes().get("removeCrono");
        dp.getCronogramaDesembolsos().remove(cro);
    }

    public void removePlano(ActionEvent evt) {
        ConvenioDespesa dp = (selecionado);
        PlanoAplicacao pl = (PlanoAplicacao) evt.getComponent().getAttributes().get("removePlano");
        dp.getPlanoAplicacoes().remove(pl);
    }

    public void removerCategoriaDespesa(ActionEvent evt) {
        ConvenioDespesa dp = (selecionado);
        PlanoAplicacaoCategDesp pl = (PlanoAplicacaoCategDesp) evt.getComponent().getAttributes().get("removeCategoria");
        planoAplicacaoSelecionado.getListaCategoriaDespesas().remove(pl);
    }

    public void removerElementoDespesa(ActionEvent evt) {
        ConvenioDespesa dp = (selecionado);
        PlanoAplicacaoContaDesp pl = (PlanoAplicacaoContaDesp) evt.getComponent().getAttributes().get("removeElemento");
        planoAplicacaoSelecionado.getListaElementosDespesa().remove(pl);
    }

    public void removeDespesa(ActionEvent evt) {
        DespesaExercConvenio dec = (DespesaExercConvenio) evt.getComponent().getAttributes().get("removeDespesa");
        selecionado.getDespesaExercConvenios().remove(dec);
    }

    public void removeOcorrencia(ActionEvent evt) {
        TramiteConvenioDesp tcp = (TramiteConvenioDesp) evt.getComponent().getAttributes().get("removeOcorrencia");
        selecionado.getTramites().remove(tcp);
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public List<Interveniente> completaIntervenienteSimples(String parte) {
        List<Interveniente> interLista = new ArrayList<Interveniente>();
        List<Interveniente> retorno = new ArrayList<Interveniente>();
        ConvenioDespesa cd = (selecionado);
        for (ConvenioDespInterveniente cri : cd.getConvenioDespIntervenientes()) {
            interLista.add(convenioDespesaFacade.getIntervenienteFacade().recuperar(cri.getInterveniente().getId()));
        }
        for (Interveniente i : interLista) {
            if (i.getPessoa().getNome().startsWith(parte.trim())) {
                retorno.add(i);
            }
        }
        return retorno;
    }

    public List<SelectItem> getContasCorrentes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        List<ContaCorrenteBancaria> contas = new ArrayList<ContaCorrenteBancaria>();
        if (selecionado.getEntidadeBeneficiaria() != null) {
            if (selecionado.getEntidadeBeneficiaria().getPessoaJuridica() != null) {
                contas = convenioDespesaFacade.getContaCorrenteBancariaFacade().listaContasPorPessoaJuridica(selecionado.getEntidadeBeneficiaria().getPessoaJuridica());
            }
        }
        for (ContaCorrenteBancaria object : contas) {
            if (object.getSituacao().equals(SituacaoConta.ATIVO)) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public List<EntidadeBeneficiaria> completaEntidadeBeneficiaria(String parte) {
        return convenioDespesaFacade.getEntidadeBeneficiariaFacade().listaPorPessoaEClasseCredor(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        if (selecionado.getEntidadeBeneficiaria() != null) {
            return convenioDespesaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte.trim(), selecionado.getEntidadeBeneficiaria().getPessoaJuridica(), TipoClasseCredor.CONVENIIO_DESPESA);
        }
        return new ArrayList<ClasseCredor>();
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, convenioDespesaFacade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public List<TipoInterveniente> completaTipoInterveniente(String parte) {
        return convenioDespesaFacade.getTipoIntervenienteFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<Interveniente> completaInterveniente(String parte) {
        return convenioDespesaFacade.getIntervenienteFacade().listaPorPessoa(parte.trim());
    }

    public List<Conta> completaContaDespesa(String parte) {
        return convenioDespesaFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<CategoriaDespesa> completaCategoriaDespesa(String parte) {
        return convenioDespesaFacade.getCategoriaDespesaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<OcorrenciaConvenioDesp> completaOcorrencia(String parte) {
        return convenioDespesaFacade.getOcorrenciaConvenioDespFacade().listaFiltrando(parte.trim(), "ocorrencia");
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return convenioDespesaFacade.getAtoLegalFacade().listaFiltrandoAtoLegalPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.CONVENIO);
    }

    public List<VeiculoDePublicacao> completaOrgaoPublicacao(String parte) {
        return convenioDespesaFacade.getVeiculoDePublicacaoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<SelectItem> getDestinacaoRecursos() {
        List<SelectItem> listaDest = new ArrayList<SelectItem>();
        listaDest.add(new SelectItem(null, ""));
        for (DestinacaoRecurso dest : DestinacaoRecurso.values()) {
            listaDest.add(new SelectItem(dest, dest.getDescricao()));
        }
        return listaDest;
    }

    public List<SelectItem> getPeriodicidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Periodicidade object : convenioDespesaFacade.getPeriodicidadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDePrestacaoContas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoPrestacaoContas object : TipoPrestacaoContas.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getUnidadeMedidas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (UnidadeMedida object : convenioDespesaFacade.getUnidadeMedidaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novo-convenio-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ConvenioDespesa cd = (selecionado);
        prestacaoContas = new PrestacaoContas();
        aditivos = new Aditivos();
        despesaORC = new DespesaORC();
        tramiteConvenioDespSelecionado = new TramiteConvenioDesp();
        planoAplicacaoSelecionado = new PlanoAplicacao();
        categoriaDespesaSelecionada = new PlanoAplicacaoCategDesp();
        elementoDespesaSelecionado = new PlanoAplicacaoContaDesp();
        convenioDespesaLiberacoes = new ConvenioDespesaLiberacao();
        cronogramaDesembolsoSelecionado = new CronogramaDesembolso();
        despesaExercConvenioSelecionado = new DespesaExercConvenio();
        intervenienteSelecionado = new Interveniente();
        andamentoConvenio = new AndamentoConvenio();
        intervenientes = new ArrayList<Interveniente>();
        listaTramites = new ArrayList<TramiteConvenioDesp>();
        listaAndamentos = new ArrayList<AndamentoConvenio>();
        andamentosRemovidos = new ArrayList<AndamentoConvenio>();
        pertenceEmenda = Boolean.FALSE;
        recuperarObjetosSessao();
    }

    private void recuperarObjetosSessao() {
        OcorrenciaConvenioDesp ocorrenciaConvenioDesp = (OcorrenciaConvenioDesp) Web.pegaDaSessao(OcorrenciaConvenioDesp.class);
        if (ocorrenciaConvenioDesp != null) {
            if (ocorrenciaConvenioDesp.getId() != null) {
                tramiteConvenioDespSelecionado.setOcorrenciaConvenioDesp(ocorrenciaConvenioDesp);
            }
        }
//        CategoriaDespesa categoriaDespesa = (CategoriaDespesa) Web.pegaDaSessao(CategoriaDespesa.class);
//        if (categoriaDespesa != null) {
//            if (categoriaDespesa.getId() != null) {
//                planoAplicacaoSelecionado.setCategoriaDespesa(categoriaDespesa);
//            }
//        }
        Interveniente interveniente = (Interveniente) Web.pegaDaSessao(Interveniente.class);
        if (interveniente != null) {
            if (interveniente.getId() != null) {
                intervenienteSelecionado = interveniente;
            }
        }
    }

    @URLAction(mappingId = "ver-convenio-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-convenio-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/convenio-despesa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void recuperarEditarVer() {
        selecionado = convenioDespesaFacade.recuperar(super.getId());
        tramiteConvenioDespSelecionado = new TramiteConvenioDesp();
        planoAplicacaoSelecionado = new PlanoAplicacao();
        categoriaDespesaSelecionada = new PlanoAplicacaoCategDesp();
        elementoDespesaSelecionado = new PlanoAplicacaoContaDesp();
        cronogramaDesembolsoSelecionado = new CronogramaDesembolso();
        despesaExercConvenioSelecionado = new DespesaExercConvenio();
        prestacaoContas = new PrestacaoContas();
        convenioDespesaLiberacoes = new ConvenioDespesaLiberacao();
        aditivos = new Aditivos();
        intervenienteSelecionado = new Interveniente();
        andamentoConvenio = new AndamentoConvenio();
        andamentosRemovidos = new ArrayList<AndamentoConvenio>();
        listaAndamentos = new ArrayList<AndamentoConvenio>();
        for (AndamentoConvenioDespesa acd : selecionado.getAndamentoConvenioDespesa()) {
            listaAndamentos.add(convenioDespesaFacade.recuperarAndamento(acd.getAndamentoConvenio().getId()));
        }
        if (selecionado.getValorConvenio() == null) {
            selecionado.setValorConvenio(BigDecimal.ZERO);
        }
        if (selecionado.getValorContrapartida() == null) {
            selecionado.setValorContrapartida(BigDecimal.ZERO);
        }
        if (selecionado.getValorRepasse() == null) {
            selecionado.setValorRepasse(BigDecimal.ZERO);
        }
        pertenceEmenda = selecionado.getEmenda() != null;
    }

    public void calculaValorRepasse() {
        selecionado.setValorRepasse(selecionado.getValorConvenio().subtract(selecionado.getValorContrapartida()));
    }

    private void adicionaAndamentosAoSelecionado() {
        for (AndamentoConvenio listaAndamento : listaAndamentos) {
            AndamentoConvenioDespesa acd = new AndamentoConvenioDespesa();
            acd.setConvenioDespesa(this.selecionado);
            acd.setAndamentoConvenio(listaAndamento);
            selecionado.getAndamentoConvenioDespesa().add(acd);
        }
    }

    public void adicionaLiberacoes() {
        if (convenioDespesaLiberacoes.getDataLiberacaoRecurso() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Data da Liberação de Recurso deve ser informado adicionar.");
        }
        if (convenioDespesaLiberacoes.getValorLiberadoConcedente() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor Liberado Concedente deve ser informado adicionar.");
        }
        if (convenioDespesaLiberacoes.getValorLiberadoConcedente().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor Liberado Concedente deve ser maior que zero (0).");
        } else {
            convenioDespesaLiberacoes.setConvenioDespesa(selecionado);
            selecionado.setConvenioDespesaLiberacoes(Util.adicionarObjetoEmLista(selecionado.getConvenioDespesaLiberacoes(), convenioDespesaLiberacoes));
            convenioDespesaLiberacoes = new ConvenioDespesaLiberacao();
        }
    }

    public void adicionaStatusPrestacaoContas() {
        ConvenioDespesa cd = (selecionado);
        if (prestacaoContas.getDataLancamento() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Data deve ser informado para adicionar.");
        } else if (prestacaoContas.getTipoPrestacaoContas() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Situação deve ser informado para adicionar.");
        } else {
            prestacaoContas.setConvenioDespesa(cd);
            cd.getPrestacaoContas().add(prestacaoContas);
            prestacaoContas = new PrestacaoContas();
        }
    }

    public Boolean verificaSelecionado() {
        if (selecionado != null) {
            if (selecionado.getId() != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public List<Empenho> getListaEmpenhos() {
        if (verificaSelecionado()) {
            return convenioDespesaFacade.listaEmpenhoPorConvenioDespesa(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<EmpenhoEstorno> getListaEmpenhoEstorno() {
        if (verificaSelecionado()) {
            return convenioDespesaFacade.listaEmpenhoEstornoPorConvenioDespesa(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Liquidacao> getListaLiquidacao() {
        if (verificaSelecionado()) {
            return convenioDespesaFacade.listaLiquidacaoPorConvenioDespesa(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<LiquidacaoEstorno> getListaLiquidacaoEstorno() {
        if (verificaSelecionado()) {
            return convenioDespesaFacade.listaLiquidacaoEstornoPorConvenioDespesa(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pagamento> getListaPagamento() {
        if (verificaSelecionado()) {
            return convenioDespesaFacade.listaPagamentoPorConvenioDespesa(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<PagamentoEstorno> getListaPagamentoEstorno() {
        if (verificaSelecionado()) {
            return convenioDespesaFacade.listaPagamentoEstornoPorConvenioDespesa(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Entidade> completaOrgaoConvenente(String parte) {
        return convenioDespesaFacade.getEntidadeFacade().listaEntidadesAtivas(parte.trim());
    }

    public List<FonteDespesaORC> completaFonteDespesaORC(String parte) {
        if (despesaORC.getId() != null) {
            return convenioDespesaFacade.getFonteDespesaORCFacade().completaFonteDespesaORC(parte.trim(), despesaORC);
        } else {
            return new ArrayList<FonteDespesaORC>();
        }
    }

    public void verificarDataPeriodiciade() {
        if (selecionado.getDataPeriodicidade() != null) {
            if (selecionado.getDataPeriodicidade() > 30 || selecionado.getDataPeriodicidade() < 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A data da Periodicidade não pode ser inferior a 0 (ZERO) ou superior a 30 (TRINTA).");
            }
        }
    }

    private void verificarAoSalvarDataPeriodiciade() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataPeriodicidade() != null) {
            if (selecionado.getDataPeriodicidade() > 30 || selecionado.getDataPeriodicidade() < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data da Periodicidade não pode ser inferior a 0 (ZERO) ou superior a 30 (TRINTA).");
            }
        }
        ve.lancarException();
    }

    public void validarDatas(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage message = new FacesMessage();
        Date dataVigenciaFinal = (Date) value;
        Date dataVigenciaInicial = selecionado.getDataVigenciaInicial();

        if (dataVigenciaInicial != null && dataVigenciaFinal != null) {
            if (dataVigenciaFinal.before(dataVigenciaInicial)) {
                selecionado.setDataVigenciaFinal(null);
                selecionado.setDataVigenciaInicial(null);
                message.setSummary("Data inválida! ");
                message.setDetail(" Data de Fim de Vigência deve ser posterior ou igual à Data de Início de Vigência. ");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new javax.faces.validator.ValidatorException(message);
            }
        }
    }

    private void verificarAoSalvarDataVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataVigenciaFinal() != null && selecionado.getDataVigenciaInicial() !=null) {
            if (selecionado.getDataVigenciaFinal().before(selecionado.getDataVigenciaInicial())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Data de Fim de Vigência deve ser posterior ou igual à Data de Início de Vigência.");
            }
        }
        ve.lancarException();
    }


    public DespesaExercConvenio getDespesaExercConvenioSelecionado() {
        return despesaExercConvenioSelecionado;
    }

    public void setDespesaExercConvenioSelecionado(DespesaExercConvenio despesaExercConvenioSelecionado) {
        this.despesaExercConvenioSelecionado = despesaExercConvenioSelecionado;
    }

    public TramiteConvenioDesp getTramiteConvenioDespSelecionado() {
        return tramiteConvenioDespSelecionado;
    }

    public void setTramiteConvenioDespSelecionado(TramiteConvenioDesp tramiteConvenioDespSelecionado) {
        this.tramiteConvenioDespSelecionado = tramiteConvenioDespSelecionado;
    }

    public PlanoAplicacao getPlanoAplicacaoSelecionado() {
        return planoAplicacaoSelecionado;
    }

    public void setPlanoAplicacaoSelecionado(PlanoAplicacao planoAplicacaoSelecionado) {
        this.planoAplicacaoSelecionado = planoAplicacaoSelecionado;
    }

    public AndamentoConvenio getAndamentoConvenio() {
        return andamentoConvenio;
    }

    public void setAndamentoConvenio(AndamentoConvenio andamentoConvenio) {
        this.andamentoConvenio = andamentoConvenio;
    }

    public Interveniente getIntervenienteSelecionado() {
        return intervenienteSelecionado;
    }

    public void setIntervenienteSelecionado(Interveniente intervenienteSelecionado) {
        this.intervenienteSelecionado = intervenienteSelecionado;
    }

    public List<TramiteConvenioDesp> getListaTramites() {
        return listaTramites;
    }

    public void setListaTramites(List<TramiteConvenioDesp> listaTramites) {
        this.listaTramites = listaTramites;
    }

    public CronogramaDesembolso getCronogramaDesembolsoSelecionado() {
        return cronogramaDesembolsoSelecionado;
    }

    public void setCronogramaDesembolsoSelecionado(CronogramaDesembolso cronogramaDesembolsoSelecionado) {
        this.cronogramaDesembolsoSelecionado = cronogramaDesembolsoSelecionado;
    }

    public List<Interveniente> getIntervenientes() {
        return intervenientes;
    }

    public void setIntervenientes(List<Interveniente> intervenientes) {
        this.intervenientes = intervenientes;
    }

    public List<AndamentoConvenio> getListaAndamentos() {
        return listaAndamentos;
    }

    public void setListaAndamentos(List<AndamentoConvenio> listaAndamentos) {
        this.listaAndamentos = listaAndamentos;
    }

    public PrestacaoContas getPrestacaoContas() {
        return prestacaoContas;
    }

    public void setPrestacaoContas(PrestacaoContas prestacaoContas) {
        this.prestacaoContas = prestacaoContas;
    }

    public ConvenioDespesaLiberacao getConvenioDespesaLiberacoes() {
        return convenioDespesaLiberacoes;
    }

    public void setConvenioDespesaLiberacoes(ConvenioDespesaLiberacao convenioDespesaLiberacoes) {
        this.convenioDespesaLiberacoes = convenioDespesaLiberacoes;
    }

    public Aditivos getAditivos() {
        return aditivos;
    }

    public void setAditivos(Aditivos aditivos) {
        this.aditivos = aditivos;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public PlanoAplicacaoCategDesp getCategoriaDespesaSelecionada() {
        return categoriaDespesaSelecionada;
    }

    public void setCategoriaDespesaSelecionada(PlanoAplicacaoCategDesp categoriaDespesaSelecionada) {
        this.categoriaDespesaSelecionada = categoriaDespesaSelecionada;
    }

    public PlanoAplicacaoContaDesp getElementoDespesaSelecionado() {
        return elementoDespesaSelecionado;
    }

    public void setElementoDespesaSelecionado(PlanoAplicacaoContaDesp elementoDespesaSelecionado) {
        this.elementoDespesaSelecionado = elementoDespesaSelecionado;
    }

    public List<SelectItem> getSituacoesCadastrais() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (SituacaoCadastralContabil situacaoCadastralContabil : SituacaoCadastralContabil.values()) {
            retorno.add(new SelectItem(situacaoCadastralContabil, situacaoCadastralContabil.getDescricao()));
        }
        return retorno;
    }

    public List<ConvenioDespesa> completarConvenioDespesa(String parte) {
        return convenioDespesaFacade.listaFiltrando(parte.trim(), "numConvenio", "objeto");
    }

    public List<SelectItem> getTiposConvenios() {
        return Util.getListSelectItem(TipoConvenioDespesa.values(), false);
    }

    public Boolean getPertenceEmenda() {
        return pertenceEmenda;
    }

    public void setPertenceEmenda(Boolean pertenceEmenda) {
        this.pertenceEmenda = pertenceEmenda;
    }

    public List<Emenda> completarEmendas(String parte) {
        return convenioDespesaFacade.getEmendaFacade().buscarEmendasAprovadasPorExercicioAndVereador(parte);
    }

    public void limparEmenda() {
        selecionado.setEmenda(null);
    }
}
