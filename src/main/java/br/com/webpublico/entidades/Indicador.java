/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Indicador")
public class Indicador extends SuperEntidade{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Nome")
    @Pesquisavel
    private String nome;
    /**
     * Define qual instituto/órgão/empresa fornece as informações deste
     * indicador.
     */
    @Tabelavel
    @Pesquisavel
    private String fonte;
    /**
     * Descrição da fórmula usada para calcular o indicador
     */
    @Obrigatorio
    @Etiqueta("Fórmula Do Indicador")
    @Tabelavel
    @Pesquisavel
    private String formula;

    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;

    @Etiqueta("Objetivos de Desenvolvimento Sustentável")
    private BigDecimal ods;

    @Invisivel
    @OneToMany(mappedBy = "indicador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValorIndicador> valorIndicadores;

    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public String getFonte() {
        return fonte;
    }

    public List<ValorIndicador> getValorIndicadores() {
        return valorIndicadores;
    }

    public void setValorIndicadores(List<ValorIndicador> valorIndicadores) {
        this.valorIndicadores = valorIndicadores;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public Indicador() {
        dataRegistro = new Date();
    }

    public Indicador(String descricao, UnidadeMedida unidadeMedida) {
        this.descricao = descricao;

    }

    public BigDecimal getOds() {
        return ods;
    }

    public void setOds(BigDecimal ods) {
        this.ods = ods;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        return nome;
    }
}
