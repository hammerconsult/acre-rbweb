package br.com.webpublico.negocios.rh.pccr;


import br.com.webpublico.entidades.rh.pccr.CategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.EnquadramentoPCCR;
import br.com.webpublico.entidades.rh.pccr.PlanoCargosSalariosPCCR;
import br.com.webpublico.entidades.rh.pccr.ProgressaoPCCR;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.negocios.AbstractFacade;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 25/06/14
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class EnquadramentoPCCRFacade extends AbstractFacade<EnquadramentoPCCR> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public EnquadramentoPCCRFacade() {
        super(EnquadramentoPCCR.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void preencheTudoTUUUUDO() {

        PlanoCargosSalariosPCCR admDireta = new PlanoCargosSalariosPCCR();
        admDireta.setInicioVigencia(getDataPadrao());
        admDireta.setDescricao("SERVIDORES DA ADMINISTRAÇÃO DIRETA");
        admDireta.setTipoPCS(TipoPCS.QUADRO_EFETIVO);
        PlanoCargosSalariosPCCR rbtrans = new PlanoCargosSalariosPCCR();
        rbtrans.setInicioVigencia(getDataPadrao());
        rbtrans.setDescricao("SERVIDORES DO RBTRANS");
        rbtrans.setTipoPCS(TipoPCS.QUADRO_EFETIVO);
        PlanoCargosSalariosPCCR cargoComissao = new PlanoCargosSalariosPCCR();
        cargoComissao.setInicioVigencia(getDataPadrao());
        cargoComissao.setDescricao("TABELA DE CARGOS COMISSIONADOS");
        cargoComissao.setTipoPCS(TipoPCS.CARGO_EM_COMISSAO);

        CategoriaPCCR pai = new CategoriaPCCR();
        pai.setDescricao("Grupo I");
        pai.setPlanoCargosSalariosPCCR(admDireta);
        pai.setFilhos(getFilhosGrupoI(pai));
        CategoriaPCCR grupoII = new CategoriaPCCR();
        grupoII.setDescricao("Grupo II");
        grupoII.setPlanoCargosSalariosPCCR(admDireta);
        grupoII.setFilhos(getFilhosGrupoII(grupoII));
        em.persist(admDireta);
        em.persist(rbtrans);
        em.persist(cargoComissao);
        em.persist(pai);
        em.persist(grupoII);
        for (ProgressaoPCCR prog : getProgressoes(admDireta)) {
            em.persist(prog);
        }


    }

    private List<ProgressaoPCCR> getProgressoes(PlanoCargosSalariosPCCR admDireta) {
        ProgressaoPCCR progressao1 = new ProgressaoPCCR();
        progressao1.setDescricao("A");
        progressao1.setPlanoCargosSalarios(admDireta);

        ProgressaoPCCR progressao2 = new ProgressaoPCCR();
        progressao2.setDescricao("B");
        progressao2.setPlanoCargosSalarios(admDireta);

        ProgressaoPCCR progressao3 = new ProgressaoPCCR();
        progressao3.setDescricao("C");
        progressao3.setPlanoCargosSalarios(admDireta);

        ProgressaoPCCR progressao4 = new ProgressaoPCCR();
        progressao4.setDescricao("D");
        progressao4.setPlanoCargosSalarios(admDireta);

        List<ProgressaoPCCR> progressaoPCCRList = new ArrayList<>();
        progressaoPCCRList.add(progressao1);
        progressaoPCCRList.add(progressao2);
        progressaoPCCRList.add(progressao3);
        progressaoPCCRList.add(progressao4);
        return progressaoPCCRList;
    }

    private List<CategoriaPCCR> getFilhosGrupoI(CategoriaPCCR superior) {
        CategoriaPCCR filho1 = new CategoriaPCCR();
        filho1.setDescricao("Nível I");
        filho1.setPlanoCargosSalariosPCCR(superior.getPlanoCargosSalariosPCCR());
        filho1.setSuperior(superior);
        CategoriaPCCR filho2 = new CategoriaPCCR();
        filho2.setDescricao("Nível II");
        filho2.setPlanoCargosSalariosPCCR(superior.getPlanoCargosSalariosPCCR());
        filho2.setSuperior(superior);
        List<CategoriaPCCR> filhos = new ArrayList<>();
        filhos.add(filho1);
        filhos.add(filho2);
        return filhos;
    }

    private List<CategoriaPCCR> getFilhosGrupoII(CategoriaPCCR superior) {
        CategoriaPCCR filho1 = new CategoriaPCCR();
        filho1.setDescricao("Nível III");
        filho1.setPlanoCargosSalariosPCCR(superior.getPlanoCargosSalariosPCCR());
        filho1.setSuperior(superior);
        CategoriaPCCR filho2 = new CategoriaPCCR();
        filho2.setDescricao("Nível IV");
        filho2.setPlanoCargosSalariosPCCR(superior.getPlanoCargosSalariosPCCR());
        filho2.setSuperior(superior);
        List<CategoriaPCCR> filhos = new ArrayList<>();
        filhos.add(filho1);
        filhos.add(filho2);
        return filhos;
    }


    public Date getDataPadrao() {
        DateTime dateTime = new DateTime(2000, 1, 1, 0, 0);
        return dateTime.toDate();
    }

    public List<PlanoCargosSalariosPCCR> findPlanosCargosPCCRVigentes(Date data) {
        Query q = em.createQuery("from PlanoCargosSalariosPCCR where :data between inicioVigencia and coalesce(finalVigencia,:data)");
        q.setParameter("data", data);
        return q.getResultList();
    }

    public List<CategoriaPCCR> findCategoriasPCCRByPlanoCargos(PlanoCargosSalariosPCCR plano) {
        Query q = em.createQuery("from CategoriaPCCR where planoCargosSalariosPCCR = :plano and superior is null");
        q.setParameter("plano", plano);
        return q.getResultList();
    }

    public List<ProgressaoPCCR> findProgressoesPCCRByPlanoCargos(PlanoCargosSalariosPCCR plano) {
        Query q = em.createQuery("from ProgressaoPCCR where planoCargosSalarios = :plano order by descricao");
        q.setParameter("plano", plano);
        return q.getResultList();
    }

    public PlanoCargosSalariosPCCR findOnePlanosCargosPCCR(Long id) {
        Query q = em.createQuery("from PlanoCargosSalariosPCCR where id = :id ");
        q.setParameter("id", id);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (PlanoCargosSalariosPCCR) q.getResultList().get(0);
    }
}


