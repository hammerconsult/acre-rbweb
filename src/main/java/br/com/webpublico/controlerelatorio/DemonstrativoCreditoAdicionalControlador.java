package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.OrigemSuplementacaoORC;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoDespesaORC;
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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 10/01/14
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-credito-adicional-orgao-unidade", pattern = "/demonstrativo-credito-adicional/orgao-unidade/", viewId = "/faces/financeiro/relatorio/demonstrativo-credito-adicional-orgao-unidade.xhtml"),
    @URLMapping(id = "demonstrativo-credito-adicional", pattern = "/demonstrativo-credito-adicional/", viewId = "/faces/financeiro/relatorio/demonstrativo-credito-adicional.xhtml")
})
@ManagedBean
public class DemonstrativoCreditoAdicionalControlador extends RelatorioContabilSuperControlador implements Serializable {

    private AtoLegal atoLegal;
    private TipoDespesaORC tiposCredito;
    private OrigemSuplementacaoORC origemRecurso;
    private Boolean isOrgao;

    @URLAction(mappingId = "demonstrativo-credito-adicional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        atoLegal = null;
        tiposCredito = null;
        origemRecurso = null;
        isOrgao = false;
    }

    @URLAction(mappingId = "demonstrativo-credito-adicional-orgao-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposOrgaoUnidade() {
        limparCamposGeral();
        atoLegal = null;
        tiposCredito = null;
        origemRecurso = null;
        isOrgao = true;
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTRO_DATA", getFiltrosDatas());
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            dto.adicionarParametro("FILTRO_UG", getUnidadeGestoraCabecalho());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-credito-adicional/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioPorOrgaoUnidade() {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltrosOrgaoUnidade()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTRO_DATA", getFiltrosDatas());
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            dto.adicionarParametro("FILTRO_UG", getUnidadeGestoraCabecalho());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-credito-adicional-orgao-unidade/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametrosEFiltrosOrgaoUnidade() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        montarFiltrosData(parametros);
        montarFiltrosGerais(parametros);
        filtrosParametrosUnidade(parametros);
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        montarFiltrosData(parametros);
        montarFiltrosGerais(parametros);
        filtrosParametrosUnidade(parametros);
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicioFacade().getExercicioPorAno(new Integer(DataUtil.getAno(dataInicial))).getId(), null, 0, false));
    }

    private void montarFiltrosData(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, getDataInicialFormatada(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, getDataFinalFormatada(), null, 0, true));
        parametros.add(new ParametrosRelatorios(" SUB.EXERCICIO_ID ", ":EXERCICIO_ID", null, OperacaoRelatorio.IGUAL, getExercicioFacade().getExercicioPorAno(new Integer(DataUtil.getAno(dataInicial))).getId(), null, 4, false));
    }

    private List<ParametrosRelatorios> montarFiltrosGerais(List<ParametrosRelatorios> parametros) {
        if (atoLegal != null) {
            parametros.add(new ParametrosRelatorios(" ATO.NUMERO", ":codigoAto", null, OperacaoRelatorio.IGUAL, atoLegal.getNumero(), null, 5, false));
            filtros += " Ato Legal: " + atoLegal.getNumero() + " - " + DataUtil.getDataFormatada(atoLegal.getDataPublicacao()) + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" FONTE.CODIGO", ":codigoFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString().trim() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" C.CODIGO", ":codigoConta", null, OperacaoRelatorio.IGUAL, conta.getCodigo(), null, 1, false));
            filtros += " Conta: " + conta + " -";
        }
        if (tiposCredito != null) {
            parametros.add(new ParametrosRelatorios(" SUP.TIPODESPESAORC", ":tipoCredito", null, OperacaoRelatorio.IGUAL, tiposCredito.name(), null, 2, false));
            parametros.add(new ParametrosRelatorios(" ANUL.TIPODESPESAORC", ":tipoCredito", null, OperacaoRelatorio.IGUAL, tiposCredito.name(), null, 3, false));
            filtros += " Tipo de Crédito: " + tiposCredito.getDescricao() + " -";
        }
        if (origemRecurso != null) {
            parametros.add(new ParametrosRelatorios(" SUP.ORIGEMSUPLEMTACAO", ":orgiemRecurso", null, OperacaoRelatorio.IGUAL, origemRecurso.name(), null, 2, false));
            filtros += " Origem de Recurso: " + origemRecurso.getDescricao() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    public List<SelectItem> getTiposCreditos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDespesaORC obj : TipoDespesaORC.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOrigemRecursos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (OrigemSuplementacaoORC obj : OrigemSuplementacaoORC.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<Conta> completaContaDespesa(String parte) {
        try {
            List<Conta> contas = relatorioContabilSuperFacade.getContaFacade().listaFiltrandoDespesaCriteria(parte.trim(), getSistemaControlador().getExercicioCorrente());
            if (contas.isEmpty()) {
                return new ArrayList<Conta>();
            } else {
                return contas;
            }
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            return new ArrayList<Conta>();
        }
    }

    @Override
    public String getNomeRelatorio() {
        if (isOrgao) {
            return "DEMONSTRATIVO-CREDITO-ADICIONAL-ORGAO-UNIDADE";
        } else
            return "DEMONSTRATIVO-CREDITO-ADICIONAL";
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
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
