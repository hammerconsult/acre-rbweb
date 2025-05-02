package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.CadastroMalaDiretaIPTU;
import br.com.webpublico.entidades.MalaDiretaRBTransPermissao;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MalaDiretaRBTransPermissaoSetter implements BatchPreparedStatementSetter {

    MalaDiretaRBTransPermissao malaDiretaRBTransPermissao;
    Long id;

    public MalaDiretaRBTransPermissaoSetter(MalaDiretaRBTransPermissao malaDiretaRBTransPermissao, Long id) {
        this.id = id;
        this.malaDiretaRBTransPermissao = malaDiretaRBTransPermissao;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        malaDiretaRBTransPermissao.setId(id);
        ps.setLong(1, malaDiretaRBTransPermissao.getId());
        ps.setLong(2, malaDiretaRBTransPermissao.getMalaDiretaRBTrans().getId());
        ps.setLong(3, malaDiretaRBTransPermissao.getPermissaoTransporte().getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
