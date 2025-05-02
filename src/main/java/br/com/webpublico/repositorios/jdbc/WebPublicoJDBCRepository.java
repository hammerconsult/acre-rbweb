package br.com.webpublico.repositorios.jdbc;

import br.com.webpublico.entidades.EntidadeWebPublico;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.exception.CampoObrigatorioVazioException;
import br.com.webpublico.exception.MetodoNaoImplementadoException;
import br.com.webpublico.repositorios.jdbc.statement.JDBCStatement;
import br.com.webpublico.repositorios.jdbc.util.JDBCUtil;
import br.com.webpublico.repositorios.jdbc.util.NextIDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static br.com.webpublico.repositorios.jdbc.util.JDBCUtil.stringVazia;

/**
 * @author Daniel
 * @since 26/08/2015 10:27
 */
@SuppressWarnings("unused")
public abstract class WebPublicoJDBCRepository<T extends EntidadeWebPublico> implements WebPublicoRepository<T> {

    private static final int DEFAULT_BATCH_SIZE = 1000;
    protected NextIDFactory nextIDFactory;
    private Map<JDBCStatement, PreparedStatement> statements = new HashMap<>();
    private Connection conn;
    private Map<Long, T> mapaPorID;
    private RevisaoAuditoriaJDBCRepository revisaoAuditoriaJDBCRepository;

    public WebPublicoJDBCRepository(Connection conn, NextIDFactory nextIDFactory) {
        if (!JDBCUtil.validateConnection(conn)) {
            throw new WebPublicoJDBCException("Connection inválida");
        }
        this.conn = conn;
        try {
            this.conn.setAutoCommit(false);
            this.nextIDFactory = nextIDFactory;
        } catch (SQLException ex) {
            throw new WebPublicoJDBCException("Erro Inicializando Repository", ex);
        }
    }

    public WebPublicoJDBCRepository(DataSource dataSource) throws SQLException {
        this(dataSource.getConnection());
    }

    public WebPublicoJDBCRepository(Connection conn) {
        this(conn, NextIDFactory.createInstance(conn));
    }

    public WebPublicoJDBCRepository(Connection conn, RevisaoAuditoriaJDBCRepository revisaoAuditoriaJDBCRepository) {
        this(conn, NextIDFactory.createInstance(conn));
        this.revisaoAuditoriaJDBCRepository = revisaoAuditoriaJDBCRepository;
    }

    /**
     * Executa as validações específicas para a gravação do Registro. A validação de ID preenchido é desnecessária pois
     * já é tratada automaticamente. Em caso de registro inválido, deve-se jogar uma Exception
     */
    protected void validarRegistro(T registro) {

    };

    /**
     * Retorna qual o PreparedStatement utilizado para a inserção de registros
     *
     * @return o PreparedStatement de inserção
     */
    protected abstract PreparedStatement recuperarPreparedStatementInsert();

    /**
     * Retorna qual o PreparedStatement utilizado para a atualização de registros
     *
     * @return o PreparedStatement de atualização
     */
    protected PreparedStatement recuperarPreparedStatementUpdate() {
        return null;
    }

    /**
     * Retorna qual o PreparedStatement utilizado para a a inserção da auditoria dos registros
     *
     * @return o PreparedStatement de inserção de auditoria
     */
    protected PreparedStatement recuperarPreparedStatementInsertAud() {
        return null;
    }

    /**
     * Retorna qual o PreparedStatement utilizado para a Pré-Carga de Registros
     *
     * @return O PreparedStatement que faz um SELECT normalmente sem WHERE, contendo os campos relevantes
     */
    protected abstract PreparedStatement recuperarPreparedStatementPreCarga();

    protected JDBCStatement recuperarStatementDeletePorID() {
        return null;
    }

    /**
     * Utilizado na pré-carga para preencher um registro da entidade com os dados vindos do ResultSet
     *
     * @param rs o ResultSet que contém os dados vindos do banco para a pré-carga
     * @return O Objeto preenchido para ser incluído no mapa de registros pré-carregados
     * @throws SQLException
     */
    protected abstract T preencherRegistro(ResultSet rs) throws SQLException;


    /**
     * Utilizado na pré-carga para preencher um registro da entidade com os dados do resultSet,
     * porem somente é preenchido os dados de Id e atributos pertencentes a Chave de Negocio da Entidade
     *
     * @param rs o ResultSet que contém os dados vindos do banco para a pré-carga
     * @return O Objeto preenchido para ser incluído no mapa de registros pré-carregados
     * @throws SQLException
     */
    protected abstract T preencherRegistroSimples(ResultSet rs) throws SQLException;

    /**
     * Preenche os campos do registro nos parâmetros do PreparedStatement. O registro SEMPRE virá com o ID null, então
     * é necessário preencher o ID neste método
     *
     * @param registro o Registro que contém os valores a serem gravados
     * @param ps       o PreparedStatement já pronto para receber os valores e executar o comando no banco
     */
    protected abstract void preencherCamposInsert(T registro, PreparedStatement ps) throws SQLException;

    /**
     * Preenche os campos do registro nos parâmetros do PreparedStatement.
     *
     * @param registro o Registro que contém os valores a serem gravados
     * @param ps       o PreparedStatement já pronto para receber os valores e executar o comando no banco
     */
    protected void preencherCamposUpdate(T registro, PreparedStatement ps) throws SQLException {

    };

    /**
     * Preenche os campos do registro nos parâmetros do PreparedStatement de Auditoria.
     * O registro NUNCA virá com o ID null, então
     * não é necessário preencher o ID neste método
     *
     * @param revisaoAuditoria objeto com os dados da revisão da auditoria
     * @param registro         o Registro que contém os valores a serem gravados
     * @param ps               o PreparedStatement já pronto para receber os valores e executar o comando no banco
     * @param tipo
     */
    protected void preencherCamposInsertAud(RevisaoAuditoria revisaoAuditoria, T registro, PreparedStatement ps, Long tipo) throws SQLException {
        setLongObrigatorio(ps, 1, registro.getId());
        setLongObrigatorio(ps, 2, revisaoAuditoria.getId());
        setLongObrigatorio(ps, 3, tipo);
    }

    public Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    public final List<T> preCarregarLista() {
        getLogger().trace("pré-carregando lista");
        List<T> lista = new ArrayList<>(10000);
        try (ResultSet rs = recuperarPreparedStatementPreCarga().executeQuery()) {
            while (rs.next()) {
                T registro = preencherRegistroSimples(rs);
                if (registro == null) {
                    registro = preencherRegistro(rs);
                }
                lista.add(registro);
            }
        } catch (SQLException ex) {
            getLogger().debug("Erro no método preCarregarLista() " + ex.getMessage());
            throw new WebPublicoJDBCException("Erro pré-carregando Registros", ex);
        }
        getLogger().trace("Pré-Carregado Mapa por ID");
        return lista;
    }

    private Map<Long, T> preCarregarMapaId() {
        getLogger().trace("Pré-Carregando Mapa por ID");
        List<T> lista = preCarregarLista();
        Map<Long, T> retorno = new HashMap<>();
        for (T registro : lista) {
            retorno.put(registro.getId(), registro);
        }
        return retorno;
    }

    /**
     * Método executado após o loop de pré-carga de registros por ID, necessário apenas quando alguma instrução
     * específica deve ser executada, como carregar atributos recursivos
     *
     * @param lista A lista de registros pré-carregados
     */
    protected void afterPreCarga(List<T> lista) {
    }

    /**
     * Executa o Insert sem BatchUpdates, com o comando PreparedStatement.execute()<br/>
     * É ideal para pequenas quantidades de registros, até cerca de 100 registros
     *
     * @param registro o EntidadeWebPublico que será gravado
     * @return o mesmo EntidadeWebPublico, porém com o id preenchido e qualquer outra modificação que o mesmo possa sofrer
     * durante o insert
     */
    public final T inserirDireto(T registro) {
        return insertWrapper(registro, true);
    }

    public final List<T> inserirDireto(List<T> registros) {
        for (T registro : registros) {
            insertWrapper(registro, true);
        }
        return registros;
    }

    /**
     * Executa o Update sem BatchUpdates, com o comando PreparedStatement.execute()<br/>
     * É ideal para pequenas quantidades de registros, até cerca de 100 registros
     *
     * @param registro o EntidadeWebPublico que será gravado
     * @return o mesmo EntidadeWebPublico, porém com o id preenchido e qualquer outra modificação que o mesmo possa sofrer
     * durante o update
     */
    public final T updateDireto(T registro) {
        return updateWrapper(registro, true);
    }

    public final List<T> updateDireto(List<T> registros) {
        for (T registro : registros) {
            updateWrapper(registro, true);
        }
        return registros;
    }

    /**
     * Executa o Insert usando BatchUpdates, com o comando PreparedStatement.addBatch()<br/>
     * É ideal para grandes quantidades de registros, pois manda os comandos em lotes. É necessário que se chame o método
     * PreparedStatement.executeBatch() após uma determinada quantidade de registros, para que os mesmos sejam efetivamente
     * gravados no banco.
     *
     * @param registro o EntidadeWebPublico que será gravado
     * @return o mesmo EntidadeWebPublico, já com o ID preenchido e qualquer outra modificação que o mesmo possa sofrer
     * durante o insert
     */
    public final T inserirBatch(T registro) {
        return insertWrapper(registro, false);
    }

    public final List<T> inserirBatch(List<T> registros) {
        for (T registro : registros) {
            insertWrapper(registro, false);
        }
        return registros;
    }

    /**
     * Executa o Update usando BatchUpdates, com o comando PreparedStatement.addBatch()<br/>
     * É ideal para grandes quantidades de registros, pois manda os comandos em lotes. É necessário que se chame o método
     * PreparedStatement.executeBatch() após uma determinada quantidade de registros, para que os mesmos sejam efetivamente
     * gravados no banco.
     *
     * @param registro o EntidadeWebPublico que será gravado
     * @return o mesmo EntidadeWebPublico, já com o ID preenchido e qualquer outra modificação que o mesmo possa sofrer
     * durante o update
     */
    public final T updateBatch(T registro) {
        return updateWrapper(registro, false);
    }

    public final List<T> updateBatch(List<T> registros) {
        for (T registro : registros) {
            updateWrapper(registro, false);
        }
        return registros;
    }


    protected T getRegistroExistente(T registro) {
        return null;
    }

    protected void afterInsert(T registro) throws SQLException {
    }

    protected void afterUpdate(T registro) throws SQLException {
    }

    private T insertWrapper(T registro, boolean isDirect) {
        if (registro == null) {
            getLogger().debug("Impossível Salvar Registro Null");
            return null;
        }
        if (registro.getId() != null) {
            getLogger().trace("Retornando por id != de null [{}]", registro);
            return registro;
        }
        T registroPreexistente = getRegistroExistente(registro);
        if (registroPreexistente != null) {
            getLogger().trace("Retornando por registro pre existente != null [{}]", registroPreexistente);
            return registroPreexistente;
        }
        validarRegistro(registro);
        try {

            PreparedStatement ps = recuperarPreparedStatementInsert();
            ps.clearParameters();
            preencherCamposInsert(registro, ps);
            if (isDirect) {
                ps.execute();
            } else {
                ps.addBatch();
            }
            RevisaoAuditoria revisaoAuditoria = inserirAuditoria(registro, isDirect, 0l);
            inserirFilhos(revisaoAuditoria, registro, isDirect, 0l);
            adicionarRegistroNoMapa(registro);
            afterInsert(registro);
        } catch (Exception ex) {
            getLogger().error("Erro ao insertWrapper", ex);
            throw new WebPublicoJDBCException("Erro inserindo Registro [" + registro + "]", ex);
        }
        return registro;
    }

    private RevisaoAuditoria inserirAuditoria(T registro, boolean isDirect, Long tipo) throws SQLException {
        PreparedStatement ps = recuperarPreparedStatementInsertAud();
        if (ps == null) {
            return null;
        }
        RevisaoAuditoria revisaoAuditoria = gerarRevisaoAuditoria(isDirect);
        ps.clearParameters();
        preencherCamposInsertAud(revisaoAuditoria, registro, ps, tipo);
        if (isDirect) {
            ps.execute();
        } else {
            ps.addBatch();
        }

        return revisaoAuditoria;
    }

    private RevisaoAuditoria gerarRevisaoAuditoria(boolean isDirect) {
        RevisaoAuditoria revisaoAuditoria = new RevisaoAuditoria();
        revisaoAuditoria.setDataHora(new java.util.Date());
        revisaoAuditoria.setIp("localhost");
        revisaoAuditoria.setUsuario("Migração de Dados");
        if (isDirect) {
            return revisaoAuditoriaJDBCRepository.inserirDireto(revisaoAuditoria);
        } else {
            return revisaoAuditoriaJDBCRepository.inserirBatch(revisaoAuditoria);
        }
    }

    protected void adicionarRegistroNoMapa(T registro) {
        if (mapaPorID != null) {
            mapaPorID.put(registro.getId(), registro);
        }
    }

    private T updateWrapper(T registro, boolean isDirect) {
        if (registro == null) {
            getLogger().debug("Impossível Atualizar Registro Null");
            return null;
        }
        validarRegistro(registro);
        try {
            PreparedStatement ps = recuperarPreparedStatementUpdate();
            ps.clearParameters();
            preencherCamposUpdate(registro, ps);
            if (isDirect) {
                ps.execute();
            } else {
                ps.addBatch();
            }
            RevisaoAuditoria revisaoAuditoria = inserirAuditoria(registro, isDirect, 1l);
            updateFilhos(revisaoAuditoria, registro, isDirect, 1l);
            adicionarRegistroNoMapa(registro);
            afterUpdate(registro);
        } catch (Exception ex) {
            throw new WebPublicoJDBCException("Erro atualizando Registro [" + registro + "]", ex);
        }
        return registro;
    }

    /**
     * Método opcional utilizado apenas quando a inserção de um registro requer que outros registros sejam inseridos,
     * por exemplo em Herança, onde o registro "pai" deve ser gravado antes do filho.
     * <p>
     * Este método deve apenas preencher o(s) PreparedStatement(s) necessários e executar o comando execute() ou addBatch()
     * conforme o valor do parâmetro isDirect.
     *
     * @param revisaoAuditoria
     * @param registro         O registro, já com o ID preenchido pelo método preencherCamposInsert
     * @param isDirect         informa se está sendo utilizado batch ou não
     * @param tipo
     */
    protected void inserirFilhos(RevisaoAuditoria revisaoAuditoria, T registro, boolean isDirect, Long tipo) throws SQLException {
    }

    /**
     * Método opcional utilizado apenas quando a atualização de um registro requer que outros registros sejam atualizados,
     * por exemplo em Herança, onde o registro "pai" deve ser gravado antes do filho.
     * <p>
     * Este método deve apenas preencher o(s) PreparedStatement(s) necessários e executar o comando execute() ou addBatch()
     * conforme o valor do parâmetro isDirect.
     *
     * @param revisaoAuditoria
     * @param registro         O registro, já com o ID preenchido pelo método preencherCamposInsert
     * @param isDirect         informa se está sendo utilizado batch ou não
     * @param tipo
     */
    protected void updateFilhos(RevisaoAuditoria revisaoAuditoria, T registro, boolean isDirect, Long tipo) throws SQLException {
    }

    public final T buscarPorID(ResultSet rs, String nomeCampo) throws SQLException {
        return buscarPorID(getLong(rs, nomeCampo));
    }

    public final T buscarPorID(Long id) {
        if (id == null) {
            return null;
        }
        if (mapaPorID == null) {
            mapaPorID = preCarregarMapaId();
            afterPreCarga(new ArrayList<>(mapaPorID.values()));
        }
        return mapaPorID.get(id);
    }

    public final PreparedStatement getPreparedStatement(JDBCStatement statement) {
        if (statements.containsKey(statement)) {
            return statements.get(statement);
        }
        try {
            PreparedStatement retorno = this.conn.prepareStatement(statement.getStatement());
            retorno.setFetchSize(100);
            statements.put(statement, retorno);
            return retorno;
        } catch (Exception ex) {
            throw new RuntimeException("Erro Preparando Statement [" + statement.getClass() + "." + statement.name() + "]", ex);
        }
    }

    /**
     * Executa o lote de inserts agendados pelo método inserirBatch() quando o lote estiver completo, ou for o último lote
     *
     * @param progress  A quantidade de registros no lote. O lote estará completo quanto progress for igual a @DEFAULT_BATCH_SIZE
     * @param lastBatch true caso seja o último lote. Quando lastBatch for true, o parâmetro progress é ignorado
     */
    public final void executarBatchInsert(int progress, boolean lastBatch) {
        if (progress % getDefaultBatchSize() == 0 || lastBatch) {
            executarBatchInsert();
        }
    }

    public final void executarBatchInsert() {
        try {
            recuperarPreparedStatementInsert().executeBatch();
            PreparedStatement psAuditoria = recuperarPreparedStatementInsertAud();
            if (psAuditoria != null) {
                revisaoAuditoriaJDBCRepository.recuperarPreparedStatementInsert().executeBatch();
                psAuditoria.executeBatch();
            }
            executarBatchInsertEspecifico();
        } catch (SQLException ex) {
            throw new WebPublicoJDBCException("Erro Executando Batch de Inserts", ex.getNextException() != null ? ex.getNextException() : ex);
        }
    }

    public void executarBatchInsertEspecifico() throws SQLException {
    }

    /**
     * Executa o lote de inserts agendados pelo método updateBatch() quando o lote estiver completo, ou for o último lote
     *
     * @param progress  A quantidade de registros no lote. O lote estará completo quanto progress for igual a @DEFAULT_BATCH_SIZE
     * @param lastBatch true caso seja o último lote. Quando lastBatch for true, o parâmetro progress é ignorado
     */
    public final void executarBatchUpdate(int progress, boolean lastBatch) {
        if (progress % getDefaultBatchSize() == 0 || lastBatch) {
            executarBatchUpdate();
        }
    }

    public final void executarBatchUpdate() {
        try {
            PreparedStatement psUpdate = recuperarPreparedStatementUpdate();

            if (psUpdate == null)
                return;

            psUpdate.executeBatch();
            PreparedStatement psAuditoria = recuperarPreparedStatementInsertAud();
            if (psAuditoria != null) {
                revisaoAuditoriaJDBCRepository.recuperarPreparedStatementInsert().executeBatch();
                psAuditoria.executeBatch();
            }
            executarBatchUpdateEspecifico();
        } catch (SQLException ex) {
            throw new WebPublicoJDBCException("Erro Executando Batch de Updates", ex.getNextException() != null ? ex.getNextException() : ex);
        }
    }

    public void executarBatchUpdateEspecifico() throws SQLException {

    }

    public final java.util.Date toUtilDate(java.sql.Date data) {
        if (data == null) {
            return null;
        }
        try {
            return new java.util.Date(data.getTime());
        } catch (Exception ex) {
            getLogger().debug("Erro convertendo toUtilDate", ex);
            return null;
        }
    }

    public final java.sql.Date toSQLDate(java.util.Date data) {
        if (data == null) {
            return null;
        }
        try {
            return new java.sql.Date(data.getTime());
        } catch (Exception ex) {
            getLogger().debug("Erro convertendo toSQLDate", ex);
            return null;
        }
    }

    public final java.sql.Timestamp toSQLTimestamp(java.util.Date data) {
        if (data == null) {
            return null;
        }
        try {
            return new java.sql.Timestamp(data.getTime());
        } catch (Exception ex) {
            getLogger().debug("Erro convertendo toSQLTimestamp", ex);
            return null;
        }
    }

    public final java.sql.Time toSQLTime(java.util.Date data) {
        if (data == null) {
            return null;
        }
        try {
            return new java.sql.Time(data.getTime());
        } catch (Exception ex) {
            getLogger().debug("Erro convertendo toSQLTime", ex);
            return null;
        }
    }

    private void setLong(PreparedStatement ps, int position, Long field, boolean obrigatorio) throws SQLException {
        if (field == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Long obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.NUMERIC);
            }
        } else {
            ps.setLong(position, field);
        }
    }

    private void setBytes(PreparedStatement ps, int position, byte field[], boolean obrigatorio) throws SQLException {
        if (field == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Bytes obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.BIT);
            }
        } else {
            ps.setBytes(position, field);
        }
    }

    private void setDouble(PreparedStatement ps, int position, Double field, boolean obrigatorio) throws SQLException {
        if (field == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Double obrigatório não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.NUMERIC);
            }
        } else {
            ps.setDouble(position, field);
        }
    }

    private void setBigDecimal(PreparedStatement ps, int position, BigDecimal field, boolean obrigatorio) throws SQLException {
        if (field == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo BigDecimal obrigatório não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.NUMERIC);
            }
        } else {
            ps.setBigDecimal(position, field);
        }
    }

    private void setInt(PreparedStatement ps, int position, Integer field, boolean obrigatorio) throws SQLException {
        if (field == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Integer obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.NUMERIC);
            }
        } else {
            ps.setInt(position, field);
        }
    }

    private void setDate(PreparedStatement ps, int position, java.util.Date field, boolean obrigatorio) throws SQLException {
        if (field == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Date obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.DATE);
            }
        } else {
            ps.setDate(position, toSQLDate(field));
        }
    }

    private void setTimestamp(PreparedStatement ps, int position, java.util.Date field, boolean obrigatorio) throws SQLException {
        if (field == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Date obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.TIMESTAMP);
            }
        } else {
            ps.setTimestamp(position, toSQLTimestamp(field));
        }
    }

    private void setTime(PreparedStatement ps, int position, java.util.Date field, boolean obrigatorio) throws SQLException {
        if (field == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Date obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.TIME);
            }
        } else {
            ps.setTime(position, toSQLTime(field));
        }
    }

    private void setString(PreparedStatement ps, int position, String field, boolean obrigatorio) throws SQLException {
        if (stringVazia(field)) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo String obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.VARCHAR);
            }
        } else {
            ps.setString(position, field);
        }
    }

    private void setBoolean(PreparedStatement ps, int position, Boolean field, boolean obrigatorio) throws SQLException {
        if (field == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Boolean obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.BOOLEAN);
            }
        } else {
            ps.setBoolean(position, field);
        }
    }

    private void setEnum(PreparedStatement ps, int position, Enum field, boolean obrigatorio) throws SQLException {
        if (field == null || stringVazia(field.name())) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Enum obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.VARCHAR);
            }
        } else {
            ps.setString(position, field.name());
        }
    }

    private void setEntity(PreparedStatement ps, int position, EntidadeWebPublico field, boolean obrigatorio) throws SQLException {
        if (field == null || field.getId() == null) {
            if (obrigatorio) {
                throw new CampoObrigatorioVazioException("Campo Entidade obrigatorio não pode ser Null", ps, position);
            } else {
                ps.setNull(position, Types.NUMERIC);
            }
        } else {
            ps.setLong(position, field.getId());
        }
    }

    public final void setLongObrigatorio(PreparedStatement ps, int position, Long field) throws SQLException {
        setLong(ps, position, field, true);
    }

    public final void setLongOpcional(PreparedStatement ps, int position, Long field) throws SQLException {
        setLong(ps, position, field, false);
    }

    public final void setBytesObrigatorio(PreparedStatement ps, int position, byte field[]) throws SQLException {
        setBytes(ps, position, field, true);
    }

    public final void setBytesOpcional(PreparedStatement ps, int position, byte field[]) throws SQLException {
        setBytes(ps, position, field, false);
    }

    public final void setDoubleObrigatorio(PreparedStatement ps, int position, Double field) throws SQLException {
        setDouble(ps, position, field, true);
    }

    public final void setDoubleOpcional(PreparedStatement ps, int position, Double field) throws SQLException {
        setDouble(ps, position, field, false);
    }

    public final void setBigDecimalObrigatorio(PreparedStatement ps, int position, BigDecimal field) throws SQLException {
        setBigDecimal(ps, position, field, true);
    }

    public final void setBigDecimalOpcional(PreparedStatement ps, int position, BigDecimal field) throws SQLException {
        setBigDecimal(ps, position, field, false);
    }

    public final void setIntObrigatorio(PreparedStatement ps, int position, Integer field) throws SQLException {
        setInt(ps, position, field, true);
    }

    public final void setIntOpcional(PreparedStatement ps, int position, Integer field) throws SQLException {
        setInt(ps, position, field, false);
    }

    public final void setStringObrigatorio(PreparedStatement ps, int position, String field) throws SQLException {
        setString(ps, position, field, true);
    }

    public final void setStringOpcional(PreparedStatement ps, int position, String field) throws SQLException {
        setString(ps, position, field, false);
    }

    public final void setBooleanObrigatorio(PreparedStatement ps, int position, Boolean field) throws SQLException {
        setBoolean(ps, position, field, true);
    }

    public final void setBooleanOpcional(PreparedStatement ps, int position, Boolean field) throws SQLException {
        setBoolean(ps, position, field, false);
    }

    public final void setEnumObrigatorio(PreparedStatement ps, int position, Enum field) throws SQLException {
        setEnum(ps, position, field, true);
    }

    public final void setEnumOpcional(PreparedStatement ps, int position, Enum field) throws SQLException {
        setEnum(ps, position, field, false);
    }

    public final void setDateObrigatorio(PreparedStatement ps, int position, java.util.Date field) throws SQLException {
        setDate(ps, position, field, true);
    }

    public final void setDateOpcional(PreparedStatement ps, int position, java.util.Date field) throws SQLException {
        setDate(ps, position, field, false);
    }

    public final void setTimeObrigatorio(PreparedStatement ps, int position, java.util.Date field) throws SQLException {
        setTime(ps, position, field, true);
    }

    public final void setTimeOpcional(PreparedStatement ps, int position, java.util.Date field) throws SQLException {
        setTime(ps, position, field, false);
    }

    public final void setTimestampObrigatorio(PreparedStatement ps, int position, java.util.Date field) throws SQLException {
        setTimestamp(ps, position, field, true);
    }

    public final void setTimestampOpcional(PreparedStatement ps, int position, java.util.Date field) throws SQLException {
        setTimestamp(ps, position, field, false);
    }

    public final void setEntityObrigatorio(PreparedStatement ps, int position, EntidadeWebPublico field) throws SQLException {
        setEntity(ps, position, field, true);
    }

    public final void setEntityOpcional(PreparedStatement ps, int position, EntidadeWebPublico field) throws SQLException {
        setEntity(ps, position, field, false);
    }

    public final Long getLong(ResultSet rs, String fieldName) throws SQLException {
        return JDBCUtil.getLong(rs, fieldName);
    }

    public final Double getDouble(ResultSet rs, String fieldName) throws SQLException {
        final Double retorno = rs.getDouble(fieldName);
        return rs.wasNull() ? null : retorno;
    }

    public final BigDecimal getBigDecimal(ResultSet rs, String fieldName) throws SQLException {
        return JDBCUtil.getBigDecimal(rs, fieldName);
    }

    public final java.util.Date getDate(ResultSet rs, String fieldName) throws SQLException {
        return JDBCUtil.getDate(rs, fieldName);
    }

    public final Boolean getBoolean(ResultSet rs, String fieldName) throws SQLException {
        return JDBCUtil.getBoolean(rs, fieldName);
    }

    public final Integer getInt(ResultSet rs, String fieldName) throws SQLException {
        return JDBCUtil.getInt(rs, fieldName);
    }

    public final String getString(ResultSet rs, String fieldName) throws SQLException {
        return JDBCUtil.getString(rs, fieldName);
    }

    public final <E extends Enum<E>> E getEnum(ResultSet rs, Class<E> classeEnum, String fieldName) throws SQLException {
        for (E valorEnum : EnumSet.allOf(classeEnum)) {
            if (valorEnum.name().equalsIgnoreCase(getString(rs, fieldName)))
                return valorEnum;
        }

        return null;
    }

    public final Time getTime(ResultSet rs, String fieldName) throws SQLException {
        return JDBCUtil.getTime(rs, fieldName);
    }

    protected final int getDefaultBatchSize() {
        return DEFAULT_BATCH_SIZE;
    }

    @Override
    public T salvar(T entity) {
        return inserirDireto(entity);
    }

    @Override
    public final void remover(T entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }
        try {
            removerDepoisDeValidacao(entity);
        } catch (Exception ex) {
            throw new WebPublicoJDBCException("Erro removendo " + entity.getClass().getName() + " com ID " + entity.getId(), ex);
        }
    }

    protected void removerDepoisDeValidacao(T entity) throws SQLException {
        if (recuperarStatementDeletePorID() == null) {
            throw new MetodoNaoImplementadoException("Método remover não implementado em " + this.getClass().getName());
        }
        PreparedStatement ps = getPreparedStatement(recuperarStatementDeletePorID());
        setLongObrigatorio(ps, 1, entity.getId());
        ps.executeUpdate();
    }

    @Override
    public T recuperar(Long id) {
        return buscarPorID(id);
    }

    @Override
    public List<T> listar(String campo, String... valores) {
        throw new MetodoNaoImplementadoException("Método listar não implementado em " + this.getClass().getName());
    }

    @Override
    public long contar() {
        return preCarregarLista().size();
    }

    @Override
    public String getNomeDependencia(String mensagem) throws ClassNotFoundException {
        throw new MetodoNaoImplementadoException("Método getNomeDependencia não implementado em " + this.getClass().getName());
    }

    @Override
    public T initializeAndUnproxy(T proxiedEntity) {
        return proxiedEntity;
    }

    @Override
    public List<Object[]> recuperarHistoricoAlteracoes(T entity) {
        throw new MetodoNaoImplementadoException("Método recuperarHistoricoAlteracoes não implementado em " + this.getClass().getName());
    }

    @Override
    public void forcarRegistroInicialNaAuditoria(T entity) {
        throw new MetodoNaoImplementadoException("Método forcarRegistroInicialNaAuditoria não implementado em " + this.getClass().getName());
    }

    @Override
    public Long retornarUltimoCodigo() {
        throw new MetodoNaoImplementadoException("Método retornarUltimoCodigo não implementado em " + this.getClass().getName());
    }

    @Override
    public List<String> buscarCasosDeUsoComRegistroVinculado(T registro) {
        return null;
    }

    public final Long buscarNextId() {
        return nextIDFactory.nextID();
    }

    public void commit() throws SQLException {
        this.conn.commit();
    }

    public void rollback() throws SQLException {
        this.conn.rollback();
    }

    public void preencherCamposInsert(PreparedStatement ps, CampoInsert... camposInsert) throws SQLException {
        for (int i = 0; i < camposInsert.length; i++) {
            CampoInsert campo = camposInsert[i];
            Integer position = i+1;

            if (campo.getClass().equals(LongObrigatorio.class))
                setLongObrigatorio(ps, position, ((LongObrigatorio)campo).field);
            else if (campo.getClass().equals(LongOpcional.class))
                setLongOpcional(ps, position, ((LongOpcional)campo).field);
            else if (campo.getClass().equals(StringObrigatorio.class))
                setStringObrigatorio(ps, position, ((StringObrigatorio)campo).field);
            else if (campo.getClass().equals(StringOpcional.class))
                setStringOpcional(ps, position, ((StringOpcional)campo).field);
            else if (campo.getClass().equals(IntObrigatorio.class))
                setIntObrigatorio(ps, position, ((IntObrigatorio)campo).field);
            else if (campo.getClass().equals(IntOpcional.class))
                setIntOpcional(ps, position, ((IntOpcional)campo).field);
            else if (campo.getClass().equals(BigDecimalObrigatorio.class))
                setBigDecimalObrigatorio(ps, position, ((BigDecimalObrigatorio)campo).field);
            else if (campo.getClass().equals(BigDecimalOpcional.class))
                setBigDecimalOpcional(ps, position, ((BigDecimalOpcional)campo).field);
            else if (campo.getClass().equals(DoubleObrigatorio.class))
                setDoubleObrigatorio(ps, position, ((DoubleObrigatorio)campo).field);
            else if (campo.getClass().equals(DoubleOpcional.class))
                setDoubleOpcional(ps, position, ((DoubleOpcional)campo).field);
            else if (campo.getClass().equals(BytesObrigatorio.class))
                setBytesObrigatorio(ps, position, ((BytesObrigatorio)campo).field);
            else if (campo.getClass().equals(BytesOpcional.class))
                setBytesOpcional(ps, position, ((BytesOpcional)campo).field);
            else if (campo.getClass().equals(BooleanObrigatorio.class))
                setBooleanObrigatorio(ps, position, ((BooleanObrigatorio)campo).field);
            else if (campo.getClass().equals(BooleanOpcional.class))
                setBooleanOpcional(ps, position, ((BooleanOpcional)campo).field);
            else if (campo.getClass().equals(DateObrigatorio.class))
                setDateObrigatorio(ps, position, ((DateObrigatorio)campo).field);
            else if (campo.getClass().equals(DateOpcional.class))
                setDateOpcional(ps, position, ((DateOpcional)campo).field);
            else if (campo.getClass().equals(TimeObrigatorio.class))
                setTimeObrigatorio(ps, position, ((TimeObrigatorio)campo).field);
            else if (campo.getClass().equals(TimeOpcional.class))
                setTimeOpcional(ps, position, ((TimeOpcional)campo).field);
            else if (campo.getClass().equals(TimestampObrigatorio.class))
                setTimestampObrigatorio(ps, position, ((TimestampObrigatorio)campo).field);
            else if (campo.getClass().equals(TimestampOpcional.class))
                setTimestampOpcional(ps, position, ((TimestampOpcional)campo).field);
            else if (campo.getClass().equals(EnumObrigatorio.class))
                setEnumObrigatorio(ps, position, ((EnumObrigatorio)campo).field);
            else if (campo.getClass().equals(EnumOpcional.class))
                setEnumOpcional(ps, position, ((EnumOpcional)campo).field);
            else if (campo.getClass().equals(EntityObrigatorio.class))
                setEntityObrigatorio(ps, position, ((EntityObrigatorio)campo).field);
            else if (campo.getClass().equals(EntityOpcional.class))
                setEntityOpcional(ps, position, ((EntityOpcional)campo).field);
        }
    }

    public interface CampoInsert {}

    public final LongObrigatorio setId(NextIDFactory nextIDFactory) {
        return new LongObrigatorio(nextIDFactory.nextID());
    }

    public final LongObrigatorio longObrigatorio(Long field) {
        return new LongObrigatorio(field);
    }

    private class LongObrigatorio implements CampoInsert {
        private Long field;
        public LongObrigatorio(Long field) {
            this.field = field;
        }
    }

    public final LongOpcional longOpcional(Long field) {
        return new LongOpcional(field);
    }

    private class LongOpcional implements CampoInsert {
        private Long field;
        public LongOpcional(Long field) {
            this.field = field;
        }
    }

    public final IntObrigatorio intObrigatorio(Integer field) {
        return new IntObrigatorio(field);
    }

    private class IntObrigatorio implements CampoInsert {
        private Integer field;
        public IntObrigatorio(Integer field) {
            this.field = field;
        }
    }

    public final IntOpcional intOpcional(Integer field) {
        return new IntOpcional(field);
    }

    private class IntOpcional implements CampoInsert {
        private Integer field;
        public IntOpcional(Integer field) {
            this.field = field;
        }
    }

    public final BigDecimalObrigatorio bigDecimalObrigatorio(BigDecimal field) {
        return new BigDecimalObrigatorio(field);
    }

    private class BigDecimalObrigatorio implements CampoInsert {
        private BigDecimal field;
        public BigDecimalObrigatorio(BigDecimal field) {
            this.field = field;
        }
    }

    public final BigDecimalOpcional bigDecimalOpcional(BigDecimal field) {
        return new BigDecimalOpcional(field);
    }

    private class BigDecimalOpcional implements CampoInsert {
        private BigDecimal field;
        public BigDecimalOpcional(BigDecimal field) {
            this.field = field;
        }
    }

    public final DoubleObrigatorio doubleObrigatorio(Double field) {
        return new DoubleObrigatorio(field);
    }

    private class DoubleObrigatorio implements CampoInsert {
        private Double field;
        public DoubleObrigatorio(Double field) {
            this.field = field;
        }
    }

    public final DoubleOpcional doubleOpcional(Double field) {
        return new DoubleOpcional(field);
    }

    private class DoubleOpcional implements CampoInsert {
        private Double field;
        public DoubleOpcional(Double field) {
            this.field = field;
        }
    }

    public final StringObrigatorio stringObrigatorio(String field) {
        return new StringObrigatorio(field);
    }

    private class StringObrigatorio implements CampoInsert {
        private String field;
        public StringObrigatorio(String field) {
            this.field = field;
        }
    }

    public final StringOpcional stringOpcional(String field) {
        return new StringOpcional(field);
    }

    private class StringOpcional implements CampoInsert {
        private String field;
        public StringOpcional(String field) {
            this.field = field;
        }
    }

    public final EntityObrigatorio entityObrigatorio(EntidadeWebPublico field) {
        return new EntityObrigatorio(field);
    }

    private class EntityObrigatorio implements CampoInsert {
        private EntidadeWebPublico field;
        public EntityObrigatorio(EntidadeWebPublico field) {
            this.field = field;
        }
    }

    public final EntityOpcional entityOpcional(EntidadeWebPublico field) {
        return new EntityOpcional(field);
    }

    private class EntityOpcional implements CampoInsert {
        private EntidadeWebPublico field;
        public EntityOpcional(EntidadeWebPublico field) {
            this.field = field;
        }
    }

    public final DateObrigatorio dateObrigatorio(java.util.Date field) {
        return new DateObrigatorio(field);
    }

    private class DateObrigatorio implements CampoInsert {
        private java.util.Date field;
        public DateObrigatorio(java.util.Date field) {
            this.field = field;
        }
    }

    public final DateOpcional dateOpcional(java.util.Date field) {
        return new DateOpcional(field);
    }

    private class DateOpcional implements CampoInsert {
        private java.util.Date field;
        public DateOpcional(java.util.Date field) {
            this.field = field;
        }
    }

    public final TimeObrigatorio timeObrigatorio(java.util.Date field) {
        return new TimeObrigatorio(field);
    }

    private class TimeObrigatorio implements CampoInsert {
        private java.util.Date field;
        public TimeObrigatorio(java.util.Date field) {
            this.field = field;
        }
    }

    public final TimeOpcional timeOpcional(java.util.Date field) {
        return new TimeOpcional(field);
    }

    private class TimeOpcional implements CampoInsert {
        private java.util.Date field;
        public TimeOpcional(java.util.Date field) {
            this.field = field;
        }
    }

    public final TimestampObrigatorio timestampObrigatorio(java.util.Date field) {
        return new TimestampObrigatorio(field);
    }

    private class TimestampObrigatorio implements CampoInsert {
        private java.util.Date field;
        public TimestampObrigatorio(java.util.Date field) {
            this.field = field;
        }
    }

    public final TimestampOpcional timestampOpcional(java.util.Date field) {
        return new TimestampOpcional(field);
    }

    private class TimestampOpcional implements CampoInsert {
        private java.util.Date field;
        public TimestampOpcional(java.util.Date field) {
            this.field = field;
        }
    }

    public final EnumObrigatorio enumObrigatorio(Enum field) {
        return new EnumObrigatorio(field);
    }

    private class EnumObrigatorio implements CampoInsert {
        private Enum field;
        public EnumObrigatorio(Enum field) {
            this.field = field;
        }
    }

    public final EnumOpcional enumOpcional(Enum field) {
        return new EnumOpcional(field);
    }

    private class EnumOpcional implements CampoInsert {
        private Enum field;
        public EnumOpcional(Enum field) {
            this.field = field;
        }
    }

    public final BytesObrigatorio bytesObrigatorio(byte field[]) {
        return new BytesObrigatorio(field);
    }

    private class BytesObrigatorio implements CampoInsert {
        private byte field[];
        public BytesObrigatorio(byte field[]) {
            this.field = field;
        }
    }

    public final BytesOpcional bytesOpcional(byte field[]) {
        return new BytesOpcional(field);
    }

    private class BytesOpcional implements CampoInsert {
        private byte field[];
        public BytesOpcional(byte field[]) {
            this.field = field;
        }
    }

    public final BooleanObrigatorio booleanObrigatorio(Boolean field) {
        return new BooleanObrigatorio(field);
    }

    private class BooleanObrigatorio implements CampoInsert {
        private Boolean field;
        public BooleanObrigatorio(Boolean field) {
            this.field = field;
        }
    }

    public final BooleanOpcional booleanOpcional(Boolean field) {
        return new BooleanOpcional(field);
    }

    private class BooleanOpcional implements CampoInsert {
        private Boolean field;
        public BooleanOpcional(Boolean field) {
            this.field = field;
        }
    }

}
