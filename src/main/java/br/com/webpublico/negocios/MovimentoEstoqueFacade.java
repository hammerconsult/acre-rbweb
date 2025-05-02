/*
 * Codigo gerado automaticamente em Tue May 24 11:05:44 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Estoque;
import br.com.webpublico.entidades.EstoqueLoteMaterial;
import br.com.webpublico.entidades.MovimentoEstoque;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.interfaces.ItemMovimentoEstoque;
import br.com.webpublico.negocios.administrativo.dao.JdbcReprocessamentoEstoque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Stateless
public class MovimentoEstoqueFacade extends AbstractFacade<MovimentoEstoque> {

    protected static final Logger logger = LoggerFactory.getLogger(MovimentoEstoqueFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EstoqueFacade estoqueFacade;
    private JdbcReprocessamentoEstoque jdbcReprocessamentoEstoque;

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcReprocessamentoEstoque = (JdbcReprocessamentoEstoque) ap.getBean("jdbcReprocessamentoEstoque");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MovimentoEstoqueFacade() {
        super(MovimentoEstoque.class);
    }

    public MovimentoEstoque criarMovimentoEstoque(ItemMovimentoEstoque item, Date dataMovimento) throws OperacaoEstoqueException {
        return criarMovimentoEstoque(item, false, dataMovimento);
    }

    public MovimentoEstoque criarMovimentoEstoque(ItemMovimentoEstoque item) throws OperacaoEstoqueException {
        return criarMovimentoEstoque(item, false, item.getDataMovimento());
    }

    public MovimentoEstoque criarMovimentoEstoque(ItemMovimentoEstoque item, boolean reprocessamento, Date dataMovimento) throws OperacaoEstoqueException {
        Estoque estoque = null;
        try {
            estoque = estoqueFacade.recuperarEstoque(item.getLocalEstoqueOrcamentario(), item.getMaterial(), item.getDataMovimento());
        } catch (OperacaoEstoqueException eng) {
            logger.info("Estoque não encontrado para o material " + item.getMaterial());
        }
        EstoqueLoteMaterial estoqueLote = recuperarLoteMaterial(item, estoque);

        if (estoque == null) {
            estoque = criarNovoEstoque(item, reprocessamento);

            if (item.getMaterial().getControleDeLote()) {
                estoqueLote = crirarEstoqueLoteMaterialPorControleLote(item, estoque);
                estoque.getLotesMaterial().add(estoqueLote);
            }
        } else if (estoque.getBloqueado()) {
            validarEstoqueBloqueado(item, reprocessamento);
        } else if (!estoque.foiCriadoNaData(item.getDataMovimento())) {
            estoque = new Estoque(estoque, item.getDataMovimento()); // cria novo estoque com base no último encontrado

            if (item.getMaterial().getControleDeLote() && estoqueLote != null) {
                estoqueLote = estoque.getEstoqueLoteMaterialDoLote(estoqueLote.getLoteMaterial()); // recupera novo estoque LOTE com base nos últimos criados
            }
        }
        if (item.getMaterial().getControleDeLote() && estoqueLote == null) {
            estoqueLote = crirarEstoqueLoteMaterialPorControleLote(item, estoque);
            estoque.getLotesMaterial().add(estoqueLote);
        }
        if (reprocessamento) {
            MovimentoEstoque movimentoEstoque = new MovimentoEstoque(item, estoque);
            movimentoEstoque.setDataMovimento(dataMovimento);
            atualizarEstoqueLotePorControleLote(item, estoqueLote);
            estoque.atualizarValores(item);
            return jdbcReprocessamentoEstoque.inserir(movimentoEstoque, estoque, estoqueLote);
        }
        atualizarEstoqueLotePorControleLote(item, estoqueLote);
        estoque.atualizarValores(item);
        estoque = em.merge(estoque);
        MovimentoEstoque movimentoEstoque = new MovimentoEstoque(item, estoque);
        movimentoEstoque.setDataMovimento(dataMovimento);
        return em.merge(movimentoEstoque);
    }

    private void validarEstoqueBloqueado(ItemMovimentoEstoque item, boolean reprocessamento) throws OperacaoEstoqueException {
        if (reprocessamento) {
            logger.error("O estoque do material " + item.getMaterial() + " no local de estoque " + item.getLocalEstoqueOrcamentario().getLocalEstoque() + " está bloqueado no momento.");
        } else {
            throw new OperacaoEstoqueException("O estoque do material " + item.getMaterial() + " no local de estoque " + item.getLocalEstoqueOrcamentario().getLocalEstoque() + " está bloqueado no momento.");
        }
    }

    private Estoque criarNovoEstoque(ItemMovimentoEstoque item, boolean reprocessamento) throws OperacaoEstoqueException {
        validarSePodeCriarEstoque(item, reprocessamento);
        return new Estoque(item.getLocalEstoqueOrcamentario(), item.getMaterial(), item.getDataMovimento(), item.getTipoEstoque());
    }

    private EstoqueLoteMaterial atualizarEstoqueLotePorControleLote(ItemMovimentoEstoque item, EstoqueLoteMaterial estoqueLote) {
        if (item.getMaterial().getControleDeLote()) {
            estoqueLote.atualizarValores(item);
        }
        return estoqueLote;
    }

    private EstoqueLoteMaterial crirarEstoqueLoteMaterialPorControleLote(ItemMovimentoEstoque item, Estoque estoque) {
        return new EstoqueLoteMaterial(estoque, item.getLoteMaterial());
    }

    private void validarSePodeCriarEstoque(ItemMovimentoEstoque item, boolean reprocessamento) throws OperacaoEstoqueException {
        if (item.getTipoOperacao().isDebito()) {
            if (reprocessamento) {
                logger.error("Não existe estoque do material " + item.getMaterial().getDescricao() + " no local de estoque " + item.getLocalEstoqueOrcamentario().getLocalEstoque().toString() + ".");
            } else {
                throw new OperacaoEstoqueException("Não existe estoque do material " + item.getMaterial().getDescricao() + " no local de estoque " + item.getLocalEstoqueOrcamentario().getLocalEstoque().toString() + ".");
            }
        }
    }

    private EstoqueLoteMaterial recuperarLoteMaterial(ItemMovimentoEstoque item, Estoque estoque) {
        EstoqueLoteMaterial estoqueLote = null;
        if (item.getMaterial().getControleDeLote() && estoque != null) {
            estoqueLote = estoque.getEstoqueLoteMaterialDoLote(item.getLoteMaterial());
        }
        return estoqueLote;
    }
}
