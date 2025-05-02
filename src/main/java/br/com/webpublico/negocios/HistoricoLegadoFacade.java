/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HistoricoLegadoBCI;
import br.com.webpublico.util.ExecutaJDBC;
import br.com.webpublico.util.RegistroDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel
 */
@Stateless
public class HistoricoLegadoFacade extends AbstractFacade<HistoricoLegadoBCI> {

    private static final Logger logger = LoggerFactory.getLogger(HistoricoLegadoFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistoricoLegadoFacade() {
        super(HistoricoLegadoBCI.class);
    }

    public List<RegistroDB> listaHistoricoCMC(String inscricaoCadastral) {
        String pesquisa = inscricaoCadastral;

        StringBuilder sb = new StringBuilder("select * from HistoricoLegadoBCE ");
        sb.append(" where insAltCon = :insAltCon ");
        sb.append(" order by DtaAltCon desc, horAltCon desc");

        if (pesquisa == null || pesquisa.isEmpty()) {
            return new ArrayList<>();
        }
        Connection con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
        try {
            List<RegistroDB> resultado = ExecutaJDBC.consulta(con, sb.toString(), Long.parseLong(pesquisa));
            return resultado == null || resultado.isEmpty() ? new ArrayList<RegistroDB>() : resultado;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        } finally {
            AbstractReport.fecharConnection(con);
        }
    }



}
