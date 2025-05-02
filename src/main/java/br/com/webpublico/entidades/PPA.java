/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("PPA")
public class PPA extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Planejamento Estratégico")
    private PlanejamentoEstrategico planejamentoEstrategico;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    /**
     * Data na qual esta versão do PPA foi gerada.
     */
    @Etiqueta("Data de Registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    /**
     * Data na qual esta versão do PPA foi aprovada.
     */
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Aprovação")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date aprovacao;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Versão")
    private String versao;
    /**
     * Determina se este PPA juntamente com todos os seus registros associados
     * podem ou não serem alterados.
     */
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;
    /**
     * Lei aprovada do PPA.
     */
    @ManyToOne
    private AtoLegal atoLegal;
    @OneToMany(mappedBy = "ppa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private List<ReceitaExercicioPPA> receitasExerciciosPPAs;
    @OneToMany(mappedBy = "ppa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private List<ReceitaPPA> receitaPPAs;
    @OneToOne
    private PPA origem;
    private Boolean contabilizado;
    @Transient
    @Invisivel
    private Long criadoEm;

    public PPA() {
        criadoEm = System.nanoTime();
        dataRegistro = new Date();
        somenteLeitura = false;
        contabilizado = false;
    }

    public PPA(PlanejamentoEstrategico planejamentoEstrategico, String descricao, Date aprovacao, String versao, AtoLegal atoLegal) {
        this.planejamentoEstrategico = planejamentoEstrategico;
        this.descricao = descricao;
        this.aprovacao = aprovacao;
        this.versao = versao;
        this.atoLegal = atoLegal;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PlanejamentoEstrategico getPlanejamentoEstrategico() {
        return planejamentoEstrategico;
    }

    public void setPlanejamentoEstrategico(PlanejamentoEstrategico planejamentoEstrategico) {
        this.planejamentoEstrategico = planejamentoEstrategico;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ReceitaExercicioPPA> getReceitasExerciciosPPAs() {
        return receitasExerciciosPPAs;
    }

    public void setReceitasExerciciosPPAs(List<ReceitaExercicioPPA> receitasExerciciosPPAs) {
        this.receitasExerciciosPPAs = receitasExerciciosPPAs;
    }

    public PPA getOrigem() {
        return origem;
    }

    public void setOrigem(PPA origem) {
        this.origem = origem;
    }

    public Date getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(Date aprovacao) {
        this.aprovacao = aprovacao;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public Boolean getContabilizado() {
        return contabilizado;
    }

    public void setContabilizado(Boolean contabilizado) {
        this.contabilizado = contabilizado;
    }

    public List<ReceitaPPA> getReceitaPPAs() {
        return receitaPPAs;
    }

    public void setReceitaPPAs(List<ReceitaPPA> receitaPPAs) {
        this.receitaPPAs = receitaPPAs;
    }

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
        return descricao + "(" + versao + ")";
    }

    public String getComplementoContabil() {
        String toReturn = "";
        if (this.atoLegal != null) {
            toReturn = toReturn + " - " + this.atoLegal.getNome();
        }
        if (this.dataRegistro != null) {
            toReturn = toReturn + ", " + this.dataRegistro;
        }
        return toReturn;
    }
}
