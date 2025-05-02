/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.geradores;

import br.com.webpublico.entidades.Cidade;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Munif
 */
public class TestadorDeHQL extends JFrame implements ActionListener {

    private EntityManager em;
    private JPanel painel;
    private JTextArea tArea;
    private JButton btTestar;
    private JTable tbResultado;
    private ModeloResultado mr;

    public TestadorDeHQL() {
        setLayout(new BorderLayout());
        painel = new JPanel(new FlowLayout());
        tArea = new JTextArea(5, 50);
        btTestar = new JButton("Testar");
        painel.add(new JScrollPane(tArea));
        painel.add(btTestar);
        mr = new ModeloResultado(Cidade.class);
        tbResultado = new JTable(mr);
        add(painel, BorderLayout.NORTH);
        add(new JScrollPane(tbResultado), BorderLayout.CENTER);
        pack();

        em = createEntityManagerFactory().createEntityManager();
        btTestar.addActionListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private EntityManagerFactory createEntityManagerFactory() {

        EntityManagerFactory result = Persistence.createEntityManagerFactory("webpublicoPU");
        return result;
    }

    public static void main(String args[]) {
        new TestadorDeHQL();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println("Consultando....");
        Query q = em.createQuery(tArea.getText());
        mr.setLista(q.getResultList());
        //System.out.println("Consulta concluída!");
    }
}
