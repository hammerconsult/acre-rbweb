/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author Claudio
 */
@Stateless
public class ProvAtuarialMatematicaFacade extends SuperFacadeContabil<ProvAtuarialMatematica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoPassivoAtuarialFacade tipoPassivoAtuarialFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private ConfigProvMatematicaFacade configProvMatematicaFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ProvAtuarialMatematicaFacade() {
        super(ProvAtuarialMatematica.class);
    }

    public TipoPassivoAtuarialFacade getTipoPassivoAtuarialFacade() {
        return tipoPassivoAtuarialFacade;
    }

    public String getUltimoNumero() {
        String sql = "SELECT max(to_number(numero))+1 AS ultimoNumero FROM ProvAtuarialMatematica";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public boolean existeProvAtuarialMatematicaComNumero(String numero) {
        String sql = "SELECT * FROM ProvAtuarialMatematica WHERE numero = :numero";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numero", numero);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public BigDecimal getSaldoProvAtuarialMatematica(ProvAtuarialMatematica provAtuarialMatematica) {
        return getSaldoNormal(provAtuarialMatematica).subtract(getSaldoEstorno(provAtuarialMatematica));
    }

    public BigDecimal getSaldoEstorno(ProvAtuarialMatematica provAtuarialMatematica) {

        String sql = "SELECT coalesce(sum(valorlancamento),0) "
                + "FROM ProvAtuarialMatematica "
                + "WHERE tipolancamento = 'ESTORNO' "
                + "AND tipooperacaoatuarial = :tipooperacao "
                + "AND unidadeorganizacional_id = :unidade "
                + "AND tipopassivoatuarial_id = :tipopassivo "
                + "AND tipoplano = :tipoplano "
                + "AND tipoprovisao = :tipoprovisao "
                + "AND datalancamento <= :data";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipooperacao", provAtuarialMatematica.getTipoOperacaoAtuarial().name());
        q.setParameter("unidade", provAtuarialMatematica.getUnidadeOrganizacional().getId());
        q.setParameter("tipopassivo", provAtuarialMatematica.getTipoPassivoAtuarial().getId());
        q.setParameter("tipoplano", provAtuarialMatematica.getTipoPlano().name());
        q.setParameter("tipoprovisao", provAtuarialMatematica.getTipoProvisao().name());
        q.setParameter("data", provAtuarialMatematica.getDataLancamento());

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return new BigDecimal(BigInteger.ZERO);
    }

    public BigDecimal getSaldoNormal(ProvAtuarialMatematica provAtuarialMatematica) {
        String sql = "SELECT coalesce(sum(valorlancamento),0) "
                + "FROM ProvAtuarialMatematica "
                + "WHERE tipolancamento = 'NORMAL' "
                + "AND tipooperacaoatuarial = :tipooperacao "
                + "AND unidadeorganizacional_id = :unidade "
                + "AND tipopassivoatuarial_id = :tipopassivo "
                + "AND tipoplano = :tipoplano "
                + "AND tipoprovisao = :tipoprovisao "
                + "AND datalancamento <= :data";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipooperacao", provAtuarialMatematica.getTipoOperacaoAtuarial().name());
        q.setParameter("unidade", provAtuarialMatematica.getUnidadeOrganizacional().getId());
        q.setParameter("tipopassivo", provAtuarialMatematica.getTipoPassivoAtuarial().getId());
        q.setParameter("tipoplano", provAtuarialMatematica.getTipoPlano().name());
        q.setParameter("tipoprovisao", provAtuarialMatematica.getTipoProvisao().name());
        q.setParameter("data", provAtuarialMatematica.getDataLancamento());

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return new BigDecimal(BigInteger.ZERO);
    }

    public void contabilizarProvisao(ProvAtuarialMatematica entity) throws ExcecaoNegocioGenerica {
        ConfigProvMatematicaPrev configProvMatematicaPrev = configProvMatematicaFacade.recuperaEvento(entity);
        if (configProvMatematicaPrev != null && configProvMatematicaPrev.getEventoContabil() != null) {
            entity.setEventoContabil(configProvMatematicaPrev.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Provisão Matemática Previdenciária.");
        }
        entity.gerarHistoricos();

        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataLancamento());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId().toString());

        Calendar instance = Calendar.getInstance();
        instance.setTime(entity.getDataLancamento());
        parametroEvento.setExercicio(contabilizacaoFacade.getExercicioFacade().getExercicioPorAno(instance.get(Calendar.YEAR)));

        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValorLancamento());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);


        List<ObjetoParametro> objetos = Lists.newArrayList();
        if (entity.getTipoPassivoAtuarial() == null) {
            throw new ExcecaoNegocioGenerica("O Tipo Passivo Atuarial está vazio.");
        }
        objetos.add(new ObjetoParametro(entity, item));
        objetos.add(new ObjetoParametro(entity.getTipoPassivoAtuarial(), item));
        objetos.add(new ObjetoParametro(entity.getDividaPublica().getCategoriaDividaPublica(), item));

        item.setObjetoParametros(objetos);


        parametroEvento.getItensParametrosEvento().add(item);

        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    @Override
    public void salvar(ProvAtuarialMatematica entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    @Override
    public void salvarNovo(ProvAtuarialMatematica entity) {

        if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        } else {

            if (entity.getId() == null) {
                entity.setNumero(getUltimoNumero());
                entity.gerarHistoricos();
                em.persist(entity);
            } else {
                entity.gerarHistoricos();
                entity = em.merge(entity);
            }
            contabilizarProvisao(entity);
        }
    }

    public DividaPublicaFacade getDividaPublicaFacade() {
        return dividaPublicaFacade;
    }

    public ConfigProvMatematicaFacade getConfigProvMatematicaFacade() {
        return configProvMatematicaFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarProvisao((ProvAtuarialMatematica) entidadeContabil);
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(ProvAtuarialMatematica.class, filtros);
        consulta.incluirJoinsComplementar(" inner join dividaPublica divida on obj.dividaPublica_id = divida.id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "obj.dataLancamento");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "obj.dataLancamento");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "divida.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "divida.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valorLancamento");
        return Arrays.asList(consulta);
    }
}
