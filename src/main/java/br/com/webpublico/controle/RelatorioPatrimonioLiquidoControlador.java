package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioContabilSuperControlador;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoPatrimonioLiquido;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.PatrimonioLiquidoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mga on 18/10/2017.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-patrimonio-liquido", pattern = "/relatorio/patrimonio-liquido/", viewId = "/faces/financeiro/relatorio/relatorio-patrimonio-liquido.xhtml")
})
@ManagedBean
public class RelatorioPatrimonioLiquidoControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private PatrimonioLiquidoFacade facade;
    private OperacaoPatrimonioLiquido operacao;

    @URLAction(mappingId = "relatorio-patrimonio-liquido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        operacao = null;
        tipoLancamento = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTRO_DATA", getFiltrosDatas());
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("FILTRO_UG", filtroUG);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/patrimonio-liquido/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        }catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String getCaminhoRelatorio() {
        return "/relatorio/patrimonio-liquido/";
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-PATRIMONIO-LIQUIDO";
    }

    public List<SelectItem> getTipoLancamentos() {
        return Util.getListSelectItem(TipoLancamento.values());
    }

    public List<SelectItem> getOperacoes() {
        return Util.getListSelectItem(OperacaoPatrimonioLiquido.values());
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        filtrosParametros(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        return parametros;
    }

    private void filtrosParametros(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":datainicial", null, null, getDataInicialFormatada(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":datafinal", null, null, getDataFinalFormatada(), null, 0, true));
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
        }
        filtros += getFiltrosPeriodo();
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> parametros) {
        if (tipoLancamento != null) {
            parametros.add(new ParametrosRelatorios(" obj.tipoLancamento", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.name(), null, 1, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        if (operacao != null) {
            parametros.add(new ParametrosRelatorios(" obj.operacaoPatrimonioLiquido", ":operacao", null, OperacaoRelatorio.IGUAL, operacao.name(), null, 1, false));
            filtros += " Operação: " + operacao.getDescricao() + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" obj.pessoa_id ", ":idPessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.toString() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    public OperacaoPatrimonioLiquido getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoPatrimonioLiquido operacao) {
        this.operacao = operacao;
    }
}
