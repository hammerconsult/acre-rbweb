/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.AtributoMetadata;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EntidadeMetaData;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Claudio
 */
@Stateless
@LocalBean
public class BensIntangiveisFacade extends SuperFacadeContabil<BensIntangiveis> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private TipoBaixaBensFacade tipoBaixaBensFacade;
    @EJB
    private TipoIngressoFacade tipoIngressoFacade;
    @EJB
    private ConfigBensIntangiveisFacade configBensIntangiveisFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private SaldoGrupoBemIntangivelFacade saldoGrupoBemIntangivelFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public BensIntangiveisFacade() {
        super(BensIntangiveis.class);
    }

    public Long ultimoNumeroMaisUm() {
        Query q = em.createNativeQuery("SELECT coalesce(max(to_number(numero)), 0) + 1 AS codigo FROM BensIntangiveis");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public void validaCamposIntegracao(BensIntangiveis bensIntangiveis) throws ExcecaoNegocioGenerica {
        EntidadeMetaData metadata = new EntidadeMetaData(BensIntangiveis.class);

        for (Iterator<AtributoMetadata> it = metadata.getAtributos().iterator(); it.hasNext(); ) {
            AtributoMetadata object = it.next();
            if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Obrigatorio.class)) {
                if (object.getString(bensIntangiveis).equals("") || ((AtributoMetadata) object).getString(bensIntangiveis) == null) {
                    throw new ExcecaoNegocioGenerica("O campo " + object.getEtiqueta() + " deve ser informado.");
                }
            }
        }
    }

    public void salvaIntegracaoBensIntangiveis(BensIntangiveis bensIntangiveis) throws ExcecaoNegocioGenerica {
        validaCamposIntegracao(bensIntangiveis);

        if (bensIntangiveis.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            String msg = "O valor deve ser maior que zero!";
            throw new ExcecaoNegocioGenerica(msg);
        } else {
            BensIntangiveis bem = new BensIntangiveis();
            bem.setNumero(ultimoNumeroMaisUm() + "");
            bem.setDataBem(bensIntangiveis.getDataBem());
            bem.setUnidadeOrganizacional(bensIntangiveis.getUnidadeOrganizacional());
            bem.setTipoLancamento(bensIntangiveis.getTipoLancamento());
            bem.setTipoBaixaBens(bensIntangiveis.getTipoBaixaBens());
            bem.setTipoIngresso(bensIntangiveis.getTipoIngresso());
            bem.setTipoOperacaoBemEstoque(bensIntangiveis.getTipoOperacaoBemEstoque());
            bem.setGrupoBem(bensIntangiveis.getGrupoBem());
            bem.setHistorico(bensIntangiveis.getHistorico());
            bem.setValor(bensIntangiveis.getValor());

            salvar(bem);
        }
    }

    @Override
    public void salvarNovo(BensIntangiveis bensIntangiveis) {
        try {
            if (bensIntangiveis.getId() == null) {
                bensIntangiveis.setNumero(singletonGeradorCodigoContabil.getNumeroBensIntagiveis(bensIntangiveis.getExercicio()));
                bensIntangiveis.gerarHistoricos(hierarquiaOrganizacionalFacade);
                em.persist(bensIntangiveis);
            } else {
                bensIntangiveis.gerarHistoricos(hierarquiaOrganizacionalFacade);
                bensIntangiveis = em.merge(bensIntangiveis);
            }
            gerarSaldoBensIntangiveis(bensIntangiveis);
            contabilizarBensIntangiveis(bensIntangiveis);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarSaldoBensIntangiveis(BensIntangiveis bensIntangiveis) {

        if (!bensIntangiveis.getTipoOperacaoBemEstoque().equals(TipoOperacaoBensIntangiveis.AQUISICAO_BENS_INTANGIVEIS)) {
            saldoGrupoBemIntangivelFacade.geraSaldoGrupoBemIntangiveis(
                bensIntangiveis.getUnidadeOrganizacional(),
                bensIntangiveis.getGrupoBem(),
                bensIntangiveis.getValor(),
                bensIntangiveis.getTipoGrupo(),
                bensIntangiveis.getDataBem(),
                bensIntangiveis.getTipoOperacaoBemEstoque(),
                bensIntangiveis.getTipoLancamento(),
                TipoOperacao.DEBITO,
                true);

            saldoGrupoBemIntangivelFacade.geraSaldoGrupoBemIntangiveis(
                bensIntangiveis.getUnidadeOrganizacional(),
                bensIntangiveis.getGrupoBem(),
                bensIntangiveis.getValor(),
                bensIntangiveis.getTipoGrupo(),
                bensIntangiveis.getDataBem(),
                bensIntangiveis.getTipoOperacaoBemEstoque(),
                bensIntangiveis.getTipoLancamento(),
                TipoOperacao.CREDITO,
                true);
        }
    }

    @Override
    public void salvar(BensIntangiveis entity) {
        entity.gerarHistoricos(hierarquiaOrganizacionalFacade);
        em.merge(entity);
    }

    public void contabilizarBensIntangiveis(BensIntangiveis bensIntangiveis) {
        contabilizarBensIntagiveis(bensIntangiveis);
    }

    private void contabilizarBensIntagiveis(BensIntangiveis entity) {

        ConfigBensIntangiveis configuracao = configBensIntangiveisFacade.recuperaEventoPorTipoOperacao(entity.getTipoOperacaoBemEstoque(), entity.getTipoLancamento(), entity.getDataBem());
        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabil(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Bens Intangíveis. ");
        }

        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos(hierarquiaOrganizacionalFacade);
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataBem());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setClasseOrigem(Empenho.class.getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());

            Calendar instance = Calendar.getInstance();
            instance.setTime(entity.getDataBem());
            parametroEvento.setExercicio(contabilizacaoFacade.getExercicioFacade().getExercicioPorAno(instance.get(Calendar.YEAR)));

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetosParametro = Lists.newArrayList();
            objetosParametro.add(new ObjetoParametro(entity.getId().toString(), BensIntangiveis.class.getSimpleName(), item));
            objetosParametro.add(new ObjetoParametro(entity.getGrupoBem().getId().toString(), GrupoBem.class.getSimpleName(), item));
            objetosParametro.add(new ObjetoParametro(entity.getTipoOperacaoBemEstoque().toString(), TipoOperacaoBensImoveis.class.getSimpleName(), item));
            objetosParametro.add(new ObjetoParametro(entity.getTipoGrupo().name(), TipoGrupo.class.getSimpleName(), item));

            item.setObjetoParametros(objetosParametro);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não existe configuração de Bens Intangíveis");
        }
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

    public ConfigBensIntangiveisFacade getConfigBensIntangiveisFacade() {
        return configBensIntangiveisFacade;
    }

    public ContabilizacaoFacade getContabilizacaoFacade() {
        return contabilizacaoFacade;
    }

    public SaldoGrupoBemIntangivelFacade getSaldoGrupoBemIntangivelFacade() {
        return saldoGrupoBemIntangivelFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarBensIntagiveis((BensIntangiveis) entidadeContabil);
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(BensIntangiveis.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataBem)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataBem)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_BENS_INTANGIVEIS, "obj.tipoOperacaoBemEstoque");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.GRUPO_PATRIMONIAL, "obj.grupoBem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        return Arrays.asList(consulta);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
