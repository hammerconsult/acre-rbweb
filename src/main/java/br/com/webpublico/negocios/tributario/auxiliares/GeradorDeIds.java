package br.com.webpublico.negocios.tributario.auxiliares;

import br.com.webpublico.interfaces.AssitenteDoGeradorDeIds;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 19/09/13
 * Time: 08:17
 * To change this template use File | Settings | File Templates.
 */
public class GeradorDeIds {

    private final AssitenteDoGeradorDeIds assistente;
    private List<Long> ids;

    public GeradorDeIds(AssitenteDoGeradorDeIds assistente) {
        this.assistente = assistente;
    }

    public synchronized Long getProximoId() {
        if (ids == null || ids.isEmpty()) {
            ids = getIds(assistente.getQuantidade(), assistente.getDao());
        }

        return ids.remove(0);
    }

    private List<Long> getIds(int quantidade, JdbcDaoSupport dao) {
        String consultaId = "select hibernate_sequence.nextval from ( select level from dual connect by level <= ?)";
        return dao.getJdbcTemplate().queryForList(consultaId, new Object[]{quantidade}, Long.class);
    }
}
