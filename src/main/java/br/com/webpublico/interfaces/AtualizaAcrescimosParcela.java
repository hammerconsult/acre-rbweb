package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.ConfiguracaoAcrescimos;
import br.com.webpublico.entidades.ParcelaValorDivida;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.util.List;

/**
 * @author Gustavo
 */
public interface AtualizaAcrescimosParcela {

    void atualizaParcelaJPA(ParcelaValorDivida Parcela, EntityManager em, ConfiguracaoAcrescimos configuracao);

    void atualizaParcelaJPA(List<ParcelaValorDivida> listaDeParcelas, EntityManager em, ConfiguracaoAcrescimos configuracao);

    void atualizaParcelaJDBC(List<ParcelaValorDivida> listaDeParcelas, Connection conn);
}
