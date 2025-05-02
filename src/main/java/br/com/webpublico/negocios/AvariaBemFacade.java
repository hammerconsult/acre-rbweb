package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/11/14
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AvariaBemFacade extends AbstractFacade<AvariaBem> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ConfigBensMoveisFacade configBensMoveisFacade;
    @EJB
    private ConfigBensImoveisFacade configBensImoveisFacade;
    @EJB
    private ConfigBensIntangiveisFacade configBensIntangiveisFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    public AvariaBemFacade() {
        super(AvariaBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    @Override
    public void salvarNovo(AvariaBem avariaBem) {
        integradorPatrimonialContabilFacade.ajustarPerda(avariaBem, "Ajuste de perda do bem " + avariaBem.getBem().getIdentificacao() + " - " + DataUtil.getDataFormatada(avariaBem.getDataLancamento()));
        EventoBem ultimoEvento = bemFacade.recuperarUltimoEventoBem(avariaBem.getBem());
        avariaBem.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        avariaBem.setEstadoResultante(em.merge(avariaBem.getEstadoResultante()));
        avariaBem.setCodigo(singletonGeradorCodigo.getProximoCodigo(AvariaBem.class, "codigo"));
        super.salvarNovo(avariaBem);
    }

    public void estornarAvariaBem(AvariaBem avariaBem) {
        EstornoAvariaBem estornoAvariaBem = new EstornoAvariaBem(avariaBem);

        EstadoBem estadoBem = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(bemFacade.recuperarUltimoEstadoDoBem(estornoAvariaBem.getBem()));
        if (estornoAvariaBem.getEstadoResultante().getValorAcumuladoDeAjuste().compareTo(estadoBem.getValorAcumuladoDeAjuste()) > 0) {
            throw new ExcecaoNegocioGenerica("O saldo de ajuste de perda é menor que o valor do estorno.");
        }
        estadoBem.setValorAcumuladoDeAjuste(estadoBem.getValorAcumuladoDeAjuste().subtract(estornoAvariaBem.getAvariaBem().getValorAvaria()));
        integradorPatrimonialContabilFacade.ajustarPerda(estornoAvariaBem, "Estorno de ajuste de perda do bem " + estornoAvariaBem.getBem() + " - " + DataUtil.getDataFormatada(estornoAvariaBem.getDataLancamento()));

        estadoBem = em.merge(estadoBem);
        estornoAvariaBem.setEstadoResultante(estadoBem);
        em.persist(estornoAvariaBem);
    }

    public SolicitacaoAlienacao buscarSolicitacaoAlienacaoPorBem(Bem bem) {
        String sql = "SELECT " +
            "    SOLICITACAOALIENACAO.*  " +
            " FROM SOLICITACAOALIENACAO " +
            " INNER JOIN ItemSolicitacaoAlienacao ITEM ON ITEM.SOLICITACAOALIENACAO_ID = SOLICITACAOALIENACAO.ID " +
            " INNER JOIN EVENTOBEM EV ON EV.ID = ITEM.ID " +
            " WHERE SOLICITACAOALIENACAO.SITUACAO <> :REJEITADA " +
            " AND EV.BEM_ID = :bem";
        Query query = em.createNativeQuery(sql, SolicitacaoAlienacao.class);
        query.setParameter("bem", bem.getId());
        query.setParameter("REJEITADA", SituacaoAlienacao.REJEITADA.name());
        try {
            return (SolicitacaoAlienacao) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public AvariaBem recuperar(Object id) {
        AvariaBem avariaBem = em.find(AvariaBem.class, id);
        avariaBem.setHierarquiaOrganizacional(bemFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(avariaBem.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        avariaBem.setBem(bemFacade.recuperar(avariaBem.getBem().getId()));
        if (avariaBem.getDetentorArquivoComposicao() != null) {
            avariaBem.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return avariaBem;
    }

    public String buscarDescricaoEventoContabil(AvariaBem avariaBem) {
        ConfigBensMoveis configBensMoveis = null;
        ConfigBensImoveis configBensImoveis = null;
        ConfigBensIntangiveis configuracao = null;
        String retorno = "Nenhum evento encontrado";
        TipoLancamento tipoLancamento = avariaBem.getEstorno() ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL;
        if (TipoBem.MOVEIS.equals(avariaBem.getTipoBem())) {
            try {
                configBensMoveis = configBensMoveisFacade.buscarEventoContabilPorOperacaoLancamentoAndDataMov(TipoOperacaoBensMoveis.REDUCAO_VALOR_RECUPERAVEL_BENS_MOVEIS, tipoLancamento, avariaBem.getDataDoLancamento());
                if (configBensMoveis != null) {
                    retorno = configBensMoveis.getEventoContabil().toString();
                }
            } catch (ExcecaoNegocioGenerica ex) {
                retorno = ex.getMessage();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        if (TipoBem.IMOVEIS.equals(avariaBem.getTipoBem())) {
            try {
                configBensImoveis = configBensImoveisFacade.recuperaEventoPorTipoOperacao(TipoOperacaoBensImoveis.REDUCAO_VALOR_RECUPERAVEL_BENS_IMOVEIS, tipoLancamento, avariaBem.getDataDoLancamento());
                if (configBensImoveis != null) {
                    retorno = configBensImoveis.getEventoContabil().toString();
                }
            } catch (ExcecaoNegocioGenerica ex) {
                retorno = ex.getMessage();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        if (TipoBem.INTANGIVEIS.equals(avariaBem.getTipoBem())) {
            try {
                configuracao = configBensIntangiveisFacade.recuperaEventoPorTipoOperacao(TipoOperacaoBensIntangiveis.REDUCAO_VALOR_RECUPERAVEL_BENS_INTANGIVEIS, tipoLancamento, avariaBem.getDataDoLancamento());
                if (configuracao != null) {
                    retorno = configuracao.getEventoContabil().toString();
                }
            } catch (ExcecaoNegocioGenerica ex) {
                retorno = ex.getMessage();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return retorno;
    }

    public String buscarDescricaoTipoOperacao(AvariaBem avariaBem) {
        String retorno = "";
        if (TipoBem.MOVEIS.equals(avariaBem.getTipoBem())) {
            return TipoOperacaoBensMoveis.REDUCAO_VALOR_RECUPERAVEL_BENS_MOVEIS.getDescricao();
        }
        if (TipoBem.IMOVEIS.equals(avariaBem.getTipoBem())) {
            return TipoOperacaoBensImoveis.REDUCAO_VALOR_RECUPERAVEL_BENS_IMOVEIS.getDescricao();
        }
        if (TipoBem.INTANGIVEIS.equals(avariaBem.getTipoBem())) {
            return TipoOperacaoBensIntangiveis.REDUCAO_VALOR_RECUPERAVEL_BENS_INTANGIVEIS.getDescricao();
        }
        return retorno;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public IntegradorPatrimonialContabilFacade getIntegradorPatrimonialContabilFacade() {
        return integradorPatrimonialContabilFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public void setSingletonBloqueioPatrimonio(SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio) {
        this.singletonBloqueioPatrimonio = singletonBloqueioPatrimonio;
    }
}
