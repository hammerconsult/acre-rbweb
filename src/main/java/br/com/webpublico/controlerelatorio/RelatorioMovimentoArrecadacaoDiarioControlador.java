/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author juggernaut
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-arrecadacao-diario", pattern = "/relatorio/movimento-arrecadacao-diario/", viewId = "/faces/financeiro/relatorio/relatoriomovimentoarrecadacaodiario.xhtml")
})
public class RelatorioMovimentoArrecadacaoDiarioControlador extends RelatorioContabilSuperControlador implements Serializable {

    public RelatorioMovimentoArrecadacaoDiarioControlador() {
    }

    public List<SubConta> completarSubContas(String parte) {
        return relatorioContabilSuperFacade.getSubContaFacade().listaFiltrandoSubConta(parte);
    }

    public List<Conta> completarContas(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoReceita(parte, getSistemaControlador().getExercicioCorrente());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/movimento-arrecadacao-diario/");
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
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        parametros.add(new ParametrosRelatorios(" trunc(LANC.DATALANCAMENTO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicialFormatada(), getDataFinalFormatada(), 1, true));
        filtros = " Período: " + getDataInicialFormatada() + " a " + getDataFinalFormatada() + " -";
        parametros = filtrosParametrosUnidade(parametros);
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.ID ", ":contaId", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
            filtros += " Conta Receita: " + conta.getCodigo() + " -";
        }
        if (this.contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" SUB.ID ", ":contaFinanceira", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " - " + contaFinanceira.getDescricao() + " -";
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    @URLAction(mappingId = "relatorio-arrecadacao-diario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
    }

    @Override
    public String getNomeRelatorio() {
        return "ARRECADAÇÃO-DIARIO";
    }
}
