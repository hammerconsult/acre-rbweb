package br.com.webpublico.negocios.administrativo.dao;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.ExecucaoContrato;
import br.com.webpublico.entidades.MovimentoItemContrato;
import br.com.webpublico.entidades.SaldoItemContrato;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository("jdbcReprocessamentoContrato")
public class JdbcReprocessamentoContrato extends JdbcDaoSupport implements Serializable {

    public static final String INSERT_SALDO = " insert into saldoitemcontrato (id, itemcontrato_id, idorigem, datasaldo, origem, subtipo,  operacao, saldoquantidade, valorunitario, saldovalor) " + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

    public static final String UPDATE_SALDO = " update saldoitemcontrato set datasaldo = ?, saldoquantidade = ?, saldovalor = ? where id = ? ";

    public static final String INSERT_MOVIMENTO = " insert into movimentoitemcontrato (id, itemcontrato_id, indice, datamovimento, idorigem, idmovimento, origem, tipo, subtipo, operacao, quantidade, valorunitario, valortotal) " + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

    public static final String UPDATE_CONTRATO = " update contrato set valoratualcontrato = ?, variacaoatualcontrato = ?, saldoatualcontrato = ?, terminovigenciaatual = ?, terminoexecucaoatual = ?, reprocessado = ?  where id = ? ";

    public static final String UPDATE_EXECUCAO = " update execucaocontrato set idorigem  = ?, origem = ?, operacaoorigem = ?, reprocessada = ?  where id = ? ";

    public static final String DELETE_MOVIMENTO = " delete from movimentoitemcontrato where itemcontrato_id in (select item.id from itemcontrato item where item.contrato_id = ?) ";

    public static final String DELETE_SALDO = " delete from saldoitemcontrato where itemcontrato_id in (select item.id from itemcontrato item where item.contrato_id = ?) ";

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcReprocessamentoContrato(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void insertMovimento(MovimentoItemContrato mov) {
        mov.setId(geradorDeIds.getProximoId());
        getJdbcTemplate().update(INSERT_MOVIMENTO, getAtributosMovimentoInsert(mov));
    }

    public void insertSaldo(SaldoItemContrato saldo) {
        saldo.setId(geradorDeIds.getProximoId());
        getJdbcTemplate().update(INSERT_SALDO, getAtributosSaldoInsert(saldo));
    }

    public void updateSaldo(SaldoItemContrato saldo) {
        getJdbcTemplate().update(UPDATE_SALDO, getAtributosSaldoUpdate(saldo));
    }

    public void updateContrato(Contrato contrato) {
        getJdbcTemplate().update(UPDATE_CONTRATO, getAtributosContratoUpdate(contrato));
    }

    public void updateExecucaoContrato(ExecucaoContrato execucaoContrato) {
        getJdbcTemplate().update(UPDATE_EXECUCAO, getAtributosExecucaoContratoUpdate(execucaoContrato));
    }

    private void deleteSaldoItemContrato(Contrato contrato) {
        getJdbcTemplate().update(DELETE_SALDO, contrato.getId());
    }

    private void deleteMovimentoItemContrato(Contrato contrato) {
        getJdbcTemplate().update(DELETE_MOVIMENTO, contrato.getId());
    }

    public void deleteSaldoAndMovimento(Contrato contrato) {
        deleteMovimentoItemContrato(contrato);
        deleteSaldoItemContrato(contrato);
    }

    private Object[] getAtributosMovimentoInsert(MovimentoItemContrato mov) {
        Object[] objetos = new Object[13];
        objetos[0] = mov.getId();
        objetos[1] = mov.getItemContrato().getId();
        objetos[2] = mov.getIndice();
        objetos[3] = mov.getDataMovimento();
        objetos[4] = mov.getIdOrigem();
        objetos[5] = mov.getIdMovimento();
        objetos[6] = mov.getOrigem().name();
        objetos[7] = mov.getTipo().name();
        objetos[8] = mov.getSubTipo().name();
        objetos[9] = mov.getOperacao().name();
        objetos[10] = mov.getQuantidade();
        objetos[11] = mov.getValorUnitario();
        objetos[12] = mov.getValorTotal();
        return objetos;
    }

    private Object[] getAtributosSaldoInsert(SaldoItemContrato saldo) {
        Object[] objetos = new Object[10];
        objetos[0] = saldo.getId();
        objetos[1] = saldo.getItemContrato().getId();
        objetos[2] = saldo.getIdOrigem();
        objetos[3] = saldo.getDataSaldo();
        objetos[4] = saldo.getOrigem().name();
        objetos[5] = saldo.getSubTipo().name();
        objetos[6] = saldo.getOperacao().name();
        objetos[7] = saldo.getSaldoQuantidade();
        objetos[8] = saldo.getValorUnitario();
        objetos[9] = saldo.getSaldoValor();
        return objetos;
    }

    private Object[] getAtributosSaldoUpdate(SaldoItemContrato saldo) {
        Object[] objetos = new Object[4];
        objetos[0] = saldo.getDataSaldo();
        objetos[1] = saldo.getSaldoQuantidade();
        objetos[2] = saldo.getSaldoValor();
        objetos[3] = saldo.getId();
        return objetos;
    }

    private Object[] getAtributosContratoUpdate(Contrato contrato) {
        Object[] objetos = new Object[7];
        objetos[0] = contrato.getValorAtualContrato();
        objetos[1] = contrato.getVariacaoAtualContrato();
        objetos[2] = contrato.getSaldoAtualContrato();
        objetos[3] = contrato.getTerminoVigenciaAtual();
        objetos[4] = contrato.getTerminoExecucaoAtual();
        objetos[5] = contrato.getReprocessado();
        objetos[6] = contrato.getId();
        return objetos;
    }

    private Object[] getAtributosExecucaoContratoUpdate(ExecucaoContrato execucaoContrato) {
        Object[] objetos = new Object[5];
        objetos[0] = execucaoContrato.getIdOrigem();
        objetos[1] = execucaoContrato.getOrigem().name();
        objetos[2] = execucaoContrato.getOperacaoOrigem().name();
        objetos[3] = execucaoContrato.getReprocessada();
        objetos[4] = execucaoContrato.getId();
        return objetos;
    }
}
