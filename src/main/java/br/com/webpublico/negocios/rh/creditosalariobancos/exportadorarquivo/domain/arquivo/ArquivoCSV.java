package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.arquivo;


import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.registro.Registro;

/**
 * @author Daniel Franco
 * @since 18/05/2016 14:55
 */
public abstract class ArquivoCSV<T extends Registro> extends Arquivo<T> {

    public abstract String getSeparador();

    public String getSeparadorRegex() {
        String separador = getSeparador();
        if ("|".equals(separador)) {
            return "\\|";
        }
        return separador;
    }

    public abstract boolean incluiSeparadorNoFinal();
}
