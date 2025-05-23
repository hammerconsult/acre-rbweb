/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.OperacaoDiarioDividaPublica;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@Stateless
public class MovimentoDividaPublicaFacade extends SuperFacadeContabil<MovimentoDividaPublica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigMovDividaPublicaFacade configMovDividaPublicaFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SistemaFacade sistemaFacade;

    public MovimentoDividaPublicaFacade() {
        super(MovimentoDividaPublica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Long retornaUltimoCodigoLong() {
        Long num;
        String sql = " SELECT max(coalesce(obj.numero,0)) FROM MovimentoDividaPublica obj ";
        Query query = getEntityManager().createNativeQuery(sql);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            BigDecimal b = (BigDecimal) query.getSingleResult();
            if (b != null) {
                b = b.add(BigDecimal.ONE);
            } else {
                b = BigDecimal.ONE;
            }
            num = b.setScale(0, BigDecimal.ROUND_UP).longValueExact();
        } else {
            return 1l;
        }
        return num;
    }

    public DividaPublicaFacade getDividaPublicaFacade() {
        return dividaPublicaFacade;
    }

    public ConfigMovDividaPublicaFacade getConfigMovDividaPublicaFacade() {
        return configMovDividaPublicaFacade;
    }

    @Override
    public void salvar(MovimentoDividaPublica entity) {
        entity.gerarHistoricos();
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(MovimentoDividaPublica movimentoDividaPublica) {
        if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        } else {
            if (movimentoDividaPublica.getId() == null) {
                movimentoDividaPublica.setNumero(retornaUltimoCodigoLong());
                movimentoDividaPublica.gerarHistoricos();
                movimentoDividaPublica.setExercicio(sistemaFacade.getExercicioCorrente());
                em.persist(movimentoDividaPublica);
            } else {
                movimentoDividaPublica.gerarHistoricos();
                movimentoDividaPublica = em.merge(movimentoDividaPublica);
            }
            contabilizarMovimentoDividaPublica(movimentoDividaPublica);

            saldoDividaPublicaFacade.gerarMovimento(movimentoDividaPublica.getData(),
                movimentoDividaPublica.getValor(),
                movimentoDividaPublica.getUnidadeOrganizacional(),
                movimentoDividaPublica.getDividaPublica(),
                movimentoDividaPublica.getOperacaoMovimentoDividaPublica(),
                movimentoDividaPublica.getTipoLancamento().isEstorno(),
                OperacaoDiarioDividaPublica.MOVIMENTO,
                OperacaoMovimentoDividaPublica.TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO.equals(movimentoDividaPublica.getOperacaoMovimentoDividaPublica()) ? Intervalo.CURTO_PRAZO : Intervalo.LONGO_PRAZO,
                movimentoDividaPublica.getContaDeDestinacao(),
                true);
        }
    }

    public List<MovimentoDividaPublica> filtraPorUnidadeOrganizacionalOperacao(String parte, UnidadeOrganizacional unidade, OperacaoMovimentoDividaPublica filtroOperacaoMovimentoDividaPublica, Date data) {
        String hql = "select mov from MovimentoDividaPublica mov where lower(to_char(mov.numero)) like :parte and mov.saldo != 0";
        if (unidade != null) {
            hql += " and mov.unidadeOrganizacional = :unidade";
        }
        if (filtroOperacaoMovimentoDividaPublica != null) {
            hql += " and mov.operacaoMovimentoDividaPublica = :operacao";
        }
        if (data != null) {
            hql += " and trunc(mov.data) <= :data";
        }
        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        if (unidade != null) {
            consulta.setParameter("unidade", unidade);
        }
        if (filtroOperacaoMovimentoDividaPublica != null) {
            consulta.setParameter("operacao", filtroOperacaoMovimentoDividaPublica);
        }
        if (data != null) {
            consulta.setParameter("data", data);
        }
        return consulta.getResultList();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void contabilizarMovimentoDividaPublica(MovimentoDividaPublica entity) {
        ConfigMovDividaPublica configMovDividaPublica = configMovDividaPublicaFacade.recuperaConfiguracaoExistente(entity);
        if (configMovDividaPublica != null && configMovDividaPublica.getEventoContabil() != null) {
            entity.setEventoContabil(configMovDividaPublica.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Movimento da Dívida Pública.");
        }
        entity.gerarHistoricos();

        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getData());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setExercicio(entity.getExercicio());
        parametroEvento.setIdOrigem(entity.getId().toString());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());

        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getDividaPublica().getPessoa(), entity.getDividaPublica().getClasseCredor(), entity.getData()));

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity.getId().toString(), MovimentoDividaPublica.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getDividaPublica().getCategoriaDividaPublica().getId().toString(), CategoriaDividaPublica.class.getSimpleName(), item));
        item.setObjetoParametros(objetos);

        parametroEvento.getItensParametrosEvento().add(item);
        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SaldoDividaPublicaFacade getSaldoDividaPublicaFacade() {
        return saldoDividaPublicaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarMovimentoDividaPublica((MovimentoDividaPublica) entidadeContabil);
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(MovimentoDividaPublica.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.data)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.data)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.OPERACAO_DIVIDAPUBLICA, "obj.operacaoMovimentoDividaPublica");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }
}
