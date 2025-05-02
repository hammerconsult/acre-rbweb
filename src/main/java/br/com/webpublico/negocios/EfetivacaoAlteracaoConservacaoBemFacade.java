package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class EfetivacaoAlteracaoConservacaoBemFacade extends AbstractFacade<EfetivacaoAlteracaoConservacaoBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private PesquisaBemFacade pesquisaBemFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private SolicitacaoAlteracaoConservacaoBemFacade solicitacaoAlteracaoConservacaoBemFacade;

    public EfetivacaoAlteracaoConservacaoBemFacade() {
        super(EfetivacaoAlteracaoConservacaoBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EfetivacaoAlteracaoConservacaoBem recuperarComDependenciasAqruivo(Object id) {
        EfetivacaoAlteracaoConservacaoBem lote = super.recuperar(id);
        if (lote.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(lote.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return lote;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteMovimentacaoBens> salvarEfetivacao(AssistenteMovimentacaoBens assistente) {
        EfetivacaoAlteracaoConservacaoBem entity = (EfetivacaoAlteracaoConservacaoBem) assistente.getSelecionado();
        assistente.configurarInicializacao("Inicializando Processo...", 0);
        try {
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(EfetivacaoAlteracaoConservacaoBem.class, "codigo"));
            }
            atualizarItemSolicitacao(assistente, entity);
            entity = em.merge(entity);

            criarEventoAlteracaoConservacao(entity, assistente);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso("");
        } catch (Exception ex) {
            logger.error("Erro ao salvar efetivação de alteração de conservação do bem. ", ex);
            throw ex;
        }
        return new AsyncResult<>(assistente);
    }

    private void atualizarItemSolicitacao(AssistenteMovimentacaoBens assistente, EfetivacaoAlteracaoConservacaoBem entity) {
        assistente.configurarInicializacao("Finalizando Evento da Solicitação de Alteração de Conservação de Bens " + entity.getTipoBem().getDescricao() + "...", assistente.getBensMovimentadosVo().size());

        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            ItemSolicitacaoAlteracaoConservacaoBem itemSol = em.find(ItemSolicitacaoAlteracaoConservacaoBem.class, bemVo.getIdEventoBem());
            itemSol.setSituacaoEventoBem(assistente.getSituacaoMovimento().isRecusado() ? SituacaoEventoBem.RECUSADO : SituacaoEventoBem.FINALIZADO);
            em.merge(itemSol);
            assistente.conta();
        }
        em.merge(entity.getSolicitacaoAlteracaoConsBem());
    }

    private EstadoBem criaEstadoResultante(EfetivacaoAlteracaoConservacaoBem entity, EstadoBem ultimoEstado) {
        EstadoBem estadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        estadoResultante.setEstadoBem(entity.getSolicitacaoAlteracaoConsBem().getEstadoConservacao());
        estadoResultante.setSituacaoConservacaoBem(entity.getSolicitacaoAlteracaoConsBem().getSituacaoConservacao());
        return em.merge(estadoResultante);
    }

    private void criarEventoAlteracaoConservacao(EfetivacaoAlteracaoConservacaoBem entity, AssistenteMovimentacaoBens assistente) {
        assistente.configurarInicializacao("Criando Evento da Efetivação de Alteração de Conservação de Bens " + entity.getTipoBem().getDescricao() + "...", assistente.getBensMovimentadosVo().size());

        List<Number> bensBloqueados = Lists.newArrayList();
        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            Bem bem = bemVo.getBem();
            EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(bem);

            AlteracaoConservacaoBem novoItem = new AlteracaoConservacaoBem(bem, ultimoEstado, entity, assistente.getDataLancamento());
            EstadoBem estadoResultante = criaEstadoResultante(entity, ultimoEstado);
            novoItem.setEstadoResultante(estadoResultante);
            novoItem.setSituacaoEventoBem(assistente.getSituacaoMovimento().isRecusado() ? SituacaoEventoBem.RECUSADO : SituacaoEventoBem.FINALIZADO);
            em.merge(novoItem);

            bensBloqueados.add(bem.getId());
            assistente.conta();
        }
        configMovimentacaoBemFacade.desbloquearBens(assistente.getConfigMovimentacaoBem(), bensBloqueados);
    }

    public List<EfetivacaoAlteracaoConservacaoBem> buscarEfetivacoes(String part) {
        String sql = " select efet.* from efetivacaoalteracaoconsbem efet " +
            "          where (efet.codigo like :parte or efet.descricao like :parte)";
        Query q = em.createNativeQuery(sql, EfetivacaoAlteracaoConservacaoBem.class);
        q.setParameter("parte", "%" + part.trim() + "%");
        return q.getResultList();
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public void setSingletonBloqueioPatrimonio(SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio) {
        this.singletonBloqueioPatrimonio = singletonBloqueioPatrimonio;
    }

    public SolicitacaoAlteracaoConservacaoBemFacade getSolicitacaoAlteracaoConservacaoBemFacade() {
        return solicitacaoAlteracaoConservacaoBemFacade;
    }

    public PesquisaBemFacade getPesquisaBemFacade() {
        return pesquisaBemFacade;
    }
}
