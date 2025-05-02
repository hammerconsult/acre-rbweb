package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Stateless
public class RestituicaoFacade extends AbstractFacade<Restituicao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContaCorrenteTributariaFacade contaCorrenteTributariaFacade;
    @EJB
    private ProcessoCreditoContaCorrenteFacade processoCreditoContaCorrenteFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;

    public RestituicaoFacade() {
        super(Restituicao.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ContaCorrenteTributariaFacade getContaCorrenteTributariaFacade() {
        return contaCorrenteTributariaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ProcessoCreditoContaCorrenteFacade getProcessoCreditoContaCorrenteFacade() {
        return processoCreditoContaCorrenteFacade;
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    @Override
    public Restituicao recuperar(Object id) {
        Restituicao restituicao = em.find(Restituicao.class, id);
        Hibernate.initialize(restituicao.getItens());
        return restituicao;
    }

    public Long buscarProxCodigoPorExercicio(Exercicio exercicio) {
        String sql = " select max(coalesce(obj.codigo,0)) from Restituicao obj "
            + " where obj.exercicio = :exercicio";
        Query query = em.createQuery(sql);
        query.setParameter("exercicio", exercicio);
        query.setMaxResults(1);
        try {
            Long resultado = (Long) query.getSingleResult();
            if (resultado == null) {
                resultado = 0L;
            }

            return resultado + 1;
        } catch (Exception e) {
            return 1L;
        }
    }

    public void processarRestituicao(Restituicao selecionado, ConfiguracaoContabil configuracaoContabil) {

        criarClasseCredor(selecionado, configuracaoContabil);
        alterarSituacaoParcela(selecionado, SituacaoParcela.RESTITUICAO);
        criarCalculoContaCorrente(selecionado);
        gerarSolicitacaoEmpenho(selecionado);

        selecionado.setSituacao(SituacaoProcessoDebito.FINALIZADO);
        em.merge(selecionado);
    }

    private void criarClasseCredor(Restituicao selecionado, ConfiguracaoContabil configuracaoContabil) {
        boolean hasClasseRestituicao = false;
        Pessoa pessoaEmpenho = em.find(Pessoa.class, selecionado.getPessoaEmpenho().getId());
        Hibernate.initialize(pessoaEmpenho.getClasseCredorPessoas());

        for (ClasseCredorPessoa classe : pessoaEmpenho.getClasseCredorPessoas()) {
            if (classe.getClasseCredor() != null && TipoClasseCredor.RESTITUICAO.equals(classe.getClasseCredor().getTipoClasseCredor())) {
                hasClasseRestituicao = true;
                break;
            }
        }
        if (!hasClasseRestituicao) {
            ClasseCredorPessoa novaClasse = criarClasseCredorPessoa(pessoaEmpenho, configuracaoContabil, selecionado);

            pessoaEmpenho.getClasseCredorPessoas().add(novaClasse);
            pessoaEmpenho = em.merge(pessoaEmpenho);

            if (OpcaoCredor.CONTRIBUINTE.equals(selecionado.getOpcaoCredor())) {
                selecionado.setContribuinte(pessoaEmpenho);
            } else {
                selecionado.setProcurador(pessoaEmpenho);
            }
        }
    }

    private void criarCalculoContaCorrente(Restituicao selecionado) throws ExcecaoNegocioGenerica {
        for (ItemRestituicao item : selecionado.getItens()) {
            ParcelaContaCorrenteTributaria parcela = recuperarParcelaContaCorrTrib(item.getParcelaValorDivida().getId());
            if (parcela != null) {
                CalculoContaCorrente calculo = criarNovoCalculo(selecionado);
                calculo.setProcessoCalculo(getContaCorrenteTributariaFacade().salvarProcessoCalculoContaCorrente(calculo.getProcessoCalculo()));
                calculo = calculo.getProcessoCalculo().getCalculoContaCorrente().get(0);
                parcela.setCalculoContaCorrente(calculo);
                parcela.setValorCompesado(item.getDiferencaAtualizada());
                parcela.setValorDiferencaUtilizada(item.getDiferencaAtualizada());
                parcela.setCompensada(true);
                item.setValorRestituido(item.getDiferencaAtualizada());
                getContaCorrenteTributariaFacade().salvarParcela(parcela);
            }
        }
    }

    private ParcelaContaCorrenteTributaria recuperarParcelaContaCorrTrib(Long idParcela) {
        String sql = " select pc.* from parcelacontacorrentetrib pc " +
            " where pc.parcelavalordivida_id = :idParcela ";

        Query q = em.createNativeQuery(sql, ParcelaContaCorrenteTributaria.class);
        q.setParameter("idParcela", idParcela);

        if (!q.getResultList().isEmpty()) {
            return (ParcelaContaCorrenteTributaria) q.getResultList().get(0);
        }
        return null;
    }

    private CalculoContaCorrente criarNovoCalculo(Restituicao restituicao) {
        ProcessoCalculoContaCorrente processoCalculoContaCorrente = new ProcessoCalculoContaCorrente();
        processoCalculoContaCorrente.setExercicio(restituicao.getExercicio());
        processoCalculoContaCorrente.setUsuarioSistema(restituicao.getUsuarioResponsavel());
        processoCalculoContaCorrente.setDataLancamento(restituicao.getDataLancamento());

        CalculoContaCorrente calculoContaCorrente = new CalculoContaCorrente();
        calculoContaCorrente.setRestituicao(restituicao);

        processoCalculoContaCorrente.getCalculoContaCorrente().add(calculoContaCorrente);
        calculoContaCorrente.setProcessoCalculo(processoCalculoContaCorrente);

        return calculoContaCorrente;
    }

    private void alterarSituacaoParcela(Restituicao selecionado, SituacaoParcela situacao) {
        for (ItemRestituicao item : selecionado.getItens()) {
            ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, item.getParcelaValorDivida().getId());

            SituacaoParcelaValorDivida situacaoAnterior = consultaDebitoFacade.getUltimaSituacao(pvd);
            SituacaoParcelaValorDivida novaSituacao = new SituacaoParcelaValorDivida(situacao, pvd,
                pvd.getSituacaoAtual().getSaldo());

            novaSituacao.setGeraReferencia(false);
            novaSituacao.setReferencia(situacaoAnterior.getReferencia());

            Hibernate.initialize(pvd.getSituacoes());
            pvd.getSituacoes().add(novaSituacao);

            em.merge(pvd);
        }
    }

    private void gerarSolicitacaoEmpenho(Restituicao selecionado) {
        String complementoHistorico = "Solicitação gerada através do processo de restituição n.º: " + selecionado.getCodigo()
            + " em: " + DataUtil.getDataFormatada(selecionado.getDataRestituicao());

        SolicitacaoEmpenho solicitacaoEmpenho = solicitacaoEmpenhoFacade.gerarSolicitacaoEmpenhoSalvando(
            selecionado.getValorRestituir().setScale(2, RoundingMode.HALF_EVEN), selecionado.getUnidadeEmpenho(), null, null, null, null,
            selecionado.getDataRestituicao(), null, complementoHistorico, selecionado.getPessoaEmpenho(), false,
            null, null, null, OrigemSolicitacaoEmpenho.RESTITUICAO, selecionado.getNumProtocolo());

        selecionado.setSolicitacaoEmpenho(solicitacaoEmpenho);

        em.merge(solicitacaoEmpenho);
    }

    private ClasseCredorPessoa criarClasseCredorPessoa(Pessoa pessoaEmpenho, ConfiguracaoContabil configuracaoContabil, Restituicao selecionado) {
        ClasseCredorPessoa novaClasse = new ClasseCredorPessoa();
        novaClasse.setClasseCredor(configuracaoContabil.getClasseTribContRestituicao());
        novaClasse.setPessoa(pessoaEmpenho);
        novaClasse.setDataInicioVigencia(selecionado.getDataRestituicao());
        novaClasse.setOperacaoClasseCredor(OperacaoClasseCredor.EXTRA);
        return novaClasse;
    }

    public Restituicao salvarRestituicao(Restituicao restituicao) {
        return em.merge(restituicao);
    }

    public ValorDivida recuperarItens(Long id) {
        ValorDivida vd = em.find(ValorDivida.class, id);
        Hibernate.initialize(vd.getItemValorDividas());
        return vd;
    }

    public Empenho buscarEmpenhoDaSolicitacao(Long idRestituicao) {
        String sql = " select e.* from solicitacaoempenho solicitacao " +
            " inner join restituicao res on res.solicitacaoempenho_id = solicitacao.id " +
            " inner join empenho e on solicitacao.empenho_id = e.id " +
            " where res.id = :idRestit ";

        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idRestit", idRestituicao);

        List<Empenho> empenhos = q.getResultList();

        if (empenhos != null && !empenhos.isEmpty()) {
            Empenho empenho = empenhos.get(0);
            Hibernate.initialize(empenho.getEmpenhoEstornos());

            return empenho;
        }
        return null;
    }

    public void estornarRestituicaoAndSolicitacaoEmpenho(Restituicao selecionado, Empenho empenho) {
        alterarSituacaoParcela(selecionado, SituacaoParcela.PAGO);
        selecionado.setSituacao(SituacaoProcessoDebito.ESTORNADO);


        for (ItemRestituicao item : selecionado.getItens()) {
            ParcelaContaCorrenteTributaria parcela = recuperarParcelaContaCorrTrib(item.getParcelaValorDivida().getId());
            if (parcela != null) {
                parcela.setCalculoContaCorrente(null);
                parcela.setCompensada(false);
                parcela.setValorCompesado(parcela.getValorCompesado().subtract(item.getValorRestituido()));
                parcela.setValorDiferencaUtilizada(parcela.getValorDiferencaAtualizada().subtract(item.getValorRestituido()));
            }

            if (item.getValorRestituido().compareTo(BigDecimal.ZERO) > 0) {
                item.setValorRestituido(BigDecimal.ZERO);
            }
        }
        if(empenho == null) {
            SolicitacaoEmpenho solicitacao = em.find(SolicitacaoEmpenho.class, selecionado.getSolicitacaoEmpenho().getId());
            selecionado.setSolicitacaoEmpenho(null);
            em.merge(selecionado);
            excluirSolicitacaoEmpenhoNaoEfetivada(solicitacao);
        } else {
            em.merge(selecionado);
        }
    }

    private void excluirSolicitacaoEmpenhoNaoEfetivada(SolicitacaoEmpenho solicitacao) {
        if (solicitacao != null) {
            solicitacaoEmpenhoFacade.remover(solicitacao);
        }
    }

    public Restituicao buscarRestituicaoEmAbertoPorIdPessoa(Long idContribuinte) {
        String sql = " select r.* from restituicao r " +
            " where r.contribuinte_id =  :idContribuinte " +
            " and r.situacao = :situacaoProcesso ";

        Query q = em.createNativeQuery(sql, Restituicao.class);
        q.setParameter("idContribuinte", idContribuinte);
        q.setParameter("situacaoProcesso", SituacaoProcessoDebito.EM_ABERTO.name());

        List<Restituicao> restituicoes = q.getResultList();
        return (restituicoes != null && !restituicoes.isEmpty()) ? restituicoes.get(0) : null;
    }

    public ItemRestituicao buscarUltimoProcessoRestituicaoPorParcelaValorDivida(Long idParcela) {
        String sql = "select item.*" +
            "           from ItemRestituicao item " +
            "          inner join Restituicao r on r.id = item.restituicao_id" +
            "         where item.parcelaValorDivida_id = :pvd" +
            "           and r.situacao = :situacao" +
            "           order by r.id desc";
        Query q = em.createNativeQuery(sql, ItemRestituicao.class);
        try {
            q.setMaxResults(1);
            q.setParameter("pvd", idParcela);
            q.setParameter("situacao", SituacaoProcessoDebito.FINALIZADO.name());
            return (ItemRestituicao) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }

    public Restituicao buscarRestituicaoFinalizadaParaItemCreditoContaCorrente(ItemCreditoContaCorrente itemCreditoContaCorrente) {
        String sql = "select r.* from Restituicao r " +
            "inner join ItemRestituicao item on item.restituicao_id = r.id " +
            "where item.parcelaValorDivida_id = :parcela";
        Query q = em.createNativeQuery(sql, Restituicao.class);
        try {
            q.setParameter("parcela", itemCreditoContaCorrente.getParcela().getId());
            q.setMaxResults(1);
            Restituicao restituicao = (Restituicao) q.getSingleResult();
            if (SituacaoProcessoDebito.FINALIZADO.equals(restituicao.getSituacao())) {
                for (ItemRestituicao itemRestituicao : restituicao.getItens()) {
                    if (itemRestituicao.getValorRestituido().compareTo(BigDecimal.ZERO) > 0) {
                        return restituicao;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao buscar a restituicao: {}", ex);
        }
        return null;
    }
}
