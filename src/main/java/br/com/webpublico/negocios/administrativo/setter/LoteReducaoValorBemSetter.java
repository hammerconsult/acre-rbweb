package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.LoteReducaoValorBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * User: Wellington
 * Date: 27/06/17
 */
public class LoteReducaoValorBemSetter implements BatchPreparedStatementSetter {
    public static final String SQL_INSERT_LOTE_REDUCAO_VALOR_BEM = "INSERT INTO LOTEREDUCAOVALORBEM (ID, DATA, " +
        "USUARIOSISTEMA_ID, TIPOREDUCAO, SITUACAO, LOGREDUCAOOUESTORNO_ID, TIPOBEM, UNIDADEORCAMENTARIA_ID, DETENTORARQUIVOCOMPOSICAO_ID," +
        "QUANTIDADEDEPRECIACAO, VALORDEPRECIACAO, QUANTIDADEEXAUSTAO, VALOREXAUSTAO, QUANTIDADEAMORTIZACAO, VALORAMORTIZACAO, DATALANCAMENTO, CODIGO) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final LoteReducaoValorBem loteReducaoValorBem;
    private final SingletonGeradorId geradorDeIds;


    public LoteReducaoValorBemSetter(LoteReducaoValorBem loteReducaoValorBem, SingletonGeradorId geradorDeIds) {
        this.loteReducaoValorBem = loteReducaoValorBem;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        if (loteReducaoValorBem.getId() == null) {
            loteReducaoValorBem.setId(geradorDeIds.getProximoId());
        }
        ps.setLong(1, loteReducaoValorBem.getId());
        ps.setDate(2, new java.sql.Date(loteReducaoValorBem.getData().getTime()));
        ps.setLong(3, loteReducaoValorBem.getUsuarioSistema().getId());
        ps.setString(4, loteReducaoValorBem.getTipoReducao().name());
        ps.setString(5, loteReducaoValorBem.getSituacao().name());
        if (loteReducaoValorBem.getLogReducaoOuEstorno() != null && loteReducaoValorBem.getLogReducaoOuEstorno().getId() != null) {
            ps.setLong(6, loteReducaoValorBem.getLogReducaoOuEstorno().getId());
        }
        ps.setString(7, loteReducaoValorBem.getTipoBem().name());
        ps.setLong(8, loteReducaoValorBem.getUnidadeOrcamentaria().getId());
        if (loteReducaoValorBem.getDetentorArquivoComposicao() != null) {
            ps.setLong(9, loteReducaoValorBem.getDetentorArquivoComposicao().getId());
        } else {
            ps.setNull(9, Types.NULL);
        }
        ps.setInt(10, loteReducaoValorBem.getQuantidadeDepreciacao());
        ps.setBigDecimal(11, loteReducaoValorBem.getValorDepreciacao());
        ps.setInt(12, loteReducaoValorBem.getQuantidadeExaustao());
        ps.setBigDecimal(13, loteReducaoValorBem.getValorExaustao());
        ps.setInt(14, loteReducaoValorBem.getQuantidadeAmortizacao());
        ps.setBigDecimal(15, loteReducaoValorBem.getValorAmortizacao());
        ps.setDate(16, new java.sql.Date(loteReducaoValorBem.getDataLancamento().getTime()));
        ps.setLong(17, loteReducaoValorBem.getCodigo());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
