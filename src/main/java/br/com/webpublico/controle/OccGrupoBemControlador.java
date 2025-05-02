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
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 24/01/14
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "occGrupoBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-occ-grupo-bem", pattern = "/occ/grupo-bem/novo/", viewId = "/faces/financeiro/origemcontacontabil/grupobem/novo.xhtml"),
    @URLMapping(id = "editar-occ-grupo-bem", pattern = "/occ/grupo-bem/editar/#{occGrupoBemControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/grupobem/edita.xhtml"),
    @URLMapping(id = "ver-occ-grupo-bem", pattern = "/occ/grupo-bem/ver/#{occGrupoBemControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/grupobem/visualizar.xhtml"),
    @URLMapping(id = "listar-occ-grupo-bem", pattern = "/occ/grupo-bem/listar/", viewId = "/faces/financeiro/origemcontacontabil/grupobem/listar.xhtml"),})
public class OccGrupoBemControlador extends PrettyControlador<OCCGrupoBem> implements Serializable, CRUD {

    @EJB
    private OrigemOCCFacade origemOCCFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private EntidadeOCC entidadeOCC;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterGrupoBem;
    private ConverterAutoComplete converterTagOCC;
    private OrigemContaContabil origemNaoAlterada;
    private List<OrigemContaContabil> origenscc;
    private List<Conta> listConta;
    private List<GrupoBem> listGrupos;
    private GrupoBem[] arrayGrupo;
    private String palavra;

    public OccGrupoBemControlador() {
        super(OCCGrupoBem.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/occ/grupo-bem/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    @URLAction(mappingId = "novo-occ-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new OCCGrupoBem();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        setEntidadeOCC(EntidadeOCC.GRUPOBEM);
        origenscc = new ArrayList<>();
    }

    @URLAction(mappingId = "ver-occ-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-occ-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void alterarConta(ActionEvent evento) {
        OCCGrupoBem occ = (OCCGrupoBem) evento.getComponent().getAttributes().get("occ");
        selecionado = occ;
    }

    public List<SelectItem> getTipoGruposBem() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoGrupo tg : TipoGrupo.values()) {
            toReturn.add(new SelectItem(tg, tg.getDescricao()));
        }
        return toReturn;
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

    public List<GrupoBem> completaGrupoBemPorTipoGrupoBem(String parte) {
        if (TipoGrupo.BEM_MOVEL_ALIENAR.equals(selecionado.getTipoGrupo())
            || TipoGrupo.BEM_MOVEL_INSERVIVEL.equals(selecionado.getTipoGrupo())
            || TipoGrupo.BEM_MOVEL_INTEGRACAO.equals(selecionado.getTipoGrupo())
            || TipoGrupo.BEM_MOVEL_PRINCIPAL.equals(selecionado.getTipoGrupo())) {
            return origemOCCFacade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte, TipoBem.MOVEIS);
        } else if (TipoGrupo.BEM_IMOVEL_PRINCIPAL.equals(selecionado.getTipoGrupo())
            || TipoGrupo.BEM_IMOVEL_ALIENAR.equals(selecionado.getTipoGrupo())) {
            return origemOCCFacade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte, TipoBem.IMOVEIS);
        } else {
            return origemOCCFacade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte, TipoBem.INTANGIVEIS);
        }
    }

    public void recuperaEditarVer() {
        selecionado = (OCCGrupoBem) origemOCCFacade.recuperar(OCCGrupoBem.class, selecionado.getId());
        origemNaoAlterada = (OCCGrupoBem) origemOCCFacade.recuperar(OCCGrupoBem.class, selecionado.getId());
        setEntidadeOCC(EntidadeOCC.GRUPOBEM);
    }

    private void verificaAlteracoesDaOrigem() {
        try {
            if (origemNaoAlterada != null && !origemNaoAlterada.meuEquals(selecionado)) {
                for (EventoContabil ec : origemOCCFacade.retornaEventosPorTag(selecionado.getTagOCC())) {
                    origemOCCFacade.getEventoContabilFacade().geraEventosReprocessar(ec, selecionado.getId(), selecionado.getClass().getSimpleName());
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void setaNullGrupoBem() {
        selecionado.setGrupoBem(null);
    }

    @Override
    public void salvar() {
        try {
            verificaAlteracoesDaOrigem();
            validarCampos();
            validarOrigemContaContabil();
            if (Operacoes.NOVO.equals(operacao)) {
                if (entidadeOCC.equals(EntidadeOCC.GRUPOBEM)) {
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
            FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
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

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        validarCampos(ve);
        if (Operacoes.NOVO.equals(operacao)) {
            if (origenscc == null || origenscc.isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio(" Para salvar o registro, é necessário informar uma configuração para a Origem Conta Contábil - Grupo Patrimonial. ");
            }
        } else {
            if (selecionado.getGrupoBem() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo grupo patrimonial deve ser informado.");
            }
        }
        ve.lancarException();
    }

    public OrigemContaContabil verificaConfiguracaoExistente(OrigemContaContabil origemContaContabil) {
        return origemOCCFacade.buscarConfiguracaoPorGrupoBem(origemContaContabil, sistemaControlador.getDataOperacao());
    }

    public void validarOrigemContaContabil() {
        ValidacaoException ve = new ValidacaoException();
        if (Operacoes.NOVO.equals(operacao)) {
            for (OrigemContaContabil origemContaContabil : origenscc) {
                validarOrigemContaContabil(origemContaContabil, ve);
            }
        } else {
            validarOrigemContaContabil(selecionado, ve);
        }
        ve.lancarException();
    }

    public void validarOrigemContaContabil(OrigemContaContabil origemContaContabil, ValidacaoException ve) {
        OrigemContaContabil configuracaoEncontrada = verificaConfiguracaoExistente(origemContaContabil);
        if (origemContaContabil.getContaContabil() == null
            && origemContaContabil.getContaInterEstado() == null
            && origemContaContabil.getContaInterMunicipal() == null
            && origemContaContabil.getContaInterUniao() == null
            && origemContaContabil.getContaIntra() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar uma Conta Contábil para o grupo patrimonial " + ((OCCGrupoBem) origemContaContabil).getGrupoBem().getCodigo() + " para continuar a operação.");
        }
        if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe uma configuração para a Origem Conta Contábil, "
                + "TAG: " + configuracaoEncontrada.getTagOCC().getCodigo() + " - "
                + configuracaoEncontrada.getTagOCC().getDescricao() + ", Tipo de Grupo Bem: "
                + ((OCCGrupoBem) configuracaoEncontrada).getTipoGrupo().getDescricao() + ", Grupo Patrimonial: "
                + ((OCCGrupoBem) configuracaoEncontrada).getGrupoBem().getCodigo() + " - "
                + ((OCCGrupoBem) configuracaoEncontrada).getGrupoBem().getDescricao() + ", com início de vigência em: "
                + new SimpleDateFormat("dd/MM/yyyy").format((configuracaoEncontrada).getInicioVigencia()));
        }
    }

    public List<TagOCC> completaTagsOCCPorEntidadeOCC(String parte) {
        return origemOCCFacade.getTagOCCFacade().listaPorEntidadeOCC(parte, EntidadeOCC.GRUPOBEM);
    }

    public OrigemOCCFacade getOrigemOCCFacade() {
        return origemOCCFacade;
    }

    public void setOrigemOCCFacade(OrigemOCCFacade origemOCCFacade) {
        this.origemOCCFacade = origemOCCFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public EntidadeOCC getEntidadeOCC() {
        return entidadeOCC;
    }

    public void setEntidadeOCC(EntidadeOCC entidadeOCC) {
        this.entidadeOCC = entidadeOCC;
    }

    public void validarCampos(ValidacaoException ve) {
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Início de Vigência deve ser informado.");
        }
        if (selecionado.getTagOCC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo TAG deve ser informado.");
        }
        if (((OCCGrupoBem) selecionado).getTipoGrupo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Tipo de Grupo deve ser informado.");
        }
        if (selecionado.getContaContabil() == null
            && selecionado.getContaInterEstado() == null
            && selecionado.getContaInterMunicipal() == null
            && selecionado.getContaInterUniao() == null
            && selecionado.getContaIntra() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar uma Conta Contábil para o continuar a operação.");
        }
    }

    public void addAllGrupo() {
        try {
            ValidacaoException ve = new ValidacaoException();
            validarCampos(ve);
            ve.lancarException();
            for (int i = 0; i < arrayGrupo.length; i++) {
                OCCGrupoBem selecionadoNova = new OCCGrupoBem();
                selecionadoNova.setTagOCC(selecionado.getTagOCC());
                selecionadoNova.setTipoGrupo(selecionado.getTipoGrupo());
                selecionadoNova.setContaContabil(selecionado.getContaContabil());
                selecionadoNova.setContaInterEstado(selecionado.getContaInterEstado());
                selecionadoNova.setContaInterMunicipal(selecionado.getContaInterMunicipal());
                selecionadoNova.setContaInterUniao(selecionado.getContaInterUniao());
                selecionadoNova.setContaIntra(selecionado.getContaIntra());
                selecionadoNova.setTipoContaAuxiliarSiconfi(selecionado.getTipoContaAuxiliarSiconfi());
                selecionadoNova.setOrigem(selecionado.getOrigem());
                selecionadoNova.setReprocessar(selecionado.getReprocessar());
                selecionadoNova.setGrupoBem(arrayGrupo[i]);
                selecionadoNova.setInicioVigencia(selecionado.getInicioVigencia());
                selecionadoNova.setFimVigencia(selecionado.getFimVigencia());
                if (podeAdicionarGrupoBem(selecionadoNova)) {
                    origenscc.add(selecionadoNova);
                } else {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" A conta: " + selecionadoNova.getGrupoBem() + " já foi adicionada. Informe uma conta diferente para a configuração.");
                }
            }
            ve.lancarException();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public boolean podeAdicionarGrupoBem(OCCGrupoBem occGrupo) {
        for (OrigemContaContabil origemContaContabil : origenscc) {
            if (origemContaContabil.getTagOCC().equals(occGrupo.getTagOCC())) {
                OCCGrupoBem occDaVez = (OCCGrupoBem) origemContaContabil;
                if (occDaVez.getGrupoBem().equals(occGrupo.getGrupoBem())) {
                    return false;
                }
            }
        }
        return true;
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

    //Converters
    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, origemOCCFacade.getContaFacade());
        }
        return converterConta;
    }

    public ConverterAutoComplete getConverterTagOCC() {
        if (converterTagOCC == null) {
            converterTagOCC = new ConverterAutoComplete(TagOCC.class, origemOCCFacade.getTagOCCFacade());
        }
        return converterTagOCC;
    }

    public ConverterAutoComplete getConverterGrupoBem() {
        if (converterGrupoBem == null) {
            converterGrupoBem = new ConverterAutoComplete(GrupoBem.class, origemOCCFacade.getGrupoBemFacade());
        }
        return converterGrupoBem;
    }

    public void filtrarGrupo() {
        if (selecionado.getTipoGrupo() == null) {
            FacesUtil.addCampoObrigatorio("Informe o tipo de grupo.");
            return;
        }
        listConta = new ArrayList<Conta>();
        listGrupos = completaGrupoBemPorTipoGrupoBem(palavra);
    }

    public void removeGrupo(ActionEvent evento) {
        OCCGrupoBem occ = (OCCGrupoBem) evento.getComponent().getAttributes().get("occ");
        origenscc.remove(occ);
    }

    //get e sets
    public OrigemContaContabil getOrigemNaoAlterada() {
        return origemNaoAlterada;
    }

    public void setOrigemNaoAlterada(OrigemContaContabil origemNaoAlterada) {
        this.origemNaoAlterada = origemNaoAlterada;
    }

    public void setListConta(List<Conta> listConta) {
        this.listConta = listConta;
        listGrupos = new ArrayList<>();
        palavra = "";
    }

    public void limparListaGrupo() {
        listGrupos = new ArrayList<>();
        palavra = "";
    }

    public List<Conta> getListConta() {
        return listConta;
    }

    public List<GrupoBem> getListGrupos() {
        return listGrupos;
    }

    public void setListGrupos(List<GrupoBem> listGrupos) {
        this.listGrupos = listGrupos;
    }

    public GrupoBem[] getArrayGrupo() {
        return arrayGrupo;
    }

    public void setArrayGrupo(GrupoBem[] arrayGrupo) {
        this.arrayGrupo = arrayGrupo;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public List<OrigemContaContabil> getOrigenscc() {
        return origenscc;
    }

    public void setOrigenscc(List<OrigemContaContabil> origenscc) {
        this.origenscc = origenscc;
    }
}
