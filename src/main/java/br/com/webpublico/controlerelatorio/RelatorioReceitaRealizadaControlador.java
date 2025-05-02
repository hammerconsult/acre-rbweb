/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.LoteBaixa;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean(name = "relatorioReceitaRealizadaControlador")
@ViewScoped
@URLMapping(id = "relatorio-receita-realizada", pattern = "/relatorio/receita-realizada/", viewId = "/faces/financeiro/relatorio/relatorioreceitarealizada.xhtml")
public class RelatorioReceitaRealizadaControlador extends RelatorioContabilSuperControlador implements Serializable {

    private ConverterAutoComplete converterLoteBaixa;
    private String numeroInicial;
    private String numeroFinal;
    private Conta contaReceitaInicial;
    private Conta contaReceitaFinal;
    private LoteBaixa loteBaixa;
    private Boolean somenteReceitaTributario;
    private OperacaoReceita operacaoReceita;

    public RelatorioReceitaRealizadaControlador() {
    }

    @URLAction(mappingId = "relatorio-receita-realizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        numeroInicial = null;
        numeroFinal = null;
        contaReceitaInicial = null;
        contaReceitaFinal = null;
        loteBaixa = null;
        somenteReceitaTributario = Boolean.FALSE;
        tipoLancamento = null;
    }

    public List<SelectItem> getTiposLancamento() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        for (TipoLancamento lancamento : TipoLancamento.values()) {
            retorno.add(new SelectItem(lancamento, lancamento.getDescricao()));
        }
        return retorno;
    }

    public String getCaminhoRelatorio() {
        return "/relatorio/receita-realizada/";
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getNomeUsuarioLogado());
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("MODULO", "Execução Orçamentária");
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/receita-realizada/");
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
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtrosGerais(parametros);
        adicionarExercicioParaApresentacao(parametros);
        filtrosParametrosUnidade(parametros);
        return parametros;
    }

    private void adicionarExercicioParaApresentacao(List<ParametrosRelatorios> listaParametros) {
        if (apresentacao.isApresentacaoUnidadeGestora() || unidadeGestora != null) {
            listaParametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
        }
    }

    private List<ParametrosRelatorios> filtrosGerais(List<ParametrosRelatorios> parametros) {

        parametros.add(new ParametrosRelatorios(" trunc(LANC.DATALANCAMENTO) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + getDataInicialFormatada() + " a " + getDataFinalFormatada() + " -";

        if (eventoContabil != null) {
            parametros.add(new ParametrosRelatorios(" ec.id ", ":eventoId", null, OperacaoRelatorio.IGUAL, eventoContabil.getId(), null, 1, false));
            filtros += " Evento Contabil: " + eventoContabil.getCodigo() + " - " + eventoContabil.getDescricao() + " -";
        }
        if (!Strings.isNullOrEmpty(numeroInicial) && !Strings.isNullOrEmpty(numeroFinal)) {
            parametros.add(new ParametrosRelatorios(" lanc.numero  ", ":numInicial", ":numFinal", OperacaoRelatorio.BETWEEN, numeroInicial, numeroFinal, 1, false));
            filtros += " Número Inicial: " + numeroInicial + " Número Final: " + numeroFinal + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" lanc.pessoa_id ", ":pessoaId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" lanc.classecredor_id ", ":classeCredorID", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe Credor: " + classeCredor.getDescricao() + " -";
        }
        if (contaReceitaInicial != null && contaReceitaFinal != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":contaInicial", ":contaFinal", OperacaoRelatorio.BETWEEN, contaReceitaInicial.getCodigo(), contaReceitaFinal.getCodigo(), 1, false));
            filtros += " Conta de Receita Inicial: " + contaReceitaInicial.getCodigo() + " - Conta de Receita Final: " + contaReceitaFinal.getCodigo() + " -";
        }
        if (contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" cbe.ID ", ":cbeId", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 1, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroConta() + " - " + contaBancariaEntidade.getDigitoVerificador() + " -";
        }
        if (contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" SUB.CODIGO ", ":codigo", null, OperacaoRelatorio.IGUAL, contaFinanceira.getCodigo(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " - " + contaFinanceira.getDescricao() + " -";
        }
        if (loteBaixa != null) {
            parametros.add(new ParametrosRelatorios(" lanc.lote", ":codigoLote", null, OperacaoRelatorio.LIKE, loteBaixa.getCodigoLote(), null, 1, false));
            filtros += " Lote: " + loteBaixa.getCodigoLote() + " -";
        }
        if (somenteReceitaTributario) {
            parametros.add(new ParametrosRelatorios(" lanc.INTEGRACAOTRIBCONT_ID ", "", null, OperacaoRelatorio.IS_NOT_NULL, null, null, 1, false));
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" f.id", ":fonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" FonteDeRecursos.id ", ":fonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 2, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" cd.id", ":cdest", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" cd.id", ":cdest", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 2, false));
            filtros += " Conta de Destinação de Recurso: " + conta.getCodigo() + " -";
        }
        if (operacaoReceita != null) {
            parametros.add(new ParametrosRelatorios(" lanc.operacaoReceitaRealizada ", ":operacao", null, OperacaoRelatorio.IGUAL, operacaoReceita.name(), null, 1, false));
            filtros += " Operação: " + operacaoReceita.getDescricao() + " -";
        }
        if (tipoLancamento != null) {
            parametros.add(new ParametrosRelatorios(" tipoLancamento ", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.getAbreviacao(), null, 3, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    public List<SelectItem> getListaOperacoesReceitaRealizada() {
        return Util.getListSelectItem(OperacaoReceita.values());
    }


    public void atribuirContaBancaria() {
        if (contaFinanceira != null) {
            contaBancariaEntidade = contaFinanceira.getContaBancariaEntidade();
        } else {
            contaBancariaEntidade = null;
        }
    }

    public List<ClasseCredor> completarClasseCredor(String parte) {
        if (pessoa != null) {
            return relatorioContabilSuperFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), getPessoa());
        } else {
            return relatorioContabilSuperFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
        }
    }

    public List<EventoContabil> completarEventoContabil(String parte) {
        return relatorioContabilSuperFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.RECEITA_REALIZADA, TipoLancamento.NORMAL);
    }

    public List<LoteBaixa> completarLoteBaixa(String parte) {
        return relatorioContabilSuperFacade.getLoteBaixaFacade().completaLotePorPeriodo(parte.trim(), dataInicial, dataFinal);
    }

    public List<Conta> completarConta(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<Conta> completarContaDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-RECEITA-REALIZADA";
    }


    public ConverterAutoComplete getConverterLoteBaixa() {
        if (converterLoteBaixa == null) {
            converterLoteBaixa = new ConverterAutoComplete(LoteBaixa.class, relatorioContabilSuperFacade.getLoteBaixaFacade());
        }
        return converterLoteBaixa;
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

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public OperacaoReceita getOperacaoReceita() {
        return operacaoReceita;
    }

    public void setOperacaoReceita(OperacaoReceita operacaoReceita) {
        this.operacaoReceita = operacaoReceita;
    }
}
