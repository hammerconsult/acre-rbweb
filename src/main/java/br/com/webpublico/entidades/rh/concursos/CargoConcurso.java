package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by venom on 10/11/14.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Concursos")
public class CargoConcurso extends SuperEntidade implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Concurso concurso;

    @Etiqueta(value = "Cargo")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private Cargo cargo;

    @Etiqueta(value = "Vagas Disponíveis")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer vagasDisponiveis;

    @Etiqueta(value = "Vagas Especiais")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer vagasEspeciais;

    @Etiqueta(value = "Vagas Totais")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer vagasTotais;

    @Etiqueta(value = "Taxa de Inscrição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private BigDecimal taxaInscricao;

    @Etiqueta(value = "Classificação")
    @Pesquisavel
    @OneToOne(mappedBy = "cargo")
    private ClassificacaoConcurso classificacaoConcurso;

    public CargoConcurso() {
        super();
    }

    public CargoConcurso(Concurso concurso) {
        setConcurso(concurso);
    }

    public ClassificacaoConcurso getClassificacaoConcurso() {
        return classificacaoConcurso;
    }

    public void setClassificacaoConcurso(ClassificacaoConcurso classificacaoConcurso) {
        this.classificacaoConcurso = classificacaoConcurso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Integer getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(Integer vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public Integer getVagasEspeciais() {
        return vagasEspeciais;
    }

    public void setVagasEspeciais(Integer vagasEspeciais) {
        this.vagasEspeciais = vagasEspeciais;
    }

    public Integer getVagasTotais() {
        return vagasTotais;
    }

    public void setVagasTotais(Integer vagasTotais) {
        this.vagasTotais = vagasTotais;
    }

    public BigDecimal getTaxaInscricao() {
        return taxaInscricao;
    }

    public void setTaxaInscricao(BigDecimal taxaInscricao) {
        this.taxaInscricao = taxaInscricao;
    }

    @Override
    public String toString() {
        if (this.getCargo() == null) {
            return "";
        }
        return this.getCargo().toString();
    }

    @Override
    public int compareTo(Object o) {
        return this.getId().compareTo(((CargoConcurso) o).getId());
    }

    public boolean temClassificacao() {
        return this.getClassificacaoConcurso() != null;
    }
}
