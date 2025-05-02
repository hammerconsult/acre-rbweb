package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exportador;



import java.io.File;

/**
 * @author Daniel Franco
 * @since 27/05/2016 14:04
 */
public abstract class AbstractExportador<T extends br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.arquivo.Arquivo> {

    public void exportar(T arquivo, File destino) {
        if (arquivo == null) {
            throw new IllegalArgumentException("O Arquivo não pode ser Null");
        }
        if (arquivo.getLinhas().isEmpty()) {
            throw new IllegalArgumentException("O Arquivo informado não possui nenhuma linha/item");
        }
        processarArquivo(arquivo, destino);
    }

    protected abstract void processarArquivo(T arquivo, File destino);
}
