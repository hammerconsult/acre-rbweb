package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-execucao-fisica-financeira", pattern = "/relatorio/execucao-fisica-financeira/", viewId = "/faces/financeiro/relatorio/relatorioexecucaofisicafinanceira.xhtml")
})
public class RelatorioExecucaoFisicaFinanceiraPpaControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private LDOFacade ldoFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private PPA ppa;
    private LDO ldo;
    private AcaoPPA acaoPPA;
    private ProgramaPPA programaPPA;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Boolean exibirEmpenhado;
    private Boolean exibirLiquidado;
    private Boolean exibirPago;
    private Boolean exibirDataPreenchimento;
    private Boolean exibirResponsavel;
    private Boolean exibirRodape;

    @URLAction(mappingId = "relatorio-execucao-fisica-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        ppa = null;
        ldo = null;
        acaoPPA = null;
        programaPPA = null;
        hierarquiaOrganizacional = null;
        exibirEmpenhado = Boolean.TRUE;
        exibirLiquidado = Boolean.TRUE;
        exibirPago = Boolean.TRUE;
        exibirDataPreenchimento = Boolean.TRUE;
        exibirResponsavel = Boolean.TRUE;
        exibirRodape = Boolean.TRUE;
    }

    public List<PPA> completarPpas(String filtro) {
        return ppaFacade.buscarPpas(filtro);
    }

    public List<SelectItem> buscarLdos() {
        return ppa != null ? Util.getListSelectItem(ldoFacade.listaLdoPorPpa(ppa)) : Lists.<SelectItem>newArrayList();
    }

    public List<AcaoPPA> completarProjetosAtividade(String filtro) {
        return projetoAtividadeFacade.buscarAcoesPPAPorExercicio(filtro, sistemaFacade.getExercicioCorrente());
    }

    public List<ProgramaPPA> completarProgramasPPA(String filtro) {
        return programaPPAFacade.listaFiltrandoProgramasPorExercicio(filtro, sistemaFacade.getExercicioCorrente());
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String filtro) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(filtro, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getDataOperacao());
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("DESCRICAO_PPA", ppa != null ? ppa.getDescricao() : "PPA");
            dto.adicionarParametro("EXIBIR_EMPENHO", exibirEmpenhado);
            dto.adicionarParametro("EXIBIR_LIQUIDADO", exibirLiquidado);
            dto.adicionarParametro("EXIBIR_PAGO", exibirPago);
            dto.adicionarParametro("EXIBIR_DATAPREENCHIMENTO", exibirDataPreenchimento);
            dto.adicionarParametro("EXIBIR_RESPONSAVEL", exibirResponsavel);
            dto.adicionarParametro("EXIBIR_RODAPE", exibirRodape);
            montarParametros(dto);
            dto.setNomeRelatorio("RELATORIO-EXECUCAO-FISICA-FINANCEIRA");
            dto.setApi("contabil/relatorio-execucao-fisica-financeira/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void montarParametros(RelatorioDTO relatorioDTO) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()), null, 0, true));
        if (ppa != null) {
            parametros.add(new ParametrosRelatorios(" PPA.id ", ":PPA_ID", null, OperacaoRelatorio.IGUAL, ppa.getId(), null, 1, false));
        }
        if (ldo != null) {
            parametros.add(new ParametrosRelatorios(" ld.id ", ":LDO_ID", null, OperacaoRelatorio.IGUAL, ldo.getId(), null, 1, false));
            parametros.add(new ParametrosRelatorios(null, ":ID_EXERCICIO", null, OperacaoRelatorio.IGUAL, ldo.getExercicio().getId(), null, 0, false));
        } else {
            parametros.add(new ParametrosRelatorios(null, ":ID_EXERCICIO", null, OperacaoRelatorio.IGUAL, sistemaFacade.getExercicioCorrente().getId(), null, 0, false));
        }
        if (acaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" ac.id ", ":ACAO_ID", null, OperacaoRelatorio.IGUAL, acaoPPA.getId(), null, 1, false));
        }
        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" prog.id ", ":PROG_ID", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
        }
        if (hierarquiaOrganizacional != null) {
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":subordinada_ID", null, OperacaoRelatorio.IGUAL, hierarquiaOrganizacional.getSubordinada().getId(), null, 1, false));
        }
        relatorioDTO.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(parametros));
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getExibirEmpenhado() {
        return exibirEmpenhado;
    }

    public void setExibirEmpenhado(Boolean exibirEmpenhado) {
        this.exibirEmpenhado = exibirEmpenhado;
    }

    public Boolean getExibirLiquidado() {
        return exibirLiquidado;
    }

    public void setExibirLiquidado(Boolean exibirLiquidado) {
        this.exibirLiquidado = exibirLiquidado;
    }

    public Boolean getExibirPago() {
        return exibirPago;
    }

    public void setExibirPago(Boolean exibirPago) {
        this.exibirPago = exibirPago;
    }

    public Boolean getExibirDataPreenchimento() {
        return exibirDataPreenchimento;
    }

    public void setExibirDataPreenchimento(Boolean exibirDataPreenchimento) {
        this.exibirDataPreenchimento = exibirDataPreenchimento;
    }

    public Boolean getExibirResponsavel() {
        return exibirResponsavel;
    }

    public void setExibirResponsavel(Boolean exibirResponsavel) {
        this.exibirResponsavel = exibirResponsavel;
    }

    public Boolean getExibirRodape() {
        return exibirRodape;
    }

    public void setExibirRodape(Boolean exibirRodape) {
        this.exibirRodape = exibirRodape;
    }
}
