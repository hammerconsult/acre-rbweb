package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MotoristaSaud;
import br.com.webpublico.entidades.VeiculoSaud;
import br.com.webpublico.entidades.VistoriaVeiculoSaud;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class VeiculoSaudFacade extends AbstractFacade<VeiculoSaud> {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private MotoristaSaudFacade motoristaSaudFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public VeiculoSaudFacade() {
        super(VeiculoSaud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotoristaSaudFacade getMotoristaSaudFacade() {
        return motoristaSaudFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    @Override
    public void preSave(VeiculoSaud veiculoSaud) {
        veiculoSaud.realizarValidacoes();
    }

    @Override
    public VeiculoSaud recuperar(Object id) {
        VeiculoSaud vs = super.recuperar(id);
        Hibernate.initialize(vs.getVistorias());
        for (VistoriaVeiculoSaud vistoria : vs.getVistorias()) {
            if (vistoria.getDetentorArquivoComposicao() != null && vistoria.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
                Hibernate.initialize(vistoria.getDetentorArquivoComposicao().getArquivosComposicao());
            }
        }
        return vs;
    }

    public List<MotoristaSaud> buscarFiltrandoMotoristasSaud(String parte) {
        String sql = "select ms.* from motoristasaud ms " +
            " inner join pessoafisica pf on ms.pessoafisica_id = pf.id " +
            " where ms.ativo = 1 and (pf.nome like :parte or pf.cpf like :parte)";
        Query q = em.createNativeQuery(sql, MotoristaSaud.class);
        q.setParameter("parte", "%" + parte + "%");
        List<MotoristaSaud> retorno = q.getResultList();
        return retorno;
    }

    public List<VeiculoSaud> buscarVeiculosDoMotorista(Long idMotorista, boolean ativos) {
        String sql = "SELECT VS.* FROM VEICULOSAUD VS " +
            " WHERE VS.MOTORISTASAUD_ID = :idMotorista ";
        sql += ativos ? " AND COALESCE(VS.ATIVO, 0) = :ativo" : "";
        Query q = em.createNativeQuery(sql, VeiculoSaud.class);
        q.setParameter("idMotorista", idMotorista);
        if (ativos) q.setParameter("ativo", true);
        return q.getResultList();
    }
}
