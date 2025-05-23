package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProcessoCalculoAlvara;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.CalculoAlvaraFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.PageEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "novoLancamentoTaxaAlvaraVigenteLote",
        pattern = "/tributario/alvara/lancamento-taxas-em-lote/",
        viewId = "/faces/tributario/alvara/lancamento-taxas-em-lote/edita.xhtml"),
})
public class LancamentoTaxaAlvaraVigenteLoteControlador implements Serializable {

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Exercicio exercicio;
    private GrauDeRiscoDTO grauDeRisco;
    private CadastroEconomico cadastroEconomico;
    private Future<List<CadastroEconomico>> futureCadastros;
    private List<CadastroEconomico> cadastros;
    private List<CadastroEconomico> cadastrosSelecionados;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private CompletableFuture<List<ProcessoCalculoAlvara>> future;
    private Future futureEnvioDAMs;
    private TipoCalculoLoteAlvara tipoCalculoLoteAlvara;
    private static String ID_DATATABLE_CADASTROS = ":Formulario:tabelaCadastros";

    private Integer rowsTable;
    private Integer pageTable;

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public GrauDeRiscoDTO getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(GrauDeRiscoDTO grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<CadastroEconomico> getCadastros() {
        return cadastros;
    }

    public void setCadastros(List<CadastroEconomico> cadastros) {
        this.cadastros = cadastros;
    }

    public List<CadastroEconomico> getCadastrosSelecionados() {
        if (cadastrosSelecionados == null) {
            cadastrosSelecionados = Lists.newArrayList();
        }
        return cadastrosSelecionados;
    }

    public void setCadastrosSelecionados(List<CadastroEconomico> cadastrosSelecionados) {
        this.cadastrosSelecionados = cadastrosSelecionados;
    }

    public Integer getPageTable() {
        if (pageTable == null) {
            pageTable = 0;
        }
        return pageTable;
    }

    public void setPageTable(Integer pageTable) {
        DataTable dataTable = (DataTable) FacesUtil.buscarComponente(ID_DATATABLE_CADASTROS);
        if (dataTable != null) {
            dataTable.reset();
        }
        this.pageTable = pageTable;
    }

    public Integer getRowsTable() {
        return rowsTable;
    }

    public void setRowsTable(Integer rowsTable) {
        DataTable dataTable = (DataTable) FacesUtil.buscarComponente(ID_DATATABLE_CADASTROS);
        if (dataTable != null) {
            dataTable.setRows(rowsTable);
        }
        setPageTable(0);
        this.rowsTable = rowsTable;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public TipoCalculoLoteAlvara getTipoCalculoLoteAlvara() {
        return tipoCalculoLoteAlvara;
    }

    public void setTipoCalculoLoteAlvara(TipoCalculoLoteAlvara tipoCalculoLoteAlvara) {
        this.tipoCalculoLoteAlvara = tipoCalculoLoteAlvara;
    }

    @URLAction(mappingId = "novoLancamentoTaxaAlvaraVigenteLote",
        phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        exercicio = sistemaFacade.getExercicioCorrente();
        grauDeRisco = null;
        cadastroEconomico = null;
        cadastros = Lists.newArrayList();
        cadastrosSelecionados = Lists.newArrayList();
        tipoCalculoLoteAlvara = TipoCalculoLoteAlvara.CMCS_COM_CALCULOS_DE_ALVARAS_VIGENTES;
        setRowsTable(10);
    }

    public List<SelectItem> getGrausDeRisco() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(GrauDeRiscoDTO.BAIXO_RISCO_A, GrauDeRiscoDTO.BAIXO_RISCO_A.getDescricao()));
        toReturn.add(new SelectItem(GrauDeRiscoDTO.BAIXO_RISCO_B, GrauDeRiscoDTO.BAIXO_RISCO_B.getDescricao()));
        return toReturn;
    }

    public List<CadastroEconomico> buscarCadastrosEconomicos(String parte) {
        return cadastroEconomicoFacade.buscarCMCsPorNomeRazaoSocialOrCpfCnpjOrSituacao(parte);
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (tipoCalculoLoteAlvara == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cálculo deve ser informado.");
        } else if (TipoCalculoLoteAlvara.CMCS_ATIVOS_SEM_CALCULOS_DE_ALVARAS_VIGENTES.equals(tipoCalculoLoteAlvara) && !GrauDeRiscoDTO.BAIXO_RISCO_A.equals(grauDeRisco)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Cálculo selecionado só é permitido para Grau de " + GrauDeRiscoDTO.BAIXO_RISCO_A.getDescricao());
        }
        ve.lancarException();
    }

    public void buscarCadastros() {
        try {
            validarFiltros();
            cadastrosSelecionados = null;
            if (TipoCalculoLoteAlvara.CMCS_COM_CALCULOS_DE_ALVARAS_VIGENTES.equals(tipoCalculoLoteAlvara)) {
                futureCadastros = cadastroEconomicoFacade.buscarCadastrosComAlvaraVigenteSemDebito(exercicio, grauDeRisco, cadastroEconomico);
            } else if (TipoCalculoLoteAlvara.CMCS_ATIVOS_SEM_CALCULOS_DE_ALVARAS_VIGENTES.equals(tipoCalculoLoteAlvara)) {
                futureCadastros = cadastroEconomicoFacade.buscarCadastrosAtivosSemAlvaraVigente(grauDeRisco, cadastroEconomico);
            }
            FacesUtil.executaJavaScript("pollBuscarCadastros.start()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void acompanharBuscaCadastros() throws ExecutionException, InterruptedException {
        if (futureCadastros != null && futureCadastros.isDone()) {
            FacesUtil.executaJavaScript("pollBuscarCadastros.stop()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            cadastros = futureCadastros.get();
            FacesUtil.atualizarComponente("Formulario");
            if (cadastros == null || cadastros.isEmpty()) {
                FacesUtil.addAtencao("Nenhum cadastro econômico encontrado para geração das taxas de alvará.");
            } else {
                setRowsTable(10);
            }
        }
    }

    public boolean hasCadastroAdicionado(CadastroEconomico cadastroEconomico) {
        return cadastrosSelecionados != null && cadastrosSelecionados.contains(cadastroEconomico);
    }

    public void adicionarCadastro(CadastroEconomico cadastroEconomico) {
        if (cadastrosSelecionados == null) {
            cadastrosSelecionados = Lists.newArrayList();
        }
        if (!cadastrosSelecionados.contains(cadastroEconomico)) {
            cadastrosSelecionados.add(cadastroEconomico);
        }
    }

    public void removerCadastro(CadastroEconomico cadastroEconomico) {
        cadastrosSelecionados.remove(cadastroEconomico);
    }

    public boolean todosCadastrosAdicionados() {
        return cadastros != null && cadastrosSelecionados != null && !cadastros.isEmpty() && cadastros.size() == cadastrosSelecionados.size();
    }

    public void changePage(PageEvent event) {
        setPageTable(event.getPage());
        FacesUtil.atualizarComponente("Formulario:selecionarPagina");
    }

    public boolean todosCadastrosDaPaginaAdicionados() {
        int index = getPageTable() == 0 ? 0 : (getRowsTable() * getPageTable());
        boolean todosDaPaginaSelecionados = !cadastros.isEmpty();
        for (int i = 0; i < getRowsTable() && index < cadastros.size(); i++, index++) {
            if (!getCadastrosSelecionados().contains(cadastros.get(index))) {
                todosDaPaginaSelecionados = false;
                break;
            }
        }
        return todosDaPaginaSelecionados;
    }

    public void todosCadastros(Boolean adicionar) {
        cadastrosSelecionados = Lists.newArrayList();
        if (adicionar && cadastros != null && !cadastros.isEmpty()) {
            cadastrosSelecionados.addAll(cadastros);
        } else {
            cadastrosSelecionados.clear();
        }
    }

    public void selecionarOuDesmarcarTodosPorPagina() {
        int index = getPageTable() == 0 ? 0 : (getRowsTable() * getPageTable());
        boolean todosCadastrosDaPaginaSelecionados = todosCadastrosDaPaginaAdicionados();
        for (int i = 0; i < getRowsTable() && index < cadastros.size(); i++, index++) {
            if (!todosCadastrosDaPaginaSelecionados) {
                getCadastrosSelecionados().add(cadastros.get(index));
            } else if (getRowsTable() > cadastros.size() || getRowsTable().equals(cadastros.size())) {
                getCadastrosSelecionados().clear();
            } else {
                for (int i1 = 0; i1 < getCadastrosSelecionados().size(); i1++) {
                    if (getCadastrosSelecionados().get(i1).getId().equals(cadastros.get(index).getId())) {
                        getCadastrosSelecionados().remove(i1);
                    }
                }
            }
        }
    }

    private void validarGeracao() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastrosSelecionados == null || cadastrosSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum cadastro econômico selecionado.");
        }
        ve.lancarException();
    }

    public void gerarTaxas() {
        try {
            validarGeracao();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso, () -> {
                    try {
                        return calculoAlvaraFacade.gerarTaxasAlvarasEmLote(assistenteBarraProgresso,
                            exercicio, cadastrosSelecionados);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            );
            FacesUtil.executaJavaScript("openDialog(dialogGeracao)");
            FacesUtil.executaJavaScript("pollGeracao.start()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharGeracao() {
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("pollGeracao.stop()");
            try {
                futureEnvioDAMs = calculoAlvaraFacade.enviarPdfDAMsRedeSim(assistenteBarraProgresso, future.get());
                FacesUtil.executaJavaScript("pollEnvioDAMs.start()");
            } catch (Exception e) {
                FacesUtil.addErrorPadrao(e);
            }
        }
    }

    public void acompanharEnvioDAMs() {
        if (futureEnvioDAMs != null && futureEnvioDAMs.isDone()) {
            FacesUtil.executaJavaScript("pollEnvioDAMs.stop()");
            finalizarGeracao();
        }
    }

    public void finalizarGeracao() {
        cadastros = Lists.newArrayList();
        cadastrosSelecionados = null;
        FacesUtil.executaJavaScript("closeDialog(dialogGeracao)");
        FacesUtil.atualizarComponente("Formulario");
        FacesUtil.addOperacaoRealizada("Taxas de alvará geradas com sucesso!");
    }

    public enum TipoCalculoLoteAlvara implements EnumComDescricao {
        CMCS_COM_CALCULOS_DE_ALVARAS_VIGENTES("CMCs com cálculos de Alvarás Vigentes"),
        CMCS_ATIVOS_SEM_CALCULOS_DE_ALVARAS_VIGENTES("CMCs Ativos, mas sem cálculos de Alvarás Vigentes");

        private String descricao;

        TipoCalculoLoteAlvara(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    public List<TipoCalculoLoteAlvara> getTiposDeCalculos() {
        return Arrays.asList(TipoCalculoLoteAlvara.values());
    }
}
