package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ContaAuxiliarDetalhadaRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ContaAuxiliarDetalhadaSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ContaSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcContaAuxiliarDetalhadaDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcContaAuxiliarDetalhadaDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public synchronized ContaAuxiliarDetalhada persistir(ContaAuxiliarDetalhada contaAuxiliarDetalhada) {
        Long proximoId = geradorDeIds.getProximoId();
        getJdbcTemplate().batchUpdate(ContaSetter.SQL_INSERT, new ContaSetter(contaAuxiliarDetalhada, proximoId));
        getJdbcTemplate().batchUpdate(ContaAuxiliarDetalhadaSetter.SQL_INSERT, new ContaAuxiliarDetalhadaSetter(contaAuxiliarDetalhada, proximoId));
        return buscarContaAuxiliarDetalhada(proximoId);
    }

    public ContaAuxiliarDetalhada buscarContaAuxiliarDetalhada(Long idContaAuxiliar) {
        String sql = "SELECT c.id, " +
            "                c.ATIVA, " +
            "                c.CODIGO, " +
            "                c.DATAREGISTRO, " +
            "                c.DESCRICAO, " +
            "                c.FUNCAO, " +
            "                c.PERMITIRDESDOBRAMENTO, " +
            "                c.RUBRICA, " +
            "                c.TIPOCONTACONTABIL, " +
            "                c.PLANODECONTAS_ID, " +
            "                c.SUPERIOR_ID, " +
            "                c.DTYPE, " +
            "                c.EXERCICIO_ID, " +
            "                c.CATEGORIA, " +
            "                c.CODIGOTCE, " +
            "                c.codigoSICONFI, " +
            "                obj.CONTADEDESTINACAO_ID," +
            "                obj.conta_id," +
            "                obj.exercicioAtual_id," +
            "                obj.unidadeOrganizacional_id, " +
            "                obj.subSistema, " +
            "                obj.dividaConsolidada, " +
            "                obj.es, " +
            "                obj.classificacaoFuncional, " +
            "                obj.hashContaAuxiliarDetalhada, " +
            "                obj.contaContabil_id, " +
            "                obj.tipoContaAuxiliar_id, " +
            "                obj.exercicioOrigem_id, " +
            "                obj.CODIGOCO " +
            "     FROM ContaAuxiliarDetalhada obj " +
            "    inner join conta c on c.id = obj.id " +
            "    where obj.id = ? ";
        List<ContaAuxiliarDetalhada> contasAuxiliares = getJdbcTemplate().query(sql, new Object[]{idContaAuxiliar}, new ContaAuxiliarDetalhadaRowMapper());
        if (!contasAuxiliares.isEmpty()) {
            return contasAuxiliares.get(0);
        } else {
            return null;
        }
    }
}
