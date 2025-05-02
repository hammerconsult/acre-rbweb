package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoInvestimento;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoLancamento;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mateus on 19/10/17.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-investimento", pattern = "/relatorio/investimento/", viewId = "/faces/financeiro/relatorio/relatorio-investimento.xhtml")
})
@ManagedBean
public class RelatorioInvestimentoControlador extends RelatorioContabilSuperControlador implements Serializable {

    private OperacaoInvestimento operacao;

    public RelatorioInvestimentoControlador() {
    }

    @URLAction(mappingId = "relatorio-investimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        operacao = null;
        tipoLancamento = null;
    }

    public void gerarRelatorio() {
        try {
            validarDatas();
            RelatorioDTO dto = montarRelatorioDto();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
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
        dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
        dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
        dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("MODULO", "Contábil");
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("FILTRO", filtros);
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi("contabil/investimento/");
        return dto;
    }

    public String getCaminhoRelatorio() {
        return "/relatorio/investimento/";
    }


    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-INVESTIMENTO";
    }

    public List<SelectItem> getTiposDeLancamentos() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem("", null));
        for (TipoLancamento tipo : TipoLancamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem("", null));
        for (OperacaoInvestimento tipo : OperacaoInvestimento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        filtrosParametros(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        return parametros;
    }

    private void filtrosParametros(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":datainicial", ":datafinal", null, getDataInicialFormatada(), getDataFinalFormatada(), 0, true));
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
        }
        filtros += getFiltrosPeriodo();
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> parametros) {
        if (tipoLancamento != null) {
            parametros.add(new ParametrosRelatorios(" inv.tipoLancamento ", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.name(), null, 1, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        if (operacao != null) {
            parametros.add(new ParametrosRelatorios(" inv.operacaoInvestimento ", ":operacao", null, OperacaoRelatorio.IGUAL, operacao.name(), null, 1, false));
            filtros += " Operação: " + operacao.getDescricao() + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" inv.pessoa_id ", ":idPessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.toString() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    public OperacaoInvestimento getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoInvestimento operacao) {
        this.operacao = operacao;
    }
}
