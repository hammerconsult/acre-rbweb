package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.pccr.ModoGeracaoProgressao;
import br.com.webpublico.entidadesauxiliares.ProgressaoAutomatica;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.executores.GerarProgressoesExecutor;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.YearMonth;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean(name = "gerarProgressoesControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaProgressaoAutomatica", pattern = "/gerarprogressao/novo/", viewId = "/faces/rh/administracaodepagamento/gerarprogressoes/edita.xhtml"),
    @URLMapping(id = "editarProgressaoAutomatica", pattern = "/gerarprogressao/editar/#{gerarProgressoesControlador.id}/", viewId = "/faces/rh/administracaodepagamento/gerarprogressoes/edita.xhtml"),
    @URLMapping(id = "listarGeracao", pattern = "/gerarprogressao/listar/", viewId = "/faces/rh/administracaodepagamento/gerarprogressoes/lista.xhtml"),
    @URLMapping(id = "verProgressaoAutomatica", pattern = "/gerarprogressao/ver/#{gerarProgressoesControlador.id}/", viewId = "/faces/rh/administracaodepagamento/gerarprogressoes/visualizar.xhtml")
})
public class GerarProgressoesControlador extends PrettyControlador<ProgressaoAuto> implements Serializable, CRUD {
    private AtoLegal atoLegal;
    @EJB
    private GerarProgressoesFacade gerarProgressoesFacade;
    private List<ProgressaoAutomatica> progressaoAutomaticaList;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    private ConverterAutoComplete converterAtoLegal, converterHierarquiaOrganizacional;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Boolean todosOrgaos;
    private Boolean marcarTodos;
    private Date dataInicioVigencia;
    private PlanoCargosSalarios planoCargosSalarios;
    private CategoriaPCS selecionadoCategoriaPCS;
    private ProgressaoPCS selecionadoProgressaoPCS;
    private VinculoFP vinculoFP;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    private Boolean multiplasLetras;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<AssistenteBarraProgresso> future;
    private Date competenciaReferencia;
    private List<HierarquiaOrganizacional> hieraquiasOrganizacionaisSelecionadas;

    public GerarProgressoesControlador() {
        super(ProgressaoAuto.class);
        this.todosOrgaos = Boolean.TRUE;
        hieraquiasOrganizacionaisSelecionadas = Lists.newArrayList();
    }

    public void gerarProgressoesAutomaticas() {
        if (todosOrgaos) {
            hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getRaizHierarquia(UtilRH.getDataOperacao());
        }
        try {
            validarCamposGeracao();
            selecionado.setUnidades(recuperarUnidades());
            List<BigDecimal> listaContratos = gerarProgressoesFacade.buscarIdsContratosProgressao(vinculoFP, planoCargosSalarios, selecionadoCategoriaPCS, selecionadoProgressaoPCS, hieraquiasOrganizacionaisSelecionadas);

            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Gerando Progressões Automáticas...");
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setTotal(listaContratos.size());
            future = new GerarProgressoesExecutor(gerarProgressoesFacade, tipoProvimentoFacade, contratoFPFacade, provimentoFPFacade, enquadramentoFuncionalFacade, planoCargosSalariosFacade,
                vinculoFPFacade, progressaoPCSFacade, enquadramentoPCSFacade, afastamentoFacade).execute(hierarquiaOrganizacional, dataInicioVigencia, selecionado.getModoGeracaoProgressao(), planoCargosSalarios,
                selecionadoCategoriaPCS, selecionadoProgressaoPCS, vinculoFP, multiplasLetras, sistemaFacade.getDataOperacao(), listaContratos, competenciaReferencia, assistenteBarraProgresso);
            FacesUtil.executaJavaScript("gerarProgrecoesAutomaticas()");
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar Progressões Automáticas: " + ex);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private List<UnidadeProgressaoAuto> recuperarUnidades() {
        List<UnidadeProgressaoAuto> unidadesProgressaoAuto = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : hieraquiasOrganizacionaisSelecionadas) {
            unidadesProgressaoAuto.add(new UnidadeProgressaoAuto(selecionado, ho.getSubordinada()));
        }
        return unidadesProgressaoAuto;
    }

    public void verificarTermino() {
        if (future != null && future.isDone()) {
            try {
                AssistenteBarraProgresso assistente = future.get();
                progressaoAutomaticaList = (List<ProgressaoAutomatica>) assistente.getSelecionado();
                Collections.sort(progressaoAutomaticaList);
                ordenarProgressoes(progressaoAutomaticaList);
            } catch (Exception e) {
                logger.error("Erro ao gerar Progressões Automáticas: ", e);
                FacesUtil.addOperacaoNaoRealizada(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao() + "Erro ao gerar Progressões Automáticas.");
            }
            RequestContext.getCurrentInstance().update("Formulario:tabelaPrincipal");
            FacesUtil.executaJavaScript("termina()");
            future = null;
            FacesUtil.executaJavaScript("dialogo.hide()");
        }
    }

    private void ordenarProgressoes(List<ProgressaoAutomatica> progressoes) {
        Collections.sort(progressoes, new Comparator<ProgressaoAutomatica>() {
            @Override
            public int compare(ProgressaoAutomatica o1, ProgressaoAutomatica o2) {
                if (o1.getInconsistenciaProgressao() != null && o2.getInconsistenciaProgressao() != null) {
                    return o1.getInconsistenciaProgressao().getBloquearSalvar().compareTo(o2.getInconsistenciaProgressao().getBloquearSalvar());
                }
                return 0;
            }
        });
    }

    private void validarCamposGeracao() {
        ValidacaoException ve = new ValidacaoException();
        if (!todosOrgaos && hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Orgão é obrigatório.");
        }
        if (selecionado.getModoGeracaoProgressao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Modo de Geração é obrigatório.");
        }
        if (vinculoFP == null && planoCargosSalarios == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Plano de Cargos e Salários é obrigatório.");
        }
        if (competenciaReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Competência de Referência é obrigatório.");
        } else {
            YearMonth anoMesReferencia = new YearMonth(competenciaReferencia);
            YearMonth anoMesOp = new YearMonth(DataUtil.adicionarMeses(sistemaFacade.getDataOperacao(), 1));
            if (anoMesReferencia.isAfter(anoMesOp)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A competência da referência não pode ser maior que o mês sequente ao da data atual.");
            }
        }
        if (hieraquiasOrganizacionaisSelecionadas.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Deve ser selecionado ao menos um órgão.");
        }
        ve.lancarException();
    }

    public void todos() {
        logger.debug("seraSalvo : " + marcarTodos);
        for (ProgressaoAutomatica progressaoAutomatica : progressaoAutomaticaList) {
            if (progressaoAutomatica.getInconsistenciaProgressao() == null || !progressaoAutomatica.getInconsistenciaProgressao().getBloquearSalvar()) {
                progressaoAutomatica.setSeraSalvo(marcarTodos);
            }
        }
    }

    public List<ProgressaoAutomatica> getProgressaoAutomaticaList() {
        return progressaoAutomaticaList;
    }

    public void setProgressaoAutomaticaList(List<ProgressaoAutomatica> progressaoAutomaticaList) {
        this.progressaoAutomaticaList = progressaoAutomaticaList;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    @URLAction(mappingId = "novaProgressaoAutomatica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        progressaoAutomaticaList = new ArrayList<>();
        marcarTodos = false;
        multiplasLetras = true;
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
        selecionado.setModoGeracaoProgressao(ModoGeracaoProgressao.MODO_PADRAO_BASE_ULTIMO_ENQUADRAMENTO);
        competenciaReferencia = DataUtil.adicionarMeses(sistemaFacade.getDataOperacao(), 1);
        hieraquiasOrganizacionaisSelecionadas = Lists.newArrayList();
    }

    @URLAction(mappingId = "verProgressaoAutomatica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
        carregarValores();
        hieraquiasOrganizacionaisSelecionadas = Lists.newArrayList();
        buscarHierarquiasDaUnidade();
    }

    @URLAction(mappingId = "editarProgressaoAutomatica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void carregarValores() {
        for (ItemProgressaoAuto item : selecionado.getItemProgressaoAutos()) {
            definirVencimentoBase(item.getEnquadramentoAntigo());
            definirVencimentoBase(item.getEnquadramentoNovo());
        }
    }

    private void buscarHierarquiasDaUnidade(){
        for (UnidadeProgressaoAuto unidadeProgressao : selecionado.getUnidades()) {
           hieraquiasOrganizacionaisSelecionadas.add(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(unidadeProgressao.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, UtilRH.getDataOperacao()));
        }

    }

    private void definirVencimentoBase(EnquadramentoFuncional ef) {
        EnquadramentoPCS pcs = enquadramentoPCSFacade.recuperaValor(ef.getCategoriaPCS(), ef.getProgressaoPCS(), sistemaFacade.getDataOperacao());
        if (pcs != null && pcs.getId() != null) {
            ef.setVencimentoBase(pcs.getVencimentoBase());
        } else {
            ef.setVencimentoBase(BigDecimal.ZERO);
        }
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoNomeNumero(parte.trim());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void salvar() {
        try {
            validarCampos();
            selecionado.setDataCadastro(new Date());
            gerarProgressoesFacade.salvarProgressoesAutomaticas(selecionado, atoLegal, progressaoAutomaticaList);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar Progressões Automáticas: " + ex);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (atoLegal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O ato legal deve ser preenchido.");
        }
        if (progressaoAutomaticaList == null || progressaoAutomaticaList.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessário gerar as progressões primeiro, para depois salvar-las.");
        } else {
            boolean progressaoSelecionada = false;
            for (ProgressaoAutomatica p : progressaoAutomaticaList) {
                if (p.getSeraSalvo()) {
                    progressaoSelecionada = true;
                    break;
                }
            }
            if (!progressaoSelecionada) {
                ve.adicionarMensagemDeCampoObrigatorio("É necessário selecionar ao menos uma progressão para ser salva.");
            }
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/gerarprogressao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Date getDataInicioVigencia() {
        return dataInicioVigencia;
    }

    public void setDataInicioVigencia(Date dataInicioVigencia) {
        this.dataInicioVigencia = dataInicioVigencia;
    }

    public List<SelectItem> getModosGeracao() {
        return Util.getListSelectItem(ModoGeracaoProgressao.values(), false);
    }

    public PlanoCargosSalarios getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(PlanoCargosSalarios planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    public CategoriaPCS getSelecionadoCategoriaPCS() {
        return selecionadoCategoriaPCS;
    }

    public void setSelecionadoCategoriaPCS(CategoriaPCS selecionadoCategoriaPCS) {
        this.selecionadoCategoriaPCS = selecionadoCategoriaPCS;
    }

    public ProgressaoPCS getSelecionadoProgressaoPCS() {
        return selecionadoProgressaoPCS;
    }

    public void setSelecionadoProgressaoPCS(ProgressaoPCS selecionadoProgressaoPCS) {
        this.selecionadoProgressaoPCS = selecionadoProgressaoPCS;
    }

    public List<SelectItem> getPlanos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoCargosSalarios object : planoCargosSalariosFacade.listaFiltrandoVigencia(UtilRH.getDataOperacao())) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategorias() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalarios != null) {
            if (planoCargosSalarios.getId() != null) {
                for (CategoriaPCS object : categoriaPCSFacade.getRaizPorPlano(planoCargosSalarios)) {
                    toReturn.add(new SelectItem(object, object.getDescricao()));
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getProgressoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalarios != null && selecionadoCategoriaPCS != null) {
            if (planoCargosSalarios.getId() != null) {
                for (ProgressaoPCS object : progressaoPCSFacade.buscaProgressoesSuperioresEnquadramentoPCSPorCategoriaSuperior(selecionadoCategoriaPCS, sistemaFacade.getDataOperacao())) {
                    toReturn.add(new SelectItem(object, (object.getCodigo() != null ? object.getCodigo() + " - " : "") + "" + object.getDescricao()));
                }
            }
        }
        return toReturn;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    @Override
    public AbstractFacade getFacede() {
        return gerarProgressoesFacade;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getTodosOrgaos() {
        return todosOrgaos;
    }

    public void setTodosOrgaos(Boolean todosOrgaos) {
        this.todosOrgaos = todosOrgaos;
    }

    public Boolean getMarcarTodos() {
        return marcarTodos;
    }

    public void setMarcarTodos(Boolean marcarTodos) {
        this.marcarTodos = marcarTodos;
    }

    public Boolean getMultiplasLetras() {
        return multiplasLetras == null ? false : multiplasLetras;
    }

    public void setMultiplasLetras(Boolean multiplasLetras) {
        this.multiplasLetras = multiplasLetras;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public Date getCompetenciaReferencia() {
        return competenciaReferencia;
    }

    public void setCompetenciaReferencia(Date competenciaReferencia) {
        this.competenciaReferencia = competenciaReferencia;
    }

    public List<HierarquiaOrganizacional> getHieraquiasOrganizacionaisSelecionadas() {
        return hieraquiasOrganizacionaisSelecionadas;
    }

    public void setHieraquiasOrganizacionaisSelecionadas(List<HierarquiaOrganizacional> hieraquiasOrganizacionaisSelecionadas) {
        this.hieraquiasOrganizacionaisSelecionadas = hieraquiasOrganizacionaisSelecionadas;
    }
}
