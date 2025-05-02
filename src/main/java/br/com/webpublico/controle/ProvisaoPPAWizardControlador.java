package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.PrevistoRealizadoDespesaUnidade;
import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProvisaoPPAFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 01/08/13
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "provisaoPPAWizardControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-provisao-ppa-Despesa", pattern = "/provisao-ppa-despesa/passo-a-passo/novo/", viewId = "/faces/financeiro/ppa/provisaoppadespesawizard/edita.xhtml"),
    @URLMapping(id = "editar-provisao-ppa-Despesa", pattern = "/provisao-ppa-despesa/passo-a-passo/editar/#{provisaoPPAWizardControlador.id}/", viewId = "/faces/financeiro/ppa/provisaoppadespesawizard/edita.xhtml"),
    @URLMapping(id = "ver-provisao-ppa-Despesa", pattern = "/provisao-ppa-despesa/passo-a-passo/ver/#{provisaoPPAWizardControlador.id}/", viewId = "/faces/financeiro/ppa/provisaoppadespesawizard/visualizar.xhtml"),
    @URLMapping(id = "contas-provisao-ppa-Despesa", pattern = "/provisao-ppa-despesa/passo-a-passo/contas/#{provisaoPPAWizardControlador.id}/", viewId = "/faces/financeiro/ppa/provisaoppadespesawizard/contas.xhtml"),
    @URLMapping(id = "nova-conta-provisao-ppa-Despesa", pattern = "/provisao-ppa-despesa/passo-a-passo/contas/#{provisaoPPAWizardControlador.id}/nova-conta/", viewId = "/faces/financeiro/ppa/provisaoppadespesawizard/conta.xhtml"),
    @URLMapping(id = "editar-conta-provisao-ppa-Despesa", pattern = "/provisao-ppa-despesa/passo-a-passo/contas/#{provisaoPPAWizardControlador.id}/editar-conta/#{provisaoPPAWizardControlador.idConta}/", viewId = "/faces/financeiro/ppa/provisaoppadespesawizard/conta.xhtml"),
    @URLMapping(id = "ver-conta-provisao-ppa-Despesa", pattern = "/provisao-ppa-despesa/passo-a-passo/contas/#{provisaoPPAWizardControlador.id}/ver-conta/#{provisaoPPAWizardControlador.idConta}/", viewId = "/faces/financeiro/ppa/provisaoppadespesawizard/visualizar-conta.xhtml"),
    @URLMapping(id = "listar-provisao-ppa-Despesa", pattern = "/provisao-ppa-despesa/passo-a-passo/listar/", viewId = "/faces/financeiro/ppa/provisaoppadespesawizard/lista.xhtml")
})
public class ProvisaoPPAWizardControlador extends PrettyControlador<SubAcaoPPA> implements Serializable, CRUD {

    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Integer intervalo;
    private Long idConta;
    private ConverterAutoComplete converterContaDeDespesa;
    private ConverterAutoComplete converterFonteDeRecursos;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterExtensaoFonteRecurso;
    private ProvisaoPPADespesa provisaoPPADespesa;
    private ProvisaoPPAFonte provisaoPPAFonte;
    private List<ProvisaoPPAFonte> provisoesPPAFonteLoaEfetivada;
    private List<PrevisaoHOContaDestinacao> listaPrevisaoPorUnidade;
    private List<PrevistoRealizadoDespesaUnidade> previsoes;
    private PrevistoRealizadoDespesaUnidade previstoRealizadoDespesaUnidade;

    public ProvisaoPPAWizardControlador() {
        super(SubAcaoPPA.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return provisaoPPAFacade.getSubProjetoAtividadeFacade();
    }

    @URLAction(mappingId = "novo-provisao-ppa-Despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-provisao-ppa-Despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-provisao-ppa-Despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "contas-provisao-ppa-Despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void contas() {
        recuperarSelecionado();
    }

    @URLAction(mappingId = "nova-conta-provisao-ppa-Despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConta() {
        recuperarSelecionado();
        provisaoPPADespesa = new ProvisaoPPADespesa();
        provisaoPPADespesa.setDataRegistro(new Date());
        if (selecionado != null) {
            provisaoPPADespesa.setCodigo(provisaoPPAFacade.getProvisaoPPADespesaFacade().getCodigo(selecionado.getExercicio(), selecionado));
        }
        provisaoPPAFonte = new ProvisaoPPAFonte();
        provisoesPPAFonteLoaEfetivada = new ArrayList<>();
        listaPrevisaoPorUnidade = new ArrayList<>();

        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        provisaoPPADespesa.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
    }

    private void recuperarSelecionado() {
        selecionado = (SubAcaoPPA) Web.pegaDaSessao(SubAcaoPPA.class);
        if (selecionado == null) {
            selecionado = provisaoPPAFacade.getSubProjetoAtividadeFacade().recuperar(super.getId());
        }
    }

    @URLAction(mappingId = "editar-conta-provisao-ppa-Despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarConta() {
        recuperarSelecionado();
        recuperaProvisaoPPADespesa();
        provisaoPPAFonte = new ProvisaoPPAFonte();
        provisoesPPAFonteLoaEfetivada = new ArrayList<>();
        listaPrevisaoPorUnidade = new ArrayList<>();
    }

    public void excluirProvisaoPPADespesa() {
        try {
            provisaoPPAFacade.getProvisaoPPADespesaFacade().remover(provisaoPPADespesa);
            redirecionarParaContas();
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "A Conta foi excluida com sucesso.");
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), e.getMessage());
        }
    }

    private void recuperaProvisaoPPADespesa() {
        provisaoPPADespesa = provisaoPPAFacade.getProvisaoPPADespesaFacade().recuperarComFontes(idConta);
    }

    @URLAction(mappingId = "ver-conta-provisao-ppa-Despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verConta() {
        recuperarSelecionado();
        recuperaProvisaoPPADespesa();
        provisaoPPAFonte = new ProvisaoPPAFonte();
        listaPrevisaoPorUnidade = new ArrayList<>();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/provisao-ppa-despesa/passo-a-passo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    /*
     * GETTER E SETTERS
     */

    public Integer getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Integer intervalo) {
        this.intervalo = intervalo;
    }

    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }

    public ProvisaoPPADespesa getProvisaoPPADespesa() {
        return provisaoPPADespesa;
    }

    public void setProvisaoPPADespesa(ProvisaoPPADespesa provisaoPPADespesa) {
        this.provisaoPPADespesa = provisaoPPADespesa;
    }


    public ProvisaoPPAFonte getProvisaoPPAFonte() {
        return provisaoPPAFonte;
    }

    public void setProvisaoPPAFonte(ProvisaoPPAFonte provisaoPPAFonte) {
        this.provisaoPPAFonte = provisaoPPAFonte;
    }

    /*
     *
     * * REDIRECIONAMENTOS * *
     * */
    public void redirecionarParaContas() {
        Web.poeNaSessao(this.selecionado);
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/");
    }

    public void redirecionarNovaConta() {
        Web.poeNaSessao(this.selecionado);
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/nova-conta/");
    }

    public String redirecionarConta(Object o) {
        if (selecionado.getId() != null) {
            return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/editar-conta/" + Persistencia.getId(o) + "/";
        }
        Web.poeNaSessao(this.selecionado);
        return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/0/editar-conta/" + Persistencia.getId(o) + "/";
    }

    public String redirecionarVerConta(Object o) {
        if (selecionado.getId() != null) {
            return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/ver-conta/" + Persistencia.getId(o) + "/";
        }
        Web.poeNaSessao(this.selecionado);
        return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/0/ver-conta/" + Persistencia.getId(o) + "/";
    }

    public void redicionarInicio() {
        Web.poeNaSessao(selecionado);
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
    }

    public void voltarRedirecionarContas() {
        Web.poeNaSessao(selecionado);
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/");
    }
    /*
     * METODOS UTILIZADOS PELA VIEW
     * */

    public Boolean getVerificaProvisaoDeLoaEfetivada() {
        if (!provisaoPPAFacade.validaEfetivacaoLoa(selecionado.getExercicio()).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isGestorOrcamentoPorExercicio() {
        Boolean toReturn = Boolean.FALSE;
        if (provisaoPPAFacade.getLoaFacade().listaUltimaLoaPorExercicio(selecionado.getExercicio()).getAprovacao() != null) {
            toReturn = Boolean.TRUE;
            if (provisaoPPAFacade.isGestorOrcamento(sistemaControlador.getUsuarioCorrente(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getDataOperacao())) {
                toReturn = Boolean.FALSE;
            }
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterContaDeDespesa() {
        if (converterContaDeDespesa == null) {
            converterContaDeDespesa = new ConverterAutoComplete(Conta.class, provisaoPPAFacade.getContaFacade());
        }
        return converterContaDeDespesa;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, provisaoPPAFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public ConverterAutoComplete getConverterExtensaoFonteRecurso() {
        if (converterExtensaoFonteRecurso == null) {
            converterExtensaoFonteRecurso = new ConverterAutoComplete(ExtensaoFonteRecurso.class, provisaoPPAFacade.getExtensaoFonteRecursoFacade());
        }
        return converterExtensaoFonteRecurso;
    }

    public ConverterAutoComplete getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(Conta.class, provisaoPPAFacade.getContaFacade());
        }
        return converterFonteDeRecursos;
    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        return provisaoPPAFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.toLowerCase(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
    }

    public List<Conta> completaFonteDeRecursos(String parte) {
        if (getVerificaProvisaoDeLoaEfetivada()) {
            return provisaoPPAFacade.getContaFacade().listaFiltrandoDestinacaoRecursos(parte.trim(), selecionado.getExercicio());
        } else {
            return provisaoPPAFacade.getContaFacade().listaFiltrandoDestinacaoRecursosPorProvisaoHOConta(parte.trim(), selecionado.getExercicio(), provisaoPPADespesa.getUnidadeOrganizacional());
        }
    }

    public List<Conta> completaContas(String parte) {
        try {
            List<Conta> contas = provisaoPPAFacade.getContaFacade().listaFiltrandoDespesaCriteria(parte.trim(), selecionado.getExercicio());
            if (contas.isEmpty()) {
                return new ArrayList<Conta>();
            } else {
                return contas;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            return new ArrayList<Conta>();
        }
    }

    public List<SelectItem> getTipoDespesaOrc() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();

        Boolean loaEfetivada = Boolean.FALSE;
        if (!provisaoPPAFacade.validaEfetivacaoLoa(selecionado.getExercicio()).isEmpty()) {
            loaEfetivada = Boolean.TRUE;
        }

        for (TipoDespesaORC object : TipoDespesaORC.values()) {
            if (loaEfetivada) {
                if (object != TipoDespesaORC.ORCAMENTARIA) {
                    toReturn.add(new SelectItem(object, object.getDescricao()));
                }
            } else {
                if (object == TipoDespesaORC.ORCAMENTARIA) {
                    toReturn.add(new SelectItem(object, object.getDescricao()));
                }
            }
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getListaEsferaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (EsferaOrcamentaria eo : EsferaOrcamentaria.values()) {
            if (eo != EsferaOrcamentaria.ORCAMENTOGERAL) {
                toReturn.add(new SelectItem(eo, eo.getDescricao()));
            }
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getExtensaoFonteRecurso() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ExtensaoFonteRecurso obj : provisaoPPAFacade.getExtensaoFonteRecursoFacade().listaDecrescente()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public BigDecimal getSomaTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (provisaoPPADespesa != null) {
            if (provisaoPPADespesa.getProvisaoPPAFontes() != null) {
                for (ProvisaoPPAFonte pro : provisaoPPADespesa.getProvisaoPPAFontes()) {
                    total = total.add(pro.getValor());
                }
            }
        }
        return total;
    }

    public void salvarConta() {
        try {
            provisaoPPADespesa.setSubAcaoPPA(selecionado);
            if (!Util.validaCampos(provisaoPPADespesa)) {
                return;
            }
            if(provisaoPPADespesa.getProvisaoPPAFontes() == null || provisaoPPADespesa.getProvisaoPPAFontes().isEmpty()){
                throw new ExcecaoNegocioGenerica("É obrigatório ter pelo menos uma fonte adicionada.");
            }
            provisaoPPAFacade.getProvisaoPPADespesaFacade().salvarDespesaOrcamentaria(provisaoPPADespesa, provisoesPPAFonteLoaEfetivada,  null);

            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
            redirecionarParaContas();
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    public void adicionarFonte() {
        try {
            provisaoPPADespesa.setSubAcaoPPA(selecionado);
            provisaoPPAFonte.setProvisaoPPADespesa(provisaoPPADespesa);
            Util.validarCampos(provisaoPPADespesa);
            validarCamposProvisaoPPAFonte(provisaoPPAFonte);
            validarFonteJaAdicionada(provisaoPPADespesa, provisaoPPAFonte);
            validarValorPrevistoComRealizado();
            validarFonteDeRecurso();
            provisaoPPAFonte.setProvisaoPPADespesa(provisaoPPADespesa);
            provisaoPPADespesa.setProvisaoPPAFontes(Util.adicionarObjetoEmLista(provisaoPPADespesa.getProvisaoPPAFontes(), provisaoPPAFonte));
            provisaoPPADespesa.setValor(getSomaTotal());
            Util.adicionarObjetoEmLista(provisoesPPAFonteLoaEfetivada, provisaoPPAFonte);
            provisaoPPAFonte = new ProvisaoPPAFonte();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarValorPrevistoComRealizado() {
        ValidacaoException ve = new ValidacaoException();
        try {
            recuperarPrevistoRealizadoDespesaUnidade();
            if (provisaoPPADespesa.getProvisaoPPAFontes().isEmpty()) {
                if (provisaoPPAFonte.getValor().compareTo(previstoRealizadoDespesaUnidade.getSaldo()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" O Valor informado de <b>"
                        + Util.formataValor(provisaoPPAFonte.getValor()) + "</b> é maior do que o saldo disponível previsto de <b>"
                        + Util.formataValor(previstoRealizadoDespesaUnidade.getSaldo()) + "</b> para a Fonte de Recurso " +
                        previstoRealizadoDespesaUnidade.getConta().toString() + " e Unidade " +
                        provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional().getDescricao() + ".");
                }
            }
            ve.lancarException();
            for (ProvisaoPPAFonte ppaFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
                if (ppaFonte.equals(provisaoPPAFonte)) {
                    if (provisaoPPAFonte.getId() != null) {
                        previstoRealizadoDespesaUnidade.setSaldo(previstoRealizadoDespesaUnidade.getSaldo().add(ppaFonte.getValor()));
                    }
                    if (provisaoPPAFonte.getValor().compareTo(previstoRealizadoDespesaUnidade.getSaldo()) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(" O Valor informado de <b>"
                            + Util.formataValor(provisaoPPAFonte.getValor()) + "</b> é maior do que o saldo disponível previsto de <b>"
                            + Util.formataValor(previstoRealizadoDespesaUnidade.getSaldo()) + "</b> para a Fonte de Recurso " +
                            previstoRealizadoDespesaUnidade.getConta().toString() + " e Unidade " +
                            provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional().getDescricao() + ".");
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        ve.lancarException();
    }

    private void recuperarPrevistoRealizadoDespesaUnidade() {
        List<PrevistoRealizadoDespesaUnidade> previstoRealizadoDespesaUnidades = provisaoPPAFacade.getHierarquiaOrganizacionalFacade().buscarCriarPrevistoRealizadoPorUnidadeExercicio(selecionado.getAcaoPPA().getResponsavel(), selecionado.getExercicio(), provisaoPPAFonte.getDestinacaoDeRecursosAsContaDeDestinacao());
        if (!previstoRealizadoDespesaUnidades.isEmpty()) {
            previstoRealizadoDespesaUnidade = previstoRealizadoDespesaUnidades.get(0);
        }
    }

    private void validarFonteJaAdicionada(ProvisaoPPADespesa provisaoPPADespesa, ProvisaoPPAFonte provisaoPPAFonte) {
        ValidacaoException ve = new ValidacaoException();
        for (ProvisaoPPAFonte ppaFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
            if (!ppaFonte.equals(provisaoPPAFonte) && ppaFonte.getDestinacaoDeRecursos().getId().equals(provisaoPPAFonte.getDestinacaoDeRecursos().getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A Fonte de Recurso " + ppaFonte.getDestinacaoDeRecursos() + " já foi adicionada.");
            }
        }
        ve.lancarException();
    }


    public void recuperarProvisoesPorUnidadeOrganizacional() {
        try {
            listaPrevisaoPorUnidade = provisaoPPAFacade.getHierarquiaOrganizacionalFacade().recuperarPrevisaoHOContaDestinacao(provisaoPPADespesa.getUnidadeOrganizacional(), selecionado.getExercicio());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao recuperar Unidade Organizacional ", e.getMessage());
        }
    }

    public void buscarValoresPrevistoRealizadoPorUnidade() {
        try {
            previsoes = provisaoPPAFacade.getHierarquiaOrganizacionalFacade().buscarCriarPrevistoRealizadoPorUnidadeExercicio(selecionado.getAcaoPPA().getResponsavel(), selecionado.getExercicio(), null);
        } catch (Exception e) {
            previsoes = Lists.newArrayList();
            FacesUtil.addError("Erro as previsões da Unidade " + selecionado.getAcaoPPA().getResponsavel(), e.getMessage());
        }
    }

    public BigDecimal getValorTotalPrevisto() {
        try {
            BigDecimal valor = BigDecimal.ZERO;
            for (PrevistoRealizadoDespesaUnidade previsoe : previsoes) {
                valor = valor.add(previsoe.getValorPrevisto());
            }
            return valor;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalDespesa() {
        try {
            BigDecimal valor = BigDecimal.ZERO;
            for (PrevistoRealizadoDespesaUnidade previsoe : previsoes) {
                valor = valor.add(previsoe.getTotalDespesa());
            }
            return valor;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalSaldo() {
        try {
            BigDecimal valor = BigDecimal.ZERO;
            for (PrevistoRealizadoDespesaUnidade previsoe : previsoes) {
                valor = valor.add(previsoe.getSaldo());
            }
            return valor;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalMetasFinanceiras() {
        BigDecimal soma = BigDecimal.ZERO;
        try {
            listaPrevisaoPorUnidade = provisaoPPAFacade.getHierarquiaOrganizacionalFacade().recuperarPrevisaoHOContaDestinacao(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), selecionado.getExercicio());
            if (listaPrevisaoPorUnidade != null) {
                for (PrevisaoHOContaDestinacao prev : listaPrevisaoPorUnidade) {
                    soma = soma.add(prev.getValor());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return soma;
    }

    public BigDecimal getSomaPrevisoes() {
        BigDecimal soma = BigDecimal.ZERO;
        for (PrevisaoHOContaDestinacao prev : listaPrevisaoPorUnidade) {
            soma = soma.add(prev.getValor());
        }
        return soma;
    }

    public void alterarProvisaoFonte(ProvisaoPPAFonte provisaoPPAFonte) {
        try {
            this.provisaoPPAFonte = (ProvisaoPPAFonte) Util.clonarObjeto(provisaoPPAFonte);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível alterar a Fonte de Recurso.");
        }
    }

    private void removerFonte(ProvisaoPPAFonte provisaoPPAFonte) {
        if (provisaoPPAFonte.getId() != null) {
            provisaoPPAFacade.removerFonte(provisaoPPAFonte);
        }
    }

    public void removerProvisaoFonte(ProvisaoPPAFonte provisaoPPAFonte) {
        try {
            provisaoPPADespesa.getProvisaoPPAFontes().remove(provisaoPPAFonte);
            provisaoPPADespesa.setValor(getSomaTotal());
            removerFonte(provisaoPPAFonte);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível remover a Fonte de Recurso.");
        }
    }

    private void validarCamposProvisaoPPAFonte(ProvisaoPPAFonte provisaoPPAFonte) {
        ValidacaoException ve = new ValidacaoException();
        if (provisaoPPAFonte.getDestinacaoDeRecursos() == null || provisaoPPAFonte.getDestinacaoDeRecursos().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Fonte de Recursos deve ser informado.");
        }
        if (provisaoPPAFonte.getExtensaoFonteRecurso() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Extensão da Fonte de Recurso deve ser informado.");
        }
        if (provisaoPPAFonte.getValor() == null) {
            provisaoPPAFonte.setValor(BigDecimal.ZERO);
        }
        if (!getVerificaProvisaoDeLoaEfetivada()) {
            if (provisaoPPAFonte.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor deve ser maior que 0 (ZERO).");
            }
        }
        ve.lancarException();
    }

    public void validarFonteDeRecurso() {
        if (provisaoPPADespesa.getProvisaoPPAFontes() == null) {
            provisaoPPADespesa.setProvisaoPPAFontes(new ArrayList<ProvisaoPPAFonte>());
        }
        ValidacaoException ve = new ValidacaoException();
        List<PrevisaoHOContaDestinacao> provisoes = provisaoPPAFacade.getProvisaoPPADespesaFacade().getHierarquiaOrganizacionalFacade().listaPrevisoesPorUnidade(provisaoPPADespesa.getUnidadeOrganizacional(), selecionado.getExercicio());
        if (!provisoes.isEmpty()) {
            if (!provisaoPPAFacade.getProvisaoPPADespesaFacade().getHierarquiaOrganizacionalFacade().validaSeAFonteExiste(provisaoPPADespesa.getUnidadeOrganizacional(), provisaoPPAFonte.getDestinacaoDeRecursos(), selecionado.getExercicio())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Não existe lançamento para a Fonte de Recurso " + provisaoPPAFonte.getDestinacaoDeRecursos()
                    + " na unidade " + provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional()
                    + " para este exercício.");
            } else {
                validarValorFonteComPrevisao(ve);
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrada nenhuma Fonte de Recurso lançada para a unidade " + provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional() + " para esse exercício.");
        }
        ve.lancarException();
    }

    private void validarValorFonteComPrevisao(ValidacaoException ve) {
        Exercicio ex = provisaoPPAFonte.getProvisaoPPADespesa().getSubAcaoPPA().getExercicio();
        ContaDeDestinacao conta = (ContaDeDestinacao) provisaoPPAFonte.getDestinacaoDeRecursos();
        UnidadeOrganizacional uni = provisaoPPADespesa.getUnidadeOrganizacional();
        BigDecimal valorPrevisao = provisaoPPAFacade.getProvisaoPPADespesaFacade().retornaValorDasPrevisoesPorContaUnidadeExerc(ex, conta, uni);
        BigDecimal valorFonte = provisaoPPAFacade.getProvisaoPPADespesaFacade().retornaValorFontesPorExercicioUnidadeConta(ex, conta, uni);
        BigDecimal saldoDisponivel = valorPrevisao.subtract(valorFonte);
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ProvisaoPPAFonte pp : provisaoPPADespesa.getProvisaoPPAFontes()) {
            if (pp.getId() == null && pp.getDestinacaoDeRecursos().equals(conta)) {
                valorTotal = valorTotal.add(pp.getValor());
            }
        }
        valorFonte = valorFonte.add(valorTotal).add(provisaoPPAFonte.getValor());
        if (valorFonte.compareTo(valorPrevisao) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O Valor não pode ultrapassar o estipulado para essa fonte no cadastro da unidade. Saldo Disponível : " + new DecimalFormat("#,##0.00").format(saldoDisponivel));
        }
    }

    public void calcularPorIntervalo() {
        try {
            LDO ldo = provisaoPPAFacade.getLdoFacade().listaVigenteNoExercicio(selecionado.getExercicio());

            provisaoPPAFacade.getProvisaoPPADespesaFacade().recalculaCodigoReduzido(ldo, selecionado.getExercicio(), intervalo);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Recálculo feito com sucesso.");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), " "));
        }
    }


    public BigDecimal getSomaDespesa() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado == null) {
            recuperarSelecionado();
        }
        List<ProvisaoPPADespesa> retorno = getProvisaoPPADespesas();
        for (ProvisaoPPADespesa pro : retorno) {
            if (pro.getValor() != null) {
                total = total.add(pro.getValor());
            }
        }
        return total;
    }

    public List<ProvisaoPPADespesa> getProvisaoPPADespesas() {
        try {
            return provisaoPPAFacade.getProvisaoPPADespesaFacade().listaProvisaoDispesaPPA(selecionado);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public BigDecimal getFalta() {
        try {
            return getValorTotalMetasFinanceiras().subtract(getSomaDespesa());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<PrevisaoHOContaDestinacao> getListaPrevisaoPorUnidade() {
        return listaPrevisaoPorUnidade;
    }

    public void setListaPrevisaoPorUnidade(List<PrevisaoHOContaDestinacao> listaPrevisaoPorUnidade) {
        this.listaPrevisaoPorUnidade = listaPrevisaoPorUnidade;
    }

    public List<PrevistoRealizadoDespesaUnidade> getPrevisoes() {
        return previsoes;
    }

    public void setPrevisoes(List<PrevistoRealizadoDespesaUnidade> previsoes) {
        this.previsoes = previsoes;
    }

    public ProvisaoPPAFacade getProvisaoPPAFacade() {
        return provisaoPPAFacade;
    }

    public PrevistoRealizadoDespesaUnidade getPrevistoRealizadoDespesaUnidade() {
        return previstoRealizadoDespesaUnidade;
    }

    public void setPrevistoRealizadoDespesaUnidade(PrevistoRealizadoDespesaUnidade previstoRealizadoDespesaUnidade) {
        this.previstoRealizadoDespesaUnidade = previstoRealizadoDespesaUnidade;
    }

    public List<SubAcaoPPA> completarSubProjetoAtividade(String parte) {
        return provisaoPPAFacade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(parte, getSistemaControlador().getExercicioCorrente());
    }
}
