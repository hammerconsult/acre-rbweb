package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SaldoItemContratoOrigemVO;
import br.com.webpublico.enums.OperacaoSaldoItemContrato;
import br.com.webpublico.enums.OrigemSaldoItemContrato;
import br.com.webpublico.enums.SubTipoSaldoItemContrato;
import br.com.webpublico.enums.TipoSaldoItemContrato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.administrativo.dao.JdbcReprocessamentoContrato;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class SaldoItemContratoFacade extends AbstractFacade<SaldoItemContrato> {

    protected static final Logger logger = LoggerFactory.getLogger(SaldoItemContratoFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    private JdbcReprocessamentoContrato jdbcReprocessamentoContrato;

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcReprocessamentoContrato = (JdbcReprocessamentoContrato) ap.getBean("jdbcReprocessamentoContrato");
    }

    public SaldoItemContratoFacade() {
        super(SaldoItemContrato.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void gerarSaldoPorMovimento(MovimentoItemContrato novoMov) {
        jdbcReprocessamentoContrato.insertMovimento(novoMov);
        criarOrAtualizarSaldoItemContrato(novoMov, novoMov.getDataMovimento());
    }

    public void gerarSaldoInicialContrato(ItemContrato item) {
        MovimentoItemContrato novoMovVariacao = novoMovimentoInicialItemContrato(item, SubTipoSaldoItemContrato.VARIACAO);
        jdbcReprocessamentoContrato.insertMovimento(novoMovVariacao);
        novoSaldoInicialItemContrato(item, SubTipoSaldoItemContrato.VARIACAO);

        MovimentoItemContrato novoMovExecucao = novoMovimentoInicialItemContrato(item, SubTipoSaldoItemContrato.EXECUCAO);
        jdbcReprocessamentoContrato.insertMovimento(novoMovExecucao);
        novoSaldoInicialItemContrato(item, SubTipoSaldoItemContrato.EXECUCAO);
    }

    public void gerarSaldoExecucao(ExecucaoContratoItem itemExec, Date dataOperacao) {
        ExecucaoContrato execucao = itemExec.getExecucaoContrato();

        MovimentoItemContrato novoMov = new MovimentoItemContrato();
        novoMov.setItemContrato(itemExec.getItemContrato());
        novoMov.setDataMovimento(getDataComHoraAtual(execucao.getDataLancamento()));
        novoMov.setIndice(gerarIndiceMovimentoItemContrato(itemExec.getItemContrato()));
        novoMov.setIdOrigem(execucao.getIdOrigem());
        novoMov.setIdMovimento(execucao.getId());
        novoMov.setOrigem(execucao.getOrigem());
        novoMov.setTipo(TipoSaldoItemContrato.DESPESA_NORMAL);
        novoMov.setSubTipo(SubTipoSaldoItemContrato.EXECUCAO);
        novoMov.setOperacao(execucao.getOperacaoOrigem());
        novoMov.setQuantidade(itemExec.getQuantidadeUtilizada());
        novoMov.setValorUnitario(itemExec.getValorUnitario());
        novoMov.setValorTotal(itemExec.getValorTotal());
        jdbcReprocessamentoContrato.insertMovimento(novoMov);

        criarOrAtualizarSaldoItemContrato(novoMov, dataOperacao);
    }

    public void gerarSaldoExecucaoEstorno(ExecucaoContratoEmpenhoEstornoItem itemExecEst, Date dataOperacao) {
        ExecucaoContratoEstorno exEst = itemExecEst.getExecucaoContratoEmpenhoEst().getExecucaoContratoEstorno();

        MovimentoItemContrato novoMov = new MovimentoItemContrato();
        novoMov.setItemContrato(itemExecEst.getExecucaoContratoItem().getItemContrato());
        novoMov.setDataMovimento(getDataComHoraAtual(exEst.getDataLancamento()));
        novoMov.setIndice(gerarIndiceMovimentoItemContrato(itemExecEst.getExecucaoContratoItem().getItemContrato()));
        novoMov.setIdOrigem(exEst.getExecucaoContrato().getIdOrigem());
        novoMov.setIdMovimento(exEst.getId());
        novoMov.setOrigem(exEst.getExecucaoContrato().getOrigem());
        novoMov.setTipo(TipoSaldoItemContrato.DESPESA_ESTORNO);
        novoMov.setSubTipo(SubTipoSaldoItemContrato.EXECUCAO);
        novoMov.setOperacao(exEst.getExecucaoContrato().getOperacaoOrigem());
        novoMov.setQuantidade(itemExecEst.getQuantidade());
        novoMov.setValorUnitario(itemExecEst.getValorUnitario());
        novoMov.setValorTotal(itemExecEst.getValorTotal());
        jdbcReprocessamentoContrato.insertMovimento(novoMov);

        criarOrAtualizarSaldoItemContrato(novoMov, dataOperacao);
    }

    public void gerarSaldoAlteracaoContratual(MovimentoAlteracaoContratualItem movItem, SubTipoSaldoItemContrato subTipo, Date dataOperacao) {
        AlteracaoContratual alteracaoCont = movItem.getMovimentoAlteracaoCont().getAlteracaoContratual();

        MovimentoItemContrato novoMov = new MovimentoItemContrato();
        novoMov.setItemContrato(movItem.getItemContrato());
        novoMov.setDataMovimento(getDataComHoraAtual(alteracaoCont.getDataLancamento()));
        novoMov.setIndice(gerarIndiceMovimentoItemContrato(movItem.getItemContrato()));
        novoMov.setIdOrigem(movItem.getMovimentoAlteracaoCont().getFinalidade().isAcrescimo() ? alteracaoCont.getId() : movItem.getMovimentoAlteracaoCont().getIdMovimentoOrigem());
        novoMov.setIdMovimento(alteracaoCont.getId());

        OrigemSaldoItemContrato origemSaldo = movItem.getMovimentoAlteracaoCont().getFinalidade().isAcrescimo()
            ? alteracaoCont.getTipoAlteracaoContratual().isAditivo() ? OrigemSaldoItemContrato.ADITIVO : OrigemSaldoItemContrato.APOSTILAMENTO
            : movItem.getMovimentoAlteracaoCont().getOrigemSupressao();
        novoMov.setOrigem(origemSaldo);

        novoMov.setTipo(movItem.getMovimentoAlteracaoCont().getFinalidade().isAcrescimo() ? TipoSaldoItemContrato.ALTERACAO_CONTRATUAL_ACRESCIMO : TipoSaldoItemContrato.ALTERACAO_CONTRATUAL_SUPRESSAO);
        novoMov.setSubTipo(subTipo);
        novoMov.setOperacao(OperacaoSaldoItemContrato.getOperacaoSaldoPorOperacaoMovimentoContratual(movItem.getMovimentoAlteracaoCont().getOperacao()));
        novoMov.setQuantidade(movItem.getQuantidade());
        novoMov.setValorUnitario(movItem.getValorUnitario());
        novoMov.setValorTotal(movItem.getValorTotal());
        jdbcReprocessamentoContrato.insertMovimento(novoMov);

        criarOrAtualizarSaldoItemContrato(novoMov, dataOperacao);
    }

    private void novoSaldoInicialItemContrato(ItemContrato item, SubTipoSaldoItemContrato subTipo) {
        SaldoItemContrato novoSaldo = new SaldoItemContrato();
        novoSaldo.setItemContrato(item);
        novoSaldo.setDataSaldo(item.getContrato().getDataLancamento());
        novoSaldo.setIdOrigem(item.getContrato().getId());
        novoSaldo.setOrigem(OrigemSaldoItemContrato.CONTRATO);
        novoSaldo.setOperacao(OperacaoSaldoItemContrato.PRE_EXECUCAO);
        novoSaldo.setSubTipo(subTipo);
        novoSaldo.setValorUnitario(item.getValorUnitario());
        novoSaldo.setSaldoQuantidade(item.getQuantidadeTotalContrato());
        novoSaldo.setSaldoValor(item.getValorTotal());
        jdbcReprocessamentoContrato.insertSaldo(novoSaldo);
    }

    public MovimentoItemContrato novoMovimentoInicialItemContrato(ItemContrato item, SubTipoSaldoItemContrato subTipo) {
        MovimentoItemContrato novoMov = new MovimentoItemContrato();
        novoMov.setItemContrato(item);
        novoMov.setDataMovimento(item.getContrato().getDataLancamento());
        novoMov.setIndice(gerarIndiceMovimentoItemContrato(item));
        novoMov.setIdOrigem(item.getContrato().getId());
        novoMov.setIdMovimento(item.getContrato().getId());
        novoMov.setOrigem(OrigemSaldoItemContrato.CONTRATO);
        novoMov.setTipo(TipoSaldoItemContrato.ORIGINAL);
        novoMov.setOperacao(OperacaoSaldoItemContrato.PRE_EXECUCAO);
        novoMov.setSubTipo(subTipo);
        novoMov.setQuantidade(item.getQuantidadeTotalContrato());
        novoMov.setValorUnitario(item.getValorUnitario());
        novoMov.setValorTotal(item.getValorTotal());
        return novoMov;
    }

    private void criarOrAtualizarSaldoItemContrato(MovimentoItemContrato novoMov, Date dataOperacao) {
        SaldoItemContrato ultimoSaldo = buscarUtilmoSaldoPorOrigemOperacaoAndSubTipo(novoMov, dataOperacao);
        if (ultimoSaldo == null) {
            SaldoItemContrato novoSaldo = new SaldoItemContrato();
            novoSaldo.setItemContrato(novoMov.getItemContrato());
            novoSaldo.setDataSaldo(getDataComHoraAtual(novoMov.getDataMovimento()));
            novoSaldo.setIdOrigem(novoMov.getIdOrigem());
            novoSaldo.setOrigem(novoMov.getOrigem());
            novoSaldo.setOperacao(novoMov.getOperacao());
            novoSaldo.setSubTipo(novoMov.getSubTipo());
            novoSaldo.setValorUnitario(novoMov.getValorUnitario());
            novoSaldo.setSaldoQuantidade(novoMov.getQuantidade());
            novoSaldo.setSaldoValor(novoMov.getValorTotal());
            jdbcReprocessamentoContrato.insertSaldo(novoSaldo);

        } else if (DataUtil.dataSemHorario(ultimoSaldo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(novoMov.getDataMovimento())) == 0) {
            atualizarSaldoQuantidade(ultimoSaldo, novoMov.getTipo(), novoMov.getQuantidade());
            atualizarSaldoValor(ultimoSaldo, novoMov.getTipo(), novoMov.getValorTotal());
            jdbcReprocessamentoContrato.updateSaldo(ultimoSaldo);

        } else {
            SaldoItemContrato novoSaldo = (SaldoItemContrato) Util.clonarObjeto(ultimoSaldo);
            novoSaldo.setId(null);
            novoSaldo.setDataSaldo(getDataComHoraAtual(novoMov.getDataMovimento()));
            atualizarSaldoQuantidade(novoSaldo, novoMov.getTipo(), novoMov.getQuantidade());
            atualizarSaldoValor(novoSaldo, novoMov.getTipo(), novoMov.getValorTotal());
            jdbcReprocessamentoContrato.insertSaldo(novoSaldo);
        }
    }

    private void atualizarSaldoQuantidade(SaldoItemContrato saldo, TipoSaldoItemContrato tipoMov, BigDecimal quantidadeMov) {
        saldo.setSaldoQuantidade(tipoMov.isTipoMovimentoAcrescimo()
            ? saldo.getSaldoQuantidade().add(quantidadeMov)
            : saldo.getSaldoQuantidade().subtract(quantidadeMov));
    }

    private void atualizarSaldoValor(SaldoItemContrato saldo, TipoSaldoItemContrato tipoMov, BigDecimal valorMov) {
        saldo.setSaldoValor(tipoMov.isTipoMovimentoAcrescimo()
            ? saldo.getSaldoValor().add(valorMov)
            : saldo.getSaldoValor().subtract(valorMov));
    }

    public Date getDataComHoraAtual(Date dataMovimento) {
        return DataUtil.getDataComHoraAtual(dataMovimento);
    }

    public SaldoItemContrato buscarUtilmoSaldoPorOrigemOperacaoAndSubTipo(MovimentoItemContrato movimento, Date dataOperacao) {
        String sql = " " +
            " select saldo.* from saldoitemcontrato saldo "
            + " where saldo.itemcontrato_id = :idItem "
            + " and saldo.idorigem = :idOrigem "
            + " and saldo.origem = :origem " +
            "   and saldo.operacao = :operacao " +
            "   and saldo.subtipo = :subtipo " +
            "   and trunc(saldo.datasaldo) =   " +
            "           (select max(trunc(saldoMax.datasaldo))    " +
            "             from saldoitemcontrato saldoMax    " +
            "             inner join itemcontrato itemMax on itemMax.id = saldoMax.itemcontrato_id " +
            "             where saldoMax.itemcontrato_id = saldo.itemcontrato_id   " +
            "               and saldoMax.origem = saldo.origem " +
            "               and saldoMax.operacao = saldo.operacao " +
            "               and saldoMax.subtipo = saldo.subtipo " +
            "               and saldoMax.idorigem = saldo.idorigem " +
            "               and trunc(saldoMax.datasaldo) <= to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, SaldoItemContrato.class);
        q.setParameter("idItem", movimento.getItemContrato().getId());
        q.setParameter("idOrigem", movimento.getIdOrigem());
        q.setParameter("origem", movimento.getOrigem().name());
        q.setParameter("operacao", movimento.getOperacao().name());
        q.setParameter("subtipo", movimento.getSubTipo().name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        try {
            return (SaldoItemContrato) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new ValidacaoException("Foi encontrado mais de um saldo para o item: " + movimento.getItemContrato()
                + " para a origem " + movimento.getOrigem().getDescricao() + " e operação: " + movimento.getOperacao().getDescricao() + ".");
        }
    }

    public List<SaldoItemContrato> buscarSaldoPorItemContrato(ItemContrato itemContrato, SubTipoSaldoItemContrato subTipo) {
        if (itemContrato == null || itemContrato.getId() == null) return Lists.newArrayList();
        String sql = " select saldo.* from saldoitemcontrato saldo " +
            "           where saldo.itemcontrato_id = :idItem " +
            "               and trunc(saldo.datasaldo) =   " +
            "                (select max(trunc(saldoMax.datasaldo))    " +
            "                   from saldoitemcontrato saldoMax    " +
            "                   inner join itemcontrato itemMax on itemMax.id = saldoMax.itemcontrato_id " +
            "                   where saldoMax.itemcontrato_id = saldo.itemcontrato_id   " +
            "                       and saldoMax.origem = saldo.origem " +
            "                       and saldoMax.operacao = saldo.operacao " +
            "                       and saldoMax.subtipo = saldo.subtipo " +
            "                       and saldoMax.idorigem = saldo.idorigem " +
            "                       and trunc(saldoMax.datasaldo) <= to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        sql += subTipo != null ? " and saldo.subtipo = :subtipo " : " ";
        Query q = em.createNativeQuery(sql, SaldoItemContrato.class);
        q.setParameter("idItem", itemContrato.getId());
        if (subTipo != null) {
            q.setParameter("subtipo", subTipo.name());
        }
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<SaldoItemContratoOrigemVO> buscarSaldoOrigemVO(Contrato contrato, SubTipoSaldoItemContrato subTipoSaldo) {
        String sql = "" +
            " select  " +
            "    saldo.origem                       as origem,   " +
            "    saldo.operacao                     as operacao,  " +
            "    saldo.subtipo                      as subtipo,  " +
            "    saldo.idorigem                     as id_origem,  " +
            "    coalesce(sum(saldo.saldovalor), 0) as saldo_mov  " +
            " from saldoitemcontrato saldo    " +
            " inner join itemcontrato item on item.id = saldo.itemcontrato_id  " +
            "  where item.contrato_id = :idContrato " +
            "    and saldo.saldovalor > 0 " +
            "    and trunc(saldo.datasaldo) =   " +
            "           (select max(trunc(saldoMax.datasaldo))    " +
            "             from saldoitemcontrato saldoMax    " +
            "             inner join itemcontrato itemMax on itemMax.id = saldoMax.itemcontrato_id " +
            "             where saldoMax.itemcontrato_id = saldo.itemcontrato_id   " +
            "               and saldoMax.origem = saldo.origem " +
            "               and saldoMax.operacao = saldo.operacao " +
            "               and saldoMax.subtipo = saldo.subtipo " +
            "               and saldoMax.idorigem = saldo.idorigem " +
            "               and trunc(saldoMax.datasaldo) <= to_date(:dataOperacao, 'dd/MM/yyyy'))  ";
        sql += subTipoSaldo != null ? " and saldo.subtipo = :subTipo " : "";
        sql += "  group by saldo.origem, saldo.operacao, saldo.subtipo, saldo.idorigem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (subTipoSaldo != null) {
            q.setParameter("subTipo", subTipoSaldo.name());
        }
        List<SaldoItemContratoOrigemVO> toReturn = Lists.newArrayList();
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                SaldoItemContratoOrigemVO item = new SaldoItemContratoOrigemVO();
                item.setOrigem(OrigemSaldoItemContrato.valueOf((String) obj[0]));
                item.setOperacao(OperacaoSaldoItemContrato.valueOf((String) obj[1]));
                item.setSubTipo(SubTipoSaldoItemContrato.valueOf((String) obj[2]));
                item.setIdMovimentoOrigem(((BigDecimal) obj[3]).longValue());
                item.setSaldo((BigDecimal) obj[4]);

                if (item.getOrigem().isContrato()) {
                    item.setDescricaoMovimento(contrato.toString());
                    item.setNumeroAnoTermo(contrato.getNumeroAnoTermo());
                } else {
                    AlteracaoContratual alteracaoCont = em.find(AlteracaoContratual.class, item.getIdMovimentoOrigem());
                    if (alteracaoCont != null) {
                        item.setDescricaoMovimento(alteracaoCont.toString());
                        item.setNumeroAnoTermo(alteracaoCont.getNumeroAnoTermo());
                    }
                }
                item.setItensSaldo(buscarItensSaldoPorIdOrigemOperacaoAndSubTipo(item.getIdMovimentoOrigem(), item.getOperacao(), item.getSubTipo()));
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public List<SaldoItemContrato> buscarItensSaldoPorIdOrigemOperacaoAndSubTipo(Long idOrigem, OperacaoSaldoItemContrato operacao, SubTipoSaldoItemContrato subTipo) {
        String sql = "" +
            " select saldo.* from saldoitemcontrato saldo    " +
            " inner join itemcontrato item on item.id = saldo.itemcontrato_id  " +
            "  where saldo.idorigem = :idOrigem  " +
            "  and saldo.operacao = :operacao " +
            "  and saldo.subtipo = :subtipo " +
            "  and saldo.saldovalor > 0 " +
            "  and trunc(saldo.datasaldo) =   " +
            "           (select max(trunc(saldoMax.datasaldo))    " +
            "             from saldoitemcontrato saldoMax    " +
            "             inner join itemcontrato itemMax on itemMax.id = saldoMax.itemcontrato_id " +
            "             where saldoMax.itemcontrato_id = saldo.itemcontrato_id   " +
            "               and saldoMax.origem = saldo.origem " +
            "               and saldoMax.operacao = saldo.operacao " +
            "               and saldoMax.subtipo = saldo.subtipo " +
            "               and saldoMax.idorigem = saldo.idorigem " +
            "               and trunc(saldoMax.datasaldo) <= to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, SaldoItemContrato.class);
        q.setParameter("idOrigem", idOrigem);
        q.setParameter("operacao", operacao.name());
        q.setParameter("subtipo", subTipo.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<MovimentoItemContrato> buscarMovimentosItemContrato(ItemContrato itemContrato, Long idOrigem, OrigemSaldoItemContrato origem,
                                                                    OperacaoSaldoItemContrato operacao, SubTipoSaldoItemContrato subTipo) {
        String sql = "" +
            " select " +
            "   mov.* from movimentoitemcontrato mov " +
            " where mov.itemcontrato_id = :idItem " +
            "   and mov.idorigem = :idOrigem " +
            "   and mov.origem = :origem " +
            "   and mov.operacao = :operacao " +
            "   and mov.subtipo = :subTipo " +
            "  order by mov.datamovimento ";
        Query q = em.createNativeQuery(sql, MovimentoItemContrato.class);
        q.setParameter("idItem", itemContrato.getId());
        q.setParameter("idOrigem", idOrigem);
        q.setParameter("origem", origem.name());
        q.setParameter("operacao", operacao.name());
        q.setParameter("subTipo", subTipo.name());
        return q.getResultList();
    }

    public Long gerarIndiceMovimentoItemContrato(ItemContrato itemContrato) {
        if (itemContrato.getId() == null) {
            return 1L;
        }
        String sql = " select coalesce(max(mov.indice),0) +1 from movimentoitemcontrato mov " +
            "           where mov.itemcontrato_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", itemContrato.getId());
        return ((BigDecimal) q.getSingleResult()).longValue();
    }

    public void excluirMovimentoItemContrato(ItemContrato itemContrato) {
        String sql = " delete from movimentoitemcontrato where itemcontrato_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", itemContrato.getId());
        q.executeUpdate();
    }

    public void excluirSaldoItemContrato(ItemContrato itemContrato) {
        String sql = " delete from saldoitemcontrato where itemcontrato_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", itemContrato.getId());
        q.executeUpdate();
    }

    public void excluirSaldoAndMovimento(ItemContrato itemContrato) {
        if (itemContrato != null && itemContrato.getId() != null) {
            excluirMovimentoItemContrato(itemContrato);
            excluirSaldoItemContrato(itemContrato);
        }
    }
}
