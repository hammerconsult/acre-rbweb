package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.FiltroExecucaoObraPorConvenio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by wellington on 23/05/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioExecucaoObrasPorConvenio",
        pattern = "/administrativo/obras/relatorio-execucao-obra-por-convenio/",
        viewId = "/faces/administrativo/obras/relatorios/execucaoobraporconvenio.xhtml")})

public class RelatorioExecucaoObraPorConvenioControlador extends AbstractReport {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private FiltroExecucaoObraPorConvenio filtroExecucaoObraPorConvenio;
    private String filtros;

    public RelatorioExecucaoObraPorConvenioControlador() {
    }

    public FiltroExecucaoObraPorConvenio getFiltroExecucaoObraPorConvenio() {
        return filtroExecucaoObraPorConvenio;
    }

    public void setFiltroExecucaoObraPorConvenio(FiltroExecucaoObraPorConvenio filtroExecucaoObraPorConvenio) {
        this.filtroExecucaoObraPorConvenio = filtroExecucaoObraPorConvenio;
    }

    @URLAction(mappingId = "novoRelatorioExecucaoObrasPorConvenio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        inicializarFiltros();
    }

    private void inicializarFiltros() {
        filtroExecucaoObraPorConvenio = new FiltroExecucaoObraPorConvenio();
    }

    public void limparFiltros() {
        inicializarFiltros();
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("SECRETARIA", getSecretaria());
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("condicao", montarCondicaoEFiltros());
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/execucao-obra-por-convenio/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "Relatório de Execução de Obras por Convênio";
    }

    private String getSecretaria() {
        return hierarquiaOrganizacionalFacade.getDescricaoHierarquia(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente(), getSistemaFacade().getDataOperacao());
    }

    private String montarCondicaoEFiltros() {
        StringBuilder condicao = new StringBuilder();
        filtros =  "";
        String juncaoFiltros = "";
        String juncao = " where ";
        if (filtroExecucaoObraPorConvenio.getObra() != null && filtroExecucaoObraPorConvenio.getObra().getId() != null) {
            condicao.append(juncao).append(" o.id = ").append(filtroExecucaoObraPorConvenio.getObra().getId());
            juncao = " and ";
            filtros += juncaoFiltros + "Obra: " + filtroExecucaoObraPorConvenio.getObra();
            juncaoFiltros = "; ";
        }
        if (filtroExecucaoObraPorConvenio.getConvenioReceita() != null && filtroExecucaoObraPorConvenio.getConvenioReceita().getId() != null) {
            condicao.append(juncao).append(" cr.id = ").append(filtroExecucaoObraPorConvenio.getConvenioReceita().getId());
            juncao = " and ";
            filtros += juncaoFiltros + "Convênio de Despesa: " + filtroExecucaoObraPorConvenio.getConvenioReceita();
            juncaoFiltros = "; ";
        }
        if (filtroExecucaoObraPorConvenio.getExercicioContrato() != null) {
            condicao.append(juncao).append(" ec.id = ").append(filtroExecucaoObraPorConvenio.getExercicioContrato().getId());
            juncao = " and ";
            filtros += juncaoFiltros + "Exercício do Contrato: " + filtroExecucaoObraPorConvenio.getExercicioContrato();
            juncaoFiltros = "; ";
        }
        if (filtroExecucaoObraPorConvenio.getNumeroContrato() != null) {
            condicao.append(juncao).append(" c.numerotermo = ").append(filtroExecucaoObraPorConvenio.getNumeroContrato());
            filtros += juncaoFiltros + "Número do Contrato: " + filtroExecucaoObraPorConvenio.getNumeroContrato();
        }
        if (filtros.length() == 0) {
            filtros += "Nenhum critério utilizado.";
        }
        return condicao.toString();
    }
}
