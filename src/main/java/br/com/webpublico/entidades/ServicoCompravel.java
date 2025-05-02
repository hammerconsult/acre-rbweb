/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Licitacao")
@Entity

@Audited
@Etiqueta("Serviço Comprável")
public class ServicoCompravel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Código")

    @Pesquisavel
    private Long codigo;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Grupo Serviço Comprável")
    private GrupoServicoCompravel grupoServCompravel;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ServicoCompravel() {
        criadoEm = System.nanoTime();
    }

    public GrupoServicoCompravel getGrupoServCompravel() {
        return grupoServCompravel;
    }

    public void setGrupoServCompravel(GrupoServicoCompravel grupoServCompravel) {
        this.grupoServCompravel = grupoServCompravel;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
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
        try {
            if (this.getDescricao() != null) {
                return "" + this.getCodigo() + " - " + this.getDescricao();
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }
}
