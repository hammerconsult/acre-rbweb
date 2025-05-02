/*
 * Codigo gerado automaticamente em Mon Nov 28 17:16:17 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.enums.TipoFuncionalidadeParaAnexo;
import br.com.webpublico.enums.TipoSolicitacao;
import br.com.webpublico.enums.administrativo.SituacaoCotacao;
import br.com.webpublico.util.GeradorCodigoCotacao;
import br.com.webpublico.util.StringUtil;
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

@Stateless
public class CotacaoFacade extends AbstractFacade<Cotacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private FornecedorFacade fornecedorFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private FormularioCotacaoFacade formularioCotacaoFacade;
    @EJB
    private GeradorCodigoCotacao geradorCodigoCotacao;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private DocumentosNecessariosAnexoFacade documentosNecessariosAnexoFacade;
    @EJB
    private UnidadeExternaFacade unidadeExternaFacade;

    public CotacaoFacade() {
        super(Cotacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Cotacao recuperar(Object id) {
        Cotacao entity = super.recuperar(id);
        Hibernate.initialize(entity.getLotes());
        Hibernate.initialize(entity.getFornecedores());
        Hibernate.initialize(entity.getFormularioCotacao().getLotesFormulario());
        for (LoteFormularioCotacao loteFormularioCotacao : entity.getFormularioCotacao().getLotesFormulario()) {
            Hibernate.initialize(loteFormularioCotacao.getItensLoteFormularioCotacao());
        }
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        for (LoteCotacao lote : entity.getLotes()) {
            Hibernate.initialize(lote.getItens());
            for (ItemCotacao item : lote.getItens()) {
                Hibernate.initialize(item.getValoresCotacao());
            }
        }
        return entity;
    }

    public Cotacao concluirCotacao(Cotacao entity) {
        entity.setSituacao(SituacaoCotacao.CONCLUIDO);
        return em.merge(entity);
    }

    public Cotacao salvarCotacao(Cotacao entity) {
        if (entity.getSituacao().isConcluido() && !hasCotacaoUtilizadaEmSolicitacaoDeCompra(entity)) {
            entity.setSituacao(SituacaoCotacao.EM_ELABORACAO);
        }
        if (entity.getNumero() == null) {
            entity.setNumero(geradorCodigoCotacao.buscarNumeroCotacaoPorSecretaria(entity));
        }
        salvarEspecificacaoObjetoCompra(entity);
        return em.merge(entity);
    }

    private void salvarEspecificacaoObjetoCompra(Cotacao entity) {
        List<ObjetoDeCompraEspecificacao> list = Lists.newArrayList();
        for (LoteCotacao lote : entity.getLotes()) {
            for (ItemCotacao item : lote.getItens()) {
                if (!Strings.isNullOrEmpty(item.getDescricaoComplementar())) {
                    objetoCompraFacade.adicionarNovaEspecificacao(item.getObjetoCompra(), item.getDescricaoComplementar(), list);
                }
            }
        }
        objetoCompraFacade.salvarEspecificacoes(list);
    }

    public void validarDocumentosPlanilhaOrcamentaria(Cotacao cotacao) {
        if (cotacao.getFormularioCotacao().getTipoSolicitacao().isObraServicoEngenharia()) {
            documentosNecessariosAnexoFacade.validarSeTodosOsTipoDeDocumentoForamAnexados(TipoFuncionalidadeParaAnexo.PLANILHA_ORCAMENTARIA,
                cotacao.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
    }

    public Cotacao recuperarCotacaoComFornecedores(Object id) {
        Cotacao cotacao = this.recuperar(id);
        cotacao.getFornecedores().size();
        return cotacao;
    }

    public BigDecimal recuperarPrecoDoItemPorCriterio(String criterio, ItemCotacao ic, int qtdeCasasDecimais) {
        String sql = "select coalesce(round(" + criterio + "(vc.preco), " + qtdeCasasDecimais + "), 0)"
            + "  from  itemcotacao ic "
            + " inner join valorcotacao vc ON vc.itemcotacao_id = ic.id"
            + " where ic.id = :paramItem "
            + " and vc.preco > 0 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("paramItem", ic.getId());
        try {
            List resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                return (BigDecimal) q.getSingleResult();
            }
            return BigDecimal.ZERO;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar valor do item!" + ex.getMessage());
        }
    }

    public List<Cotacao> buscarCotacaoPorTipoSolicitacao(String anoOrNumeroOrDescricao, UsuarioSistema usuarioSistema, TipoSolicitacao tipoSolicitacao,
                                                         String data, TipoApuracaoLicitacao tipoApuracaoLicitacao) {
        String numeroOrAno = StringUtil.retornaApenasNumeros(anoOrNumeroOrDescricao);
        String sql = "select cot.*  " +
            "   from cotacao cot " +
            "  inner join formulariocotacao form on form.id = cot.formulariocotacao_id " +
            "  inner join exercicio e on cot.exercicio_id = e.id  " +
            "      where form.tiposolicitacao = :tipo_solicitacao " +
            "        and cot.situacao = :concluido " +
            "        and exists (select 1 from usuariounidadeorganizacio u_un " +
            "               where u_un.usuariosistema_id = :id_usuario " +
            "                 and u_un.unidadeorganizacional_id = cot.unidadeorganizacional_id " +
            "                 and u_un.gestorlicitacao = :gestor_licitacao) ";
        if (data != null) {
            sql += " and  cot.validadecotacao >= to_date(:data, 'dd/MM/yyyy')";
        }
        if (numeroOrAno != null && !numeroOrAno.trim().isEmpty()) {
            sql += " and (cot.numero =:numero_ano or e.ano =:numero_ano or LOWER(cot.descricao) like :descricao) ";
        } else {
            sql += " and (lower(cot.descricao) like :descricao) ";
        }
        sql += " and form.tipoapuracaolicitacao = :tipo_apuracao ";
        sql += " order by e.ano desc, cot.numero desc ";

        Query q = em.createNativeQuery(sql, Cotacao.class);
        q.setParameter("descricao", "%" + anoOrNumeroOrDescricao.toLowerCase().trim() + "%");
        if (numeroOrAno != null && !numeroOrAno.trim().isEmpty()) {
            q.setParameter("numero_ano", numeroOrAno);
        }
        q.setParameter("id_usuario", usuarioSistema.getId());
        q.setParameter("gestor_licitacao", Boolean.TRUE);
        q.setParameter("tipo_solicitacao", tipoSolicitacao.name());
        q.setParameter("concluido", SituacaoCotacao.CONCLUIDO.name());
        if (data != null) {
            q.setParameter("data", data);
        }
        q.setParameter("tipo_apuracao", tipoApuracaoLicitacao.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<Cotacao> buscarCotacao(String parte) {
        String sql = "" +
            " select cot.* from cotacao cot " +
            "  inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
            "  inner join exercicio ex on ex.id = cot.exercicio_id " +
            "  where (cot.numero like :parte " +
            "       or to_char(cot.numero) || '/' || to_char(ex.ano) like :parte " +
            "       or lower(cot.descricao) like :parte) " +
            " order by cot.numero desc ";
        Query q = em.createNativeQuery(sql, Cotacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<ItemCotacao> buscarItemCotacao(Cotacao cotacao) {
        String sql = "" +
            " select item.* from itemcotacao item " +
            "  inner join lotecotacao lote on lote.id = item.lotecotacao_id " +
            "    where lote.cotacao_id = :idCotacao ";
        Query q = em.createNativeQuery(sql, ItemCotacao.class);
        q.setParameter("idCotacao", cotacao.getId());
        return q.getResultList();
    }

    public Integer getQuantidadePropostaValorCotacao(Pessoa pessoa, ItemCotacao itemCotacao) {
        String sql = " select count(vl.id) as qtde_proposta " +
            "           from valorcotacao vl " +
            "           where vl.itemcotacao_id = :idItemCotacao " +
            "           and vl.preco > 0 ";
        sql += pessoa != null ? " and vl.fornecedor_id = :idFornecedor " : " ";
        Query q = em.createNativeQuery(sql);
        if (pessoa != null) {
            q.setParameter("idFornecedor", pessoa.getId());
        }
        q.setParameter("idItemCotacao", itemCotacao.getId());
        q.setMaxResults(1);
        return ((BigDecimal) q.getResultList().get(0)).intValue();
    }

    public boolean hasCotacaoUtilizadaEmSolicitacaoDeCompra(Cotacao cotacao) {
        if (cotacao.getId() == null) {
            return false;
        }
        String sql = "select sm.* from SolicitacaoMaterial sm where sm.COTACAO_ID = :idCotacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCotacao", cotacao.getId());
        return !q.getResultList().isEmpty();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public FormularioCotacaoFacade getFormularioCotacaoFacade() {
        return formularioCotacaoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public FornecedorFacade getFornecedorFacade(){
        return fornecedorFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public UnidadeExternaFacade getUnidadeExternaFacade() {
        return unidadeExternaFacade;
    }
}
