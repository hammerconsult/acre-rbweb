package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.DespesaORC;
import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DespesaORCFacade;
import br.com.webpublico.negocios.FonteDespesaORCFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
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

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-movimento-despesaorc", pattern = "/relatorio-movimento-despesaorc/", viewId = "/faces/financeiro/relatorio/relatorio-movimento-despesaorc.xhtml")
})
public class RelatorioMovimentacaoDespesaOrcamentariaControlador extends RelatorioContabilSuperControlador {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    private ConverterAutoComplete converterFonteDespesaORC;
    private ConverterAutoComplete converterDespesaORC;
    private FonteDespesaORC fonteDespesaORC;
    private DespesaORC despesaORC;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @URLAction(mappingId = "relatorio-movimento-despesaorc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        dataInicial = DataUtil.getPrimeiroDiaAno(getSistemaFacade().getExercicioCorrente().getAno());
        dataFinal = getSistemaFacade().getDataOperacao();
        hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataFinal, getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        atribuirNullDespesaEFonte();
    }

    public void atribuirNullDespesaEFonte() {
        despesaORC = null;
        atribuirNullFonte();
    }

    public void atribuirNullFonte() {
        fonteDespesaORC = null;
    }

    public void gerarRelatorio() {
        try {
            validarDatasSemVerificarExercicioLogado();
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.setNomeParametroBrasao("BRASAO");
            montarParametros(dto);
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("dataInicial", dataInicial.getTime());
            dto.adicionarParametro("dataFinal", dataFinal.getTime());
            dto.setNomeRelatorio("Relatório de Movimentação de Despesa Orçamentária");
            dto.setApi("contabil/extrato-movimento-despesa-orc/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }


    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Unidade organizacional é obrigatório.");
        }
        if (despesaORC == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Despesa orçamentária é obrigatório.");
        }
        if (fonteDespesaORC == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Fonte de recursos é obrigatório.");
        }
        ve.lancarException();
    }

    private void montarParametros(RelatorioDTO dto) {
        filtros = getFiltrosPeriodo();
        List<ParametrosRelatorios> parametrosRelatorios = Lists.newArrayList();
        parametrosRelatorios.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IGUAL, hierarquiaOrganizacional.getSubordinada().getId(), null, 1, false));
        parametrosRelatorios.add(new ParametrosRelatorios(" desp.id ", ":despId", null, OperacaoRelatorio.IGUAL, despesaORC.getId(), null, 1, false));
        parametrosRelatorios.add(new ParametrosRelatorios(" font.id ", ":fnotId", null, OperacaoRelatorio.IGUAL, fonteDespesaORC.getId(), null, 1, false));
        filtros += " Unidade Organizacional: " + hierarquiaOrganizacional.getCodigo() + " -";
        filtros += " Despesa Orçamentária: " + despesaORC.getCodigo() + " -";
        filtros += " Fonte de Recursos: " + fonteDespesaORC.getDescricaoFonteDeRecurso();
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(parametrosRelatorios));
    }

    public List<HierarquiaOrganizacional> completarHierquias(String filtro) {
        if (dataFinal != null) {
            return hierarquiaOrganizacionalFacade.filtraPorNivel(filtro.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), dataFinal);
        }
        return Lists.newArrayList();
    }

    @Override
    public String getNomeRelatorio() {
        return "";
    }

    public List<DespesaORC> completarDespesaORC(String parte) {
        if (hierarquiaOrganizacional != null) {
            return despesaORCFacade.buscarDespesasOrcPorExercicioAndUnidade(parte.trim(), buscarExercicioPelaDataFinal(), hierarquiaOrganizacional.getSubordinada(), null);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getFontesDespesaORC() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (despesaORC != null) {
            for (FonteDespesaORC fonteDespesaORC : fonteDespesaORCFacade.completaFonteDespesaORC("", despesaORC)) {
                toReturn.add(new SelectItem(fonteDespesaORC, "" + fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos()));
            }
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, fonteDespesaORCFacade);
        }
        return converterFonteDespesaORC;
    }

    public ConverterAutoComplete getConverterDespesaORC() {
        if (converterDespesaORC == null) {
            converterDespesaORC = new ConverterAutoComplete(DespesaORC.class, despesaORCFacade);
        }
        return converterDespesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
