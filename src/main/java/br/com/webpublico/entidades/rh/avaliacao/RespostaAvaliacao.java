package br.com.webpublico.entidades.rh.avaliacao;


import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@GrupoDiagrama(nome = "RecursosHumanos - Avaliações")
@Audited
@Etiqueta("Execução da Avaliação")
public class RespostaAvaliacao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private AvaliacaoVinculoFP avaliacaoVinculoFP;

    @ManyToOne
    private QuestaoAvaliacao questaoAvaliacao;

    @ManyToOne
    private NivelResposta nivelResposta;

    public RespostaAvaliacao() {

    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NivelResposta getNivelResposta() {
        return nivelResposta;
    }

    public void setNivelResposta(NivelResposta nivelResposta) {
        this.nivelResposta = nivelResposta;
    }

    public AvaliacaoVinculoFP getAvaliacaoVinculoFP() {
        return avaliacaoVinculoFP;
    }

    public void setAvaliacaoVinculoFP(AvaliacaoVinculoFP avaliacaoVinculoFP) {
        this.avaliacaoVinculoFP = avaliacaoVinculoFP;
    }

    public QuestaoAvaliacao getQuestaoAvaliacao() {
        return questaoAvaliacao;
    }

    public void setQuestaoAvaliacao(QuestaoAvaliacao questaoAvaliacao) {
        this.questaoAvaliacao = questaoAvaliacao;
    }
}
