package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Adesao;
import br.com.webpublico.entidades.AtaRegistroPreco;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoSolicitacaoRegistroPreco;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/04/14
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AdesaoFacade extends AbstractFacade<Adesao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoFacade documentoFacade;
    @EJB
    private UnidadeExternaFacade unidadeExternaFacade;

    public AdesaoFacade() {
        super(Adesao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtaRegistroPrecoFacade getAtaRegistroPrecoFacade() {
        return ataRegistroPrecoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DocumentoFacade getDocumentoFacade() {
        return documentoFacade;
    }

    public UnidadeExternaFacade getUnidadeExternaFacade() {
        return unidadeExternaFacade;
    }

    public List<Adesao> recuperarAdesaoDaAtaDeRegistroDePreco(AtaRegistroPreco ataDeRegistroDePreco) {
        String hql = "select "
                   + "  ade "
                   + "from Adesao ade "
                   + "inner join ade.ataDeRegistroDePreco ata "
                   + "where ata = :param "
                   + "order by ade.id desc";

        Query q = em.createQuery(hql);
        q.setParameter("param", ataDeRegistroDePreco);

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<Adesao> buscarAdesaoInternaPorAtaRegistroPreco(String parte, AtaRegistroPreco ataRegistroPreco) {
        String sql = " select distinct ad.* from solicitacaomaterialext sol " +
            " inner join ataregistropreco ata on ata.id = sol.ataregistropreco_id " +
            " inner join hierarquiaorganizacional ho on ho.subordinada_id = sol.unidadeorganizacional_id " +
            "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            " inner join adesao ad on ad.ataderegistrodepreco_id = ata.id " +
            " where ata.id = :idAta " +
            " and sol.tiposolicitacaoregistropreco = :tipoAdesao " +
            " and ad.adesaoaceita = :adesaoAceita " +
            " and exists (select 1 from usuariounidadeorganizacio uuo  " +
            "            where uuo.usuariosistema_id = :idUsuario " +
            "            and uuo.unidadeorganizacional_id = ho.subordinada_id  " +
            "            and uuo.gestorlicitacao = :gestorLicitacao) " +
            " and (upper(translate(ad.observacao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') " +
            "      or ad.numerorequisicao like :parte) ";
        Query q = em.createNativeQuery(sql, Adesao.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("idAta", ataRegistroPreco.getId());
        q.setParameter("gestorLicitacao", Boolean.TRUE);
        q.setParameter("adesaoAceita", Boolean.TRUE);
        q.setParameter("tipoAdesao", TipoSolicitacaoRegistroPreco.INTERNA.name());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<Adesao> buscarAdesaoPorTipoGestorLicitacao(String parte) {
        String sql = " select distinct ad.* from adesao ad" +
            " inner join solicitacaomaterialext sol on sol.id = ad.solicitacaomaterialexterno_id  " +
            " inner join ataregistropreco ata on ata.id = sol.ataregistropreco_id " +
            " inner join hierarquiaorganizacional ho on ho.subordinada_id = sol.unidadeorganizacional_id " +
            "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            " where sol.tiposolicitacaoregistropreco = :tipoAdesao " +
            " and ad.adesaoaceita = :adesaoAceita " +
            " and exists (select 1 from usuariounidadeorganizacio uuo  " +
            "            where uuo.usuariosistema_id = :idUsuario " +
            "            and uuo.unidadeorganizacional_id = ho.subordinada_id  " +
            "            and uuo.gestorlicitacao = :gestorLicitacao) " +
            " and (upper(translate(ad.observacao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') " +
            "      or ad.numerorequisicao like :parte" +
            "      or upper(translate(sol.objeto,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') " +
            "      or sol.numero like :parte" +
            "      or replace(ho.codigo, '.', '') like :parte " +
            "      or ho.codigo like :parte " +
            "          or upper(translate(ho.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) " +
            " order by ad.numerorequisicao desc ";
        Query q = em.createNativeQuery(sql, Adesao.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("gestorLicitacao", Boolean.TRUE);
        q.setParameter("adesaoAceita", Boolean.TRUE);
        q.setParameter("tipoAdesao", TipoSolicitacaoRegistroPreco.INTERNA.name());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }
}
