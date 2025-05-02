package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.tributario.setter.RevisaoAuditoriaSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;

@Repository
public class JdbcRevisaoAuditoria extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcRevisaoAuditoria(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public RevisaoAuditoria gerarRevisaoAuditoria(String ip, UsuarioSistema usuarioSistema) {
        RevisaoAuditoria rev = new RevisaoAuditoria();
        rev.setId(geradorDeIds.getProximoId());
        rev.setDataHora(new Date());
        rev.setUsuario(usuarioSistema.getLogin());
        rev.setIp(ip);

        getJdbcTemplate().batchUpdate(RevisaoAuditoriaSetter.SQL_INSERT, new RevisaoAuditoriaSetter(rev));
        return rev;
    }

}
