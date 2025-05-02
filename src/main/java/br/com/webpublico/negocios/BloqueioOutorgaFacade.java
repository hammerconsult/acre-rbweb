package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoPassageiro;
import br.com.webpublico.util.Util;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class BloqueioOutorgaFacade extends AbstractFacade<BloqueioOutorga> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public BloqueioOutorgaFacade() {
        super(BloqueioOutorga.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long ultimoCodigoMaisUm(Exercicio exercicio) {
        Query q = em.createNativeQuery("SELECT coalesce(max(codigo), 0) + 1 AS codigo FROM BLOQUEIOOUTORGA WHERE EXERCICIO_ID = :exercicio");
        q.setParameter("exercicio", exercicio.getId());
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public BloqueioOutorga buscarBloqueioFinalizadoParaParametroSubVencao(CadastroEconomico cadastroEconomico, ParametroOutorgaSubvencao parametroOutorgaSubvencao) {
        Query q = em.createNativeQuery("select bloq.* from BLOQUEIOOUTORGA bloq " +
            "inner join PARAMETROBLOQUEIOOUTORGA param on " +
            "param.BLOQUEIOOUTORGA_ID = bloq.ID " +
            "where bloq.situacao = :situacao " +
            "and param.PARAMETROOUTORGASUBVENCAO_ID = :parametro " +
            "and bloq.CADASTROECONOMICO_ID = :cadastroEconomico", BloqueioOutorga.class);
        q.setParameter("situacao", BloqueioOutorga.Situacao.FINALIZADO.name());
        q.setParameter("parametro", parametroOutorgaSubvencao.getId());
        q.setParameter("cadastroEconomico", cadastroEconomico.getId());
        q.setMaxResults(1);
        List<BloqueioOutorga> bloqueiosOutorga = q.getResultList();
        if (bloqueiosOutorga.isEmpty()) {
            return null;
        } else {
            BloqueioOutorga bloqueioOutorga =  bloqueiosOutorga.get(0);
            Hibernate.initialize(bloqueioOutorga.getDadosBloqueioOutorgas());
            Hibernate.initialize(bloqueioOutorga.getParametros());
            if (bloqueioOutorga.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(bloqueioOutorga.getDetentorArquivoComposicao().getArquivosComposicao());
                for (ArquivoComposicao arquivoComposicao : bloqueioOutorga.getDetentorArquivoComposicao().getArquivosComposicao()) {
                    Hibernate.initialize(arquivoComposicao.getArquivo());
                    Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
                }
            }
            return bloqueioOutorga;
        }
    }

    @Override
    public BloqueioOutorga recuperar(Object id) {
        BloqueioOutorga bloqueioOutorga = super.recuperar(id);
        Hibernate.initialize(bloqueioOutorga.getDadosBloqueioOutorgas());
        Hibernate.initialize(bloqueioOutorga.getParametros());
        if (bloqueioOutorga.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(bloqueioOutorga.getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao arquivoComposicao : bloqueioOutorga.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(arquivoComposicao.getArquivo());
                Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
            }
        }
        return bloqueioOutorga;
    }
}
