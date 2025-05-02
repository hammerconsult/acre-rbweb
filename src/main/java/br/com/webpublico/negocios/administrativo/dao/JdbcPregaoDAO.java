package br.com.webpublico.negocios.administrativo.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.licitacao.ItemPregaoLanceVencedor;
import br.com.webpublico.enums.administrativo.TipoSetterJdbc;
import br.com.webpublico.negocios.administrativo.setter.*;
import br.com.webpublico.negocios.tributario.setter.RevisaoAuditoriaSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;

@Repository
public class JdbcPregaoDAO extends JdbcDaoSupport implements Serializable {

    public static final String SQL_INSERT_REVISAO = " INSERT INTO REVISAOAUDITORIA (ID, DATAHORA, IP, USUARIO) VALUES (?, ?, ?, ?) ";
    @Autowired
    private SingletonGeradorId singletonGeradorId;

    @Autowired
    public JdbcPregaoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    // pregao
    public synchronized void insertPregao(Pregao pregao, String ip, UsuarioSistema usuarioSistema) {
        pregao.setId(singletonGeradorId.getProximoId());
        getJdbcTemplate().batchUpdate(PregaoSetter.SQL_INSERT, new PregaoSetter(pregao, TipoSetterJdbc.INSERT));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(PregaoSetter.SQL_INSERT_AUDITORIA, new PregaoSetter(pregao, rev.getId(), 0L, TipoSetterJdbc.INSERT));
    }

    public synchronized void updatePregao(Pregao pregao, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(PregaoSetter.SQL_UPDATE, new PregaoSetter(pregao, TipoSetterJdbc.UPDATE));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(PregaoSetter.SQL_INSERT_AUDITORIA, new PregaoSetter(pregao, rev.getId(), 1L, TipoSetterJdbc.INSERT));
    }



    // item pregao
    public synchronized void insertItemPregao(ItemPregao itemPregao, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(ItemPregaoSetter.SQL_INSERT, new ItemPregaoSetter(itemPregao, TipoSetterJdbc.INSERT));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(ItemPregaoSetter.SQL_INSERT_AUDITORIA, new ItemPregaoSetter(itemPregao, rev.getId(), 0L, TipoSetterJdbc.INSERT));
    }

    public synchronized void updateItemPregao(ItemPregao itemPregao, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(ItemPregaoSetter.SQL_UPDATE, new ItemPregaoSetter(itemPregao, TipoSetterJdbc.UPDATE));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(ItemPregaoSetter.SQL_INSERT_AUDITORIA, new ItemPregaoSetter(itemPregao, rev.getId(), 1L, TipoSetterJdbc.INSERT));
    }


    // item pregao item processo
    public synchronized void insertItemPregaoItemProcesso(ItemPregaoItemProcesso itemPregaoItemProcesso, String ip, UsuarioSistema usuarioSistema) {
        itemPregaoItemProcesso.setId(singletonGeradorId.getProximoId());
        getJdbcTemplate().batchUpdate(ItemPregaoItemProcessoSetter.SQL_INSERT, new ItemPregaoItemProcessoSetter(itemPregaoItemProcesso, TipoSetterJdbc.INSERT));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(ItemPregaoItemProcessoSetter.SQL_INSERT_AUDITORIA, new ItemPregaoItemProcessoSetter(itemPregaoItemProcesso, rev.getId(), 0L, TipoSetterJdbc.INSERT));
    }

    //item pregao lote processo
    public synchronized void insertItemPregaoLoteProcesso(ItemPregaoLoteProcesso itemPregaoLoteProcesso, String ip, UsuarioSistema usuarioSistema) {
        itemPregaoLoteProcesso.setId(singletonGeradorId.getProximoId());
        getJdbcTemplate().batchUpdate(ItemPregaoLoteProcessoSetter.SQL_INSERT, new ItemPregaoLoteProcessoSetter(itemPregaoLoteProcesso, TipoSetterJdbc.INSERT));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(ItemPregaoLoteProcessoSetter.SQL_INSERT_AUDITORIA, new ItemPregaoLoteProcessoSetter(itemPregaoLoteProcesso, rev.getId(), 0L, TipoSetterJdbc.INSERT));
    }


    //item pregao lote item processo
    public synchronized void insertItemPregaoLoteItemProcesso(ItemPregaoLoteItemProcesso itemPregaoLoteItemProcesso, String ip, UsuarioSistema usuarioSistema) {
        itemPregaoLoteItemProcesso.setId(singletonGeradorId.getProximoId());
        getJdbcTemplate().batchUpdate(ItemPregaoLoteItemProcessoSetter.SQL_INSERT, new ItemPregaoLoteItemProcessoSetter(itemPregaoLoteItemProcesso, TipoSetterJdbc.INSERT));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(ItemPregaoLoteItemProcessoSetter.SQL_INSERT_AUDITORIA, new ItemPregaoLoteItemProcessoSetter(itemPregaoLoteItemProcesso, rev.getId(), 0L, TipoSetterJdbc.INSERT));
    }

    public synchronized void updateItemPregaoLoteItemProcesso(ItemPregaoLoteItemProcesso itemPregaoLoteItemProcesso, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(ItemPregaoLoteItemProcessoSetter.SQL_UPDATE, new ItemPregaoLoteItemProcessoSetter(itemPregaoLoteItemProcesso, TipoSetterJdbc.UPDATE));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(ItemPregaoLoteItemProcessoSetter.SQL_INSERT_AUDITORIA, new ItemPregaoLoteItemProcessoSetter(itemPregaoLoteItemProcesso, rev.getId(), 1L, TipoSetterJdbc.INSERT));
    }

    //rodada
    public synchronized void insertRodadaPregao(RodadaPregao rodadaPregao, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(RodadaPregaoSetter.SQL_INSERT, new RodadaPregaoSetter(rodadaPregao, TipoSetterJdbc.INSERT));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(RodadaPregaoSetter.SQL_INSERT_AUDITORIA, new RodadaPregaoSetter(rodadaPregao, rev.getId(), 0L, TipoSetterJdbc.INSERT));
    }

    public synchronized void updateRodadaPregao(RodadaPregao rodadaPregao, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(RodadaPregaoSetter.SQL_UPDATE, new RodadaPregaoSetter(rodadaPregao, TipoSetterJdbc.UPDATE));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(RodadaPregaoSetter.SQL_INSERT_AUDITORIA, new RodadaPregaoSetter(rodadaPregao, rev.getId(), 1L, TipoSetterJdbc.INSERT));
    }

    public synchronized void deleteRodadaPregao(RodadaPregao rodadaPregao, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(RodadaPregaoSetter.SQL_DELETE, new RodadaPregaoSetter(rodadaPregao, TipoSetterJdbc.DELETE));
        rodadaPregao.setItemPregao(null);
        rodadaPregao.setNumero(null);
        rodadaPregao.setJustificativaExclusao(null);
        rodadaPregao.setObservacao(null);
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(RodadaPregaoSetter.SQL_INSERT_AUDITORIA, new RodadaPregaoSetter(rodadaPregao, rev.getId(), 2L, TipoSetterJdbc.INSERT));
    }


    //lance
    public synchronized void insertLancePregao(LancePregao lancePregao, String ip, UsuarioSistema usuarioSistema) {
        lancePregao.setId(singletonGeradorId.getProximoId());
        getJdbcTemplate().batchUpdate(LancePregaoSetter.SQL_INSERT, new LancePregaoSetter(lancePregao, TipoSetterJdbc.INSERT));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(LancePregaoSetter.SQL_INSERT_AUDITORIA, new LancePregaoSetter(lancePregao, rev.getId(), 0L, TipoSetterJdbc.INSERT));
    }

    public synchronized void updateLancePregao(LancePregao lancePregao, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(LancePregaoSetter.SQL_UPDATE, new LancePregaoSetter(lancePregao, TipoSetterJdbc.UPDATE));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(LancePregaoSetter.SQL_INSERT_AUDITORIA, new LancePregaoSetter(lancePregao, rev.getId(), 1L, TipoSetterJdbc.INSERT));
    }

    public synchronized void deleteLancePregao(LancePregao lancePregao, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(LancePregaoSetter.SQL_DELETE, new LancePregaoSetter(lancePregao, TipoSetterJdbc.DELETE));
        lancePregao.setStatusLancePregao(null);
        lancePregao.setRodadaPregao(null);
        lancePregao.setPropostaFornecedor(null);
        lancePregao.setValor(null);
        lancePregao.setJustificativaCancelamento(null);
        lancePregao.setPercentualDesconto(null);
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(LancePregaoSetter.SQL_INSERT_AUDITORIA, new LancePregaoSetter(lancePregao, rev.getId(), 2L, TipoSetterJdbc.INSERT));
    }

    //item Pregão Lance Vencedor
    public synchronized void insertItemPregaoLanceVencedor(ItemPregaoLanceVencedor itemPregaoLanceVencedor, String ip, UsuarioSistema usuarioSistema) {
        itemPregaoLanceVencedor.setId(singletonGeradorId.getProximoId());
        getJdbcTemplate().batchUpdate(ItemPregaoLanceVencedorSetter.SQL_INSERT, new ItemPregaoLanceVencedorSetter(itemPregaoLanceVencedor, TipoSetterJdbc.INSERT));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(ItemPregaoLanceVencedorSetter.SQL_INSERT_AUDITORIA, new ItemPregaoLanceVencedorSetter(itemPregaoLanceVencedor, rev.getId(), 0L, TipoSetterJdbc.INSERT));
    }

    public synchronized void updateItemPregaoLanceVencedor(ItemPregaoLanceVencedor itemPregaoLanceVencedor, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(ItemPregaoLanceVencedorSetter.SQL_UPDATE, new ItemPregaoLanceVencedorSetter(itemPregaoLanceVencedor, TipoSetterJdbc.UPDATE));
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(ItemPregaoLanceVencedorSetter.SQL_INSERT_AUDITORIA, new ItemPregaoLanceVencedorSetter(itemPregaoLanceVencedor, rev.getId(), 1L, TipoSetterJdbc.INSERT));
    }

    public synchronized void deleteItemPregaoLanceVencedor(ItemPregaoLanceVencedor itemPregaoLanceVencedor, String ip, UsuarioSistema usuarioSistema) {
        getJdbcTemplate().batchUpdate(ItemPregaoLanceVencedorSetter.SQL_DELETE, new ItemPregaoLanceVencedorSetter(itemPregaoLanceVencedor, TipoSetterJdbc.DELETE));
        itemPregaoLanceVencedor.setItemPregao(null);
        itemPregaoLanceVencedor.setLancePregao(null);
        itemPregaoLanceVencedor.setStatus(null);
        itemPregaoLanceVencedor.setValor(null);
        itemPregaoLanceVencedor.setPercentualDesconto(null);
        RevisaoAuditoria rev = gerarRevisaoAuditoria(ip, usuarioSistema);
        getJdbcTemplate().batchUpdate(ItemPregaoLanceVencedorSetter.SQL_INSERT_AUDITORIA, new ItemPregaoLanceVencedorSetter(itemPregaoLanceVencedor, rev.getId(), 2L, TipoSetterJdbc.INSERT));
    }

    private RevisaoAuditoria gerarRevisaoAuditoria(String ip, UsuarioSistema usuarioSistema) {
        RevisaoAuditoria rev = new RevisaoAuditoria();
        rev.setId(singletonGeradorId.getProximoId());
        rev.setDataHora(new Date());
        rev.setUsuario(usuarioSistema.getLogin());
        rev.setIp(ip);

        getJdbcTemplate().batchUpdate(SQL_INSERT_REVISAO, new RevisaoAuditoriaSetter(rev));
        return rev;
    }
}
