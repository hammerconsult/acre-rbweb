package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.ItemParametroEvento;
import br.com.webpublico.entidades.LancamentoContabil;
import br.com.webpublico.entidades.ParametroEvento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ItemParametroEventoSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.LancamentoContabilSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ParametroEventoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 23/10/14
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcLancamentoContabilDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;


    @Autowired
    public JdbcLancamentoContabilDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }


    public void persistirParametroEvento(ParametroEvento parametroEvento) {
        getJdbcTemplate().batchUpdate(ParametroEventoSetter.SQL_INSERT, new ParametroEventoSetter(Arrays.asList(parametroEvento), geradorDeIds));
        getJdbcTemplate().batchUpdate(ItemParametroEventoSetter.SQL_INSERT, new ItemParametroEventoSetter(parametroEvento.getItensParametrosEvento(), geradorDeIds));
    }


    public void persistirLancamentoContabil(LancamentoContabil lancamentoContabil) {
        getJdbcTemplate().batchUpdate(LancamentoContabilSetter.SQL_INSERT, new LancamentoContabilSetter(Arrays.asList(lancamentoContabil), geradorDeIds));
    }
}
