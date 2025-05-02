package br.com.webpublico.negocios.administrativo.patrimonio;

import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.ManutencaoBemMovelEntrada;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * Created by zaca on 12/05/17.
 */
@Stateless
public class ManutencaoBensMoveisEntradaFacade extends AbstractFacade<ManutencaoBemMovelEntrada> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private BemFacade bemFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    public ManutencaoBensMoveisEntradaFacade() {
        super(ManutencaoBemMovelEntrada.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return getEm();
    }

    @Override
    public ManutencaoBemMovelEntrada recuperar(Object id) {
        ManutencaoBemMovelEntrada manutencaoBemMovel = em.find(ManutencaoBemMovelEntrada.class, id);
        if (manutencaoBemMovel.getDetentorArquivoComposicao() != null) {
            manutencaoBemMovel.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return manutencaoBemMovel;
    }

    public void remover(ManutencaoBemMovelEntrada entity) {
        ConfigMovimentacaoBem configuracao = recuperarConfiguracaoMovimentacaoBem(entity.getDataLancamento());
        configMovimentacaoBemFacade.desbloquearBens(configuracao, Lists.<Number>newArrayList(entity.getBem().getId()));
        super.remover(entity);
    }

    public ConfigMovimentacaoBem recuperarConfiguracaoMovimentacaoBem(Date dataLancamento) {
        ConfigMovimentacaoBem configMovimentacaoBem = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(dataLancamento, OperacaoMovimentacaoBem.MANUTENCAO_BEM_REMESSA);
        if (configMovimentacaoBem != null) {
            return configMovimentacaoBem;
        }
        return null;
    }

    public ManutencaoBemMovelEntrada salvarManutencao(ManutencaoBemMovelEntrada entity, ConfigMovimentacaoBem configMovimentacaoBem) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.debug("salvarManutencao", e);
        }
        EstadoBem estadoInicial = getBemFacade().recuperarUltimoEstadoDoBem(entity.getBem());
        EstadoBem estadoResultante = getBemFacade().criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial);
        entity.setSituacaoEventoBem(SituacaoEventoBem.EM_MANUTENCAO);
        entity.setEstadoInicial(estadoInicial);
        entity.setEstadoResultante(em.merge(estadoResultante));
        Date dataLancamento = DataUtil.getDataComHoraAtual(entity.getInicioEm());
        entity.setDataOperacao(new Date());
        entity.setDataLancamento(dataLancamento);

        configMovimentacaoBemFacade.bloquearUnicoBem(configMovimentacaoBem, entity.getBem().getId());
        entity = em.merge(entity);
        return entity;
    }

    public Boolean isManutencaoEmRetorno(ManutencaoBemMovelEntrada entity) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT entrada.* ")
            .append(" FROM ManutencaoBemMovelEntrada entrada ")
            .append("  INNER JOIN ManutencaoBemMovelRetorno retorno ON retorno.bemMovelEntrada_id = entrada.id ")
            .append(" WHERE entrada.id = :idEntrada ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idEntrada", entity.getId());
        return q.getResultList().isEmpty();
    }

    public Boolean isBemEmManutencao(ManutencaoBemMovelEntrada manutencao) {
        StringBuilder hql = new StringBuilder();
        hql.append(" SELECT entrada  ")
            .append(" FROM ManutencaoBemMovelEntrada entrada  ")
            .append(" WHERE entrada.bem.id = :bem ")
            .append("    and not exists( select 1 from ManutencaoBemMovelRetorno retorno) ")
            .append("    and entrada.tipoManutencao = :tipo ");
        if (manutencao.getId() != null) {
            hql.append(" and entrada.id <> :idManutencao ");
        }
        Query q = em.createQuery(hql.toString());
        if (manutencao.getId() != null) {
            q.setParameter("idManutencao", manutencao.getId());
        }
        q.setParameter("bem", manutencao.getBem().getId());
        q.setParameter("tipo", manutencao.getTipoManutencao());
        return !q.getResultList().isEmpty();
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public EntityManager getEm() {
        return em;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}
