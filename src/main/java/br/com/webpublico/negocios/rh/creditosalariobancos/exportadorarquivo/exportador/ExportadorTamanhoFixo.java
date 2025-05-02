package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exportador;


import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.registro.Registro;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception.WPArquivoException;
import br.com.webpublico.util.StringUtil;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.util.SortedMap;


public class ExportadorTamanhoFixo<T extends br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exportador.ArquivoTamanhoFixo> extends AbstractExportador<T> {

    @Override
    protected void processarArquivo(T arquivo, File destino) {
        try (BufferedWriter writer = Files.newWriter(destino, arquivo.getEncoding().getCharset())) {
            SortedMap<Integer, br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.registro.Registro> mapa = arquivo.getLinhas();
            for (Integer numeroLinha : mapa.keySet()) {
                writer.write(montaLinhaDoRegistro(mapa.get(numeroLinha), arquivo.getSeparador(), arquivo.incluiSeparadorNoFinal(), arquivo.getTerminadorDeLinha(), arquivo.getRemoverCaracteresEspecais()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WPArquivoException("Erro Exportando objeto Arquivo [" + arquivo + "] para o disco em [" + destino + "]", ex);
        }
    }

    private String montaLinhaDoRegistro(Registro registro, String separador, boolean incluiSeparadorNoFinal, String terminadorDeLinha, boolean removerCaracteresEspecais) {
        StringBuilder sb = new StringBuilder();
        int ultimoCampo = registro.getMaiorPosicao();
        for (int i = 1; i <= ultimoCampo; i++) {
            String valor = registro.getAsString(i);
            sb.append(removerCaracteresEspecais ? StringUtil.converterAcentosERemoverCarateresEspecais(valor) : valor);
            if (i < ultimoCampo || incluiSeparadorNoFinal) {
                sb.append(separador);
            }
        }
        sb.append(terminadorDeLinha);
        return sb.toString();
    }
}
