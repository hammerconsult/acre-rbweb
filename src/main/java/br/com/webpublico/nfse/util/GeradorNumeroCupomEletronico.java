package br.com.webpublico.nfse.util;

import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

public class GeradorNumeroCupomEletronico {

    private Random gerador;
    private List<Long> numerosGerados;

    public GeradorNumeroCupomEletronico() {
        gerador = new Random();
        numerosGerados = Lists.newArrayList();
    }

    public void clear() {
        numerosGerados.clear();
    }

    public String gerarNumero() {
        Long numero = new Long(gerador.nextInt(999999));
        while (numerosGerados.contains(numero)) {
            numero = new Long(gerador.nextInt(999999));
        }
        numerosGerados.add(numero);
        return Util.zerosAEsquerda(numero.toString(), 6);
    }
}
