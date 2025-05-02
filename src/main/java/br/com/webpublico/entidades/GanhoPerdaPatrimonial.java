package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 14/11/14
 * Time: 09:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class GanhoPerdaPatrimonial extends EventoBem {

    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    private BigDecimal valor;
    @ManyToOne
    @Etiqueta("Efetivação da Baixa Patrimonial")
    private EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial;


    public GanhoPerdaPatrimonial() {
        super(TipoEventoBem.GANHOPERDAPATRIMONIAL, TipoOperacao.DEBITO);
    }

    public GanhoPerdaPatrimonial(Bem bem, EstadoBem estadoInicial, EstadoBem estadoResultante,
                                 SituacaoEventoBem situacaoEventoBem, Tipo tipo, BigDecimal valor) {
        super(TipoEventoBem.GANHOPERDAPATRIMONIAL, TipoOperacao.DEBITO);
        this.setBem(bem);
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoResultante);
        this.setSituacaoEventoBem(situacaoEventoBem);
        this.setTipo(tipo);
        this.setValor(valor);
    }

    public EfetivacaoBaixaPatrimonial getEfetivacaoBaixaPatrimonial() {
        return efetivacaoBaixaPatrimonial;
    }

    public void setEfetivacaoBaixaPatrimonial(EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial) {
        this.efetivacaoBaixaPatrimonial = efetivacaoBaixaPatrimonial;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        setValorDoLancamento(valor);
        this.valor = valor;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return valor;
    }

    public enum Tipo {
        GANHO, PERDA
    }

    public String getHistoricoRazaoPerda(String numeroEfetivacaoBaixa) {
        return "Registro de Perda da Efetivação da Baixa de Bens Móveis nº " + numeroEfetivacaoBaixa + ".";
    }

    public String getHistoricoRazaoGanho(String numeroEfetivacaoBaixa) {
        return "Registro de Ganho da Efetivação da Baixa de Bens Móveis nº " + numeroEfetivacaoBaixa + ".";
    }
}
