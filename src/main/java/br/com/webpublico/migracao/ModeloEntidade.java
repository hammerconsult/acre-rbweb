/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
public class ModeloEntidade<T> extends AbstractTableModel {

    private List lista;
    private Class classe;
    private List<Field> atributos;

    public ModeloEntidade(Class c) {
        classe = c;
        arrumaClasse();
        lista = new ArrayList();
        atributos = new ArrayList<Field>();
        for (Field f : classe.getDeclaredFields()) {
            atributos.add(f);
        }

    }

    @Override
    public String getColumnName(int column) {
        Field f = atributos.get(column);
        return f.getName();

    }

    public int getRowCount() {
        return lista.size();
    }

    public int getColumnCount() {
        return atributos.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Field f = atributos.get(columnIndex);
            f.setAccessible(true);
            return f.get(lista.get(rowIndex));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List getLista() {
        return lista;
    }

    public void setLista(List lista) {
        if (lista.size() > 0) {
            classe = lista.get(0).getClass();
        }
        arrumaClasse();
        this.lista = lista;
        fireTableStructureChanged();
    }

    private void arrumaClasse() {
        atributos = new ArrayList<Field>();
        for (Field f : classe.getDeclaredFields()) {
            atributos.add(f);
        }
    }
}
