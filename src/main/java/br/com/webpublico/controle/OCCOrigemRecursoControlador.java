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
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 09/01/14
 * Time: 17:23
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "oCCOrigemRecursoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-occ-origem-recurso", pattern = "/occ/origem-recurso/novo/", viewId = "/faces/financeiro/origemcontacontabil/origemrecurso/edita.xhtml"),
        @URLMapping(id = "editar-occ-origem-recurso", pattern = "/occ/origem-recurso/editar/#{oCCOrigemRecursoControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/origemrecurso/edita.xhtml"),
        @URLMapping(id = "ver-occ-origem-recurso", pattern = "/occ/origem-recurso/ver/#{oCCOrigemRecursoControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/origemrecurso/visualizar.xhtml"),
        @URLMapping(id = "listar-occ-origem-recurso", pattern = "/occ/origem-recurso/listar/", viewId = "/faces/financeiro/origemcontacontabil/origemrecurso/listar.xhtml"),})
public class OCCOrigemRecursoControlador extends PrettyControlador<OCCOrigemRecurso> implements Serializable, CRUD {

    @EJB
    private OrigemOCCFacade origemOCCFacade;
    private EntidadeOCC entidadeOCC;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterTagOCC;
    private OrigemContaContabil origemNaoAlterada;
    private List<OrigemContaContabil> origenscc;
    private OrigemSuplementacaoORC[] origemSuplementacaoORCsSelecionadas;

    public OCCOrigemRecursoControlador() {
        super(OCCOrigemRecurso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/occ/origem-recurso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-occ-origem-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        setEntidadeOCC(EntidadeOCC.ORIGEM_RECURSO);
        origenscc = new ArrayList<>();
    }

    @URLAction(mappingId = "ver-occ-origem-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-occ-origem-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        OrigemContaContabil occ = (OrigemContaContabil) selecionado;
        selecionado = (OCCOrigemRecurso) origemOCCFacade.recuperar(OCCOrigemRecurso.class, occ.getId());
        origemNaoAlterada = (OCCOrigemRecurso) origemOCCFacade.recuperar(OCCOrigemRecurso.class, occ.getId());
        setEntidadeOCC(EntidadeOCC.ORIGEM_RECURSO);
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
                if (entidadeOCC.equals(EntidadeOCC.ORIGEM_RECURSO)) {
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
            message.setSummary("Operação não Permitida!");
            message.setDetail(" O ano da data é diferente do exercício corrente.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    private Boolean validaSalvar() {
        Boolean controle = Boolean.TRUE;
        if (origenscc.isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(),  " Para salvar o registro, é necessário informar uma configuração para a Origem Conta Contábil - Origem de Recurso. ");
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
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma configuração para a Origem Conta Contábil, TAG: " + ((OCCOrigemRecurso) origemContaContabil).getTagOCC().getCodigo() + " - " + ((OCCOrigemRecurso) origemContaContabil).getTagOCC().getDescricao() +  ", Conta: " + ((OCCOrigemRecurso) origemContaContabil).getOrigemSuplementacaoORC().getDescricao() + ", com início de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(((OCCOrigemRecurso) origemContaContabil).getInicioVigencia()));
            controle = Boolean.FALSE;
        }
        return controle;
    }

    public OrigemContaContabil verificaConfiguracaoExistente() {
        OrigemContaContabil configuracaoEncontrada = null;
        if (entidadeOCC.equals(EntidadeOCC.ORIGEM_RECURSO)) {
            for (OrigemContaContabil occ : origenscc) {
                configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorOrigemRecurso(occ);
            }
        } else {
            configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorOrigemRecurso(selecionado);
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

    public Boolean addAllOrigensRecurso() {
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
        else if (origemSuplementacaoORCsSelecionadas.length <= 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " É necessário informar uma Origem do Recurso para continuar a operação.");
            controle = Boolean.FALSE;
        }
        if (controle) {
            for (int i = 0; i < origemSuplementacaoORCsSelecionadas.length; i++) {
                OCCOrigemRecurso selecionadoNova = new OCCOrigemRecurso();
                selecionadoNova.setTagOCC(selecionado.getTagOCC());
                selecionadoNova.setContaContabil(selecionado.getContaContabil());
                selecionadoNova.setContaInterEstado(selecionado.getContaInterEstado());
                selecionadoNova.setContaInterMunicipal(selecionado.getContaInterMunicipal());
                selecionadoNova.setContaInterUniao(selecionado.getContaInterUniao());
                selecionadoNova.setContaIntra(selecionado.getContaIntra());
                selecionadoNova.setTipoContaAuxiliarSiconfi(selecionado.getTipoContaAuxiliarSiconfi());
                selecionadoNova.setOrigem(selecionado.getOrigem());
                selecionadoNova.setReprocessar(selecionado.getReprocessar());
                selecionadoNova.setOrigemSuplementacaoORC(origemSuplementacaoORCsSelecionadas[i]);
                selecionadoNova.setInicioVigencia(selecionado.getInicioVigencia());
                selecionadoNova.setFimVigencia(selecionado.getFimVigencia());
                if (podeAdicionarContaNatureza(selecionadoNova)) {
                    origenscc.add(selecionadoNova);
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(),  " A origem do recurso: " + selecionadoNova.getOrigemSuplementacaoORC().getDescricao() + " já foi adicionada. Informe uma origem diferente para a configuração.");
                }
            }
        }
        RequestContext.getCurrentInstance().update("Formulario");
        return controle;
    }

    public Boolean adicionarOrigemRecursoAlterado() {
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
            message.setSummary("Operação não Realizada!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public boolean podeAdicionarContaNatureza(OCCOrigemRecurso occOrigemRecurso) {
        for (OrigemContaContabil origemContaContabil : origenscc) {
            if (origemContaContabil.getTagOCC().equals(occOrigemRecurso.getTagOCC())) {
                OCCOrigemRecurso occDaVez = (OCCOrigemRecurso) origemContaContabil;
                if (occDaVez.getOrigemSuplementacaoORC().equals(occOrigemRecurso.getOrigemSuplementacaoORC())) {
                    return false;
                }
            }
        }
        return true;
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

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<OrigemSuplementacaoORC> getListaOrigens() {
        return Arrays.asList(OrigemSuplementacaoORC.values());
    }

    public OrigemSuplementacaoORC[] getOrigemSuplementacaoORCsSelecionadas() {
        return origemSuplementacaoORCsSelecionadas;
    }

    public void setOrigemSuplementacaoORCsSelecionadas(OrigemSuplementacaoORC[] origemSuplementacaoORCsSelecionadas) {
        this.origemSuplementacaoORCsSelecionadas = origemSuplementacaoORCsSelecionadas;
    }

    public void removeOrigemRecurso(ActionEvent evento) {
        OCCOrigemRecurso occ = (OCCOrigemRecurso) evento.getComponent().getAttributes().get("occ");
        origenscc.remove(occ);
    }

    public void alterarConta(ActionEvent evento) {
        OCCOrigemRecurso occ = (OCCOrigemRecurso) evento.getComponent().getAttributes().get("occ");
        selecionado = occ;
    }
}
