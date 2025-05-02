/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OrigemOCCFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
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
 * @author Mateus
 */
@ManagedBean(name = "oCCContaContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-occ-conta-contabil", pattern = "/occ/conta-contabil/novo/", viewId = "/faces/financeiro/origemcontacontabil/contacontabil/edita.xhtml"),
    @URLMapping(id = "editar-occ-conta-contabil", pattern = "/occ/conta-contabil/editar/#{oCCContaContabilControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/contacontabil/edita.xhtml"),
    @URLMapping(id = "ver-occ-conta-contabil", pattern = "/occ/conta-contabil/ver/#{oCCContaContabilControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/contacontabil/visualizar.xhtml"),
    @URLMapping(id = "listar-occ-conta-contabil", pattern = "/occ/conta-contabil/listar/", viewId = "/faces/financeiro/origemcontacontabil/contacontabil/listar.xhtml"),})
public class OCCContaContabilControlador extends PrettyControlador<OCCConta> implements Serializable, CRUD {

    @EJB
    private OrigemOCCFacade origemOCCFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private EntidadeOCC entidadeOCC;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterTagOCC;
    private List<OrigemContaContabil> origenscc;
    private List<ContaContabil> contas;
    private Conta[] arrayConta;
    private String palavra;

    public OCCContaContabilControlador() {
        super(OCCConta.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/occ/conta-contabil/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-occ-conta-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        setEntidadeOCC(EntidadeOCC.CONTACONTABIL);
        origenscc = Lists.newArrayList();
    }

    @URLAction(mappingId = "ver-occ-conta-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-occ-conta-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    public void removeConta(ActionEvent evento) {
        OrigemContaContabil occ = (OrigemContaContabil) evento.getComponent().getAttributes().get("occ");
        origenscc.remove(occ);
        RequestContext.getCurrentInstance().update("Formulario");
    }

    public void alterarConta(ActionEvent evento) {
        OCCConta occ = (OCCConta) evento.getComponent().getAttributes().get("occ");
        selecionado = occ;
    }

    public void recuperaEditarVer() {
        selecionado = (OCCConta) origemOCCFacade.recuperar(OCCConta.class, selecionado.getId());
        setEntidadeOCC(EntidadeOCC.CONTACONTABIL);
        origenscc = Lists.newArrayList();
        origenscc.add(selecionado);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarOrigemContaContabil();
            if (isOperacaoNovo()) {
                if (EntidadeOCC.CONTACONTABIL.equals(entidadeOCC)) {
                    for (OrigemContaContabil occ : origenscc) {
                        occ.setInicioVigencia(selecionado.getInicioVigencia());
                        origemOCCFacade.salvar(occ);
                    }
                } else {
                    origemOCCFacade.salvarNovo(selecionado);
                }
            } else {
                origemOCCFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
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
            message.setDetail(" O ano da data é diferente do exercício corrente. ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (origenscc.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Para salvar o registro, é necessário informar uma configuração para a Origem Conta Contábil - Conta de Despesa. ");
        }
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Início de Vigência deve ser informado.");
        }
        ve.lancarException();
    }


    private void validarOrigemContaContabil() {
        OrigemContaContabil origemContaContabil = buscarConfiguracao();
        ValidacaoException ve = new ValidacaoException();
        if (origemContaContabil != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe uma configuração para a Origem Conta Contábil: TAG: " + ((OCCConta) origemContaContabil).getTagOCC().getCodigo() + " - " + ((OCCConta) origemContaContabil).getTagOCC().getDescricao() + ", Conta: " + ((OCCConta) origemContaContabil).getContaOrigem().getCodigo() + " - " + ((OCCConta) origemContaContabil).getContaOrigem().getDescricao() + ", com início de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(((OCCConta) origemContaContabil).getInicioVigencia()));
        }
        ve.lancarException();
    }

    public boolean canAdicionarConta(OCCConta occConta) {
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

    private OrigemContaContabil buscarConfiguracao() {
        OrigemContaContabil configuracaoEncontrada = null;
        if (isOperacaoNovo()) {
            for (OrigemContaContabil occ : origenscc) {
                configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorConta(occ);
            }
        } else if (isOperacaoEditar()) {
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

    public boolean canEditarOrigem() {
        return selecionado.getFimVigencia() == null ||
            Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0;
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
        contas = completarContasContabeis(palavra);
    }

    public List<ContaContabil> completarContasContabeis(String parte) {
        if (sistemaControlador != null) {
            return origemOCCFacade.getContaFacade().buscarContasContabeisPorCodigoOrDescricaoAndExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
        }
        return null;
    }

    public void adicionarContas() {
        try {
            validarConta();
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
                if (canAdicionarConta(selecionadoNova)) {
                    origenscc.add(selecionadoNova);
                } else {
                    FacesUtil.addOperacaoNaoPermitida(" A conta: " + selecionadoNova.getContaOrigem() + " já foi adicionada. Informe uma conta diferente para a configuração.");
                }
                RequestContext.getCurrentInstance().update("Formulario");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarConta() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTagOCC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo TAG deve ser informado.");
        }
        if (selecionado.getContaContabil() == null
            && selecionado.getContaInterEstado() == null
            && selecionado.getContaInterMunicipal() == null
            && selecionado.getContaInterUniao() == null
            && selecionado.getContaIntra() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" É necessário informar uma Conta Contábil para continuar a operação.");
        } else if (arrayConta.length <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" É necessário informar uma Conta de Despesa para continuar a operação.");
        }
        ve.lancarException();
    }

    public void adicionarConta() {
        try {
            validarConta();
            origenscc.set(origenscc.indexOf(selecionado), selecionado);
            RequestContext.getCurrentInstance().execute("dialog.hide()");
            RequestContext.getCurrentInstance().update("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validaCategoriaConta(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Conta c = (Conta) value;
        if (c.getCategoria() == null) {
            return;
        }
        if (c.getCategoria().equals(CategoriaConta.SINTETICA)) {
            message.setDetail(" Conta Sintética: " + c.getCodigo() + " - " + c.getDescricao() + ", " + "não pode ser utilizada.");
            message.setSummary("Operação não Permitida! ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public SelectItem[] tiposContaDespesaView() {
        SelectItem[] opcoes = new SelectItem[TipoContaDespesa.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TipoContaDespesa tipo : TipoContaDespesa.values()) {
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


    public List<OrigemContaContabil> getOrigenscc() {
        return origenscc;
    }

    public void setOrigenscc(List<OrigemContaContabil> origenscc) {
        this.origenscc = origenscc;
    }

    public List<ContaContabil> getContas() {
        return contas;
    }

    public void setContas(List<ContaContabil> contas) {
        this.contas = contas;
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
