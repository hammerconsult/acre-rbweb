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
@ManagedBean(name = "oCCNaturezaDividaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-occ-natureza-divida", pattern = "/occ/natureza-divida-publica/novo/", viewId = "/faces/financeiro/origemcontacontabil/naturezadividapublica/edita.xhtml"),
    @URLMapping(id = "editar-occ-natureza-divida", pattern = "/occ/natureza-divida-publica/editar/#{oCCNaturezaDividaControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/naturezadividapublica/edita.xhtml"),
    @URLMapping(id = "ver-occ-natureza-divida", pattern = "/occ/natureza-divida-publica/ver/#{oCCNaturezaDividaControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/naturezadividapublica/visualizar.xhtml"),
    @URLMapping(id = "listar-occ-natureza-divida", pattern = "/occ/natureza-divida-publica/listar/", viewId = "/faces/financeiro/origemcontacontabil/naturezadividapublica/listar.xhtml"),})
public class OCCNaturezaDividaControlador extends PrettyControlador<OCCNaturezaDividaPublica> implements Serializable, CRUD {

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
    private List<CategoriaDividaPublica> listCategorias;
    private CategoriaDividaPublica[] arrayCategoria;
    private String palavra;
    private NaturezaDividaPublica naturezaDividaPublica;

    public OCCNaturezaDividaControlador() {
        super(OCCNaturezaDividaPublica.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/occ/natureza-divida-publica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-occ-natureza-divida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        setEntidadeOCC(EntidadeOCC.NATUREZADIVIDAPUBLICA);
        origenscc = new ArrayList<>();
    }

    @URLAction(mappingId = "ver-occ-natureza-divida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-occ-natureza-divida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
        OCCNaturezaDividaPublica occ = (OCCNaturezaDividaPublica) evento.getComponent().getAttributes().get("occ");
        origenscc.remove(occ);
    }

    public void alterarConta(ActionEvent evento) {
        OCCNaturezaDividaPublica occ = (OCCNaturezaDividaPublica) evento.getComponent().getAttributes().get("occ");
        selecionado = occ;
    }

    public void recuperaEditarVer() {
        OCCNaturezaDividaPublica occ = (OCCNaturezaDividaPublica) selecionado;
        selecionado = (OCCNaturezaDividaPublica) origemOCCFacade.recuperar(OCCNaturezaDividaPublica.class, occ.getId());
        origemNaoAlterada = (OCCNaturezaDividaPublica) origemOCCFacade.recuperar(OCCNaturezaDividaPublica.class, occ.getId());
        setEntidadeOCC(EntidadeOCC.NATUREZADIVIDAPUBLICA);
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
                if (entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
                    for (OrigemContaContabil occ : origenscc) {
                        occ.setInicioVigencia(selecionado.getInicioVigencia());
                        origemOCCFacade.salvar(occ);
                    }
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                } else {
                    origemOCCFacade.salvarNovo(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                }
            } else {
                origemOCCFacade.salvar(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro alterado com sucesso."));
            }
            redireciona();
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
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
            message.setDetail(" O ano é da data é diferente do exercício corrente.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    private Boolean validaSalvar() {
        Boolean controle = Boolean.TRUE;
        if (origenscc.isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Para salvar o registro, é necessário informar uma configuração para a Origem Conta Contábil - Natureza da Dívida Pública. ");
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
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma configuração para a Origem Conta Contábil, " +
                " TAG: : " + ((OCCNaturezaDividaPublica) origemContaContabil).getTagOCC().getCodigo() + " - " + ((OCCNaturezaDividaPublica) origemContaContabil).getTagOCC().getDescricao()
                + ", Conta: " + ((OCCNaturezaDividaPublica) origemContaContabil).getCategoriaDividaPublica().getCodigo() + " - " + ((OCCNaturezaDividaPublica) origemContaContabil).getCategoriaDividaPublica().getDescricao() + ", " +
                " com início de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(((OCCNaturezaDividaPublica) origemContaContabil).getInicioVigencia()));
            controle = Boolean.FALSE;
        }
        return controle;
    }

    public OrigemContaContabil verificaConfiguracaoExistente() {
        OrigemContaContabil configuracaoEncontrada = null;
        if (entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
            for (OrigemContaContabil occ : origenscc) {
                configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorNaturezaDividaPublica(occ);
            }
        } else {
            configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorNaturezaDividaPublica(selecionado);
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

    public void filtrarNatureza() {
        listConta = new ArrayList<Conta>();
        listCategorias = completaNaturezaDaDivida(palavra);
    }

    public List<CategoriaDividaPublica> completaNaturezaDaDivida(String parte) {
        return origemOCCFacade.getCategoriaDividaPublicaFacade().listaFiltrandoNaturezaDividaPorTipoNatureza(parte.trim(), naturezaDividaPublica);
    }

    public Boolean addAllCategorias() {
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
        } else if (arrayCategoria.length <= 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " É necessário informar uma Natureza da Dívida Pública para continuar a operação.");
            controle = Boolean.FALSE;
        }
        if (controle) {
            for (int i = 0; i < arrayCategoria.length; i++) {
                OCCNaturezaDividaPublica selecionadoNova = new OCCNaturezaDividaPublica();
                selecionadoNova.setTagOCC(selecionado.getTagOCC());
                selecionadoNova.setContaContabil(selecionado.getContaContabil());
                selecionadoNova.setContaInterEstado(selecionado.getContaInterEstado());
                selecionadoNova.setContaInterMunicipal(selecionado.getContaInterMunicipal());
                selecionadoNova.setContaInterUniao(selecionado.getContaInterUniao());
                selecionadoNova.setContaIntra(selecionado.getContaIntra());
                selecionadoNova.setTipoContaAuxiliarSiconfi(selecionado.getTipoContaAuxiliarSiconfi());
                selecionadoNova.setOrigem(selecionado.getOrigem());
                selecionadoNova.setReprocessar(selecionado.getReprocessar());
                selecionadoNova.setCategoriaDividaPublica(arrayCategoria[i]);
                selecionadoNova.setInicioVigencia(selecionado.getInicioVigencia());
                selecionadoNova.setFimVigencia(selecionado.getFimVigencia());
                if (podeAdicionarContaNatureza(selecionadoNova)) {
                    origenscc.add(selecionadoNova);
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Natureza da Dívida: " + selecionadoNova.getCategoriaDividaPublica() + " já foi adicionada. Informe uma natureza diferente para a configuração.");
                }
            }
            if (entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
            }
            RequestContext.getCurrentInstance().update("Formulario");
        }
        return controle;
    }

    public Boolean adicionarNaturezaAlterada() {
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

    public boolean podeAdicionarContaNatureza(OCCNaturezaDividaPublica occNaturezaDividaPublica) {
        for (OrigemContaContabil origemContaContabil : origenscc) {
            if (origemContaContabil.getTagOCC().equals(occNaturezaDividaPublica.getTagOCC())) {
                OCCNaturezaDividaPublica occDaVez = (OCCNaturezaDividaPublica) origemContaContabil;
                if (occDaVez.getCategoriaDividaPublica().equals(occNaturezaDividaPublica.getCategoriaDividaPublica())) {
                    return false;
                }
            }
        }
        return true;
    }

    public SelectItem[] naturezaDivida() {
        SelectItem[] toReturn = new SelectItem[NaturezaDividaPublica.values().length + 1];
        toReturn[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (NaturezaDividaPublica tipo : NaturezaDividaPublica.values()) {
            toReturn[i] = new SelectItem(tipo, tipo.toString());
            i++;
        }
        return toReturn;
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
        listCategorias = new ArrayList<>();
        palavra = "";
        naturezaDividaPublica = null;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public List<CategoriaDividaPublica> getListCategorias() {
        return listCategorias;
    }

    public void setListCategorias(List<CategoriaDividaPublica> listCategorias) {
        this.listCategorias = listCategorias;

    }

    public CategoriaDividaPublica[] getArrayCategoria() {
        return arrayCategoria;
    }

    public void setArrayCategoria(CategoriaDividaPublica[] arrayCategoria) {
        this.arrayCategoria = arrayCategoria;
    }

    public NaturezaDividaPublica getNaturezaDividaPublica() {
        return naturezaDividaPublica;
    }

    public void setNaturezaDividaPublica(NaturezaDividaPublica naturezaDividaPublica) {
        this.naturezaDividaPublica = naturezaDividaPublica;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
