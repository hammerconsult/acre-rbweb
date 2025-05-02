package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mga on 13/07/2017.
 */
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Liquidação Obrigação a Pagar")
@Audited
@Entity
public class LiquidacaoObrigacaoPagar extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Liquidação")
    private Liquidacao liquidacao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Obrigação a Pagar")
    private ObrigacaoAPagar obrigacaoAPagar;

    public LiquidacaoObrigacaoPagar() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public ObrigacaoAPagar getObrigacaoAPagar() {
        return obrigacaoAPagar;
    }

    public void setObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar) {
        this.obrigacaoAPagar = obrigacaoAPagar;
    }
}
