package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.AssistenteConsultaMovimentacaoBemContabil;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created  by Mateus on 29/06/2015.
 */
@ManagedBean(name = "relatorioDemonstrativoBensMoveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-bens-moveis", pattern = "/relatorio/demonstrativo-bens-moveis/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativobensmoveis.xhtml")
})
public class RelatorioDemonstrativoBensMoveisControlador extends RelatorioContabilSuperControlador implements Serializable {

    private Boolean exibirOperacao;

    public RelatorioDemonstrativoBensMoveisControlador() {
    }

    @URLAction(mappingId = "relatorio-demonstrativo-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        exibirOperacao = false;
    }

    public void gerarRelatorio(AssistenteConsultaMovimentacaoBemContabil assistenteConsulta) {
        try {
            limparCampos();
            exibirOperacao = false;
            assistenteConsulta.setDataInicial(DataUtil.montaData(1, 1, DataUtil.getAno(assistenteConsulta.getDataFinal())).getTime());
            setApresentacao(ApresentacaoRelatorio.CONSOLIDADO);
            setDataInicial(assistenteConsulta.getDataInicial());
            setDataFinal(assistenteConsulta.getDataFinal());
            if (assistenteConsulta.getHierarquiaOrcamentaria() != null) {
                setListaUnidades(Lists.newArrayList(assistenteConsulta.getHierarquiaOrcamentaria()));
            }

            gerarRelatorio();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorio() {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("apresentacaoRelatorioDTO", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("exibirOperacao", exibirOperacao);
            dto.adicionarParametro("FILTRO", getFiltrosAtualizados());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("NOME_RELATORIO", exibirOperacao ? "Demonstrativo de Bens Móveis por Grupo Patrimonial e Operação" : "Demonstrativo de Bens Móveis por Grupo Patrimonial");
            dto.adicionarParametro("DATA_INICIAL", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("DATA_FINAL", DataUtil.getDataFormatada(dataFinal));
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-bens-moveis/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
            filtros = "";
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getFiltrosAtualizados() {
        filtros = getFiltrosPeriodo() + filtros;
        filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 1);
        return filtros;
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATA_INICIAL", ":DATA_FINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        parametros = filtrosParametrosUnidade(parametros);

        if (unidadeGestora != null || getApresentacao().equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
            adicionarExercicio(parametros);
        }
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(formato.format(dataInicial)));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
    }

    @Override
    public String getNomeRelatorio() {
        if (exibirOperacao) {
            return "DEMONSTRATIVO-BENS-MOVEIS-POR-OPERACAO";
        }
        return "DEMONSTRATIVO-BENS-MOVEIS";
    }

    public Boolean getExibirOperacao() {
        return exibirOperacao;
    }

    public void setExibirOperacao(Boolean exibirOperacao) {
        this.exibirOperacao = exibirOperacao;
    }
}
