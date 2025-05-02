package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Motorista;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/09/14
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class MotoristaFacade extends AbstractFacade<Motorista> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotoristaFacade() {
        super(Motorista.class);
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public boolean existeMotoristaRegistrado(Motorista motorista) {
        String sql = " select m.* from motorista m "
            + " where m.pessoafisica_id = " + motorista.getPessoaFisica().getId()
            + " and sysdate between m.inicioVigencia and coalesce(m.finalVigencia, sysdate)"
            + " and m.inicioVigencia <= sysdate ";
        if (motorista.getId() != null) {
            sql += " and m.id <> " + motorista.getId();
        }
        Query q = em.createNativeQuery(sql, Motorista.class);
        return q.getResultList() != null && q.getResultList().size() > 0;
    }

    public List<Motorista> buscarMotoristas(String parte) {
        String sql = "" +
            " select " +
            "   mot.* " +
            " from Motorista mot " +
            "   inner join pessoafisica pf on mot.pessoafisica_id = pf.id " +
            "   inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = mot.unidadeOrganizacional_id" +
            "   inner join vwhierarquiaadministrativa vw on vw.subordinada_id = uuo.unidadeorganizacional_id " +
            " where uuo.usuariosistema_id = :idUsuario " +
            "   and (lower(pf.nome) like :parte or (replace(replace(pf.cpf,'.',''),'-','') like :parte)) " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vw.inicioVigencia) and coalesce(trunc(vw.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(mot.inicioVigencia) and coalesce(trunc(mot.finalVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, Motorista.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);

        return q.getResultList();
    }
}
