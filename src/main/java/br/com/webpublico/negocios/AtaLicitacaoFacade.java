package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AtaLicitacao;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.ModeloDocto;
import br.com.webpublico.entidades.RatificacaoAta;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 17/03/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AtaLicitacaoFacade extends AbstractFacade<AtaLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public AtaLicitacaoFacade() {
        super(AtaLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<Licitacao> recuperaLicitacao(String parte) {
        String hql = "  select distinct  l "
            + "       from " + Licitacao.class.getSimpleName() + " as l"
            + " inner join l.listaDeStatusLicitacao as status"
            + " inner join l.processoDeCompra as p"
            + "      where (lower(translate(l.resumoDoObjeto,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')"
            +"              or to_char(l.numeroLicitacao) like :parte or to_char(l.exercicio.ano) like :parte or lower(l.modalidadeLicitacao) like :parte) "
            + "      and (status.tipoSituacaoLicitacao <> :homologada "
            + licitacaoFacade.adicionarCondicoesStatusLicitacaoHQL("l", "status") + ")"
            + "   order by l.numeroLicitacao desc ";
        Query query = em.createQuery(hql);
        query.setParameter("parte", "%" + parte.toLowerCase() + "%");
        query.setParameter("homologada", TipoSituacaoLicitacao.HOMOLOGADA);
        query.setMaxResults(50);
        return query.getResultList();
    }

    public List<ModeloDocto> recuperaModelo(String parte) {
        String hql = "select l" +
            " from " + ModeloDocto.class.getSimpleName() + " as l"
            + " where l.nome like :parte ";

        Query query = em.createQuery(hql);
        query.setParameter("parte", "%" + parte.toLowerCase() + "%");
        query.setMaxResults(10);
        return query.getResultList();
    }

    public Licitacao recarregarLicitacao(Licitacao licitacao) {
        String hql = "select l" +
            " from " + Licitacao.class.getSimpleName() + " as l"
            + " where l.id = :licitacao ";

        Query query = em.createQuery(hql);
        query.setParameter("licitacao", licitacao.getId());
        query.setMaxResults(10);
        for (Licitacao lit : (List<Licitacao>) query.getResultList()) {
            lit.getFornecedores().size();
        }
        return (Licitacao) query.getResultList().get(0);
    }

    public BigDecimal getProximoNumeroDaAtaPorLicitacao(Licitacao licitacao) {
        return (BigDecimal) em.createNativeQuery(" select count(id) from atalicitacao where licitacao_id = :lic ").setParameter("lic", licitacao.getId()).getSingleResult();
    }

    public RatificacaoAta recuperarRatificaoDaAtaRatificao(AtaLicitacao ataRatificacao) {
        return (RatificacaoAta) em.createQuery("select ratata from RatificacaoAta ratata where ataLicitacaoRatificacao = :ar ").setParameter("ar", ataRatificacao).getSingleResult();
    }

    // Ex: http://localhost:8080/webpublico/
    public String geraUrlImagemDir() {
        try {
            return FacesUtil.geraUrlImagemDir();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AtaLicitacao salvar(AtaLicitacao selecionado, RatificacaoAta ratificacaoAta) {
        try {
            selecionado = em.merge(selecionado);
            if (ratificacaoAta != null) {
                ratificacaoAta.setAtaLicitacaoRatificacao(selecionado);
                em.merge(ratificacaoAta);
            }
            return selecionado;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Problema ao salvar. " + ex.getMessage());
        }
    }

    public void excluirAtaRatificacao(AtaLicitacao selecionado) {
        try {
            RatificacaoAta ratificacaoAta = recuperarRatificaoDaAtaRatificao(selecionado);

            em.remove(ratificacaoAta);
            em.remove(em.contains(selecionado) ? selecionado : em.merge(selecionado));
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Não foi possível excluir este registro. " + ex.getMessage());
        }
    }
}
