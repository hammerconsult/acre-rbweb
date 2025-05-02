/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.geradores;

import br.com.webpublico.util.AtributoMetadata;
import br.com.webpublico.util.EntidadeMetaData;

import javax.persistence.ManyToOne;
import javax.swing.*;
import javax.validation.constraints.Size;
import java.io.FileWriter;
import java.util.Date;

/**
 * @author Munif
 */
public class GeraCadastro {

    public static void main(String args[]) {
        try {
            String nomeClasse = JOptionPane.showInputDialog("Digite o nome da entidade");
            Class classe = Class.forName("br.com.webpublico.entidades." + nomeClasse);
            String nomeControle = nomeClasse.substring(0, 1).toLowerCase() + nomeClasse.substring(1);
            EntidadeMetaData metadata = new EntidadeMetaData(classe);


            //Gera facade


            FileWriter fw = new FileWriter("src\\main\\java\\br\\com\\webpublico\\geradores\\" + classe.getSimpleName() + "Facade.java", false);
            fw.write("/*\n"
                    + " * Codigo gerado automaticamente em " + new Date() + "\n"
                    + " * Gerador de Facace" + "\n"
                    + "*/\n\n");
            fw.write("package br.com.webpublico.geradores;\n\n");
            fw.write("import br.com.webpublico.entidades." + classe.getSimpleName() + ";\n");
            fw.write("import br.com.webpublico.negocios.AbstractFacade;\n");
            fw.write("import javax.ejb.Stateless;\n");
            fw.write("import javax.persistence.EntityManager;\n");
            fw.write("import javax.persistence.PersistenceContext;\n");
            fw.write("@Stateless\n");
            fw.write("public class " + classe.getSimpleName() + "Facade extends AbstractFacade<" + classe.getSimpleName() + ">{\n");
            fw.write("@PersistenceContext(unitName = \"webpublicoPU\")\n");
            fw.write("private EntityManager em;\n");
            fw.write("@Override\n");
            fw.write("protected EntityManager getEntityManager() {\n");
            fw.write("return em;\n");
            fw.write("}\n");
            fw.write("public " + classe.getSimpleName() + "Facade() {\n");
            fw.write("super(" + classe.getSimpleName() + ".class);\n");
            fw.write("}\n");
            fw.write("}\n");
            fw.close();

            //Gera lista.xhtml

            FileWriter fw2 = new FileWriter("src\\main\\java\\br\\com\\webpublico\\geradores\\lista.xhtml", false);
            fw2.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            fw2.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//PT\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            fw2.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"\n");
            fw2.write("xmlns:ui=\"http://java.sun.com/jsf/facelets\"\n");
            fw2.write("xmlns:h=\"http://java.sun.com/jsf/html\"\n");
            fw2.write("xmlns:f=\"http://java.sun.com/jsf/core\"\n");
            fw2.write("xmlns:fc=\"http://java.sun.com/jsf/composite/components\"\n");
            fw2.write("xmlns:p=\"http://primefaces.org/ui\"\n");
            fw2.write(">\n");
            fw2.write("<ui:composition template=\"/corpo.xhtml\">\n");
            fw2.write("<ui:define name=\"body\">\n");
            fw2.write("<fc:tabelaGenerica controlador=\"#{" + nomeControle + "Controlador}\" />\n");
            fw2.write("</ui:define>\n");
            fw2.write("</ui:composition>\n");
            fw2.write("</html>\n");
            fw2.close();

            //Gera visualizar.xhtml

            FileWriter fw3 = new FileWriter("src\\main\\java\\br\\com\\webpublico\\geradores\\visualizar.xhtml", false);
            fw3.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            fw3.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//PT\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            fw3.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"\n");
            fw3.write("xmlns:ui=\"http://java.sun.com/jsf/facelets\"\n");
            fw3.write("xmlns:h=\"http://java.sun.com/jsf/html\"\n");
            fw3.write("xmlns:f=\"http://java.sun.com/jsf/core\"\n");
            fw3.write("xmlns:fc=\"http://java.sun.com/jsf/composite/components\"\n");
            fw3.write("xmlns:p=\"http://primefaces.org/ui\"\n");
            fw3.write(">\n");
            fw3.write("<ui:composition template=\"/corpo.xhtml\">\n");
            fw3.write("<ui:define name=\"body\">\n");
            fw3.write("<fc:visualizadorGenerico controlador=\"#{" + nomeControle + "Controlador}\" />\n");
            fw3.write("</ui:define>\n");
            fw3.write("</ui:composition>\n");
            fw3.write("</html>\n");
            fw3.close();

            //Gera edita.xhtml

            FileWriter fw4 = new FileWriter("src\\main\\java\\br\\com\\webpublico\\geradores\\edita.xhtml", false);
            fw4.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            fw4.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//PT\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            fw4.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"\n");
            fw4.write("xmlns:ui=\"http://java.sun.com/jsf/facelets\"\n");
            fw4.write("xmlns:h=\"http://java.sun.com/jsf/html\"\n");
            fw4.write("xmlns:f=\"http://java.sun.com/jsf/core\"\n");
            fw4.write("xmlns:fc=\"http://java.sun.com/jsf/composite/components\"\n");
            fw4.write("xmlns:p=\"http://primefaces.org/ui\"\n");
            fw4.write("xmlns:c=\"http://java.sun.com/jsp/jstl/core\">\n");
            fw4.write("<ui:composition template=\"/corpo.xhtml\">\n");
            fw4.write("<ui:define name=\"body\">\n");
            fw4.write("<p:messages showDetail=\"true\"/>\n");
            fw4.write("<h:form id=\"Formulario\">\n");
            fw4.write("<fc:cabecarioEditar controlador=\"#{" + nomeControle + "Controlador}\"/>\n");
            fw4.write("<p:panel header=\"Cadastro de " + metadata.getNomeEntidade() + "\" >\n");
            fw4.write("<h:panelGrid columns=\"3\">\n");

            for (AtributoMetadata a : metadata.getAtributos()) {
                String atributo = a.getAtributo().getName();
                String atributoRoluto = a.getEtiqueta();
                int tamanho = 70;
                Size s = a.getAtributo().getAnnotation(Size.class);
                if (s != null) {
                    tamanho = s.max();
                }
                ManyToOne mto = a.getAtributo().getAnnotation(ManyToOne.class);

                if (!atributo.equals("id")) {
                    if (mto != null) {
                        fw4.write("<h:outputText value=\"" + atributoRoluto + ":\" />\n");
                        fw4.write("<h:selectOneMenu title=\"Selecione um " + atributo + "\" value=\"#{" + nomeControle + "Controlador.selecionado." + atributo + "}\" id=\"" + atributo + "\" converter=\"#{" + nomeControle + "Controlador.converter" + atributo + "}\" >\n");
                        fw4.write("<f:selectItems value=\"#{" + nomeControle + "Controlador." + atributo + "}\" />\n");
                        fw4.write("</h:selectOneMenu>\n");
                        fw4.write("<p:message for=\"" + atributo + "\" showDetail=\"true\"/>\n");
                    } else {
                        fw4.write("<h:outputText value=\"" + atributoRoluto + ":\" />\n");
                        fw4.write("<p:inputText title=\"Digite o " + atributo + " da " + metadata.getNomeEntidade() + "\" value=\"#{" + nomeControle + "Controlador.selecionado." + atributo + "}\" id=\"" + atributo + "\" maxlength=\"" + tamanho + "\" size=\"" + tamanho + "\"/>\n");
                        fw4.write("<p:message for=\"" + atributo + "\" showDetail=\"true\" />\n");
                    }
                }
            }

            fw4.write("</h:panelGrid>\n");
            fw4.write("</p:panel>\n");
            fw4.write("<fc:rodapeEditar controlador=\"#{" + nomeControle + "Controlador}\"/>\n");
            fw4.write("</h:form>\n");
            fw4.write("</ui:define>\n");
            fw4.write("</ui:composition>\n");
            fw4.write("</html>\n");
            fw4.close();


            //Gera Controlador

            FileWriter fw5 = new FileWriter("src\\main\\java\\br\\com\\webpublico\\geradores\\" + classe.getSimpleName() + "Controlador.java", false);
            fw5.write("/*\n"
                    + " * Codigo gerado automaticamente em " + new Date() + "\n"
                    + " * Gerador de Controlador" + "\n"
                    + "*/\n\n");
            fw5.write("package br.com.webpublico.controle;\n");
            fw5.write("\n\n");
            fw5.write("import br.com.webpublico.entidades." + nomeClasse + ";\n");

            for (AtributoMetadata a2 : metadata.getAtributos()) {
                String atributo2 = a2.getAtributo().getName();
                String atributoRoluto2 = atributo2.substring(0, 1).toUpperCase() + atributo2.substring(1);
                ManyToOne mto2 = a2.getAtributo().getAnnotation(ManyToOne.class);
                if (mto2 != null) {
                    fw5.write("import br.com.webpublico.entidades." + atributoRoluto2 + ";\n");
                    fw5.write("import br.com.webpublico.negocios." + atributoRoluto2 + "Facade;\n");
                }
            }

            fw5.write("import br.com.webpublico.negocios.AbstractFacade;\n");
            fw5.write("import br.com.webpublico.negocios." + nomeClasse + "Facade;\n");
            fw5.write("import br.com.webpublico.util.ConverterGenerico;\n");
            fw5.write("import br.com.webpublico.util.EntidadeMetaData;\n");
            fw5.write("import java.io.Serializable;\n");
            fw5.write("import java.util.LinkedList;\n");
            fw5.write("import java.util.List;\n");
            fw5.write("import javax.ejb.EJB;\n");
            fw5.write("import javax.faces.bean.ManagedBean;\n");
            fw5.write("import javax.faces.bean.SessionScoped;\n");
            fw5.write("import javax.faces.model.SelectItem;\n");
            fw5.write("\n\n");
            fw5.write("\n\n");
            fw5.write("@ManagedBean\n");
            fw5.write("@SessionScoped\n");
            fw5.write("public class " + nomeClasse + "Controlador extends SuperControladorCRUD implements Serializable {\n");
            fw5.write("\n");
            fw5.write("@EJB\n");
            fw5.write("private " + nomeClasse + "Facade " + nomeControle + "Facade;\n");

            for (AtributoMetadata a3 : metadata.getAtributos()) {
                String atributo3 = a3.getAtributo().getName();
                String atributoUP3 = atributo3.substring(0, 1).toUpperCase() + atributo3.substring(1);

                ManyToOne mto3 = a3.getAtributo().getAnnotation(ManyToOne.class);
                if (mto3 != null) {
                    fw5.write("@EJB\n");
                    fw5.write("private " + atributoUP3 + "Facade " + atributo3 + "Facade;\n");
                    fw5.write("private ConverterGenerico converter" + atributoUP3 + ";\n");
                }
            }

            fw5.write("\n");
            fw5.write("public " + nomeClasse + "Controlador() {\n");
            fw5.write("metadata = new EntidadeMetaData(" + nomeClasse + ".class);\n");
            fw5.write("}\n");
            fw5.write("\n");
            fw5.write("public " + nomeClasse + "Facade getFacade() {\n");
            fw5.write("return " + nomeControle + "Facade;\n");
            fw5.write("}\n");
            fw5.write("\n");
            fw5.write("@Override\n");
            fw5.write("public AbstractFacade getFacede() {\n");
            fw5.write("return " + nomeControle + "Facade;\n");
            fw5.write("}\n");
            fw5.write("\n");

            for (AtributoMetadata a4 : metadata.getAtributos()) {
                String atributo4 = a4.getAtributo().getName();
                String atributoUP4 = atributo4.substring(0, 1).toUpperCase() + atributo4.substring(1);

                ManyToOne mto4 = a4.getAtributo().getAnnotation(ManyToOne.class);
                if (mto4 != null) {
                    fw5.write("public List<SelectItem> get" + atributoUP4 + "() {\n");
                    fw5.write("List<SelectItem> toReturn = new ArrayList<SelectItem>();\n");
                    fw5.write("for (" + atributoUP4 + " object : " + atributo4 + "Facade.lista()) {\n");
                    fw5.write("toReturn.add(new SelectItem(object, object.get" + atributoUP4 + "()));\n");
                    fw5.write("}\n");
                    fw5.write("return toReturn;\n");
                    fw5.write("}\n");
                    fw5.write("\n");
                    fw5.write("public ConverterGenerico getConverter" + atributoUP4 + "() {\n");
                    fw5.write("if (converter" + atributoUP4 + " == null) {\n");
                    fw5.write("converter" + atributoUP4 + " = new ConverterGenerico(" + atributoUP4 + ".class, " + atributo4 + "Facade);\n");
                    fw5.write("}\n");
                    fw5.write("return converter" + atributoUP4 + ";\n");
                    fw5.write("}\n");
                }
            }

            fw5.write("\n");
            fw5.write("}\n");
            fw5.close();


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }
}
