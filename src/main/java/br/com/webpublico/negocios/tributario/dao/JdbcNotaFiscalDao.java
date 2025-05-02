package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.negocios.tributario.rowmapper.NotaFiscalRowMapper;
import br.com.webpublico.negocios.tributario.setter.DeclaracaoMensalServicoSetter;
import br.com.webpublico.negocios.tributario.setter.DeclaracaoMensalServicoUpdateSetter;
import br.com.webpublico.negocios.tributario.setter.NotaDeclaradaSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.nfse.domain.DeclaracaoMensalServico;
import br.com.webpublico.nfse.domain.NotaDeclarada;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository(value = "notaFiscalDao")
public class JdbcNotaFiscalDao extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcNotaFiscalDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void updateNumeroNotaFiscal(Long idNotaFiscal, Long numero) {
        String sql = " UPDATE NOTAFISCAL SET NUMERO = ? WHERE ID = ? ";
        getJdbcTemplate().update(sql, new Object[]{numero, idNotaFiscal});
    }

    public List<NotaFiscalRowMapper> buscarNotasPorPrestadorAndEmissao(Long prestador,
                                                                       Date emissaoInicial) {
        String sql = " SELECT NF.ID AS ID," +
            "                 NF.NUMERO AS NUMERO" +
            "  FROM NOTAFISCAL NF " +
            " WHERE NF.PRESTADOR_ID = :PRESTADOR_ID " +
            "   AND TRUNC(NF.EMISSAO) >= :EMISSAO " +
            " ORDER BY NF.EMISSAO ASC, NF.NUMERO ASC ";
        return getJdbcTemplate().query(sql,
            new Object[]{prestador, DataUtil.dataSemHorario(emissaoInicial)},
            new NotaFiscalRowMapper());
    }

    public void atualizarVwNotasFiscais() {
        getJdbcTemplate().update(" call dbms_snapshot.refresh('vwnotasfiscais') ");
    }
}
