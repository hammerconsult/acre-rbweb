package br.com.webpublico.negocios.administrativo.patrimonio;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.administrativo.patrimonio.LoteReavaliacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.ReavaliacaoBem;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoDaSolicitacao;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
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

@Stateless
public class LoteReavaliacaoBemFacade extends AbstractFacade<LoteReavaliacaoBem> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private BemFacade bemFacade;
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

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
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

    public LoteReavaliacaoBemFacade() {
        super(LoteReavaliacaoBem.class);
    }

    @Override
    public LoteReavaliacaoBem recuperar(Object id) {
        LoteReavaliacaoBem lote = em.find(LoteReavaliacaoBem.class, id);
        lote.getReavaliacaoBens().size();
        if (lote.getDetentorArquivoComposicao() != null) {
            lote.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        atribuirHierarquiaAoLoteRevaliacaoBem(lote);
        return lote;
    }

    public LoteReavaliacaoBem recuperarSimples(Object id) {
        LoteReavaliacaoBem lote = em.find(LoteReavaliacaoBem.class, id);
        if (lote.getDetentorArquivoComposicao() != null) {
            lote.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        atribuirHierarquiaAoLoteRevaliacaoBem(lote);
        return lote;
    }

    public void atribuirHierarquiaAoLoteRevaliacaoBem(LoteReavaliacaoBem lote) {
        HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), lote.getUnidadeOrigem(), sistemaFacade.getDataOperacao());
        if (hierarquia != null) {
            lote.setHierarquiaOrganizacional(hierarquia);
        }
    }

    private List<ReavaliacaoBem> criarReavaliacaoBem(List<Bem> bens, LoteReavaliacaoBem lote, AssistenteMovimentacaoBens assistente, SituacaoEventoBem situacaoEventoBem) {
        List<ReavaliacaoBem> lista = new ArrayList<>();
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Reavaliação de Bens...");
        assistente.setTotal(bens.size());
        for (Bem bem : bens) {
            EstadoBem ultimoEstadoBem = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstadoBem));
            Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
            ReavaliacaoBem reavaliacaoBem = new ReavaliacaoBem(bem, lote, ultimoEstadoBem, estadoResultante, dataLancamento);
            reavaliacaoBem.setValor(bem.getValorAjuste());
            reavaliacaoBem.setSituacaoEventoBem(situacaoEventoBem);
            lista.add(reavaliacaoBem);
            assistente.conta();
        }
        return lista;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<LoteReavaliacaoBem> salvarReavaliacao(LoteReavaliacaoBem entity, List<Bem> bens, AssistenteMovimentacaoBens assistente) {
        try {
            desbloquearBens(entity, assistente.getConfigMovimentacaoBem());
            entity.setSituacaoReavaliacaoBem(SituacaoDaSolicitacao.EM_ELABORACAO);
            entity.setDataVersao(new Date());
            List<ReavaliacaoBem> reavaliacoes = criarReavaliacaoBem(bens, entity, assistente, SituacaoEventoBem.EM_ELABORACAO);
            entity.setReavaliacaoBens(reavaliacoes);

            bloquearBens(bens, assistente.getConfigMovimentacaoBem());

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Persistindo Registros...");
            entity = em.merge(entity);
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao salvar solicitação de reavaliação de bens. " + ex.getMessage());
            throw ex;
        }
        return new AsyncResult<>(entity);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<LoteReavaliacaoBem> concluirReavaliacao(LoteReavaliacaoBem entity, List<Bem> bens, AssistenteMovimentacaoBens assistente) {
        try {
            desbloquearBens(entity, assistente.getConfigMovimentacaoBem());
            List<ReavaliacaoBem> reavaliacoes = criarReavaliacaoBem(bens, entity, assistente, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
            entity.setReavaliacaoBens(reavaliacoes);


            List<Number> bensBloqueio = Lists.newArrayList();
            for (Bem bem : bens) {
                bensBloqueio.add(bem.getId());
            }
            configMovimentacaoBemFacade.bloquearBensPorSituacao(assistente.getConfigMovimentacaoBem(), bensBloqueio, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);

            entity.setSituacaoReavaliacaoBem(SituacaoDaSolicitacao.AGUARDANDO_EFETIVACAO);
            entity.setDataVersao(new Date());
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(LoteReavaliacaoBem.class, "codigo"));
            }
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Persistindo Registros...");
            entity = em.merge(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao concluir solicitação de reavaliação de bens. " + ex.getMessage());
            throw ex;
        }
        return new AsyncResult<>(entity);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<Bem>> pesquisarBens(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente) {
        assistente.setDescricaoProcesso("Pesquisando bens da unidade: " + filtro.getHierarquiaAdministrativa() + "...");
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ")
            .append("    bem.id as idBem, ")
            .append("    bem.identificacao, ")
            .append("    bem.descricao, ")
            .append("    estb.id as idEstado, ")
            .append("    bem.dataaquisicao as dataAquisicao, ")
            .append("    vworc.codigo || ' - ' || vworc.descricao as unidade, ")
            .append("    0 as valorAjuste  ")
            .append("  from eventobem eb   ")
            .append("    inner join estadobem estb on estb.id = eb.estadoresultante_id ")
            .append("    inner join vwhierarquiaorcamentaria vworc on estb.detentoraorcamentaria_id = vworc.subordinada_id ")
            .append("    inner join vwhierarquiaadministrativa vwadm on estb.detentoraadministrativa_id = vwadm.subordinada_id ")
            .append("    inner join grupobem gb on estb.grupobem_id = gb.id  ")
            .append("    inner join bem on bem.id = eb.bem_id ")
            .append("    inner join movimentobloqueiobem mov on mov.bem_id = bem.id  ")
            .append(" where gb.tipobem = :tipoBem ")
            .append("    and vwadm.subordinada_id = :idUnidadeAdm ")
            .append("    and estb.estadoBem <> :baixado ")
            .append("    and eb.dataoperacao between vworc.iniciovigencia and coalesce(vworc.fimvigencia, eb.dataoperacao)  ")
            .append("    and eb.dataoperacao between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, eb.dataoperacao)  ")
            .append("    and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where bem.id = mov2.bem_id) ")
            .append("    and eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = bem.id)");
        sql.append(FiltroPesquisaBem.getCondicaoBloqueioPesquisaBem(assistente.getConfigMovimentacaoBem()));
        sql.append(filtro.getHierarquiaOrcamentaria() != null ? " and vworc.subordinada_id = :idUnidadeOrc" : "");
        sql.append(filtro.getGrupoBem() != null ? " and gb.id = :idGrupoBem" : "");
        sql.append(filtro.getBem() != null ? " and bem.id = :idBem" : "");
        sql.append(" order by to_number(bem.IDENTIFICACAO), bem.DESCRICAO ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idUnidadeAdm", filtro.getHierarquiaAdministrativa().getSubordinada().getId());
        q.setParameter("tipoBem", filtro.getTipoBem().name());
        q.setParameter("baixado", EstadoConservacaoBem.BAIXADO.name());
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, assistente.getConfigMovimentacaoBem());
        if (filtro.getHierarquiaOrcamentaria() != null && filtro.getHierarquiaOrcamentaria().getSubordinada() != null) {
            q.setParameter("idUnidadeOrc", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        }
        if (filtro.getGrupoBem() != null) {
            q.setParameter("idGrupoBem", filtro.getGrupoBem().getId());
        }
        if (filtro.getBem() != null) {
            q.setParameter("idBem", filtro.getBem().getId());
        }
        List<Bem> bensPesquisados = preencherListaDeBensApartirArrayObject(q.getResultList());
        List<Bem> bensDisponiveis = configMovimentacaoBemFacade.validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);
        if (assistente.isOperacaoEditar()) {
            assistente.setBensSalvos(pesquisarBensVinculadoAoItemReavaliacaoBem((LoteReavaliacaoBem) assistente.getSelecionado(), assistente));
        } else {
            assistente.setBensSalvos(bensDisponiveis);
        }
        configMovimentacaoBemFacade.verificarBensMoveisEmBloqueioParaMovimentacao(filtro, assistente);
        return new AsyncResult<List<Bem>>(bensDisponiveis);
    }

    public List<Bem> pesquisarBensVinculadoAoItemReavaliacaoBem(LoteReavaliacaoBem entity, AssistenteMovimentacaoBens assistente) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ")
            .append("    bem.id as idBem, ")
            .append("    bem.identificacao, ")
            .append("    bem.descricao, ")
            .append("    est.id as idEstado, ")
            .append("    bem.dataaquisicao as dataAquisicao, ")
            .append("    vworc.codigo || ' - ' || vworc.descricao as unidade, ")
            .append("    item.valor as valorAjuste  ")
            .append(" from reavaliacaobem item ")
            .append("   inner join eventobem ev on ev.id = item.id ")
            .append("   inner join estadobem est on est.id = ev.estadoresultante_id ")
            .append("   inner join bem on bem.id = ev.bem_id ")
            .append("   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = est.detentoraorcamentaria_id ")
            .append("         and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = est.detentoraadministrativa_id   ")
            .append("    and to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))   ")
            .append(" where item.loteReavaliacaoBem_id = :idSolicitacao ")
            .append(" order by to_number(bem.identificacao) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idSolicitacao", entity.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(entity.getDataHoraCriacao()));
        List<Bem> bensPesquisados = preencherListaDeBensApartirArrayObject(q.getResultList());
        List<Bem> bensDisponiveis = configMovimentacaoBemFacade.validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);
        return bensDisponiveis;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<ReavaliacaoBem> buscarItensDaSolicitacao(LoteReavaliacaoBem selecionado) {
        StringBuilder sql = new StringBuilder();
        sql.append("   select item.*, ev.* from reavaliacaobem item ")
            .append("    inner join eventobem ev on ev.id = item.id ")
            .append("  where item.loteReavaliacaoBem_id = :idSolicitacao");
        Query q = em.createNativeQuery(sql.toString(), ReavaliacaoBem.class);
        q.setParameter("idSolicitacao", selecionado.getId());
        return q.getResultList();
    }

    private List<Bem> preencherListaDeBensApartirArrayObject(List<Object[]> objetosDaConsulta) {
        List<Bem> retornaBens = new ArrayList<>();
        for (Object[] obj : objetosDaConsulta) {
            Bem b = bemFacade.recuperarSemDependencias(((BigDecimal) obj[0]).longValue());
            b.setIdUltimoEstado(((BigDecimal) obj[3]).longValue());
            b.setOrcamentaria((String) obj[5]);
            b.setValorAjuste((BigDecimal) obj[6]);
            retornaBens.add(b);
        }
        for (Bem bem : retornaBens) {
            try {
                EstadoBem ultimoEstadoBem = bemFacade.recuperarEstadoBemPorId(bem.getIdUltimoEstado());
                Bem.preencherDadosTrasientsDoBem(bem, ultimoEstadoBem);
                bem.setValorAjustadoBem(bem.getValorLiquido());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return retornaBens;
    }

    private void desbloquearBens(LoteReavaliacaoBem entity, ConfigMovimentacaoBem configuracao) {
        if (entity.getId() != null) {
            List<Number> bensRecuperados = buscarIdsBemPorSolicitacaoReavaliacaoBens(entity);
            if (bensRecuperados != null && !bensRecuperados.isEmpty()) {
                configMovimentacaoBemFacade.desbloquearBens(configuracao, bensRecuperados);
            }
        }
    }

    public List<Number> buscarIdsBemPorSolicitacaoReavaliacaoBens(LoteReavaliacaoBem entity) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ev.bem_id from reavaliacaobem item ")
            .append("   inner join eventobem ev on ev.id = item.id ")
            .append(" where item.loteReavaliacaoBem_id = :isSolicitacao");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("isSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public void remover(LoteReavaliacaoBem entity, ConfigMovimentacaoBem configuracao) {
        desbloquearBens(entity, configuracao);
        super.remover(entity);
    }

    private void bloquearBens(List<Bem> bensSelecionados, ConfigMovimentacaoBem configMovimentacaoBem) {
        List<Number> bensBloqueio = Lists.newArrayList();
        for (Bem bem : bensSelecionados) {
            bensBloqueio.add(bem.getId());
        }
        configMovimentacaoBemFacade.bloquearBens(configMovimentacaoBem, bensBloqueio);
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}
