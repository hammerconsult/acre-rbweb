package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fernando, credits to Arthur Zavadsky on 29/01/15.
 */
public final class IdGenerator extends ClassPatternJDBC {
    private static final int DEFAULT_BUCKET_SIZE = 10;
    private static final int MAX_BUCKET_SIZE = 1000;
    private static final int MIN_BUCKET_SIZE = 1;
    private static IdGenerator instance;
    private PreparedStatement psNextID;
    private List<Long> bucket = new LinkedList<>();

    private IdGenerator(Connection conn) {
        this.conexao = conn;
    }

    public static IdGenerator createInstance(Connection conn) {
        return createInstance(conn, DEFAULT_BUCKET_SIZE);
    }

    public static synchronized IdGenerator createInstance(Connection conn, int bucketSize) {
        if (instance == null) {
            instance = new IdGenerator(conn);
            int realBucketSize = bucketSize;
            if (bucketSize <= MIN_BUCKET_SIZE) {
                realBucketSize = MIN_BUCKET_SIZE;
            } else if (bucketSize > MAX_BUCKET_SIZE) {
                realBucketSize = MAX_BUCKET_SIZE;
            }
            try {
                instance.psNextID = instance.conexao.prepareStatement("select hibernate_sequence.nextval "
                        + " from ( select level from dual connect by level <= " + realBucketSize + ")");
            } catch (SQLException ex) {
                throw new RuntimeException("Não foi possível preparar a instrução para recuperar o próximo id, verifique.", ex);
            }
        }
        showConnection(instance.getClass());
        return instance;
    }

    public long createID() {
        if (bucket.isEmpty()) {
            fillBucket();
        }
        return bucket.remove(0);
    }

    private void fillBucket() {
        System.out.println(psNextID.toString());
        try (ResultSet rs = instance.psNextID.executeQuery()) {
            while (rs.next()) {
                bucket.add(rs.getLong(1));
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Foda mano: " + ex.getMessage(), ex);
        }
    }
}
