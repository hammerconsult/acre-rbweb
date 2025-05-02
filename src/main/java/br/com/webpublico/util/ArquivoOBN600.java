/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemOBN600;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.*;
import br.com.webpublico.util.obn600.LinhaOBN600TipoCinco;
import br.com.webpublico.util.obn600.LinhaOBN600TipoDois;
import br.com.webpublico.util.obn600.LinhaOBN600TipoQuatro;
import br.com.webpublico.util.obn600.LinhaOBN600TipoTres;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author reidocrime
 */
@Stateless
public class ArquivoOBN600 {

    private static final Logger logger = LoggerFactory.getLogger(ArquivoOBN600.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UFFacade estadoFacade;
    @EJB
    private BorderoFacade borderoFacade;
    @EJB
    private ConfiguracaoArquivoObnFacade configuracaoArquivoObnFacade;
    private ItemOBN600 itemOBN600;
    private StringBuilder linha;
    private JdbcRevisaoAuditoria jdbcRevisaoAuditoria;
    private JdbcPagamento jdbcPagamento;
    private JdbcPagamentoExtra jdbcPagamentoExtra;
    private JdbcGuiaPagamento jdbcGuiaPagamento;
    private JdbcGuiaPagamentoExtra jdbcGuiaPagamentoExtra;
    private JdbcRetencaoPagamento jdbcRetencaoPagamento;
    private JdbcPagamentoEstorno jdbcPagamentoEstorno;
    private JdbcPagamentoReceitaExtra jdbcPagamentoReceitaExtra;
    private JdbcDesdobramentoPagamento jdbcDesdobramentoPagamento;
    private JdbcGuiaConvenio jdbcGuiaConvenio;
    private JdbcPessoa jdbcPessoa;
    private JdbcGuiaFatura jdbcGuiaFatura;
    private JdbcGuiaGps jdbcGuiaGps;
    private JdbcGuiaDARF jdbcGuiaDARF;
    private JdbcGuiaDARFSimples jdbcGuiaDARFSimples;
    private JdbcEnderecoPessoa jdbcEnderecoPessoa;
    private JdbcGuiaGRU jdbcGuiaGRU;

    @PostConstruct
    private void init() {
        jdbcPagamento = (JdbcPagamento) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcPagamento");
        jdbcPagamentoExtra = (JdbcPagamentoExtra) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcPagamentoExtra");
        jdbcRevisaoAuditoria = (JdbcRevisaoAuditoria) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcRevisaoAuditoria");
        jdbcGuiaPagamentoExtra = (JdbcGuiaPagamentoExtra) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcGuiaPagamentoExtra");
        jdbcRetencaoPagamento = (JdbcRetencaoPagamento) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcRetencaoPagamento");
        jdbcPagamentoEstorno = (JdbcPagamentoEstorno) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcPagamentoEstorno");
        jdbcGuiaPagamento = (JdbcGuiaPagamento) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcGuiaPagamento");
        jdbcPagamentoReceitaExtra = (JdbcPagamentoReceitaExtra) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcPagamentoReceitaExtra");
        jdbcDesdobramentoPagamento = (JdbcDesdobramentoPagamento) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcDesdobramentoPagamento");
        jdbcGuiaConvenio = (JdbcGuiaConvenio) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcGuiaConvenio");
        jdbcPessoa = (JdbcPessoa) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcPessoa");
        jdbcGuiaFatura = (JdbcGuiaFatura) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcGuiaFatura");
        jdbcGuiaGps = (JdbcGuiaGps) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcGuiaGps");
        jdbcGuiaDARF = (JdbcGuiaDARF) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcGuiaDARF");
        jdbcGuiaDARFSimples = (JdbcGuiaDARFSimples) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcGuiaDARFSimples");
        jdbcEnderecoPessoa = (JdbcEnderecoPessoa) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcEnderecoPessoa");
        jdbcGuiaGRU = (JdbcGuiaGRU) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcGuiaGRU");
    }

    private String gerarNomeDoArquivo(ArquivoRemessaBanco arb) {
        SimpleDateFormat dataHora = new SimpleDateFormat("ddMMyyyyHHmm");
        String nome = dataHora.format(new Date());
        if (arb.getContratoObn() != null) {
            if (arb.getContratoObn().getComplementoNomeArquivo() != null) {
                return nome + arb.getContratoObn().getComplementoNomeArquivo();
            }
        }
        return nome;
    }

    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Asynchronous
    public Future<StreamedContent> gerarArquivoOBN600(ArquivoRemessaBanco arb, AssistenteBarraProgresso assistenteBarraProgresso) {

        try {
            linha = new StringBuilder();
            itemOBN600 = new ItemOBN600();
            itemOBN600.setSequenciaInterna(1);
            itemOBN600.setSequenciaInternaGuia(1);
            itemOBN600.setSequenciaInternoMovimentosOB(1);
            itemOBN600.setContratoObn(arb.getContratoObn());

            recuperarBancoGeradorDoArquivo(arb.getBanco());

            List<ArquivoRemBancoBordero> arquivoRemBancoBorderos = arb.getArquivoRemBancoBorderos();

            alterarSituacaoBorderoParaAguardandoBaixa(arb, assistenteBarraProgresso.getUsuarioSistema(), assistenteBarraProgresso.getIp());

            adicionarHeader(arb);
            RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(assistenteBarraProgresso.getIp(), assistenteBarraProgresso.getUsuarioSistema());
            percorrerOrdemBancaria(arquivoRemBancoBorderos, assistenteBarraProgresso, rev.getId(), arb.getGerarLinhaTipoUm());
            adicionarTrailer(arb);
            String nomeArquivo = gerarNomeDoArquivo(arb);
            File arquivo = new File(nomeArquivo);

            try (FileOutputStream fos = new FileOutputStream(arquivo)) {

                String s = removerAcentos(linha.toString());

                s = s.replaceAll("[^\\x00-\\x7F]", " ");

                fos.write(s.getBytes("ISO-8859-1"));
                InputStream stream = new FileInputStream(arquivo);
                return new AsyncResult<StreamedContent>(new DefaultStreamedContent(stream, "text/plain", nomeArquivo));
            }
        } catch (Exception ex) {
//            Adicionado para verificar possiveis erros enquanto o arquivo da caixa é validado (não remover).
            logger.error("Erro ao gerar arquivo obn600", ex);
            assistenteBarraProgresso.setExecutando(false);
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(ex.getMessage()));
        }
        return null;
    }

    private void triagemMovimentacaoFinanceiraEmOrdemBancaria(ArquivoRemessaBanco arquivoRemessaBanco, Bordero bordero, AssistenteBarraProgresso assistenteBarraProgresso, Long idRev) {
        assistenteBarraProgresso.setTotal(assistenteBarraProgresso.getTotal() + bordero.getListaPagamentos().size() + bordero.getListaPagamentosExtra().size() + bordero.getListaLiberacaoCotaFinanceira().size() +
            bordero.getListaTransferenciaFinanceira().size() + bordero.getListaTransferenciaMesmaUnidade().size());
        percorrerPagamentos(arquivoRemessaBanco, bordero.getListaPagamentos(), assistenteBarraProgresso, idRev);
        percorrerPagamentosExtras(arquivoRemessaBanco, bordero.getListaPagamentosExtra(), assistenteBarraProgresso, idRev);
        percorrerLiberacoesFinanceiras(bordero.getListaLiberacaoCotaFinanceira(), assistenteBarraProgresso);
        percorrerTransferenciasFinanceiras(bordero.getListaTransferenciaFinanceira(), assistenteBarraProgresso);
        percorrerTransferenciasMesmaUnidade(bordero.getListaTransferenciaMesmaUnidade(), assistenteBarraProgresso);
    }

    private void percorrerOrdemBancaria(List<ArquivoRemBancoBordero> arquivosRemessaBanco, AssistenteBarraProgresso assistenteBarraProgresso, Long idRev, boolean gerarLinhaTipoUm) {
        List<Bordero> ordensBancarias = Lists.newArrayList();
        assistenteBarraProgresso.setTotal(0);
        for (ArquivoRemBancoBordero a : arquivosRemessaBanco) {
            iniciarVariaveisOrdemBancaria(a.getBordero(), a.getArquivoRemessaBanco());

            if (podeConstruirLinhaTipoUm(ordensBancarias) && !itemOBN600.getBancoObn().isArquivoCaixaEconomica() && (!itemOBN600.getBancoObn().isArquivoBancoDoBrasil() || gerarLinhaTipoUm)) {
                construirLinhaTipoUm();
            }
            ordensBancarias.add(itemOBN600.getOrdemBancaria());
            triagemMovimentacaoFinanceiraEmOrdemBancaria(a.getArquivoRemessaBanco(), itemOBN600.getOrdemBancaria(), assistenteBarraProgresso, idRev);
        }
    }

    private void percorrerPagamentos(ArquivoRemessaBanco arquivoRemessaBanco, List<BorderoPagamento> paramentos, AssistenteBarraProgresso assistenteBarraProgresso, Long idRev) {

        for (BorderoPagamento borderoPagamento : paramentos) {
            if (!SituacaoItemBordero.INDEFIRIDO.equals(borderoPagamento.getSituacaoItemBordero())
                && !SituacaoItemBordero.PAGO.equals(borderoPagamento.getSituacaoItemBordero())) {
                Pagamento pagamento = borderoPagamento.getPagamento();

                pagamento.setRetencaoPgtos(jdbcRetencaoPagamento.buscarRetencaoPagamentosPorPagamento(pagamento));
                pagamento.setGuiaPagamento(jdbcGuiaPagamento.buscarGuiaPagamentoPorPagamento(pagamento));
                pagamento.setPagamentoEstornos(jdbcPagamentoEstorno.buscarPagamentosEstornoPorPagamento(pagamento));
                pagamento.setDesdobramentos(jdbcDesdobramentoPagamento.buscarDesdobramentoPorPagamento(pagamento));

                pagamento.setIdObn600(arquivoRemessaBanco.getId());
                iniciarVariaveisPagamento(borderoPagamento, pagamento);

                construirLinhaTipoDois();

                if (isGuia(pagamento.getTipoOperacaoPagto())) {
                    if (isGuiaCodigoBarra(pagamento.getTipoOperacaoPagto()) || TipoDocPagto.GRU.equals(pagamento.getTipoDocPagto())) {
                        construirLinhaTipoQuatroPagamento(pagamento);
                    }
                    if (isGuiaSemCodigoBarra(pagamento.getTipoOperacaoPagto())) {
                        construirLinhaTipoCincoPagamento(pagamento);
                    }
                    if (isGuiaTransferencia(pagamento.getTipoOperacaoPagto()) && itemOBN600.getBancoObn().isArquivoBancoDoBrasil()) {
                        construirLinhaTipoTres(pagamento);
                    }
                }
                jdbcPagamento.atualizar(pagamento, idRev);
            }
            assistenteBarraProgresso.conta();
        }
    }

    private void percorrerPagamentosExtras(ArquivoRemessaBanco arquivoRemessaBanco, List<BorderoPagamentoExtra> pagamentosExtras, AssistenteBarraProgresso assistenteBarraProgresso, Long idRev) {
        for (BorderoPagamentoExtra borderoPagamentoExtra : pagamentosExtras) {
            if (!SituacaoItemBordero.INDEFIRIDO.equals(borderoPagamentoExtra.getSituacaoItemBordero())
                && !SituacaoItemBordero.PAGO.equals(borderoPagamentoExtra.getSituacaoItemBordero())) {
                PagamentoExtra pagamentoExtra = borderoPagamentoExtra.getPagamentoExtra();

                pagamentoExtra.setPagamentoReceitaExtras(jdbcPagamentoReceitaExtra.buscarPagamentosReceitaExtraPorPagamentoExtra(pagamentoExtra));
                pagamentoExtra.setGuiaPagamentoExtras(jdbcGuiaPagamentoExtra.buscarGuiaPagamentoExtraPorPagamentoExtra(pagamentoExtra));

                pagamentoExtra.setIdObn600(arquivoRemessaBanco.getId());
                iniciarVariaveisDespesaExtra(borderoPagamentoExtra, pagamentoExtra);

                construirLinhaTipoDois();

                if (isGuia(pagamentoExtra.getTipoOperacaoPagto())) {
                    if (isGuiaCodigoBarra(pagamentoExtra.getTipoOperacaoPagto()) || TipoDocPagto.GRU.equals(pagamentoExtra.getTipoDocumento())) {
                        construirLinhaTipoQuatroDespesaExtra(pagamentoExtra);
                    }
                    if (isGuiaSemCodigoBarra(pagamentoExtra.getTipoOperacaoPagto())) {
                        construirLinhaTipoCincoDespesaExtra(pagamentoExtra);
                    }
                    if (isGuiaTransferencia(pagamentoExtra.getTipoOperacaoPagto()) && itemOBN600.getBancoObn().isArquivoBancoDoBrasil()) {
                        construirLinhaTipoTres(pagamentoExtra);
                    }
                }
                jdbcPagamentoExtra.atualizar(pagamentoExtra, idRev);
            }
            assistenteBarraProgresso.conta();
        }
    }

    private void percorrerLiberacoesFinanceiras(List<BorderoLiberacaoFinanceira> liberacoesFinanceiras, AssistenteBarraProgresso assistenteBarraProgresso) {
        for (BorderoLiberacaoFinanceira borderoLiberacaoFinanceira : liberacoesFinanceiras) {
            if (!SituacaoItemBordero.INDEFIRIDO.equals(borderoLiberacaoFinanceira.getSituacaoItemBordero())
                && !SituacaoItemBordero.INDEFIRIDO.equals(borderoLiberacaoFinanceira.getSituacaoItemBordero())) {

                iniciarVariaveisLiberacaoFinanceira(borderoLiberacaoFinanceira);
                construirLinhaTipoDois();
            }
            assistenteBarraProgresso.conta();
        }
    }

    private void percorrerTransferenciasFinanceiras(List<BorderoTransferenciaFinanceira> transferenciasFinanceiras, AssistenteBarraProgresso assistenteBarraProgresso) {
        for (BorderoTransferenciaFinanceira borderoTransferencia : transferenciasFinanceiras) {
            if (!SituacaoItemBordero.INDEFIRIDO.equals(borderoTransferencia.getSituacaoItemBordero())
                && !SituacaoItemBordero.PAGO.equals(borderoTransferencia.getSituacaoItemBordero())) {

                iniciarVariaveisTransferenciaFinanceira(borderoTransferencia);
                construirLinhaTipoDois();
            }
            assistenteBarraProgresso.conta();
        }
    }

    private void percorrerTransferenciasMesmaUnidade(List<BorderoTransferenciaMesmaUnidade> transferenciasMesmaUnidade, AssistenteBarraProgresso assistenteBarraProgresso) {
        for (BorderoTransferenciaMesmaUnidade borderoTransferencia : transferenciasMesmaUnidade) {
            if (!SituacaoItemBordero.INDEFIRIDO.equals(borderoTransferencia.getSituacaoItemBordero())
                && !SituacaoItemBordero.PAGO.equals(borderoTransferencia.getSituacaoItemBordero())) {

                iniciarVariaveisTransferenciaMesmaUnidade(borderoTransferencia);
                construirLinhaTipoDois();
            }
            assistenteBarraProgresso.conta();
        }
    }

    public void recuperarBancoGeradorDoArquivo(Banco banco) {

        BancoObn bancoObn = configuracaoArquivoObnFacade.recuperarBancoGeradorArquivo(banco.getNumeroBanco());
        if (bancoObn != null) {
            itemOBN600.setBancoObn(bancoObn);
        } else {
            throw new RuntimeException("Configuração para banco (" + banco.toString() + ") gerador do arquivo não encontrada.");
        }
    }

    private void alterarSituacaoBorderoParaAguardandoBaixa(ArquivoRemessaBanco arb, UsuarioSistema usuarioCorrente, String ip) {
        for (ArquivoRemBancoBordero arquivoRemBancoBordero : arb.getArquivoRemBancoBorderos()) {
            if (arquivoRemBancoBordero.getBordero().getSituacao().equals(SituacaoBordero.AGUARDANDO_ENVIO)) {
                arquivoRemBancoBordero.getBordero().setSituacao(SituacaoBordero.AGUARDANDO_BAIXA);
                arquivoRemBancoBordero.getBordero().setDataGeracaoArquivo(arb.getDataGeracao());
                borderoFacade.salvarBorderoNoArquivoBancario(arquivoRemBancoBordero.getBordero(), usuarioCorrente, ip);
            }
        }
    }

    private HierarquiaOrganizacional codigoHieraquiaDaUnidade(UnidadeOrganizacional und, Date data) {
        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade("ORCAMENTARIA", und, data);
        return ho;
    }

    private void adicionarTrailer(ArquivoRemessaBanco arq) {
        linha.append("\r\n");
        linha.append(StringUtil.cortaOuCompletaDireita("", 35, "9"));
        linha.append(StringUtil.cortaOuCompletaDireita("", 285, " "));
        linha.append(StringUtil.cortarOuCompletarEsquerda((arq.getValorTotalDoc().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda(calculaValorSomatorioQtdRegistros(itemOBN600.getSequenciaInterna() - 1), 13, "0"));
        linha.append("\r\n");
    }

    private String calculaValorSomatorioQtdRegistros(long qtd) {
        int toReturn = 0;
        for (int x = 0; x <= qtd; x++) {
            toReturn += x;
        }
        return toReturn + "";
    }

    private void adicionarHeader(ArquivoRemessaBanco arq) {
        itemOBN600.setSequenciaInterna(itemOBN600.getSequenciaInterna() + 1);
        itemOBN600.setDataDoArquivo(new SimpleDateFormat("ddMMyyyy").format(arq.getDataGeracao()));
        itemOBN600.setHoraMinutoDoArquivo(new SimpleDateFormat("HHmm").format(new Date()));
        linha.append(StringUtil.cortaOuCompletaDireita("", 35, "0"));
        linha.append(itemOBN600.getDataDoArquivo());
        linha.append(itemOBN600.getHoraMinutoDoArquivo());
        linha.append(StringUtil.cortarOuCompletarEsquerda(arq.getNumero(), 5, "0"));
        linha.append(itemOBN600.getContratoObn().getNumeroHeaderObn600());
        linha.append(StringUtil.cortarOuCompletarEsquerda(arq.getContratoObn().getNumeroContrato(), 9, "0"));
        if (arq.getGerarArquivoTeste()) {
            linha.append(StringUtil.cortaOuCompletaDireita("", 51, " "));
            linha.append(StringUtil.cortaOuCompletaDireita("TESTE", 5, ""));
            linha.append(StringUtil.cortaOuCompletaDireita("", 220, " "));
        } else {
            linha.append(StringUtil.cortaOuCompletaDireita("", 276, " "));
        }
        linha.append("0000001");
    }

    private void iniciarVariaveisOrdemBancaria(Bordero bordero, ArquivoRemessaBanco arquivo) {

        itemOBN600.setOrdemBancaria(bordero);
        itemOBN600.setUnidadeOrganizacional(bordero.getUnidadeOrganizacional());
        HierarquiaOrganizacional hierarquia = codigoHieraquiaDaUnidade(itemOBN600.getUnidadeOrganizacional(), bordero.getDataGeracao());
        itemOBN600.setDescricaoUnidade(hierarquia.getSubordinada().getDescricao());
        itemOBN600.setDescricaoOrgao(hierarquia.getSuperior().getDescricao());
        itemOBN600.setCodigoDaUnidade(hierarquia.getCodigo().substring(7, 10));
        itemOBN600.setCodigoDoOrgao(hierarquia.getCodigo().substring(3, 6));
        itemOBN600.setContaBancariaEntidade(bordero.getSubConta().getContaBancariaEntidade());
        itemOBN600.setCodigoDaAgenciaDevolucaPorErro(itemOBN600.getContaBancariaEntidade().getAgencia().getNumeroAgenciaComDigitoVerificador().replace(" - ", ""));
        getNumeroContaBancariaDevolucaoPorErro();
        itemOBN600.setCidadeUnidade(itemOBN600.getUnidadeOrganizacional().getCidade());
        itemOBN600.setRuaUnidade(itemOBN600.getUnidadeOrganizacional().getEnderecoDesc());
        itemOBN600.setCepUnidade(itemOBN600.getUnidadeOrganizacional().getCep());
        itemOBN600.setUfUnidade(itemOBN600.getUnidadeOrganizacional().getUf());
        itemOBN600.setDataDaOrdemBancaria(new SimpleDateFormat("ddMMyyyy").format(bordero.getDataGeracaoArquivo() != null ? bordero.getDataGeracaoArquivo() : arquivo.getDataGeracao()));
        itemOBN600.setCodigoOrgaoUnidadeUGEmitente((StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoDoOrgao(), 3, "0") + "00" + StringUtil.cortaOuCompletaDireita(itemOBN600.getCodigoDaUnidade(), 3, "0")).replace(".", ""));
        verificarContaUnica(bordero);
    }


    public String getNumeroContaFavorecido(ModalidadeConta modalidadeConta, Banco banco, String contaComDigito, Pessoa fornecedor) {

        BancoObn bancoObnFornecedor = configuracaoArquivoObnFacade.recuperarBancoGeradorArquivo(banco.getNumeroBanco());
        if (bancoObnFornecedor != null) {
            if (bancoObnFornecedor.isArquivoCaixaEconomica()) {
                String codigoOperacaoCaixa = "";
                if (isFornecedorPF(fornecedor)) {
                    if (ModalidadeConta.CONTA_CORRENTE.equals(modalidadeConta)
                        || ModalidadeConta.CONTA_SALARIO.equals(modalidadeConta)) {
                        codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.CONTA_CORRENTE_PESSOA_FISICA.getCodigo();
                    }
                } else {
                    if (ModalidadeConta.CONTA_CORRENTE.equals(modalidadeConta)) {
                        codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.CONTA_CORRENTE_PESSOA_JURIDICA.getCodigo();
                    }
                    if (ModalidadeConta.CONTA_POUPANCA_PESSOA_JURIDICA.equals(modalidadeConta)) {
                        codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.POUPANCA_PESSOA_JURIDICA.getCodigo();
                    }
                    if (ModalidadeConta.ENTIDADES_PUBLICAS.equals(modalidadeConta)) {
                        codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.ENTIDADES_PUBLICAS.getCodigo();
                    }
                }
                if (ModalidadeConta.CONTA_POUPANCA.equals(modalidadeConta)) {

                    codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.POUPANCA.getCodigo();
                }
                if (ModalidadeConta.CONTA_CAIXA_FACIL.equals(modalidadeConta)) {
                    codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.CONTA_CAIXA_FACIL.getCodigo();
                }
                if (ModalidadeConta.CONTA_SALARIO_NSGD.equals(modalidadeConta)) {
                    return codigoOperacaoCaixa + StringUtil.cortarOuCompletarEsquerda(contaComDigito, 10, "0");
                }
                return codigoOperacaoCaixa + StringUtil.cortarOuCompletarEsquerda(contaComDigito, 9, "0");
            } else {
                if (ModalidadeConta.CONTA_POUPANCA.equals(modalidadeConta)) {
                    return "51" + StringUtil.cortarOuCompletarEsquerda(contaComDigito, 8, "0");
                } else {
                    return StringUtil.cortarOuCompletarEsquerda(contaComDigito, 10, "0");
                }
            }
        } else {
            return StringUtil.cortarOuCompletarEsquerda(contaComDigito, 10, "0");
        }
    }


    private void getNumeroContaBancariaDevolucaoPorErro() {
        if (itemOBN600.getBancoObn().isArquivoCaixaEconomica()) {
            String codigoOperacaoCaixa = "";
            if (ModalidadeConta.CONTA_CORRENTE.equals(itemOBN600.getContaBancariaEntidade().getModalidadeConta())) {
                codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.CONTA_CORRENTE_PESSOA_JURIDICA.getCodigo();
            }
            if (ModalidadeConta.ENTIDADES_PUBLICAS.equals(itemOBN600.getContaBancariaEntidade().getModalidadeConta())) {
                codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.ENTIDADES_PUBLICAS.getCodigo();
            }
            if (ModalidadeConta.CONTA_POUPANCA.equals(itemOBN600.getContaBancariaEntidade().getModalidadeConta())) {
                codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.POUPANCA.getCodigo();
            }
            if (ModalidadeConta.CONTA_POUPANCA_PESSOA_JURIDICA.equals(itemOBN600.getContaBancariaEntidade().getModalidadeConta())) {
                codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.POUPANCA_PESSOA_JURIDICA.getCodigo();
            }
            if (ModalidadeConta.CONTA_CAIXA_FACIL.equals(itemOBN600.getContaBancariaEntidade().getModalidadeConta())) {
                codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.CONTA_CAIXA_FACIL.getCodigo();
            }
            itemOBN600.setCodigoDaContaDevolucaPorErro(StringUtil.cortarOuCompletarEsquerda(codigoOperacaoCaixa + StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaBancariaEntidade().getNumeroConta() + itemOBN600.getContaBancariaEntidade().getDigitoVerificador(), 9, "0"), 13, "0"));
        } else {
            itemOBN600.setCodigoDaContaDevolucaPorErro(itemOBN600.getContaBancariaEntidade().getNumeroContaComDigitoVerificadorArquivoOBN600());
        }
    }

    private void iniciarVariaveisDespesaExtra(BorderoPagamentoExtra borderoPagamentoExtra, PagamentoExtra pagamentoExtra) {
        itemOBN600.setCodigoBorderoRelacaoPtos(new SimpleDateFormat("yy").format(new Date()) + pagamentoExtra.getNumeroRE());
        itemOBN600.setFornecedor(pagamentoExtra.getFornecedor());
        itemOBN600.setEndereco(enderecoPessoa(itemOBN600.getFornecedor()));
        itemOBN600.setContaFornecedor(borderoPagamentoExtra.getContaCorrenteFavorecido());

        iniciarVariaveisContaFornecedor(pagamentoExtra.getTipoDocumento());

        itemOBN600.setNumeroDoMovimento(TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA.getCodigo() + pagamentoExtra.getNumero());
        itemOBN600.setTipoOperacaoPagto(borderoPagamentoExtra.getTipoOperacaoPagto());
        itemOBN600.setValor(borderoPagamentoExtra.getValor());
        itemOBN600.setUf(recuperaUfPorNome(itemOBN600.getEndereco(), itemOBN600.getFornecedor()));
        if (TipoDocPagto.DEPJU.equals(pagamentoExtra.getTipoDocumento())) {
            //quando é deposito judicial, o histórico deve começar na posição 269 e não 264, por isso tem os espaços.
            itemOBN600.setHistorico("     " + pagamentoExtra.getHistoricoArquivoOBN600());
        } else {
            itemOBN600.setHistorico(pagamentoExtra.getHistoricoArquivoOBN600());
        }
        itemOBN600.setFinalidadePagamento(getFinalidadePagamento(pagamentoExtra));
    }

    private void iniciarVariaveisContaFornecedor(TipoDocPagto tipoDocPagto) {
        itemOBN600.setCodigoContaCorrenteFornecedor("");
        itemOBN600.setAgenciaFornecedor(null);
        itemOBN600.setBancoFornecedor(null);

        if (itemOBN600.getContaFornecedor() != null) {
            ModalidadeConta modalidadeConta = itemOBN600.getContaFornecedor().getModalidadeConta();
            Banco banco = itemOBN600.getContaFornecedor().getBanco();
            String codigoContaComDigito = itemOBN600.getContaFornecedor().getNumeroContaComDigito();
            itemOBN600.setCodigoContaCorrenteFornecedor(getNumeroContaFavorecido(modalidadeConta, banco, codigoContaComDigito, itemOBN600.getFornecedor()));

            itemOBN600.setAgenciaFornecedor(itemOBN600.getContaFornecedor().getAgencia());
            itemOBN600.setBancoFornecedor(banco);

            if (itemOBN600.getBancoObn().isArquivoBancoDoBrasil() && TipoDocPagto.DEPJU.equals(tipoDocPagto)) {
                itemOBN600.setCodigoContaCorrenteFornecedor(StringUtil.cortaOuCompletaDireita(TipoDocPagto.DEPJU.name(), 10, " "));
            }
        }
    }

    private void iniciarVariaveisPagamento(BorderoPagamento borderoPagamento, Pagamento pagamento) {
        itemOBN600.setCodigoBorderoRelacaoPtos(new SimpleDateFormat("yy").format(new Date()) + pagamento.getNumeroRE());
        itemOBN600.setFornecedor(pagamento.getLiquidacao().getEmpenho().getFornecedor());
        itemOBN600.setEndereco(enderecoPessoa(itemOBN600.getFornecedor()));
        itemOBN600.setContaFornecedor(borderoPagamento.getContaCorrenteFavorecido());

        iniciarVariaveisContaFornecedor(pagamento.getTipoDocPagto());

        itemOBN600.setNumeroDoMovimento(TipoMovimentoArquivoBancario.PAGAMENTO.getCodigo() + pagamento.getNumeroPagamento());
        itemOBN600.setTipoOperacaoPagto(borderoPagamento.getTipoOperacaoPagto());
        itemOBN600.setValor(borderoPagamento.getValor());
        itemOBN600.setUf(recuperaUfPorNome(itemOBN600.getEndereco(), itemOBN600.getFornecedor()));
        itemOBN600.setHistorico(pagamento.getHistoricoArquivoOBN600());
        itemOBN600.setFinalidadePagamento(getFinalidadePagamento(pagamento));
    }

    private void iniciarVariaveisLiberacaoFinanceira(BorderoLiberacaoFinanceira borderoLiberacaoFinanceira) {
        LiberacaoCotaFinanceira liberacao = borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira();
        Preconditions.checkNotNull(liberacao.getContaFinanceiraRecebida().getContaBancariaEntidade(), " A Conta Bancária da Conta Financeira Recebida " + liberacao.getContaFinanceiraRecebida() + " da Liberação " + liberacao.toString() + " está vazia.");
        Preconditions.checkNotNull(liberacao.getContaFinanceiraRecebida().getContaBancariaEntidade().getEntidade(), " A Entidade da Conta Bancária " + liberacao.getContaFinanceiraRecebida().getContaBancariaEntidade() + " está vazia.");
        Preconditions.checkNotNull(liberacao.getContaFinanceiraRecebida().getContaBancariaEntidade().getEntidade().getPessoaJuridica(), " A Pessoa da Entidade " + liberacao.getContaFinanceiraRecebida().getContaBancariaEntidade().getEntidade() + " está vazia.");

        itemOBN600.setCodigoBorderoRelacaoPtos(new SimpleDateFormat("yy").format(new Date()) + borderoLiberacaoFinanceira.getBordero().getSequenciaArquivo());
        itemOBN600.setFornecedor(liberacao.getContaFinanceiraRecebida().getContaBancariaEntidade().getEntidade().getPessoaJuridica());
        itemOBN600.setEndereco(enderecoPessoa(itemOBN600.getFornecedor()));
        Preconditions.checkNotNull(itemOBN600.getEndereco(), "Não foi encontrado um endereço para a Pessoa " + itemOBN600.getFornecedor().toString());
        itemOBN600.setContaBancariaEntidade(liberacao.getContaFinanceiraRecebida().getContaBancariaEntidade());

        iniciarVariaveisContaEntidade();

        itemOBN600.setNumeroDoMovimento(TipoMovimentoArquivoBancario.LIBERACAO.getCodigo() + liberacao.getNumero());
        itemOBN600.setTipoOperacaoPagto(borderoLiberacaoFinanceira.getTipoOperacaoPagto());
        itemOBN600.setValor(borderoLiberacaoFinanceira.getValor());
        itemOBN600.setUf(recuperaUfPorNome(itemOBN600.getEndereco(), itemOBN600.getFornecedor()));
        itemOBN600.setHistorico(liberacao.getHistoricoArquivoOBN600());
        itemOBN600.setFinalidadePagamento(getFinalidadePagamento(liberacao));
    }

    private void iniciarVariaveisContaEntidade() {
        if (itemOBN600.getContaBancariaEntidade() != null) {
            ModalidadeConta modalidadeConta = itemOBN600.getContaBancariaEntidade().getModalidadeConta();
            Banco banco = itemOBN600.getContaBancariaEntidade().getAgencia().getBanco();
            String codigoContaComDigito = itemOBN600.getContaBancariaEntidade().getNumeroContaComDigito();

            itemOBN600.setCodigoContaCorrenteFornecedor(getNumeroContaFavorecido(modalidadeConta, banco, codigoContaComDigito, itemOBN600.getFornecedor()));
            itemOBN600.setAgenciaFornecedor(itemOBN600.getContaBancariaEntidade().getAgencia());
            itemOBN600.setBancoFornecedor(banco);
        } else {
            itemOBN600.setCodigoContaCorrenteFornecedor("");
            itemOBN600.setAgenciaFornecedor(null);
            itemOBN600.setBancoFornecedor(null);
        }
    }


    private void iniciarVariaveisTransferenciaFinanceira(BorderoTransferenciaFinanceira borderoTransferencia) {
        TransferenciaContaFinanceira transf = borderoTransferencia.getTransferenciaContaFinanceira();
        Preconditions.checkNotNull(transf.getSubContaDeposito().getContaBancariaEntidade(), " A Conta Bancária da Conta Financeira Recebida " + transf.getSubContaDeposito() + " da Transferência " + transf.toString() + " está vazia.");
        Preconditions.checkNotNull(transf.getSubContaDeposito().getContaBancariaEntidade().getEntidade(), " A Entidade da Conta Bancária " + transf.getSubContaDeposito().getContaBancariaEntidade() + " está vazia.");
        Preconditions.checkNotNull(transf.getSubContaDeposito().getContaBancariaEntidade().getEntidade().getPessoaJuridica(), " A Pessoa da Entidade " + transf.getSubContaDeposito().getContaBancariaEntidade().getEntidade() + " está vazia.");

        itemOBN600.setCodigoBorderoRelacaoPtos(new SimpleDateFormat("yy").format(new Date()) + borderoTransferencia.getBordero().getSequenciaArquivo());
        itemOBN600.setFornecedor(transf.getSubContaDeposito().getContaBancariaEntidade().getEntidade().getPessoaJuridica());
        itemOBN600.setEndereco(enderecoPessoa(itemOBN600.getFornecedor()));
        Preconditions.checkNotNull(itemOBN600.getEndereco(), "Não foi encontrado um endereço para a Pessoa: " + itemOBN600.getFornecedor().toString());
        itemOBN600.setContaBancariaEntidade(transf.getSubContaDeposito().getContaBancariaEntidade());

        iniciarVariaveisContaEntidade();

        itemOBN600.setNumeroDoMovimento(TipoMovimentoArquivoBancario.TRANSFERENCIA_FINANCEIRA.getCodigo() + transf.getNumero());
        itemOBN600.setTipoOperacaoPagto(borderoTransferencia.getTipoOperacaoPagto());
        itemOBN600.setValor(borderoTransferencia.getValor());
        itemOBN600.setUf(recuperaUfPorNome(itemOBN600.getEndereco(), itemOBN600.getFornecedor()));
        itemOBN600.setHistorico(transf.getHistoricoArquivoOBN600());
        itemOBN600.setFinalidadePagamento(getFinalidadePagamento(transf));
    }

    private void iniciarVariaveisTransferenciaMesmaUnidade(BorderoTransferenciaMesmaUnidade borderoTransferencia) {
        TransferenciaMesmaUnidade transf = borderoTransferencia.getTransferenciaMesmaUnidade();

        Preconditions.checkNotNull(transf.getSubContaDeposito().getContaBancariaEntidade(), " A Conta Bancária da Conta Financeira Recebida " + transf.getSubContaDeposito() + " da Transferência " + transf.toString() + " está vazia.");
        Preconditions.checkNotNull(transf.getSubContaDeposito().getContaBancariaEntidade().getEntidade(), " A Entidade da Conta Bancária " + transf.getSubContaDeposito().getContaBancariaEntidade() + " está vazia.");
        Preconditions.checkNotNull(transf.getSubContaDeposito().getContaBancariaEntidade().getEntidade().getPessoaJuridica(), " A Pessoa da Entidade " + transf.getSubContaDeposito().getContaBancariaEntidade().getEntidade() + " está vazia.");

        itemOBN600.setCodigoBorderoRelacaoPtos(new SimpleDateFormat("yy").format(new Date()) + borderoTransferencia.getBordero().getSequenciaArquivo());
        itemOBN600.setFornecedor(transf.getSubContaDeposito().getContaBancariaEntidade().getEntidade().getPessoaJuridica());
        itemOBN600.setEndereco(enderecoPessoa(itemOBN600.getFornecedor()));
        Preconditions.checkNotNull(itemOBN600.getEndereco(), "Não foi encontrado um endereço para a Pessoa: " + itemOBN600.getFornecedor().toString());
        itemOBN600.setContaBancariaEntidade(transf.getSubContaDeposito().getContaBancariaEntidade());

        iniciarVariaveisContaEntidade();

        itemOBN600.setNumeroDoMovimento(TipoMovimentoArquivoBancario.TRANSFERENCIA_MESMA_UNIDADE.getCodigo() + transf.getNumero());
        itemOBN600.setTipoOperacaoPagto(borderoTransferencia.getTipoOperacaoPagto());
        itemOBN600.setValor(borderoTransferencia.getValor());
        itemOBN600.setUf(recuperaUfPorNome(itemOBN600.getEndereco(), itemOBN600.getFornecedor()));
        itemOBN600.setHistorico(transf.getHistoricoArquivoOBN600());
        itemOBN600.setFinalidadePagamento(getFinalidadePagamento(transf));
    }

    private void iniciarVariaveisGuiaDespesaExtra(PagamentoExtra pagamentoExtra, GuiaPagamentoExtra guiaPagamentoExtra) {
        guiaPagamentoExtra.setPagamentoExtra(pagamentoExtra);
        itemOBN600.setTipoDocPagto(pagamentoExtra.getTipoDocumento());
        itemOBN600.setGuia(guiaPagamentoExtra);
    }

    private void iniciarVariaveisGuiaPagamento(Pagamento pagamento, GuiaPagamento guiaPagamento) {
        guiaPagamento.setPagamento(pagamento);
        itemOBN600.setTipoDocPagto(pagamento.getTipoDocPagto());
        itemOBN600.setGuia(guiaPagamento);
    }

    private void verificarContaUnica(Bordero bordero) {
        if (itemOBN600.getBancoObn().isArquivoBancoDoBrasil()) {
            if (bordero.getSubConta().getContaUnica() != null
                && bordero.getSubConta().getContaUnica()) {
                itemOBN600.setContaUnica(Boolean.TRUE);
            } else {
                itemOBN600.setContaUnica(Boolean.FALSE);
            }
        } else {
            itemOBN600.setContaUnica(Boolean.FALSE);
        }
    }

    private Boolean podeConstruirLinhaTipoUm(List<Bordero> listaOrdemBancarias) {
        for (Bordero ob : listaOrdemBancarias) {
            if (ob.getUnidadeOrganizacional().getId().equals(itemOBN600.getUnidadeOrganizacional().getId())
                && ob.getSubConta().getContaBancariaEntidade().getId().equals(itemOBN600.getContaBancariaEntidade().getId())) {
                return false;
            }
        }
        return true;
    }

    private Boolean podeConstruirLinhaTipoDois(List<Bordero> listaOrdemBancarias) {
        for (Bordero ob : listaOrdemBancarias) {
            if (ob.getSequenciaArquivo().equals(itemOBN600.getOrdemBancaria().getSequenciaArquivo())) {
                return false;
            }
        }
        return true;
    }

    private boolean isFornecedorPF(Pessoa fornecedor) {
        return fornecedor instanceof PessoaFisica;
    }

    private boolean isFornecedorPJ(Pessoa fornecedor) {
        return fornecedor instanceof PessoaJuridica;
    }

    private void construirLinhaTipoUm() {
        linha.append("\r\n");
        linha.append("1");
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoDaAgenciaDevolucaPorErro(), 5, "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoOrgaoUnidadeUGEmitente(), 11, "0"));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getCodigoDaContaDevolucaPorErro(), 10, " "));
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 26, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getDescricaoUnidade(), 45, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getDescricaoOrgao(), 45, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getRuaUnidade(), 65, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getCidadeUnidade(), 45, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getCepUnidade(), 88, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getUfUnidade(), 2, " "));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getSequenciaInterna() + "", 7, "0"));
        itemOBN600.setSequenciaInterna(itemOBN600.getSequenciaInterna() + 1);
    }


    private void construirLinhaTipoDois() {
        LinhaOBN600TipoDois linhaOBN600TipoDois = new LinhaOBN600TipoDois();
        String linhaConstruida = linhaOBN600TipoDois.construirLinha(itemOBN600).toString();
        linha.append(linhaConstruida);
        itemOBN600.setSequenciaInterna(itemOBN600.getSequenciaInterna() + 1);
    }

    private void construirLinhaTipoTres(Pagamento pagamento) {
        for (GuiaPagamento guiaPagamento : pagamento.getGuiaPagamento()) {
            if (guiaPagamento.getGuiaGRU() != null && guiaPagamento.getGuiaGRU().getId() != null) {
                guiaPagamento.setGuiaGRU(jdbcGuiaGRU.buscarGuiaGRUPorId(guiaPagamento.getGuiaGRU().getId()));
                iniciarVariaveisGuiaPagamento(pagamento, guiaPagamento);
                LinhaOBN600TipoTres linhaOBN600TipoTres = new LinhaOBN600TipoTres();
                String pagamentoTipoTres = linhaOBN600TipoTres.construirLinha(itemOBN600).toString();
                linha.append(pagamentoTipoTres);
                itemOBN600.setSequenciaInterna(itemOBN600.getSequenciaInterna() + 1);
                itemOBN600.setSequenciaInternaGuia(itemOBN600.getSequenciaInternaGuia() + 1);
            }
        }
    }

    private void construirLinhaTipoTres(PagamentoExtra pagamentoExtra) {
        for (GuiaPagamentoExtra guiaPagamentoExtra : pagamentoExtra.getGuiaPagamentoExtras()) {
            if (guiaPagamentoExtra.getGuiaGRU() != null && guiaPagamentoExtra.getGuiaGRU().getId() != null) {
                guiaPagamentoExtra.setGuiaGRU(jdbcGuiaGRU.buscarGuiaGRUPorId(guiaPagamentoExtra.getGuiaGRU().getId()));
                iniciarVariaveisGuiaDespesaExtra(pagamentoExtra, guiaPagamentoExtra);
                LinhaOBN600TipoTres linhaOBN600TipoTres = new LinhaOBN600TipoTres();
                String pagamentoTipoTres = linhaOBN600TipoTres.construirLinha(itemOBN600).toString();
                linha.append(pagamentoTipoTres);
                itemOBN600.setSequenciaInterna(itemOBN600.getSequenciaInterna() + 1);
                itemOBN600.setSequenciaInternaGuia(itemOBN600.getSequenciaInternaGuia() + 1);
            }
        }
    }

    private void construirLinhaTipoQuatroPagamento(Pagamento pagamento) {
        for (GuiaPagamento guiaPagamento : pagamento.getGuiaPagamento()) {
            guiaPagamento.setGuiaConvenio(recuperarGuiaConvenioEmJdbc(guiaPagamento.getGuiaConvenio()));
            guiaPagamento.setGuiaFatura(recuperarGuiaFaturaEmJdbc(guiaPagamento.getGuiaFatura()));
            if (guiaPagamento.getGuiaGRU() != null && guiaPagamento.getGuiaGRU().getId() != null) {
                guiaPagamento.setGuiaGRU(jdbcGuiaGRU.buscarGuiaGRUPorId(guiaPagamento.getGuiaGRU().getId()));
                Pessoa pessoa = guiaPagamento.getPessoa();
                if (pessoa != null) {
                    pessoa = recuperarPessoaEmJdbc(pessoa);
                    itemOBN600.setFornecedor(pessoa);
                }
            }
            iniciarVariaveisGuiaPagamento(pagamento, guiaPagamento);

            LinhaOBN600TipoQuatro linhaOBN600TipoQuatro = new LinhaOBN600TipoQuatro();
            linha.append(linhaOBN600TipoQuatro.construirLinha(itemOBN600));

            itemOBN600.setSequenciaInterna(itemOBN600.getSequenciaInterna() + 1);
            itemOBN600.setSequenciaInternaGuia(itemOBN600.getSequenciaInternaGuia() + 1);
        }
    }

    public GuiaConvenio recuperarGuiaConvenioEmJdbc(GuiaConvenio guiaConvenio) {
        if (Util.isNotNull(guiaConvenio)) {
            guiaConvenio = jdbcGuiaConvenio.buscarGuiaConvenioPorId(guiaConvenio.getId());
        }
        return guiaConvenio;
    }

    public Pessoa recuperarPessoaEmJdbc(Pessoa pessoa) {
        if (Util.isNotNull(pessoa)) {
            pessoa = jdbcPessoa.buscarPessoaPorId(pessoa.getId());
        }
        return pessoa;
    }

    public GuiaFatura recuperarGuiaFaturaEmJdbc(GuiaFatura guiaFatura) {
        if (Util.isNotNull(guiaFatura)) {
            guiaFatura = jdbcGuiaFatura.buscarGuiaConvenioPorId(guiaFatura.getId());
        }
        return guiaFatura;
    }

    public GuiaGPS recuperarGuiaGpsEmJdbc(GuiaGPS guiaGPS) {
        if (Util.isNotNull(guiaGPS)) {
            guiaGPS = jdbcGuiaGps.buscarGuiaGpsPorId(guiaGPS.getId());
        }
        return guiaGPS;
    }

    public GuiaDARF recuperarGuiaDarfEmJdbc(GuiaDARF guiaDARF) {
        if (Util.isNotNull(guiaDARF)) {
            guiaDARF = jdbcGuiaDARF.buscarGuiaDARFPorId(guiaDARF.getId());
        }
        return guiaDARF;
    }

    public GuiaDARFSimples recuperarGuiaDarfSimplesEmJdbc(GuiaDARFSimples guiaDARFSimples) {
        if (Util.isNotNull(guiaDARFSimples)) {
            guiaDARFSimples = jdbcGuiaDARFSimples.buscarGuiaDARFSimplesPorId(guiaDARFSimples.getId());
        }
        return guiaDARFSimples;
    }

    private void construirLinhaTipoQuatroDespesaExtra(PagamentoExtra pagamentoExtra) {
        for (GuiaPagamentoExtra guiaPagamentoExtra : pagamentoExtra.getGuiaPagamentoExtras()) {
            guiaPagamentoExtra.setGuiaConvenio(recuperarGuiaConvenioEmJdbc(guiaPagamentoExtra.getGuiaConvenio()));
            guiaPagamentoExtra.setGuiaFatura(recuperarGuiaFaturaEmJdbc(guiaPagamentoExtra.getGuiaFatura()));
            if (guiaPagamentoExtra.getGuiaGRU() != null && guiaPagamentoExtra.getGuiaGRU().getId() != null) {
                guiaPagamentoExtra.setGuiaGRU(jdbcGuiaGRU.buscarGuiaGRUPorId(guiaPagamentoExtra.getGuiaGRU().getId()));
            }
            iniciarVariaveisGuiaDespesaExtra(pagamentoExtra, guiaPagamentoExtra);
            LinhaOBN600TipoQuatro linhaOBN600TipoQuatro = new LinhaOBN600TipoQuatro();
            linha.append(linhaOBN600TipoQuatro.construirLinha(itemOBN600));

            itemOBN600.setSequenciaInterna(itemOBN600.getSequenciaInterna() + 1);
            itemOBN600.setSequenciaInternaGuia(itemOBN600.getSequenciaInternaGuia() + 1);
        }
    }

    private void construirLinhaTipoCincoPagamento(Pagamento pagamento) {
        for (GuiaPagamento guiaPagamento : pagamento.getGuiaPagamento()) {
            guiaPagamento.setPessoa(recuperarPessoaEmJdbc(guiaPagamento.getPessoa()));
            guiaPagamento.setGuiaGPS(recuperarGuiaGpsEmJdbc(guiaPagamento.getGuiaGPS()));
            guiaPagamento.setGuiaDARF(recuperarGuiaDarfEmJdbc(guiaPagamento.getGuiaDARF()));
            guiaPagamento.setGuiaDARFSimples(recuperarGuiaDarfSimplesEmJdbc(guiaPagamento.getGuiaDARFSimples()));
            iniciarVariaveisGuiaPagamento(pagamento, guiaPagamento);

            LinhaOBN600TipoCinco linhaOBN600TipoCinco = new LinhaOBN600TipoCinco();
            linha.append(linhaOBN600TipoCinco.construirLinha(itemOBN600));

            itemOBN600.setSequenciaInterna(itemOBN600.getSequenciaInterna() + 1);
            itemOBN600.setSequenciaInternaGuia(itemOBN600.getSequenciaInternaGuia() + 1);
        }
    }


    private void construirLinhaTipoCincoDespesaExtra(PagamentoExtra pagamentoExtra) {
        if (pagamentoExtra.getGuiaPagamentoExtras() != null && !pagamentoExtra.getGuiaPagamentoExtras().isEmpty()) {
            for (GuiaPagamentoExtra guiaPagamentoExtra : pagamentoExtra.getGuiaPagamentoExtras()) {
                guiaPagamentoExtra.setPessoa(recuperarPessoaEmJdbc(guiaPagamentoExtra.getPessoa()));
                guiaPagamentoExtra.setGuiaGPS(recuperarGuiaGpsEmJdbc(guiaPagamentoExtra.getGuiaGPS()));
                guiaPagamentoExtra.setGuiaDARF(recuperarGuiaDarfEmJdbc(guiaPagamentoExtra.getGuiaDARF()));
                guiaPagamentoExtra.setGuiaDARFSimples(recuperarGuiaDarfSimplesEmJdbc(guiaPagamentoExtra.getGuiaDARFSimples()));
                iniciarVariaveisGuiaDespesaExtra(pagamentoExtra, guiaPagamentoExtra);

                LinhaOBN600TipoCinco linhaOBN600TipoCinco = new LinhaOBN600TipoCinco();
                linha.append(linhaOBN600TipoCinco.construirLinha(itemOBN600));

                itemOBN600.setSequenciaInterna(itemOBN600.getSequenciaInterna() + 1);
                itemOBN600.setSequenciaInternaGuia(itemOBN600.getSequenciaInternaGuia() + 1);
            }
        }
    }

    private boolean isGuia(TipoOperacaoPagto tipoOperacaoPagto) {
        return isGuiaCodigoBarra(tipoOperacaoPagto) || isGuiaSemCodigoBarra(tipoOperacaoPagto) || isGuiaTransferencia(tipoOperacaoPagto);
    }

    private boolean isGuiaSemCodigoBarra(TipoOperacaoPagto tipoOperacaoPagto) {
        return TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA.equals(tipoOperacaoPagto)
            || TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.equals(tipoOperacaoPagto);

    }

    private boolean isGuiaCodigoBarra(TipoOperacaoPagto tipoOperacaoPagto) {
        return TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA.equals(tipoOperacaoPagto)
            || TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.equals(tipoOperacaoPagto);
    }

    private boolean isGuiaTransferencia(TipoOperacaoPagto tipoOperacaoPagto) {
        return TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA.equals(tipoOperacaoPagto)
            || TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA_NAO_CONTA_UNICA.equals(tipoOperacaoPagto);
    }

    private FinalidadePagamento getFinalidadePagamento(PagamentoExtra p) {
        if (p.getSubConta().getFinalidadeOB() != null) {
            if (p.getSubConta().getFinalidadeOB()) {
                return p.getFinalidadePagamento();
            }
        }
        return null;
    }

    private FinalidadePagamento getFinalidadePagamento(LiberacaoCotaFinanceira p) {
        if (p.getSubConta().getFinalidadeOB() != null) {
            if (p.getSubConta().getFinalidadeOB()) {
                return p.getFinalidadePagamento();
            }
        }
        return null;
    }

    private FinalidadePagamento getFinalidadePagamento(TransferenciaContaFinanceira p) {
        if (p.getSubContaDeposito().getFinalidadeOB() != null) {
            if (p.getSubContaDeposito().getFinalidadeOB()) {
                return p.getFinalidadePagamento();
            }
        }
        return null;
    }

    private FinalidadePagamento getFinalidadePagamento(TransferenciaMesmaUnidade p) {
        if (p.getSubContaDeposito().getFinalidadeOB() != null) {
            if (p.getSubContaDeposito().getFinalidadeOB()) {
                return p.getFinalidadePagamento();
            }
        }
        return null;
    }

    private FinalidadePagamento getFinalidadePagamento(Pagamento p) {
        if (p.getSubConta().getFinalidadeOB() != null) {
            if (p.getSubConta().getFinalidadeOB()) {
                return p.getFinalidadePagamento();
            }
        }
        return null;
    }

    private String recuperaUfPorNome(EnderecoCorreio enderecoCorreio, Pessoa pessoa) {
        if (enderecoCorreio == null) {
            return "";
        }
        if (enderecoCorreio.getUf() == null) {
            String caminho = "/pessoa-fisica/editar/" + pessoa.getId() + "/";
            if (pessoa.isPessoaJuridica()) {
                caminho = "/pessoa-juridica/editar/" + pessoa.getId() + "/";
            }
            String url = "<b><a href='" + caminho + "' target='_blank'>Link</a></b>";
            throw new ExcecaoNegocioGenerica("O campo UF no endereço da pessoa " + pessoa.getNomeCpfCnpj() + " deve ser informado. " +
                "Acesse o cadastro da pessoa e altere os dados de endereço. " + url + "");
        }
        String uf = enderecoCorreio.getUf();
        if (uf.trim().length() > 2) {
            return estadoFacade.recuperaUfPorNome(uf);
        } else {
            return uf;
        }
    }

    private boolean objetoNaoEstaSalvo(LayoutArquivoRemessa obj) {
        try {
            return obj.getId() == null;
        } catch (NullPointerException ex) {
            return true;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao verifica Status de persitencia do Objeto");
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoHeaderObn600(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {
            List<RegistroArquivoRemessa> headerArquivoRemessas = new ArrayList<>();
            headerArquivoRemessas.add(new RegistroArquivoRemessa(1, 35, 35, "NUM", lar, TipoRegistroArquivoRemessa.HEADER, TipoRegistroOBN600Header.ZEROS));
            headerArquivoRemessas.add(new RegistroArquivoRemessa(36, 8, 43, "NUM", lar, TipoRegistroArquivoRemessa.HEADER, TipoRegistroOBN600Header.DATA_GERACAO));
            headerArquivoRemessas.add(new RegistroArquivoRemessa(44, 4, 47, "NUM", lar, TipoRegistroArquivoRemessa.HEADER, TipoRegistroOBN600Header.HORA_GERACAO));
            headerArquivoRemessas.add(new RegistroArquivoRemessa(48, 5, 52, "NUM", lar, TipoRegistroArquivoRemessa.HEADER, TipoRegistroOBN600Header.NUMERO_REMESSA_CONSECUTIVO));
            headerArquivoRemessas.add(new RegistroArquivoRemessa(53, 6, 58, "ALFA", lar, TipoRegistroArquivoRemessa.HEADER, TipoRegistroOBN600Header.UMZEROEZEROZEROUM));
            headerArquivoRemessas.add(new RegistroArquivoRemessa(59, 9, 67, "NUM", lar, TipoRegistroArquivoRemessa.HEADER, TipoRegistroOBN600Header.NUMERO_CONTRATO_BANCO_CLIENTE));
            headerArquivoRemessas.add(new RegistroArquivoRemessa(68, 276, 343, "ALFA", lar, TipoRegistroArquivoRemessa.HEADER, TipoRegistroOBN600Header.BRANCOS));
            headerArquivoRemessas.add(new RegistroArquivoRemessa(344, 7, 350, "NUM", lar, TipoRegistroArquivoRemessa.HEADER, TipoRegistroOBN600Header.NUMERO_SEQUENCIAL_ARQUIVO));
            return headerArquivoRemessas;
        } else {
            return lar.getHeaderArquivoRemessas();
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoRegistroUmObn600(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {
            List<RegistroArquivoRemessa> registroUmArquivoRemessas = new ArrayList<>();
            registroUmArquivoRemessas.add(new RegistroArquivoRemessa(1, 1, 1, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_1, TipoRegistroOBN600Tipo1.UM));
            registroUmArquivoRemessas.add(new RegistroArquivoRemessa(2, 5, 6, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_1, TipoRegistroOBN600Tipo1.CODIGO_DV_AGENCIA_BANCARIA_EMITENTE));
            registroUmArquivoRemessas.add(new RegistroArquivoRemessa(7, 11, 17, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_1, TipoRegistroOBN600Tipo1.CODIGO_UG_GESTAO_EMITENTE));
            registroUmArquivoRemessas.add(new RegistroArquivoRemessa(18, 10, 27, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_1, TipoRegistroOBN600Tipo1.CONTA_UG_DV));
            registroUmArquivoRemessas.add(new RegistroArquivoRemessa(28, 26, 53, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_1, TipoRegistroOBN600Tipo1.BRANCOS));
            registroUmArquivoRemessas.add(new RegistroArquivoRemessa(54, 45, 98, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_1, TipoRegistroOBN600Tipo1.NOME_UG));
            registroUmArquivoRemessas.add(new RegistroArquivoRemessa(99, 251, 350, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_1, TipoRegistroOBN600Tipo1.BRANCOS));
            return registroUmArquivoRemessas;
        } else {
            return lar.getRegistroUmArquivoRemessas();
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoRegistroDoisObn600(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {

            List<RegistroArquivoRemessa> registroDoisArquivoRemessas = new ArrayList<>();
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(1, 1, 1, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.DOIS));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(2, 5, 6, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_DV_AGBANCARIA_UG_EMITENTE));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(7, 11, 17, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_UG_GESTAO_EMITENTE_OBS));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(18, 11, 28, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_RELACAO_OB));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(29, 11, 39, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_OB));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(40, 8, 47, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.DATA_REFERENCIA_RELACAO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(48, 4, 51, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.BRANCOS));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(52, 2, 53, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_OPERACAO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(54, 1, 54, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.INDICADOR_PAGAMENTO_PESSOAL));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(55, 9, 63, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.ZEROS));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(64, 17, 80, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.VALOR_LIQUIDO_OB));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(81, 3, 83, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_BANCO_FAVORECIDO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(84, 4, 87, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_AGENCIA_FAVORECIDA));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(88, 1, 88, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.DIGITO_VERIFICADOR_AGENCIA_BANCARIA));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(89, 10, 98, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_CONTA_BANCARIA_FAVORECIDO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(99, 45, 143, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.NOME_FAVORECIDO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(144, 65, 208, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.ENDERECO_FAVORECIDO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(209, 28, 236, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.MUNICIPIO_FAVORECIDO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(237, 17, 253, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_GRU));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(254, 8, 261, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CEP_FAVORECIDO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(262, 2, 263, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.UF_FAVORECIDO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(264, 40, 303, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.OBSERVACAO_OB));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(304, 1, 304, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.INDICADOR_TIPO_PAGAMENTO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(305, 1, 305, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.TIPO_FAVORECIDO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(306, 14, 319, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_FAVORECIDO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(320, 5, 324, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.PREFIXO_AGENCIA_DV_DEBITO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(325, 10, 334, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.NUMERO_CONTA_DV_CONVENIO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(335, 3, 337, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.FINALIDADE_PAGAMENTO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(338, 4, 341, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.BRANCOS));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(342, 2, 343, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.CODIGO_RETORNO_OPERACAO));
            registroDoisArquivoRemessas.add(new RegistroArquivoRemessa(344, 7, 350, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_2, TipoRegistroOBN600Tipo2.NUMERO_SEQUENCIAL_ARQUIVO));
            return registroDoisArquivoRemessas;
        } else {
            return lar.getRegistroDoisArquivoRemessas();
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoTrailerObn600(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {
            List<RegistroArquivoRemessa> trailerArquivoRemessas = new ArrayList<>();
            trailerArquivoRemessas.add(new RegistroArquivoRemessa(1, 35, 35, "NUM", lar, TipoRegistroArquivoRemessa.TRAILER, TipoRegistroOBN600Trailer.NOVES));
            trailerArquivoRemessas.add(new RegistroArquivoRemessa(36, 285, 320, "ALFA", lar, TipoRegistroArquivoRemessa.TRAILER, TipoRegistroOBN600Trailer.BRANCOS));
            trailerArquivoRemessas.add(new RegistroArquivoRemessa(321, 17, 337, "NUM", lar, TipoRegistroArquivoRemessa.TRAILER, TipoRegistroOBN600Trailer.SOMATORIO_VALORES_TODAS_OBS_TIPO_2));
            trailerArquivoRemessas.add(new RegistroArquivoRemessa(338, 13, 350, "NUM", lar, TipoRegistroArquivoRemessa.TRAILER, TipoRegistroOBN600Trailer.SOMATORIO_SEQ_TODOS_REG_EXCETO_TRAILER));
            return trailerArquivoRemessas;
        } else {
            return lar.getTrailerArquivoRemessas();
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoRegistroTresObn600(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {
            List<RegistroArquivoRemessa> registroTresArquivoRemessas = new ArrayList<>();
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(1, 1, 1, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.TRES));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(2, 5, 6, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_AGENCIA_BANCARIA_EMITENTE));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(7, 11, 17, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_UG_GESTAO_EMITENTE));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(18, 11, 28, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_RELACAO_OB));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(29, 11, 39, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_OB));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(40, 8, 47, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.DATA_REFERENCIA_RELACAO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(48, 4, 51, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.BRANCOS));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(52, 2, 53, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_OPERACAO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(54, 1, 54, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.TIPO_PAGAMENTO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(55, 6, 60, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.NUMERO_SEQUENCIAL_LISTA));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(61, 3, 63, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.BRANCOS));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(64, 17, 80, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.VALOR_LIQUIDO_OB));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(81, 3, 83, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_BANCO_FAVORECIDO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(84, 4, 87, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_AGENCIA_BANCARIA_FAVORECIDA));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(88, 1, 88, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.DIGITO_VERIFICADOR_AGBANCARIA_FAVOR));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(89, 10, 98, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_CONTA_BANCARIA_FAVORECIDO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(99, 45, 143, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.NOME_FAVORECIDO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(144, 65, 208, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.ENDERECO_FAVORECIDO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(209, 28, 236, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.MUNICIPIO_FAVORECIDO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(237, 17, 253, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_GRU));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(254, 8, 261, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CEP_FAVORECIDO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(262, 2, 263, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.UF_FAVORECIDO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(264, 40, 303, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.OBSERVACAO_OB));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(304, 1, 304, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.ZEROS));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(305, 1, 301, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.TIPO_FAVORECIDO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(306, 14, 319, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_FAVORECIDO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(320, 5, 324, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.PREFIXO_AGENCIA_DV_DEBITO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(325, 10, 334, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.NUMERO_CONTA_DV_CONVENIO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(335, 7, 341, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.BRANCOS));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(342, 2, 343, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.CODIGO_RETORNO_OPERACAO));
            registroTresArquivoRemessas.add(new RegistroArquivoRemessa(344, 7, 350, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_3, TipoRegistroOBN600Tipo3.NUMERO_SEQUENCIAL_ARQUIVO));
            return registroTresArquivoRemessas;
        } else {
            return lar.getRegistroTresArquivoRemessas();
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoRegistroQuatroObn600Fatura(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {
            List<RegistroArquivoRemessa> registroQuatroArquivoRemessas = new ArrayList<>();
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(1, 1, 1, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.QUATRO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(2, 5, 6, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_AGENCIA_BANCARIA_UG_EMITENTE));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(7, 11, 17, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_UG_GESTAO_EMITENTE_OBS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(18, 11, 28, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_RELACAO_OB));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(29, 11, 39, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_OB));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(40, 8, 47, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.DATA_REFERENCIA_RELACAO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(48, 4, 51, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(52, 2, 53, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_OPERACAO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(54, 1, 54, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(55, 6, 60, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.NUMERO_SEQUENCIAL_LISTA));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(61, 3, 63, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(64, 17, 80, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.VALOR_LIQUIDO_OB));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(81, 15, 95, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(96, 1, 96, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.TIPO_FATURA));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(97, 44, 140, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_BARRA));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(141, 8, 148, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.DATA_VENCIMENTO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(149, 17, 165, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.VALOR_NOMINAL));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(166, 17, 182, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.VALOR_DESCONTO_ABATIMENTO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(183, 17, 199, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.VALOR_MORA_JUROS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(200, 64, 263, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(264, 40, 303, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.OBSERVACAO_OB));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(304, 16, 319, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.NUMERO_AUTENTICACAO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(320, 5, 324, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.PREFIXO_AGENCIA_DV_DEBITO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(325, 10, 334, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.NUMERO_CONTA_DV_CONVENIO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(335, 7, 341, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(342, 2, 343, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_RETORNO_OPERACAO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(344, 7, 350, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.NUMERO_SEQUENCIAL_ARQUIVO));
            return registroQuatroArquivoRemessas;
        } else {
            return lar.getRegistroQuatroArquivoRemessas();
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoRegistroQuatroObn600Convenio(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {
            List<RegistroArquivoRemessa> registroQuatroArquivoRemessas = new ArrayList<>();
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(1, 1, 1, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.QUATRO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(2, 5, 6, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_AGENCIA_BANCARIA_UG_EMITENTE));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(7, 11, 17, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_UG_GESTAO_EMITENTE_OBS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(18, 11, 28, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_RELACAO_OB));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(29, 11, 39, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_OB));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(40, 8, 47, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.DATA_REFERENCIA_RELACAO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(48, 4, 51, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(52, 2, 53, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_OPERACAO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(54, 1, 54, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(55, 6, 60, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.NUMERO_SEQUENCIAL_LISTA));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(61, 3, 63, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(64, 17, 80, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.VALOR_LIQUIDO_OB));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(81, 15, 95, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(96, 1, 96, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.TIPO_FATURA));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(97, 44, 140, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_BARRA));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(141, 59, 199, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(200, 64, 263, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(264, 40, 303, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.OBSERVACAO_OB));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(304, 16, 319, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.NUMERO_AUTENTICACAO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(320, 5, 324, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.PREFIXO_AGENCIA_DV_DEBITO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(325, 10, 334, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.NUMERO_CONTA_DV_CONVENIO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(335, 7, 341, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.BRANCOS));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(342, 2, 343, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.CODIGO_RETORNO_OPERACAO));
            registroQuatroArquivoRemessas.add(new RegistroArquivoRemessa(344, 7, 350, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_4, TipoRegistroOBN600Tipo4.NUMERO_SEQUENCIAL_ARQUIVO));
            return registroQuatroArquivoRemessas;
        } else {
            return lar.getRegistroQuatroArquivoRemessas();
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoRegistroCincoObn600GPS(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {
            List<RegistroArquivoRemessa> registroCincoArquivoRemessas = new ArrayList<>();
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(1, 1, 1, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CINCO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(2, 5, 6, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_AGENCIA_UG_EMITENTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(7, 11, 17, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_UG_GESTAO_EMITENTE_OBS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(18, 11, 28, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_RELACAO_OB));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(29, 11, 39, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_OB));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(40, 8, 48, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.DATA_REFERENCIA_RELACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(49, 4, 51, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(52, 2, 53, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_OPERACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(54, 1, 54, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(55, 6, 60, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_SEQUENCIAL_LISTA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(61, 3, 63, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(64, 17, 80, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_PAGAMENTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(81, 8, 88, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.DATA_PAGAMENTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(89, 7, 95, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(96, 1, 96, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.TIPO_PAGAMENTO));
            //-------------------------------- GPS
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(97, 6, 102, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_RECEITA_TRIBUTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(103, 2, 104, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_IDENTIFICACAO_TRIBUTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(105, 6, 110, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.MES_ANO_COMPETENCIA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(111, 17, 127, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_PREVISTO_PAGAMENTO_INSS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(128, 17, 144, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_OUTRAS_ENTIDADES));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(145, 17, 161, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.ATUALIZACAO_MONETARIA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(162, 27, 188, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            //--------------------------------
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(189, 2, 190, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.TIPO_IDENTIFICACAO_CONTRIBUINTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(191, 14, 204, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_IDENTIFICACAO_CONTRIBUINTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(205, 30, 234, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NOME_CONTRIBUINTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(235, 29, 263, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(264, 40, 303, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.OBSERVACAO_OB));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(304, 16, 319, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_AUTENTICACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(320, 5, 324, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.PREFIXO_AGENCIA_DV_DEBITO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(325, 10, 334, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_CONTA_DV_CONVENIO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(335, 7, 341, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(342, 2, 343, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_RETORNO_OPERACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(344, 7, 350, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_SEQUENCIAL_ARQUIVO));
            return registroCincoArquivoRemessas;
        } else {
            return lar.getRegistroCincoArquivoRemessas();
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoRegistroCincoObn600DARF(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {
            List<RegistroArquivoRemessa> registroCincoArquivoRemessas = new ArrayList<>();
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(1, 1, 1, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CINCO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(2, 5, 6, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_AGENCIA_UG_EMITENTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(7, 11, 17, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_UG_GESTAO_EMITENTE_OBS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(18, 11, 28, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_RELACAO_OB));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(29, 11, 39, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_OB));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(40, 8, 48, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.DATA_REFERENCIA_RELACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(49, 4, 51, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(52, 2, 53, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_OPERACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(54, 1, 54, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(55, 6, 60, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_SEQUENCIAL_LISTA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(61, 3, 63, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(64, 17, 80, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_PAGAMENTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(81, 8, 88, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.DATA_PAGAMENTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(89, 7, 95, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(96, 1, 96, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.TIPO_PAGAMENTO));
            //-------------------------------- DARF
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(97, 6, 102, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_RECEITA_TRIBUTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(103, 2, 104, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_IDENTIFICACAO_TRIBUTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(105, 8, 112, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.PERIODO_APURACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(113, 17, 129, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_REFERENCIA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(130, 17, 146, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_PRINCIPAL));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(147, 17, 163, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_MULTA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(164, 17, 180, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_JUROS_ENCARGOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(181, 8, 188, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.DATA_VENCIMENTO));
            //--------------------------------
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(189, 2, 190, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.TIPO_IDENTIFICACAO_CONTRIBUINTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(191, 14, 204, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_IDENTIFICACAO_CONTRIBUINTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(205, 30, 234, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NOME_CONTRIBUINTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(235, 29, 263, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(264, 40, 303, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.OBSERVACAO_OB));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(304, 16, 319, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_AUTENTICACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(320, 5, 324, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.PREFIXO_AGENCIA_DV_DEBITO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(325, 10, 334, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_CONTA_DV_CONVENIO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(335, 7, 341, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(342, 2, 343, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_RETORNO_OPERACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(344, 7, 350, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_SEQUENCIAL_ARQUIVO));
            return registroCincoArquivoRemessas;
        } else {
            return lar.getRegistroCincoArquivoRemessas();
        }
    }

    public List<RegistroArquivoRemessa> gerarPosicaoRegistroCincoObn600DARFSimples(LayoutArquivoRemessa lar) {
        if (objetoNaoEstaSalvo(lar)) {
            List<RegistroArquivoRemessa> registroCincoArquivoRemessas = new ArrayList<>();
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(1, 1, 1, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CINCO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(2, 5, 6, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_AGENCIA_UG_EMITENTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(7, 11, 17, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_UG_GESTAO_EMITENTE_OBS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(18, 11, 28, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_RELACAO_OB));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(29, 11, 39, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_OB));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(40, 8, 48, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.DATA_REFERENCIA_RELACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(49, 4, 51, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(52, 2, 53, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_OPERACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(54, 1, 54, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(55, 6, 60, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_SEQUENCIAL_LISTA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(61, 3, 63, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(64, 17, 80, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_PAGAMENTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(81, 8, 88, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.DATA_PAGAMENTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(89, 7, 95, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(96, 1, 96, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.TIPO_PAGAMENTO));
            //-------------------------------- DARF Simples
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(97, 6, 102, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_RECEITA_TRIBUTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(103, 2, 104, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_IDENTIFICACAO_TRIBUTO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(105, 8, 112, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.PERIODO_APURACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(113, 17, 129, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_RECEITA_BRUTA_ACUMULADA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(130, 7, 136, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.PERCENTUAL_SOBRE_RECEITA_BRUTA_ACUMULADA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(137, 17, 153, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_PRINCIPAL));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(154, 17, 170, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_MULTA));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(171, 17, 187, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.VALOR_JUROS_ENCARGOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(188, 1, 188, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            //--------------------------------
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(189, 2, 190, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.TIPO_IDENTIFICACAO_CONTRIBUINTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(191, 14, 204, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_IDENTIFICACAO_CONTRIBUINTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(205, 30, 234, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NOME_CONTRIBUINTE));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(235, 29, 263, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(264, 40, 303, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.OBSERVACAO_OB));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(304, 16, 319, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_AUTENTICACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(320, 5, 324, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.PREFIXO_AGENCIA_DV_DEBITO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(325, 10, 334, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_CONTA_DV_CONVENIO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(335, 7, 341, "ALFA", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.BRANCOS));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(342, 2, 343, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.CODIGO_RETORNO_OPERACAO));
            registroCincoArquivoRemessas.add(new RegistroArquivoRemessa(344, 7, 350, "NUM", lar, TipoRegistroArquivoRemessa.TIPO_5, TipoRegistroOBN600Tipo5.NUMERO_SEQUENCIAL_ARQUIVO));
            return registroCincoArquivoRemessas;
        } else {
            return lar.getRegistroCincoArquivoRemessas();
        }
    }

    private String removerAcentos(String str) {
        return StringUtil.removeCaracteresEspeciais(str).toUpperCase();
    }

    public List<String> convertInputStreamArquivoParaListString(InputStream inputStream) throws ExcecaoNegocioGenerica {

        try {
            List<String> linhas = new ArrayList<>();
            InputStreamReader in = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(in);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                linhas.add(line);
            }
            return linhas;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao converter em linhas o arquivo selecionado");
        }

    }

    public EntityManager getEntityManager() {
        return entityManager;
    }


    private EnderecoCorreio enderecoPessoa(Pessoa p) {
        if (p.getEnderecoPrincipal() != null) {
            return p.getEnderecoPrincipal();
        }
        p.setEnderecoscorreio(jdbcEnderecoPessoa.buscarEnderecoPorPessoa(p.getId()));

        String caminho = "/pessoa-fisica/editar/" + p.getId() + "/";
        if (p.isPessoaJuridica()) {
            caminho = "/pessoa-juridica/editar/" + p.getId() + "/";
        }
        String url = "<b><a href='" + caminho + "' target='_blank'>Link</a></b>";
        if (p.getEnderecoscorreio() == null || p.getEnderecoscorreio().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado endereço da pessoa " + p.getNomeCpfCnpj() + ". " +
                "Acesse o cadastro da pessoa e altere os dados de endereço. " + url + "");

        }
        if (p.getEnderecoscorreio().size() >= 1) {
            return p.getEnderecoscorreio().get(0);
        }
        return null;
//        throw new ExcecaoNegocioGenerica("Erro ao recuperar para a Pessoa " + p.toString() + ". Não existe endereço configurada para o Fornecedor. ");
    }

}
