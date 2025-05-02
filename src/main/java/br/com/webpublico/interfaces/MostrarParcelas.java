/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.CBO;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.FolhaDePagamento;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.util.List;

/**
 * @author William
 */
public interface MostrarParcelas {
    public List<ResultadoParcela> getParcelas();
}
