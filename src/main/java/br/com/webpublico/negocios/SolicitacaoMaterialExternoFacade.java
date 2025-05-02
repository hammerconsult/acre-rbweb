package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoAdesao;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.enums.TipoSolicitacaoRegistroPreco;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 30/07/14
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SolicitacaoMaterialExternoFacade extends AbstractFacade<SolicitacaoMaterialExterno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private CertameFacade certameFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private DocumentosNecessariosAnexoFacade documentosNecessariosAnexoFacade;
    @EJB
    private ConfigTipoObjetoCompraFacade configTipoObjetoCompraFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;

    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public SolicitacaoMaterialExternoFacade() {
        super(SolicitacaoMaterialExterno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public AtaRegistroPrecoFacade getAtaRegistroPrecoFacade() {
        return ataRegistroPrecoFacade;
    }

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public CertameFacade getCertameFacade() {
        return certameFacade;
    }

    public PregaoFacade getPregaoFacade() {
        return pregaoFacade;
    }

    public DocumentosNecessariosAnexoFacade getDocumentosNecessariosAnexoFacade() {
        return documentosNecessariosAnexoFacade;
    }

    @Override
    public SolicitacaoMaterialExterno recuperar(Object id) {
        SolicitacaoMaterialExterno sme = super.recuperar(id);
        Hibernate.initialize(sme.getItensDaSolicitacao());
        Hibernate.initialize(sme.getAdesoes());
        if (sme.getItensDaSolicitacao() != null) {
            for (ItemSolicitacaoExterno ise : sme.getItensDaSolicitacao()) {
                if (ise.getDotacoes() != null) {
                    Hibernate.initialize(ise.getDotacoes());
                }
            }
        }
        if (sme.getAtaRegistroPreco() != null) {
            Hibernate.initialize(sme.getAtaRegistroPreco().getAdesoes());
        }
        if (sme.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(sme.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        return sme;
    }

    public SolicitacaoMaterialExterno recuperarSolicitacaoComItens(Object id) {
        SolicitacaoMaterialExterno sme = super.recuperar(id);
        Hibernate.initialize(sme.getItensDaSolicitacao());
        return sme;
    }

    public BigDecimal recuperarQuantidadeUtilizadaOutrasSolicitacoes(AtaRegistroPreco ata, ObjetoCompra objetoCompra, UnidadeOrganizacional unidade, ItemProcessoDeCompra itemProcessoDeCompra) {
        String sql = " select coalesce (sum(item.quantidade),0) from itemsolicitacaoexterno item " +
            "           inner join solicitacaomaterialext sol on sol.id = item.solicitacaomaterialexterno_id " +
            "           inner join objetocompra ob on ob.id = item.objetocompra_id " +
            "          where sol.ataregistropreco_id = :idAta " +
            "           and ob.id  = :idObjetoCompra " +
            "           and item.itemprocessocompra_id  = :idItemProcessoCompra " +
            "           and sol.unidadeorganizacional_id = :idUnidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAta", ata.getId());
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("idObjetoCompra", objetoCompra.getId());
        q.setParameter("idItemProcessoCompra", itemProcessoDeCompra.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }


    public BigDecimal recuperarValorUtilizadaOutrasSolicitacoes(AtaRegistroPreco ata, ObjetoCompra objetoCompra, UnidadeOrganizacional unidade, ItemProcessoDeCompra itemProcessoDeCompra) {
        String sql = " select coalesce (sum(item.valortotal),0) from itemsolicitacaoexterno item " +
            "           inner join solicitacaomaterialext sol on sol.id = item.solicitacaomaterialexterno_id " +
            "           inner join objetocompra ob on ob.id = item.objetocompra_id " +
            "          where sol.ataregistropreco_id = :idAta " +
            "           and ob.id  = :idObjetoCompra " +
            "           and item.itemprocessocompra_id  = :idItemProcessoCompra " +
            "           and sol.unidadeorganizacional_id = :idUnidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAta", ata.getId());
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("idObjetoCompra", objetoCompra.getId());
        q.setParameter("idItemProcessoCompra", itemProcessoDeCompra.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }

    public List<SolicitacaoMaterialExterno> buscarSoliciacoesPorAtaResgistroPreco(AtaRegistroPreco ata) {
        String sql = "select sol.* from solicitacaomaterialext sol " +
            "         where sol.ataregistropreco_id = :idAta";
        Query q = em.createNativeQuery(sql, SolicitacaoMaterialExterno.class);
        q.setParameter("idAta", ata.getId());
        List<SolicitacaoMaterialExterno> list = q.getResultList();
        List<SolicitacaoMaterialExterno> toReturn = Lists.newArrayList();
        for (SolicitacaoMaterialExterno sol : list) {
            Hibernate.initialize(sol.getItensDaSolicitacao());
            toReturn.add(sol);
        }
        return toReturn;
    }

    public List<ItemSolicitacaoExterno> getItensVencidosPeloFornecedorPorStatus(RegistroSolicitacaoMaterialExterno rse, Pessoa p, List<TipoClassificacaoFornecedor> status) {
        String hql = "select ise from RegistroSolicitacaoMaterialExterno rsme" +
            " inner join rsme.fornecedores f" +
            " inner join f.itens itens" +
            " inner join itens.itemSolicitacaoExterno ise" +
            " where f.pessoa = :pessoa" +
            "   and rsme = :registroSolicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("registroSolicitacao", rse);
        q.setParameter("pessoa", p);
        return q.getResultList();
    }

    public List<SolicitacaoMaterialExterno> buscarSolicitacaoInternaPorAtaRegistroPreco(String parte, AtaRegistroPreco ataRegistroPreco) {
        String sql = " select distinct sol.* from solicitacaomaterialext sol " +
            " inner join ataregistropreco ata on ata.id = sol.ataregistropreco_id " +
            " inner join adesao ad on ad.ataderegistrodepreco_id = ata.id " +
            " where ata.id = :idAta " +
            " and sol.tiposolicitacaoregistropreco = :tipoAdesao " +
            " and ad.adesaoaceita = :adesaoAceita " +
            " and (upper(translate(sol.objeto,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') " +
            "      or sol.numero like :parte) ";
        Query q = em.createNativeQuery(sql, SolicitacaoMaterialExterno.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("idAta", ataRegistroPreco.getId());
        q.setParameter("adesaoAceita", Boolean.TRUE);
        q.setParameter("tipoAdesao", TipoSolicitacaoRegistroPreco.INTERNA.name());
        return q.getResultList();
    }

    public List<SolicitacaoMaterialExterno> buscarSolicitacoesPorTipo(TipoSolicitacaoRegistroPreco tipo, String parte) {
        String sql = "select sol.* from solicitacaomaterialext sol " +
            "       where sol.tiposolicitacaoregistropreco = :tipoSol " +
            "           and (upper(translate(sol.objeto,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') " +
            "       or sol.numero like :parte) ";
        Query q = em.createNativeQuery(sql, SolicitacaoMaterialExterno.class);
        q.setParameter("tipoSol", tipo.name());
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }

    public List<ItemSolicitacaoExterno> buscarItens(SolicitacaoMaterialExterno solicitacao) {
        String sql = "select item.* from itemsolicitacaoexterno item where item.solicitacaomaterialexterno_id = :idSolicitacao " +
            "         order by item.numero ";
        Query q = em.createNativeQuery(sql, ItemSolicitacaoExterno.class);
        q.setParameter("idSolicitacao", solicitacao.getId());
        return q.getResultList();
    }

    public List<SolicitacaoMaterialExterno> buscarSolicitacaoMaterialExternoPorAnoOrNumeroAndTipoOndeUsuarioGestorLicitacao(String anoOrNumero,
                                                                                                                            TipoSolicitacaoRegistroPreco tipoSolicitacaoRegistroPreco,
                                                                                                                            UsuarioSistema usuarioSistema) {
        String sql = "select solmatext.* \n" +
            "   from solicitacaomaterialext solmatext\n" +
            "  inner join exercicio e on e.id = solmatext.exercicio_id\n" +
            "where exists (select 1 from usuariounidadeorganizacio u_un \n" +
            "               where u_un.usuariosistema_id = :id_usuario\n" +
            "                 and u_un.unidadeorganizacional_id = solmatext.unidadeorganizacional_id\n" +
            "                 and u_un.gestorlicitacao = :gestor_licitacao)\n" +
            "      and (solmatext.numero like :ano_numero or e.ano like :ano_numero or solmatext.NUMERO || '/' || e.ANO like :ano_numero)" +
            "      and solmatext.tiposolicitacaoregistropreco = :tiposolicitacaoregistropreco ";

        Query q = em.createNativeQuery(sql, SolicitacaoMaterialExterno.class);
        q.setParameter("ano_numero", "%" + anoOrNumero.trim() + "%");
        q.setParameter("tiposolicitacaoregistropreco", tipoSolicitacaoRegistroPreco.name());
        q.setParameter("id_usuario", usuarioSistema.getId());
        q.setParameter("gestor_licitacao", Boolean.TRUE);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public SolicitacaoMaterialExterno salvarSolicitacao(SolicitacaoMaterialExterno entity) {
        if (entity.getNumero() == null){
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(SolicitacaoMaterialExterno.class, "numero").intValue());
        }
        gerarAdesao(entity);
        salvarEspecificacaoObjetoCompra(entity);
        return em.merge(entity);
    }

    private void gerarAdesao(SolicitacaoMaterialExterno selecionado) {
        if (selecionado.isSolicitacaoInterna() && selecionado.getId() == null) {
            Adesao adesao = new Adesao();
            adesao.setSolicitacaoMaterialExterno(selecionado);
            adesao.setAtaDeRegistroDePreco(selecionado.getAtaRegistroPreco());
            adesao.setData(selecionado.getDataSolicitacao());
            adesao.setObservacao(selecionado.getHierarquiaOrganizacional() != null ? selecionado.getHierarquiaOrganizacional().getDescricao() : selecionado.getUnidadeOrganizacional().toString());
            adesao.setTipoAdesao(TipoAdesao.INTERNA);
            adesao.setAdesaoAceita(Boolean.FALSE);
            adesao.setNumeroRequisicao(selecionado.getNumero());
            adesao.setDataRequisicao(selecionado.getDataSolicitacao());
            adesao.setDocumento(selecionado.getDocumento());
            selecionado.setAdesoes(Util.adicionarObjetoEmLista(selecionado.getAdesoes(), adesao));
        }
    }

    private void salvarEspecificacaoObjetoCompra(SolicitacaoMaterialExterno entity) {
        List<ObjetoDeCompraEspecificacao> list = Lists.newArrayList();
        for (ItemSolicitacaoExterno item : entity.getItensDaSolicitacao()) {
            if (!Strings.isNullOrEmpty(item.getDescricaoComplementar())) {
                objetoCompraFacade.adicionarNovaEspecificacao(item.getObjetoCompra(), item.getDescricaoComplementar(), list);
            }
        }
        objetoCompraFacade.salvarEspecificacoes(list);
    }

    public ConfigTipoObjetoCompraFacade getConfigTipoObjetoCompraFacade() {
        return configTipoObjetoCompraFacade;
    }

    public BigDecimal buscarSaldoFonteDespesaORC(ItemSolicitacaoExternoDotacao itemSolicitacaoExternoDotacao) {
        BigDecimal saldo = BigDecimal.ZERO;
        SaldoFonteDespesaORC saldoFonteDespesaORC = saldoFonteDespesaORCFacade.recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(
            itemSolicitacaoExternoDotacao.getFonteDespesaORC(),
            sistemaFacade.getDataOperacao(),
            itemSolicitacaoExternoDotacao.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());
        if (saldoFonteDespesaORC != null) {
            saldo = saldoFonteDespesaORC.getSaldoAtual();
        }
        return saldo;
    }

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public Boolean verificarSolicitacaoContratada(SolicitacaoMaterialExterno selecionado) {
        String sql;
        if (selecionado.isSolicitacaoInterna()) {
            sql = " select sol.* from solicitacaomaterialext sol " +
                "           inner join conlicitacao cl on cl.solicitacaomaterialexterno_id = sol.id " +
                "          where sol.id = :idSolicitacao ";
        } else {
            sql = " select sol.* from solicitacaomaterialext sol " +
                "           inner join registrosolmatext reg on reg.solicitacaomaterialexterno_id = sol.id " +
                "           inner join conregprecoexterno cr on cr.regsolmatext_id = reg.id " +
                "          where sol.id = :idSolicitacao ";
        }
        Query q = em.createNativeQuery(sql, SolicitacaoMaterialExterno.class);
        q.setParameter("idSolicitacao", selecionado.getId());
        return !q.getResultList().isEmpty();
    }

    public Boolean verificarSolicitacaoComAdesaoExterna(SolicitacaoMaterialExterno selecionado) {
        String sql;
        sql = " select sol.* from solicitacaomaterialext sol " +
            "           inner join registrosolmatext reg on reg.solicitacaomaterialexterno_id = sol.id " +
            "          where sol.id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql, SolicitacaoMaterialExterno.class);
        q.setParameter("idSolicitacao", selecionado.getId());
        return !q.getResultList().isEmpty();
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }
}
