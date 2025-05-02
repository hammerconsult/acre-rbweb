package br.com.webpublico.controle.rh.creditodesalariobancos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.entidades.rh.creditodesalario.ItemCreditoSalario;
import br.com.webpublico.entidades.rh.creditodesalario.LogCreditoSalario;
import br.com.webpublico.entidades.rh.creditodesalario.VinculoFPCreditoSalario;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.entidadesauxiliares.rh.creditosalario.DependenciasCreditoSalario;
import br.com.webpublico.enums.TipoDePlataforma;
import br.com.webpublico.enums.TipoGeracaoCreditoSalario;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.creditosalario.BancoCreditoSalario;
import br.com.webpublico.enums.rh.relatorios.TipoLotacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.creditosalariobancos.CreditoSalarioBancosFacade;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.CNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.HeaderCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.TrailerCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.exception.CreditoSalarioExistenteException;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.lote.HeaderLoteCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.lote.TrailerLoteCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.segmento.SegmentoACNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.segmento.SegmentoBCNAB240;
import br.com.webpublico.util.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static br.com.webpublico.util.UtilRH.getDataOperacao;


@ManagedBean(name = "creditoSalarioBancosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-credito-salario-bancos", pattern = "/credito-de-salario/novo/", viewId = "/faces/rh/rotinasanuaisemensais/credito-salario-bancos/edita.xhtml"),
    @URLMapping(id = "editar-credito-salario-bancos", pattern = "/credito-de-salario/editar/#{creditoSalarioBancosControlador.id}/", viewId = "/faces/rh/rotinasanuaisemensais/credito-salario-bancos/edita.xhtml"),
    @URLMapping(id = "ver-credito-salario-bancos", pattern = "/credito-de-salario/ver/#{creditoSalarioBancosControlador.id}/", viewId = "/faces/rh/rotinasanuaisemensais/credito-salario-bancos/visualizar.xhtml"),
    @URLMapping(id = "listar-credito-salario-bancos", pattern = "/credito-de-salario/listar/", viewId = "/faces/rh/rotinasanuaisemensais/credito-salario-bancos/lista.xhtml"),
    @URLMapping(id = "acompanhamento-credito-salario-bancos", pattern = "/credito-de-salario/acompanhamento/", viewId = "/faces/rh/rotinasanuaisemensais/credito-salario-bancos/log.xhtml"),
    @URLMapping(id = "comparar-credito-salario-bancos", pattern = "/credito-de-salario/comparar/", viewId = "/faces/rh/rotinasanuaisemensais/credito-salario-bancos/comparar.xhtml")
})
public class CreditoSalarioBancosControlador extends PrettyControlador<CreditoSalario> implements Serializable, CRUD {


    protected static final Logger logger = LoggerFactory.getLogger(CreditoSalarioBancosControlador.class);

    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private CreditoSalarioBancosFacade creditoSalarioFacade;
    @EJB
    private CreditoSalarioFacade creditoSalarioFacadeOld;
    @EJB
    private VinculoFPFacade vinculoFPFacade;


    private StreamedContent fileDownload;
    private List<GrupoRecursoFP> grupos;
    private List<RecursoFP> recursos;
    private List<HierarquiaOrganizacional> hierarquiaOrganizacionals;
    private List<BeneficioPensaoAlimenticia> beneficiarios;
    private DependenciasCreditoSalario dependenciasCreditoSalario;
    private TipoLotacao tipoLotacao;

    private FileUploadEvent nossoArquivo;
    private FileUploadEvent outroArquivo;
    private DependenciasDirf dependenciasDirf;
    private List<String> nosso;
    private List<String> deles;

    private String logDetalhado;
    private String logErros;
    private AbstractReport abstractReport;
    private Boolean omitirMatricula;
    private List<VinculoFPCreditoSalario> itemVinculoCreditoSalarios;


    public CreditoSalarioBancosControlador() {
        super(CreditoSalario.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return creditoSalarioFacade;
    }

    @URLAction(mappingId = "novo-credito-salario-bancos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializaParametros();
        atribuirContaBancaria();
    }

    @URLAction(mappingId = "comparar-credito-salario-bancos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void comparar() {
        super.novo();
        inicializaParametros();
    }

    private void atribuirContaBancaria() {
        selecionado.setContaBancariaEntidade(contaBancariaEntidadeFacade.buscarContaBancariaPrincipalParaCreditoDeSalario());
        if (selecionado.getContaBancariaEntidade() != null) {
            for (BancoCreditoSalario value : BancoCreditoSalario.values()) {
                if (value.getCodigo().equals(selecionado.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco())) {
                    selecionado.setBancoCreditoSalario(value);
                }
            }
        }
    }

    private void inicializaParametros() {
        selecionado = new CreditoSalario();
        selecionado.setDataRegistro(new Date());
        grupos = Lists.newLinkedList();
        recursos = Lists.newLinkedList();
        hierarquiaOrganizacionals = Lists.newLinkedList();
        beneficiarios = Lists.newArrayList();
        nosso = Lists.newArrayList();
        deles = Lists.newArrayList();
        abstractReport = AbstractReport.getAbstractReport();
        dependenciasCreditoSalario = new DependenciasCreditoSalario();
        dependenciasCreditoSalario.setParado(true);
        dependenciasCreditoSalario.setGerarLogDetalhado(false);
        selecionado.setNumeroRemessa(creditoSalarioFacadeOld.getSequencial());
        tipoLotacao = TipoLotacao.GRUPO_RECURSO_FP;
        omitirMatricula = false;
    }

    @URLAction(mappingId = "ver-credito-salario-bancos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-credito-salario-bancos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }


    @Override
    public String getCaminhoPadrao() {
        return "/credito-de-salario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void limparGrupo() {
        grupos = Lists.newLinkedList();
    }

    public void limparTipoLotacao() {
        buscarFiltros();
    }

    public void buscarFiltros() {
        recursos = Lists.newLinkedList();
        hierarquiaOrganizacionals = Lists.newLinkedList();
        grupos = Lists.newLinkedList();
        selecionado.instanciarListas();
        if (selecionado.getFolhaDePagamento() != null) {
            buscarFiltroPorTipo();
        }
    }

    private void buscarFiltroPorTipo() {
        if (tipoLotacao != null) {
            switch (tipoLotacao) {
                case RECURSO_FP:
                    buscarRecursosFp();
                    break;
                case LOTACAO_FUNCIONAL:
                    buscarLotacaoFuncional();
                    break;
                case GRUPO_RECURSO_FP:
                    carregarListaGruposRecursosFP();
                    break;
            }

        }
    }

    private void buscarRecursosFp() {
        recursos = recursoFPFacade.buscarRecursosFPPorFolha(selecionado.getFolhaDePagamento());
    }

    private void buscarLotacaoFuncional() {
        hierarquiaOrganizacionals = hierarquiaOrganizacionalFacade.buscarHierarquiasPorFolha(selecionado.getFolhaDePagamento());
    }

    public void limparOrgao() {
        selecionado.setHierarquiaOrganizacional(null);
        carregarListaGruposRecursosFP();
    }

    public Boolean getOmitirMatricula() {
        return omitirMatricula;
    }

    public void setOmitirMatricula(Boolean omitirMatricula) {
        this.omitirMatricula = omitirMatricula;
    }

    public String getLogDetalhado() {
        return logDetalhado;
    }

    public void setLogDetalhado(String logDetalhado) {
        this.logDetalhado = logDetalhado != null ? logDetalhado.replaceAll("\n", "<br/>\n") : "";
    }

    public String getLogErros() {
        return logErros;
    }

    public void setLogErros(String logErros) {
        this.logErros = logErros != null ? logErros.replaceAll("\n", "<br/>\n") : "";
    }


    public List<SelectItem> getContasBancariasDaEntidade() {
        return Util.getListSelectItem(contaBancariaEntidadeFacade.buscarContasBancariasParaCreditoDeSalario(), false);
    }

    public List<SelectItem> getBancos() {
        List<BancoCreditoSalario> bancos = Lists.newArrayList();
        if (selecionado.getContaBancariaEntidade() != null) {
            for (BancoCreditoSalario value : BancoCreditoSalario.values()) {
                if (value.getCodigo().equals(selecionado.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco())) {
                    bancos.add(value);
                }
            }

        }
        return Util.getListSelectItem(bancos, false);
    }

    public List<SelectItem> getCompetenciasFP() {
        return Util.getListSelectItem(competenciaFPFacade.buscarTodasCompetencias(), false);
    }

    public List<SelectItem> getFolhasDePagamento() {
        if (selecionado.getCompetenciaFP() != null) {
            return Util.getListSelectItem(folhaDePagamentoFacade.buscarFolhasPorCompetencia(selecionado.getCompetenciaFP()));
        }
        return Arrays.asList(new SelectItem(null, ""));
    }

    public void carregarListaGruposRecursosFP() {
        limparGrupo();
        if (selecionado.getFolhaDePagamento() != null) {
            if (selecionado.getHierarquiaOrganizacional() != null) {
                grupos = grupoRecursoFPFacade.buscarGruposRecursoFPPorFolhaPagamento(selecionado.getFolhaDePagamento().getId(), selecionado.getHierarquiaOrganizacional());
            } else {
                grupos.addAll(grupoRecursoFPFacade.buscarGruposRecursoFPPorFolhaPagamento(selecionado.getFolhaDePagamento().getId(), null));
            }
        }
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
    }

    public List<VinculoFP> completarVinculosFP(String parte) {
        return creditoSalarioFacade.buscarContratoPorNomeOrUnidadeOrNumeroOrMatriculaFP(parte.trim(), null);
    }

    public void buscarBeneficiarios() {
        beneficiarios = Lists.newLinkedList();
        if (TipoGeracaoCreditoSalario.PENSIONISTAS.equals(selecionado.getTipoGeracaoCreditoSalario()) && selecionado.getVinculoFPS() != null) {
            Date dataFolha = Util.criaDataPrimeiroDiaMesJoda(selecionado.getFolhaDePagamento().getMes().getNumeroMes(), selecionado.getFolhaDePagamento().getAno()).toDate();
            for (VinculoFP vinculoFP : selecionado.getVinculoFPS()) {
                verificarAndAdicionarNaLista(creditoSalarioFacade.buscarBeneficiarioPensaoAlimenticiaVigentePorVinculo(vinculoFP, dataFolha));
            }
        }
    }

    private List<VinculoFP> buscarContratoPorMatricula(MatriculaFP matriculaFP) {
        return vinculoFPFacade.buscarVinculosVigentePorMatricula(matriculaFP.getMatricula(), getDataOperacao());
    }

    private void verificarAndAdicionarNaLista(List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticias) {
        for (BeneficioPensaoAlimenticia beneficioPensaoAlimenticia : beneficioPensaoAlimenticias) {
            if (!beneficiarios.contains(beneficioPensaoAlimenticia)) {
                beneficiarios.add(beneficioPensaoAlimenticia);
            }
        }
    }

    public void redirecionarParaNovo() {
        FacesUtil.redirecionamentoInterno("/credito-de-salario/novo/");
    }


    public void redirecionarParaVisualizar() {
        FacesUtil.redirecionamentoInterno("/credito-de-salario/ver/" + dependenciasCreditoSalario.getCreditoSalario().getId());
    }

    public String icone(GrupoRecursoFP grupo) {
        if (selecionado.getGrupos().contains(grupo)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String iconeTodos() {
        if (selecionado.getGrupos().size() == grupos.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String title(GrupoRecursoFP grupo) {
        if (selecionado.getGrupos().contains(grupo)) {
            return "Clique para desmarcar este grupo de recurso.";
        }
        return "Clique para selecionar este grupo de recurso.";
    }

    public String titleTodos() {
        if (selecionado.getGrupos().size() == grupos.size()) {
            return "Clique para desmarcar todos os grupos de recurso.";
        }
        return "Clique para selecionar todos os grupos de recurso.";
    }


    public void selecionarGrupoRecursoFP(GrupoRecursoFP grupo) {
        if (selecionado.getGrupos().contains(grupo)) {
            selecionado.getGrupos().remove(grupo);
        } else {
            selecionado.getGrupos().add(grupo);
        }
    }

    public void selecionarTodosGruposRecursoFP() {
        if (selecionado.getGrupos().size() == grupos.size()) {
            selecionado.getGrupos().removeAll(grupos);
        } else {
            selecionado.getGrupos().clear();
            for (GrupoRecursoFP grupo : grupos) {
                selecionarGrupoRecursoFP(grupo);
            }
        }
    }


    public String iconeRecursoFP(RecursoFP recurso) {
        return "ui-icon-check";
    }

    public String iconeTodosRecursoFP() {
        if (selecionado.getRecursos().size() == recursos.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleRecursoFP(RecursoFP grupo) {
        if (selecionado.getRecursos().contains(grupo)) {
            return "Clique para desmarcar este recurso FP.";
        }
        return "Clique para selecionar este recurso FP.";
    }

    public String titleTodosRecursoFP() {
        if (selecionado.getRecursos().size() == recursos.size()) {
            return "Clique para desmarcar todos os recursos FP.";
        }
        return "Clique para selecionar todos os recurso FP.";
    }


    public void selecionarRecursoFP(RecursoFP grupo) {
        if (selecionado.getRecursos().contains(grupo)) {
            selecionado.getRecursos().remove(grupo);
        } else {
            selecionado.getRecursos().add(grupo);
        }
    }

    public void selecionarTodosRecursoFP() {
        if (selecionado.getRecursos().size() == recursos.size()) {
            selecionado.getRecursos().removeAll(recursos);
        } else {
            selecionado.getRecursos().clear();
            for (RecursoFP grupo : recursos) {
                selecionarRecursoFP(grupo);
            }
        }
    }


    public String iconeHieraquiaOrganizacional(HierarquiaOrganizacional recurso) {
        return "ui-icon-check";
    }

    public String iconeTodosHierarquiaOrganizacional() {
        if (selecionado.getHierarquiaOrganizacionals().size() == hierarquiaOrganizacionals.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleHierarquiaOrganizacional(HierarquiaOrganizacional grupo) {
        if (selecionado.getHierarquiaOrganizacionals().contains(grupo)) {
            return "Clique para desmarcar este esta Lotação Funcional.";
        }
        return "Clique para selecionar esta Lotação Funcional.";
    }

    public String titleTodosHierarquiaOrganizacional() {
        if (selecionado.getHierarquiaOrganizacionals().size() == hierarquiaOrganizacionals.size()) {
            return "Clique para desmarcar todos as Lotações Funcionais.";
        }
        return "Clique para selecionar todos as Lotações Funcionais.";
    }


    public void selecionarHierarquiaOrganizacional(HierarquiaOrganizacional grupo) {
        if (selecionado.getHierarquiaOrganizacionals().contains(grupo)) {
            selecionado.getHierarquiaOrganizacionals().remove(grupo);
        } else {
            selecionado.getHierarquiaOrganizacionals().add(grupo);
        }
    }

    public void selecionarTodosHierarquiaOrganizacional() {
        if (selecionado.getHierarquiaOrganizacionals().size() == hierarquiaOrganizacionals.size()) {
            selecionado.getHierarquiaOrganizacionals().removeAll(hierarquiaOrganizacionals);
        } else {
            selecionado.getHierarquiaOrganizacionals().clear();
            for (HierarquiaOrganizacional grupo : hierarquiaOrganizacionals) {
                selecionarHierarquiaOrganizacional(grupo);
            }
        }
    }

    public DependenciasCreditoSalario getDependenciasCreditoSalario() {
        return dependenciasCreditoSalario;
    }

    public void setDependenciasCreditoSalario(DependenciasCreditoSalario dependenciasCreditoSalario) {
        this.dependenciasCreditoSalario = dependenciasCreditoSalario;
    }

    public List<GrupoRecursoFP> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoRecursoFP> grupos) {
        this.grupos = grupos;
    }

    public List<BeneficioPensaoAlimenticia> getBeneficiarios() {
        return beneficiarios;
    }

    public boolean isPodeRenderizar() {
        return !beneficiarios.isEmpty();
    }

    public void setBeneficiarios(List<BeneficioPensaoAlimenticia> beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    public void selecionar(ActionEvent evento) {
        selecionado = (CreditoSalario) evento.getComponent().getAttributes().get("objetoArquivo");
    }

    public void uploadNosso(FileUploadEvent nossoArquivo) {
        this.nossoArquivo = nossoArquivo;
    }

    public void uploadOutro(FileUploadEvent nossoArquivo) {
        this.outroArquivo = nossoArquivo;
    }

    public FileUploadEvent getOutroArquivo() {
        return outroArquivo;
    }

    public void setOutroArquivo(FileUploadEvent outroArquivo) {
        this.outroArquivo = outroArquivo;
    }

    public List<RecursoFP> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoFP> recursos) {
        this.recursos = recursos;
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionals() {
        return hierarquiaOrganizacionals;
    }

    public void setHierarquiaOrganizacionals(List<HierarquiaOrganizacional> hierarquiaOrganizacionals) {
        this.hierarquiaOrganizacionals = hierarquiaOrganizacionals;
    }

    public List<String> getNosso() {
        return nosso;
    }

    public void setNosso(List<String> nosso) {
        this.nosso = nosso;
    }

    public List<String> getDeles() {
        return deles;
    }

    public void setDeles(List<String> deles) {
        this.deles = deles;
    }

    public TipoLotacao getTipoLotacao() {
        return tipoLotacao;
    }

    public void setTipoLotacao(TipoLotacao tipoLotacao) {
        this.tipoLotacao = tipoLotacao;
    }

    @URLAction(mappingId = "acompanhamento-credito-salario-bancos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        selecionado = (CreditoSalario) Web.pegaDaSessao(CreditoSalario.class);
        dependenciasCreditoSalario = (DependenciasCreditoSalario) Web.pegaDaSessao(DependenciasCreditoSalario.class);
    }

    public void gerarCreditoDeSalario(Boolean substituir) {
        try {
            if (dependenciasCreditoSalario.getParado()) {
                if (substituir) {
                    List<CreditoSalario> existentes = creditoSalarioFacade.buscarCreditoSalarioExistente(selecionado);
                    if (existentes != null && !existentes.isEmpty()) {
                        for (CreditoSalario existente : existentes) {
                            creditoSalarioFacade.remover(existente);
                        }
                    }
                }

                dependenciasCreditoSalario.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
                dependenciasCreditoSalario.setCaminhoRelatorio(abstractReport.getCaminho());
                dependenciasCreditoSalario.setCaminhoImagem(abstractReport.getCaminhoImagem());
                dependenciasCreditoSalario.iniciarProcesso();

                omitirMatricula = false;

                List<GrupoRecursoFP> gruposRecursoFP = selecionado.getGrupos();
                List<VinculoFP> vinculoFPS = selecionado.getVinculoFPS();
                List<HierarquiaOrganizacional> hierarquiaOrganizacionals = selecionado.getHierarquiaOrganizacionals();
                List<RecursoFP> recursos = selecionado.getRecursos();
                BeneficioPensaoAlimenticia[] beneficiarios = selecionado.getBeneficiarios();
                selecionado = creditoSalarioFacade.salvarRetornando(selecionado);
                HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getContaBancariaEntidade().getEntidade().getUnidadeOrganizacional(), sistemaFacade.getDataOperacao());
                selecionado.setHierarquiaOrganizacional(ho);
                FacesUtil.redirecionamentoInterno("/credito-de-salario/acompanhamento/");
                CompletableFuture<DependenciasCreditoSalario> dependenciasCreditoSalarioCompletableFuture = AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(), "Gerar Crédito Salário", 0),
                    () -> {
                        try {
                            return creditoSalarioFacade.gerarCreditoSalario(selecionado, dependenciasCreditoSalario, gruposRecursoFP, vinculoFPS, hierarquiaOrganizacionals, recursos, beneficiarios,  omitirMatricula);
                        } catch (Exception e) {
                            return null;
                        }
                    });
                dependenciasCreditoSalario.setFuture(dependenciasCreditoSalarioCompletableFuture);

                logger.debug("Liberado processo assincrono para geração do crédito de salário.");
                Web.poeNaSessao(selecionado);
                Web.poeNaSessao(dependenciasCreditoSalario);
            }
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void iniciarProcessoGeracaoCreditoSalario() {
        try {
            validarCampos();
            validarDadosDoSelecionado();
            HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getContaBancariaEntidade().getEntidade().getUnidadeOrganizacional(), sistemaFacade.getDataOperacao());
            validarDadosUsadosDaHierarquiaRoot(ho);
            selecionado.setHierarquiaOrganizacional(ho);
            EnderecoCorreio ec = recuperarEndereco(creditoSalarioFacade.recuperarPessoaJuridica(selecionado.getHierarquiaOrganizacional().getSubordinada().getEntidade().getPessoaJuridica()));
            validarEndereco(ec, selecionado.getHierarquiaOrganizacional());
            selecionado.getHierarquiaOrganizacional().getSubordinada().getEntidade().getPessoaJuridica().setEnderecoPrincipal(ec);
            creditoSalarioFacade.validarCreditoSalario(selecionado);
            gerarCreditoDeSalario(true);
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (CreditoSalarioExistenteException re) {
            FacesUtil.executaJavaScript("dialogArquivoJaExistente.show()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("form-arquivo-existente");
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void validarEndereco(EnderecoCorreio e, HierarquiaOrganizacional ho) {
        ValidacaoException ve = new ValidacaoException();
        if (e == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Endereço cadastrado para a Pessoa Jurídica " + ho.getSubordinada().getEntidade().getPessoaJuridica());
        }
        ve.lancarException();
    }

    private EnderecoCorreio recuperarEndereco(Pessoa pessoa) {
        return pessoa.getEnderecoPorPrioridade();
    }

    private void validarDadosUsadosDaHierarquiaRoot(HierarquiaOrganizacional ho) {
        ValidacaoException ve = new ValidacaoException();
        if (ho.getSubordinada().getEntidade() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe entidade cadastrada para a Unidade Organizacional " + ho.getSubordinada());
        } else {
            if (ho.getSubordinada().getEntidade().getPessoaJuridica() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Pessoa Jurídica cadastrada para a Entidade " + ho.getSubordinada().getEntidade());
            } else if (ho.getSubordinada().getEntidade().getPessoaJuridica().getCnpj() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe CNPJ cadastrado para a Pessoa Jurídica " + ho.getSubordinada().getEntidade().getPessoaJuridica());
            }
        }
        ve.lancarException();
    }


    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContaBancariaEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Bancária deve ser informado.");
        }
        if (selecionado.getCompetenciaFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Competência deve ser informado.");
        }
        if (selecionado.getFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Folha de Pagamento deve ser informado.");
        }
        if (selecionado.getDataCredito() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Crédito deve ser informado.");
        }
        if (selecionado.getNumeroRemessa() == null || selecionado.getNumeroRemessa() == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo número sequencial deve ser maior que zero.");
            if (creditoSalarioFacadeOld.getSequencial().equals(selecionado.getNumeroRemessa())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O número sequencial " + selecionado.getNumeroRemessa() + " já está cadastrado.");
            }
        }
        ve.lancarException();
    }

    private void validarDadosDoSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContaBancariaEntidade().getAgencia() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Agência cadastrada para a Conta Bancária selecionada.");
        } else if (selecionado.getContaBancariaEntidade().getAgencia().getBanco() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Banco cadastrado para a Agência da Conta Bancária selecionada.");
        } else {
            if (selecionado.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco() == null || selecionado.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco().trim().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Número do Banco cadastrado para o Banco da Conta Bancária selecionada.");
            }
            if (selecionado.getContaBancariaEntidade().getAgencia().getBanco().getDescricao() == null || selecionado.getContaBancariaEntidade().getAgencia().getBanco().getDescricao().trim().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Nome cadastrado para o Banco da Conta Bancária selecionada.");
            }
            if (selecionado.getContaBancariaEntidade().getAgencia().getNumeroAgencia() == null || selecionado.getContaBancariaEntidade().getAgencia().getNumeroAgencia().trim().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Número do Banco cadastrado para o Banco da Conta Bancária selecionada.");
            }
        }
        ve.lancarException();
    }

    public String mensagemArquivoExistente() {
        String retorno = "Já existe um arquivo gerado para a conta bancária: <b>" + selecionado.getContaBancariaEntidade() + "</b> e folha: <b>" + selecionado.getFolhaDePagamento() + "</b> para: <b> " + selecionado.getTipoGeracaoCreditoSalario().getDescricao() + "</b>.";
        retorno += "<br/><br/>";
        return retorno;
    }

    public void atualizarFinal() {
        if (dependenciasCreditoSalario != null) {
            if (dependenciasCreditoSalario.getParado()) {
                if (dependenciasCreditoSalario.getFuture() != null) {
                    dependenciasCreditoSalario.getFuture().cancel(true);
                }
                FacesUtil.atualizarComponente("Formulario");
            }
        }
    }

    private List<Arquivo> buscarArquivos(CreditoSalario selecionado) {
        selecionado = creditoSalarioFacade.recuperar(selecionado.getId());
        List<Arquivo> arquivos = Lists.newLinkedList();
        for (ItemCreditoSalario itemCreditoSalario : selecionado.getItensCreditoSalario()) {
            for (ArquivoComposicao itemArquivo : itemCreditoSalario.getArquivo().getArquivosComposicao()) {
                arquivos.add(itemArquivo.getArquivo());
            }
        }
        return arquivos;
    }

    public StreamedContent recuperarArquivoParaDownload() {
        if (selecionado == null) {
            FacesUtil.addError("Erro", "Com os parâmetros utilizados, não foram obtidos dados para informar. Arquivo não gerado!");
            return null;
        }
        List<Arquivo> arquivos = buscarArquivos(selecionado);
        StreamedContent s = createMasterFile(arquivos);
        return s;
    }

    public StreamedContent recuperarArquivoParaDownload(ItemCreditoSalario item) {
        StreamedContent s = null;
        Arquivo arquivo = arquivoFacade.recuperaDependencias(item.getArquivo().getId());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arquivo.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        String bufferWindows = null;
        try {
            bufferWindows = converterArquivoLfToCrlf(buffer.toString(StandardCharsets.ISO_8859_1.name()));
        } catch (UnsupportedEncodingException e) {
            logger.error("erro: ", e);
        }
        InputStream is = new ByteArrayInputStream(bufferWindows.getBytes());
        s = new DefaultStreamedContent(is, arquivo.getMimeType(), arquivo.getNome());
        return s;
    }

    public StreamedContent recuperarArquivoComposicaoParaDownload(ArquivoComposicao item) {
        Arquivo arquivo = arquivoFacade.recuperaDependencias(item.getArquivo().getId());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arquivo.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        String bufferWindows = null;
        try {
            bufferWindows = converterArquivoLfToCrlf(buffer.toString(StandardCharsets.ISO_8859_1.name()));
        } catch (UnsupportedEncodingException e) {
            logger.error("erro: ", e);
        }
        InputStream is = new ByteArrayInputStream(bufferWindows.getBytes());
        StreamedContent s = new DefaultStreamedContent(is, arquivo.getMimeType(), arquivo.getNome());
        if (arquivo.getNome() != null && arquivo.getNome().contains("pdf")) {
            InputStream inpStrRelatorio = new ByteArrayInputStream(buffer.toByteArray());
            StreamedContent sCRelatorio = new DefaultStreamedContent(inpStrRelatorio, arquivo.getMimeType(), arquivo.getNome());
            return sCRelatorio;
        } else {
            return s;
        }
    }

    private String converterArquivoLfToCrlf(String byteArrayOutputStream) {
        ConfiguracaoRH configuracaoRH = creditoSalarioFacade.getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente();
        return TipoDePlataforma.WINDOWS.equals(configuracaoRH.getTipoDePlataforma()) ?
            byteArrayOutputStream.replaceAll("\n", "\r\n") : byteArrayOutputStream;
    }

    private String getNomeArquivo() {
        String dataGeracao = new SimpleDateFormat("ddMMyyyy").format(new Date());
        String codigoConvenio = selecionado.getContaBancariaEntidade().getCodigoDoConvenio();
        String numeroRemessa = StringUtil.cortaOuCompletaEsquerda(selecionado.getNumeroRemessa() + "", 6, "0");
        return selecionado.getBancoCreditoSalario().name() + "240." + dataGeracao + "." + codigoConvenio + "." + numeroRemessa + ".REM";
    }

    public void compararArquivos() {
        try {
            dependenciasCreditoSalario = new DependenciasCreditoSalario();
            dependenciasCreditoSalario.iniciarProcesso();
            dependenciasCreditoSalario.setGerarLogDetalhado(true);

            this.nosso = getRegistroParticionado(nossoArquivo.getFile().getInputstream());
            dependenciasCreditoSalario.setLogDetalhado(new StringBuilder());

            this.deles = getRegistroParticionado(outroArquivo.getFile().getInputstream());
            dependenciasCreditoSalario.setLogDetalhado(new StringBuilder());

            logger.debug("{}", nosso);
            logger.debug("{}", deles);
        } catch (Exception e) {
            logger.error("Erro: ", e);
        }

    }

    private List<String> getRegistroParticionado(InputStream stream) {
        List<String> linhas = Lists.newLinkedList();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String linha;
            while ((linha = reader.readLine()) != null) {

                CNAB240 cnab240 = getLinha(getInstanciaCorreta(linha), linha);
                creditoSalarioFacade.gerarLogDetalhado(cnab240, dependenciasCreditoSalario);
                linhas.addAll(Arrays.asList(dependenciasCreditoSalario.getLogDetalhado().toString().split("\n")));
                dependenciasCreditoSalario.setLogDetalhado(new StringBuilder());
            }
            return linhas;

        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
        return linhas;
    }

    private CNAB240 getInstanciaCorreta(String linha) {
        String tipo = linha.substring(7, 8);
        String segmento = linha.substring(13, 14);
        if ("0".equalsIgnoreCase(tipo)) {
            return HeaderCNAB240.newInstance();
        }
        if ("1".equalsIgnoreCase(tipo)) {
            return HeaderLoteCNAB240.newInstance();
        }
        if ("3".equalsIgnoreCase(tipo) && "A".equalsIgnoreCase(segmento)) {
            return SegmentoACNAB240.newInstance();
        }
        if ("3".equalsIgnoreCase(tipo) && "B".equalsIgnoreCase(segmento)) {
            return SegmentoBCNAB240.newInstance();
        }
        if ("5".equalsIgnoreCase(tipo)) {
            return TrailerLoteCNAB240.newInstance();
        }
        if ("9".equalsIgnoreCase(tipo)) {
            return TrailerCNAB240.newInstance();
        }
        return null;
    }

    public CNAB240 getLinha(CNAB240 registro, String linha) throws NoSuchFieldException, IllegalAccessException {
        Iterable<Field> campos = Util.getFieldsUpTo(registro.getClass(), Object.class);

        Integer contadorPosicao = 0;
        for (Field campo : campos) {
            if (campo.isAnnotationPresent(Campo.class)) {
                Campo annotation = campo.getAnnotation(Campo.class);
                String substring = "";
                try {
                    substring = linha.substring(contadorPosicao, contadorPosicao + annotation.tamanho());
                } catch (IndexOutOfBoundsException idx) {
                    logger.error("Erro: {}", idx.getMessage());
                }
                campo.setAccessible(true);
                campo.set(registro, substring);

                contadorPosicao += annotation.tamanho();
            }
        }
        return registro;
    }

    public File getFileFromImputStream(FileUploadEvent arquivo) throws IOException {
        InputStream initialStream = new FileInputStream(File.createTempFile(arquivo.getFile().getFileName(), ".txt"));
        byte[] buffer = new byte[arquivo.getFile().getInputstream().available()];
        initialStream.read(buffer);

        File nossoFile = File.createTempFile(arquivo.getFile().getFileName() + "SAIDA", ".txt");
        OutputStream outStream = new FileOutputStream(nossoFile);
        outStream.write(buffer);
        buffer.clone();
        outStream.close();
        return nossoFile;
    }

    public StreamedContent createMasterFile(List<Arquivo> arquivos) {
        try {
            File zip = File.createTempFile("creditoSalario-" + Util.dateToString(new Date()), ".zip");
            byte[] buffer = new byte[1024];
            FileOutputStream fout = new FileOutputStream(zip);
            ZipOutputStream zout = new ZipOutputStream(fout);

            for (Arquivo m : arquivos) {

                /*arquivo = File.createTempFile("Holerite", "txt");
                fos = new FileOutputStream(arquivo);
                fos.write(selecionado.getConteudoArquivo().toString().getBytes());
                fos.close();
                InputStream stream = new FileInputStream(arquivo);*/

                m = arquivoFacade.recuperaDependencias(m.getId());
                File temp = File.createTempFile(m.getNome(), "REM");
                FileOutputStream fos = new FileOutputStream(temp);
                if (m.getMimeType() == null) {
                    fos.write(getByteArrayDosDados(m));
                } else {
                    fos.write(m.getByteArrayDosDados());
                }
                fos.close();
                FileInputStream fin = new FileInputStream(temp);
                zout.putNextEntry(new ZipEntry(m.getNome()));
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }
                zout.closeEntry();
                fin.close();
            }
            FileInputStream fis = new FileInputStream(zip);
            fileDownload = new DefaultStreamedContent(fis, "application/zip", "creditoSalario-" + Util.dateToString(new Date()) + ".zip");
            zout.close();
            return fileDownload;
        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo ZIP do Sicap, comunique o administrador. Detalhe: " + ioe.getMessage());
        }
        return null;
    }

    public byte[] getByteArrayDosDados(Arquivo arquivo) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arquivo.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao recuperar o arquivo " + ex.getMessage());
            }
        }
        String novoBuffer = null;
        try {
            novoBuffer = converterArquivoLfToCrlf(buffer.toString(StandardCharsets.ISO_8859_1.name()));
        } catch (UnsupportedEncodingException e) {
            logger.error("erro: ", e);
        }
        return novoBuffer.getBytes();
    }

    public void gerarRelatorioInconsistencias() {
        try {
            validarInconsistencias(selecionado);
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("rh/relatorio-inconsistencias-credito-salario/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("IMAGEM");
        dto.setNomeRelatorio("Inconsistências do Arquivo Crédito Salário");
        dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getLogin());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "Departamento de Recursos Humanos");
        dto.adicionarParametro("NOMERELATORIO", "Inconsistências do Arquivo Crédito Salário");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("FOLHADEPAGAMENTO", selecionado.getFolhaDePagamento().toString());
        dto.adicionarParametro("COMPETENCIA", selecionado.getFolhaDePagamento().getCompetenciaFP().toString());
        dto.adicionarParametro("FILTRO", selecionado.getId());
        return dto;
    }

    private void validarInconsistencias(CreditoSalario creditoSalario) {
        ValidacaoException ve = new ValidacaoException();
        List<LogCreditoSalario> inconsistencias = selecionado.getItemLogCreditoSalario();
        if (inconsistencias == null || inconsistencias.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado inconsistências durante a geração do arquivo.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposGeracaoCreditoSalario() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoGeracaoCreditoSalario tga : TipoGeracaoCreditoSalario.values()) {
            toReturn.add(new SelectItem(tga, tga.getDescricao()));
        }
        return toReturn;
    }

    public List<VinculoFPCreditoSalario> getItemVinculoCreditoSalarios() {
        return itemVinculoCreditoSalarios;
    }

    public void setItemVinculoCreditoSalarios(List<VinculoFPCreditoSalario> itemVinculoCreditoSalarios) {
        if (itemVinculoCreditoSalarios != null) {
            Collections.sort(itemVinculoCreditoSalarios, new Comparator<VinculoFPCreditoSalario>() {
                @Override
                public int compare(VinculoFPCreditoSalario o1, VinculoFPCreditoSalario o2) {
                    return o1.getVinculoFP().getMatriculaFP().getPessoa().getNome().compareTo(o2.getVinculoFP().getMatriculaFP().getPessoa().getNome());
                }
            });
        }
        this.itemVinculoCreditoSalarios = itemVinculoCreditoSalarios;
    }

    public List<SelectItem> getTiposLotacao() {
        //TODO limitando somente para os grupos de recurso para RB
        return Util.getListSelectItem(Arrays.asList(TipoLotacao.GRUPO_RECURSO_FP));
    }

    public void selecionarRelatorioCreditoSalario(ActionEvent evento) {
        selecionado = (CreditoSalario) evento.getComponent().getAttributes().get("objRelatorio");
        try {
            selecionado.setArquivoRelatorio(arquivoFacade.recuperaDependencias(selecionado.getArquivoRelatorio().getId()));
        } catch (Exception e) {
            logger.error("Erro ao gerar o Relatório {}", e.getMessage());
        }
    }

    public StreamedContent montarRelatorioConferenciaParaDownload() {
        if (selecionado.getArquivoRelatorio() != null) {
            return arquivoFacade.montarArquivoParaDownloadPorArquivo(selecionado.getArquivoRelatorio());
        }
        return null;
    }
}



