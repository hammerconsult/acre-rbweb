/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.geradores;

import br.com.webpublico.entidades.ServicoUrbano;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/**
 * @author gustavo
 */
public class imprimeInsert {

    private void imprimeResultado() {
        String hql = "from ServicoUrbano";
        List<ServicoUrbano> lista = em.createQuery(hql).getResultList();
        for (ServicoUrbano objeto : lista) {
            System.out.println("INSERT INTO SERVICOURBANO VALUES ("
                    + "HIBERNATE_SEQUENCE.NEXTVAL, "
                    + "TO_TIMESTAMP('" + objeto.getDataRegistro() + "', 'YYYY-MM-DD HH24:MI:SS.FF3'), "
                    + "'" + objeto.getNome() + "', "
                    + "'" + objeto.getIdentificacao() + "', ");
        }
    }

    private EntityManager em;

    public void imprimeInsert() {
        //System.out.println("ImprimeInsert");
        em = createEntityManagerFactory().createEntityManager();
        imprimeResultado();
    }

    private EntityManagerFactory createEntityManagerFactory() {
        EntityManagerFactory result = Persistence.createEntityManagerFactory("webPublicoTeste");
        return result;
    }

    public static void main(String args[]) {
        new imprimeInsert();
    }
}
