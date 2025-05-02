package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoTransferencia;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class AprovacaoTransferenciaBemFacade extends AbstractFacade<AprovacaoTransferenciaBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private LoteTransferenciaFacade loteTransferenciaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private DocumentosNecessariosAnexoFacade documentosNecessariosAnexoFacade;
    @EJB
    private TipoDocumentoAnexoFacade tipoDocumentoAnexoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private FaseFacade faseFacade;


    public AprovacaoTransferenciaBemFacade() {
        super(AprovacaoTransferenciaBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public AprovacaoTransferenciaBem recuperar(Object id) {
        AprovacaoTransferenciaBem entity = super.recuperar(id);
        Hibernate.initialize(entity.getAnexos());
        if (entity.getItens() != null) {
            for (AprovacaoTransfBemAnexo arquivo : entity.getAnexos()) {
                Hibernate.initialize(arquivo.getArquivo().getPartes());
            }
        }
        return entity;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public void remover(AprovacaoTransferenciaBem entity, ConfigMovimentacaoBem configuracao) {
        List<Number> idsBem = buscarIdsBemAprovacao(entity);
        desbloquearBem(idsBem, configuracao);
        atualizarSolicitacaoAoRemoverAprovacao(entity, idsBem);
        super.remover(entity);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AprovacaoTransferenciaBem> salvarAprovacaoAsync(List<VOItemSolicitacaoTransferencia> bens, AprovacaoTransferenciaBem entity, AssistenteMovimentacaoBens assistente) {
        entity = salvarAprovacao(bens, entity, assistente);
        return new AsyncResult<>(entity);
    }

    public AprovacaoTransferenciaBem salvarAprovacao(List<VOItemSolicitacaoTransferencia> bens, AprovacaoTransferenciaBem entity, AssistenteMovimentacaoBens assistente) {
        SistemaFacade.threadLocalUsuario.set(assistente.getUsuarioSistema().getLogin());
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(AprovacaoTransferenciaBem.class, "codigo"));
        }
        entity = em.merge(entity);

        assistente.setDescricaoProcesso("Criando Eventos de Aprovação de Transferência...");
        assistente.setTotal(bens.size());
        criarItemAprovacaoTransferencia(bens, entity, assistente);

        atualizarSituacaoSolicitacao(entity);
        return entity;
    }

    private void atualizarSolicitacaoAoRemoverAprovacao(AprovacaoTransferenciaBem entity, List<Number> idsBem) {
        LoteTransferenciaBem solicitacao = entity.getSolicitacaoTransferencia();
        solicitacao.setSituacaoTransferenciaBem(SituacaoDaSolicitacao.AGUARDANDO_APROVACAO);
        solicitacao.setMotivoRecusa(null);
        em.merge(solicitacao);

        for (Number idBem : idsBem) {
            updateSituacaoEvento(idBem, SituacaoEventoBem.AGUARDANDO_APROVACAO);
        }
    }

    private void atualizarSituacaoSolicitacao(AprovacaoTransferenciaBem entity) {
        LoteTransferenciaBem solicitacao = entity.getSolicitacaoTransferencia();
        solicitacao.setSituacaoTransferenciaBem(entity.isAceita() ? SituacaoDaSolicitacao.AGUARDANDO_EFETIVACAO : SituacaoDaSolicitacao.RECUSADA);
        solicitacao.setMotivoRecusa(entity.getMotivo());
        em.merge(solicitacao);
    }

    private void desbloquearBem(List<Number> idsBem, ConfigMovimentacaoBem configuracao) {
        configMovimentacaoBemFacade.desbloquearBensRejeicaoDuranteProcesso(configuracao, idsBem);
    }

    private void criarItemAprovacaoTransferencia(List<VOItemSolicitacaoTransferencia> bens, AprovacaoTransferenciaBem entity, AssistenteMovimentacaoBens assistente) {
        List<Number> bensBloqueio = Lists.newArrayList();
        for (VOItemSolicitacaoTransferencia item : bens) {
            TransferenciaBem itemSolicitacao = em.find(TransferenciaBem.class, item.getIdItem());
            Bem bem = itemSolicitacao.getBem();
            SituacaoEventoBem situacaoEvento = entity.isAceita() ? SituacaoEventoBem.AGUARDANDO_EFETIVACAO : SituacaoEventoBem.RECUSADO;

            EstadoBem estadoInicial = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial));
            Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());

            ItemAprovacaoTransferenciaBem novoItem = new ItemAprovacaoTransferenciaBem(bem, entity, estadoInicial, estadoResultante);
            novoItem.setSituacaoEventoBem(situacaoEvento);
            novoItem.setDataLancamento(dataLancamento);
            if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()){
                novoItem.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 1, TipoPrazo.SEGUNDOS));
            }
            itemSolicitacao.setSituacaoEventoBem(situacaoEvento);
            em.merge(itemSolicitacao);

            bensBloqueio.add(bem.getId());
            assistente.conta();
            em.merge(novoItem);
        }
        if (entity.isAceita()) {
            configMovimentacaoBemFacade.bloquearBensPorSituacao(assistente.getConfigMovimentacaoBem(), bensBloqueio, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        } else {
            configMovimentacaoBemFacade.desbloquearBens(assistente.getConfigMovimentacaoBem(), bensBloqueio);
        }
    }

    public void updateSituacaoEvento(Number idBem, SituacaoEventoBem situacao) {
        String sql = " update eventobem set situacaoEventoBem = :situacao where bem_id = :idBem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idBem", idBem);
        q.setParameter("situacao", situacao.name());
        q.executeUpdate();
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<VOItemSolicitacaoTransferencia>> pesquisarBem(AssistenteMovimentacaoBens assistente, LoteTransferenciaBem solicitacao) {
        assistente.setDescricaoProcesso("Pesquisando bens da solicitação de transferência " + solicitacao);
        String sql = " select " +
            "       item.id  as idItem," +
            "       bem.id  as idBem," +
            "       bem.identificacao                            as registro, " +
            "       bem.descricao                                          as descricao, " +
            "       estresultante.estadobem                                as estadobem, " +
            "       vworcorigem.codigo || ' - ' || vworcorigem.descricao   as unidadeorcorigem, " +
            "       vworcorigem.codigo || ' - ' || vworcorigem.descricao as unidadeorcdestino, " +
            "       coalesce((select estOriginal.valororiginal " +
            "                 from estadobem estOriginal " +
            "                 inner join eventobem evOriginal on evOriginal.estadoresultante_id = estOriginal.id " +
            "                 where evOriginal.dataoperacao =  (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evOriginal.bem_id) " +
            "                   and evOriginal.bem_id = bem.id), 0) as valorOriginal, " +
            "       coalesce((select estajuste.valoracumuladodaamortizacao + estajuste.valoracumuladodadepreciacao + " +
            "                        estajuste.valoracumuladodaexaustao + estajuste.valoracumuladodeajuste " +
            "                 from estadobem estajuste " +
            "                          inner join eventobem evAjuste on evAjuste.estadoresultante_id = estajuste.id " +
            "                 where evAjuste.dataoperacao = " +
            "                       (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evAjuste.bem_id) " +
            "                   and evAjuste.bem_id = bem.id), 0)  as valorAjuste " +
            " from transferenciabem item " +
            "  inner join lotetransferenciabem sol on sol.id = item.LOTETRANSFERENCIABEM_ID " +
            "  inner join eventobem ev on ev.id = item.id " +
            "  inner join estadobem estInicial on estInicial.id = ev.estadoinicial_id " +
            "  inner join estadobem estResultante on estResultante.id = ev.estadoresultante_id " +
            "  inner join bem on bem.id = ev.bem_id " +
            "  inner join vwhierarquiaorcamentaria vworcOrigem on vworcOrigem.subordinada_id = estInicial.detentoraorcamentaria_id " +
            "        and trunc(sol.DATAHORACRIACAO) between vworcOrigem.INICIOVIGENCIA and coalesce(vworcOrigem.FIMVIGENCIA, trunc(sol.DATAHORACRIACAO)) " +
            " where sol.situacaotransferenciabem = :situacaoTransferencia " +
            "  and sol.tipoBem = :tipobem " +
            "  and sol.id = :idSolicitacao " +
            " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacao.getId());
        q.setParameter("tipobem", TipoBem.MOVEIS.name());
        q.setParameter("situacaoTransferencia", SituacaoDaSolicitacao.AGUARDANDO_APROVACAO.name());
        List<VOItemSolicitacaoTransferencia> itens = preencherArrayObjetoVOItemSolicitacaoTransferencia(q);
        List<VOItemSolicitacaoTransferencia> bens = validarAndRetornarBensDisponiveisParaMovimentacao(assistente, itens);
        return new AsyncResult<>(bens);
    }

    private List<VOItemSolicitacaoTransferencia> validarAndRetornarBensDisponiveisParaMovimentacao(AssistenteMovimentacaoBens assistente, List<VOItemSolicitacaoTransferencia> itens) {
        List<VOItemSolicitacaoTransferencia> bens = Lists.newArrayList();
        assistente.zerarContadoresProcesso();
        assistente.setTotal(itens.size());
        if (assistente.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
            for (VOItemSolicitacaoTransferencia item : itens) {
                String mensagem = configMovimentacaoBemFacade.validarMovimentoComDataRetroativaBem(item.getIdBem(), assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getDescricao(), assistente.getDataLancamento());
                if (!mensagem.isEmpty()) {
                    assistente.getMensagens().add(mensagem);
                    assistente.conta();
                    continue;
                }
                bens.add(item);
                assistente.conta();
            }
        }
        return bens;
    }

    public AprovacaoTransferenciaBem recuperarAprovacaoDaSolicitacao(LoteTransferenciaBem solicitacao) {
        String sql = " select  * from aprovacaotransferenciabem where solicitacaoTransferencia_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql, AprovacaoTransferenciaBem.class);
        q.setParameter("idSolicitacao", solicitacao.getId());
        return (AprovacaoTransferenciaBem) q.getSingleResult();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<VOItemSolicitacaoTransferencia> buscarBensAprovacao(AprovacaoTransferenciaBem aprovacao) {
        String sql = " select " +
            "       item.id  as idItem," +
            "       bem.id  as idBem," +
            "       bem.identificacao                            as registro, " +
            "       bem.descricao                                          as descricao, " +
            "       estresultante.estadobem                                as estadobem, " +
            "       vworcorigem.codigo || ' - ' || vworcorigem.descricao   as unidadeorcorigem, " +
            "       vworcdestino.codigo || ' - ' || vworcdestino.descricao as unidadeorcdestino, " +
            "       coalesce(estResultante.valororiginal, 0)               as valorOriginal, " +
            "       coalesce(estResultante.valoracumuladodaamortizacao " +
            "                    + estResultante.valoracumuladodadepreciacao " +
            "                    + estResultante.valoracumuladodaexaustao " +
            "                    + estResultante.valoracumuladodeajuste, 0)   as valorAjuste" +
            " from itemaprovacaotransfbem item " +
            "  inner join aprovacaotransferenciabem ap on ap.id = item.aprovacao_id " +
            "         inner join eventobem ev on ev.id = item.id " +
            "         inner join estadobem estinicial on estinicial.id = ev.estadoinicial_id " +
            "         inner join estadobem estresultante on estresultante.id = ev.estadoresultante_id " +
            "         inner join bem on bem.id = ev.bem_id " +
            "         inner join vwhierarquiaorcamentaria vworcorigem on vworcorigem.subordinada_id = estinicial.detentoraorcamentaria_id " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between vworcorigem.iniciovigencia and coalesce(vworcorigem.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "         inner join vwhierarquiaorcamentaria vworcdestino on vworcdestino.subordinada_id = estresultante.detentoraorcamentaria_id " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between vworcdestino.iniciovigencia and coalesce(vworcdestino.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " where item.aprovacao_id = :idAprovacao " +
            "order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAprovacao", aprovacao.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(aprovacao.getDataAprovacao()));
        return preencherArrayObjetoVOItemSolicitacaoTransferencia(q);
    }

    private List<VOItemSolicitacaoTransferencia> preencherArrayObjetoVOItemSolicitacaoTransferencia(Query q) {
        List<VOItemSolicitacaoTransferencia> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            VOItemSolicitacaoTransferencia item = new VOItemSolicitacaoTransferencia();
            item.setIdItem(((BigDecimal) obj[0]).longValue());
            item.setIdBem(((BigDecimal) obj[1]).longValue());
            item.setRegistroPatrimonial((String) obj[2]);
            item.setDescricao((String) obj[3]);
            item.setEstadoConservacaoBem(EstadoConservacaoBem.valueOf((String) obj[4]));
            item.setUnidadeOrcOrigem((String) obj[5]);
            item.setUnidadeOrcDestino((String) obj[6]);
            item.setValorOriginal((BigDecimal) obj[7]);
            item.setValorAjuste((BigDecimal) obj[8]);
            item.setEstadoBem(bemFacade.recuperarUltimoEstadoDoBem(((BigDecimal) obj[1]).longValue()));
            toReturn.add(item);
        }
        return toReturn;
    }

    public List<Number> buscarIdsBemAprovacao(AprovacaoTransferenciaBem lote) {
        String sql = "select ev.bem_id " +
            " from itemaprovacaotransfbem item " +
            " inner join eventobem ev on ev.id = item.id " +
            " where item.aprovacao_id = :idAprovacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAprovacao", lote.getId());
        return ((List<Number>) q.getResultList());
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public LoteTransferenciaFacade getLoteTransferenciaFacade() {
        return loteTransferenciaFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public DocumentosNecessariosAnexoFacade getDocumentosNecessariosAnexoFacade() {
        return documentosNecessariosAnexoFacade;
    }

    public TipoDocumentoAnexoFacade getTipoDocumentoAnexoFacade() {
        return tipoDocumentoAnexoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public void finalizarAprovcacao(LoteTransferenciaBem solicitacao) {
        AprovacaoTransferenciaBem aprovacao = recuperarAprovacaoDaSolicitacao(solicitacao);
        List<Number> bens = buscarIdsBemAprovacao(aprovacao);
        for (Number idBem : bens) {
            updateSituacaoEvento(idBem, SituacaoEventoBem.FINALIZADO);
        }
        aprovacao.setSituacao(SituacaoDaSolicitacao.FINALIZADA);
        em.merge(aprovacao);
    }

    public boolean hasAprovacaoPorSolicitacao(LoteTransferenciaBem loteTransferenciaBem) {
        return loteTransferenciaFacade.hasSolicitacao(loteTransferenciaBem.getId());
    }
}
