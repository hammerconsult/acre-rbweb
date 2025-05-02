/*
 * Codigo gerado automaticamente em Fri Feb 24 09:19:35 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemConversaoUnidadeMedidaVO;
import br.com.webpublico.enums.SituacaoMovimentoMaterial;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class ConvercaoUnidadeMedidaMaterialFacade extends AbstractFacade<ConversaoUnidadeMedidaMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MovimentoEstoqueFacade movimentoEstoqueFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private LoteMaterialFacade loteMaterialFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private MaterialFacade materialFacade;

    public ConvercaoUnidadeMedidaMaterialFacade() {
        super(ConversaoUnidadeMedidaMaterial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConversaoUnidadeMedidaMaterial recuperar(Object id) {
        ConversaoUnidadeMedidaMaterial entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        return entity;
    }

    @Override
    public ConversaoUnidadeMedidaMaterial salvarRetornando(ConversaoUnidadeMedidaMaterial entity) {
        return super.salvarRetornando(entity);
    }

    public ConversaoUnidadeMedidaMaterial concluir(ConversaoUnidadeMedidaMaterial entity) throws OperacaoEstoqueException {
        entity.setSituacao(SituacaoMovimentoMaterial.CONCLUIDA);
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ConversaoUnidadeMedidaMaterial.class, "numero"));
        }
        gerarMovimentoEstoque(entity);
        return em.merge(entity);
    }


    private void gerarMovimentoEstoque(ConversaoUnidadeMedidaMaterial entity) throws OperacaoEstoqueException {
        Map<LocalEstoque, Map<UnidadeOrganizacional, LocalEstoqueOrcamentario>> mapa = new HashMap<>();
        for (ItemConversaoUnidadeMedidaMaterial itemConversao : entity.getItens()) {
            ItemConversaoUnidadeMedidaVO itemSaida = new ItemConversaoUnidadeMedidaVO(itemConversao, TipoOperacao.DEBITO, itemConversao.getEstoqueSaida().getLocalEstoqueOrcamentario());
            itemConversao.setMovimentoEstoqueSaida(movimentoEstoqueFacade.criarMovimentoEstoque(itemSaida));

            LocalEstoqueOrcamentario localEstoqueOrcamentario = localEstoqueFacade.getLocalEstoqueOrcamentario(mapa, entity.getLocalEstoque(), entity.getUnidadeOrcamentaria(), entity.getDataMovimento());
            ItemConversaoUnidadeMedidaVO itemEntrada = new ItemConversaoUnidadeMedidaVO(itemConversao, TipoOperacao.CREDITO, localEstoqueOrcamentario);
            itemConversao.setMovimentoEstoqueEntrada(movimentoEstoqueFacade.criarMovimentoEstoque(itemEntrada));
        }
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public LoteMaterialFacade getLoteMaterialFacade() {
        return loteMaterialFacade;
    }

    public EstoqueFacade getEstoqueFacade() {
        return estoqueFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }
}
