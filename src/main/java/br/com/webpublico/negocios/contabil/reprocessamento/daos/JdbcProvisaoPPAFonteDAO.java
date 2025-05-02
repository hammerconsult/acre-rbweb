package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.ProvisaoPPADespesa;
import br.com.webpublico.entidades.ProvisaoPPAFonte;
import br.com.webpublico.negocios.contabil.reprocessamento.rowmapper.ProvisaoPPAFonteDespesaRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ProvisaoPPAFonteSetter;
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
public class JdbcProvisaoPPAFonteDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcProvisaoPPAFonteDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistir(ProvisaoPPAFonte fonte) {
        List<ProvisaoPPAFonte> fontes = Lists.newArrayList();
        fontes.add(fonte);
        getJdbcTemplate().batchUpdate(ProvisaoPPAFonteSetter.SQL_INSERT, new ProvisaoPPAFonteSetter(fontes, geradorDeIds));
    }

    public List<ProvisaoPPAFonte> recuperar(ProvisaoPPADespesa provisaoPPADespesa) {
        return getJdbcTemplate().query(ProvisaoPPAFonteSetter.SQL_SELECT_PROVISAO, new Object[]{provisaoPPADespesa.getId()}, new ProvisaoPPAFonteDespesaRowMapper());
    }

    public void atualizar(ProvisaoPPAFonte mov) {
        Object[] objetos = new Object[11];

        objetos[0] = mov.getSomenteLeitura();
        objetos[1] = mov.getValor();
        objetos[2] = mov.getDestinacaoDeRecursos().getId();
        if (mov.getOrigem() != null) {
            objetos[3] = mov.getOrigem().getId();
        } else {
            objetos[3] = null;
        }
        objetos[4] = mov.getProvisaoPPADespesa().getId();
        objetos[5] = mov.getEsferaOrcamentaria().name();
        objetos[6] = mov.getHistoricoRazao();
        objetos[7] = mov.getHistoricoNota();
        if (mov.getEventoContabil() != null) {
            objetos[8] = mov.getEventoContabil().getId();
        } else {
            objetos[8] = null;
        }
        if (mov.getExtensaoFonteRecurso() != null) {
            objetos[9] = mov.getExtensaoFonteRecurso().getId();
        } else {
            objetos[9] = null;
        }
        objetos[10] = mov.getId();
        getJdbcTemplate().update(ProvisaoPPAFonteSetter.SQL_UPDATE, objetos);
    }
}
