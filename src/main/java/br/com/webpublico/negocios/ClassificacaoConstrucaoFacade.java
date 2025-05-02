package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.ClassificacaoConstrucao;
import br.com.webpublico.entidades.Habitese;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabitesePadroesConstrucao;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabitesePadroesConstrucaoFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ClassificacaoConstrucaoFacade extends AbstractFacade<ClassificacaoConstrucao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    @EJB
    private HabiteseConstrucaoFacade habiteseConstrucaoFacade;
    @EJB
    private HabitesePadroesConstrucaoFacade habitesePadroesConstrucaoFacade;
    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private ConstrucaoFacade construcaoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;

    public ClassificacaoConstrucaoFacade() {
        super(ClassificacaoConstrucao.class);
    }

    public AlvaraConstrucaoFacade getAlvaraConstrucaoFacade() {
        return alvaraConstrucaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConstrucaoFacade getConstrucaoFacade() {
        return construcaoFacade;
    }

    public AtributoFacade getAtributoFacade() {
        return atributoFacade;
    }

    public HabiteseConstrucaoFacade getHabiteseConstrucaoFacade() {
        return habiteseConstrucaoFacade;
    }

    public HabitesePadroesConstrucaoFacade getHabitesePadroesConstrucaoFacade() {
        return habitesePadroesConstrucaoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    @Override
    public ClassificacaoConstrucao recuperar(Object id) {
        ClassificacaoConstrucao cf = super.recuperar(id);
        if (cf.getHabitese().getAlvaraConstrucao().getHabiteses() != null) {
            Hibernate.initialize(cf.getHabitese().getAlvaraConstrucao().getHabiteses());
        }
        return cf;
    }

    private Long buscarUltimoCodigoMaisUm() {
        Query q = em.createNativeQuery("select nvl(max(codigo),0)+1 from CLASSIFICACAOCONSTRUCAO");
        return ((Number) q.getResultList().get(0)).longValue();
    }

    public Long buscarCodigoNovaContrucaoCadastro(Long idCadastro) {
        Query q = em.createNativeQuery("select count(id)+1 from construcao where IMOVEL_ID = :idCadastro and coalesce(CANCELADA, 0) = 0");
        q.setParameter("idCadastro", idCadastro);
        return ((Number) q.getResultList().get(0)).longValue();
    }

    public List<CadastroImobiliario> buscarCadastrosComHabiteseComCaracteristicaSemPadraoConstrucao(String parte) {
        String hql = "select ci " +
            " from CadastroImobiliario ci " +
            "         inner join ProcRegularizaConstrucao proc on proc.cadastroImobiliario.id = ci.id " +
            "         inner join AlvaraConstrucao ac on ac.procRegularizaConstrucao.id = proc.id " +
            "         inner join Habitese h on ac.id = h.alvaraConstrucao.id " +
            "         inner join CaracteristicaConstrucaoHabitese c on h.caracteristica.id = c.id and h.id = c.habitese.id " +
            " where c.padrao.id is null and ci.inscricaoCadastral like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }

    public List<Habitese> buscarHabiteseComCaracteristicaSemPadraoConstrucaoDoCadastro(Long idCadastro) {
        String sql = "select h.* " +
            " from habitese h " +
            "         inner join CARACTCONSTRUHABITESE c on h.CARACTERISTICA_ID = c.ID and h.ID = c.HABITESE_ID " +
            "         inner join alvaraconstrucao ac on h.ALVARACONSTRUCAO_ID = ac.ID " +
            "         inner join PROCREGULARIZACONSTRUCAO proc on ac.PROCREGULARIZACONSTRUCAO_ID = proc.ID " +
            "         inner join cadastroimobiliario ci on proc.CADASTROIMOBILIARIO_ID = ci.ID " +
            " where c.PADRAO_ID is null and ci.ID = :idCadastro ";
        Query q = em.createNativeQuery(sql, Habitese.class);
        q.setParameter("idCadastro", idCadastro);
        return q.getResultList();
    }

    public List<HabitesePadroesConstrucao> buscarPadroesConstrucao() {
        String sql = "select * " +
            " from HABITESEPADROESCONSTRUCAO";
        Query q = em.createNativeQuery(sql, HabitesePadroesConstrucao.class);
        List<HabitesePadroesConstrucao> retorno = q.getResultList();
        return retorno;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(ClassificacaoConstrucao entity) {
        entity.setCodigo(buscarUltimoCodigoMaisUm());
        super.salvarNovo(entity);
    }
}
