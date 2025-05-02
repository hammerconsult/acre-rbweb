package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.ComponentePesquisaGenerico;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class PesquisaGenericaRegistroEsocial extends ComponentePesquisaGenerico implements Serializable {

    private String tipoClasseESocial;

    @Override
    public String getComplementoQuery() {
        return " where obj.tipoClasseESocial = '" + tipoClasseESocial + "'";
    }

    public void tipoClasseEsocialS1300() {
        tipoClasseESocial = TipoClasseESocial.S1300.name();
    }

    public void tipoClasseEsocialS1200() {
        tipoClasseESocial = TipoClasseESocial.S1200.name();
    }

    public void tipoClasseEsocialS1207() {
        tipoClasseESocial = TipoClasseESocial.S1207.name();
    }

    public void tipoClasseEsocialS1202() {
        tipoClasseESocial = TipoClasseESocial.S1202.name();
    }

    public void tipoClasseEsocialS1298() {
        tipoClasseESocial = TipoClasseESocial.S1298.name();
    }

    public void tipoClasseEsocialS1210() {
        tipoClasseESocial = TipoClasseESocial.S1210.name();
    }
}




