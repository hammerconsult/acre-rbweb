package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.LoteReducaoValorBem;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * User: Wellington
 * Date: 27/06/17
 */
public class UpdateLoteReducaoValorBemSetter implements BatchPreparedStatementSetter {
    public static final String
        SQL_UPDATE_LOTE_REDUCAO_VALOR_BEM = " UPDATE LOTEREDUCAOVALORBEM SET DATA = ?, " +
        "USUARIOSISTEMA_ID = ?, TIPOREDUCAO = ?, CODIGO = ?, LOGREDUCAOOUESTORNO_ID = ?, TIPOBEM = ?, UNIDADEORCAMENTARIA_ID = ?, " +
        "DETENTORARQUIVOCOMPOSICAO_ID = ?, DATALANCAMENTO = ?, SITUACAO = ? WHERE ID = ? ";

    private final LoteReducaoValorBem loteReducaoValorBem;

    public UpdateLoteReducaoValorBemSetter(LoteReducaoValorBem loteReducaoValorBem) {
        this.loteReducaoValorBem = loteReducaoValorBem;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setDate(1, new java.sql.Date(loteReducaoValorBem.getData().getTime()));
        ps.setLong(2, loteReducaoValorBem.getUsuarioSistema().getId());
        ps.setString(3, loteReducaoValorBem.getTipoReducao().name());
        ps.setLong(4, loteReducaoValorBem.getCodigo());
        if (loteReducaoValorBem.getLogReducaoOuEstorno() != null && loteReducaoValorBem.getLogReducaoOuEstorno().getId() != null) {
            ps.setLong(5, loteReducaoValorBem.getLogReducaoOuEstorno().getId());
        } else {
            ps.setNull(5, Types.NULL);
        }
        ps.setString(6, loteReducaoValorBem.getTipoBem().name());
        ps.setLong(7, loteReducaoValorBem.getUnidadeOrcamentaria().getId());
        if (loteReducaoValorBem.getDetentorArquivoComposicao() != null) {
            ps.setLong(8, loteReducaoValorBem.getDetentorArquivoComposicao().getId());
        } else {
            ps.setNull(8, Types.NULL);
        }
        ps.setDate(9, new java.sql.Date(loteReducaoValorBem.getDataLancamento().getTime()));
        ps.setString(10, loteReducaoValorBem.getSituacao().name());
        ps.setLong(11, loteReducaoValorBem.getId());

    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
