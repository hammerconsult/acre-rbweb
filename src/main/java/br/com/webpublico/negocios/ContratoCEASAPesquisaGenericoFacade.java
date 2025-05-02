/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoCEASA;

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
public class ContratoCEASAPesquisaGenericoFacade extends ComponentePesquisaGenericoFacade {

    @EJB
    private ContratoCEASAFacade contratoCEASAFacade;
    @EJB
    private MoedaFacade moedaFacade;

    public List<Object> filtrar(String sql, Object objeto, int inicio, int max) throws Exception {
        Query consulta = getEm().createQuery(sql);
        if (max != 0) {
            consulta.setMaxResults(max + 1);
            consulta.setFirstResult(inicio);
        }

        List<ContratoCEASA> lista = consulta.getResultList();
        List<Object> listaRetorno = new ArrayList<Object>();
        BigDecimal valorUFM = moedaFacade.recuperaValorVigenteUFM();
        for (ContratoCEASA c : lista) {
            c = contratoCEASAFacade.recuperar(c.getId());
            c.setNomeLocatario(c.getLocatario().getPessoa().getNome() + "  " + c.getLocatario().getPessoa().getCpf_Cnpj());
            c.setNumeroCMC(c.getLocatario().getInscricaoCadastral());
            c.setValorUFM(valorUFM);
            c.setValorContratoFormatado(c.getValorContratoFormatado());
            c.setValorLicitacaoFormatado(c.getValorLicitacaoFormatado());
            listaRetorno.add(c);
        }
        return listaRetorno;
    }

    @Override
    public Object[] filtarComContadorDeRegistros(String sql, String sqlCount, Object objeto, int inicio, int max) throws Exception {
        Query consulta = getEm().createQuery(sql);

        Query consultaCount = getEm().createQuery(sqlCount);

        Long count = 0l;
        List<ContratoCEASA> lista = new ArrayList<>();
        List<ContratoCEASA> listaRetorno = new ArrayList<>();
        try {
            count = (Long) consultaCount.getSingleResult();

            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }

            lista = consulta.getResultList();
            listaRetorno = new ArrayList<ContratoCEASA>();
            BigDecimal valorUFM = moedaFacade.recuperaValorVigenteUFM();
            for (ContratoCEASA c : lista) {
                c = contratoCEASAFacade.recuperar(c.getId());
                c.setNomeLocatario(c.getLocatario().getPessoa().getNome() + "  " + c.getLocatario().getPessoa().getCpf_Cnpj());
                c.setNumeroCMC(c.getLocatario().getInscricaoCadastral());
                c.setValorUFM(valorUFM);
                c.setValorContratoFormatado(c.getValorContratoFormatado());
                c.setValorLicitacaoFormatado(c.getValorLicitacaoFormatado());
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
