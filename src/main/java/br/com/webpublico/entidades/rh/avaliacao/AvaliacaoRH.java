package br.com.webpublico.entidades.rh.avaliacao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@GrupoDiagrama(nome = "RecursosHumanos - Avaliações")
@Audited
@Etiqueta("Grupo de Avaliação")
public class AvaliacaoRH extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String nome;
    @ManyToOne
    private MontagemAvaliacao montagemAvaliacao;
    @OneToMany(mappedBy = "avaliacaoRH", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvaliacaoVinculoFP> avaliacaoVinculoFPs;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Date vigenciaInicial;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Date vigenciaFinal;

    public AvaliacaoRH() {
        this.dataRegistro = new Date();
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

    public MontagemAvaliacao getMontagemAvaliacao() {
        return montagemAvaliacao;
    }

    public void setMontagemAvaliacao(MontagemAvaliacao montagemAvaliacao) {
        this.montagemAvaliacao = montagemAvaliacao;
    }

    public List<AvaliacaoVinculoFP> getAvaliacaoVinculoFPs() {
        return avaliacaoVinculoFPs;
    }

    public void setAvaliacaoVinculoFPs(List<AvaliacaoVinculoFP> avaliacaoVinculoFPs) {
        this.avaliacaoVinculoFPs = avaliacaoVinculoFPs;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getVigenciaInicial() {
        return vigenciaInicial;
    }

    public void setVigenciaInicial(Date vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    public Date getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(Date vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }
}
