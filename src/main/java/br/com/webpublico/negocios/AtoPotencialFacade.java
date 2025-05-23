package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class AtoPotencialFacade extends SuperFacadeContabil<AtoPotencial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfigAtoPotencialFacade configAtoPotencialFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private ConvenioReceitaFacade convenioReceitaFacade;
    @EJB
    private ConvenioDespesaFacade convenioDespesaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ExercicioFacade exercicioFacade;

    public AtoPotencialFacade() {
        super(AtoPotencial.class);
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizar((AtoPotencial) entidadeContabil);
    }

    @Override
    public void salvarNovo(AtoPotencial entity) {
        if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        } else {
            atualizarNumeroAndExercicioReferencia(entity);
            if (entity.getId() == null) {
                entity.setNumero(singletonGeradorCodigoContabil.getNumeroAtoPotencial(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getData()));
                em.persist(entity);
            } else {
                entity = em.merge(entity);
            }
            entity.gerarHistoricos();
            contabilizar(entity);
        }
    }

    private void atualizarNumeroAndExercicioReferencia(AtoPotencial atoPotencial) {
        if (atoPotencial.getConvenioReceita() != null) {
            atoPotencial.setNumeroReferencia(atoPotencial.getConvenioReceita().getNumero());
            atoPotencial.setExercicioReferencia(exercicioFacade.getExercicioPorAno(DataUtil.getAno(atoPotencial.getConvenioReceita().getVigenciaInicial())));
        } else if (atoPotencial.getConvenioDespesa() != null) {
            atoPotencial.setNumeroReferencia(atoPotencial.getConvenioDespesa().getNumConvenio());
            atoPotencial.setExercicioReferencia(exercicioFacade.getExercicioPorAno(DataUtil.getAno(atoPotencial.getConvenioDespesa().getDataVigenciaInicial())));
        } else if (atoPotencial.getContrato() != null) {
            atoPotencial.setNumeroReferencia(atoPotencial.getContrato().getNumeroContrato());
            atoPotencial.setExercicioReferencia(exercicioFacade.getExercicioPorAno(DataUtil.getAno(atoPotencial.getContrato().getDataLancamento())));
        }
    }

    @Override
    public void salvar(AtoPotencial entity) {
        entity.gerarHistoricos();
        super.salvar(entity);
    }

    private void contabilizar(AtoPotencial atoPotencial) {
        EventoContabil eventoContabil = buscarEventoContabil(atoPotencial);
        Preconditions.checkNotNull(eventoContabil, "Evento contábil não encontrado.");
        atoPotencial.setEventoContabil(eventoContabil);
        atoPotencial.gerarHistoricos();
        ParametroEvento parametroEvento = criarParametroEvento(atoPotencial);
        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    public ParametroEvento criarParametroEvento(AtoPotencial atoPotencial) {
        ParametroEvento parametroEvento = getParametroEvento(atoPotencial);
        ItemParametroEvento item = getItemParametroEvento(atoPotencial.getValor(), parametroEvento);
        item.setObjetoParametros(getObjetoParametros(atoPotencial, item));
        parametroEvento.getItensParametrosEvento().add(item);
        return parametroEvento;
    }

    private List<ObjetoParametro> getObjetoParametros(AtoPotencial atoPotencial, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(atoPotencial.getId().toString(), AtoPotencial.class.getSimpleName(), item));
        return objetos;
    }

    private ItemParametroEvento getItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    public ParametroEvento getParametroEvento(AtoPotencial atoPotencial) {
        try {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(atoPotencial.getHistoricoRazao());
            parametroEvento.setDataEvento(atoPotencial.getData());
            parametroEvento.setUnidadeOrganizacional(atoPotencial.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(atoPotencial.getEventoContabil());
            parametroEvento.setExercicio(atoPotencial.getExercicio());
            parametroEvento.setClasseOrigem(AtoPotencial.class.getSimpleName());
            parametroEvento.setIdOrigem(atoPotencial.getId() == null ? null : atoPotencial.getId().toString());
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    public EventoContabil buscarEventoContabil(AtoPotencial atoPotencial) {
        EventoContabil eventoContabil = null;
        ConfigAtoPotencial configuracao = configAtoPotencialFacade.buscarCDEAtoPotencial(
            atoPotencial.getTipoLancamento(),
            atoPotencial.getData(),
            atoPotencial.getTipoOperacaoAtoPotencial());
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConvenioReceitaFacade getConvenioReceitaFacade() {
        return convenioReceitaFacade;
    }

    public ConvenioDespesaFacade getConvenioDespesaFacade() {
        return convenioDespesaFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }
}
