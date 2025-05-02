package br.com.webpublico.repositorios.jdbc.util;

import br.com.webpublico.repositorios.jdbc.WebPublicoJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel
 * @since 26/08/2015 10:53
 */
@Component
@Lazy
public class NextIDFactory {

    public static final int DEFAULT_BUCKET_SIZE = 100;

    private static NextIDFactory instance;
    private PreparedStatement psNextID;
    private List<Long> bucket = new LinkedList<>();

    @Autowired
    private NextIDFactory(Connection conn) {
        if (!JDBCUtil.validateConnection(conn)) {
            throw new WebPublicoJDBCException("Connection inválida");
        }
        try {
            this.psNextID = conn.prepareStatement(getPreparedStatement(conn, DEFAULT_BUCKET_SIZE));
        } catch (Exception ex) {
            throw new WebPublicoJDBCException("Erro configurando PreparedStatement", ex);
        }
    }

    public static synchronized NextIDFactory createInstance(Connection conn) {
        if (instance != null) {
            return instance;
        }
        instance = new NextIDFactory(conn);
        return instance;
    }

    private static String getPreparedStatement(Connection conn, int bucketSize) {
        if (conn.getClass().getName().contains("oracle")) {
            return OracleNextIDFactory.getStatement(bucketSize);
        }
        return PostgreSQLNextIDFactory.getStatement(bucketSize);
    }

    public long nextID() {
        if (bucket.isEmpty()) {
            fillBucket();
        }
        return bucket.remove(0);
    }

    private void fillBucket() {
        try (ResultSet rs = this.psNextID.executeQuery()) {
            while (rs.next()) {
                bucket.add(rs.getLong(1));
            }
        } catch (Exception ex) {
            throw new WebPublicoJDBCException("Erro Recuperando IDs", ex);
        }
    }

    @SuppressWarnings("SqlDialectInspection")
    private static class OracleNextIDFactory {
        private static synchronized String getStatement(int bucketSize) {
            return "SELECT hibernate_sequence.nextval FROM ( SELECT level FROM dual CONNECT BY level <= )" + bucketSize + ")";
        }
    }

    @SuppressWarnings("SqlDialectInspection")
    private static class PostgreSQLNextIDFactory {
        private static synchronized String getStatement(int bucketSize) {
            return "SELECT nextVal('hibernate_sequence') FROM generate_series(1, " + bucketSize + ");";
        }
    }
}
