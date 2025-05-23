/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.enums.TipoAtributo;
import br.com.webpublico.enums.TipoComponenteVisual;
import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author Munif
 */
@CorEntidade("#0000ff")
@GrupoDiagrama(nome = "AtributosDinamicos")
@Entity
@Audited
@Etiqueta("Atributo Genérico")
public class Atributo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Tabelavel
    @Etiqueta("Sequência de Apresentação")
    private Integer sequenciaapresentacao;
    @Tabelavel
    @Etiqueta("Nome")
    @Pesquisavel
    private String nome;
    @Tabelavel
    @Etiqueta("Classe do Atributo")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private ClasseDoAtributo classeDoAtributo;
    @Obrigatorio
    @Etiqueta("Tipo do Atributo")
    @Enumerated(EnumType.STRING)
    private TipoAtributo tipoAtributo;
    @Obrigatorio
    @Etiqueta("Componente Visual")
    @Enumerated(EnumType.STRING)
    private TipoComponenteVisual componenteVisual;
    @Etiqueta("Valores Possíveis")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "atributo", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ValorPossivel> valoresPossiveis = new ArrayList<ValorPossivel>();
    @Etiqueta("Obrigatório")
    private Boolean obrigatorio;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Identificação")
    private String identificacao;
    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;
    private Boolean somenteLeitura;
    @Transient
    private Long criadoEm;

    public Atributo() {
        criadoEm = System.nanoTime();
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public Integer getSequenciaapresentacao() {
        return sequenciaapresentacao;
    }

    public void setSequenciaapresentacao(Integer sequenciaapresentacao) {
        this.sequenciaapresentacao = sequenciaapresentacao;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura == null ? false : somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Atributo(Integer sequenciaApresentacao, String nome, ClasseDoAtributo classeDoAtributo, TipoAtributo tipoAtributo, TipoComponenteVisual componenteVisual, Boolean obrigatorio, Date dataRegistro) {
        this.sequenciaapresentacao = sequenciaApresentacao;
        this.nome = nome;
        this.classeDoAtributo = classeDoAtributo;
        this.tipoAtributo = tipoAtributo;
        this.componenteVisual = componenteVisual;
        this.obrigatorio = obrigatorio;
        this.dataRegistro = dataRegistro;
    }

    public TipoAtributo getTipoAtributo() {
        return tipoAtributo;
    }

    public void setTipoAtributo(TipoAtributo tipoAtributo) {
        this.tipoAtributo = tipoAtributo;
    }

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

    public ClasseDoAtributo getClasseDoAtributo() {
        return classeDoAtributo;
    }

    public void setClasseDoAtributo(ClasseDoAtributo classeDoAtributo) {
        this.classeDoAtributo = classeDoAtributo;
    }

    public List<ValorPossivel> getValoresPossiveis() {
        Collections.sort(valoresPossiveis, new Comparator<ValorPossivel>() {
            @Override
            public int compare(ValorPossivel o1, ValorPossivel o2) {
                try {
                    return o1.getCodigo().compareTo(o2.getCodigo());
                } catch (NumberFormatException e) {
                    return -1;
                } catch (Exception e) {
                    return 0;
                }
            }
        });
        return valoresPossiveis;
    }

    public void setValoresPossiveis(List<ValorPossivel> valoresPossiveis) {
        this.valoresPossiveis = valoresPossiveis;
    }

    public TipoComponenteVisual getComponenteVisual() {
        return componenteVisual;
    }

    public void setComponenteVisual(TipoComponenteVisual componenteVisual) {
        this.componenteVisual = componenteVisual;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Atributo atributo = (Atributo) o;

        if (id != null ? !id.equals(atributo.id) : atributo.id != null) return false;
        return !(identificacao != null ? !identificacao.equals(atributo.identificacao) : atributo.identificacao != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (identificacao != null ? identificacao.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return nome;
    }
}
