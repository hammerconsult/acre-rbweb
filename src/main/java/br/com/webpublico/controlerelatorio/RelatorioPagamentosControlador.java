package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.ComponenteTreeDespesaORC;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioPagamento;
import br.com.webpublico.entidadesauxiliares.PagamentoItem;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.relatoriofacade.RelatorioPagamentosFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by mateus on 28/09/17.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-pagamentos", pattern = "/relatorio/pagamentos/", viewId = "/faces/financeiro/relatorio/relatoriopagamentos.xhtml")
}
)
@ManagedBean
public class RelatorioPagamentosControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private RelatorioPagamentosFacade relatorioPagamentosFacade;
    private Empenho empenho;
    private Liquidacao liquidacao;
    private DespesaORC despesaORC;
    private ContaExtraorcamentaria contaExtraorcamentaria;
    private FonteDespesaORC fonteDespesaORC;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private ConverterAutoComplete converterFonteDespesaORC;
    @Enumerated(EnumType.STRING)
    private Ordenacao ordenacao;
    private Boolean exibirPagamentoAbertos;

    public RelatorioPagamentosControlador() {
    }

    @URLAction(mappingId = "relatorio-pagamentos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        this.dataInicial = DataUtil.montaData(1, 0, getSistemaFacade().getExercicioCorrente().getAno()).getTime();
        this.empenho = null;
        this.liquidacao = null;
        this.exibirPagamentoAbertos = Boolean.FALSE;
        this.ordenacao = Ordenacao.NUMERO;
        this.fonteDespesaORC = null;
        this.contaExtraorcamentaria = null;
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
    }

    public List<SelectItem> getOrdenacoes() {
        return Util.getListSelectItemSemCampoVazio(Ordenacao.values());
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values(), false);
    }

    public List<Conta> completarContasDespesa(String parte) {
        return relatorioPagamentosFacade.getContaFacade().listaFiltrandoDespesaCriteria(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<Empenho> completarEmpenho(String parte) {
        return relatorioPagamentosFacade.getPagamentoFacade().getEmpenhoFacade().buscarPorNumeroAndPessoaRelatorio(parte.trim(), getSistemaControlador().getExercicioCorrente().getAno());
    }

    public List<Liquidacao> completarLiquidacao(String parte) {
        if (empenho != null && empenho.getId() != null) {
            return relatorioPagamentosFacade.getPagamentoFacade().getLiquidacaoFacade().listaPorEmpenho(empenho);
        } else {
            return relatorioPagamentosFacade.getPagamentoFacade().getLiquidacaoFacade().buscarLiquidacaoNormalPorExercicioRelatorio(parte.trim(), getSistemaControlador().getExercicioCorrente().getAno());
        }
    }

    public List<Pessoa> completarPessoas(String parte) {
        return relatorioPagamentosFacade.getPessoaFacade().listaTodasPessoasPorId(parte.trim());
    }

    public List<ClasseCredor> completarClasseCredor(String parte) {
        return relatorioPagamentosFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
    }

    public List<ContaBancariaEntidade> completarContaBancariaEntidade(String parte) {
        return relatorioPagamentosFacade.getContaBancariaEntidadeFacade().listaFiltrandoPorUnidadeDoUsuario(parte.trim(), getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getDataOperacao());
    }

    public List<SubConta> completarSubContas(String parte) {
        return relatorioPagamentosFacade.getContaBancariaEntidadeFacade().getSubContaFacade().listaPorContaBancariaEntidadeDoUsuarioLogadoOuTodas(parte.trim(), contaBancariaEntidade, getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getDataOperacao());
    }

    public List<ContaExtraorcamentaria> completarContaExtraorcamentaria(String parte) {
        return relatorioPagamentosFacade.getContaFacade().listaFiltrandoExtraorcamentario(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public void gerarRelatorio() {
        try {
            validarDatas();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarDados());
            parameters = getParametrosPadrao();
            parameters.putAll(getFiltrosPadrao());
            parameters.put("APRESENTACAO", apresentacao.name());
            parameters.put("EXIBIRTOTALPESSOA", Ordenacao.CREDOR_NUMERO.equals(ordenacao));
            String nomeArquivo = "RelatorioPagamentos.jasper";
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeRelatorio(), nomeArquivo, parameters, ds);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private List<PagamentoItem> buscarDados() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        filtrosParametrosUnidade(parametros);
        if (!exibirPagamentoAbertos) {
            parametros.add(new ParametrosRelatorios(" PGTO.STATUS ", ":status", null, OperacaoRelatorio.DIFERENTE, StatusPagamento.ABERTO.name(), null, 1, false));
        }

        if (this.empenho != null) {
            parametros.add(new ParametrosRelatorios(" EMP.ID ", ":empId", null, OperacaoRelatorio.IGUAL, empenho.getId(), null, 1, false));
            filtros += " Empenho:  " + empenho.getNumero() + " -";
        }

        if (this.liquidacao != null) {
            parametros.add(new ParametrosRelatorios(" L.ID ", ":liqId", null, OperacaoRelatorio.IGUAL, liquidacao.getId(), null, 1, false));
            filtros += " Liquidação:  " + liquidacao + " -";
        }
        if (this.pessoa != null) {
            parametros.add(new ParametrosRelatorios(" P.ID ", ":fornecedorId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa + " -";
        }

        if (this.classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" cc.ID ", ":classeId", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe:  " + classeCredor.getCodigo() + " -";
        }

        if (this.contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" cbe.ID ", ":contaBancId", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 1, false));
            filtros += " Conta Bancária:  " + contaBancariaEntidade.getNumeroConta() + " - " + contaBancariaEntidade.getDigitoVerificador() + " -";
        }

        if (this.contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" sc.ID ", ":subContaId", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira:  " + contaFinanceira.getCodigo() + " -";
        }

        if (this.contaExtraorcamentaria != null) {
            parametros.add(new ParametrosRelatorios(" Cextra.ID ", ":CextraId", null, OperacaoRelatorio.IGUAL, contaExtraorcamentaria.getId(), null, 1, false));
            filtros += " Conta Extraorçamentária:  " + contaExtraorcamentaria.getCodigo() + " -";
        }

        if (despesaORC != null) {
            parametros.add(new ParametrosRelatorios(" DESP.ID ", ":despesaId", null, OperacaoRelatorio.IGUAL, despesaORC.getId(), null, 1, false));
            filtros += " Despesa Orçamentária: " + despesaORC.getCodigo() + " -";
        }

        if (fonteDespesaORC != null) {
            if (fonteDespesaORC.getId() != null) {
                parametros.add(new ParametrosRelatorios(" FTD.ID ", ":fteDesp", null, OperacaoRelatorio.IGUAL, fonteDespesaORC.getId(), null, 1, false));
                filtros += " Fonte: " + fonteDespesaORC.getDescricaoFonteDeRecurso() + " -";
            }
        }

        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" con.codigo ", ":cCodigo", null, OperacaoRelatorio.IGUAL, conta.getCodigo(), null, 1, false));
            filtros += " Conta de Despesa: " + conta.getCodigo() + " -";
        }

        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" PROG.ID", ":idPrograma", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
            filtros += " Programa: " + programaPPA + " -";
        }
        if (acaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" A.ID", ":idProjetoAtividade", null, OperacaoRelatorio.IGUAL, acaoPPA.getId(), null, 1, false));
            filtros += " Projeto/Atividade: " + acaoPPA + " -";
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" F.ID", ":idFuncao", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            filtros += " Função: " + funcao + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" SF.ID", ":idSubFuncao", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            filtros += " SubFunção: " + subFuncao + " -";
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.id ", ":fonteCodigo", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (ApresentacaoRelatorio.UNIDADE_GESTORA.equals(apresentacao)) {
            adicionarExercicio(parametros);
        }
        FiltroRelatorioPagamento filtroRelatorioPagamento = new FiltroRelatorioPagamento();
        filtroRelatorioPagamento.setApresentacao(apresentacao.name());
        filtroRelatorioPagamento.setPesquisouUg(unidadeGestora != null);
        filtroRelatorioPagamento.setFiltrarContaExtra(contaExtraorcamentaria != null);
        filtroRelatorioPagamento.setParametros(parametros);
        filtroRelatorioPagamento.setOrdenacao(montarOrdenacao());
        return relatorioPagamentosFacade.buscarDados(filtroRelatorioPagamento);
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(formato.format(dataFinal)));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
    }

    private String montarOrdenacao() {
        StringBuilder sb = new StringBuilder();
        sb.append(" order by ");
        if (ApresentacaoRelatorio.UNIDADE_GESTORA.equals(apresentacao)) {
            sb.append(" codigoUg, ");
        } else if (ApresentacaoRelatorio.ORGAO.equals(apresentacao) || ApresentacaoRelatorio.UNIDADE.equals(apresentacao)) {
            sb.append(" codigoUnidade, ");
        }
        switch (ordenacao) {
            case NUMERO:
                sb.append(" pgto_numero ");
                break;
            case CREDOR_NUMERO:
                sb.append(" fornecedor, pgto_numero ");
                break;
        }

        return sb.toString();
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public List<FonteDespesaORC> completarFonteDespesaORC(String parte) {
        if (componenteTreeDespesaORC.getDespesaORCSelecionada().getId() != null) {
            return relatorioPagamentosFacade.getPagamentoFacade().getEmpenhoFacade().getFonteDespesaORCFacade().completaFonteDespesaORC(parte.trim(), componenteTreeDespesaORC.getDespesaORCSelecionada());
        } else {
            return Lists.newArrayList();
        }
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, relatorioPagamentosFacade.getPagamentoFacade().getEmpenhoFacade().getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public void atribuirNullContaFinanceira() {
        contaFinanceira = null;
    }

    public void atribuirNullContaBancariaEntidade() {
        contaFinanceira = null;
    }

    public void recuperarContaBancariaApartirDaContaFinanceira() {
        contaBancariaEntidade = getContaBancariaPelaContaFinanceira();
    }

    private ContaBancariaEntidade getContaBancariaPelaContaFinanceira() {
        try {
            return contaFinanceira.getContaBancariaEntidade();
        } catch (Exception e) {
            return new ContaBancariaEntidade();
        }
    }

    public void atribuirFonteDespesa(SelectEvent evt) {
        fonteDespesaORC = (FonteDespesaORC) evt.getObject();
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public void setConverterFonteDespesaORC(ConverterAutoComplete converterFonteDespesaORC) {
        this.converterFonteDespesaORC = converterFonteDespesaORC;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }

    public Boolean getExibirPagamentoAbertos() {
        return exibirPagamentoAbertos;
    }

    public void setExibirPagamentoAbertos(Boolean exibirPagamentoAbertos) {
        this.exibirPagamentoAbertos = exibirPagamentoAbertos;
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-PAGAMENTOS";
    }

    private enum Ordenacao {

        NUMERO("Número do Pagamento"),
        CREDOR_NUMERO("Credor e Número do Pagamento");

        private String descricao;

        private Ordenacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

}
