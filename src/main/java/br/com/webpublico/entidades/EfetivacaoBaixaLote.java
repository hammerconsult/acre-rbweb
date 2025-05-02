package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 01/09/15
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta("Patrimonial")
@Entity
@Audited
public class EfetivacaoBaixaLote extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @ManyToOne
    @Etiqueta("Efetivação de Baixa")
    private EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial;

    @OneToOne
    @Etiqueta("Lote do Leilão")
    private LeilaoAlienacaoLote leilaoAlienacaoLote;

    public EfetivacaoBaixaLote() {
        super();
    }

    public EfetivacaoBaixaLote(EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial, LeilaoAlienacaoLote leilaoAlienacaoLote) {
        super();
        this.efetivacaoBaixaPatrimonial = efetivacaoBaixaPatrimonial;
        this.leilaoAlienacaoLote = leilaoAlienacaoLote;
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

    public LeilaoAlienacaoLote getLeilaoAlienacaoLote() {
        return leilaoAlienacaoLote;
    }

    public void setLeilaoAlienacaoLote(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        this.leilaoAlienacaoLote = leilaoAlienacaoLote;
    }
}
