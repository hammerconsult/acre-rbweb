package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ReceitaLOA;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ReceitaLOAFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
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
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 06/01/14
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-previsao-adicional-receita", pattern = "/relatorio/previsao-adicional-da-receita/", viewId = "/faces/financeiro/relatorio/relatorio-previsao-adicional-receita.xhtml")
})
@ManagedBean
public class RelatorioPrevisaoAdicionalDaReceitaControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    private AtoLegal atoLegal;
    private ReceitaLOA contaReceita;
    private FonteDeRecursos fonteDeRecursos;
    private TipoDespesaORC tipoDespesaORC;
    private TipoAlteracaoORC operacao;
    private OrigemReceitaAlteracaoORC origemRecurso;

    @URLAction(mappingId = "relatorio-previsao-adicional-receita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        atoLegal = null;
        contaReceita = null;
        fonteDeRecursos = null;
        tipoDespesaORC = null;
        operacao = null;
        origemRecurso = null;
    }

    public void gerarRelatorio() {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            dto.adicionarParametro("FILTRO_DATA", getFiltrosDatas());
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
            if (unidadeGestora != null) {
                dto.adicionarParametro("FILTRO_UG", filtroUG);
            }
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/previsao-adicional-receita/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve){
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
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> parametros) {
        if (atoLegal != null) {
            parametros.add(new ParametrosRelatorios(" ATO.NUMERO", ":codigoAto", null, OperacaoRelatorio.LIKE, atoLegal.getNumero(), null, 1, false));
            filtros += " Ato Legal: " + atoLegal.getNumero() + " - " + DataUtil.getDataFormatada(atoLegal.getDataPublicacao()) + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" FONTE.CODIGO", ":codigoFonte", null, OperacaoRelatorio.LIKE, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString().trim() + " -";
        }
        if (contaReceita != null) {
            parametros.add(new ParametrosRelatorios(" C.CODIGO", ":codigoConta", null, OperacaoRelatorio.LIKE, contaReceita.getContaDeReceita().getCodigo(), null, 1, false));
            filtros += " Conta: " + contaReceita.getContaDeReceita() + " -";
        }
        if (operacao != null) {
            parametros.add(new ParametrosRelatorios(" RECEITA.TIPOALTERACAOORC", ":operacao", null, OperacaoRelatorio.IGUAL, operacao.name(), null, 1, false));
            filtros += " Operação: " + operacao.getDescricao() + " -";
        }
        if (tipoDespesaORC != null) {
            parametros.add(new ParametrosRelatorios(" alt.tipodespesaORC", ":tipoCredito", null, OperacaoRelatorio.IGUAL, tipoDespesaORC.name(), null, 1, false));
            filtros += " Tipo de Crédito: " + tipoDespesaORC.getDescricao() + " -";
        }
        if (origemRecurso != null) {
            parametros.add(new ParametrosRelatorios(" RECEITA.ORIGEMRECEITAALTERACAOORC", ":orgiemRecurso", null, OperacaoRelatorio.IGUAL, origemRecurso.name(), null, 1, false));
            filtros += " Origem de Recurso: " + origemRecurso.getDescricao() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    public List<SelectItem> getTipoCredito() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoDespesaORC obj : TipoDespesaORC.values()) {
            if (!obj.equals(TipoDespesaORC.ORCAMENTARIA)) {
                toReturn.add(new SelectItem(obj, obj.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todas"));
        for (TipoAlteracaoORC obj : TipoAlteracaoORC.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOrigemRecursos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todas"));
        for (OrigemReceitaAlteracaoORC obj : OrigemReceitaAlteracaoORC.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<ReceitaLOA> completarContasReceita(String parte) {
        return dataFinal != null ? receitaLOAFacade.listaReceitaLoaPorExercicioUnidadesUsuarioLogado(parte.trim(),
            buscarExercicioPelaDataFinal(), getSistemaControlador().getUsuarioCorrente(), dataFinal) : new ArrayList<ReceitaLOA>();
    }

    public void limparContaReceita (){
        contaReceita = null;
    }

    @Override
    public List<ParametrosRelatorios> filtrosParametrosUnidade(List<ParametrosRelatorios> listaParametros) {
        List<Long> listaIdsUnidades = new ArrayList<>();
        if (!listaUnidades.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            filtros += " Unidade(s): " + unidades;

        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), buscarExercicioPelaDataFinal(), dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        } else {
            listaParametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            adicionarExercicio(listaParametros);
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
            filtroUG = unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao();
        }
        return listaParametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
    }

    @Override
    public String getNomeRelatorio() {
        return "RELACAO-PREVISAO-ADICIONAL-RECEITA";
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public TipoDespesaORC getTipoDespesaORC() {
        return tipoDespesaORC;
    }

    public void setTipoDespesaORC(TipoDespesaORC tipoDespesaORC) {
        this.tipoDespesaORC = tipoDespesaORC;
    }

    public TipoAlteracaoORC getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoAlteracaoORC operacao) {
        this.operacao = operacao;
    }

    public OrigemReceitaAlteracaoORC getOrigemRecurso() {
        return origemRecurso;
    }

    public void setOrigemRecurso(OrigemReceitaAlteracaoORC origemRecurso) {
        this.origemRecurso = origemRecurso;
    }

    public ReceitaLOA getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ReceitaLOA contaReceita) {
        this.contaReceita = contaReceita;
    }
}
