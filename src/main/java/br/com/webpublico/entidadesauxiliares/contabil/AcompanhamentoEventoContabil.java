package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RelatorioPesquisaGenerico;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by romanini on 03/11/17.
 */
public class AcompanhamentoEventoContabil {


    private EventosReprocessar eventosReprocessar;
    private RelatorioPesquisaGenerico relatorioPesquisaGenerico;


    public AcompanhamentoEventoContabil() {

    }

    public AcompanhamentoEventoContabil(EventosReprocessar eventosReprocessar, RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        this.eventosReprocessar = eventosReprocessar;
        this.relatorioPesquisaGenerico = relatorioPesquisaGenerico;
    }

    public EventosReprocessar getEventosReprocessar() {
        return eventosReprocessar;
    }

    public void setEventosReprocessar(EventosReprocessar eventosReprocessar) {
        this.eventosReprocessar = eventosReprocessar;
    }

    public RelatorioPesquisaGenerico getRelatorioPesquisaGenerico() {
        return relatorioPesquisaGenerico;
    }

    public void setRelatorioPesquisaGenerico(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        this.relatorioPesquisaGenerico = relatorioPesquisaGenerico;
    }
}
