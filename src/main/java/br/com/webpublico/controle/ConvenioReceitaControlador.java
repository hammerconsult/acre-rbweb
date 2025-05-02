
/*
 * Codigo gerado automaticamente em Thu Apr 05 10:40:14 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConvenioReceitaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoconvenioreceita", pattern = "/convenio-receita/novo/", viewId = "/faces/financeiro/convenios/receita/convenioreceita/edita.xhtml"),
    @URLMapping(id = "editarconvenioreceita", pattern = "/convenio-receita/editar/#{convenioReceitaControlador.id}/", viewId = "/faces/financeiro/convenios/receita/convenioreceita/edita.xhtml"),
    @URLMapping(id = "verconvenioreceita", pattern = "/convenio-receita/ver/#{convenioReceitaControlador.id}/", viewId = "/faces/financeiro/convenios/receita/convenioreceita/visualizar.xhtml"),
    @URLMapping(id = "listarconvenioreceita", pattern = "/convenio-receita/listar/", viewId = "/faces/financeiro/convenios/receita/convenioreceita/lista.xhtml")
})
public class ConvenioReceitaControlador extends PrettyControlador<ConvenioReceita> implements Serializable, CRUD {

    @EJB
    private ConvenioReceitaFacade convenioReceitaFacade;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterEntidadeRepassadora;
    private ConverterAutoComplete converterUnidadeMedida;
    private ConverterGenerico converterPeriodicidade;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterSubConta;
    private ConverterAutoComplete converterInterveniente;
    private ConverterAutoComplete converterTipoInterveniente;
    private ConverterAutoComplete converterOcorrencia;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterClasseCredor;
    private Interveniente intervenienteSelecionado;
    private AndamentoConvenio andamentoConvenio;
    private PrestacaoContas prestacaoContas;
    private ConvenioReceitaLiberacao convenioReceitaLiberacoes;
    private List<AndamentoConvenio> listaAndamentos;
    private List<AndamentoConvenio> andamentosRemovidos;
    private Aditivos aditivos;
    private List<Aditivos> listaAditivos;
    private Conta conta;
    private List<Conta> listaConta;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private TramiteConvenioRec tramiteConvenioRecSelecionado;
    private ConvenioReceitaUnidMedida convenioReceitaUnidMedida;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private SubConta subConta;
    private List<HierarquiaOrganizacional> listaUnidades;
    private List<PrestacaoContas> listaPrestacaoContas;
    //    private List<SubConta> listaSubConta;
    private List<Long> listaSubConta;
    private Boolean pertenceEmenda;

    public ConvenioReceitaControlador() {
        super(ConvenioReceita.class);
    }

    @URLAction(mappingId = "novoconvenioreceita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        intervenienteSelecionado = new Interveniente();
        prestacaoContas = new PrestacaoContas();
        convenioReceitaLiberacoes = new ConvenioReceitaLiberacao();
        andamentoConvenio = new AndamentoConvenio();
        listaAndamentos = new ArrayList<>();
        andamentosRemovidos = new ArrayList<>();
        listaAditivos = new ArrayList<>();
        listaSubConta = new ArrayList<>();
        listaConta = new ArrayList<>();
        tramiteConvenioRecSelecionado = new TramiteConvenioRec();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        subConta = new SubConta();
        aditivos = new Aditivos();
        conta = new Conta();
        listaUnidades = new ArrayList<>();
        convenioReceitaUnidMedida = new ConvenioReceitaUnidMedida();
        pertenceEmenda = Boolean.FALSE;
    }

    @URLAction(mappingId = "editarconvenioreceita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaPrestacaoContas();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "verconvenioreceita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    public void recuperarEditarVer() {
        intervenienteSelecionado = new Interveniente();
        andamentoConvenio = new AndamentoConvenio();
        aditivos = new Aditivos();
        conta = new Conta();
        prestacaoContas = new PrestacaoContas();
        convenioReceitaLiberacoes = new ConvenioReceitaLiberacao();
        listaAndamentos = new ArrayList<>();
        andamentosRemovidos = new ArrayList<>();
        listaAditivos = new ArrayList<>();
        listaPrestacaoContas = new ArrayList<>();
        listaConta = new ArrayList<>();
        listaSubConta = new ArrayList<>();
        tramiteConvenioRecSelecionado = new TramiteConvenioRec();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        subConta = new SubConta();
        convenioReceitaUnidMedida = new ConvenioReceitaUnidMedida();
        listaUnidades = new ArrayList<>();
        pertenceEmenda = selecionado.getEmendaGoverno() != null;
        recuperaUnidades();

//        alimentaListaSubcontas();
        for (ConvenioReceitaUnidade cru : selecionado.getConvenioReceitaUnidades()) {
            HierarquiaOrganizacional h = new HierarquiaOrganizacional();
            h.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
            h.setSubordinada(cru.getUnidadeOrganizacional());
            h = convenioReceitaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(cru.getUnidadeOrganizacional(), h, sistemaControlador.getExercicioCorrente());
            listaUnidades.add(h);
        }
        if (selecionado.getValorRepasse() == null) {
            selecionado.setValorRepasse(BigDecimal.ZERO);
        }
        if (selecionado.getValorContrapartida() == null) {
            selecionado.setValorContrapartida(BigDecimal.ZERO);
        }
        if (selecionado.getValorConvenio() == null) {
            selecionado.setValorConvenio(BigDecimal.ZERO);
        }
    }

    public Integer recuperaPrestacaoContas() {
        if (this.prestacaoContas == null) {
            listaPrestacaoContas = new ArrayList<>();
        }
        return listaPrestacaoContas.size();
    }

    public Integer recuperaUnidades() {
        if (this.listaUnidades == null) {
            listaUnidades = new ArrayList<>();
        }
        return listaUnidades.size();
    }

    public Boolean getVerificaEdicao() {
        if (selecionado == null) {
            return false;
        }
        if (((ConvenioReceita) selecionado).getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    private void validaSalvar() {
        ValidacaoException ve = new ValidacaoException();

        verificarAoSalvarDataVigencia();

        if (selecionado.getConvenioRecUnidMedida().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Informe pelo menos uma Meta para o Convênio de Receita.");
        }
        if (selecionado.getDetentorArquivoComposicao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Arquivo anexo é obrigatório!");
        }
        if (selecionado.getEntidadeRepassadora().getPessoaJuridica().equals(selecionado.getPessoa())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A Entidade Concedente e a Unidade Executora devem ser diferentes.");
        }
        if (pertenceEmenda && selecionado.getEmendaGoverno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Emenda deve ser informado.");
        }
        ve.lancarException();
    }

    public Boolean validaValores() {
        Boolean toReturn = true;
        if (selecionado.getValorConvenio() != null) {
            if (selecionado.getValorConvenio().compareTo(BigDecimal.ZERO) <= 0) {
                toReturn = false;
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Valor do Convênio deve ser maior que zero (0).");
            }
        }
        return toReturn;
    }

    public void alimentaListaSubcontas() {
        if (listaSubConta == null) {
            listaSubConta = new ArrayList<>();
        }
        for (ConvenioReceitaSubConta sub : selecionado.getConvenioReceitaSubContas()) {
            listaSubConta.add(sub.getSubConta().getId());
        }
    }

    @Override
    public void salvar() {
        ConvenioReceita cr = ((ConvenioReceita) selecionado);
        if (Util.validaCampos(cr) && validaValores()) {
            if (selecionado.getId() == null) {
                try {
                    validaSalvar();
                    List<ConvenioReceitaUnidade> listaCru = new ArrayList<>();
                    for (HierarquiaOrganizacional ho : listaUnidades) {
                        UnidadeOrganizacional uo = new UnidadeOrganizacional();
                        uo = convenioReceitaFacade.getUnidadeOrganizacionalFacade().recarregar(ho.getSubordinada());
                        ConvenioReceitaUnidade cru = new ConvenioReceitaUnidade();
                        cru.setConvenioReceita(cr);
                        cru.setExercicio(sistemaControlador.getExercicioCorrente());
                        cru.setUnidadeOrganizacional(uo);
                        listaCru.add(cru);
                    }
                    cr.setConvenioReceitaUnidades(listaCru);
                    cr.setNumero(getNumeroMaiorConvenioReceita().toString());
                    convenioReceitaFacade.salvarNovo(cr);
                    convenioReceitaFacade.salvarNotificaoConvenioReceita(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada ", " Registro " + selecionado + "salvo com sucesso!"));
                    redireciona();
                } catch (ValidacaoException ve) {
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                } catch (ExcecaoNegocioGenerica ex) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
                    redireciona();
                }
            } else {
                try {
                    validaSalvar();
                    List<ConvenioReceitaUnidade> listaCru = new ArrayList<>();
                    for (HierarquiaOrganizacional ho : listaUnidades) {
                        UnidadeOrganizacional uo = new UnidadeOrganizacional();
                        uo = convenioReceitaFacade.getUnidadeOrganizacionalFacade().recarregar(ho.getSubordinada());
                        ConvenioReceitaUnidade cru = new ConvenioReceitaUnidade();
                        cru.setConvenioReceita(cr);
                        cru.setExercicio(sistemaControlador.getExercicioCorrente());
                        cru.setUnidadeOrganizacional(uo);
                        listaCru.add(cru);
                    }
                    cr.setConvenioReceitaUnidades(listaCru);
                    convenioReceitaFacade.salvar(cr);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada ", " Registro " + selecionado + " alterado com sucesso!"));
                    redireciona();
                } catch (ValidacaoException ve) {
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                } catch (ExcecaoNegocioGenerica ex) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
                    redireciona();
                }
            }
        }
    }

    public ConvenioReceitaFacade getFacade() {
        return convenioReceitaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return convenioReceitaFacade;
    }

    public Integer getNumeroMaiorConvenioReceita() {
        BigDecimal maior = new BigDecimal(convenioReceitaFacade.retornaUltimoNumeroConvenioReceita());
        maior = maior.add(BigDecimal.ONE);
        return maior.intValue();
    }

    public void removerInterveniente(ActionEvent evt) {
        ConvenioRecInterveniente cri = (ConvenioRecInterveniente) evt.getComponent().getAttributes().get("removeInter");
        ConvenioReceita cr = ((ConvenioReceita) selecionado);
        cr.getConvenioRecIntervenientes().remove(cri);
    }

    public void adicionaInterveniente() {
        ConvenioReceita cr = ((ConvenioReceita) selecionado);
        if (intervenienteSelecionado != null) {
            ConvenioRecInterveniente cri = new ConvenioRecInterveniente();
            cri.setConvenioReceita(cr);
            cri.setInterveniente(intervenienteSelecionado);
            cr.getConvenioRecIntervenientes().add(cri);
            intervenienteSelecionado = new Interveniente();
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Informe uma Unidade Executora para adicionar.");
        }
    }

    public void adicionaOcorrencia() {
        ConvenioReceita cr = ((ConvenioReceita) selecionado);
        if (tramiteConvenioRecSelecionado.getOcorrencia() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Campo obrigatório! ", " O campo Ocorrência é obrigatório para adicionar."));
        }
        if (tramiteConvenioRecSelecionado.getDataTramite() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Campo obrigatório! ", " O campo Data da Ocorrência é obrigatório para adicionar."));
        } else {
            tramiteConvenioRecSelecionado.setConvenioReceita(cr);
            cr.getTramitesConvenioRec().add(tramiteConvenioRecSelecionado);
            tramiteConvenioRecSelecionado = new TramiteConvenioRec();
        }
    }

    public void adicionaUnidadeMedida() {
        ConvenioReceita cr = ((ConvenioReceita) selecionado);
        if (convenioReceitaUnidMedida.getDescricao().trim() == null || convenioReceitaUnidMedida.getDescricao().trim().equals("")) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Descrição é obrigatório para adicionar.");
        } else if (convenioReceitaUnidMedida.getUnidadeMedida() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Unidade Medida é obrigatório para adicionar.");
        } else if (convenioReceitaUnidMedida.getQtdeMetaFisica() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Meta Física é obrigatório para adicionar.");
        } else if (convenioReceitaUnidMedida.getMetaFisica().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O valor da Meta Física deve ser maior que zero (0).");
        } else {
            convenioReceitaUnidMedida.setConvenioReceita(cr);
            cr.getConvenioRecUnidMedida().add(convenioReceitaUnidMedida);
            convenioReceitaUnidMedida = new ConvenioReceitaUnidMedida();
        }
    }

    public void adicionaStatusPrestacaoContas() {
        ConvenioReceita cr = ((ConvenioReceita) selecionado);
        if (prestacaoContas.getDataLancamento() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Data é obrigatório para adicionar.");
        } else if (prestacaoContas.getTipoPrestacaoContas() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Situação é obrigatório para adicionar.");
        } else {
            prestacaoContas.setConvenioReceita(cr);
            cr.getPrestacaoContas().add(prestacaoContas);
            prestacaoContas = new PrestacaoContas();
        }
    }

    public void adicionaLiberacoes() {
        if (convenioReceitaLiberacoes.getDataLiberacaoRecurso() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Data da Liberação de Recurso é obrigatório para adicionar.");
        }
        if (convenioReceitaLiberacoes.getValorLiberadoConcedente() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Valor Liberado Concedente é obrigatório para adicionar.");
        }
        if (convenioReceitaLiberacoes.getValorLiberadoContrapartida() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Valor Liberado Contrapartida é obrigatório para adicionar.");
        }
        if (convenioReceitaLiberacoes.getValorLiberadoConcedente().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor Liberado Concedente deve ser maior que zero (0).");
        }
        if (convenioReceitaLiberacoes.getValorLiberadoContrapartida().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor Liberado Contrapartida deve ser maior que zero (0).");
        } else {
            convenioReceitaLiberacoes.setConvenioReceita(selecionado);
            selecionado.getConvenioReceitaLiberacoes().add(convenioReceitaLiberacoes);
            convenioReceitaLiberacoes = new ConvenioReceitaLiberacao();
        }
    }

    public void navegarParaUnidadeMedida() {
        FacesUtil.redirecionamentoInterno("/unidademedida/novo/");
    }

    public void navegarParaConvenio() {
        FacesUtil.redirecionamentoInterno("/ocorrencia-convenio-despesa/novo/");
    }

    public void removeOcorrencia(ActionEvent evt) {
        TramiteConvenioRec tcr = (TramiteConvenioRec) evt.getComponent().getAttributes().get("removeOcorrencia");
        ((ConvenioReceita) selecionado).getTramitesConvenioRec().remove(tcr);
    }

    public void removeUnidadeMedida(ActionEvent evt) {
        ConvenioReceitaUnidMedida crum = (ConvenioReceitaUnidMedida) evt.getComponent().getAttributes().get("removeUnidadeMedida");
        ((ConvenioReceita) selecionado).getConvenioRecUnidMedida().remove(crum);
    }

    public void removeStatusPrestacaoContas(ActionEvent evt) {
        PrestacaoContas pc = (PrestacaoContas) evt.getComponent().getAttributes().get("removeStatusPrestacao");
        ((ConvenioReceita) selecionado).getPrestacaoContas().remove(pc);
    }

    public void removerLiberacoes(ActionEvent evt) {
        ConvenioReceitaLiberacao crl = (ConvenioReceitaLiberacao) evt.getComponent().getAttributes().get("removeLiberacao");
        ((ConvenioReceita) selecionado).getConvenioReceitaLiberacoes().remove(crl);
    }

    public List<SelectItem> getPeriodicidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Periodicidade object : convenioReceitaFacade.getPeriodicidadeFacade().lista()) {
            if (object.getDescricao() != null && object.getDias() != null) {
                toReturn.add(new SelectItem(object, object.getDescricao() + " (" + object.getDias() + " dias)"));
            } else {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public ConverterGenerico getConverterPeriodicidade() {
        if (converterPeriodicidade == null) {
            converterPeriodicidade = new ConverterGenerico(Periodicidade.class, convenioReceitaFacade.getPeriodicidadeFacade());
        }
        return converterPeriodicidade;
    }

    public void removerAndamento(ActionEvent evt) {
        AndamentoConvenio ac = (AndamentoConvenio) evt.getComponent().getAttributes().get("removeAnda");
        andamentosRemovidos.add(ac);
        listaAndamentos.remove(ac);
    }

    public void adicionaAndamento() {
        //validar campos de andamento
        if (andamentoConvenio.getDataAndamento() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " Por favor informe o campo Data");
        } else if (andamentoConvenio.getInterveniente() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório", " Por favor informe o campo Interveniente "));
        } else if (andamentoConvenio.getTipoInterveniente() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " Informe o campo Tipo de Interveniência ");
        } else {
            listaAndamentos.add(andamentoConvenio);
            andamentoConvenio = new AndamentoConvenio();
        }
    }

    public void removerAditivos(ActionEvent evt) {
        AditivosConvenioReceita acr = (AditivosConvenioReceita) evt.getComponent().getAttributes().get("removeAditi");
        ((ConvenioReceita) selecionado).getAditivosConvenioReceitas().remove(acr);
        calculaValorFinalConvenioPorAditivo(acr);
    }

    private void calculaValorFinalConvenioPorAditivo(AditivosConvenioReceita acr) {
        selecionado.setValorConvenioAditivado(selecionado.getValorConvenioAditivado().subtract(acr.getAditivos().getValorAditivo()));
        selecionado.setValorContrapartidaAditivada(selecionado.getValorContrapartidaAditivada().subtract(acr.getAditivos().getValorContrapartida()));
        selecionado.setValorRepasseAditivado(selecionado.getValorConvenioAditivado().subtract(selecionado.getValorContrapartidaAditivada()));
    }

    public void adicionaAditivos() {
        if (validaAditivo()) {
            AditivosConvenioReceita acr = new AditivosConvenioReceita();
            acr.setConvenioReceita((ConvenioReceita) selecionado);
            acr.setAditivos(aditivos);
            ((ConvenioReceita) selecionado).getAditivosConvenioReceitas().add(acr);
            atribuirValorFinalAoConvenio(acr);
            aditivos = new Aditivos();
        }
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
        for (AditivosConvenioReceita adr : selecionado.getAditivosConvenioReceitas()) {
            if (adr.getAditivos().getNumero().equals(aditivos.getNumero())) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe um Aditivo adicionado com o número: " + aditivos.getNumero());
                toReturn = false;
            }
        }
        return toReturn;
    }

    public void setaValorConvenio() {
        setaValorRepasse();
    }

    public void setaValorContrapartida() {
        setaValorRepasse();
    }

    public void setaValorRepasse() {
        selecionado.setValorRepasse(selecionado.getValorConvenio().subtract(selecionado.getValorContrapartida()));
    }

    public void calcularValorRepasse() {
        selecionado.setValorRepasse(selecionado.getValorConvenio().subtract(selecionado.getValorContrapartida()));
    }

    public void calcularValorRepasseAditivado() {
        selecionado.setValorRepasseAditivado(selecionado.getValorConvenioAditivado().subtract(selecionado.getValorContrapartidaAditivada()));
    }


    public List<LancamentoReceitaOrc> getListaReceitasRealizadas() {
        if (listaSubConta != null) {
            alimentaListaSubcontas();
        }
        try {
            return convenioReceitaFacade.listaReceitasRealizadasPorConvenioReceita(selecionado, listaSubConta);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ReceitaORCEstorno> getListaEstornoReceitasRealizadas() {
        if (listaSubConta != null) {
            alimentaListaSubcontas();
        }
        try {
            return convenioReceitaFacade.listaEstornoReceitasRealizadasPorConvenioReceita(selecionado, listaSubConta);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Liquidacao> getListaLiquidacao() {
        if (verificaSelecionado()) {
            return convenioReceitaFacade.listaLiquidacaoPorConvenio(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<LiquidacaoEstorno> getListaLiquidacaoEstorno() {
        if (verificaSelecionado()) {
            return convenioReceitaFacade.listaLiquidacaoEstornoPorConvenio(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pagamento> getListaPagamento() {
        if (verificaSelecionado()) {
            return convenioReceitaFacade.listaPagamentoPorConvenio(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<PagamentoEstorno> getListaPagamentoEstorno() {
        if (verificaSelecionado()) {
            return convenioReceitaFacade.listaPagamentoEstornoPorConvenio(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Empenho> getListaEmpenhos() {
        if (verificaSelecionado()) {
            return convenioReceitaFacade.listaEmpenhosPorConvenioReceita(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<EmpenhoEstorno> getListaEmpenhoEstornos() {
        if (verificaSelecionado()) {
            return convenioReceitaFacade.listaEmpenhoEstornosPorConvenioReceita(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<HierarquiaOrganizacional> getListaUnidadeOrganizacionais() {
        if (verificaSelecionado()) {
            return convenioReceitaFacade.listaUnidadesOrganizacionais(selecionado);
        } else {
            return new ArrayList<>();
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

    public void editarAditivo(AditivosConvenioReceita acr) {
        aditivos = acr.getAditivos();
        selecionado.getAditivosConvenioReceitas().remove(acr);
        calculaValorFinalConvenioPorAditivo(acr);
    }

    public void editarMetas(ConvenioReceitaUnidMedida convenioReceitaUnid) {
        convenioReceitaUnidMedida = convenioReceitaUnid;
        selecionado.getConvenioRecUnidMedida().remove(convenioReceitaUnid);
    }

    public BigDecimal atribuirValorFinalAoConvenio(AditivosConvenioReceita aditivosConvenioReceita) {
        BigDecimal total = BigDecimal.ZERO;
        selecionado.setValorConvenioAditivado(selecionado.getValorConvenioAditivado().add(aditivosConvenioReceita.getAditivos().getValorAditivo()));
        selecionado.setValorContrapartidaAditivada(selecionado.getValorContrapartidaAditivada().add(aditivosConvenioReceita.getAditivos().getValorContrapartida()));
        selecionado.setValorRepasseAditivado(selecionado.getValorConvenioAditivado().subtract(selecionado.getValorContrapartidaAditivada()));
        selecionado.setVigenciaFinal(aditivosConvenioReceita.getAditivos().getDataFimVigencia());
        return total;
    }

    public BigDecimal calcularTotalAditivo() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (AditivosConvenioReceita aditivo : selecionado.getAditivosConvenioReceitas()) {
            total = total.add(aditivo.getAditivos().getValorAditivo());
        }
        return total;
    }

    public BigDecimal calcularTotalAditivoContrapartida() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (AditivosConvenioReceita aditivo : selecionado.getAditivosConvenioReceitas()) {
            total = total.add(aditivo.getAditivos().getValorContrapartida());
        }
        return total;
    }

    public BigDecimal calcularTotalRecursoLiberadoContrapartida() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ConvenioReceitaLiberacao convenioReceitaLiberacao : selecionado.getConvenioReceitaLiberacoes()) {
            total = total.add(convenioReceitaLiberacao.getValorLiberadoContrapartida());
        }
        return total;
    }

    public BigDecimal calcularTotalMetas() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ConvenioReceitaUnidMedida receitaUnidMedida : selecionado.getConvenioRecUnidMedida()) {
            total = total.add(receitaUnidMedida.getMetaFisica());
        }
        return total;
    }

    public BigDecimal calcularTotalRecursoLiberado() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ConvenioReceitaLiberacao convenioReceitaLiberacao : selecionado.getConvenioReceitaLiberacoes()) {
            total = total.add(convenioReceitaLiberacao.getValorLiberadoConcedente());
        }
        return total;
    }

    public BigDecimal getCalculaTotalEmpenho() {
        BigDecimal empenho = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            for (Empenho emp : convenioReceitaFacade.listaEmpenhosPorConvenioReceita(selecionado)) {
                empenho = empenho.add(emp.getValor());
            }
        }
        return empenho;
    }

    public BigDecimal getCalculaTotalEmpenhoEstorno() {
        BigDecimal estorno = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            for (EmpenhoEstorno est : convenioReceitaFacade.listaEmpenhoEstornosPorConvenioReceita(selecionado)) {
                estorno = estorno.add(est.getValor());
            }
        }
        return estorno;
    }

    public BigDecimal getCalculaTotalReceitaRealizada() {
        BigDecimal recRealizada = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            if (listaSubConta != null) {
                alimentaListaSubcontas();
            }
            for (LancamentoReceitaOrc lrorc : convenioReceitaFacade.listaReceitasRealizadasPorConvenioReceita(selecionado, listaSubConta)) {
                recRealizada = recRealizada.add(lrorc.getValor());
            }
        }
        return recRealizada;
    }

    public BigDecimal getCalculaTotalEstornoReceitaRealizada() {
        BigDecimal estRealizado = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            if (listaSubConta != null) {
                alimentaListaSubcontas();
            }
            for (ReceitaORCEstorno roe : convenioReceitaFacade.listaEstornoReceitasRealizadasPorConvenioReceita(selecionado, listaSubConta)) {
                estRealizado = estRealizado.add(roe.getValor());
            }
        }
        return estRealizado;
    }

    public BigDecimal getCalculaSaldoFinal() {
        BigDecimal saldoFinal = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            BigDecimal soma = getCalculaTotalReceitaRealizada().add(getCalculaTotalEmpenhoEstorno());
            BigDecimal subtrai = getCalculaTotalEstornoReceitaRealizada().add(getCalculaTotalEmpenho());

            saldoFinal = soma.subtract(subtrai);
        }
        return saldoFinal;
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

    public void removeConta(ActionEvent evt) {
        ConvenioRecConta ac = (ConvenioRecConta) evt.getComponent().getAttributes().get("con");
        ((ConvenioReceita) selecionado).getConvenioRecConta().remove(ac);
    }

    public void adicionaConta() {
        Boolean podeAdd = true;
        if (conta == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Fonte de Recursos deve ser informado para adicionar.");
            podeAdd = false;
        }

        if (conta != null) {
            if (((ConvenioReceita) selecionado).getConvenioRecConta() != null) {
                for (ConvenioRecConta crc : ((ConvenioReceita) selecionado).getConvenioRecConta()) {
                    if (crc.getConta().equals(conta)) {
                        podeAdd = false;
                        conta = new Conta();
                        break;
                    }
                }
                if (podeAdd) {
                    ConvenioRecConta crc = new ConvenioRecConta();
                    crc.setConvenioReceita((ConvenioReceita) selecionado);
                    crc.setConta(conta);
                    ((ConvenioReceita) selecionado).getConvenioRecConta().add(crc);
                    conta = new Conta();
                }
            } else {
                ConvenioRecConta crc = new ConvenioRecConta();
                crc.setConvenioReceita((ConvenioReceita) selecionado);
                crc.setConta(conta);
                ((ConvenioReceita) selecionado).getConvenioRecConta().add(crc);
                conta = new Conta();
            }
        }
    }

    public void validaCategoriaConta(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Conta c = (Conta) value;
        c = convenioReceitaFacade.getContaFacade().recuperar(c);
        if (c.getCategoria().equals(CategoriaConta.SINTETICA)) {
            message.setDetail("Conta Sintética não pode ser utilizada.");
            message.setSummary("Operação não Permitida!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void validarDatas(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage message = new FacesMessage();
        Date dataVigenciaFinal = (Date) value;
        Date dataVigenciaInicial = selecionado.getVigenciaInicial();

        if (dataVigenciaInicial != null && dataVigenciaFinal != null) {
            if (dataVigenciaFinal.before(dataVigenciaInicial)) {
                selecionado.setVigenciaInicial(null);
                selecionado.setVigenciaFinal(null);
                message.setSummary("Data inválida! ");
                message.setDetail(" Data de Fim de Vigência deve ser posterior ou igual à Data de Início de Vigência. ");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new javax.faces.validator.ValidatorException(message);
            }
        }
    }

    private void verificarAoSalvarDataVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getVigenciaFinal() != null && selecionado.getVigenciaInicial() !=null) {
            if (selecionado.getVigenciaFinal().before(selecionado.getVigenciaInicial())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Data de Fim de Vigência deve ser posterior ou igual à Data de Início de Vigência.");
            }
        }
        ve.lancarException();
    }

    public List<EntidadeRepassadora> completaEntidadeRepassadora(String parte) {
        return convenioReceitaFacade.getEntidadeRepassadoraFacade().completaEntidadeRepassadora(parte);
    }

    public List<Pessoa> completaPessoa(String parte) {
        return convenioReceitaFacade.getPessoaFacade().listaPessoasJuridicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        if (selecionado.getPessoa() != null) {
            return convenioReceitaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte.trim(), ((ConvenioReceita) selecionado).getPessoa(), TipoClasseCredor.CONVENIIO_RECEITA);
        }
        return new ArrayList<>();
    }

    public ConverterAutoComplete getConverterEntidadeRepassadora() {
        if (converterEntidadeRepassadora == null) {
            converterEntidadeRepassadora = new ConverterAutoComplete(EntidadeRepassadora.class, convenioReceitaFacade.getEntidadeRepassadoraFacade());
        }
        return converterEntidadeRepassadora;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, convenioReceitaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, convenioReceitaFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public List<SelectItem> getUnidadeMedida() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UnidadeMedida object : convenioReceitaFacade.getUnidadeMedidaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<UnidadeMedida> completaUnidadeMedida(String parte) {
        return convenioReceitaFacade.getUnidadeMedidaFacade().listaFiltrando(parte.trim(), "descricao", "sigla");
    }

    public ConverterAutoComplete getConverterUnidadeMedida() {
        if (converterUnidadeMedida == null) {
            converterUnidadeMedida = new ConverterAutoComplete(UnidadeMedida.class, convenioReceitaFacade.getUnidadeMedidaFacade());
        }
        return converterUnidadeMedida;
    }

    public List<UnidadeOrganizacional> completaUnidadeOrganizacional(String parte) {
        return convenioReceitaFacade.getUnidadeOrganizacionalFacade().retornaUnidadesOrcamentarias(parte.trim());
    }

    public List<Entidade> completaUnidadeConvenente(String parte) {
        return convenioReceitaFacade.getEntidadeFacade().listaEntidadesAtivas(parte.trim());
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(Entidade.class, convenioReceitaFacade.getEntidadeFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public List<Conta> completaConta(String parte) {
        ConvenioReceita cr = ((ConvenioReceita) selecionado);
        List<Long> listaSubConta = new ArrayList<>();
        for (ConvenioReceitaSubConta crsc : cr.getConvenioReceitaSubContas()) {
            listaSubConta.add(crsc.getSubConta().getId());
        }
        return convenioReceitaFacade.getContaFacade().listaFiltrandoDestinacaoRecursosSubConta(parte.trim(), sistemaControlador.getExercicioCorrente(), listaSubConta);
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, convenioReceitaFacade.getContaFacade());
        }
        return converterConta;
    }

    public List<SubConta> completaSubConta(String parte) {
//        String listaUni = "";
//        String virgula = ",";
        List<Long> listaUni = new ArrayList<>();
        for (HierarquiaOrganizacional ho : listaUnidades) {
//            listaUni += ho.getSubordinada().getId() + virgula;
            listaUni.add(ho.getSubordinada().getId());
        }
//        listaUni = listaUni.substring(0, listaUni.length() - 1);
        return convenioReceitaFacade.getSubContaFacade().listaFiltrandoSubContaPorTipoUnidades(parte.trim(), sistemaControlador.getExercicioCorrente(), listaUni, TipoRecursoSubConta.CONVENIO_CONGENERE);
    }

    public ConverterAutoComplete getConverterSubConta() {
        if (converterSubConta == null) {
            converterSubConta = new ConverterAutoComplete(SubConta.class, convenioReceitaFacade.getSubContaFacade());
        }
        return converterSubConta;
    }

    public List<Interveniente> completaInterveniente(String parte) {
        return convenioReceitaFacade.getIntervenienteFacade().listaPorPessoa(parte.trim());
    }

    public List<Interveniente> completaIntervenienteSimples(String parte) {
        List<Interveniente> interLista = new ArrayList<>();
        List<Interveniente> retorno = new ArrayList<>();
        ConvenioReceita cr = ((ConvenioReceita) selecionado);
        for (ConvenioRecInterveniente cri : cr.getConvenioRecIntervenientes()) {
            interLista.add(convenioReceitaFacade.getIntervenienteFacade().recuperar(cri.getInterveniente().getId()));
        }
        for (Interveniente i : interLista) {
            if (i.getPessoa().getNome().startsWith(parte.trim())) {
                retorno.add(i);
            }
        }
        return retorno;
    }

    public ConverterAutoComplete getConverterInterveniente() {
        if (converterInterveniente == null) {
            converterInterveniente = new ConverterAutoComplete(Interveniente.class, convenioReceitaFacade.getIntervenienteFacade());
        }
        return converterInterveniente;
    }

    public List<TipoInterveniente> completaTipoInterveniente(String parte) {
        return convenioReceitaFacade.getTipoIntervenienteFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterTipoInterveniente() {
        if (converterTipoInterveniente == null) {
            converterTipoInterveniente = new ConverterAutoComplete(TipoInterveniente.class, convenioReceitaFacade.getTipoIntervenienteFacade());
        }
        return converterTipoInterveniente;
    }

    public List<OcorrenciaConvenioDesp> completaOcorrencia(String parte) {
        return convenioReceitaFacade.getOcorrenciaConvenioDespFacade().listaFiltrando(parte.trim(), "ocorrencia");
    }

    public ConverterAutoComplete getConverterOcorrencia() {
        if (converterOcorrencia == null) {
            converterOcorrencia = new ConverterAutoComplete(OcorrenciaConvenioDesp.class, convenioReceitaFacade.getOcorrenciaConvenioDespFacade());
        }
        return converterOcorrencia;
    }

    public List<SelectItem> getOrigemRecurso() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(OrigemRecurso.ORCAMENTARIO, OrigemRecurso.ORCAMENTARIO.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getTipoDePrestacaoContas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoPrestacaoContas object : TipoPrestacaoContas.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getDestinacaoRecurso() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (DestinacaoRecurso object : DestinacaoRecurso.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoConvenio() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoConvenioReceita object : TipoConvenioReceita.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public Interveniente getIntervenienteSelecionado() {
        return intervenienteSelecionado;
    }

    public void setIntervenienteSelecionado(Interveniente intervenienteSelecionado) {
        this.intervenienteSelecionado = intervenienteSelecionado;
    }

    public AndamentoConvenio getAndamentoConvenio() {
        return andamentoConvenio;
    }

    public void setAndamentoConvenio(AndamentoConvenio andamentoConvenio) {
        this.andamentoConvenio = andamentoConvenio;
    }

    public List<AndamentoConvenio> getListaAndamentos() {
        return listaAndamentos;
    }

    public void setListaAndamentos(List<AndamentoConvenio> listaAndamentos) {
        this.listaAndamentos = listaAndamentos;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TramiteConvenioRec getTramiteConvenioRecSelecionado() {
        return tramiteConvenioRecSelecionado;
    }

    public void setTramiteConvenioRecSelecionado(TramiteConvenioRec tramiteConvenioRecSelecionado) {
        this.tramiteConvenioRecSelecionado = tramiteConvenioRecSelecionado;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public Aditivos getAditivos() {
        return aditivos;
    }

    public void setAditivos(Aditivos aditivos) {
        this.aditivos = aditivos;
    }

    public List<Aditivos> getListaAditivos() {
        return listaAditivos;
    }

    public void setListaAditivos(List<Aditivos> listaAditivos) {
        this.listaAditivos = listaAditivos;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public List<Conta> getListaConta() {
        return listaConta;
    }

    public void setListaConta(List<Conta> listaConta) {
        this.listaConta = listaConta;
    }

    public void addUnidadeOrganizacional() {
        if (hierarquiaOrganizacional != null) {
            ConvenioReceitaUnidade cru = new ConvenioReceitaUnidade();
            cru.setConvenioReceita((ConvenioReceita) selecionado);
            cru.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            cru.setExercicio(sistemaControlador.getExercicioCorrente());
            ((ConvenioReceita) selecionado).getConvenioReceitaUnidades().add(cru);

            hierarquiaOrganizacional = new HierarquiaOrganizacional();
        }
    }

    public void removeUnidade(ConvenioReceitaUnidade unidade) {
        ((ConvenioReceita) selecionado).getConvenioReceitaUnidades().remove(unidade);
    }

    public void addSubConta() {
        Boolean podeAdd = true;
        if (subConta == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Conta Financeira deve ser informado para adicionar.");
            podeAdd = false;
        }
        if (subConta != null) {
            if (((ConvenioReceita) selecionado).getConvenioReceitaSubContas() != null) {
                for (ConvenioReceitaSubConta crsc : ((ConvenioReceita) selecionado).getConvenioReceitaSubContas()) {
                    if (crsc.getSubConta().equals(subConta)) {
                        podeAdd = false;
                        subConta = new SubConta();
                        break;
                    }
                }
                if (podeAdd) {
                    if (!convenioReceitaFacade.verificaSubContaExistente(subConta)) {
                        ConvenioReceitaSubConta crsc = new ConvenioReceitaSubConta();
                        crsc.setConvenioReceita((ConvenioReceita) selecionado);
                        crsc.setSubConta(subConta);
                        crsc.setExercicio(sistemaControlador.getExercicioCorrente());
                        ((ConvenioReceita) selecionado).getConvenioReceitaSubContas().add(crsc);
                        subConta = new SubConta();
                    } else {
                        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Conta Financeira selecionada pertence ao Convênio: " + convenioReceitaFacade.recuperaConvenioPorSubConta(subConta));
                    }
                }
            } else {
                if (!convenioReceitaFacade.verificaSubContaExistente(subConta)) {
                    ConvenioReceitaSubConta crsc = new ConvenioReceitaSubConta();
                    crsc.setConvenioReceita((ConvenioReceita) selecionado);
                    crsc.setSubConta(subConta);
                    crsc.setExercicio(sistemaControlador.getExercicioCorrente());
                    ((ConvenioReceita) selecionado).getConvenioReceitaSubContas().add(crsc);
                    subConta = new SubConta();
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Conta Financeira selecionada pertence ao Convênio: " + convenioReceitaFacade.recuperaConvenioPorSubConta(subConta));
                }
            }
        }
        subConta = null;
    }

    public void removeSubConta(ConvenioReceitaSubConta conta) {
        ((ConvenioReceita) selecionado).getConvenioReceitaSubContas().remove(conta);
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/convenio-receita/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ConvenioReceitaUnidMedida getConvenioReceitaUnidMedida() {
        return convenioReceitaUnidMedida;
    }

    public void setConvenioReceitaUnidMedida(ConvenioReceitaUnidMedida convenioReceitaUnidMedida) {
        this.convenioReceitaUnidMedida = convenioReceitaUnidMedida;
    }

    public PrestacaoContas getPrestacaoContas() {
        return prestacaoContas;
    }

    public void setPrestacaoContas(PrestacaoContas prestacaoContas) {
        this.prestacaoContas = prestacaoContas;
    }

    public ConvenioReceitaLiberacao getConvenioReceitaLiberacoes() {
        return convenioReceitaLiberacoes;
    }

    public void setConvenioReceitaLiberacoes(ConvenioReceitaLiberacao convenioReceitaLiberacoes) {
        this.convenioReceitaLiberacoes = convenioReceitaLiberacoes;
    }

    public List<SelectItem> getSituacoesCadastrais() {
        List<SelectItem> retorno = new ArrayList<>();
        for (SituacaoCadastralContabil situacaoCadastralContabil : SituacaoCadastralContabil.values()) {
            retorno.add(new SelectItem(situacaoCadastralContabil, situacaoCadastralContabil.getDescricao()));
        }
        return retorno;
    }

    public List<ConvenioReceita> completarConvenioReceita(String parte) {
        return convenioReceitaFacade.buscarFiltrandoConvenioReceita(parte.trim());
    }

    public void limparOrigem() {
        selecionado.setEmendaGoverno(null);
        selecionado.setProgramaGoverno(null);
    }

    public Boolean getPertenceEmenda() {
        return pertenceEmenda;
    }

    public void setPertenceEmenda(Boolean pertenceEmenda) {
        this.pertenceEmenda = pertenceEmenda;
    }
}
