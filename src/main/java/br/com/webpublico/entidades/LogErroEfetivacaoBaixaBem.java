package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class LogErroEfetivacaoBaixaBem extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @ManyToOne
    @Etiqueta("Efetivação de Baixa")
    private EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial;

    @ManyToOne
    @Etiqueta("Bem")
    private Bem bem;

    @Etiqueta("Erro")
    private String erro;

    public LogErroEfetivacaoBaixaBem() {
    }

    public LogErroEfetivacaoBaixaBem(EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial, Bem bem, String erro) {
        this.efetivacaoBaixaPatrimonial = efetivacaoBaixaPatrimonial;
        this.bem = bem;
        this.erro = erro;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EfetivacaoBaixaPatrimonial getEfetivacaoBaixaPatrimonial() {
        return efetivacaoBaixaPatrimonial;
    }

    public void setEfetivacaoBaixaPatrimonial(EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial) {
        this.efetivacaoBaixaPatrimonial = efetivacaoBaixaPatrimonial;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }
}
