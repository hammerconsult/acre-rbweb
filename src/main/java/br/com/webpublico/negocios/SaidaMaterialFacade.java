/*
 * Codigo gerado automaticamente em Fri Feb 24 09:19:35 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RelatorioSaidaDiretaMateriais;
import br.com.webpublico.enums.SituacaoMovimentoMaterial;
import br.com.webpublico.enums.SituacaoRequisicaoMaterial;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoRequisicaoMaterial;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.singletons.SingletonConcorrenciaMaterial;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class SaidaMaterialFacade extends AbstractFacade<SaidaMaterial> {

    @EJB
    protected IntegradorMateriaisContabilFacade integradorMateriaisContabilFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PoliticaDeEstoqueFacade politicaDeEstoqueFacade;
    @EJB
    private MovimentoEstoqueFacade movimentoEstoqueFacade;
    @EJB
    private InventarioEstoqueFacade inventarioEstoqueFacade;
    @EJB
    private RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private LoteMaterialFacade loteMaterialFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private EstoqueLoteMaterialFacade estoqueLoteMaterialFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private TipoBaixaBensFacade tipoBaixaBensFacade;
    @EJB
    private AprovacaoMaterialFacade aprovacaoMaterialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private SingletonConcorrenciaMaterial singletonConcorrenciaMaterial;

    public SaidaMaterialFacade() {
        super(SaidaMaterial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SaidaMaterial recarregar(SaidaMaterial entity) {
        SaidaMaterial sm = super.recarregar(entity);
        sm.getListaDeItemSaidaMaterial().size();
        return sm;
    }

    @Override
    public SaidaMaterial recuperar(Object id) {
        SaidaMaterial sm = super.recuperar(id);
        Hibernate.initialize(sm.getListaDeItemSaidaMaterial());
        return sm;
    }

    @Override
    public SaidaMaterial salvarRetornando(SaidaMaterial entity) {
        return em.merge(entity);
    }

    public void bloquearRequisicaoMaterialSingleton(SaidaMaterial saidaMaterial) {
        if (saidaMaterial.isSaidaConsumo()) {
            singletonConcorrenciaMaterial.bloquearRequisicao(((SaidaRequisicaoMaterial) saidaMaterial).getRequisicaoMaterial());
        }
    }

    public void desbloquearRequisicaoMaterialSingleton(SaidaMaterial saidaMaterial) {
        if (saidaMaterial.isSaidaConsumo()) {
            singletonConcorrenciaMaterial.desbloquearRequisicao(((SaidaRequisicaoMaterial) saidaMaterial).getRequisicaoMaterial());
        }
    }

    public void validarRequisicaoMaterialBloqueadaSingleton(SaidaMaterial saidaMaterial) {
        if (saidaMaterial.isSaidaConsumo()) {
            singletonConcorrenciaMaterial.validarRequisicaoSingleton(((SaidaRequisicaoMaterial) saidaMaterial).getRequisicaoMaterial());
        }
    }

    public List<ItemSaidaMaterial> buscarItensSaidaMaterial(SaidaMaterial saidaMaterial) {
        String sql = " select * from itemsaidamaterial ism where ism.saidaMaterial_id = :idSaida";
        Query q = em.createNativeQuery(sql, ItemSaidaMaterial.class);
        q.setParameter("idSaida", saidaMaterial.getId());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        }
        return resultList;
    }

    public SaidaMaterialDesincorporacao recuperarSaidaDesincorporacaoPorMaterialNaoConcluida(Material material) {
        String sql = " select sm.*, smd.* from saidamaterial sm " +
            "           inner join saidamatdesincorporacao smd on smd.id = sm.id " +
            "           inner join itemsaidamaterial item on item.saidamaterial_id = sm.id " +
            "         where sm.situacao = :emElaboracao " +
            "          and item.material_id = :idMaterial ";
        Query q = em.createNativeQuery(sql, SaidaMaterialDesincorporacao.class);
        q.setParameter("idMaterial", material.getId());
        q.setParameter("emElaboracao", SituacaoMovimentoMaterial.EM_ELABORACAO.name());
        try {
            return (SaidaMaterialDesincorporacao) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private void atribuirQuantidadeAtendidaItemRequisicaoMaterial(SaidaMaterial saidaMaterial) {
        if (saidaMaterial.isSaidaRequisicao()) {
            if (saidaMaterial.isSaidaConsumo()) {
                ((SaidaRequisicaoMaterial) saidaMaterial).atribuirQuantidadeAtendida();
            }
            if (saidaMaterial.isSaidaTransferencia()) {
                ((SaidaRequisicaoMaterial) saidaMaterial).atribuirQuantidadeEmTransito();
            }
            saidaMaterial.getListaDeItemSaidaMaterial().forEach(ism -> {
                ItemRequisicaoMaterial itemReqMat = em.merge(ism.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial());
                ism.getItemRequisicaoSaidaMaterial().setItemRequisicaoMaterial(itemReqMat);
            });
        }
    }

    private void salvarItemEntradaMaterial(SaidaMaterial saidaMaterial) {
        if (saidaMaterial.isSaidaDevolucao()) {
            for (ItemSaidaMaterial ism : saidaMaterial.getListaDeItemSaidaMaterial()) {
                ism.getItemDevolucaoMaterial().setItemEntradaMaterial(em.merge(ism.getItemDevolucaoMaterial().getItemEntradaMaterial()));
            }
        }
    }

    public void definirValorFinanceiroDeCadaItem(SaidaMaterial saidaMaterial) throws OperacaoEstoqueException {
        for (ItemSaidaMaterial ism : saidaMaterial.getListaDeItemSaidaMaterial()) {
            Estoque estoque = estoqueFacade.recuperarEstoque(ism.getLocalEstoqueOrcamentario(), ism.getMaterial(), sistemaFacade.getDataOperacao());
            BigDecimal custoMedio = calcularCustoMedio(estoque);
            ism.setValorUnitario(custoMedio);
        }
    }

    public BigDecimal obterCustoMedioConsolidado(Material material, LocalEstoque le, Date dataOperacao) throws OperacaoEstoqueException {
        return calcularCustoMedio(estoqueFacade.recuperarEstoquesDaHierarquiaDoLocalDeEstoque(le, material, dataOperacao));
    }

    public BigDecimal calcularCustoMedio(Estoque estoque) {
        if (estoque.getFisico().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return estoque.getFinanceiro().divide(estoque.getFisico(), 8, RoundingMode.HALF_EVEN);
    }

    public BigDecimal calcularCustoMedio(List<Estoque> estoques) {
        BigDecimal fisico = BigDecimal.ZERO;
        BigDecimal financeiro = BigDecimal.ZERO;
        for (Estoque estoque : estoques) {
            fisico = fisico.add(estoque.getFisico());
            financeiro = financeiro.add(estoque.getFinanceiro());
        }
        Double fisicoConsolidado = fisico.doubleValue();
        Double financeiroConsolidado = financeiro.doubleValue();
        if (fisicoConsolidado <= 0) {
            return BigDecimal.ZERO;
        }
        Double resultado = financeiroConsolidado / fisicoConsolidado;
        return new BigDecimal(resultado).setScale(4, RoundingMode.HALF_EVEN);
    }

    private void gerarMovimentoEstoqueDeCadaItem(SaidaMaterial saidaMaterial) throws OperacaoEstoqueException {
        for (ItemSaidaMaterial ism : saidaMaterial.getListaDeItemSaidaMaterial()) {
            ism.setMovimentoEstoque(movimentoEstoqueFacade.criarMovimentoEstoque(ism));//aqui pode ter um problema.
        }
    }

    public SaidaMaterial salvarNovaSaidaMaterial(SaidaMaterial entity) throws ParseException, OperacaoEstoqueException, ExcecaoNegocioGenerica {
        if (entity.getNumero() == null) {
            entity.setNumero(getSingletonGeradorCodigo().getProximoCodigo(SaidaMaterial.class, "numero"));
        }
        entity.setRetroativo(false);
        definirValorFinanceiroDeCadaItem(entity);
        gerarMovimentoEstoqueDeCadaItem(entity);
        atribuirQuantidadeAtendidaItemRequisicaoMaterial(entity);
        salvarItemEntradaMaterial(entity);
        entity = em.merge(entity);
        contabilizar(entity);
        atribuirSituacaoRequisicaoAndFinalizarReservaEstoque(entity);
        return entity;
    }

    public SaidaMaterial concluirSaidaMaterial(SaidaMaterial entity) throws OperacaoEstoqueException, ExcecaoNegocioGenerica {
        bloquearRequisicaoMaterialSingleton(entity);
        entity.setSituacao(SituacaoMovimentoMaterial.CONCLUIDA);
        if (entity.getNumero() == null) {
            entity.setNumero(getSingletonGeradorCodigo().getProximoCodigo(SaidaMaterial.class, "numero"));
        }
        gerarMovimentoEstoqueDeCadaItem(entity);
        atribuirQuantidadeAtendidaItemRequisicaoMaterial(entity);
        entity = em.merge(entity);
        contabilizar(entity);
        atribuirSituacaoRequisicaoAndFinalizarReservaEstoque(entity);
        desbloquearRequisicaoMaterialSingleton(entity);
        return entity;
    }

    public SaidaMaterial salvarSaidaTransferencia(SaidaMaterial entity) throws ParseException, OperacaoEstoqueException {
        validarEstoquePorItem(entity);
        entity.setSituacao(SituacaoMovimentoMaterial.CONCLUIDA);
        entity.setDataConclusao(sistemaFacade.getDataOperacao());
        if (entity.getNumero() == null) {
            entity.setNumero(getSingletonGeradorCodigo().getProximoCodigo(SaidaMaterial.class, "numero"));
        }
        definirValorFinanceiroDeCadaItem(entity);
        for (ItemSaidaMaterial iem : entity.getListaDeItemSaidaMaterial()) {
            iem.setMovimentoEstoque(movimentoEstoqueFacade.criarMovimentoEstoque(iem, entity.getDataSaida()));
        }
        return em.merge(entity);
    }

    public SaidaMaterial salvarSaidaDireta(SaidaMaterial entity) throws ParseException, OperacaoEstoqueException {
        validarEstoquePorItem(entity);
        if (entity.getNumero() == null) {
            entity.setNumero(getSingletonGeradorCodigo().getProximoCodigo(SaidaMaterial.class, "numero"));
        }
        definirValorFinanceiroDeCadaItem(entity);
        gerarMovimentoEstoqueDeCadaItem(entity);
        return em.merge(entity);
    }

    public void validarEstoquePorItem(SaidaMaterial selecionado) throws OperacaoEstoqueException {
        ValidacaoException ve = new ValidacaoException();
        for (ItemSaidaMaterial itemSaidaMaterial : selecionado.getListaDeItemSaidaMaterial()) {
            BigDecimal qtdeSaida = itemSaidaMaterial.getQuantidade();
            BigDecimal qtdeEstoque = BigDecimal.ZERO;
            if (itemSaidaMaterial.getMaterial().getControleDeLote() && itemSaidaMaterial.getLoteMaterial() != null) {
                EstoqueLoteMaterial estoqueLote = estoqueLoteMaterialFacade.recuperarEstoqueLoteMaterial(itemSaidaMaterial.getLoteMaterial(), itemSaidaMaterial.getLocalEstoqueOrcamentario());
                if (estoqueLote != null) {
                    qtdeEstoque = estoqueLote.getQuantidade();
                }
            } else {
                Estoque estoque = estoqueFacade.recuperarEstoque(itemSaidaMaterial.getLocalEstoqueOrcamentario(), itemSaidaMaterial.getMaterial(), sistemaFacade.getDataOperacao());
                qtdeEstoque = estoque.getFisico();
            }
            if (qtdeEstoque.compareTo(qtdeSaida) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade em estoque <b>" + qtdeEstoque + "</b> é menor que a quantidade desejada <b>" + qtdeSaida + "</b> para o item <b>" + itemSaidaMaterial.getDescricao() + "</b>.");
            }
        }
        ve.lancarException();
    }

    public void contabilizar(SaidaMaterial saidaMaterial) throws ExcecaoNegocioGenerica {
        if (saidaMaterial.isSaidaDesincorporacao() || saidaMaterial.isSaidaDevolucao()) {
            integradorMateriaisContabilFacade.contabilizarSaidaPorDesincorporacao(saidaMaterial);

        } else if (saidaMaterial.isSaidaConsumo()) {
            integradorMateriaisContabilFacade.contabilizarSaidaPorConsumo(saidaMaterial);

        } else if (saidaMaterial.isSaidaDireta()) {
            integradorMateriaisContabilFacade.contabilizarSaidaDireta(saidaMaterial);
        }
    }

    private void atribuirSituacaoRequisicaoAndFinalizarReservaEstoque(SaidaMaterial saidaMaterial) {
        if (saidaMaterial instanceof SaidaRequisicaoMaterial) {
            SaidaRequisicaoMaterial saidaReqMat = (SaidaRequisicaoMaterial) saidaMaterial;

            boolean atendidaParcialmente = saidaMaterial.getListaDeItemSaidaMaterial().stream()
                .anyMatch(itemReqMat -> aprovacaoMaterialFacade.isItemAprovado(itemReqMat.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial())
                    && itemReqMat.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().getQuantidadeAAtender().compareTo(BigDecimal.ZERO) != 0);
            saidaReqMat.setRequisicaoMaterial(requisicaoMaterialFacade.atribuirSituacaoRequisicao(saidaReqMat.getRequisicaoMaterial(), atendidaParcialmente));

            em.merge(saidaReqMat.getRequisicaoMaterial());
            finalizarReservaEstoque(saidaReqMat.getRequisicaoMaterial());
        }
    }

    private void finalizarReservaEstoque(RequisicaoMaterial requisicaoMaterial) {
        List<ReservaEstoque> reservas = recuperarReservasEstoqueDaRequisicao(requisicaoMaterial);
        reservas.stream()
            .filter(re -> re.getQuantidadeReservada().compareTo(BigDecimal.ZERO) == 0)
            .forEach(re -> {
                re.setStatusReservaEstoque(ReservaEstoque.StatusReservaEstoque.FINALIZADA);
                em.merge(re);
            });
    }

    private List<ReservaEstoque> recuperarReservasEstoqueDaRequisicao(RequisicaoMaterial requisicaoMaterial) {
        String sql = "SELECT RE.*  " +
            "       FROM REQUISICAOMATERIAL RM " +
            " INNER JOIN ITEMREQUISICAOMATERIAL IRM ON IRM.REQUISICAOMATERIAL_ID = RM.ID " +
            " INNER JOIN ITEMAPROVACAOMATERIAL IAM ON IAM.ITEMREQUISICAOMATERIAL_ID = IRM.ID " +
            " INNER JOIN RESERVAESTOQUE RE ON RE.ITEMAPROVACAOMATERIAL_ID = IAM.ID " +
            "      WHERE RM.ID = :requisicao_id";

        return em.createNativeQuery(sql, ReservaEstoque.class).setParameter("requisicao_id", requisicaoMaterial.getId()).getResultList();
    }

    public void setarValorFinanceiroMovimentacaoEstoque(SaidaMaterial saidaMaterial) throws ParseException, OperacaoEstoqueException {
        definirValorFinanceiroDeCadaItem(saidaMaterial);
        gerarMovimentoEstoqueDeCadaItem(saidaMaterial);
    }

    public EntradaTransferenciaMaterial recuperarEntradaDaSaida(SaidaMaterial saida) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");
        String hql = " select entrada " +
            "        from EntradaTransferenciaMaterial entrada"
            + "     where saidaRequisicaoMaterial.id = :saida";

        Query consulta = em.createQuery(hql);
        consulta.setParameter("saida", saida.getId());
        consulta.setMaxResults(1);

        try {
            return (EntradaTransferenciaMaterial) consulta.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<SaidaMaterial> recuperarSaidasNaoProcessadasAPartirDaData(Date data, UnidadeOrganizacional uo) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");
        String hql = "     select sm "
            + "          from SaidaMaterial sm, ItemSaidaMaterial ism "
            + "    inner join ism.localEstoqueOrcamentario.localEstoque le "
            + "    inner join le.unidadeOrganizacional unidade "
            + "         where sm.dataSaida >= :data "
            + "           and ism.saidaMaterial = sm "
            + "           and unidade = :uo "
            + "      order by sm.dataSaida";

        Query q = em.createQuery(hql);
        q.setParameter("data", data, TemporalType.TIMESTAMP);
        q.setParameter("uo", uo);

        return q.getResultList();
    }


    public List<SaidaMaterial> buscarSaidaMaterialPorTipoRequisicao(String parte, TipoRequisicaoMaterial tipoRequisicao) {
        String sql = " " +
            "   select srm.requisicaomaterial_id, " +
            "           saida.* " +
            "  from saidarequisicaomaterial srm " +
            "         inner join saidamaterial saida on saida.id = srm.id " +
            "         inner join requisicaomaterial req on req.id = srm.requisicaomaterial_id " +
            "   where (saida.numero like :parte or req.numero like :parte or lower(req.descricao) like :parte) " +
            "   and req.tiporequisicao = :tipoRequisicao " +
            "   order by saida.numero desc ";
        Query q = em.createNativeQuery(sql, SaidaRequisicaoMaterial.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tipoRequisicao", tipoRequisicao.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }


    @Override
    public void remover(SaidaMaterial entity) {
        super.remover(entity);
    }

    public List<SaidaRequisicaoMaterial> recuperaSaidasDaRequisicao(RequisicaoMaterial requisicao) {
        String hql = " from SaidaRequisicaoMaterial srm" +
            "     where srm.requisicaoMaterial.id = :requisicao";
        Query q = em.createQuery(hql);
        q.setParameter("requisicao", requisicao.getId());
        List<SaidaRequisicaoMaterial> resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        } else {
            for (SaidaRequisicaoMaterial saida : resultList) {
                saida.getListaDeItemSaidaMaterial().size();
            }
        }
        return resultList;
    }

    public List<LocalEstoque> completaLocalEstoque(String parte, UsuarioSistema usuario) {
        String sql = "WITH PAI (ID, CODIGO, DESCRICAO, FECHADOEM, SUPERIOR_ID, UNIDADEORGANIZACIONAL_ID, PRIMEIRONIVEL_ID)  " +
            "       AS (SELECT LEPAI.ID, " +
            "                  LEPAI.CODIGO," +
            "                  LEPAI.DESCRICAO, " +
            "                  LEPAI.FECHADOEM, " +
            "                  LEPAI.SUPERIOR_ID, " +
            "                  LEPAI.UNIDADEORGANIZACIONAL_ID, " +
            "                  COALESCE(LEPAI.SUPERIOR_ID, LEPAI.ID) " +
            "             FROM LOCALESTOQUE LEPAI " +
            "            WHERE LEPAI.SUPERIOR_ID IS NULL " +
            "        UNION ALL " +
            "           SELECT LEFILHO.ID, " +
            "                  LEFILHO.CODIGO, " +
            "                  LEFILHO.DESCRICAO, " +
            "                  LEFILHO.FECHADOEM, " +
            "                  LEFILHO.SUPERIOR_ID, " +
            "                  LEFILHO.UNIDADEORGANIZACIONAL_ID, " +
            "                  COALESCE(P.PRIMEIRONIVEL_ID, LEFILHO.SUPERIOR_ID) " +
            "             FROM LOCALESTOQUE LEFILHO " +
            "       INNER JOIN PAI P ON P.ID = LEFILHO.SUPERIOR_ID)  " +
            "           SELECT LOCALESTOQUE.* " +
            "             FROM PAI LE " +
            "       INNER JOIN LOCALESTOQUE ON LOCALESTOQUE.ID = LE.ID" +
            "       INNER JOIN GESTORLOCALESTOQUE GLE ON GLE.LOCALESTOQUE_ID = LE.PRIMEIRONIVEL_ID " +
            "            WHERE GLE.USUARIOSISTEMA_ID = :usuario_id " +
            "              AND LOCALESTOQUE.SUPERIOR_ID IS NULL" +
            "              AND (LE.CODIGO LIKE :parte " +
            "                   OR LOWER(LE.DESCRICAO) LIKE :parte)" +
            "         ORDER BY LE.CODIGO";

        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("usuario_id", usuario.getId());
        return q.getResultList();
    }

    public List<SaidaRequisicaoMaterial> completaSaidaTransferenciaDeRequisicoesNaoAtendidasTotalmente(String parte) {
        String sql = "SELECT SM.*,SRM.* " +
            "       FROM SAIDAREQUISICAOMATERIAL SRM " +
            " INNER JOIN SAIDAMATERIAL SM ON SM.ID = SRM.ID " +
            " INNER JOIN REQUISICAOMATERIAL RM ON SRM.REQUISICAOMATERIAL_ID = RM.ID " +
            " INNER JOIN USUARIOSISTEMA US ON US.ID = RM.REQUERENTEEAUTORIZADOR_ID " +
            " INNER JOIN PESSOAFISICA US_PF ON US.PESSOAFISICA_ID = US_PF.ID " +
            " LEFT  JOIN ENTRADATRANSFMATERIAL ENTRADA ON ENTRADA.SAIDAREQUISICAOMATERIAL_ID = SRM.ID " +
            "      WHERE RM.TIPOREQUISICAO = :tipo_requisicao " +
            "        AND RM.TIPOSITUACAOREQUISICAO <> :tipo_situacao_requisicao" +
            "        AND ENTRADA.ID IS NULL" +
            "        AND (to_char(SM.NUMERO) LIKE :parte OR SM.DATASAIDA LIKE :parte OR US_PF.NOME LIKE :parte)" +
            " ORDER BY SM.ID DESC ";
        Query q = em.createNativeQuery(sql, SaidaRequisicaoMaterial.class);
        q.setParameter("tipo_requisicao", TipoRequisicaoMaterial.TRANSFERENCIA.name());
        q.setParameter("tipo_situacao_requisicao", SituacaoRequisicaoMaterial.TRANSFERENCIA_TOTAL_CONCLUIDA.name());
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }

    public boolean verificarSeExistePinCadastrado(String pin) {
        String sql = "select * from saidamaterial where lower(pin) = :pin";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pin", pin.trim().toLowerCase());
        return !q.getResultList().isEmpty();
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    public Long recuperarIdRevisaoAuditoria(Long id) {
        String sql = " select rev.id from saidamaterial_aud aud " +
            " inner join revisaoauditoria rev on rev.id = aud.rev " +
            " where rev.id = (select rev from saidamaterial_aud saud where id = :id and saud.revtype = 0) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Object[] filtarComContadorDeRegistrosSaidaDireta(String sql, String sqlCount, int inicio, int max) throws Exception {
        Query consulta = em.createNativeQuery(sql);
        Query consultaCount = em.createNativeQuery(sqlCount);
        Long count = 0L;
        List<SaidaDireta> list = new ArrayList<>();
        try {
            count = ((BigDecimal) consultaCount.getSingleResult()).longValue();
            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }
            List<Object[]> lista = consulta.getResultList();
            for (Object[] object : lista) {
                SaidaDireta saidaDireta = new SaidaDireta();
                saidaDireta.setId(((BigDecimal) object[0]).longValue());
                saidaDireta.setNumero(((BigDecimal) object[1]).longValue());
                saidaDireta.setDataSaida(((Date) object[2]));
                UsuarioSistema usuarioSistema = new UsuarioSistema();
                PessoaFisica pessoaFisica = pessoaFisicaFacade.recuperar(((BigDecimal) object[3]).longValue());
                usuarioSistema.setPessoaFisica(pessoaFisica);
                saidaDireta.setUsuario(usuarioSistema);
                saidaDireta.setRetroativo(((BigDecimal) object[4]).compareTo(BigDecimal.ONE) == 0 ? Boolean.TRUE : Boolean.FALSE);
                saidaDireta.setProcessado(((BigDecimal) object[5]).compareTo(BigDecimal.ONE) == 0 ? Boolean.TRUE : Boolean.FALSE);
                saidaDireta.setRetiradoPor(((String) object[6]));
                list.add(saidaDireta);
            }
        } catch (NoResultException nre) {
            logger.error("Erro ao montar lista de saída direta de material {} ", nre);
        }
        Object[] retorno = new Object[2];
        retorno[0] = list;
        retorno[1] = count;
        return retorno;
    }

    public List<RelatorioSaidaDiretaMateriais> buscarDadosRelatorioSaidaDiretaMateriais(SaidaMaterial selecionado) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct   ")
            .append("   saida.numero                                   as numerosaida,  ")
            .append("   saida.datasaida                                as datasaida,  ")
            .append("   pf.nome                                        as usuariosaida,  ")
            .append("   saida.retiradopor                              as retiradopor,  ")
            .append("   tpb.descricao                                  as tipobaixatem,  ")
            .append("   mat.codigo || ' - ' || mat.descricao           as material,  ")
            .append("   vwadm.codigo || ' - ' || vwadm.descricao       as unidadeadm,  ")
            .append("   localest.codigo || ' - ' || localest.descricao as localestitem,  ")
            .append("   localest.tipoestoque                           as tipolocalitem,  ")
            .append("   vworc.codigo  || ' - ' || vworc.descricao      as unidadeorc,  ")
            .append("   lote.id                                        as lotematerial,  ")
            .append("   um.sigla                                       as unidadaMedida,  ")
            .append("   coalesce(item.quantidade, 0)            as quantidade,  ")
            .append("   mat.codigo                                     as codigoMaterial  ")
            .append(" from saidamaterial saida   ")
            .append("   inner join saidadireta on saidadireta.id = saida.id  ")
            .append("   inner join usuariosistema usu on usu.id = saida.usuario_id  ")
            .append("   inner join pessoafisica pf on pf.id = usu.pessoafisica_id  ")
            .append("   inner join tipobaixabens tpb on tpb.id = saida.tipobaixabens_id  ")
            .append("   inner join itemsaidamaterial item on item.saidamaterial_id = saida.id  ")
            .append("   inner join itemsaidadireta itemSaida on itemSaida.itemsaidamaterial_id = item.id  ")
            .append("   inner join material mat on mat.id = itemsaida.material_id  ")
            .append("   left join unidademedida um on um.id = mat.unidademedida_id  ")
            .append("   left join lotematerial lote on lote.id = item.lotematerial_id  ")
            .append("   inner join localestoqueorcamentario localorc on localorc.id = item.localestoqueorcamentario_id  ")
            .append("   inner join localestoque localest on localest.id = localorc.localestoque_id  ")
            .append("   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = localorc.unidadeorcamentaria_id  ")
            .append("   inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = localest.unidadeorganizacional_id  ")
            .append(" where trunc(saida.datasaida) between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, saida.datasaida)  ")
            .append("   and trunc(saida.datasaida) between vworc.iniciovigencia and coalesce(vworc.fimvigencia, saida.datasaida)  ")
            .append("   and saida.id = :idSaidaDireta  ")
            .append(" order by codigoMaterial ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idSaidaDireta", selecionado.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<RelatorioSaidaDiretaMateriais> toRetun = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RelatorioSaidaDiretaMateriais item = new RelatorioSaidaDiretaMateriais();
                item.setNumero(((BigDecimal) obj[0]).longValue());
                item.setDataSaida((Date) obj[1]);
                item.setUsuario((String) obj[2]);
                item.setRetiradoPor((String) obj[3]);
                item.setTipoBaixa((String) obj[4]);
                item.setMaterial((String) obj[5]);
                item.setUnidadeAdm((String) obj[6]);
                item.setLocalEstoque((String) obj[7] + " - " + TipoEstoque.valueOf((String) obj[8]).getDescricao());
                item.setUnidadeOrc((String) obj[9]);
                if (obj[10] != null) {
                    LoteMaterial loteMaterial = loteMaterialFacade.recuperar(((BigDecimal) obj[10]).longValue());
                    item.setLote(loteMaterial.getIdentificacao() + " - " + DataUtil.getDataFormatada(loteMaterial.getValidade()));
                } else {
                    item.setLote("Não requer lote");
                }
                item.setUnidadeMedida((String) obj[11]);
                item.setQuantidade((BigDecimal) obj[12]);
                toRetun.add(item);
            }
            return toRetun;
        }
    }

    public BigDecimal buscarQuantidadeTotalJaUtilizadaEmOutraSaidaEmElaboracao(ItemSaidaMaterial itemSaidaMaterial) {
        String sql = " select coalesce(sum(itemSaida.quantidade), 0) as quantidadeTotalQueSaiu " +
            " from itemsaidamaterial itemSaida " +
            "  inner join ITEMREQSAIDAMAT irsm on irsm.ITEMSAIDAMATERIAL_ID = itemSaida.ID " +
            "  inner join SAIDAMATERIAL sm on sm.ID = itemSaida.SAIDAMATERIAL_ID " +
            " where irsm.ITEMREQUISICAOMATERIAL_ID = :idItemRequisicao " +
            "   and sm.situacao = :emElaboracao " +
            (itemSaidaMaterial.getSaidaMaterial().getId() != null ? " and sm.id <> :idSaidaMaterial " : "");
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemRequisicao", itemSaidaMaterial.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().getId());
        q.setParameter("emElaboracao", SituacaoMovimentoMaterial.EM_ELABORACAO.name());
        if (itemSaidaMaterial.getSaidaMaterial().getId() != null) {
            q.setParameter("idSaidaMaterial", itemSaidaMaterial.getSaidaMaterial().getId());
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal buscarQuantidadeTotalJaUtilizadaEmOutraSaidaDesincorporacaoEmElaboracao(ItemSaidaMaterial itemSaidaMaterial) {
        String sql = " select coalesce(sum(itemSaida.quantidade), 0) as quantidadeTotalQueSaiu " +
            " from itemsaidamaterial itemSaida " +
            "  inner join ItemSaidaDesincorporacao isd on isd.ITEMSAIDAMATERIAL_ID = itemSaida.ID " +
            "  inner join SAIDAMATERIAL sm on sm.ID = itemSaida.SAIDAMATERIAL_ID " +
            " where sm.situacao = :emElaboracao " +
            " and isd.material_id = :material " +
            (itemSaidaMaterial.getSaidaMaterial().getId() != null ? " and sm.id <> :idSaidaMaterial " : "");
        Query q = em.createNativeQuery(sql);
        q.setParameter("emElaboracao", SituacaoMovimentoMaterial.EM_ELABORACAO.name());
        q.setParameter("material", itemSaidaMaterial.getItemSaidaDesincorporacao().getMaterial().getId());
        if (itemSaidaMaterial.getSaidaMaterial().getId() != null) {
            q.setParameter("idSaidaMaterial", itemSaidaMaterial.getSaidaMaterial().getId());
        }
        return (BigDecimal) q.getSingleResult();
    }

    public ItemSaidaMaterial recuperarItemSaidaMaterial(ItemRequisicaoMaterial itemReq, LocalEstoqueOrcamentario localEstoqueOrc, LoteMaterial loteMaterial, SaidaMaterial saida) {
        String sql = " select item.* from itemsaidamaterial item " +
            "            inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "            inner join itemreqsaidamat irs on irs.itemsaidamaterial_id = item.id " +
            "          where irs.itemrequisicaomaterial_id = :idItemReq " +
            "            and leo.id = :idLocalEstoque " +
            "            and item.saidamaterial_id = :idSaida ";
        sql += loteMaterial != null ? " and item.lotematerial_id = :idLote " : "";
        Query q = em.createNativeQuery(sql, ItemSaidaMaterial.class);
        q.setParameter("idItemReq", itemReq.getId());
        q.setParameter("idLocalEstoque", localEstoqueOrc.getId());
        q.setParameter("idSaida", saida.getId());
        if (loteMaterial != null) {
            q.setParameter("idLote", loteMaterial.getId());
        }
        try {
            return (ItemSaidaMaterial) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ItemSaidaMaterial recuperarItemSaidaMaterial(Material material, LocalEstoqueOrcamentario localEstoqueOrc, LoteMaterial loteMaterial, SaidaMaterial saida) {
        String sql = " select item.* from itemsaidamaterial item " +
            "            inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "          where item.material_id = :idMaterial " +
            "            and leo.id = :idLocalEstoque " +
            "            and item.saidamaterial_id = :idSaida ";
        sql += loteMaterial != null ? " and item.lotematerial_id = :idLote " : "";
        Query q = em.createNativeQuery(sql, ItemSaidaMaterial.class);
        q.setParameter("idMaterial", material.getId());
        q.setParameter("idLocalEstoque", localEstoqueOrc.getId());
        q.setParameter("idSaida", saida.getId());
        if (loteMaterial != null) {
            q.setParameter("idLote", loteMaterial.getId());
        }
        try {
            return (ItemSaidaMaterial) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public BigDecimal getQuantidadeSaida(ItemRequisicaoMaterial itemReqMat) {
        String sql = " select coalesce(sum(itemSaida.quantidade), 0) as quantidade " +
            "           from itemsaidamaterial itemSaida " +
            "           inner join ITEMREQSAIDAMAT irsm on irsm.ITEMSAIDAMATERIAL_ID = itemSaida.ID " +
            "           where irsm.ITEMREQUISICAOMATERIAL_ID = :idItemRequisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemRequisicao", itemReqMat.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public RequisicaoMaterialFacade getRequisicaoMaterialFacade() {
        return requisicaoMaterialFacade;
    }

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public EntradaMaterialFacade getEntradaMaterialFacade() {
        return entradaMaterialFacade;
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

    public EstoqueLoteMaterialFacade getEstoqueLoteMaterialFacade() {
        return estoqueLoteMaterialFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public TipoBaixaBensFacade getTipoBaixaBensFacade() {
        return tipoBaixaBensFacade;
    }

    public AprovacaoMaterialFacade getAprovacaoMaterialFacade() {
        return aprovacaoMaterialFacade;
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

    public PoliticaDeEstoqueFacade getPoliticaDeEstoqueFacade() {
        return politicaDeEstoqueFacade;
    }
}
