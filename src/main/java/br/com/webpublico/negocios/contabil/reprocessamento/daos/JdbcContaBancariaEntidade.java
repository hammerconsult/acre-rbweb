package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ContaBancariaEntidadeSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcContaBancariaEntidade extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcContaBancariaEntidade(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private SingletonGeradorId geradorDeIds;


    public ContaBancariaEntidade atualizarStatusArquivoGerado(ContaBancariaEntidade contaBancariaEntidade, Long idRev) {
        if (Util.isNotNull(contaBancariaEntidade)) {
            getJdbcTemplate().batchUpdate(ContaBancariaEntidadeSetter.SQL_INSERT_AUD, new ContaBancariaEntidadeSetter(contaBancariaEntidade, contaBancariaEntidade.getId(), idRev, 1));
        }
        return contaBancariaEntidade;
    }

}
