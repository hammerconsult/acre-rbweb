/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoRendasPatrimoniais;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andre
 */
@Stateless
public class ContratoRendasPatrimoniaisPesquisaGenericoFacade extends ComponentePesquisaGenericoFacade {

    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private MoedaFacade moedaFacade;

    public List<Object> filtrar(String sql, Object objeto, int inicio, int max) throws Exception {
        Query consulta = getEm().createQuery(sql);
        if (max != 0) {
            consulta.setMaxResults(max + 1);
            consulta.setFirstResult(inicio);
        }

        List<ContratoRendasPatrimoniais> lista = consulta.getResultList();
        List<Object> listaRetorno = new ArrayList<Object>();
        BigDecimal ufm = moedaFacade.recuperaValorVigenteUFM();
        for (ContratoRendasPatrimoniais c : lista) {
            c = contratoRendasPatrimoniaisFacade.recuperar(c.getId());
            c.setValorUfm(ufm);
            c.setCpfCnpjLocatario(c.getCpfCnpjLocatario());
            c.setSomaDoValorTotalContratoFormatado(c.getSomaDoValorTotalContratoFormatado());
            listaRetorno.add(c);
        }

        return listaRetorno;
    }

    @Override
    public Object[] filtarComContadorDeRegistros(String sql, String sqlCount, Object objeto, int inicio, int max) throws Exception {
        Query consulta = getEm().createQuery(sql);

        Query consultaCount = getEm().createQuery(sqlCount);

        Long count = 0l;
        List<ContratoRendasPatrimoniais> lista = new ArrayList<>();
        List<Object> listaRetorno = new ArrayList<Object>();
        try {
            count = (Long) consultaCount.getSingleResult();

            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }

            lista = consulta.getResultList();
            listaRetorno = new ArrayList<Object>();

            for (ContratoRendasPatrimoniais c : lista) {
                BigDecimal ufm = c.getValorUfmDoContrato();
                if (ufm == null) {
                    ufm = BigDecimal.ZERO;
                }
                c = contratoRendasPatrimoniaisFacade.recuperar(c.getId());
                c.setValorUfm(ufm);
                c.setCpfCnpjLocatario(c.getCpfCnpjLocatario());
                c.setSomaDoValorTotalContratoFormatado(c.getSomaDoValorTotalContratoFormatado());
                listaRetorno.add(c);
            }
        } catch (NoResultException nre) {
        }
        Object[] retorno = new Object[2];
        retorno[0] = listaRetorno;
        retorno[1] = count;
        return retorno;
    }
}
