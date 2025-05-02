package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.CancelamentoIsencaoCadastroImobiliario;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.setter.CancelamentoIsencaoIPTUSetter;
import br.com.webpublico.negocios.tributario.setter.RevisaoAuditoriaSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;

@Repository
public class JdbcIsencaoCadastroImobiliarioDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;
    @Autowired
    private JdbcCalculoIptuDAO jdbcCalculoIptuDAO;

    @Autowired
    public JdbcIsencaoCadastroImobiliarioDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }


    public void inserirCancelamentoIsencaoIPTU(CancelamentoIsencaoCadastroImobiliario cancelamento) {
        cancelamento.setId(geradorDeIds.getProximoId());

        String insertCancelamento = "INSERT INTO CANCELAMENTOISENCAOCI " +
            "(ID, USUARIOSISTEMA_ID, DATAOPERACAO, MOTIVO, ISENCAO_ID) " +
            "VALUES (?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(insertCancelamento, new CancelamentoIsencaoIPTUSetter(cancelamento));

        String insertRevisao = "INSERT INTO revisaoauditoria " +
            "(ID, DATAHORA, IP, USUARIO) " +
            "VALUES (?,?,?,?)";

        RevisaoAuditoria rev = criarRevisaoAuditoria(cancelamento);
        getJdbcTemplate().batchUpdate(insertRevisao, new RevisaoAuditoriaSetter(rev));

        String insertCancelamentoAud = "INSERT INTO CANCELAMENTOISENCAOCI_AUD " +
            "(ID, USUARIOSISTEMA_ID, DATAOPERACAO, MOTIVO, ISENCAO_ID, REV, REVTYPE) " +
            "VALUES (?,?,?,?,?, ?, ?)";
        getJdbcTemplate().batchUpdate(insertCancelamentoAud, new CancelamentoIsencaoIPTUSetter(cancelamento, rev.getId(), 1l));

        if (cancelamento.getIsencao() != null) {
            jdbcCalculoIptuDAO.encerrarVigenciaAndMudaSituacaoParaCanceladoIsencaoIPTU(cancelamento.getIsencao().getId(), cancelamento.getDataOperacao());
        }

    }

    private RevisaoAuditoria criarRevisaoAuditoria(CancelamentoIsencaoCadastroImobiliario cancelamento) {
        RevisaoAuditoria rev = new RevisaoAuditoria();
        rev.setId(geradorDeIds.getProximoId());
        rev.setDataHora(new Date());
        rev.setUsuario(cancelamento.getUsuarioSistema().getLogin());
        rev.setIp(SistemaFacade.obtemIp());
        return rev;
    }


}
