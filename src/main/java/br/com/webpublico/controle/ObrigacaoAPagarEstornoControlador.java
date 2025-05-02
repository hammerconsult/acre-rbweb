package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ObrigacaoAPagarEstornoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
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
import java.util.List;

/**
 * Created by mga on 26/07/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEstornoObrigacaoPagar", pattern = "/estorno-obrigacao-a-pagar/novo/", viewId = "/faces/financeiro/orcamentario/obrigacaopagarestorno/edita.xhtml"),
    @URLMapping(id = "editarEstornoObrigacaoPagar", pattern = "/estorno-obrigacao-a-pagar/editar/#{obrigacaoAPagarEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/obrigacaopagarestorno/edita.xhtml"),
    @URLMapping(id = "verEstornoObrigacaoPagar", pattern = "/estorno-obrigacao-a-pagar/ver/#{obrigacaoAPagarEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/obrigacaopagarestorno/visualizar.xhtml"),
    @URLMapping(id = "listarEstornoObrigacaoPagar", pattern = "/estorno-obrigacao-a-pagar/listar/", viewId = "/faces/financeiro/orcamentario/obrigacaopagarestorno/lista.xhtml"),
})
public class ObrigacaoAPagarEstornoControlador extends PrettyControlador<ObrigacaoAPagarEstorno> implements Serializable, CRUD {

    @EJB
    private ObrigacaoAPagarEstornoFacade facade;
    private DesdobramentoObrigacaoAPagarEstorno desdobramento;
    private ObrigacaoPagarEstornoDoctoFiscal documentoFiscal;
    private ConverterAutoComplete converterDocumentoFiscal;
    private List<DesdobramentoObrigacaoPagar> desdobramentosObrigacaoPagar;
    private List<ObrigacaoAPagarDoctoFiscal> documentosFiscaisObrigacaoPagar;

    public ObrigacaoAPagarEstornoControlador() {
        super(ObrigacaoAPagarEstorno.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-obrigacao-a-pagar/";
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novoEstornoObrigacaoPagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataEstorno(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
    }

    @URLAction(mappingId = "editarEstornoObrigacaoPagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado.setObrigacaoAPagar(facade.getObrigacaoAPagarFacade().recuperar(selecionado.getObrigacaoAPagar().getId()));
        documentosFiscaisObrigacaoPagar = selecionado.getObrigacaoAPagar().getDocumentosFiscais();
        desdobramentosObrigacaoPagar = selecionado.getObrigacaoAPagar().getDesdobramentos();
    }


    @URLAction(mappingId = "verEstornoObrigacaoPagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCampo();
            if (isOperacaoNovo()) {
                selecionado = facade.salvarEstorno(selecionado);
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            } else {
                facade.salvar(selecionado);
                redireciona();
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarCampo() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        validarRegrasEspecificas(ve);
        validarTipoEmpenho(ve);
        ve.lancarException();
    }

    private void validarRegrasEspecificas(ValidacaoException ve) {
        if (selecionado.getDataEstorno().compareTo(selecionado.getObrigacaoAPagar().getDataLancamento()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data do estorno deve ser superior ou igual a data da obrigação a pagar.");
        }
        if (selecionado.getDocumentosFiscais().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O estorno dever ter ao menos um documento comprobatório adicionado na lista.");
        }
        if (selecionado.getDesdobramentos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O estorno dever ter ao menos um detalhamento adicionado na lista.");
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor deve ser maior que zero(0).");
        }
        if (isOperacaoNovo() && selecionado.getValor().compareTo(selecionado.getObrigacaoAPagar().getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do estorno deve ser menor ou igual ao saldo disponível na obrigação a pagar de " + Util.formataValor(selecionado.getObrigacaoAPagar().getSaldo()) + ".");
        }
        if (selecionado.getValorTotalDetalhamentos().compareTo(selecionado.getValorTotalDocumentos()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total dos detalhamentos e o valor total dos documentos comprobatórios estão divergentes.");
        }
    }

    private void validarTipoEmpenho(ValidacaoException va) {
        if (selecionado.getObrigacaoAPagar() != null && selecionado.getObrigacaoAPagar().isObrigacaoPagarDepoisEmpenho()) {
            String msg = " O Tipo do Empenho vinculado a Obrigação a Pagar é 'Ordinário'. Por isso o valor da seu estorno deve ser igual ao seu saldo.";
            selecionado.getObrigacaoAPagar().getEmpenho().validarTipoEmpenho(va, msg, selecionado.getValor(), selecionado.getObrigacaoAPagar().getEmpenho().getSaldo());
        }
    }

    public boolean isRegistroEditavel() {
        return selecionado.getId() != null;
    }

    public List<ObrigacaoAPagar> completarObrigacaoPagar(String parte) {
        return facade.getObrigacaoAPagarFacade().buscarObrigacaoPagarPorUnidadeUsuarioVinculado(
            parte.trim(),
            selecionado.getDataEstorno(),
            selecionado.getUnidadeOrganizacional(),
            selecionado.getExercicio(),
            selecionado.getUsuarioSistema());
    }

    public void selecionarObrigacaoPagar(ActionEvent evento) {
        selecionado.setObrigacaoAPagar((ObrigacaoAPagar) evento.getComponent().getAttributes().get("objeto"));
        definirObrigacaoPagar();
    }

    public void definirObrigacaoPagar() {
        if (selecionado.getObrigacaoAPagar() != null) {
            selecionado.setObrigacaoAPagar(facade.getObrigacaoAPagarFacade().recuperar(selecionado.getObrigacaoAPagar().getId()));
            documentosFiscaisObrigacaoPagar = selecionado.getObrigacaoAPagar().getDocumentosFiscais();
            desdobramentosObrigacaoPagar = selecionado.getObrigacaoAPagar().getDesdobramentos();
            selecionado.setHistorico(selecionado.getObrigacaoAPagar().getHistorico());
        }
    }

    public void definirNullParaObrigacao() {
        selecionado.setObrigacaoAPagar(null);
        selecionado.setHistorico(null);
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            List<NotaExecucaoOrcamentaria> notas = facade.buscarNotasDeEstornosDeObrigacoesAPagar(" and nota.id = " + selecionado.getId());
            if (notas != null && !notas.isEmpty()) {
                facade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notas.get(0), ModuloTipoDoctoOficial.NOTA_OBRIGACAO_A_PAGAR_ESTORNO, selecionado, isDownload);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Erro ao gerar nota de estorno de obrigação a pagar: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void novoDocumentoFiscal() {
        try {
            validarNovoDesdobramento();
            documentoFiscal = new ObrigacaoPagarEstornoDoctoFiscal();
            FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:doctoFiscal_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarDocumentoFiscal() {
        documentoFiscal = null;
        definirFocoBtnNovoDocumentoFiscal();
    }

    public void novoDesdobramento() {
        try {
            validarNovoDesdobramento();
            desdobramento = new DesdobramentoObrigacaoAPagarEstorno();
            FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:contaDesdobramento_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarDesdobramento() {
        desdobramento = null;
        definirFocoBtnNovoDesdobramento();
    }

    public void adicionarDesdobramento() {
        try {
            validarDesdobramento();
            desdobramento.setObrigacaoAPagarEstorno(selecionado);
            definirEventoContabil();
            Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento);
            selecionado.setValor(selecionado.getValorTotalDetalhamentos());
            cancelarDesdobramento();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDesdobramento() {
        ValidacaoException ve = new ValidacaoException();
        validarNovoDesdobramento();
        desdobramento.realizarValidacoes();
        if (desdobramento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero(0)");
        }
        if (desdobramento.getValor().compareTo(selecionado.getObrigacaoAPagar().getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do detalhamento deve ser menor ou igual ao saldo da obrigação a pagar.");
        }
        selecionado.getObrigacaoAPagar().validarSaldoDisponivelPorContaDespesa(desdobramento.getValor(), desdobramento.getConta());
        for (DesdobramentoObrigacaoAPagarEstorno desd : selecionado.getDesdobramentos()) {
            if (!desd.equals(desdobramento) && desd.getConta().equals(desdobramento.getConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A conta de despesa já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    public void adicionarDocumentoFiscal() {
        try {
            validarDocumentoFiscal();
            documentoFiscal.setObrigacaoAPagarEstorno(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDocumentosFiscais(), documentoFiscal);
            cancelarDocumentoFiscal();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDocumentoFiscal() {
        ValidacaoException ve = new ValidacaoException();
        validarNovoDesdobramento();
        if (documentoFiscal.getDocumentoFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo documento fiscal deve ser informado.");
        }
        if (documentoFiscal.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero(0)");
        }
        if (documentoFiscal.getValor().compareTo(selecionado.getObrigacaoAPagar().getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do detalhamento deve ser menor ou igual ao saldo da obrigação a pagar.");
        }
        selecionado.getObrigacaoAPagar().validarSaldoDisponivelPorDocumentoFiscal(documentoFiscal.getValor(), documentoFiscal.getDocumentoFiscal());
        for (ObrigacaoPagarEstornoDoctoFiscal doctoFiscal : selecionado.getDocumentosFiscais()) {
            if (!doctoFiscal.equals(documentoFiscal) && doctoFiscal.getDocumentoFiscal().equals(documentoFiscal.getDocumentoFiscal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O documento fiscal já foi adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    public List<Conta> completarDetalhamento(String parte) {
        if (selecionado.getObrigacaoAPagar() != null) {
            return facade.getObrigacaoAPagarFacade().buscarContaDesdobradaPorObrigacaoPagar(parte.trim(), selecionado.getObrigacaoAPagar());
        }
        return Lists.newArrayList();
    }

    public List<DoctoFiscalLiquidacao> completarDocumentosFiscais(String parte) {
        if (selecionado.getObrigacaoAPagar() != null) {
            return facade.getDoctoFiscalLiquidacaoFacade().buscarDocumentoFiscalPorObrigacaoPagar(selecionado.getObrigacaoAPagar(), parte.trim());
        }
        return Lists.newArrayList();
    }

    public void editarDesdobramento(DesdobramentoObrigacaoAPagarEstorno desdobramento) {
        this.desdobramento = (DesdobramentoObrigacaoAPagarEstorno) Util.clonarObjeto(desdobramento);
    }

    public void editarDocumentoFiscal(ObrigacaoPagarEstornoDoctoFiscal doc) {
        this.documentoFiscal = (ObrigacaoPagarEstornoDoctoFiscal) Util.clonarObjeto(doc);
    }

    public void removerDesdobramento(DesdobramentoObrigacaoAPagarEstorno desdobramento) {
        selecionado.getDesdobramentos().remove(desdobramento);
        selecionado.setValor(selecionado.getValorTotalDetalhamentos());
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:contaDesdobramento_input')");
    }

    public void removerDocumentoFiscal(ObrigacaoPagarEstornoDoctoFiscal doc) {
        selecionado.getDocumentosFiscais().remove(doc);
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:doctoFiscal_input')");
    }

    public ParametroEvento getParametroEvento(DesdobramentoObrigacaoAPagarEstorno desdobramento) {
        try {
            ParametroEvento parametroEvento = facade.criarParametroEvento(desdobramento);
            if (parametroEvento != null) {
                return parametroEvento;
            }
            throw new ExcecaoNegocioGenerica("Parametro evento não encontrado para visualizar o evento contábil .");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (RuntimeException ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
        return null;
    }

    public void definirSaldoDocumentoFiscal() {
        if (selecionado.getObrigacaoAPagar() != null) {
            for (ObrigacaoAPagarDoctoFiscal doctoObrigacao : selecionado.getObrigacaoAPagar().getDocumentosFiscais()) {
                if (documentoFiscal.getDocumentoFiscal().equals(doctoObrigacao.getDocumentoFiscal())) {
                    documentoFiscal.setValor(doctoObrigacao.getSaldo());
                }
            }
        }
    }

    public void definirSaldoDetalhamento() {
        if (selecionado.getObrigacaoAPagar() != null) {
            for (DesdobramentoObrigacaoPagar desdObrigacao : selecionado.getObrigacaoAPagar().getDesdobramentos()) {
                if (desdobramento.getConta().equals(desdObrigacao.getConta())) {
                    desdobramento.setValor(desdObrigacao.getSaldo());
                }
            }
        }
    }

    private void definirFocoBtnNovoDocumentoFiscal() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnNovoDocumento')");
    }

    private void definirFocoBtnNovoDesdobramento() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnNovoDesdobramento')");
    }

    private void validarNovoDesdobramento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getObrigacaoAPagar() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo obrigação a pagar deve ser informado.");
        }
        ve.lancarException();
    }

    private void definirEventoContabil() {
        EventoContabil eventoContabil = facade.buscarEventoContabil(desdobramento);
        if (eventoContabil != null) {
            desdobramento.setEventoContabil(eventoContabil);
        }
    }

    public ConverterAutoComplete getConverterDocumentoFiscal() {
        if (converterDocumentoFiscal == null) {
            converterDocumentoFiscal = new ConverterAutoComplete(DoctoFiscalLiquidacao.class, facade.getDoctoFiscalLiquidacaoFacade());
        }
        return converterDocumentoFiscal;
    }

    public DesdobramentoObrigacaoAPagarEstorno getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(DesdobramentoObrigacaoAPagarEstorno desdobramento) {
        this.desdobramento = desdobramento;
    }

    public ObrigacaoPagarEstornoDoctoFiscal getDocumentoFiscal() {
        return documentoFiscal;
    }

    public void setDocumentoFiscal(ObrigacaoPagarEstornoDoctoFiscal documentoFiscal) {
        this.documentoFiscal = documentoFiscal;
    }

    public List<DesdobramentoObrigacaoPagar> getDesdobramentosObrigacaoPagar() {
        return desdobramentosObrigacaoPagar;
    }

    public void setDesdobramentosObrigacaoPagar(List<DesdobramentoObrigacaoPagar> desdobramentosObrigacaoPagar) {
        this.desdobramentosObrigacaoPagar = desdobramentosObrigacaoPagar;
    }

    public List<ObrigacaoAPagarDoctoFiscal> getDocumentosFiscaisObrigacaoPagar() {
        return documentosFiscaisObrigacaoPagar;
    }

    public void setDocumentosFiscaisObrigacaoPagar(List<ObrigacaoAPagarDoctoFiscal> documentosFiscaisObrigacaoPagar) {
        this.documentosFiscaisObrigacaoPagar = documentosFiscaisObrigacaoPagar;
    }
}
