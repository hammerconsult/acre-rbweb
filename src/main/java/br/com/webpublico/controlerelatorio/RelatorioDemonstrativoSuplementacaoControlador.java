package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.OrigemSuplementacaoORC;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 28/07/15
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */

@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "demonstrativo-suplementacao", pattern = "/relatorio/demonstrativo-suplementacao/", viewId = "/faces/financeiro/relatorio/relatorio-demonstrativo-suplementacao.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoSuplementacaoControlador extends RelatorioContabilSuperControlador implements Serializable {

    private TipoDespesaORC tiposCredito;
    private OrigemSuplementacaoORC origemRecurso;

    @URLAction(mappingId = "demonstrativo-suplementacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        tiposCredito = null;
        origemRecurso = null;
    }

    public void gerarRelatorio() throws IOException, JRException {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("FILTRO_UG", getUnidadeGestoraCabecalho());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-suplementacao/");
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
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        filtros = getFiltrosPeriodo() + filtros;
        atualizaFiltrosGerais();
        return parametros;
    }


    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> listaParametros) {

        listaParametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, getDataInicialFormatada(), null, 0, true));
        listaParametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, getDataFinalFormatada(), null, 0, true));
        listaParametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));

        if (fonteDeRecursos != null) {
            listaParametros.add(new ParametrosRelatorios(" FONTE.CODIGO", ":codigoFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString().trim() + " -";
        }
        if (conta != null) {
            listaParametros.add(new ParametrosRelatorios(" C.CODIGO", ":codigoConta", null, OperacaoRelatorio.IGUAL, conta.getCodigo(), null, 1, false));
            filtros += " Conta: " + conta + " -";
        }
        if (tiposCredito != null) {
            listaParametros.add(new ParametrosRelatorios(" SU.TIPODESPESAORC", ":tipoCredito", null, OperacaoRelatorio.IGUAL, tiposCredito.name(), null, 2, false));
            filtros += " Tipo de Crédito: " + tiposCredito.getDescricao() + " -";
        }
        if (origemRecurso != null) {
            listaParametros.add(new ParametrosRelatorios(" SU.ORIGEMSUPLEMTACAO", ":orgiemRecurso", null, OperacaoRelatorio.IGUAL, origemRecurso.name(), null, 2, false));
            filtros += " Origem de Recurso: " + origemRecurso.getDescricao() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return listaParametros;
    }

    public List<SelectItem> getTiposCreditos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoDespesaORC obj : TipoDespesaORC.values()) {
            if (!obj.equals(TipoDespesaORC.ORCAMENTARIA)) {
                toReturn.add(new SelectItem(obj, obj.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getOrigemRecursos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todas"));
        for (OrigemSuplementacaoORC obj : OrigemSuplementacaoORC.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<Conta> completarContasDeDespesa(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoDespesaCriteria(parte.trim(), getSistemaFacade().getExercicioCorrente());
    }

    @Override
    public String getNomeRelatorio() {
        return "DEMONSTRATIVO-SUPLEMENTACAO";
    }

    public TipoDespesaORC getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TipoDespesaORC tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public OrigemSuplementacaoORC getOrigemRecurso() {
        return origemRecurso;
    }

    public void setOrigemRecurso(OrigemSuplementacaoORC origemRecurso) {
        this.origemRecurso = origemRecurso;
    }
}
