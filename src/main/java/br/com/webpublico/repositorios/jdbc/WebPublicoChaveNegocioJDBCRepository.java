package br.com.webpublico.repositorios.jdbc;

import br.com.webpublico.entidades.ChaveNegocio;
import br.com.webpublico.entidades.EntidadeWebPublicoComChave;
import br.com.webpublico.repositorios.jdbc.util.NextIDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Franco
 * @since 21/10/2015 14:59
 */
public abstract class WebPublicoChaveNegocioJDBCRepository<T extends EntidadeWebPublicoComChave> extends WebPublicoJDBCRepository<T> {

    protected Map<ChaveNegocio, T> mapaPorChaveNegocio;

    public WebPublicoChaveNegocioJDBCRepository(Connection conn) {
        super(conn);
    }

    public WebPublicoChaveNegocioJDBCRepository(Connection conn, RevisaoAuditoriaJDBCRepository revisaoAuditoriaJDBCRepository) {
        super(conn, revisaoAuditoriaJDBCRepository);
    }

    public WebPublicoChaveNegocioJDBCRepository(Connection conn, NextIDFactory nextIDFactory) {
        super(conn, nextIDFactory);
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementUpdate() {
        return null;
    }

    @Override
    protected T preencherRegistroSimples(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    protected void preencherCamposUpdate(T registro, PreparedStatement ps) throws SQLException {

    }

    public Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Pré-carrega um mapa com todos os registros já existentes no banco, ordenados pela sua chave de negócio,
     * facilitando e agilizando comparação de existência ou apenas a recuperação para associação
     *
     * @return Mapa com os Registros existentes no banco, com os campos relevantes preenchidos (ao menos ID e a chave de
     * negócio)
     */
    protected Map<ChaveNegocio, T> preCarregarMapaChaveNegocio() {
        getLogger().trace("Pré-Carregando Mapa por Chave de Negócio");
        List<T> lista = preCarregarLista();
        afterPreCarga(lista);
        Map<ChaveNegocio, T> retorno = new HashMap<>();
        for (T registro : lista) {
            retorno.put(registro.getChaveNegocio(), registro);
        }
        return retorno;
    }

    @Override
    protected T getRegistroExistente(T registro) {
        if (registro == null) {
            return null;
        }
        if (mapaPorChaveNegocio == null) {
            mapaPorChaveNegocio = preCarregarMapaChaveNegocio();
        }
        return mapaPorChaveNegocio.get(registro.getChaveNegocio());
    }

    @Override
    protected void afterInsert(T registro) {
        mapaPorChaveNegocio.put(registro.getChaveNegocio(), registro);
    }

    public final T buscarPorChaveNegocio(ChaveNegocio chave) {
        return buscarPorChaveNegocio(chave, true);
    }

    public final T buscarPorChaveNegocio(ChaveNegocio chave, boolean exibirMensagemNaoEncontrado) {
        if (chave == null) {
            return null;
        }
        if (mapaPorChaveNegocio == null) {
            mapaPorChaveNegocio = preCarregarMapaChaveNegocio();
        }
        if (exibirMensagemNaoEncontrado && !mapaPorChaveNegocio.containsKey(chave)) {
            getLogger().warn("Nenhum Registro Encontrado com a Chave [{}]", chave);
        }
        return mapaPorChaveNegocio.get(chave);
    }

    @Override
    protected void adicionarRegistroNoMapa(T registro) {
        super.adicionarRegistroNoMapa(registro);
        if (mapaPorChaveNegocio != null) {
            mapaPorChaveNegocio.put(registro.getChaveNegocio(), registro);
        }
    }
}
