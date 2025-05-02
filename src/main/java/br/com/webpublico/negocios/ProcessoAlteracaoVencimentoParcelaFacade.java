package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Wellington on 08/12/2015.
 */
@Stateless
public class ProcessoAlteracaoVencimentoParcelaFacade extends AbstractFacade<ProcessoAlteracaoVencimentoParcela> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ProcessoAlteracaoVencimentoParcelaFacade() {
        super(ProcessoAlteracaoVencimentoParcela.class);
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(ProcessoAlteracaoVencimentoParcela entity) {
        for (ItemProcessoAlteracaoVencimentoParcela item : entity.getItensProcessoAlteracaoVencParc()) {
            consultaDebitoFacade.alterarVencimentoParcelaValorDivida(item.getParcelaValorDivida(), item.getVencimentoModificado());
        }
        entity.setSituacao(SituacaoProcessoDebito.FINALIZADO);
        getEntityManager().persist(entity);
    }

    @Override
    public ProcessoAlteracaoVencimentoParcela recuperar(Object id) {
        ProcessoAlteracaoVencimentoParcela toReturn = super.recuperar(id);
        toReturn.getItensProcessoAlteracaoVencParc().size();
        return toReturn;
    }

    public Long recuperarProximoCodigoPorExercicio(Exercicio exercicio) {
        String sql = " select max(coalesce(obj.codigo,0)) from ProcessoAlteracaoVencimentoParcela obj "
            + " where obj.exercicio = :exercicio";
        Query query = em.createQuery(sql);
        query.setParameter("exercicio", exercicio);
        query.setMaxResults(1);
        try {
            Long resultado = (Long) query.getSingleResult();
            if (resultado == null) {
                resultado = 0l;
            }

            return resultado + 1;
        } catch (Exception e) {
            return 1l;
        }
    }

    public List<String> recuperarCadastrosDoProcesso(ProcessoAlteracaoVencimentoParcela processo) {
        List<String> toReturn = Lists.newArrayList();

        StringBuilder sb = new StringBuilder();
        sb.append(" select cad from ProcessoAlteracaoVencimentoParcela obj ");
        sb.append(" inner join obj.itensProcessoAlteracaoVencParc i ");
        sb.append(" inner join i.parcelaValorDivida p ");
        sb.append(" inner join p.valorDivida vd ");
        sb.append(" inner join vd.calculo c ");
        sb.append(" inner join c.cadastro cad ");
        sb.append(" where obj.id = :idProcesso");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idProcesso", processo.getId());
        List<Cadastro> cadastros = new ArrayList<>(new HashSet(q.getResultList()));
        if (cadastros != null) {
            for (Cadastro cadastro : cadastros) {
                toReturn.add(cadastro.getNumeroCadastro());
            }
        }

        sb = new StringBuilder();
        sb.append(" select pes.pessoa from ProcessoAlteracaoVencimentoParcela obj ");
        sb.append(" inner join obj.itensProcessoAlteracaoVencParc i ");
        sb.append(" inner join i.parcelaValorDivida p ");
        sb.append(" inner join p.valorDivida vd ");
        sb.append(" inner join vd.calculo c ");
        sb.append(" inner join c.pessoas pes ");
        sb.append(" where obj.id = :idProcesso and c.cadastro is null ");

        q = em.createQuery(sb.toString());
        q.setParameter("idProcesso", processo.getId());
        List<Pessoa> pessoas = new ArrayList(new HashSet(q.getResultList()));
        if (pessoas != null) {
            for (Pessoa pessoa : pessoas) {
                toReturn.add(pessoa.getCpf_Cnpj());
            }
        }

        return toReturn;
    }

    public void estornarProcesso(ProcessoAlteracaoVencimentoParcela entity) {
        String mottivo = entity.getMotivoEstorno();
        entity = recuperar(entity.getId());
        for (ItemProcessoAlteracaoVencimentoParcela item : entity.getItensProcessoAlteracaoVencParc()) {
            consultaDebitoFacade.alterarVencimentoParcelaValorDivida(item.getParcelaValorDivida(), item.getVencimentoAnterior());
        }
        entity.setSituacao(SituacaoProcessoDebito.ESTORNADO);
        entity.setMotivoEstorno(mottivo);
        getEntityManager().merge(entity);
    }
}
