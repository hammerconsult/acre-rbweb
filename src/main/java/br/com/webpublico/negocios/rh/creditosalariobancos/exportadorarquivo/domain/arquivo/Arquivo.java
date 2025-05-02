package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.arquivo;


import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.EncodingArquivo;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Daniel Franco
 * @since 16/05/2016 14:48
 */
public abstract class Arquivo<T extends br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.domain.registro.Registro> implements Serializable {

    private SortedMap<Integer, T> linhas = new TreeMap<>();
    private boolean removerCaracteresEspecais = false;

    public abstract String getTerminadorDeLinha();

    public abstract EncodingArquivo getEncoding();

    public abstract T novoRegistro();

    public SortedMap<Integer, T> getLinhas() {
        return new TreeMap<>(linhas);
    }

    public String getNomePadrao() {
        return this.getClass().getSimpleName();
    }

    public boolean getRemoverCaracteresEspecais() {
        return removerCaracteresEspecais;
    }

    public void addLinha(int numero, T linha) {
        if (numero <= 0) {
            throw new IllegalArgumentException("O número da linha deve ser maior que zero");
        }
        linhas.put(numero, linha);
    }

    public void addLinha(T linha) {
        int ultimaLinha = 0;
        for (Integer numero : linhas.keySet()) {
            if (numero > ultimaLinha) {
                ultimaLinha = numero;
            }
        }
        addLinha(ultimaLinha + 1, linha);
    }
}
