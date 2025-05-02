package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SubContaFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 23/10/13
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-conta-financeira", pattern = "/conta-financeira/novo/", viewId = "/faces/tributario/cadastromunicipal/contafinanceira/edita.xhtml"),
    @URLMapping(id = "editar-conta-financeira", pattern = "/conta-financeira/editar/#{contaFinanceiraControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/contafinanceira/edita.xhtml"),
    @URLMapping(id = "ver-conta-financeira", pattern = "/conta-financeira/ver/#{contaFinanceiraControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/contafinanceira/visualizar.xhtml"),
    @URLMapping(id = "listar-conta-financeira", pattern = "/conta-financeira/listar/", viewId = "/faces/tributario/cadastromunicipal/contafinanceira/lista.xhtml"),
    @URLMapping(id = "novo-convenio-receita", pattern = "/conta-financeira/convenio-receita/#{contaFinanceiraControlador.idConvenioReceita}/notificacao/#{contaFinanceiraControlador.notificacao}/", viewId = "/faces/tributario/cadastromunicipal/contafinanceira/edita.xhtml")
})
public class ContaFinanceiraControlador extends PrettyControlador<SubConta> implements Serializable, CRUD {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private SubContaFacade subContaFacade;
    private ConverterAutoComplete converterContaVinculada;
    private ConverterAutoComplete converterContaBancaria;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private ConverterAutoComplete converterConvenioReceita;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConvenioReceita convenioReceita;
    private List<Number> conveniosSelecionados;
    private ContaDeDestinacao contaDeDestinacao;
    private Boolean desabilitaAutoCompleteContaBancaria;
    private Notificacao notificacaoRecuperada;
    private Long notificacao;
    private Long idConvenioReceita;

    public ContaFinanceiraControlador() {
        super(SubConta.class);
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/conta-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return subContaFacade;
    }

    @URLAction(mappingId = "novo-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        desabilitaAutoCompleteContaBancaria = Boolean.FALSE;
        contaDeDestinacao = null;
        conveniosSelecionados = Lists.newArrayList();
    }

    @URLAction(mappingId = "novo-convenio-receita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoConvenio() {
        super.novo();
//        notificacaoRecuperada = NotificacaoService.getService().recuperar(notificacao);
        convenioReceita = (ConvenioReceita) subContaFacade.getConvenioReceitaFacade().recuperar(ConvenioReceita.class, idConvenioReceita);
        selecionado.setTipoRecursoSubConta(TipoRecursoSubConta.CONVENIO_CONGENERE);
        selecionado.setDescricao(convenioReceita.getNomeConvenio().toUpperCase());
        selecionado.setCodigo(convenioReceita.getNumeroTermo());
        selecionado.setSituacao(SituacaoConta.ATIVO);
        adicionarConvenioReceita();
        contaDeDestinacao = null;
        conveniosSelecionados = Lists.newArrayList();
    }

    public void visualizarConvenioReceita(ConvenioReceita convenioReceita) {
        ConvenioReceitaControlador convenioReceitaControlador = (ConvenioReceitaControlador) Util.getControladorPeloNome("convenioReceitaControlador");
        Web.navegacao(getCaminhoOrigem(), convenioReceitaControlador.getCaminhoPadrao() + "ver/" + convenioReceita.getId() + "/", selecionado);
    }

    @URLAction(mappingId = "editar-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        desabilitaAutoCompleteContaBancaria = Boolean.TRUE;
        contaDeDestinacao = null;
        Collections.sort(selecionado.getUnidadesOrganizacionais());
        Collections.sort(selecionado.getSubContaFonteRecs());
        recuperarHierarquias();
        conveniosSelecionados = Lists.newArrayList();
    }

    private void recuperarHierarquias() {
        for (SubContaUniOrg subContaUniOrg : selecionado.getUnidadesOrganizacionais()) {
            UnidadeOrganizacional uo = subContaUniOrg.getUnidadeOrganizacional();
            Date data = DataUtil.montaData(1, 0, subContaUniOrg.getExercicio().getAno()).getTime();
            HierarquiaOrganizacional hierarquiaDaUnidade = subContaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), uo, data);
            subContaUniOrg.setHierarquiaOrganizacional(hierarquiaDaUnidade);
        }

    }

    @URLAction(mappingId = "ver-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        Collections.sort(selecionado.getUnidadesOrganizacionais());
        Collections.sort(selecionado.getSubContaFonteRecs());
        recuperarHierarquias();
    }


    @Override
    public void salvar() {
        try {
            if (validaCampos() && validaDuplicidadeCodigo()) {
                validarConvenios();
                if (isOperacaoNovo()) {
                    subContaFacade.salvarNovaContaFinanceira(selecionado, notificacaoRecuperada);
                    FacesUtil.addOperacaoRealizada("Registro salvo com sucesso!");
                } else {
                    selecionado = subContaFacade.salvarRetornando(selecionado);
                    subContaFacade.salvarUnidadeFonteConvenioReceita(selecionado);
                    FacesUtil.addOperacaoRealizada("Registro alterado com sucesso!");
                }
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void validarConvenios() {
        ValidacaoException ve = new ValidacaoException();
        if (SituacaoConta.ATIVO.equals(selecionado.getSituacao()) &&
            TipoRecursoSubConta.CONVENIO_CONGENERE.equals(selecionado.getTipoRecursoSubConta()) &&
            selecionado.getConvenioReceitas().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessário informar pelo menos 1(um) convênio de receita.");
        }
        ve.lancarException();
    }

    private boolean validaDuplicidadeCodigo() {
        if (subContaFacade.validaSubContaMesmoCodigo(selecionado)) {
            return true;
        }
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma Conta Financeira cadastrada com o código: " + selecionado.getCodigo() + " ");
        return false;
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getContaBancariaEntidade() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório ", " O campo Conta Bancária é obrigatório!"));
            retorno = false;
        }
        if (selecionado.getCodigo() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório ", " O campo Código é obrigatório!"));
            retorno = false;
        }
        if (selecionado.getDescricao() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório ", " O campo Descrição é obrigatório!"));
            retorno = false;
        }
        if (selecionado.getTipoContaFinanceira() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório ", " O campo Tipo de Conta Financeira é obrigatório!"));
            retorno = false;
        }
        if (selecionado.getTipoRecursoSubConta() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório ", " O campo de Tipo Recurso é obrigatório!"));
            retorno = false;
        }
        if (selecionado.getSituacao() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório ", " O campo Situação é obrigatório!"));
            retorno = false;
        }
        return retorno;
    }


    public void addUnidadeOrganizacional() {
        if (hierarquiaOrganizacional == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Selecione uma Unidade Orçamentária para adicionar.");
            return;
        }
        if (hierarquiaOrganizacional != null) {
            SubContaUniOrg su = new SubContaUniOrg();
            if (validaUnidadeMesmoExercicio()) {
                if (this.selecionado.verificarUnidadeExistente(hierarquiaOrganizacional.getSubordinada(), sistemaControlador.getExercicioCorrente())) {
                    su.setSubConta(selecionado);
                    su.setExercicio(sistemaControlador.getExercicioCorrente());
                    su.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
                    su.setHierarquiaOrganizacional(hierarquiaOrganizacional);
                    selecionado.getUnidadesOrganizacionais().add(su);
                    hierarquiaOrganizacional = new HierarquiaOrganizacional();
                    Collections.sort(selecionado.getUnidadesOrganizacionais());
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Unidade Orçamentária já adicionada na lista.");
                }
            }
        }
    }

    public void selecionarConvenio(ActionEvent evento) {
        convenioReceita = (ConvenioReceita) evento.getComponent().getAttributes().get("objeto");
    }

    public void adicionarConvenioReceita() {
        try {
            validarConvenio();
            ConvenioReceitaSubConta crs = new ConvenioReceitaSubConta();
            crs.setSubConta(selecionado);
            crs.setExercicio(sistemaControlador.getExercicioCorrente());
            crs.setConvenioReceita(convenioReceita);
            Util.adicionarObjetoEmLista(selecionado.getConvenioReceitas(), crs);
            convenioReceita = null;
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getAllMensagens());
        }
    }

    public void validarConvenio() {
        ValidacaoException ve = new ValidacaoException();
        if (convenioReceita == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Convênio de Receita é obrigatório.");
        }
        ve.lancarException();
        for (ConvenioReceitaSubConta crs : selecionado.getConvenioReceitas()) {
            if (crs.getExercicio().equals(sistemaControlador.getExercicioCorrente())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Convênio de Receita no exercício " + sistemaControlador.getExercicioCorrente().getAno());
            }
        }
        ve.lancarException();
    }

    public boolean validaUnidadeMesmoExercicio() {
        boolean controle = true;
        for (SubContaUniOrg sub : selecionado.getUnidadesOrganizacionais()) {
            if (sub.getExercicio().getId().equals(sistemaControlador.getExercicioCorrente().getId())) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já foi adicionada uma Unidade Orçamentária para o Exercício " + sistemaControlador.getExercicioCorrente());
                controle = false;
            }
        }
        return controle;
    }

    public void removeUnidadeOrganizacional(ActionEvent evt) {
        SubContaUniOrg s = (SubContaUniOrg) evt.getComponent().getAttributes().get("un");
        selecionado.getUnidadesOrganizacionais().remove(s);
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
    }

    public void removeConvenioReceita(ActionEvent event) {
        ConvenioReceitaSubConta c = (ConvenioReceitaSubConta) event.getComponent().getAttributes().get("convenio");
        selecionado.getConvenioReceitas().remove(c);
        convenioReceita = new ConvenioReceita();
    }

    public void adicionarContaDeDestinacao() {
        try {
            validarContaDeDestinacao();
            SubContaFonteRec subContaFonteRec = new SubContaFonteRec();
            subContaFonteRec.setSubConta(selecionado);
            subContaFonteRec.setFonteDeRecursos(contaDeDestinacao.getFonteDeRecursos());
            subContaFonteRec.setContaDeDestinacao(contaDeDestinacao);
            Util.adicionarObjetoEmLista(selecionado.getSubContaFonteRecs(), subContaFonteRec);
            Collections.sort(selecionado.getSubContaFonteRecs());
            contaDeDestinacao = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarContaDeDestinacao() {
        ValidacaoException ve = new ValidacaoException();
        if (contaDeDestinacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Destinação de Recurso deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.hasContaDeDestinacaoAdicionada(contaDeDestinacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta de Destinação de Recurso selecionada já está adicionada.");
        }
        ve.lancarException();
    }

    public void setaContaBancaria(SelectEvent evt) {
        selecionado.setContaBancariaEntidade((ContaBancariaEntidade) evt.getObject());
    }

    public void setaContaVinculada(SelectEvent evt) {
        selecionado.setContaVinculada((SubConta) evt.getObject());
    }

    public Boolean renderDetalhesSubConta() {
        if (selecionado.getContaVinculada() == null) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean renderDetalhesContaBancaria() {
        if (selecionado.getContaBancariaEntidade() == null) {
            return false;
        } else {
            return true;
        }
    }

    public List<ContaBancariaEntidade> completaContaBancaria(String parte) {
        return subContaFacade.listaContaBancariaEntidade(parte.trim());
    }

    public List<SubConta> completaContaVinculada(String parte) {
        return subContaFacade.listaFiltrandoSubConta(parte.trim());
    }


    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return subContaFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", "ORCAMENTARIA", sistemaControlador.getDataOperacao());
    }

    public List<FonteDeRecursos> completaFonteDeRecursos(String parte) {
        return subContaFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<ConvenioReceita> completaConvenioReceita(String parte) {
        return subContaFacade.getConvenioReceitaFacade().buscarFiltrandoConvenioReceita(parte.trim());
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        return subContaFacade.getContaFacade().buscarContasDeDestinacaoPorCodigoOrDescricao(parte, sistemaControlador.getExercicioCorrente());
    }

    public void removerContaDestinacao(SubContaFonteRec scfr) {
        selecionado.getSubContaFonteRecs().remove(scfr);
    }

    public List<SelectItem> getListaTipoContaFinanceira() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem("", null));
        for (TipoContaFinanceira tcf : TipoContaFinanceira.values()) {
            toReturn.add(new SelectItem(tcf, tcf.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoRecurso() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem("", null));
        for (TipoRecursoSubConta tcf : TipoRecursoSubConta.values()) {
            toReturn.add(new SelectItem(tcf, tcf.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoConta object : SituacaoConta.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, subContaFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public Converter getConverterConvenioReceita() {
        if (converterConvenioReceita == null) {
            converterConvenioReceita = new ConverterAutoComplete(ConvenioReceita.class, subContaFacade.getConvenioReceitaFacade());
        }
        return converterConvenioReceita;
    }

    public Converter getConverterContaBancaria() {
        if (converterContaBancaria == null) {
            converterContaBancaria = new ConverterAutoComplete(ContaBancariaEntidade.class, subContaFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancaria;
    }


    public Converter getConverterContaVinculada() {
        if (converterContaVinculada == null) {
            converterContaVinculada = new ConverterAutoComplete(SubConta.class, subContaFacade);
        }
        return converterContaVinculada;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public Boolean getDesabilitaAutoCompleteContaBancaria() {
        return desabilitaAutoCompleteContaBancaria;
    }

    public void setDesabilitaAutoCompleteContaBancaria(Boolean desabilitaAutoCompleteContaBancaria) {
        this.desabilitaAutoCompleteContaBancaria = desabilitaAutoCompleteContaBancaria;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public Long getIdConvenioReceita() {
        return idConvenioReceita;
    }

    public void setIdConvenioReceita(Long idConvenioReceita) {
        this.idConvenioReceita = idConvenioReceita;
    }

    public Notificacao getNotificacaoRecuperada() {
        return notificacaoRecuperada;
    }

    public void setNotificacaoRecuperada(Notificacao notificacaoRecuperada) {
        this.notificacaoRecuperada = notificacaoRecuperada;
    }

    public Long getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(Long notificacao) {
        this.notificacao = notificacao;
    }

    public List<Number> getConveniosSelecionados() {
        return conveniosSelecionados;
    }

    public void setConveniosSelecionados(List<Number> conveniosSelecionados) {
        this.conveniosSelecionados = conveniosSelecionados;
    }

    public void definirConvenio() {
        if (!conveniosSelecionados.isEmpty()) {
            convenioReceita = subContaFacade.getConvenioReceitaFacade().recuperar(conveniosSelecionados.get(0).longValue());
        }
    }
}
