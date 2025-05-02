package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.FonteDespesaOrcSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 23/10/14
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcFonteDespesaOrcDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcFonteDespesaOrcDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistir(FonteDespesaORC fonte) {
        List<FonteDespesaORC> fontes = Lists.newArrayList();
        fontes.add(fonte);
        getJdbcTemplate().batchUpdate(FonteDespesaOrcSetter.SQL_INSERT, new FonteDespesaOrcSetter(fontes, geradorDeIds));
    }

    public void atualizar(FonteDespesaORC mov) {
        Object[] objetos = new Object[4];

        objetos[0] = mov.getDespesaORC().getId();
        objetos[1] = mov.getProvisaoPPAFonte().getId();
        if (mov.getGrupoOrcamentario() != null) {
            objetos[2] = mov.getGrupoOrcamentario().getId();
        } else {
            objetos[2] = null;
        }
        objetos[3] = mov.getId();
        getJdbcTemplate().update(FonteDespesaOrcSetter.SQL_UPDATE, objetos);
    }
}
