package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zaca on 19/10/16.
 */
@ManagedBean(name = "demonstrativoExecucaoDespesaFuncao")
@ViewScoped
@URLMapping(id = "demonstrativoExecucaoOrcamentariaDespesaFuncao",
    pattern = "/relatorio/demonstrativo-execucao-orcamentaria-despesa-por-funcao/",
    viewId = "/faces/financeiro/relatorio/demonstrativoexecucaoorcamentariadespesafuncao.xhtml")
public class DemonstrativoExecucaoOrcamentariaDespesaFuncaoControlador extends RelatorioContabilSuperControlador implements Serializable {

    private FonteDeRecursos fonteInicial;
    private FonteDeRecursos fonteFinal;

    @Override
    public String getNomeRelatorio() {
        return "DEMONSTRATIVO-EXECUÇÃO-ORÇAMENTÁRIA-DESPESA-POR-FUNÇÃO";
    }

    public DemonstrativoExecucaoOrcamentariaDespesaFuncaoControlador() {
    }

    @URLAction(mappingId = "demonstrativoExecucaoOrcamentariaDespesaFuncao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        fonteInicial = null;
        fonteFinal = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarRelatorio();
            filtros = "";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno() + "");
            dto.adicionarParametro("MES", mes.getDescricao());
            dto.adicionarParametro("SIGLA", "RLEOD003");
            dto.adicionarParametro("NOME_RELATORIO", "Demonstrativo da Execução Orçamentária da Despesa por Função");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-execucao-orcamentaria-despesa-por-funcao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e){
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        adicionarFitrosPadrao(parametros);
        filtrosParametrosUnidade(parametros);
        return parametros;
    }

    private void adicionarFitrosPadrao(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATACORRENTE", null, OperacaoRelatorio.IGUAL, definirDataAsParameter(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ANO", null, OperacaoRelatorio.IGUAL, getSistemaControlador().getExercicioCorrente().getAno(), null, 0, false));

        if (this.getUnidadeGestora() != null) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, OperacaoRelatorio.IGUAL, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
        }

        if (this.getFonteInicial() != null) {
            filtros = "Fonte de Recurso Inicial: " + getFonteInicial().toString() + " - ";
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":FONTE_INICIAL", null, OperacaoRelatorio.MAIOR_IGUAL, this.fonteInicial.getCodigo(), null, 2, false));
        }
        if (this.getFonteFinal() != null) {
            filtros += "Fonte de Recurso Final: " + getFonteFinal().toString() + " - ";
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":FONTE_FINAL", null, OperacaoRelatorio.MENOR_IGUAL, this.fonteFinal.getCodigo(), null, 2, false));
        }

        filtros = atualizaFiltrosGerais();
    }

    private String definirDataAsParameter() {
        return !Mes.EXERCICIO.equals(mes) ? getAsDataMontada() : getAsUltimaDataDiaAnoPorExercicioCorrente();
    }

    private String getAsDataMontada() {
        int diaMes = DataUtil.ultimoDiaDoMes(mes.getNumeroMes());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, diaMes);
        calendar.set(Calendar.MONTH, mes.getNumeroMes() - 1);
        calendar.set(Calendar.YEAR, getSistemaControlador().getExercicioCorrente().getAno());
        return DataUtil.getDataFormatada(calendar.getTime());
    }

    private String getAsUltimaDataDiaAnoPorExercicioCorrente() {
        return DataUtil.getDataFormatada(DataUtil.getDataUltimoDiaAnoFromExercicioCorrente());
    }

    private void validarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (getFonteInicial() != null
            && getFonteFinal() != null
            && (Integer.valueOf(getFonteFinal().getCodigo()) < Integer.valueOf(getFonteInicial().getCodigo()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código da Fonte de Recurso Final deve ser maior ou igual a Fonte de Recurso Inicial");
        }

        ve.lancarException();
    }

    public FonteDeRecursos getFonteInicial() {
        return fonteInicial;
    }

    public void setFonteInicial(FonteDeRecursos fonteInicial) {
        this.fonteInicial = fonteInicial;
    }

    public FonteDeRecursos getFonteFinal() {
        return fonteFinal;
    }

    public void setFonteFinal(FonteDeRecursos fonteFinal) {
        this.fonteFinal = fonteFinal;
    }

}
