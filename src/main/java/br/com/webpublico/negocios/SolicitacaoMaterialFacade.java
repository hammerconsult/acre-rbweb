/*
 * Codigo gerado automaticamente em Wed Nov 09 08:42:27 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.administrativo.SolicitacaoMaterialVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class SolicitacaoMaterialFacade extends AbstractFacade<SolicitacaoMaterial> {

    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private ServicoCompravelFacade servicoCompravelFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SolicitacaoReposicaoEstoqueFacade solicitacaoReposicaoEstoqueFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CotacaoFacade cotacaoFacade;
    @EJB
    private ConfigTipoObjetoCompraFacade configTipoObjetoCompraFacade;
    @EJB
    private ParticipanteIRPFacade participanteIRPFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private AmparoLegalFacade amparoLegalFacade;
    @EJB
    private PlanoContratacaoAnualObjetoCompraFacade planoContratacaoAnualObjetoCompraFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;
    @EJB
    private ConfigDespesaBensFacade configDespesaBensFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public SolicitacaoMaterialFacade() {
        super(SolicitacaoMaterial.class);
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public SolicitacaoMaterial salvarRetornando(SolicitacaoMaterial entity) {
        if (entity.getNumero() == null) {
            entity.setNumero((singletonGeradorCodigo.getProximoCodigo(SolicitacaoMaterial.class, "numero")).intValue());
        }
        entity = em.merge(entity);
        return entity;
    }

    public List<LoteSolicitacaoMaterial> buscarLotesWithItens(SolicitacaoMaterial solicitacaoMaterial) {
        String sql = " select lote.* from lotesolicitacaomaterial lote " +
            "          where lote.solicitacaomaterial_id = :idSolicitacao" +
            "           order by lote.numero ";
        Query q = em.createNativeQuery(sql, LoteSolicitacaoMaterial.class);
        q.setParameter("idSolicitacao", solicitacaoMaterial.getId());
        List<LoteSolicitacaoMaterial> lotes = q.getResultList();
        if (lotes.isEmpty()) {
            return Lists.newArrayList();
        }
        for (LoteSolicitacaoMaterial lote : lotes) {
            Hibernate.initialize(lote.getItensSolicitacao());
        }
        return lotes;
    }

    public List<ItemSolicitacao> buscarItens(SolicitacaoMaterial solicitacaoMaterial) {
        String sql = " select item.* from itemsolicitacao item " +
            "          inner join lotesolicitacaomaterial lote on lote.id = item.lotesolicitacaomaterial_id" +
            "          where lote.solicitacaomaterial_id = :idSolicitacao" +
            "           order by lote.numero, item.numero ";
        Query q = em.createNativeQuery(sql, ItemSolicitacao.class);
        q.setParameter("idSolicitacao", solicitacaoMaterial.getId());
        return q.getResultList();
    }

    public DotacaoSolicitacaoMaterial buscarDotacaoSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        Query hql = em.createQuery(" from DotacaoSolicitacaoMaterial dsm where dsm.solicitacaoMaterial = :param");
        hql.setParameter("param", solicitacaoMaterial);

        if (!hql.getResultList().isEmpty()) {
            DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial = (DotacaoSolicitacaoMaterial) hql.getResultList().get(0);
            dotacaoSolicitacaoMaterial.getItens().size();
            for (DotacaoSolicitacaoMaterialItem item : dotacaoSolicitacaoMaterial.getItens()) {
                item.getFontes().size();
            }
            return dotacaoSolicitacaoMaterial;
        }

        return null;
    }

    public List<SolicitacaoMaterial> buscarSolicitacaoAprovadaPeloTipoAndTipoProcessoCompraAndDescricaoOrNumeroOndeUsuarioGestorLicitacao(String numeroOrDescricao,
                                                                                                                                          TipoSolicitacao tipoSolicitacao,
                                                                                                                                          TipoProcessoDeCompra tipoProcessoDeCompra,
                                                                                                                                          UsuarioSistema usuarioSistema) {
        if (tipoSolicitacao == null || tipoProcessoDeCompra == null) {
            return null;
        }
        String sql = " select distinct sol.* " +
            "   from solicitacaomaterial sol" +
            "  inner join avaliacaosolcompra ava on ava.solicitacao_id = sol.id       " +
            "  inner join lotesolicitacaomaterial loto on loto.solicitacaomaterial_id = sol.id " +
            "  inner join itemsolicitacao item on item.lotesolicitacaomaterial_id = loto.id " +
            "  left join itemprocessodecompra itemcompra on itemcompra.itemsolicitacaomaterial_id = item.id " +
            "  left join loteprocessodecompra lote on lote.id = itemcompra.loteprocessodecompra_id " +
            "  left join processodecompra proc on proc.id = lote.processodecompra_id " +
            "where proc.id is null " +
            "  and ava.dataavaliacao = (select max(avali.dataavaliacao) from avaliacaosolcompra avali where avali.solicitacao_id = sol.id) " +
            "  and ava.tipostatussolicitacao = :tipo_status_solicitacao " +
            "  and sol.tiposolicitacao = :tipo_solicitacao " +
            "  and sol.modalidadelicitacao in (:modalidades) " +
            "  and exists (select 1 from usuariounidadeorganizacio u_un " +
            "              where u_un.usuariosistema_id = :id_usuario" +
            "                and u_un.unidadeorganizacional_id = sol.unidadeorganizacional_id " +
            "                and u_un.gestorlicitacao = :gestor_licitacao)" +
            "  and (lower(sol.descricao) like :numero_descricao or to_char(sol.numero) like :numero_descricao) " +
            "order by sol.numero desc ";
        Query query = getEntityManager().createNativeQuery(sql, SolicitacaoMaterial.class);
        query.setParameter("tipo_status_solicitacao", TipoStatusSolicitacao.APROVADA.name());
        query.setParameter("tipo_solicitacao", tipoSolicitacao.name());
        query.setParameter("modalidades", Util.getListOfEnumName(ModalidadeLicitacao.getModalidadesParaTipoDoProcesso(tipoProcessoDeCompra)));
        query.setParameter("id_usuario", usuarioSistema.getId());
        query.setParameter("gestor_licitacao", Boolean.TRUE);
        query.setParameter("numero_descricao", "%" + numeroOrDescricao.trim().toLowerCase() + "%");
        query.setMaxResults(30);
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            return new ArrayList<>();
        }
    }

    public boolean hasVinculoComDotacao(SolicitacaoMaterial solicitacaoMaterial) {
        String sql = " select dsm.* from dotsolmat dsm where dsm.solicitacaomaterial_id = :idSolicitacao  ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idSolicitacao", solicitacaoMaterial.getId());
        query.setMaxResults(1);
        try {
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    public boolean temVinculoComProcessoDeCompra(SolicitacaoMaterial solicitacaoMaterial) {
        if (solicitacaoMaterial == null || solicitacaoMaterial.getId() == null) {
            return false;
        }

        String sql = "SELECT count(ipdc.itemsolicitacaomaterial_id)"
            + "          FROM itemprocessodecompra ipdc"
            + "    INNER JOIN itemsolicitacao ism ON ipdc.itemsolicitacaomaterial_id = ism.id"
            + "    INNER JOIN lotesolicitacaomaterial lsm ON ism.lotesolicitacaomaterial_id = lsm.id"
            + "    INNER JOIN solicitacaomaterial sm ON lsm.solicitacaomaterial_id = sm.id"
            + "         WHERE sm.id = :param";

        Query q = em.createNativeQuery(sql);
        q.setParameter("param", solicitacaoMaterial.getId());

        BigDecimal count = (BigDecimal) q.getSingleResult();

        if (count.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }

        return true;
    }

    /*
     * Método que recupera a quantidade de determinado material, que irá
     * para determinada unidade organizacional, e cuja licitação não está
     * finalizada, ou seja, ainda está em andamento. Seu pedido foi feito, mas
     * ainda não foi comprado.
     */
    public BigDecimal recuperarItemSolicitacaoPorMaterialEUnidade(Material material, UnidadeOrganizacional uo) {
        String sql = "          SELECT sum(itemsol.quantidade)"
            + "               FROM solicitacaomaterial sm"
            + "         INNER JOIN avaliacaosolcompra ava ON ava.solicitacao_id = sm.id"
            + "         INNER JOIN lotesolicitacaomaterial lsm ON lsm.solicitacaomaterial_id = sm.id"
            + "         INNER JOIN itemsolicitacao itemsol ON itemsol.lotesolicitacaomaterial_id = lsm.id"
            + "         INNER JOIN objetocompra oc on oc.id = itemsol.objetocompra_id"
            + "         INNER JOIN material m ON m.objetocompra_id = oc.id"
            + "              WHERE sm.unidadeorganizacional_id = :unidade"
            + "                AND itemsol.saldo > 0"
            + "                AND ava.tipostatussolicitacao = '" + TipoStatusSolicitacao.APROVADA.name() + "' "
            + "                AND m.id = :material";


        Query q = em.createNativeQuery(sql);

        q.setParameter("unidade", uo.getId());
        q.setParameter("material", material.getId());

        return (BigDecimal) q.getSingleResult();
    }

    public List<SolicitacaoMaterial> buscarSolicitacaoMaterialNaoAvaliada(String parte) {
        String sql = "   select distinct sm.*  " +
            "    from solicitacaomaterial sm " +
            "   inner join avaliacaosolcompra ava on ava.solicitacao_id = sm.id " +
            "   left join dotsolmat dsm on dsm.solicitacaomaterial_id = sm.id " +
            " where ava.tipostatussolicitacao in (:tipos_status)  " +
            "   and ava.id = (select max(avali.id) from avaliacaosolcompra avali where avali.solicitacao_id = sm.id) " +
            "   and (dsm.id is not null or sm.tiponaturezadoprocedimento in (:tipos_de_registro_de_preco)) " +
            "   and (to_char(sm.datasolicitacao) like :parte or sm.numero like :parte or lower(sm.descricao) like :parte) " +
            " order by sm.numero desc";

        Query q = em.createNativeQuery(sql, SolicitacaoMaterial.class);
        q.setParameter("parte", "%" + parte + "%");
        q.setParameter("tipos_de_registro_de_preco", TipoNaturezaDoProcedimentoLicitacao.getTiposNaturezaAsString(TipoNaturezaDoProcedimentoLicitacao.getTiposDeNaturezaDeRegistroDePreco()));
        q.setParameter("tipos_status", Lists.newArrayList(new String[]{TipoStatusSolicitacao.AGUARDANDO_AVALIACAO.name(),
            TipoStatusSolicitacao.REJEITADA.name()}));
        if (q.getResultList().isEmpty()) {
            return Lists.newArrayList();
        } else {
            return q.getResultList();
        }
    }

    public List<SolicitacaoMaterial> buscarSolicitacaoMaterial(String parte) {
        String sql = " " +
            " select sm.* from solicitacaomaterial sm " +
            "  inner join exercicio ex on ex.id = sm.exercicio_id " +
            " where (to_char(sm.datasolicitacao) like :parte " +
            "       or sm.numero like :parte " +
            "       or to_char(sm.numero) || '/' || to_char(ex.ano) like :parte " +
            "       or lower(sm.descricao) like :parte) " +
            " order by sm.numero desc";
        Query q = em.createNativeQuery(sql, SolicitacaoMaterial.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    @Override
    public SolicitacaoMaterial recuperar(Object id) {
        SolicitacaoMaterial sm = super.recuperar(id);
        Hibernate.initialize(sm.getLoteSolicitacaoMaterials());
        Hibernate.initialize(sm.getAvaliacoes());
        Hibernate.initialize(sm.getUnidadesParticipantes());
        Hibernate.initialize(sm.getDocumentosOficiais());
        Hibernate.initialize(sm.getDotacoes());
        for (SolicitacaoCompraDotacao dot : sm.getDotacoes()) {
            Hibernate.initialize(dot.getItens());
        }
        for (LoteSolicitacaoMaterial loteSolicitacaoMaterial : sm.getLoteSolicitacaoMaterials()) {
            Hibernate.initialize(loteSolicitacaoMaterial.getItensSolicitacao());
        }
        recuperaListasCriterioTecnicaSolicitacao(sm);
        if (sm.getCotacao().getFormularioCotacao().getFormularioIrp()) {
            for (UnidadeSolicitacaoMaterial unidade : sm.getUnidadesParticipantes()) {
                participanteIRPFacade.atribuirUnidadeVigenteParticipante(unidade.getParticipanteIRP());
            }
        }
        if (sm.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(sm.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        return sm;
    }

    public SolicitacaoMaterial recuperarInicializandoAvaliacoes(Object id) {
        SolicitacaoMaterial sm = super.recuperar(id);
        Hibernate.initialize(sm.getAvaliacoes());
        return sm;
    }

    public SolicitacaoMaterial recarregarWithLotes(SolicitacaoMaterial solicitacaoMaterial) {
        solicitacaoMaterial = em.find(SolicitacaoMaterial.class, solicitacaoMaterial.getId());
        Hibernate.initialize(solicitacaoMaterial.getLoteSolicitacaoMaterials());
        return solicitacaoMaterial;
    }

    public Map<TipoObjetoCompra, List<ItemSolicitacao>> preencherMapItensPorTipoObjetoCompra(SolicitacaoMaterial selecionado) {
        Map<TipoObjetoCompra, List<ItemSolicitacao>> map = Maps.newHashMap();
        selecionado.getLoteSolicitacaoMaterials().stream()
            .flatMap(lote -> lote.getItensSolicitacao().stream())
            .forEach(item -> {
                TipoObjetoCompra tipoObjetoCompra = item.getObjetoCompra().getTipoObjetoCompra();
                if (!map.containsKey(tipoObjetoCompra)) {
                    map.put(tipoObjetoCompra, Lists.newArrayList(item));
                } else {
                    map.get(tipoObjetoCompra).add(item);
                }
            });
        return map;
    }

    private void recuperaListasCriterioTecnicaSolicitacao(SolicitacaoMaterial sm) {
        if (sm.getCriterioTecnicaSolicitacao() != null) {
            if (sm.getCriterioTecnicaSolicitacao().getItens() != null && !sm.getCriterioTecnicaSolicitacao().getItens().isEmpty()) {
                sm.getCriterioTecnicaSolicitacao().getItens().size();

                for (ItemCriterioTecnica item : sm.getCriterioTecnicaSolicitacao().getItens()) {
                    if (item.getPontos() != null && !item.getPontos().isEmpty()) {
                        item.getPontos().size();
                    }
                }
            }
        }
    }

    public SolicitacaoMaterial recuperarSolicitacaoMaterialDaLicitacao(Licitacao l) {
        String hql = "select sm from Licitacao l" +
            " inner join l.processoDeCompra pc" +
            " inner join pc.lotesProcessoDeCompra lpc" +
            " inner join lpc.itensProcessoDeCompra ipc" +
            " inner join ipc.itemSolicitacaoMaterial ism" +
            " inner join ism.loteSolicitacaoMaterial lsm" +
            " inner join lsm.solicitacaoMaterial sm" +
            " where l = :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", l);
        q.setMaxResults(1);
        try {
            return (SolicitacaoMaterial) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public String recuperaMotivoRejeicao(SolicitacaoMaterial material) {
        String hql = "select  av.motivo From AvaliacaoSolicitacaoDeCompra av where av.solicitacao.id = :sol order by av.dataAvaliacao desc ";
        Query q = em.createQuery(hql);
        q.setParameter("sol", material.getId());
        return (String) q.getResultList().get(0);
    }

    public TipoStatusSolicitacao getStatusAtualDaSolicitacao(SolicitacaoMaterial sl) {
        String hql = "select ava "
            + "from AvaliacaoSolicitacaoDeCompra ava "
            + "where ava.solicitacao = :solicitacao ";

        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", sl);

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            List<AvaliacaoSolicitacaoDeCompra> avaliacoes = q.getResultList();
            Collections.sort(avaliacoes, new Comparator<AvaliacaoSolicitacaoDeCompra>() {
                @Override
                public int compare(AvaliacaoSolicitacaoDeCompra o1, AvaliacaoSolicitacaoDeCompra o2) {
                    int i = o2.getDataAvaliacao().compareTo(o1.getDataAvaliacao());
                    if (i == 0) {
                        i = o2.getId().compareTo(o1.getId());
                    }
                    return i;
                }
            });
            return avaliacoes.get(0).getTipoStatusSolicitacao();

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhuma Avaliação para a Solicitação " + sl);
        }
    }

    public AvaliacaoSolicitacaoDeCompra getUltimaAvaliacaoDaSolicitacao(SolicitacaoMaterial sl) {
        String hql = "select ava "
            + "from AvaliacaoSolicitacaoDeCompra ava "
            + "where ava.solicitacao = :solicitacao "
            + "order by ava.dataAvaliacao, ava.id desc ";

        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", sl);
        q.setMaxResults(1);

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (AvaliacaoSolicitacaoDeCompra) q.getSingleResult();

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhuma Avaliação para a Solicitação " + sl);
        }
    }

    public List<SolicitacaoMaterial> buscarSolicitacaoMaterialDisponivelParaReservaPorNumeroAndDescricao(String numeroOrDescricao) {
        String sql = "select sol.* " +
            "    from solicitacaomaterial sol " +
            "   inner join exercicio e on sol.exercicio_id = e.id " +
            " where  (sol.numero like :numeroOrDescricao or lower(sol.descricao) like :numeroOrDescricao) " +
            "    and not exists(select 1 from dotsolmat dotacao " +
            "                   where dotacao.solicitacaomaterial_id = sol.id) " +
            "    and sol.unidadeorganizacional_id = :unidadecorrente " +
            "    and exists(select 1 from usuariounidadeorganizacio uuo " +
            "               where uuo.unidadeorganizacional_id = sol.unidadeorganizacional_id " +
            "                 and uuo.usuariosistema_id = :usuario_id " +
            "                 and uuo.gestorlicitacao = :gestorlicitacao) " +
            " order by e.ano desc , sol.numero desc ";
        Query q = em.createNativeQuery(sql, SolicitacaoMaterial.class);
        q.setParameter("numeroOrDescricao", "%" + numeroOrDescricao.toLowerCase() + "%");
        q.setParameter("unidadecorrente", sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente().getId());
        q.setParameter("usuario_id", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("gestorlicitacao", Boolean.TRUE);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        try {
            return q.getResultList();
        } catch (NoResultException noex) {
            return Lists.newArrayList();
        }
    }

    public List<UsuarioSistema> buscarUsuariosGestorLicitacaoPorTipoNotificacaoEUnidade(UnidadeOrganizacional unidade, TipoNotificacao tipoNotificacao) {
        String sql = " select distinct us.* " +
            " from USUARIOSISTEMA us " +
            "  inner join GRUPOUSUARIOSISTEMA gus on gus.USUARIOSISTEMA_ID = us.ID " +
            "  inner join GRUPOUSUARIO gu on gu.ID = gus.GRUPOUSUARIO_ID " +
            "  inner join GRUPOUSUARIONOTIFICACAO gun on gun.GRUPOUSUARIO_ID = gu.ID " +
            "  inner join USUARIOUNIDADEORGANIZACIO uuo on uuo.USUARIOSISTEMA_ID = us.ID " +
            " where gun.TIPONOTIFICACAO = :tipoNotificacao " +
            "  and uuo.GESTORLICITACAO = 1 " +
            "  and uuo.UNIDADEORGANIZACIONAL_ID = :idUnidade ";
        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("tipoNotificacao", tipoNotificacao.name());
        return q.getResultList();
    }

    public List<SolicitacaoMaterial> buscarSolicitacaoPorModalidadeNatureza(ModalidadeLicitacao modalidade, TipoNaturezaDoProcedimentoLicitacao natureza) {
        String sql = "   select distinct sm.* from solicitacaomaterial sm " +
            "            where sm.tiponaturezadoprocedimento = :tipoNatureza " +
            "            and sm.modalidadeLicitacao = :modalidade ";
        Query q = em.createNativeQuery(sql, SolicitacaoMaterial.class);
        q.setParameter("modalidade", modalidade.name());
        q.setParameter("tipoNatureza", natureza.name());
        if (q.getResultList().isEmpty()) {
            return Lists.newArrayList();
        }
        return q.getResultList();
    }

    public ConfigTipoObjetoCompraFacade getConfigTipoObjetoCompraFacade() {
        return configTipoObjetoCompraFacade;
    }

    public void validarCriterioProcessamentoItem(CriterioProcessamentoPrecoItemCotacao criterioProcessamento) {
        ValidacaoException ve = new ValidacaoException();
        if (criterioProcessamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo critério deve ser informado para processar os itens.");
        }
        ve.lancarException();
    }

    public ParticipanteIRPFacade getParticipanteIRPFacade() {
        return participanteIRPFacade;
    }

    public List<SolicitacaoMaterialVO> buscarSolicitacoesVO(TipoStatusSolicitacao tipoStatus, String condicao) {
        String sql = " select " +
            "  obj.id, " +
            "  obj.tipoSolicitacao, " +
            "  al.leiLicitacao, " +
            "  obj.subTipoObjetoCompra, " +
            "  obj.dataSolicitacao, " +
            "  obj.numero, " +
            "  obj.descricao, " +
            "  obj.justificativa, " +
            "  pf.cpf || ' - ' || pf.nome as solicitante, " +
            "  obj.modalidadeLicitacao, " +
            "  vwadm.codigo || ' - ' || vwadm.descricao as unidadeAdm, " +
            "  ultimoStatus.tipoStatusSolicitacao, " +
            "  coalesce(pfUsu.nome, usu.login) as criadoPor " +
            " from SolicitacaoMaterial obj " +
            "  inner join VWHIERARQUIAADMINISTRATIVA vwadm on obj.UNIDADEORGANIZACIONAL_ID = vwadm.SUBORDINADA_ID " +
            "  inner join AVALIACAOSOLCOMPRA ultimoStatus on ultimoStatus.solicitacao_id = obj.id " +
            "         and ultimoStatus.id = (select max(avaliacao.id) from AVALIACAOSOLCOMPRA avaliacao where avaliacao.solicitacao_id = obj.id) " +
            "  left join usuariosistema usu on usu.id = obj.usuarioCriacao_id " +
            "  left join pessoafisica pfUsu on usu.pessoafisica_id = pfUsu.id " +
            "  inner join exercicio ex on obj.exercicio_id = ex.id " +
            "  inner join pessoafisica pf on obj.SOLICITANTE_ID = pf.id" +
            "  inner join amparolegal al on al.id = obj.amparolegal_id  " +
            "  INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = obj.UNIDADEORGANIZACIONAL_ID " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   AND uu.gestorlicitacao = 1 " +
            "   AND uu.usuariosistema_id = :idUsuarioSistema " +
            "   and ultimoStatus.tipoStatusSolicitacao = :tipoStatus " +
            condicao +
            " order by obj.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idUsuarioSistema", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("tipoStatus", tipoStatus.name());
        List<Object[]> resultado = q.getResultList();
        List<SolicitacaoMaterialVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                SolicitacaoMaterialVO smvo = new SolicitacaoMaterialVO();
                smvo.setId(((BigDecimal) obj[0]).longValue());
                smvo.setTipoSolicitacao(obj[1] != null ? TipoSolicitacao.valueOf((String) obj[1]).getDescricao() : "");
                smvo.setLeiLicitacao(obj[2] != null ? LeiLicitacao.valueOf((String) obj[2]).getDescricao() : "");
                smvo.setSubTipoObjetoCompra(obj[3] != null ? SubTipoObjetoCompra.valueOf((String) obj[3]).getDescricao() : "");
                smvo.setDataSolicitacao((Date) obj[4]);
                smvo.setNumero(obj[5] != null ? ((BigDecimal) obj[5]).intValue() : null);
                smvo.setDescricao((String) obj[6]);
                smvo.setJustificativa((String) obj[7]);
                smvo.setSolicitante((String) obj[8]);
                smvo.setModalidadeLicitacao(obj[9] != null ? ModalidadeLicitacao.valueOf((String) obj[9]).getDescricao() : "");
                smvo.setUnidadeAdm((String) obj[10]);
                smvo.setStatusAtual(obj[11] != null ? TipoStatusSolicitacao.valueOf((String) obj[11]).getDescricao() : "");
                smvo.setCriadoPor((String) obj[12]);
                retorno.add(smvo);
            }
        }
        return retorno;
    }

    public void gerarNotificacaoNovaAvaliacaoSolicitacaoMaterialAguardando(AvaliacaoSolicitacaoDeCompra avaliacao) {
        List<UsuarioSistema> usuarios = buscarUsuariosGestorLicitacaoPorTipoNotificacaoEUnidade(avaliacao.getSolicitacao().getUnidadeOrganizacional(), TipoNotificacao.AVALIACAO_SOLICITACAO_COMPRA_AGUARDANDO);
        if (usuarios != null && !usuarios.isEmpty()) {
            for (UsuarioSistema usuario : usuarios) {
                Notificacao notificacao = new Notificacao();
                notificacao.setDescricao("Nova avaliação da solicitação de compra " + avaliacao.getSolicitacao().toString() + ", esta aguardando avaliação");
                notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
                notificacao.setTitulo(TipoNotificacao.AVALIACAO_SOLICITACAO_COMPRA_AGUARDANDO.getDescricao());
                notificacao.setTipoNotificacao(TipoNotificacao.AVALIACAO_SOLICITACAO_COMPRA_AGUARDANDO);
                notificacao.setUsuarioSistema(usuario);
                notificacao.setLink("/avaliacao-solicitacao-compra/editar/" + avaliacao.getId() + "/");
                NotificacaoService.getService().notificar(notificacao);
            }
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public ServicoCompravelFacade getServicoCompravelFacade() {
        return servicoCompravelFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public SolicitacaoReposicaoEstoqueFacade getSolicitacaoReposicaoEstoqueFacade() {
        return solicitacaoReposicaoEstoqueFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public CotacaoFacade getCotacaoFacade() {
        return cotacaoFacade;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public AmparoLegalFacade getAmparoLegalFacade() {
        return amparoLegalFacade;
    }

    public PlanoContratacaoAnualObjetoCompraFacade getPlanoContratacaoAnualObjetoCompraFacade() {
        return planoContratacaoAnualObjetoCompraFacade;
    }

    public SolicitacaoMaterialDocumentoOficial salvarNovoDocumentoOficial(TipoDoctoOficial tipoDoctoOficial, SolicitacaoMaterial selecionado) throws AtributosNulosException, UFMException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipoDoctoOficial);
        DocumentoOficial documentoOficial = documentoOficialFacade.geraNovoDocumento(selecionado, mod, null, null, null, null);
        SolicitacaoMaterialDocumentoOficial smdo = em.merge(new SolicitacaoMaterialDocumentoOficial(documentoOficial, selecionado, getUltimaVersaoPorTipoDoctoOficial(tipoDoctoOficial, selecionado) + 1));
        return smdo;
    }

    public Integer getUltimaVersaoPorTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial, SolicitacaoMaterial selecionado) {
        Integer retorno = 0;
        for (SolicitacaoMaterialDocumentoOficial smDocOficial : selecionado.getDocumentosOficiais()) {
            if (tipoDoctoOficial.equals(smDocOficial.getDocumentoOficial().getModeloDoctoOficial().getTipoDoctoOficial()) &&
                smDocOficial.getVersao().compareTo(retorno) > 0) {
                retorno = smDocOficial.getVersao();
            }
        }
        return retorno;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public ConfigGrupoMaterialFacade getConfigGrupoMaterialFacade() {
        return configGrupoMaterialFacade;
    }

    public ConfigDespesaBensFacade getConfigDespesaBensFacade() {
        return configDespesaBensFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }
}
