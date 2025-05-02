package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity

@Audited
@Table(name = "PROGRAMAALIMENTACAOLOCALES")
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Programa de Alimentação - Local de Estoque")
public class ProgramaAlimentacaoLocalEstoque  extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Programa de alimentação")
    private ProgramaAlimentacao programaAlimentacao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Local de Estoque")
    private LocalEstoque localEstoque;

    @Etiqueta("Quantidade de Alunos")
    private Long quantidadeAluno;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramaAlimentacao getProgramaAlimentacao() {
        return programaAlimentacao;
    }

    public void setProgramaAlimentacao(ProgramaAlimentacao programaAlimentacao) {
        this.programaAlimentacao = programaAlimentacao;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public Long getQuantidadeAluno() {
        return quantidadeAluno;
    }

    public void setQuantidadeAluno(Long quantidadeAluno) {
        this.quantidadeAluno = quantidadeAluno;
    }
}
