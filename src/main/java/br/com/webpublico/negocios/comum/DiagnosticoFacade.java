package br.com.webpublico.negocios.comum;

import br.com.webpublico.controlerelatorio.AbstractReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Daniel Franco
 * @since 10/02/2017 09:57
 */
@Stateless
public class DiagnosticoFacade {

    private static final int iteracoes = 100000;
    private static final int qtdBuscasNoBanco = 5;
    private static final Random random = new Random();
    private static final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final Logger logger = LoggerFactory.getLogger(DiagnosticoFacade.class);

    /**
     * Testa a performance da JVM, instanciando um grande número de objetos em uma lista, depois percorrendo essa lista
     * Este teste deve verificar tanto a performance da CPU quanto memória da JVM
     *
     * @return o tempo, em milissegundos para instanciar um grande numero de objetos e guardá-los em um arraylist
     */
    public long performanceJVM() {
        long duracao = System.currentTimeMillis();
        List<ObjetoTeste> lista = new ArrayList<>();
        for (int i = 0; i <= iteracoes; i++) {
            lista.add(new ObjetoTeste(i));
        }
        int total = 0;
        for (ObjetoTeste obj : lista) {
            total += obj.valor;
        }
        return System.currentTimeMillis() - duracao;
    }

    /**
     * Testa a latência entre o Servidor de Aplicação e o Banco de Dados, verificando quanto tempo leva para executar
     * uma query simples, teoricamente instantânea
     *
     * @return o tempo em milissegundos para executar uma query simples
     */
    public long pingBancoDeDados() {
        final String sql = "SELECT 1 FROM dual";
        long duracao = System.currentTimeMillis();
        Connection conn = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
        try {
            try (Statement st = conn.createStatement()) {
                st.execute(sql);
                return System.currentTimeMillis() - duracao;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erro Executando Diagnóstico de Ping no Banco de Dados", ex);
        } finally {
            AbstractReport.fecharConnection(conn);
        }
    }

    /**
     * Testa a performance do Banco de Dados criando uma tabela temporária, gravando uma quantidade de registros,
     * selecionando estes registros e então excluindo a tabela temporária.
     * O nome da tabela é aleatório para evitar problemas com 2 ou mais execuções simultâneas do Diagnóstico
     *
     * @return o tempo em milissegundos para criar, preencher, consultar e excluir uma tabela no banco de dados
     */
    public long performanceBancoDeDados() {
        long duracao = System.currentTimeMillis();
        Connection conn = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
        try {
            try (Statement st = conn.createStatement()) {
                final String tableName = "tmp" + randomString(20);
                st.execute("CREATE TABLE " + tableName + "(valor number(19, 0))");
                PreparedStatement psInsert = conn.prepareStatement("INSERT INTO " + tableName + "(valor) VALUES (?)");
                for (int i = 0; i <= iteracoes; i++) {
                    psInsert.clearParameters();
                    psInsert.setLong(1, i);
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
                PreparedStatement psSelect = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE valor = ?");
                for (int i = 0; i <= qtdBuscasNoBanco; i++) {
                    buscarNoBanco(psSelect, random.nextInt(iteracoes));
                }
                st.execute("DROP TABLE " + tableName);
                return System.currentTimeMillis() - duracao;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erro Executando Diagnóstico de Ping no Banco de Dados", ex);
        } finally {
            AbstractReport.fecharConnection(conn);
        }
    }

    private void buscarNoBanco(PreparedStatement psSelect, int valor) throws SQLException {
        psSelect.clearParameters();
        psSelect.setLong(1, valor);
        psSelect.executeQuery();
    }


    private String randomString(int tamanho) {
        if (tamanho <= 0) {
            tamanho = 20;
        }
        String retorno = "";
        for (int i = 0; i < tamanho; i++) {
            char c = chars[random.nextInt(chars.length)];
            retorno += c;
        }
        return retorno;
    }

    private class ObjetoTeste {
        private Integer valor;

        public ObjetoTeste(Integer valor) {
            this.valor = valor;
        }
    }
}
