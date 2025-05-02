/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import br.com.webpublico.entidades.Pais;

import java.sql.*;

/**
 * @author munif
 */
public class MigraCadastroImobiliario {

    public static Connection conexaoWebPublico;
    public static Connection conexaoAgata;

    public static long contaRegistros(Connection con, String tabela, String coluna) throws Exception {
        ResultSet res = con.createStatement().executeQuery("SELECT COUNT(" + coluna + ") FROM " + tabela);
        res.next();
        return res.getLong(1);
    }

    public static void criaPaises() throws Exception {
        if (contaRegistros(conexaoWebPublico, "PAIS", "ID") > 0) {
            //System.out.println("Tabela países com dados, ignorando migração.");
            return;
        } else {
            //System.out.println("Migrando tabela países....");
        }
        long id = -1L;
        PreparedStatement ps = conexaoWebPublico.prepareStatement("INSERT INTO PAIS (id,nome,ddi) VALUES (?,?,?)");
        for (Pais p : paises) {
            ps.setLong(1, id);
            ps.setString(2, p.getNome());
            ps.setInt(3, p.getCodigo());
            ps.execute();
            id--;
        }
    }

    public static void copiaCEPUFparaUF() throws Exception {
        if (contaRegistros(conexaoWebPublico, "UF", "ID") > 0) {
            //System.out.println("Tabela UF com dados, ignorando migração.");
            return;
        }
        //System.out.println("Migrando tabela UF....");
        Statement s = conexaoWebPublico.createStatement();
        s.execute("INSERT INTO UF  SELECT -ID AS ID ,NOME AS NOME,SIGLA AS UF,-1 AS PAIS_ID FROM CEPUF");
    }

    public static void copiaCEPLocalidadeparaCidadeEDistrito() throws Exception {
        if (contaRegistros(conexaoWebPublico, "CIDADE", "ID") > 0) {
            //System.out.println("Tabela CIDADE com dados, ignorando migração.");
            return;
        }
        //System.out.println("Migrando tabela CIDADE....");
        Statement s = conexaoWebPublico.createStatement();
//        s.execute("alter table CIDADE disable  constraint 'SYS_C00127417'");
        s.execute("INSERT INTO CIDADE (ID,NOME,CEP,UF_ID)  SELECT -ID AS ID ,NOME AS NOME, CEP AS CEP,-UF_ID AS UF_ID FROM CEPLOCALIDADE ");
        //System.out.println("Criando Distritos....");
        s.execute("INSERT INTO DISTRITO  SELECT -ID AS ID ,-ID AS CIDADE_ID  FROM CEPLOCALIDADE WHERE TIPO = 'D' ");
        //s.execute("alter table CIDADE enable  constraint 'SYS_C00127417'");

    }

    public static void agataATR10ParaBairro() throws Exception {
        if (contaRegistros(conexaoWebPublico, "BAIRRO", "ID") > 0) {
            //System.out.println("Tabela BAIRRO com dados, ignorando migração.");
            return;
        }
        //System.out.println("Migrando tabela Bairros....");
        PreparedStatement ps = conexaoWebPublico.prepareStatement("INSERT INTO BAIRRO (ID,DESCRICAO,CIDADE_ID) VALUES (?,?,?)");
        ResultSet res = conexaoAgata.createStatement().executeQuery("SELECT -CDGSETOR AS ID, NOMSETOR AS DESCRICAO, -16 AS CIDADE_ID FROM RIOBRANCO.ATR10");
        while (res.next()) {
            for (int i = 1; i <= 3; i++) {
                ps.setObject(i, res.getObject(i));
            }
            ps.execute();
        }
    }

    public static void agataATR19ParaServicoUrano() throws Exception {
        if (contaRegistros(conexaoWebPublico, "ServicoUrbano", "ID") > 0) {
            //System.out.println("Tabela ServicoUrbano com dados, ignorando migração.");
            return;
        }
        //System.out.println("Migrando tabela ServicoUrbano....");
        PreparedStatement ps = conexaoWebPublico.prepareStatement("INSERT INTO ServicoUrbano (id,nome) VALUES (?,?)");
        ResultSet res = conexaoAgata.createStatement().executeQuery("SELECT -CDGBENMUN AS ID, DSCBENMUN AS NOME FROM RIOBRANCO.ATR19");
        while (res.next()) {
            for (int i = 1; i <= 2; i++) {
                ps.setObject(i, res.getObject(i));
            }
            ps.execute();
        }
    }

    public static void agataATR22ParaTipoLogradouro() throws Exception {
        if (contaRegistros(conexaoWebPublico, "TipoLogradouro", "ID") > 0) {
            //System.out.println("Tabela TipoLogradouro com dados, ignorando migração.");
            return;
        }
        //System.out.println("Migrando tabela TipoLogradouro....");
        PreparedStatement ps = conexaoWebPublico.prepareStatement("INSERT INTO TipoLogradouro(id,descricao,sigla) VALUES (?,?,?)");
        ResultSet res = conexaoAgata.createStatement().executeQuery("SELECT NOMTPOLGR AS DESCRICAO, TPOLGR AS SIGLA FROM RioBranco.ATR22");
        long id = -1;
        while (res.next()) {
            ps.setLong(1, id);
            for (int i = 2; i <= 3; i++) {
                ps.setObject(i, res.getObject(i - 1));
            }
            ps.execute();
            id--;
        }
    }

    public static void migraLogradourosECeps() throws Exception {
        ResultSet nomesLogradourosUnicos = conexaoAgata.createStatement().executeQuery("SELECT  DISTINCT(NOMLGR) FROM atr13 ");
        PreparedStatement dadosCompletosLogradouros = conexaoAgata.prepareStatement("SELECT  * FROM atr13 LEFT JOIN WEBPUBLICO.TIPOLOGRADOURO ON (trim(ATR13.TPOLGR)=trim(WEBPUBLICO.TIPOLOGRADOURO.DESCRICAO)) WHERE NOMLGR=?");
        PreparedStatement psConsultaCeps = conexaoAgata.prepareStatement("SELECT  CDGLGR,CEPLGR FROM ATR13 WHERE NOMLGR=?");
        long id = 0;
        while (nomesLogradourosUnicos.next()) {
            id--;
            String nomeLogradouro = nomesLogradourosUnicos.getString("NOMLGR");
            dadosCompletosLogradouros.setString(1, nomeLogradouro);
            ResultSet dadosCompletosLogradouro = dadosCompletosLogradouros.executeQuery();
            if (dadosCompletosLogradouro.next()) {
                long idTipoLogradouro = dadosCompletosLogradouro.getLong("ID");
                System.out.print(normalize(nomeLogradouro) + " " + idTipoLogradouro);
                psConsultaCeps.setString(1, nomeLogradouro);
                ResultSet dadosCeps = psConsultaCeps.executeQuery();
                while (dadosCeps.next()) {
                    long codigo = dadosCeps.getLong("CDGLGR");
                    String cep = dadosCeps.getString("CEPLGR");
                    if (cep != null) {
                        cep = cep.trim();
                    }
                    System.out.print(" " + cep);
                }
                //System.out.println();
            }
        }
    }

    public static void listaImoveis() throws Exception {
        ResultSet dadosImoveis = conexaoAgata.createStatement().executeQuery("SELECT * FROM ATR02 LEFT JOIN ATR13 ON (ATR02.LGRCORIMV=ATR13.CDGLGR) LEFT JOIN ATR10 ON (ATR02.CDGSETOR=ATR10.CDGSETOR) ");
        int contaok = 0, contaerrado = 0;
        while (dadosImoveis.next()) {

            String fcequadra = dadosImoveis.getString("fcequadra");
            if (fcequadra != null) {
                fcequadra = fcequadra.trim();
            }
            if (fcequadra != null && fcequadra.length() == 10 && !fcequadra.startsWith("0000")) {
                contaok++;
                System.out.print(dadosImoveis.getString("NMRINS") + " ");
                System.out.print(fcequadra + " " + fcequadra.substring(3, 8));
                //System.out.println(" OK");
            } else {
                System.out.print(dadosImoveis.getString("NMRINS") + " " + fcequadra);
                contaerrado++;
                //System.out.println(" ERRO");
            }
        }
        //System.out.println("contaok:" + contaok + " contaerrado" + contaerrado);

    }

    public static void main(String args[]) {

        try {
            conexaoWebPublico = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.100:1521:orcl", "webpublico", "senha10"); //NOSONAR
            conexaoAgata = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.100:1521:orcl", "riobranco", "senha10"); //NOSONAR
            criaPaises();
            copiaCEPUFparaUF();
            copiaCEPLocalidadeparaCidadeEDistrito();
            agataATR10ParaBairro();
            agataATR19ParaServicoUrano();
            agataATR22ParaTipoLogradouro();
            //migraLogradourosECeps();
            listaImoveis();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public static Pais paises[] = {
            new Pais("Brasil", 55),
            new Pais("Argentina", 54),
            new Pais("Aruba", 297),
            new Pais("ERITREA", 291),
            new Pais("ESLOVENIA", 386),
            new Pais("ESPANHA", 34),
            new Pais("ESTADOS UNIDOS", 1),
            new Pais("ESTONIA", 372),
            new Pais("ETIOPIA", 251),
            new Pais("FAROE ILHAS", 298),
            new Pais("IRAQUE", 964),
            new Pais("IRLANDA", 353),
            new Pais("ISLANDIA", 354),
            new Pais("ISRAEL", 972),
            new Pais("ITALIA", 39),
            new Pais("JAMAICA", 1),
            new Pais("JAPAO", 81),
            new Pais("MACAU", 853),
            new Pais("MACEDONIA", 389),
            new Pais("MADAGASCAR", 261),
            new Pais("MALASIA", 60),
            new Pais("MALAVI", 265),
            new Pais("MALDIVAS", 960),
            new Pais("MALI", 223),
            new Pais("MALTA", 356),
            new Pais("MALVINAS ILHAS", 500),
            new Pais("MARIANAS NORTE ISL.", 1),
            new Pais("MARROCOS", 212),
            new Pais("MARSHALL ILHAS", 692),
            new Pais("MARTINICA", 596),
            new Pais("NIGERIA", 234),
            new Pais("NIUE", 683),
            new Pais("NORFOLK ILHA", 672),
            new Pais("NORUEGA", 47),
            new Pais("NOVA CALEDONIA", 687),
            new Pais("NOVA ZELANDIA", 64),
            new Pais("OMA", 968),
            new Pais("PALAU", 680),
            new Pais("PALESTINA", 970),
            new Pais("PANAMA", 507),
            new Pais("REUNIAO ILHAS", 262),
            new Pais("RODRIGUES ILHAS", 854),
            new Pais("ROMENIA", 40),
            new Pais("RUANDA", 250),
            new Pais("RUSSIA", 7),
            new Pais("URUGUAI", 598),
            new Pais("UZBEQUISTAO", 998),
            new Pais("VANUATU", 678),
            new Pais("VENEZUELA", 58),
            new Pais("VIETNA", 84),
            new Pais("VIRGENS A. ILHAS", 1),
            new Pais("ZAIRE", 243),
            new Pais("ZAMBIA", 260),
            new Pais("ZANZIBAR", 259),
            new Pais("ZIMBABUE", 263)
    };
    private static final char[] FIRST_CHAR =
            (" !'#$%&'()*+\\-./0123456789:;<->?@ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ E ,f'.++^%S<O Z  ''''.-"
                    + "-~Ts>o ZY !C#$Y|$'(a<--(_o+23'u .,1o>113?AAAAAAACEEEEIIIIDNOO"
                    + "OOOXOUUUUyTsaaaaaaaceeeeiiiidnooooo/ouuuuyty").toCharArray();
    private static final char[] SECOND_CHAR =
            ("  '         ,                                               "
                    + "\\                                   $  r'. + o  E      ''  "
                    + "  M  e     #  =  'C.<  R .-..     ..>424     E E            "
                    + "   E E     hs    e e         h     e e     h ").toCharArray();

    /**
     * Efetua as seguintes normalizações:
     * - Elimina acentos e cedilhas dos nomes;
     * - Converte aspas duplas em simples;
     * - Converte algumas letras estrangeiras para seus equivalentes ASCII
     * (como ß = eszet, convertido para ss)
     * - Põe um "\" antes de vírgulas, permitindo nomes como
     * "Verisign, Corp." e de "\", permitindo nomes como " a \ b ";
     * - Converte os sinais de = para -
     * - Alguns caracteres são removidos:
     * -> os superiores a 255,
     * mesmo que possam ser representados por letras latinas normais
     * (como S, = U+015E = Latin Capital Letter S With Cedilla);
     * -> os caracteres de controle (exceto tab, que é trocado por um espaço)
     *
     * @param str A string a normalizar.
     * @return A string normalizada.
     */
    public static String normalize(String str) {
        char[] chars = str.toCharArray();
        StringBuffer ret = new StringBuffer(chars.length * 2);
        for (int i = 0; i < chars.length; ++i) {
            char aChar = chars[i];
            if (aChar == ' ' || aChar == '\t') {
                ret.append(' '); // convertido para espaço
            } else if (aChar > ' ' && aChar < '\u0100') {
                if (FIRST_CHAR[aChar - ' '] != ' ') {
                    ret.append(FIRST_CHAR[aChar - ' ']); // 1 caracter
                }
                if (SECOND_CHAR[aChar - ' '] != ' ') {
                    ret.append(SECOND_CHAR[aChar - ' ']); // 2 caracteres
                }
            }
        }

        return ret.toString();
    }
}
