package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EfetivacaoAjusteBemMovel;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.ItemEfetivacaoAjusteBemMovel;
import br.com.webpublico.entidades.SolicitacaoAjusteBemMovel;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.enums.OperacaoAjusteBemMovel;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoAjusteBemMovel;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by mga on 08/03/2018.
 */
@Stateless
public class EfetivacaoAjusteBemMovelFacade extends AbstractFacade<EfetivacaoAjusteBemMovel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SolicitacaoAjusteBemMovelFacade solicitacaoAjusteBemMovelFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    public EfetivacaoAjusteBemMovelFacade() {
        super(EfetivacaoAjusteBemMovel.class);
    }

    @Override
    public EfetivacaoAjusteBemMovel recuperar(Object id) {
        EfetivacaoAjusteBemMovel entity = super.recuperar(id);
        if (entity != null) {
            if (entity.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
            }
        }
        return entity;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<BemVo>> pesquisarBensSolicitacao(AssistenteMovimentacaoBens assistente) {
        EfetivacaoAjusteBemMovel entity = (EfetivacaoAjusteBemMovel) assistente.getSelecionado();
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Pesquisando Bens da Solicitação de Ajuste de Bens Móveis " + entity.getTipoAjusteBemMovel().getDescricao() + "...");
        String sql = " " +
            " select distinct " +
            "    bem.id as idBem, " +
            "    bem.identificacao, " +
            "    bem.descricao, " +
            "    ev.id as id_evento, " +
            "    ev.situacaoeventobem as situacao_evento, " +
            "    ev.tipoeventobem as tipo_evento, " +
            "    vworc.codigo || ' - ' || vworc.descricao as unidade,  " +
            "    estInic.id as id_estado_inicial, " +
            "    estRes.id as id_estado_resultante, " +
            "    item.valorajuste as valor_ajuste " +
            " from itemsolicitacaoajustemovel item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join estadobem estInic on estInic.id = ev.estadoinicial_id " +
            "   inner join estadobem estRes on estRes.id = ev.estadoresultante_id " +
            "   inner join bem on bem.id = ev.bem_id " +
            "   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = estRes.detentoraorcamentaria_id " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " where item.solicitacaoajustebemmovel_id = :idSolicitacao " +
            " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getSolicitacaoAjusteBemMovel().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(entity.getDataEfetivacao()));
        List<Object[]> list = q.getResultList();
        List<BemVo> bensDisponiveis = Lists.newArrayList();

        assistente.setTotal(list.size());
        for (Object[] obj : list) {
            long idBem = ((BigDecimal) obj[0]).longValue();
            if (assistente.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
                String mensagem = configMovimentacaoBemFacade.validarMovimentoComDataRetroativaBem(idBem, assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getDescricao(), assistente.getDataLancamento());
                if (!Strings.isNullOrEmpty(mensagem)) {
                    assistente.getMensagens().add(mensagem);
                    continue;
                }
            }
            BemVo bemVo = novoBemVo(entity, obj, idBem);
            bensDisponiveis.add(bemVo);
            assistente.conta();
        }
        return new AsyncResult<List<BemVo>>(bensDisponiveis);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<BemVo>> pesquisarAssincronoBensEfetivacao(AssistenteMovimentacaoBens assistente) {

        EfetivacaoAjusteBemMovel entity = (EfetivacaoAjusteBemMovel) assistente.getSelecionado();
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Pesquisando Bens da Efetivação de Ajuste de Bens Móveis " + entity.getTipoAjusteBemMovel().getDescricao() + "...");

        String sql = " " +
            " select distinct " +
            "    bem.id as idBem, " +
            "    bem.identificacao, " +
            "    bem.descricao, " +
            "    ev.id as id_evento, " +
            "    ev.situacaoeventobem as situacao_evento, " +
            "    ev.tipoeventobem as tipo_evento, " +
            "    vworc.codigo || ' - ' || vworc.descricao as unidade,  " +
            "    estInic.id as id_estado_inicial, " +
            "    estRes.id as id_estado_resultante, " +
            "    ev.valordolancamento as valor_ajuste " +
            " from itemefetivacaoajustemovel item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join estadobem estInic on estInic.id = ev.estadoinicial_id " +
            "   inner join estadobem estRes on estRes.id = ev.estadoresultante_id " +
            "   inner join bem on bem.id = ev.bem_id " +
            "   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = estRes.detentoraorcamentaria_id " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " where item.efetivacaoajustebemmovel_id = :idEfetivacao " +
            " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", entity.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(entity.getDataEfetivacao()));
        List<Object[]> list = q.getResultList();
        List<BemVo> bensDisponiveis = Lists.newArrayList();

        assistente.zerarContadoresProcesso();
        assistente.setTotal(list.size());
        for (Object[] obj : list) {
            long idBem = ((BigDecimal) obj[0]).longValue();
            BemVo bemVo = novoBemVo(entity, obj, idBem);
            bensDisponiveis.add(bemVo);
            assistente.conta();
        }
        return new AsyncResult<List<BemVo>>(bensDisponiveis);
    }

    private BemVo novoBemVo(EfetivacaoAjusteBemMovel entity, Object[] obj, long idBem) {
        BemVo bemVo = new BemVo();
        bemVo.setBem(bemFacade.recuperarSemDependencias(idBem));
        bemVo.setIdentificacao((String) obj[1]);
        bemVo.setDescricao((String) obj[2]);
        bemVo.setIdEventoBem(((BigDecimal) obj[3]).longValue());
        bemVo.setSituacaoEventoBem(SituacaoEventoBem.valueOf((String) obj[4]));
        bemVo.setTipoEventoBem(TipoEventoBem.valueOf((String) obj[5]));
        bemVo.setUnidadeOrcamentaria((String) obj[6]);
        bemVo.setEstadoInicial(bemFacade.recuperarEstadoBemPorId(((BigDecimal) obj[7]).longValue()));
        bemVo.setEstadoResultante(bemFacade.recuperarEstadoBemPorId(((BigDecimal) obj[8]).longValue()));
        bemVo.setValorAjusteInicial(bemVo.getValorAjusteInicialPorTipoAjuste(entity.getTipoAjusteBemMovel(), true));
        bemVo.setValorAjuste((BigDecimal) obj[9]);
        bemVo.setValorAjusteFinal(bemVo.calcularValorAjusteFinal(entity.getSolicitacaoAjusteBemMovel().getOperacaoAjusteBemMovel()));
        bemVo.setValorLancamento(bemVo.getValorAjuste());
        return bemVo;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteMovimentacaoBens> salvarEfetivacao(AssistenteMovimentacaoBens assistente) {
        try {
            EfetivacaoAjusteBemMovel entity = (EfetivacaoAjusteBemMovel) assistente.getSelecionado();
            atualizarSituacaoSolicitacaoAjuste(entity.getSolicitacaoAjusteBemMovel());

            if (entity.getCodigo() == null) {
                entity.setCodigo(getCodigoSequencial(entity.getTipoAjusteBemMovel()));
            }
            entity = em.merge(entity);

            movimentarBem(entity, assistente);

            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
            assistente.zerarContadoresProcesso();
        } catch (Exception ex) {
            logger.error("Erro ao salvar efetivação de ajuste bens móveis. " + ex.getMessage());
            throw ex;
        }
        return new AsyncResult<>(assistente);
    }

    private void desbloquearBens(AssistenteMovimentacaoBens assistente) {
        List<Number> bens = Lists.newArrayList();
        for (BemVo item : assistente.getBensMovimentadosVo()) {
            bens.add(item.getBem().getId());
        }
        if (!bens.isEmpty()) {
            configMovimentacaoBemFacade.desbloquearBens(assistente.getConfigMovimentacaoBem(), bens);
        }
    }

    private void atualizarSituacaoSolicitacaoAjuste(SolicitacaoAjusteBemMovel solicitacao) {
        solicitacao.setDataVersao(new Date());
        em.merge(solicitacao);
    }

    private void movimentarBem(EfetivacaoAjusteBemMovel selecionado, AssistenteMovimentacaoBens assistente) {
        assistente.setTotal(assistente.getBensMovimentadosVo().size());
        assistente.setDescricaoProcesso("Gerando Evento Bem para os Itens da Efetivação...");

        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            criarNovoEventoItemEfetivcacao(selecionado, bemVo, assistente);
            SituacaoEventoBem situacaoEventoBem = selecionado.isSolicitacaoAceita() ? SituacaoEventoBem.FINALIZADO : SituacaoEventoBem.RECUSADO;
            bemFacade.atualizarSituacaoEventoBem(bemVo.getIdEventoBem(), situacaoEventoBem);
            assistente.conta();
        }
        if (selecionado.isSolicitacaoAceita() && selecionado.getSolicitacaoAjusteBemMovel().getContabilizar()) {
            contabilizar(assistente);
        }
        desbloquearBens(assistente);
    }

    private void criarNovoEventoItemEfetivcacao(EfetivacaoAjusteBemMovel selecionado, BemVo bemVo, AssistenteMovimentacaoBens assistente) {
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        ItemEfetivacaoAjusteBemMovel itemEfetivacao = new ItemEfetivacaoAjusteBemMovel(selecionado.getTipoEventoBem(), selecionado.getTipoOperacao());

        EstadoBem estadoInicial = selecionado.getSolicitacaoAjusteBemMovel().getContabilizar()
            ? bemFacade.recuperarUltimoEstadoDoBem(bemVo.getBem())
            : bemFacade.buscarEstadoDoBemPorData(bemVo.getBem(), assistente.getDataLancamento());

        EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial));
        if (selecionado.isSolicitacaoAceita()) {
            definirValor(selecionado, estadoResultante, bemVo);
        }
        itemEfetivacao.setSituacaoEventoBem(selecionado.isSolicitacaoAceita() ? SituacaoEventoBem.FINALIZADO : SituacaoEventoBem.RECUSADO);
        itemEfetivacao.setEfetivacaoAjusteBemMovel(selecionado);
        itemEfetivacao.setDataLancamento(dataLancamento);
        if (!selecionado.getSolicitacaoAjusteBemMovel().getContabilizar()) {
            itemEfetivacao.setDataOperacao(dataLancamento);
        }
        itemEfetivacao.setBem(bemVo.getBem());
        itemEfetivacao.setValorDoLancamento(selecionado.isSolicitacaoAceita() ? bemVo.getValorLancamento() : BigDecimal.ZERO);
        itemEfetivacao.setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());
        itemEfetivacao.setEstadoInicial(estadoInicial);
        itemEfetivacao.setEstadoResultante(estadoResultante);
        em.persist(itemEfetivacao);
    }

    private void definirValor(EfetivacaoAjusteBemMovel selecionado, EstadoBem estadoResultante, BemVo bemVo) {
        OperacaoAjusteBemMovel operacao = selecionado.getSolicitacaoAjusteBemMovel().getOperacaoAjusteBemMovel();
        switch (selecionado.getTipoAjusteBemMovel()) {
            case ORIGINAL:
                estadoResultante.setValorOriginal(operacao.isAumentativoOriginal()
                    ? estadoResultante.getValorOriginal().add(bemVo.getValorAjuste())
                    : estadoResultante.getValorOriginal().subtract(bemVo.getValorAjuste()));
                break;
            case DEPRECIACAO:
                estadoResultante.setValorAcumuladoDaDepreciacao(operacao.isDiminutivoDepreciacao()
                    ? estadoResultante.getValorAcumuladoDaDepreciacao().add(bemVo.getValorAjuste())
                    : estadoResultante.getValorAcumuladoDaDepreciacao().subtract(bemVo.getValorAjuste()));
                break;
            case AMORTIZACAO:
                estadoResultante.setValorAcumuladoDaAmortizacao(operacao.isDiminutivoAmortizacao()
                    ? estadoResultante.getValorAcumuladoDaAmortizacao().add(bemVo.getValorAjuste())
                    : estadoResultante.getValorAcumuladoDaAmortizacao().subtract(bemVo.getValorAjuste()));
                break;
            default:
                throw new ExcecaoNegocioGenerica("Erro ao definir o valor do ajuste para a efetivação de " + selecionado.getTipoAjusteBemMovel().getDescricao());
        }
    }

    private void contabilizar(AssistenteMovimentacaoBens assistente) {
        EfetivacaoAjusteBemMovel selecionado = (EfetivacaoAjusteBemMovel) assistente.getSelecionado();
        integradorPatrimonialContabilFacade.contabilizarAjusteBemMovel(
            getHistoricoContabilizacao(selecionado),
            isOperacaoAumentativo(selecionado),
            selecionado.getTipoAjusteBemMovel(),
            false,
            assistente,
            selecionado.getSolicitacaoAjusteBemMovel().getOperacaoAjusteBemMovel());
    }

    private String getHistoricoContabilizacao(EfetivacaoAjusteBemMovel selecionado) {
        return selecionado.getSolicitacaoAjusteBemMovel().getOperacaoAjusteBemMovel() + "( " + selecionado.toString() + " )";
    }

    private boolean isOperacaoAumentativo(EfetivacaoAjusteBemMovel selecionado) {
        return OperacaoAjusteBemMovel.retornaOperacoesAumentativo().contains(selecionado.getSolicitacaoAjusteBemMovel().getOperacaoAjusteBemMovel());
    }

    public void simularContabilizacaoAjusteValorBem(AssistenteMovimentacaoBens assistente) {
        ValidacaoException ve = new ValidacaoException();
        try {
            EfetivacaoAjusteBemMovel selecionado = (EfetivacaoAjusteBemMovel) assistente.getSelecionado();
            integradorPatrimonialContabilFacade.contabilizarAjusteBemMovel(
                getHistoricoContabilizacao(selecionado),
                isOperacaoAumentativo(selecionado),
                selecionado.getTipoAjusteBemMovel(),
                true,
                assistente,
                selecionado.getSolicitacaoAjusteBemMovel().getOperacaoAjusteBemMovel());
        } catch (ExcecaoNegocioGenerica ex) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(ex.getMessage());
        }
        ve.lancarException();
    }

    private Long getCodigoSequencial(TipoAjusteBemMovel tipoAjusteBemMovel) {
        String sql = " select coalesce(max(to_number(codigo)),0)+1 from EfetivacaoAjusteBemMovel where tipoAjusteBemMovel = :tipoAjuste ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("tipoAjuste", tipoAjusteBemMovel.name());
        try {
            BigDecimal b = (BigDecimal) q.getSingleResult();
            return b.longValue();
        } catch (NoResultException e) {
            return 1L;
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SolicitacaoAjusteBemMovelFacade getSolicitacaoAjusteBemMovelFacade() {
        return solicitacaoAjusteBemMovelFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}
