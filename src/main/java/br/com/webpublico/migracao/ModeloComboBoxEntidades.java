/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import br.com.webpublico.geradores.GeraDiagramaClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
public class ModeloComboBoxEntidades implements ComboBoxModel {

    private static final Logger logger = LoggerFactory.getLogger(ModeloComboBoxEntidades.class);

    private List<Class> entidades;
    private Class selecionado;

    public ModeloComboBoxEntidades() {
        entidades = new ArrayList<Class>();
        File pastaAtual = new File(GeraDiagramaClasses.class.getResource("GeraDiagramaClasses.class").getFile());
        File pastaEntidades = new File(pastaAtual.getParentFile().getParentFile().getAbsolutePath().replaceAll("%20", " ") + "/entidades");
        File lista[] = pastaEntidades.listFiles();
        for (int i = 0; i < lista.length - 1; i++) {
            for (int j = i + 1; j < lista.length; j++) {
                if (lista[i].getName().compareTo(lista[j].getName()) > 0) {
                    File aux = lista[i];
                    lista[i] = lista[j];
                    lista[j] = aux;
                }
            }
        }


        for (File arquivo : lista) {
            try {
                String nomeArquivoSimples = arquivo.getAbsolutePath();
                int posicao = nomeArquivoSimples.indexOf(File.separator + "br");
                nomeArquivoSimples = nomeArquivoSimples.substring(posicao + 1);
                nomeArquivoSimples = nomeArquivoSimples.replace(File.separator, ".");
                nomeArquivoSimples = nomeArquivoSimples.replace(".class", "");
                Class entidade = Class.forName(nomeArquivoSimples);
                entidades.add(entidade);
            } catch (ClassNotFoundException ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public void setSelectedItem(Object anItem) {
        selecionado = (Class) anItem;
    }

    public Object getSelectedItem() {
        return selecionado;
    }

    public int getSize() {
        return entidades.size();
    }

    public Object getElementAt(int index) {
        return entidades.get(index);
    }

    public void addListDataListener(ListDataListener l) {

    }

    public void removeListDataListener(ListDataListener l) {

    }
}
