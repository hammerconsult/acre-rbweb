package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoArquivoTCE;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 17/04/14
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class PartidaArquivoTCE implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private TipoArquivoTCE tipoArquivoTCE;
    public String contaContabil, conteudoContaCorrente, tipoContaCorrente, natureza, indicadorSuperavitFinanceiro, numeroLancamento, tipoLancamento, valor;

    public PartidaArquivoTCE() {
    }

    public PartidaArquivoTCE(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getIndicadorSuperavitFinanceiro() {
        return indicadorSuperavitFinanceiro;
    }

    public void setIndicadorSuperavitFinanceiro(String indicadorSuperavitFinanceiro) {
        this.indicadorSuperavitFinanceiro = indicadorSuperavitFinanceiro;
    }

    public String getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(String contaContabil) {
        this.contaContabil = contaContabil;
    }

    public String getConteudoContaCorrente() {
        return conteudoContaCorrente;
    }

    public void setConteudoContaCorrente(String conteudoContaCorrente) {
        this.conteudoContaCorrente = conteudoContaCorrente;
    }

    public String getTipoContaCorrente() {
        return tipoContaCorrente;
    }

    public void setTipoContaCorrente(String tipoContaCorrente) {
        this.tipoContaCorrente = tipoContaCorrente;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getNumeroLancamento() {
        return numeroLancamento;
    }

    public void setNumeroLancamento(String numeroLancamento) {
        this.numeroLancamento = numeroLancamento;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoArquivoTCE getTipoArquivoTCE() {
        return tipoArquivoTCE;
    }

    public void setTipoArquivoTCE(TipoArquivoTCE tipoArquivoTCE) {
        this.tipoArquivoTCE = tipoArquivoTCE;
    }
}
