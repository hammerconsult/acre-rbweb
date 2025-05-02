/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.geradores;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author cheles
 */
public class GeraChangeSetForeignKey {

    private int id;
    private int count;
    private String author;
    private String baseTableName;
    private String baseColumnNames;
    private String constraintName;
    private String referencedTableName;
    private String referencedColumnNames;
    private String indexName;
    private static final int INDEX = 1;
    private static final int FOREIGN_KEY = 2;

    public static void main(String[] args) {
        new GeraChangeSetForeignKey().gerarChangeSets();
    }

    public void gerarChangeSets() {
        obterAuthorEId();

        gerarChangeSetsPrimaryKey();
        gerarChangeSetsForeignKey();
    }

    private void gerarChangeSetsPrimaryKey() {
        for (ArmazemDeTabelasEColunas armazem : obterArmazensPrimaryKey()) {
            atribuirVariaveisPrimaryKey(armazem.tabela, armazem.coluna);
            //System.out.println(obterChangeSetPrimaryKey());
            id++;
        }
    }

    private void gerarChangeSetsForeignKey() {
        for (ArmazemDeTabelasEColunas armazem : obterArmazensForeignKey()) {
            count++;
            Class classePrincipal = null;
            String tabelaPrincipal = armazem.tabela;
            try {
                classePrincipal = obterClasse(tabelaPrincipal);
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }

            String colunaPrincipal = armazem.coluna;

            if (classePrincipal != null) {
                Field atributoPrincipal = obterAtributo(classePrincipal, colunaPrincipal);
                if (atributoPrincipal != null) {
                    atribuirVariaveisForeignKey(tabelaPrincipal, colunaPrincipal, atributoPrincipal.getType());
                    //System.out.println(obterChangeSetForeignKey());
                    id++;
                    if (!armazem.temIndice) {
                        atribuirVariaveisIndex();
                        //System.out.println(obterChangeSetCreateIndex());
                        id++;
                    }
                }
            }
        }
    }

    private void obterAuthorEId() throws NumberFormatException, HeadlessException {
        author = JOptionPane.showInputDialog(null, "Informe o nome do autor:");
        String idString = JOptionPane.showInputDialog(null, "Informe o número do id inicial para os changeSets:");
        id = Integer.parseInt(idString);
    }

    private void atribuirVariaveisPrimaryKey(String tabela, String coluna) {
        baseTableName = tabela.toUpperCase();
        baseColumnNames = coluna.toUpperCase();
        constraintName = "PK_" + baseTableName;
        constraintName = tratarTamanhoMaiorQue30(constraintName);
    }

    private void atribuirVariaveisForeignKey(String tabelaPrincipal, String colunaPrincipal, Class classeEstrangeira) {
        baseTableName = tabelaPrincipal.toUpperCase();
        baseColumnNames = colunaPrincipal.toUpperCase();
        referencedTableName = obterNomeDaTabela(classeEstrangeira).toUpperCase();
        referencedColumnNames = "ID";
        constraintName = "FK_" + count + baseTableName.substring(0, 3) + "_" + referencedTableName;
        constraintName = tratarTamanhoMaiorQue30(constraintName);
    }

    private void atribuirVariaveisIndex() {
        indexName = constraintName.replace("FK_", "IDX_");
        indexName = tratarTamanhoMaiorQue30(indexName);
    }

    private String tratarTamanhoMaiorQue30(String nome) {
        if (nome.length() > 30) {
            nome = nome.substring(0, 30);
        }

        return nome;
    }

    private Class obterClasse(String nomeClasse) throws ClassNotFoundException, IOException {
        Class classe = null;

        try {
            classe = Class.forName("br.com.webpublico.entidades." + nomeClasse);
        } catch (NoClassDefFoundError ex) {
            String[] split = ex.getMessage().split("/");
            nomeClasse = split[split.length - 1];
            nomeClasse = nomeClasse.substring(0, nomeClasse.length() - 1);
            classe = Class.forName("br.com.webpublico.entidades." + nomeClasse);
        } catch (ClassNotFoundException ex1) {
            for (Class class1 : getClasses("br.com.webpublico.entidades")) {
                Annotation annotation = class1.getAnnotation(Table.class);
                if (annotation != null && ((Table) annotation).name().equalsIgnoreCase(nomeClasse)) {
                    return class1;
                }
            }
        }

        return classe;
    }

    private String tratarNomeDaColuna(String nomeColuna) {
        if (nomeColuna.endsWith("_ID")) {
            return nomeColuna.substring(0, nomeColuna.length() - 3);
        } else {
            return nomeColuna;
        }
    }

    private String obterChangeSetPrimaryKey() {
        return "<changeSet id=\"" + id + "\" author=\"" + author + "\">\n"
                + "    <addPrimaryKey columnNames=\"" + baseColumnNames + "\" constraintName=\"" + constraintName + "\" tableName=\"" + baseTableName + "\" />\n"
                + "</changeSet>";
    }

    private String obterChangeSetForeignKey() {
        return "<changeSet id=\"" + id + "\" author=\"" + author + "\">\n"
                + "    <preConditions onFail=\"MARK_RAN\">\n"
                + "        <sqlCheck expectedResult=\"0\">" + obterQueryQueVerificaExistenciaDaConstraintOuIndex(GeraChangeSetForeignKey.FOREIGN_KEY) + "</sqlCheck>\n"
                + "    </preConditions>\n"
                + "    <addForeignKeyConstraint baseColumnNames=\"" + baseColumnNames + "\"\n"
                + "                             baseTableName=\"" + baseTableName + "\"\n"
                + "                             constraintName=\"" + constraintName + "\"\n"
                + "                             referencedColumnNames=\"" + referencedColumnNames + "\"\n"
                + "                             referencedTableName=\"" + referencedTableName + "\" />\n"
                + "</changeSet>";
    }

    private String obterQueryQueVerificaExistenciaDaConstraintOuIndex(int tipo) {
        String tabela = null;

        switch (tipo) {
            case GeraChangeSetForeignKey.FOREIGN_KEY:
                tabela = "USER_CONS_COLUMNS";
                break;
            case GeraChangeSetForeignKey.INDEX:
                tabela = "USER_IND_COLUMNS";
                break;
        }

        return "SELECT COUNT(1) FROM " + tabela + " WHERE TABLE_NAME = '" + baseTableName + "' AND COLUMN_NAME = '" + baseColumnNames + "'";
    }

    private String obterChangeSetCreateIndex() {
        return "<changeSet id=\"" + id + "\" author=\"" + author + "\">\n"
                + "    <preConditions onFail=\"MARK_RAN\">\n"
                + "        <sqlCheck expectedResult=\"0\">" + obterQueryQueVerificaExistenciaDaConstraintOuIndex(GeraChangeSetForeignKey.INDEX) + "</sqlCheck>\n"
                + "    </preConditions>\n"
                + "    <createIndex tableName=\"" + baseTableName + "\" indexName=\"" + indexName + "\">\n"
                + "        <column name=\"" + baseColumnNames + "\"/>\n"
                + "    </createIndex>\n"
                + "</changeSet>";
    }

    private Field obterAtributo(Class classe, String colunaPrincipal) {
        for (Field field : classe.getDeclaredFields()) {
            try {
                if (field.getName().equalsIgnoreCase(tratarNomeDaColuna(colunaPrincipal))) {
                    return field;
                }

                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null && annotation.name().equalsIgnoreCase(colunaPrincipal)) {
                    return field;
                }

                JoinColumn annotation2 = field.getAnnotation(JoinColumn.class);
                if (annotation2 != null && annotation2.name().equalsIgnoreCase(colunaPrincipal)) {
                    return field;
                }
            } catch (Exception e) {
//                //System.out.println("NÃO FOI POSSÍVEL RECUPERAR O ATRIBUTO REFERENTE A COLUNA " + colunaPrincipal + ", DA CLASSE " + classe.getSimpleName());
//                e.printStackTrace();
//                continue;
            }
        }

        return null;
    }

    private String obterNomeDaTabela(Class classe) {
        Annotation annotation = classe.getAnnotation(Table.class);
        if (annotation != null) {
            String name = ((Table) annotation).name();
            return name.equals("") ? classe.getSimpleName() : name;
        } else {
            return classe.getSimpleName();
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base
     *                    directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private List<ArmazemDeTabelasEColunas> obterArmazensPrimaryKey() {
        List<ArmazemDeTabelasEColunas> lista = new ArrayList<>();

        lista.add(new ArmazemDeTabelasEColunas("ARQUIVOAIDF", "ID"));
        lista.add(new ArmazemDeTabelasEColunas("CEPBAIRRO", "ID"));
        lista.add(new ArmazemDeTabelasEColunas("CEPLOCALIDADE", "ID"));
        lista.add(new ArmazemDeTabelasEColunas("CEPLOGRADOURO", "ID"));
        lista.add(new ArmazemDeTabelasEColunas("CEPUF", "ID"));
        lista.add(new ArmazemDeTabelasEColunas("LOTECONDOMINIO", "ID"));
        lista.add(new ArmazemDeTabelasEColunas("OCORRENCIACALCULOIPTU", "ID"));
        lista.add(new ArmazemDeTabelasEColunas("RELATORIOGRUPOITEMDEMONS", "ID"));
        lista.add(new ArmazemDeTabelasEColunas("VAGA", "ID"));

        return lista;
    }

    private List<ArmazemDeTabelasEColunas> obterArmazensForeignKey() {
        List<ArmazemDeTabelasEColunas> lista = new ArrayList<>();

        lista.add(new ArmazemDeTabelasEColunas("ARQUIVOUSUARIOSISTEMA", "ARQUIVO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ARQUIVOUSUARIOSISTEMA", "USUARIOSISTEMA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("AUTUACAOFISCALIZACAO", "ANALISEAUTUACAORBTRANS_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CALCULOLANCAMENTOOUTORGA", "TRIBUTO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CALCULOLICITACAOCEASA", "CONTRATO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CALCULOLICITACAOCEASA", "PROCESSOCALCULO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CARTEIRATRABALHO", "BANCOPISPASEP_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CATEGORIAISENCAOIPTU", "EXERCICIOINICIAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CATEGORIAISENCAOIPTU", "TIPODOCTOOFICIAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CATEGORIAISENCAOIPTU", "EXERCICIOFINAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CEPBAIRRO", "LOCALIDADE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CEPLOCALIDADE", "UF_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CEPLOGRADOURO", "BAIRRO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CEPLOGRADOURO", "LOCALIDADE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CERTIDAOATIVIDADEBCE", "USUARIOSISTEMAFISCAL_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CERTIDAOCASAMENTO", "NACIONALIDADE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CERTIDAOCASAMENTO", "CIDADECONJUGE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CLPCONFIGURACAOPARAMETRO", "CLPTAG_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CLPCONFPLACLPCONFITEM", "CLPCONFIGPLANOCONTAS_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CLPCONFPLACLPCONFITEM", "CLPCONFIGURACAOITEM_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CONCESSAOFERIASLICENCA", "ATOLEGAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CONCESSAOFERIASLICENCA", "ARQUIVO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CONFIGURACAOTRIBUTARIO", "RENDASLOTACAOVISTORIADORA_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONFIGURACAOTRIBUTARIO", "CEASADOCTOOFICIALCONTRATO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONFIGURACAOTRIBUTARIO", "CEASATRIBUTOLICITACAO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONFIGURACAOTRIBUTARIO", "CEASADIVIDALICITACAO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONFIGURACAOTRIBUTARIO", "CEASATRIBUTOCONTRATO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONFIGURACAOTRIBUTARIO", "CEASADIVIDACONTRATO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONFIGURACAOTRIBUTARIO", "CEASALOTACAOVISTORIADORA_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONFIGURACAOTRIBUTARIO", "RENDASTIPODOCTOOFICIALPJ_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONFIGURACAOTRIBUTARIO", "RENDASTIPODOCTOOFICIALPF_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONTARECEITA", "CORRESPONDENTE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CONTATRIBUTORECEITA", "TRIBUTO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CONTRATO", "CLASSECREDOR_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("CONTRATOFP", "ATOLEGAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CONTRATORENDASPATRIMONIAIS", "ORIGINARIO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CONTRATOVP", "CONTRATANTE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CONTRATOVP", "VEICULODEPUBLICACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CONVENIODESPESA", "VEICULODEPUBLICACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("COTACAOSOLICITACAO", "COTACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("COTACAOSOLICITACAO", "SOLICITACAOMATERIAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("CREDITORECEBER", "EVENTOCONTABIL_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("DENUNCIAOCORRENCIAS", "DENUNCIA_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("DESCONTOOPCAOPAGAMENTO", "OPCAOPAGAMENTO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("DIVIDAATIVACONTABIL", "EVENTOCONTABIL_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("DOTITEMSOLMAT", "ITEMSOLICITACAOMATERIAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("DOTITEMSOLMAT", "DOTACAOSOLICITACAOMAT_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("DOTSOLMAT", "SOLICITACAOMATERIAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("DOTSOLMAT", "CONTADEDESPESA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("DOTSOLMAT", "FONTEDESPESAORC_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("EMPENHO", "EVENTOCONTABIL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("EMPENHOFICHAFINANCEIRAFP", "EMPENHO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("FATOCONTABILEMPENHO", "EMPENHO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("FATOCONTABILEMPENHO", "FATOCONTABIL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("FATOCONTABILLIQUIDACAO", "FATOCONTABIL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("FATOCONTABILLOA", "FATOCONTABIL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("FATOCONTABILPAGAMENTO", "FATOCONTABIL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("FORMULAITEMDEMONSTRATIVO", "EXERCICIO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("GRUPORECURSOSISTEMA", "GRUPORECURSO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("GRUPORECURSOSISTEMA", "RECURSOSISTEMA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("INFRACAOFUNDAMENTACAOLEGAL", "INFRACAOFISCALIZACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("INFRACAOFUNDAMENTACAOLEGAL", "FUNDAMENTACAOLEGAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMCALCULOISS", "TRIBUTO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("ITEMCOTACAO", "COTACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMCOTACAOSOLICITACAO", "ITEMCOTACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMCOTACAOSOLICITACAO", "ITEMSOLICITACAOMATERIAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMDEMONSTRATIVO", "EXERCICIO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMENTRADAMATERIAL", "MOVIMENTOESTOQUE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMGRUPOUSUARIO", "GRUPOUSUARIO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMGRUPOUSUARIO", "GRUPORECURSO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMPARCELAVALORDIVIDA", "DESCONTO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMSAIDAMATERIAL", "MOVIMENTOESTOQUE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMSOLICITACAO", "LOTESOLICITACAOMATERIAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMSOLICITACAO", "UNIDADEMEDIDA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("ITEMSOLICITACAO", "COTACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("LANCAMENTOCONTABILFISCAL", "REGISTROLANCAMENTOCONTABIL_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("LANCAMENTORECEITAORC", "RECEITALOA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("LANCAMENTORECEITAORC", "EVENTOCONTABIL_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("LICITACAOFORNECEDOR", "REPRESENTANTE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("LICITACAOFORNECEDOR", "LICITACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("LICITACAOFORNECEDOR", "EMPRESA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("LICITMEMBCOM", "LICITACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("LICITMEMBCOM", "MEMBROCOMISSAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("LOTACAOFUNCIONAL", "RETORNOCEDENCIACONTRATOFP_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("LOTE", "BAIRRO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("LOTESOLICITACAOMATERIAL", "SOLICITACAOMATERIAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("MATERIAL", "UNIDADEMEDIDA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("NFSAVULSA", "TOMADOR_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("OPCAOPAGAMENTODIVIDA", "OPCAOPAGAMENTO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PAGAMENTOLIBERACAOCOTA", "LIBERACAOCOTAFINANCEIRA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PAGTOEXTRALIBCOTA", "LIBERACAOCOTAFINANCEIRA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARAMETOSPETICAO", "FUNCIONARIORESPONSAVEL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARAMETOSPETICAO", "DOCTOPESSOAFISICA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARAMETOSPETICAO", "DOCTOPESSOAJURIDICA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARAMETROFISCALIZACAO", "SECRETARIA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARAMETRORENDAS", "EXERCICIO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARAMETRORENDAS", "INDICEECONOMICO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARAMETROSEXERCICIO", "EXERCICIO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARAMOUTORGARBTRANS", "TRIBUTO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARCELAVALORDIVIDA", "OPCAOPAGAMENTO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARECERLICITACAO", "LICITACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PARECERLICITACAO", "PESSOA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PENHORA", "AUTOR_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PENHORA", "CADASTROIMOBILIARIO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PESSOAFISICA", "NACIONALIDADEPAI_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PESSOAFISICA", "ARQUIVO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PESSOAFISICA", "NACIONALIDADEMAE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PLACAAUTOMOVELCMC", "CADASTROECONOMICO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PROCESSOCALCULOMOTAUX", "MOTORISTAAUXILIAR_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PROPOSTAFORNECEDOR", "REPRESENTANTE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PUBLICACAOLICITACAO", "LICITACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("PUBLICACAOLICITACAO", "VEICULODEPUBLICACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("RECEITAPPACONTAS", "RECEITAPPA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("RECEITAPPACONTAS", "CONTARECEITA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("RECURSOLICITACAO", "LICITACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("RECURSOLICITACAO", "INTERPONENTE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("REGISTRODEOBITO", "ARQUIVO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("SOLICITACAODOCTOOFICIAL", "TIPODOCTOOFICIAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("SOLICITACAOMATERIAL", "UNIDADEORGANIZACIONAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("SUBASSUNTODOCUMENTO", "SUBASSUNTO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("SUBASSUNTODOCUMENTO", "DOCUMENTO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("TEMP_ACRESCIMOS", "PARCELA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("TIPODENUNCIAINFRACAO", "TIPODENUNCIA_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("TIPODENUNCIAINFRACAO", "INFRACAOFISCALIZACAO_ID", false));
        lista.add(new ArmazemDeTabelasEColunas("TRANSFERENCIAPERMTRANS", "PERMISSAONOVA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("TRANSFERENCIAPERMTRANS", "SOLICITANTE_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("USUARIOUNIDADEORG", "UNIDADEORGANIZACIONAL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("USUARIOUNIDADEORG", "USUARIOSISTEMA_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("VALORCOTACAO", "ITEMCOTACAO_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("VALORCOTACAO", "FORNECEDOR_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("VEICULOTRANSPORTE", "COMBUSTIVEL_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("VINCULOFP", "SINDICATOVINCULOFP_ID", true));
        lista.add(new ArmazemDeTabelasEColunas("VISTORIAFISCALIZACAO", "USUARIOSISTEMA_ID", false));

        return lista;
    }

    private class ArmazemDeTabelasEColunas {

        String tabela;
        String coluna;
        boolean temIndice;

        public ArmazemDeTabelasEColunas(String tabela, String coluna) {
            this.tabela = tabela;
            this.coluna = coluna;
        }

        public ArmazemDeTabelasEColunas(String tabela, String coluna, boolean temIndice) {
            this.tabela = tabela;
            this.coluna = coluna;
            this.temIndice = temIndice;
        }
    }
}
