package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.interfaces.GeradorDeID;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SingletonGeradorId implements GeradorDeID {

    private static final Logger logger = LoggerFactory.getLogger(SingletonGeradorId.class);
    private List<Long> ids;

    @Autowired
    private JdbcCalculoIptuDAO dao;

    public synchronized Long getProximoId() {
        if (ids == null || ids.isEmpty()) {
            ids = getIds(2000);
        }
        return ids.remove(0);
    }

    public List<Long> getIds(int quantidade) {
        String consultaId = "select hibernate_sequence.nextval from ( select level from dual connect by level <= ?)";
        return dao.getJdbcTemplate().queryForList(consultaId, new Object[]{quantidade}, Long.class);
    }
}
