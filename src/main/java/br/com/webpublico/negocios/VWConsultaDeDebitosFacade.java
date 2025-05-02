package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Agencia;
import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class VWConsultaDeDebitosFacade extends AbstractFacade<ParcelaValorDivida> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VWConsultaDeDebitosFacade() {
        super(ParcelaValorDivida.class);
    }


    public String getRetornaConteudoDaView() {
        String dataBase = getSistemaControlador().getUsuarioBancoConectado();
        String sql = " select TEXT " +
                "    FROM DBA_VIEWS " +
                "    where VIEW_NAME  = 'VWCONSULTADEDEBITOS'" +
                "      and owner = :dataBase";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataBase",  dataBase);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    public static SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

}
