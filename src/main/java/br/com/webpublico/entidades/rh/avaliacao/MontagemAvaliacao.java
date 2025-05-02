package br.com.webpublico.entidades.rh.avaliacao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@GrupoDiagrama(nome = "RecursosHumanos - Avaliações")
@Audited
@Etiqueta("Montagem da Avaliação")
public class MontagemAvaliacao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String nome;
    @OneToMany(mappedBy = "montagemAvaliacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestaoAvaliacao> questoes;

    public MontagemAvaliacao() {
        this.questoes = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<QuestaoAvaliacao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<QuestaoAvaliacao> questoes) {
        this.questoes = questoes;
    }

    @Override
    public String toString() {
        return nome;
    }
}
