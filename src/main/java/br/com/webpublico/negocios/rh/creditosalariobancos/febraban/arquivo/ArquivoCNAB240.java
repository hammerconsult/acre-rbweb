package br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo;


import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.EncodingArquivo;


public abstract class ArquivoCNAB240<T extends br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.registro.Registro> extends br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exportador.ArquivoTamanhoFixo<T> {

    @Override
    public String getSeparador() {
        return "";
    }

    @Override
    public boolean incluiSeparadorNoFinal() {
        return true;
    }

    @Override
    public String getTerminadorDeLinha() {
        return "\n";
    }

    @Override
    public EncodingArquivo getEncoding() {
        return EncodingArquivo.WINDOWS_1252;
    }

    @Override
    public boolean getRemoverCaracteresEspecais() {
        return true;
    }
}
