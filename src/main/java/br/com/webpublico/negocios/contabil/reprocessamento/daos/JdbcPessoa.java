package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PessoaRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcPessoa extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcPessoa(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Pessoa buscarPessoaPorId(Long pessoa_id) {
        String sql = "select p.id, p.dataregistro, p.email, p.homepage, p.contacorrentecontribuinte_id, p.nacionalidade_id, " +
            " p.situacaocadastralpessoa, p.migracaochave, p.unidadeorganizacional_id, p.motivo, p.bloqueado, p.classepessoa, " +
            " p.unidadeexterna_id, p.observacao, p.enderecoprincipal_id, p.profissao_id, p.telefoneprincipal_id, " +
            " p.contacorrenteprincipal_id, p.detentorarquivocomposicao_id, p.arquivo_id, p.codigocnaebi, p.receitabrutacprb, " +
            " pf.cpf as cpf, pj.cnpj as cnpj " +
            " from pessoa p" +
            " left join pessoafisica pf on pf.id = p.id" +
            " left join pessoajuridica pj on pj.id = p.id where p.id = ?";

        List<Pessoa> pessoa = getJdbcTemplate().query(sql, new Object[]{pessoa_id}, new PessoaRowMapper());
        if (!pessoa.isEmpty()) {
            return pessoa.get(0);
        } else {
            return null;
        }
    }
}
