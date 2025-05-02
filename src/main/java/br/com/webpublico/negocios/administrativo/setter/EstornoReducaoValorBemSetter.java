package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.EstornoReducaoValorBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 30/10/14
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class EstornoReducaoValorBemSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_ESTORNO = "INSERT INTO ESTORNOREDUCAOVALORBEM (ID, LOTEESTORNOREDUCAOVALORBEM_ID, REDUCAOVALORBEM_ID) VALUES (?, ?, ?)";

    private List<EstornoReducaoValorBem> estornos;

    public EstornoReducaoValorBemSetter(List<EstornoReducaoValorBem> estornos) {
        this.estornos = estornos;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        EstornoReducaoValorBem estorno = estornos.get(i);

        ps.setLong(1, estorno.getId());
        ps.setLong(2, estorno.getLoteEstornoReducaoValorBem().getId());
        ps.setLong(3, estorno.getReducaoValorBem().getId());
    }

    @Override
    public int getBatchSize() {
        return this.estornos.size();
    }
}
