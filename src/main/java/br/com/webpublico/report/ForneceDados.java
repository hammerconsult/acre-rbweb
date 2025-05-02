/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author terminal1
 */
public class ForneceDados implements JRDataSource {

    protected List lista;
    private int atual = -1;
    protected boolean emTeste = true;

    public ForneceDados() {
    }

    public boolean next() throws JRException {
        if (emTeste) {
            return ++atual < 10;
        }
        return ++atual < lista.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        if (emTeste) {
            return jrf.getName() + atual;
        }
        try {
            String nome = jrf.getName();
            String partes[] = nome.split("\\.");
            int i = 0;
            Object obj = lista.get(atual);
            while (i < partes.length) {
                Class classe = obj.getClass();
                String nomeAtributo = partes[i];
                Method m = classe.getMethod("get" + primeiraMaiuscula(nomeAtributo));
                obj = m.invoke(obj);
                i++;
            }
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "nada";
    }

    public static JRDataSource createDataSource() {
        return new ForneceDados();
    }

    private String primeiraMaiuscula(String nomeAtributo) {
        return nomeAtributo.substring(0, 1).toUpperCase() + nomeAtributo.substring(1);
    }

    public boolean isEmTeste() {
        return emTeste;
    }

    public void setEmTeste(boolean emTeste) {
        this.emTeste = emTeste;
    }

    public List getLista() {
        return lista;
    }

    public void setLista(List lista) {
        this.lista = lista;
    }
}
