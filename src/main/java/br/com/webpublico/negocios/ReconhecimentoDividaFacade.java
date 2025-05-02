package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.SituacaoReconhecimentoDivida;
import br.com.webpublico.enums.SituacaoSolicitacaoReconhecimentoDivida;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
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
public class ReconhecimentoDividaFacade extends AbstractFacade<ReconhecimentoDivida> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DoctoHabilitacaoFacade doctoHabilitacaoFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ConfigTipoObjetoCompraFacade configTipoObjetoCompraFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;

    public ReconhecimentoDividaFacade() {
        super(ReconhecimentoDivida.class);
    }

    @Override
    public ReconhecimentoDivida recuperar(Object id) {
        ReconhecimentoDivida reconhecimentoDivida = em.find(ReconhecimentoDivida.class, id);
        Hibernate.initialize(reconhecimentoDivida.getItens());
        Hibernate.initialize(reconhecimentoDivida.getDocumentosHabilitacao());
        Hibernate.initialize(reconhecimentoDivida.getPublicacoes());
        Hibernate.initialize(reconhecimentoDivida.getPareceres());
        Hibernate.initialize(reconhecimentoDivida.getHistoricos());
        for (ItemReconhecimentoDivida itemReconhecimentoDivida : reconhecimentoDivida.getItens()) {
            Hibernate.initialize(itemReconhecimentoDivida.getDotacoes());
        }
        for (ParecerReconhecimentoDivida parecerReconhecimentoDivida : reconhecimentoDivida.getPareceres()) {
            if (parecerReconhecimentoDivida.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(parecerReconhecimentoDivida.getDetentorArquivoComposicao().getArquivosComposicao());
            }
        }
        return reconhecimentoDivida;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return recuperar(id);
    }

    public List<Pessoa> buscarPessoasComVinculoVigente(String filtro) {
        return pessoaFacade.listaPessoaComVinculoVigente(filtro, SituacaoCadastralPessoa.ATIVO);
    }

    public List<ObjetoCompra> buscarObjetosDeCompra(String filtro) {
        return objetoCompraFacade.completaObjetoCompra(filtro);
    }

    public ReconhecimentoDivida salvarNovoRetornando(ReconhecimentoDivida entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigoContabil.getNumeroReconhecimentoDivida(entity.getExercicio(), entity.getUnidadeOrcamentaria(), entity.getDataReconhecimento()));
        }
        return super.salvarRetornando(entity);
    }

    public boolean isGestorControleInterno() {
        return usuarioSistemaFacade.isGestorControleInterno(sistemaFacade.getUsuarioCorrente(),
            sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente(),
            sistemaFacade.getDataOperacao());
    }

    public BigDecimal buscarSaldoFonteDespesaORC(ItemReconhecimentoDividaDotacao itemReconhecimentoDividaDotacao) {
        BigDecimal saldo = BigDecimal.ZERO;
        SaldoFonteDespesaORC saldoFonteDespesaORC = saldoFonteDespesaORCFacade.recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(
            itemReconhecimentoDividaDotacao.getFonteDespesaORC(),
            sistemaFacade.getDataOperacao(),
            itemReconhecimentoDividaDotacao.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());
        if (saldoFonteDespesaORC != null) {
            saldo = saldoFonteDespesaORC.getSaldoAtual();
        }
        return saldo;
    }

    public DoctoHabilitacaoFacade getDoctoHabilitacaoFacade() {
        return doctoHabilitacaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public ConfigTipoObjetoCompraFacade getConfigTipoObjetoCompraFacade() {
        return configTipoObjetoCompraFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Empenho> buscarEmpenhosPorReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        String sql = " select emp.* from empenho emp where emp.reconhecimentoDivida_id = :idReconhecimentoDivida ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idReconhecimentoDivida", reconhecimentoDivida.getId());
        return q.getResultList();
    }

    public List<EmpenhoEstorno> buscarEstornosDeEmpenhosPorReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        String sql = " select est.* " +
            " from EmpenhoEstorno est " +
            "  inner join empenho emp on emp.id = est.EMPENHO_ID " +
            " where emp.reconhecimentoDivida_id = :idReconhecimentoDivida ";
        Query q = em.createNativeQuery(sql, EmpenhoEstorno.class);
        q.setParameter("idReconhecimentoDivida", reconhecimentoDivida.getId());
        return q.getResultList();
    }

    public List<Liquidacao> buscarLiquidacoesPorReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        String sql = " select liq.* " +
            " from Liquidacao liq " +
            "  inner join empenho emp on emp.id = liq.EMPENHO_ID " +
            " where emp.reconhecimentoDivida_id = :idReconhecimentoDivida ";
        Query q = em.createNativeQuery(sql, Liquidacao.class);
        q.setParameter("idReconhecimentoDivida", reconhecimentoDivida.getId());
        return q.getResultList();
    }

    public List<LiquidacaoEstorno> buscarEstornosDeLiquidacoesPorReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        String sql = " select est.* " +
            " from LiquidacaoEstorno est " +
            "  inner join Liquidacao liq on liq.id = est.LIQUIDACAO_ID " +
            "  inner join empenho emp on emp.id = liq.EMPENHO_ID " +
            " where emp.reconhecimentoDivida_id = :idReconhecimentoDivida ";
        Query q = em.createNativeQuery(sql, LiquidacaoEstorno.class);
        q.setParameter("idReconhecimentoDivida", reconhecimentoDivida.getId());
        return q.getResultList();
    }

    public List<Pagamento> buscarPagamentosPorReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        String sql = " select pag.* " +
            " from Pagamento pag " +
            "  inner join Liquidacao liq on liq.id = pag.LIQUIDACAO_ID " +
            "  inner join empenho emp on emp.id = liq.EMPENHO_ID " +
            " where emp.reconhecimentoDivida_id = :idReconhecimentoDivida ";
        Query q = em.createNativeQuery(sql, Pagamento.class);
        q.setParameter("idReconhecimentoDivida", reconhecimentoDivida.getId());
        return q.getResultList();
    }

    public List<PagamentoEstorno> buscarEstornosDePagamentosPorReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        String sql = " select est.* " +
            " from PagamentoEstorno est " +
            "  inner join Pagamento pag on pag.id = est.PAGAMENTO_ID " +
            "  inner join Liquidacao liq on liq.id = pag.LIQUIDACAO_ID " +
            "  inner join empenho emp on emp.id = liq.EMPENHO_ID " +
            " where emp.reconhecimentoDivida_id = :idReconhecimentoDivida ";
        Query q = em.createNativeQuery(sql, PagamentoEstorno.class);
        q.setParameter("idReconhecimentoDivida", reconhecimentoDivida.getId());
        return q.getResultList();
    }

    public List<ReconhecimentoDivida> buscarReconhecimentosDaDivida(String filtro, Exercicio exercicio) {
        String sql = " select rec.* from reconhecimentodivida rec " +
            " inner join pessoafisica pfResp on rec.responsavel_id = pfResp.ID " +
            " inner join pessoa forn on rec.fornecedor_id = forn.ID " +
            " left join pessoafisica pf on forn.id = pf.id " +
            " left join pessoajuridica pj on forn.id = pj.ID " +
            " where (rec.numero like :filtro or to_char(rec.datareconhecimento, 'dd/MM/yyyy') like :filtro " +
            " or upper(pfResp.nome) like :filtro or replace(replace(pfResp.cpf, '-', ''), '.', '') like :filtro " +
            " or upper(coalesce(pf.nome, pj.RAZAOSOCIAL)) like :filtro " +
            " or replace(replace(replace(coalesce(pf.cpf, pj.cnpj), '/', ''), '-', ''), '.', '') like :filtro " +
            " or upper(rec.objeto) like :filtro) " +
            " and rec.situacaoReconhecimentoDivida = :aprovado " +
            " and rec.exercicio_id = :exercicio " +
            " and not exists (select 1 from SOLRECONHECIMENTODIVIDA sol where sol.reconhecimentodivida_id = rec.id) " +
            " order by rec.dataReconhecimento desc ";
        Query q = em.createNativeQuery(sql, ReconhecimentoDivida.class);
        q.setParameter("filtro", "%" + filtro.trim().toUpperCase() + "%");
        q.setParameter("aprovado", SituacaoReconhecimentoDivida.APROVADO.name());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<ReconhecimentoDivida> buscarReconhecimentoDividaSolicitacaoConcluida(String parte) {
        String sql = " select distinct rec.* from reconhecimentodivida rec " +
            "           inner join solreconhecimentodivida sol on sol.reconhecimentodivida_id = rec.id " +
            "           inner join solicitacaoempenho solEmp on solEmp.reconhecimentodivida_id = rec.id " +
            "           inner join empenho emp on emp.id = solEmp.empenho_id " +
            "           inner join pessoafisica pfResp on rec.responsavel_id = pfResp.ID " +
            "           inner join pessoa forn on rec.fornecedor_id = forn.ID " +
            "           left join pessoafisica pf on forn.id = pf.id " +
            "           left join pessoajuridica pj on forn.id = pj.ID " +
            "           inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = rec.unidadeadministrativa_id " +
            "          where (rec.numero like :filtro or to_char(rec.datareconhecimento, 'dd/MM/yyyy') like :filtro " +
            "                 or upper(pfResp.nome) like :filtro or replace(replace(pfResp.cpf, '-', ''), '.', '') like :filtro " +
            "                 or upper(coalesce(pf.nome, pj.RAZAOSOCIAL)) like :filtro " +
            "                 or replace(replace(replace(coalesce(pf.cpf, pj.cnpj), '/', ''), '-', ''), '.', '') like :filtro " +
            "                  or upper(rec.objeto) like :filtro) " +
            "           and sol.situacao = :concluido " +
            "           and uuo.usuariosistema_id = :idUsuario ";
        Query q = em.createNativeQuery(sql, ReconhecimentoDivida.class);
        q.setParameter("filtro", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("concluido", SituacaoSolicitacaoReconhecimentoDivida.CONCLUIDO.name());
        return q.getResultList();
    }

    public List<ItemReconhecimentoDivida> buscarItemReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        String sql = " select item.* from itemreconhecimentodivida item " +
            "           inner join objetocompra ob on ob.id = item.objetocompra_id " +
            "          where item.reconhecimentodivida_id = :reconhecimento" +
            "          order by ob.codigo ";
        Query q = em.createNativeQuery(sql, ItemReconhecimentoDivida.class);
        q.setParameter("reconhecimento", reconhecimentoDivida.getId());
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }
}
