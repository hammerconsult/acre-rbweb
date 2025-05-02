package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class LoteEfetivacaoLevantamentoImovelFacade extends AbstractFacade<LoteEfetivacaoLevantamentoImovel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private BemFacade bemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;

    public LoteEfetivacaoLevantamentoImovelFacade() {
        super(LoteEfetivacaoLevantamentoImovel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public LoteEfetivacaoLevantamentoImovel salvarAlternativo(LoteEfetivacaoLevantamentoImovel lote, List<LevantamentoBemImovel> itens) {

        ParametroPatrimonio parametroPatrimonio = parametroPatrimonioFacade.recuperarParametroComDependenciasEntidadeGeradoCodigo();
        if (parametroPatrimonio == null) {
            throw new ExcecaoNegocioGenerica("Parâmetro do patrimônio não encontrato para salvar o levantamento.");
        }
        for (LevantamentoBemImovel item : itens) {
            EfetivacaoLevantamentoImovel ef = new EfetivacaoLevantamentoImovel();
            ef.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
            ef.setLevantamentoBemImovel(item);
            ef.setLoteEfetivacaoImovel(lote);
            Entidade entidade = entidadeFacade.recuperarEntidadePorUnidadeOrcamentaria(lote.getUnidadeOrcamentaria());
            Bem bem = new Bem(item);
            bem.setSeguradora(item.getSeguradora());
            bem.setGarantia(item.getGarantia());
            bem.setIdentificacao(singletonGeradorCodigoPatrimonio.getProximoCodigo(entidade, TipoBem.IMOVEIS, parametroPatrimonio).toString());
            ef.setBem(em.merge(bem));
            ef.setEstadoInicial(null);
            EstadoBem resultante = bemFacade.criarNovoEstadoResultanteAPartirDoCaracterizadorDeBemImovel(item);
            resultante.setIdentificacao(bem.getIdentificacao());
            resultante.setTipoGrupo(TipoGrupo.BEM_IMOVEL_PRINCIPAL);
            resultante = em.merge(resultante);
            ef.setEstadoResultante(resultante);
            lote.getItensEfetivacaoImovel().add(ef);
            bemFacade.salvarPortal(ef.getBem());
        }
        return em.merge(lote);
    }

    @Override
    public LoteEfetivacaoLevantamentoImovel recuperar(Object id) {
        LoteEfetivacaoLevantamentoImovel lote = super.recuperar(id);
        lote.getItensEfetivacaoImovel().size();
        if (lote.getDetentorArquivoComposicao() != null) {
            lote.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return lote;
    }

    public SingletonGeradorCodigoPatrimonio getSingletonGeradorCodigoPatrimonio() {
        return singletonGeradorCodigoPatrimonio;
    }
}
