package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.log4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * Created by zaca on 08/11/16.
 */
@ManagedBean(name = "relatorioComparativoSaldo")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-comparativo-saldo",
        pattern = "/relatorio/relatorio-comparativo-saldo-contabil-financeiro/",
        viewId = "/faces/financeiro/relatorio/relatorio-comparativo-saldo-contabil-financeiro.xhtml")
})
public class RelatorioComparativoSaldoContabilFinanceiroControlador extends RelatorioContabilSuperControlador {

    public static final Logger log = Logger.getLogger(RelatorioComparativoSaldoContabilFinanceiroControlador.class);

    @Override
    public String getNomeRelatorio() {
        return "Relatório Comparativo Saldo Contábil e Financeiro";
    }

    public void gerarRelatorio() {
        try {
            validarDataDeReferencia();
            RelatorioDTO dto = montarRelatorioDto();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            log.error(" erro ao gerar o relatorio.: {} ", ex);
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDataDeReferencia();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("MODULO", "Financeiro e Contábil");
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USER", getNomeUsuarioLogado(), true);
        dto.adicionarParametro("NOME_RELATORIO", getNomeRelatorio());
        dto.setNomeRelatorio(getNomeArquivo());
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("FILTRO", filtros);
        dto.adicionarParametro("DATA_REFERENCIA", DataUtil.getDataFormatada(dataReferencia));
        dto.adicionarParametro("EXERCICIO", buscarExercicioPelaDataDeReferencia().getAno().toString());
        dto.adicionarParametro("DATA_HOJE", DataUtil.getDataFormatadaDiaHora(getSistemaFacade().getDataOperacao()));
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.setApi("contabil/comparativo-saldo-contabil-financeiro/");
        return dto;
    }

    public void limparUnidadeAndUnidadeGestora() {
        unidadeGestora = null;
        listaUnidades = Lists.newArrayList();
    }

    @URLAction(mappingId = "relatorio-comparativo-saldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
    }

    @Override
    public String getNomeArquivo() {
        return new StringBuilder("Relatorio-Comparativo-Saldo-Contabil-e-Saldo-Financeiro-")
            .append(DataUtil.getDataFormatada(dataReferencia, "yyyy/MM/dd")).append("-CSL").toString();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametrosRelatorio = Lists.newArrayList();
        carregarParametroRelatorio(parametrosRelatorio);
        carregarParametroUnidade(parametrosRelatorio);
        return parametrosRelatorio;
    }

    private void carregarParametroUnidade(List<ParametrosRelatorios> parametrosRelatorio) {
        List<Long> idsDeUnidades = Lists.newArrayList();
        filtros = "Data de Referência: " + DataUtil.getDataFormatada(this.getDataReferencia()) + " ";
        if (!listaUnidades.isEmpty()) {
            String concat = "  ";
            StringBuilder filtroUnidade = new StringBuilder(" ");
            for (HierarquiaOrganizacional hierarquia : listaUnidades) {
                idsDeUnidades.add(hierarquia.getSubordinada().getId());
                filtroUnidade.append(concat).append(hierarquia.getCodigo().substring(3, 10));
                concat = " - ";
            }
            parametrosRelatorio.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsDeUnidades, null, 1, false));
            filtros += " Unidade(s): " + filtroUnidade.toString();

        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : unidadesDoUsuario) {
                idsDeUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsDeUnidades.isEmpty()) {
                parametrosRelatorio.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsDeUnidades, null, 1, false));
            }
        } else {
            parametrosRelatorio.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            filtros += " -  Unidade Gestora: " + unidadeGestora.getDescricao() + " ";
            filtroUG = unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao();
        }
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataDeReferencia().getId(), null, 0, false));
    }

    private void carregarParametroRelatorio(List<ParametrosRelatorios> parametrosRelatorio) {
        parametrosRelatorio.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, null, DataUtil.getDataFormatada(dataReferencia), null, 0, true));
        parametrosRelatorio.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, DataUtil.getDataFormatada(DataUtil.getPrimeiroDiaAno(buscarExercicioPelaDataDeReferencia().getAno())), null, 0, true));
        adicionarExercicio(parametrosRelatorio);
    }
}
