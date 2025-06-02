package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.Persistencia;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mga on 18/10/2017.
 */
@Stateless
public class PatrimonioLiquidoFacade extends SuperFacadeContabil<PatrimonioLiquido> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoPatrimonioLiquidoFacade configuracaoPatrimonioLiquidoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;

    public PatrimonioLiquidoFacade() {
        super(PatrimonioLiquido.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(PatrimonioLiquido entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public PatrimonioLiquido salvarPatrimonio(PatrimonioLiquido entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(reprocessamentoLancamentoContabilSingleton.getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                if (Persistencia.getId(entity) == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroPatrimonioLiquido(entity.getUnidadeOrganizacional(), entity.getDataLancamento()));
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                contabilizar(entity);
            }
            return entity;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        PatrimonioLiquido entity = (PatrimonioLiquido) entidadeContabil;
        contabilizar(entity);
    }

    public EventoContabil buscarEventoContabil(PatrimonioLiquido entity) {
        EventoContabil eventoContabil = null;
        ConfigPatrimonioLiquido configuracao = configuracaoPatrimonioLiquidoFacade.buscarCDEPatrimonioLiquido(
            entity.getTipoLancamento(),
            entity.getOperacaoPatrimonioLiquido(),
            entity.getDataLancamento());
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    private void contabilizar(PatrimonioLiquido entity) {
        EventoContabil eventoContabil = buscarEventoContabil(entity);
        Preconditions.checkNotNull(eventoContabil, "Evento contábil não encontrado.");
        entity.setEventoContabil(eventoContabil);
        entity.gerarHistoricos();
        ParametroEvento parametroEvento = criarParametroEvento(entity);
        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    public ParametroEvento criarParametroEvento(PatrimonioLiquido entity) {
        ParametroEvento parametroEvento = getParametroEvento(entity);
        ItemParametroEvento item = getItemParametroEvento(entity.getValor(), parametroEvento);
        List<ObjetoParametro> listaObj = getObjetoParametros(entity, item);
        item.setObjetoParametros(listaObj);
        parametroEvento.getItensParametrosEvento().add(item);
        return parametroEvento;
    }

    private List<ObjetoParametro> getObjetoParametros(PatrimonioLiquido enity, ItemParametroEvento item) {
        Preconditions.checkNotNull(enity.getPessoa(), "A pessoa não foi preenchida.");
        Preconditions.checkNotNull(enity.getClasse(), "A classe credor não foi preenchida.");

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(enity, item));
        objetos.add(new ObjetoParametro(enity.getPessoa().getId(), item));
        objetos.add(new ObjetoParametro(enity.getClasse().getId(), item));
        return objetos;
    }

    private ItemParametroEvento getItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private ParametroEvento getParametroEvento(PatrimonioLiquido entity) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataLancamento());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setExercicio(entity.getExercicio());
        parametroEvento.setClasseOrigem(PatrimonioLiquido.class.getSimpleName());
        parametroEvento.setIdOrigem(entity.getId() == null ? null : entity.getId().toString());
        return parametroEvento;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }
}
