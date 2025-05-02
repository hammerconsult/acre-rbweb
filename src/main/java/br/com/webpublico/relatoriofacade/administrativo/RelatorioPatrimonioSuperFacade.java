package br.com.webpublico.relatoriofacade.administrativo;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zaca on 30/03/17.
 */
@Stateless
public class RelatorioPatrimonioSuperFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public void adicionarParameters(List<ParametrosRelatorios> parametros, Query q) {
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
    }

    public String concatenarParameters(List<ParametrosRelatorios> parametros, Integer local, String clausula) {
        StringBuilder sql = new StringBuilder();
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == local) {
                sql.append(clausula + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        return sql.toString();
    }

}
