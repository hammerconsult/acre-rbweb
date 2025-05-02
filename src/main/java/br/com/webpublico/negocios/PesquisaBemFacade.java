package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class PesquisaBemFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(PesquisaBemFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<BemVo>> pesquisarBens(FiltroPesquisaBem filtroPesquisaBem, AssistenteMovimentacaoBens assistente) {
        assistente.setDescricaoProcesso("Pesquisando Bens da Unidade: " + filtroPesquisaBem.getHierarquiaAdministrativa() + "...");
        String sql = " " +
            "  select distinct " +
            "   bem.id as id_bem, " +
            "   bem.identificacao as identificacao, " +
            "   bem.descricao as descricao, " +
            "   ev.id as id_evento, " +
            "   ev.situacaoeventobem as situacao_evento, " +
            "   vwadm.codigo || ' - ' || vwadm.descricao as unidade_adm, " +
            "   vworc.codigo || ' - ' || vworc.descricao as unidade_orc, " +
            "   estIni.id as estado_inicial, " +
            "   est.id as estado_resultante" +
            " from bem " +
            "  inner join eventobem ev on ev.bem_id = bem.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "  inner join estadobem estIni on estini.id = ev.estadoinicial_id " +
            "  inner join grupobem gb on gb.id = est.grupobem_id " +
            "  inner join movimentobloqueiobem mov on mov.bem_id = bem.id " +
            "  inner join vwhierarquiaorcamentaria vworc on est.detentoraOrcamentaria_id = vworc.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  inner join vwhierarquiaadministrativa vwadm on est.detentoraadministrativa_id = vwadm.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  where est.detentoraAdministrativa_id = :idUnidadeAdm " +
            "     and gb.tipobem = :tipoBem " +
            "     and est.estadobem <> :baixado " +
            "     and ev.dataoperacao = (select max(evMax.dataoperacao) from eventobem evMax where evMax.bem_id = ev.bem_id) " +
            "     and mov.datamovimento = (select max(movMax.datamovimento) from movimentobloqueiobem movMax where movMax.bem_id = mov.bem_id) ";
        sql += FiltroPesquisaBem.getCondicaoBloqueioPesquisaBem(assistente.getConfigMovimentacaoBem());
        sql += FiltroPesquisaBem.getCondicaoGeral(filtroPesquisaBem);
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, assistente.getConfigMovimentacaoBem());
        FiltroPesquisaBem.adicionarParametrosGerais(q, filtroPesquisaBem);
        q.setParameter("tipoBem", filtroPesquisaBem.getTipoBem().name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtroPesquisaBem.getDataOperacao()));
        q.setParameter("idUnidadeAdm", filtroPesquisaBem.getHierarquiaAdministrativa().getSubordinada().getId());
        q.setParameter("baixado", EstadoConservacaoBem.BAIXADO.name());

        List<BemVo> bensPesquisados = preencherBensPesquisaBem(assistente, q.getResultList());
        List<BemVo> bensDisponiveis = configMovimentacaoBemFacade.validarRetornandoBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);

        adicionarBensMovimentadosParaAssistente(filtroPesquisaBem, assistente, bensDisponiveis);
        configMovimentacaoBemFacade.verificarBensMoveisEmBloqueioParaMovimentacao(filtroPesquisaBem, assistente);
        return new AsyncResult<List<BemVo>>(bensDisponiveis);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<BemVo>> pesquisarBensPorTipoEvento(FiltroPesquisaBem filtroPesquisaBem, AssistenteMovimentacaoBens assistente) {
        List<Object[]> resultList = getBensVo(filtroPesquisaBem);

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Pesquisando Bens da(o) " + filtroPesquisaBem.getTipoEventoBem().getDescricao() + "...");
        assistente.setTotal(resultList.size());

        List<BemVo> bens = preencherBensPesquisaBem(assistente, resultList);

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso(" ");
        return new AsyncResult<List<BemVo>>(bens);
    }

    public List<BemVo> pesquisarBensPorTipoEvento(FiltroPesquisaBem filtroPesquisaBem) {
        List<Object[]> resultList = getBensVo(filtroPesquisaBem);
        return preencherBensPesquisaBem(null, resultList);
    }

    private List<Object[]> getBensVo(FiltroPesquisaBem filtroPesquisaBem) {
        String sql = " " +
            " select * from (" +
            "  select distinct " +
            "   bem.id as id_bem, " +
            "   bem.identificacao as identificacao, " +
            "   bem.descricao as descricao, " +
            "   ev.id as id_evento, " +
            "   ev.situacaoeventobem as situacao_evento, " +
            "   vwadm.codigo || ' - ' || vwadm.descricao as unidade_adm, " +
            "   vworc.codigo || ' - ' || vworc.descricao as unidade_orc, " +
            "   estIni.id as estado_inicial, " +
            "   estResul.id as estado_resultante" +
            " from bem " +
            "  inner join eventobem ev on ev.bem_id = bem.id " +
            getJoinTipoEvento(filtroPesquisaBem.getTipoEventoBem()) +
            "  inner join estadobem estResul on estResul.id = ev.estadoresultante_id " +
            "  inner join estadobem estIni on estini.id = ev.estadoinicial_id " +
            "  inner join vwhierarquiaorcamentaria vworc on estResul.detentoraOrcamentaria_id = vworc.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  inner join vwhierarquiaadministrativa vwadm on estResul.detentoraadministrativa_id = vwadm.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            getWhereTipoEvento(filtroPesquisaBem.getTipoEventoBem()) +
            " ) order by to_number(identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSelecionado", filtroPesquisaBem.getIdSelecionado());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtroPesquisaBem.getDataOperacao()));
        return q.getResultList();
    }

    private String getJoinTipoEvento(TipoEventoBem tipoEventoBem) {
        switch (tipoEventoBem) {
            case SOLICITACAO_ALTERACAO_CONSERVACAO_BEM:
                return "  inner join itemsolicitacaoaltconsbem item on item.id = ev.id ";
            case EFETIVACAO_ALTERACAO_CONSERVACAO_BEM:
                return "  inner join alteracaoconservacaobem item on item.id = ev.id ";
            case SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL:
                return "  inner join itemsoltransfgrupobem item on item.id = ev.id ";
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_RECEBIDA:
                return "  inner join itemefetivtransfgrupobem item on item.id = ev.id ";
            default:
                return "";
        }
    }

    private String getWhereTipoEvento(TipoEventoBem tipoEventoBem) {
        switch (tipoEventoBem) {
            case SOLICITACAO_ALTERACAO_CONSERVACAO_BEM:
                return " where item.solicitacaoalteracaoconsbem_id = :idSelecionado ";
            case EFETIVACAO_ALTERACAO_CONSERVACAO_BEM:
                return " where item.efetivacaoalteracaoconsbem_id = :idSelecionado ";
            case SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL:
                return " where item.solicitacao_id = :idSelecionado ";
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_RECEBIDA:
                return " where item.efetivacao_id = :idSelecionado " +
                    "    and ev.tipoeventobem = '" + tipoEventoBem.name() +"'";
            default:
                return "";
        }
    }

    private List<BemVo> preencherBensPesquisaBem(AssistenteMovimentacaoBens assistente, List<Object[]> resultList) {
        if (hasAssistenteMovimentacao(assistente)) {
            assistente.zerarContadoresProcesso();
            assistente.setTotal(resultList.size());
        }
        List<BemVo> bens = Lists.newArrayList();
        for (Object[] obj : resultList) {
            BemVo bemVo = novoBemVo(obj);
            bens.add(bemVo);
            if (hasAssistenteMovimentacao(assistente)) {
                assistente.conta();
            }
        }
        if (hasAssistenteMovimentacao(assistente)) {
            assistente.zerarContadoresProcesso();
        }
        return bens;
    }

    private BemVo novoBemVo(Object[] obj) {
        BemVo bemVo = new BemVo();
        bemVo.setId(((BigDecimal) obj[0]).longValue());
        Bem bem = bemFacade.recuperarSemDependencias(bemVo.getId());
        bemVo.setBem(bem);
        bemVo.setIdentificacao((String) obj[1]);
        bemVo.setDescricao((String) obj[2]);
        bemVo.setIdEventoBem(((BigDecimal) obj[3]).longValue());
        bemVo.setSituacaoEventoBem(SituacaoEventoBem.valueOf((String) obj[4]));
        bemVo.setUnidadeAdministrativa((String) obj[5]);
        bemVo.setUnidadeOrcamentaria((String) obj[6]);
        bemVo.setEstadoInicial(bemFacade.recuperarEstadoBemPorId(((BigDecimal) obj[7]).longValue()));
        bemVo.setEstadoResultante(bemFacade.recuperarEstadoBemPorId(((BigDecimal) obj[8]).longValue()));
        return bemVo;
    }

    private void adicionarBensMovimentadosParaAssistente(FiltroPesquisaBem filtroPesquisaBem, AssistenteMovimentacaoBens assistente, List<BemVo> bensDisponiveis) {
        assistente.setBensMovimentadosVo(bensDisponiveis);
        if (assistente.isOperacaoEditar()) {
            assistente.setBensMovimentadosVo(pesquisarBensPorTipoEvento(filtroPesquisaBem));
        }
    }

    public boolean hasAssistenteMovimentacao(AssistenteMovimentacaoBens assistente) {
        return assistente != null;
    }
}
