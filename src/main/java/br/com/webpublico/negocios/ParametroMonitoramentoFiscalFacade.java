package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametroMonitoramentoFiscal;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroMonitoramentoFiscalFacade extends AbstractFacade<ParametroMonitoramentoFiscal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public ParametroMonitoramentoFiscalFacade() {
        super(ParametroMonitoramentoFiscal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroMonitoramentoFiscal retornarParametroExercicioCorrente() {

        String sql = "select parametro from ParametroMonitoramentoFiscal parametro " +
            "where parametro.exercicio = :exercicio";
        Query q = em.createQuery(sql);
        q.setParameter("exercicio", sistemaFacade.getExercicioCorrente());
        if (!q.getResultList().isEmpty()) {
            return (ParametroMonitoramentoFiscal) q.getResultList().get(0);
        }
        return null;
    }

    public List<PessoaFisica> buscarPessoasComContratosVigentes(String parte) {
        String hql = " select new PessoaFisica(pessoa.id, pessoa.nome, pessoa.cpf) from ContratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pessoa "
            + " where :data between contrato.inicioVigencia "
            + " and coalesce(contrato.finalVigencia, :data) "
            + " and ((lower(pessoa.nome) like :parte) or (pessoa.cpf like :parte)) "
            + " order by pessoa.nome";
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("parte", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }


}
