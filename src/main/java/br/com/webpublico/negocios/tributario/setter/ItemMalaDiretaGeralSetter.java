package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ItemMalaDiretaGeral;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ItemMalaDiretaGeralSetter implements BatchPreparedStatementSetter {

    ItemMalaDiretaGeral itemMalaDiretaGeral;
    Long id;

    public ItemMalaDiretaGeralSetter(ItemMalaDiretaGeral itemMalaDiretaGeral, Long id) {
        this.id = id;
        this.itemMalaDiretaGeral = itemMalaDiretaGeral;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        itemMalaDiretaGeral.setId(id);
        ps.setLong(1, itemMalaDiretaGeral.getId());
        ps.setLong(2, itemMalaDiretaGeral.getMalaDiretaGeral().getId());
        if (itemMalaDiretaGeral.getDam() != null) {
            ps.setLong(3, itemMalaDiretaGeral.getDam().getId());
        } else {
            ps.setNull(3, Types.NUMERIC);
        }
        if (itemMalaDiretaGeral.getContribuinteTributario() != null) {
            if (itemMalaDiretaGeral.getContribuinteTributario().getTipoCadastroTributario().isPessoa()) {
                ps.setNull(4, Types.NUMERIC);
                ps.setLong(5, itemMalaDiretaGeral.getContribuinteTributario().getId());
            } else {
                ps.setLong(4, itemMalaDiretaGeral.getContribuinteTributario().getId());
                ps.setLong(5, itemMalaDiretaGeral.getContribuinteTributario().getIdPessoa());
            }
        }
        ps.setString(6, itemMalaDiretaGeral.getTexto());

    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
