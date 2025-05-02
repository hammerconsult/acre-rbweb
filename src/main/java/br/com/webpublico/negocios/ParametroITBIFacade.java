/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParametrosFuncionarios;
import br.com.webpublico.entidades.ParametrosITBI;
import br.com.webpublico.enums.TipoITBI;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Heinz
 */
@Stateless
public class ParametroITBIFacade extends AbstractFacade<ParametrosITBI> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private FuncaoParametroITBIFacade funcaoParametroITBIFacade;

    public ParametroITBIFacade() {
        super(ParametrosITBI.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FuncaoParametroITBIFacade getFuncaoParametroITBIFacade() {
        return funcaoParametroITBIFacade;
    }

    @Override
    public ParametrosITBI recuperar(Object id) {
        ParametrosITBI retorno = em.find(ParametrosITBI.class, id);
        for(ParametrosFuncionarios pf : retorno.getListaFuncionarios()) {
            if(pf.getDetentorArquivoComposicao() != null) {
                if(pf.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
                    Hibernate.initialize(pf.getDetentorArquivoComposicao().getArquivosComposicao());
                    Hibernate.initialize(pf.getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getPartes());
                }
            }
        }
        retorno.getListaFuncionarios().size();
        retorno.getListaDeFaixaValorParcelamento().size();
        retorno.getDocumentos().size();
        return retorno;
    }

    public ParametrosITBI ParametroVigente(TipoITBI tipoITBI) {
        String hql = "from ParametrosITBI p where p.tipoITBI = :tipoITBI"
                + "   and coalesce(p.exercicio.ano, "
                + sistemaFacade.getExercicioCorrente().getAno() + ") <= :anoVigencia "
                + "    and coalesce(p.exercicio.ano, " + sistemaFacade.getExercicioCorrente().getAno() + ") >= :anoVigencia ";
        //+ "    and coalesce(p.exercicioVigenciaFim.ano, " + sistemaFacade.getExercicioCorrente().getAno() + ") >= :anoVigencia ";
        Query q = em.createQuery(hql);
        q.setParameter("tipoITBI", tipoITBI);
        q.setParameter("anoVigencia", sistemaFacade.getExercicioCorrente().getAno());
        List<ParametrosITBI> retorno = q.getResultList();
        if (retorno.isEmpty()) {
            return null;
        } else {
            return retorno.get(0);
        }
    }

    public ParametrosITBI getParametroVigente(Exercicio exercicio, TipoITBI tipoITBI) {
        String sql = " SELECT p.* "
                + "      FROM parametrositbi p "
                + "     WHERE p.tipoITBI = :tipoITBI"
                + "       AND p.exercicio_id = :exercicio";

        Query q = em.createNativeQuery(sql, ParametrosITBI.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("tipoITBI", tipoITBI.name());

        List<ParametrosITBI> retorno = q.getResultList();
        ParametrosITBI p = null;

        if (!retorno.isEmpty()) {
            p = retorno.get(0);
            Hibernate.initialize(p.getListaFuncionarios());
            Hibernate.initialize(p.getListaDeFaixaValorParcelamento());
            Hibernate.initialize(p.getDocumentos());
        }

        return p;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }
}
