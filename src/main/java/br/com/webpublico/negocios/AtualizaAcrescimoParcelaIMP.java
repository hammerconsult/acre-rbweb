/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParcelaValorDivida;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

/**
 * @author Gustavo
 */
public class AtualizaAcrescimoParcelaIMP {

    private static final Logger logger = LoggerFactory.getLogger(AtualizaAcrescimoParcelaIMP.class);

    private Date dataReferencia;
    private ConsultaDebitoFacade consultaDebito;

    private AtualizaAcrescimoParcelaIMP() {
    }

    public AtualizaAcrescimoParcelaIMP(Date dataReferencia, ConsultaDebitoFacade consultaDebito) {
        this.dataReferencia = dataReferencia;
        this.consultaDebito = consultaDebito;
    }

    public void atualizaParcelaJDBC(List<ParcelaValorDivida> listaDeParcelas, Connection conn) {
        try {
            conn.setAutoCommit(false);
            Statement stm = conn.createStatement();
            stm.executeUpdate("delete from temp_acrescimos");
            for (ParcelaValorDivida parcela : listaDeParcelas) {
                stm.executeUpdate("INSERT INTO temp_acrescimos(parcela_id, juros, multa) VALUES (" + parcela.getId() + "," + parcela.getValorJuros() + "," + parcela.getValorMulta() + ")");
            }
        } catch (SQLException ex) {
            logger.error("Erro:", ex);
        }
    }
}
