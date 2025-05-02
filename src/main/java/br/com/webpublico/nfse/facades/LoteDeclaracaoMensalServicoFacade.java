package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.nfse.controladores.LoteDeclaracaoMensalServicoControlador;
import br.com.webpublico.nfse.domain.DeclaracaoMensalServico;
import br.com.webpublico.nfse.domain.LoteDeclaracaoMensalServico;
import br.com.webpublico.nfse.domain.LoteDeclaracaoMensalServicoItem;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class LoteDeclaracaoMensalServicoFacade extends AbstractFacade<LoteDeclaracaoMensalServico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LoteDeclaracaoMensalServicoFacade() {
        super(LoteDeclaracaoMensalServico.class);
    }

    public void persistirItem(LoteDeclaracaoMensalServicoItem item) {
        em.persist(item);
    }

    @Override
    public LoteDeclaracaoMensalServico recuperar(Object id) {
        LoteDeclaracaoMensalServico lote = super.recuperar(id);
        Hibernate.initialize(lote.getItens());
        return lote;
    }

    public List<LoteDeclaracaoMensalServico> buscarLotes(LoteDeclaracaoMensalServicoControlador.FiltroPesquisaLoteDMS filtro, int first, int pageSize) {
        String sql = "select lote from LoteDeclaracaoMensalServico lote";
        Query q = montarQueryPesquisaLote(filtro, sql, " order by lote.id desc");
        q.setMaxResults(pageSize);
        q.setFirstResult(first);
        return q.getResultList();
    }

    public Integer contarLotes(LoteDeclaracaoMensalServicoControlador.FiltroPesquisaLoteDMS filtro) {
        String sql = "select count(lote.id) from LoteDeclaracaoMensalServico lote";
        Query q = montarQueryPesquisaLote(filtro, sql, null);
        return ((Number) q.getSingleResult()).intValue();
    }

    private Query montarQueryPesquisaLote(LoteDeclaracaoMensalServicoControlador.FiltroPesquisaLoteDMS filtro, String sql, String orderBy) {
        String juncao = " where ";
        Map<String, Object> params = Maps.newHashMap();
        if (filtro.getCompetenciaInicial() == null) {
            filtro.setCompetenciaInicial(Mes.JANEIRO);
        }
        if (filtro.getCompetenciaFinal() == null) {
            filtro.setCompetenciaFinal(Mes.DEZEMBRO);
        }
        List<Mes> competencias = Mes.getTodosMesesNoIntevalo(filtro.getCompetenciaInicial(), filtro.getCompetenciaFinal());
        if (!competencias.isEmpty()) {
            sql += juncao + " lote.mes in (:competencias) ";
            params.put("competencias", competencias);
            juncao = " and ";
        }
        if (filtro.getExercicio() != null) {
            sql += juncao + " lote.exercicio = :exercicio ";
            params.put("exercicio", filtro.getExercicio());
            juncao = " and ";
        }
        if (filtro.getTipoMovimento() != null) {
            sql += juncao + " lote.tipoMovimento = :tipoMovimento ";
            params.put("tipoMovimento", filtro.getTipoMovimento());
            juncao = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCmcInicial())) {
            sql += juncao + " lote.cmcInicial >= :cmcInicial ";
            params.put("cmcInicial", filtro.getCmcInicial());
            juncao = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCmcFinal())) {
            sql += juncao + " lote.cmcFinal <= :cmcFinal ";
            params.put("cmcFinal", filtro.getCmcFinal());
        }
        if (!Strings.isNullOrEmpty(orderBy)) {
            sql += orderBy;
        }
        Query q = em.createQuery(sql);
        for (String s : params.keySet()) {
            q.setParameter(s, params.get(s));
        }
        return q;
    }


    public boolean hasDebitoDiferenteEmAberto(LoteDeclaracaoMensalServico loteDeclaracaoMensalServico) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.PARCELA_SITUACAO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.DIFERENTE,
            SituacaoParcela.EM_ABERTO.name());
        consultaParcela.addComplementoDoWhere(getComplementoWhereDebitosLote(loteDeclaracaoMensalServico));
        return !consultaParcela.executaConsulta().getResultados().isEmpty();
    }

    private String getComplementoWhereDebitosLote(LoteDeclaracaoMensalServico loteDeclaracaoMensalServico) {
        return " and exists (select 1 " +
            " from lotedecmensalservico ld" +
            " inner join lotedecmensalservicoitem ldi on ldi.lote_id = ld.id " +
            " inner join declaracaomensalservico d on d.id = ldi.declaracaomensalservico_id " +
            " inner join processocalculo pc on pc.id = d.processocalculo_id " +
            " inner join calculo c on c.processocalculo_id = pc.id " +
            " where ld.id = " + loteDeclaracaoMensalServico.getId() +
            "   and c.id = vw.calculo_id) ";
    }

    public void cancelarLoteEncerramento(AssistenteBarraProgresso assistenteBarraProgresso,
                                         LoteDeclaracaoMensalServico loteDeclaracaoMensalServico) {
        cancelarDebitosLoteEncerramento(assistenteBarraProgresso, loteDeclaracaoMensalServico);
        cancelarItensLoteEncerramento(assistenteBarraProgresso, loteDeclaracaoMensalServico);
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Atualizando situação do encerramento mensal de serviço em lote.");
        loteDeclaracaoMensalServico.setSituacao(LoteDeclaracaoMensalServico.Situacao.CANCELADO);
        em.merge(loteDeclaracaoMensalServico);
    }

    private void cancelarItensLoteEncerramento(AssistenteBarraProgresso assistenteBarraProgresso,
                                               LoteDeclaracaoMensalServico loteDeclaracaoMensalServico) {
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Cancelando encerramentos mensais de serviço.");
        em.createNativeQuery(" update declaracaomensalservico set situacao = :situacao, usuariocancelamentowp_id = :idUsuario, " +
                "                                   datacancelamento = :dataCancelamento " +
                " where id in (select i.declaracaomensalservico_id  " +
                "                 from lotedecmensalservicoitem i  " +
                "             where i.lote_id = :idLote) ")
            .setParameter("situacao", DeclaracaoMensalServico.Situacao.CANCELADO.name())
            .setParameter("idUsuario", assistenteBarraProgresso.getUsuarioSistema().getId())
            .setParameter("dataCancelamento", assistenteBarraProgresso.getDataAtual())
            .setParameter("idLote", loteDeclaracaoMensalServico.getId())
            .executeUpdate();

    }

    private void cancelarDebitosLoteEncerramento(AssistenteBarraProgresso assistenteBarraProgresso,
                                                 LoteDeclaracaoMensalServico loteDeclaracaoMensalServico) {
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Cancelando débitos gerados pelo encerramento mensal de serviço em lote.");
        em.createNativeQuery(" insert into situacaoparcelavalordivida (id, datalancamento, situacaoparcela, " +
                "                                        parcela_id, saldo, inconsistente, " +
                "                                        referencia) " +
                " select hibernate_sequence.nextval, :dataLancamento, :situacao, spvd.parcela_id, " +
                "        spvd.saldo, spvd.inconsistente, spvd.referencia " +
                "    from lotedecmensalservico ld " +
                "   inner join lotedecmensalservicoitem ild on ild.lote_id = ld.id " +
                "   inner join declaracaomensalservico d on d.id = ild.declaracaomensalservico_id " +
                "   inner join processocalculo pc on pc.id = d.processocalculo_id " +
                "   inner join calculo c on c.processocalculo_id = pc.id " +
                "   inner join valordivida vd on vd.calculo_id = c.id " +
                "   inner join parcelavalordivida pvd on  pvd.valordivida_id = vd.id " +
                "   inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
                " where ld.id = :idLote ")
            .setParameter("idLote", loteDeclaracaoMensalServico.getId())
            .setParameter("situacao", SituacaoParcela.CANCELAMENTO.name())
            .setParameter("dataLancamento", assistenteBarraProgresso.getDataAtual())
            .executeUpdate();
    }
}
