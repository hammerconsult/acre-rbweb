package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mga on 04/10/2017.
 */
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Empenho Obrigação a Pagar")
@Audited
@Entity
public class EmpenhoObrigacaoPagar extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Empenho")
    private Empenho empenho;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Obrigação a Pagar")
    private ObrigacaoAPagar obrigacaoAPagar;

    public EmpenhoObrigacaoPagar() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public ObrigacaoAPagar getObrigacaoAPagar() {
        return obrigacaoAPagar;
    }

    public void setObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar) {
        this.obrigacaoAPagar = obrigacaoAPagar;
    }
}
