/*
 * Codigo gerado automaticamente em Wed Jun 15 12:16:05 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ProducaoFacade extends AbstractFacade<Producao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Producao producao;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private IntegradorMateriaisContabilFacade integradorMateriaisContabilFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProducaoFacade() {
        super(Producao.class);
    }

    public void removerProducaoManualmente(Producao producao) {
        reajustarEstoqueERemoverDependencias(producao);
        em.flush();
        removerProducao(producao);
    }

    @Override
    public Producao recuperar(Object id) {
        Producao p = super.recuperar(id);
        p.getItemsProduzidos().size();
        return p;
    }

    public void salvarNovoAlternativo(Producao p) throws ExcecaoNegocioGenerica, ParseException, OperacaoEstoqueException, Exception {
        this.producao = p;
        em.persist(producao);

        EntradaProducao entrada = gerarEntradaComSeusItens();
        entradaMaterialFacade.salvarNovo(entrada);
        entradaMaterialFacade.gerarMovimentoEstoqueDeCadaItemEntrada(entrada);

        SaidaProducao saida = gerarSaidaComSeusItens();
        saidaMaterialFacade.salvarNovo(saida);
        saidaMaterialFacade.setarValorFinanceiroMovimentacaoEstoque(saida);

        this.producao = em.merge(p);
        integradorMateriaisContabilFacade.contabilizarProducaoBensEstoque(producao, "Produção de Materiais n°:" + entrada.getNumero(), Boolean.FALSE);
    }

    private void corrigirEstoquesModificadosPelaEntrada(Producao producao) {
        EntradaMaterial entrada = producao.getEntradaGerada();
        entrada.setItens(entradaMaterialFacade.recuperarItensEntradaMaterial(entrada));

        for (ItemEntradaMaterial iem : entrada.getItens()) {
            MovimentoEstoque movto = iem.getMovimentoEstoque();
            Estoque estoque = movto.getEstoque();

            /*A entrada quando criada gerou um acréscimo de saldo físico no estoque.
            Agora que será excluída, o saldo acrescido anteriormente deve ser DEcrescido.
             */
            corrigirValorFisicoEstoque(estoque, movto.getFisico(), producao.getDataProducao(), "-");

            if (estoque.getMaterial().getControleDeLote()) {
                corrigirValorFisicoEstoqueLote(estoque, iem.getLoteMaterial(), movto.getFisico(), producao.getDataProducao(), "-");
            }

            BigDecimal financeiro;

            if (movto.getFinanceiroReajustado() != null) {
                financeiro = movto.getFinanceiroReajustado();
            } else {
                financeiro = movto.getFinanceiro();
            }

            corrigirValorFinanceiroEstoque(estoque, financeiro, producao.getDataProducao(), "-");
        }
    }

    private void corrigirEstoquesModificadosPelaSaida(Producao producao) {
        SaidaMaterial saida = producao.getSaidaGerada();
        saida.setListaDeItemSaidaMaterial(saidaMaterialFacade.buscarItensSaidaMaterial(saida));

        for (ItemSaidaMaterial ism : saida.getListaDeItemSaidaMaterial()) {
            MovimentoEstoque movto = ism.getMovimentoEstoque();
            Estoque estoque = movto.getEstoque();

            /*A saída quando criada gerou um decréscimo de saldo físico e financeiro no estoque.
            Agora que será excluída, o saldo decrescido anteriormente deve ser adicionado.
             */
            corrigirValorFisicoEstoque(estoque, movto.getFisico(), producao.getDataProducao(), "+");

            if (estoque.getMaterial().getControleDeLote()) {
                corrigirValorFisicoEstoqueLote(estoque, ism.getLoteMaterial(), movto.getFisico(), producao.getDataProducao(), "+");
            }

            BigDecimal financeiro;

            if (movto.getFinanceiroReajustado() != null) {
                financeiro = movto.getFinanceiroReajustado();
            } else {
                financeiro = movto.getFinanceiro();
            }

            corrigirValorFinanceiroEstoque(estoque, financeiro, producao.getDataProducao(), "+");
        }
    }

    private EntradaProducao gerarEntradaComSeusItens() {
        EntradaProducao entradaProducao;

        entradaProducao = criarNovaEntradaPreenchida();
        entradaProducao.setItens(gerarItensEntrada(entradaProducao));

        return entradaProducao;
    }

    private SaidaProducao gerarSaidaComSeusItens() {
        SaidaProducao saidaProducao;

        saidaProducao = criarNovaSaidaPreenchida();
        saidaProducao.setListaDeItemSaidaMaterial(gerarItensSaida(saidaProducao));

        return saidaProducao;
    }

    private EntradaProducao criarNovaEntradaPreenchida() {
        EntradaProducao entrada = new EntradaProducao();

        entrada.setDataEntrada(producao.getDataProducao());
        entrada.setNumero(singletonGeradorCodigo.getProximoCodigo(EntradaProducao.class, "numero"));
        entrada.setProducao(producao);
        entrada.setResponsavel(sistemaFacade.getUsuarioCorrente().getPessoaFisica());
        return entrada;
    }

    private SaidaProducao criarNovaSaidaPreenchida() {
        SaidaProducao saida = new SaidaProducao();

        saida.setDataSaida(producao.getDataProducao());
        saida.setNumero(singletonGeradorCodigo.getProximoCodigo(SaidaProducao.class, "numero"));
        saida.setProducao(producao);
        saida.setUsuario(sistemaFacade.getUsuarioCorrente());

        return saida;
    }

    private List<ItemEntradaMaterial> gerarItensEntrada(EntradaProducao entradaProducao) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");
        List<ItemEntradaMaterial> itensEntrada = new ArrayList<ItemEntradaMaterial>();

        for (ItemProduzido ip : producao.getItemsProduzidos()) {
            ItemEntradaMaterial iem = new ItemEntradaMaterial();

            iem.setEntradaMaterial(entradaProducao);
            iem.setItemProduzido(ip);
            iem.setLocalEstoqueOrcamentario(ip.getLocalEstoqueOrcamentario());
            iem.setLoteMaterial(ip.getLoteMaterial());
            iem.setMaterial(ip.getMaterial());
            iem.setQuantidade(ip.getQuantidade());
            iem.setQuantidadeDevolvida(BigDecimal.ZERO);
            iem.setValorUnitario(ip.getValor());

            ip.setItemEntradaMaterial(iem);
            itensEntrada.add(iem);
        }

        return itensEntrada;
    }

    private List<ItemSaidaMaterial> gerarItensSaida(SaidaProducao saidaProducao) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");
        List<ItemSaidaMaterial> itensSaida = new ArrayList<ItemSaidaMaterial>();

        for (ItemProduzido ip : producao.getItemsProduzidos()) {
            for (InsumoAplicado ia : ip.getInsumosAplicados()) {
                ItemSaidaMaterial ism = new ItemSaidaMaterial();
                InsumoProducao insumo = ia.getInsumoProducao();

                ism.setInsumoProducao(insumo);
//                ism.setTipoEstoque(insumo.getTipoEstoque());
                ism.setLocalEstoqueOrcamentario(insumo.getLocalEstoqueOrcamentario());
                ism.setMaterial(ip.getMaterial());
                ism.setLoteMaterial(insumo.getLoteMaterial());
                ism.setQuantidade(insumo.getQuantidade());
                ism.setSaidaMaterial(saidaProducao);

                insumo.setItemSaidaMaterial(ism);
                itensSaida.add(ism);
            }
        }

        return itensSaida;
    }

    public List<ItemProduzido> recuperarItensProduzidos(Producao producao) {
        String hql = " from ItemProduzido ip "
                + "   where ip.producao = :producao";

        Query q = em.createQuery(hql);
        q.setParameter("producao", producao);

        return q.getResultList();
    }

    public List<InsumoAplicado> recuperarInsumos(ItemProduzido ip) {
        String hql = " from InsumoAplicado ia "
                + "   where ia.itemProduzido = :item";

        Query q = em.createQuery(hql);
        q.setParameter("item", ip);

        return q.getResultList();
    }

    public Producao recuperaUltimaProducaoComDataPosteriorAoArgumento(Date data) {
        String hql = "    select p "
                + "         from Producao p "
                + "        where p.dataProducao > :data "
                + "          and p.dataProducao = ( select max(prod.dataProducao)  "
                + "                                   from Producao prod "
                + "                                  where prod.dataProducao > :data)";

        Query q = em.createQuery(hql);
        q.setParameter("data", data, TemporalType.TIMESTAMP);

        if (!q.getResultList().isEmpty()) {
            return (Producao) q.getSingleResult();
        }

        return null;
    }

    public void validaQuantidadesFuturasDoEstoque(Date date, BigDecimal fisico, Material material, LocalEstoque local) throws OperacaoEstoqueException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String data = sdf.format(date);

        String SQL = " SELECT e.fisico - " + fisico + " "
                + "      FROM estoque e"
                + "     WHERE e.dataestoque > '" + data + "'"
                + "       AND e.material_id = " + material.getId()
                + "       AND e.localestoque_id = " + local.getId()
                + "       AND (e.fisico - " + fisico + ") < 0 ";

        Query q = em.createNativeQuery(SQL);
        List<BigDecimal> retornoQuery = q.getResultList();

        if (!q.getResultList().isEmpty()) {
            throw new OperacaoEstoqueException("Esta operação retroativa fará com que as quantidades futuras do item "
                    + material.getDescricao().toUpperCase() + " sejam negativas.");
        }
    }

    public void validaQuantidadesFuturasDoEstoqueLote(Date date, BigDecimal fisico, Material material, LocalEstoque local, LoteMaterial loteMaterial) throws OperacaoEstoqueException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String data = sdf.format(date);

        String SQL = " SELECT estoquelote.quantidade - " + fisico
                + "              FROM estoquelotematerial estoquelote "
                + "        INNER JOIN estoque e ON estoquelote.estoque_id = e.id "
                + "        INNER JOIN material m ON e.material_id = m.id "
                + "             WHERE m.id = " + material.getId() + " "
                + "               AND estoquelote.lotematerial_id=  " + loteMaterial.getId() + ""
                + "               AND e.dataestoque > '" + data + "'"
                + "               AND e.localestoque_id = " + local.getId()
                + "               AND estoquelote.quantidade - " + fisico + " < 0";

        Query q = em.createNativeQuery(SQL);
        List<BigDecimal> retornoQuery = q.getResultList();

        if (!q.getResultList().isEmpty()) {
            throw new OperacaoEstoqueException("Esta operação retroativa fará com que as quantidades futuras do lote "
                    + loteMaterial.getIdentificacao().toUpperCase() + ", do item " + material.getDescricao().toUpperCase() + ", sejam negativas.");
        }
    }

    public void validaValoresFinanceirosFuturos(Date date, BigDecimal financeiro, Material material, LocalEstoque local) throws OperacaoEstoqueException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String data = sdf.format(date);

        String SQL = " SELECT e.financeiro - " + financeiro + " "
                + "      FROM estoque e"
                + "     WHERE e.dataestoque > '" + data + "'"
                + "       AND e.material_id = " + material.getId()
                + "       AND e.localestoque_id = " + local.getId()
                + "       AND (e.financeiro - " + financeiro + ") < 0 ";

        Query q = em.createNativeQuery(SQL);
        List<BigDecimal> retornoQuery = q.getResultList();

        if (!q.getResultList().isEmpty()) {
            throw new OperacaoEstoqueException("Esta operação retroativa fará com que os valores futuros do item "
                    + material.getDescricao().toUpperCase() + " sejam negativos.");
        }
    }

    private void reajustarEstoqueERemoverDependencias(Producao producao) {
        removerMovimentoEstoqueItemProduzido(producao);
        removerMovimentoEstoqueInsumoProducao(producao);
        removerInsumoAplicado(producao);

        reajustarEstoque(producao);

        entradaMaterialFacade.remover(producao.getEntradaGerada());
        saidaMaterialFacade.remover(producao.getSaidaGerada());
    }

    private void removerInsumoAplicado(Producao producao) {
        for (ItemProduzido itemProduzido : producao.getItemsProduzidos()) {
            for (InsumoAplicado insumoAplicado : itemProduzido.getInsumosAplicados()) {
                removerInsumoAplicadoRealmente(insumoAplicado);
                removerInsumoProducao(insumoAplicado);
            }
            removerItemProduzido(itemProduzido);
        }
    }

    private void removerInsumoAplicadoRealmente(InsumoAplicado insumoAplicado) {
        String sql = " DELETE FROM insumoaplicado WHERE id = :param";

        Query q = em.createNativeQuery(sql);
        q.setParameter("param", insumoAplicado.getId());

        q.executeUpdate();
    }

    private void removerInsumoProducao(InsumoAplicado insumoAplicado) {
        String sql = " DELETE FROM insumoproducao WHERE id = :param";

        Query q = em.createNativeQuery(sql);
        q.setParameter("param", insumoAplicado.getInsumoProducao().getId());

        q.executeUpdate();
    }

    private void removerItemProduzido(ItemProduzido itemProduzido) {
        String sql = " DELETE FROM itemproduzido WHERE id = :param";

        Query q = em.createNativeQuery(sql);
        q.setParameter("param", itemProduzido.getId());

        q.executeUpdate();
    }

    private void removerMovimentoEstoqueInsumoProducao(Producao producao) {
        for (ItemProduzido itemProduzido : producao.getItemsProduzidos()) {
            for (InsumoAplicado insumoAplicado : itemProduzido.getInsumosAplicados()) {
                String sql = " DELETE FROM movestoqueinsumoproducao WHERE insumoproducao_id = :param";

                Query q = em.createNativeQuery(sql);
                q.setParameter("param", insumoAplicado.getInsumoProducao().getId());

                q.executeUpdate();
            }
        }
    }

    private void removerMovimentoEstoqueItemProduzido(Producao producao) {
        for (ItemProduzido itemProduzido : producao.getItemsProduzidos()) {
            String sql = " DELETE FROM movestoqueitemproduzido WHERE itemproduzido_id = :param";

            Query q = em.createNativeQuery(sql);
            q.setParameter("param", itemProduzido.getId());

            q.executeUpdate();
        }
    }

    private void removerProducao(Producao producao) {
        String sql = " DELETE FROM producao WHERE id = :param";

        Query q = em.createNativeQuery(sql);
        q.setParameter("param", producao.getId());

        q.executeUpdate();
    }

    private void reajustarEstoque(Producao producao) {
        corrigirEstoquesModificadosPelaSaida(producao);
        corrigirEstoquesModificadosPelaEntrada(producao);
    }

    private void corrigirValorFisicoEstoque(Estoque e, BigDecimal fisico, Date dataMovimento, String operacao) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String data = sdf.format(dataMovimento);

        String SQL = " UPDATE estoque e"
                + "       SET e.fisico = (e.fisico " + operacao + " " + fisico + ")"
                + "     WHERE e.dataestoque >= '" + data + "'"
                + "       AND e.material_id = " + e.getMaterial().getId() + " "
                + "       AND e.localEstoqueOrcamentario_id = " + e.getLocalEstoqueOrcamentario().getId();


        Query q = em.createNativeQuery(SQL);
        int executeUpdate = q.executeUpdate();
    }

    private void corrigirValorFisicoEstoqueLote(Estoque estoque, LoteMaterial loteMaterial, BigDecimal fisico, Date dataProducao, String operacao) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String data = sdf.format(dataProducao);

        String SQL = " UPDATE (SELECT estoquelote.quantidade "
                + "              FROM estoquelotematerial estoquelote "
                + "        INNER JOIN estoque e ON estoquelote.estoque_id = e.id "
                + "        INNER JOIN material m ON e.material_id = m.id "
                + "             WHERE m.id = " + estoque.getMaterial().getId() + " "
                + "               AND estoquelote.lotematerial_id=  " + loteMaterial.getId() + ""
                + "               AND e.dataestoque >= '" + data + "'"
                + "               AND e.localEstoqueOrcamentario_id = " + estoque.getLocalEstoqueOrcamentario().getId() + " ) estoquelote "
                + "      SET estoquelote.quantidade = (estoquelote.quantidade " + operacao + " " + fisico + " )";

        Query q = em.createNativeQuery(SQL);
        int executeUpdate = q.executeUpdate();
    }

    private void corrigirValorFinanceiroEstoque(Estoque estoque, BigDecimal financeiro, Date dataProducao, String operacao) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String data = sdf.format(dataProducao);

        String SQL = " UPDATE estoque e"
                + "       SET e.financeiro = (e.financeiro " + operacao + " " + financeiro + ")"
                + "     WHERE e.dataestoque > '" + data + "'"
                + "       AND e.material_id = " + estoque.getMaterial().getId() + " "
                + "       AND e.localEstoqueOrcamentario_id = " + estoque.getLocalEstoqueOrcamentario().getId();

        Query q = em.createNativeQuery(SQL);
        int executeUpdate = q.executeUpdate();
    }
}
