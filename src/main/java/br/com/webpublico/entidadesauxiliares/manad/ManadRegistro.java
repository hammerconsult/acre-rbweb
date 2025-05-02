package br.com.webpublico.entidadesauxiliares.manad;

import br.com.webpublico.interfaces.IManadRegistro;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 09/06/14
 * Time: 19:18
 * To change this template use File | Settings | File Templates.
 */
public class ManadRegistro {


    private ManadRegistroTipo tipoRegistro;
    private ManadModulo modulo;
    private Manad manad;
    private Object objeto;

    public ManadRegistro(Object objeto, ManadRegistroTipo tipoRegistro, ManadModulo modulo, Manad manad) {
        this.tipoRegistro = tipoRegistro;
        this.modulo = modulo;
        this.objeto = objeto;
        this.manad = manad;
    }

    public ManadRegistro(Object objeto, Manad manad) {
        this.objeto = objeto;
        this.modulo = ((IManadRegistro) objeto).getModulo();
        this.tipoRegistro = ((IManadRegistro) objeto).getTipoRegistro();
        this.manad = manad;
    }

    public ManadRegistroTipo getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(ManadRegistroTipo tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public ManadModulo getModulo() {
        return modulo;
    }

    public void setModulo(ManadModulo modulo) {
        this.modulo = modulo;
    }

    public Object getObjeto() {
        return objeto;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;
    }

    public Manad getManad() {
        return manad;
    }

    public void setManad(Manad manad) {
        this.manad = manad;
    }

    public enum ManadRegistroTipo {
        G0,
        G1,
        G50,
        G100,
        G990,
        I001,
        I990,
        K001,
        K050,
        K100,
        K150,
        K250,
        K300,
        L050,
        L100,
        L150,
        L200,
        L250,
        L300,
        L350,
        L400,
        L450,
        L500,
        L550,
        L600,
        L650,
        L700,
        L750,
        L990,
        G9001,
        G9900,
        G9990,
        G9999;

    }

    public enum ManadModulo {
        GERAL,
        RH,
        CONTABIL;
    }

    public enum ManadTipoCampo {
        N,
        C;
    }
}
