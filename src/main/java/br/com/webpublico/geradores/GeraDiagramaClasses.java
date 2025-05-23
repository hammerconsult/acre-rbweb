package br.com.webpublico.geradores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Menu;
import br.com.webpublico.singletons.SingletonMenu;
import br.com.webpublico.util.ClassFinder;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class GeraDiagramaClasses {

    public static final String DEMAIS = "Demais";
    private List<String> associacoes = new ArrayList<String>();
    private FileWriter fileWriter;
    private List<Class> enums;
    private Map<String, List<Class>> grupos;

    public GeraDiagramaClasses() {

    }

    public void gerar() {
        try {
            grupos = new HashMap<String, List<Class>>();
            grupos.put(DEMAIS, new ArrayList<Class>());
            enums = new ArrayList<Class>();
            grupos = procurarModulos();
            for (String grupo : grupos.keySet()) {
                try {
                    associacoes.clear();
                    fileWriter = new FileWriter("C:\\tools\\projetos\\riobranco\\src\\main\\resources\\br\\com\\webpublico\\geradordiagramas\\dot\\" + grupo + ".dot", false);
                    escreveCabecalho(fileWriter);

                    fileWriter.write("subgraph cluster" + grupo + "\n{\n");
                    for (Class entidade : grupos.get(grupo)) {
                        try {
                            criaClasse(entidade, fileWriter);
                            verificaHeranca(entidade);
                        } catch (Exception e) {
                            System.out.println("Não foi possível gerar a entidade " + entidade);
                            e.printStackTrace();
                        }
                    }

                    fileWriter.write("}\n");

                    for (String a : associacoes) {
                        fileWriter.write(a + "\n");
                    }
                    fileWriter.write("}\n");
                    fileWriter.close();

                } catch (Exception ex) {
                    System.out.println("Deu pau no " + grupo + " " + ex.getLocalizedMessage());
                }
            }

            File pasta = new File("C:/tools/projetos/riobranco/src/main/resources/br/com/webpublico/geradordiagramas/dot");
            System.out.println("PASTA " + pasta.getName());

            FileWriter executavel = new FileWriter("C:\\tools\\projetos\\riobranco\\src\\main\\resources\\br\\com\\webpublico\\geradordiagramas\\dot\\executavel.bat", false);


            for (File file : pasta.listFiles()) {
                if (file.getName().contains(".dot")) {
                    executavel.write("\"C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot\" -T png -o " + file.getName().replace(".dot", ".png") + "  " + file.getName() + "\n");
                }
            }

            executavel.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void escreveCabecalho(FileWriter fileWriter) throws IOException {
        fileWriter.write("digraph G{\n"
            + "fontname = \"Times-Roman\"\n"
            + "fontsize = 8\n\n"
            + "node [\n"
            + "        fontname = \"Times-Roman\"\n"
            + "        fontsize = 8\n"
            + "        shape = \"record\"\n"
            + "]\n\n"
            + "edge [\n"
            + "        fontname = \"Times-Roman\"\n"
            + "        fontsize = 8\n"
            + "]\n\n");
    }

    private void adicionaClasse(File arquivo) throws ClassNotFoundException {
        if (arquivo.isDirectory()) {
            for (File f : arquivo.listFiles()) {
                adicionaClasse(f);
            }
            return;
        }
        String nomeArquivoSimples = arquivo.getAbsolutePath();
        int posicao = nomeArquivoSimples.indexOf("\\" + "br");
        nomeArquivoSimples = nomeArquivoSimples.substring(posicao + 1);
        nomeArquivoSimples = nomeArquivoSimples.replace("\\" + "", ".");
        nomeArquivoSimples = nomeArquivoSimples.replace(".class", "");
        Class entidade = Class.forName(nomeArquivoSimples);
        if (entidade.isEnum()) {
            enums.add(entidade);
        } else {
            if (entidade.isAnnotationPresent(GrupoDiagrama.class)) {
                GrupoDiagrama gd = (GrupoDiagrama) entidade.getAnnotation(GrupoDiagrama.class);
                String nome = gd.nome();
                if (grupos.get(nome) == null) {
                    grupos.put(nome, new ArrayList<Class>());
                }
                grupos.get(nome).add(entidade);
            } else {
                grupos.get(DEMAIS).add(entidade);
            }
        }
    }

    private void verificaHeranca(Class entidade) {
        if (entidade.getSuperclass() != null && entidade.getSuperclass().getCanonicalName().contains("entidades")) {
            associacoes.add("edge [ arrowhead = \"empty\" headlabel = \"\" taillabel = \"\"] " + entidade.getSimpleName() + " -> " + entidade.getSuperclass().getSimpleName());
        }
    }

    private void criaClasse(Class entidade, FileWriter fw) throws Exception {
        String cor = "";
        if (entidade.isAnnotationPresent(CorEntidade.class)) {
            CorEntidade ce = (CorEntidade) entidade.getAnnotation(CorEntidade.class);
            cor = "style=bold,color=\"" + ce.value() + "\"";
        }
        fw.write(entidade.getSimpleName() + " [" + cor + "label = \"{" + entidade.getSimpleName() + "|");
        Field atributos[] = entidade.getDeclaredFields();
        //System.out.println(entidade.getSimpleName());
        int i = 0;
        for (Field f : atributos) {
            i++;
            Class tipoAtributo = f.getType();
            String nomeCompleto = tipoAtributo.getCanonicalName();
            String tipo = tipoAtributo.getSimpleName();
            String nomeAtributo = f.getName();
            if ((f.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            if (f.getType().equals(List.class)) {
                ParameterizedType type = (ParameterizedType) f.getGenericType();
                Class tipoGenerico = (Class<?>) type.getActualTypeArguments()[0];
                associacoes.add("edge [arrowhead = \"none\" headlabel = \"*\" taillabel = \"*\"] "
                    + entidade.getSimpleName() + " -> " + tipoGenerico.getSimpleName()
                    + " [label = \"" + nomeAtributo + "\"]");


            } else if (nomeCompleto.contains("entidades")) {
                if (f.isAnnotationPresent(ManyToOne.class)) {
                    associacoes.add("edge [arrowhead = \"none\" headlabel = \"1\" taillabel = \"*\"] " + entidade.getSimpleName() + " -> " + tipo + " [label = \"" + nomeAtributo + "\"]");
                } else if (f.isAnnotationPresent(OneToOne.class)) {
                    OneToOne oo = f.getAnnotation(OneToOne.class);
                    if (oo.mappedBy().length() == 0) {
                        associacoes.add("edge [arrowhead = \"none\" headlabel = \"1\" taillabel = \"1\"] " + entidade.getSimpleName() + " -> " + tipo + " [label = \"" + nomeAtributo + "\"]");
                    }

                } else {
                    // fw.write(nomeAtributo + ":" + tipo + "\\l");
                }

            } else {
                // name  : string\l+ age : int\l
                //fw.write(nomeAtributo + ":" + tipo + "\\l");
            }
            if (f.getType().equals(List.class) || f.getType().equals(Set.class) || f.getType().equals(Map.class)) {
                ParameterizedType type = (ParameterizedType) f.getGenericType();
                Type[] typeArguments = type.getActualTypeArguments();
                Type tipoGenerico = typeArguments[f.getType().equals(Map.class) ? 1 : 0];
                fw.write(nomeAtributo + ":" + tipo + " " + tipoGenerico.getClass().getSimpleName() + "\\l");

            } else {
                fw.write(nomeAtributo + ":" + tipo + "\\l");
            }
        }
        fw.write("|\\l}\"]\n");
    }

    public static void gerarDiagramas() {
        System.out.println("INICIO gerarDiagramas!");
        new GeraDiagramaClasses().gerar();
        System.out.println("FIM gerarDiagramas!");
    }

    public Map<String, List<Class>> procurarModulos() {
        List<Class<?>> controladores = ClassFinder.findClassByReflection("br.com.webpublico.controle");
        Map<String, List<Class>> grupoEntidades = Maps.newHashMap();

        SingletonMenu menuFacade = (SingletonMenu) Util.getSpringBeanPeloNome("singletonMenu");

        List<Menu> itensParaAutoComplete = menuFacade.getMenus();

        for (Menu menu : itensParaAutoComplete) {
            if (menu.getCaminho() == null) {
                continue;
            }

            for (Class<?> controlador : controladores) {
                try {
                    if (!controlador.getSuperclass().equals(PrettyControlador.class)) {
                        continue;
                    }
                    if (!controlador.isAnnotationPresent(URLMappings.class)) {
                        continue;
                    }
                    URLMappings mapp = controlador.getAnnotation(URLMappings.class);
                    boolean continuar = false;
                    for (URLMapping urlMapping : mapp.mappings()) {
                        if (urlMapping.viewId().replace("/faces", "").equals(menu.getCaminho())) {
                            continuar = true;
                        }
                    }
                    if (!continuar) {
                        continue;
                    }

                    ParameterizedType genericSuperclass = (ParameterizedType) controlador.getGenericSuperclass();
                    Class<?> entidade = (Class<?>) genericSuperclass.getActualTypeArguments()[0];
                    List<Class<?>> entidades = Lists.newArrayList();
                    entidades.add(entidade);
                    for (Field f : entidade.getDeclaredFields()) {
                        if (f.getType().equals(List.class)) {
                            ParameterizedType listType = (ParameterizedType) f.getGenericType();
                            Class<?> associoativa = (Class<?>) listType.getActualTypeArguments()[0];
                            System.out.println("Tipo " + associoativa);
                            entidades.add(associoativa);
                        }
                    }


                    String label = menu.getLabel();
                    label = StringUtil.removeCaracteresEspeciaisSemEspaco(label).trim()
                        .replace(" ", "_");
                    if (grupoEntidades.get(label) == null) {
                        grupoEntidades.put(label, new ArrayList<Class>());
                    }
                    for (Class<?> aClass : entidades) {
                        if (!grupoEntidades.get(label).contains(aClass)) {
                            grupoEntidades.get(label).add(aClass);
                        }

                    }
                } catch (Exception e) {
                    System.out.println("Deu pau no " + controlador.getSimpleName() + " " + e.getLocalizedMessage());
                }
            }
        }

        return grupoEntidades;
    }


}
