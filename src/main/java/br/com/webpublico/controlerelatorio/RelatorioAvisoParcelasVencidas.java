/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;

import java.util.Date;

/**
 * @author doctor
 */
public class RelatorioAvisoParcelasVencidas extends AbstractReport {
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private Integer numParcInicial;
    private Integer numParcFinal;
    private Date dataInicialVenc;
    private Date dataFinalVenc;
    private Divida dividaInicial;
    private Divida dividaFinal;


}
