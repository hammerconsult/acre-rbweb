package br.com.webpublico.negocios.administrativo.dao;


import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.AgrupadorMovimentoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;

@Repository
public class JdbcMovimentoBloqueioBem extends JdbcDaoSupport implements Serializable {
    public static final String INSERT_MOVIMENTO_BLOQUEIO = " insert into movimentobloqueiobem (id, bem_id, datamovimento, operacaomovimento, " +
        "agrupador, situacaoeventobem, movimentoum, movimentodois, movimentotres) " +
        " values (?, ?, ?, ?, ?, ?, ?, ?,?) ";
    public static final String DELETE_MOVIMENTO_BLOQUEIO = " delete from movimentobloqueiobem where agrupador = ? and bem_id = ? ";
    public static final String INSERIR_BEM_PORTAL = " DECLARE " +
        "    novoId number := ?; " +
        " begin " +
        "    insert into entidadeportaltransparencia values(novoId, 'NAO_PUBLICADO', 'BEM'); " +
        "    insert into bemportal values(novoId, ?); " +
        " end;";
    public static final String SQL_BUSCAR_BEM_PORTAL = " select bp.id " +
        " from bemportal bp " +
        "    inner join entidadeportaltransparencia ept on ept.id = bp.id " +
        " where bp.bem_id = ? and ept.situacao = 'NAO_PUBLICADO' and rownum = 1";

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcMovimentoBloqueioBem(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void deletarMovimentoBloqueioBem(AgrupadorMovimentoBem agrupador, Number idBem) {
        getJdbcTemplate().update(DELETE_MOVIMENTO_BLOQUEIO, agrupador.name(), idBem);
    }

    public void insertMovimentoBloqueioBem(ConfigMovimentacaoBem config, Number idBem, SituacaoEventoBem situacaoEvento) {
        getJdbcTemplate().update(INSERT_MOVIMENTO_BLOQUEIO, getAtributosMovimento(config, idBem, situacaoEvento));
    }

    public void insertBemPortal(Number idBem) {
        if (idBem != null && !hasBemAguardandoPublicacao(idBem)) {
            Object[] objetos = new Object[2];
            objetos[0] = geradorDeIds.getProximoId();
            objetos[1] = idBem;
            getJdbcTemplate().update(INSERIR_BEM_PORTAL, objetos);
        }
    }

    public boolean hasBemAguardandoPublicacao(Number idBem) {
        try {
            Number idBemPortal = getJdbcTemplate().queryForObject(SQL_BUSCAR_BEM_PORTAL, Number.class, idBem);
            return idBemPortal != null;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private Object[] getAtributosMovimento(ConfigMovimentacaoBem config, Number idBem, SituacaoEventoBem situacaoEvento) {
        Object[] objetos = new Object[9];
        objetos[0] = geradorDeIds.getProximoId();
        objetos[1] = idBem;
        objetos[2] = new Date();
        objetos[3] = config.getOperacaoMovimentacaoBem().name();
        objetos[4] = config.getOperacaoMovimentacaoBem().getAgrupadorMovimentoBem().name();
        objetos[5] = situacaoEvento.name();
        objetos[6] = config.getBloqueio().getMovimentoTipoUm();
        objetos[7] = config.getBloqueio().getMovimentoTipoDois();
        objetos[8] = config.getBloqueio().getMovimentoTipoTres();
        return objetos;
    }
}
