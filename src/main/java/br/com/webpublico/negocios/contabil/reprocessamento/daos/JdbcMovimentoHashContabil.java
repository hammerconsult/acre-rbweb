package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.contabil.conciliacaocontabil.MovimentoHashContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.MovimentoHashContabilRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.MovimentoHashContabilSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcMovimentoHashContabil extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcMovimentoHashContabil(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private SingletonGeradorId geradorDeIds;

    public MovimentoHashContabil salvar(MovimentoHashContabil movimentoHashContabil) {
        if (Util.isNotNull(movimentoHashContabil)) {
            movimentoHashContabil.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().batchUpdate(MovimentoHashContabilSetter.SQL_INSERT, new MovimentoHashContabilSetter(movimentoHashContabil));
        }
        return movimentoHashContabil;
    }

    public MovimentoHashContabil buscarMovimentos(MovimentoHashContabil movimento) {
        Date data = movimento.getData();
        String hash = movimento.getHash();
        String tipo = movimento.getTipo().name();
        if (Util.isNotNull(data) && Util.isNotNull(hash) && Util.isNotNull(tipo)) {
            String sql = "select mov.*  from MovimentoHashContabil mov where mov.data  <= ? and mov.tipo = ? and mov.hash = ? AND rownum <= 1 order by mov.data desc ";
            List<MovimentoHashContabil> retencoes = getJdbcTemplate().query(sql, new Object[]{
                data, tipo, hash
            }, new MovimentoHashContabilRowMapper());
            if (!retencoes.isEmpty()) {
                return retencoes.get(0);
            } else {
                return null;
            }
        }
        return null;
    }
}
