/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItensBordero;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.*;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.Util;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fabio
 */
@Stateless
public class BorderoFacade extends AbstractFacade<Bordero> implements Serializable {

    @EJB
    SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private TransferenciaContaFinanceiraFacade transferenciaContaFinanceiraFacade;
    @EJB
    private TransferenciaMesmaUnidadeFacade transferenciaMesmaUnidadeFacade;
    @EJB
    private LiberacaoCotaFinanceiraFacade liberacaoCotaFinanceiraFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private SistemaFacade sistemaFacade;
    private JdbcBorderoPagamento jdbcBorderoPagamento;
    private JdbcBorderoPagamentoExtra jdbcBorderoPagamentoExtra;
    private JdbcBorderoTransferenciaFinanceira jdbcBorderoTransferenciaFinanceira;
    private JdbcBorderoTransferenciaMesmaUnidade jdbcBorderoTransferenciaMesmaUnidade;
    private JdbcBorderoLiberacaoFinanceira jdbcBorderoLiberacaoFinanceira;
    private JdbcBordero jdbcBordero;
    private JdbcPagamento jdbcPagamento;
    private JdbcPagamentoExtra jdbcPagamentoExtra;
    private JdbcTransferenciaFinanceira jdbcTransferenciaFinanceira;
    private JdbcTransferenciaMesmaUnidade jdbcTransferenciaMesmaUnidade;
    private JdbcLiberacaoFinanceira jdbcLiberacaoFinanceira;

    private JdbcRevisaoAuditoria jdbcRevisaoAuditoria;

    @PostConstruct
    private void init() {
        jdbcBorderoPagamento = (JdbcBorderoPagamento) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcBorderoPagamento");
        jdbcBorderoPagamentoExtra = (JdbcBorderoPagamentoExtra) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcBorderoPagamentoExtra");
        jdbcBorderoTransferenciaFinanceira = (JdbcBorderoTransferenciaFinanceira) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcBorderoTransferenciaFinanceira");
        jdbcBorderoTransferenciaMesmaUnidade = (JdbcBorderoTransferenciaMesmaUnidade) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcBorderoTransferenciaMesmaUnidade");
        jdbcBorderoLiberacaoFinanceira = (JdbcBorderoLiberacaoFinanceira) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcBorderoLiberacaoFinanceira");
        jdbcBordero = (JdbcBordero) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcBordero");
        jdbcPagamento = (JdbcPagamento) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcPagamento");
        jdbcPagamentoExtra = (JdbcPagamentoExtra) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcPagamentoExtra");
        jdbcTransferenciaFinanceira = (JdbcTransferenciaFinanceira) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcTransferenciaFinanceira");
        jdbcTransferenciaMesmaUnidade = (JdbcTransferenciaMesmaUnidade) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcTransferenciaMesmaUnidade");
        jdbcLiberacaoFinanceira = (JdbcLiberacaoFinanceira) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcLiberacaoFinanceira");
        jdbcRevisaoAuditoria = (JdbcRevisaoAuditoria) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcRevisaoAuditoria");
    }

    public BorderoFacade() {
        super(Bordero.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public void setUnidadeOrganizacionalFacade(UnidadeOrganizacionalFacade unidadeOrganizacionalFacade) {
        this.unidadeOrganizacionalFacade = unidadeOrganizacionalFacade;
    }

    public HierarquiaOrganizacionalFacadeOLD getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return recuperar(id);
    }

    @Override
    public Bordero recuperar(Object id) {
        Bordero b = em.find(Bordero.class, id);
        b.getListaPagamentos().size();
        b.getListaPagamentosExtra().size();
        b.getListaTransferenciaFinanceira().size();
        b.getListaTransferenciaMesmaUnidade().size();
        b.getListaLiberacaoCotaFinanceira().size();
        return b;
    }

    public void geraArquivos(List<FileUploadEvent> files) throws Exception {
        if (files != null && !files.isEmpty()) {
            for (FileUploadEvent event : files) {
                ArquivoRetornoBancario arquivoRetornoBancario = new ArquivoRetornoBancario();
                UploadedFile arquivoRecebido = event.getFile();
                Arquivo arq = new Arquivo();
                arq.setNome(arquivoRecebido.getFileName());
                arq.setMimeType(arquivoFacade.getMimeType(arquivoRecebido.getFileName()));
                arq.setDescricao("Arquivo de Retorno Bancário Contábil");
                arq.setTamanho(arquivoRecebido.getSize());
                arquivoRetornoBancario.setArquivo(arq);
                arquivoRetornoBancario.setSituacaoArquivo(SituacaoArquivo.NAO_PROCESSADO);
                arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
                arquivoRecebido.getInputstream().close();
                byte[] buffer = arquivoRecebido.getContents();
                String arqString = new String(buffer).toString();
                em.persist(arquivoRetornoBancario);
            }
        } else {
            throw new ExcecaoNegocioGenerica("Nenhum arquivo selecionado para importar. Selecione um arquivo e clique no botão 'Confirmar Importação.'");
        }
    }

    public void retornarValorFinalBordero(Bordero selecionado) {
        BigDecimal totalPagamentos = BigDecimal.ZERO;
        BigDecimal totalDespesaExtra = BigDecimal.ZERO;
        BigDecimal totalTransferencia = BigDecimal.ZERO;
        BigDecimal totalTransfereciaMesmaUnid = BigDecimal.ZERO;
        BigDecimal totalLiberacao = BigDecimal.ZERO;

        selecionado.setValor(BigDecimal.ZERO);

        if (!selecionado.getListaPagamentos().isEmpty()) {
            totalPagamentos = retornaValorTotalPagamento(selecionado);
        }
        if (!selecionado.getListaPagamentosExtra().isEmpty()) {
            totalDespesaExtra = retornaValorTotalPagamentoExtra(selecionado);
        }
        if (!selecionado.getListaTransferenciaFinanceira().isEmpty()) {
            totalTransferencia = retornaValorTotalTransferenciaFinanceira(selecionado);
        }
        if (!selecionado.getListaTransferenciaMesmaUnidade().isEmpty()) {
            totalTransfereciaMesmaUnid = retornaValorTotalTransferenciaMesmaUnidade(selecionado);
        }
        if (!selecionado.getListaLiberacaoCotaFinanceira().isEmpty()) {
            totalLiberacao = retornaValorTotalLiberacaoCotaFinanceira(selecionado);
        }
        selecionado.setValor(totalPagamentos.add(totalDespesaExtra.add(totalTransferencia.add(totalTransfereciaMesmaUnid.add(totalLiberacao)))));
    }


    public BigDecimal retornaValorTotalPagamentoExtra(Bordero selecionado) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!selecionado.getListaPagamentosExtra().isEmpty() || selecionado.getListaPagamentosExtra() != null) {
            for (BorderoPagamentoExtra pagExtra : selecionado.getListaPagamentosExtra()) {
                soma = soma.add(pagExtra.getPagamentoExtra().getValor());
            }
        }
        return soma;
    }


    public BigDecimal retornaValorTotalPagamento(Bordero selecionado) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!selecionado.getListaPagamentos().isEmpty() || selecionado.getListaPagamentos() != null) {
            for (BorderoPagamento pag : selecionado.getListaPagamentos()) {
                soma = soma.add(pag.getPagamento().getValorFinal());
            }
        }
        return soma;
    }

    public BigDecimal retornaValorTotalTransferenciaFinanceira(Bordero selecionado) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!selecionado.getListaTransferenciaFinanceira().isEmpty() || selecionado.getListaTransferenciaFinanceira() != null) {
            for (BorderoTransferenciaFinanceira tra : selecionado.getListaTransferenciaFinanceira()) {
                soma = soma.add(tra.getTransferenciaContaFinanceira().getValor());
            }
        }
        return soma;
    }

    public BigDecimal retornaValorTotalTransferenciaMesmaUnidade(Bordero selecionado) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!selecionado.getListaTransferenciaMesmaUnidade().isEmpty() || selecionado.getListaTransferenciaMesmaUnidade() != null) {
            for (BorderoTransferenciaMesmaUnidade tra : selecionado.getListaTransferenciaMesmaUnidade()) {
                soma = soma.add(tra.getTransferenciaMesmaUnidade().getValor());
            }
        }
        return soma;
    }

    public BigDecimal retornaValorTotalLiberacaoCotaFinanceira(Bordero selecionado) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!selecionado.getListaLiberacaoCotaFinanceira().isEmpty() || selecionado.getListaLiberacaoCotaFinanceira() != null) {
            for (BorderoLiberacaoFinanceira lcf : selecionado.getListaLiberacaoCotaFinanceira()) {
                soma = soma.add(lcf.getLiberacaoCotaFinanceira().getValor());
            }
        }
        return soma;
    }


    public TransferenciaContaFinanceiraFacade getTransferenciaContaFinanceiraFacade() {
        return transferenciaContaFinanceiraFacade;
    }

    public TransferenciaMesmaUnidadeFacade getTransferenciaMesmaUnidadeFacade() {
        return transferenciaMesmaUnidadeFacade;
    }

    public LiberacaoCotaFinanceiraFacade getLiberacaoCotaFinanceiraFacade() {
        return liberacaoCotaFinanceiraFacade;
    }

    public String ultimoCodigoMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(b.sequenciaarquivo), 0) + 1 as sequenciaarquivo from bordero b");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.toString();
    }

    public List<Bordero> listaBorderosPorBancoStatus(SituacaoBordero situacaoBordero, Date dtInicio, Date dtFim, SubConta inicial, SubConta fim, String unidades, Banco banco) {
        String sql = " SELECT distinct B.*  FROM BORDERO B "
            + " INNER JOIN UNIDADEORGANIZACIONAL UO ON B.UNIDADEORGANIZACIONAL_ID = UO.ID "
            + " INNER JOIN hierarquiaorganizacional HO ON HO.SUBORDINADA_ID = UO.ID "
            + " LEFT JOIN BORDEROPAGAMENTO BP ON B.ID = BP.BORDERO_ID "
            + " LEFT JOIN PAGAMENTO P ON BP.PAGAMENTO_ID = P.ID "
            + " LEFT JOIN BORDEROPAGAMENTOEXTRA BPE ON B.ID = BPE.BORDERO_ID "
            + " LEFT JOIN PAGAMENTOEXTRA PE ON BPE.PAGAMENTOEXTRA_ID = PE.ID "
            + " LEFT JOIN BORDEROTRANSFFINANCEIRA BTF ON B.ID = BTF.BORDERO_ID "
            + " LEFT JOIN transferenciacontafinanc TF ON btf.transffinanceira_id = TF.id "
            + " LEFT JOIN BORDEROTRANSFMESMAUNIDADE BTMU ON B.ID = BTMU.BORDERO_ID "
            + " LEFT JOIN transferenciamesmaunidade TMU ON btmu.transfmesmaunidade_id = TMU.id "
            + " LEFT JOIN BORDEROLIBCOTAFINANCEIRA BL ON BL.BORDERO_ID = B.ID "
            + " LEFT JOIN LIBERACAOCOTAFINANCEIRA LIB ON LIB.ID = BL.LIBCOTAFINANCEIRA_ID "
            + " LEFT JOIN SOLICITACAOCOTAFINANCEIRA SOL ON SOL.ID = LIB.SOLICITACAOCOTAFINANCEIRA_ID"
            + " INNER JOIN SUBCONTA SC ON (PE.SUBCONTA_ID = SC.ID "
            + " OR P.SUBCONTA_ID = SC.ID "
            + " OR (tf.subcontadeposito_id = SC.id or tf.subcontaretirada_id = SC.id) "
            + " OR (tmu.subcontadeposito_id = SC.ID OR tmu.subcontaretirada_id = SC.ID)"
            + " OR (SOL.CONTAFINANCEIRA_ID = SC.ID OR LIB.SUBCONTA_ID = SC.ID))"
            + " INNER JOIN CONTABANCARIAENTIDADE CBE ON SC.CONTABANCARIAENTIDADE_ID = CBE.ID "
            + " INNER JOIN AGENCIA AG ON CBE.AGENCIA_ID = AG.ID " +
            " INNER JOIN BANCO BC2 ON B.BANCO_ID = BC2.ID "
            + " WHERE B.SITUACAO = :situacao and trunc(B.DATAGERACAO) BETWEEN :DTINICIO AND :DTFIM " + unidades + " AND BC2.NUMEROBANCO = :numeroBanco ";
        if (inicial != null && fim != null) {
            sql += " AND SC.CODIGO BETWEEN '" + inicial.getCodigo() + "' and '" + fim.getCodigo() + "'";
        }
        sql += " order by b.sequenciaArquivo";
        Query q = em.createNativeQuery(sql, Bordero.class);
        q.setParameter("DTINICIO", new SimpleDateFormat("dd/MM/yyyy").format(dtInicio));
        q.setParameter("DTFIM", new SimpleDateFormat("dd/MM/yyyy").format(dtFim));
        q.setParameter("situacao", situacaoBordero.name());
        q.setParameter("numeroBanco", banco.getNumeroBanco());
        List<Bordero> borderos = q.getResultList();
        for (Bordero bordero : borderos) {
            bordero.getListaPagamentos().size();
            bordero.getListaPagamentosExtra().size();
            bordero.getListaTransferenciaFinanceira().size();
            bordero.getListaTransferenciaMesmaUnidade().size();
            bordero.getListaLiberacaoCotaFinanceira().size();
        }
        return borderos;
    }

    public List<Bordero> listaPorStatusUnidade(SituacaoBordero situacaoBordero, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "select b.* from bordero b where b.situacao = :situacao and b.unidadeorganizacional_id = :uo";
        Query q = em.createNativeQuery(sql, Bordero.class);
        q.setParameter("situacao", situacaoBordero.name());
        q.setParameter("uo", unidadeOrganizacional.getId());
        return q.getResultList();
    }

    public List<Bordero> buscarOrdemBancariaDeferidaOrIndeferida(String parte, Exercicio e) {
        String sql = "select distinct b.* from bordero b  "
            + " left join borderopagamento bp on bp.bordero_id = b.id "
            + " left join borderopagamentoextra bpx on bpx.bordero_id = b.id "
            + " left join borderotransffinanceira btf on btf.bordero_id = b.id "
            + " left join borderotransfmesmaunidade btfm on btfm.bordero_id = b.id "
            + " left join borderolibcotafinanceira blf on blf.bordero_id = b.id "
            + " where ((bp.situacaoitembordero = 'BORDERO' or bp.situacaoitembordero = 'DEFERIDO' or bp.situacaoitembordero = 'INDEFIRIDO') "
            + "    or (bpx.situacaoitembordero = 'BORDERO' or bpx.situacaoitembordero = 'DEFERIDO' or bpx.situacaoitembordero = 'INDEFIRIDO') "
            + "    or (btf.situacaoitembordero = 'BORDERO'  or btf.situacaoitembordero = 'DEFERIDO' or btf.situacaoitembordero = 'INDEFIRIDO') "
            + "    or (btfm.situacaoitembordero = 'BORDERO' or btfm.situacaoitembordero = 'DEFERIDO' or btfm.situacaoitembordero = 'INDEFIRIDO') "
            + "    or (blf.situacaoitembordero = 'BORDERO' or blf.situacaoitembordero = 'DEFERIDO' or blf.situacaoitembordero = 'INDEFIRIDO')) "
            + "    and ((b.sequenciaarquivo like :parte) "
            + "      or (replace(replace(b.datageracao, '/', ''), '/', '') like :data) "
            + "      or (replace(b.valor, '.','') like :data)) "
            + " and b.exercicio_id = :idExercicio "
            + " and b.valor > 0 "
            + " order by b.sequenciaarquivo desc ";

        Query q = em.createNativeQuery(sql, Bordero.class);
        q.setParameter("idExercicio", e.getId());
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("data", "%" + parte.trim() + "%");
        if (q.getResultList().isEmpty()) {
            return new ArrayList<Bordero>();
        } else {
            return (ArrayList<Bordero>) q.getResultList();
        }
    }

    public Object recuperaLacamentoDoBorderoNumeroString(TipoLancamentoEmBordero tipoLancamentoEmBordero, Exercicio ex, String bordero, String movimento) {
        try {
            String sql;
            if (tipoLancamentoEmBordero == null) {
                throw new ExcecaoNegocioGenerica("O Tipo de Lançamento esta vazio");
            } else if (tipoLancamentoEmBordero.equals(TipoLancamentoEmBordero.PAGAMENTO)) {
                sql = "select obj.* from bordero b inner join borderopagamento lista on b.id=lista.bordero_id inner join pagamento obj on lista.pagamento_id=obj.id where obj.exercicio_id  =:ex and obj.numeropagamento=:pg and b.sequenciaarquivo=:bd";
            } else if (tipoLancamentoEmBordero.equals(TipoLancamentoEmBordero.PAGAMENTO_EXTRA)) {
                sql = "select obj.* from bordero b inner join borderopagamentoextra lista on b.id=lista.bordero_id inner join pagamentoextra obj on lista.pagamentoextra_id=obj.id where obj.exercicio_id  =:ex and obj.numero=:pg and b.sequenciaarquivo=:bd";
            } else if (tipoLancamentoEmBordero.equals(TipoLancamentoEmBordero.TRANSFERENCIA_FIANCEIRA)) {
                sql = "select obj.* from bordero b inner join borderotransffinanceira lista on b.id=lista.bordero_id inner join transferenciacontafinanc obj on lista.transffinanceira_id=obj.id where obj.exercicio_id  =:ex and obj.numero=:pg and b.sequenciaarquivo=:bd";
            } else if (tipoLancamentoEmBordero.equals(TipoLancamentoEmBordero.TRANSFERENCIA_MESMA_UNIDADE)) {
                sql = "select obj.* from bordero b inner join borderotransfmesmaunidade lista on b.id=lista.bordero_id inner join transferenciamesmaunidade obj on lista.transfmesmaunidade_id=obj.id where obj.exercicio_id  =ex and obj.numero=pg and b.sequenciaarquivo=bd";
            } else {
                throw new ExcecaoNegocioGenerica("O Tipo de Lançamento em Ordem Bancária não foi Identificado.");
            }

            Query q = em.createNativeQuery(sql, tipoLancamentoEmBordero.getClasse());
            q.setParameter("ex", ex.getId());
            q.setParameter("pg", movimento);
            q.setParameter("bd", Integer.parseInt(bordero));


            return q.getSingleResult();
        } catch (NoResultException no) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado " + tipoLancamentoEmBordero.getDescricao() + " com o numero " + movimento + " no Bordero " + bordero + " referente o Exercicio " + ex.getAno());
        } catch (NonUniqueResultException nu) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de um " + tipoLancamentoEmBordero.getDescricao() + " com o numero " + movimento + " no Bordero " + bordero + " referente o Exercicio " + ex.getAno());
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao Recuperar " + tipoLancamentoEmBordero.getDescricao() + " com o numero " + movimento + " no Bordero " + bordero + " referente o Exercicio " + ex.getAno() + "! " + e.getMessage());
        }
    }

    public PagamentoExtraFacade getPagamentoExtraFacade() {
        return pagamentoExtraFacade;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public void atualizarSituacaoItemBorderoPagamento(Pagamento pagamento, SituacaoItemBordero situacaoItemBordero) {

        Query q = em.createNativeQuery(" update borderopagamento " +
            " set situacaoitembordero = :situacao " +
            " where bordero_id = (select id from bordero where sequenciaarquivo = :numero " +
            "                                              and exercicio_id = :idExercicio) " +
            " and pagamento_id = :idPagamento ");
        q.setParameter("numero", pagamento.getNumDocumento());
        q.setParameter("idPagamento", pagamento.getId());
        q.setParameter("idExercicio", pagamento.getExercicio().getId());
        q.setParameter("situacao", situacaoItemBordero.name());
        q.executeUpdate();
    }

    public void atualizarSituacaoItemBorderoPagamentoExtra(PagamentoExtra pagamentoExtra, SituacaoItemBordero situacaoItemBordero) {

        Query q = em.createNativeQuery(" update borderopagamentoextra " +
            " set situacaoitembordero = :situacao " +
            " where bordero_id = (select id from bordero where sequenciaarquivo = :numero " +
            "                                              and exercicio_id = :idExercicio) " +
            " and pagamentoExtra_id = :idPagamento");
        q.setParameter("numero", pagamentoExtra.getNumeroPagamento());
        q.setParameter("idPagamento", pagamentoExtra.getId());
        q.setParameter("idExercicio", pagamentoExtra.getExercicio().getId());
        q.setParameter("situacao", situacaoItemBordero.name());
        q.executeUpdate();
    }

    public void atualizaSituacaoBorderoItemTransferenciaFinanceira(String numeroBordero, Long id, Long idExercicio, SituacaoItemBordero situacaoItemBordero) {

        Query q = em.createNativeQuery("update BORDEROTRANSFFINANCEIRA " +
            " set situacaoitembordero = :situacao" +
            " where bordero_id = (select id from bordero where sequenciaarquivo = :numero" +
            "                                            and situacao = '" + SituacaoBordero.AGUARDANDO_BAIXA.name() + "'" +
            "                                            and exercicio_id = :idExercicio) " +
            " and TRANSFFINANCEIRA_ID = :id");
        q.setParameter("numero", numeroBordero);
        q.setParameter("id", id);
        q.setParameter("idExercicio", idExercicio);
        q.setParameter("situacao", situacaoItemBordero.name());
        q.executeUpdate();
    }

    public void atualizaSituacaoBorderoItemTransferenciaMesmaUnidade(String numeroBordero, Long id, Long idExercicio, SituacaoItemBordero situacaoItemBordero) {

        Query q = em.createNativeQuery("update BORDEROTRANSFMESMAUNIDADE " +
            " set situacaoitembordero = :situacao" +
            " where bordero_id = (select id from bordero where sequenciaarquivo = :numero" +
            "                                            and situacao = '" + SituacaoBordero.AGUARDANDO_BAIXA.name() + "'" +
            "                                            and exercicio_id = :idExercicio) " +
            " and TRANSFMESMAUNIDADE_ID = :id");
        q.setParameter("numero", numeroBordero);
        q.setParameter("id", id);
        q.setParameter("idExercicio", idExercicio);
        q.setParameter("situacao", situacaoItemBordero.name());
        q.executeUpdate();
    }

    public void atualizaSituacaoBorderoItemLiberacao(String numeroBordero, Long id, Long idExercicio, SituacaoItemBordero situacaoItemBordero) {

        Query q = em.createNativeQuery("update BORDEROLIBCOTAFINANCEIRA " +
            " set situacaoitembordero = :situacao" +
            " where bordero_id = (select id from bordero where sequenciaarquivo = :numero " +
            "                                            and situacao = '" + SituacaoBordero.AGUARDANDO_BAIXA.name() + "'" +
            "                                            and exercicio_id = :idExercicio) " +
            " and LIBCOTAFINANCEIRA_ID = :id");
        q.setParameter("numero", numeroBordero);
        q.setParameter("id", id);
        q.setParameter("idExercicio", idExercicio);
        q.setParameter("situacao", situacaoItemBordero.name());
        q.executeUpdate();
    }

    public void atualizarSituacaoBordero(String numeroBordero, Long idExercicio, SituacaoBordero situacaoBordero, Date dataDebito) {

        String sql = " UPDATE BORDERO  " +
            " SET SITUACAO = :situacao, dataDebito = :dataDebito " +
            " WHERE SEQUENCIAARQUIVO = :numero " +
            " AND EXERCICIO_ID = :idExercicio ";
        if (dataDebito == null) {
            sql = " UPDATE BORDERO  " +
                " SET SITUACAO = :situacao " +
                " WHERE SEQUENCIAARQUIVO = :numero " +
                " AND EXERCICIO_ID = :idExercicio ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("numero", numeroBordero);
        q.setParameter("idExercicio", idExercicio);
        q.setParameter("situacao", situacaoBordero.name());
        if (dataDebito != null) {
            q.setParameter("dataDebito", dataDebito);
        }
        q.executeUpdate();
    }

    public void atualizarSituacaoBorderoIndeferido(String numeroBordero, Long idExercicio) {
        String sqlConsultaItensBordero = " " +
            " select distinct pb.SITUACAOITEMBORDERO||pbx.SITUACAOITEMBORDERO||trx.SITUACAOITEMBORDERO||tmx.SITUACAOITEMBORDERO|| lbx.SITUACAOITEMBORDERO " +
            " from BORDERO b " +
            " inner join exercicio ex on b.EXERCICIO_ID = ex.ID " +
            " left join borderopagamento pb on  pb.BORDERO_ID = b.id " +
            " left join borderopagamentoextra pbx on  pbx.BORDERO_ID = b.id " +
            " left join BORDEROTRANSFFINANCEIRA trx on  trx.BORDERO_ID = b.id " +
            " left join BORDEROTRANSFMESMAUNIDADE tmx on  tmx.BORDERO_ID = b.id " +
            " left join BORDEROLIBCOTAFINANCEIRA lbx on  lbx.BORDERO_ID = b.id " +
            " where ex.id = :idExercicio and b.SEQUENCIAARQUIVO = :numeroBordero ";
        Query consulta = em.createNativeQuery(sqlConsultaItensBordero);
        consulta.setParameter("numeroBordero", numeroBordero);
        consulta.setParameter("idExercicio", idExercicio);
        try {
            String retorno = (String) consulta.getResultList().get(0);

            if (retorno.contains(SituacaoItemBordero.INDEFIRIDO.name()) && retorno.contains(SituacaoItemBordero.PAGO.name())) {
                atualizarSituacaoBordero(numeroBordero, idExercicio, SituacaoBordero.PAGO_COM_RESTRICOES, null);
            } else if (retorno.contains(SituacaoItemBordero.INDEFIRIDO.name())) {
                atualizarSituacaoBordero(numeroBordero, idExercicio, SituacaoBordero.INDEFERIDO, null);
            } else if (retorno.contains(SituacaoItemBordero.DEFERIDO.name())) {
                atualizarSituacaoBordero(numeroBordero, idExercicio, SituacaoBordero.DEFERIDO, null);
            } else if (retorno.contains(SituacaoItemBordero.PAGO.name())) {
                atualizarSituacaoBordero(numeroBordero, idExercicio, SituacaoBordero.PAGO, null);
            } else {
                atualizarSituacaoBordero(numeroBordero, idExercicio, SituacaoBordero.INDEFERIDO, null);
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar situação bordero indeferido", e);
        }
    }

    private void validarBloqueio(Bordero bordero) {
        ValidacaoException ve = new ValidacaoException();
        if (!singletonConcorrenciaContabil.isDisponivel(bordero)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Ordem Bancária" + bordero + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente a Ordem Bancária.");
        }
        ve.lancarException();
    }

    public Bordero salvarNovaOB(Bordero entity) {
        entity.setSequenciaArquivo(singletonGeradorCodigoContabil.getNumeroOrdemBancaria(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataGeracao()));
        entity.setDataGeracao(sistemaFacade.getDataOperacao());
        jdbcBordero.salvarNovoOB(entity, sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
        return entity;
    }

    public void desbloquear(Bordero entity) {
        singletonConcorrenciaContabil.desbloquear(entity);
    }

    public void deferirOrdemBancaria(Bordero entity, ItensBordero itensBordero) {
        RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
        validarBloqueio(entity);
        singletonConcorrenciaContabil.bloquear(entity);
        entity.setSituacao(SituacaoBordero.DEFERIDO);
        movimentarItensOrdemBancaria(entity, StatusPagamento.EFETUADO, SituacaoItemBordero.DEFERIDO, rev.getId());
        movimentarItemRemovidoDaOrdemBancaria(itensBordero, rev.getId());
        definirNumeroOrdemBancariaNoMovimento(entity, null);
        validarNumeroRE(entity);
        jdbcBordero.atualizarBordero(entity, rev.getId());
        desbloquear(entity);
    }

    public void indeferirOrdemBancaria(Bordero ordemBancaria) {
        ordemBancaria.setSituacao(SituacaoBordero.INDEFERIDO);
        RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
        for (BorderoPagamento borderoPagamento : ordemBancaria.getListaPagamentos()) {
            indeferirItemPagamentoDaOB(borderoPagamento, rev.getId());
        }
        for (BorderoPagamentoExtra borderoPagamentoExtra : ordemBancaria.getListaPagamentosExtra()) {
            indeferirItemPagamentoExtraDaOB(borderoPagamentoExtra, rev.getId());
        }

        for (BorderoTransferenciaFinanceira borderoTransferenciaFinanceira : ordemBancaria.getListaTransferenciaFinanceira()) {
            indeferirItemTransferenciaFinanceiraDaOB(borderoTransferenciaFinanceira, rev.getId());
        }

        for (BorderoTransferenciaMesmaUnidade borderoTransferenciaMesmaUnidade : ordemBancaria.getListaTransferenciaMesmaUnidade()) {
            indeferirItemTransferenciaFinanceiraMesmaUnidadeDaOB(borderoTransferenciaMesmaUnidade, rev.getId());
        }

        for (BorderoLiberacaoFinanceira borderoLiberacaoFinanceira : ordemBancaria.getListaLiberacaoCotaFinanceira()) {
            indeferirItemLiberacaoFinanceiraDaOB(borderoLiberacaoFinanceira, rev.getId());
        }
        jdbcBordero.atualizarSituacao(ordemBancaria, sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente(), rev.getId());
    }

    public void indeferirItemTransferenciaFinanceiraDaOB(BorderoTransferenciaFinanceira borderoTransferenciaFinanceira, Long idRev) {
        TransferenciaContaFinanceira transferenciaContaFinanceira = borderoTransferenciaFinanceira.getTransferenciaContaFinanceira();
        if (!StatusPagamento.PAGO.equals(transferenciaContaFinanceira.getStatusPagamento())
            && transferenciaContaFinanceira.getSaldo().compareTo(BigDecimal.ZERO) != 0) {

            transferenciaContaFinanceira.setStatusPagamento(StatusPagamento.INDEFERIDO);
            transferenciaContaFinanceira.setDataConciliacao(null);
            transferenciaContaFinanceira.setRecebida(null);
            jdbcTransferenciaFinanceira.indeferir(transferenciaContaFinanceira, idRev);

            borderoTransferenciaFinanceira.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
            jdbcBorderoTransferenciaFinanceira.atualizarSituacao(borderoTransferenciaFinanceira, idRev);
        }
    }


    public void indeferirItemTransferenciaFinanceiraMesmaUnidadeDaOB(BorderoTransferenciaMesmaUnidade borderoTransferenciaMesmaUnidade, Long idRev) {
        TransferenciaMesmaUnidade transferenciaMesmaUnidade = borderoTransferenciaMesmaUnidade.getTransferenciaMesmaUnidade();
        if (!StatusPagamento.PAGO.equals(transferenciaMesmaUnidade.getStatusPagamento())
            && transferenciaMesmaUnidade.getSaldo().compareTo(BigDecimal.ZERO) != 0) {

            transferenciaMesmaUnidade.setStatusPagamento(StatusPagamento.INDEFERIDO);
            transferenciaMesmaUnidade.setDataConciliacao(null);
            transferenciaMesmaUnidade.setRecebida(null);
            jdbcTransferenciaMesmaUnidade.indeferir(transferenciaMesmaUnidade, idRev);

            borderoTransferenciaMesmaUnidade.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
            jdbcBorderoTransferenciaMesmaUnidade.atualizarSituacao(borderoTransferenciaMesmaUnidade, idRev);
        }
    }

    public void indeferirItemLiberacaoFinanceiraDaOB(BorderoLiberacaoFinanceira borderoLiberacaoFinanceira, Long idRev) {
        LiberacaoCotaFinanceira liberacaoCotaFinanceira = borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira();
        if (!StatusPagamento.PAGO.equals(liberacaoCotaFinanceira.getStatusPagamento())
            && liberacaoCotaFinanceira.getSaldo().compareTo(BigDecimal.ZERO) != 0) {

            liberacaoCotaFinanceira.setStatusPagamento(StatusPagamento.INDEFERIDO);
            liberacaoCotaFinanceira.setDataConciliacao(null);
            liberacaoCotaFinanceira.setRecebida(null);
            jdbcLiberacaoFinanceira.indefir(liberacaoCotaFinanceira, idRev);

            borderoLiberacaoFinanceira.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
            jdbcBorderoLiberacaoFinanceira.atualizarSituacao(borderoLiberacaoFinanceira, idRev);
        }
    }

    public void indeferirItemPagamentoExtraDaOB(BorderoPagamentoExtra borderoPagamentoExtra, Long idRev) {
        if (Util.isNull(idRev)) {
            idRev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente()).getId();
        }
        PagamentoExtra pagamentoExtra = borderoPagamentoExtra.getPagamentoExtra();
        if (!StatusPagamento.PAGO.equals(pagamentoExtra.getStatus())
            && pagamentoExtra.getSaldo().compareTo(BigDecimal.ZERO) != 0) {

            pagamentoExtra.setStatus(StatusPagamento.INDEFERIDO);
            pagamentoExtra.setDataConciliacao(null);
            jdbcPagamentoExtra.indefeir(pagamentoExtra, idRev);

            borderoPagamentoExtra.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
            jdbcBorderoPagamentoExtra.atualizarSituacao(borderoPagamentoExtra, idRev);
        }
    }

    public void indeferirItemPagamentoDaOB(BorderoPagamento borderoPagamento, Long idRev) {
        if (Util.isNull(idRev)) {
            idRev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente()).getId();
        }
        Pagamento pagamento = borderoPagamento.getPagamento();
        if (!StatusPagamento.PAGO.equals(pagamento.getStatus())
            && pagamento.getSaldo().compareTo(BigDecimal.ZERO) != 0) {

            pagamento.setStatus(StatusPagamento.INDEFERIDO);
            jdbcPagamento.atualizarStatus(pagamento, idRev);

            borderoPagamento.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
            jdbcBorderoPagamento.atualizarSituacao(borderoPagamento, idRev);
        }
    }

    public void salvar(Bordero entity, ItensBordero itensBordero) {
        validarBloqueio(entity);
        RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
        singletonConcorrenciaContabil.bloquear(entity);
        if (SituacaoBordero.ABERTO.equals(entity.getSituacao())) {
            movimentarItensOrdemBancaria(entity, StatusPagamento.BORDERO, SituacaoItemBordero.BORDERO, rev.getId());
            retornarValorFinalBordero(entity);
            movimentarItemRemovidoDaOrdemBancaria(itensBordero, rev.getId());
        }
        if (SituacaoBordero.DEFERIDO.equals(entity.getSituacao())) {
            validarNumeroRE(entity);
        }
        jdbcBordero.atualizarBordero(entity, rev.getId());
        desbloquear(entity);
    }

    public void movimentarItemRemovidoDaOrdemBancaria(ItensBordero itensBordero, Long idRev) {
        if (itensBordero != null) {
            if (!itensBordero.getListaPagamentosExclusao().isEmpty()) {
                for (BorderoPagamento borderoPagamento : itensBordero.getListaPagamentosExclusao()) {
                    Pagamento pagamento = borderoPagamento.getPagamento();
                    pagamento.setStatus(StatusPagamento.INDEFERIDO);
                    pagamento.setNumDocumento(null);
                    pagamento.setNumeroRE(null);
                    borderoPagamento.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
                    jdbcBorderoPagamento.atualizarSituacao(borderoPagamento, idRev);
                    jdbcPagamento.atualizarPagamento(StatusPagamento.INDEFERIDO, pagamento, idRev);
                }
            }
            if (!itensBordero.getListaPagamentoExtraExclusao().isEmpty()) {
                for (BorderoPagamentoExtra borderoPagamentoExtra : itensBordero.getListaPagamentoExtraExclusao()) {
                    PagamentoExtra pagamentoExtra = borderoPagamentoExtra.getPagamentoExtra();
                    pagamentoExtra.setStatus(StatusPagamento.INDEFERIDO);
                    pagamentoExtra.setNumeroPagamento(null);
                    pagamentoExtra.setNumeroRE(null);
                    borderoPagamentoExtra.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
                    jdbcBorderoPagamentoExtra.atualizarSituacao(borderoPagamentoExtra, idRev);
                    jdbcPagamentoExtra.atualizarPagamentoExtra(StatusPagamento.INDEFERIDO, pagamentoExtra, idRev);
                }
            }
            if (!itensBordero.getListaTransferenciaFinanceiraExclusao().isEmpty()) {
                for (BorderoTransferenciaFinanceira borderoTransferenciaFinanceira : itensBordero.getListaTransferenciaFinanceiraExclusao()) {
                    borderoTransferenciaFinanceira.getTransferenciaContaFinanceira().setStatusPagamento(StatusPagamento.INDEFERIDO);
                    borderoTransferenciaFinanceira.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
                    jdbcBorderoTransferenciaFinanceira.atualizarSituacao(borderoTransferenciaFinanceira, idRev);
                    jdbcTransferenciaFinanceira.atualizarStatusTransferenciaContaFinanceira(borderoTransferenciaFinanceira.getTransferenciaContaFinanceira(), StatusPagamento.INDEFERIDO, idRev);
                }
            }
            if (!itensBordero.getListaTransferenciaMesmaUnidadeExclusao().isEmpty()) {
                for (BorderoTransferenciaMesmaUnidade borderoTransferenciaMesmaUnidade : itensBordero.getListaTransferenciaMesmaUnidadeExclusao()) {
                    borderoTransferenciaMesmaUnidade.getTransferenciaMesmaUnidade().setStatusPagamento(StatusPagamento.INDEFERIDO);
                    borderoTransferenciaMesmaUnidade.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
                    jdbcBorderoTransferenciaMesmaUnidade.atualizarSituacao(borderoTransferenciaMesmaUnidade, idRev);
                    jdbcTransferenciaMesmaUnidade.atualizarStatusTransferenciaMesmaUnidade(borderoTransferenciaMesmaUnidade.getTransferenciaMesmaUnidade(), StatusPagamento.INDEFERIDO, idRev);
                }
            }
            if (!itensBordero.getListaLiberacaoFinanceiraExclusao().isEmpty()) {
                for (BorderoLiberacaoFinanceira borderoLiberacaoFinanceira : itensBordero.getListaLiberacaoFinanceiraExclusao()) {
                    borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira().setStatusPagamento(StatusPagamento.INDEFERIDO);
                    borderoLiberacaoFinanceira.setSituacaoItemBordero(SituacaoItemBordero.INDEFIRIDO);
                    jdbcBorderoLiberacaoFinanceira.atualizarSituacao(borderoLiberacaoFinanceira, idRev);
                    jdbcLiberacaoFinanceira.atualizarStatus(borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira(), StatusPagamento.INDEFERIDO, idRev);
                }
            }
        }
    }

    public void salvarBorderoNoArquivoBancario(Bordero entity, UsuarioSistema usuarioCorrente, String ip) {
        try {
            RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(ip, usuarioCorrente);
            jdbcBordero.atualizarBordero(entity, rev.getId());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void baixarBordero(Bordero entity) {
        try {
            entity.setSituacao(SituacaoBordero.PAGO);
            entity.setDataDebito(sistemaFacade.getDataOperacao());
            baixarOrdemBancaria(entity, StatusPagamento.PAGO, SituacaoItemBordero.PAGO);
            salvar(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }

    }

    public void baixarItemOrdemBancaria(Bordero entity) {
        try {
            baixarOrdemBancaria(entity, StatusPagamento.PAGO, SituacaoItemBordero.PAGO);
            salvar(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }

    }

    public void estornarBaixarBordero(Bordero entity) {
        try {
            if (verificarOrdemBancariaDentroDoArquivo(entity)) {
                entity.setSituacao(SituacaoBordero.DEFERIDO);
                entity.setDataDebito(null);
                estornarBaixaOrdemBancaria(entity, StatusPagamento.EFETUADO, SituacaoItemBordero.DEFERIDO);
            } else {
                entity.setSituacao(SituacaoBordero.AGUARDANDO_BAIXA);
                estornarBaixaOrdemBancaria(entity, StatusPagamento.EFETUADO, SituacaoItemBordero.EFETUADO);
            }
            salvar(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void salvarRemovendoItem(ItensBordero itensBordero) throws ExcecaoNegocioGenerica {
        try {
            RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
            salvarExcluindoItemBorderoAlterandoSituacaoDoMovimento(itensBordero, StatusPagamento.INDEFERIDO, rev.getId());
            retirarNumeroOrdemBancariaDoMovimentoNoEstorno(itensBordero, rev.getId());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void retirarNumeroOrdemBancariaDoMovimentoNoEstorno(ItensBordero entity, Long idRev) {
        for (BorderoPagamento borderoPagamento : entity.getArrayPgto()) {
            Pagamento pagamento = borderoPagamento.getPagamento();
            pagamento.setNumDocumento(null);
            pagamento.setNumeroRE(null);
            jdbcPagamento.atualizarPagamento(pagamento.getStatus(), pagamento, idRev);
        }
        for (BorderoPagamentoExtra borderoPagamento : entity.getArrayPgtoExtra()) {
            PagamentoExtra pagamentoExtra = borderoPagamento.getPagamentoExtra();
            pagamentoExtra.setNumeroPagamento(null);
            pagamentoExtra.setNumeroRE(null);
            jdbcPagamentoExtra.atualizarPagamentoExtra(pagamentoExtra.getStatus(), pagamentoExtra, idRev);
        }
    }

    private void retirarNumeroOrdemBancariaNoMovimento(Bordero entity) {
        if (!entity.getListaPagamentos().isEmpty()) {
            for (BorderoPagamento borderoPagamento : entity.getListaPagamentos()) {
                Pagamento pagamento = borderoPagamento.getPagamento();
                pagamento.setNumDocumento(null);
                pagamento.setNumeroRE(null);
                em.merge(pagamento);
            }
        }
        if (!entity.getListaPagamentosExtra().isEmpty()) {
            for (BorderoPagamentoExtra borderoPagamento : entity.getListaPagamentosExtra()) {
                PagamentoExtra pagamentoExtra = borderoPagamento.getPagamentoExtra();
                pagamentoExtra.setNumeroPagamento(null);
                pagamentoExtra.setNumeroRE(null);
                em.merge(pagamentoExtra);
            }
        }
    }

    public void definirNumeroOrdemBancariaNoMovimento(Bordero entity, Long idRev) {
        if (!entity.getListaPagamentos().isEmpty()) {
            for (BorderoPagamento borderoPagamento : entity.getListaPagamentos()) {
                Pagamento pagamento = borderoPagamento.getPagamento();
                pagamento.setNumDocumento(entity.getSequenciaArquivo());
                if (pagamento.isPagamentoComOperacaoDeGuia()) {
                    pagamento.setNumeroRE(singletonGeradorCodigoContabil.getNumeroOrdemBancaria(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataGeracao()));
                } else {
                    pagamento.setNumeroRE(entity.getSequenciaArquivo());
                }
                jdbcPagamento.atualizarPagamento(StatusPagamento.EFETUADO, pagamento, idRev);
            }
        }
        if (!entity.getListaPagamentosExtra().isEmpty()) {
            for (BorderoPagamentoExtra borderoPagamento : entity.getListaPagamentosExtra()) {
                PagamentoExtra pagamentoExtra = borderoPagamento.getPagamentoExtra();
                pagamentoExtra.setNumeroPagamento(entity.getSequenciaArquivo());
                if (pagamentoExtra.isDespesaExtraComOperacaoDeGuia()) {
                    pagamentoExtra.setNumeroRE(singletonGeradorCodigoContabil.getNumeroOrdemBancaria(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataGeracao()));
                } else {
                    pagamentoExtra.setNumeroRE(entity.getSequenciaArquivo());
                }
                jdbcPagamentoExtra.atualizarPagamentoExtra(StatusPagamento.EFETUADO, pagamentoExtra, idRev);
            }
        }
    }

    public void salvarExcluindoItemBorderoAlterandoSituacaoDoMovimento(ItensBordero itensBordero, StatusPagamento statusPagamento, Long idRev) {
        Bordero bordero = itensBordero.getBordero();

        for (BorderoPagamento pag : itensBordero.getArrayPgto()) {
            pag.getPagamento().setStatus(statusPagamento);
            bordero.setValor(pag.getBordero().getValor().subtract(pag.getPagamento().getValorFinal()));
            bordero.setQntdPagamentos(pag.getBordero().getQntdPagamentos() - 1l);
            if (itensBordero.getBordero().getValor().compareTo(BigDecimal.ZERO) == 0) {
                bordero.setSituacao(SituacaoBordero.INDEFERIDO);
            }
            bordero.getListaPagamentos().remove(pag);
            jdbcBorderoPagamento.remove(pag);
        }
        for (BorderoPagamentoExtra pag : itensBordero.getArrayPgtoExtra()) {
            pag.getPagamentoExtra().setStatus(statusPagamento);
            bordero.setValor(pag.getBordero().getValor().subtract(pag.getPagamentoExtra().getValor()));
            bordero.setQntdPagamentos(pag.getBordero().getQntdPagamentos() - 1l);
            if (itensBordero.getBordero().getValor().compareTo(BigDecimal.ZERO) == 0) {
                bordero.setSituacao(SituacaoBordero.INDEFERIDO);
            }
            bordero.getListaPagamentosExtra().remove(pag);
            jdbcBorderoPagamentoExtra.remove(pag);
        }
        for (BorderoTransferenciaMesmaUnidade trans : itensBordero.getArrayTransferenciaMesmaUnidade()) {
            trans.getTransferenciaMesmaUnidade().setStatusPagamento(statusPagamento);
            bordero.setValor(trans.getBordero().getValor().subtract(trans.getTransferenciaMesmaUnidade().getValor()));
            bordero.setQntdPagamentos(trans.getBordero().getQntdPagamentos() - 1l);
            if (itensBordero.getBordero().getValor().compareTo(BigDecimal.ZERO) == 0) {
                bordero.setSituacao(SituacaoBordero.INDEFERIDO);
            }
            bordero.getListaTransferenciaMesmaUnidade().remove(trans);
            jdbcBorderoTransferenciaMesmaUnidade.remove(trans);
        }
        for (BorderoLiberacaoFinanceira lib : itensBordero.getArrayLiberacaoCotaFinanceira()) {
            lib.getLiberacaoCotaFinanceira().setStatusPagamento(statusPagamento);
            bordero.setValor(lib.getBordero().getValor().subtract(lib.getLiberacaoCotaFinanceira().getValor()));
            bordero.setQntdPagamentos(lib.getBordero().getQntdPagamentos() - 1l);
            if (itensBordero.getBordero().getValor().compareTo(BigDecimal.ZERO) == 0) {
                bordero.setSituacao(SituacaoBordero.INDEFERIDO);
            }
            bordero.getListaLiberacaoCotaFinanceira().remove(lib);
            jdbcBorderoLiberacaoFinanceira.remove(lib);
        }
        for (BorderoTransferenciaFinanceira trans : itensBordero.getArrayTransferenciaFinanceira()) {
            trans.getTransferenciaContaFinanceira().setStatusPagamento(statusPagamento);
            bordero.setValor(trans.getBordero().getValor().subtract(trans.getTransferenciaContaFinanceira().getValor()));
            bordero.setQntdPagamentos(trans.getBordero().getQntdPagamentos() - 1l);
            if (itensBordero.getBordero().getValor().compareTo(BigDecimal.ZERO) == 0) {
                bordero.setSituacao(SituacaoBordero.INDEFERIDO);
            }
            bordero.getListaTransferenciaFinanceira().remove(trans);
            jdbcBorderoTransferenciaFinanceira.remove(trans);
        }
        if (bordero.getValor().compareTo(BigDecimal.ZERO) == 0) {
            bordero.setSituacao(SituacaoBordero.INDEFERIDO);
        }
        jdbcBordero.atualizarBordero(bordero, idRev);
    }

    @Override
    public void remover(Bordero entity) {
        RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
        movimentarItensOrdemBancaria(entity, StatusPagamento.DEFERIDO, SituacaoItemBordero.INDEFIRIDO, rev.getId());
        retirarNumeroOrdemBancariaNoMovimento(entity);
        super.remover(entity);
    }

    public void baixarOrdemBancaria(Bordero b, StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero) {
        for (BorderoPagamento bp : b.getListaPagamentos()) {
            baixarItemPagamentoOrdemBancaria(statusPagamento, situacaoItemBordero, bp);
        }
        for (BorderoPagamentoExtra pag : b.getListaPagamentosExtra()) {
            baixarItemDespesaExtraOrdemBancaria(statusPagamento, situacaoItemBordero, pag);
        }
        for (BorderoTransferenciaFinanceira btf : b.getListaTransferenciaFinanceira()) {
            baixarItemTransferenciaFinanceiraOB(situacaoItemBordero, btf);
        }
        for (BorderoTransferenciaMesmaUnidade trans : b.getListaTransferenciaMesmaUnidade()) {
            baixarItemTransferenciaMesmaUnidOB(statusPagamento, situacaoItemBordero, trans);
        }
        for (BorderoLiberacaoFinanceira lib : b.getListaLiberacaoCotaFinanceira()) {
            baixarItemLiberacaoFinanceiraOB(statusPagamento, situacaoItemBordero, lib);
        }
    }

    public void baixarItemLiberacaoFinanceiraOB(StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, BorderoLiberacaoFinanceira lib) {
        lib.setSituacaoItemBordero(situacaoItemBordero);
        lib.getLiberacaoCotaFinanceira().setStatusPagamento(statusPagamento);
        lib.getLiberacaoCotaFinanceira().setSaldo(lib.getLiberacaoCotaFinanceira().getSaldo().subtract(lib.getValor()));
        em.merge(lib.getLiberacaoCotaFinanceira());
    }

    public void baixarItemTransferenciaMesmaUnidOB(StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, BorderoTransferenciaMesmaUnidade trans) {
        trans.setSituacaoItemBordero(situacaoItemBordero);
        trans.getTransferenciaMesmaUnidade().setStatusPagamento(statusPagamento);
        trans.getTransferenciaMesmaUnidade().setSaldo(trans.getTransferenciaMesmaUnidade().getSaldo().subtract(trans.getValor()));
        em.merge(trans.getTransferenciaMesmaUnidade());
    }

    public void baixarItemTransferenciaFinanceiraOB(SituacaoItemBordero situacaoItemBordero, BorderoTransferenciaFinanceira btf) {
        TransferenciaContaFinanceira transferencia = btf.getTransferenciaContaFinanceira();
        btf.setSituacaoItemBordero(situacaoItemBordero);
        transferenciaContaFinanceiraFacade.baixar(transferencia);
        em.merge(transferencia);
    }

    public void baixarItemDespesaExtraOrdemBancaria(StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, BorderoPagamentoExtra pag) {
        PagamentoExtra despesa = pagamentoExtraFacade.recuperar(pag.getPagamentoExtra().getId());
        pag.setSituacaoItemBordero(situacaoItemBordero);
        if (!StatusPagamento.PAGO.equals(despesa.getStatus())) {
            pagamentoExtraFacade.baixar(despesa, statusPagamento);
        }
    }

    public void baixarItemPagamentoOrdemBancaria(StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, BorderoPagamento bp) {
        Pagamento pagamento = pagamentoFacade.recuperar(bp.getPagamento().getId());
        bp.setSituacaoItemBordero(situacaoItemBordero);
        if (!StatusPagamento.PAGO.equals(pagamento.getStatus())) {
            pagamentoFacade.baixar(pagamento, statusPagamento);
        }
    }

    public void estornarBaixaOrdemBancaria(Bordero b, StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero) {
        try {

            for (BorderoPagamento pag : b.getListaPagamentos()) {
                if (pag.getSituacaoItemBordero().equals(SituacaoItemBordero.PAGO)) {
                    Pagamento pagamento = pagamentoFacade.recuperar(pag.getPagamento().getId());
                    pag.setSituacaoItemBordero(situacaoItemBordero);
                    pagamentoFacade.estornarBaixa(pagamento, statusPagamento);
                }
            }
            for (BorderoPagamentoExtra pag : b.getListaPagamentosExtra()) {
                if (pag.getSituacaoItemBordero().equals(SituacaoItemBordero.PAGO)) {
                    PagamentoExtra pagamentoExtra = pagamentoExtraFacade.recuperar(pag.getPagamentoExtra().getId());
                    pag.setSituacaoItemBordero(situacaoItemBordero);
                    pagamentoExtraFacade.estornarBaixa(pagamentoExtra, statusPagamento);
                }
            }
            for (BorderoTransferenciaFinanceira trans : b.getListaTransferenciaFinanceira()) {
                if (trans.getSituacaoItemBordero().equals(SituacaoItemBordero.PAGO)) {
                    TransferenciaContaFinanceira transferencia = transferenciaContaFinanceiraFacade.recuperar(trans.getTransferenciaContaFinanceira().getId());
                    trans.setSituacaoItemBordero(situacaoItemBordero);
                    transferenciaContaFinanceiraFacade.estornarBaixa(transferencia);
                }
            }
            for (BorderoTransferenciaMesmaUnidade trans : b.getListaTransferenciaMesmaUnidade()) {
                if (trans.getSituacaoItemBordero().equals(SituacaoItemBordero.PAGO)) {
                    TransferenciaMesmaUnidade transferencia = transferenciaMesmaUnidadeFacade.recuperar(trans.getTransferenciaMesmaUnidade().getId());
                    trans.setSituacaoItemBordero(situacaoItemBordero);
                    transferenciaMesmaUnidadeFacade.estornarBaixa(transferencia);
                }
            }
            for (BorderoLiberacaoFinanceira lib : b.getListaLiberacaoCotaFinanceira()) {
                if (lib.getSituacaoItemBordero().equals(SituacaoItemBordero.PAGO)) {
                    LiberacaoCotaFinanceira liberacao = liberacaoCotaFinanceiraFacade.recuperar(lib.getLiberacaoCotaFinanceira().getId());
                    lib.setSituacaoItemBordero(situacaoItemBordero);
                    liberacaoCotaFinanceiraFacade.estornarBaixa(liberacao);
                }
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void movimentarItensOrdemBancaria(Bordero b, StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, Long idRev) {
        jdbcBorderoPagamento.atualizarTodosBorderoPagamento(b, statusPagamento, situacaoItemBordero, idRev);
        jdbcBorderoPagamentoExtra.atualizarTodosBorderoPagamentoExtra(b, statusPagamento, situacaoItemBordero, idRev);
        jdbcBorderoTransferenciaFinanceira.atualizarTodosBorderoTranferenciaFinanceira(b, statusPagamento, situacaoItemBordero, idRev);
        jdbcBorderoTransferenciaMesmaUnidade.atualizarTodosBorderoTransferenciaMesmaUnidade(b, statusPagamento, situacaoItemBordero, idRev);
        jdbcBorderoLiberacaoFinanceira.atualizarTodosBorderoLiberacaoFinanceira(b, statusPagamento, situacaoItemBordero, idRev);
    }

    public Boolean verificarOrdemBancariaDentroDoArquivo(Bordero bordero) {
        if (bordero.getId() == null) {
            return false;
        }
        String sql = " select b.* from bordero b                                                               " +
            "       where b.id not in( select b.id from arquivorembancobordero arq                             " +
            "                          inner join arquivoremessabanco ar on ar.id = arq.arquivoremessabanco_id " +
            "                          inner join bordero b on b.id = arq.bordero_id)                          " +
            "       and b.id = :idBordero                                                                      ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idBordero", bordero.getId());
        try {
            if (!query.getResultList().isEmpty()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String recuperaNumeroArquivoBancario(Bordero bordero) {
        String sql = " select arb.numero from arquivoremessabanco arb " +
            " inner join arquivorembancobordero arbb on arb.id = arbb.arquivoremessabanco_id " +
            " where arbb.bordero_id = :bordero ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("bordero", bordero.getId());
        query.setMaxResults(1);
        try {
            if (!query.getResultList().isEmpty()) {
                return (String) query.getSingleResult();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void validarNumeroRE(Bordero bordero) {
        ValidacaoException ve = new ValidacaoException();
        for (BorderoPagamento borderoPagamento : bordero.getListaPagamentos()) {
            Pagamento pagamento = borderoPagamento.getPagamento();
            if (pagamento.getNumeroRE() == null || pagamento.getNumeroRE().trim().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Pagamento Nº: " + pagamento.getNumeroPagamento() + ", não possui número da Relação da Ordem Bancária em seu cadastro.");
            }
        }
        for (BorderoPagamentoExtra borderoPagamentoExtra : bordero.getListaPagamentosExtra()) {
            PagamentoExtra pagamentoExtra = borderoPagamentoExtra.getPagamentoExtra();
            if (pagamentoExtra.getNumeroRE() == null || pagamentoExtra.getNumeroRE().trim().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Despesa Extra Nº: " + pagamentoExtra.getNumero() + ", não possui número da Relação da Ordem Bancária em seu cadastro.");
            }
        }
        ve.lancarException();
    }

    public void validarCampos(Bordero selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getBanco() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Banco deve ser informado.");
        }
        if (selecionado.getSubConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira deve ser informado.");
        } else if (selecionado.getListaPagamentos().isEmpty()
            && selecionado.getListaPagamentosExtra().isEmpty()
            && selecionado.getListaTransferenciaFinanceira().isEmpty()
            && selecionado.getListaTransferenciaMesmaUnidade().isEmpty()
            && selecionado.getListaLiberacaoCotaFinanceira().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Para continuar a operação, selecione um item em uma das abas (Pagamento, Despesa Extraorçamentária, Transferência Fianceira, Transferência Mesma Unidade ou Liberação Financeira).");
        }
        ve.lancarException();
        validarPagamentoEmOutraOB(selecionado);
        validarDespesaExtraEmOutraOB(selecionado);
        validarTransferenciaFinanceiraEmOutraOB(selecionado);
        validarTransferenciaMesmaUnidadeEmOutraOB(selecionado);
        validarLiberacaoFinanceiraEmOutraOB(selecionado);
    }

    private void validarPagamentoEmOutraOB(Bordero selecionado) {
        ValidacaoException ve = new ValidacaoException();
        for (BorderoPagamento bp : selecionado.getListaPagamentos()) {
            Bordero borderoComMesmoPagamento = buscarOBPorPagamentoEmOutraOB(bp);
            if (borderoComMesmoPagamento != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Pagamento <b>" + bp.getPagamento().getNumeroPagamento() +
                    "</b> já foi adicionado em outra Ordem Bancária <b>" + borderoComMesmoPagamento.toStringAutoComplete() + "</b>"
                );
            }
        }
        ve.lancarException();
    }

    private Bordero buscarOBPorPagamentoEmOutraOB(BorderoPagamento borderoPagamento) {
        if (borderoPagamento == null || borderoPagamento.getPagamento() == null || borderoPagamento.getBordero() == null)
            return null;
        String sql = " select b.* " +
            " from bordero b " +
            "    inner join borderopagamento bp on bp.bordero_id = b.id " +
            " where bp.pagamento_id = :idPagamento " +
            "   and bp.situacaoitembordero <> :indeferido ";
        if (borderoPagamento.getBordero().getId() != null) {
            sql += " and b.id <> :idBordero ";
        }
        Query q = em.createNativeQuery(sql, Bordero.class);
        q.setParameter("idPagamento", borderoPagamento.getPagamento().getId());
        q.setParameter("indeferido", SituacaoItemBordero.INDEFIRIDO.name());
        if (borderoPagamento.getBordero().getId() != null) {
            q.setParameter("idBordero", borderoPagamento.getBordero().getId());
        }
        List<Bordero> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }

    private void validarDespesaExtraEmOutraOB(Bordero selecionado) {
        ValidacaoException ve = new ValidacaoException();
        for (BorderoPagamentoExtra bpe : selecionado.getListaPagamentosExtra()) {
            Bordero borderoComMesmoPagamentoExtra = buscarOBPorPagamentoExtraEmOutraOB(bpe);
            if (borderoComMesmoPagamentoExtra != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Despesa Extraorçamentária <b>" + bpe.getPagamentoExtra().getNumero() +
                    "</b> já foi adicionada em outra Ordem Bancária <b>" + borderoComMesmoPagamentoExtra.toStringAutoComplete() + "</b>"
                );
            }
        }
        ve.lancarException();
    }

    private Bordero buscarOBPorPagamentoExtraEmOutraOB(BorderoPagamentoExtra borderoPagamentoExtra) {
        if (borderoPagamentoExtra == null || borderoPagamentoExtra.getPagamentoExtra() == null || borderoPagamentoExtra.getBordero() == null)
            return null;
        String sql = " select b.* " +
            " from bordero b " +
            "    inner join borderopagamentoextra bpe on bpe.bordero_id = b.id " +
            " where bpe.pagamentoextra_id = :idPagamentoExtra " +
            "   and bpe.situacaoitembordero <> :indeferido ";
        if (borderoPagamentoExtra.getBordero().getId() != null) {
            sql += " and b.id <> :idBordero ";
        }
        Query q = em.createNativeQuery(sql, Bordero.class);
        q.setParameter("idPagamentoExtra", borderoPagamentoExtra.getPagamentoExtra().getId());
        q.setParameter("indeferido", SituacaoItemBordero.INDEFIRIDO.name());
        if (borderoPagamentoExtra.getBordero().getId() != null) {
            q.setParameter("idBordero", borderoPagamentoExtra.getBordero().getId());
        }
        List<Bordero> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }

    private void validarTransferenciaFinanceiraEmOutraOB(Bordero selecionado) {
        ValidacaoException ve = new ValidacaoException();
        for (BorderoTransferenciaFinanceira btf : selecionado.getListaTransferenciaFinanceira()) {
            Bordero borderoComMesmaTransferencia = buscarOBPorTransferenciaFinanceiraEmOutraOB(btf);
            if (borderoComMesmaTransferencia != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Transferência Financeira <b>" + btf.getTransferenciaContaFinanceira().getNumero() +
                    "</b> já foi adicionada em outra Ordem Bancária <b>" + borderoComMesmaTransferencia.toStringAutoComplete() + "</b>"
                );
            }
        }
        ve.lancarException();
    }

    private Bordero buscarOBPorTransferenciaFinanceiraEmOutraOB(BorderoTransferenciaFinanceira borderoTransferenciaFinanceira) {
        if (borderoTransferenciaFinanceira == null || borderoTransferenciaFinanceira.getTransferenciaContaFinanceira() == null || borderoTransferenciaFinanceira.getBordero() == null)
            return null;
        String sql = " select b.* " +
            " from bordero b " +
            "    inner join borderotransffinanceira btf on btf.bordero_id = b.id " +
            " where btf.transffinanceira_id = :idTransferenciaFinanceira " +
            "   and btf.situacaoitembordero <> :indeferido ";
        if (borderoTransferenciaFinanceira.getBordero().getId() != null) {
            sql += " and b.id <> :idBordero ";
        }
        Query q = em.createNativeQuery(sql, Bordero.class);
        q.setParameter("idTransferenciaFinanceira", borderoTransferenciaFinanceira.getTransferenciaContaFinanceira().getId());
        q.setParameter("indeferido", SituacaoItemBordero.INDEFIRIDO.name());
        if (borderoTransferenciaFinanceira.getBordero().getId() != null) {
            q.setParameter("idBordero", borderoTransferenciaFinanceira.getBordero().getId());
        }
        List<Bordero> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }

    private void validarTransferenciaMesmaUnidadeEmOutraOB(Bordero selecionado) {
        ValidacaoException ve = new ValidacaoException();
        for (BorderoTransferenciaMesmaUnidade btmu : selecionado.getListaTransferenciaMesmaUnidade()) {
            Bordero borderoComMesmaTransferencia = buscarOBPorTransferenciaMesmaUnidadeEmOutraOB(btmu);
            if (borderoComMesmaTransferencia != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Tranferência Financeira Mesma Unidade <b>" + btmu.getTransferenciaMesmaUnidade().getNumero() +
                    "</b> já foi adicionada em outra Ordem Bancária <b>" + borderoComMesmaTransferencia.toStringAutoComplete() + "</b>"
                );
            }
        }
        ve.lancarException();
    }

    private Bordero buscarOBPorTransferenciaMesmaUnidadeEmOutraOB(BorderoTransferenciaMesmaUnidade borderoTransferenciaMesmaUnidade) {
        if (borderoTransferenciaMesmaUnidade == null || borderoTransferenciaMesmaUnidade.getTransferenciaMesmaUnidade() == null || borderoTransferenciaMesmaUnidade.getBordero() == null)
            return null;
        String sql = " select b.* " +
            " from bordero b " +
            "    inner join borderotransfmesmaunidade btmu on btmu.bordero_id = b.id " +
            " where btmu.transfmesmaunidade_id = :idTransferenciaMesmaUnidade " +
            "   and btmu.situacaoitembordero <> :indeferido ";
        if (borderoTransferenciaMesmaUnidade.getBordero().getId() != null) {
            sql += " and b.id <> :idBordero ";
        }
        Query q = em.createNativeQuery(sql, Bordero.class);
        q.setParameter("idTransferenciaMesmaUnidade", borderoTransferenciaMesmaUnidade.getTransferenciaMesmaUnidade().getId());
        q.setParameter("indeferido", SituacaoItemBordero.INDEFIRIDO.name());
        if (borderoTransferenciaMesmaUnidade.getBordero().getId() != null) {
            q.setParameter("idBordero", borderoTransferenciaMesmaUnidade.getBordero().getId());
        }
        List<Bordero> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }

    private void validarLiberacaoFinanceiraEmOutraOB(Bordero selecionado) {
        ValidacaoException ve = new ValidacaoException();
        for (BorderoLiberacaoFinanceira blf : selecionado.getListaLiberacaoCotaFinanceira()) {
            Bordero borderoComMesmaLiberacaoFinanceira = buscarOBPorLiberacaoFinanceiraEmOutraOB(blf);
            if (borderoComMesmaLiberacaoFinanceira != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Liberação Financeira <b>" + blf.getLiberacaoCotaFinanceira().getNumero() +
                    "</b> já foi adicionada em outra Ordem Bancária <b>" + borderoComMesmaLiberacaoFinanceira.toStringAutoComplete() + "</b>"
                );
            }
        }
        ve.lancarException();
    }

    private Bordero buscarOBPorLiberacaoFinanceiraEmOutraOB(BorderoLiberacaoFinanceira borderoLiberacaoFinanceira) {
        if (borderoLiberacaoFinanceira == null || borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira() == null || borderoLiberacaoFinanceira.getBordero() == null)
            return null;
        String sql = " select b.* " +
            " from bordero b " +
            "    inner join borderolibcotafinanceira blf on blf.bordero_id = b.id " +
            " where blf.libcotafinanceira_id = :idLiberacaoFinanceira " +
            "   and blf.situacaoitembordero <> :indeferido ";
        if (borderoLiberacaoFinanceira.getBordero().getId() != null) {
            sql += " and b.id <> :idBordero ";
        }
        Query q = em.createNativeQuery(sql, Bordero.class);
        q.setParameter("idLiberacaoFinanceira", borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira().getId());
        q.setParameter("indeferido", SituacaoItemBordero.INDEFIRIDO.name());
        if (borderoLiberacaoFinanceira.getBordero().getId() != null) {
            q.setParameter("idBordero", borderoLiberacaoFinanceira.getBordero().getId());
        }
        List<Bordero> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }
}
