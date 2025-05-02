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
@Etiqueta("Questão - Alternativa")
public class QuestaoAlternativa extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    private String descricao;
    @ManyToOne
    private QuestaoAvaliacao questaoAvaliacao;

    @ManyToOne
    private NivelResposta nivelResposta;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestaoAvaliacao getQuestaoAvaliacao() {
        return questaoAvaliacao;
    }

    public void setQuestaoAvaliacao(QuestaoAvaliacao questaoAvaliacao) {
        this.questaoAvaliacao = questaoAvaliacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public NivelResposta getNivelResposta() {
        return nivelResposta;
    }

    public void setNivelResposta(NivelResposta nivelResposta) {
        this.nivelResposta = nivelResposta;
    }

    @Override
    public String toString() {
        return nivelResposta != null ? (nivelResposta.getNome() + " - de " + nivelResposta.getPercentualInicio() + " a " + nivelResposta.getPercentualFim()) : " ";
    }
}
