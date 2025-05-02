/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.pccr.MesesProgressao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author everton
 */
@ManagedBean(name = "provimentoFPProgressaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProvimentoProgressao", pattern = "/progressao-salarial/novo/", viewId = "/faces/rh/administracaodepagamento/provimentoprogressao/edita.xhtml"),
    @URLMapping(id = "editarProvimentoProgressao", pattern = "/progressao-salarial/editar/#{provimentoFPProgressaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/provimentoprogressao/edita.xhtml"),
    @URLMapping(id = "listarProvimentoProgressao", pattern = "/progressao-salarial/listar/", viewId = "/faces/rh/administracaodepagamento/provimentoprogressao/lista.xhtml"),
    @URLMapping(id = "verProvimentoProgressao", pattern = "/progressao-salarial/ver/#{provimentoFPProgressaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/provimentoprogressao/visualizar.xhtml")
})
public class ProvimentoFPProgressaoControlador extends PrettyControlador<EnquadramentoFuncional> implements Serializable, CRUD {

    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    private ConverterAutoComplete converterAtoLegal;
    private ContratoFP contratoFP;
    private ConverterAutoComplete converterContratoFP;
    private List<EnquadramentoFuncional> listaEnquadramentoFuncional;
    private EnquadramentoFuncional enquadramentoFuncional;
    private EnquadramentoFuncional enquadramentoFuncionalVigente;
    private PlanoCargosSalarios planoCargosSalarios;
    private CategoriaPCS categoriaPCS;
    private ProgressaoPCS progressaoPCS;
    private ProgressaoPCS novaProgressaoPCS;
    private AtoLegal atoLegal;
    private String observacao;
    private EnquadramentoPCS enquadramentoPCS;
    private EnquadramentoPCS novoEnquadramentoPCS;
    private List<ProgressaoPCS> progressoesVigentes;
    private ConverterGenerico converterProgressaoPCS;
    private Integer meses;
    private Boolean existeEnquadramentoFuncionalVigente;
    private Date inicioEnquadramento;

    public ProvimentoFPProgressaoControlador() {
        super(EnquadramentoFuncional.class);
    }

    public Integer getMeses() {
        return meses;
    }

    public void setMeses(Integer meses) {
        this.meses = meses;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.buscarContratoPorTipoRegime(parte.trim(), TipoRegime.ESTATUTARIO);
    }

    public Date getInicioEnquadramento() {
        return inicioEnquadramento;
    }

    public void setInicioEnquadramento(Date inicioEnquadramento) {
        this.inicioEnquadramento = inicioEnquadramento;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public EnquadramentoPCS getEnquadramentoPCS() {
        return enquadramentoPCS;
    }

    public void setEnquadramentoPCS(EnquadramentoPCS enquadramentoPCS) {
        this.enquadramentoPCS = enquadramentoPCS;
    }

    public List<ProgressaoPCS> getProgressoesVigentes() {
        return progressoesVigentes;
    }

    public ProgressaoPCS getNovaProgressaoPCS() {
        return novaProgressaoPCS;
    }

    public void setNovaProgressaoPCS(ProgressaoPCS novaProgressaoPCS) {
        this.novaProgressaoPCS = novaProgressaoPCS;
    }

    public EnquadramentoPCS getNovoEnquadramentoPCS() {
        return novoEnquadramentoPCS;
    }

    public List<EnquadramentoFuncional> getListaEnquadramentoFuncional() {
        return listaEnquadramentoFuncional;
    }

    public EnquadramentoFuncional getEnquadramentoFuncional() {
        return enquadramentoFuncional;
    }

    public void setEnquadramentoFuncional(EnquadramentoFuncional enquadramentoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public PlanoCargosSalarios getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegalPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL);
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void selecionaServidor(SelectEvent item) {
        categoriaPCS = null;
        planoCargosSalarios = null;
        progressaoPCS = null;
        novaProgressaoPCS = null;
        enquadramentoPCS = new EnquadramentoPCS();
        progressoesVigentes = new ArrayList<>();
        if (item != null) {
            contratoFP = (ContratoFP) item.getObject();
        }
        listaEnquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoContratoFP(contratoFP);
        enquadramentoFuncionalVigente = enquadramentoFuncionalFacade.recuperaEnquadramentoVigente(contratoFP);
        if (enquadramentoFuncionalVigente == null) {
            FacesUtil.addMessageWarn("Formulario:msgs", "Atenção!", "O servidor não possui enquadramento funcional vigente!");
        } else {
            planoCargosSalarios = enquadramentoFuncionalVigente.getCategoriaPCS().getPlanoCargosSalarios();
            categoriaPCS = enquadramentoFuncionalVigente.getCategoriaPCS();

            List<ProgressaoPCS> listProgPCS = progressaoPCSFacade.listaUltimoNivelProgressaoPCS(planoCargosSalarios);
            if (listProgPCS.contains(enquadramentoFuncionalVigente.getProgressaoPCS())) {
                Integer pos = listProgPCS.indexOf(enquadramentoFuncionalVigente.getProgressaoPCS()) + 1;
                if (pos < listProgPCS.size()) {
                    progressaoPCS = listProgPCS.get(pos);
                }
            }

            progressoesVigentes = progressaoPCSFacade.getFilhosDe(enquadramentoFuncionalVigente.getProgressaoPCS().getSuperior(), planoCargosSalarios);

            List<ProgressaoPCS> progressoesDisponiveis = new ArrayList<>();
            for (ProgressaoPCS p : progressoesVigentes) {
                if (this.progressaoPCS.getDescricao().compareTo(p.getDescricao()) < 0) {
                    progressoesDisponiveis.add(p);
                }
            }
            progressoesVigentes.clear();
            progressoesVigentes = progressoesDisponiveis;
            if (!progressoesVigentes.isEmpty()) {
                novaProgressaoPCS = progressoesVigentes.get(0);
                novoEnquadramentoPCS = buscarValoresParaEnquadramentoPCS(categoriaPCS, novaProgressaoPCS, provimentoFPFacade.getSistemaFacade().getDataOperacao());
            } else {
                FacesUtil.addMessageWarn("Atenção!", "O servidor selecionado atingiu o limite máximo de progressões disponíveis.");
            }

            progressaoPCS = enquadramentoFuncionalVigente.getProgressaoPCS();

            if (progressaoPCS == null) {
                FacesUtil.addMessageWarn("Formulario:msgs", "Atenção!", "Não encontrada progressão maior que a vigente no plano de cargos e salários deste servidor!");
            }
            if (categoriaPCS != null && progressaoPCS != null) {
                enquadramentoPCS = enquadramentoPCSFacade.recuperaValor(categoriaPCS, progressaoPCS, enquadramentoFuncionalVigente.getInicioVigencia());
            }
        }
        existeEnquadramentoFuncionalVigente = (enquadramentoFuncionalVigente != null);
        if (existeEnquadramentoFuncionalVigente && !bloquearNovaProgressaoDevidoPrazoMininoDeTresAnoAPartirDoInicioDaUltimaProgressao()) {
            FacesUtil.atualizarComponente("form-enquadramento-vigente");
            FacesUtil.executaJavaScript("enquadramentoVigente.show()");
        }

    }

    public EnquadramentoFuncional getEnquadramentoFuncionalVigente() {
        return enquadramentoFuncionalVigente;
    }

    public void setEnquadramentoFuncionalVigente(EnquadramentoFuncional enquadramentoFuncionalVigente) {
        this.enquadramentoFuncionalVigente = enquadramentoFuncionalVigente;
    }

    public void confirmarFinalVigenciaDoEnquadramentoFuncionalVigente() {
        if (encerrarEnquadramentoFuncionalVigente()) {
            FacesUtil.executaJavaScript("enquadramentoVigente.hide()");
        }
    }

    public Boolean encerrarEnquadramentoFuncionalVigente() {
        if (podeEncerrarEnquadramentoFuncionalVigente()) {
            existeEnquadramentoFuncionalVigente = false;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private boolean podeEncerrarEnquadramentoFuncionalVigente() {
        if (enquadramentoFuncionalVigente != null) {
            if (enquadramentoFuncionalVigente.getFinalVigencia() == null) {
                FacesUtil.addCampoObrigatorio("Informe a data final de vigência para encerrar o enquadramento funcional vigente.");
                return false;
            }
            if (enquadramentoFuncionalVigente.getFinalVigencia() != null && enquadramentoFuncionalVigente.getFinalVigencia().before(enquadramentoFuncionalVigente.getInicioVigencia())) {
                FacesUtil.addOperacaoNaoPermitida("A data final de vigência deve ser posterior a data inicial da vigência.");
                return false;
            }
        }
        return true;
    }

    private void validarAfastamento() {
        ValidacaoException ve = new ValidacaoException();
        List<Afastamento> afastamentos = afastamentoFacade.buscarAfastamentoVigentePorContratoAndNaoPermitirProgressao(contratoFP, provimentoFPFacade.getSistemaFacade().getDataOperacao());
        if (!afastamentos.isEmpty()) {
            for (Afastamento afastamento : afastamentos) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor selecionado tem afastamento '" + afastamento.getTipoAfastamento().getDescricao() + " (Início: " + DataUtil.getDataFormatada(afastamento.getInicio())
                    + ", Término: " + DataUtil.getDataFormatada(afastamento.getTermino()) + ", " + (afastamento.getRetornoInformado() ? "Com Retorno" : "Sem Retorno") + ")' que não permite Progressão.");
            }
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return enquadramentoFuncionalFacade;
    }

    public void recuperarDependenciasDoServidor(ContratoFP item) {
        categoriaPCS = null;
        planoCargosSalarios = null;
        progressaoPCS = null;
        novaProgressaoPCS = null;
        enquadramentoPCS = new EnquadramentoPCS();
        progressoesVigentes = new ArrayList<>();
        if (item != null) {
            contratoFP = item;
        }
        listaEnquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoContratoFP(contratoFP);
        enquadramentoFuncionalVigente = enquadramentoFuncionalFacade.recuperaEnquadramentoVigente(contratoFP);
        if (enquadramentoFuncionalVigente == null) {
            FacesUtil.addAtencao("O servidor não possui enquadramento funcional vigente!");
        } else {
            planoCargosSalarios = enquadramentoFuncionalVigente.getCategoriaPCS().getPlanoCargosSalarios();
            categoriaPCS = enquadramentoFuncionalVigente.getCategoriaPCS();

            List<ProgressaoPCS> listProgPCS = progressaoPCSFacade.listaUltimoNivelProgressaoPCS(planoCargosSalarios);
            if (listProgPCS.contains(enquadramentoFuncionalVigente.getProgressaoPCS())) {
                Integer pos = listProgPCS.indexOf(enquadramentoFuncionalVigente.getProgressaoPCS()) + 1;
                progressaoPCS = pos < listProgPCS.size() ? listProgPCS.get(pos) : enquadramentoFuncionalVigente.getProgressaoPCS();
            }

            progressoesVigentes = progressaoPCSFacade.getFilhosDe(enquadramentoFuncionalVigente.getProgressaoPCS().getSuperior(), planoCargosSalarios);

            List<ProgressaoPCS> progressoesDisponiveis = new ArrayList<>();
            for (ProgressaoPCS p : progressoesVigentes) {
                if (this.progressaoPCS.getDescricao().compareTo(p.getDescricao()) < 0) {
                    progressoesDisponiveis.add(p);
                }
            }
            progressoesVigentes.clear();
            progressoesVigentes = progressoesDisponiveis;
            if (!progressoesVigentes.isEmpty()) {
                novaProgressaoPCS = progressoesVigentes.get(0);
                buscarValorNovaProgressao();
            } else {
                FacesUtil.addOperacaoNaoPermitida("O servidor selecionado atingiu o limite máximo de progressões disponíveis. Será necessário uma nova Promoção para realizar novas Progressões para esse Servidor!");
            }

            progressaoPCS = enquadramentoFuncionalVigente.getProgressaoPCS();

            if (progressaoPCS == null) {
                FacesUtil.addOperacaoNaoPermitida("Não encontrada progressão maior que a vigente no plano de cargos e salários deste servidor!");
            }
            if (categoriaPCS != null && progressaoPCS != null) {
                enquadramentoPCS = buscarValoresParaEnquadramentoPCS(categoriaPCS, progressaoPCS, provimentoFPFacade.getSistemaFacade().getDataOperacao());
            }
        }
    }

    public EnquadramentoPCS buscarValoresParaEnquadramentoPCS(CategoriaPCS categoria, ProgressaoPCS progressao, Date dataOperacao) {
        try {
            return enquadramentoPCSFacade.recuperaValor(categoria, progressao, dataOperacao);
        } catch (NoResultException no) {
            logger.debug("Nenhum EnquadramentoPCS para a CategoriaPCS " + categoria + " e ProgressãoPCS " + progressao + " em " + DataUtil.getDataFormatada(dataOperacao), no);
            FacesUtil.addAtencao("Nenhum EnquadramentoPCS para a CategoriaPCS " + categoria + " e ProgressãoPCS " + progressao + " em " + DataUtil.getDataFormatada(dataOperacao));
        }
        return null;
    }

    @Override
    @URLAction(mappingId = "novoProvimentoProgressao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        contratoFP = null;
        enquadramentoPCS = null;
        novoEnquadramentoPCS = null;
        listaEnquadramentoFuncional = new ArrayList<>();
        novoEnquadramentoFuncional();
        progressoesVigentes = new ArrayList<>();
        atoLegal = (AtoLegal) Web.pegaDaSessao(AtoLegal.class);
        if (atoLegal == null) {
            atoLegal = new AtoLegal();
        }
        operacao = Operacoes.NOVO;
    }

    private void novoEnquadramentoFuncional() {
        enquadramentoFuncional = new EnquadramentoFuncional();
        categoriaPCS = null;
        planoCargosSalarios = null;
        progressaoPCS = null;
        novaProgressaoPCS = null;
        enquadramentoFuncionalVigente = null;
        atoLegal = null;
        observacao = null;
    }

    @URLAction(mappingId = "verProvimentoProgressao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        enquadramentoFuncional = enquadramentoFuncionalFacade.recuperar(getId());
        enquadramentoFuncionalVigente = enquadramentoFuncionalFacade.recuperar(getId());
        enquadramentoPCS.getVencimentoBase();
        recuperarDependenciasDoServidor(enquadramentoFuncionalVigente.getContratoServidor());
    }

    @URLAction(mappingId = "editarProvimentoProgressao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        enquadramentoFuncional = enquadramentoFuncionalFacade.recuperar(getId());
        enquadramentoFuncionalVigente = enquadramentoFuncionalFacade.recuperar(getId());
        enquadramentoFuncional.setId(null);

        try {
            buscarMesesParaProgressao();
        } catch (ExcecaoNegocioGenerica eng) {
            String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/plano-cargos-salarios/editar/" + enquadramentoFuncional.getCategoriaPCS().getPlanoCargosSalarios().getId() + "' target='_blank'>Plano de Cargos e Salários</a></b>";
            FacesUtil.addOperacaoNaoPermitida("Não foi localizado o parâmetro da quantidade de meses necessários para progressão.");
            FacesUtil.addOperacaoNaoPermitida("Verifique as informações em: " + url);
        }

        recuperarDependenciasDoServidor(enquadramentoFuncionalVigente.getContratoServidor());
        atoLegal = (AtoLegal) Web.pegaDaSessao(AtoLegal.class);
        if (atoLegal == null) {
            atoLegal = new AtoLegal();
        }
    }

    private void buscarMesesParaProgressao() {
        ValidacaoException ve = new ValidacaoException();
        meses = planoCargosSalariosFacade.getMesesProgressaoPorPrioridade(enquadramentoFuncional.getCategoriaPCS().getPlanoCargosSalarios(), enquadramentoFuncional.getCategoriaPCS(), enquadramentoFuncional.getProgressaoPCS(), enquadramentoFuncionalVigente.getInicioVigencia());
        if (meses == null || meses <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Este servidor só poderá ter nova progressão após o prazo mínimo parametrizado na Progressão PCCR, Categoria PCCR ou no Plano de cargos e salários a partir do início da última progressão");
        }
        ve.lancarException();
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    @Override
    public void salvar() {
        try {
            validarProgressao();
            validarAfastamento();
            ProvimentoFP provimentoFP;
            provimentoFP = criarProvimentoAndSetarEnquadramento();
            encerrarVigenciaEnquaadramento();
            enquadramentoFuncionalFacade.salvarNovoEnquadramentoFuncional(enquadramentoFuncional, enquadramentoFuncionalVigente, provimentoFP, observacao, atoLegal, categoriaPCS, novaProgressaoPCS);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", e.getMessage()));
            redireciona();
        }
    }

    private void encerrarVigenciaEnquaadramento() {
        enquadramentoFuncionalVigente.setFinalVigencia(DataUtil.getDataDiaAnterior(enquadramentoFuncional.getInicioVigencia()));
        enquadramentoFuncionalVigente.setDataCadastroFinalVigencia(UtilRH.getDataOperacao());
    }

    private ProvimentoFP criarProvimentoAndSetarEnquadramento() {
        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
        enquadramentoFuncional.setContratoServidor(contratoFP);
        enquadramentoFuncional.setCategoriaPCS(categoriaPCS);
        enquadramentoFuncional.setProgressaoPCS(novaProgressaoPCS);
        enquadramentoFuncional.setDataCadastro(UtilRH.getDataOperacao());
        return provimentoFP;
    }

    public void excluirSelecionado() {
        if (folhaDePagamentoFacade.existeFolhaPorContratoData(enquadramentoFuncionalVigente.getContratoServidor(), enquadramentoFuncionalVigente.getInicioVigencia())) {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_INFO, "Impossível excluir ! O Enquadramento Funcional já foi utilizado no cálculo da folha de pagamento !", ""));
        } else {
            excluir();
        }
    }

    private void validarProgressao() {
        ValidacaoException ve = new ValidacaoException();
        if (contratoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo servidor é obrigatório!");
        } else {
            if (enquadramentoFuncionalVigente == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor não possui enquadramento funcional vigente!");
            }

            if (enquadramentoFuncionalVigente != null && (enquadramentoFuncional.getInicioVigencia() != null && enquadramentoFuncional.getInicioVigencia().compareTo(enquadramentoFuncionalVigente.getInicioVigencia()) == 0)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Início da Vigência não pode ser igual a data do Enquadramento Vigente - " + DataUtil.getDataFormatada(enquadramentoFuncionalVigente.getInicioVigencia()));
            }

            if (novaProgressaoPCS == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrada progressão maior que a vigente no plano de cargos e salários deste servidor!");
            }

            if (enquadramentoFuncional.getInicioVigencia() != null) {
                if (enquadramentoFuncional.getInicioVigencia().before(enquadramentoFuncionalVigente.getInicioVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial de vigência da progressão salarial deve ser posterior ao início do enquadramento funcional vigente!");
                }
            }
        }

        if (enquadramentoFuncional.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo início de vigência é obrigatório!");
        }

        if (enquadramentoFuncional.getFinalVigencia() != null) {
            if (enquadramentoFuncional.getFinalVigencia().before(enquadramentoFuncional.getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O final da vigência deve ser posterior o início da vigência!");
            }
        }

        if (atoLegal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ato legal é obrigatório!");
        }
        ve.lancarException();
    }

    public Converter getConverterProgressaoPCS() {
        if (converterProgressaoPCS == null) {
            converterProgressaoPCS = new ConverterGenerico(ProgressaoPCS.class, progressaoPCSFacade);
        }
        return converterProgressaoPCS;
    }

    public void buscarValorNovaProgressao() {
        novoEnquadramentoPCS = buscarValoresParaEnquadramentoPCS(categoriaPCS, novaProgressaoPCS, provimentoFPFacade.getSistemaFacade().getDataOperacao());
    }

    private void recarregaValores() {
        limpaCampos();
        selecionaServidor(null);
    }

    private void limpaCampos() {
        enquadramentoFuncional.setFinalVigencia(null);
        atoLegal = null;
        observacao = "";
    }

    public void navegarAtoLegal() {
        Web.navegacao(getUrlAtual(), "/atolegal/novo/", selecionado, atoLegal);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/progressao-salarial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return this.enquadramentoFuncional.getId();
    }

    public boolean bloqueiaNovaProgressaoDevidoAfastamentoDoServidor() {
        try {
            if (afastamentoFacade.recuperaAfastamentoVigente(contratoFP).getTipoAfastamento().getNaoPermitirProgressao()) {
                return true;
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean bloquearNovaProgressaoDevidoPrazoMininoDeTresAnoAPartirDoInicioDaUltimaProgressao() {
        try {
            if (contratoFP != null) {
                enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoVigente(contratoFP);
                enquadramentoFuncional.setInicioVigencia(getInicioEnquadramento());
                enquadramentoFuncional.setId(null);
                buscarMesesParaProgressao();
                ProvimentoFP provimento = provimentoFPFacade.buscarUltimoProvimentoFPDoTipoProgressao(enquadramentoFuncionalVigente.getContratoServidor().getId(), TipoProvimento.PROVIMENTO_PROGRESSAO);
                if (provimento != null) {
                    DateTime inicioVigenciaNovoEnquadramento = new DateTime(enquadramentoFuncional.getInicioVigencia());
                    DateTime inicioVigenciaEnquadramentoVigente = new DateTime(provimento.getDataProvimento());
                    DateTime prazo = inicioVigenciaEnquadramentoVigente.plusMonths(meses);
                    if (inicioVigenciaNovoEnquadramento.isBefore(prazo)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public Boolean getExisteEnquadramentoFuncionalVigente() {
        return existeEnquadramentoFuncionalVigente;
    }

    public void setExisteEnquadramentoFuncionalVigente(Boolean existeEnquadramentoFuncionalVigente) {
        this.existeEnquadramentoFuncionalVigente = existeEnquadramentoFuncionalVigente;
    }

    public String toStringComAnos() {
        if(meses == null){
            return "";
        }
        return getMeses() + " meses - (" + getMeses() / 12 + ") anos";
    }
}
