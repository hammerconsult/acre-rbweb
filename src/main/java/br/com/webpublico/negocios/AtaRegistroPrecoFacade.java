package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.AtaRegistroPrecoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 03/04/14
 * Time: 21:48
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AtaRegistroPrecoFacade extends AbstractFacade<AtaRegistroPreco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private AdesaoFacade adesaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private ItemPropostaFornecedorFacade itemPropostaFornecedorFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ParticipanteIRPFacade participanteIRPFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private ExecucaoProcessoFacade execucaoProcessoFacade;
    @EJB
    private ItemPregaoFacade itemPregaoFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private SaldoProcessoLicitatorioFacade saldoProcessoLicitatorioFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;

    public AtaRegistroPrecoFacade() {
        super(AtaRegistroPreco.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void remover(AtaRegistroPreco entity) {
        List<AlteracaoContratual> alteracoesCont = alteracaoContratualFacade.buscarAlteracoesContratuaisPorAta(entity);
        alteracoesCont.forEach(alt -> em.remove(em.find(AlteracaoContratual.class, alt.getId())));
        super.remover(entity);
    }

    @Override
    public AtaRegistroPreco recuperar(Object id) {
        AtaRegistroPreco entity = super.recuperar(id);
        Hibernate.initialize(entity.getAdesoes());
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        inicializarItensSolicitacaoMaterialExterno(entity);
        entity.getLicitacao().setProcessoDeCompra(processoDeCompraFacade.recuperar(entity.getLicitacao().getProcessoDeCompra().getId()));
        return entity;
    }

    public AtaRegistroPreco recuperarComDependencias(Object id) {
        AtaRegistroPreco entity = super.recuperar(id);
        Hibernate.initialize(entity.getAdesoes());
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        inicializarItensSolicitacaoMaterialExterno(entity);
        entity.getLicitacao().setProcessoDeCompra(processoDeCompraFacade.recuperar(entity.getLicitacao().getProcessoDeCompra().getId()));
        return entity;
    }

    public AtaRegistroPreco recuperarSemDependencias(Object id) {
        return super.recuperar(id);
    }

    public AtaRegistroPreco recuperarComDependenciasAdesao(Object id) {
        AtaRegistroPreco entity = super.recuperar(id);
        Hibernate.initialize(entity.getAdesoes());
        return entity;
    }

    public AtaRegistroPreco salvarRetornandoAta(AtaRegistroPreco entity) {
        return em.merge(entity);
    }

    @Override
    public AtaRegistroPreco salvarRetornando(AtaRegistroPreco entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(recuperaUltimoNumero());
        }
        entity = em.merge(entity);
        salvarPortal(entity);
        return entity;
    }

    @Override
    public void salvar(AtaRegistroPreco entity) {
        super.salvar(entity);
        salvarPortal(entity);
    }

    private void salvarPortal(AtaRegistroPreco entity) {
        portalTransparenciaNovoFacade.salvarPortal(new AtaRegistroPrecoPortal(entity));
    }

    private void inicializarItensSolicitacaoMaterialExterno(AtaRegistroPreco arp) {
        if (arp.getAdesoes() != null && !arp.getAdesoes().isEmpty()) {
            for (Adesao adesao : arp.getAdesoes()) {
                if (adesao.isInterna()) {
                    Hibernate.initialize(adesao.getSolicitacaoMaterialExterno().getItensDaSolicitacao());
                }
            }
        }
    }

    public List<AtaRegistroPreco> recuperarAtasDeRegistroDePreco() {
        Query q = em.createQuery("from AtaRegistroPreco order by id desc");
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public Long recuperaUltimoNumero() {
        String hql = "select coalesce(max(arp.numero), 0) from AtaRegistroPreco arp";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        Long numero = (Long) q.getResultList().get(0);
        if (numero != null) {
            return numero + 1;
        } else {
            return Long.valueOf(1);
        }
    }

    public void atualizarAdesao(Adesao adesaoSelecionada) {
        em.merge(adesaoSelecionada);
    }

    public List<AtaRegistroPreco> recuperarAtaPorNumero(String prm) {
        String hql = " from AtaRegistroPreco arp WHERE cast(arp.numero as string ) like :prm";
        Query q = em.createQuery(hql);
        q.setParameter("prm", "%" + prm.trim() + "%");
        return q.getResultList();

    }

    public List<AtaRegistroPreco> buscarAtaRegistroPrecoPorNumeroOndeUsuarioGestorLicitacao(String numero, UsuarioSistema usuarioSistema) {
        String sql = " select ata.* " +
            "   from ataregistropreco ata  " +
            "  inner join licitacao l on l.id = ata.licitacao_id " +
            "  inner join processodecompra pc on pc.id = l.processodecompra_id " +
            "  inner join exercicio e on e.id = l.exercicio_id " +
            " where exists (select 1 from usuariounidadeorganizacio u_un " +
            "               where u_un.usuariosistema_id = :id_usuario " +
            "                 and u_un.unidadeorganizacional_id = pc.unidadeorganizacional_id " +
            "                 and u_un.gestorlicitacao = :gestor_licitacao) " +
            "      and ata.numero like :numero  "
            + licitacaoFacade.adicionarCondicoesStatusLicitacaoSQL("l")
            + " order by ata.numero desc ";
        Query q = em.createNativeQuery(sql, AtaRegistroPreco.class);
        q.setParameter("numero", "%" + numero.trim() + "%");
        q.setParameter("id_usuario", usuarioSistema.getId());
        q.setParameter("gestor_licitacao", Boolean.TRUE);
        return q.getResultList();
    }

    public List<AtaRegistroPreco> buscarAtaRegistroPrecoVigenteOndeUsuarioGestorLicitacao(String parte) {
        String sql = " select ata.* " +
            "   from ataregistropreco ata  " +
            "  inner join licitacao lic on lic.id = ata.licitacao_id " +
            "  inner join processodecompra pc on pc.id = lic.processodecompra_id " +
            "  inner join exercicio e on e.id = lic.exercicio_id " +
            "  where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ata.dataInicio) and coalesce(trunc(ata.dataVencimento), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and exists (select 1 from usuariounidadeorganizacio u_un " +
            "               where u_un.usuariosistema_id = :id_usuario " +
            "                 and u_un.unidadeorganizacional_id = ata.unidadeorganizacional_id " +
            "                 and u_un.gestorlicitacao = :gestor_licitacao) " +
            "  and (lower(to_char(ata.numero)) like :filtro " +
            "               or lower(translate(lic.resumodoobjeto,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "               or to_char(lic.numerolicitacao)  like :filtro                " +
            "               or to_char(e.ano) like :filtro     " +
            "               or to_char(lic.numerolicitacao) || '/' || to_char(e.ano) like :filtro) " +
            licitacaoFacade.adicionarCondicoesStatusLicitacaoSQL("lic")
            + " order by ata.numero desc ";
        Query q = em.createNativeQuery(sql, AtaRegistroPreco.class);
        q.setParameter("filtro", "%" + parte.trim() + "%");
        q.setParameter("id_usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("gestor_licitacao", Boolean.TRUE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<AtaRegistroPreco> buscarAtasRegistroPrecoVigentes(String param) {
        String hql = "select ata from AtaRegistroPreco ata "
            + "       inner join ata.licitacao l "
            + "       inner join l.listaDeStatusLicitacao status "
            + "       where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ata.dataInicio) and coalesce(trunc(ata.dataVencimento), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "         and ata.gerenciadora = :gerenciadora "
            + "       and lower(to_char(ata.numero)) like :filtro "
            + licitacaoFacade.adicionarCondicoesStatusLicitacaoHQL("l", "status")
            + " order by l.numeroLicitacao desc ";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + param.toLowerCase() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("gerenciadora", Boolean.TRUE);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return Lists.newArrayList();
        }
        return q.getResultList();
    }

    public List<AtaRegistroPreco> buscarAtaRegistroPreco(String param) {
        String sql = "select ata.* from ataregistropreco ata " +
            "         inner join licitacao lic on lic.id = ata.licitacao_id " +
            "         inner join exercicio  ex on ex.id = lic.exercicio_id  " +
            "       where (lower(to_char(ata.numero)) like :filtro " +
            "               or lower(translate(lic.resumodoobjeto,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "               or to_char(lic.numerolicitacao)  like :filtro                " +
            "               or to_char(ex.ano) like :filtro     " +
            "               or to_char(lic.numerolicitacao) || '/' || to_char(ex.ano) like :filtro) " +
            "       order by ata.numero desc ";
        Query q = em.createNativeQuery(sql, AtaRegistroPreco.class);
        q.setParameter("filtro", "%" + param.toLowerCase() + "%");
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public List<AtaRegistroPreco> buscarAtaRegistroPrecoComExecucao(String param, TipoObjetoCompra tipoObjetoCompra) {
        String sql = "select distinct ata.* from ataregistropreco ata " +
            "         inner join licitacao lic on lic.id = ata.licitacao_id " +
            "         inner join exercicio  ex on ex.id = lic.exercicio_id  " +
            "         inner join execucaoprocessoata exata on exata.ataregistropreco_id = ata.id " +
            "         inner join execucaoprocesso exproc on exproc.id = exata.execucaoprocesso_id" +
            "         inner join execucaoprocessoreserva exres on exres.execucaoprocesso_id = exproc.id " +
            "       where (lower(to_char(ata.numero)) like :filtro " +
            "               or lower(translate(lic.resumodoobjeto,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "               or to_char(lic.numerolicitacao)  like :filtro                " +
            "               or to_char(ex.ano) like :filtro     " +
            "               or to_char(lic.numerolicitacao) || '/' || to_char(ex.ano) like :filtro) " +
            "         and exres.tipoobjetocompra = :tipoOc" +
            "       order by ata.numero desc ";
        Query q = em.createNativeQuery(sql, AtaRegistroPreco.class);
        q.setParameter("filtro", "%" + param.toLowerCase() + "%");
        q.setParameter("tipoOc", tipoObjetoCompra.name());
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {

            return Lists.newArrayList();
        }
        return resultList;
    }

    public AtaRegistroPreco recuperarAtaRegistroPrecoPorUnidade(UnidadeOrganizacional unidadeOrganizacional, Licitacao licitacao, AtaRegistroPreco ata) {
        String sql = " " +
            " select ata.* from ataregistropreco ata  " +
            "  inner join licitacao l on l.id = ata.licitacao_id " +
            "  inner join processodecompra pc on pc.id = l.processodecompra_id " +
            "  inner join exercicio e on e.id = l.exercicio_id " +
            "  where ata.unidadeorganizacional_id = :id_unidade" +
            "  and l.id = :id_licitacao ";
        sql += ata != null && ata.getId() != null ? " and ata.id <> :idAta " : "";
        sql += licitacaoFacade.adicionarCondicoesStatusLicitacaoSQL("l") +
            " order by ata.numero desc ";
        Query q = em.createNativeQuery(sql, AtaRegistroPreco.class);
        q.setParameter("id_licitacao", licitacao.getId());
        q.setParameter("id_unidade", unidadeOrganizacional.getId());
        if (ata != null && ata.getId() != null) {
            q.setParameter("idAta", ata.getId());
        }
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (AtaRegistroPreco) q.getSingleResult();
        }
        return null;
    }

    public AtaRegistroPreco buscarPorLicitacao(Licitacao licitacao) {
        try {
            return (AtaRegistroPreco) em.createQuery("from AtaRegistroPreco where licitacao = :licitacao").setParameter("licitacao", licitacao).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<AtaRegistroPreco> buscarAtaPortalTransparencia(Exercicio exercicio) {
        String sql = "select ata.* from solicitacaomaterialext sol " +
            "inner join ataregistropreco ata on ata.id = sol.ataregistropreco_id " +
            "inner join licitacao lic on ata.licitacao_id = lic.id " +
            "inner join exercicio ex on sol.exercicio_id = ex.id "
            + " where ex.id = :exercicio";
        Query q = em.createNativeQuery(sql, AtaRegistroPreco.class);
        q.setParameter("exercicio", exercicio.getId());
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public AdesaoFacade getAdesaoFacade() {
        return adesaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public ItemPropostaFornecedorFacade getItemPropostaFornecedorFacade() {
        return itemPropostaFornecedorFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ParticipanteIRPFacade getParticipanteIRPFacade() {
        return participanteIRPFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public ExecucaoProcessoFacade getExecucaoProcessoFacade() {
        return execucaoProcessoFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public SaldoProcessoLicitatorioFacade getSaldoProcessoLicitatorioFacade() {
        return saldoProcessoLicitatorioFacade;
    }

    public AlteracaoContratualFacade getAlteracaoContratualFacade() {
        return alteracaoContratualFacade;
    }
}
