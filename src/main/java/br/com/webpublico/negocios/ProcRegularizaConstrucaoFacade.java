package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
public class ProcRegularizaConstrucaoFacade extends AbstractFacade<ProcRegularizaConstrucao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    @EJB
    private HabiteseConstrucaoFacade habiteseConstrucaoFacade;
    @Resource
    private SessionContext sessionContext;
    @EJB
    private SistemaFacade sistemaFacade;

    public ProcRegularizaConstrucaoFacade() {
        super(ProcRegularizaConstrucao.class);
    }

    @Override
    public ProcRegularizaConstrucao recuperar(Object id) {
        return inicializar(super.recuperar(id));
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return inicializar((ProcRegularizaConstrucao) super.recuperar(entidade, id));
    }

    private ProcRegularizaConstrucao inicializar(ProcRegularizaConstrucao procRegularizaConstrucao) {
        em.refresh(procRegularizaConstrucao);
        Hibernate.initialize(procRegularizaConstrucao.getCadastroImobiliario().getPropriedade());
        Hibernate.initialize(procRegularizaConstrucao.getCadastroImobiliario().getLote().getTestadas());
        Hibernate.initialize(procRegularizaConstrucao.getCadastroImobiliario().getLote().getTestadaPrincipal().getFace());
        Hibernate.initialize(procRegularizaConstrucao.getCadastroImobiliario().getLote().getTestadaPrincipal().getFace().getLogradouroBairro());
        Hibernate.initialize(procRegularizaConstrucao.getCadastroImobiliario().getConstrucoes());
        for (Construcao construcao : procRegularizaConstrucao.getCadastroImobiliario().getConstrucoes()) {
            Hibernate.initialize(construcao.getCaracteristicasConstrucao());
        }
        Hibernate.initialize(procRegularizaConstrucao.getAlvarasDeConstrucao());
        for (AlvaraConstrucao alvaraConstrucao : procRegularizaConstrucao.getAlvarasDeConstrucao()) {
            Hibernate.initialize(alvaraConstrucao.getServicos());
            if (alvaraConstrucao.getProcessoCalcAlvaConstVisto() != null) {
                Hibernate.initialize(alvaraConstrucao.getProcessoCalcAlvaConstVisto().getCalculosAlvaraConstrucaoHabitese());
                for (CalculoAlvaraConstrucaoHabitese calculoAlvaraConstrucaoHabitese : alvaraConstrucao.getProcessoCalcAlvaConstVisto().getCalculosAlvaraConstrucaoHabitese()) {
                    Hibernate.initialize(calculoAlvaraConstrucaoHabitese.getItensCalculo());
                }
            }
            if (alvaraConstrucao.getProcessoCalcAlvaConstHabi() != null) {
                Hibernate.initialize(alvaraConstrucao.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese());
                for (CalculoAlvaraConstrucaoHabitese calculoAlvaraConstrucaoHabitese : alvaraConstrucao.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese()) {
                    Hibernate.initialize(calculoAlvaraConstrucaoHabitese.getItensCalculo());
                }
            }
        }
        if (procRegularizaConstrucao.getUltimoAlvara() != null) {
            Hibernate.initialize(procRegularizaConstrucao.getUltimoAlvara().getHabiteses());
            for (Habitese habitese : procRegularizaConstrucao.getUltimoAlvara().getHabiteses()) {
                Hibernate.initialize(habitese.getDeducoes());
                Hibernate.initialize(habitese.getCaracteristica());
                Hibernate.initialize(habitese.getProcessoCalcAlvaConstHabi());
                if (habitese.getProcessoCalcAlvaConstHabi() != null) {
                    Hibernate.initialize(habitese.getProcessoCalcAlvaConstHabi());
                    for (CalculoAlvaraConstrucaoHabitese calculoAlvaraConstrucaoHabitese : habitese.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese()) {
                        Hibernate.initialize(calculoAlvaraConstrucaoHabitese.getItensCalculo());
                    }
                }
            }
        }
        return procRegularizaConstrucao;
    }

    private Long buscarUltimoCodigoMaisUm() {
        Query q = em.createNativeQuery("select nvl(max(codigo),0)+1 from ProcRegularizaConstrucao");
        return ((Number) q.getResultList().get(0)).longValue();
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public AlvaraConstrucaoFacade getAlvaraConstrucaoFacade() {
        return alvaraConstrucaoFacade;
    }

    public HabiteseConstrucaoFacade getHabiteseConstrucaoFacade() {
        return habiteseConstrucaoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(ProcRegularizaConstrucao entity) {
        entity.setCodigo(buscarUltimoCodigoMaisUm());
        super.salvarNovo(entity);
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    public List<ProcRegularizaConstrucao> buscarProcessosPorCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        String sql = "select p.* from ProcRegularizaConstrucao p where p.cadastroImobiliario_id = :idCadastro";
        Query q = em.createNativeQuery(sql, ProcRegularizaConstrucao.class);
        q.setParameter("idCadastro", cadastroImobiliario.getId());
        List<ProcRegularizaConstrucao> processos = q.getResultList();
        for (ProcRegularizaConstrucao proc : processos) {
            inicializar(proc);
        }
        return processos;
    }

    public List<ProcRegularizaConstrucao> buscarProcessos(CriteriaQuery<ProcRegularizaConstrucao> criteria) {
        TypedQuery<ProcRegularizaConstrucao> typedQuery = em.createQuery(criteria);
        List<ProcRegularizaConstrucao> processos = typedQuery.getResultList();
        for (ProcRegularizaConstrucao proc : processos) {
            inicializar(proc);
        }
        return processos;
    }

    public CriteriaQuery<ProcRegularizaConstrucao> criarCriteriaConsulta(String anoProtocolo, String numeroProtocolo, String inscricaoCadastral, String cpfCnpj, Integer numeroProcesso, Integer anoProcesso, Date dataCriacaoProcesso) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ProcRegularizaConstrucao> query = builder.createQuery(ProcRegularizaConstrucao.class);
        Root<ProcRegularizaConstrucao> root = query.from(ProcRegularizaConstrucao.class);
        Join<ProcRegularizaConstrucao, Exercicio> exercicio = root.join("exercicio");
        Join<ProcRegularizaConstrucao, CadastroImobiliario> cadastroImobiliario = root.join("cadastroImobiliario");
        Join<Propriedade, CadastroImobiliario> propriedade = cadastroImobiliario.join("propriedade");
        List<Predicate> predicates = Lists.newArrayList();
        if (numeroProcesso != null) {
            predicates.add(builder.equal(root.get("codigo"), numeroProcesso));
        }
        if (anoProcesso != null) {
            predicates.add(builder.equal(exercicio.get("ano"), anoProcesso));
        }
        if (!Strings.isNullOrEmpty(anoProtocolo)) {
            predicates.add(builder.equal(root.get("anoProtocolo"), anoProtocolo));
        }
        if (!Strings.isNullOrEmpty(numeroProtocolo)) {
            predicates.add(builder.equal(root.get("numeroProtocolo"), numeroProtocolo));
        }
        if (!Strings.isNullOrEmpty(inscricaoCadastral)) {
            predicates.add(builder.equal(cadastroImobiliario.get("inscricaoCadastral"), inscricaoCadastral));
        }
        if (!Strings.isNullOrEmpty(cpfCnpj)) {
            predicates.add(builder.equal(propriedade.get("pessoa"), pessoaFacade.buscarPessoaPorCpfOrCnpj(cpfCnpj)));
        }
        if (dataCriacaoProcesso != null) {
            predicates.add(builder.equal(root.get("dataCriacao"), dataCriacaoProcesso));
        }
        query.select(root);
        if (predicates != null && !predicates.isEmpty()) {
            query.where(builder.and(predicates.toArray(new Predicate[0])));
        }
        query.orderBy(builder.asc(root.get("codigo")));
        return query;
    }

    public List<ProcRegularizaConstrucao> listarPorSituacao(String parte, ProcRegularizaConstrucao.Situacao... situacao) {
        if (parte == null) {
            parte = "";
        }
        Query query = em.createQuery("from ProcRegularizaConstrucao where " +
            "(numeroProtocolo like :parte " +
            "or anoProtocolo like :parte " +
            "or cadastroImobiliario.inscricaoCadastral like :parte) " + (situacao != null && situacao.length > 0 ? "and situacao in (:situa)" : ""));
        query.setParameter("parte", "%" + parte.trim() + "%");
        if (situacao != null && situacao.length > 0) {
            query.setParameter("situa", Arrays.asList(situacao));
        }
        return query.getResultList();
    }

    public void atualizarSituacaoProcesso(Long idProcesso, ProcRegularizaConstrucao.Situacao situacao) {
        if (idProcesso == null || situacao == null) return;
        Query query = em.createNativeQuery("update ProcRegularizaConstrucao set SITUACAO = :situacao where id = :idProcesso");
        query.setParameter("idProcesso", idProcesso);
        query.setParameter("situacao", situacao.name());
        query.executeUpdate();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public ProcRegularizaConstrucao salvarRetornando(ProcRegularizaConstrucao entity) {
        if (entity.getId() == null) {
            entity.setCodigo(buscarUltimoCodigoMaisUm());
        }
        return super.salvarRetornando(entity);
    }
}
