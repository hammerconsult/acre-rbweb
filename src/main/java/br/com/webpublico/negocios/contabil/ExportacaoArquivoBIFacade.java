package br.com.webpublico.negocios.contabil;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.bi.ExportacaoArquivoBI;
import br.com.webpublico.entidadesauxiliares.bi.TipoExportacaoArquivoBI;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.tributario.TipoTributoBI;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDebitoDAO;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo01Facade;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo03Calculator;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorAcrescimos;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Stateless
@Interceptors(TransactionInterceptor.class)
public class ExportacaoArquivoBIFacade implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(ExportacaoArquivoBIFacade.class);
    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ReceitaAlteracaoOrcFacade receitaAlteracaoOrcFacade;
    @EJB
    private ProvisaoPPAFonteFacade provisaoPPAFonteFacade;
    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;
    @EJB
    private ReservaDotacaoFacade reservaDotacaoFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private BiAgrupadorTaxaFacade biAgrupadorTaxaFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private RelatorioRGFAnexo01Facade relatorioRGFAnexo01Facade;
    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private LeitorWsConfig leitorWsConfig;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;

    public ExportacaoArquivoBIFacade() {

    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Asynchronous
    public Future<List<ExportacaoArquivoBI>> exportarArquivo(ExportacaoArquivoBI exportacaoArquivoBI) {
        List<ExportacaoArquivoBI> gerados = Lists.newArrayList();
        inicializar(exportacaoArquivoBI);
        exportacaoArquivoBI.getBarraProgressoItens().setTotal(exportacaoArquivoBI.getTiposSelecionados().size());
        for (TipoExportacaoArquivoBI tipo : exportacaoArquivoBI.getTiposSelecionados()) {
            ExportacaoArquivoBI exportacao = criarNovaExportacaoParaTipo(exportacaoArquivoBI, tipo);
            exportacaoArquivoBI.getBarraProgressoItens().setMensagens("Gerando arquivo: " + tipo.getDescricao());

            inicializar(exportacao);
            gerarArquivo(exportacao);
            escreverNoArquivo(exportacao, exportacao.getTipo().getNomeArquivo());
            finalizar(exportacao);

            aumentarProcessado(exportacaoArquivoBI);
            gerados.add(exportacao);
        }
        finalizar(exportacaoArquivoBI);
        return new AsyncResult<>(gerados);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private ExportacaoArquivoBI criarNovaExportacaoParaTipo(ExportacaoArquivoBI exportacaoArquivoBI, TipoExportacaoArquivoBI tipo) {
        ExportacaoArquivoBI nova = new ExportacaoArquivoBI();
        nova.setTipo(tipo);
        nova.setDataOperacao(exportacaoArquivoBI.getDataOperacao());
        nova.setExercicioInicial(exportacaoArquivoBI.getExercicioInicial());
        nova.setExercicioFinal(exportacaoArquivoBI.getExercicioFinal());
        return nova;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void finalizar(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().finaliza();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void inicializar(ExportacaoArquivoBI selecionado) {
        selecionado.setBarraProgressoItens(new BarraProgressoItens());
        selecionado.getBarraProgressoItens().inicializa();
        selecionado.getBarraProgressoItens().setMensagens("Iniciando exportação");
        selecionado.setLinha(new StringBuilder());
        selecionado.setStreamedContent(null);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void gerarArquivo(ExportacaoArquivoBI selecionado) {
        try {
            switch (selecionado.getTipo()) {
                case PLANO_CONTAS:
                    exportarPlanoDeContas(selecionado);
                    break;
                case ACAO:
                    exportarAcao(selecionado);
                    break;
                case PROGRAMA:
                    exportarProgramas(selecionado);
                    break;
                case ORGAO:
                    exportarOrgao(selecionado);
                    break;
                case FONTE:
                    exportarFonte(selecionado);
                    break;
                case SUB_FONTE:
                    exportarSubFonte(selecionado);
                    break;
                case RECEITA:
                    exportarReceitas(selecionado);
                    break;
                case CONTRATO:
                    exportarContratos(selecionado);
                    break;
                case FORNECEDOR:
                    exportarFornecedores(selecionado);
                    break;
                case LIMITE_PESSOAL:
                    exportarLimitePessoal(selecionado);
                    break;
                case ORCAMENTO_RECEITA:
                    exportarOrcamentoReceita(selecionado);
                    break;
                case ORCAMENTO_INICIAL:
                    exportarOrcamentoInicial(selecionado);
                    break;
                case ORCAMENTO_ALTERADO:
                    exportarOrcamentoAlterado(selecionado);
                    break;
                case BLOQUEADOS:
                    exportarBloqueado(selecionado);
                    break;
                case CREDITOS:
                    exportarCreditos(selecionado);
                    break;
                case LOGRADOURO:
                    exportarLogradouros(selecionado);
                    break;
                case BAIRRO:
                    exportarBairros(selecionado);
                    break;
                case PESSOA:
                    exportarPessoas(selecionado);
                    break;
                case EVENTO:
                    exportarEventos(selecionado);
                    break;
                case GUIAS_TAXAS:
                    exportarTaxas(selecionado);
                    break;
                case IMOVEIS:
                    exportarImoveis(selecionado);
                    break;
                case TIPO_USO_IMOVEL_TERRENO:
                    exportarTiposDeUsoImovelTerreno(selecionado);
                    break;
                case VALORES_IPTU_PAGO:
                    exportarValoresIPTUPago(selecionado);
                    break;
                case VALORES_LANCAMENTO_IPTU:
                    exportarValoresLancamentoIPTU(selecionado);
                    break;
                case TIPOS_IPTU:
                    exportarTiposDeIPTU(selecionado);
                    break;
                case EMPENHADO:
                    exportarEmpenhos(selecionado);
                    break;
                case LIQUIDADO:
                    exportarLiquidacoes(selecionado);
                    break;
                case PAGO:
                    exportarPagamentos(selecionado);
                    break;
                case RESTOS_A_PAGAR:
                    exportarRestos(selecionado);
                    break;
                case CMC:
                    exportarCMCs(selecionado);
                    break;
                case TIPOISS:
                    exportarTipoISS(selecionado);
                    break;
                case ATIVIDADE:
                    exportarAtividades(selecionado);
                    break;
                case GUIA_ISS:
                    exportarGuiaISS(selecionado);
                    break;
                case ORIGEM_DEBITOS:
                    exportarOrigemDebito(selecionado);
                    break;
                case SITUACAO_DEBITO:
                    exportarSituacaoParcela(selecionado);
                    break;
                case TRIBUTO:
                    exportarTributo(selecionado);
                    break;
                case DEBITOS_GERAL:
                    exportarDebitosGeral(selecionado);
                    break;
                case PARCELAMENTO:
                    exportarParcelamento(selecionado);
                    break;
                case CONTAS_BANCARIAS:
                    exportarDadosBancarios(selecionado);
                    break;
                case MOVIMENTO_CONTA_BANCARIA:
                    exportarMovimentoContasBancarias(selecionado);
                    break;
                case SALDO_CONTA_BANCARIA:
                    exportarSaldosDasContasBancarias(selecionado);
                    break;
                case CONTAS_FINANCEIRAS:
                    exportarContasFinanceiras(selecionado);
                    break;
                case MOVIMENTO_CONTA_FINANCEIRA:
                    exportarMovimentoContasFinanceiras(selecionado);
                    break;
                case NOME_BANCO:
                    exportarBancos(selecionado);
                    break;
                case SALDO_CONTA_FINANCEIRA:
                    exportarSaldosDasContasFinanceiras(selecionado);
                    break;
                default:
                    throw new ExcecaoNegocioGenerica("O Tipo do arquivo não foi definido.");
            }
        } catch (Exception e) {
            selecionado.setDeuErro(Boolean.TRUE);
            logger.error("Erro ao gerar o arquivo. Detalhes do erro: {} ", e);
            selecionado.getBarraProgressoItens().setMensagens("Erro ao gerar o arquivo. Detalhes do erro: " + e.getMessage());
        }

    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void escreverNoArquivo(ExportacaoArquivoBI selecionado, String nomeArquivo) {
        try {
            selecionado.getBarraProgressoItens().setMensagens("Gravando o arquivo " + nomeArquivo);
            File file = gravarArquivo(selecionado, nomeArquivo);
            selecionado.setStreamedContent(new DefaultStreamedContent(new FileInputStream(file), "text/plain", nomeArquivo));
        } catch (Exception e) {
            selecionado.getBarraProgressoItens().setMensagens("Erro ao escrever no arquivo. Detalhes do erro : " + e.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarSubFonte(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando as Sub-Fontes de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<FonteDeRecursos> fonteDeRecursos = fonteDeRecursosFacade.listaPorExercicio(exercicio);
            adicionarQuantidadeTotal(selecionado, fonteDeRecursos.size());
            for (FonteDeRecursos fonte : fonteDeRecursos) {
                selecionado.getLinha().append(fonte.getCodigo());
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("0");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(StringUtil.removeEspacos(fonte.getDescricao()));
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarReceitas(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando as Receitas de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> receitas = lancamentoReceitaOrcFacade.buscarReceitas(exercicio);
            adicionarQuantidadeTotal(selecionado, receitas.size());
            for (Object[] receita : receitas) {
                selecionado.getLinha().append(receita[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(receita[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(receita[2]);
                selecionado.adicionarSeparador();
                String contaCodigo = (String) receita[3];
                contaCodigo = contaCodigo.substring(0, contaCodigo.length() - 2);
                selecionado.getLinha().append(contaCodigo.replace(".", ";"));
                selecionado.getLinha().append(receita[4]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("0");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(receita[5]));
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarContratos(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os Contratos de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> contratos = contratoFacade.buscarContratos(exercicio);
            adicionarQuantidadeTotal(selecionado, contratos.size());
            for (Object[] contrato : contratos) {
                selecionado.getLinha().append(contrato[0]);
                selecionado.adicionarSeparador();
                String descricaoContrato = ((String) contrato[1]).replace(System.getProperty("line.separator"), "").replace("\t", "").replace("\r", "");
                selecionado.getLinha().append(descricaoContrato);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(contrato[2]);
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarFornecedores(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os Fornecedores de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> fornecedores = contratoFacade.buscarFornecedores(exercicio);
            adicionarQuantidadeTotal(selecionado, fornecedores.size());
            for (Object[] fornecedor : fornecedores) {
                selecionado.getLinha().append(fornecedor[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(fornecedor[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(fornecedor[2] != null ? ((String) fornecedor[2]).replace(".", "").replace("-", "").replace("/", "") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(fornecedor[3]);
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarLimitePessoal(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os Limites Pessoais de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            RelatoriosItemDemonst anexo3 = relatorioRREOAnexo03Calculator.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RREO);
            RelatoriosItemDemonst anexo1 = relatorioRREOAnexo03Calculator.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", exercicio, TipoRelatorioItemDemonstrativo.RGF);
            ItemDemonstrativo itemDemonstrativoRCL = null;
            if (anexo3.getId() != null) {
                itemDemonstrativoRCL = relatorioRREOAnexo03Calculator.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", exercicio, anexo3);
            }
            ItemDemonstrativo itemDemonstrativoDLP = null;
            if (anexo1.getId() != null) {
                itemDemonstrativoDLP = relatorioRREOAnexo03Calculator.recuperarItemDemonstrativoPeloNomeERelatorio("DESPESA LÍQUIDA COM PESSOAL (III) = (I - II)", exercicio, anexo1);
            }
            ItemDemonstrativoFiltros itemDemonstrativoFiltros;
            adicionarQuantidadeTotal(selecionado, Mes.values().length);
            for (Mes mes : Mes.values()) {
                itemDemonstrativoFiltros = instanciarItemDemonstrativoFiltros(exercicio, anexo1, mes);
                GregorianCalendar dataCalendar = new GregorianCalendar(exercicio.getAno(), (mes.getNumeroMes() - 1), new Integer(1));
                String data = Util.formatterMesAno.format(dataCalendar.getTime());
                BigDecimal rcl = BigDecimal.ZERO;
                if (itemDemonstrativoRCL != null) {
                    rcl = relatorioRREOAnexo03Calculator.calcularAlterado(itemDemonstrativoRCL, data, anexo3);
                }
                BigDecimal gastoPessoal = BigDecimal.ZERO;
                if (itemDemonstrativoDLP != null) {
                    gastoPessoal = relatorioRGFAnexo01Facade.calcularValor(itemDemonstrativoDLP.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_LIQUIDADAS_ATE_BIMESTRE);
                }
                BigDecimal percentual = BigDecimal.ZERO;
                if (rcl.compareTo(BigDecimal.ZERO) > 0) {
                    percentual = (gastoPessoal.divide(rcl, 4, RoundingMode.HALF_UP)).multiply(new BigDecimal("100"));
                }
                if (rcl.compareTo(BigDecimal.ZERO) > 0 || gastoPessoal.compareTo(BigDecimal.ZERO) > 0) {
                    selecionado.getLinha().append(exercicio.getAno());
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(mes.getNumeroMes());
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(adicionarMascaraValor(rcl));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(adicionarMascaraValor(gastoPessoal));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(adicionarMascaraValor(adicionarMascaraValor(percentual)));
                }
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private ItemDemonstrativoFiltros instanciarItemDemonstrativoFiltros(Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst, Mes mes) {
        ItemDemonstrativoFiltros itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/" + mes.getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(exercicio);
        itemDemonstrativoFiltros.setParametros(Lists.<ParametrosRelatorios>newArrayList());
        return itemDemonstrativoFiltros;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private BigDecimal adicionarMascaraValor(Object o) {
        return ((BigDecimal) o).setScale(2, RoundingMode.HALF_UP);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarFonte(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando as Fontes de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<FonteDeRecursos> fonteDeRecursos = fonteDeRecursosFacade.listaPorExercicio(exercicio);
            adicionarQuantidadeTotal(selecionado, fonteDeRecursos.size());
            for (FonteDeRecursos fonte : fonteDeRecursos) {
                selecionado.getLinha().append(fonte.getCodigo());
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(StringUtil.removeEspacos(fonte.getDescricao()));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(fonte.getExercicio().getAno());
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarOrgao(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os órgãos de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        List<HierarquiaOrganizacional> orgaos = hierarquiaOrganizacionalFacade.filtraPorNivelTrazendoTodas("", "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), selecionado.getDataOperacao());

        adicionarQuantidadeTotal(selecionado, orgaos.size());
        for (HierarquiaOrganizacional hierarquiaOrganizacional : orgaos) {
            selecionado.getLinha().append(hierarquiaOrganizacional.getCodigo().replace(".", ""));
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(hierarquiaOrganizacional.getDescricao());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append("1");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(hierarquiaOrganizacional.getExercicio().getAno());
            finalizarLinhaAumentandoProcessado(selecionado);
        }

    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarProgramas(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os programas de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<ProgramaPPA> programas = programaPPAFacade.buscarProgramasPorExercicioDaSubAcao(exercicio);
            adicionarQuantidadeTotal(selecionado, programas.size());
            for (ProgramaPPA programaPPA : programas) {
                selecionado.getLinha().append(programaPPA.getCodigo());
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(programaPPA.getDenominacao());
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("0");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(programaPPA.getExercicio().getAno());

                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }

    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarAcao(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando as ações de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<AcaoPPA> projetos = projetoAtividadeFacade.buscarProjetoAtividadeExercicio(exercicio);
            adicionarQuantidadeTotal(selecionado, projetos.size());
            for (AcaoPPA acaoPPA : projetos) {
                selecionado.getLinha().append(acaoPPA.getCodigo());
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(acaoPPA.getDescricao());
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(acaoPPA.getTipoAcaoPPA().getCodigo());
                selecionado.adicionarSeparador();
                if (acaoPPA.getPrograma() != null) {
                    selecionado.getLinha().append(acaoPPA.getPrograma().getCodigo());
                } else {
                    if (acaoPPA.getAcaoPrincipal() != null && acaoPPA.getAcaoPrincipal().getPrograma() != null) {
                        selecionado.getLinha().append(acaoPPA.getAcaoPrincipal().getPrograma().getCodigo());
                    }
                }
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(acaoPPA.getExercicio().getAno());

                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }

    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarPlanoDeContas(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando as contas de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<PlanoDeContas> planoDeContas = planoDeContasFacade.recuperaPlanosPorExercicio(exercicio);
            for (PlanoDeContas planoDeConta : planoDeContas) {
                if (ClasseDaConta.DESPESA.equals(planoDeConta.getTipoConta().getClasseDaConta())
                    || ClasseDaConta.RECEITA.equals(planoDeConta.getTipoConta().getClasseDaConta())) {
                    planoDeConta = planoDeContasFacade.recuperarContas(planoDeConta.getId());

                    adicionarQuantidadeTotal(selecionado, planoDeConta.getContas().size());

                    for (Conta conta : planoDeConta.getContas()) {
                        String codigo = conta.getCodigo();
                        /*3.0.00.00.00.00.00*/
                        /*1.1.1.3.00.0.0.00*/
                        if (conta instanceof ContaReceita) {
                            codigo = codigo.substring(0, codigo.length() - 2);
                        }
                        selecionado.getLinha().append(codigo.replace(".", ";"));
                        if (conta instanceof ContaDespesa) {
                            selecionado.adicionarSeparador();
                        }
                        selecionado.getLinha().append(conta.getDescricao());
                        selecionado.adicionarSeparador();
                        selecionado.getLinha().append(conta.getPlanoDeContas().getExercicio().getAno());
                        selecionado.adicionarSeparador();
                        selecionado.getLinha().append(conta instanceof ContaReceita ? "1" : "2");

                        finalizarLinhaAumentandoProcessado(selecionado);
                    }
                }
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarOrcamentoReceita(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando orçamento da receita de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> receitas = receitaAlteracaoOrcFacade.buscarOrcamentoReceita(exercicio);
            adicionarQuantidadeTotal(selecionado, receitas.size());
            for (Object[] receita : receitas) {
                selecionado.getLinha().append(receita[0]);
                selecionado.adicionarSeparador();
                String contaCodigo = (String) receita[1];
                contaCodigo = contaCodigo.substring(0, contaCodigo.length() - 2);
                selecionado.getLinha().append(contaCodigo.replace(".", ";"));
                selecionado.getLinha().append(receita[2]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("0");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(receita[3]));
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarOrcamentoInicial(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando orçamento inicial de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> orcamentosIniciais = provisaoPPAFonteFacade.buscarOrcamentoInicial(exercicio, selecionado.getDataOperacao());
            adicionarQuantidadeTotal(selecionado, orcamentosIniciais.size());
            for (Object[] orcamento : orcamentosIniciais) {
                selecionado.getLinha().append(orcamento[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) orcamento[1]).replace(".", ";"));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) orcamento[2]).replace(".", ""));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(orcamento[3]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(orcamento[4]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(orcamento[5]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(orcamento[6]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(orcamento[7]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(orcamento[8]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("0");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(orcamento[9]));
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarOrcamentoAlterado(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando orçamento alterado de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> alteracoes = alteracaoORCFacade.buscarAlteracoesOrcamentarias(exercicio, selecionado.getDataOperacao());
            adicionarQuantidadeTotal(selecionado, alteracoes.size());
            for (Object[] alteracao : alteracoes) {
                selecionado.getLinha().append(alteracao[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[2]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) alteracao[3]).replace(".", ";"));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) alteracao[4]).replace(".", ""));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[5]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[6]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[7]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[8]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[9]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[10]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("0");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(alteracao[11]));
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarCreditos(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando créditos de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> alteracoes = alteracaoORCFacade.buscarSuplementacoesOrcamentarias(exercicio, selecionado.getDataOperacao());
            adicionarQuantidadeTotal(selecionado, alteracoes.size());
            for (Object[] alteracao : alteracoes) {
                selecionado.getLinha().append(alteracao[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[2]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) alteracao[3]).replace(".", ";"));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) alteracao[4]).replace(".", ""));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[5]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[6]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[7]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[8]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[9]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[10]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("0");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(alteracao[11]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("1");
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarBloqueado(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando bloqueados de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> alteracoes = reservaDotacaoFacade.buscarBloqueios(exercicio, selecionado.getDataOperacao());
            adicionarQuantidadeTotal(selecionado, alteracoes.size());
            for (Object[] alteracao : alteracoes) {
                selecionado.getLinha().append(alteracao[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[2]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) alteracao[3]).replace(".", ";"));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) alteracao[4]).replace(".", ""));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[5]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[6]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[7]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[8]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[9]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(alteracao[10]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("0");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(alteracao[11]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append("3");
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarLogradouros(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os logradouros");
        List<Object[]> logradouros = logradouroFacade.buscarLogradouros();
        adicionarQuantidadeTotal(selecionado, logradouros.size());
        for (Object[] logradouro : logradouros) {
            selecionado.getLinha().append(logradouro[0]);
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(logradouro[1]);
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(logradouro[2]);
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(logradouro[3]);
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarBairros(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os bairros");
        List<Object[]> bairros = bairroFacade.buscarBairros();
        adicionarQuantidadeTotal(selecionado, bairros.size());
        for (Object[] bairro : bairros) {
            selecionado.getLinha().append(bairro[0]);
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(bairro[1]);
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(bairro[2]);
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarPessoas(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os contribuintes");
        List<Object[]> pessoas = Lists.newArrayList();
        pessoas.addAll(pessoaFacade.buscarTodasAsPessoasFisicasAtivas());
        pessoas.addAll(pessoaFacade.buscarTodasAsPessoasJuridicasAtivas());
        adicionarQuantidadeTotal(selecionado, pessoas.size());
        for (Object[] pessoa : pessoas) {
            selecionado.getLinha().append(StringUtil.removerQuebraDeLinha(((String) pessoa[0])).replace(".", "").replace("/", "").replace("-", "").trim());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(pessoa[1] != null ? StringUtil.removerQuebraDeLinha(((String) pessoa[1]).replaceAll(";", ".")).trim() : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(pessoa[2]);
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(pessoa[3] != null ? StringUtil.removerQuebraDeLinha(((String) pessoa[3]).replaceAll(";", "")).trim() : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(pessoa[4] != null ? StringUtil.removerQuebraDeLinha(((String) pessoa[4]).replaceAll(";", "")).trim() : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(pessoa[5] != null ? StringUtil.removerQuebraDeLinha(((String) pessoa[5]).replaceAll(";", "")).trim() : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(pessoa[6] != null ? StringUtil.removerQuebraDeLinha(((String) pessoa[6]).replaceAll(";", "")).trim() : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(pessoa[7] != null ? StringUtil.cortaDireita(50, StringUtil.removerQuebraDeLinha((String) pessoa[7]).trim().replaceAll(";", "")) : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(pessoa[8] != null ? StringUtil.removerQuebraDeLinha(((String) pessoa[8]).replaceAll(";", "")).trim() : "");
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarEventos(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os eventos");
        List<VOTributoBi> tributos = biAgrupadorTaxaFacade.buscarTributosAgrupados();
        adicionarQuantidadeTotal(selecionado, tributos.size());
        for (VOTributoBi tributo : tributos) {
            selecionado.getLinha().append(tributo.getCodigo());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(tributo.getDescricao());
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarCMCs(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando cadastros econômicos");
        List<Object[]> cmcs = buscarCadastrosEconomicosExportacaoBI();
        adicionarQuantidadeTotal(selecionado, cmcs.size());
        Map<String, SituacaoCadastralCadastroEconomico> cmcsInseridos = Maps.newHashMap();
        for (Object[] cmc : cmcs) {
            if (cmcsInseridos.containsKey((String) cmc[0])) {
                continue;
            }

            selecionado.getLinha().append(cmc[0] != null ? cmc[0] : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(cmc[1] != null ? ((String) cmc[1]).replace(".", "").replace("/", "").replace("-", "") : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(cmc[2] != null ? StringUtil.removerQuebraDeLinha(((String) cmc[2]).replaceAll(";", ".")) : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(cmc[3] != null ? DataUtil.getDataFormatada((Date) cmc[3], "ddMMyyyy") : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(cmc[4] != null ? DataUtil.getDataFormatada((Date) cmc[4], "ddMMyyyy") : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(cmc[5] != null ? cmc[5] : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(cmc[6] != null ? cmc[6] : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(cmc[7] != null ? cmc[7] : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(cmc[8] != null ? cmc[8] : "");
            selecionado.adicionarSeparador();

            String situacao = cmc[9] != null ? (String) cmc[9] : null;
            if (SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO.name().equals(situacao)) {
                situacao = SituacaoCadastralCadastroEconomico.AGUARDANDO_AVALIACAO.name();
            }
            final String situacaoEnum = situacao;
            SituacaoCadastralCadastroEconomico situacaoCadastral = Arrays.stream(SituacaoCadastralCadastroEconomico.values())
                .anyMatch((s) -> s.name().equals(situacaoEnum)) ? (SituacaoCadastralCadastroEconomico.valueOf(situacao)) : null;
            selecionado.getLinha().append(situacaoCadastral != null ? situacaoCadastral.getCodigo() : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(situacaoCadastral != null ? situacaoCadastral.getDescricao() : "");
            cmcsInseridos.put((String) cmc[0], situacaoCadastral);
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarTipoISS(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando tipos de ISS");

        List<Divida> todasDividasIss = buscarDividasISS(TipoExportacaoArquivoBI.TIPOISS);

        adicionarQuantidadeTotal(selecionado, todasDividasIss.size());
        for (Divida divida : todasDividasIss) {
            selecionado.getLinha().append(divida.getCodigo());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(divida.getDescricao());
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Divida> buscarDividasISS(TipoExportacaoArquivoBI tipoExportacaoArquivoBI) {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        return configuracaoTributario != null &&
            configuracaoTributario.getConfiguracaoTributarioBI() != null ?
            configuracaoTributarioFacade
                .buscarDividasTipoExportacaoBI(configuracaoTributario.getConfiguracaoTributarioBI(),
                    tipoExportacaoArquivoBI) : Lists.newArrayList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Tributo> buscarTributosIss() {
        String sql = "select distinct t.* " +
            "   from tributo t " +
            "  inner join itemvalordivida ivd on ivd.tributo_id = t.id " +
            "  inner join valordivida vd on vd.id = ivd.valordivida_id " +
            "  inner join calculoiss iss on iss.id = vd.calculo_id " +
            "where t.tipotributo in (:tipoImposto, :tipoTaxa)";
        return em.createNativeQuery(sql, Tributo.class)
            .setParameter("tipoImposto", Tributo.TipoTributo.IMPOSTO.name())
            .setParameter("tipoTaxa", Tributo.TipoTributo.TAXA.name())
            .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Tributo> buscarTributosIptu() {
        String sql = "select distinct t.* " +
            "   from tributo t " +
            "  inner join itemvalordivida ivd on ivd.tributo_id = t.id " +
            "  inner join valordivida vd on vd.id = ivd.valordivida_id " +
            "  inner join calculoiptu iptu on iptu.id = vd.calculo_id " +
            "where t.tipotributo in (:tipoImposto, :tipoTaxa)";
        return em.createNativeQuery(sql, Tributo.class)
            .setParameter("tipoImposto", Tributo.TipoTributo.IMPOSTO.name())
            .setParameter("tipoTaxa", Tributo.TipoTributo.TAXA.name())
            .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarAtividades(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recupernado atividades");
        List<Object[]> atividades = buscarAtividadesExportacaoBI(CNAE.Situacao.ATIVO);
        adicionarQuantidadeTotal(selecionado, atividades.size());
        for (Object[] atividade : atividades) {
            selecionado.getLinha().append(atividade[0] != null ? atividade[0] : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(atividade[1] != null ? atividade[1] : "");
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarGuiaISS(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando guias");
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            selecionado.getBarraProgressoItens().setMensagens("Recuperando guias para o exercício de " + exercicio.getAno());
            List<Object[]> guias = buscarGuiasISSExportacaoBI(exercicio);
            adicionarQuantidadeTotal(selecionado, guias.size());
            for (Object[] guia : guias) {
                if (((BigDecimal) guia[10]).compareTo(BigDecimal.ZERO) != 0 || ((BigDecimal) guia[11]).compareTo(BigDecimal.ZERO) != 0 ||
                    ((BigDecimal) guia[12]).compareTo(BigDecimal.ZERO) != 0 || ((BigDecimal) guia[13]).compareTo(BigDecimal.ZERO) != 0 ||
                    ((BigDecimal) guia[14]).compareTo(BigDecimal.ZERO) != 0) {

                    Integer anoReferencia = guia[16] != null ? ((BigDecimal) guia[16]).intValue() : null;
                    Integer mesReferencia = guia[17] != null ? ((BigDecimal) guia[17]).intValue() : null;
                    Date dataCompetencia = guia[7] != null ? (Date) guia[7] : null;
                    if (mesReferencia != null && mesReferencia <= 0) {
                        mesReferencia = 1;
                    }
                    if (anoReferencia != null && anoReferencia > 0) {
                        dataCompetencia = DataUtil.getDia(1, mesReferencia.intValue(), anoReferencia.intValue());
                    }

                    selecionado.getLinha().append(guia[0] != null ? guia[0] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(guia[1] != null ? StringUtil.retornaApenasNumeros((String) guia[1]) : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(guia[2] != null ? guia[2] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(guia[3] != null ? guia[3] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(guia[4] != null ? guia[4] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(guia[5] != null ? guia[5] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(guia[6] != null ? guia[6] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(dataCompetencia != null ? DataUtil.getDataFormatada(dataCompetencia, "ddMMyyyy") : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(guia[8] != null ? DataUtil.getDataFormatada((Date) guia[8], "ddMMyyyy") : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(guia[18] != null ? guia[18] : "");
                    selecionado.adicionarSeparador();
                    selecionado.adicionarSeparador();
                    selecionado.adicionarSeparador();

                    boolean isDividaAtiva = guia[15] != null && ((BigDecimal) guia[15]).compareTo(BigDecimal.ZERO) != 0;

                    if (!isDividaAtiva) {
                        selecionado.getLinha().append(guia[10] != null ? formatarValorArquivo(guia[10]) : formatarValorArquivo(BigDecimal.ZERO));
                    } else {
                        selecionado.getLinha().append(formatarValorArquivo(BigDecimal.ZERO));
                    }
                    selecionado.adicionarSeparador();

                    BigDecimal acrescimos = adicionarMascaraValor(guia[11] != null ? guia[11] : BigDecimal.ZERO)
                        .add(adicionarMascaraValor(guia[12] != null ? guia[12] : BigDecimal.ZERO))
                        .add(adicionarMascaraValor(guia[14] != null ? guia[14] : BigDecimal.ZERO));

                    selecionado.getLinha().append(!isDividaAtiva && guia[11] != null ? formatarValorArquivo(guia[11]) : formatarValorArquivo(BigDecimal.ZERO));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(!isDividaAtiva && guia[12] != null ? formatarValorArquivo(guia[12]) : formatarValorArquivo(BigDecimal.ZERO));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(!isDividaAtiva && guia[13] != null ? formatarValorArquivo(((BigDecimal) guia[13]).add(acrescimos)) : formatarValorArquivo(BigDecimal.ZERO));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(!isDividaAtiva && guia[14] != null ? formatarValorArquivo(guia[14]) : formatarValorArquivo(BigDecimal.ZERO));
                    selecionado.adicionarSeparador();
                    selecionado.adicionarSeparador();
                    if (isDividaAtiva) {
                        selecionado.getLinha().append(guia[10] != null ? formatarValorArquivo(((BigDecimal) guia[10]).add(acrescimos)) : formatarValorArquivo(BigDecimal.ZERO));
                    } else {
                        selecionado.getLinha().append(formatarValorArquivo(BigDecimal.ZERO));
                    }
                    selecionado.adicionarSeparador();
                    Date dataSituacao = guia[20] != null ? (Date) guia[20] : null;
                    if (SituacaoParcela.CANCELAMENTO.name().equals((String) guia[19])) {
                        selecionado.getLinha().append(dataSituacao != null ? DataUtil.getDataFormatada(dataSituacao, "ddMMyyyy") : "");
                    }
                    finalizarLinhaAumentandoProcessado(selecionado);
                }
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarTaxas(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando as taxas");
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            selecionado.getBarraProgressoItens().setMensagens("Recuperando as taxas para o Exercício de " + exercicio.getAno());
            List<Object[]> taxas = buscarTaxas(exercicio);
            adicionarQuantidadeTotal(selecionado, taxas.size());
            for (Object[] taxa : taxas) {
                selecionado.getLinha().append(taxa[0] != null ? taxa[0] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) taxa[1]).replace("/", ""));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(taxa[2] != null ? taxa[2] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(taxa[3] != null ? taxa[3] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(taxa[4] != null ? taxa[4] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(exercicio.getAno());
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(taxa[5] != null ? taxa[5] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(taxa[6] != null ? DataUtil.getDataFormatada((Date) taxa[6], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(taxa[7] != null ? DataUtil.getDataFormatada((Date) taxa[7], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(taxa[8] != null ? SituacaoParcela.valueOf((String) taxa[8]).getCodigo().toString() : "0");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(taxa[9] != null ? taxa[9] : "");
                selecionado.adicionarSeparador();

                boolean isDividaAtiva = taxa[15] != null && ((BigDecimal) taxa[15]).compareTo(BigDecimal.ZERO) != 0;
                if (!isDividaAtiva) {
                    selecionado.getLinha().append(taxa[10] != null ? formatarValorArquivo(taxa[10]) : "");
                } else {
                    selecionado.getLinha().append(formatarValorArquivo(BigDecimal.ZERO));
                }
                selecionado.adicionarSeparador();

                BigDecimal acrescimos = adicionarMascaraValor(taxa[11] != null ? taxa[11] : BigDecimal.ZERO)
                    .add(adicionarMascaraValor(taxa[12] != null ? taxa[12] : BigDecimal.ZERO))
                    .add(adicionarMascaraValor(taxa[14] != null ? taxa[14] : BigDecimal.ZERO));

                selecionado.getLinha().append(!isDividaAtiva && taxa[11] != null ? formatarValorArquivo(taxa[11]) : formatarValorArquivo(BigDecimal.ZERO));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(!isDividaAtiva && taxa[12] != null ? formatarValorArquivo(taxa[12]) : formatarValorArquivo(BigDecimal.ZERO));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(!isDividaAtiva && taxa[13] != null ? formatarValorArquivo(((BigDecimal) taxa[13]).add(acrescimos)) : formatarValorArquivo(BigDecimal.ZERO));
                selecionado.adicionarSeparador();
                if (isDividaAtiva) {
                    selecionado.getLinha().append(taxa[10] != null ? formatarValorArquivo(((BigDecimal) taxa[10]).add(acrescimos)) : formatarValorArquivo(BigDecimal.ZERO));
                } else {
                    selecionado.getLinha().append(formatarValorArquivo(BigDecimal.ZERO));
                }
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(!isDividaAtiva && taxa[14] != null ? formatarValorArquivo(taxa[14]) : formatarValorArquivo(BigDecimal.ZERO));

                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarOrigemDebito(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando origem do débito");
        List<OrigemDebitoBI> origens = Arrays.asList(OrigemDebitoBI.values());
        adicionarQuantidadeTotal(selecionado, origens.size());
        for (OrigemDebitoBI obj : origens) {
            selecionado.getLinha().append(obj.getCodigoBI());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(obj.getDescricao());
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarSituacaoParcela(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando situação parcela");
        List<SituacaoParcela> situacoes = Arrays.asList(SituacaoParcela.values());
        situacoes.sort(new Comparator<SituacaoParcela>() {
            @Override
            public int compare(SituacaoParcela sp1, SituacaoParcela sp2) {
                return sp1.getCodigo().compareTo(sp2.getCodigo());
            }
        });
        int codigoUltimaSituacao = situacoes.get(situacoes.size() - 1).getCodigo();
        List<String> situacoesDA = Lists.newArrayList(
            "Dívida Atíva Em Aberto",
            "Dívida Ativa Ajuizada Em Aberto",
            "Dívida Ativa Protestada Em Aberto"
        );
        adicionarQuantidadeTotal(selecionado, situacoes.size() + situacoesDA.size());
        for (SituacaoParcela obj : situacoes) {
            selecionado.getLinha().append(obj.getCodigo());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(obj.getDescricao());
            finalizarLinhaAumentandoProcessado(selecionado);
        }
        for (String situacaoDA : situacoesDA) {
            codigoUltimaSituacao++;
            selecionado.getLinha().append(codigoUltimaSituacao);
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(situacaoDA);
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarTributo(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando tributo");
        List<Divida> dividas = dividaFacade.listaDividasOrdenadoPorDescricao();
        adicionarQuantidadeTotal(selecionado, dividas.size());
        for (Divida obj : dividas) {
            selecionado.getLinha().append(obj.getCodigo() != null ? obj.getCodigo() : "");
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(obj.getDescricao());
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarDebitosGeral(ExportacaoArquivoBI selecionado) throws Exception {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando débitos geral");
        try {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            CalculadorAcrescimos calculadorAcrescimos = new CalculadorAcrescimos(consultaParcela.getServiceConsultaDebitos());

            int anoInicial = selecionado.getExercicioInicial().getAno();
            int anoFinal = selecionado.getExercicioFinal().getAno();
            logger.debug("Recuperando débitos gerais de {} a {}", anoInicial, anoFinal);
            for (int ano = anoInicial; ano <= anoFinal; ano++) {
                logger.debug("Recuperando débitos gerais para o exercício de {}", ano);
                selecionado.getBarraProgressoItens().setMensagens("Recuperando débitos gerais para o exercício de " + ano);
                List<Object[]> debitos = JdbcDebitoDAO.getInstance().buscarDados(ano);
                adicionarQuantidadeTotal(selecionado, debitos.size());

                for (Object[] obj : debitos) {
                    selecionado.getLinha().append(obj[0] != null ? obj[0] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(obj[1] != null ? obj[1] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(obj[2] != null ? obj[2] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(obj[3] != null ? obj[3] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(obj[4] != null ? obj[4] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(obj[5] != null ? DataUtil.getDataFormatada((Date) obj[5], "ddMMyyyy") : "");
                    selecionado.adicionarSeparador();

                    ResultadoParcela resultadoParcela = new ResultadoParcela();
                    resultadoParcela.setIdParcela(((BigDecimal) obj[15]).longValue());
                    resultadoParcela.setIdCalculo(((BigDecimal) obj[22]).longValue());
                    resultadoParcela.setVencimento((Date) obj[5]);
                    resultadoParcela.setEmissao((Date) obj[20]);
                    resultadoParcela.setIdDivida(((BigDecimal) obj[21]).longValue());
                    resultadoParcela.setSituacao(SituacaoParcela.valueOf((String) obj[14]).name());
                    resultadoParcela.setTipoCalculo(Calculo.TipoCalculo.valueOf((String) obj[16]).name());
                    resultadoParcela.setCotaUnica(false);
                    resultadoParcela.setIdConfiguracaoAcrescimo(((BigDecimal) obj[17]).longValue());
                    resultadoParcela.setExercicio(((BigDecimal) obj[18]).intValue());
                    resultadoParcela.setDividaAtiva(true);
                    resultadoParcela.setDividaAtivaAjuizada((obj[19] != null) && (((BigDecimal) obj[19]).compareTo(BigDecimal.ZERO) > 0));
                    resultadoParcela.setDividaIsDividaAtiva(resultadoParcela.getDividaAtiva());
                    resultadoParcela.setUtilizaCache(false);

                    if (resultadoParcela.getIdCalculo() != null) {
                        calculadorAcrescimos.preencherValoresAcrescimosImpostoTaxaResultadoParcela(resultadoParcela, new Date(), null, null, null, null, null);
                    }

                    selecionado.getLinha().append(formatarValorArquivo(resultadoParcela.getValorImposto().add(resultadoParcela.getValorTaxa())));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(formatarValorArquivo(resultadoParcela.getValorJuros()));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(formatarValorArquivo(resultadoParcela.getValorMulta()));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(formatarValorArquivo(resultadoParcela.getValorHonorarios()));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(formatarValorArquivo(resultadoParcela.getValorCorrecao()));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(formatarValorArquivo(resultadoParcela.getValorImposto()
                        .add(resultadoParcela.getValorTaxa()).add(resultadoParcela.getValorJuros())
                        .add(resultadoParcela.getValorMulta()).add(resultadoParcela.getValorCorrecao())
                        .add(resultadoParcela.getValorHonorarios())));
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(obj[11] != null ? SituacaoParcela.valueOf((String) obj[11]).getCodigo() : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(obj[12] != null ? obj[12] : "");
                    selecionado.adicionarSeparador();
                    selecionado.getLinha().append(obj[13] != null ? obj[13] : "");
                    selecionado.adicionarSeparador();
                    formataNomeSituacaoParcelaArquivoDebitoGeralTributario(selecionado, obj);
                    finalizarLinhaAumentandoProcessado(selecionado);
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao extrair os dados de débitos gerais para o BI. {}", ex.getMessage());
            logger.debug("Detalhes do erro ao extrair os dados de débitos gerais para o BI.", ex);
            throw ex;
        }
    }

    void formataNomeSituacaoParcelaArquivoDebitoGeralTributario(ExportacaoArquivoBI selecionado, Object[] obj) {
        String situacao;
        boolean ajuizado = (obj[19] != null) && (((BigDecimal) obj[19]).compareTo(BigDecimal.ZERO) > 0);
        boolean protestado = (obj[23] != null) && (((BigDecimal) obj[23]).compareTo(BigDecimal.ZERO) > 0);
        if (protestado) {
            situacao = "DIVIDA ATIVA PROTESTADA ";
        } else if (ajuizado) {
            situacao = "DIVIDA ATIVA AJUIZADA ";
        } else {
            situacao = "DIVIDA ATIVA ";
        }
        selecionado.getLinha().append(situacao + (obj[14] != null ? SituacaoParcela.valueOf((String) obj[14]).getDescricao() : ""));
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarParcelamento(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando parcelamentos");
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            selecionado.getBarraProgressoItens().setMensagens("Recuperando parcelamentos para o exercício de " + exercicio.getAno());
            List<Object[]> parecelamentos = buscarParcelamentosArquivoBI(exercicio);
            adicionarQuantidadeTotal(selecionado, parecelamentos.size());
            for (Object[] obj : parecelamentos) {
                selecionado.getLinha().append(obj[0] != null ? obj[0] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(obj[1] != null ? obj[1] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(obj[2] != null ? obj[2] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(obj[3] != null ? obj[3] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(obj[4] != null ? obj[4] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(obj[5] != null ? DataUtil.getDataFormatada((Date) obj[5], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(formatarValorArquivo(obj[6]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(formatarValorArquivo(obj[7]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(formatarValorArquivo(obj[8]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(formatarValorArquivo(obj[9]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(formatarValorArquivo(obj[10]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(obj[11] != null ? SituacaoParcela.valueOf((String) obj[11]).getCodigo() : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(obj[12] != null ? obj[12] : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(obj[13] != null ? ((String) obj[13]) : "");
                selecionado.adicionarSeparador();
                String tipoCalculo = obj[14] != null ? Calculo.TipoCalculo.valueOf((String) obj[14]).getDescricao() : "";
                selecionado.getLinha().append(tipoCalculo);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(getCodigoTipoUsoParcelamento((String) obj[16]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(obj[17] != null ? SituacaoParcela.valueOf((String) obj[17]).getDescricao() : "");
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private String formatarValorArquivo(Object o) {
        return o != null ? (Util.reaisToString((BigDecimal) o).replace(".", "")).replace(",", ".") : "";
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private String getCodigoTipoUsoParcelamento(String tipoUso) {
        if (!Strings.isNullOrEmpty(tipoUso)) {
            if ("RESIDENCIAL".equals(tipoUso.toUpperCase())) {
                return "1";
            }
            if ("COMERCIAL".equals(tipoUso.toUpperCase())) {
                return "2";
            }
            if ("INDUSTRIAL".equals(tipoUso.toUpperCase())) {
                return "3";
            }
            if ("PRESTAÇÃO DE SERVIÇO".equals(tipoUso.toUpperCase())) {
                return "4";
            }
            if ("HOSPITALAR".equals(tipoUso.toUpperCase())) {
                return "5";
            }
            if ("AGROPECUÁRIA".equals(tipoUso.toUpperCase())) {
                return "6";
            }
            if ("RELIGIOSO".equals(tipoUso.toUpperCase())) {
                return "7";
            }
            if ("SERVIÇO PÚBLICO".equals(tipoUso.toUpperCase())) {
                return "8";
            }
        }
        return "";
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarImoveis(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os Imóveis");
        List<Object[]> imoveis = buscarCadastrosImobiliariosExportacaoBI();
        for (Object[] imovel : imoveis) {
            gerarLinha(selecionado, imovel);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarTiposDeUsoImovelTerreno(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os Tipos de Uso");
        Atributo atributo = atributoFacade.recuperaAtributoComIdentificacao("utilizacao_imovel");
        List<ValorPossivel> valoresOrdenados = new ArrayList<>(atributo.getValoresPossiveis());
        Collections.sort(valoresOrdenados, new Comparator<ValorPossivel>() {
            @Override
            public int compare(ValorPossivel o1, ValorPossivel o2) {
                return o1.getCodigo().compareTo(o2.getCodigo());
            }
        });
        for (ValorPossivel vp : atributo.getValoresPossiveis()) {
            selecionado.getLinha().append(vp.getCodigo());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(vp.getValor());
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarDadosBancarios(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando dados bancários de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> contasBancarias = contaBancariaEntidadeFacade.buscarContasBancariasEntidades(exercicio, selecionado.getDataOperacao());
            adicionarQuantidadeTotal(selecionado, contasBancarias.size());
            for (Object[] conta : contasBancarias) {
                selecionado.getLinha().append(conta[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[2]);
                selecionado.adicionarSeparador();
                TipoContaBancaria tipoConta = TipoContaBancaria.valueOf((String) conta[3]);
                switch (tipoConta) {
                    case APLICACAO_CORRENTE:
                    case APLICACAO_VINCULADA:
                        selecionado.getLinha().append("1");
                        break;
                    default:
                        selecionado.getLinha().append("2");
                        break;
                }
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[4] != null ? DataUtil.getDataFormatada((Date) conta[4], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[5] != null ? DataUtil.getDataFormatada((Date) conta[5], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(conta[6]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) conta[7]).replace(".", ""));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[8] != null ? ((String) conta[8]).replace(".", "").replace("-", "").replace("/", "") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[9]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[10] != null ? ((String) conta[10]).replace(".", "") : "");
                selecionado.adicionarSeparador();
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarContasFinanceiras(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando contas financeiras de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> contasBancarias = contaBancariaEntidadeFacade.buscarContasFinanceiras(exercicio, selecionado.getDataOperacao());
            adicionarQuantidadeTotal(selecionado, contasBancarias.size());
            for (Object[] conta : contasBancarias) {
                selecionado.getLinha().append(conta[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[2]);
                selecionado.adicionarSeparador();
                TipoContaBancaria tipoContaBancaria = TipoContaBancaria.valueOf((String) conta[3]);
                switch (tipoContaBancaria) {
                    case APLICACAO_CORRENTE:
                    case APLICACAO_VINCULADA:
                        selecionado.getLinha().append("1");
                        break;
                    default:
                        selecionado.getLinha().append("2");
                        break;
                }
                selecionado.adicionarSeparador();
                TipoContaFinanceira tipoConta = TipoContaFinanceira.valueOf((String) conta[4]);
                selecionado.getLinha().append(TipoContaFinanceira.APLICACAO.equals(tipoConta) ? "1" : "2");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[4] != null ? DataUtil.getDataFormatada((Date) conta[5], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[5] != null ? DataUtil.getDataFormatada((Date) conta[6], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(conta[7]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(((String) conta[8]).replace(".", ""));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[9] != null ? ((String) conta[9]).replace(".", "").replace("-", "").replace("/", "") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[10]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[11] != null ? ((String) conta[11]).replace(".", "") : "");
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarMovimentoContasBancarias(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando movimento das contas de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> contasBancarias = contaBancariaEntidadeFacade.buscarMovimentoContas(exercicio, false);
            adicionarQuantidadeTotal(selecionado, contasBancarias.size());
            for (Object[] conta : contasBancarias) {
                selecionado.getLinha().append(conta[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[2]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[3] != null ? DataUtil.getDataFormatada((Date) conta[3], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[4]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(conta[5]));
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarBancos(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando bancos de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> contasBancarias = contaBancariaEntidadeFacade.buscarBancos(exercicio);
            adicionarQuantidadeTotal(selecionado, contasBancarias.size());
            for (Object[] conta : contasBancarias) {
                selecionado.getLinha().append(conta[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[1]);
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarMovimentoContasFinanceiras(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando movimento das contas de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> contasBancarias = contaBancariaEntidadeFacade.buscarMovimentoContas(exercicio, true);
            adicionarQuantidadeTotal(selecionado, contasBancarias.size());
            for (Object[] conta : contasBancarias) {
                selecionado.getLinha().append(conta[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[2]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[3] != null ? DataUtil.getDataFormatada((Date) conta[3], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[4]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(conta[5]));
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarSaldosDasContasBancarias(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando saldos das contas de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> contasBancarias = contaBancariaEntidadeFacade.buscarSaldosContas(exercicio, false);
            adicionarQuantidadeTotal(selecionado, contasBancarias.size());
            for (Object[] conta : contasBancarias) {
                selecionado.getLinha().append(conta[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[2]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[3] != null ? DataUtil.getDataFormatada((Date) conta[3], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(conta[4]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[5]);
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarSaldosDasContasFinanceiras(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando saldos das contas de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> contasBancarias = contaBancariaEntidadeFacade.buscarSaldosContas(exercicio, true);
            adicionarQuantidadeTotal(selecionado, contasBancarias.size());
            for (Object[] conta : contasBancarias) {
                selecionado.getLinha().append(conta[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[2]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[3] != null ? DataUtil.getDataFormatada((Date) conta[3], "ddMMyyyy") : "");
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(conta[4]));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(conta[5]);
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarEmpenhos(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando empenhos de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> empenhos = empenhoFacade.buscarEmpenhos(exercicio, selecionado.getDataOperacao());
            adicionarQuantidadeTotal(selecionado, empenhos.size());
            for (Object[] empenho : empenhos) {
                popularExecucaoDespesa(selecionado, empenho);
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarLiquidacoes(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando liquidações de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> liquidacoes = liquidacaoFacade.buscarLiquidacoes(exercicio, selecionado.getDataOperacao());
            adicionarQuantidadeTotal(selecionado, liquidacoes.size());
            for (Object[] liquidacao : liquidacoes) {
                popularExecucaoDespesa(selecionado, liquidacao);
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarPagamentos(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando pagamentos de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> pagamentos = pagamentoFacade.buscarPagamentos(exercicio, selecionado.getDataOperacao(), CategoriaOrcamentaria.NORMAL);
            adicionarQuantidadeTotal(selecionado, pagamentos.size());
            for (Object[] pagamento : pagamentos) {
                popularExecucaoDespesa(selecionado, pagamento);
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarRestos(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando Restos a Pagar Pagos de " + selecionado.getExercicioInicial() + " até " + selecionado.getExercicioFinal());
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            List<Object[]> pagamentos = pagamentoFacade.buscarPagamentos(exercicio, selecionado.getDataOperacao(), CategoriaOrcamentaria.RESTO);
            adicionarQuantidadeTotal(selecionado, pagamentos.size());
            for (Object[] pagamento : pagamentos) {
                popularExecucaoDespesa(selecionado, pagamento);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(pagamento[14]);
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarValoresLancamentoIPTU(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os I.P.T.U Lançados para Imposto e Taxas");
        List<Exercicio> exercicios = exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(),
            selecionado.getExercicioFinal().getAno(), false);
        for (Exercicio exercicio : exercicios) {
            selecionado.getBarraProgressoItens().setMensagens("Recuperando os I.P.T.U Lançados para Imposto e Taxas" +
                " do exercício de " + exercicio.getAno());

            List<Object[]> valoresLancados = buscarValoresLancadosIPTUExercicio(exercicio);
            adicionarQuantidadeTotal(selecionado, valoresLancados.size() * 2);
            preecherValoresLancamentoIPTU(selecionado, valoresLancados);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Object[]> buscarValoresLancadosIPTUEmDividasTaxasDiversasDoExercicio(Exercicio exercicio) {
        String sql = " select\n" +
            "    exer.ano as ano,\n" +
            "    ci.inscricaoCadastral as matricula,\n" +
            "    vd.valor as valorinicial,\n" +
            "    vd.valor as valoralterado,\n" +
            "    case when exists (select ct.id from construcao ct where ct.imovel_id = ci.id and coalesce(ct.cancelada,0) = 0)\n" +
            "        then '1' else '2' end as codigoTipoIptu,\n" +
            "    (select max(vp.codigo)\n" +
            "        from construcao cons\n" +
            "       inner join construcao_valoratributo vacons on vacons.construcao_id = cons.id\n" +
            "       inner join atributo atributo on atributo.id = vacons.atributos_key and atributo.identificacao = 'utilizacao_imovel'\n" +
            "       inner join valoratributo va on va.id = vacons.atributos_id\n" +
            "       inner join valorpossivel vp on vp.id = va.valordiscreto_id\n" +
            "     where cons.imovel_id = ci.id) as codigotipouso,\n" +
            "    null as codigoisencao,\n" +
            "    (select max(vp.codigo)\n" +
            "        from lote_valoratributo civa\n" +
            "       inner join valoratributo va on va.ID = civa.atributos_id\n" +
            "       inner join valorpossivel vp on vp.id = va.valordiscreto_id\n" +
            "       inner join atributo on atributo.id = civa.atributos_key\n" +
            "       inner join lote on lote.id = civa.lote_id\n" +
            "       inner join cadastroimobiliario cad on cad.lote_id = lote.id\n" +
            "     where cad.id = ci.id and atributo.identificacao = 'patrimonio') as patrimonio,\n" +
            "    null as desconto,\n" +
            "    null as tipodesconto,\n" +
            "    (select sum(ivd.valor)\n" +
            "        from itemvalordivida ivd\n" +
            "       inner join itemparcelavalordivida ipvd on ivd.id = ipvd.itemvalordivida_id\n" +
            "       inner join tributo tr on tr.id = ivd.tributo_id\n" +
            "       inner join parcelavalordivida pvd on pvd.id = ipvd.parcelavalordivida_id\n" +
            "       inner join opcaopagamento op on op.id = pvd.opcaopagamento_id\n" +
            "     where pvd.valordivida_id = vd.id and tr.tipotributo = :tipoImposto) as valorImposto,\n" +
            "    (select sum(ivd.valor)\n" +
            "        from ItemValorDivida ivd\n" +
            "       inner join itemparcelavalordivida ipvd on ivd.id = ipvd.itemvalordivida_id\n" +
            "       inner join tributo tr on tr.id = ivd.tributo_id\n" +
            "       inner join parcelavalordivida pvd on pvd.id = ipvd.parcelavalordivida_id\n" +
            "     where pvd.valordivida_id = vd.id and tr.tipotributo = :tipoTaxa) as valorTaxa,\n" +
            "    (select pvd.vencimento\n" +
            "        from parcelavalordivida pvd\n" +
            "     where pvd.valordivida_id = vd.id" +
            "     order by pvd.vencimento\n" +
            "     fetch first 1 rows only) as vencimento,\n" +
            "    (select lb.datapagamento\n" +
            "        from lotebaixa lb\n" +
            "       inner join itemlotebaixa ilb on lb.id = ilb.lotebaixa_id\n" +
            "       inner join dam on ilb.dam_id = dam.id\n" +
            "       inner join itemdam idam on dam.id = idam.dam_id\n" +
            "       inner join parcelavalordivida pvd on idam.parcela_id = pvd.id\n" +
            "       inner join opcaopagamento op on pvd.opcaopagamento_id = op.id\n" +
            "     where pvd.valordivida_id = vd.id\n" +
            "       and lb.situacaolotebaixa in (:situacoesBaixado)\n" +
            "     order by lb.datapagamento\n" +
            "     fetch first 1 rows only) as datapagamento\n" +
            "   from calculo calc\n" +
            "  inner join valordivida vd on vd.calculo_id = calc.id\n" +
            "  inner join cadastroimobiliario ci on ci.id = calc.cadastro_id\n" +
            "  inner join exercicio exer on exer.id = vd.exercicio_id\n" +
            "where exer.ano = :exercicio\n" +
            "  and calc.tipocalculo in (:dividasDiversas, :taxasDiversas)\n" +
            "  and exists (select 1\n" +
            "                 from itemvalordivida ivd\n" +
            "              where ivd.valordivida_id = vd.id\n" +
            "                and ivd.tributo_id in (:tributos))\n" +
            "  and not regexp_like(ci.inscricaoCadastral, '[a-z]')";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoImposto", Tributo.TipoTributo.IMPOSTO.name());
        q.setParameter("tipoTaxa", Tributo.TipoTributo.TAXA.name());
        q.setParameter("situacoesBaixado", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(),
            SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        q.setParameter("exercicio", exercicio.getAno());
        q.setParameter("dividasDiversas", Calculo.TipoCalculo.DIVIDA_DIVERSA.name());
        q.setParameter("taxasDiversas", Calculo.TipoCalculo.TAXAS_DIVERSAS.name());
        q.setParameter("tributos", buscarTributosIptu().stream().map(Tributo::getId).collect(Collectors.toList()));
        return (List<Object[]>) q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Object[]> buscarValoresLancadosIPTUExercicio(Exercicio exercicio) {
        List<Object[]> retorno = Lists.newArrayList();
        String sql = "select " +
            " exer.ano as ano, " +
            " ci.inscricaoCadastral as matricula,  " +
            " vd.valor as valorinicial, " +
            " vd.valor as valoralterado, " +
            " case when exists (select ct.id from construcao ct where ct.imovel_id = ci.id and coalesce(ct.cancelada,0) = 0) " +
            " then '1' else '2' end as codigoTipoIptu, " +
            " (select max(vp.codigo) from construcao cons  " +
            "  inner join construcao_valoratributo vacons on vacons.construcao_id = cons.id  " +
            "  inner join atributo atributo on atributo.id = vacons.atributos_key and atributo.identificacao = 'utilizacao_imovel'  " +
            "  inner join valoratributo va on va.id = vacons.atributos_id  " +
            "  inner join valorpossivel vp on vp.id = va.valordiscreto_id  " +
            "  where cons.imovel_id = ci.id) as codigotipouso, " +
            " isencao.id as codigoisencao, " +
            " (select max(vp.codigo) from lote_valoratributo civa " +
            "  inner join valoratributo va on va.ID = civa.atributos_id " +
            "  inner join valorpossivel vp on vp.id = va.valordiscreto_id " +
            "  inner join atributo on atributo.id = civa.atributos_key " +
            "  inner join lote on lote.id = civa.lote_id " +
            "  inner join cadastroimobiliario cad on cad.lote_id = lote.id " +
            "  where cad.id = ci.id and atributo.identificacao = 'patrimonio') as patrimonio," +
            " (select max(desconto.desconto || ';' || desconto.tipo) from descontoitemparcela desconto " +
            "  inner join itemparcelavalordivida ipvd on desconto.itemparcelavalordivida_id = ipvd.id " +
            "  inner join parcelavalordivida pvd on pvd.id = ipvd.parcelavalordivida_id " +
            "  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "  inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
            "  where pvd.valordivida_id = vd.id and op.promocional = 1 " +
            "  and spvd.situacaoparcela not in (:situacoesExcluidas)) as desconto, " +
            " (select listagg(tr.tipotributo, ',') within group (order by tr.tipotributo) as tipo from descontoitemparcela desconto " +
            "  inner join itemparcelavalordivida ipvd on desconto.itemparcelavalordivida_id = ipvd.id " +
            "  inner join itemvalordivida ivd on ivd.id = ipvd.itemvalordivida_id " +
            "  inner join tributo tr on tr.id = ivd.tributo_id " +
            "  inner join parcelavalordivida pvd on pvd.id = ipvd.parcelavalordivida_id " +
            "  inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
            "  where pvd.valordivida_id = vd.id and op.promocional = 1) as tipodesconto, " +
            " (select sum(ivd.valor) from itemvalordivida ivd " +
            "  inner join itemparcelavalordivida ipvd on ivd.id = ipvd.itemvalordivida_id " +
            "  inner join tributo tr on tr.id = ivd.tributo_id " +
            "  inner join parcelavalordivida pvd on pvd.id = ipvd.parcelavalordivida_id " +
            "  inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
            "  where pvd.valordivida_id = vd.id and op.promocional = 1 and tr.tipotributo = 'IMPOSTO') as valorImposto, " +
            " (select sum(ivd.valor) from ItemValorDivida ivd " +
            "  inner join itemparcelavalordivida ipvd on ivd.id = ipvd.itemvalordivida_id " +
            "  inner join tributo tr on tr.id = ivd.tributo_id " +
            "  inner join parcelavalordivida pvd on pvd.id = ipvd.parcelavalordivida_id " +
            "  inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
            "  where pvd.valordivida_id = vd.id and op.PROMOCIONAL = 1 and tr.tipotributo = 'TAXA') as valorTaxa , " +
            " (select pvd.vencimento from parcelavalordivida pvd " +
            "  inner join opcaopagamento op on pvd.opcaopagamento_id = op.id " +
            "  where pvd.valordivida_id = vd.id " +
            "  and op.promocional = 1) as vencimento, " +
            " (select lb.datapagamento from lotebaixa lb " +
            "  inner join itemlotebaixa ilb on lb.id = ilb.lotebaixa_id " +
            "  inner join dam on ilb.dam_id = dam.id " +
            "  inner join itemdam idam on dam.id = idam.dam_id " +
            "  inner join parcelavalordivida pvd on idam.parcela_id = pvd.id " +
            "  inner join opcaopagamento op on pvd.opcaopagamento_id = op.id " +
            "  where pvd.valordivida_id = vd.id " +
            "  and op.promocional = 1 " +
            "  and lb.situacaolotebaixa in (:situacoesBaixado)" +
            " order by lb.datapagamento fetch first 1 rows only) as datapagamento" +
            " from calculoiptu iptu " +
            " inner join valordivida vd on vd.calculo_id = iptu.id " +
            " inner join cadastroimobiliario ci on ci.id = iptu.cadastroimobiliario_id " +
            " inner join exercicio exer on exer.id = vd.exercicio_id  " +
            " left join isencaocadastroimobiliario isencao on isencao.id = iptu.isencaocadastroimobiliario_id " +
            " where not exists (select vd2.id from valordivida vd2 " +
            " inner join parcelavalordivida pvd on pvd.valordivida_id = vd2.ID " +
            " inner join situacaoparcelavalordivida situacao on situacao.id = pvd.situacaoatual_id " +
            " inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
            " where vd2.ID = vd.id and op.promocional <> 1 " +
            " and situacao.situacaoparcela in (:situacoesExcluidas))  " +
            " and exer.ano = :exercicio  " +
            " and not regexp_like(ci.inscricaoCadastral, '[a-z]') ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getAno());
        q.setParameter("situacoesExcluidas", getSituacoesExcluidas(null));
        q.setParameter("situacoesBaixado", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));

        List<Object[]> valoresLancadosIPTU = (List<Object[]>) q.getResultList();
        if (!valoresLancadosIPTU.isEmpty()) {
            retorno.addAll(valoresLancadosIPTU);
        }
        List<Object[]> valoresLancadosIPTUEmDividasTaxasDiversas =
            buscarValoresLancadosIPTUEmDividasTaxasDiversasDoExercicio(exercicio);
        if (!valoresLancadosIPTU.isEmpty()) {
            retorno.addAll(valoresLancadosIPTUEmDividasTaxasDiversas);
        }
        return retorno;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void preecherValoresLancamentoIPTU(ExportacaoArquivoBI selecionado, List<Object[]> valoresLancados) {
        for (Object[] valores : valoresLancados) {
            for (Tributo.TipoTributo tipoTributo : Tributo.TipoTributo.getTipoImpostoTaxa()) {
                BigDecimal valorImposto = valores[10] != null ? (BigDecimal) valores[10] : BigDecimal.ZERO;
                BigDecimal valorTaxa = valores[11] != null ? (BigDecimal) valores[11] : BigDecimal.ZERO;
                Date vencimento = valores[12] != null ? DataUtil.dataSemHorario((Date) valores[12]) : null;
                Date dataPagamento = valores[13] != null ? DataUtil.dataSemHorario((Date) valores[13]) : null;

                BigDecimal desconto = BigDecimal.ZERO;
                DescontoItemParcela.Tipo tipoDescontoItemParcela = null;

                if (valores[8] != null) {
                    try {
                        String[] split = ((String) valores[8]).split(";");
                        desconto = split[0] != null ? new BigDecimal(split[0].replace(".", "").replace(",", ".")) : BigDecimal.ZERO;
                        tipoDescontoItemParcela = split[1] != null ? DescontoItemParcela.Tipo.valueOf(split[1]) : null;
                    } catch (Exception ex) {
                        logger.error("Erro ao pegar o valor do desconto: ", ex);
                    }
                }

                String tipoDesconto = valores[9] != null ? (String) valores[9] : "";

                BigDecimal valorComDesconto = BigDecimal.ZERO;
                BigDecimal valorSemDesconto;
                if (tipoTributo.isImposto()) {
                    valorSemDesconto = valorImposto;
                } else {
                    valorSemDesconto = valorTaxa;
                }

                if (tipoDesconto.contains(Tributo.TipoTributo.IMPOSTO.name()) && tipoTributo.isImposto()) {
                    valorComDesconto = valorComDesconto.add(valorImposto);
                }
                if (tipoDesconto.contains(Tributo.TipoTributo.TAXA.name()) && tipoTributo.isTaxa()) {
                    valorComDesconto = valorComDesconto.add(valorTaxa);
                }

                if (desconto.compareTo(BigDecimal.ZERO) > 0) {
                    if (tipoDescontoItemParcela != null) {
                        if (DescontoItemParcela.Tipo.VALOR.equals(tipoDescontoItemParcela)) {
                            valorComDesconto = valorSemDesconto.subtract(desconto);
                        } else {
                            valorComDesconto = valorSemDesconto.subtract(valorComDesconto.multiply(desconto.divide(BigDecimal.valueOf(100), 2,
                                RoundingMode.HALF_UP)));
                        }
                    }
                }

                if (valorComDesconto.compareTo(BigDecimal.ZERO) == 0) {
                    valorComDesconto = valorSemDesconto;
                }

                selecionado.getLinha().append(valores[0]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(valores[1]);
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(valorSemDesconto));
                selecionado.adicionarSeparador();
                selecionado.getLinha().append(adicionarMascaraValor(valorComDesconto));
                selecionado.adicionarSeparador();
                if (tipoTributo.isImposto()) {
                    selecionado.getLinha().append(valores[4]);
                } else {
                    if (((Character) valores[4]).equals('1')) {
                        selecionado.getLinha().append("3");
                    } else {
                        selecionado.getLinha().append("4");
                    }
                }
                selecionado.adicionarSeparador();
                if (valores[5] != null) {
                    selecionado.getLinha().append(valores[5]);
                }
                selecionado.adicionarSeparador();
                BigDecimal isencao = valores[6] != null ? (BigDecimal) valores[6] : null;
                int imunidade = valores[7] != null ? ((BigDecimal) valores[7]).intValue() : 1;
                if (isencao != null) {
                    selecionado.getLinha().append("1");
                } else if (imunidade != 1) {
                    selecionado.getLinha().append("2");
                } else {
                    selecionado.getLinha().append("0");
                }
                finalizarLinhaAumentandoProcessado(selecionado);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<VOValoresPagosIptuBI> consultarValoresPagosDeIPTU(Exercicio exercicio) {
        String sql = " select id, matricula, ano, mes, cotaunica, to_char(datapagamento, 'ddMMyyyy'), codigotipoiptu, dividaativa, " +
            "                 sum(imposto) as imposto, sum(taxa) as taxa, sum(juros) as juros, sum(multa) as multa, sum(correcao) as correcao " +
            " from (select distinct idam.id, ci.inscricaocadastral as matricula, ex.ano as ano, extract(month from pvd.vencimento) as mes, " +
            "                       case when op.promocional = 1 then '1' else '2' end as cotaunica, " +
            "                       lb.datapagamento as datapagamento, " +

            "                       coalesce((select sum(coalesce(ipvd2.valor, 0) - case when lb.datapagamento <= pvd.vencimento" +
            "                                                                            then (case when desconto.tipo = :tipoValor" +
            "                                                                                  then coalesce(desconto.desconto, 0)" +
            "                                                                                  else (coalesce(desconto.desconto, 0) / 100) *" +
            "                                                                                  coalesce(ipvd2.valor, 0) end ) else 0 end) " +
            "                       from itemparcelavalordivida ipvd2 " +
            "                       inner join itemvalordivida ivd on ipvd2.itemvalordivida_id = ivd.id " +
            "                       inner join tributo trib on ivd.tributo_id = trib.id " +
            "                       inner join itemdam idam2 on ipvd2.parcelavalordivida_id = idam2.parcela_id " +
            "                       left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id " +
            "                       where ipvd2.parcelavalordivida_id = pvd.id " +
            "                       and idam2.id = idam.id                     " +
            "                       and trib.id in (:tributos_imposto) " +
            "                       and trib.tipotributo = :tipoImposto), 0) as imposto, " +

            "                       coalesce((select sum(coalesce(ipvd2.valor, 0) - case when lb.datapagamento <= pvd.vencimento" +
            "                                                                            then (case when desconto.tipo = :tipoValor" +
            "                                                                                  then coalesce(desconto.desconto, 0)" +
            "                                                                                  else (coalesce(desconto.desconto, 0) / 100) *" +
            "                                                                                  coalesce(ipvd2.valor, 0) end ) else 0 end) " +
            "                       from itemparcelavalordivida ipvd2 " +
            "                       inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id " +
            "                       inner join tributo trib on ivd2.tributo_id = trib.id " +
            "                       inner join itemdam idam2 on ipvd2.parcelavalordivida_id = idam2.parcela_id " +
            "                       left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id " +
            "                       where ipvd2.parcelavalordivida_id = pvd.id " +
            "                       and idam2.id = idam.id                     " +
            "                       and trib.id in (:tributos_taxa) " +
            "                       and trib.tipotributo = :tipoTaxa), 0) as taxa, " +

            "                      coalesce(idam.juros, 0) - case when cal.tipocalculo = 'PARCELAMENTO' then coalesce((select sum(case when lb.datapagamento <= pvd.vencimento " +
            "                                                                            then (case when desconto.tipo = 'VALOR' " +
            "                                                                                  then coalesce(desconto.desconto, 0) " +
            "                                                                                  else (coalesce(desconto.desconto, 0) / 100) * " +
            "                                                                                  coalesce(ipvd2.valor, 0) end ) else 0 end) " +
            "                       from itemparcelavalordivida ipvd2 " +
            "                       inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id " +
            "                       inner join tributo trib on ivd2.tributo_id = trib.id " +
            "                       inner join itemdam idam2 on ipvd2.parcelavalordivida_id = idam2.parcela_id " +
            "                       left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id " +
            "                       where ipvd2.parcelavalordivida_id = pvd.id " +
            "                       and idam2.id = idam.id " +
            "                       and trib.tipotributo = 'JUROS'), 0) else 0 end as juros, " +

            "                      coalesce(idam.multa, 0) - case when cal.tipocalculo = 'PARCELAMENTO' then coalesce((select sum(case when lb.datapagamento <= pvd.vencimento " +
            "                                                                            then (case when desconto.tipo = 'VALOR' " +
            "                                                                                  then coalesce(desconto.desconto, 0) " +
            "                                                                                  else (coalesce(desconto.desconto, 0) / 100) * " +
            "                                                                                  coalesce(ipvd2.valor, 0) end ) else 0 end) " +
            "                       from itemparcelavalordivida ipvd2 " +
            "                       inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id " +
            "                       inner join tributo trib on ivd2.tributo_id = trib.id " +
            "                       inner join itemdam idam2 on ipvd2.parcelavalordivida_id = idam2.parcela_id " +
            "                       left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id " +
            "                       where ipvd2.parcelavalordivida_id = pvd.id " +
            "                       and idam2.id = idam.id " +
            "                       and trib.tipotributo = 'MULTA'), 0) else 0 end as multa, " +

            "                      coalesce(idam.correcaomonetaria, 0) - case when cal.tipocalculo = 'PARCELAMENTO' then coalesce((select sum(case when lb.datapagamento <= pvd.vencimento " +
            "                                                                            then (case when desconto.tipo = 'VALOR' " +
            "                                                                                  then coalesce(desconto.desconto, 0) " +
            "                                                                                  else (coalesce(desconto.desconto, 0) / 100) * " +
            "                                                                                  coalesce(ipvd2.valor, 0) end ) else 0 end) " +
            "                       from itemparcelavalordivida ipvd2 " +
            "                       inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id " +
            "                       inner join tributo trib on ivd2.tributo_id = trib.id " +
            "                       inner join itemdam idam2 on ipvd2.parcelavalordivida_id = idam2.parcela_id " +
            "                       left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id " +
            "                       where ipvd2.parcelavalordivida_id = pvd.id " +
            "                       and idam2.id = idam.id " +
            "                       and trib.tipotributo = 'CORRECAO'), 0) else 0 end as correcao, " +

            "                      case when exists (select ct.id from construcao ct where ct.imovel_id = ci.id and coalesce(ct.cancelada,0) = 0) " +
            "                      then '1' else '2' end  as codigoTipoIptu, " +
            "                      cast(pvd.dividaativa as varchar(1)) as dividaativa " +

            "      from lotebaixa lb " +
            "      inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id " +
            "      inner join dam on dam.id = ilb.dam_id " +
            "      inner join itemdam idam on idam.dam_id = dam.id " +
            "      inner join parcelavalordivida pvd on pvd.id = idam.parcela_id " +
            "      inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
            "      inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "      inner join exercicio ex on ex.id = vd.exercicio_id " +
            "      inner join calculo cal on cal.id = vd.calculo_id " +
            "      inner join cadastroimobiliario ci on ci.id = cal.cadastro_id " +

            "      where extract(year from lb.datapagamento) = :ano " +
            "      and lb.situacaolotebaixa in (:baixado, :baixado_inconsistente) " +
            "      and length(trim(translate(ci.inscricaocadastral, '0123456789', ' '))) is null " +
            "      and exists(select ipvd.id from itemparcelavalordivida ipvd " +
            "                  inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id" +
            "                  where ipvd.parcelavalordivida_id = pvd.id " +
            "                  and ivd.tributo_id in (:lista_tributos)) " +
            "      order by ci.inscricaocadastral, extract(year from lb.datapagamento), extract(month from lb.datapagamento)) " +
            " group by id, matricula, ano, mes, cotaunica, datapagamento, codigotipoiptu, dividaativa ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("baixado", SituacaoLoteBaixa.BAIXADO.name());
        q.setParameter("baixado_inconsistente", SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name());
        q.setParameter("tipoImposto", Tributo.TipoTributo.IMPOSTO.name());
        q.setParameter("tipoTaxa", Tributo.TipoTributo.TAXA.name());
        q.setParameter("lista_tributos", buscarTributosParaIPTU(Lists.newArrayList(Tributo.TipoTributo.IMPOSTO.name(), Tributo.TipoTributo.TAXA.name())));
        q.setParameter("tributos_imposto", buscarTributosParaIPTU(Tributo.TipoTributo.IMPOSTO.name()));
        q.setParameter("tributos_taxa", buscarTributosParaIPTU(Tributo.TipoTributo.TAXA.name()));
        q.setParameter("tipoValor", DescontoItemParcela.Tipo.VALOR.name());

        List<Object[]> valores = q.getResultList();
        List<VOValoresPagosIptuBI> valoresPagos = Lists.newArrayList();

        if (valores != null && !valores.isEmpty()) {
            for (Object[] valorPago : valores) {
                VOValoresPagosIptuBI vo = new VOValoresPagosIptuBI(valorPago);
                if ((vo.getImposto() != null && vo.getImposto().compareTo(BigDecimal.ZERO) > 0) ||
                    (vo.getTaxa() != null && vo.getTaxa().compareTo(BigDecimal.ZERO) > 0)) {

                    valoresPagos.add(new VOValoresPagosIptuBI(valorPago));
                }
            }
        }
        return valoresPagos;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarValoresIPTUPago(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os I.P.T.U Pagos");
        for (Exercicio exercicio : exercicioFacade.buscarPorIntervaloDeAno(selecionado.getExercicioInicial().getAno(), selecionado.getExercicioFinal().getAno(), false)) {
            selecionado.getBarraProgressoItens().setMensagens("Recuperando os I.P.T.U Pagos para o exercício de " + exercicio.getAno());
            List<VOValoresPagosIptuBI> valoresPagos = calcularJurosMultaCorrecao(exercicio);
            adicionarQuantidadeTotal(selecionado, valoresPagos.size());
            ordenarValoresPagosIptu(valoresPagos);
            preencherValoresPagosIPTU(selecionado, valoresPagos);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<VOValoresPagosIptuBI> calcularJurosMultaCorrecao(final Exercicio exercicio) {
        try {
            List<VOValoresPagosIptuBI> valoresPagos = buscarValoresPagosPorAnoAndMes(exercicio);
            List<VOValoresPagosIptuBI> novosValores = Lists.newArrayList();

            for (VOValoresPagosIptuBI valorPago : valoresPagos) {
                if (valorPago.getImposto().compareTo(BigDecimal.ZERO) > 0 && valorPago.getTaxa().compareTo(BigDecimal.ZERO) > 0) {
                    calcularValorProporcional(valorPago);
                }
            }

            for (VOValoresPagosIptuBI valorPago : valoresPagos) {
                if (valorPago.getImposto().compareTo(BigDecimal.ZERO) > 0 && valorPago.getTaxa().compareTo(BigDecimal.ZERO) > 0) {
                    valorPago.setTipoTributo(Tributo.TipoTributo.IMPOSTO);
                    novosValores.add(criarNovoValorPago(valorPago, Tributo.TipoTributo.TAXA, "1".equals(valorPago.getCodigoTipoIptu()) ? "3" : "4"));
                } else if (valorPago.getImposto().compareTo(BigDecimal.ZERO) <= 0) {
                    valorPago.setTipoTributo(Tributo.TipoTributo.TAXA);
                    valorPago.setCodigoTipoIptu("1".equals(valorPago.getCodigoTipoIptu()) ? "3" : "4");
                } else if (valorPago.getTaxa().compareTo(BigDecimal.ZERO) <= 0) {
                    valorPago.setTipoTributo(Tributo.TipoTributo.IMPOSTO);
                }
            }
            valoresPagos.addAll(novosValores);

            return valoresPagos;
        } catch (Exception e) {
            logger.error("Erro ao buscar juros, multa e correcao. ", e);
        }
        return Lists.newArrayList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private VOValoresPagosIptuBI criarNovoValorPago(VOValoresPagosIptuBI valorPago, Tributo.TipoTributo tipoTributo, String codigoTipoIptu) {
        VOValoresPagosIptuBI novoValorPago = new VOValoresPagosIptuBI(valorPago);
        novoValorPago.setTipoTributo(tipoTributo);
        novoValorPago.setCodigoTipoIptu(codigoTipoIptu);
        return novoValorPago;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<VOValoresPagosIptuBI> buscarValoresPagosPorAnoAndMes(final Exercicio exercicio) throws InterruptedException, ExecutionException {
        return consultarValoresPagosDeIPTU(exercicio);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void calcularValorProporcional(VOValoresPagosIptuBI valorPago) {
        BigDecimal valorProporcao = valorPago.getImposto().add(valorPago.getTaxa());

        BigDecimal percentualImposto = valorPago.getImposto().multiply(CEM).divide(valorProporcao, 2, BigDecimal.ROUND_UP);
        BigDecimal percentualTaxa = valorPago.getTaxa().multiply(CEM).divide(valorProporcao, 2, BigDecimal.ROUND_UP);

        valorPago.setJurosImposto(valorPago.getJuros().multiply(percentualImposto).divide(CEM, 2, BigDecimal.ROUND_UP));
        valorPago.setJurosTaxa(valorPago.getJuros().multiply(percentualTaxa).divide(CEM, 2, BigDecimal.ROUND_UP));
        valorPago.setJurosImposto(corrigirValor(valorPago.getJurosImposto(), valorPago.getJurosTaxa(), valorPago.getJuros()));

        valorPago.setMultaImposto(valorPago.getMulta().multiply(percentualImposto).divide(CEM, 2, BigDecimal.ROUND_UP));
        valorPago.setMultaTaxa(valorPago.getMulta().multiply(percentualTaxa).divide(CEM, 2, BigDecimal.ROUND_UP));
        valorPago.setMultaImposto(corrigirValor(valorPago.getMultaImposto(), valorPago.getMultaTaxa(), valorPago.getMulta()));

        valorPago.setCorrecaoImposto(valorPago.getCorrecao().multiply(percentualImposto).divide(CEM, 2, BigDecimal.ROUND_UP));
        valorPago.setCorrecaoTaxa(valorPago.getCorrecao().multiply(percentualTaxa).divide(CEM, 2, BigDecimal.ROUND_UP));
        valorPago.setCorrecaoImposto(corrigirValor(valorPago.getCorrecaoImposto(), valorPago.getCorrecaoTaxa(), valorPago.getCorrecao()));
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private BigDecimal corrigirValor(BigDecimal valorImposto, BigDecimal valorTaxa, BigDecimal valor) {
        while (valorImposto.add(valorTaxa).compareTo(valor) != 0) {
            BigDecimal diferenca = valor.subtract(valorImposto.add(valorTaxa)).setScale(2, RoundingMode.HALF_UP);
            valorImposto = valorImposto.add(diferenca);
        }
        return valorImposto.compareTo(BigDecimal.ZERO) >= 0 ? valorImposto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void ordenarValoresPagosIptu(List<VOValoresPagosIptuBI> valoresPagos) {
        Collections.sort(valoresPagos, new Comparator<VOValoresPagosIptuBI>() {
            @Override
            public int compare(VOValoresPagosIptuBI o1, VOValoresPagosIptuBI o2) {
                return ComparisonChain.start().compare(o1.getMatricula(), o2.getMatricula())
                    .compare(o1.getAno(), o2.getAno())
                    .compare(o1.getMes(), o2.getMes())
                    .compare(o1.getTipoTributo().name(), o2.getTipoTributo().name(), Ordering.<String>natural().reverse())
                    .result();
            }
        });
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void preencherValoresPagosIPTU(ExportacaoArquivoBI selecionado, List<VOValoresPagosIptuBI> valoresPagos) {
        BigDecimal valorImposto = BigDecimal.ZERO;
        BigDecimal valorTaxa = BigDecimal.ZERO;
        BigDecimal valorTipo1 = BigDecimal.ZERO;
        BigDecimal valorTipo2 = BigDecimal.ZERO;
        BigDecimal valorTipo3 = BigDecimal.ZERO;
        BigDecimal valorTipo4 = BigDecimal.ZERO;

        for (VOValoresPagosIptuBI valorPago : valoresPagos) {
            selecionado.getLinha().append(valorPago.getMatricula());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(valorPago.getAno());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(valorPago.getMes());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(valorPago.getCotaUnica());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(valorPago.getDataPagamento());
            selecionado.adicionarSeparador();
            if (!valorPago.isDividaAtivida()) {
                if (valorPago.getTipoTributo().isImposto()) {
                    valorImposto = valorImposto.add(valorPago.getValorTotalPagoPorTipoTributo());
                } else {
                    valorTaxa = valorTaxa.add(valorPago.getValorTotalPagoPorTipoTributo());
                }

                if ("1".equals(valorPago.getCodigoTipoIptu())) {
                    valorTipo1 = valorTipo1.add(valorPago.getValorTotalPagoPorTipoTributo());
                }
                if ("2".equals(valorPago.getCodigoTipoIptu())) {
                    valorTipo2 = valorTipo2.add(valorPago.getValorTotalPagoPorTipoTributo());
                }
                if ("3".equals(valorPago.getCodigoTipoIptu())) {
                    valorTipo3 = valorTipo3.add(valorPago.getValorTotalPagoPorTipoTributo());
                }
                if ("4".equals(valorPago.getCodigoTipoIptu())) {
                    valorTipo4 = valorTipo4.add(valorPago.getValorTotalPagoPorTipoTributo());
                }
            }
            selecionado.getLinha().append(adicionarMascaraValor(!valorPago.isDividaAtivida() ? valorPago.getValorTotalPagoPorTipoTributo() : BigDecimal.ZERO));
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(adicionarMascaraValor(!valorPago.isDividaAtivida() ? valorPago.getJurosPorTipoTributo() : BigDecimal.ZERO));
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(adicionarMascaraValor(!valorPago.isDividaAtivida() ? valorPago.getMultaPorTipoTributo() : BigDecimal.ZERO));
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(adicionarMascaraValor(!valorPago.isDividaAtivida() ? valorPago.getValorPagoPorTipoTributo() : BigDecimal.ZERO));
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(adicionarMascaraValor(!valorPago.isDividaAtivida() ? valorPago.getCorrecaoPorTipoTributo() : BigDecimal.ZERO));
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(adicionarMascaraValor(valorPago.isDividaAtivida() ? valorPago.getValorTotalPagoPorTipoTributo() : BigDecimal.ZERO));
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(valorPago.getCodigoTipoIptu());
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Object[]> buscarDebitosGeraisArquivoBI(Exercicio exercicio) {
        String sql = "select div.codigo as tributo, " +
            "  case when cal.cadastro_id is not null then '1'  " +
            "    when pf.id is not null then '2' " +
            "    when pj.id is not null then '3' " +
            "  end as tipodainscricao, " +
            "  coalesce(ci.inscricaoCadastral, ce.inscricaocadastral, cast(pes.id as varchar(20))) as matricula, " +
            "  ex.ano as ano, " +
            "  pvd.sequenciaparcela as parcela, " +
            "  pvd.vencimento as vencimento, " +
            "  (select sum(ipvd.valor) from itemparcelavalordivida ipvd " +
            "    inner join parcelavalordivida pvd2 on ipvd.parcelavalordivida_id = pvd2.id " +
            "    inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id " +
            "    inner join tributo tr on ivd.tributo_id = tr.id " +
            "    inner join situacaoparcelavalordivida spvd2 on pvd2.situacaoatual_id = spvd2.id " +
            "    where pvd2.id = pvd.id " +
            "      and tr.tipotributo in ('IMPOSTO','TAXA')) as valorprincipal, " +
            "  0 as valorjuros, " +
            "  0 as valormulta, " +
            "  0 as valorhonorario, " +
            "  0 as valorcorrecao, " +
            "  spvd.situacaoparcela as situacaododebito, " +
            "  case when cal.cadastro_id is null then '3' " +
            "       when ci.id is not null then '2' " +
            "       when ce.id is not null then '1' " +
            "   end as origemdodebito, " +
            "  replace(replace(replace(coalesce(pf.cpf, pj.cnpj, ''), '.', ''),'-', ''), '/', '') as cpfcnpjcontribuinte, " +
            "  spvd.situacaoparcela as statusparcela, " +
            "  pvd.id as idParcela, " +
            "  cal.tipoCalculo as tipoCalculo, " +
            "  dva.ACRESCIMO_ID, " +
            "  ex.ano as exercicio, " +
            "  pvd.dividaAtivaAjuizada as ajuizada, " +
            "  cal.dataCalculo as dataCalculo, " +
            "  div.id as idDivida, " +
            "  cal.id as idCalculo, " +
            "  pvd.debitoProtestado as protestado " +
            " from parcelavalordivida pvd " +
            "  inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "  inner join calculo cal on cal.id = vd.calculo_id " +
            "  inner join divida div on div.id = vd.divida_id " +
            "  inner join exercicio ex on ex.id = vd.exercicio_id " +
            "  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "  left join cadastroimobiliario ci on ci.id = cal.cadastro_id " +
            "  left join cadastroeconomico ce on ce.id = cal.cadastro_id " +
            "  inner join calculopessoa cp on cp.calculo_id = cal.id and cp.id = (select max(cp2.id) from calculopessoa cp2 where cp2.calculo_id = cal.id) " +
            "  inner join pessoa pes on pes.id = cp.pessoa_id " +
            "  left join pessoafisica pf on pf.id = pes.id " +
            "  left join pessoajuridica pj on pj.id = pes.id " +
            "  inner join dividaacrescimo dva on dva.divida_id = vd.divida_id AND CURRENT_DATE BETWEEN DVA.INICIOVIGENCIA AND COALESCE(DVA.FINALVIGENCIA, CURRENT_DATE) " +
            " where ex.ano = :ano " +
            "   and cal.tipocalculo = :tipoDividaAtiva " +
            "   and spvd.SITUACAOPARCELA in (:situacoes) " +
            "   and pvd.DIVIDAATIVA = :dividaAtiva " +
            "   and not REGEXP_LIKE(coalesce(ci.inscricaoCadastral, ce.inscricaocadastral, cast(pes.id as varchar(20))), '[a-z]') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("tipoDividaAtiva", Calculo.TipoCalculo.INSCRICAO_DA.name());
        q.setParameter("situacoes", SituacaoParcela.getSituacoesParaExportacaoDebitosGeraisArquivoBI().stream().map(Enum::name).collect(Collectors.toList()));
        q.setParameter("dividaAtiva", 1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Object[]> buscarCadastrosImobiliariosExportacaoBI() {
        Query q = em.createNativeQuery("select ci.inscricaoCadastral as inscricaoFiscal, " +
            "REGEXP_REPLACE(coalesce(pf.cpf,pj.cnpj,''), '[^0-9]', '') as cpfcnpj, " +
            "logra.CODIGO as codigologradouro, " +
            "REGEXP_REPLACE(cI.NUMERO, '[^0-9]', '') as numerologradouro, " +
            "bairro.CODIGO as codigobairro, " +
            "replace(coalesce(pf.NOME,pj.razaoSocial),';',' ') as nome , " +
            "REGEXP_REPLACE(cI.INSCRICAOCADASTRAL, '[^0-9]', '') as matricula, " +
            "cI.ATIVO from cadastroimobiliario cI " +
            "inner join lote lote on lote.id = cI.LOTE_ID " +
            "left join testada testa on testa.LOTE_ID = lote.id " +
            "left join face face on face.id = testa.FACE_ID " +
            "left join logradourobairro logbairro on logbairro.id = face.LOGRADOUROBAIRRO_ID " +
            "left join bairro bairro on bairro.id = logbairro.BAIRRO_ID " +
            "left join logradouro logra on logra.id = logbairro.LOGRADOURO_ID " +
            "inner join propriedade propi on propi.IMOVEL_ID = cI.id and propi.FINALVIGENCIA is null " +
            "inner join pessoa pe on pe.id = propi.pessoa_id " +
            "left join pessoajuridica pj on pj.id = pe.id " +
            "left join pessoafisica pf on pf.id = pe.id " +
            "where propi.id = (select max(pp.id) from propriedade pp where pp.imovel_id = ci.id and pp.FINALVIGENCIA is null) " +
            " and testa.id = (select max(tt.id) from testada tt where tt.LOTE_ID = lote.id and coalesce(tt.principal,0) = 1) " +
            " and not REGEXP_LIKE(ci.INSCRICAOCADASTRAL, '[a-z]') " +
            " order by cI.id");
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object[]> buscarTaxas(Exercicio exercicio) {
        String sql = "select calc.id, \n" +
            "  dam.numeroDam, \n" +
            "  case when ce.id is null and pf.cpf is not null then 2 \n" +
            "       when ce.id is null and pj.cnpj is not null then 3  \n" +
            "  else 1 end as tipoInscricao, \n" +

            "  coalesce(ce.inscricaocadastral, cast(cp.pessoa_id as varchar(20))) as inscricaoCadastral, \n" +
            "  REPLACE(REPLACE(REPLACE(coalesce(pf.cpf, pj.cnpj), '.', ''), '-', ''), '/', '') AS cpfCnpj, \n" +
            "  pvd.sequenciaparcela AS parcela, \n" +
            "  calc.datacalculo AS dataCompetencia, \n" +
            "  (select lb.datapagamento from lotebaixa lb \n" +
            "        inner join itemlotebaixa ilb on lb.id = ilb.lotebaixa_id \n" +
            "        inner join dam on ilb.dam_id = dam.id \n" +
            "        inner join itemdam idam on dam.id = idam.dam_id \n" +
            "        where idam.parcela_id = pvd.id \n" +
            "        and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "        order by lb.datapagamento desc fetch first 1 rows only) as dataPagamento,\n" +
            "  spvd.SITUACAOPARCELA, \n" +

            "  (select max(coalesce(agr.codigo, tr.codigo)) from tributodam tdam \n" +
            "    inner join tributo tr on tr.id = tdam.tributo_id\n" +
            "     left join biagrupadortaxatributo btr on btr.tributo_id = tr.id\n" +
            "     left join biagrupadortaxa agr on agr.ID = btr.biagrupadortaxa_id\n" +
            "    where tdam.itemdam_id = idam.id) as codigoTipo, \n" +

            "  (case when spvd.situacaoparcela = :situacaoCompensado \n" +
            "             then coalesce((select sum(coalesce(ic.valorcompensado, 0) - coalesce(ic.juros, 0) - \n" +
            "                                       coalesce(ic.multa, 0) - coalesce(ic.correcao, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only)), \n" +
            "                           (select sum(coalesce(ic.valorcompensado, 0) - coalesce(ic.juros, 0) - \n" +
            "                                       coalesce(ic.multa, 0) - coalesce(ic.correcao, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only))) \n" +
            "           else \n" +
            "           coalesce((select coalesce(sum(ipvd.valor), 0) \n" +
            "                 from itemparcelavalordivida ipvd \n" +
            "                 inner join parcelavalordivida pvd2 on ipvd.parcelavalordivida_id = pvd2.id \n" +
            "                 inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id \n" +
            "                 inner join tributo tr on ivd.tributo_id = tr.id \n" +
            "                 Inner join itemdam idam on pvd2.id = idam.parcela_id \n" +
            "                 inner join dam on idam.dam_id = dam.id and dam.situacao = :situacaoDam \n" +
            "                 inner join itemlotebaixa ilb on dam.id = ilb.dam_id \n" +
            "                 inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                 where pvd2.id = pvd.id \n" +
            "                 and ilb.lotebaixa_id = (select max(ilb2.lotebaixa_id) from itemlotebaixa ilb2 \n" +
            "                                          inner join lotebaixa lb2 on ilb2.lotebaixa_id = lb2.id and lb2.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                                          where ilb2.DAM_ID = dam.id)\n" +
            "                 and tr.tipotributo in (:tipoImposto,:tipoTaxa) \n" +
            "                 and idam.id = (select s_idm.id \n" +
            "                                from itemdam s_idm \n" +
            "                                inner join dam s_dam on s_dam.id = s_idm.dam_id \n" +
            "                                where s_dam.situacao = :situacaoDam\n" +
            "                                and s_idm.parcela_id = pvd.id \n" +
            "                                order by s_idm.id desc fetch first 1 rows only) \n" +
            "                 ), 0) end) - \n" +
            "                 (case when calc.tipocalculo = :tipoParcelamento then coalesce((select sum((case when desconto.tipo = :tipoValor \n" +
            "                                         then coalesce(desconto.desconto, 0) \n" +
            "                                         else (coalesce(desconto.desconto, 0) / 100) * \n" +
            "                                         coalesce(ipvd2.valor, 0) end )) \n" +
            "                                   from itemparcelavalordivida ipvd2 \n" +
            "                                   inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id \n" +
            "                                   inner join tributo trib on ivd2.tributo_id = trib.id \n" +
            "                                   left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id \n" +
            "                                   where ipvd2.parcelavalordivida_id = pvd.id \n" +
            "                                   and trib.tipotributo in (:tipoImposto,:tipoTaxa)), 0) else 0 end) as valorpago,\n" +

            "  case when spvd.situacaoparcela = :situacaoCompensado \n" +
            "            then coalesce((select sum(coalesce(ic.juros, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only)), \n" +
            "                           (select sum(coalesce(ic.juros, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only))) \n" +
            "            else \n" +
            "            coalesce((select sum(idam.juros)\n" +
            "                  from itemdam idam \n" +
            "                  inner join parcelavalordivida pvd2 on idam.PARCELA_ID = pvd2.id \n" +
            "                  inner join dam on idam.dam_id = dam.id\n" +
            "                  inner join itemlotebaixa ilb on dam.id = ilb.dam_id \n" +
            "                  inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                  where pvd2.id = pvd.id \n" +
            "                    and dam.situacao = :situacaoDam\n" +
            "                  ), 0) end - (case when calc.tipocalculo = :tipoParcelamento then coalesce((select sum((case when desconto.tipo = :tipoValor \n" +
            "                                         then coalesce(desconto.desconto, 0) \n" +
            "                                         else (coalesce(desconto.desconto, 0) / 100) * \n" +
            "                                         coalesce(ipvd2.valor, 0) end )) \n" +
            "                                   from itemparcelavalordivida ipvd2 \n" +
            "                                   inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id \n" +
            "                                   inner join tributo trib on ivd2.tributo_id = trib.id \n" +
            "                                   left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id \n" +
            "                                   where ipvd2.parcelavalordivida_id = pvd.id \n" +
            "                                   and trib.tipotributo = :tipoJuros), 0) else 0 end) as juros, \n" +

            "       case when spvd.situacaoparcela = :situacaoCompensado \n" +
            "            then coalesce((select sum(coalesce(ic.multa, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only)), \n" +
            "                           (select sum(coalesce(ic.multa, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only))) \n" +
            "            else \n" +
            "            coalesce((select sum(idam.multa)\n" +
            "                      from itemdam idam\n" +
            "                      inner join parcelavalordivida pvd2 on idam.parcela_id = pvd2.id \n" +
            "                      inner join dam on idam.dam_id = dam.id\n" +
            "                      inner join itemlotebaixa ilb on dam.id = ilb.dam_id \n" +
            "                      inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                      where pvd2.id = pvd.id \n" +
            "                        and dam.situacao = :situacaoDam \n" +
            "                      ), 0) end  - (case when calc.tipocalculo = :tipoParcelamento then coalesce((select sum((case when desconto.tipo = :tipoValor \n" +
            "                                         then coalesce(desconto.desconto, 0) \n" +
            "                                         else (coalesce(desconto.desconto, 0) / 100) * \n" +
            "                                         coalesce(ipvd2.valor, 0) end )) \n" +
            "                                   from itemparcelavalordivida ipvd2 \n" +
            "                                   inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id \n" +
            "                                   inner join tributo trib on ivd2.tributo_id = trib.id \n" +
            "                                   left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id \n" +
            "                                   where ipvd2.parcelavalordivida_id = pvd.id \n" +
            "                                   and trib.tipotributo = :tipoMulta), 0) else 0 end) as multa, \n" +

            "       (select coalesce(sum(case when sum(ipvd.valor) > 0 and coalesce(pvd2.valor, 0) > 0 \n" +
            "                    then round(coalesce(pvd2.valor, 0) * \n" +
            "                    ((coalesce(sum(ipvd.valor), 0) * 100) / coalesce(pvd2.valor, 0)) / 100, 2) else 0 end), 0) \n" +
            "        from itemparcelavalordivida ipvd \n" +
            "        inner join parcelavalordivida pvd2 on ipvd.parcelavalordivida_id = pvd2.id \n" +
            "        inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id \n" +
            "        inner join tributo tr on ivd.tributo_id = tr.id \n" +
            "        inner join situacaoparcelavalordivida spvd2 on pvd2.situacaoatual_id = spvd2.id \n" +
            "        where pvd2.id = pvd.id \n" +
            "        and tr.tipotributo in (:tipoImposto,:tipoTaxa) \n" +
            "        group by tr.tipotributo, pvd2.valor) as valororiginal, \n" +

            "       case when spvd.situacaoparcela = :situacaoCompensado \n" +
            "            then coalesce((select sum(coalesce(ic.correcao, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only)), \n" +
            "                           (select sum(coalesce(ic.correcao, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only))) \n" +
            "            else \n" +
            "            coalesce((select sum(idam.correcaomonetaria)\n" +
            "                      from itemdam idam \n" +
            "                      inner join parcelavalordivida pvd2 on idam.parcela_id = pvd2.id \n" +
            "                      inner join dam on idam.dam_id = dam.id\n" +
            "                      inner join itemlotebaixa ilb on dam.id = ilb.dam_id \n" +
            "                      inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                      where pvd2.id = pvd.id \n" +
            "                       and dam.situacao = :situacaoDam\n" +
            "                      ), 0) end - (case when calc.tipocalculo = :tipoParcelamento then coalesce((select sum((case when desconto.tipo = :tipoValor \n" +
            "                                         then coalesce(desconto.desconto, 0) \n" +
            "                                         else (coalesce(desconto.desconto, 0) / 100) * \n" +
            "                                         coalesce(ipvd2.valor, 0) end )) \n" +
            "                                   from itemparcelavalordivida ipvd2 \n" +
            "                                   inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id \n" +
            "                                   inner join tributo trib on ivd2.tributo_id = trib.id  \n" +
            "                                   left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id \n" +
            "                                   where ipvd2.parcelavalordivida_id = pvd.id \n" +
            "                                   and trib.tipotributo = :tipoCorrecao), 0) else 0 end) as correcao, \n" +

            "   coalesce(pvd.dividaAtiva,0) as dividaAtiva\n" +
            " from calculo calc \n" +
            " inner join valordivida vd on calc.id = vd.calculo_id \n" +
            " inner join exercicio ex on ex.id = vd.exercicio_id\n" +
            " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id \n" +
            " inner join itemdam idam on pvd.id = idam.parcela_id \n" +
            " inner join dam dam on idam.dam_id = dam.id \n" +
            " inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id \n" +
            " inner join divida div on div.id = vd.divida_id \n" +
            " left join cadastroeconomico ce on ce.id = calc.cadastro_id\n" +
            " inner join calculopessoa cp on cp.calculo_id = calc.id\n" +
            " left join pessoafisica pf on cp.pessoa_id = pf.id \n" +
            " left join pessoajuridica pj on cp.pessoa_id = pj.id \n" +

            " where dam.SITUACAO <> :cancelado\n" +
            " and dam.tipo = :unico\n" +
            " and ex.ano = :exercicio\n" +
            " and spvd.situacaoparcela in (:situacoesIncluidas)\n" +
            " and cp.id = (select max(calPes.id) from calculopessoa calPes where calPes.calculo_id = calc.id)\n" +
            " and not exists (select tdam.id from tributodam tdam \n" +
            "                 where tdam.itemdam_id = idam.id \n" +
            "                  and tdam.tributo_id in (:tributosIptu))\n" +
            " and coalesce(pf.cpf, pj.cnpj) is not null\n" +
            " and div.id not in (:dividasIss) \n" +
            " and not (calc.tipoCalculo in (:dividasDiversas, :taxasDiversas)\n" +
            "          and exists (select 1\n" +
            "                         from itemvalordivida ivd\n" +
            "                      where ivd.valordivida_id = vd.id\n" +
            "                       and ivd.tributo_id in (:tributosDividasTaxasDiversasDescartados)))\n" +
            "order by calc.datacalculo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getAno());
        List<Divida> todasDividasIss = buscarDividasISS(TipoExportacaoArquivoBI.GUIAS_TAXAS);
        List<Long> idsTodasDividasIss = Lists.newArrayList();
        for (Divida dividasIss : todasDividasIss) {
            idsTodasDividasIss.add(dividasIss.getId());
        }
        q.setParameter("dividasIss", idsTodasDividasIss);
        q.setParameter("tributosIptu", buscarTributosParaIPTU(Lists.newArrayList(Tributo.TipoTributo.IMPOSTO.name(),
            Tributo.TipoTributo.TAXA.name())));
        q.setParameter("unico", DAM.Tipo.UNICO.name());
        q.setParameter("cancelado", DAM.Situacao.CANCELADO.name());
        q.setParameter("situacoesIncluidas", getSituacoesIncluidas());
        q.setParameter("tipoImposto", Tributo.TipoTributo.IMPOSTO.name());
        q.setParameter("tipoTaxa", Tributo.TipoTributo.TAXA.name());
        q.setParameter("tipoJuros", Tributo.TipoTributo.JUROS.name());
        q.setParameter("tipoMulta", Tributo.TipoTributo.MULTA.name());
        q.setParameter("tipoCorrecao", Tributo.TipoTributo.CORRECAO.name());
        q.setParameter("tipoValor", DescontoItemParcela.Tipo.VALOR.name());
        q.setParameter("tipoParcelamento", Calculo.TipoCalculo.PARCELAMENTO.name());
        q.setParameter("situacoesLoteBaixa", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(),
            SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        q.setParameter("situacaoDam", DAM.Situacao.PAGO.name());
        q.setParameter("situacaoCompensado", SituacaoParcela.COMPENSACAO.name());
        q.setParameter("dividasDiversas", Calculo.TipoCalculo.DIVIDA_DIVERSA.name());
        q.setParameter("taxasDiversas", Calculo.TipoCalculo.TAXAS_DIVERSAS.name());
        q.setParameter("tributosDividasTaxasDiversasDescartados", getTributosDividasTaxasDiversasDescartados());
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Long> getTributosDividasTaxasDiversasDescartados() {
        List<Tributo> tributos = Lists.newArrayList();
        List<Tributo> tributosIss = buscarTributosIss();
        if (tributosIss != null && !tributosIss.isEmpty()) {
            tributos.addAll(tributosIss);
        }
        List<Tributo> tributosIptu = buscarTributosIptu();
        if (tributosIptu != null && !tributosIptu.isEmpty()) {
            tributos.addAll(tributosIptu);
        }
        return tributos.stream()
            .map(Tributo::getId)
            .collect(Collectors.toList());
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Object[]> buscarParcelamentosArquivoBI(Exercicio exercicio) {
        String sql = " select div.codigo as tributo, " +
            "  case when calculo.cadastro_id is not null then '1'  " +
            "    when pf.id is not null then '2' " +
            "    when pj.id is not null then '3' " +
            "  end as tipodainscricao, " +
            "  coalesce(ci.inscricaoCadastral, ce.inscricaocadastral, cast(pes.id as varchar(20))) as inscricao, " +
            "  ex.ano as ano, " +
            "  pvd.sequenciaparcela as parcela, " +
            "  pvd.vencimento as vencimento, " +
            "  coalesce(dam.valororiginal, pvd.valor) as valorprincipal, " +
            "  coalesce(dam.juros,0) as valorjuros, " +
            "  coalesce(dam.multa,0) as valormulta, " +
            "  coalesce(dam.honorarios,0) as valorhonorario, " +
            "  coalesce(dam.valororiginal, pvd.valor) " +
            "   + coalesce(dam.juros,0) " +
            "   + coalesce(dam.multa,0) " +
            "   +coalesce(dam.honorarios,0) as valortotal, " +
            "  spvd.situacaoparcela as situacaododebito, " +
            "  case when calculo.cadastro_id is null then '3'  " +
            "    when ci.id is not null then '2' " +
            "    when ce.id is not null then '1' " +
            "  end as origemdodebito, " +
            "  replace(replace(replace(coalesce(pf.cpf, pj.cnpj, ''), '.', ''),'-', ''), '/', '') as cpfcnpjcontribuinte, " +
            "  (select max(co.tipocalculo) from calculo co  " +
            "    inner join valordivida vdo on vdo.calculo_id = co.id " +
            "    inner join parcelavalordivida pvdo on pvdo.valordivida_id = vdo.id " +
            "    inner join inscricaodividaparcela idp on idp.parcelavalordivida_id = pvdo.id " +
            "    inner join iteminscricaodividaativa iida on iida.id = idp.iteminscricaodividaativa_id " +
            "    inner join valordivida vdda on vdda.calculo_id = iida.id " +
            "    inner join parcelavalordivida pvdda on pvdda.valordivida_id = vdda.id " +
            "    inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = pvdda.id " +
            "    where ipp.processoparcelamento_id = pp.id) as tipocalculoorigem, " +
            "  case when ci.id is not null then  " +
            "    coalesce((select max('P') from construcao ct where ct.imovel_id = ci.id and coalesce(ct.cancelada,0) = 0),'T') " +
            "  else '' end tipoUso, " +
            " coalesce((select max(vp.VALOR) from construcao_valoratributo civa " +
            "   inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID " +
            "   inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID " +
            "   inner join atributo on atributo.id = civa.ATRIBUTOS_KEY " +
            "   inner join construcao constru on constru.id = civa.CONSTRUCAO_ID " +
            "  where constru.IMOVEL_ID = ci.id and coalesce(constru.cancelada,0) = 0 " +
            "  and atributo.IDENTIFICACAO = 'utilizacao_imovel'),'') as utilizacao_imovel, " +
            "  spvd.situacaoparcela as statusparcela, " +
            "  pp.numero as numeroparcelamento " +
            " from processoparcelamento pp " +
            "  inner join calculo calculo on calculo.id = pp.id " +
            "  inner join valordivida vd on vd.calculo_id = pp.id " +
            "  inner join exercicio ex on ex.id = vd.exercicio_id " +
            "  inner join divida div on div.id = vd.divida_id " +
            "  inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "  left join cadastroimobiliario ci on ci.id = calculo.cadastro_id " +
            "  left join cadastroeconomico ce on ce.id = calculo.cadastro_id " +
            "  inner join calculopessoa cp on cp.calculo_id = pp.id " +
            "  inner join pessoa pes on pes.id = cp.pessoa_id " +
            "  left join pessoafisica pf on pf.id = pes.id " +
            "  left join pessoajuridica pj on pj.id = pes.id " +
            "  left join itemdam idam on idam.parcela_id = pvd.id " +
            "  left join dam dam on dam.id = idam.dam_id and dam.tipo = :tipoDamUnico and dam.situacao <> :situacaoDamCancelado " +
            "where ex.id = :idExercicio " +
            " and pp.situacaoparcelamento <> :situacaoParcelamentoCancelado " +
            " and not REGEXP_LIKE(coalesce(ci.inscricaoCadastral, ce.inscricaocadastral, cast(pes.id as varchar(20))), '[a-z]')";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("situacaoParcelamentoCancelado", SituacaoParcelamento.CANCELADO.name());
        q.setParameter("tipoDamUnico", DAM.Tipo.UNICO.name());
        q.setParameter("situacaoDamCancelado", DAM.Situacao.CANCELADO.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Object[]> buscarCadastrosEconomicosExportacaoBI() {
        String sql = " select inscricaomobiliaria, cpfcnpj, nome, inicio, fim, " +
            "                 cast(codigologradouro as integer) as codigologradouro, cast(codigobairro as integer) as codigobairro, " +
            "                 numero, atividadeprincipal, situacao " +
            "   from VwCadastroEconomicoBI " +
            " where cpfcnpj is not null " +
            " order by cpfcnpj, situacao, inscricaomobiliaria ";
        Query q = em.createNativeQuery(sql);
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Object[]> buscarAtividadesExportacaoBI(CNAE.Situacao situacao) {
        String sql = " select cnae.codigocnae as codigo, cnae.descricaodetalhada as descricao " +
            "from cnae where situacao = :situacaoCnae order by cnae.codigocnae ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacaoCnae", situacao.name());
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Long> buscarTributosParaIPTU(String tipoTributo) {
        return buscarTributosParaIPTU(Lists.newArrayList(tipoTributo));
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Long> buscarTributosParaIPTU(List<String> tiposTributo) {
        String sql = " select tr.id, tr.tributojuros_id, tr.tributomulta_id, tr.tributocorrecaomonetaria_id" +
            " from tributo tr " +
            " inner join eventocalculo ec on ec.tributo_id = tr.id " +
            " where tr.tipotributo in (:tiposTributo) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tiposTributo", tiposTributo);

        List<Object[]> ids = q.getResultList();
        List<Long> tributos = Lists.newArrayList();

        for (Object[] id : ids) {
            tributos.add(((BigDecimal) id[0]).longValue());
            tributos.add(((BigDecimal) id[1]).longValue());
            tributos.add(((BigDecimal) id[2]).longValue());
            tributos.add(((BigDecimal) id[3]).longValue());
        }

        sql = " select conf.tributo_id " +
            " from configuracaotributobi conf " +
            " inner join tributo tr on conf.tributo_id = tr.id " +
            " where conf.tipotributobi = :tipoIPTU " +
            " and tr.tipotributo in (:tiposTributo)";

        q = em.createNativeQuery(sql);
        q.setParameter("tipoIPTU", TipoTributoBI.IPTU.name());
        q.setParameter("tiposTributo", tiposTributo);

        List<BigDecimal> idsConfigBI = q.getResultList();
        for (BigDecimal idConfig : idsConfigBI) {
            tributos.add(idConfig.longValue());
        }

        return tributos;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Object[]> buscarGuiaISSEmDividasTaxasDiverasDoExercicio(Exercicio exercicio) {
        String sql = " select pvd.id as processamento,\n" +
            "       (select dam.numerodam\n" +
            "           from dam\n" +
            "          inner join itemdam idam on dam.id = idam.dam_id\n" +
            "        where idam.parcela_id = pvd.id\n" +
            "        order by dam.id desc\n" +
            "        fetch first 1 rows only) as numerodam,\n" +
            "       case when pf.id is not null then '2' else '3' end as tipoinscricao,\n" +
            "       ce.inscricaocadastral as inscricaomunipal,\n" +
            "       regexp_replace(coalesce(pf.cpf, pj.cnpj, ''), '[^0-9]', '') as cpfcnpj,\n" +
            "       ex.ano as ano,\n" +
            "       pvd.sequenciaparcela as parcela,\n" +
            "       c.datacalculo as datacompetencia,\n" +
            "       (select lb.datapagamento\n" +
            "           from lotebaixa lb\n" +
            "          inner join itemlotebaixa ilb on lb.id = ilb.lotebaixa_id\n" +
            "          inner join dam on ilb.dam_id = dam.id\n" +
            "          inner join itemdam idam on dam.id = idam.dam_id\n" +
            "        where idam.parcela_id = pvd.id\n" +
            "          and lb.situacaolotebaixa in (:situacoesLoteBaixa)\n" +
            "        order by lb.datapagamento desc fetch first 1 rows only) as dataPagamento,\n" +
            "       c.tipocalculo as tipo,\n" +
            "       coalesce((select coalesce(sum(ipvd.valor), 0)\n" +
            "                    from itemparcelavalordivida ipvd\n" +
            "                   inner join parcelavalordivida pvd2 on ipvd.parcelavalordivida_id = pvd2.id\n" +
            "                   inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id\n" +
            "                   inner join tributo tr on ivd.tributo_id = tr.id\n" +
            "                   inner join itemdam idam on pvd2.id = idam.parcela_id\n" +
            "                   inner join dam on idam.dam_id = dam.id and dam.situacao = :situacaoDam\n" +
            "                   inner join itemlotebaixa ilb on dam.id = ilb.dam_id\n" +
            "                   inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa)\n" +
            "                 where pvd2.id = pvd.id\n" +
            "                   and ilb.LOTEBAIXA_ID = (select max(ilb2.lotebaixa_id)\n" +
            "                                              from itemlotebaixa ilb2\n" +
            "                                             inner join lotebaixa lb2 on ilb2.lotebaixa_id = lb2.id\n" +
            "                                                                    and lb2.situacaolotebaixa in (:situacoesLoteBaixa)\n" +
            "                                           where ilb2.DAM_ID = dam.id)\n" +
            "                   and tr.tipotributo in (:tipoImposto,:tipoTaxa)\n" +
            "                   and idam.id = (select s_idm.id\n" +
            "                                  from itemdam s_idm\n" +
            "                                           inner join dam s_dam on s_dam.id = s_idm.dam_id\n" +
            "                                  where s_dam.situacao = :situacaoDam\n" +
            "                                    and s_idm.parcela_id = pvd.id\n" +
            "                                  order by s_idm.id desc fetch first 1 rows only)\n" +
            "                ), 0) as valorpago,\n" +
            "       coalesce((select sum(idam.juros)\n" +
            "                    from itemdam idam\n" +
            "                   inner join parcelavalordivida pvd2 on idam.PARCELA_ID = pvd2.id\n" +
            "                   inner join dam on idam.dam_id = dam.id\n" +
            "                   inner join itemlotebaixa ilb on dam.id = ilb.dam_id\n" +
            "                   inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa)\n" +
            "                 where pvd2.id = pvd.id\n" +
            "                   and dam.situacao = :situacaoDam), 0) as juros,\n" +
            "       coalesce((select sum(idam.multa)\n" +
            "                    from itemdam idam\n" +
            "                   inner join parcelavalordivida pvd2 on idam.parcela_id = pvd2.id\n" +
            "                   inner join dam on idam.dam_id = dam.id\n" +
            "                   inner join itemlotebaixa ilb on dam.id = ilb.dam_id\n" +
            "                   inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa)\n" +
            "                 where pvd2.id = pvd.id\n" +
            "                   and dam.situacao = :situacaoDam\n" +
            "                ), 0) as multa,\n" +
            "       (select coalesce(sum(case when sum(ipvd.valor) > 0 and coalesce(pvd2.valor, 0) > 0\n" +
            "                                     then round(coalesce(pvd2.valor, 0) *\n" +
            "                                                ((coalesce(sum(ipvd.valor), 0) * 100) / coalesce(pvd2.valor, 0)) / 100, 2) else 0 end), 0)\n" +
            "           from itemparcelavalordivida ipvd\n" +
            "          inner join parcelavalordivida pvd2 on ipvd.parcelavalordivida_id = pvd2.id\n" +
            "          inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id\n" +
            "          inner join tributo tr on ivd.tributo_id = tr.id\n" +
            "          inner join situacaoparcelavalordivida spvd2 on pvd2.situacaoatual_id = spvd2.id\n" +
            "        where pvd2.id = pvd.id\n" +
            "          and tr.tipotributo in (:tipoImposto,:tipoTaxa)\n" +
            "        group by tr.tipotributo, pvd2.valor) as original,\n" +
            "       coalesce((select sum(idam.correcaomonetaria)\n" +
            "                    from itemdam idam\n" +
            "                   inner join parcelavalordivida pvd2 on idam.parcela_id = pvd2.id\n" +
            "                   inner join dam on idam.dam_id = dam.id\n" +
            "                   inner join itemlotebaixa ilb on dam.id = ilb.dam_id\n" +
            "                   inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa)\n" +
            "                 where pvd2.id = pvd.id\n" +
            "                   and dam.situacao = :situacaoDam\n" +
            "                ), 0) correcao,\n" +
            "       coalesce(pvd.dividaativa,0) as dividaativa,\n" +
            "       ex.ano as anoreferencia,\n" +
            "       0 as mesreferencia,\n" +
            "       d.codigo as codigoDivida,\n" +
            "       spvd.situacaoParcela,\n" +
            "       spvd.dataLancamento,\n" +
            "       c.datacalculo\n" +
            "   from calculo c\n" +
            "  inner join valordivida vd on c.id = vd.calculo_id\n" +
            "  inner join exercicio ex on vd.exercicio_id = ex.id\n" +
            "  inner join divida d on vd.divida_id = d.id\n" +
            "  inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id\n" +
            "  inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id\n" +
            "  inner join cadastroeconomico ce on c.cadastro_id = ce.id\n" +
            "  inner join pessoa pes on ce.pessoa_id = pes.id\n" +
            "  left join pessoafisica pf on pes.id = pf.id\n" +
            "  left join pessoajuridica pj on pes.id = pj.id\n" +
            "where ex.ano = :ano\n" +
            "  and c.tipocalculo in (:dividasDiversas, :taxasDiversas)\n" +
            "  and spvd.situacaoparcela in (:situacoesIncluidas)\n" +
            "  and exists (select 1\n" +
            "                 from itemvalordivida ivd\n" +
            "              where ivd.valordivida_id = vd.id\n" +
            "                and ivd.tributo_id in (:tributos))\n" +
            "order by c.datacalculo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacoesLoteBaixa", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(),
            SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        q.setParameter("situacaoDam", DAM.Situacao.PAGO.name());
        q.setParameter("tipoImposto", Tributo.TipoTributo.IMPOSTO.name());
        q.setParameter("tipoTaxa", Tributo.TipoTributo.TAXA.name());
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("dividasDiversas", Calculo.TipoCalculo.DIVIDA_DIVERSA.name());
        q.setParameter("taxasDiversas", Calculo.TipoCalculo.TAXAS_DIVERSAS.name());
        q.setParameter("tributos", buscarTributosIss().stream().map(Tributo::getId).collect(Collectors.toList()));
        q.setParameter("situacoesIncluidas", getSituacoesIncluidas());
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Object[]> buscarGuiasISSExportacaoBI(Exercicio exercicio) {
        List<Object[]> guiasIss = Lists.newArrayList();
        String sql = " select pvd.id as processamento, \n" +
            "       (select dam.numerodam from dam \n" +
            "        inner join itemdam idam on dam.id = idam.dam_id \n" +
            "        where idam.parcela_id = pvd.id \n" +
            "        order by dam.id desc fetch first 1 rows only) as numerodam, \n" +
            "       case when pf.id is not null then '2' else '3' end as tipoinscricao, \n" +
            "       ce.inscricaocadastral as inscricaomunipal, \n" +
            "       regexp_replace(coalesce(pf.cpf, pj.cnpj, ''), '[^0-9]', '') as cpfcnpj, \n" +
            "       ex.ano as ano, pvd.sequenciaparcela as parcela, c.datacalculo as datacompetencia, \n" +
            "       (select lb.datapagamento from lotebaixa lb \n" +
            "        inner join itemlotebaixa ilb on lb.id = ilb.lotebaixa_id \n" +
            "        inner join dam on ilb.dam_id = dam.id \n" +
            "        inner join itemdam idam on dam.id = idam.dam_id \n" +
            "        where idam.parcela_id = pvd.id \n" +
            "        and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "        order by lb.datapagamento desc fetch first 1 rows only) as dataPagamento, \n" +
            "       case when iss.id is not null then iss.tipocalculoiss \n" +
            "            when c.tipocalculo = 'NFSE' then 'MENSAL' \n" +
            "            when c.tipocalculo = 'NFS_AVULSA' then 'MENSAL' \n" +
            "            when c.tipocalculo = 'CEASA' then 'FIXO' \n" +
            "            else c.tipocalculo end as tipo, \n" +
            "       (case when spvd.situacaoparcela = :situacaoCompensado \n" +
            "             then coalesce((select sum(coalesce(ic.valorcompensado, 0) - coalesce(ic.juros, 0) - \n" +
            "                                       coalesce(ic.multa, 0) - coalesce(ic.correcao, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only)), \n" +
            "                           (select sum(coalesce(ic.valorcompensado, 0) - coalesce(ic.juros, 0) - \n" +
            "                                       coalesce(ic.multa, 0) - coalesce(ic.correcao, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only))) \n" +
            "           else \n" +
            "           coalesce((select coalesce(sum(ipvd.valor), 0) \n" +
            "                 from itemparcelavalordivida ipvd \n" +
            "                 inner join parcelavalordivida pvd2 on ipvd.parcelavalordivida_id = pvd2.id \n" +
            "                 inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id \n" +
            "                 inner join tributo tr on ivd.tributo_id = tr.id \n" +
            "                 Inner join itemdam idam on pvd2.id = idam.parcela_id \n" +
            "                 inner join dam on idam.dam_id = dam.id and dam.situacao = :situacaoDam \n" +
            "                 inner join itemlotebaixa ilb on dam.id = ilb.dam_id \n" +
            "                 inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                 where pvd2.id = pvd.id \n" +
            "                 and ilb.LOTEBAIXA_ID = (select max(ilb2.lotebaixa_id) from itemlotebaixa ilb2 \n" +
            "                                          inner join lotebaixa lb2 on ilb2.lotebaixa_id = lb2.id and lb2.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                                          where ilb2.DAM_ID = dam.id)\n" +
            "                 and tr.tipotributo in (:tipoImposto,:tipoTaxa) \n" +
            "                 and idam.id = (select s_idm.id \n" +
            "                                from itemdam s_idm \n" +
            "                                inner join dam s_dam on s_dam.id = s_idm.dam_id \n" +
            "                                where s_dam.situacao = :situacaoDam\n" +
            "                                and s_idm.parcela_id = pvd.id \n" +
            "                                order by s_idm.id desc fetch first 1 rows only) \n" +
            "                 ), 0) end) - \n" +
            "                 (case when c.tipocalculo = :tipoParcelamento then coalesce((select sum((case when desconto.tipo = :tipoValor \n" +
            "                                         then coalesce(desconto.desconto, 0) \n" +
            "                                         else (coalesce(desconto.desconto, 0) / 100) * \n" +
            "                                         coalesce(ipvd2.valor, 0) end )) \n" +
            "                                   from itemparcelavalordivida ipvd2 \n" +
            "                                   inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id \n" +
            "                                   inner join tributo trib on ivd2.tributo_id = trib.id \n" +
            "                                   left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id \n" +
            "                                   where ipvd2.parcelavalordivida_id = pvd.id \n" +
            "                                   and trib.tipotributo in (:tipoImposto,:tipoTaxa)), 0) else 0 end) as valorpago, \n" +
            "       case when spvd.situacaoparcela = :situacaoCompensado \n" +
            "            then coalesce((select sum(coalesce(ic.juros, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only)), \n" +
            "                           (select sum(coalesce(ic.juros, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only))) \n" +
            "            else \n" +
            "            coalesce((select sum(idam.juros)\n" +
            "                  from itemdam idam \n" +
            "                  inner join parcelavalordivida pvd2 on idam.PARCELA_ID = pvd2.id \n" +
            "                  inner join dam on idam.dam_id = dam.id\n" +
            "                  inner join itemlotebaixa ilb on dam.id = ilb.dam_id \n" +
            "                  inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                  where pvd2.id = pvd.id \n" +
            "                    and dam.situacao = :situacaoDam\n" +
            "                  ), 0) end - (case when c.tipocalculo = :tipoParcelamento then coalesce((select sum((case when desconto.tipo = :tipoValor \n" +
            "                                         then coalesce(desconto.desconto, 0) \n" +
            "                                         else (coalesce(desconto.desconto, 0) / 100) * \n" +
            "                                         coalesce(ipvd2.valor, 0) end )) \n" +
            "                                   from itemparcelavalordivida ipvd2 \n" +
            "                                   inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id \n" +
            "                                   inner join tributo trib on ivd2.tributo_id = trib.id \n" +
            "                                   left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id \n" +
            "                                   where ipvd2.parcelavalordivida_id = pvd.id \n" +
            "                                   and trib.tipotributo = :tipoJuros), 0) else 0 end) as juros, \n" +
            "       case when spvd.situacaoparcela = :situacaoCompensado \n" +
            "            then coalesce((select sum(coalesce(ic.multa, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only)), \n" +
            "                           (select sum(coalesce(ic.multa, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only))) \n" +
            "            else \n" +
            "            coalesce((select sum(idam.multa)\n" +
            "                      from itemdam idam\n" +
            "                      inner join parcelavalordivida pvd2 on idam.parcela_id = pvd2.id \n" +
            "                      inner join dam on idam.dam_id = dam.id\n" +
            "                      inner join itemlotebaixa ilb on dam.id = ilb.dam_id \n" +
            "                      inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                      where pvd2.id = pvd.id \n" +
            "                        and dam.situacao = :situacaoDam \n" +
            "                      ), 0) end  - (case when c.tipocalculo = :tipoParcelamento then coalesce((select sum((case when desconto.tipo = :tipoValor \n" +
            "                                         then coalesce(desconto.desconto, 0) \n" +
            "                                         else (coalesce(desconto.desconto, 0) / 100) * \n" +
            "                                         coalesce(ipvd2.valor, 0) end )) \n" +
            "                                   from itemparcelavalordivida ipvd2 \n" +
            "                                   inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id \n" +
            "                                   inner join tributo trib on ivd2.tributo_id = trib.id \n" +
            "                                   left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id \n" +
            "                                   where ipvd2.parcelavalordivida_id = pvd.id \n" +
            "                                   and trib.tipotributo = :tipoMulta), 0) else 0 end) as multa, \n" +
            "                                   \n" +
            "       (select coalesce(sum(case when sum(ipvd.valor) > 0 and coalesce(pvd2.valor, 0) > 0 \n" +
            "                    then round(coalesce(pvd2.valor, 0) * \n" +
            "                    ((coalesce(sum(ipvd.valor), 0) * 100) / coalesce(pvd2.valor, 0)) / 100, 2) else 0 end), 0) \n" +
            "        from itemparcelavalordivida ipvd \n" +
            "        inner join parcelavalordivida pvd2 on ipvd.parcelavalordivida_id = pvd2.id \n" +
            "        inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id \n" +
            "        inner join tributo tr on ivd.tributo_id = tr.id \n" +
            "        inner join situacaoparcelavalordivida spvd2 on pvd2.situacaoatual_id = spvd2.id \n" +
            "        where pvd2.id = pvd.id \n" +
            "        and tr.tipotributo in (:tipoImposto,:tipoTaxa) \n" +
            "        group by tr.tipotributo, pvd2.valor) as original, \n" +
            "       case when spvd.situacaoparcela = :situacaoCompensado \n" +
            "            then coalesce((select sum(coalesce(ic.correcao, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only)), \n" +
            "                           (select sum(coalesce(ic.correcao, 0)) \n" +
            "                            from itemcompensacao ic \n" +
            "                            where ic.id = (select ic2.id from itemcompensacao ic2 \n" +
            "                                           where ic2.parcela_id = pvd.id \n" +
            "                                           order by ic2.id fetch first 1 rows only))) \n" +
            "            else \n" +
            "            coalesce((select sum(idam.correcaomonetaria)\n" +
            "                      from itemdam idam \n" +
            "                      inner join parcelavalordivida pvd2 on idam.parcela_id = pvd2.id \n" +
            "                      inner join dam on idam.dam_id = dam.id\n" +
            "                      inner join itemlotebaixa ilb on dam.id = ilb.dam_id \n" +
            "                      inner join lotebaixa lb on ilb.lotebaixa_id = lb.id and lb.situacaolotebaixa in (:situacoesLoteBaixa) \n" +
            "                      where pvd2.id = pvd.id \n" +
            "                       and dam.situacao = :situacaoDam\n" +
            "                      ), 0) end - (case when c.tipocalculo = :tipoParcelamento then coalesce((select sum((case when desconto.tipo = :tipoValor \n" +
            "                                         then coalesce(desconto.desconto, 0) \n" +
            "                                         else (coalesce(desconto.desconto, 0) / 100) * \n" +
            "                                         coalesce(ipvd2.valor, 0) end )) \n" +
            "                                   from itemparcelavalordivida ipvd2 \n" +
            "                                   inner join itemvalordivida ivd2 on ipvd2.itemvalordivida_id = ivd2.id \n" +
            "                                   inner join tributo trib on ivd2.tributo_id = trib.id  \n" +
            "                                   left join descontoitemparcela desconto on desconto.itemparcelavalordivida_id = ipvd2.id \n" +
            "                                   where ipvd2.parcelavalordivida_id = pvd.id \n" +
            "                                   and trib.tipotributo = :tipoCorrecao), 0) else 0 end) as correcao, \n" +
            "       coalesce(pvd.dividaativa,0) as dividaativa, \n" +
            "       coalesce(nfse.anodereferencia, exiss.ano, 0) as anoreferencia, \n" +
            "       coalesce(nfse.mesdereferencia, piss.mesreferencia, 0) as mesreferencia, \n" +
            "       d.codigo as codigoDivida, \n" +
            "       spvd.situacaoParcela, \n" +
            "       spvd.dataLancamento," +
            "       c.datacalculo\n" +
            " from calculo c \n" +
            " inner join valordivida vd on c.id = vd.calculo_id \n" +
            " inner join exercicio ex on vd.exercicio_id = ex.id \n" +
            " inner join divida d on vd.divida_id = d.id \n" +
            " inner join exercicio ex on vd.exercicio_id = ex.id \n" +
            " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id \n" +
            " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id \n" +
            " inner join cadastroeconomico ce on c.cadastro_id = ce.id \n" +
            " inner join pessoa pes on ce.pessoa_id = pes.id \n" +
            " left join pessoafisica pf on pes.id = pf.id \n" +
            " left join pessoajuridica pj on pes.id = pj.id \n" +
            " left join calculoiss iss on c.id = iss.id \n" +
            " left join processocalculoiss piss on iss.processocalculoiss_id = piss.id \n" +
            " left join processocalculo pc on piss.id = pc.id \n" +
            " left join exercicio exiss on pc.exercicio_id = exiss.id \n" +
            " left join calculonfse nfse on c.id = nfse.id \n" +
            " where ex.ano = :ano\n" +
            " and spvd.situacaoparcela in (:situacoesIncluidas)\n" +
            "   and d.id in (:lista_dividas)";

        Query q = em.createNativeQuery(sql);
        q.setParameter("situacoesLoteBaixa", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(),
            SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        q.setParameter("situacaoDam", DAM.Situacao.PAGO.name());
        q.setParameter("situacoesIncluidas", getSituacoesIncluidas());
        q.setParameter("situacaoCompensado", SituacaoParcela.COMPENSACAO.name());
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("tipoImposto", Tributo.TipoTributo.IMPOSTO.name());
        q.setParameter("tipoTaxa", Tributo.TipoTributo.TAXA.name());
        q.setParameter("tipoJuros", Tributo.TipoTributo.JUROS.name());
        q.setParameter("tipoMulta", Tributo.TipoTributo.MULTA.name());
        q.setParameter("tipoCorrecao", Tributo.TipoTributo.CORRECAO.name());
        q.setParameter("tipoParcelamento", Calculo.TipoCalculo.PARCELAMENTO.name());
        q.setParameter("tipoValor", DescontoItemParcela.Tipo.VALOR.name());
        List<Divida> todasDividasIss = buscarDividasISS(TipoExportacaoArquivoBI.GUIA_ISS);
        List<Long> idsTodasDividasIss = Lists.newArrayList();
        for (Divida dividasIss : todasDividasIss) {
            idsTodasDividasIss.add(dividasIss.getId());
        }
        q.setParameter("lista_dividas", idsTodasDividasIss);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            guiasIss.addAll(resultList);
        }
        List<Object[]> lancadasDividasTaxasDiversas = buscarGuiaISSEmDividasTaxasDiverasDoExercicio(exercicio);
        if (!lancadasDividasTaxasDiversas.isEmpty()) {
            guiasIss.addAll(lancadasDividasTaxasDiversas);
        }
        Collections.sort(guiasIss, (o1, o2) -> {
            Long id1 = ((Number) o1[0]).longValue();
            Long id2 = ((Number) o2[0]).longValue();
            return id1.compareTo(id2);
        });
        return guiasIss;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<String> getSituacoesIncluidas() {
        return Lists.newArrayList(SituacaoParcela.EM_ABERTO.name(), SituacaoParcela.PAGO.name(),
            SituacaoParcela.BAIXADO.name(), SituacaoParcela.COMPENSACAO.name());
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<String> getSituacoesExcluidas(SituacaoParcela particular) {
        List<String> situacoes = Lists.newArrayList(SituacaoParcela.CANCELADO_RECALCULO.name(),
            SituacaoParcela.CANCELAMENTO.name(),
            SituacaoParcela.ANISTIA.name(),
            SituacaoParcela.DACAO.name(),
            SituacaoParcela.DECADENCIA.name(),
            SituacaoParcela.PRESCRICAO.name(),
            SituacaoParcela.REMISSAO.name(),
            SituacaoParcela.DESISTENCIA.name(),
            SituacaoParcela.BAIXADO.name(),
            SituacaoParcela.CANCELADO_ISENCAO.name());
        if (particular != null) {
            situacoes.add(particular.name());
        }
        return situacoes;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Long> buscarTributoParaISS() {
        Query q = em.createNativeQuery("select conf.tributoNFSAvulsa_id, conf.tributoISS_id, conf.tributoTaxaSobreISS_id, conf.tributoMultaAcessoria_id, conf.tributoRateio_id, \n" +
            "       trNfsa.TRIBUTOJUROS_ID as jurosNfsa, trNfsa.TRIBUTOMULTA_ID as multaNfsa, trNfsa.TRIBUTOCORRECAOMONETARIA_ID as correcaoNfsa,\n" +
            "       trIss.TRIBUTOJUROS_ID as jurosIss, trIss.TRIBUTOMULTA_ID as multaIss, trIss.TRIBUTOCORRECAOMONETARIA_ID as correcaoIss,\n" +
            "       trMA.TRIBUTOJUROS_ID as jurosMa, trMA.TRIBUTOMULTA_ID as multaMa, trMA.TRIBUTOCORRECAOMONETARIA_ID as correcaoMa,\n" +
            "       trRat.TRIBUTOJUROS_ID as jurosRat, trRat.TRIBUTOMULTA_ID as multaRat, trRat.TRIBUTOCORRECAOMONETARIA_ID as correcaoRat\n" +
            "from CONFIGURACAOTRIBUTARIO conf\n" +
            "left join tributo trNfsa on trnfsa.id = conf.tributoNFSAvulsa_id\n" +
            "left join tributo trIss on trIss.id = conf.tributoISS_id\n" +
            "left join tributo trMA on trMA.id = conf.tributoMultaAcessoria_id\n" +
            "left join tributo trRat on trRat.id = conf.tributoRateio_id");
        q.setMaxResults(1);
        Object[] ids = (Object[]) q.getResultList().get(0);
        List<Long> tributos = Lists.newArrayList();
        for (int i = 0; i < 16; i++) {
            if (ids[i] != null) {
                tributos.add(((BigDecimal) ids[i]).longValue());
            }
        }

        q = em.createNativeQuery("select distinct tributo_id, tr.TRIBUTOJUROS_ID, tr.TRIBUTOMULTA_ID, tr.TRIBUTOCORRECAOMONETARIA_ID \n" +
            "    from ConfiguracaoNfseDivida\n" +
            "    left join tributo tr on tr.id = tributo_id");
        List<Object[]> idsIss = q.getResultList();
        for (Object[] id : idsIss) {
            for (int i = 0; i < 4; i++) {
                if (id[i] != null) {
                    tributos.add(((BigDecimal) id[i]).longValue());
                }
            }
        }

        q = em.createNativeQuery(" select conf.tributo_id from configuracaotributobi conf where conf.tipotributobi = :tipoISSQN ")
            .setParameter("tipoISSQN", TipoTributoBI.ISSQN.name());

        List<BigDecimal> idsConfigBI = q.getResultList();
        for (BigDecimal idConfig : idsConfigBI) {
            tributos.add(idConfig.longValue());
        }

        return tributos;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportarTiposDeIPTU(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setMensagens("Recuperando os Tipos de IPTU");
        List<TipoIPTU> tiposIptu = Arrays.asList(TipoIPTU.values());
        Collections.sort(tiposIptu, new Comparator<TipoIPTU>() {
            @Override
            public int compare(TipoIPTU o1, TipoIPTU o2) {
                return o1.getCodigo().compareTo(o2.getCodigo());
            }
        });
        for (TipoIPTU tipoIPTU : tiposIptu) {
            selecionado.getLinha().append(tipoIPTU.getCodigo());
            selecionado.adicionarSeparador();
            selecionado.getLinha().append(tipoIPTU.getDescricao());
            finalizarLinhaAumentandoProcessado(selecionado);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void gerarLinha(ExportacaoArquivoBI selecionado, Object[] object) {
        for (int x = 0; x < object.length; x++) {
            selecionado.getLinha().append(object[x] == null ? "" :
                object[x] instanceof String ?
                    StringUtil.removerQuebraDeLinha(((String) object[x]).replace(";", "")) :
                    (object[x]).toString());
            if (x + 1 < object.length) {
                selecionado.adicionarSeparador();
            }
        }
        finalizarLinhaAumentandoProcessado(selecionado);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void popularExecucaoDespesa(ExportacaoArquivoBI selecionado, Object[] execucaoDespesa) {
        selecionado.getLinha().append(execucaoDespesa[0]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[1]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[2]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(((String) execucaoDespesa[3]).replace(".", ";"));
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(((String) execucaoDespesa[4]).replace(".", ""));
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[5]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[6]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[7]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[8]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[9]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[10]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[11]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[12]);
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(execucaoDespesa[13] != null ? execucaoDespesa[13] : "");
        selecionado.adicionarSeparador();
        selecionado.getLinha().append(adicionarMascaraValor(execucaoDespesa[14]));
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void finalizarLinhaAumentandoProcessado(ExportacaoArquivoBI selecionado) {
        selecionado.quebrarLinha();
        aumentarProcessado(selecionado);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void adicionarQuantidadeTotal(ExportacaoArquivoBI selecionado, Integer quantidade) {
        selecionado.getBarraProgressoItens().setTotal(selecionado.getBarraProgressoItens().getTotal() + quantidade);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void aumentarProcessado(ExportacaoArquivoBI selecionado) {
        selecionado.getBarraProgressoItens().setProcessados(selecionado.getBarraProgressoItens().getProcessados() + 1);
    }

    public PlanoDeContasFacade getPlanoDeContasFacade() {
        return planoDeContasFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public StreamedContent gerarEnviarAquivosBI(TipoExportacaoArquivoBI tipo) throws Exception {
        ExportacaoArquivoBI selecionado = new ExportacaoArquivoBI();
        selecionado.setBarraProgressoItens(new BarraProgressoItens());
        selecionado.getBarraProgressoItens().inicializa();
        selecionado.getBarraProgressoItens().setMensagens("Iniciando exportação");
        selecionado.setLinha(new StringBuilder());
        selecionado.setStreamedContent(null);
        selecionado.setDataOperacao(new Date());
        selecionado.setTipo(tipo);
        selecionado.setExercicioInicial(exercicioFacade.getExercicioPorAno(DataUtil.getAno(new Date()) - 5));
        selecionado.setExercicioFinal(exercicioFacade.getExercicioPorAno(DataUtil.getAno(new Date())));
        gerarArquivo(selecionado);

        String nomeArquivo = selecionado.getTipo().getNomeArquivo();
        File arquivo = new File(nomeArquivo);

        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            String s = selecionado.getLinha().toString().toUpperCase();
            fos.write(s.getBytes("ISO-8859-1"));
            InputStream stream = new FileInputStream(arquivo);
            selecionado.setStreamedContent(new DefaultStreamedContent(stream, "text/plain", nomeArquivo));
        } catch (Exception e) {
            logger.error("Erro ao gerar arquivo de exportação do BI. Erro: {}", e.getMessage());
            logger.debug("Detalhes do erro ao gerar arquivo de exportação do BI.", e);
            selecionado.setStreamedContent(null);
            throw e;
        }
        return selecionado.getStreamedContent();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void enviarArquivosBIDebitosGeralTributario(TipoWebService tipoWebService) {
        TipoExportacaoArquivoBI tipo = TipoExportacaoArquivoBI.DEBITOS_GERAL;
        try {
            String lineSeparator = System.getProperty("line.separator");
            System.setProperty("line.separator", "\r\n");

            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            if (configuracaoTributario.getConfiguracaoTributarioBI() == null
                || configuracaoTributario.getConfiguracaoTributarioBI().getDebitosGeralExercicioInicialParte1() == null
                || configuracaoTributario.getConfiguracaoTributarioBI().getDebitosGeralExercicioFinalParte1() == null
                || configuracaoTributario.getConfiguracaoTributarioBI().getDebitosGeralExercicioInicialParte2() == null
                || configuracaoTributario.getConfiguracaoTributarioBI().getDebitosGeralExercicioFinalParte2() == null) {
                throw new ValidacaoException("Não está definido a Configuração do BI nas Configuirações do Tributário.");
            }

            gerarAndEnviarFTPArquivoBIDebitosGeral(tipoWebService,
                configuracaoTributario.getConfiguracaoTributarioBI().getDebitosGeralExercicioInicialParte1(),
                configuracaoTributario.getConfiguracaoTributarioBI().getDebitosGeralExercicioFinalParte1(),
                tipo.getNomeArquivo() + "_PARTE1");

            gerarAndEnviarFTPArquivoBIDebitosGeral(tipoWebService,
                configuracaoTributario.getConfiguracaoTributarioBI().getDebitosGeralExercicioInicialParte2(),
                configuracaoTributario.getConfiguracaoTributarioBI().getDebitosGeralExercicioFinalParte2(),
                tipo.getNomeArquivo() + "_PARTE2");

            System.setProperty("line.separator", lineSeparator);
        } catch (Exception e) {
            logger.error("Erro ao enviar arquivo bi débitos gerais. {}", e.getMessage());
            logger.debug("Detalhes do erro ao enviar arquivo bi débitos gerais.", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void gerarAndEnviarFTPArquivoBIDebitosGeral(TipoWebService tipoWebService,
                                                        Integer exercicioInicial,
                                                        Integer exercicioFinal,
                                                        String nomeArquivo) throws Exception {
        StreamedContent arquivo;
        logger.debug("Vai gerar o arquivo {}", nomeArquivo);
        arquivo = gerarEnviarAquivosBIDebitosGerais(exercicioInicial, exercicioFinal,
                nomeArquivo);
        logger.debug("Vai enviar o Arquivo {} via FTP", nomeArquivo);
        enviarArquivoParaFTP(arquivo, tipoWebService);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public StreamedContent gerarEnviarAquivosBIDebitosGerais(Integer exercicioInicial,
                                                             Integer exercicioFinal,
                                                             String nomeArquivo) throws Exception {
        ExportacaoArquivoBI selecionado = new ExportacaoArquivoBI();
        selecionado.setBarraProgressoItens(new BarraProgressoItens());
        selecionado.getBarraProgressoItens().inicializa();
        selecionado.getBarraProgressoItens().setMensagens("Iniciando exportação");
        selecionado.setLinha(new StringBuilder());
        selecionado.setStreamedContent(null);
        selecionado.setDataOperacao(new Date());
        selecionado.setTipo(TipoExportacaoArquivoBI.DEBITOS_GERAL);

        selecionado.setExercicioInicial(exercicioFacade.recuperarExercicioPeloAno(exercicioInicial));
        selecionado.setExercicioFinal(exercicioFacade.recuperarExercicioPeloAno(exercicioFinal));
        gerarArquivo(selecionado);
        escreverNoArquivo(selecionado, nomeArquivo);
        return selecionado.getStreamedContent();
    }

    private File gravarArquivo(ExportacaoArquivoBI selecionado, String nomeArquivo) throws IOException {
        File arquivo = new File(nomeArquivo);
        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            String s = selecionado.getLinha().toString().toUpperCase();
            fos.write(s.getBytes("ISO-8859-1"));
        } catch (Exception e) {
            logger.error("Erro ao gerar o arquivo {} de exportação do BI. Erro: {}", nomeArquivo, e.getMessage());
            logger.debug("Detalhes do erro ao gerar o arquivo {} de exportação do BI.", nomeArquivo, e);
            throw e;
        }
        return arquivo;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void enviarArquivoParaFTP(StreamedContent arquivo, TipoWebService tipoWebService) {
        String nomeArquivo = arquivo.getName();
        FTPClient ftp = new FTPClient();
        try {
            ConfiguracaoWebService configuracaoWs = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(tipoWebService,
                sistemaFacade.getUsuarioBancoDeDados());
            if (configuracaoWs != null) {
                try {
                    ftp.connect(configuracaoWs.getUrl(), 21);
                } catch (Exception ex) {
                    ftp = new FTPSClient(false);
                    ftp.connect(configuracaoWs.getUrl(), 21);
                }
                if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                    ftp.login(configuracaoWs.getUsuario(), configuracaoWs.getSenha());
                    logger.debug("Conectou com o servidor de FTP");
                } else {
                    ftp.disconnect();
                    logger.debug("Conexão recusada pelo servidor de FTP");
                }
                logger.debug("Diretório " + configuracaoWs.getDetalhe());
                boolean b = ftp.changeWorkingDirectory(configuracaoWs.getDetalhe());
                logger.debug("Encontrou o diretório dentro do servidor de FTP " + b);
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                logger.debug("Enviando arquivo " + nomeArquivo + "...");
                ftp.enterLocalPassiveMode();
                if (ftp.storeFile(nomeArquivo, arquivo.getStream())) {
                    logger.debug("Arquivo " + nomeArquivo + " enviado com sucesso!");
                } else {
                    logger.debug("Arquivo " + nomeArquivo + " não foi enviado!");
                    logger.debug("Vai tentar com enterLocalActiveMode!");
                    ftp.enterLocalActiveMode();
                    if (ftp.storeFile(nomeArquivo, arquivo.getStream())) {
                        logger.debug("Arquivo " + nomeArquivo + " enviado com sucesso!");
                    } else {
                        logger.debug("Arquivo " + nomeArquivo + " não foi enviado!");
                    }
                }
                ftp.logout();
                ftp.disconnect();
                logger.debug("Encerrando a conexão FTP");
            } else {
                logger.debug("Não foi encontrado configuração de FTP para envio!");
            }
        } catch (Exception e) {
            logger.error("Ocorreu um erro: {}", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void criarArquivoZipado(List<ExportacaoArquivoBI> exportacoes, ExportacaoArquivoBI selecionado, ModuloSistema moduloSistema) {
        if (selecionado.getStreamedContent() == null) {
            try {
                File zip = File.createTempFile(moduloSistema.toString(), ".zip");
                FileOutputStream fout = new FileOutputStream(zip);
                ZipOutputStream zout = new ZipOutputStream(fout);
                for (ExportacaoArquivoBI exportacao : exportacoes) {
                    InputStream fin = exportacao.getStreamedContent().getStream();

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[1024];
                    while ((nRead = fin.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    zout.putNextEntry(new ZipEntry(exportacao.getStreamedContent().getName() + ".txt"));
                    zout.write(buffer.toByteArray());
                    zout.closeEntry();
                }
                FileInputStream fis = new FileInputStream(zip);
                StreamedContent fileDownload = new DefaultStreamedContent(fis, "application/zip", moduloSistema.toString() + ".zip");
                zout.close();
                selecionado.setStreamedContent(fileDownload);
            } catch (IOException ioe) {
                logger.error("Ocorreu um erro para gerar o arquivo ZIP do " + moduloSistema.getDescricao() + ": ", ioe);
                FacesUtil.addError("Erro!", "Ocorreu um erro para gerar o arquivo ZIP do " + moduloSistema.getDescricao() + ": " + ioe.getMessage());
            }
        }
    }
}
