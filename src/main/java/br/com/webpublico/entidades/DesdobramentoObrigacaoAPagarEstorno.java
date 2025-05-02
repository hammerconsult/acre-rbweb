package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by mga on 26/07/2017.
 */
@Entity
@Audited
@Etiqueta("Desdobramento de Estorno de Obrigação a Pagar")
@GrupoDiagrama(nome = "Contabil")
@Table(name = "DESDOBRAMENTOOBRIGACAOEST")
public class DesdobramentoObrigacaoAPagarEstorno extends SuperEntidade implements EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Estorno de Obrigação a Pagar")
    private ObrigacaoAPagarEstorno obrigacaoAPagarEstorno;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Conta de Despesa")
    private Conta conta;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;

    @Monetario
    @Etiqueta("Valor")
    @Obrigatorio
    @Tabelavel
    @Positivo(permiteZero = false)
    private BigDecimal valor;

    public DesdobramentoObrigacaoAPagarEstorno() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObrigacaoAPagarEstorno getObrigacaoAPagarEstorno() {
        return obrigacaoAPagarEstorno;
    }

    public void setObrigacaoAPagarEstorno(ObrigacaoAPagarEstorno obrigacaoAPagarEstorno) {
        this.obrigacaoAPagarEstorno = obrigacaoAPagarEstorno;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        try {
            return this.obrigacaoAPagarEstorno + ", " + conta + Util.formataValor(valor);
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return ((EntidadeContabil) obrigacaoAPagarEstorno).getReferenciaArquivoPrestacaoDeContas();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }
}


