package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MotoristaSaud;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class MotoristaSaudFacade extends AbstractFacade<MotoristaSaud> {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public MotoristaSaudFacade() {
        super(MotoristaSaud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    @Override
    public void preSave(MotoristaSaud motoristaSaud) {
        motoristaSaud.realizarValidacoes();
    }

    public List<MotoristaSaud> buscarFiltrando(String parte) {
        String sql = " SELECT MS.* FROM MOTORISTASAUD MS" +
            " INNER JOIN PESSOAFISICA PF ON MS.PESSOAFISICA_ID = PF.ID " +
            " WHERE (TRIM(REGEXP_REPLACE(PF.CPF, '[^0-9]', '')) like REGEXP_REPLACE(:parte, '[^0-9]', '') " +
            " or trim(lower(PF.nome)) like :parte) ";
        Query q = em.createNativeQuery(sql, MotoristaSaud.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }
}
