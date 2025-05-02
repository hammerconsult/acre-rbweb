package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SessaoAtividade;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Stateless
public class RelatorioReceitaAbrasfFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public List<SessaoAtividade> buscarSessoesAtividade() {
        String sql = " select distinct sa.* from sessaoatividade sa " +
            " order by sa.sessao ";

        Query q = em.createNativeQuery(sql, SessaoAtividade.class);
        List<SessaoAtividade> sessoes = q.getResultList();
        return sessoes != null ? sessoes : Lists.<SessaoAtividade>newArrayList();
    }

    public Exercicio buscarExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public Date getDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }
}
