package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.AtaRegistroDePrecoExternoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.Persistencia;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 04/08/14
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RegistroSolicitacaoMaterialExternoFacade extends AbstractFacade<RegistroSolicitacaoMaterialExterno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SolicitacaoMaterialExternoFacade solicitacaoMaterialExternoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public RegistroSolicitacaoMaterialExternoFacade() {
        super(RegistroSolicitacaoMaterialExterno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SolicitacaoMaterialExternoFacade getSolicitacaoMaterialExternoFacade() {
        return solicitacaoMaterialExternoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    @Override
    public RegistroSolicitacaoMaterialExterno recuperar(Object id) {
        RegistroSolicitacaoMaterialExterno entity = super.recuperar(id);
        entity.getPublicacoes().size();
        entity.getFornecedores();

        carregarListasFornecedor(entity);

        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        return entity;
    }

    private void carregarListasFornecedor(RegistroSolicitacaoMaterialExterno rsme) {
        if (rsme.getFornecedores() != null && !rsme.getFornecedores().isEmpty()) {
            for (RegistroSolicitacaoMaterialExternoFornecedor fornecedor : rsme.getFornecedores()) {
                fornecedor.getItens().size();
            }
        }
    }

    public List<Pessoa> recuperarFornecedoresRegistroPrecoExterno(RegistroSolicitacaoMaterialExterno rsme) {
        String hql = "select p from RegistroSolicitacaoMaterialExterno rsme" +
            " inner join rsme.fornecedores f" +
            " inner join f.pessoa p" +
            " where rsme = :rsme";
        Query q = em.createQuery(hql);
        q.setParameter("rsme", rsme);
        List<Pessoa> pessoas = q.getResultList();
        pessoas = new ArrayList(new HashSet(pessoas));
        return pessoas;
    }

    public RegistroSolicitacaoMaterialExterno recuperarRegistroSolicitacaoMaterialExternoPorNumeroAnoDoProcesso(Integer numeroProcesso, Integer anoProcesso) {
        String hql = "select rsme from RegistroSolicitacaoMaterialExterno rsme" +
            " inner join rsme.exercicioRegistro e" +
            "      where rsme.numeroRegistro = :numeroProcesso" +
            "        and e.ano = :anoProcesso";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("numeroProcesso", numeroProcesso);
        q.setParameter("anoProcesso", anoProcesso);
        try {
            return (RegistroSolicitacaoMaterialExterno) q.getSingleResult();
        } catch (NoResultException npe) {
            return null;
        }
    }

    public Boolean isRegistroPrecoExternoComItensDeMateriais(RegistroSolicitacaoMaterialExterno rsme) {
        String hql = "select rsme.id from RegistroSolicitacaoMaterialExterno rsme" +
            " inner join rsme.solicitacaoMaterialExterno sme " +
            " inner join sme.itensDaSolicitacao item" +
            " inner join item.objetoCompra objeto" +
            " where rsme = :rsme and objeto.tipoObjetoCompra <> :tipo_objeto_compra";
        Query q = em.createQuery(hql);
        q.setParameter("rsme", rsme);
        q.setParameter("tipo_objeto_compra", TipoObjetoCompra.SERVICO);
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public Boolean isRegistroPrecoExternoComItensDeServicos(RegistroSolicitacaoMaterialExterno rsme) {
        String hql = "select rsme.id from RegistroSolicitacaoMaterialExterno rsme" +
            " inner join rsme.solicitacaoMaterialExterno sme " +
            " inner join sme.itensDaSolicitacao item" +
            " inner join item.objetoCompra objeto " +
            " where rsme = :rsme and objeto.tipoObjetoCompra =:tipo_objeto_compra ";
        Query q = em.createQuery(hql);
        q.setParameter("rsme", rsme);
        q.setParameter("tipo_objeto_compra", TipoObjetoCompra.SERVICO);
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }


    public List<RegistroSolicitacaoMaterialExterno> buscarRegistroSolicitacaoMaterialExternoPorNumeroOrExercicioAndUsuarioAndGestor(String parte, UsuarioSistema usuarioSistema, Boolean gestorLicitacao) {
        String sql = "select r.* " +
            "   from registrosolmatext r " +
            "  inner join exercicio e on r.exercicioregistro_id = e.id " +
            "  inner join solicitacaomaterialext sol on sol.id = r.solicitacaomaterialexterno_id " +
            "  inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = r.unidadeorganizacional_id " +
            "  where (to_char(r.numeroregistro) like :parte or to_char(e.ano) like :parte " +
            "    or to_char(r.numeroregistro) || '/' || to_char(e.ano) like :parte  " +
            "    or to_char(sol.numero) || '/' || to_char(e.ano) like :parte ) " +
            "  and uuo.usuariosistema_id = :usuario_id " +
            "  and uuo.gestorlicitacao = :gestor_licitacao " +
            " order by r.numeroregistro desc, e.ano desc ";
        Query q = em.createNativeQuery(sql, RegistroSolicitacaoMaterialExterno.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("usuario_id", usuarioSistema.getId());
        q.setParameter("gestor_licitacao", gestorLicitacao);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<RegistroSolicitacaoMaterialExterno> buscarRegistroPrecoExterno(String parte) {
        String sql = "select r.* " +
            "   from registrosolmatext r " +
            "  inner join exercicio e on r.exercicioregistro_id = e.id " +
            "  inner join solicitacaomaterialext sol on sol.id = r.solicitacaomaterialexterno_id " +
            "  where (to_char(r.numeroregistro) like :parte or to_char(e.ano) like :parte " +
            "    or to_char(r.numeroregistro) || '/' || to_char(e.ano) like :parte  " +
            "    or to_char(sol.numero) || '/' || to_char(e.ano) like :parte ) " +
            " order by r.numeroregistro desc, e.ano desc ";
        Query q = em.createNativeQuery(sql, RegistroSolicitacaoMaterialExterno.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<RegistroSolicitacaoMaterialExterno> recuperarRegistroSolicitacaoMaterialExternoPorSolicitacao(SolicitacaoMaterialExterno solicitacaoMaterialExterno) {
        String hql = "select r from RegistroSolicitacaoMaterialExterno r where r.solicitacaoMaterialExterno = :solicitacao";
        return em.createQuery(hql).setParameter("solicitacao", solicitacaoMaterialExterno).getResultList();
    }

    public List<RegistroSolicitacaoMaterialExterno> buscarAtaPortalTransparencia(Exercicio exercicio) {
        String sql = "select ata.*  " +
            "from SOLICITACAOMATERIALEXT sol " +
            "inner join REGISTROSOLMATEXT ata on ata.SOLICITACAOMATERIALEXTERNO_ID = sol.id " +
            "inner join exercicio ex on sol.EXERCICIO_ID = ex.id "
            + " where ex.id = :exercicio";
        Query q = em.createNativeQuery(sql, RegistroSolicitacaoMaterialExterno.class);
        q.setParameter("exercicio", exercicio.getId());
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    @Override
    public void salvar(RegistroSolicitacaoMaterialExterno entity) {
        super.salvar(entity);
        salvarPortal(entity);
    }

    @Override
    public void salvarNovo(RegistroSolicitacaoMaterialExterno entity) {
        if (entity.getNumeroRegistro() == null){
            entity.setNumeroRegistro(singletonGeradorCodigo.getProximoCodigo(RegistroSolicitacaoMaterialExterno.class, "numeroRegistro").intValue());
        }
        super.salvarNovo(entity);
        salvarPortal(entity);
    }

    private void salvarPortal(RegistroSolicitacaoMaterialExterno entity) {
        portalTransparenciaNovoFacade.salvarPortal(new AtaRegistroDePrecoExternoPortal(entity));
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    @Override
    @Transactional
    public void remover(RegistroSolicitacaoMaterialExterno entity) {
        try {
            Object chave = Persistencia.getId(entity);
            Object obj = recuperar(chave);
            if (obj != null) {
                removerDependencias(entity);
                getEntityManager().remove(obj);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void removerDependencias(RegistroSolicitacaoMaterialExterno entity) {
        List<AtaRegistroDePrecoExternoPortal> listaAtas = buscarAtaRegistroDePrecoExternoPortal(entity);

        for(AtaRegistroDePrecoExternoPortal ata : listaAtas) {
            ata.setRegistroSolicitacaoMaterialExterno(null);
            em.remove(ata);
        }

    }

    public List<AtaRegistroDePrecoExternoPortal> buscarAtaRegistroDePrecoExternoPortal(RegistroSolicitacaoMaterialExterno selecionado) {
        String sql = " SELECT a.id, a.REGISTROSOLMATEXT_ID, e.SITUACAO, e.TIPO FROM ATAREGISTROEXTERNOPORTAL a " +
            " INNER JOIN ENTIDADEPORTALTRANSPARENCIA e ON e.ID = a.ID " +
            " WHERE a.REGISTROSOLMATEXT_ID = :registroSolMatExtId";

        Query q = em.createNativeQuery(sql, AtaRegistroDePrecoExternoPortal.class);
        q.setParameter("registroSolMatExtId", selecionado.getId());
        List resultado = q.getResultList();

        if(resultado == null){
            return Lists.newArrayList();
        }
        return resultado;
    }

    public Boolean verificarSeExisteContrato(RegistroSolicitacaoMaterialExterno selecionado) {
        String sql;
            sql = " select rsme.* from registrosolmatext rsme " +
                "   inner join conregprecoexterno crpx on rsme.id = crpx.regsolmatext_id " +
                "   where rsme.id = :id ";
        Query q = em.createNativeQuery(sql, RegistroSolicitacaoMaterialExterno.class);
        q.setParameter("id", selecionado.getId());
        return !q.getResultList().isEmpty();
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }
}
