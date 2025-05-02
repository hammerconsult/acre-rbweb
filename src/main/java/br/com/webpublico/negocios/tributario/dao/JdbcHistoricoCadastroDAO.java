package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.Cadastro;
import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Historico;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.tributario.setter.CadastroImobiliarioSetter;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;

@Repository("historicoCadastroDAO")
public class JdbcHistoricoCadastroDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcHistoricoCadastroDAO(DataSource ds) {
        setDataSource(ds);
    }

    public void inserirAuditoria(Long idRevisao, Historico historico, CadastroImobiliario cadastroImobiliario) {
        inserirRevisaoAuditoria(idRevisao);
        inserirAuditoriaHistorico(idRevisao, historico);
        inserirAuditoriaCadastro(idRevisao, historico.getCadastro());
        inserirAuditoriaCadastroImobiliario(idRevisao, cadastroImobiliario);
    }

    private void inserirAuditoriaHistorico(Long idRevisao, Historico historico) {
        String insert = " insert into historico_aud " +
            " (id, dataregistro, motivo, sequencia, solicitante, " +
            "  datasolicitacao, cadastro_id, rev, revtype, digitador_id) " +
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().update(insert,
            historico.getId(), historico.getDataRegistro(), historico.getMotivo(), historico.getSequencia(),
            historico.getSolicitante(), historico.getDataSolicitacao(), historico.getCadastro().getId(), idRevisao,
            0, (historico.getDigitador() != null ? historico.getDigitador().getId() : null));
    }

    private void inserirRevisaoAuditoria(Long idRevisao) {
        String insert = " insert into revisaoauditoria (id, datahora, ip, usuario) " +
            " values (?, ?, ?, ?) ";
        UsuarioSistema usuarioSistema = Util.recuperarUsuarioCorrente();
        getJdbcTemplate().update(insert, idRevisao, new Date(), Util.obterIpUsuario(), (usuarioSistema != null ? usuarioSistema.getLogin() : null));
    }

    private void inserirAuditoriaCadastro(Long idRevisao, Cadastro cadastro) {
        String insert = " insert into cadastro_aud (id, rev, revtype, migracaochave, responsavelpelocadastro_id) " +
            " values(?, ?, ?, ?, ?) ";

        getJdbcTemplate().update(insert, cadastro.getId(), idRevisao, 1,
            cadastro.getMigracaoChave(), (cadastro.getResponsavelPeloCadastro() != null ? cadastro.getResponsavelPeloCadastro().getId() : null));
    }

    public void inserirAuditoriaCadastroImobiliario(Long idRevisao, CadastroImobiliario cadastroImobiliario) {
        if (cadastroImobiliario != null) {
            String insert = " insert into cadastroimobiliario_aud " +
                " (id, rev, revtype, codigo, folharegistro, inscricaocadastral, livroregistro, matricularegistro, atolegalisencao_id, " +
                " cartorio_id, lote_id, observacoes_id, processo_id, ativo, datainativacao, motivoinativacao, areatotalconstruida, " +
                " dataisencao, tipoisencao_id, complementoendereco, imune, tipomatricula, dataregistro, datatitulo, numerodotitulo, " +
                " unidadeexterna_id, unidadeorganizacional_id, identificacaopatrimonial, autonoma, numero) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            getJdbcTemplate().update(insert, new CadastroImobiliarioSetter(idRevisao, cadastroImobiliario));
        }
    }
}
