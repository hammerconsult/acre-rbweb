/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
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
import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author reidocrime
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-previsao-inicial-receita", pattern = "/relatorio/planejamento/demonstrativo-previsao-inicial-receita", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativoprevisaoinicialdareceita.xhtml")
})
public class RelatorioDemonstrativoPrevisaoInicialReceitaControlador extends RelatorioContabilSuperControlador {

    private FonteDeRecursos fonteInicial;
    private FonteDeRecursos fonteFinal;
    private EsferaOrcamentaria esferaOrcamentaria;

    public List<SelectItem> getListaEsferaOrcamentaria() {
        return Util.getListSelectItem(EsferaOrcamentaria.values());
    }

    @URLAction(mappingId = "demonstrativo-previsao-inicial-receita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        esferaOrcamentaria = EsferaOrcamentaria.ORCAMENTOGERAL;
    }

    public void gerarRelatorio() throws IOException, JRException {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.setNomeRelatorio("Demonstrativo da Previsão Inicial da Receita por Lançamento");
            dto.setApi("contabil/demonstrativo-previsao-inicial-receita/");
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

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, null, DataUtil.getDataFormatada(getSistemaControlador().getDataOperacao()), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
        if (listaUnidades.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            parametros.add(new ParametrosRelatorios(" vwsub.subordinada_id", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        }
        if (fonteInicial != null && fonteFinal != null) {
            parametros.add(new ParametrosRelatorios(" fr.codigo ", ":codigoFonteInicial", ":codigoFonteFinal", OperacaoRelatorio.BETWEEN, fonteInicial.getCodigo(), fonteFinal.getCodigo(), 1, false));
        }
        if (!EsferaOrcamentaria.ORCAMENTOGERAL.equals(esferaOrcamentaria)) {
            parametros.add(new ParametrosRelatorios(" rlf.esferaorcamentaria ", ":esferaOrcamentaria", null, OperacaoRelatorio.LIKE, esferaOrcamentaria.name(), null, 1, false));
        }
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        return "demonstrativo-previsao-inicial-receita";
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
}
