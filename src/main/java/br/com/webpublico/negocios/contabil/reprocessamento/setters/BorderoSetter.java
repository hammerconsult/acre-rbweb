package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.Bordero;
import br.com.webpublico.util.Util;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public class BorderoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "insert into bordero (id, situacao, datageracao, datageracaoarquivo, datadebito, subconta_id, unidadeorganizacional_id,\n" +
        "                     observacao, banco_id, valor, qntdpagamentos, exercicio_id, sequenciaarquivo)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "update bordero set situacao = ?, datageracao = ?, datageracaoarquivo = ?, datadebito = ?, subconta_id =?, \n" +
        "                   unidadeorganizacional_id = ?, observacao = ?, banco_id = ?, valor = ?, qntdpagamentos = ?,\n" +
        "                   exercicio_id = ?, sequenciaarquivo = ? where id = ?";

    public static final String SQL_UPDATE_SITUACAO = "update bordero set situacao = ? where id = ?";
    public static final String SQL_INSERT_AUD = "insert into bordero_aud (id, situacao, datageracao, datageracaoarquivo, datadebito, subconta_id, unidadeorganizacional_id,\n" +
        "                                         observacao, banco_id, valor, qntdpagamentos, exercicio_id, sequenciaarquivo, rev, revtype )\n" +
        "                     values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final Bordero bordero;
    private Long idRev;
    private Integer typeRev;

    public BorderoSetter(Bordero bordero) {
        this.bordero = bordero;
    }

    public BorderoSetter(Bordero bordero, Long idRev, Integer typeRev) {
        this.bordero = bordero;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, bordero.getId());
        ps.setString(2, bordero.getSituacao().name());
        ps.setDate(3, new java.sql.Date(bordero.getDataGeracao().getTime()));
        if (Util.isNotNull(bordero.getDataGeracaoArquivo())) {
            ps.setDate(4, new java.sql.Date(bordero.getDataGeracaoArquivo().getTime()));
        } else {
            ps.setNull(4, Types.DATE);
        }
        if (Util.isNotNull(bordero.getDataDebito())) {
            ps.setDate(5, new java.sql.Date(bordero.getDataDebito().getTime()));
        } else {
            ps.setNull(5, Types.DATE);
        }
        ps.setLong(6, bordero.getSubConta().getId());
        ps.setLong(7, bordero.getUnidadeOrganizacional().getId());
        if (!Util.isStringNulaOuVazia(bordero.getObservacao())) {
            ps.setString(8, bordero.getObservacao());
        } else {
            ps.setNull(8, Types.VARCHAR);
        }
        ps.setLong(9, bordero.getBanco().getId());
        ps.setBigDecimal(10, bordero.getValor());
        ps.setLong(11, bordero.getQntdPagamentos());
        ps.setLong(12, bordero.getExercicio().getId());
        ps.setString(13, bordero.getSequenciaArquivo());
        if (idRev != null) {
            ps.setLong(14, idRev);
            ps.setLong(15, typeRev);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
