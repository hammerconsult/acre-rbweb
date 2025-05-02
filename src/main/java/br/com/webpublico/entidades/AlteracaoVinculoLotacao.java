package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author octavio
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class AlteracaoVinculoLotacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Alterar Local de Trabalho Pro Lotação")
    @ManyToOne
    private AlteracaoLocalTrabalhoPorLote alterLocTrabalhoLotacao;
    @Etiqueta("VinculoFP")
    @ManyToOne
    private VinculoFP vinculoFP;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlteracaoLocalTrabalhoPorLote getAlterLocTrabalhoLotacao() {
        return alterLocTrabalhoLotacao;
    }

    public void setAlterLocTrabalhoLotacao(AlteracaoLocalTrabalhoPorLote alterLocTrabalhoLotacao) {
        this.alterLocTrabalhoLotacao = alterLocTrabalhoLotacao;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }
}
