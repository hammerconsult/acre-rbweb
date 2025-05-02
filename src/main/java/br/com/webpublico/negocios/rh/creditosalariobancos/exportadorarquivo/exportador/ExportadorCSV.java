/*
package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exportador;

import br.com.webpublico.domain.arquivo.ArquivoCSV;
import br.com.webpublico.domain.registro.Registro;
import br.com.webpublico.exception.WPArquivoException;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.util.SortedMap;

*/
/**
 * @author Daniel Franco
 * @since 27/05/2016 16:53
 *//*

public class ExportadorCSV<T extends ArquivoCSV> extends AbstractExportador<T> {

    @Override
    protected void processarArquivo(T arquivo, File destino) {
        try (BufferedWriter writer = Files.newWriter(destino, arquivo.getEncoding().getCharset())) {
            SortedMap<Integer, Registro> mapa = arquivo.getLinhas();
            for (Integer numeroLinha : mapa.keySet()) {
                writer.write(montaLinhaDoRegistro(mapa.get(numeroLinha), arquivo.getSeparador(), arquivo.incluiSeparadorNoFinal(), arquivo.getTerminadorDeLinha()));
            }
        } catch (Exception ex) {
            throw new WPArquivoException("Erro Exportando objeto Arquivo [" + arquivo + "] para o disco em [" + destino + "]", ex);
        }
    }

    private String montaLinhaDoRegistro(Registro registro, String separador, boolean incluiSeparadorNoFinal, String terminadorDeLinha) {
        StringBuilder sb = new StringBuilder();
        int ultimoCampo = registro.getMaiorPosicao();
        for (int i = 1; i <= ultimoCampo; i++) {
            String valor = registro.getAsString(i);
            sb.append(valor);
            if (i < ultimoCampo || incluiSeparadorNoFinal) {
                sb.append(separador);
            }
        }
        sb.append(terminadorDeLinha);
        return sb.toString();
    }
}
*/
