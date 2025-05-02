package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by HardRock on 21/02/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-liberacao-financeira", pattern = "/relatorio/liberacao-financeira/", viewId = "/faces/financeiro/relatorio/relatorio-liberacao-financeira.xhtml")
})
public class RelatorioLiberacaoFinanceiraControlador extends RelatorioContabilSuperControlador implements Serializable {

    @URLAction(mappingId = "relatorio-liberacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
    }

    public List<Conta> completarContaDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));

            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("NOME_RELATORIO", "Relatório de Liberações Financeira");
            dto.adicionarParametro("FILTRO_DATA", getFiltrosDatas());
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("parametrosRelatorioDTO", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            if (unidadeGestora != null) {
                dto.adicionarParametro("FILTRO_UG", filtroUG);
            }
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/liberacao-financeira/");
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

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        filtrosParametros(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    private void filtrosParametros(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, getDataInicialFormatada(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, getDataFinalFormatada(), null, 0, true));
        filtros += getFiltrosPeriodo();
    }

    public void filtrosParametrosGerais(List<ParametrosRelatorios> parametros) {
        if (contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" cbe.id ", ":idCbe", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 1, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroContaComDigitoVerificador().trim() + " -";
        }
        if (contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" sb.id ", ":idSubConta", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo().trim() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" font.codigo ", ":codigoFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString().trim() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" cdest.id", ":cdest", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + conta.getCodigo() + " -";
        }
        filtros = atualizaFiltrosGerais();
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATÓRIO-LIBERAÇÃO-FINANCEIRA";
    }
}
