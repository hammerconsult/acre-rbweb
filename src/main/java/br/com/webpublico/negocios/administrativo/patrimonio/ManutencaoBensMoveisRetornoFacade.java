package br.com.webpublico.negocios.administrativo.patrimonio;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.ManutencaoBemMovelEntrada;
import br.com.webpublico.entidades.administrativo.patrimonio.ManutencaoBemMovelRetorno;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.negocios.ConfigMovimentacaoBemFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by zaca on 15/05/17.
 */
@Stateless
public class ManutencaoBensMoveisRetornoFacade extends AbstractFacade<ManutencaoBemMovelRetorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private ManutencaoBensMoveisEntradaFacade manutencaoBensMoveisEntradaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;

    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    public ManutencaoBensMoveisRetornoFacade() {
        super(ManutencaoBemMovelRetorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return getEm();
    }

    public EntityManager getEm() {
        return em;
    }

    @Override
    public ManutencaoBemMovelRetorno recuperar(Object id) {
        ManutencaoBemMovelRetorno entity = em.find(ManutencaoBemMovelRetorno.class, id);
        if (entity.getDetentorArquivoComposicao() != null) {
            entity.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return entity;
    }

    public ManutencaoBemMovelRetorno salvarRetornoManutencao(ManutencaoBemMovelRetorno entity, ConfigMovimentacaoBem configMovimentacaoBem) {
        EstadoBem estadoInicial = getBemFacade().recuperarUltimoEstadoDoBem(entity.getBem());
        EstadoBem estadoResultante = getBemFacade().criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial);
        entity.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        entity.setEstadoInicial(estadoInicial);
        entity.setEstadoResultante(em.merge(estadoResultante));
        entity.setValorDoLancamento(entity.getValorManutencao());
        Date dataLancamento = DataUtil.getDataComHoraAtual(entity.getRetornoEm());
        entity.setDataOperacao(new Date());
        entity.setDataLancamento(dataLancamento);

        configMovimentacaoBemFacade.desbloquearBens(configMovimentacaoBem, Lists.<Number>newArrayList(entity.getBem().getId()));
        entity = em.merge(entity);

        entity.getBemMovelEntrada().setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        em.merge(entity.getBemMovelEntrada());
        return entity;
    }

    public List<ManutencaoBemMovelEntrada> buscarBensEmManutencao(String filter, TipoBem tipoBem) {
        StringBuilder hql = new StringBuilder();
        hql.append(" SELECT entrada  ")
            .append(" FROM ManutencaoBemMovelEntrada entrada   ")
            .append(" WHERE (lower(entrada.bem.identificacao) like :filter or lower(entrada.bem.descricao) like :filter or TO_CHAR(entrada.inicioEm, 'dd/MM/yyyy') like :filter )  ")            .append("    AND entrada.estadoResultante.grupoBem.tipoBem = :TIPOBEM  ")
            .append("    AND NOT EXISTS (SELECT 1  ")
            .append("                    FROM ManutencaoBemMovelRetorno retorno    ")
            .append("                    where retorno.bemMovelEntrada.id = entrada.id )  ")
            .append(" order by entrada.inicioEm ");
        Query q = em.createQuery(hql.toString(), ManutencaoBemMovelEntrada.class);
        q.setParameter("filter", "% " + filter.trim().toLowerCase() + "%");
        q.setParameter("TIPOBEM", tipoBem);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public ManutencaoBemMovelEntrada buscarEntradaManutencaoBemPorBem(Bem bem, TipoBem tipoBem) {
        StringBuilder hql = new StringBuilder();
        hql.append("select entrada from ManutencaoBemMovelEntrada  entrada  ")
            .append(" inner join entrada.estadoInicial estadoInicial  ")
            .append(" inner join entrada.estadoResultante estadoResultante  ")
            .append(" inner join entrada.bem b  ")
            .append(" inner join estadoResultante.grupoBem grupo  ")
            .append(" where b.id = :IdBem and grupo.tipoBem = :tipoBem and ")
            .append(" not exists (select 1 from ManutencaoBemMovelRetorno retorno  ")
            .append(" inner join retorno.bemMovelEntrada BemEntrada  ")
            .append(" where BemEntrada = entrada) ");
        Query q = em.createQuery(hql.toString());
        q.setParameter("IdBem", bem.getId());
        q.setParameter("tipoBem", tipoBem);
        q.setMaxResults(1);
        if (q.getSingleResult() != null) {
            return (ManutencaoBemMovelEntrada) q.getSingleResult();
        }
        return null;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public ManutencaoBensMoveisEntradaFacade getManutencaoBensMoveisEntradaFacade() {
        return manutencaoBensMoveisEntradaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}
