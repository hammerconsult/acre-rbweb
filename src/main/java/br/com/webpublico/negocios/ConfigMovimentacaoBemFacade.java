package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.ConfigMovimentacaoBemPesquisa;
import br.com.webpublico.entidades.MovimentoBloqueioBem;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.BensMoveisBloqueio;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.administrativo.AgrupadorMovimentoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.administrativo.dao.JdbcMovimentoBloqueioBem;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Stateless
public class ConfigMovimentacaoBemFacade extends AbstractFacade<ConfigMovimentacaoBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private BemFacade bemFacade;
    private JdbcMovimentoBloqueioBem jdbcMovimentoBloqueioBem;

    public ConfigMovimentacaoBemFacade() {
        super(ConfigMovimentacaoBem.class);
    }

    @PostConstruct
    public void init() {
        jdbcMovimentoBloqueioBem = (JdbcMovimentoBloqueioBem) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcMovimentoBloqueioBem");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void encerrarVigencia(ConfigMovimentacaoBem entity) {
        super.salvar(entity);
    }

    @Override
    public void salvar(ConfigMovimentacaoBem entity) {
        atualizarConfiguracaoPesquisa(entity);
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(ConfigMovimentacaoBem entity) {
        criarConfiguracaoPesquisa(entity);
        super.salvarNovo(entity);
    }

    public void salvarMovimentoBloqueio(MovimentoBloqueioBem movimentoBloqueioBem) {
        em.merge(movimentoBloqueioBem);
    }

    private void criarConfiguracaoPesquisa(ConfigMovimentacaoBem entity) {
        ConfigMovimentacaoBemPesquisa pesquisa = new ConfigMovimentacaoBemPesquisa();
        if (entity.getOperacaoMovimentacaoBem().isOperacaoMovimentoUm()) {
            pesquisa.setMovimentoTipoUm(true);
            pesquisa.setMovimentoTipoDois(true);
        }
        if (entity.getOperacaoMovimentacaoBem().isOperacaoMovimentoDois()) {
            pesquisa.setMovimentoTipoDois(true);
        }
        if (entity.getOperacaoMovimentacaoBem().isOperacaoMovimentoTres()) {
            pesquisa.setMovimentoTipoTres(true);
        }
        entity.setPesquisa(pesquisa);
    }

    private void atualizarConfiguracaoPesquisa(ConfigMovimentacaoBem entity) {
        if (entity.getOperacaoMovimentacaoBem().isOperacaoMovimentoUm()) {
            entity.getPesquisa().setMovimentoTipoUm(true);
            entity.getPesquisa().setMovimentoTipoDois(true);
        }
        if (entity.getOperacaoMovimentacaoBem().isOperacaoMovimentoDois()) {
            entity.getPesquisa().setMovimentoTipoDois(true);
        }
        if (entity.getOperacaoMovimentacaoBem().isOperacaoMovimentoTres()) {
            entity.getPesquisa().setMovimentoTipoTres(true);
        }
    }

    public void deletarMovimentoBloqueioBem(ConfigMovimentacaoBem configuracao, List<Number> bens) {
        for (Number bem : bens) {
            MovimentoBloqueioBem movimentoAtual = buscarUltimoMovimentoPorAgrupadorAndSituacao(bem, configuracao.getOperacaoMovimentacaoBem().getAgrupadorMovimentoBem());
            if (movimentoAtual != null) {
                deletarMovimentoBloqueio(movimentoAtual);
            }
            jdbcMovimentoBloqueioBem.insertBemPortal(bem);
        }
    }

    public void desbloquearBensRejeicaoDuranteProcesso(ConfigMovimentacaoBem configuracao, List<Number> bens) {
        for (Number bem : bens) {
            MovimentoBloqueioBem movimentoAnterior = buscarUltimoMovimentoPorAgrupadorAndSituacao(bem, configuracao.getOperacaoMovimentacaoBem().getAgrupadorMovimentoBem());
            deletarMovimentoBloqueio(movimentoAnterior);
            jdbcMovimentoBloqueioBem.insertBemPortal(bem);
        }
    }

    public void desbloquearBensEfetivacaoBaixa(ConfigMovimentacaoBem configMovAtual, List<Number> bens) {
        for (Number bem : bens) {
            deletarMovimentoBloqueoPorAgrupador(configMovAtual.getOperacaoMovimentacaoBem().getAgrupadorMovimentoBem(), bem);
            deletarMovimentoBloqueoPorAgrupador(AgrupadorMovimentoBem.ALIENACAO, bem);
            jdbcMovimentoBloqueioBem.insertBemPortal(bem);
        }
    }

    public void desbloquearBens(ConfigMovimentacaoBem configMovAtual, List<Number> bens) {
        for (Number bem : bens) {
            deletarMovimentoBloqueoPorAgrupador(configMovAtual.getOperacaoMovimentacaoBem().getAgrupadorMovimentoBem(), bem);
            jdbcMovimentoBloqueioBem.insertBemPortal(bem);
        }
    }

    public void desbloquearBensAssincrono(AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Desbloqueando Bens para Movimentação...");
        assistente.setTotal(assistente.getIdsBensBloqueados().size());

        for (Number bem : assistente.getIdsBensBloqueados()) {
            deletarMovimentoBloqueoPorAgrupador(assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getAgrupadorMovimentoBem(), bem);
            assistente.conta();
            jdbcMovimentoBloqueioBem.insertBemPortal(bem);
        }
    }

    public void desbloquearBensJDBCAssincrono(AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Desbloqueando Bens para Movimentação...");
        assistente.setTotal(assistente.getIdsBensBloqueados().size());

        for (Number bem : assistente.getIdsBensBloqueados()) {
            jdbcMovimentoBloqueioBem.deletarMovimentoBloqueioBem(assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getAgrupadorMovimentoBem(), bem);
            jdbcMovimentoBloqueioBem.insertBemPortal(bem);
            assistente.conta();
        }
    }

    public void desbloquearBensJDBC(ConfigMovimentacaoBem configruacao, List<Number> bens) {
        for (Number bem : bens) {
            jdbcMovimentoBloqueioBem.deletarMovimentoBloqueioBem(configruacao.getOperacaoMovimentacaoBem().getAgrupadorMovimentoBem(), bem);
            jdbcMovimentoBloqueioBem.insertBemPortal(bem);
        }
    }

    public void bloquearBensPorSituacao(ConfigMovimentacaoBem configMovimentacaoBem, List<Number> bens, SituacaoEventoBem situacao) {
        bloquearBens(configMovimentacaoBem, bens, situacao);
    }

    public void bloquearBensPorSituacaoJDBC(ConfigMovimentacaoBem configMovimentacaoBem, List<Number> bens, SituacaoEventoBem situacaoEventoBem) {
        for (Number bem : bens) {
            jdbcMovimentoBloqueioBem.insertMovimentoBloqueioBem(configMovimentacaoBem, bem, situacaoEventoBem);
            jdbcMovimentoBloqueioBem.insertBemPortal(bem);
        }
    }

    public void bloquearBens(ConfigMovimentacaoBem configMovimentacaoBem, List<Number> bens) {
        bloquearBens(configMovimentacaoBem, bens, null);
    }

    private void bloquearBens(ConfigMovimentacaoBem configMovimentacaoBem, List<Number> bens, SituacaoEventoBem situacaoEventoBem) {
        for (Number bem : bens) {
            OperacaoMovimentacaoBem operacaoAtual = configMovimentacaoBem.getOperacaoMovimentacaoBem();
            insertMovimentoBloqueioBem(configMovimentacaoBem, bem, situacaoEventoBem != null ? situacaoEventoBem : OperacaoMovimentacaoBem.getSituacaoPorOperacao(operacaoAtual));
            jdbcMovimentoBloqueioBem.insertBemPortal(bem);
        }
    }

    public void bloquearUnicoBem(ConfigMovimentacaoBem configMovimentacaoBem, Number idBem) {
        insertMovimentoBloqueioBem(configMovimentacaoBem, idBem, OperacaoMovimentacaoBem.getSituacaoPorOperacao(configMovimentacaoBem.getOperacaoMovimentacaoBem()));
        jdbcMovimentoBloqueioBem.insertBemPortal(idBem);
    }

    public void bloquearBensLiquidacao(List<Bem> bens) {
        if (!bens.isEmpty()) {
            for (Bem bem : bens) {
                MovimentoBloqueioBem novoMovimento = new MovimentoBloqueioBem(new Date(), OperacaoMovimentacaoBem.AQUISICAO_BEM, bem, SituacaoEventoBem.AGUARDANDO_LIQUIDACAO, AgrupadorMovimentoBem.AQUISICAO);
                novoMovimento.setMovimentoUm(true);
                novoMovimento.setMovimentoDois(true);
                novoMovimento.setMovimentoTres(true);
                em.merge(novoMovimento);
                jdbcMovimentoBloqueioBem.insertBemPortal(bem.getId());
            }
        }
    }

    public void inserirMovimentoBloqueioInicial(Bem bem) {
        MovimentoBloqueioBem movimentoInicial = new MovimentoBloqueioBem(bem.getDataAquisicao(), OperacaoMovimentacaoBem.NAO_APLICAVEL, bem, SituacaoEventoBem.FINALIZADO, AgrupadorMovimentoBem.NAO_APLICAVEL);
        em.merge(movimentoInicial);
        jdbcMovimentoBloqueioBem.insertBemPortal(bem.getId());
    }

    public void bloquearBemAquisicao(Bem bem) {
        inserirMovimentoBloqueioInicial(bem);

        Date dataMovimento = DataUtil.getDataComHoraAtual(bem.getDataAquisicao());
        MovimentoBloqueioBem novoMovimento = new MovimentoBloqueioBem(dataMovimento, OperacaoMovimentacaoBem.AQUISICAO_BEM, bem, SituacaoEventoBem.AGUARDANDO_LIQUIDACAO, AgrupadorMovimentoBem.AQUISICAO);
        novoMovimento.setMovimentoUm(true);
        novoMovimento.setMovimentoDois(true);
        novoMovimento.setMovimentoTres(true);
        em.merge(novoMovimento);
    }

    private void insertMovimentoBloqueioBem(ConfigMovimentacaoBem configuracao, Number idBem, SituacaoEventoBem situacaoEventoBem) {
        String sql = getSqlInsertMovimentoBloqueio();
        Query q = em.createNativeQuery(sql);
        q.setParameter("idBem", idBem.longValue());
        q.setParameter("dataMovimento", new Date());
        q.setParameter("operacao", configuracao.getOperacaoMovimentacaoBem().name());
        q.setParameter("tipoMov", configuracao.getOperacaoMovimentacaoBem().getAgrupadorMovimentoBem().name());
        q.setParameter("situacaoEventoBem", situacaoEventoBem.name());
        q.setParameter("movUm", configuracao.getBloqueio().getMovimentoTipoUm());
        q.setParameter("movDois", configuracao.getBloqueio().getMovimentoTipoDois());
        q.setParameter("movTres", configuracao.getBloqueio().getMovimentoTipoTres());
        q.executeUpdate();
    }

    private String getSqlInsertMovimentoBloqueio() {
        return " insert into movimentobloqueiobem values(HIBERNATE_SEQUENCE.nextval, :idBem, :dataMovimento, :operacao, :movUm, :movDois, :movTres, :situacaoEventoBem, :tipoMov) ";
    }

    public void alterarSituacaoEvento(List<Number> idsBem, SituacaoEventoBem situacaoEventoBem, OperacaoMovimentacaoBem operacao) {
        for (Number idBem : idsBem) {
            MovimentoBloqueioBem movimentoBloqueioBem = buscarMovimentoBloqueioPorBem(idBem, operacao);
            movimentoBloqueioBem.setSituacaoEventoBem(situacaoEventoBem);
            updateSituacaoEventoBemBloqueio(movimentoBloqueioBem);
        }
    }

    private void updateSituacaoEventoBemBloqueio(MovimentoBloqueioBem movimento) {
        String sql = " update movimentobloqueiobem set situacaoEventoBem = :situacao where id = :idMovimento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMovimento", movimento.getId());
        q.setParameter("situacao", movimento.getSituacaoEventoBem().name());
        q.executeUpdate();
    }

    private void deletarMovimentoBloqueio(MovimentoBloqueioBem movimento) {
        String sql = " delete from movimentobloqueiobem where id = :idMovimento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMovimento", movimento.getId());
        q.executeUpdate();
    }


    private void deletarMovimentoBloqueoPorAgrupador(AgrupadorMovimentoBem agrupador, Number idBem) {
        String sql = " delete from movimentobloqueiobem where agrupador = :agrupador and bem_id = :idBem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("agrupador", agrupador.name());
        q.setParameter("idBem", idBem);
        q.executeUpdate();
    }

    public String validarMovimentoComDataRetroativaBem(Long idBem, String movimentoAtual, Date dataAtual) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select  ")
            .append("   bem.identificacao, ")
            .append("   ev.tipoeventobem, ")
            .append("   ev.dataLancamento ")
            .append(" from eventobem ev  ")
            .append("  inner join bem on bem.id = ev.bem_id  ")
            .append(" where ev.dataoperacao = (select max(evmax.dataoperacao) from eventobem evmax where evmax.bem_id = ev.bem_id) ")
            .append("  and ev.bem_id = :idBem ")
            .append("  and to_date(:dataAtual, 'dd/MM/yyyy') < trunc(ev.datalancamento) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(dataAtual));
        q.setParameter("idBem", idBem);
        try {
            String toReturn = "";
            if (q.getResultList() != null && !q.getResultList().isEmpty()) {
                for (Object[] obj : (List<Object[]>) q.getResultList()) {
                    if (obj != null) {
                        String identificacao = (String) obj[0];
                        String ultimoEvento = TipoEventoBem.valueOf((String) obj[1]).getDescricao();
                        Date dataLancamento = (Date) obj[2];
                        toReturn = "O bem: " + identificacao + " possui movimento de " + ultimoEvento + " em " + DataUtil.getDataFormatada(dataLancamento)
                            + " que é posterior a data de lançamento de " + movimentoAtual + ": " + DataUtil.getDataFormatada(dataAtual) + ".";
                    }
                }
            }
            return toReturn;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<Bem> validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(List<Bem> bensPesquisados, AssistenteMovimentacaoBens assistente) {
        List<Bem> bensDisponiveis = Lists.newArrayList();
        if (assistente != null && assistente.getConfigMovimentacaoBem() != null && assistente.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
            for (Bem bem : bensPesquisados) {
                if (validarMovimentoRetroativo(assistente, bem)) continue;
                bensDisponiveis.add(bem);
            }
            return bensDisponiveis;
        }
        return bensPesquisados;
    }

    public List<BemVo> validarRetornandoBensDisponiveisParaMovimentacaoComDataRetroativa(List<BemVo> bensPesquisados, AssistenteMovimentacaoBens assistente) {

        List<BemVo> bensDisponiveis = Lists.newArrayList(bensPesquisados);
        if (assistente != null && assistente.getConfigMovimentacaoBem() != null && assistente.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
            bensDisponiveis = Lists.newArrayList();
            assistente.zerarContadoresProcesso();
            assistente.setTotal(bensPesquisados.size());
            assistente.setDescricaoProcesso("Verificando bens disponível para movimentação com data retroativa...");

            for (BemVo bemVo : bensPesquisados) {
                if (validarMovimentoRetroativo(assistente, bemVo.getBem())) continue;
                bensDisponiveis.add(bemVo);
                assistente.conta();
            }
        }
        return bensDisponiveis;
    }

    public boolean validarMovimentoRetroativo(AssistenteMovimentacaoBens assistente, Bem bem) {
        String mensagem = validarMovimentoComDataRetroativaBem(bem.getId(), assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getDescricao(), assistente.getDataLancamento());
        if (!mensagem.isEmpty()) {
            assistente.getMensagens().add(mensagem);
            return true;
        }
        return false;
    }

    public void verificarBensMoveisEmBloqueioParaMovimentacao(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente) {
        if (filtro != null) {
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Verificando bens em bloqueio para a unidade: " + filtro.getHierarquiaAdministrativa() + "...");
            assistente.setMensagens(Lists.<String>newArrayList());
            List<BensMoveisBloqueio> bensBloqueados = buscarBensEmBloqueioMovimentacao(filtro);
            removerBemBloqueadoDoMovimento(assistente, bensBloqueados);
            adicionarMensagemBloqueio(assistente, bensBloqueados);
        }
    }

    private void adicionarMensagemBloqueio(AssistenteMovimentacaoBens assistente, List<BensMoveisBloqueio> bensBloqueados) {
        for (BensMoveisBloqueio bensBloqueado : bensBloqueados) {
            assistente.getMensagens().add(bensBloqueado.toString());
        }
    }

    private void removerBemBloqueadoDoMovimento(AssistenteMovimentacaoBens assistente, List<BensMoveisBloqueio> bensBloqueados) {
        Iterator<BensMoveisBloqueio> iterator = bensBloqueados.iterator();
        while (iterator.hasNext()) {
            BensMoveisBloqueio bemBloqueado = iterator.next();
            for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
                if (bemBloqueado.getIdBem().equals(bemVo.getBem().getId())) {
                    iterator.remove();
                }
            }
            for (Bem bem : assistente.getBensSalvos()) {
                if (bemBloqueado.getIdBem().equals(bem.getId())) {
                    iterator.remove();
                }
            }
        }
    }

    public void verificarBensSelecionadosSeDisponivelConfiguracaoBloqueio(AssistenteMovimentacaoBens assistente) {
        ValidacaoException ve = new ValidacaoException();
        for (Bem bem : assistente.getBensSelecionados()) {
            FiltroPesquisaBem filtro = new FiltroPesquisaBem();
            filtro.setBem(bem);
            if (!bemFacade.verificarBemBloqueado(assistente.getConfigMovimentacaoBem(), filtro)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O bem: " + bem + ". está sendo movimentado em outro processo.");
                assistente.setBloquearAcoesTela(true);
            }
        }
        for (BemVo bemVo : assistente.getBensSelecionadosVo()) {
            FiltroPesquisaBem filtro = new FiltroPesquisaBem();
            filtro.setBem(bemVo.getBem());
            if (!bemFacade.verificarBemBloqueado(assistente.getConfigMovimentacaoBem(), filtro)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O bem: " + bemVo.getBem() + ". está sendo movimentado em outro processo.");
                assistente.setBloquearAcoesTela(true);
            }
        }
        ve.lancarException();
    }

    public void verificarSeBemPossuiBloqueioParaMovimentacao(FiltroPesquisaBem filtro, ConfigMovimentacaoBem configuracao) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracao != null) {
            List<BensMoveisBloqueio> bensBloqueado = buscarBensEmBloqueioMovimentacao(filtro);
            if (!bensBloqueado.isEmpty()) {
                for (BensMoveisBloqueio s : bensBloqueado) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(s.toString());
                }
            }
            if (filtro.getBem() != null && configuracao.getValidarMovimentoRetroativo()) {
                String msgValidacao = validarMovimentoComDataRetroativaBem(filtro.getBem().getId(), configuracao.getOperacaoMovimentacaoBem().getDescricao(), filtro.getDataOperacao());
                if (!Strings.isNullOrEmpty(msgValidacao)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(msgValidacao);
                }
            }
        }
        ve.lancarException();
    }

    public List<BensMoveisBloqueio> buscarBensEmBloqueioMovimentacao(FiltroPesquisaBem filtro) {
        return bemFacade.buscarBensBloqueadoParaMovimentacao(filtro);
    }

    public Boolean verificarConfiguracaoExistente(ConfigMovimentacaoBem configuracao, Date dataOperacao, OperacaoMovimentacaoBem operacao) {
        try {
            String sql = " " +
                " select config.* from ConfigMovimentacaoBem config " +
                " where to_date(:dataOperacao,'dd/MM/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataOperacao,'dd/MM/yyyy'))" +
                "   and config.operacaoMovimentacaoBem = :tipoEvento";
            if (configuracao.getId() != null) {
                sql += " and config.id <> :idConfiguracao";
            }
            Query q = em.createNativeQuery(sql, ConfigMovimentacaoBem.class);
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
            q.setParameter("tipoEvento", operacao.name());
            if (configuracao.getId() != null) {
                q.setParameter("idConfiguracao", configuracao.getId());
            }
            return q.getResultList() != null && !q.getResultList().isEmpty();
        } catch (Exception ex) {
            throw new ValidacaoException(ex.getMessage());
        }
    }

    public MovimentoBloqueioBem buscarMovimentoBloqueioPorBem(Number idBem, OperacaoMovimentacaoBem operacao) {
        try {
            String sql = " " +
                " select mov.* from MovimentoBloqueioBem mov " +
                " where mov.bem_id = :idBem ";
            sql += operacao != null ? " and mov.operacaomovimento = :operacao " : "" +
                " order by mov.datamovimento desc ";
            Query q = em.createNativeQuery(sql, MovimentoBloqueioBem.class);
            q.setParameter("idBem", idBem);
            if (operacao != null) {
                q.setParameter("operacao", operacao.name());
            }
            q.setMaxResults(1);
            return (MovimentoBloqueioBem) q.getResultList().get(0);
        } catch (Exception ex) {
            return null;
        }
    }

    public MovimentoBloqueioBem buscarUltimoMovimentoPorAgrupadorAndSituacao(Number idBem, AgrupadorMovimentoBem agrupador) {
        try {
            String sql = " " +
                " select mov.* from MovimentoBloqueioBem mov " +
                " where mov.bem_id = :idBem " +
                " and mov.agrupador = :tipo " +
                " order by mov.datamovimento desc ";
            Query q = em.createNativeQuery(sql, MovimentoBloqueioBem.class);
            q.setParameter("idBem", idBem);
            q.setParameter("tipo", agrupador.name());
            q.setMaxResults(1);
            return (MovimentoBloqueioBem) q.getResultList().get(0);
        } catch (Exception ex) {
            return null;
        }
    }

    public MovimentoBloqueioBem buscarUltimoMovimentoBloqueio(Long idBem) {
        try {
            String sql = " " +
                " select mov.* from MovimentoBloqueioBem mov " +
                " where mov.bem_id = :idBem " +
                " order by mov.datamovimento desc ";
            Query q = em.createNativeQuery(sql, MovimentoBloqueioBem.class);
            q.setParameter("idBem", idBem);
            q.setMaxResults(1);
            return (MovimentoBloqueioBem) q.getResultList().get(0);
        } catch (Exception ex) {
            return null;
        }
    }

    public ConfigMovimentacaoBem buscarConfiguracaoMovimentacaoBem(Date dataOperacao, OperacaoMovimentacaoBem operacao) {
        String msgErro;
        try {
            String sql = " select  config.* from ConfigMovimentacaoBem config " +
                "          where to_date(:dataoperacao,'dd/MM/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataoperacao,'dd/MM/yyyy'))" +
                "          and config.operacaoMovimentacaoBem = :tipoEvento";
            Query q = em.createNativeQuery(sql, ConfigMovimentacaoBem.class);
            q.setParameter("dataoperacao", DataUtil.getDataFormatada(dataOperacao));
            q.setParameter("tipoEvento", operacao.name());
            return (ConfigMovimentacaoBem) q.getSingleResult();
        } catch (NoResultException nr) {
            msgErro = "Configuração de movimentação de bens móveis não encontrada para a operação de " + operacao.getDescricao() + "  na data " + DataUtil.getDataFormatada(dataOperacao) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
