package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.DetalhamentoLiquidacao;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoAdministracao;
import br.com.webpublico.relatoriofacade.RelatorioDetalhamentoLiquidacaoMensalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 24/07/2015.
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-detalhamento-liquidacao-mensal", pattern = "/relatorio/detalhamento-liquidacao-mensal", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativoliquidacaomensal.xhtml")
})
@ManagedBean
public class RelatorioDetalhamentoLiquidacaoMensalControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private RelatorioDetalhamentoLiquidacaoMensalFacade facade;
    private Mes mesInicial;
    private Mes mesFinal;
    private FonteDeRecursos fonteDeRecursos;
    private ConverterAutoComplete converterContaDespesa;
    @Enumerated(EnumType.STRING)
    private TipoAdministracao tipoAdministracao;

    public RelatorioDetalhamentoLiquidacaoMensalControlador() {
    }

    public ConverterAutoComplete getConverterContaDespesa() {
        if (converterContaDespesa == null) {
            converterContaDespesa = new ConverterAutoComplete(Conta.class, facade.getContaFacade());
        }
        return converterContaDespesa;
    }

    public List<Conta> completaContas(String parte) {
        return facade.getContaFacade().listaFiltrandoDespesaCriteria(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    @URLAction(mappingId = "relatorio-detalhamento-liquidacao-mensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        limparCamposGeral();
    }

    private Boolean validaFiltros() {
        if (this.mesInicial.getNumeroMes() > this.mesFinal.getNumeroMes()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validação!", "O Mês Inicial não pode ser maior que o Mês Final"));
            return false;
        }
        return true;
    }

    public List<SelectItem> getListaTipoAdministracao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdministracao ta : TipoAdministracao.values()) {
            toReturn.add(new SelectItem(ta, ta.getDescricao()));
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            if (validaFiltros()) {
                String arquivoJasper = "RelatorioDetalhamentoLiquidacaoMensal.jasper";
                parameters = getParametrosPadrao();
                JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(geraDadosRelatorio());
                parameters.put("FILTRO", atualizaFiltrosGerais());
                parameters.put("APRESENTACAO", apresentacao.name());
                gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeArquivo(), arquivoJasper, parameters, ds);
                filtros = "";
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String getDiaFinalMes(Mes mesParametro) {
        if (!mesParametro.equals(Mes.EXERCICIO)
                && !mesParametro.equals(Mes.DEZEMBRO)
                && !mesParametro.equals(Mes.NOVEMBRO)
                && !mesParametro.equals(Mes.OUTUBRO)) {
            return Util.getDiasMes(mesParametro.getNumeroMes(), getSistemaControlador().getExercicioCorrente().getAno()) + "/" + "0" + mesParametro.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
        } else {
            return Util.getDiasMes(mesParametro.getNumeroMes(), getSistemaControlador().getExercicioCorrente().getAno()) + "/" + mesParametro.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
        }
    }

    public String getDiaInicialMes(Mes mesParametro) {
        if (!mesParametro.equals(Mes.EXERCICIO)
                && !mesParametro.equals(Mes.DEZEMBRO)
                && !mesParametro.equals(Mes.NOVEMBRO)
                && !mesParametro.equals(Mes.OUTUBRO)) {
            return "01/" + "0" + mesParametro.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
        } else {
            return "01/" + mesParametro.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
        }
    }

    private List<DetalhamentoLiquidacao> geraDadosRelatorio() {
        List<ParametrosRelatorios> listaParametros = new ArrayList<>();
        listaParametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDiaInicialMes(mesInicial), getDiaFinalMes(mesFinal), 0, true));
        filtros = "Período: " + mesInicial.getDescricao() + " a " + mesFinal.getDescricao() + " -";
        if (conta != null) {
            listaParametros.add(new ParametrosRelatorios(" c.id ", ":contaId", null, OperacaoRelatorio.LIKE, this.conta.getId(), null, 1, false));
            filtros += " Conta de Despesa: " + conta.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            listaParametros.add(new ParametrosRelatorios(" fonte.codigo ", ":fonteCodigo", null, OperacaoRelatorio.LIKE, this.fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (tipoAdministracao != null) {
            listaParametros.add(new ParametrosRelatorios(" vw.TIPOADMINISTRACAO ", ":tipoAdm", null, OperacaoRelatorio.LIKE, this.tipoAdministracao.name(), null, 1, false));
            filtros += " Administração: " + tipoAdministracao.getDescricao() + " -";
        }
        listaParametros = filtrosParametrosUnidade(listaParametros);
        if (unidadeGestora != null || getApresentacao().equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
            listaParametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
        }
        if (filtros.length() > 0) {
            filtros = filtros.substring(0, filtros.length() - 1);
        }
        return facade.recuperaRelatorio(listaParametros, this.unidadeGestora != null, getApresentacao().name());
    }


    @Override
    public String getNomeRelatorio() {
        return "detalhamento-liquidacao-mensal";
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }
}
