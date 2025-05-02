package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BensMoveisBloqueio;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.entidadesauxiliares.VOItemBaixaPatrimonial;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 10/06/14
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SolicitacaoBaixaPatrimonialFacade extends AbstractFacade<SolicitacaoBaixaPatrimonial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private LeilaoAlienacaoFacade leilaoAlienacaoFacade;
    @EJB
    private EfetivacaoSolicitacaoIncorporacaoMovelFacade efetivacaoSolicitacaoIncorporacaoMovelFacade;
    @EJB
    private EfetivacaoAquisicaoFacade efetivacaoAquisicaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    public SolicitacaoBaixaPatrimonialFacade() {
        super(SolicitacaoBaixaPatrimonial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SolicitacaoBaixaPatrimonial recuperar(Object id) {
        SolicitacaoBaixaPatrimonial solicitacao = super.recuperar(id);
        Hibernate.initialize(solicitacao.getListaItemSolicitacao());
        if (solicitacao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(solicitacao.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        if (solicitacao.getUnidadeAdministrativa() != null) {
            HierarquiaOrganizacional hoAdm = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                solicitacao.getUnidadeAdministrativa(),
                solicitacao.getDataSolicitacao());
            solicitacao.setHierarquiaAdministrativa(hoAdm);
        }
        if (solicitacao.getUnidadeOrcamentaria() != null) {
            HierarquiaOrganizacional hoOrc = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                solicitacao.getUnidadeOrcamentaria(),
                solicitacao.getDataSolicitacao());
            solicitacao.setHierarquiaOrcamentaria(hoOrc);
        }
        return solicitacao;
    }

    public SolicitacaoBaixaPatrimonial recuperarComDependenciasArquivo(Object id) {
        SolicitacaoBaixaPatrimonial solicitacao = super.recuperar(id);
        if (solicitacao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(solicitacao.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        if (solicitacao.getUnidadeAdministrativa() != null) {
            HierarquiaOrganizacional hoAdm = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                solicitacao.getUnidadeAdministrativa(),
                solicitacao.getDataSolicitacao());
            solicitacao.setHierarquiaAdministrativa(hoAdm);
        }
        if (solicitacao.getUnidadeOrcamentaria() != null) {
            HierarquiaOrganizacional hoOrc = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                solicitacao.getUnidadeOrcamentaria(),
                solicitacao.getDataSolicitacao());
            solicitacao.setHierarquiaOrcamentaria(hoOrc);
        }
        return solicitacao;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public SolicitacaoBaixaPatrimonial concluirSolicitacaoBaixa(SolicitacaoBaixaPatrimonial entity, List<VOItemBaixaPatrimonial> itensSolicitacao, AssistenteMovimentacaoBens assistente) {
        try {
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Concluíndo Itens da Solicitação de Baixa Patrimonial...");
            assistente.setTotal(adicionarTotalAssistenteBarraProgresso(itensSolicitacao));
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoBaixaPatrimonial.class, "codigo"));
            }
            entity.setSituacao(SituacaoBaixaPatrimonial.AGUARDANDO_PARECER);
            entity = em.merge(entity);

            atribuirHierarquiaOrcamentariaDistinta(entity);
            movimentarSituacaoEventoBem(itensSolicitacao, assistente);

            assistente.zerarContadoresProcesso();
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao concluir solicitação de baixa patrimonial de bens móveis. " + ex.getMessage());
            throw ex;
        }
        return entity;
    }

    private Integer adicionarTotalAssistenteBarraProgresso(List<VOItemBaixaPatrimonial> itensSolicitacao) {
        int total = 0;
        for (VOItemBaixaPatrimonial unidade : itensSolicitacao) {
            total = total + unidade.getBensAgrupados().size();
        }
        return total;
    }

    private void movimentarSituacaoEventoBem(List<VOItemBaixaPatrimonial> itensSolicitacao, AssistenteMovimentacaoBens assitente) {
        List<Number> bens = Lists.newArrayList();
        for (VOItemBaixaPatrimonial unidade : itensSolicitacao) {
            for (VOItemBaixaPatrimonial item : unidade.getBensAgrupados()) {
                atribuirSituacaotemSolicitacaoBaixa(item, SituacaoEventoBem.AGUARDANDO_PARECER);
                bens.add(item.getIdBem());
                assitente.conta();
            }
        }
        configMovimentacaoBemFacade.alterarSituacaoEvento(bens, SituacaoEventoBem.AGUARDANDO_PARECER, OperacaoMovimentacaoBem.SOLICITACAO_BAIXA_PATRIMONIAL);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public void remover(SolicitacaoBaixaPatrimonial entity, ConfigMovimentacaoBem configuracao) {
        try {
            desbloquearBem(entity, configuracao);
            em.remove(em.find(SolicitacaoBaixaPatrimonial.class, entity.getId()));
        } catch (Exception ex) {
            logger.error("Erro ao remover solicitação de baixa de bens móveis {}", ex);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public SolicitacaoBaixaPatrimonial salvarRetornando(SolicitacaoBaixaPatrimonial entity, List<Bem> bensSelecionados,
                                                                AssistenteMovimentacaoBens assistente, List<VOItemBaixaPatrimonial> itensLeilaoAlienacao) {
        try {
            atribuirHierarquiaOrcamentariaDistinta(entity);
            entity.setDataVersao(new Date());
            entity = em.merge(entity);
            if (!assistente.isOperacaoNovo()) {
                processarBensRemovidos(entity, assistente.getConfigMovimentacaoBem());
            }
            salvarProcessandoBens(entity, assistente, bensSelecionados, itensLeilaoAlienacao);

            bloquearBens(entity, bensSelecionados, assistente, itensLeilaoAlienacao);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao salvar solicitação de baixa de bens móveis {}", ex);
            throw ex;
        }
        return entity;
    }

    private void bloquearBens(SolicitacaoBaixaPatrimonial entity, List<Bem> bensSelecionados, AssistenteMovimentacaoBens assistente, List<VOItemBaixaPatrimonial> itensLeilaoAlienacao) {
        if (TipoBem.MOVEIS.equals(entity.getTipoBem())) {
            List<Number> bensBloqueio = Lists.newArrayList();
            if (entity.isTipoBaixaAlienacao()) {
                for (VOItemBaixaPatrimonial lote : itensLeilaoAlienacao) {
                    for (VOItemBaixaPatrimonial item : lote.getBensAgrupados()) {
                        bensBloqueio.add(item.getIdBem());
                    }
                }
            } else {
                for (Bem bem : bensSelecionados) {
                    bensBloqueio.add(bem.getId());
                }
            }
            configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), bensBloqueio);
        }
    }

    public void bloquearBens(ConfigMovimentacaoBem configMovimentacaoBem, SolicitacaoBaixaPatrimonial entity) {
        List<Number> idsBens = buscarIdsBemPorSolicitacaoBaixa(entity);
        for (Number idBem : idsBens) {
            configMovimentacaoBemFacade.bloquearUnicoBem(configMovimentacaoBem, idBem);
        }
    }

    private void salvarProcessandoBens(SolicitacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente, List<Bem> bens, List<VOItemBaixaPatrimonial> itensLeilaoAlienacao) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Eventos Solicitação Baixa Patrimonial de Bens " + selecionado.getTipoBem().getDescricao() + "...");
        if (selecionado.isTipoBaixaAlienacao()) {
            if (assistente.isOperacaoNovo()) {
                for (VOItemBaixaPatrimonial unidade : itensLeilaoAlienacao) {
                    assistente.setTotal(assistente.getTotal() + unidade.getBensAgrupados().size());
                }
                processarBemEmElaboracaoPorAlienacao(assistente, selecionado, itensLeilaoAlienacao);
            }
        } else {
            assistente.setTotal(bens.size());
            processarBensEmElaboracao(selecionado, bens, assistente);
        }
    }

    private void processarBemEmElaboracaoPorAlienacao(AssistenteMovimentacaoBens assistente, SolicitacaoBaixaPatrimonial selecionado,
                                                      List<VOItemBaixaPatrimonial> itensLeilaoAlienacao) {

        for (VOItemBaixaPatrimonial unidade : itensLeilaoAlienacao) {
            for (VOItemBaixaPatrimonial itemLeilao : unidade.getBensAgrupados()) {
                ItemSolicitacaoBaixaPatrimonial itemSolicitacao = criarItemSolicitacaoBaixa(selecionado, itemLeilao.getBemEfetivacao(), assistente);
                em.merge(itemSolicitacao);
                assistente.conta();
            }
        }
    }

    public void processarBensEmElaboracao(SolicitacaoBaixaPatrimonial selecionado, List<Bem> bens, AssistenteMovimentacaoBens assistente) {
        for (Bem bem : bens) {
            ItemSolicitacaoBaixaPatrimonial itemSolicitacao = criarItemSolicitacaoBaixa(selecionado, bem, assistente);
            em.merge(itemSolicitacao);
            assistente.conta();
        }
    }

    private ItemSolicitacaoBaixaPatrimonial criarItemSolicitacaoBaixa(SolicitacaoBaixaPatrimonial selecionado, Bem bem, AssistenteMovimentacaoBens assistente) {
        EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(bem);
        EstadoBem estadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        estadoResultante = em.merge(estadoResultante);
        ItemSolicitacaoBaixaPatrimonial itemSolicitacaoBaixa = new ItemSolicitacaoBaixaPatrimonial();
        itemSolicitacaoBaixa.setBem(bem);
        itemSolicitacaoBaixa.setEstadoInicial(ultimoEstado);
        itemSolicitacaoBaixa.setEstadoResultante(estadoResultante);
        itemSolicitacaoBaixa.setSolicitacaoBaixa(selecionado);
        itemSolicitacaoBaixa.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        itemSolicitacaoBaixa.setDataLancamento(dataLancamento);
        itemSolicitacaoBaixa.setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());
        return itemSolicitacaoBaixa;
    }

    private void processarBensRemovidos(SolicitacaoBaixaPatrimonial selecionado, ConfigMovimentacaoBem configuracao) {
        if (selecionado.getId() != null) {
            desbloquearBem(selecionado, configuracao);
            if (!selecionado.isTipoBaixaAlienacao()) {
                List<Long> idsEstadoResultante = buscarIdsEstadoResultante(selecionado);
                List<Long> idsItemSolicitacao = buscarIdsItemSolicitacaoBaixa(selecionado);
                if (idsItemSolicitacao.isEmpty()) {
                    throw new ExcecaoNegocioGenerica("Não foi possível remover os itens da solicitação de baixa patrimonial.");
                }
                for (Number id : idsItemSolicitacao) {
                    Query deleteItem = em.createNativeQuery(" delete from itemsolicitbaixapatrimonio where id = " + id.longValue());
                    deleteItem.executeUpdate();

                    Query deleteEvento = em.createNativeQuery("delete from eventobem where id = " + id.longValue());
                    deleteEvento.executeUpdate();
                }
                for (Number idEstado : idsEstadoResultante) {
                    Query deleteEstadoBem = em.createNativeQuery(" delete from estadobem where id = " + idEstado.longValue());
                    deleteEstadoBem.executeUpdate();
                }
            }
        }
    }

    private void desbloquearBem(SolicitacaoBaixaPatrimonial selecionado, ConfigMovimentacaoBem configuracao) {
        List<Number> bensRecuperados = buscarIdsBemPorSolicitacaoBaixa(selecionado);
        configMovimentacaoBemFacade.desbloquearBens(configuracao, bensRecuperados);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<VOItemBaixaPatrimonial> buscarItensSolicitacaoBaixa(SolicitacaoBaixaPatrimonial selecionado, UnidadeOrganizacional unidadeOrganizacional, AssistenteMovimentacaoBens assistente) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select   ")
            .append("  item.id as idItem,  ")
            .append("  bem.id as idBem,   ")
            .append("  null as idLote,  ")
            .append("  est.detentoraorcamentaria_id as idUnidadeOrc,   ")
            .append("  est.detentoraadministrativa_id as idUnidadeAdm,   ")
            .append("  bem.identificacao as registro,   ")
            .append("  bem.descricao as descricao,   ")
            .append("  bem.dataaquisicao as dataAquisicao,   ")
            .append("  est.estadobem as estadoConservacao,  ")
            .append("  hoorc.codigo || ' - ' || hoorc.descricao as unidade,   ")
            .append("  hoadm.codigo || ' - ' || hoadm.descricao as unidadeAdm,    ")
            .append("  coalesce(est.valororiginal,0) as valorOriginal,   ")
            .append("  coalesce(est.valoracumuladodadepreciacao, 0) as valorDepreciacao,    ")
            .append("  coalesce(est.valoracumuladodaamortizacao, 0) as valorAmortizacao,    ")
            .append("  coalesce(est.valoracumuladodaexaustao, 0) as valorExaustao,    ")
            .append("  coalesce(est.valoracumuladodeajuste, 0) as valorAjustePerda,   ")
            .append("  coalesce(est.valoracumuladodaamortizacao, 0) +    ")
            .append("  coalesce(est.valoracumuladodadepreciacao, 0) +    ")
            .append("  coalesce(est.valoracumuladodaexaustao, 0) +    ")
            .append("  coalesce(est.valoracumuladodeajuste, 0) as valorAjuste,   ")
            .append("  0 as valorAvaliadao,    ")
            .append("  0 as valorProporcionalArrematado,    ")
            .append("  grupo.codigo || ' - ' || grupo.descricao as grupoPatrionial   ")
            .append(" from solicitabaixapatrimonial sol   ")
            .append("  inner join itemsolicitbaixapatrimonio item on item.solicitacaobaixa_id = sol.id  ")
            .append("  inner join eventobem ev on ev.id = item.id   ")
            .append("  inner join estadobem est on est.id = ev.estadoresultante_id   ")
            .append("  inner join grupobem grupo on grupo.id = est.grupobem_id  ")
            .append("  inner join hierarquiaorganizacional hoorc on est.detentoraorcamentaria_id = hoorc.subordinada_id ")
            .append("       and hoorc.tipohierarquiaorganizacional = :tipoHierarquiaOrc ")
            .append("       and trunc(sol.dataSolicitacao) between trunc(hoorc.iniciovigencia) and coalesce(trunc(hoorc.fimvigencia), trunc(sol.dataSolicitacao)) ")
            .append("  inner join hierarquiaorganizacional hoadm on hoadm.subordinada_id = est.detentoraadministrativa_id ")
            .append("       and hoadm.tipohierarquiaorganizacional = :tipoHierarquiaAdm ")
            .append("       and trunc(sol.dataSolicitacao) between trunc(hoadm.iniciovigencia) and coalesce(trunc(hoadm.fimvigencia), trunc(sol.dataSolicitacao)) ")
            .append("  inner join bem on bem.id = ev.bem_id ")
            .append("  where sol.id = :idSolicitacao ");
        sql.append(unidadeOrganizacional != null ? " and hoadm.subordinada_id = :idUniade " : "");
        sql.append(" order by to_number(bem.identificacao) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idSolicitacao", selecionado.getId());
        q.setParameter("tipoHierarquiaAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("tipoHierarquiaOrc", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        if (unidadeOrganizacional != null) {
            q.setParameter("idUniade", unidadeOrganizacional.getId());
        }
        List<VOItemBaixaPatrimonial> bensPesquisados = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            VOItemBaixaPatrimonial item = VOItemBaixaPatrimonial.criarObjetoVoItemSolicitacaoBaixa(obj);
            item.setBemEfetivacao(bemFacade.recuperarSemDependencias(item.getIdBem()));
            bensPesquisados.add(item);
        }
        return validarAndRetornarBensDisponiveisParaMovimentacao(assistente, bensPesquisados);
    }

    private List<VOItemBaixaPatrimonial> validarAndRetornarBensDisponiveisParaMovimentacao(AssistenteMovimentacaoBens assistente, List<VOItemBaixaPatrimonial> bensPesquisados) {
        List<VOItemBaixaPatrimonial> bensDisponiveis = Lists.newArrayList();
        if (assistente != null && assistente.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
            for (VOItemBaixaPatrimonial item : bensPesquisados) {
                String mensagem = configMovimentacaoBemFacade.validarMovimentoComDataRetroativaBem(item.getIdBem(), assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getDescricao(), assistente.getDataLancamento());
                if (!mensagem.isEmpty()) {
                    assistente.getMensagens().add(mensagem);
                    continue;
                }
                bensDisponiveis.add(item);
            }
            return bensDisponiveis;
        }
        return bensPesquisados;
    }

    public String getSelectItensLeilaoAlienacao() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select   ")
            .append("   itemle.id as iditem,  ")
            .append("   bem.id as idbem,  ")
            .append("   lote.id as idlote,  ")
            .append("   est.detentoraorcamentaria_id   as idunidadeorc,  ")
            .append("   est.detentoraadministrativa_id as idunidadeadm,  ")
            .append("   bem.identificacao as registro,  ")
            .append("   bem.descricao as descricao,  ")
            .append("   bem.dataaquisicao as dataaquisicao,  ")
            .append("   est.estadobem as estadoconservacao,  ")
            .append("   hoorc.codigo || ' - ' || hoorc.descricao as unidade,  ")
            .append("   hoadm.codigo || ' - ' || hoadm.descricao                                                                                                                                                                as unidadeadm,  ")
            .append("   coalesce(est.valororiginal,0)                                                                                                                                                     as valororiginal,  ")
            .append("   coalesce(est.valoracumuladodadepreciacao, 0)                                                                                                                                      as valordepreciacao,  ")
            .append("   coalesce(est.valoracumuladodaamortizacao, 0)                                                                                                                                      as valoramortizacao,  ")
            .append("   coalesce(est.valoracumuladodaexaustao, 0)                                                                                                                                         as valorexaustao,  ")
            .append("   coalesce(est.valoracumuladodeajuste, 0)                                                                                                                                           as valorajusteperda,  ")
            .append("   coalesce(est.valoracumuladodaamortizacao, 0) + coalesce(est.valoracumuladodadepreciacao, 0) + coalesce(est.valoracumuladodaexaustao, 0) + coalesce(est.valoracumuladodeajuste, 0) as valorajuste,  ")
            .append("   coalesce(itemsol.valoravaliado,0)                                                                                                                                                 as valoravaliadao,  ")
            .append("   coalesce(itemle.valorproporcionalarrematado,0)                                                                                                                                      as valorarrematadoproporcional,  ")
            .append("   grupo.codigo || ' - ' || grupo.descricao as grupopatrionial  ")
            .append("from eventobem ev  ")
            .append("   inner join estadobem est on est.id = ev.estadoresultante_id  ")
            .append("   inner join bem on bem.id = ev.bem_id  ")
            .append("   inner join grupobem grupo on grupo.id = est.grupobem_id  ")
            .append("   inner join eventobem evLe on evle.bem_id = ev.bem_id  ")
            .append("   inner join leilaoalienacaolotebem itemLe on itemle.id = evle.id  ")
            .append("   inner join leilaoalienacaolote lote on lote.id = itemle.leilaoalienacaolote_id  ")
            .append("   inner join leilaoalienacao la on la.id = lote.leilaoalienacao_id  ")
            .append("   inner join itemsolicitacaoalienacao itemsol on itemsol.id = itemle.itemsolicitacaoalienacao_id  ")
            .append("   inner join hierarquiaorganizacional hoorc on hoorc.subordinada_id = est.detentoraorcamentaria_id  ")
            .append("      and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(hoorc.iniciovigencia) and coalesce(trunc(hoorc.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))   ")
            .append("      and hoorc.tipohierarquiaorganizacional = :tipoHierarquiaOrc ")
            .append("   inner join hierarquiaorganizacional hoadm on hoadm.subordinada_id = est.detentoraadministrativa_id  ")
            .append("      and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(hoadm.iniciovigencia) and coalesce(trunc(hoadm.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("      and hoadm.tipohierarquiaorganizacional = :tipoHierarquiaAdm ");
        return sql.toString();
    }


    private List<VOItemBaixaPatrimonial> buscarItensLeilaoAlienacaoPorUnidade(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente) {
        String sql = getSelectItensLeilaoAlienacao();
        sql += " where la.id = :idLeilao ";
        sql += " and lote.arrematado = :arrematado ";
        sql += " and ev.dataoperacao = (select max(evmax.dataoperacao) from eventobem evmax where evmax.bem_id = ev.bem_id) ";
        sql += " and exists (select 1 from movimentobloqueiobem mov  " +
            "      where mov.bem_id = bem.id " +
            "           and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where  mov.bem_id = mov2.bem_id)" +
            "           and mov.movimentodois = :bloqueado" +
            "           and rownum = 1) ";
        if (filtro.getHierarquiaAdministrativa().getSubordinada() != null) {
            sql += " and hoadm.subordinada_id = :idUniade ";
        }
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLeilao", ((LeilaoAlienacao) filtro.getSelecionado()).getId());
        q.setParameter("arrematado", Boolean.TRUE);
        q.setParameter("bloqueado", Boolean.FALSE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtro.getDataOperacao()));
        q.setParameter("tipoHierarquiaAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("tipoHierarquiaOrc", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        if (filtro.getHierarquiaAdministrativa().getSubordinada() != null) {
            q.setParameter("idUniade", filtro.getHierarquiaAdministrativa().getSubordinada().getId());
        }
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            VOItemBaixaPatrimonial item = VOItemBaixaPatrimonial.criarObjetoVoItemSolicitacaoBaixa(obj);
            Bem bem = bemFacade.recuperarSemDependencias(item.getIdBem());
            item.setBemEfetivacao(bem);
            toReturn.add(item);
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaAdministrativaItemSolicitacaoBaixa(SolicitacaoBaixaPatrimonial selecionado) {
        String sql = "" +
            " select  " +
            "   distinct ho.* " +
            " from hierarquiaorganizacional ho " +
            "   inner join estadobem est on est.detentoraadministrativa_id = ho.subordinada_id " +
            "   inner join eventobem ev on ev.estadoresultante_id = est.id " +
            "   inner join itemsolicitbaixapatrimonio item on ev.id = item.id " +
            "   inner join solicitabaixapatrimonial sol on sol.id = item.solicitacaobaixa_id " +
            " where sol.dataSolicitacao between ho.iniciovigencia and coalesce(ho.fimvigencia, sol.dataSolicitacao) " +
            "   and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            "   and sol.id = :idSolicitacao " +
            "order by ho.codigo";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("idSolicitacao", selecionado.getId());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaAdministrativaItemLoteLeilao(LeilaoAlienacao leilaoAlienacao) {
        String sql = "" +
            " select  " +
            "   distinct ho.* " +
            " from hierarquiaorganizacional ho " +
            "   inner join estadobem est on est.detentoraadministrativa_id = ho.subordinada_id " +
            "   inner join eventobem ev on ev.estadoresultante_id = est.id " +
            "   inner join bem on bem.id = ev.bem_id " +
            "   inner join leilaoalienacaolotebem item on ev.id = item.id " +
            "   inner join leilaoalienacaolote lote on lote.id = item.leilaoalienacaolote_id " +
            "   inner join leilaoalienacao la on la.id = lote.leilaoalienacao_id " +
            " where trunc(la.dataefetivacao) between ho.iniciovigencia and coalesce(ho.fimvigencia, trunc(la.dataefetivacao)) " +
            "   and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            "   and lote.arrematado = :arrematado " +
            "   and la.id = :idLeilao " +
            " order by ho.codigo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("idLeilao", leilaoAlienacao.getId());
        q.setParameter("arrematado", Boolean.TRUE);
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public void atribuirSituacaotemSolicitacaoBaixa(VOItemBaixaPatrimonial item, SituacaoEventoBem situacaoEventoBem) {
        String sql = " " +
            "  update eventobem ev set ev.situacaoeventobem = :situacao " +
            " where ev.id = :idItem" +
            " and ev.tipoeventobem = :tipoEvento " +
            " and ev.tipooperacao = :tipoOperacao " +
            " and ev.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = ev.bem_id) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", item.getIdItem());
        q.setParameter("situacao", situacaoEventoBem.name());
        q.setParameter("tipoEvento", TipoEventoBem.ITEMSOLICITACAOBAIXAPATRIMONIAL.name());
        q.setParameter("tipoOperacao", TipoOperacao.NENHUMA_OPERACAO.name());
        q.executeUpdate();
    }

    public List<SolicitacaoBaixaPatrimonial> buscarSolicitacaoSemParecerBaixaPorTipoBem(String filtro, TipoBem tipoBem) {
        String sql = "SELECT S.* " +
            "       FROM SOLICITABAIXAPATRIMONIAL S " +
            "  LEFT JOIN PARECERBAIXAPATRIMONIAL  P   ON P.SOLICITACAOBAIXA_ID = S.ID " +
            "  WHERE P.ID IS NULL " +
            "      AND   ((TO_CHAR(S.CODIGO)          LIKE :FILTRO) " +
            "          OR (TO_CHAR(S.DATASOLICITACAO) LIKE :FILTRO)" +
            "          OR (LOWER(S.MOTIVO)            LIKE :FILTRO)) " +
            "          AND S.SITUACAO = :SIT      AND S.TIPOBEM = :TIPOBEM" +
            " ORDER BY S.CODIGO DESC";

        Query q = em.createNativeQuery(sql, SolicitacaoBaixaPatrimonial.class);
        q.setParameter("FILTRO", "%" + filtro.toLowerCase() + "%");
        q.setParameter("SIT", SituacaoBaixaPatrimonial.AGUARDANDO_PARECER.name());
        q.setParameter("TIPOBEM", tipoBem.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public Boolean podeEditarSolicitacao(SolicitacaoBaixaPatrimonial solicitacao) {
        String sql = "SELECT S.* " +
            "       FROM SOLICITABAIXAPATRIMONIAL S " +
            "  LEFT JOIN PARECERBAIXAPATRIMONIAL P ON P.SOLICITACAOBAIXA_ID = S.ID " +
            "      WHERE P.ID IS NULL  " +
            "        AND S.ID = :SOL";

        Query q = em.createNativeQuery(sql, SolicitacaoBaixaPatrimonial.class);
        q.setParameter("SOL", solicitacao.getId());
        return !q.getResultList().isEmpty();
    }

    public Boolean hasSolicitacaoComParecer(Number idItemSolicitacao) {
        String sql = "  select pa.* from parecerbaixapatrimonial pa " +
            "where pa.solicitacaobaixa_id = (select sol.id from solicitabaixapatrimonial sol " +
            "                                inner join itemsolicitbaixapatrimonio item on item.solicitacaobaixa_id = sol.id " +
            "                                where item.id = :idItemSolicitacao" +
            "                               ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSolicitacao", idItemSolicitacao);
        return !q.getResultList().isEmpty();
    }

    public Boolean hasSolicitacaoEfetivada(Number idItemSolicitacao) {
        String sql = "  select ef.* from efetivacaobaixapatrimonial ef " +
            "            where ef.parecerbaixapatrimonial_id = ( " +
            "                                      select pa.id from solicitabaixapatrimonial sol " +
            "                                      inner join parecerbaixapatrimonial pa on pa.solicitacaobaixa_id = sol.id " +
            "                                      inner join itemsolicitbaixapatrimonio item on item.solicitacaobaixa_id = sol.id " +
            "                                      where item.id = :idItemSolicitacao " +
            "                                    ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSolicitacao", idItemSolicitacao);
        return !q.getResultList().isEmpty();
    }


    public List<Number> buscarIdsBemPorSolicitacaoBaixa(SolicitacaoBaixaPatrimonial entity) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from itemsolicitbaixapatrimonio item " +
            "   inner join eventobem ev on ev.id = item.id " +
            " where item.solicitacaobaixa_id = :idSolicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    private List<Long> buscarIdsEstadoResultante(SolicitacaoBaixaPatrimonial entity) {
        String sql = "" +
            " select " +
            "  ev.estadoresultante_id " +
            "from itemsolicitbaixapatrimonio item " +
            "inner join eventobem ev on ev.id = item.id " +
            "where item.solicitacaobaixa_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", entity.getId());
        return ((List<Long>) q.getResultList());
    }

    public List<Long> buscarIdsItemSolicitacaoBaixa(SolicitacaoBaixaPatrimonial solicitacao) {
        String sql = "" +
            "  select " +
            "   it.id " +
            "  from itemsolicitbaixapatrimonio it " +
            "   inner join eventobem ev on ev.id = it.id " +
            "  where it.solicitacaoBaixa_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacao.getId());
        List<Long> toReturn = Lists.newArrayList();
        try {
            List<BigDecimal> ids = q.getResultList();
            if (!ids.isEmpty()) {
                for (BigDecimal id : ids) {
                    toReturn.add(id.longValue());
                }
            }
        } catch (Exception ex) {
            logger.error("buscarIdsItemSolicitacaoBaixa {}", ex);
        }
        return toReturn;
    }

    public List<Bem> pesquisarBensVinculadoAoItemSolicitacaoPorSolicitacaoBaixa(SolicitacaoBaixaPatrimonial selecionado) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct  ")
            .append("   bem.id,  ")
            .append("   bem.identificacao,  ")
            .append("   bem.descricao,  ")
            .append("   est.id as idestadobem,  ")
            .append("   bem.dataaquisicao,  ")
            .append("   hoorc.codigo || ' - ' || hoorc.descricao as unidade  ")
            .append(" from itemsolicitbaixapatrimonio item  ")
            .append("   inner join eventobem ev on ev.id = item.id  ")
            .append("   inner join estadobem est on est.id = ev.estadoresultante_id  ")
            .append("   inner join bem on bem.id = ev.bem_id  ")
            .append("   inner join hierarquiaorganizacional hoorc on hoorc.subordinada_id = est.detentoraorcamentaria_id  ")
            .append("         and to_date(:dataOperacao, 'dd/MM/yyyy') between hoorc.iniciovigencia and coalesce(hoorc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))  ")
            .append("         and hoorc.tipohierarquiaorganizacional = :tipoHierarquiaOrc ")
            .append("  inner join hierarquiaorganizacional hoadm on hoadm.subordinada_id = est.detentoraadministrativa_id    ")
            .append("         and hoadm.tipohierarquiaorganizacional = :tipoHierarquiaAdm ")
            .append("         and to_date(:dataOperacao, 'dd/MM/yyyy') between hoadm.iniciovigencia and coalesce(hoadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))    ")
            .append(" where item.solicitacaobaixa_id = :idSolicitacao  ")
            .append(" order by to_number(bem.identificacao) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idSolicitacao", selecionado.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(selecionado.getDataSolicitacao()));
        q.setParameter("tipoHierarquiaAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("tipoHierarquiaOrc", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        return preencherListaDeBensApartirArrayObject(q.getResultList());
    }

    public List<Bem> preencherListaDeBensApartirArrayObject(List<Object[]> objetosDaConsulta) {
        List<Bem> retornaBens = new ArrayList<>();
        for (Object[] bem : objetosDaConsulta) {
            BigDecimal id = (BigDecimal) bem[0];
            Bem b = bemFacade.recuperarSemDependencias(id.longValue());
            b.setIdUltimoEstado(((BigDecimal) bem[3]).longValue());
            b.setOrcamentaria((String) bem[5]);
            retornaBens.add(b);
        }
        for (Bem bem : retornaBens) {
            try {
                Bem.preencherDadosTrasientsDoBem(bem, bemFacade.recuperarEstadoBemPorId(bem.getIdUltimoEstado()));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return retornaBens;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<Bem> pesquisarBens(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente) {
        assistente.setDescricaoProcesso("Pesquisando bens da unidade: " + filtro.getHierarquiaAdministrativa() + "...");

        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ")
            .append("   bem.id, ")
            .append("   bem.identificacao, ")
            .append("   bem.descricao, ")
            .append("   est.id as estado, ")
            .append("   bem.dataaquisicao, ")
            .append("   hoorc.codigo || ' - ' || hoorc.descricao as unidade ")
            .append(" from bem ")
            .append("  inner join eventobem ev on ev.bem_id = bem.id ")
            .append("  inner join estadobem est on est.id = ev.estadoresultante_id ")
            .append("  inner join grupobem gb on gb.id = est.grupobem_id ")
            .append("  inner join movimentobloqueiobem mov on mov.bem_id = bem.id ")
            .append("  inner join vwhierarquiaorcamentaria  hoorc on est.detentoraorcamentaria_id = hoorc.subordinada_id ")
            .append("        and to_date(:dataOperacao, 'dd/MM/yyyy') between hoorc.iniciovigencia and coalesce(hoorc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("  inner join vwhierarquiaadministrativa hoadm on est.detentoraadministrativa_id = hoadm.subordinada_id ")
            .append("        and to_date(:dataOperacao, 'dd/MM/yyyy') between hoadm.iniciovigencia and coalesce(hoadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("  where gb.tipobem = :tipoBem ")
            .append("        and est.estadoBem <> :baixado ")
            .append("        and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where bem.id = mov2.bem_id) ")
            .append("        and ev.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = ev.bem_id) ");
        sql.append(filtro.getHierarquiaAdministrativa() != null ? " and hoadm.subordinada_id = :idUnidadeAdm " : "");
        sql.append(filtro.getHierarquiaOrcamentaria() != null ? " and est.detentoraOrcamentaria_id = :idOrcamentaria " : "");
        sql.append(filtro.getGrupoBem() != null ? " and gb.id = :idGrupo " : "");
        sql.append(filtro.getBem() != null ? " and bem.id = :idBem " : "");
        sql.append(!Util.isListNullOrEmpty(filtro.getIdsBem()) ? " and bem.id in :idsBem " : "");
        sql.append(FiltroPesquisaBem.getCondicaoBloqueioPesquisaBem(assistente.getConfigMovimentacaoBem()));
        sql.append(" order by to_number(bem.identificacao) ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("tipoBem", filtro.getTipoBem().name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(((SolicitacaoBaixaPatrimonial) filtro.getSelecionado()).getDataSolicitacao()));
        q.setParameter("baixado", EstadoConservacaoBem.BAIXADO.name());
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, assistente.getConfigMovimentacaoBem());

        if (filtro.getHierarquiaAdministrativa() != null) {
            q.setParameter("idUnidadeAdm", filtro.getHierarquiaAdministrativa().getSubordinada().getId());
        }
        if (filtro.getHierarquiaOrcamentaria() != null) {
            q.setParameter("idOrcamentaria", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        }
        if (filtro.getGrupoBem() != null) {
            q.setParameter("idGrupo", filtro.getGrupoBem().getId());
        }
        if (filtro.getBem() != null) {
            q.setParameter("idBem", filtro.getBem().getId());
        }
        if (!Util.isListNullOrEmpty(filtro.getIdsBem())) {
            q.setParameter("idsBem", filtro.getIdsBem());
        }
        List<Bem> bensPesquisados = preencherListaDeBensApartirArrayObject(q.getResultList());
        List<Bem> bensDisponiveis = configMovimentacaoBemFacade.validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);
        if (assistente.isOperacaoEditar()) {
            assistente.setBensSalvos(pesquisarBensVinculadoAoItemSolicitacaoPorSolicitacaoBaixa((SolicitacaoBaixaPatrimonial) assistente.getSelecionado()));
        } else {
            assistente.setBensSalvos(bensDisponiveis);
        }
        configMovimentacaoBemFacade.verificarBensMoveisEmBloqueioParaMovimentacao(filtro, assistente);
        return bensDisponiveis;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<VOItemBaixaPatrimonial> buscarBensDaUnidadeArrematadosLeilaoAlienacao(LeilaoAlienacao leilaoAlienacao, AssistenteMovimentacaoBens assistente) {

        List<HierarquiaOrganizacional> hierarquias = buscarHierarquiaAdministrativaItemLoteLeilao(leilaoAlienacao);
        List<VOItemBaixaPatrimonial> loteComBensAgrupados = Lists.newArrayList();

        for (HierarquiaOrganizacional hierarquia : hierarquias) {
            VOItemBaixaPatrimonial item = new VOItemBaixaPatrimonial();
            item.setHierarquiaAdministrativa(hierarquia);

            FiltroPesquisaBem filtroPesquisaBem = new FiltroPesquisaBem(TipoBem.MOVEIS, leilaoAlienacao);
            filtroPesquisaBem.setHierarquiaAdministrativa(hierarquia);
            filtroPesquisaBem.setDataOperacao(leilaoAlienacao.getDataEfetivacao());

            List<VOItemBaixaPatrimonial> bensPesquisados = buscarItensLeilaoAlienacaoPorUnidade(filtroPesquisaBem, assistente);

            List<VOItemBaixaPatrimonial> bensDisponiveis = validarAndRetornarBensDisponiveisParaMovimentacao(assistente, bensPesquisados);

            validarAndRetornarBensDisponiveisParaMovimentacaoPorAlienacao(leilaoAlienacao, assistente, hierarquia, bensDisponiveis);
            item.setBensAgrupados(bensDisponiveis);
            loteComBensAgrupados.add(item);
        }
        return loteComBensAgrupados;
    }

    private void validarAndRetornarBensDisponiveisParaMovimentacaoPorAlienacao(LeilaoAlienacao leilaoAlienacao, AssistenteMovimentacaoBens assistente, HierarquiaOrganizacional hierarquia, List<VOItemBaixaPatrimonial> bensDisponiveis) {
        if (assistente.isOperacaoNovo()) {
            FiltroPesquisaBem filtro = new FiltroPesquisaBem(TipoBem.MOVEIS, leilaoAlienacao);
            filtro.setHierarquiaAdministrativa(hierarquia);
            filtro.setAlienado(Boolean.TRUE);
            for (VOItemBaixaPatrimonial voItem : bensDisponiveis) {
                assistente.getBensSalvos().add(voItem.getBemEfetivacao());
            }

            assistente.setDescricaoProcesso("Verificando bens em bloqueio para unidade: " + hierarquia + "...");
            List<BensMoveisBloqueio> bensBloqueados = configMovimentacaoBemFacade.buscarBensEmBloqueioMovimentacao(filtro);
            for (BensMoveisBloqueio bensBloqueado : bensBloqueados) {
                if (assistente.getBensSalvos() != null && !assistente.getBensSalvos().isEmpty()) {
                    Bem bem = bemFacade.recuperarSemDependencias(bensBloqueado.getIdBem());
                    if (assistente.getBensSalvos().contains(bem)) {
                        assistente.getMensagens().add(bensBloqueado.toString());
                    }
                } else {
                    assistente.getMensagens().add(bensBloqueado.toString());
                }
            }
        }
    }

    public List<VOItemBaixaPatrimonial> buscarBensUnidadePorSolicitacaoBaixa(SolicitacaoBaixaPatrimonial selecionado) {
        List<HierarquiaOrganizacional> hierarquias = buscarHierarquiaAdministrativaItemSolicitacaoBaixa(selecionado);
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        for (HierarquiaOrganizacional hierarquia : hierarquias) {
            VOItemBaixaPatrimonial item = new VOItemBaixaPatrimonial();
            item.setHierarquiaAdministrativa(hierarquia);
            item.setBensAgrupados(buscarItensSolicitacaoBaixa(selecionado, hierarquia.getSubordinada(), null));
            toReturn.add(item);
        }
        return toReturn;
    }

    private void atribuirHierarquiaOrcamentariaDistinta(SolicitacaoBaixaPatrimonial entity) {
        if (!entity.isTipoBaixaAlienacao() && entity.getHierarquiaAdministrativa() != null) {
            HierarquiaOrganizacional hoOrcamentaria = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(
                entity.getHierarquiaAdministrativa().getSubordinada(),
                TipoHierarquiaOrganizacional.ORCAMENTARIA,
                entity.getDataSolicitacao());

            if (hoOrcamentaria != null) {
                entity.setUnidadeOrcamentaria(hoOrcamentaria.getSubordinada());
            }
        }
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public LeilaoAlienacaoFacade getLeilaoAlienacaoFacade() {
        return leilaoAlienacaoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public EfetivacaoSolicitacaoIncorporacaoMovelFacade getEfetivacaoSolicitacaoIncorporacaoMovelFacade() {
        return efetivacaoSolicitacaoIncorporacaoMovelFacade;
    }

    public EfetivacaoAquisicaoFacade getEfetivacaoAquisicaoFacade() {
        return efetivacaoAquisicaoFacade;
    }
}
