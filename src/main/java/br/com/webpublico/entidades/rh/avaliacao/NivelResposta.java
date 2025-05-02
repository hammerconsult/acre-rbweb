package br.com.webpublico.entidades.rh.avaliacao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@GrupoDiagrama(nome = "RecursosHumanos - Avaliações")
@Audited
@Etiqueta("Nivel da Resposta")
public class NivelResposta extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String nome;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Integer percentualInicio;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Integer percentualFim;

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

    public Integer getPercentualInicio() {
        return percentualInicio;
    }

    public void setPercentualInicio(Integer percentualInicio) {
        this.percentualInicio = percentualInicio;
    }

    public Integer getPercentualFim() {
        return percentualFim;
    }

    public void setPercentualFim(Integer percentualFim) {
        this.percentualFim = percentualFim;
    }

    @Override
    public String toString() {
        return nome + " - de " + percentualInicio + " a " + percentualFim;
    }
}
