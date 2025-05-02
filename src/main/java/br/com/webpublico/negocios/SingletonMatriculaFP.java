package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MatriculaFP;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 25/11/13
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonMatriculaFP implements Serializable {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private List<String> matriculas;
    private List<String> matriculasUsadas;
    private Integer ultimaMatriculaUsada;
    private final Integer NUMEROMATRICULAS = 5;
    @EJB
    private MatriculaFPFacade matriculaFacade;

    @PostConstruct
    private void init() {
        matriculas = new ArrayList<>();
        matriculasUsadas = new ArrayList<>();
        ultimaMatriculaUsada = recuperaUltimaMatriculaDoBanco();
    }

    public Integer recuperaUltimaMatriculaDoBanco() {
        Query q = em.createQuery("select max(cast(m.matricula as integer)) from MatriculaFP m");
        q.setMaxResults(1);
        try {
            return (Integer) q.getSingleResult();
        } catch (NoResultException nre) {
            return 0;
        }
    }

    public Integer getProximaMatricula() {
        return ultimaMatriculaUsada++;
    }


    @Lock(LockType.WRITE)
    public MatriculaFP gravarMatricula(MatriculaFP matricula) {
        if (!isExistsMatricula(matricula)) {
            matriculaFacade.salvarNovo(matricula);
        } else {
            matricula.setMatricula(buscarProximaMatricula());
            matriculaFacade.salvarNovo(matricula);
        }
        return matricula;
    }

    private String buscarProximaMatricula() {
        Integer matriculaDoBanco = recuperaUltimaMatriculaDoBanco();
        matriculaDoBanco++;
        return String.valueOf(matriculaDoBanco);
    }

    private Boolean isExistsMatricula(MatriculaFP matricula) {
        return matriculaFacade.isExistsMatriculaCadastrada(matricula.getMatricula());
    }
}
