package br.com.webpublico.entidades.contabil.execucao;

import br.com.webpublico.entidades.Desdobramento;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.PagamentoEstorno;
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
@Etiqueta("Desdobramento Pagamento Estorno")
@GrupoDiagrama(nome = "Contabil")
@Table(name = "DESDOBRAMENTOPAGAMESTORNO")
public class DesdobramentoPagamentoEstorno extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Pagamento estorno")
    @Tabelavel
    private PagamentoEstorno pagamentoEstorno;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Desdobramento do pagamento")
    @Tabelavel
    private DesdobramentoPagamento desdobramentoPagamento;

    @Monetario
    @Etiqueta("Valor")
    @Tabelavel
    private BigDecimal valor;


    public DesdobramentoPagamentoEstorno() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PagamentoEstorno getPagamentoEstorno() {
        return pagamentoEstorno;
    }

    public void setPagamentoEstorno(PagamentoEstorno pagamentoEstorno) {
        this.pagamentoEstorno = pagamentoEstorno;
    }

    public DesdobramentoPagamento getDesdobramentoPagamento() {
        return desdobramentoPagamento;
    }

    public void setDesdobramentoPagamento(DesdobramentoPagamento desdobramentoPagamento) {
        this.desdobramentoPagamento = desdobramentoPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
