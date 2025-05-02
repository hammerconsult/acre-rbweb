package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RelatorioAtoPotencial;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioAtoPotencialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-ato-potencial", pattern = "/relatorio/ato-potencial/", viewId = "/faces/financeiro/relatorio/relatorio-ato-potencial.xhtml")
})
public class RelatorioAtoPotencialControlador extends RelatorioContabilSuperControlador {

    @EJB
    private RelatorioAtoPotencialFacade relatorioAtoPotencialFacade;
    @Enumerated(EnumType.STRING)
    private TipoAtoPotencial tipoAtoPotencial;
    @Enumerated(EnumType.STRING)
    private TipoOperacaoAtoPotencial tipoOperacaoAtoPotencial;
    private String numeroReferencia;
    private Exercicio exercicioReferencia;

    @URLAction(mappingId = "relatorio-ato-potencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        tipoLancamento = null;
    }

    public void gerarRelatorio() {
        try {
            validarDatas();
            String arquivoJasper = "RelatorioAtosPotenciais.jasper";
            HashMap parameters = new HashMap();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarDados());
            parameters.put("USER", getSistemaControlador().getUsuarioCorrente().getNome());
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            parameters.put("NOME_RELATORIO", "Relatório de Atos Potenciais");
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("FILTRO", filtros);
            parameters.put("APRESENTACAO", apresentacao.name());
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeRelatorio(), arquivoJasper, parameters, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getTiposDeLancamento() {
        return Util.getListSelectItem(TipoLancamento.values(), false);
    }

    public List<SelectItem> getTiposDeAtosPotenciais() {
        return Util.getListSelectItem(TipoAtoPotencial.values(), false);
    }

    public List<SelectItem> getTiposDeOperacaoAtoPotencial() {
        return Util.getListSelectItem(TipoOperacaoAtoPotencial.values(), false);
    }

    public List<EventoContabil> completarEventosContabeis(String parte) {
        return relatorioAtoPotencialFacade.getEventoContabilFacade().buscarEventosContabeisPorTipoEvento(parte.trim(), TipoEventoContabil.ATO_POTENCIAL);
    }

    private List<RelatorioAtoPotencial> buscarDados() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        filtrosParametrosUnidade(parametros);

        if (tipoLancamento != null) {
            parametros.add(new ParametrosRelatorios(" ap.tipoLancamento ", ":tipoLanc", null, OperacaoRelatorio.IGUAL, tipoLancamento.name(), null, 1, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        if (tipoAtoPotencial != null) {
            parametros.add(new ParametrosRelatorios(" ap.tipoAtoPotencial ", ":tipoAto", null, OperacaoRelatorio.IGUAL, tipoAtoPotencial.name(), null, 1, false));
            filtros += " Tipo de Ato Potencial: " + tipoAtoPotencial.getDescricao() + " -";
        }
        if (tipoOperacaoAtoPotencial != null) {
            parametros.add(new ParametrosRelatorios(" ap.tipoOperacaoAtoPotencial ", ":tipoOperacao", null, OperacaoRelatorio.IGUAL, tipoOperacaoAtoPotencial.name(), null, 1, false));
            filtros += " Operação: " + tipoOperacaoAtoPotencial.getDescricao() + " -";
        }
        if (eventoContabil != null) {
            parametros.add(new ParametrosRelatorios(" ap.eventoContabil_id ", ":evento", null, OperacaoRelatorio.IGUAL, eventoContabil.getId(), null, 1, false));
            filtros += " Evento Contábil: " + eventoContabil.getCodigo() + " -";
        }
        if (numeroReferencia != null && !numeroReferencia.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" ap.numeroReferencia ", ":numero", null, OperacaoRelatorio.LIKE, numeroReferencia, null, 1, false));
            filtros += " Número de Referência: " + numeroReferencia + " -";
        }
        if (exercicioReferencia != null) {
            parametros.add(new ParametrosRelatorios(" ap.exercicioReferencia_id ", ":exercicio", null, OperacaoRelatorio.IGUAL, exercicioReferencia.getId(), null, 1, false));
            filtros += " Exercício de Referência: " + exercicioReferencia + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" p.id ", ":pessoaId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" classe.id ", ":classeId", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe: " + classeCredor.getCodigo() + " -";
        }
        if (ApresentacaoRelatorio.UNIDADE_GESTORA.equals(apresentacao)) {
            adicionarExercicio(parametros);
        }
        atualizaFiltrosGerais();
        return relatorioAtoPotencialFacade.buscarDados(parametros, unidadeGestora != null, apresentacao);
    }

    public List<ClasseCredor> completarClasseCredor(String parte) {
        return relatorioAtoPotencialFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
    }

    public TipoAtoPotencial getTipoAtoPotencial() {
        return tipoAtoPotencial;
    }

    public void setTipoAtoPotencial(TipoAtoPotencial tipoAtoPotencial) {
        this.tipoAtoPotencial = tipoAtoPotencial;
    }

    public TipoOperacaoAtoPotencial getTipoOperacaoAtoPotencial() {
        return tipoOperacaoAtoPotencial;
    }

    public void setTipoOperacaoAtoPotencial(TipoOperacaoAtoPotencial tipoOperacaoAtoPotencial) {
        this.tipoOperacaoAtoPotencial = tipoOperacaoAtoPotencial;
    }

    public String getNumeroReferencia() {
        return numeroReferencia;
    }

    public void setNumeroReferencia(String numeroReferencia) {
        this.numeroReferencia = numeroReferencia;
    }

    public Exercicio getExercicioReferencia() {
        return exercicioReferencia;
    }

    public void setExercicioReferencia(Exercicio exercicioReferencia) {
        this.exercicioReferencia = exercicioReferencia;
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-ATO-POTENCIAL";
    }
}
