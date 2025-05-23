package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mateus on 18/10/17.
 */
@Stateless
public class InvestimentoFacade extends SuperFacadeContabil<Investimento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigInvestimentoFacade configInvestimentoFacade;

    public InvestimentoFacade() {
        super(Investimento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(Investimento entity) {
        entity.setNumero(singletonGeradorCodigoContabil.getNumeroInvestimento(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getData()));
        entity.gerarHistoricos();
        entity = em.merge(entity);
        contabilizar(entity);
    }

    @Override
    public void salvar(Investimento entity) {
        entity.gerarHistoricos();
        super.salvar(entity);
    }

    private void contabilizar(Investimento investimento) {
        if (!Hibernate.isInitialized(investimento)) {
            investimento = recuperar(investimento.getId());
        }
        EventoContabil eventoContabil = investimento.getEventoContabil();
        Preconditions.checkNotNull(eventoContabil, "Evento contábil não encontrado.");
        investimento.setEventoContabil(eventoContabil);
        investimento.gerarHistoricos();
        ParametroEvento parametroEvento = criarParametroEvento(investimento);
        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    public ParametroEvento criarParametroEvento(Investimento investimento) {
        ParametroEvento parametroEvento = getParametroEvento(investimento);
        ItemParametroEvento item = getItemParametroEvento(investimento.getValor(), parametroEvento);
        List<ObjetoParametro> listaObj = getObjetoParametros(investimento, item);
        item.setObjetoParametros(listaObj);
        parametroEvento.getItensParametrosEvento().add(item);
        return parametroEvento;
    }

    private List<ObjetoParametro> getObjetoParametros(Investimento investimento, ItemParametroEvento item) {
        Preconditions.checkNotNull(investimento.getClasseCredor(), "A classe credor não foi preenchida.");

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(investimento.getId().toString(), Investimento.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(investimento.getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(investimento.getPessoa().getId().toString(), Pessoa.class.getSimpleName(), item));
        return objetos;
    }

    private ItemParametroEvento getItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private ParametroEvento getParametroEvento(Investimento investimento) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(investimento.getHistoricoRazao());
        parametroEvento.setDataEvento(investimento.getData());
        parametroEvento.setUnidadeOrganizacional(investimento.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(investimento.getEventoContabil());
        parametroEvento.setExercicio(investimento.getExercicio());
        parametroEvento.setClasseOrigem(Investimento.class.getSimpleName());
        parametroEvento.setIdOrigem(investimento.getId() == null ? null : investimento.getId().toString());
        return parametroEvento;
    }

    public EventoContabil buscarEventoContabil(Investimento investimento) {
        EventoContabil eventoContabil = null;
        ConfigInvestimento configuracao = configInvestimentoFacade.buscarCDEInvestimento(
            investimento.getTipoLancamento(),
            investimento.getData(),
            investimento.getOperacaoInvestimento());
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    public UtilConfiguracaoEventoContabilFacade getUtilFacade() {
        return utilFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizar((Investimento) entidadeContabil);
    }
}
