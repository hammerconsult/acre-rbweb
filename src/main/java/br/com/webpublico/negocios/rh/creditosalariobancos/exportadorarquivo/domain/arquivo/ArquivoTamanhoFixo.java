package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exportador;



/**
 * @author Daniel Franco
 * @since 18/05/2016 14:55
 */
public abstract class ArquivoTamanhoFixo<T extends br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.registro.Registro> extends br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.arquivo.Arquivo<T> {

    public abstract String getSeparador();

    public String getSeparadorRegex() {
        return getSeparador();
    }

    @Override
    public boolean getRemoverCaracteresEspecais() {
        return true;
    }

    public abstract boolean incluiSeparadorNoFinal();
}
