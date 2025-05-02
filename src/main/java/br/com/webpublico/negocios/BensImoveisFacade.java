/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.enums.TipoOperacaoBensImoveis;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author Fabio
 */
@Stateless
public class BensImoveisFacade extends SuperFacadeContabil<BensImoveis> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private TipoBaixaBensFacade tipoBaixaBensFacade;
    @EJB
    private TipoIngressoFacade tipoIngressoFacade;
    @EJB
    private SaldoGrupoBemImovelFacade saldoGrupoBemImovelFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigBensImoveisFacade configBensImoveisFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;

    public BensImoveisFacade() {
        super(BensImoveis.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    private void gerarSaldoGrupoBemImoveis(BensImoveis bensImoveis) {
        if (!TipoOperacaoBensImoveis.AQUISICAO_BENS_IMOVEIS.equals(bensImoveis.getTipoOperacaoBemEstoque())) {
            if (TipoOperacaoBensImoveis.AJUSTE_BENS_IMOVEIS_DEPRECIACAO_AUMENTATIVO.equals(bensImoveis.getTipoOperacaoBemEstoque())
                || TipoOperacaoBensImoveis.AJUSTE_BENS_IMOVEIS_AMORTIZACAO_AUMENTATIVO.equals(bensImoveis.getTipoOperacaoBemEstoque())) {
                gerarSaldoBensImoveisPorTipoOperacao(bensImoveis, TipoOperacao.DEBITO);

            } else if (TipoOperacaoBensImoveis.AJUSTE_BENS_IMOVEIS_DEPRECIACAO_DIMINUTIVO.equals(bensImoveis.getTipoOperacaoBemEstoque())
                || TipoOperacaoBensImoveis.AJUSTE_BENS_IMOVEIS_AMORTIZACAO_DIMINUTIVO.equals(bensImoveis.getTipoOperacaoBemEstoque())) {
                gerarSaldoBensImoveisPorTipoOperacao(bensImoveis, TipoOperacao.CREDITO);

            } else {
                gerarSaldoBensImoveisPorTipoOperacao(bensImoveis, TipoOperacao.DEBITO);
                gerarSaldoBensImoveisPorTipoOperacao(bensImoveis, TipoOperacao.CREDITO);
            }
        }
    }

    private void gerarSaldoBensImoveisPorTipoOperacao(BensImoveis bensImoveis, TipoOperacao tipoOperacao) {
        saldoGrupoBemImovelFacade.geraSaldoGrupoBemImoveis(
            bensImoveis.getUnidadeOrganizacional(),
            bensImoveis.getGrupoBem(),
            bensImoveis.getValor(),
            bensImoveis.getTipoGrupo(),
            bensImoveis.getDataBem(),
            bensImoveis.getTipoOperacaoBemEstoque(),
            bensImoveis.getTipoLancamento(),
            tipoOperacao,
            true);
    }

    public void geraContabilizacaoBensImoveis(BensImoveis bensImoveis, boolean simulacao) {
        contabilizarBensImoveis(bensImoveis, simulacao);
    }

    public void contabilizarBemImovel(BensImoveis bensMoveis) {
        contabilizarBemImovel(bensMoveis, false);
    }

    public void contabilizarBemImovel(BensImoveis bensMoveis, boolean simulacao) {
        salvarNovo(bensMoveis, simulacao);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void simularContabilizacao(BensImoveis entity) {
        try {
            if (entity.getEventoContabil() == null) {
                atribuirEventoAoSelecionado(entity);
            }
            contabilizarBensImoveis(entity, true);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void contabilizarBensImoveis(BensImoveis entity) {
        contabilizarBensImoveis(entity, false);
    }

    private void contabilizarBensImoveis(BensImoveis entity, boolean simulacao) {

        ConfigBensImoveis configuracao = configBensImoveisFacade.recuperaEventoPorTipoOperacao(entity.getTipoOperacaoBemEstoque(), entity.getTipoLancamento(), entity.getDataBem());
        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabil(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Bens Imóveis. ");
        }

        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos(hierarquiaOrganizacionalFacade);
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataBem());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setClasseOrigem(Empenho.class.getSimpleName());
            parametroEvento.setIdOrigem(simulacao ? null : entity.getId().toString());

            Calendar instance = Calendar.getInstance();
            instance.setTime(entity.getDataBem());
            parametroEvento.setExercicio(contabilizacaoFacade.getExercicioFacade().getExercicioPorAno(instance.get(Calendar.YEAR)));

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetosParametro = Lists.newArrayList();
            if (!simulacao) {
                objetosParametro.add(new ObjetoParametro(entity, item));
            }
            objetosParametro.add(new ObjetoParametro(entity.getGrupoBem(), item));
            objetosParametro.add(new ObjetoParametro(entity.getTipoOperacaoBemEstoque(), item));
            objetosParametro.add(new ObjetoParametro(entity.getTipoGrupo(), item));

            item.setObjetoParametros(objetosParametro);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento, simulacao);

        } else {
            throw new ExcecaoNegocioGenerica("Não existe configuração de Bens Imóveis");
        }
    }

    @Override
    public void salvarNovo(BensImoveis bensImoveis) {
        salvarNovo(bensImoveis, false);
    }

    public void salvarNovo(BensImoveis bensImoveis, boolean simulacao) {
        simularContabilzacao(bensImoveis);
        if (bensImoveis.getId() == null) {
            atribuirEventoAoSelecionado(bensImoveis);
            if (!simulacao) {
                gerarNumero(bensImoveis);
                bensImoveis.gerarHistoricos(hierarquiaOrganizacionalFacade);
                em.persist(bensImoveis);
            }
        } else {
            if (!simulacao) {
                bensImoveis.gerarHistoricos(hierarquiaOrganizacionalFacade);
                bensImoveis = em.merge(bensImoveis);
            }
        }
        if (!simulacao) {
            gerarSaldoGrupoBemImoveis(bensImoveis);
        }
        geraContabilizacaoBensImoveis(bensImoveis, simulacao);
    }

    private void simularContabilzacao(BensImoveis bensImoveis) {
        geraContabilizacaoBensImoveis(bensImoveis, true);
    }

    private void gerarNumero(BensImoveis entity) {
        entity.setNumero(singletonGeradorCodigoContabil.getNumeroBensImoveis(entity.getExercicio()));
    }

    private void atribuirEventoAoSelecionado(BensImoveis bensImoveis) {
        if (bensImoveis.getEventoContabil() == null) {
            definirEventoContabil(bensImoveis);
        }
    }

    @Override
    public void salvar(BensImoveis entity) {
        entity.gerarHistoricos(hierarquiaOrganizacionalFacade);
        em.merge(entity);
    }

    public void definirEventoContabil(BensImoveis selecionado) {
        try {
            selecionado.setEventoContabil(null);
            if (selecionado.getTipoOperacaoBemEstoque() != null) {
                ConfigBensImoveis configBensImoveis = getConfigBensImoveisFacade().recuperaEventoPorTipoOperacao(selecionado.getTipoOperacaoBemEstoque(), selecionado.getTipoLancamento(), selecionado.getDataBem());
                Preconditions.checkNotNull(configBensImoveis, "Não foi encontrada uma Configuração para os Parametros informados ");
                selecionado.setEventoContabil(configBensImoveis.getEventoContabil());
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarBensImoveis((BensImoveis) entidadeContabil);
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(BensImoveis.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataBem)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataBem)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_BENS_IMOVEIS, "obj.tipoOperacaoBemEstoque");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.GRUPO_PATRIMONIAL, "obj.grupoBem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        return Arrays.asList(consulta);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public TipoBaixaBensFacade getTipoBaixaBensFacade() {
        return tipoBaixaBensFacade;
    }

    public TipoIngressoFacade getTipoIngressoFacade() {
        return tipoIngressoFacade;
    }


    public ContabilizacaoFacade getContabilizacaoFacade() {
        return contabilizacaoFacade;
    }

    public ConfigBensImoveisFacade getConfigBensImoveisFacade() {
        return configBensImoveisFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
