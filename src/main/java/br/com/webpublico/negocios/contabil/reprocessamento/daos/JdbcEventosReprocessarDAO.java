package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.EventosReprocessar;
import br.com.webpublico.entidades.LancamentoContabil;
import br.com.webpublico.entidades.ParametroEvento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.EventosReprocessarSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ItemParametroEventoSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.LancamentoContabilSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ParametroEventoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 28/10/14
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcEventosReprocessarDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcEventosReprocessarDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }
    public synchronized void persistir(EventosReprocessar eventosReprocessar) {
        List<EventosReprocessar> itens = new ArrayList<EventosReprocessar>();
        itens.add(eventosReprocessar);
        getJdbcTemplate().update(EventosReprocessarSetter.SQL_INSERT, new EventosReprocessarSetter(itens, geradorDeIds));
    }
}
