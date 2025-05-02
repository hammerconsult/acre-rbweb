package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidadesauxiliares.VOCdaProtesto;
import com.google.common.base.Strings;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

@Repository("protestoDAO")
public class JDBCProtestoDAO extends AbstractJdbc {

    private static final String SITUACAO_PROTESTADA = "Protestado";

    public void atualizarSituacaoProtestoParcelas(List<VOCdaProtesto> cdasProtesto, String usuario, boolean correcao) {
        String update = "  update cdaremessaprotesto set situacaoprotesto = ?, ultimaatualizacao = current_date, " +
            " usuarioultimaatualizacao = ? " +
            " where id = ? ";

        getJdbcTemplate().batchUpdate(update, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setString(1, cdasProtesto.get(i).getSituacaoParcela());
                preparedStatement.setString(2, !Strings.isNullOrEmpty(usuario) ? usuario : "Atualização feita automaticamente");
                preparedStatement.setLong(3, cdasProtesto.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return cdasProtesto.size();
            }
        });
        for (VOCdaProtesto voCdaProtesto : cdasProtesto) {
            inserirLogsCDAs(voCdaProtesto, usuario, "Situação atualizada para: " + voCdaProtesto.getSituacaoParcela());
        }

        List<VOCdaProtesto> protestadas = cdasProtesto.stream()
            .filter(cda -> SITUACAO_PROTESTADA.equals(cda.getSituacaoParcela())).collect(Collectors.toList());
        if (!protestadas.isEmpty()) {
            atualizarDebitoProtestadoParcelas(protestadas, false);
        }

        if (correcao) {
            List<VOCdaProtesto> naoProtestadas = cdasProtesto.stream()
                .filter(cda -> !SITUACAO_PROTESTADA.equals(cda.getSituacaoParcela())).collect(Collectors.toList());
            if (!naoProtestadas.isEmpty()) {
                atualizarDebitoProtestadoParcelas(naoProtestadas, true);
            }
        }
    }

    public void inserirLogsCDAs(List<VOCdaProtesto> cdasProtesto, String usuario, String log) {
        for (VOCdaProtesto voCdaProtesto : cdasProtesto) {
            inserirLogsCDAs(voCdaProtesto, usuario, log);
        }
    }

    private void inserirLogsCDAs(VOCdaProtesto cdaProtesto, String usuario, String log) {
        String insertSql = " insert into logcdaremessaprotesto (id, cdaremessaprotesto_id, dataregistro, usuario, log) " +
            " values (hibernate_sequence.nextval, ?, current_date, ?, ?) ";

        getJdbcTemplate().batchUpdate(insertSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setLong(1, cdaProtesto.getId());
                preparedStatement.setString(2, !Strings.isNullOrEmpty(usuario) ? usuario : "Atualização feita automaticamente");
                preparedStatement.setString(3, log);
            }

            @Override
            public int getBatchSize() {
                return 1;
            }
        });
    }

    public void atualizarDebitoProtestadoParcelas(List<VOCdaProtesto> cdasProtesto, boolean reverterProtesto) {
        String update = "  update parcelavalordivida set debitoprotestado = ?, dataprotesto = ? " +
            "where id in (select pvd.id " +
            "                from cdaremessaprotesto cr " +
            "               inner join certidaodividaativa cda on cda.id = cr.cda_id " +
            "               inner join itemcertidaodividaativa icda on icda.certidao_id = cda.id " +
            "               inner join iteminscricaodividaativa iida on iida.id = icda.iteminscricaodividaativa_id " +
            "               inner join valordivida vd on vd.calculo_id = iida.id " +
            "               inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "             where cr.nossonumero = ?)  ";
        getJdbcTemplate().batchUpdate(update, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setBoolean(1, !reverterProtesto);
                if (reverterProtesto) {
                    preparedStatement.setNull(2, Types.DATE);
                } else {
                    preparedStatement.setDate(2, new Date(System.currentTimeMillis()));
                }
                preparedStatement.setString(3, cdasProtesto.get(i).getNossoNumero());
            }

            @Override
            public int getBatchSize() {
                return cdasProtesto.size();
            }
        });
    }

}
