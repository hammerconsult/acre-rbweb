package br.com.webpublico.entidades.contabil.execucao;

import br.com.webpublico.entidades.Desdobramento;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by renato on 26/09/18.
 */
@Entity
@Audited
@Etiqueta("Desdobramento Pagamento")
@GrupoDiagrama(nome = "Contabil")
public class DesdobramentoPagamento extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Pagamento")
    @Tabelavel
    private Pagamento pagamento;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Desdobramento")
    @Tabelavel
    private Desdobramento desdobramento;

    @Monetario
    @Etiqueta("Valor")
    @Tabelavel
    private BigDecimal valor;

    @Monetario
    @Etiqueta("Saldo")
    @Tabelavel
    private BigDecimal saldo;


    public DesdobramentoPagamento() {
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public Desdobramento getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(Desdobramento desdobramento) {
        this.desdobramento = desdobramento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String toStringDesdobramento() {
        try {
            return desdobramento.toStringDesdobramento();
        } catch (Exception ex) {
            return "";
        }
    }
}
