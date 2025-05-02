/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OrigemOCCFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Edi
 */
@ManagedBean(name = "oCCContaDestinacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-occ-conta-destinacao", pattern = "/occ/conta-destinacao/novo/", viewId = "/faces/financeiro/origemcontacontabil/contadestinacao/edita.xhtml"),
        @URLMapping(id = "editar-occ-conta-destinacao", pattern = "/occ/conta-destinacao/editar/#{oCCContaDestinacaoControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/contadestinacao/edita.xhtml"),
        @URLMapping(id = "ver-occ-conta-destinacao", pattern = "/occ/conta-destinacao/ver/#{oCCContaDestinacaoControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/contadestinacao/visualizar.xhtml"),
        @URLMapping(id = "listar-occ-conta-destinacao", pattern = "/occ/conta-destinacao/listar/", viewId = "/faces/financeiro/origemcontacontabil/contadestinacao/listar.xhtml"),})
public class OCCContaDestinacaoControlador extends PrettyControlador<OCCConta> implements Serializable, CRUD {

    @EJB
    private OrigemOCCFacade origemOCCFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private EntidadeOCC entidadeOCC;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterTagOCC;
    private OrigemContaContabil origemNaoAlterada;
    private List<OrigemContaContabil> origenscc;
    private List<Conta> listConta;
    private Conta[] arrayConta;
    private String palavra;

    public OCCContaDestinacaoControlador() {
        super(OCCConta.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/occ/conta-destinacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-occ-conta-destinacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        setEntidadeOCC(EntidadeOCC.DESTINACAO);
        origenscc = new ArrayList<>();
    }

    @URLAction(mappingId = "ver-occ-conta-destinacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-occ-conta-destinacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void removeConta(ActionEvent evento) {
        OrigemContaContabil occ = (OrigemContaContabil) evento.getComponent().getAttributes().get("occ");
        origenscc.remove(occ);
    }

    public void alterarConta(ActionEvent evento) {
        OCCConta occ = (OCCConta) evento.getComponent().getAttributes().get("occ");
        selecionado = occ;
    }

    public void recuperaEditarVer() {
        OrigemContaContabil occ = (OrigemContaContabil) selecionado;
        selecionado = (OCCConta) origemOCCFacade.recuperar(OCCConta.class, occ.getId());
        origemNaoAlterada = (OCCConta) origemOCCFacade.recuperar(OCCConta.class, occ.getId());
        setEntidadeOCC(EntidadeOCC.DESTINACAO);
        origenscc = new ArrayList<>();
        origenscc.add(selecionado);
    }

    private void verificaAlteracoesDaOrigem() {
        try {
            boolean alteroOrigem = false;
            if (!origemNaoAlterada.meuEquals(selecionado)) {
                alteroOrigem = true;
            }
            if (alteroOrigem) {
                for (EventoContabil ec : origemOCCFacade.retornaEventosPorTag(selecionado.getTagOCC())) {
                    origemOCCFacade.getEventoContabilFacade().geraEventosReprocessar(ec, selecionado.getId(), selecionado.getClass().getSimpleName());
                }

            }
        } catch (Exception e) {
        }
    }

    @Override
    public void salvar() {
        try {
            verificaAlteracoesDaOrigem();
            if (!validaOrigemContaContabil()) {
                return;
            }
            if (!validaSalvar()) {
                return;
            }
            if (operacao.equals(Operacoes.NOVO)) {
                if (entidadeOCC.equals(EntidadeOCC.DESTINACAO)) {
                    for (OrigemContaContabil occ : origenscc) {
                        occ.setInicioVigencia(selecionado.getInicioVigencia());
                        origemOCCFacade.salvar(occ);
                    }
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", "Registro salvo com sucesso."));
                } else {
                    origemOCCFacade.salvarNovo(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", "Registro salvo com sucesso."));
                }
            } else {
                origemOCCFacade.salvar(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", "Registro alterado com sucesso."));
            }
            redireciona();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Operação não Realizada! ", e.getMessage()));
        }
    }

    public void validaDataVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date dataVigencia = (Date) value;
        Calendar dataInicioVigencia = Calendar.getInstance();
        dataInicioVigencia.setTime(dataVigencia);
        Integer ano = sistemaControlador.getExercicioCorrente().getAno();
        if (dataInicioVigencia.get(Calendar.YEAR) != ano) {
            message.setSummary("Operação não Permitida! ");
            message.setDetail(" O ano da data e diferente do exercício corrente.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    private Boolean validaSalvar() {
        Boolean controle = Boolean.TRUE;
        if (origenscc.isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Para salvar o registro, é necessário informar uma configuração para a Origem Conta Contábil - Conta de Destinação. ");
            controle = Boolean.FALSE;
        }
        if (selecionado.getInicioVigencia() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Início de Vigência deve ser informado.");
            controle = Boolean.FALSE;
        }
        return controle;
    }


    public Boolean validaOrigemContaContabil() {
        Boolean controle = Boolean.TRUE;
        OrigemContaContabil origemContaContabil = verificaConfiguracaoExistente();
        if (origemContaContabil != null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma configuração para a Origem Conta Contábil, TAG: " + ((OCCConta) origemContaContabil).getTagOCC().getCodigo() + " - " + ((OCCConta) origemContaContabil).getTagOCC().getDescricao() + ", Conta: " + ((OCCConta) origemContaContabil).getContaOrigem().getCodigo() + " - " + ((OCCConta) origemContaContabil).getContaOrigem().getDescricao() + ", com início de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(((OCCConta) origemContaContabil).getInicioVigencia()));
            controle = Boolean.FALSE;
        }
        return controle;
    }


    public boolean podeAdicionarConta(OCCConta occConta) {
        for (OrigemContaContabil origemContaContabil : origenscc) {
            if (origemContaContabil.getTagOCC().equals(occConta.getTagOCC())) {
                OCCConta occDaVez = (OCCConta) origemContaContabil;
                if (occDaVez.getContaOrigem().equals(occConta.getContaOrigem())) {
                    return false;
                }
            }
        }
        return true;
    }

    public OrigemContaContabil verificaConfiguracaoExistente() {
        OrigemContaContabil configuracaoEncontrada = null;
        if (operacao.equals(Operacoes.NOVO)) {
            for (OrigemContaContabil occ : origenscc) {
                configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorConta(occ);
            }
        } else if ((operacao.equals(Operacoes.EDITAR))) {
            configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorConta(selecionado);

        }
        return configuracaoEncontrada;
    }

    public void encerrarVigencia() {
        try {
            validarEncerramentoVigencia();
            origemOCCFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(" Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarEncerramentoVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFimVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fim de Vigência é obrigatório!");
        }
        ve.lancarException();
        if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O Fim de Vigência deve ser superior ao Início de Vigência.");
        }
        ve.lancarException();
    }


    public boolean podeEditarOrigem() {
        if (selecionado.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<TagOCC> completaTagsOCC(String parte) {
        if (entidadeOCC != null) {
            return origemOCCFacade.getTagOCCFacade().listaPorEntidadeOCC(parte, entidadeOCC);
        } else {
            return new ArrayList<TagOCC>();
        }
    }

    public List<Conta> completaContaContabil(String parte) {
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        if (sistemaControlador != null) {
            return origemOCCFacade.getContaFacade().listaContasContabeis(parte.trim(), sistemaControlador.getExercicioCorrente());
        }
        return null;
    }

    public void filtrarContas() {
        listConta = new ArrayList<Conta>();
        listConta = completaContaDestinacao(palavra);
    }

    public List<Conta> completaContaDestinacao(String parte) {
        if (sistemaControlador != null) {
            return origemOCCFacade.getContaFacade().listaFiltrandoContaDestinacaoPorTpoDestinacao(parte.trim(), sistemaControlador.getExercicioCorrente());
        }
        return null;
    }

    public Boolean addAllContas() {
        Boolean controle = Boolean.TRUE;
        if (selecionado.getTagOCC() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo TAG deve ser informado.");
            controle = Boolean.FALSE;
        }
        if (selecionado.getContaContabil() == null
                && selecionado.getContaInterEstado() == null
                && selecionado.getContaInterMunicipal() == null
                && selecionado.getContaInterUniao() == null
                && selecionado.getContaIntra() == null) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " É necessário informar uma Conta Contábil para continuar a operação.");
            controle = Boolean.FALSE;
        }
       else if (arrayConta.length <= 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " É necessário informar uma Conta de Despesa para continuar a operação.");
            controle = Boolean.FALSE;
        }
        if (controle) {
            for (int i = 0; i < arrayConta.length; i++) {
                OCCConta selecionadoNova = new OCCConta();
                selecionadoNova.setTagOCC(selecionado.getTagOCC());
                selecionadoNova.setContaContabil(selecionado.getContaContabil());
                selecionadoNova.setContaInterEstado(selecionado.getContaInterEstado());
                selecionadoNova.setContaInterMunicipal(selecionado.getContaInterMunicipal());
                selecionadoNova.setContaInterUniao(selecionado.getContaInterUniao());
                selecionadoNova.setContaIntra(selecionado.getContaIntra());
                selecionadoNova.setTipoContaAuxiliarSiconfi(selecionado.getTipoContaAuxiliarSiconfi());
                selecionadoNova.setOrigem(selecionado.getOrigem());
                selecionadoNova.setReprocessar(selecionado.getReprocessar());
                selecionadoNova.setContaOrigem(arrayConta[i]);
                selecionadoNova.setInicioVigencia(selecionado.getInicioVigencia());
                selecionadoNova.setFimVigencia(selecionado.getFimVigencia());
                if (podeAdicionarConta(selecionadoNova)) {
                    origenscc.add(selecionadoNova);
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(),  " A conta: " + selecionadoNova.getContaOrigem() + " já foi adicionada. Informe uma conta diferente para a configuração.");
                }
            }
            RequestContext.getCurrentInstance().update("Formulario");
        }
        return controle;

    }

    public Boolean adicionarContaAlterada() {
        Boolean controle = Boolean.TRUE;
        if (selecionado.getTagOCC() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo TAG deve ser informado.");
            controle = Boolean.FALSE;
        } else if (selecionado.getContaContabil() == null
                && selecionado.getContaInterEstado() == null
                && selecionado.getContaInterMunicipal() == null
                && selecionado.getContaInterUniao() == null
                && selecionado.getContaIntra() == null) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " É necessário informar uma Conta Contábil para continuar a operação.");
            controle = Boolean.FALSE;
        } else {
            origenscc.set(origenscc.indexOf(selecionado), selecionado);
            RequestContext.getCurrentInstance().execute("dialog.hide()");
            RequestContext.getCurrentInstance().update("Formulario");
        }
        return controle;
    }

    public void validaCategoriaConta(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Conta c = (Conta) value;
        if (c.getCategoria() == null) {
            return;
        }
        if (c.getCategoria().equals(CategoriaConta.SINTETICA)) {
            message.setDetail("Conta Sintética: " + c.getCodigo() + " - " + c.getDescricao() + ", " + "não pode ser utilizada.");
            message.setSummary("Operação não Permitida! ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public SelectItem[] tiposContaDestinacaoView() {
        SelectItem[] opcoes = new SelectItem[TipoDestinacaoRecurso.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TipoDestinacaoRecurso tipo : TipoDestinacaoRecurso.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.toString());
            i++;
        }
        return opcoes;
    }

    public ConverterAutoComplete getConverterTagOCC() {
        if (converterTagOCC == null) {
            converterTagOCC = new ConverterAutoComplete(TagOCC.class, origemOCCFacade.getTagOCCFacade());
        }
        return converterTagOCC;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, origemOCCFacade.getContaFacade());
        }
        return converterConta;
    }

    public EntidadeOCC getEntidadeOCC() {
        return entidadeOCC;
    }

    public void setEntidadeOCC(EntidadeOCC entidadeOCC) {
        this.entidadeOCC = entidadeOCC;
    }

    public OrigemContaContabil getOrigemNaoAlterada() {
        return origemNaoAlterada;
    }

    public void setOrigemNaoAlterada(OrigemContaContabil origemNaoAlterada) {
        this.origemNaoAlterada = origemNaoAlterada;
    }

    public List<OrigemContaContabil> getOrigenscc() {
        return origenscc;
    }

    public void setOrigenscc(List<OrigemContaContabil> origenscc) {
        this.origenscc = origenscc;
    }

    public List<Conta> getListConta() {
        return listConta;
    }

    public void setListConta(List<Conta> listConta) {
        this.listConta = listConta;
        palavra = "";
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public Conta[] getArrayConta() {
        return arrayConta;
    }

    public void setArrayConta(Conta[] arrayConta) {
        this.arrayConta = arrayConta;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
