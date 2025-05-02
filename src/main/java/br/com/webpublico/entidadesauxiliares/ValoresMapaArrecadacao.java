package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.enums.TipoTributoDTO;

import java.io.Serializable;
import java.math.BigDecimal;

public class ValoresMapaArrecadacao implements Serializable {
    private BigDecimal arrecadacao;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal honorarios;

    public ValoresMapaArrecadacao() {
        this.arrecadacao = BigDecimal.ZERO;
        this.juros = BigDecimal.ZERO;
        this.multa = BigDecimal.ZERO;
        this.correcao = BigDecimal.ZERO;
        this.honorarios = BigDecimal.ZERO;
    }

    public void adicionarValor(BigDecimal valor, TipoTributoDTO tipoTributo) {
        if (tipoTributo.isImpostoTaxa()) {
            arrecadacao = arrecadacao.add(valor);
        } else if (TipoTributoDTO.JUROS.equals(tipoTributo)) {
            juros = juros.add(valor);
        } else if (TipoTributoDTO.MULTA.equals(tipoTributo)) {
            multa = multa.add(valor);
        } else if (TipoTributoDTO.CORRECAO.equals(tipoTributo)) {
            correcao = correcao.add(valor);
        } else if (TipoTributoDTO.HONORARIOS.equals(tipoTributo)) {
            honorarios = honorarios.add(valor);
        }
    }

    public void subtrairValor(BigDecimal valor, TipoTributoDTO tipoTributo) {
        if (tipoTributo.isImpostoTaxa()) {
            arrecadacao = valor.subtract(arrecadacao);
        } else if (TipoTributoDTO.JUROS.equals(tipoTributo)) {
            juros = valor.subtract(juros);
        } else if (TipoTributoDTO.MULTA.equals(tipoTributo)) {
            multa = valor.subtract(multa);
        } else if (TipoTributoDTO.CORRECAO.equals(tipoTributo)) {
            correcao = valor.subtract(correcao);
        } else if (TipoTributoDTO.HONORARIOS.equals(tipoTributo)) {
            honorarios = valor.subtract(honorarios);
        }
    }

    public boolean containsValor() {
        return arrecadacao.compareTo(BigDecimal.ZERO) != 0 || juros.compareTo(BigDecimal.ZERO) != 0 ||
            multa.compareTo(BigDecimal.ZERO) != 0 || correcao.compareTo(BigDecimal.ZERO) != 0 ||
            honorarios.compareTo(BigDecimal.ZERO) != 0;
    }

    public BigDecimal getArrecadacao() {
        return arrecadacao;
    }

    public void setArrecadacao(BigDecimal arrecadacao) {
        this.arrecadacao = arrecadacao;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }
}
