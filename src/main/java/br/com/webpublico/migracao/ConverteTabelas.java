/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import java.sql.*;

/**
 * @author munif
 */
public class ConverteTabelas {

    private static long contador;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        contador = 0l;
        String t = "";
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Connection conexaoOracle2 = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "webpublico2", "senha10"); //NOSONAR
            Class.forName("oracle.jdbc.OracleDriver");
            Connection conexaoCEP = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ceps", "senha10");
            conexaoOracle2.setAutoCommit(true);

            /*String tabelas[] = {"ARQUIVO",
            "ARQUIVOPARTE",
            "CATEGORIASERVICO",
            "CEP",
            "CNAE",
            "ENTIDADE",
            "EVENTOFP",
            "EXERCICIO",
            "FUNCAO",
            "INDICEECONOMICO",
            "TIPOLOGRADOURO",
            "PAIS",
            "NACIONALIDADE",
            "UF",
            "CIDADE",
            "BAIRRO ",
            "LOGRADOURO",
            "LOGRADOURO_CEP",
            "LOTEAMENTO",
            "QUADRA",
            "LOTE",
            "ATRIBUTO",
            "VALORPOSSIVEL",
            "VALORATRIBUTO",
            "LOTE_VALORATRIBUTO",
            "PESSOA",
            "PESSOAFISICA",
            "PESSOAJURIDICA",
            "PONTUACAO",
            "PONTUACAO_ATRIBUTO",
            "CONFIGURACAOCONTABIL",
            "CONFIGURACAOMODULO",
            "SERVICOURBANO",
            "SUBFUNCAO",
            "TELEFONE",
            "FACE",
            "FACESERVICO",
            "TESTADA",
            "TEXTO",
            "TIPOCONTA",
            "UNIDADEORGANIZACIONAL",
            "CADASTROIMOBILIARIO ",
            "CI_VALORATRIBUTOS",
            "CBO",
            "CONSTRUCAO",
            "CONSTRUCAO_VALORATRIBUTO",
            "DISTRITO",
            "ENDERECO",
            "PROPRIEDADE",
            "PESSOA_ENDERECO"
            };*/

            String tabelas[] = {"CEPUF", "CEPLOCALIDADE", "CEPBAIRRO", "CEPLOGRADOURO"

            };


            for (String tab : tabelas) {
                t = tab;
                converteTabela(t, conexaoCEP, conexaoOracle2);
            }
            //  conexaoOracle2.commit();


        } catch (Exception ex) {
            System.out.printf(t + " " + ex.getLocalizedMessage());
        }
    }

    private static void converteTabela(String tabela, Connection origem, Connection destino) throws SQLException {
        destino.createStatement().execute("DELETE FROM " + tabela);
        //PreparedStatement pconsulta = destino.prepareStatement("SELECT id from " + tabela + " WHERE id = ?");
        System.out.print("Convertendo " + tabela + " ");
        ResultSet res = origem.createStatement().executeQuery("SELECT * FROM " + tabela);
        ResultSetMetaData rsmd = res.getMetaData();
        int colunas = rsmd.getColumnCount();
        int colunaId = 0;
        String sql = "INSERT INTO " + tabela + " (";
        for (int i = 1; i <= colunas; i++) {
            sql += rsmd.getColumnName(i);
            if (rsmd.getColumnName(i).equalsIgnoreCase("id")) {
                colunaId = i;
            }
            if (i != colunas) {
                sql += ",";
            }
        }
        sql += ") VALUES (";
        for (int i = 1; i <= colunas; i++) {
            sql += "?";
            if (i != colunas) {
                sql += ",";
            }
        }
        sql += ")";


        PreparedStatement ps = destino.prepareStatement(sql);
        //System.out.println(sql);

        while (res.next()) {
            Object valorId = null;
            for (int i = 1; i <= colunas; i++) {
                //   //System.out.println(i + " " + res.getObject(i));
                Object valor = res.getObject(i);
                if (i == colunaId) {
                    valorId = valor;
                }

                ps.setObject(i, valor);
            }
            //pconsulta.setObject(1, valorId);
            //if (colunaId == 0 || !pconsulta.executeQuery().next()) {
            ps.execute();
            contador++;
            if (contador % 1000 == 0) {
                System.out.print(contador + " ");
            }
            if (contador % 10000 == 0) {
                //System.out.println();
            }
            //  }
        }
        //System.out.println(tabela + " convertida " + contador + " linhas convertidas");
    }
}
