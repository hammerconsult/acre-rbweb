/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.ComponenteTreeDespesaORC;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-pagamento", pattern = "/relatorio/pagamento/", viewId = "/faces/financeiro/relatorio/relatoriofiltropagamento.xhtml")})
@ManagedBean
public class RelatorioPagamentoControlador implements Serializable {

    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private FinalidadePagamentoFacade finalidadePagamentoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    private Date dataInicial;
    private Date dataFinal;
    private ContaBancariaEntidade cbe;
    private Conta contaDespesa;
    private Empenho empenho;
    private Liquidacao liquidacao;
    private String filtros;
    private TipoRelatorio tipoRelatorio;
    private Pessoa fornecedor;
    private ProgramaPPA programaPPA;
    private AcaoPPA projetoAtividade;
    private DespesaORC despesaORC;
    private Funcao funcao;
    private SubFuncao subFuncao;
    private ClasseCredor classeCredor;
    private SubConta subConta;
    private FonteDeRecursos fonteDeRecursos;
    private Conta contaDeDestinacao;
    private ContaExtraorcamentaria contaExtraorcamentaria;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private List<HierarquiaOrganizacional> hierarquiasOrcamentarias;
    private ApresentacaoRelatorio apresentacao;
    private Ordenacao ordenacao;
    private UnidadeGestora unidadeGestora;
    private Boolean exibirPagamentoAbertos;
    private TipoLancamento tipoLancamento;
    private GeracaoRelatorio geracaoRelatorio;
    private FinalidadePagamento finalidadePagamento;
    private Contrato contrato;

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values(), false);
    }

    public List<SelectItem> getTiposLancamento() {
        return Util.getListSelectItem(TipoLancamento.values(), false);
    }

    public List<SelectItem> getGeracoesDoRelatorio() {
        return Util.getListSelectItemSemCampoVazio(GeracaoRelatorio.values());
    }

    @URLAction(mappingId = "novo-relatorio-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        dataFinal = sistemaFacade.getDataOperacao();
        dataInicial = DataUtil.montaData(1, 0, sistemaFacade.getExercicioCorrente().getAno()).getTime();
        exibirPagamentoAbertos = Boolean.FALSE;
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        tipoRelatorio = TipoRelatorio.RESUMIDO;
        ordenacao = Ordenacao.NUMERO;
        cbe = null;
        filtros = "";
        fornecedor = null;
        limparFiltros();
        geracaoRelatorio = GeracaoRelatorio.DO_EXERCICIO_E_RESTO;
    }

    public void limparFiltros() {
        contaDespesa = null;
        empenho = null;
        liquidacao = null;
        programaPPA = null;
        projetoAtividade = null;
        despesaORC = null;
        funcao = null;
        subFuncao = null;
        classeCredor = null;
        subConta = null;
        fonteDeRecursos = null;
        contaDeDestinacao = null;
        contaExtraorcamentaria = null;
        contrato = null;
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
        hierarquiasOrcamentarias = Lists.newArrayList();
    }

    public List<SelectItem> getTiposRelatorios() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorio.values(), false);
    }

    public List<SelectItem> getOrdenacoes() {
        return Util.getListSelectItemSemCampoVazio(Ordenacao.values(), false);
    }

    public List<Conta> completarContas(String parte) {
        Exercicio exDataFinal = buscarExercicioPelaDataFinal();
        if (despesaORC != null) {
            return contaFacade.buscarContasFilhasDespesaORCPorTipo(
                parte.trim(),
                despesaORC.getProvisaoPPADespesa().getContaDeDespesa(),
                exDataFinal,
                null,
                false);
        } else {
            return contaFacade.listaFiltrandoContaDespesa(parte.trim(), exDataFinal);
        }
    }

    public void limparContaDeDespesa() {
        contaDespesa = null;
    }

    public List<Empenho> completarEmpenhos(String parte) {
        return pagamentoFacade.getEmpenhoFacade().buscarPorNumeroAndPessoaRelatorio(parte.trim(), buscarExercicioPelaDataFinal().getAno());
    }

    public List<Contrato> completarContratos(String parte) {
        return contratoFacade.buscarContratos(parte.trim());
    }

    public List<Liquidacao> completarLiquidacoes(String parte) {
        if (empenho != null && empenho.getId() != null) {
            return pagamentoFacade.getLiquidacaoFacade().listaPorEmpenho(empenho);
        } else {
            return pagamentoFacade.getLiquidacaoFacade().buscarLiquidacaoNormalPorExercicioRelatorio(parte.trim(), buscarExercicioPelaDataFinal().getAno());
        }
    }

    public List<Pessoa> completarFornecedores(String parte) {
        return pessoaFacade.listaTodasPessoasPorId(parte.trim());
    }

    public List<ClasseCredor> completarClassesCredor(String parte) {
        return classeCredorFacade.listaFiltrandoDescricao(parte.trim());
    }

    public List<ContaBancariaEntidade> completarContasBancariasEntidade(String parte) {
        return contaBancariaEntidadeFacade.listaFiltrandoPorUnidadeDoUsuario(parte.trim(), sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente(), buscarExercicioPelaDataFinal(), sistemaFacade.getUsuarioCorrente(), dataFinal);
    }

    public List<SubConta> completarSubContas(String parte) {
        return subContaFacade.listaPorContaBancariaEntidadeDoUsuarioLogadoOuTodas(parte.trim(), cbe, sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente(), buscarExercicioPelaDataFinal(), sistemaFacade.getUsuarioCorrente(), dataFinal);
    }

    public List<Conta> completarContasExtraorcamentarias(String parte) {
        return contaFacade.listaFiltrandoExtraorcamentario(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        return contaFacade.buscarContasDeDestinacaoDeRecursos(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public List<ProgramaPPA> completarProgramasPorExercicio(String filtro) {
        return programaPPAFacade.buscarProgramasPorExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public List<SubAcaoPPA> completarSubAcoesPPA(String filtro) {
        return subProjetoAtividadeFacade.buscarSubProjetoAtividadePorExercicio(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    public List<AcaoPrincipal> completarAcoesPrincipais(String filtro) {
        return acaoPrincipalFacade.buscarAcaoPPAPorExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public List<Funcao> completarFuncoes(String parte) {
        return funcaoFacade.listaFiltrandoFuncao(parte);
    }

    public List<SubFuncao> completarSubFuncoes(String parte) {
        return subFuncaoFacade.listaFiltrandoSubFuncao(parte);
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public Exercicio buscarExercicioPelaDataFinal() {
        return exercicioFacade.recuperarExercicioPeloAno(DataUtil.getAno(dataFinal));
    }

    public List<FinalidadePagamento> completarFinalidades(String parte) {
        return finalidadePagamentoFacade.completaFinalidadesPorSituacao(parte.trim(), SituacaoCadastralContabil.ATIVO);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("EXIBIRTOTALPESSOA", Ordenacao.CREDOR_NUMERO.equals(ordenacao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("NOME_RELATORIO", "Relatório de " + (TipoRelatorio.RESUMIDO.equals(tipoRelatorio) ? geracaoRelatorio.getDescricao() : geracaoRelatorio.getDescricaoDetalhada()));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("filtrarContaExtra", contaExtraorcamentaria != null);
            dto.adicionarParametro("ordenacao", montarOrdenacao());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi(TipoRelatorio.RESUMIDO.equals(tipoRelatorio) ? "contabil/pagamento/" : "contabil/pagamento/detalhado/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public String getNomeRelatorio() {
        if (TipoRelatorio.RESUMIDO.equals(tipoRelatorio)) {
            return "RELATORIO-PAGAMENTO-RESUMIDO-" + DataUtil.getAno(dataInicial) + DataUtil.getMes(dataInicial)
                + "A" + DataUtil.getAno(dataFinal) + DataUtil.getMes(dataFinal);
        } else {
            return "RELATORIO-PAGAMENTO-DETALHADO-" + DataUtil.getAno(dataInicial) + DataUtil.getMes(dataInicial)
                + "A" + DataUtil.getAno(dataFinal) + DataUtil.getMes(dataFinal);
        }
    }

    private void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (this.dataInicial == null || this.dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um intervalo de datas.");
        }
        ve.lancarException();
        if (this.dataInicial.after(this.dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final.");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes.");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));

        filtros = "Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        if (tipoLancamento != null) {
            parametros.add(new ParametrosRelatorios(" tipoLancamento ", ":tipo", null, OperacaoRelatorio.IGUAL, tipoLancamento.getAbreviacao(), null, 2, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        List<Long> idsUnidades = Lists.newArrayList();
        if (!hierarquiasOrcamentarias.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiasOrcamentarias) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), buscarExercicioPelaDataFinal(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3)) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
        if (!exibirPagamentoAbertos) {
            parametros.add(new ParametrosRelatorios(" PGTO.STATUS ", ":status", null, OperacaoRelatorio.DIFERENTE, StatusPagamento.ABERTO.name(), null, 1, false));
        }

        if (empenho != null) {
            parametros.add(new ParametrosRelatorios(" EMP.ID ", ":empId", null, OperacaoRelatorio.IGUAL, empenho.getId(), null, 1, false));
            filtros += " Empenho:  " + empenho.getNumero() + " -";
        }

        if (liquidacao != null) {
            parametros.add(new ParametrosRelatorios(" L.ID ", ":liqId", null, OperacaoRelatorio.IGUAL, liquidacao.getId(), null, 1, false));
            filtros += " Liquidação:  " + liquidacao + " -";
        }
        if (fornecedor != null) {
            parametros.add(new ParametrosRelatorios(" P.ID ", ":fornecedorId", null, OperacaoRelatorio.IGUAL, fornecedor.getId(), null, 1, false));
            filtros += " Pessoa: " + fornecedor + " -";
        }

        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" cc.ID ", ":classeId", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe:  " + classeCredor.getCodigo() + " -";
        }

        if (cbe != null) {
            parametros.add(new ParametrosRelatorios(" cbe.ID ", ":contaBancId", null, OperacaoRelatorio.IGUAL, cbe.getId(), null, 1, false));
            filtros += " Conta Bancário:  " + cbe.getNumeroConta() + " - " + cbe.getDigitoVerificador() + " -";
        }

        if (subConta != null) {
            parametros.add(new ParametrosRelatorios(" sc.ID ", ":subContaId", null, OperacaoRelatorio.IGUAL, subConta.getId(), null, 1, false));
            filtros += " Conta Financeira:  " + subConta.getCodigo() + " -";
        }

        if (contaExtraorcamentaria != null) {
            parametros.add(new ParametrosRelatorios(" Cextra.ID ", ":CextraId", null, OperacaoRelatorio.IGUAL, contaExtraorcamentaria.getId(), null, 3, false));
            filtros += " Conta Extraorçamentária:  " + contaExtraorcamentaria.getCodigo() + " -";
        }

        if (despesaORC != null) {
            parametros.add(new ParametrosRelatorios(" DESP.ID ", ":despesaId", null, OperacaoRelatorio.IGUAL, despesaORC.getId(), null, 1, false));
            filtros += " Despesa Orçamentária: " + despesaORC.getCodigo() + " -";
        }
        if (contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" contaDesdob.codigo ", ":cCodigo", null, OperacaoRelatorio.LIKE, contaDespesa.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + contaDespesa.getCodigo() + " -";
        }

        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" PROG.ID", ":idPrograma", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
            filtros += " Programa: " + programaPPA + " -";
        }
        if (projetoAtividade != null) {
            parametros.add(new ParametrosRelatorios(" A.ID", ":idProjetoAtividade", null, OperacaoRelatorio.IGUAL, projetoAtividade.getId(), null, 1, false));
            filtros += " Projeto/Atividade: " + projetoAtividade + " -";
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" F.ID", ":idFuncao", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            filtros += " Função: " + funcao + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" SF.ID", ":idSubFuncao", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            filtros += " SubFunção: " + subFuncao + " -";
        }

        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.id ", ":fonteCodigo", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }

        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" contad.id ", ":contaDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }

        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy");
            Exercicio exercicio = exercicioFacade.recuperarExercicioPeloAno(new Integer(formato.format(dataInicial)));
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        }

        if (finalidadePagamento != null) {
            parametros.add(new ParametrosRelatorios(" pgto.FINALIDADEPAGAMENTO_ID ", ":finalidadePagamento", null, OperacaoRelatorio.IGUAL, finalidadePagamento.getId(), null, 1, false));
            filtros += " Finalidade do Pagamento: " + finalidadePagamento.toString() + " -";
        }

        if (contrato != null) {
            parametros.add(new ParametrosRelatorios(" emp.contrato_id ", ":idContrato", null, OperacaoRelatorio.IGUAL, contrato.getId(), null, 1, false));
            filtros += " Contrato: " + contrato.toString() + " -";
        }

        if (GeracaoRelatorio.DO_EXERCICIO.equals(geracaoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" pgto.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.NORMAL.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" l.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.NORMAL.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" emp.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.NORMAL.name(), null, 1, false));
        } else if (GeracaoRelatorio.RESTO.equals(geracaoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" pgto.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.RESTO.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" l.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.RESTO.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" emp.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.RESTO.name(), null, 1, false));
        }
        filtros = filtros.substring(0, filtros.length() - 1);
        return parametros;
    }

    private String montarOrdenacao() {
        StringBuilder sb = new StringBuilder();
        sb.append(" order by ");
        if (apresentacao.isApresentacaoUnidadeGestora()) {
            sb.append(" unidade_gestora, ");
        } else if (apresentacao.isApresentacaoOrgao() || apresentacao.isApresentacaoUnidade()) {
            sb.append(" unidade, ");
        }

        switch (ordenacao) {
            case NUMERO:
                sb.append(" pgto_numero");
                break;
            case CREDOR_NUMERO:
                sb.append(" fornecedor,  pgto_numero ");
                break;
        }

        return sb.toString();
    }

    public void atribuirNullContaFinanceira() {
        subConta = null;
    }

    public void buscarContaBancariaApartirDaContaFinanceira() {
        if (subConta != null) {
            cbe = subConta.getContaBancariaEntidade();
        } else {
            cbe = null;
        }
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public ContaBancariaEntidade getCbe() {
        return cbe;
    }

    public void setCbe(ContaBancariaEntidade cbe) {
        this.cbe = cbe;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public AcaoPPA getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(AcaoPPA projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Boolean getExibirPagamentoAbertos() {
        return exibirPagamentoAbertos;
    }

    public void setExibirPagamentoAbertos(Boolean exibirPagamentoAbertos) {
        this.exibirPagamentoAbertos = exibirPagamentoAbertos;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrcamentarias() {
        return hierarquiasOrcamentarias;
    }

    public void setHierarquiasOrcamentarias(List<HierarquiaOrganizacional> hierarquiasOrcamentarias) {
        this.hierarquiasOrcamentarias = hierarquiasOrcamentarias;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public GeracaoRelatorio getGeracaoRelatorio() {
        return geracaoRelatorio;
    }

    public void setGeracaoRelatorio(GeracaoRelatorio geracaoRelatorio) {
        this.geracaoRelatorio = geracaoRelatorio;
    }

    public FinalidadePagamento getFinalidadePagamento() {
        return finalidadePagamento;
    }

    public void setFinalidadePagamento(FinalidadePagamento finalidadePagamento) {
        this.finalidadePagamento = finalidadePagamento;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    private enum Ordenacao {

        NUMERO("Número do Pagamento"),
        CREDOR_NUMERO("Credor e Número do Pagamento");

        private String descricao;

        Ordenacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private enum TipoRelatorio {

        RESUMIDO("Resumido"),
        DETALHE("Detalhado");

        private String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private enum GeracaoRelatorio {
        DO_EXERCICIO_E_RESTO("Pagamentos do Exercício e de Restos a Pagar", "Pagamentos Detalhado do Exercício e de Restos a Pagar"),
        DO_EXERCICIO("Pagamentos do Exercício", "Pagamentos Detalhado do Exercício"),
        RESTO("Pagamentos de Restos a Pagar", "Pagamentos Detalhado de Restos a Pagar");

        private String descricao;
        private String descricaoDetalhada;

        GeracaoRelatorio(String descricao, String descricaoDetalhada) {
            this.descricao = descricao;
            this.descricaoDetalhada = descricaoDetalhada;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getDescricaoDetalhada() {
            return descricaoDetalhada;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
