package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class SolicitacaoAquisicaoFacade extends AbstractFacade<SolicitacaoAquisicao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private ConfigContaDespesaTipoDocumentoFacade configContaDespesaTipoDocumentoFacade;

    public SolicitacaoAquisicaoFacade() {
        super(SolicitacaoAquisicao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void remover(SolicitacaoAquisicao entity) {
        requisicaoDeCompraFacade.movimentarSituacaoRequisicaoCompra(entity.getRequisicaoDeCompra(), SituacaoRequisicaoCompra.EM_ELABORACAO);
        super.remover(entity);
    }

    @Override
    public SolicitacaoAquisicao recuperar(Object id) {
        SolicitacaoAquisicao entity = super.recuperar(id);
        Hibernate.initialize(entity.getItensSolicitacao());
        Hibernate.initialize(entity.getDocumentosFiscais());
        for (DoctoFiscalSolicitacaoAquisicao docto : entity.getDocumentosFiscais()) {
            Hibernate.initialize(docto.getItens());
            Hibernate.initialize(docto.getDocumentoFiscal().getListaItemDoctoFiscalLiquidacao());
        }
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return entity;
    }

    public DoctoFiscalSolicitacaoAquisicao recuperarItensDocumentoFiscal(DoctoFiscalSolicitacaoAquisicao documento) {
        DoctoFiscalSolicitacaoAquisicao doctoFiscalSol = em.find(DoctoFiscalSolicitacaoAquisicao.class, documento.getId());
        if (doctoFiscalSol != null) {
            Hibernate.initialize(doctoFiscalSol.getItens());
        }
        return doctoFiscalSol;
    }

    public SolicitacaoAquisicao recuperarComDependenciasDocumentos(Object id) {
        SolicitacaoAquisicao entity = super.recuperar(id);
        Hibernate.initialize(entity.getDocumentosFiscais());
        for (DoctoFiscalSolicitacaoAquisicao doc : entity.getDocumentosFiscais()) {
            Hibernate.initialize(doc.getItens());
            Hibernate.initialize(doc.getDocumentoFiscal().getListaItemDoctoFiscalLiquidacao());
        }
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return entity;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public Future<SolicitacaoAquisicao> salvarSolicitacao(SolicitacaoAquisicao entity, AssistenteMovimentacaoBens assistente) {
        try {
            removerItensSolicitacao(entity);
            criarItensSolicitacaoAquisicao(entity, assistente);
            assistente.setDescricaoProcesso("Salvando Processo...");
            assistente.zerarContadoresProcesso();

            entity = em.merge(entity);
            requisicaoDeCompraFacade.movimentarSituacaoRequisicaoCompra(entity.getRequisicaoDeCompra(), SituacaoRequisicaoCompra.AGUARDANDO_EFETIVACAO);
            assistente.setDescricaoProcesso("Redirecionando...");
            assistente.setSelecionado(entity);
        } catch (Exception ex) {
            assistente.getMensagensValidacaoFacesUtil().add(new FacesMessage("Ocorreu um erro ao salvar a solicitação. Detalhes: " + ex.getMessage()));
            logger.error("Erro ao salvar solicitação de aquisição de bens móveis. {}", ex);
        }
        return new AsyncResult<>(entity);
    }

    private void removerItensSolicitacao(SolicitacaoAquisicao entity) {
        entity.setItensSolicitacao(new ArrayList<ItemSolicitacaoAquisicao>());
        for (ItemSolicitacaoAquisicao item : entity.getItensSolicitacao()) {
            Query qEstadoBem = em.createNativeQuery(" delete from estadobem where id = :idEstadoBem");
            qEstadoBem.setParameter("idEstadoBem", item.getEstadoResultante().getId());
            qEstadoBem.executeUpdate();
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<SolicitacaoAquisicao> concluirSolicitacao(SolicitacaoAquisicao entity, AssistenteMovimentacaoBens assitente, List<ItemSolicitacaoAquisicao> itensSolicitacao) {
        try {
            assitente.zerarContadoresProcesso();
            assitente.setTotal(itensSolicitacao.size());
            assitente.setDescricaoProcesso("Concluíndo Solicitação de Aquisição de Bens " + entity.getTipoBem().getDescricao() + "...");
            concluirItemSolicitacao(itensSolicitacao, assitente);

            assitente.setDescricaoProcesso("Salvando Processo...");
            assitente.zerarContadoresProcesso();

            if (entity.getNumero() == null) {
                entity.setNumero(singletonGeradorCodigo.getProximoCodigo(SolicitacaoAquisicao.class, "numero"));
            }
            entity.setSituacao(SituacaoAquisicao.AGUARDANDO_EFETIVACAO);
            entity = em.merge(entity);

            assitente.setSelecionado(entity);
            assitente.setDescricaoProcesso("");
        } catch (Exception ex) {
            logger.error("Erro ao concluir a  solicitação de aquisição {}", ex);
        }
        return new AsyncResult<>(entity);
    }

    private void criarItensSolicitacaoAquisicao(SolicitacaoAquisicao entity, AssistenteMovimentacaoBens assistente) {
        UnidadeOrganizacional unidadeAdministrativa = requisicaoDeCompraFacade.getUnidadeAdministrativa(entity.getRequisicaoDeCompra(), assistente.getDataLancamento());
        List<HierarquiaOrganizacional> hierarquiasOrcReq = hierarquiaOrganizacionalFacade.buscarUnidadesOrcamentariasRequisicaoCompra(entity.getRequisicaoDeCompra());
        if (Util.isListNullOrEmpty(hierarquiasOrcReq)){
            throw new ExcecaoNegocioGenerica("Unidade Orçamentária não encontrada para a requisição de compra de " + entity.getRequisicaoDeCompra().getTipoRequisicao().getDescricao() + ".");
        }
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Itens da Solicitação de Aquisição...");
        for (DoctoFiscalSolicitacaoAquisicao doc : entity.getDocumentosFiscais()) {
            atribuirTotalAssistenteBarraProgresso(assistente, doc);

            for (ItemDoctoItemAquisicao itemDoctoSol : doc.getItens()) {
                ItemRequisicaoDeCompra itemRequisicaoDeCompra = itemDoctoSol.getItemRequisicaoDeCompra();
                int quantidadePorItem = calcularQuantidadePorItem(itemDoctoSol.getItemDoctoFiscalLiquidacao());

                for (int x = 0; x < quantidadePorItem; x++) {
                    ItemSolicitacaoAquisicao itemSolicitacao = criarItemSolicitacaoAquisicao(
                        entity, assistente, unidadeAdministrativa,
                        hierarquiasOrcReq.get(0).getSubordinada(), itemDoctoSol, itemRequisicaoDeCompra);
                    entity.getItensSolicitacao().add(itemSolicitacao);
                    assistente.conta();
                }
            }
        }
        atualizarValorItemQuandoHouverDiferencaDocumentoFiscal(entity);
    }

    private void atualizarValorItemQuandoHouverDiferencaDocumentoFiscal(SolicitacaoAquisicao entity) {
        BigDecimal diferenca = entity.getValorTotalItensSolicitacao().subtract(entity.getValorTotalDocumento());
        if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
            ItemSolicitacaoAquisicao ultimoItem = entity.getItensSolicitacao().get(entity.getItensSolicitacao().size() - 1);
            BigDecimal valorLancamentoAtualizado = ultimoItem.getValorDoLancamento().subtract(diferenca);
            ultimoItem.setValorDoLancamento(valorLancamentoAtualizado);
            ultimoItem.getEstadoResultante().setValorOriginal(valorLancamentoAtualizado);
        }
    }

    private void atribuirTotalAssistenteBarraProgresso(AssistenteMovimentacaoBens assistente, DoctoFiscalSolicitacaoAquisicao doc) {
        for (ItemDoctoItemAquisicao itemDoctoSol : doc.getItens()) {
            int quantidadePorItem = calcularQuantidadePorItem(itemDoctoSol.getItemDoctoFiscalLiquidacao());
            assistente.setTotal(assistente.getTotal() + quantidadePorItem);
        }
    }

    private ItemSolicitacaoAquisicao criarItemSolicitacaoAquisicao(SolicitacaoAquisicao entity, AssistenteMovimentacaoBens assistente, UnidadeOrganizacional unidadeAdministrativa, UnidadeOrganizacional unidadeOrcamentaria, ItemDoctoItemAquisicao itemDoctoItemAquisicao, ItemRequisicaoDeCompra itemRequisicaoDeCompra) {
        ItemSolicitacaoAquisicao itemSolicitacao = new ItemSolicitacaoAquisicao();
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        itemSolicitacao.setDataLancamento(dataLancamento);
        itemSolicitacao.setSolicitacaoAquisicao(entity);
        itemSolicitacao.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
        itemSolicitacao.setUnidadeAdministrativa(unidadeAdministrativa);
        itemSolicitacao.setUnidadeOrcamentaria(unidadeOrcamentaria);
        itemSolicitacao.setItemDoctoItemAquisicao(itemDoctoItemAquisicao);
        itemSolicitacao.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);

        EstadoBem resultante = criarEstadoResultanteBem(entity, itemSolicitacao);
        BigDecimal valorLancamento = itemRequisicaoDeCompra.getValorUnitario().setScale(2, RoundingMode.HALF_EVEN);
        resultante.setValorOriginal(valorLancamento);
        itemSolicitacao.setEstadoResultante(resultante);
        itemSolicitacao.setEstadoInicial(null);

        itemSolicitacao.setValorDoLancamento(valorLancamento);
        return itemSolicitacao;
    }

    public List<DoctoFiscalSolicitacaoAquisicao> buscarDocumentoFiscal(SolicitacaoAquisicao entity) {
        String sql = "" +
            " select " +
            "   doctoSol.* " +
            " from doctofiscalsolicaquisicao doctoSol " +
            " where doctoSol.solicitacaoaquisicao_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql, DoctoFiscalSolicitacaoAquisicao.class);
        q.setParameter("idSolicitacao", entity.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    private Integer calcularQuantidadePorItem(ItemDoctoFiscalLiquidacao itemDoctoFiscalLiquidacao) {
        int qtde = 0;
        qtde = qtde + itemDoctoFiscalLiquidacao.getQuantidade().intValue();
        return qtde;
    }

    private void concluirItemSolicitacao(List<ItemSolicitacaoAquisicao> itensSolicitacao, AssistenteMovimentacaoBens assitente) {
        for (ItemSolicitacaoAquisicao itemSolicitacao : itensSolicitacao) {
            Query qEstadoBem = em.createNativeQuery(" update eventobem set situacaoeventobem = :situacao where id = :idEvento ");
            qEstadoBem.setParameter("idEvento", itemSolicitacao.getId());
            qEstadoBem.setParameter("situacao", SituacaoEventoBem.AGUARDANDO_EFETIVACAO.name());
            qEstadoBem.executeUpdate();
            assitente.conta();
        }
    }

    private EstadoBem criarEstadoResultanteBem(SolicitacaoAquisicao entity, ItemSolicitacaoAquisicao itemSolicitacao) {
        EstadoBem resultante = bemFacade.criarNovoEstadoResultanteAPartirDoCaracterizador(itemSolicitacao);
        resultante.setGrupoObjetoCompra(itemSolicitacao.getGrupoObjetoCompra());
        resultante.setTipoGrupo(entity.getTipoBem().equals(TipoBem.MOVEIS) ? TipoGrupo.BEM_MOVEL_PRINCIPAL : TipoGrupo.BEM_IMOVEL_PRINCIPAL);
        resultante = em.merge(resultante);
        return resultante;
    }

    public List<SolicitacaoAquisicao> buscarFiltrandoSolicitacaoAguardandoEfetivacao(String filtro, TipoBem tipoBem) {
        String sql = " " +
            " select sol.* from solicitacaoaquisicao sol   " +
            "   inner join requisicaodecompra req on req.id = sol.requisicaodecompra_id  " +
            "   left join contrato cont on req.contrato_id = cont.id " +
            "   left join unidadecontrato uc on uc.contrato_id = cont.id " +
            "     and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "   left join reconhecimentodivida rd on rd.id = req.reconhecimentodivida_id " +
            "   left join requisicaocompraexecucao reqex on req.id = reqex.requisicaocompra_id " +
            "   left join execucaoprocesso exproc on exproc.id = reqex.execucaoprocesso_id " +
            "   left join execucaoprocessoata exata on exata.execucaoprocesso_id = exproc.id " +
            "   left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "   left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = exproc.id " +
            "   left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
            "   left join processodecompra pc on pc.id = disp.processodecompra_id " +
            "   inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = coalesce(uc.unidadeadministrativa_id, rd.unidadeadministrativa_id, ata.unidadeorganizacional_id, pc.unidadeorganizacional_id) " +
            " where (sol.numero like :filtro or req.numero like :filtro or lower(req.descricao) like :filtro) " +
            "   and sol.situacao = '" + SituacaoEventoBem.AGUARDANDO_EFETIVACAO.name() + "'" +
            "   and sol.tipobem = :tipoBem " +
            "   and extract(year from sol.datasolicitacao) = :exercicio " +
            "   and uuo.usuariosistema_id = :id_usuario " +
            "   and uuo.gestorPatrimonio = :gestor_patromonio " +
            " order by sol.numero ";
        Query q = em.createNativeQuery(sql, SolicitacaoAquisicao.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("tipoBem", tipoBem.name());
        q.setParameter("exercicio", sistemaFacade.getExercicioCorrente().getAno());
        q.setParameter("id_usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("gestor_patromonio", Boolean.TRUE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<BigDecimal> buscarIdsItemSolicitacaoAquisicao(SolicitacaoAquisicao solicitacaoAquisicao) {
        String sql = " " +
            " select " +
            "    to_number(item.id) " +
            " from itemsolicitacaoaquisicao item " +
            " where item.solicitacaoAquisicao_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacaoAquisicao.getId());
        return q.getResultList();
    }

    public List<SolicitacaoAquisicao> buscarSolicitacoesPorRequisicao(RequisicaoDeCompra requisicaoDeCompra) {
        if (requisicaoDeCompra.getId() != null) {
            String sql = "select sol.* " +
                " from solicitacaoaquisicao sol " +
                " where sol.requisicaodecompra_id = :idRequisicaoDeCompra " +
                " order by sol.numero ";
            Query q = em.createNativeQuery(sql, SolicitacaoAquisicao.class);
            q.setParameter("idRequisicaoDeCompra", requisicaoDeCompra.getId());
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public SolicitacaoAquisicao recuperarSolicitacaoAquisicaoComItensSolicitacaoPorItemDocto(SolicitacaoAquisicao sa) {
        sa = recuperar(sa.getId());
        sa.setIdAquisicao(buscarIdAquisicaoCanGerarTermoPorSolicitacao(sa));
        sa.getDocumentosFiscais().forEach(
            doc -> {
                doc.setLiquidacoesVOs(requisicaoDeCompraFacade.buscarLiquidacoesVOPorDocumentoFiscal(doc.getDocumentoFiscal()));
                doc.getItens().forEach(
                    itemDocto -> itemDocto.setItensSolicitacao(buscarItensSolicitacoesAquisicoesPorItemDoctoItemAquisicao(itemDocto))
                );
            }
        );
        return sa;
    }

    public List<ItemSolicitacaoAquisicao> buscarItensSolicitacoesAquisicoesPorItemDoctoItemAquisicao(ItemDoctoItemAquisicao itemDocto) {
        String sql = "select isa.* " +
            "from itemdoctoitemaquisicao associacao " +
            "         inner join itemsolicitacaoaquisicao isa on isa.itemdoctoitemaquisicao_id = associacao.id " +
            "         inner join eventobem ev on ev.id = isa.id " +
            "         inner join bem on bem.id = ev.bem_id " +
            "where associacao.id = :idItemDoctoItemAquisicao ";
        Query q = em.createNativeQuery(sql, ItemSolicitacaoAquisicao.class);
        q.setParameter("idItemDoctoItemAquisicao", itemDocto.getId());
        return q.getResultList();
    }

    public Long buscarIdAquisicaoCanGerarTermoPorSolicitacao(SolicitacaoAquisicao sa) {
        String sql = "SELECT distinct aq.id, ev.SITUACAOEVENTOBEM " +
            "FROM AQUISICAO aq " +
            "         INNER JOIN ITEMAQUISICAO item ON item.AQUISICAO_ID = aq.ID " +
            "         INNER JOIN EVENTOBEM ev ON ev.ID = item.ID " +
            "WHERE aq.SOLICITACAOAQUISICAO_ID = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", sa.getId());
        List<Object[]> resultado = q.getResultList();
        if (resultado.size() == 1 && SituacaoEventoBem.FINALIZADO.name().equals(resultado.get(0)[1])) {
            return ((BigDecimal) resultado.get(0)[0]).longValue();
        }
        return null;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<ItemSolicitacaoAquisicao>> buscarItensSolicitacaoAquisicao(SolicitacaoAquisicao solicitacaoAquisicao) {
        List<BigDecimal> idsItemSolicitacao = buscarIdsItemSolicitacaoAquisicao(solicitacaoAquisicao);
        return new AsyncResult<List<ItemSolicitacaoAquisicao>>(preencherItensSolicitacaoAquisicao(idsItemSolicitacao));
    }

    public List<ItemSolicitacaoAquisicao> buscarItensSolicitacao(SolicitacaoAquisicao solicitacaoAquisicao) {
        List<BigDecimal> idsItemSolicitacao = buscarIdsItemSolicitacaoAquisicao(solicitacaoAquisicao);
        return preencherItensSolicitacaoAquisicao(idsItemSolicitacao);
    }


    public List<ItemSolicitacaoAquisicao> preencherItensSolicitacaoAquisicao(List<BigDecimal> idsConsulta) {
        List<ItemSolicitacaoAquisicao> itens = new ArrayList<>();
        for (BigDecimal obj : idsConsulta) {
            itens.add(em.find(ItemSolicitacaoAquisicao.class, obj.longValue()));
        }
        return itens;
    }

    public SingletonGeradorCodigoPatrimonio getSingletonGeradorCodigoPatrimonio() {
        return singletonGeradorCodigoPatrimonio;
    }

    public void verificarQuantidadeNoDocumentoComItensSolicitacaoCriados(Integer qtdeTotalItensDocumento, Integer qtdeItensSolicitacao) {
        ValidacaoException ve = new ValidacaoException();
        if (!qtdeTotalItensDocumento.equals(qtdeItensSolicitacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade total dos itens do documento fiscal está diferente da quantidade de itens gerado na solicitação.");
            ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade total do documento: " + qtdeTotalItensDocumento + " <> " + "Quantidade total de itens: " + qtdeItensSolicitacao + ".");
        }
        ve.lancarException();
    }

    public void verificarAssociacaoDosGruposObjetoCompraComGrupoBem(ValidacaoException ve, Date dataOperacao, String descricaoItem, ObjetoCompra objetoCompra) {
        if (objetoCompra == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado objeto de compra para o item licitado: " + descricaoItem + ".");
        }
        ve.lancarException();
        if (objetoCompra.getGrupoObjetoCompra() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado grupo objeto de compra para o item licitado: " + descricaoItem + ".");
        }
        ve.lancarException();
        if (objetoCompra.getGrupoObjetoCompra().getCodigo().startsWith("1") && !objetoCompra.getTipoObjetoCompra().isMaterialPermanente()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O grupo objeto de compra: " + objetoCompra.getGrupoObjetoCompra() + ", não pertence ao tipo de grupo: <b>" + objetoCompra.getTipoObjetoCompra().getDescricao() + "</b>.");

        } else if (objetoCompra.getGrupoObjetoCompra().getCodigo().startsWith("2") && !objetoCompra.getTipoObjetoCompra().isMaterialConsumo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O grupo objeto de compra: " + objetoCompra.getGrupoObjetoCompra() + ", não pertence ao tipo de grupo: <b>" + objetoCompra.getTipoObjetoCompra().getDescricao() + "</b>.");

        } else if (objetoCompra.getGrupoObjetoCompra().getCodigo().startsWith("3") && !objetoCompra.getTipoObjetoCompra().isServico()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O grupo objeto de compra: " + objetoCompra.getGrupoObjetoCompra() + ", não pertence ao tipo de grupo: <b>" + objetoCompra.getTipoObjetoCompra().getDescricao() + "</b>.");

        } else if (objetoCompra.getGrupoObjetoCompra().getCodigo().startsWith("4") && !objetoCompra.getTipoObjetoCompra().isObraInstalacoes()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O grupo objeto de compra: " + objetoCompra.getGrupoObjetoCompra() + ", não pertence ao tipo de grupo: <b>" + objetoCompra.getTipoObjetoCompra().getDescricao() + "</b>.");
        }
        ve.lancarException();
        GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem = getGrupoObjetoCompraGrupoBemFacade().recuperarAssociacaoDoGrupoObjetoCompra(objetoCompra.getGrupoObjetoCompra(), dataOperacao);
        if (grupoObjetoCompraGrupoBem == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado associação do grupo objeto de compra para o grupo objeto de compra: " + objetoCompra.getGrupoObjetoCompra() + ".");
        }
        ve.lancarException();
        if (grupoObjetoCompraGrupoBem.getGrupoBem() == null) {
            String mensagem = "O grupo objeto de compra " + objetoCompra.getGrupoObjetoCompra() + " não possui associação vigente com grupo patrimonial em " + DataUtil.getDataFormatada(dataOperacao) + ".";
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
        ve.lancarException();
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public GrupoObjetoCompraGrupoBemFacade getGrupoObjetoCompraGrupoBemFacade() {
        return grupoObjetoCompraGrupoBemFacade;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public TipoDocumentoFiscalFacade getTipoDocumentoFiscalFacade() {
        return tipoDocumentoFiscalFacade;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public ConfigContaDespesaTipoDocumentoFacade getConfigContaDespesaTipoDocumentoFacade() {
        return configContaDespesaTipoDocumentoFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }
}
