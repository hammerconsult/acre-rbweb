package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoRomaneioFeira;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class RomaneioFeiraFacade extends AbstractFacade<RomaneioFeira> {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FeiranteFacade feiranteFacade;
    @EJB
    private FeiraFacade feiraFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RomaneioFeiraFacade() {
        super(RomaneioFeira.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(RomaneioFeira entity) {
        super.salvarRetornando(entity);
    }

    @Override
    public RomaneioFeira recuperar(Object id) {
        RomaneioFeira romaneio = super.recuperar(id);
        Hibernate.initialize(romaneio.getFeirantes());
        for (RomaneioFeiraFeirante feira : romaneio.getFeirantes()) {
            Hibernate.initialize(feira.getProdutos());
        }
        return romaneio;
    }

    public RomaneioFeira finalizarRomaneio(RomaneioFeira entity) {
        entity.setSituacao(SituacaoRomaneioFeira.FINALIZADO);
        return em.merge(entity);
    }

    public List<ProdutoFeira> buscarProdutos(String parte) {
        String sql = " SELECT PF.* FROM PRODUTOFEIRA PF " +
            "      WHERE PF.NOME LIKE :param " +
            "      or to_char(PF.NOME) LIKE :param " +
            "      or pf.codigo like :param  ";
        Query q = em.createNativeQuery(sql, ProdutoFeira.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public FeiranteFacade getFeiranteFacade() {
        return feiranteFacade;
    }

    public FeiraFacade getFeiraFacade() {
        return feiraFacade;
    }
}
