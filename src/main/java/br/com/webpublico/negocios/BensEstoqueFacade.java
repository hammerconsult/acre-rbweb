package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Stateless
public class BensEstoqueFacade extends SuperFacadeContabil<BensEstoque> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoIngressoFacade tipoIngressoFacade;
    @EJB
    private TipoBaixaBensFacade tipoBaixaBensFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private ConfigBensEstoqueFacade configBensEstoqueFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private SaldoGrupoMaterialFacade saldoGrupoMaterialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BensEstoqueFacade() {
        super(BensEstoque.class);
    }

    public BensEstoque salva(BensEstoque bensEstoque) throws Exception {
        em.merge(bensEstoque);
        return bensEstoque;
    }

    public String getUltimoNumero() {
        String sql = "select max(to_number(be.numero))+1 as ultimoNumero from BensEstoque be";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    @Override
    public void salvarNovo(BensEstoque entity) {
        salvarBensEstoque(entity);
    }

    public BensEstoque salvarBensEstoque(BensEstoque entity) {
        try {
            if (entity.getId() == null) {
                entity.setNumero(singletonGeradorCodigoContabil.getNumeroBensEstoque(entity.getExercicio()));
                entity.gerarHistoricos();
                em.persist(entity);
            } else {
                entity.gerarHistoricos();
                entity = em.merge(entity);
            }
            gerarSaldoBensEstoque(entity);
            contabilizarBensEstoque(entity);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
        return entity;
    }

    private void gerarSaldoBensEstoque(BensEstoque entity) {

        if (!entity.getOperacoesBensEstoque().equals(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE)) {
            saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
                entity.getUnidadeOrganizacional(),
                entity.getGrupoMaterial(),
                entity.getValor(),
                entity.getTipoEstoque(),
                entity.getDataBem(),
                entity.getOperacoesBensEstoque(),
                entity.getTipoLancamento(),
                TipoOperacao.DEBITO,
                entity.getId(),
                true);

            saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
                entity.getUnidadeOrganizacional(),
                entity.getGrupoMaterial(),
                entity.getValor(),
                entity.getTipoEstoque(),
                entity.getDataBem(),
                entity.getOperacoesBensEstoque(),
                entity.getTipoLancamento(),
                TipoOperacao.CREDITO,
                entity.getId(),
                true);
        }
    }

    @Override
    public void salvar(BensEstoque entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    private void contabilizarBensEstoque(BensEstoque entity) {
        try {
            if (!entity.getOperacoesBensEstoque().equals(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE)) {
                ConfigBensEstoque configuracao = configBensEstoqueFacade.recuperaEventoPorTipoOperacao(entity.getOperacoesBensEstoque(), entity.getTipoLancamento(), entity.getDataBem());
                if (configuracao != null && configuracao.getEventoContabil() != null) {
                    entity.setEventoContabil(configuracao.getEventoContabil());
                } else {
                    throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Bens de Estoque. ");
                }

                if (configuracao != null && configuracao.getId() != null) {
                    entity.gerarHistoricos();
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

                    List<ObjetoParametro> objetos = Lists.newArrayList();
                    objetos.add(new ObjetoParametro(entity.getId().toString(), BensEstoque.class.getSimpleName(), item));
                    objetos.add(new ObjetoParametro(entity.getGrupoMaterial().getId().toString(), GrupoMaterial.class.getSimpleName(), item));
                    objetos.add(new ObjetoParametro(entity.getOperacoesBensEstoque().toString(), TipoOperacaoBensEstoque.class.getSimpleName(), item));

                    item.setObjetoParametros(objetos);

                    parametroEvento.getItensParametrosEvento().add(item);

                    contabilizacaoFacade.contabilizar(parametroEvento);

                } else {
                    throw new ExcecaoNegocioGenerica("Não existe configuração de Bens de Estoque");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public ConfigBensEstoqueFacade getConfigBensEstoqueFacade() {
        return configBensEstoqueFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public TipoBaixaBensFacade getTipoBaixaBensFacade() {
        return tipoBaixaBensFacade;
    }

    public TipoIngressoFacade getTipoIngressoFacade() {
        return tipoIngressoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarBensEstoque((BensEstoque) entidadeContabil);
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(BensEstoque.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataBem)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataBem)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_ESTOQUE, "obj.operacoesBensEstoque");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.TIPO_ESTOQUE, "obj.tipoEstoque");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.GRUPO_MATERIAL, "obj.grupoMaterial_id");
        return Arrays.asList(consulta);
    }
}
