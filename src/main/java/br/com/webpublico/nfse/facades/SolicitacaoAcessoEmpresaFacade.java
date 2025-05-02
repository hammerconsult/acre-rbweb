/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.EscritorioContabil;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EscritorioContabilFacade;
import br.com.webpublico.nfse.domain.SolicitacaoAcessoEmpresa;
import br.com.webpublico.nfse.domain.dtos.SolicitacaoAcessoEmpresaAvaliacaoDTO;
import br.com.webpublico.nfse.domain.dtos.SolicitacaoAcessoEmpresaDTO;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Stateless
public class SolicitacaoAcessoEmpresaFacade extends AbstractFacade<SolicitacaoAcessoEmpresa> {

    @EJB
    private EscritorioContabilFacade escritorioContabilFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitacaoAcessoEmpresaFacade() {
        super(SolicitacaoAcessoEmpresa.class);
    }

    public List<SolicitacaoAcessoEmpresa> buscarSolicitacaoEmAvaliacao(String cnpjEmpresa, String login) {
        Query q = em.createQuery("from SolicitacaoAcessoEmpresa where cnpj = :cnpjEmpresa and nfseUser.login = :login");
        q.setParameter("cnpjEmpresa", cnpjEmpresa);
        q.setParameter("login", login);
        return q.getResultList();
    }

    public boolean hasSolicitacaoDoUsuarioParaEmpresa(Long id, String cnpjEmpresa, String login) {
        Query q = em.createQuery("from SolicitacaoAcessoEmpresa where id != :id and cnpj = :cnpjEmpresa and nfseUser.login = :login");
        q.setParameter("id", id);
        q.setParameter("cnpjEmpresa", cnpjEmpresa);
        q.setParameter("login", login);
        try {
            return (SolicitacaoAcessoEmpresa) q.getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    public SolicitacaoAcessoEmpresa criarSolicitacaoAcessoEmpresa(SolicitacaoAcessoEmpresaDTO solicitacaoAcessoEmpresaDTO) {
        SolicitacaoAcessoEmpresa solicitacaoAcessoEmpresa = new SolicitacaoAcessoEmpresa(solicitacaoAcessoEmpresaDTO);

//        UsuarioWeb user = usuarioNfseFacade.recuperarUsuarioPorLogin(solicitacaoAcessoEmpresaDTO.getNfseUser().getLogin());
        EscritorioContabil es = null;

        if (solicitacaoAcessoEmpresaDTO.getEscritorioContabil() != null) {
            es = escritorioContabilFacade.recuperar(solicitacaoAcessoEmpresaDTO.getEscritorioContabil().getId());
        }

//        if (!empresaFacade.hasEmpresaPorCnpj(solicitacaoAcessoEmpresaDTO.getCnpj())) {
//            NfseEmpresa empresa = new NfseEmpresa(solicitacaoAcessoEmpresaDTO);
//            empresaFacade.create(empresa);
//        }
//        solicitacaoAcessoEmpresa.setNfseUser(user);
        solicitacaoAcessoEmpresa.setEscritorioContabil(es);
        solicitacaoAcessoEmpresa = em.merge(solicitacaoAcessoEmpresa);
        return solicitacaoAcessoEmpresa;
    }

    public Page<SolicitacaoAcessoEmpresa> recuperarSolicitacoesPorUsuarioEQuery(Pageable pageable, String usuario, String query) {
        String hql = "select new SolicitacaoAcessoEmpresa(s.id, s.cnpj, s.razaoSocial, s.nomeFantasia, s.situacao, s.solicitadaEm) from SolicitacaoAcessoEmpresa s join s.nfseUser as u";

        if (!StringUtils.isBlank(query)) {
            hql += " where u.login =:usuario and (lower(s.cnpj) like :busca or lower(s.razaoSocial) like :busca or lower(s.nomeFantasia) like :busca or lower(s.situacao) like :busca or to_date(s.solicitadaEm, 'DD/MM/YYYY') = to_date( :data, 'DD/MM/YYYY'))";
        } else {
            hql += " where u.login =:usuario";
        }
        hql += PesquisaGenericaNfseUtil.montarOrdem(pageable);

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("usuario", usuario);

        String dateString = getDataValidaStringSePossivel(query);
        if (!StringUtils.isBlank(query)) {
            q.setParameter("data", dateString);
        }
        q = PesquisaGenericaNfseUtil.atribuirParametroDeBusca(q, query);
        int resultCount = q.getResultList().size();
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        return new PageImpl(q.getResultList(), pageable, resultCount);
    }

    public SolicitacaoAcessoEmpresa recuperarPorId(Long id) {
        return em.find(SolicitacaoAcessoEmpresa.class, id);
    }

    public SolicitacaoAcessoEmpresa atualizarSolicitacaoAcessoEmpresa(SolicitacaoAcessoEmpresaDTO solicitacaoAcessoEmpresaDTO) {
        SolicitacaoAcessoEmpresa solicitacaoAcessoEmpresa = new SolicitacaoAcessoEmpresa(solicitacaoAcessoEmpresaDTO);
//        UsuarioWeb user = usuarioNfseFacade.recuperarUsuarioPorLogin(solicitacaoAcessoEmpresaDTO.getNfseUser().getLogin());
//        solicitacaoAcessoEmpresa.setNfseUser(user);

        EscritorioContabil escritorio = null;
        if (solicitacaoAcessoEmpresaDTO.getEscritorioContabil() != null && solicitacaoAcessoEmpresaDTO.getEscritorioContabil().getId() != null) {
            escritorio = escritorioContabilFacade.recuperar(solicitacaoAcessoEmpresaDTO.getEscritorioContabil().getId());
        }
        solicitacaoAcessoEmpresa.setEscritorioContabil(escritorio);

        solicitacaoAcessoEmpresa = em.merge(solicitacaoAcessoEmpresa);

        return solicitacaoAcessoEmpresa;
    }

    private String getDataValidaStringSePossivel(String possivelData) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = null;
        try {
            Date date = sdf.parse(possivelData);
            sdf = new SimpleDateFormat("dd/MM/yy");
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public SolicitacaoAcessoEmpresa atualizarSolicitacaoAcessoEmpresaAvaliacao(SolicitacaoAcessoEmpresaAvaliacaoDTO solicitacaoAcessoEmpresaAvaliacaoDTO) {
        SolicitacaoAcessoEmpresa solicitacaoAcessoEmpresa = new SolicitacaoAcessoEmpresa(solicitacaoAcessoEmpresaAvaliacaoDTO);

//        UsuarioWeb userAvaliador = usuarioNfseFacade.recuperarUsuarioPorLogin(solicitacaoAcessoEmpresaAvaliacaoDTO.getNfseUserAvaliacao().getLogin());

//        solicitacaoAcessoEmpresa.setNfseUserAvaliacao(userAvaliador);

        EscritorioContabil escritorio = null;
        if (solicitacaoAcessoEmpresaAvaliacaoDTO.getEscritorioContabil() != null && solicitacaoAcessoEmpresaAvaliacaoDTO.getEscritorioContabil().getId() != null) {
            escritorio = escritorioContabilFacade.recuperar(solicitacaoAcessoEmpresaAvaliacaoDTO.getEscritorioContabil().getId());
        }
        solicitacaoAcessoEmpresa.setEscritorioContabil(escritorio);

        solicitacaoAcessoEmpresa = em.merge(solicitacaoAcessoEmpresa);

        return solicitacaoAcessoEmpresa;
    }

    public Page<SolicitacaoAcessoEmpresa> recuperarSolicitacoesAguardandoAvaliacaoPorQuery(Pageable pageable, String query) {
        String hql = "select new SolicitacaoAcessoEmpresa(s.id, s.cnpj, s.razaoSocial, s.nomeFantasia, s.situacao, s.solicitadaEm, u.id, u.login) from SolicitacaoAcessoEmpresa s join s.nfseUser as u";
        hql += " where s.situacao = 'AGUARDANDO_AVALIACAO'";

        if (!StringUtils.isBlank(query)) {
            hql += " and (lower(s.cnpj) like :busca or lower(s.razaoSocial) like :busca or lower(s.nomeFantasia) like :busca or lower(s.situacao) like :busca)";
        }
        hql += PesquisaGenericaNfseUtil.montarOrdem(pageable);

        Query q = getEntityManager().createQuery(hql);
        q = PesquisaGenericaNfseUtil.atribuirParametroDeBusca(q, query);
        int resultCount = q.getResultList().size();
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        return new PageImpl(q.getResultList(), pageable, resultCount);
    }

}
