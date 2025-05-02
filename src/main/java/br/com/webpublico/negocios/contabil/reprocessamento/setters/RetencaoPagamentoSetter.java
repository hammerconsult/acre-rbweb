package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.RetencaoPgto;
import br.com.webpublico.util.Util;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
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
public class RetencaoPagamentoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_AUD = "insert into retencaopgto_aud (numero, valor, dataretencao, saldo, complementohistorico, subconta_id, fontederecursos_id,\n" +
        "                                                        contaextraorcamentaria_id, usuariosistema_id, pagamento_id, unidadeorganizacional_id,\n" +
        "          efetivado, estornado, pessoa_id, classecredor_id, tipoconsignacao, pagamentoestorno_id, rev, revtype, id)\n" +
        "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String SQL_UPDATE_ALL = "update retencaopgto\n" +
        "set numero = ?,\n" +
        "    valor = ?,\n" +
        "    dataretencao = ?,\n" +
        "    saldo = ?,\n" +
        "    complementohistorico = ?,\n" +
        "    subconta_id = ?,\n" +
        "    fontederecursos_id = ?,\n" +
        "    contaextraorcamentaria_id = ?,\n" +
        "    usuariosistema_id = ?,\n" +
        "    pagamento_id = ?,\n" +
        "    unidadeorganizacional_id = ?,\n" +
        "    efetivado = ?,\n" +
        "    estornado = ?,\n" +
        "    pessoa_id = ?,\n" +
        "    classecredor_id = ?,\n" +
        "    tipoconsignacao = ?,\n" +
        "    pagamentoestorno_id = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT = "insert into retencaopgto (numero, valor, dataretencao, saldo, complementohistorico, subconta_id, fontederecursos_id,\n" +
        "                          contaextraorcamentaria_id, usuariosistema_id, pagamento_id, unidadeorganizacional_id,\n" +
        "                          efetivado, estornado, pessoa_id, classecredor_id, tipoconsignacao, pagamentoestorno_id, id)\n" +
        "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String SQL_DELETE = "delete retencaopgto where id = ?";
    private final RetencaoPgto retencaoPgto;
    private final Long id;
    private Long idRev;

    private Integer typeRev;

    public RetencaoPagamentoSetter(RetencaoPgto retencaoPgto, Long id) {
        this.retencaoPgto = retencaoPgto;
        this.id = id;
    }

    public RetencaoPagamentoSetter(RetencaoPgto retencaoPgto, Long id, Long idRev, Integer typeRev) {
        this.retencaoPgto = retencaoPgto;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }


    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        if (Util.isNotNull(retencaoPgto.getNumero())) {
            ps.setString(1, retencaoPgto.getNumero());
        } else {
            ps.setNull(1, Types.VARCHAR);
        }
        ps.setBigDecimal(2, retencaoPgto.getValor());
        if (Util.isNotNull(retencaoPgto.getDataRetencao())) {
            ps.setDate(3, new Date(retencaoPgto.getDataRetencao().getTime()));
        } else {
            ps.setNull(3, Types.DATE);
        }
        if (Util.isNotNull(retencaoPgto.getSaldo())) {
            ps.setBigDecimal(4, retencaoPgto.getSaldo());
        } else {
            ps.setNull(4, Types.DECIMAL);
        }
        if (Util.isNotNull(retencaoPgto.getComplementoHistorico())) {
            ps.setString(5, retencaoPgto.getComplementoHistorico());
        } else {
            ps.setNull(5, Types.VARCHAR);
        }
        if (Util.isNotNull(retencaoPgto.getSubConta())) {
            ps.setLong(6, retencaoPgto.getSubConta().getId());
        } else {
            ps.setNull(6, Types.LONGNVARCHAR);
        }
        ps.setLong(7, retencaoPgto.getFonteDeRecursos().getId());
        ps.setLong(8, retencaoPgto.getContaExtraorcamentaria().getId());
        ps.setLong(9, retencaoPgto.getUsuarioSistema().getId());
        if (Util.isNotNull(retencaoPgto.getPagamento())) {
            ps.setLong(10, retencaoPgto.getPagamento().getId());
        } else {
            ps.setNull(10, Types.LONGNVARCHAR);
        }
        ps.setLong(11, retencaoPgto.getUnidadeOrganizacional().getId());
        if (Util.isNotNull(retencaoPgto.getEfetivado())) {
            ps.setBoolean(12, retencaoPgto.getEfetivado());
        } else {
            ps.setNull(12, Types.BOOLEAN);
        }
        if (Util.isNotNull(retencaoPgto.getEstornado())) {
            ps.setBoolean(13, retencaoPgto.getEstornado());
        } else {
            ps.setNull(13, Types.BOOLEAN);
        }
        ps.setLong(14, retencaoPgto.getPessoa().getId());
        ps.setLong(15, retencaoPgto.getClasseCredor().getId());
        if (Util.isNotNull(retencaoPgto.getTipoConsignacao())) {
            ps.setString(16, retencaoPgto.getTipoConsignacao().name());
        } else {
            ps.setNull(16, Types.VARCHAR);
        }
        if (Util.isNotNull(retencaoPgto.getPagamentoEstorno())) {
            ps.setLong(17, retencaoPgto.getPagamentoEstorno().getId());
        } else {
            ps.setNull(17, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(idRev)) {
            ps.setLong(18, idRev);
            ps.setInt(19, typeRev);
            ps.setLong(20, id);
        }else {
            ps.setLong(18, id);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
