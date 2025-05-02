package br.com.webpublico.entidades.rh.avaliacao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@GrupoDiagrama(nome = "RecursosHumanos - Avaliações")
@Audited
@Etiqueta("Questão da Avaliação")
public class QuestaoAvaliacao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private MontagemAvaliacao montagemAvaliacao;
    private Integer ordem;

    private String questao;

    @OneToMany(mappedBy = "questaoAvaliacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestaoAlternativa> alternativas;
    public QuestaoAvaliacao() {
        alternativas = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MontagemAvaliacao getMontagemAvaliacao() {
        return montagemAvaliacao;
    }

    public void setMontagemAvaliacao(MontagemAvaliacao montagemAvaliacao) {
        this.montagemAvaliacao = montagemAvaliacao;
    }

    public String getQuestao() {
        return questao;
    }

    public void setQuestao(String questao) {
        this.questao = questao;
    }

    public List<QuestaoAlternativa> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<QuestaoAlternativa> alternativas) {
        this.alternativas = alternativas;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

}
