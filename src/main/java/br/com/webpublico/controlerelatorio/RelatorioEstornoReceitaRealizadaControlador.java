/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Edi
 */
@ManagedBean(name = "relatorioEstornoReceitaRealizadaControlador")
@ViewScoped
@URLMapping(id = "relatorio-estorno-receita-realizada", pattern = "/relatorio/estorno-receita-realizada/", viewId = "/faces/financeiro/relatorio/relatorioestornoreceitarealizada.xhtml")
public class RelatorioEstornoReceitaRealizadaControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    private String numeroInicial;
    private String numeroFinal;
    private String codigoLote;
    private Conta contaReceitaInicial;
    private Conta contaReceitaFinal;
    private LancamentoReceitaOrc lancamentoReceitaOrc;
    private LoteBaixa loteBaixa;
    private Boolean somenteReceitaTributario;
    private OperacaoReceita operacaoReceita;


    @URLAction(mappingId = "relatorio-estorno-receita-realizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.dataInicial = new Date();
        this.dataFinal = new Date();
        this.numeroInicial = null;
        this.numeroFinal = null;
        this.pessoa = null;
        this.classeCredor = null;
        this.contaReceitaInicial = null;
        this.contaReceitaFinal = null;
        this.contaFinanceira = null;
        this.eventoContabil = null;
        this.codigoLote = "";
        this.lancamentoReceitaOrc = null;
        this.listaUnidades = new ArrayList<>();
        this.somenteReceitaTributario = Boolean.FALSE;

    }

    public String getCaminhoRelatorio() {
        return "/relatorio/estorno-receita-realizada/";
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Execução Orçamentária");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/estorno-receita-realizada/");
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
        filtrosGerais(parametros);
        adicionarExercicioParaApresentacao(parametros);
        filtrosParametrosUnidade(parametros);
        return parametros;
    }

    private void adicionarExercicioParaApresentacao(List<ParametrosRelatorios> listaParametros) {
        if (apresentacao.equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
            listaParametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
        }
    }

    private void filtrosGerais(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(" trunc(est.dataestorno) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + getDataInicialFormatada() + " a " + getDataFinalFormatada() + " -";

        if (this.eventoContabil != null) {
            parametros.add(new ParametrosRelatorios(" ec.id ", ":eventoId", null, OperacaoRelatorio.IGUAL, eventoContabil.getId(), null, 1, false));
            filtros += " Evento Contabil: " + eventoContabil.getCodigo() + " - " + eventoContabil.getDescricao() + " -";
        }
        if (this.lancamentoReceitaOrc != null) {
            parametros.add(new ParametrosRelatorios(" lanc.id ", ":idReceita", null, OperacaoRelatorio.IGUAL, lancamentoReceitaOrc.getId(), null, 1, false));
            filtros += " Receita: " + lancamentoReceitaOrc.getNumero() + " -";
        }
        if (this.numeroInicial != null && !this.numeroInicial.equals("")
            && this.numeroFinal != null && !this.numeroFinal.equals("")) {
            parametros.add(new ParametrosRelatorios(" est.numero  ", ":numInicial", ":numFinal", OperacaoRelatorio.BETWEEN, numeroInicial, numeroFinal, 1, false));
            filtros += " Número inicial: " + numeroInicial + " Número final: " + numeroFinal + " -";
        }
        if (this.pessoa != null) {
            parametros.add(new ParametrosRelatorios(" est.pessoa_id ", ":pessoaId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (this.classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" est.classecredor_id ", ":classeCredorID", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe Credor: " + classeCredor.getDescricao() + " -";
        }
        if (this.contaReceitaInicial != null && this.contaReceitaFinal != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":contaInicial", ":contaFinal", OperacaoRelatorio.BETWEEN, contaReceitaInicial.getCodigo(), contaReceitaFinal.getCodigo(), 1, false));
            filtros += " Conta de Receita Inicial: " + contaReceitaInicial.getCodigo() + " - Conta de Receita Final: " + contaReceitaFinal.getCodigo() + " -";
        }
        if (this.contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" cbe.ID ", ":cbeId", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 1, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroConta() + " - " + contaBancariaEntidade.getDigitoVerificador() + " -";
        }
        if (this.contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" SUB.CODIGO ", ":codigo", null, OperacaoRelatorio.IGUAL, contaFinanceira.getCodigo(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " - " + contaFinanceira.getDescricao() + " -";
        }
        if (!this.codigoLote.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" est.lote", ":codigoLote", null, OperacaoRelatorio.LIKE, codigoLote, null, 1, false));
            filtros += " Lote: " + codigoLote + " -";
        }
        if (somenteReceitaTributario) {
            parametros.add(new ParametrosRelatorios(" est.INTEGRACAOTRIBCONT_ID ", "", null, OperacaoRelatorio.IS_NOT_NULL, null, null, 1, false));
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" f.id ", ":fonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" f.id ", ":fonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 2, false));
            filtros += " Destinação de Recurso: " + fonteDeRecursos.toString() + " -";
        }
        if (operacaoReceita != null) {
            parametros.add(new ParametrosRelatorios(" est.operacaoReceitaRealizada ", ":operacao", null, OperacaoRelatorio.IGUAL, operacaoReceita.name(), null, 1, false));
            filtros += " Operação: " + operacaoReceita.getDescricao() + " -";
        }
        atualizaFiltrosGerais();
    }

    public void atualizarContaBancaria() {
        if (contaFinanceira != null && contaFinanceira.getContaBancariaEntidade() != null)
            contaBancariaEntidade = contaFinanceira.getContaBancariaEntidade();
    }

    public void atualizarCodigoLote() {
        if (loteBaixa != null && loteBaixa.getCodigoLote() != null)
            codigoLote = loteBaixa.getCodigoLote();
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public List<LancamentoReceitaOrc> completarReceitasRealizadas(String parte) {
        try {
            return lancamentoReceitaOrcFacade.listaReceitaRealizadaPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<ClasseCredor> completarClassesCredores(String parte) {
        List<ClasseCredor> lista = new ArrayList<>();
        if (getPessoa() != null) {
            lista = classeCredorFacade.buscarClassesPorPessoa(parte.trim(), getPessoa());
        } else {
            lista = classeCredorFacade.listaFiltrandoDescricao(parte.trim());
        }
        return lista;
    }

    public List<EventoContabil> completarEventosContabeis(String parte) {
        return eventoContabilFacade.listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.RECEITA_REALIZADA, TipoLancamento.ESTORNO);
    }

    public List<LoteBaixa> completarLotesBaixas(String parte) {
        return loteBaixaFacade.completaLotePorPeriodo(parte.trim(), dataInicial, dataFinal);
    }

    public List<Conta> completarContas(String parte) {
        return contaFacade.listaFiltrandoReceita(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-ESTORNO-RECEITA-REALIZADA";
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public Conta getContaReceitaInicial() {
        return contaReceitaInicial;
    }

    public void setContaReceitaInicial(Conta contaReceitaInicial) {
        this.contaReceitaInicial = contaReceitaInicial;
    }

    public Conta getContaReceitaFinal() {
        return contaReceitaFinal;
    }

    public void setContaReceitaFinal(Conta contaReceitaFinal) {
        this.contaReceitaFinal = contaReceitaFinal;
    }

    public Boolean getSomenteReceitaTributario() {
        return somenteReceitaTributario;
    }

    public void setSomenteReceitaTributario(Boolean somenteReceitaTributario) {
        this.somenteReceitaTributario = somenteReceitaTributario;
    }

    public LancamentoReceitaOrc getLancamentoReceitaOrc() {
        return lancamentoReceitaOrc;
    }

    public void setLancamentoReceitaOrc(LancamentoReceitaOrc lancamentoReceitaOrc) {
        this.lancamentoReceitaOrc = lancamentoReceitaOrc;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public OperacaoReceita getOperacaoReceita() {
        return operacaoReceita;
    }

    public void setOperacaoReceita(OperacaoReceita operacaoReceita) {
        this.operacaoReceita = operacaoReceita;
    }
}
