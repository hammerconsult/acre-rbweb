/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Representa os diversos níveis do mapa estratégico (por eixos, subdivisões,
 * etc.). Permite a abertura dos níveis de maneira hierárquica.
 *
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Table(name = "Itens_Plan")
@Audited
@Etiqueta("Objetivo do Eixo")
public class ItemPlanejamentoEstrategico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    private String nome;
    @Etiqueta("Descrição")
    private String descricao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private PlanejamentoEstrategico planejamentoEstrategico;
    @ManyToOne
    @Etiqueta("Eixo Estratégico")
    private MacroObjetivoEstrategico macroObjetivoEstrategico;
    private String codigo;
    /**
     * Item superior que representa o grupo ao qual pertence o item atual.
     */
    @ManyToOne
    @Tabelavel
    @Etiqueta("Item Superior")
    private ItemPlanejamentoEstrategico itemSuperior;
    @Transient
    private Long criadoEm;

    public ItemPlanejamentoEstrategico() {
        dataRegistro = new Date();
        criadoEm = System.nanoTime();
    }

    public ItemPlanejamentoEstrategico(String nome, String descricao, PlanejamentoEstrategico planejamentoEstrategico, ItemPlanejamentoEstrategico itemSuperior, String codigo) {
        this.nome = nome;
        this.descricao = descricao;
        this.planejamentoEstrategico = planejamentoEstrategico;
        this.itemSuperior = itemSuperior;
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanejamentoEstrategico getPlanejamentoEstrategico() {
        return planejamentoEstrategico;
    }

    public void setPlanejamentoEstrategico(PlanejamentoEstrategico planejamentoEstrategico) {
        this.planejamentoEstrategico = planejamentoEstrategico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ItemPlanejamentoEstrategico getItemSuperior() {
        return itemSuperior;
    }

    public void setItemSuperior(ItemPlanejamentoEstrategico itemSuperior) {
        this.itemSuperior = itemSuperior;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getCodigo() {
        return codigo;
    }

    public Integer getCodigoInteiro() {
        return Integer.parseInt(codigo);
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public MacroObjetivoEstrategico getMacroObjetivoEstrategico() {
        return macroObjetivoEstrategico;
    }

    public void setMacroObjetivoEstrategico(MacroObjetivoEstrategico macroObjetivoEstrategico) {
        this.macroObjetivoEstrategico = macroObjetivoEstrategico;
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
        return nome;
    }
}
