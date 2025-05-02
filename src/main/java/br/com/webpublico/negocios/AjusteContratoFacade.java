package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AjusteContratoAprovacaoVO;
import br.com.webpublico.entidadesauxiliares.ItemAjusteContratoDadosCadastraisVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Stateless
public class AjusteContratoFacade extends AbstractFacade<AjusteContrato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private ReprocessamentoSaldoContratoFacade reprocessamentoSaldoContratoFacade;

    public AjusteContratoFacade() {
        super(AjusteContrato.class);
    }

    @Override
    public AjusteContrato recuperar(Object id) {
        AjusteContrato ajuste = em.find(AjusteContrato.class, id);
        Hibernate.initialize(ajuste.getAjustesDadosCadastrais());
        if (ajuste.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(ajuste.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return ajuste;
    }

    public AjusteContrato recuperarSemDependencias(Object id) {
        return super.recuperar(id);
    }

    public AjusteContrato salvarAjuste(AjusteContrato entity) {
        if (Strings.isNullOrEmpty(entity.getNumeroAjuste())) {
            entity.setNumeroAjuste(Util.zerosAEsquerda(getProximoNumeroAjuste(entity.getContrato(), "numeroAjuste").toString(), 4));
        }
        entity = em.merge(entity);
        return entity;
    }

    public AjusteContrato aprovarAjuste(AjusteContrato entity, AjusteContratoAprovacaoVO aprovacaoVO) {
        entity.setSituacao(SituacaoAjusteContrato.APROVADO);
        entity = em.merge(entity);
        salvarAlteracaoDados(aprovacaoVO, entity);
        return entity;
    }

    public void salvarAlteracaoDados(AjusteContratoAprovacaoVO aprovacaoVO, AjusteContrato entity) {
        if (entity.isAjusteContrato()) {
            em.merge(aprovacaoVO.getContrato());

            if (aprovacaoVO.isAjusteAprovadoParaEmElaboracao()) {
                for (ItemContrato itemCont : aprovacaoVO.getItensContratoDeAprovadoParaEmElaboracao()) {
                    contratoFacade.getSaldoItemContratoFacade().excluirSaldoAndMovimento(itemCont);
                }
            }
        } else if (entity.isAjusteAditivoOrApostilamento()) {
            em.merge(aprovacaoVO.getAlteracaoContratual());

        } else if (entity.isAjusteControleExecucao()) {
            aprovacaoVO.getItensAjusteDadosCadastrais().forEach(item -> {
                em.merge(item.getAjusteAlteracaoDados().getItemContrato());
            });
        } else if (entity.isSubstituicaoObjetoCompra()) {
            aprovacaoVO.getItensAjusteDadosCadastrais().forEach(item -> {
                em.merge(item.getAjusteAlteracaoDados().getItemContrato());
            });
        } else if (entity.isAjusteOperacaoAditivo()) {
            List<ExecucaoContrato> execucoes = execucaoContratoFacade.buscarExecucoesPorOrigem(aprovacaoVO.getAlteracaoContratual().getId());
            aprovacaoVO.getItensAjusteDadosCadastrais().forEach(mov -> {
                em.merge(mov.getAjusteAlteracaoDados().getMovimentoAlteracaoCont());

                for (ExecucaoContrato execucao : execucoes) {
                    execucao.setOperacaoOrigem(OperacaoSaldoItemContrato.getOperacaoSaldoPorOperacaoMovimentoContratual(mov.getAjusteAlteracaoDados().getOperacao()));
                    em.merge(execucao);
                }
            });
        }
    }

    public AjusteContrato buscarUltimoAjusteContratoAprovado(AjusteContrato ajuste) {
        String sql = " select aj.* from ajustecontrato aj " +
                "          where aj.contrato_id = :idContrato " +
                "          and aj.situacao <> :situacaoAprovado ";
        sql += ajuste.getId() != null ? " and aj.id <> :idAjuste " : "";
        sql += " order by aj.id desc ";
        Query q = em.createNativeQuery(sql, AjusteContrato.class);
        q.setParameter("idContrato", ajuste.getContrato().getId());
        q.setParameter("situacaoAprovado", SituacaoAjusteContrato.APROVADO.name());
        if (ajuste.getId() != null) {
            q.setParameter("idAjuste", ajuste.getId());
        }
        try {
            return (AjusteContrato) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<AjusteContrato> buscarAjusteContratoPorSituacao(Contrato contrato, SituacaoAjusteContrato situacao) {
        String sql = " select aj.* from ajustecontrato aj " +
                "          where aj.contrato_id = :idContrato " +
                "          and aj.situacao = :situacao " +
                "          order by aj.id desc ";
        Query q = em.createNativeQuery(sql, AjusteContrato.class);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("situacao", situacao.name());
        return q.getResultList();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidade(UnidadeOrganizacional unidadeOrganizacional, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                sistemaFacade.getDataOperacao(),
                unidadeOrganizacional,
                tipoHierarquiaOrganizacional);
    }

    public Long getProximoNumeroAjuste(Contrato contrato, String campoNumero) {
        String sql = " select coalesce(numero, 0) +1 from" +
                "         (select max(cast( ad." + campoNumero + " as number)) as numero " +
                "         from ajustecontrato ad " +
                "         where ad.contrato_id = :idContrato) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contrato.getId());
        return ((BigDecimal) q.getSingleResult()).longValue();
    }

    public List<ItemAjusteContratoDadosCadastraisVO> criarItemAjusteContratoDadosCadastraisVO(AjusteContrato selecionado) {
        String sql = "";
        if (selecionado.isAjusteControleExecucao()) {
            sql = " select * from itemcontrato item where item.contrato_id = :idContrato and item.tipocontrole = :tipoControle";
        } else if (selecionado.isSubstituicaoObjetoCompra()) {
            sql = " select * from itemcontrato item where item.contrato_id = :idContrato";
        }
        Query q = em.createNativeQuery(sql, ItemContrato.class);
        q.setParameter("idContrato", selecionado.getContrato().getId());

        if (selecionado.isAjusteControleExecucao()) {
            q.setParameter("tipoControle", TipoControleLicitacao.QUANTIDADE.name());
        }

        List<ItemAjusteContratoDadosCadastraisVO> list = Lists.newArrayList();
        try {
            for (ItemContrato itemContrato : (List<ItemContrato>) q.getResultList()) {
                ItemAjusteContratoDadosCadastraisVO item = new ItemAjusteContratoDadosCadastraisVO();
                AjusteContratoDadosCadastrais ajusteOriginal = new AjusteContratoDadosCadastrais();
                ajusteOriginal.setTipoAjuste(TipoAjusteDadosCadastrais.ORIGINAL);
                ajusteOriginal.setItemContrato(itemContrato);
                ajusteOriginal.setObjetoCompra(itemContrato.getItemAdequado().getObjetoCompra());
                ajusteOriginal.setDescricaoComplementar(itemContrato.getItemAdequado().getDescricaoComplementar());
                ajusteOriginal.setTipoControle(itemContrato.getTipoControle());
                ajusteOriginal.setQuantidade(itemContrato.getQuantidadeTotalContrato());
                ajusteOriginal.setValorUnitario(itemContrato.getValorUnitario());
                item.setAjusteDadosOriginais(ajusteOriginal);

                AjusteContratoDadosCadastrais ajusteAlteracao = new AjusteContratoDadosCadastrais();
                ajusteAlteracao.setTipoAjuste(TipoAjusteDadosCadastrais.ALTERACAO);
                ajusteAlteracao.setItemContrato(itemContrato);
                ajusteAlteracao.setQuantidade(itemContrato.getQuantidadeTotalContrato());
                ajusteAlteracao.setValorUnitario(itemContrato.getValorUnitario());

                if (selecionado.getId() != null) {
                    AjusteContratoDadosCadastrais ajusteSalvo = buscarAjustesDadosCadastraisPorItemContratoAndTipoAjuste(selecionado, TipoAjusteDadosCadastrais.ALTERACAO, itemContrato);
                    if (ajusteSalvo != null) {
                        ajusteAlteracao.setQuantidade(ajusteSalvo.getQuantidade());
                        ajusteAlteracao.setValorUnitario(ajusteSalvo.getValorUnitario());
                        ajusteAlteracao.setObjetoCompra(ajusteSalvo.getObjetoCompra());
                        ajusteAlteracao.setDescricaoComplementar(ajusteSalvo.getDescricaoComplementar());
                        ajusteAlteracao.setTipoControle(ajusteSalvo.getTipoControle());
                    }
                }
                item.setAjusteAlteracaoDados(ajusteAlteracao);
                list.add(item);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public List<ItemAjusteContratoDadosCadastraisVO> criarMovimentosOperacaoAditivo(AjusteContrato selecionado, AlteracaoContratual alteracaoContratual) {
        String sql = "select * from MOVIMENTOALTERACAOCONT mov " +
                " where mov.alteracaocontratual_id = :idAditivo " +
                " and mov.operacao in :operacoes ";
        Query q = em.createNativeQuery(sql, MovimentoAlteracaoContratual.class);
        q.setParameter("idAditivo", alteracaoContratual.getId());
        List<String> operacoesPermitidas = Arrays.asList(
                "REDIMENSIONAMENTO_OBJETO", "REAJUSTE_POS_EXECUCAO", "REAJUSTE_PRE_EXECUCAO",
                "REEQUILIBRIO_POS_EXECUCAO", "REEQUILIBRIO_PRE_EXECUCAO", "OUTRO"
        );
        q.setParameter("operacoes", operacoesPermitidas);

        List<ItemAjusteContratoDadosCadastraisVO> list = Lists.newArrayList();
        try {
            for (MovimentoAlteracaoContratual movimentoAditivo : (List<MovimentoAlteracaoContratual>) q.getResultList()) {
                ItemAjusteContratoDadosCadastraisVO mov = new ItemAjusteContratoDadosCadastraisVO();
                AjusteContratoDadosCadastrais ajusteOriginal = new AjusteContratoDadosCadastrais();
                ajusteOriginal.setTipoAjuste(TipoAjusteDadosCadastrais.ORIGINAL);
                ajusteOriginal.setMovimentoAlteracaoCont(movimentoAditivo);
                ajusteOriginal.setAlteracaoContratual(movimentoAditivo.getAlteracaoContratual());
                ajusteOriginal.setOperacao(movimentoAditivo.getOperacao());
                mov.setAjusteDadosOriginais(ajusteOriginal);

                AjusteContratoDadosCadastrais ajusteAlteracao = new AjusteContratoDadosCadastrais();
                ajusteAlteracao.setTipoAjuste(TipoAjusteDadosCadastrais.ALTERACAO);
                ajusteAlteracao.setMovimentoAlteracaoCont(movimentoAditivo);
                ajusteAlteracao.setAlteracaoContratual(movimentoAditivo.getAlteracaoContratual());

                if (selecionado.getId() != null) {
                    AjusteContratoDadosCadastrais ajusteSalvo = buscarAjustesDadosCadastraisPorMovimentoAditivoAndTipoAjuste(selecionado, TipoAjusteDadosCadastrais.ALTERACAO, movimentoAditivo);
                    if (ajusteSalvo != null) {
                        ajusteAlteracao.setOperacao(ajusteSalvo.getOperacao());
                    }
                }
                mov.setAjusteAlteracaoDados(ajusteAlteracao);
                list.add(mov);
            }
            return list;
        } catch (NoResultException ex) {
            return list;
        }
    }


    public List<ItemAjusteContratoDadosCadastraisVO> buscarAjustesDadosOriginalPopulandoAjusteAlteracao(AjusteContrato selecionado) {
        String sql = " select * from AJUSTECONTRATODADOSCAD " +
                "     where ajustecontrato_id = :idAjuste and tipoajuste = :tipoAjuste";
        Query q = em.createNativeQuery(sql, AjusteContratoDadosCadastrais.class);
        q.setParameter("idAjuste", selecionado.getId());
        q.setParameter("tipoAjuste", TipoAjusteDadosCadastrais.ORIGINAL.name());
        List<ItemAjusteContratoDadosCadastraisVO> list = Lists.newArrayList();
        try {
            for (AjusteContratoDadosCadastrais ajusteItem : (List<AjusteContratoDadosCadastrais>) q.getResultList()) {
                ItemAjusteContratoDadosCadastraisVO item = new ItemAjusteContratoDadosCadastraisVO();
                item.setAjusteDadosOriginais(ajusteItem);
                if (selecionado.isAjusteControleExecucao() || selecionado.isSubstituicaoObjetoCompra()) {
                    item.setAjusteAlteracaoDados(buscarAjustesDadosCadastraisPorItemContratoAndTipoAjuste(selecionado, TipoAjusteDadosCadastrais.ALTERACAO, ajusteItem.getItemContrato()));
                } else if (selecionado.isAjusteOperacaoAditivo()) {
                    item.setAjusteAlteracaoDados(buscarAjustesDadosCadastraisPorMovimentoAditivoAndTipoAjuste(selecionado, TipoAjusteDadosCadastrais.ALTERACAO, ajusteItem.getMovimentoAlteracaoCont()));
                }
                list.add(item);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public AjusteContratoDadosCadastrais buscarAjustesDadosCadastraisPorItemContratoAndTipoAjuste(AjusteContrato selecionado, TipoAjusteDadosCadastrais tipoAjuste, ItemContrato itemContrato) {
        String sql = " select * from AJUSTECONTRATODADOSCAD " +
                "     where ajustecontrato_id = :idAjuste and tipoajuste = :tipoAjuste and itemcontrato_id = :idItemContrato";
        Query q = em.createNativeQuery(sql, AjusteContratoDadosCadastrais.class);
        q.setParameter("idAjuste", selecionado.getId());
        q.setParameter("idItemContrato", itemContrato.getId());
        q.setParameter("tipoAjuste", tipoAjuste.name());
        return (AjusteContratoDadosCadastrais) q.getSingleResult();
    }

    public AjusteContratoDadosCadastrais buscarAjustesDadosCadastraisPorMovimentoAditivoAndTipoAjuste(AjusteContrato selecionado, TipoAjusteDadosCadastrais tipoAjuste, MovimentoAlteracaoContratual movimentoAditivo) {
        String sql = " select * from AJUSTECONTRATODADOSCAD " +
                "     where ajustecontrato_id = :idAjuste and tipoajuste = :tipoAjuste and movimentoAlteracaoCont_id = :idMovimentoAditivo";
        Query q = em.createNativeQuery(sql, AjusteContratoDadosCadastrais.class);
        q.setParameter("idAjuste", selecionado.getId());
        q.setParameter("idMovimentoAditivo", movimentoAditivo.getId());
        q.setParameter("tipoAjuste", tipoAjuste.name());
        return (AjusteContratoDadosCadastrais) q.getSingleResult();
    }

    public void atribuirGrupoContaDespesaObjetoCompra(ObjetoCompra objetoCompra) {
        objetoCompra.setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra()));
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public void setContratoFacade(ContratoFacade contratoFacade) {
        this.contratoFacade = contratoFacade;
    }


    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public AlteracaoContratualFacade getAlteracaoContratualFacade() {
        return alteracaoContratualFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public ReprocessamentoSaldoContratoFacade getReprocessamentoSaldoContratoFacade() {
        return reprocessamentoSaldoContratoFacade;
    }
}
