/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import br.com.webpublico.interfaces.Importador;

import java.io.*;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Munif
 */
public class ImportadorGenerico implements Importador {

    public static final String DEFAULT = "_DF";
    public static final String IGNORAR = "_I";
    public static final String FK = "_FK";
    private BufferedReader br;
    private File arquivoImportacao;
    private FileInputStream stream;
    private InputStreamReader streamReader;
    private String atributo[];
    private Map<String, String> atributoValorDefault;
    private String tabela;
    private String arquivo;
    int numeroLinha;

    public ImportadorGenerico(String tabela, String arquivo) {
        this.tabela = tabela;
        this.arquivo = arquivo;
    }

    private String buscaId(String nomeAtributo, String valor) throws SQLException {
        String aRetornar = null;
        String partes[] = nomeAtributo.split("_");
        if (partes.length != 5) {
            throw new RuntimeException("Nome do campo FK deve ser _FK_tabela_campoOrigem_atributo");
        }
        String sql = "SELECT id FROM " + partes[2] + " WHERE " + partes[3] + "='" + valor + "'";
        PreparedStatement psFk = Conexao.getInstancia().getConnection().prepareStatement(sql);
        ResultSet res = psFk.executeQuery();
        if (res.next()) {
            aRetornar = res.getString("id");
        }
        res.close();
        psFk.close();
        ////System.out.println("Consulta FK " + sql + " retorna " + aRetornar);
        return aRetornar;
    }

    private void valoresDefault(String cabeca) {
        atributoValorDefault = new HashMap<String, String>();
        String partes[] = cabeca.split(";");
        for (String s : partes) {
            atributoValorDefault.put(s.split("_")[2], s.split("_")[3]);
        }
        //System.out.println(atributoValorDefault);


    }

    @Override
    public void registrarMensagem(String msg) {
        //System.out.println("------------>" + tabela + " " + msg);
    }

    public boolean setValor(Object objeto, String atributo, String valor) throws Exception {
        Class classeAqui = objeto.getClass();
        Field f = classeAqui.getDeclaredField(atributo);
        f.setAccessible(true);
        Class fc = f.getType();
        if (fc.equals(Boolean.class)) {
            f.set(objeto, valor.equals("1"));
        } else {
            f.set(objeto, fc.getConstructor(String.class).newInstance(valor));
        }
        return true;
    }

    private void abreArquivo(String nome) throws FileNotFoundException, IOException {
        registrarMensagem("Abrindo arquivo " + nome);
        arquivoImportacao = new File(nome);
        stream = new FileInputStream(arquivoImportacao);
        streamReader = new InputStreamReader(stream);
        br = new BufferedReader(streamReader);
        String cabeca = br.readLine();
        numeroLinha++;
        if (cabeca.startsWith(DEFAULT)) {
            valoresDefault(cabeca);
            cabeca = br.readLine();
            numeroLinha++;
        }
        registrarMensagem(cabeca);
        atributo = cabeca.split(";");
    }

    public String gereSQL() {
        String comandoSQL = null;
        boolean campoIdAutomatico = !atributo[0].contains("id"); //Melhorar, muito fraco ser for idade ID hehehe
        registrarMensagem(campoIdAutomatico ? "Gerando campo Id automaticamente" : "Gerando campo id manualmente");
        if (campoIdAutomatico) {
            comandoSQL = "insert into " + tabela + " (id,";
        } else {
            comandoSQL = "insert into " + tabela + " (";
        }
        for (int i = 0; i < atributo.length; i++) {
            if (!atributo[i].startsWith(IGNORAR)) {
                comandoSQL = comandoSQL + (i == 0 ? "" : ",");
                if (atributo[i].toUpperCase().startsWith(FK)) {
                    String chaveEstrangeira = atributo[i].split("_")[4].trim() + "_id";
                    if (chaveEstrangeira.equals("id_id")) {
                        chaveEstrangeira = "id";
                    }
                    comandoSQL = comandoSQL + chaveEstrangeira;
                } else {
                    comandoSQL = comandoSQL + atributo[i].trim();
                }
            }
        }
        if (atributoValorDefault != null) {
            for (String s : atributoValorDefault.keySet()) {
                comandoSQL = comandoSQL + "," + s;
            }
        }
        if (campoIdAutomatico) {
            comandoSQL = comandoSQL + ")values(hibernate_sequence.nextVal,";
        } else {

            comandoSQL = comandoSQL + ")values(";
        }
        for (int i = 0; i < atributo.length; i++) {
            if (!atributo[i].startsWith(IGNORAR)) {
                comandoSQL = comandoSQL + (i == 0 ? "" : ",") + "?";
            }
        }
        if (atributoValorDefault != null) {
            for (String s : atributoValorDefault.keySet()) {
                comandoSQL = comandoSQL + ",'" + atributoValorDefault.get(s) + "'";
            }
        }

        comandoSQL = comandoSQL + ")";
        registrarMensagem(comandoSQL);
        return comandoSQL;

    }

    public void importar() {
        try {
            registrarMensagem("Iniciando Importação de " + this.tabela);
            abreArquivo(arquivo);
            String comandoSQL = gereSQL();
            PreparedStatement ps = Conexao.getInstancia().getConnection().prepareStatement(comandoSQL);
            String linha = null;

            while ((linha = br.readLine()) != null) {
                String valores[] = linha.split(";");
                int parametro = 1;
                for (int i = 0; i < atributo.length; i++) {
                    if (!atributo[i].startsWith(IGNORAR)) {
                        if (i >= valores.length) {
                            ////System.out.println(parametro + "=null");
                            ps.setNull(parametro, java.sql.Types.INTEGER);
                        } else {
                            String valor = valores[i];
                            if (atributo[i].toUpperCase().startsWith(FK)) {
                                valor = buscaId(atributo[i], valor);
                            }
                            ////System.out.println(parametro + "=" + valor);
                            if (valor != null) {
                                ps.setObject(parametro, valor);
                            } else {
                                ps.setNull(parametro, java.sql.Types.INTEGER);
                            }

                        }
                        parametro++;
                    }
                }
                try {
                    ps.execute();
                } catch (SQLException ex) {
                    //System.out.println("Arquivo linha " + numeroLinha + " " + linha);
                    ex.printStackTrace();
                }
                if (numeroLinha % 100 == 0) {
                    //System.out.println(numeroLinha + " linhas processadas");
                }
                numeroLinha++;
            }
            //System.out.println(numeroLinha + " linhas processadas");
            br.close();
            streamReader.close();
            stream.close();
            registrarMensagem("Concluído " + tabela);
        } catch (Exception ex) {
            registrarMensagem(ex.toString());
            ex.printStackTrace();
        }

    }
}
