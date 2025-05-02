package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SituacaoLoteCessao;
import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 05/05/14
 * Time: 13:53
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class LoteCessaoFacade extends AbstractFacade<LoteCessao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private CessaoDevolucaoFacade cessaoDevolucaoFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;

    public LoteCessaoFacade() {
        super(LoteCessao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public LoteCessao recuperar(Object id) {
        LoteCessao loteCessao = super.recuperar(id);
        loteCessao.getListaDeCessoes().size();
        loteCessao.getListaDePrazos().size();
        loteCessao.getListaDeSolicitacaoProrrogacaoCessao().size();
        if (loteCessao.getDetentorArquivoComposicao() != null) {
            loteCessao.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return loteCessao;
    }

    public LoteCessao recuperarComDependencias(Object id) {
        LoteCessao loteCessao = super.recuperar(id);
        loteCessao.getListaDePrazos().size();
        loteCessao.getListaDeSolicitacaoProrrogacaoCessao().size();
        if (loteCessao.getDetentorArquivoComposicao() != null) {
            loteCessao.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return loteCessao;
    }

    public List<LoteCessao> getLotesNaoAvaliadosPorUnidadeAndResponsavel(UnidadeOrganizacional unidadeOrganizacional, TipoCessao tipoCessao) {
        String sql = "select lote.*" +
            "       from lotecessao lote" +
            "      where lote.situacaolotecessao = :situacao";
        sql += TipoCessao.INTERNO.equals(tipoCessao) ? " and lote.unidadedestino_id = :unidade_id " : " and lote.unidadeOrigem_id = :unidade_id ";
        sql += "  and lote.tipocessao = :tipoCessao";
        sql += "   order by lote.codigo";

        Query q = em.createNativeQuery(sql, LoteCessao.class);
        q.setParameter("unidade_id", unidadeOrganizacional.getId());
        q.setParameter("tipoCessao", tipoCessao.name());
        q.setParameter("situacao", SituacaoLoteCessao.AGUARDANDO_EFETIVACAO.name());

        return q.getResultList();
    }

    public List<Cessao> getListaDeCessaoPorLote(LoteCessao lote) {
        return em.createQuery("select ce " +
            "                from Cessao ce" +
            "               where loteCessao = :lote" +
            "            order by ce.bem.descricao").setParameter("lote", lote).getResultList();
    }

    public List<LoteCessao> completarLoteCessaoQueNaoPossuiSolicitacaoProrrogacaoParaAvaliar(String filtro, TipoCessao tipoCessao) {
        String sql = " SELECT distinct LOTE.*" +
            "        FROM LOTECESSAO LOTE" +
            "  INNER JOIN CESSAO CS ON CS.LOTECESSAO_ID = LOTE.ID";
        sql += TipoCessao.INTERNO.equals(tipoCessao) ? "  INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = LOTE.UNIDADEDESTINO_ID " : "  INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = LOTE.UNIDADEORIGEM_ID ";
        sql += "  INNER JOIN USUARIOUNIDADEORGANIZACIO UU ON UU.UNIDADEORGANIZACIONAL_ID = UO.ID " +
            "  INNER JOIN EFETIVACAOCESSAO EF ON EF.CESSAO_ID = CS.ID " +
            "       WHERE LOTE.SITUACAOLOTECESSAO = :situacao" +
            "         AND UU.USUARIOSISTEMA_ID = :USUARIO_ID " +
            "         AND UU.GESTORPATRIMONIO = :SIM " +
            "         and lote.tipocessao = :tipoCessao " +
            "         AND LOTE.ID NOT IN (SELECT LOTE.ID " +
            "                             FROM lotecessao lote " +
            "                              INNER JOIN SOLICITAPRORROGACAOCESSAO SOLICITACAO ON SOLICITACAO.LOTECESSAO_ID = lote.ID " +
            "                              LEFT JOIN AVALISOLIPRORROGACAOCESSAO AVALIACAO ON AVALIACAO.SOLICITAPRORROGACAOCESSAO_ID = SOLICITACAO.ID " +
            "                               WHERE AVALIACAO.ID IS NULL " +
            "                            )" +
            "         AND NOT EXISTS(Select 1 from CessaoDevolucao s where s.cessao_id = cs.id)" +
            "         and ((to_char(lote.codigo) like :filtro) or (lower(lote.descricao) like :filtro)) ";
        Query q = em.createNativeQuery(sql, LoteCessao.class);
        q.setParameter("situacao", SituacaoLoteCessao.ACEITA.name());
        q.setParameter("tipoCessao", tipoCessao.name());
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("USUARIO_ID", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("SIM", Boolean.TRUE);
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public void remover(LoteCessao entity, ConfigMovimentacaoBem configuracao) {
        desbloquearBens(entity, configuracao);
        super.remover(entity);
    }

    private void desbloquearBens(LoteCessao entity, ConfigMovimentacaoBem configuracao) {
        if (entity.getId() != null) {
            List<Number> bensRecuperados = buscarIdsBemPorCessao(entity);
            if (!bensRecuperados.isEmpty()) {
                configMovimentacaoBemFacade.desbloquearBens(configuracao, bensRecuperados);
            }
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<Bem>> pesquisarBens(FiltroPesquisaBem filtroPesquisaBem, AssistenteMovimentacaoBens assistente) {
        assistente.setDescricaoProcesso("Pesquisando bens da unidade: " + filtroPesquisaBem.getHierarquiaAdministrativa() + "...");
        String sql = " " +
            " select distinct " +
            "   bem.id," +
            "   bem.identificacao," +
            "   bem.descricao," +
            "   vwadm.codigo || ' - ' || vwadm.descricao as unidade, " +
            "   est.id as estado," +
            "   bem.dataAquisicao as dataAquisicao " +
            " from bem " +
            "  inner join eventobem ev on ev.bem_id = bem.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "  inner join grupobem gb on gb.id = est.grupobem_id " +
            "  inner join movimentobloqueiobem mov on mov.bem_id = bem.id " +
            "  inner join vwhierarquiaorcamentaria vworc on est.detentoraOrcamentaria_id = vworc.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  inner join vwhierarquiaadministrativa vwadm on est.detentoraadministrativa_id = vwadm.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        sql += " where est.detentoraAdministrativa_id = :idUnidadeAdm " +
            "        and gb.tipobem = :tipoBem " +
            "        and est.estadobem <> :baixado" +
            "        and ev.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = ev.bem_id) " +
            "        and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where  bem.id = mov2.bem_id) ";
        sql += FiltroPesquisaBem.getCondicaoBloqueioPesquisaBem(assistente.getConfigMovimentacaoBem());
        if (filtroPesquisaBem.getGrupoBem() != null) {
            sql += " and gb.id = :idGrupo ";
        }
        if (filtroPesquisaBem.getBem() != null) {
            sql += " and bem.id = :idBem ";
        }
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoBem", filtroPesquisaBem.getTipoBem().name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtroPesquisaBem.getDataOperacao()));
        q.setParameter("idUnidadeAdm", filtroPesquisaBem.getHierarquiaAdministrativa().getSubordinada().getId());
        q.setParameter("baixado", EstadoConservacaoBem.BAIXADO.name());
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, assistente.getConfigMovimentacaoBem());
        if (filtroPesquisaBem.getGrupoBem() != null) {
            q.setParameter("idGrupo", filtroPesquisaBem.getGrupoBem().getId());
        }
        if (filtroPesquisaBem.getBem() != null) {
            q.setParameter("idBem", filtroPesquisaBem.getBem().getId());
        }
        List<Bem> bensPesquisados = preencherListaDeBensApartirArrayObject(q.getResultList());
        List<Bem> bensDisponiveis = configMovimentacaoBemFacade.validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);
        if (assistente.isOperacaoEditar()) {
            assistente.setBensSalvos(pesquisarBensVinculadosACessao((LoteCessao) assistente.getSelecionado(), assistente));
        } else {
            assistente.setBensSalvos(bensDisponiveis);
        }
        configMovimentacaoBemFacade.verificarBensMoveisEmBloqueioParaMovimentacao(filtroPesquisaBem, assistente);
        return new AsyncResult<List<Bem>>(bensDisponiveis);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<Bem> pesquisarBensVinculadosACessao(LoteCessao entity, AssistenteMovimentacaoBens assistente) {
        String sql = " " +
            " select distinct " +
            "   bem.id," +
            "   bem.identificacao," +
            "   bem.descricao," +
            "   vwadm.codigo || ' - ' || vwadm.descricao as unidade, " +
            "   est.id as estado," +
            "   bem.dataAquisicao as dataAquisicao " +
            " from bem " +
            "  inner join eventobem ev on ev.bem_id = bem.id " +
            "  inner join cessao cs on cs.id = ev.id " +
            "  inner join lotecessao lote on lote.id = cs.lotecessao_id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "  inner join grupobem gb on gb.id = est.grupobem_id " +
            "  inner join vwhierarquiaorcamentaria vworc on est.detentoraOrcamentaria_id = vworc.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  inner join vwhierarquiaadministrativa vwadm on est.detentoraadministrativa_id = vwadm.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  where lote.id = :idLoteCessao  " +
            "  order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteCessao", entity.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(assistente.getDataLancamento()));
        List<Bem> bensPesquisados = preencherListaDeBensApartirArrayObject(q.getResultList());
        List<Bem> bensDisponiveis = configMovimentacaoBemFacade.validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);
        return bensDisponiveis;
    }

    private List<Bem> preencherListaDeBensApartirArrayObject(List<Object[]> objetosDaConsulta) {
        List<Bem> retornaBens = new ArrayList<>();
        for (Object[] bem : objetosDaConsulta) {
            BigDecimal id = (BigDecimal) bem[0];
            Bem b = bemFacade.recuperarSemDependencias(id.longValue());
            b.setAdministrativa((String) bem[3]);
            b.setIdUltimoEstado(((BigDecimal) bem[4]).longValue());
            retornaBens.add(b);
        }
        for (Bem bem : retornaBens) {
            try {
                EstadoBem ultimoEstado = bemFacade.recuperarEstadoBemPorId(bem.getIdUltimoEstado());
                Bem.preencherDadosTrasientsDoBem(bem, ultimoEstado);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return retornaBens;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public LoteCessao salvarRetornando(LoteCessao entity, List<Bem> bensSelecionados, AssistenteMovimentacaoBens assistente) {
        try {
            if (assistente.isOperacaoEditar()) {
                desbloquearBens(entity, assistente.getConfigMovimentacaoBem());
            }
            criarCessao(bensSelecionados, entity, assistente);
            atribuirInstanciaAtualizadaDoLoteCessaoNoPrazo(entity);

            entity = em.merge(entity);

            List<Number> bensBloqueio = Lists.newArrayList();
            for (Bem bem : bensSelecionados) {
                bensBloqueio.add(bem.getId());
            }
            configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), bensBloqueio);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Persistindo Registros...");
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso("");
        } catch (Exception ex) {
            logger.error("Erro ao salvar cessão do bem. ", ex);
            throw ex;
        }
        return entity;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public LoteCessao concluirCessao(LoteCessao entity, AssistenteMovimentacaoBens assistente) {
        try {
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Concluíndo Solicitação de Cessão de Bens...");
            assistente.setTotal(entity.getListaDeCessoes().size());

            entity.setSituacaoLoteCessao(SituacaoLoteCessao.AGUARDANDO_EFETIVACAO);
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(LoteCessao.class, "codigo"));
            }
            entity = em.merge(entity);

            concluirEventoCessaoBens(entity, assistente);

            assistente.zerarContadoresProcesso();
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao concluir cessão do bem. ", ex);
        }
        return entity;
    }

    private void concluirEventoCessaoBens(LoteCessao entity, AssistenteMovimentacaoBens assistente) {
        List<Number> bensBloqueio = Lists.newArrayList();
        for (Cessao item : entity.getListaDeCessoes()) {
            bemFacade.atualizarSituacaoEventoBem(item.getId(), SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
            bensBloqueio.add(item.getBem().getId());
            assistente.conta();
        }
        configMovimentacaoBemFacade.bloquearBensPorSituacao(assistente.getConfigMovimentacaoBem(), bensBloqueio, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
    }

    private void criarCessao(List<Bem> bens, LoteCessao entity, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Cessão de Bens...");
        assistente.setTotal(bens.size());

        entity.setListaDeCessoes(Lists.newArrayList());
        for (Bem bem : bens) {
            EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem estadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
            estadoResultante = em.merge(estadoResultante);

            Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
            Cessao cessao = new Cessao(bem, entity, ultimoEstado, estadoResultante, dataLancamento);
            cessao.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
            if (entity.getDetentorArquivoComposicao() != null) {
                cessao.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
            }
            entity.getListaDeCessoes().add(cessao);
            assistente.conta();
        }
    }

    private void atribuirInstanciaAtualizadaDoLoteCessaoNoPrazo(LoteCessao lote) {
        for (PrazoCessao prazoCessao : lote.getListaDePrazos()) {
            prazoCessao.setLoteCessao(lote);
        }
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public List<Number> buscarIdsBemPorCessao(LoteCessao entity) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from cessao item " +
            "   inner join eventobem ev on ev.id = item.id " +
            " where item.lotecessao_id = :idLote ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<LoteCessao> buscarLoteCessaoQueAindaTemBemParaDevolver(String filtro) {
        String sql =
            " SELECT DISTINCT lt.*  " +
                " FROM LOTECESSAO LT " +
                " INNER JOIN CESSAO CS ON CS.LOTECESSAO_ID = LT.ID " +
                " INNER JOIN USUARIOUNIDADEORGANIZACIO UU ON UU.UNIDADEORGANIZACIONAL_ID = lt.unidadeOrigem_id " +
                "WHERE NOT EXISTS(SELECT 1 FROM CESSAODEVOLUCAO SD WHERE SD.CESSAO_ID = CS.ID) " +
                " AND EXISTS (SELECT 1 FROM EFETIVACAOCESSAO EF WHERE EF.CESSAO_ID = CS.ID) " +
                " and LT.SITUACAOLOTECESSAO = :situacao" +
                " and ((to_char(lt.codigo) like :filtro) or (lower(lt.descricao) like :filtro)) " +
                " AND UU.GESTORPATRIMONIO = :GESTOR " +
                " AND UU.USUARIOSISTEMA_ID = :USUARIO_ID " +
                " order by lt.codigo";
        Query q = em.createNativeQuery(sql, LoteCessao.class);
        q.setParameter("situacao", SituacaoLoteCessao.ACEITA.name());
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("GESTOR", Boolean.TRUE);
        q.setParameter("USUARIO_ID", sistemaFacade.getUsuarioCorrente().getId());
        return q.getResultList();
    }

    public List<Cessao> buscarCessaoParaDevolucao(LoteCessao loteCessao) {
        String hql = " select cs " +
            "            from Cessao cs where cs.loteCessao = :lote " +
            "         and not exists(select cd " +
            "                          from CessaoDevolucao cd " +
            "                        where cd.cessao = cs" +
            "                          and cd.situacaoEventoBem = :finalizado) ";
        Query q = em.createQuery(hql, Cessao.class);
        q.setParameter("lote", loteCessao);
        q.setParameter("finalizado", SituacaoEventoBem.FINALIZADO);
        return q.getResultList();
    }

    public List<Cessao> buscarCessaoComDevolucao(LoteCessao loteCessao) {
        String hql = " select cs " +
            "            from Cessao cs where cs.loteCessao = :lote " +
            "         and exists(select cd " +
            "                      from CessaoDevolucao cd " +
            "                    where cd.cessao = cs" +
            "                      and cd.situacaoEventoBem = :finalizado ) ";
        Query q = em.createQuery(hql, Cessao.class);
        q.setParameter("lote", loteCessao);
        q.setParameter("finalizado", SituacaoEventoBem.FINALIZADO);
        return q.getResultList();
    }

    public Boolean hasCessaoDevolucao(Cessao cessao) {
        String hql = " select cd " +
            "            from CessaoDevolucao cd " +
            "          where cd.cessao = :cs" +
            "            and cd.situacaoEventoBem = :finalizado ";
        Query q = em.createQuery(hql, CessaoDevolucao.class);
        q.setParameter("cs", cessao);
        q.setParameter("finalizado", SituacaoEventoBem.FINALIZADO);
        return !q.getResultList().isEmpty();
    }

    public Boolean hasCessaoDevolvida(Number idCessao) {
        String sql = "  select * from cessaodevolucao where cessao_id = :idCessao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCessao", idCessao);
        return !q.getResultList().isEmpty();
    }


    public void verificarSeUnidadePossuiSequenciaPropria(UnidadeOrganizacional unidadeOrganizacional, ParametroPatrimonio parametroPatrimonio, LoteCessao selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.isInterno() && verificarSeUnidadePossuiSequenciaPropria(unidadeOrganizacional, parametroPatrimonio)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade: " + unidadeOrganizacional + " segue sequência própria para geração de código de identificação móvel."
                + " Dessa forma, só será possível realizar cessão de bem externa.");

        }
        ve.lancarException();
    }

    private boolean verificarSeUnidadePossuiSequenciaPropria(UnidadeOrganizacional unidadeOrganizacional, ParametroPatrimonio parametroPatrimonio) {
        Entidade entidade = bemFacade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(unidadeOrganizacional);

        for (EntidadeGeradoraIdentificacaoPatrimonio entidadeGeradora : parametroPatrimonio.getEntidadeGeradoras()) {
            if (entidadeGeradora.getEntidade().equals(entidade) && entidadeGeradora.ehSequenciaPropria()) {
                return true;
            }
        }
        return false;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public CessaoDevolucaoFacade getCessaoDevolucaoFacade() {
        return cessaoDevolucaoFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}
