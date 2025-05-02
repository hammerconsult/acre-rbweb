/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSequenciaDoctoOficial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author fabio
 */
@Entity

@GrupoDiagrama(nome = "Certidao")
@Audited
@Etiqueta("Grupo de Documento Oficial")
public class GrupoDoctoOficial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Long codigo;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Sequência")
    private TipoSequenciaDoctoOficial tipoSequencia;
    @Transient
    private Long criadoEm;

    public GrupoDoctoOficial() {
        this.criadoEm = System.nanoTime();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoSequenciaDoctoOficial getTipoSequencia() {
        return tipoSequencia;
    }

    public void setTipoSequencia(TipoSequenciaDoctoOficial tipoSequencia) {
        this.tipoSequencia = tipoSequencia;
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
        return descricao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }


}
