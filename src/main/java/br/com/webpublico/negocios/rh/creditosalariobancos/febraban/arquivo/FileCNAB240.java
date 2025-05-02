package br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo;



public class FileCNAB240 extends ArquivoCNAB240<CNAB240> {


    public CNAB240 novoRegistro() {
        return new CNAB240();
    }

    @Override
    public boolean getRemoverCaracteresEspecais() {
        return true;
    }
}
