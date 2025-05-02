
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParametrosInformacoesRBTrans;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroInformacaoRBTransFacade extends AbstractFacade<ParametrosInformacoesRBTrans> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroInformacaoRBTransFacade() {
        super(ParametrosInformacoesRBTrans.class);
    }

    @Override
    public ParametrosInformacoesRBTrans recuperar(Object id) {
        ParametrosInformacoesRBTrans param = super.recuperar(id);
        if (param.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(param.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return param;
    }

    public ParametrosInformacoesRBTrans buscarParametroPeloExercicio(Exercicio exercicio) {
        if (exercicio == null) return null;
        String sql = "select param.* from PARAMINFORMACOESRBTRANS param " +
            " where param.exercicio_id = :idExercicio";
        Query q = em.createNativeQuery(sql, ParametrosInformacoesRBTrans.class);
        q.setParameter("idExercicio", exercicio.getId());
        List<ParametrosInformacoesRBTrans> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            ParametrosInformacoesRBTrans param = recuperar(resultado.get(0).getId());
            return param;
        }
        return null;
    }

}

