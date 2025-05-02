package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Cacheable
@Audited
@Etiqueta("Configuração de Cálculo de I.P.T.U.")
public class ConfiguracaoEventoIPTU implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Integer codigo;
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configuracaoEventoIPTU")
    private List<EventoConfiguradoIPTU> eventos;
    private Long criadoEm;
    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;
    @ManyToOne
    private Exercicio exercicioInicial;
    @ManyToOne
    private Exercicio exercicioFinal;

    public ConfiguracaoEventoIPTU() {
        criadoEm = System.nanoTime();
        eventos = Lists.newArrayList();
        ativo = Boolean.TRUE;
    }

    public List<EventoConfiguradoIPTU> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoConfiguradoIPTU> eventos) {
        this.eventos = eventos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public enum TipoLancamento {
        TRIBUTO, MEMORIA;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
