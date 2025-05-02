package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.DesdobramentoEmpenho;
import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.DesdobramentoEmpenhoSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.EmpenhoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;


@Repository
public class JdbcDesdobramentoEmpenhoDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcDesdobramentoEmpenhoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistir(DesdobramentoEmpenho desdobramentoEmpenho) {
        List<DesdobramentoEmpenho> obj = Lists.newArrayList();
        obj.add(desdobramentoEmpenho);
        getJdbcTemplate().batchUpdate(DesdobramentoEmpenhoSetter.SQL_INSERT, new DesdobramentoEmpenhoSetter(obj, geradorDeIds));
    }

    public void atualizar(DesdobramentoEmpenho obj) {
        Object[] objetos = new Object[8];

        objetos[0] = obj.getEmpenho().getId();
        objetos[1] = obj.getConta().getId();
        objetos[2] = obj.getEventoContabil().getId();
        objetos[3] = obj.getValor();
        objetos[4] = obj.getSaldo();
        objetos[5] = obj.getDesdobramentoObrigacaoPagar() != null ? obj.getDesdobramentoObrigacaoPagar().getId() : null;
        objetos[6] = obj.getDesdobramentoEmpenho() != null ? obj.getDesdobramentoEmpenho().getId() : null;
        objetos[7] = obj.getId();

        getJdbcTemplate().update(DesdobramentoEmpenhoSetter.SQL_UPDATE, objetos);
    }
}
