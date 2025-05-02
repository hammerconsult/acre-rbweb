/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
public class ModeloMensagens extends AbstractTableModel {

    private List<MensagemMigracao> mensagens;

    public ModeloMensagens() {
        this.mensagens = new ArrayList<MensagemMigracao>();
        mensagens.add(new MensagemMigracao("Sistema Iniciado"));
    }

    public int getRowCount() {
        return mensagens.size();
    }

    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Data/Hora";
            case 1:
                return "Mensagem";
        }
        return null;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return mensagens.get(rowIndex).getQuando();
            case 1:
                return mensagens.get(rowIndex).getMensagem();
        }
        return null;
    }

    public List<MensagemMigracao> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<MensagemMigracao> mensagens) {
        this.mensagens = mensagens;
    }

    public void adicionaMensagem(String s) {
        mensagens.add(new MensagemMigracao(s));
        fireTableDataChanged();
    }
}
