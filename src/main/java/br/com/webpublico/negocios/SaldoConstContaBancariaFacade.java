package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.SaldoConstContaBancaria;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class SaldoConstContaBancariaFacade extends AbstractFacade<SaldoConstContaBancaria> {

    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private AgenciaFacade agenciaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(SaldoConstContaBancaria entity) {
        entity.setNumero(getUltimoNumero());
        super.salvarNovo(entity);
    }

    public SaldoConstContaBancariaFacade() {
        super(SaldoConstContaBancaria.class);
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(S.NUMERO))+1 AS ULTIMONUMERO FROM SALDOCONSTCONTABANCARIA S";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    public AgenciaFacade getAgenciaFacade() {
        return agenciaFacade;
    }

    public SaldoConstContaBancaria recuperarSaldoDoDia(Date dataSaldo, ContaBancariaEntidade contaBancariaEntidade) {
        String sql = "select * from saldoconstcontabancaria " +
                " where trunc(datasaldo) <= to_date(:data,'dd/mm/yyyy') " +
                " and contaBancariaEntidade_id = :conta " +
                " order by datasaldo desc";
        Query consulta = em.createNativeQuery(sql, SaldoConstContaBancaria.class);
        consulta.setParameter("data", DataUtil.getDataFormatada(dataSaldo));
        consulta.setParameter("conta", contaBancariaEntidade.getId());
        consulta.setMaxResults(1);
        try {
            return (SaldoConstContaBancaria) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<SaldoConstContaBancaria> buscarHistoricoSaldoConstante(Date data, ContaBancariaEntidade conta) {
        String sql = " select * from saldoconstcontabancaria " +
                " where contaBancariaEntidade_id = :conta " +
                " and trunc(dataSaldo) <= to_date(:dataSaldo, 'dd/mm/yyyy') " +
                " order by datasaldo desc ";
        Query consulta = em.createNativeQuery(sql, SaldoConstContaBancaria.class);
        consulta.setParameter("conta", conta.getId());
        consulta.setParameter("dataSaldo", DataUtil.getDataFormatada(data));
        try {
            return consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

}
