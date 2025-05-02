/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jaimaum
 */
@Entity

@Audited
@Etiqueta("CLP")
public class CLP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private String codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String nome;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Função")
    private String descricao;
    @Invisivel
    @Tabelavel
    @OrderBy(value = "item")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clp", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LCP> lcps;
    //    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cLPConfiguracao")
//    private List<CLPConfiguracaoParametro> cLPConfiguracaoParametro;
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Etiqueta(value = "Exercício")
    @Tabelavel
    private Exercicio exercicio;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Início Vigência")
    @Pesquisavel
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Fim Vigência")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;


    public CLP() {
        criadoEm = System.nanoTime();
        lcps = new ArrayList<LCP>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<LCP> getLcps() {
        return lcps;
    }

    public void setLcps(List<LCP> lcps) {
        this.lcps = lcps;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }



    //    public List<CLPConfiguracaoParametro> getcLPConfiguracaoParametro() {
//        return cLPConfiguracaoParametro;
//    }
//
//    public void setcLPConfiguracaoParametro(List<CLPConfiguracaoParametro> cLPConfiguracaoParametro) {
//        this.cLPConfiguracaoParametro = cLPConfiguracaoParametro;
//    }
    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return codigo + " - " + nome;
    }
}
