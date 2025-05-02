/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.ResultadoCalculoRetroativoFP;

import java.util.List;

/**
 * @author peixe
 */
public interface CalculoRetroativoFacade {

    ResultadoCalculoRetroativoFP verifica(VinculoFP vinculoFP);

    List<EventoFP> recuperaEventosLancadosRetroativos(VinculoFP vinculoFP);
}
