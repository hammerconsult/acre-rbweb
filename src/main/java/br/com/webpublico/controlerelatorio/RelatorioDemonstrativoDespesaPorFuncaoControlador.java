package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.Funcao;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mateus
 * @since 29/10/2015 10:05
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-despesa-funcao", pattern = "/relatorio/planejamento/demonstrativo-despesa-funcao/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativodespesafuncao.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoDespesaPorFuncaoControlador extends RelatorioContabilSuperControlador {
    private FonteDeRecursos fonteInicial;
    private FonteDeRecursos fonteFinal;
    private Funcao funcaoInicial;
    private Funcao funcaoFinal;
    private EsferaOrcamentaria esferaOrcamentaria;

    public RelatorioDemonstrativoDespesaPorFuncaoControlador() {
    }

    public List<SelectItem> getListaEsferaOrcamentaria() {
        return Util.getListSelectItem(EsferaOrcamentaria.values());
    }

    @URLAction(mappingId = "demonstrativo-despesa-funcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        esferaOrcamentaria = EsferaOrcamentaria.ORCAMENTOGERAL;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-despesa-funcao/");
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

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, null, DataUtil.getDataFormatada(getSistemaControlador().getDataOperacao()), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));

        if (fonteInicial != null && fonteFinal != null) {
            parametros.add(new ParametrosRelatorios(" fr.codigo ", ":codigoFonteInicial", ":codigoFonteFinal", OperacaoRelatorio.BETWEEN, fonteInicial.getCodigo(), fonteFinal.getCodigo(), 1, false));
        }
        if (funcaoInicial != null && funcaoFinal != null) {
            parametros.add(new ParametrosRelatorios(" func.codigo ", ":codigoFuncaoInicial", ":codigoFuncaoFinal", OperacaoRelatorio.BETWEEN, funcaoInicial.getCodigo(), funcaoFinal.getCodigo(), 1, false));
        }
        if (!esferaOrcamentaria.equals(EsferaOrcamentaria.ORCAMENTOGERAL)) {
            parametros.add(new ParametrosRelatorios(" provfonte.esferaorcamentaria ", ":esferaOrcamentaria", null, OperacaoRelatorio.LIKE, esferaOrcamentaria.name(), null, 1, false));
        }
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        return "demonstrativo-despesa-funcao";
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

    public EsferaOrcamentaria getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(EsferaOrcamentaria esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public Funcao getFuncaoInicial() {
        return funcaoInicial;
    }

    public void setFuncaoInicial(Funcao funcaoInicial) {
        this.funcaoInicial = funcaoInicial;
    }

    public Funcao getFuncaoFinal() {
        return funcaoFinal;
    }

    public void setFuncaoFinal(Funcao funcaoFinal) {
        this.funcaoFinal = funcaoFinal;
    }
}
