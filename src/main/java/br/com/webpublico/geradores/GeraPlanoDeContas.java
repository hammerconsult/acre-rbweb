/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.geradores;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaEquivalente;
import br.com.webpublico.entidades.PlanoDeContas;
import br.com.webpublico.negocios.PlanoDeContasFacade;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
public class GeraPlanoDeContas extends JFrame implements ActionListener {

    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    private PlanoDeContas selecionado;
    private EntityManager em;
    private JPanel painel;
    private JTextArea tArea;
    private JButton btTestar;
    private JTable tbResultado;
    private ModeloResultado mr;
    private JProgressBar barraProgresso;
    private JComboBox comboPlano;
    private List<PlanoDeContas> listaCombo;

    public GeraPlanoDeContas() {
        setLayout(new BorderLayout());
        painel = new JPanel(new FlowLayout());
        tArea = new JTextArea(5, 50);
        btTestar = new JButton("Copiar");
        painel.add(new JScrollPane(tArea));
        painel.add(btTestar);
        mr = new ModeloResultado(PlanoDeContas.class);

        barraProgresso = new JProgressBar();

        comboPlano = new JComboBox();
        em = createEntityManagerFactory().createEntityManager();

        listaCombo = new ArrayList<PlanoDeContas>();

        montaCombo();

        add(painel, BorderLayout.NORTH);
        add(comboPlano, BorderLayout.CENTER);
        add(barraProgresso, BorderLayout.SOUTH);
        barraProgresso.setValue(0);

        pack();
        btTestar.addActionListener(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private EntityManagerFactory createEntityManagerFactory() {
        EntityManagerFactory result = Persistence.createEntityManagerFactory("webPublicoTeste");
        return result;
    }

    public static void main(String args[]) {
        new GeraPlanoDeContas();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PlanoDeContas pc = new PlanoDeContas();

        if (comboPlano.getSelectedItem() != null) {
            //pc = ((PlanoDeContas) comboPlano.getSelectedItem());

            pc = listaCombo.get(comboPlano.getSelectedIndex());

            PlanoDeContas pcNovo = new PlanoDeContas(pc.getDescricao(), pc.getTipoConta(), pc.getContas(), new Date());
            pcNovo.setDescricao(tArea.getText().trim());

            List<Conta> listaConta = new ArrayList<Conta>();
            List<Conta> listaContaVelha = pc.getContas();
            Collections.sort(listaContaVelha);

            List<Conta> listaFilhos = new ArrayList<Conta>();

            List<ContaEquivalente> listaContasEquivalentes = new ArrayList<ContaEquivalente>();

            for (Conta conta : listaContaVelha) {
                for (ContaEquivalente contaEquivalente : conta.getContasEquivalentes()) {
                    ContaEquivalente novaContaEquivalente = new ContaEquivalente();
                    novaContaEquivalente.setContaDestino(contaEquivalente.getContaDestino());
                    novaContaEquivalente.setContaOrigem(contaEquivalente.getContaOrigem());
                    novaContaEquivalente.setDataRegistro(new Date());
                    novaContaEquivalente.setVigencia(contaEquivalente.getVigencia());

                    listaContasEquivalentes.add(novaContaEquivalente);
                }

                listaConta.add(new Conta(conta.getAtiva(), conta.getCodigo(), conta.getDataRegistro(), conta.getDescricao(),
                        conta.getPermitirDesdobramento(), pcNovo, conta.getSuperior(), conta.getRubrica(),
                        conta.getTipoContaContabil(), conta.getFuncao(), null, null));
            }


            List<ContaEquivalente> listaContaEquivalenteAuxiliar = new ArrayList<ContaEquivalente>();

            for (Conta conta : listaContaVelha) {
                //busca pelo indice da lista velha, o objeto na listaConta
                Conta c = listaConta.get(listaConta.indexOf(conta));
                for (ContaEquivalente contaEquivalente : conta.getContasEquivalentes()) {
                    if (contaEquivalente.getContaDestino().getDataRegistro() == c.getDataRegistro()) {
                        ContaEquivalente equiv = new ContaEquivalente();
                        equiv.setContaDestino(c);
                        equiv.setContaOrigem(contaEquivalente.getContaOrigem());
                        equiv.setDataRegistro(contaEquivalente.getDataRegistro());
                        equiv.setVigencia(contaEquivalente.getVigencia());
                        listaContaEquivalenteAuxiliar.add(equiv);
                    } else if (contaEquivalente.getContaOrigem().getDataRegistro() == c.getDataRegistro()) {
                        ContaEquivalente equiv = new ContaEquivalente();
                        equiv.setContaDestino(contaEquivalente.getContaDestino());
                        equiv.setContaOrigem(c);
                        equiv.setDataRegistro(contaEquivalente.getDataRegistro());
                        equiv.setVigencia(contaEquivalente.getVigencia());
                        listaContaEquivalenteAuxiliar.add(equiv);
                    }


                }
            }


            pcNovo.setContas(listaConta);
            try {
                em.getTransaction().begin();

                for (ContaEquivalente contaEquivalente : listaContaEquivalenteAuxiliar) {
                    em.persist(contaEquivalente);
                }

                em.persist(pcNovo);
                em.getTransaction().commit();
                JOptionPane.showMessageDialog(rootPane, "Plano de Contas Duplicado com Sucesso!");
                montaCombo();
            } catch (Exception erro) {
                erro.printStackTrace();
            }
        }
    }

    public List<Conta> buscaContasPeloPlanoDeContas(PlanoDeContas pc) {
        Query q = em.createQuery(" from Conta conta where conta.planoDeContas = :pc "
                + " order by conta.codigo ");
        q.setParameter("pc", pc);
        return q.getResultList();
    }

    public List<Conta> buscaContasSuperioresSemSuperiores(PlanoDeContas pc) {
        Query q = em.createQuery(" from Conta conta where conta.planoDeContas = :pc "
                + " and conta in (select conta2.superior from Conta conta2 "
                + " where conta2.planoDeContas =:pc ) and conta.superior is null "
                + " order by conta.codigo ");
        q.setParameter("pc", pc);
        return q.getResultList();
    }

    public List<Conta> buscaContasSuperioresComSuperiores(PlanoDeContas pc) {
        Query q = em.createQuery(" from Conta conta where conta.planoDeContas = :pc "
                + " and conta in (select conta2.superior from Conta conta2 "
                + " where conta2.planoDeContas =:pc ) and conta.superior is not null "
                + " order by conta.codigo ");
        q.setParameter("pc", pc);
        return q.getResultList();
    }

    public List<Conta> buscaContasFilhas(PlanoDeContas pc) {
        Query q = em.createQuery(" from Conta conta where conta.planoDeContas = :pc "
                + " and conta not in (select conta2.superior from Conta conta2 "
                + " where conta2.planoDeContas =:pc ) ");
        q.setParameter("pc", pc);
        return q.getResultList();
    }

    public void montaCombo() {
        comboPlano.removeAllItems();
        Query q = em.createQuery(" from PlanoDeContas plan order by plan.descricao ");
        listaCombo = q.getResultList();

        for (PlanoDeContas pdc : listaCombo) {
            comboPlano.addItem(pdc.getDescricao());
        }
    }
}
