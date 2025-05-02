package br.com.webpublico.negocios.rh.dao;

import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;

@Repository(value = "registroESocialDAO")
public class JdbcRegistroESocialDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcRegistroESocialDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void insert(RegistroESocial registro, Long idEmpregador) {
        insertRegistroEsocial(registro, idEmpregador);
    }

    private void insertRegistroEsocial(RegistroESocial registro, Long idEmpregador) {
        String sql = "insert into RegistroESocial (id, identificador, tipoarquivoesocial, situacao, " +
            "empregador_id, observacao, dataregistro, xml, idesocial, dataintegracao, recibo, codigoresposta," +
            "descricaoresposta, operacao, codigoocorrencia, localizacao, mesapuracao, anoapuracao, identificadorwp, classewp, descricao) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        getJdbcTemplate().update(sql,
            geradorDeIds.getProximoId(),
            registro.getIdentificador(),
            registro.getTipoArquivoESocial() != null ? registro.getTipoArquivoESocial().name() : null,
            registro.getSituacao() != null ? registro.getSituacao().name() : null,
            idEmpregador,
            registro.getObservacao(),
            registro.getDataRegistro() != null ? new Date(registro.getDataRegistro().getTime()) : null,
            registro.getXml(),
            registro.getIdESocial(),
            registro.getDataIntegracao() != null ? new Date(registro.getDataIntegracao().getTime()) : null,
            registro.getRecibo(),
            registro.getCodigoResposta(),
            registro.getDescricaoResposta(),
            registro.getOperacao() != null ? registro.getOperacao().name() : null,
            registro.getCodigoOcorrencia(),
            registro.getLocalizacao(),
            registro.getMesApuracao(),
            registro.getAnoApuracao(),
            registro.getIdentificadorWP(),
            registro.getClasseWP() != null ? registro.getClasseWP().name() : null,
            registro.getDescricao()

        );
    }
}

