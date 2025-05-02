package br.com.webpublico.entidades.contabil.reinf;

import br.com.webpublico.entidades.LiquidacaoDoctoFiscal;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.util.anotacoes.Monetario;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class NotaReinf extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LiquidacaoDoctoFiscal nota;
    @ManyToOne
    private RegistroEventoRetencaoReinf registro;
    @Monetario
    private BigDecimal valorRetido;
    private BigDecimal porcentagemMaxima;
    @Transient
    private Pagamento pagamento;

    public NotaReinf() {
    }

    public NotaReinf(LiquidacaoDoctoFiscal nota, RegistroEventoRetencaoReinf registro, BigDecimal porcentagemMaxima, Pagamento pagamento) {
        this.nota = nota;
        this.registro = registro;
        this.porcentagemMaxima = porcentagemMaxima;
        this.pagamento = pagamento;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LiquidacaoDoctoFiscal getNota() {
        return nota;
    }

    public void setNota(LiquidacaoDoctoFiscal nota) {
        this.nota = nota;
    }

    public RegistroEventoRetencaoReinf getRegistro() {
        return registro;
    }

    public void setRegistro(RegistroEventoRetencaoReinf registro) {
        this.registro = registro;
    }

    public BigDecimal getValorRetido() {
        return valorRetido;
    }

    public void setValorRetido(BigDecimal valorRetido) {
        this.valorRetido = valorRetido;
    }

    public BigDecimal getValorLiquido() {
        return nota.getValorLiquidado();
    }

    public BigDecimal getPorcentagemMaxima() {
        return porcentagemMaxima;
    }

    public void setPorcentagemMaxima(BigDecimal porcentagemMaxima) {
        this.porcentagemMaxima = porcentagemMaxima;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public BigDecimal getPorcentagemRetido() {
        if (TipoArquivoReinf.R4020.equals(registro.getTipoArquivo())) {
            if (nota.getValorBaseCalculoIrrf() != null && nota.getValorBaseCalculoIrrf().compareTo(BigDecimal.ZERO) > 0) {
                return valorRetido.multiply(BigDecimal.valueOf(100)).divide(nota.getValorBaseCalculoIrrf(), 2, RoundingMode.UP);
            }
        } else if (nota.getValorBaseCalculo() != null && nota.getValorBaseCalculo().compareTo(BigDecimal.ZERO) > 0) {
            return valorRetido.multiply(BigDecimal.valueOf(100)).divide(nota.getValorBaseCalculo(), 2, RoundingMode.UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorBaseCalculoPorTipoArquivo() {
      return TipoArquivoReinf.R4020.equals(registro.getTipoArquivo()) ? nota.getValorBaseCalculoIrrf() : nota.getValorBaseCalculo();
    }

    public Boolean isValido() {
        return this.getPorcentagemRetido() != null && this.getPorcentagemRetido().doubleValue() <= (porcentagemMaxima != null ? porcentagemMaxima.doubleValue() : 11.0);
    }
}
