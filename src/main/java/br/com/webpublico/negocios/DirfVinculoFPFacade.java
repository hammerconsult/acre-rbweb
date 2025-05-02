package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.dirf.DirfPessoa;
import br.com.webpublico.entidades.rh.dirf.DirfVinculoFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DirfVinculoFPFacade extends AbstractFacade<DirfVinculoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public DirfVinculoFPFacade() {
        super(DirfVinculoFP.class);
    }

    @Override
    public DirfVinculoFP recuperar(Object id) {
        DirfVinculoFP dirfVinculoFP = em.find(DirfVinculoFP.class, id);
        dirfVinculoFP.getPessoas().size();
        dirfVinculoFP.getInformacoesComplementares().size();
        for (DirfPessoa dirfPessoa : dirfVinculoFP.getPessoas()) {
            dirfPessoa.getValores().size();
        }
        return dirfVinculoFP;
    }

    public DirfVinculoFP recuperarInformacoesComplementares(Object id) {
        DirfVinculoFP dirfVinculoFP = em.find(DirfVinculoFP.class, id);
        dirfVinculoFP.getInformacoesComplementares().size();
        return dirfVinculoFP;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DirfVinculoFP recuperarDirfCedulaC(VinculoFP vinculoFP, Exercicio exercicio) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT dvinculo.*, dicomplementares.* FROM dirfvinculofp dvinculo ")
            .append(" INNER JOIN dirf df ON dvinculo.dirf_id = df.id ")
            .append(" LEFT JOIN DirfInfoComplementares dicomplementares ")
            .append(" ON dvinculo.id = dicomplementares.dirfvinculofp_id ")
            .append(" WHERE dvinculo.vinculofp_id = :idVinculoDirf ")
            .append(" AND df.exercicio_id = :idExercicioDirf ");

        Query q = em.createNativeQuery(sql.toString(), DirfVinculoFP.class);
        q.setParameter("idVinculoDirf", vinculoFP.getId());
        q.setParameter("idExercicioDirf", exercicio.getId());

        if (!q.getResultList().isEmpty()) {
            return (DirfVinculoFP) q.getResultList().get(0);
        }
        return null;
    }

    public List<DirfPessoa> recuperarDirfPessoaCedulaC(VinculoFP vinculoFP, Exercicio exercicio) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT pessoa.* FROM dirfvinculofp dvinculo ")
            .append(" INNER JOIN dirf df ON dvinculo.dirf_id = df.id ")
            .append(" INNER JOIN dirfPessoa pessoa ON pessoa.dirfVinculoFP_id = dvinculo.id ")
            .append(" WHERE dvinculo.vinculofp_id = :idVinculoDirf ")
            .append(" AND df.exercicio_id = :idExercicioDirf ");

        Query q = em.createNativeQuery(sql.toString(), DirfPessoa.class);
        q.setParameter("idVinculoDirf", vinculoFP.getId());
        q.setParameter("idExercicioDirf", exercicio.getId());

        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }
}
