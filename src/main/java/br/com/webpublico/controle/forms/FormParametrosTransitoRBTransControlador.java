package br.com.webpublico.controle.forms;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoParametroRBTrans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FormParametrosTransitoRBTransControlador implements Serializable {
    private ParametrosTransitoRBTrans parametrosTransitoSelecionado;
    private ParametrosFiscalizacaoRBTrans parametrosFiscalizacaoRBTransSelecionado;
    private List<ParametrosTransitoTransporte> historicoParametrosTaxi;
    private List<Object[]> listaHistoricoGeral;
    private TipoParametroRBTrans tipoParametroRBTrans;
    private TipoDoctoOficial tipoDoctoOficial;

    public FormParametrosTransitoRBTransControlador() {
        parametrosTransitoSelecionado = new ParametrosTransitoRBTrans();
        parametrosFiscalizacaoRBTransSelecionado = new ParametrosFiscalizacaoRBTrans();
        tipoParametroRBTrans = null;
        tipoDoctoOficial = null;
        historicoParametrosTaxi = new ArrayList<>();
        listaHistoricoGeral = new ArrayList<>();
    }

    public ParametrosTransitoRBTrans getParametrosTransitoSelecionado() {
        return parametrosTransitoSelecionado;
    }

    public void setParametrosTransitoSelecionado(ParametrosTransitoRBTrans parametrosTransitoSelecionado) {
        this.parametrosTransitoSelecionado = parametrosTransitoSelecionado;
    }

    public ParametrosFiscalizacaoRBTrans getParametrosFiscalizacaoRBTransSelecionado() {
        return parametrosFiscalizacaoRBTransSelecionado;
    }

    public void setParametrosFiscalizacaoRBTransSelecionado(ParametrosFiscalizacaoRBTrans parametrosFiscalizacaoRBTransSelecionado) {
        this.parametrosFiscalizacaoRBTransSelecionado = parametrosFiscalizacaoRBTransSelecionado;
    }

    public List<ParametrosTransitoTransporte> getHistoricoParametrosTaxi() {
        return historicoParametrosTaxi;
    }

    public void setHistoricoParametrosTaxi(List<ParametrosTransitoTransporte> historicoParametrosTaxi) {
        this.historicoParametrosTaxi = historicoParametrosTaxi;
    }

    public List<Object[]> getListaHistoricoGeral() {
        return listaHistoricoGeral;
    }

    public void setListaHistoricoGeral(List<Object[]> listaHistoricoGeral) {
        this.listaHistoricoGeral = listaHistoricoGeral;
    }

    public TipoParametroRBTrans getTipoParametroRBTrans() {
        return tipoParametroRBTrans;
    }

    public void setTipoParametroRBTrans(TipoParametroRBTrans tipoParametroRBTrans) {
        this.tipoParametroRBTrans = tipoParametroRBTrans;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }
}
