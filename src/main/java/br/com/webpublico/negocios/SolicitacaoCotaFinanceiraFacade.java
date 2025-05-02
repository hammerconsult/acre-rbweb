/*
 * Codigo gerado automaticamente em Tue Mar 06 11:24:54 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.FiltroAprovacaoSolicitacaoFinanceira;
import br.com.webpublico.enums.StatusSolicitacaoCotaFinanceira;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class SolicitacaoCotaFinanceiraFacade extends AbstractFacade<SolicitacaoCotaFinanceira> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;

    public SolicitacaoCotaFinanceiraFacade() {
        super(SolicitacaoCotaFinanceira.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(CR.NUMERO))+1 AS ULTIMONUMERO FROM SOLICITACAOCOTAFINANCEIRA CR";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public List<SolicitacaoCotaFinanceira> buscarSolicitacoesAprovadasPorExercicio(String parte, Exercicio exercicio) {
        String sql = " SELECT SOL.* "
            + " FROM SOLICITACAOCOTAFINANCEIRA SOL "
            + " WHERE (SOL.STATUS = '" + StatusSolicitacaoCotaFinanceira.A_LIBERAR.name() + "' OR SOL.STATUS = '" + StatusSolicitacaoCotaFinanceira.LIBERADO_PARCIALMENTE.name() + "') "
            + " AND SOL.EXERCICIO_ID = :idExercicio "
            + " AND ((LOWER (SOL.NUMERO) LIKE :parte))" +
            " AND SOL.SALDO > 0 "
            + " order by sol.numero desc ";
        Query q = em.createNativeQuery(sql, SolicitacaoCotaFinanceira.class);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SolicitacaoCotaFinanceira> buscarSolicitacaosAbertaPorUnidade(UnidadeOrganizacional uni, Exercicio ex) {
        String sql = " SELECT SOL.* "
            + " FROM SOLICITACAOCOTAFINANCEIRA SOL "
            + " INNER JOIN SUBCONTA SUB ON SOL.CONTAFINANCEIRA_ID = SUB.ID "
            + " INNER JOIN SUBCONTAUNIORG UNISUB ON UNISUB.SUBCONTA_ID = SUB.ID "
            + " INNER JOIN UNIDADEORGANIZACIONAL UNI ON UNI.ID = UNISUB.UNIDADEORGANIZACIONAL_ID AND UNISUB.EXERCICIO_ID = :ex AND UNI.ID = :uni "
            + " WHERE SOL.STATUS = 'ABERTA' OR SOL.STATUS = 'LIBERADO_PARCIALMENTE'";
        Query q = em.createNativeQuery(sql, SolicitacaoCotaFinanceira.class);
        q.setParameter("uni", uni.getId());
        q.setParameter("ex", ex.getId());
        return q.getResultList();
    }

    public List<SolicitacaoCotaFinanceira> buscarSolicitacaoPendentesAprovacaoPorUnidadeAndExercicio(String parte, UnidadeOrganizacional uni, Exercicio ex) {
        String sql = " SELECT SOL.* "
            + " FROM SOLICITACAOCOTAFINANCEIRA SOL "
            + " INNER JOIN SUBCONTA SUB ON SOL.CONTAFINANCEIRA_ID = SUB.ID "
            + " INNER JOIN SUBCONTAUNIORG UNISUB ON UNISUB.SUBCONTA_ID = SUB.ID "
            + " INNER JOIN UNIDADEORGANIZACIONAL UNI ON UNI.ID = UNISUB.UNIDADEORGANIZACIONAL_ID AND UNISUB.EXERCICIO_ID = :ex AND UNI.ID = :uni "
            + "  AND SOL.EXERCICIO_ID = :ex "
            + " WHERE (SOL.STATUS = '" + StatusSolicitacaoCotaFinanceira.A_APROVAR.name() + "' "
            + "     or SOL.STATUS = '" + StatusSolicitacaoCotaFinanceira.A_LIBERAR.name() + "' "
            + "     or SOL.STATUS = '" + StatusSolicitacaoCotaFinanceira.LIBERADO_PARCIALMENTE.name() + "') "
            + " AND SOL.NUMERO LIKE :parte "
            + " ORDER BY SOL.NUMERO DESC";
        Query q = em.createNativeQuery(sql, SolicitacaoCotaFinanceira.class);
        q.setParameter("uni", uni.getId());
        q.setParameter("ex", ex.getId());
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }

    public List<SolicitacaoCotaFinanceira> buscarSolicitacaoPendentesAprovacao(FiltroAprovacaoSolicitacaoFinanceira filtro) {
        String sql = " SELECT SOL.* "
            + " FROM SOLICITACAOCOTAFINANCEIRA SOL "
            + " INNER JOIN SUBCONTA SUB ON SOL.CONTAFINANCEIRA_ID = SUB.ID "
            + " INNER JOIN SUBCONTAUNIORG UNISUB ON UNISUB.SUBCONTA_ID = SUB.ID "
            + " INNER JOIN UNIDADEORGANIZACIONAL UNI ON UNI.ID = UNISUB.UNIDADEORGANIZACIONAL_ID AND UNISUB.EXERCICIO_ID = :ex "
            + "  AND SOL.EXERCICIO_ID = :ex ";

        if (filtro.getPendente()) {
            sql += " WHERE SOL.STATUS = '" + StatusSolicitacaoCotaFinanceira.A_APROVAR.name() + "' ";
        } else {
            sql += " WHERE (SOL.STATUS = '" + StatusSolicitacaoCotaFinanceira.A_APROVAR.name() + "' "
                + "     or SOL.STATUS = '" + StatusSolicitacaoCotaFinanceira.A_LIBERAR.name() + "' "
                + "     or SOL.STATUS = '" + StatusSolicitacaoCotaFinanceira.LIBERADO_PARCIALMENTE.name() + "') ";
        }

        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql += " AND SOL.NUMERO LIKE :parte ";
        }
        if (filtro.getUnidades() != null && !filtro.getUnidades().isEmpty()) {
            sql += " AND UNI.ID in " + Util.montarClausulaIn(filtro.getIdUnidades());
        }
        if (filtro.getDataInicial() != null) {
            sql += " AND sol.dtSolicitacao >= :dataInicial";
        }
        if (filtro.getDataFinal() != null) {
            sql += " AND sol.dtSolicitacao <= :dataFinal";
        }
        sql += " ORDER BY SOL.NUMERO DESC";
        Query q = em.createNativeQuery(sql, SolicitacaoCotaFinanceira.class);
        if (filtro.getDataInicial() != null) {
            q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        }
        if (filtro.getDataFinal() != null) {
            q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        }
        q.setParameter("ex", filtro.getExercicio().getId());
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("parte", "%" + filtro.getNumero().trim() + "%");
        }
        return q.getResultList();
    }

    @Override
    public SolicitacaoCotaFinanceira recarregar(SolicitacaoCotaFinanceira entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        Query q = getEntityManager().createQuery("From SolicitacaoCotaFinanceira s left join fetch s.pagamentos where s = :param ");
        q.setParameter("param", entity);
        return (SolicitacaoCotaFinanceira) q.getSingleResult();
    }

    @Override
    public SolicitacaoCotaFinanceira recuperar(Object id) {
        SolicitacaoCotaFinanceira sol = em.find(SolicitacaoCotaFinanceira.class, id);
        sol.getElementosDespesa().size();
        recuperarSaldoOrcamentarioSolicitacao(sol.getUnidadeOrganizacional(), sol.getDtSolicitacao(), sol.getElementosDespesa());
        return sol;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        SolicitacaoCotaFinanceira sol = em.find(SolicitacaoCotaFinanceira.class, id);
        sol.getElementosDespesa().size();
        return sol;
    }

    @Override
    public void salvarNovo(SolicitacaoCotaFinanceira entity) {
        hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDtSolicitacao());
        if (entity.getId() == null) {
            String numeroSolicitacaoFinanciera = singletonGeradorCodigoContabil.getNumeroSolicitacaoFinanciera(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDtSolicitacao());
            entity.setNumero(numeroSolicitacaoFinanciera);
        }
        super.salvarNovo(entity);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public void salvarParaAprovarLiberacaoNotificando(SolicitacaoCotaFinanceira entity) {
        entity.setStatus(StatusSolicitacaoCotaFinanceira.A_APROVAR);
        entity = em.merge(entity);
        criarNotificacaoSolicitacaoPendenteAprovacao(entity);
    }

    private void criarNotificacaoSolicitacaoPendenteAprovacao(SolicitacaoCotaFinanceira entity) {
        String descricao = "Solicitada pela " + getCodigoDescricaoHierarquia(entity) + ", no valor de " + Util.formataValor(entity.getValorSolicitado()) + " na data de " + DataUtil.getDataFormatada(entity.getDtSolicitacao()) + " à Solicitação financeira n°: " + entity.getNumero();
        String titulo = "Solicitação Financeira Pendende de Aprovação";
        String link = "/aprovacao-solicitacao-financeira/" + entity.getId() + "/notificacao/$idNotificacao/";
        NotificacaoService.getService().notificar(titulo, descricao, link, Notificacao.Gravidade.INFORMACAO, TipoNotificacao.APROVACAO_SOLICITAO_FINANCEIA);
    }

    private void criarNotificacaoSolicitacaoAprovada(SolicitacaoCotaFinanceira entity, BigDecimal valorAprovado) {
        String descricao = "Solicitada pela " + getCodigoDescricaoHierarquia(entity) + ", valor aprovado: " + Util.formataValor(valorAprovado) + " na data de " + DataUtil.getDataFormatada(entity.getDtSolicitacao()) + " à Solicitação Financeira n°: " + entity.getNumero();
        String titulo = "Solicitação Financeira Aprovada Pendente de Liberação";
        String link = "/liberacao-financeira/nova-solicitacao/" + entity.getId() + "/notificacao/$idNotificacao/";
        NotificacaoService.getService().notificar(titulo, descricao, link, Notificacao.Gravidade.INFORMACAO, TipoNotificacao.LIBERACAO_FINANCEIA);
    }

    private void criarNotificacaoCancelamentoSolicitacao(SolicitacaoCotaFinanceira entity) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Solicitação Financeira: " + entity.toString() + ",foi cancelada na data: " + DataUtil.getDataFormatada(entity.getDtSolicitacao()) + ". Motivo: " + entity.getMotivoCancelamento());
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo("Solicitação Financeira Cancelada");
        notificacao.setTipoNotificacao(TipoNotificacao.RETORNO_LIBERACAO_FINANCEIA);
        notificacao.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        notificacao.setLink("/solicitacao-financeira/ver/" + entity.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void criarNotificacaoSolicitacaoLiberada(SolicitacaoCotaFinanceira entity) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Solicitação Financeira: " + entity.toString());
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo("Solicitação Financeira Liberada");
        notificacao.setTipoNotificacao(TipoNotificacao.RETORNO_LIBERACAO_FINANCEIA);
        notificacao.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        notificacao.setLink("/solicitacao-financeira/ver/" + entity.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public List<SolicitacaoFinanceiraContaDespesa> buscarDespesaOrcPorExercicioAndUnidade(Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional, Date data) {
        List<SolicitacaoFinanceiraContaDespesa> retorno = new ArrayList<>();
        try {

            String sql = "SELECT D.* FROM DESPESAORC D " +
                " INNER JOIN PROVISAOPPADESPESA PROV ON D.PROVISAOPPADESPESA_ID = PROV.ID" +
                " INNER JOIN SUBACAOPPA SUB ON PROV.SUBACAOPPA_ID = SUB.ID" +
                " INNER JOIN ACAOPPA ACAO ON SUB.ACAOPPA_ID = ACAO.ID" +
                " INNER JOIN CONTA C ON PROV.CONTADEDESPESA_ID = C.ID" +
                " WHERE D.EXERCICIO_ID = :EX" +
                " AND PROV.UNIDADEORGANIZACIONAL_ID = :UO" +
                " ORDER BY ACAO.CODIGO, C.CODIGO";
            Query consulta = em.createNativeQuery(sql, DespesaORC.class);
            consulta.setParameter("EX", exercicio.getId());
            consulta.setParameter("UO", unidadeOrganizacional.getId());
            List<DespesaORC> lista = (ArrayList<DespesaORC>) consulta.getResultList();
            for (DespesaORC despesaORC : lista) {
                retorno.add(new SolicitacaoFinanceiraContaDespesa(despesaORC));
            }
            recuperarSaldoOrcamentarioSolicitacao(unidadeOrganizacional, data, retorno);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível recuperar os elementos de despesa.");
        }
        return retorno;
    }

    public void recuperarSaldoOrcamentarioSolicitacao(UnidadeOrganizacional unidadeOrganizacional, Date data, List<SolicitacaoFinanceiraContaDespesa> retorno) {
        for (SolicitacaoFinanceiraContaDespesa solicitacaoFinanceiraContaDespesa : retorno) {
            DespesaORC despesaORC = em.find(DespesaORC.class, solicitacaoFinanceiraContaDespesa.getDespesaORC().getId());
            List<FonteDespesaORC> fonteDespesaORCs = despesaORC.getFonteDespesaORCs();
            BigDecimal saldo = BigDecimal.ZERO;
            for (FonteDespesaORC fonteDespesaORC : fonteDespesaORCs) {
                saldo = saldo.add(saldoFonteDespesaORCFacade.saldoRealPorFonte(fonteDespesaORC, data, unidadeOrganizacional));
            }
            solicitacaoFinanceiraContaDespesa.setSaldo(saldo);
        }
    }

    public SolicitacaoCotaFinanceira salvarCancelamento(SolicitacaoCotaFinanceira solicitacao, Notificacao notificacao) {
        cancelarSolicitacaoFinanceira(solicitacao);
        solicitacao = em.merge(solicitacao);
        criarNotificacaoCancelamentoSolicitacao(solicitacao);
        marcarNotificacaoComoLida(solicitacao, notificacao);
        return solicitacao;
    }

    private void cancelarSolicitacaoFinanceira(SolicitacaoCotaFinanceira solicitacao) {
        solicitacao.setStatus(StatusSolicitacaoCotaFinanceira.CANCELADO);
        solicitacao.setSaldo(BigDecimal.ZERO);
        solicitacao.setSaldoAprovar(BigDecimal.ZERO);
        solicitacao.setValorAprovado(solicitacao.getValorLiberado());
    }

    private void movimentarSolicitacao(SolicitacaoCotaFinanceira solicitacao, BigDecimal valorAprovado) {
        solicitacao.setDataCancelamento(null);
        solicitacao.setMotivoCancelamento(null);
        solicitacao.setStatus(StatusSolicitacaoCotaFinanceira.A_LIBERAR);
        solicitacao.setValorAprovado(solicitacao.getValorAprovado().add(valorAprovado));
        solicitacao.setSaldoAprovar(solicitacao.getValorSolicitado().subtract(solicitacao.getValorAprovado()));
        solicitacao.setSaldo(solicitacao.getValorAprovado().subtract(solicitacao.getValorLiberado()));
        if (solicitacao.getValorSolicitado().compareTo(solicitacao.getValorAprovado()) == 0) {
            solicitacao.setSaldoAprovar(BigDecimal.ZERO);
        }
    }

    public SolicitacaoCotaFinanceira aprovarSolicitacao(SolicitacaoCotaFinanceira solicitacao, BigDecimal valorAprovado) {
        validarConcorrencia(solicitacao);
        singletonConcorrenciaContabil.bloquear(solicitacao);
        movimentarSolicitacao(solicitacao, valorAprovado);
        solicitacao = salvarAprovacao(solicitacao);
        singletonConcorrenciaContabil.desbloquear(solicitacao);
        return solicitacao;
    }

    private SolicitacaoCotaFinanceira salvarAprovacao(SolicitacaoCotaFinanceira solicitacao) {
        solicitacao = em.merge(solicitacao);
        return solicitacao;
    }

    public void movimentarNoficiacoes(SolicitacaoCotaFinanceira solicitacao, Notificacao notificacao, BigDecimal valorAprovado) {
        if (solicitacao.getValorAprovado().compareTo(solicitacao.getValorSolicitado()) == 0) {
            marcarNotificacaoComoLida(solicitacao, notificacao);
        }
        criarNotificacaoSolicitacaoAprovada(solicitacao, valorAprovado);
    }

    private void marcarNotificacaoComoLida(SolicitacaoCotaFinanceira solicitacao, Notificacao notificacao) {
        String link = "/aprovacao-solicitacao-financeira/" + solicitacao.getId() + "/notificacao/";
        NotificacaoService service = NotificacaoService.getService();
        if (notificacao != null) {
            service.marcarComoLida(notificacao);
        } else {
//            List<Notificacao> notificacoes = service.buscarNotificacoesPorLink(link);
//            service.marcarComoLida(notificacoes);
        }
    }

    public void validarConcorrencia(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        ValidacaoException ve = new ValidacaoException();
        if (!isSolicitacaoDisponivel(solicitacaoCotaFinanceira)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Solicitação Financeira " + solicitacaoCotaFinanceira + " está sendo utilizada por outro usuário. Caso o problema persistir, selecione novamente a Solicitação Financeira.");
        }
        ve.lancarException();
    }

    public boolean isSolicitacaoAberta(Long idSolicitacaoCotaFinanceira) {
        String sql = "select * from SolicitacaoCotaFinanceira where id = :idSolicitacaoCotaFinanceira and status = :aberta";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacaoCotaFinanceira", idSolicitacaoCotaFinanceira);
        q.setParameter("aberta", StatusSolicitacaoCotaFinanceira.ABERTA.name());
        List<Object[]> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public boolean isVersaoAtual(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        if (solicitacaoCotaFinanceira.getId() != null) {
            String sql = "select * from SolicitacaoCotaFinanceira where id = :idSolicitacaoCotaFinanceira and versao = :versao ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idSolicitacaoCotaFinanceira", solicitacaoCotaFinanceira.getId());
            q.setParameter("versao", solicitacaoCotaFinanceira.getVersao());
            List<Object[]> resultado = q.getResultList();
            return resultado != null && !resultado.isEmpty();
        }
        return true;
    }

    private boolean isSolicitacaoDisponivel(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        return singletonConcorrenciaContabil.isDisponivel(solicitacaoCotaFinanceira);
    }

    public void desbloquearSolicitacao(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        singletonConcorrenciaContabil.desbloquear(solicitacaoCotaFinanceira);
    }

    private void bloquearSolicitacao(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        singletonConcorrenciaContabil.bloquear(solicitacaoCotaFinanceira);
    }

    private String getCodigoDescricaoHierarquia(SolicitacaoCotaFinanceira entity) {
        return hierarquiaOrganizacionalFacade.buscarCodigoDescricaoHierarquia(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), entity.getUnidadeOrganizacional(), entity.getDtSolicitacao());
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }
}
