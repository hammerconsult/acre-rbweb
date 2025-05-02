package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AtoLegalORC;
import br.com.webpublico.util.DataUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class AtoLegalValorOperacaoCreditoESuplementacaoVO {
    private BigDecimal totalCredito;
    private BigDecimal totalDeducao;
    private BigDecimal resto;
    private Boolean validaTotais;

    public AtoLegalValorOperacaoCreditoESuplementacaoVO() {
        totalCredito = BigDecimal.ZERO;
        totalDeducao = BigDecimal.ZERO;
        resto = BigDecimal.ZERO;
    }

    public AtoLegalValorOperacaoCreditoESuplementacaoVO(BigDecimal totalCredito, BigDecimal totalDeducao, BigDecimal resto) {
        this.totalCredito = totalCredito;
        this.totalDeducao = totalDeducao;
        this.resto = resto;
    }

    public BigDecimal getTotalCredito() {
        return totalCredito;
    }

    public void setTotalCredito(BigDecimal totalCredito) {
        this.totalCredito = totalCredito;
    }

    public BigDecimal getTotalDeducao() {
        return totalDeducao;
    }

    public void setTotalDeducao(BigDecimal totalDeducao) {
        this.totalDeducao = totalDeducao;
    }

    public BigDecimal getResto() {
        return resto;
    }

    public void setResto(BigDecimal resto) {
        this.resto = resto;
    }

    public boolean isValidaTotais() {
        return validaTotais == null ? Boolean.FALSE : validaTotais;
    }

    public void setValidaTotais(boolean validaTotais) {
        this.validaTotais = validaTotais;
    }

    public void calculaLancamento(AtoLegalORC atoLegalORC) {
        totalCredito = BigDecimal.ZERO;
        totalDeducao = BigDecimal.ZERO;
        resto = BigDecimal.ZERO;
        totalCredito = totalCredito.add(atoLegalORC.getCreditoEspecial());
        totalCredito = totalCredito.add(atoLegalORC.getCreditoExtraordinario());
        totalCredito = totalCredito.add(atoLegalORC.getCreditoSuplementar());

        totalDeducao = totalDeducao.add(atoLegalORC.getAnulacao());
        totalDeducao = totalDeducao.add(atoLegalORC.getExcessoArecadacao());
        totalDeducao = totalDeducao.add(atoLegalORC.getOperacaoDeCredito());
        totalDeducao = totalDeducao.add(atoLegalORC.getSuperAvit());

        resto = totalCredito.subtract(totalDeducao);
        validaTotais = resto.compareTo(BigDecimal.ZERO) == 0;
    }
}
