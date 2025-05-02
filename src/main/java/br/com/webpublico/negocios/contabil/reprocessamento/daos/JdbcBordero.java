package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Bordero;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoSetter;
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
public class JdbcBordero extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcBordero(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private JdbcBorderoPagamento jdbcBorderoPagamento;
    @Autowired
    private JdbcBorderoPagamentoExtra jdbcBorderoPagamentoExtra;
    @Autowired
    private JdbcBorderoLiberacaoFinanceira jdbcBorderoLiberacaoFinanceira;
    @Autowired
    private JdbcBorderoTransferenciaFinanceira jdbcBorderoTransferenciaFinanceira;
    @Autowired
    private JdbcBorderoTransferenciaMesmaUnidade jdbcBorderoTransferenciaMesmaUnidade;

    @Autowired
    private JdbcRevisaoAuditoria jdbcRevisaoAuditoria;

    public void salvarNovoOB(Bordero bordero, String ip, UsuarioSistema usuarioSistema) {
        if (Util.isNotNull(bordero)) {
            bordero.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().batchUpdate(BorderoSetter.SQL_INSERT, new BorderoSetter(bordero));
            RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(ip, usuarioSistema);
            getJdbcTemplate().batchUpdate(BorderoSetter.SQL_INSERT_AUD, new BorderoSetter(bordero, rev.getId(), 0));
            jdbcBorderoPagamento.salvarNovo(bordero, rev.getId());
            jdbcBorderoPagamentoExtra.salvarNovo(bordero, rev.getId());
            jdbcBorderoLiberacaoFinanceira.salvarNovo(bordero, rev.getId());
            jdbcBorderoTransferenciaFinanceira.salvarNovo(bordero, rev.getId());
            jdbcBorderoTransferenciaMesmaUnidade.salvarNovo(bordero, rev.getId());
        }
    }

    public void atualizarBordero(Bordero bordero, Long idRev) {
        if (Util.isNotNull(bordero)) {
            Object[] objetos = new Object[13];
            objetos[0] = bordero.getSituacao().name();
            objetos[1] = bordero.getDataGeracao();
            objetos[2] = bordero.getDataGeracaoArquivo();
            objetos[3] = bordero.getDataDebito();
            objetos[4] = bordero.getSubConta().getId();
            objetos[5] = bordero.getUnidadeOrganizacional().getId();
            objetos[6] = bordero.getObservacao();
            objetos[7] = bordero.getBanco().getId();
            objetos[8] = bordero.getValor();
            objetos[9] = bordero.getQntdPagamentos();
            objetos[10] = bordero.getExercicio().getId();
            objetos[11] = bordero.getSequenciaArquivo();
            objetos[12] = bordero.getId();
            getJdbcTemplate().update(BorderoSetter.SQL_UPDATE, objetos);
            getJdbcTemplate().batchUpdate(BorderoSetter.SQL_INSERT_AUD, new BorderoSetter(bordero, idRev, 1));
        }
    }

    public void atualizarSituacao(Bordero bordero, String ip, UsuarioSistema usuarioSistema, Long idRev) {
        if (Util.isNotNull(bordero)) {
            Object[] objetos = new Object[2];
            objetos[0] = bordero.getSituacao().name();
            objetos[1] = bordero.getId();
            getJdbcTemplate().update(BorderoSetter.SQL_UPDATE_SITUACAO, objetos);
            getJdbcTemplate().batchUpdate(BorderoSetter.SQL_INSERT_AUD, new BorderoSetter(bordero, idRev, 1));
        }
    }

}
