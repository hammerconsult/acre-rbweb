package br.com.webpublico.negocios.rh.concursos;

import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.concursos.EtapaConcurso;
import br.com.webpublico.entidades.rh.concursos.FaseConcurso;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by venom on 22/10/14.
 */
@Stateless
public class ConcursoFacade extends AbstractFacade<Concurso> {

    private final Logger log = LoggerFactory.getLogger(ConcursoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private FaseConcursoFacade faseConcursoFacade;
    @EJB
    private AvaliacaoConcursoFacade avaliacaoConcursoFacade;
    @EJB
    private BancaExaminadoraFacade bancaExaminadoraFacade;

    public ConcursoFacade() {
        super(Concurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Concurso recuperar(Object id) {
        Concurso c = super.recuperar(id);
        c.getLocais().size();
        c.getCargos().size();
        c.getEtapas().size();
        c.getInscricoes().size();
        c.getFases().size();
        c.getRecursos().size();
        c.getDesempates().size();
        Hibernate.initialize(c.getPublicacoes());

        Util.ordenarListas(c.getInscricoes(), c.getEtapas());
        return c;
    }

    @Override
    public void salvarNovo(Concurso entity) {
        entity = entityManager.merge(entity);
        adicionarEtapaAoConcurso(entity, TipoEtapa.CRIACAO);
    }

    @Override
    public void remover(Concurso entity) {
        if (temPublicacaoParaOconcurso(entity)) {
            throw new ExcecaoNegocioGenerica("Foi encontrado publicação realizada para este concurso. Somente é permitido excluir concurso sem publicação.");
        }
        if (bancaExaminadoraFacade.temBancaExaminadoraParaConcurso(entity)) {
            throw new ExcecaoNegocioGenerica("Foi encontrado banca examinadora realizada para este concurso. Somente é permitido excluir concurso sem banca examinadora.");
        }
        if (avaliacaoConcursoFacade.temAvaliacaoParaConcurso(entity)) {
            throw new ExcecaoNegocioGenerica("Foi encontrado avaliação realizada para este concurso. Somente é permitido excluir concurso sem avaliação.");
        }
        super.remover(entity);
    }

    public boolean temPublicacaoParaOconcurso(Concurso entity) {
        if (entity.getId() != null) {
            entity = recuperarConcursoComPublicacoes(entity.getId());
        }
        if (entity.temPublicacao()) {
            return true;
        }
        return false;
    }

    public Concurso recuperarConcursoComPublicacoes(Object id) {
        Concurso c = super.recuperar(id);
        c.getPublicacoes().size();
        return c;
    }

    public Concurso recuperarConcursoComCriteriosDeDesempate(Object id) {
        Concurso c = super.recuperar(id);
        c.getDesempates().size();
        return c;
    }

    public Concurso recuperarConcursoComCriteriosDeDesempateComInscricoes(Object id) {
        Concurso c = super.recuperar(id);
        c.getDesempates().size();
        return c;
    }

    public Concurso recuperarConcursoComEtapas(Object id) {
        Concurso c = super.recuperar(id);
        c.getEtapas().size();
        return c;
    }

    public Concurso recuperarConcursosParaTelaDeFases(Object id) {
        Concurso c = super.recuperar(id);
        c.getFases().size();
        if (c.getFases() != null && !c.getFases().isEmpty()) {
            for (FaseConcurso faseConcurso : c.getFases()) {
                Hibernate.initialize(faseConcurso.getAnexos());
                faseConcurso.getProvas().size();
            }
        }
        c.getCargos().size();
        return c;
    }

    public Concurso recuperarConcursoParaTelaDeInscricao(Long id) {
        Concurso c = super.recuperar(id);
        c.getInscricoes().size();
        c.getCargos().size();

        Util.ordenarListas(c.getInscricoes(), c.getCargos());
        return c;
    }

    public Concurso recuperarConcursoParaTelaDeRecurso(Object id) {
        Concurso c = super.recuperar(id);
        c.getEtapas().size();
        c.getInscricoes().size();
        c.getCargos().size();
        c.getFases().size();
        for (FaseConcurso fase : c.getFases()) {
            fase.getProvas().size();
        }
        return c;
    }

    public Concurso recuperarConcursoParaTelaDeAvaliacaoRecurso(Object id) {
        Concurso c = super.recuperar(id);
        c.getRecursos().size();
        c.getInscricoes().size();
        for (FaseConcurso fase : c.getFases()) {
            fase = faseConcursoFacade.recuperarFaseComProvas(fase.getId());
        }
        return c;
    }

    public Concurso recuperarConcursosParaTelaAvaliacaoDeProvas(Object id) {
        Concurso c = super.recuperar(id);
        c.getFases().size();
        if (c.getFases() != null && !c.getFases().isEmpty()) {
            for (FaseConcurso faseConcurso : c.getFases()) {
                faseConcurso.getProvas().size();
            }
        }
        c.getCargos().size();
        return c;
    }

    public Concurso buscarConcursosComCargosAndInscricoesAndCriteriosDesempate(Object id) {
        Concurso c = super.recuperar(id);
        c.getCargos().size();
        c.getDesempates().size();
        return c;
    }

    public Concurso buscarConcursoComCargos(Object id) {
        Concurso c = super.recuperar(id);
        c.getCargos().size();
        return c;
    }

    public ConsultaCepFacade getConsultaCepFacade() {
        return consultaCepFacade;
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public FaseConcursoFacade getFaseConcursoFacade() {
        return faseConcursoFacade;
    }

    public AvaliacaoConcursoFacade getAvaliacaoConcursoFacade() {
        return avaliacaoConcursoFacade;
    }

    public BancaExaminadoraFacade getBancaExaminadoraFacade() {
        return bancaExaminadoraFacade;
    }

    public List<Concurso> getUltimosConcursosNaoAvaliados() {
        String sql = "   select c.* from concurso c " +
            "    where c.id not in (select ac.concurso_id from avaliacaoconcurso ac)" +
            " order by c.codigo desc, c.ano desc, c.descricao asc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> getUltimosConcursosAprovadosSemBancaExaminadora() {
        String sql = "   select c.* from concurso c " +
            "     inner join avaliacaoconcurso ac on ac.concurso_id = c.id" +
            "    where ac.aprovado = :aprovado" +
            "      and (c.id not in (select banca.concurso_id from bancaexaminadora banca))" +
            " order by c.codigo desc, c.ano desc, c.descricao asc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setParameter("aprovado", true);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> getUltimosConcursosAprovadosSemRecurso() {
        String sql = "    select con.* " +
            "      from concurso con " +
            "inner join avaliacaoconcurso ac on ac.concurso_id = con.id " +
            "     where ac.aprovado = :aprovado " +
            "       and con.id not in (select rec.concurso_id from recursoconcurso rec where rec.concurso_id = con.id and rec.situacaorecurso = 'EM_ANDAMENTO') " +
            "  order by con.codigo desc, con.ano desc, con.descricao asc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setParameter("aprovado", true);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> getUltimosConcursosComBancaExaminadora() {
        String sql = "   select c.* from concurso c " +
            "     inner join bancaexaminadora ba on ba.concurso_id = c.id" +
            " order by c.codigo desc, c.ano desc, c.descricao asc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> getUltimosConcursosComPublicacoes() {
        String sql = "   select c.* from concurso c " +
            "     where c.id in (select pc.concurso_id from  publicacaoconcurso pc)" +
            " order by c.codigo desc, c.ano desc, c.descricao asc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> getUltimosConcursosQuePossuemFases() {
        String sql = "   select c.* from concurso c " +
            "     where c.id in (select fc.concurso_id from  faseconcurso fc)" +
            " order by c.codigo desc, c.ano desc, c.descricao asc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> getUltimosConcursosComAvaliacaoDeProva() {
        String sql = "    select distinct con.* " +
            "      from concurso con " +
            "inner join faseconcurso fase on fase.concurso_id = con.id " +
            "inner join provaconcurso prova on prova.faseconcurso_id = fase.id and prova.id in (select ava.prova_id from avaliacaoprovaconcurso ava) " +
            "  order by con.codigo desc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> buscarConcursosParaTelaDeConvocacao(){
        String sql = "   select c.* from concurso c " +
            "     where c.id in (select hc.concurso_id from  homologacaoconcurso hc)" +
            "       and c.id not in (select cc.concurso_id from convocacaoconcurso cc)" +
            " order by c.codigo desc, c.ano desc, c.descricao asc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> buscarConcursosParaTelaDeApresentacao(){
        String sql = "   select c.* from concurso c " +
            "     where c.id in (select cc.concurso_id from  convocacaoconcurso cc)" +
            "       and c.id not in (select ac.concurso_id from apresentacaoconcurso ac)" +
            " order by c.codigo desc, c.ano desc, c.descricao asc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> buscarConcursosParaTelaDeContratoFP(){
        String sql = "   select c.* from concurso c " +
            "     where c.id in (select ac.concurso_id from apresentacaoconcurso ac)" +
            // todo está faltando o código do joãozito bandashow :D
            " order by c.codigo desc, c.ano desc, c.descricao asc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Concurso> getUltimosConcursosComRecurso() {
        String sql = "   select c.* from concurso c " +
            "     where c.id in (select rc.concurso_id from  recursoconcurso rc)" +
            " order by c.codigo, c.ano, c.descricao desc";
        Query q = entityManager.createNativeQuery(sql, Concurso.class);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<EtapaConcurso> getEtapasDoConcurso(Concurso c) {
        String sql = "select e.* from etapaconcurso e where e.concurso_id = :concurso_id";
        Query q = entityManager.createNativeQuery(sql, EtapaConcurso.class);
        q.setParameter("concurso_id", c.getId());
        return q.getResultList();
    }

    public void adicionarEtapaAoConcurso(Concurso c, TipoEtapa tipo) {
        if (possuiEtapaAdicionada(c, tipo)) {
            return;
        }
        EtapaConcurso ec = new EtapaConcurso();
        ec.setCriacao(new Date());
        ec.setConcurso(c);
        ec.setData(UtilRH.getDataOperacao());
        ec.setTipoEtapa(tipo);
        entityManager.persist(ec);
    }

    public void removerEtapaDoConcurso(Concurso c, TipoEtapa tipo) {
        c = recuperarConcursoComEtapas(c.getId());
        EtapaConcurso etapa = c.getEtapaDoTipo(tipo);
        if (etapa != null) {
            c.getEtapas().remove(etapa);
            entityManager.merge(c);
        }
    }

    public boolean possuiEtapaAdicionada(Concurso c, TipoEtapa tipo) {
        String sql = "select ec.* from etapaconcurso ec where ec.concurso_id = :concurso_id and ec.tipoetapa = :tipo_etapa";
        Query q = entityManager.createNativeQuery(sql, EtapaConcurso.class);
        q.setParameter("concurso_id", c.getId());
        q.setParameter("tipo_etapa", tipo.name());
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }
}
