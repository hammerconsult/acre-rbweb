package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AgrupadorGrupoLevantamentoEstoque;
import br.com.webpublico.entidadesauxiliares.BensEstoqueVO;
import br.com.webpublico.enums.SituacaoEntradaMaterial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desenvolvimento on 31/01/2017.
 */
@Stateless
public class EfetivacaoLevantamentoEstoqueFacade extends AbstractFacade<EfetivacaoLevantamentoEstoque> {

    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private LevantamentoEstoqueFacade levantamentoEstoqueFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SaldoGrupoMaterialFacade saldoGrupoMaterialFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private IntegradorMateriaisContabilFacade integradorMateriaisContabilFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EfetivacaoLevantamentoEstoqueFacade() {
        super(EfetivacaoLevantamentoEstoque.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EfetivacaoLevantamentoEstoque recuperar(Object id) {
        EfetivacaoLevantamentoEstoque entity = super.recuperar(id);
        Hibernate.initialize(entity.getItensEfetivacao());
        for (ItemEfetivacaoLevantamentoEstoque itemEfetivacaoLevantamentoEstoque : entity.getItensEfetivacao()) {
            itemEfetivacaoLevantamentoEstoque.setLevantamentoEstoque(levantamentoEstoqueFacade.recuperar(itemEfetivacaoLevantamentoEstoque.getLevantamentoEstoque().getId()));
        }
        Hibernate.initialize(entity.getItensEfetivacaoGrupo());
        entity.setHierarquiaOrcamentaria(getHierarquiaOrganizacionalFacade()
            .getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                entity.getUnidadeOrganizacional(), entity.getDataEntrada()));
        return entity;
    }

    public EfetivacaoLevantamentoEstoque salvarEfetivacao(EfetivacaoLevantamentoEstoque entity, List<BensEstoque> bensEstoque) throws OperacaoEstoqueException, ExcecaoNegocioGenerica {
        try {
            if (entity.getNumero() == null) {
                entity.setNumero(singletonGeradorCodigo.getProximoCodigo(EntradaMaterial.class, "numero"));
            }
            entity.setDataConclusao(sistemaFacade.getDataOperacao());
            entity.setSituacao(SituacaoEntradaMaterial.CONCLUIDA);

            associarMaterialAoLocalEstoque(entity);
            criarItensEntradaMaterial(entity);
            validarRegrasDoNegocio(entity);
            salvarLevantamento(entity);
            entity = (EfetivacaoLevantamentoEstoque) entradaMaterialFacade.salvarEntradaMaterial(entity);
            if (!Util.isListNullOrEmpty(bensEstoque)) {
                integradorMateriaisContabilFacade.contabilizarDiferencaLevantamentoBensEstoque(bensEstoque, entity);
            }
            return entity;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Ocorreu um erro ao salvar a efetivação.");
        }
    }

    private void associarMaterialAoLocalEstoque(EfetivacaoLevantamentoEstoque entity) {
        try {
            LocalEstoque localEstoque = null;
            List<Material> materiaisLevantamento = Lists.newArrayList();
            for (ItemLevantamentoEstoque itemLevantamento : getItensLevantamentos(entity.getItensEfetivacao())) {
                materiaisLevantamento.add(itemLevantamento.getMaterial());
                localEstoque = localEstoqueFacade.recuperarLocalEstoqueMateriais(itemLevantamento.getLevantamentoEstoque().getLocalEstoque().getId());

            }
            assert localEstoque != null;
            List<Material> materiaisNaoAssociados = getMateriaisNaoAssociadosAoLocalEstoque(localEstoque, materiaisLevantamento);
            for (Material material : materiaisNaoAssociados) {
                LocalEstoqueMaterial localEstoqueMaterial = new LocalEstoqueMaterial();
                localEstoqueMaterial.setLocalEstoque(localEstoque);
                localEstoqueMaterial.setMaterial(material);
                em.merge(localEstoqueMaterial);
            }
        } catch(Exception e) {
            logger.error("Erro ao associar material ao local de estoque: ", e.getMessage());
            throw new ExcecaoNegocioGenerica("Ocorreu um erro ao associar o material ao local de estoque.");
        }
    }

    public List<Material> getMateriaisNaoAssociadosAoLocalEstoque(LocalEstoque localEstoque, List<Material> materiaisLevantamento) {
        List<Material> toReturn = Lists.newArrayList();
        List<Material> materiaisAssociados = Lists.newArrayList();
        for (LocalEstoqueMaterial localMat : localEstoque.getListaLocalEstoqueMaterial()) {
            materiaisAssociados.add(localMat.getMaterial());
        }
        for (Material material : materiaisLevantamento) {
            if (!materiaisAssociados.contains(material)) {
                toReturn.add(material);
            }
        }
        return toReturn;
    }

    public List<ItemLevantamentoEstoque> getItensLevantamentos(List<ItemEfetivacaoLevantamentoEstoque> levantamentos) {
        List<ItemLevantamentoEstoque> itensLevantamentos = new ArrayList<>();
        for (ItemEfetivacaoLevantamentoEstoque levantamento : levantamentos) {
            itensLevantamentos.addAll(levantamento.getLevantamentoEstoque().getItensLevantamentoEstoque());
        }
        return itensLevantamentos;
    }

    public void salvarLevantamento(EfetivacaoLevantamentoEstoque entity) {
        for (ItemEfetivacaoLevantamentoEstoque levantamento : entity.getItensEfetivacao()) {
            levantamentoEstoqueFacade.salvar(levantamento.getLevantamentoEstoque());
        }
    }

    private void criarItensEntradaMaterial(EfetivacaoLevantamentoEstoque entity) {
        try {
            entity.getItens().clear();
            for (ItemLevantamentoEstoque itemLevantamento : getItensLevantamentos(entity.getItensEfetivacao())) {
                if (itemLevantamento.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
                    ItemEntradaMaterial itemEntradaMaterial = new ItemEntradaMaterial();
                    itemEntradaMaterial.setEntradaMaterial(entity);
                    itemEntradaMaterial.setItemEntradaLevantamento(new ItemEntradaLevantamento(itemEntradaMaterial, itemLevantamento));
                    itemEntradaMaterial.setQuantidade(itemLevantamento.getQuantidade());
                    itemEntradaMaterial.setValorUnitario(itemLevantamento.getValorUnitario());
                    itemEntradaMaterial.setValorTotal(itemLevantamento.getValorTotal());
                    itemEntradaMaterial.setLoteMaterial(itemLevantamento.getLoteMaterial());
                    itemEntradaMaterial.setMaterial(itemLevantamento.getMaterial());
                    LocalEstoqueOrcamentario localEstoqueOrcamentario = localEstoqueFacade.recuperarOuCriarNovoLocalEstoqueOrcamentario(itemLevantamento.getLevantamentoEstoque().getLocalEstoque(), itemLevantamento.getLevantamentoEstoque().getUnidadeOrcamentaria(), entity.getDataEntrada());
                    itemEntradaMaterial.setLocalEstoqueOrcamentario(localEstoqueOrcamentario);
                    entity.getItens().add(itemEntradaMaterial);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao criar itens de entrada de material: ", e.getMessage());
            throw new ExcecaoNegocioGenerica("Ocorreu um erro ao criar os itens de entrada de material.");
        }
    }

    public void gerarBensEstoqueContabil(List<AgrupadorGrupoLevantamentoEstoque> agrupamentos, EfetivacaoLevantamentoEstoque entity) {
        for (AgrupadorGrupoLevantamentoEstoque agrup : agrupamentos) {
            if (agrup.hasDiferenca()) {
                if (agrup.getContabilMaiorMateriais()) {
                    BensEstoqueVO bensEstoqueVO = novoBensEstoqueVO(entity, agrup, TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_DESINCORPORACAO);
                    agrup.setBensEstoque(integradorMateriaisContabilFacade.novoBensEstoque(bensEstoqueVO));
                    agrup.setValorBensEstoqueContabil(bensEstoqueVO.getValor());
                } else {
                    BensEstoqueVO bensEstoqueVO = novoBensEstoqueVO(entity, agrup, TipoOperacaoBensEstoque.INCORPORACAO_BENS_ESTOQUE);
                    agrup.setBensEstoque(integradorMateriaisContabilFacade.novoBensEstoque(bensEstoqueVO));
                    agrup.setValorBensEstoqueContabil(bensEstoqueVO.getValor());
                }
            }
        }
    }

    private BensEstoqueVO novoBensEstoqueVO(EfetivacaoLevantamentoEstoque entity, AgrupadorGrupoLevantamentoEstoque agrup, TipoOperacaoBensEstoque operacao) {
        BensEstoqueVO bensEstoqueVO = new BensEstoqueVO();
        bensEstoqueVO.setDataBem(entity.getDataEntrada());
        bensEstoqueVO.setTipoEstoque(agrup.getTipoEstoque());
        bensEstoqueVO.setOperacoesBensEstoque(operacao);
        bensEstoqueVO.setTipoLancamento(TipoLancamento.NORMAL);
        bensEstoqueVO.setGrupoMaterial(agrup.getGrupoMaterial());
        bensEstoqueVO.setUnidadeOrganizacional(entity.getHierarquiaOrcamentaria().getSubordinada());
        bensEstoqueVO.setHistorico(entity.getHistoricoRazao(operacao));
        bensEstoqueVO.setValor(agrup.getDiferenca().setScale(2, RoundingMode.HALF_EVEN).abs());
        return bensEstoqueVO;
    }

    private void validarRegrasDoNegocio(EfetivacaoLevantamentoEstoque entity) throws ValidacaoException {
        ValidacaoException vex = new ValidacaoException();
        if (entity.getItens().isEmpty()) {
            vex.adicionarMensagemDeOperacaoNaoPermitida("Deve haver pelo menos um item na lista da entrada.");
        }
        validarLocalAndLoteSelecionado(vex, entity);
        validarVinculoMaterialComLocalEstoque(vex, entity);
        validarEstoqueBloqueado(vex, entity);
        vex.lancarException();
    }

    private void validarLocalAndLoteSelecionado(ValidacaoException vex, EfetivacaoLevantamentoEstoque entity) {
        for (ItemEntradaMaterial iem : entity.getItens()) {
            if (iem.getLocalEstoque() == null) {
                vex.adicionarMensagemDeCampoObrigatorio("Informe o local de estoque do item " + iem.getDescricao() + ".");
            } else if (iem.localEstoqueFechadoNaData(entity.getDataEntrada())) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("O local de estoque do item " + iem.getDescricao() + " está fechado.");
            }
            if (iem.getLoteMaterial() == null && iem.requerLote()) {
                vex.adicionarMensagemDeCampoObrigatorio("Informe o lote do item " + iem.getDescricao() + ".");
            }
            if (iem.getLoteMaterial() != null && iem.loteVencidoNaData(entity.getDataEntrada())) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("O lote do item " + iem.getDescricao() + " está vencido.");
            }
            if (iem.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria() == null) {
                vex.adicionarMensagemDeCampoObrigatorio("A unidade orçamentária do item " + iem.getDescricao() + " deve ser preenchida.");
            }
        }
    }

    private void validarVinculoMaterialComLocalEstoque(ValidacaoException vex, EfetivacaoLevantamentoEstoque entity) {
        for (ItemEntradaMaterial iem : entity.getItens()) {
            if (iem.getMaterial() != null && iem.getLocalEstoque() != null) {
                boolean possuiVinculo = true;
                LocalEstoque localEstoque = localEstoqueFacade.recuperarLocalEstoqueMateriais(iem.getLocalEstoque().getId());
                for (LocalEstoqueMaterial local : localEstoque.getListaLocalEstoqueMaterial()) {
                    if (local.getMaterial() == null) {
                        possuiVinculo = false;
                    }
                }
                if (!possuiVinculo) {
                    String url = FacesUtil.getRequestContextPath() + "/associativa-material-local-de-estoque/";
                    String mensagem = "Não é permitido realizar movimentações do</br> Material <b>" + iem.getDescricao().toUpperCase() + "</b> no local de estoque <b>" + iem.getLocalEstoque().getCodigo() + " - " + iem.getLocalEstoque().getDescricao() + "</b>. ";
                    mensagem += "<b><a href='" + url + "' target='_blank' style='color: blue;'>Clique aqui para Editar o Local de Estoque</a></b>";
                    vex.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                }
            }
        }
    }

    private void validarEstoqueBloqueado(ValidacaoException vex, EfetivacaoLevantamentoEstoque entity) {
        try {
            for (ItemEntradaMaterial item : entity.getItens()) {
                if (item.getLocalEstoque() != null && estoqueFacade.estoqueBloqueado(item.getMaterial(), item.getLocalEstoque(), item.getUnidadeOrcamentaria(), entity.getDataEntrada(), item.getTipoEstoque())) {
                    vex.adicionarMensagemDeOperacaoNaoPermitida("O estoque do item " + item.getDescricao() + " está bloqueado no momento.");
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao verificar se o estoque está bloqueado.", e.getMessage());
            throw new ExcecaoNegocioGenerica("Erro ao verificar se o estoque está bloqueado.");
        }
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            query.setParameter("dataOperacao", DataUtil.dataSemHorario(sistemaFacade.getDataOperacao()));
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SaldoGrupoMaterialFacade getSaldoGrupoMaterialFacade() {
        return saldoGrupoMaterialFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public LevantamentoEstoqueFacade getLevantamentoEstoqueFacade() {
        return levantamentoEstoqueFacade;
    }

    public EstoqueFacade getEstoqueFacade() {
        return estoqueFacade;
    }
}
