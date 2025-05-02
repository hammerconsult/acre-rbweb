/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonardo
 */
@GrupoDiagrama(nome = "Alvara")
@Audited
@Entity
@Etiqueta("Irregularidade")
public class Irregularidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo")
    @Enumerated(EnumType.STRING)
    private Irregularidade.Tipo tipoDeIrregularidade;
    @Transient
    private Long criadoEm;

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

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Tipo getTipoDeIrregularidade() {
        return tipoDeIrregularidade;
    }

    public void setTipoDeIrregularidade(Tipo tipoDeIrregularidade) {
        this.tipoDeIrregularidade = tipoDeIrregularidade;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return getDescricao();
    }

    public enum Tipo implements EnumComDescricao {

        IRREGULARIDADE("Irregularidade"),
        SOLICITACAO("Solicitação"),
        MONITORAMENTO_FISCAL("Monitoramento Fiscal");
        private String descricao;

        private Tipo(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
