/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.geradores;

import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author munif
 */
public class GeraChangeSet {

    String novasClasses[];
    private String mensagem;
    private String resultado;

    public GeraChangeSet(String entidades) {
        if (entidades != null && !entidades.isEmpty()) {
            novasClasses = entidades.split(",");
        }
    }

    public String getResultado() {
        return resultado;
    }

    private String espacos(int n) {
        String retorno = "";
        for (int i = 0; i < n; i++) {
            retorno += " ";

        }
        return retorno;
    }

    public GeraChangeSet gerar() {
        resultado = "";


        resultado += ("<changeSet id=\"\" author=\"geradorAutomatico\" >\n");
        mensagem = " ";
        try {
            for (String nova : novasClasses) {
                Class classe = Class.forName(nova.trim());
                resultado += (espacos(2) + "<createTable tableName=\"" + classe.getSimpleName().toUpperCase() + "\">\n");
                for (Field f : classe.getDeclaredFields()) {
                    if ("serialVersionUID".equals(f.getName())) {
                        continue;
                    }
                    String tipo = f.getType().toString();
                    String tipoLiquibase = "";
                    //verifica tipo
                    if (f.getType().equals(BigDecimal.class)) {
                        tipoLiquibase = "${monetario}";
                    } else if (f.getType().equals(Long.class)) {
                        tipoLiquibase = "${numero}";
                    } else if (f.getType().equals(Double.class)) {
                        tipoLiquibase = "${decimal}";
                    } else if (f.getType().equals(Integer.class)) {
                        tipoLiquibase = "${numero}";
                    } else if (f.getType().equals(Boolean.class)) {
                        tipoLiquibase = "${logico}";
                    } else if (f.getType().equals(String.class)) {
                        tipoLiquibase = "${descricaolonga}";
                    } else if (f.getType().equals(Date.class)) {
                        Temporal d = f.getAnnotation(Temporal.class);
                        if (d.value() == TemporalType.TIMESTAMP) {
                            tipoLiquibase = "${datatempo}";
                        } else if (d.value() == TemporalType.DATE) {
                            tipoLiquibase = "${data}";
                        } else if (d.value() == TemporalType.TIME) {
                            tipoLiquibase = "${tempo}";
                        }
                    } else {
                        tipoLiquibase = f.getType().toString();
                    }

                    if (!tipo.contains("entidades")) {
                        if (!f.getType().equals(List.class)) {
                            resultado += (espacos(4) + "<column name=\"" + f.getName().toUpperCase() + "\" type=\"" + tipoLiquibase + "\">\n");
                            if (f.isAnnotationPresent(Id.class)) {
                                resultado += (espacos(6) + "<constraints nullable=\"false\" primaryKey=\"true\" primaryKeyName=\"PK_" + classe.getSimpleName().toUpperCase() + "\" />\n");
                            }
                            resultado += (espacos(4) + "</column>\n");
                        } else {
                            mensagem = "\n ATENÇÃO: A NOVA ENTIDADE CONTÉM ATRIBUTOS DO TIPO LIST, ESTES TERÃO DE SER GERADOS MANUALMENTE.\n";
                        }


                    } else {
                        if (!f.isAnnotationPresent(Enumerated.class)) {
                            resultado += (espacos(4) + "<column name=\"" + f.getName().toUpperCase() + "_ID" + "\" type=\"${numero}\">\n");
                            resultado += (espacos(6) + "<constraints nullable=\"false\" />\n");

                        } else {
                            resultado += (espacos(4) + "<column name=\"" + f.getName().toUpperCase() + "\" type=\"${descricaomedia}\">\n");
                        }
                        resultado += (espacos(4) + "</column>\n");
                    }

                }
                resultado += (espacos(2) + "</createTable>\n");

                for (Field f : classe.getDeclaredFields()) {
                    String tipo = f.getType().toString();

                    if (tipo.contains("entidades")) {
                        if (!f.isAnnotationPresent(Enumerated.class)) {
                            resultado += (espacos(2) + "<addForeignKeyConstraint baseColumnNames=\"" + f.getName().toUpperCase() + "_ID\" baseTableName=\""
                                    + classe.getSimpleName().toUpperCase() + "\" constraintName=\"FK_" + classe.getSimpleName().toUpperCase() + "_" + f.getType().getSimpleName().toUpperCase()
                                    + "\" deferrable=\"false\" initiallyDeferred=\"false\" onDelete=\"RESTRICT\" referencedColumnNames=\"ID\" referencedTableName=\"" + f.getType().getSimpleName().toUpperCase()
                                    + "\" referencesUniqueColumn=\"false\" />\n");
                        }

                    }

                }
                //AUDITORIA
                resultado += (espacos(2) + "<createTable tableName=\"" + classe.getSimpleName().toUpperCase() + "_AUD\">\n");
                for (Field f : classe.getDeclaredFields()) {
                    if ("serialVersionUID".equals(f.getName())) {
                        continue;
                    }
                    String tipo = f.getType().toString();
                    String tipoLiquibase = "";
                    //verifica tipo
                    if (f.getType().equals(BigDecimal.class)) {
                        tipoLiquibase = "${monetario}";
                    } else if (f.getType().equals(Long.class)) {
                        tipoLiquibase = "${decimal}";
                    } else if (f.getType().equals(Double.class)) {
                        tipoLiquibase = "${decimal}";
                    } else if (f.getType().equals(Integer.class)) {
                        tipoLiquibase = "${numero}";
                    } else if (f.getType().equals(Boolean.class)) {
                        tipoLiquibase = "${logico}";
                    } else if (f.getType().equals(String.class)) {
                        tipoLiquibase = "${descricaolonga}";
                    } else if (f.getType().equals(Date.class)) {
                        Temporal d = f.getAnnotation(Temporal.class);
                        if (d.value() == TemporalType.TIMESTAMP) {
                            tipoLiquibase = "${datatempo}";
                        } else if (d.value() == TemporalType.DATE) {
                            tipoLiquibase = "${data}";
                        } else if (d.value() == TemporalType.TIME) {
                            tipoLiquibase = "${tempo}";
                        }
                    } else {
                        tipoLiquibase = f.getType().toString();
                    }

                    if (!tipo.contains("entidades")) {
                        if (!f.getType().equals(List.class)) {
                            resultado += (espacos(4) + "<column name=\"" + f.getName().toUpperCase() + "\" type=\"" + tipoLiquibase + "\"/>\n");

                        }
                    } else {
                        if (!f.isAnnotationPresent(Enumerated.class)) {
                            resultado += (espacos(4) + "<column name=\"" + f.getName().toUpperCase() + "_ID" + "\" type=\"${numero}\">\n");
                            resultado += (espacos(6) + "<constraints nullable=\"false\" />");

                        } else {
                            resultado += (espacos(4) + "<column name=\"" + f.getName().toUpperCase() + "\" type=\"${descricaomedia}\">\n");
                        }
                        resultado += (espacos(4) + "</column>\n");
                    }

                }
                resultado += (espacos(4) + "<column name=\"REV\" type=\"${numero}\">\n");
                resultado += (espacos(6) + "<constraints nullable=\"true\"/>\n");
                resultado += (espacos(4) + "</column>\n");

                resultado += (espacos(4) + "<column name=\"REVTYPE\" type=\"${numero}\">\n");
                resultado += (espacos(6) + "<constraints nullable=\"true\"/>\n");
                resultado += (espacos(4) + "</column>\n");
                resultado += (espacos(2) + "</createTable>\n");

                resultado += ("<addPrimaryKey columnNames=\"ID, REV\" "
                        + "constraintName=\"" + "PK_" + classe.getSimpleName().toUpperCase() + "_AUD" + "\" "
                        + "tableName=\"" + classe.getSimpleName().toUpperCase() + "_AUD" + "\" />\n");


            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //System.out.println(mensagem);
        resultado += ("</changeSet>");
        if (mensagem != null && !mensagem.isEmpty()) {
            resultado += "\n\n" + mensagem;
        }
        return this;
    }
}
