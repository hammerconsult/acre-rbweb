package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BancoMultiplicadores;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by carlos on 25/08/15.
 */
@Stateless
public class BancoDeMultiplicadoresFacade implements Serializable {

    @EJB
    private HabilidadeFacade habilidadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EventoCapacitacaoFacade eventoCapacitacaoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public BancoDeMultiplicadoresFacade() {
    }

    public List<BancoDeMultiplicadores> recuperaInformacoes(Habilidade habilidade) {
        String sql = "SELECT M.ID AS MATRICULA, "
                + " E.ID AS CAPACITACAO, "
                + " LF.ID AS LOTACAO "
                + " FROM EVENTODERH E "
                + " INNER JOIN INSCRICAOCAPACITACAO I ON I.CAPACITACAO_ID = E.ID "
                + " INNER JOIN MATRICULAFP M ON M.ID = I.MATRICULAFP_ID "
                + " INNER JOIN VINCULOFP V ON V.MATRICULAFP_ID = M.ID "
                + " INNER JOIN LOTACAOFUNCIONAL LF ON LF.VINCULOFP_ID = V.ID "
                + " INNER JOIN CAPACITACAO C ON C.ID = E.ID "
                + " INNER JOIN CAPACITACAOHABILIDADE CH ON CH.CAPACITACAO_ID = C.ID "
                + " INNER JOIN HABILIDADE H ON H.ID = CH.HABILIDADE_ID "
                + " WHERE C.SITUACAO = 'EM_ANDAMENTO' "
                + " AND LF.FINALVIGENCIA IS NULL"
                + " AND H.DESCRICAO = :habilidade";

        Query q = em.createNativeQuery(sql);
        q.setParameter("habilidade", habilidade.getDescricao());

        List resultList = q.getResultList();

        List<BancoDeMultiplicadores> lista = Lists.newArrayList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object o : resultList) {
                Object[] objeto = (Object[]) o;
                BancoDeMultiplicadores bancoDeMultiplicadores = new BancoDeMultiplicadores();
                bancoDeMultiplicadores.setMatriculaFP(em.find(MatriculaFP.class, ((BigDecimal) objeto[0]).longValue()));
                bancoDeMultiplicadores.setEventoDeRH(em.find(EventoDeRH.class, ((BigDecimal) objeto[1]).longValue()));
                bancoDeMultiplicadores.setLotacaoFuncional(em.find(LotacaoFuncional.class, ((BigDecimal) objeto[2]).longValue()));
                lista.add(bancoDeMultiplicadores);
            }
        }

        return lista;
    }

    public void salvar(BancoDeMultiplicadores bancoDeMultiplicadores) {
        if (bancoDeMultiplicadores.getId() == null) {
            em.persist(bancoDeMultiplicadores);
        } else {
            em.merge(bancoDeMultiplicadores);
        }
    }

    public HabilidadeFacade getHabilidadeFacade() {
        return habilidadeFacade;
    }

    public EventoCapacitacaoFacade getEventoCapacitacaoFacade() {
        return eventoCapacitacaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
