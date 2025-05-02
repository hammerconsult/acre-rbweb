package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 29/01/15
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CessaoDevolucaoFacade extends AbstractFacade<LoteCessaoDevolucao> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private BemFacade bemFacade;
    @EJB
    private LoteCessaoFacade loteCessaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private SolicitacaoProrrogacaoCessaoFacade solicitacaoProrrogacaoCessaoFacade;
    @EJB
    private AvaliacaoProrrogacaoCessaoFacade avaliacaoProrrogacaoCessaoFacade;
    @EJB
    private LoteEfetivacaoCessaoFacade loteEfetivacaoCessaoFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CessaoDevolucaoFacade() {
        super(LoteCessaoDevolucao.class);
    }

    @Override
    public LoteCessaoDevolucao recuperar(Object id) {
        LoteCessaoDevolucao lote = super.recuperar(id);
        lote.getBensDevolvidos().size();
        lote.getDetentorArquivoComposicao().getArquivosComposicao().size();
        return lote;
    }

    @Override
    public void remover(LoteCessaoDevolucao entity) {
        ConfigMovimentacaoBem configMovimentacaoBem = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(entity.getData(), OperacaoMovimentacaoBem.DEVOLUCAO_CESSAO_BEM);
        if (entity.getId() != null) {
            List<Number> bens = buscarIdsBemDevolucaoCessao(entity);
            configMovimentacaoBemFacade.deletarMovimentoBloqueioBem(configMovimentacaoBem, bens);
        }
        super.remover(entity);
    }

    public LoteCessaoDevolucao salvar(LoteCessaoDevolucao entity, Cessao[] cessaoSelecionados) {
        entity.getBensDevolvidos().clear();

        ConfigMovimentacaoBem configMovimentacaoBem = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(entity.getData(), OperacaoMovimentacaoBem.DEVOLUCAO_CESSAO_BEM);
        if (entity.getId() != null) {
            List<Number> bens = buscarIdsBemDevolucaoCessao(entity);
            configMovimentacaoBemFacade.deletarMovimentoBloqueioBem(configMovimentacaoBem, bens);
        }

        ValidacaoException ve = new ValidacaoException();
        List<Number> bens = Lists.newArrayList();
        for (Cessao cessao : cessaoSelecionados) {
            if (loteCessaoFacade.hasCessaoDevolucao(cessao)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O bem " + cessao.getBem() + " já foi devolvido em outra devolução.");
            } else {
                CessaoDevolucao devolucao = new CessaoDevolucao();
                devolucao.setConservacaoBem(cessao.getConservacaoBem());
                Date dataLancamento = DataUtil.getDataComHoraAtual(entity.getData());
                devolucao.setDataLancamento(dataLancamento);
                devolucao.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
                devolucao.setValorDoLancamento(cessao.getValorDoLancamento());
                devolucao.setBem(cessao.getBem());
                devolucao.setLoteCessaoDevolucao(entity);
                devolucao.setCessao(cessao);
                devolucao.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
                EstadoBem ultimoEstadoBem = bemFacade.recuperarUltimoEstadoDoBem(cessao.getBem());
                EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstadoBem));
                devolucao.setEstadoInicial(ultimoEstadoBem);
                devolucao.setEstadoResultante(estadoResultante);
                entity.getBensDevolvidos().add(devolucao);
                bens.add(devolucao.getBem().getId());
            }
        }
        ve.lancarException();
        configMovimentacaoBemFacade.bloquearBens(configMovimentacaoBem, bens);

        entity = em.merge(entity);
        Hibernate.initialize(entity.getBensDevolvidos());
        return entity;
    }

    public LoteCessaoDevolucao concluir(LoteCessaoDevolucao entity, Cessao[] cessaoSelecionados, ConfigMovimentacaoBem configuracao) {
        entity = salvar(entity, cessaoSelecionados);
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(LoteCessaoDevolucao.class, "codigo"));
        }
        entity.setSituacao(SituacaoEventoBem.CONCLUIDO);
        ValidacaoException ve = new ValidacaoException();

        List<Number> bens = Lists.newArrayList();

        for (CessaoDevolucao devolucao : entity.getBensDevolvidos()) {
            if (loteCessaoFacade.hasCessaoDevolucao(devolucao.getCessao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O bem " + devolucao.getBem() + " já foi devolvido em outra devolução;");
            } else {
                Date dataLancamento = DataUtil.getDataComHoraAtual(entity.getData());
                devolucao.setDataLancamento(dataLancamento);
                devolucao.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
                bens.add(devolucao.getBem().getId());
            }
        }
        ve.lancarException();
        concluirEventoEfetivacaoCessao(entity.getLoteCessao());
        configMovimentacaoBemFacade.desbloquearBens(configuracao, bens);
        return em.merge(entity);
    }

    private void concluirEventoEfetivacaoCessao(LoteCessao loteCessao) {
        LoteEfetivacaoCessao efetivacaoCessao = loteEfetivacaoCessaoFacade.recuperarEfetivacaoCessaoPorLote(loteCessao);
        if (efetivacaoCessao != null) {
            efetivacaoCessao = loteEfetivacaoCessaoFacade.recuperar(efetivacaoCessao.getId());
            for (EfetivacaoCessao itemEfetivacao : efetivacaoCessao.getListaEfetivacaoCessao()) {
                itemEfetivacao.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
                em.merge(itemEfetivacao);
            }
        }
    }

    public List<Number> buscarIdsBemDevolucaoCessao(LoteCessaoDevolucao entity) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from cessaodevolucao item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join lotecessaodevolucao dev on dev.id = item.lotecessaodevolucao_id " +
            " where dev.id = :idDevolucao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDevolucao", entity.getId());
        return ((List<Number>) q.getResultList());
    }


    public List<CessaoDevolucao> buscarCessoesDevolvidas(LoteCessao loteCessao) {
        String hql = "Select distinct cd " +
            "   From CessaoDevolucao cd" +
            "    inner join cd.loteCessaoDevolucao lote " +
            "    inner join lote.loteCessao cs  " +
            " where cs = :lote " +
            "   and cd.situacaoEventoBem = :finalizado";
        Query q = em.createQuery(hql);
        q.setParameter("lote", loteCessao);
        q.setParameter("finalizado", SituacaoEventoBem.FINALIZADO);
        return q.getResultList();
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public LoteCessaoFacade getLoteCessaoFacade() {
        return loteCessaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}
