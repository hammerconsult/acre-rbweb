package br.com.webpublico.negocios.administrativo.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.administrativo.setter.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 16/10/14
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcReducaoValorBemDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcReducaoValorBemDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public synchronized void persistirEstadosBem(List<EstadoBem> estados) {
        getJdbcTemplate().batchUpdate(EstadoBemSetter.SQL_INSERT_ESTADO_BEM, new EstadoBemSetter(estados, geradorDeIds));
    }

    public synchronized void persistirReducoesValorBem(List<ReducaoValorBem> reducoes) {
        getJdbcTemplate().batchUpdate(EventoBemSetter.SQL_INSERT_EVENTO_BEM, new EventoBemSetter(reducoes, geradorDeIds));
        getJdbcTemplate().batchUpdate(ReducaoValorBemSetter.SQL_INSERT_REDUCAO_VALOR_BEM, new ReducaoValorBemSetter(reducoes));
    }

    public synchronized void persistirMensagensLog(List<MsgLogReducaoOuEstorno> mensagens) {
        getJdbcTemplate().batchUpdate(MensagemLogReducaoValorBemSetter.SQL_INSERT_MENSAGEM, new MensagemLogReducaoValorBemSetter(mensagens, geradorDeIds));
    }

    public synchronized void persistirBemNaoAplicavel(List<ReducaoValorBemNaoAplicavel> bensNaoAplicavel) {
        getJdbcTemplate().batchUpdate(ReducaoValorBemNaoAplicavelSetter.SQL_INSERT_REDUCAO_VALOR_BEM_NAO_APLICAVEL, new ReducaoValorBemNaoAplicavelSetter(bensNaoAplicavel, geradorDeIds));
    }

    public synchronized void persistirBemResidual(List<ReducaoValorBemResidual> bensResidual) {
        getJdbcTemplate().batchUpdate(ReducaoValorBemResidualSetter.SQL_INSERT_REDUCAO_VALOR_BEM_RESIDUAL, new ReducaoValorBemResidualSetter(bensResidual, geradorDeIds));
    }

    public synchronized void persistirLoteEstornoReducaoValorBem(LoteEstornoReducaoValorBem loteEstornoReducaoValorBem) {
        getJdbcTemplate().batchUpdate(LoteEstornoReducaoValorBemSetter.SQL_INSERT_LOTE_ESTORNO_REDUCAO_VALOR_BEM, new LoteEstornoReducaoValorBemSetter(loteEstornoReducaoValorBem, geradorDeIds));
    }

    public synchronized void persistirEstornosReducaoValorBem(List<EstornoReducaoValorBem> estornos) {
        getJdbcTemplate().batchUpdate(EventoBemSetter.SQL_INSERT_EVENTO_BEM, new EventoBemSetter(estornos, geradorDeIds));
        getJdbcTemplate().batchUpdate(EstornoReducaoValorBemSetter.SQL_INSERT_ESTORNO, new EstornoReducaoValorBemSetter(estornos));
    }

    public synchronized void atualizarReducaoValorBem(Long idReducao) {
        getJdbcTemplate().batchUpdate(UpdateReducaoValorBemSetter.SQL_UPDATE_REDUCAO_VALOR_BEM, new UpdateReducaoValorBemSetter(idReducao));
    }

    public synchronized void persistirLoteReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem) {
        getJdbcTemplate().batchUpdate(LoteReducaoValorBemSetter.SQL_INSERT_LOTE_REDUCAO_VALOR_BEM, new LoteReducaoValorBemSetter(loteReducaoValorBem, geradorDeIds));
    }

    public synchronized void persistirLogReducaoOuEstorno(LogReducaoOuEstorno logReducaoOuEstorno) {
        getJdbcTemplate().batchUpdate(LogReducaoOuEstornoSetter.SQL_INSERT_LOG_REDUCAO_OU_ESTORNO, new LogReducaoOuEstornoSetter(logReducaoOuEstorno, geradorDeIds));
    }

    public synchronized void atualizarLoteReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem) {
        getJdbcTemplate().batchUpdate(UpdateLoteReducaoValorBemSetter.SQL_UPDATE_LOTE_REDUCAO_VALOR_BEM, new UpdateLoteReducaoValorBemSetter(loteReducaoValorBem));
    }

    public synchronized void persistirReducoesValorBemContabil(List<ReducaoValorBemContabil> reducoes) {
        getJdbcTemplate().batchUpdate(ReducaoValorBemContabilSetter.SQL_INSERT_REDUCAO_VALOR_BEM_CONTABIL, new ReducaoValorBemContabilSetter(reducoes, geradorDeIds));
    }

    public synchronized void atualizarReducaoValorBemContabil(ReducaoValorBemContabil reducaoValorBemContabil) {
        getJdbcTemplate().batchUpdate(UpdateReducaoValorBemContabilSetter.SQL_UPDATE_REDUCAO_VALOR_BEM_CONTABIL, new UpdateReducaoValorBemContabilSetter(reducaoValorBemContabil));
    }

    public synchronized void persistirReducoesValorBemEstornoContabil(List<ReducaoValorBemEstornoContabil> reducoes) {
        getJdbcTemplate().batchUpdate(ReducaoValorBemEstornoContabilSetter.SQL_INSERT_REDUCAO_VALOR_BEM_CONTABIL, new ReducaoValorBemEstornoContabilSetter(reducoes, geradorDeIds));
    }

    public synchronized void atualizarReducaoValorBemEstornoContabil(ReducaoValorBemEstornoContabil reducaoValorBemEstornoContabil) {
        getJdbcTemplate().batchUpdate(UpdateReducaoValorBemEstornoContabilSetter.SQL_UPDATE_REDUCAO_VALOR_BEM_ESTORNO_CONTABIL, new UpdateReducaoValorBemEstornoContabilSetter(reducaoValorBemEstornoContabil));
    }
}
