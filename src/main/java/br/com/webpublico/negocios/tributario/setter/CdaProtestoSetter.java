package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.CdaRemessaProtesto;
import br.com.webpublico.entidadesauxiliares.VOCdaProtesto;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class CdaProtestoSetter implements BatchPreparedStatementSetter {

    private final List<VOCdaProtesto> cdasProtesto;
    private final List<CdaRemessaProtesto> cdasSalvas;
    private final boolean isCdasVO;

    public CdaProtestoSetter(List<VOCdaProtesto> cdasProtesto,
                             List<CdaRemessaProtesto> cdasSalvas, boolean isCdasVO) {
        this.cdasProtesto = cdasProtesto;
        this.cdasSalvas = cdasSalvas;
        this.isCdasVO = isCdasVO;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        if (isCdasVO) {
            VOCdaProtesto voCda = cdasProtesto.get(i);
            ps.setString(1, voCda.getSituacaoParcela());
            ps.setString(2, voCda.getNossoNumero());
            if (voCda.getIdCda() != null) {
                ps.setLong(3, voCda.getIdCda());
            } else {
                ps.setNull(3, Types.NUMERIC);
            }
        } else {
            CdaRemessaProtesto cdaRemessa = cdasSalvas.get(i);
            ps.setString(1, cdaRemessa.getSituacaoProtesto());
            ps.setString(2, cdaRemessa.getNossoNumero());
            ps.setLong(3, cdaRemessa.getCda().getId());
        }
    }

    @Override
    public int getBatchSize() {
        return isCdasVO ? cdasProtesto.size() : cdasSalvas.size();
    }
}
