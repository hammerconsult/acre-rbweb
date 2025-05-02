package br.com.webpublico.entidades;

import br.com.webpublico.enums.PublicoAlvoPreferencial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Programa de Alimentação")
public class ProgramaAlimentacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Length(maximo = 255)
    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;

    @Length(maximo = 3000)
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @ManyToOne
    @Etiqueta("Convênio de Receita")
    private ConvenioReceita convenioReceita;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Público Alvo Preferencial")
    private PublicoAlvoPreferencial publicoAlvo;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Composição Nutricional")
    private ComposicaoNutricional composicaoNutricional;

    @OneToMany(mappedBy = "programaAlimentacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramaAlimentacaoLocalEstoque> locaisEstoque;

    public ProgramaAlimentacao() {
        locaisEstoque = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public PublicoAlvoPreferencial getPublicoAlvo() {
        return publicoAlvo;
    }

    public void setPublicoAlvo(PublicoAlvoPreferencial publicoAlvo) {
        this.publicoAlvo = publicoAlvo;
    }

    public List<ProgramaAlimentacaoLocalEstoque> getLocaisEstoque() {
        return locaisEstoque;
    }

    public void setLocaisEstoque(List<ProgramaAlimentacaoLocalEstoque> locaisEstoque) {
        this.locaisEstoque = locaisEstoque;
    }

    public ComposicaoNutricional getComposicaoNutricional() {
        return composicaoNutricional;
    }

    public void setComposicaoNutricional(ComposicaoNutricional composicaoNutricional) {
        this.composicaoNutricional = composicaoNutricional;
    }

    @Override
    public String toString() {
        return numero + " - " + nome + " - " + descricao;
    }
}
