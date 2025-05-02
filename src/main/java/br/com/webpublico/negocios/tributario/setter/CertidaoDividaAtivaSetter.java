package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.CertidaoDividaAtiva;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class CertidaoDividaAtivaSetter implements BatchPreparedStatementSetter {

    private final List<CertidaoDividaAtiva> certidoes;
    private final List<Long> ids;

    public CertidaoDividaAtivaSetter(List<CertidaoDividaAtiva> certidoes, List<Long> ids) {
        this.certidoes = certidoes;
        this.ids = ids;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        CertidaoDividaAtiva certidao = certidoes.get(i);
        certidao.setId(ids.get(i));
        ps.setLong(1, certidao.getId());
        ps.setLong(2, certidao.getNumero());
        ps.setDate(3, new Date(certidao.getDataCertidao().getTime()));
        if (certidao.getFuncionarioAssinante() != null) {
            ps.setLong(4, certidao.getFuncionarioAssinante().getId());
        } else {
            ps.setNull(4, Types.NUMERIC);
        }
        if (certidao.getFuncionarioEmissao() != null) {
            ps.setLong(5, certidao.getFuncionarioEmissao().getId());
        } else {
            ps.setNull(5, Types.NUMERIC);
        }
        ps.setLong(6, certidao.getExercicio().getId());
        ps.setString(7, certidao.getSituacaoCertidaoDA().name());
        if (certidao.getCadastro() != null) {
            ps.setLong(8, certidao.getCadastro().getId());
        } else {
            ps.setNull(8, Types.NUMERIC);
        }
        if (certidao.getPessoa() != null) {
            ps.setLong(9, certidao.getPessoa().getId());
        } else {
            ps.setNull(9, Types.NUMERIC);
        }
        ps.setString(10, certidao.getSituacaoJudicial().name());
    }

    @Override
    public int getBatchSize() {
        return certidoes.size();
    }
}
