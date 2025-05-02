/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Edi
 */
@ManagedBean(name = "oCCGrupoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-occ-grupo-material", pattern = "/occ/grupo-material/novo/", viewId = "/faces/financeiro/origemcontacontabil/grupomaterial/edita.xhtml"),
    @URLMapping(id = "editar-occ-grupo-material", pattern = "/occ/grupo-material/editar/#{oCCGrupoMaterialControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/grupomaterial/edita.xhtml"),
    @URLMapping(id = "ver-occ-grupo-material", pattern = "/occ/grupo-material/ver/#{oCCGrupoMaterialControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/grupomaterial/visualizar.xhtml"),
    @URLMapping(id = "listar-occ-grupo-material", pattern = "/occ/grupo-material/listar/", viewId = "/faces/financeiro/origemcontacontabil/grupomaterial/listar.xhtml"),})
public class OCCGrupoMaterialControlador extends PrettyControlador<OCCGrupoMaterial> implements Serializable, CRUD {

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
    private List<GrupoMaterial> listGrupos;
    private GrupoMaterial[] arrayGrupo;
    private String palavra;

    public OCCGrupoMaterialControlador() {
        super(OCCGrupoMaterial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/occ/grupo-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-occ-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        setEntidadeOCC(EntidadeOCC.GRUPOMATERIAL);
        origenscc = new ArrayList<>();
    }

    @URLAction(mappingId = "ver-occ-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-occ-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void removeGrupo(ActionEvent evento) {
        OCCGrupoMaterial occ = (OCCGrupoMaterial) evento.getComponent().getAttributes().get("occ");
        origenscc.remove(occ);
    }

    public void alterarConta(ActionEvent evento) {
        OCCGrupoMaterial occ = (OCCGrupoMaterial) evento.getComponent().getAttributes().get("occ");
        selecionado = occ;
    }

    public void recuperaEditarVer() {
        OrigemContaContabil occ = (OrigemContaContabil) selecionado;
        selecionado = (OCCGrupoMaterial) origemOCCFacade.recuperar(OCCGrupoMaterial.class, occ.getId());
        origemNaoAlterada = (OCCGrupoMaterial) origemOCCFacade.recuperar(OCCGrupoMaterial.class, occ.getId());
        setEntidadeOCC(EntidadeOCC.GRUPOMATERIAL);
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
                if (entidadeOCC.equals(EntidadeOCC.GRUPOMATERIAL)) {
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
            message.setDetail(" O ano da data é diferente do exercício corrente.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    private Boolean validaSalvar() {
        Boolean controle = Boolean.TRUE;
        if (origenscc.isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Para salvar o registro, é necessário informar uma configuração para a Origem Conta Contábil - Conta de Despesa. ");
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
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma configuração para a Origem Conta Contábil, TAG: "
                + ((OCCGrupoMaterial) origemContaContabil).getTagOCC().getCodigo() + " - " + ((OCCGrupoMaterial) origemContaContabil).getTagOCC().getDescricao() + ", Conta: "
                + ((OCCGrupoMaterial) origemContaContabil).getGrupoMaterial().getCodigo() + " - " + ((OCCGrupoMaterial) origemContaContabil).getGrupoMaterial().getDescricao()
                + ", com início de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(((OCCGrupoMaterial) origemContaContabil).getInicioVigencia()));
            controle = Boolean.FALSE;
        }
        return controle;
    }

    public boolean podeAdicionarGrupoMaterial(OCCGrupoMaterial occGrupo) {
        for (OrigemContaContabil origemContaContabil : origenscc) {
            if (origemContaContabil.getTagOCC().equals(occGrupo.getTagOCC())) {
                OCCGrupoMaterial occDaVez = (OCCGrupoMaterial) origemContaContabil;
                if (occDaVez.getGrupoMaterial().equals(occGrupo.getGrupoMaterial())) {
                    return false;
                }
            }
        }
        return true;
    }

    public OrigemContaContabil verificaConfiguracaoExistente() {
        OrigemContaContabil configuracaoEncontrada = null;
        if (entidadeOCC.equals(EntidadeOCC.GRUPOMATERIAL)) {
            for (OrigemContaContabil occ : origenscc) {
                configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorGrupoMaterial(occ);
            }
        } else {
            configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorGrupoMaterial(selecionado);
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

    public void filtrarGrupo() {
        listConta = new ArrayList<Conta>();
        listGrupos = completaGrupoMaterial(palavra);
    }

    public List<GrupoMaterial> completaGrupoMaterial(String parte) {
        return origemOCCFacade.getGrupoMaterialFacade().listaFiltrandoGrupoDeMaterialOrigemContaContabil(parte.trim());
    }

    public Boolean addAllGrupo() {
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
        } else if (arrayGrupo.length <= 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " É necessário informar uma Conta de Despesa para continuar a operação.");
            controle = Boolean.FALSE;
        }
        if (controle) {
            for (int i = 0; i < arrayGrupo.length; i++) {
                OCCGrupoMaterial selecionadoNova = new OCCGrupoMaterial();
                selecionadoNova.setTagOCC(selecionado.getTagOCC());
                selecionadoNova.setContaContabil(selecionado.getContaContabil());
                selecionadoNova.setContaInterEstado(selecionado.getContaInterEstado());
                selecionadoNova.setContaInterMunicipal(selecionado.getContaInterMunicipal());
                selecionadoNova.setContaInterUniao(selecionado.getContaInterUniao());
                selecionadoNova.setContaIntra(selecionado.getContaIntra());
                selecionadoNova.setTipoContaAuxiliarSiconfi(selecionado.getTipoContaAuxiliarSiconfi());
                selecionadoNova.setOrigem(selecionado.getOrigem());
                selecionadoNova.setReprocessar(selecionado.getReprocessar());
                selecionadoNova.setGrupoMaterial(arrayGrupo[i]);
                selecionadoNova.setInicioVigencia(selecionado.getInicioVigencia());
                selecionadoNova.setFimVigencia(selecionado.getFimVigencia());
                if (podeAdicionarGrupoMaterial(selecionadoNova)) {
                    origenscc.add(selecionadoNova);
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A conta: " + selecionadoNova.getGrupoMaterial() + " já foi adicionada. Informe uma conta diferente para a configuração.");
                }
            }
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

    public Boolean adicionarGrupoAlterado() {
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
        listGrupos = new ArrayList<>();
        palavra = "";
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public List<GrupoMaterial> getListGrupos() {
        return listGrupos;
    }

    public void setListGrupos(List<GrupoMaterial> listGrupos) {
        this.listGrupos = listGrupos;
    }

    public GrupoMaterial[] getArrayGrupo() {
        return arrayGrupo;
    }

    public void setArrayGrupo(GrupoMaterial[] arrayGrupo) {
        this.arrayGrupo = arrayGrupo;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
