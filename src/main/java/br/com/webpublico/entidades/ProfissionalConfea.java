package br.com.webpublico.entidades;

import br.com.webpublico.enums.GrupoConfea;
import br.com.webpublico.enums.ModalidadeConfea;
import br.com.webpublico.enums.NivelConfea;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by venom on 03/11/14.
 */
@Entity
@Audited
@Etiqueta(value = "Títulos Profissionais CONFEA")
public class ProfissionalConfea implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta(value = "Código")
    @Tabelavel
    @Pesquisavel
    private String codigo;
    @Obrigatorio
    @Etiqueta(value = "Título Masculino")
    @Tabelavel
    @Pesquisavel
    private String tituloMasculino;
    @Obrigatorio
    @Etiqueta(value = "Título Feminino")
    @Tabelavel
    @Pesquisavel
    private String tituloFeminino;
    @Obrigatorio
    @Etiqueta(value = "Título Abreviado")
    @Tabelavel
    @Pesquisavel
    private String tituloAbreviado;
    @Obrigatorio
    @Etiqueta(value = "Grupo")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private GrupoConfea grupoConfea;
    @Obrigatorio
    @Etiqueta(value = "Modalidade")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private ModalidadeConfea modalidadeConfea;
    @Obrigatorio
    @Etiqueta(value = "Nível")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private NivelConfea nivelConfea;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ProfissionalConfea() {
        this.criadoEm = System.nanoTime();
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

    public String getTituloMasculino() {
        return tituloMasculino;
    }

    public void setTituloMasculino(String tituloMasculino) {
        this.tituloMasculino = tituloMasculino;
    }

    public String getTituloFeminino() {
        return tituloFeminino;
    }

    public void setTituloFeminino(String tituloFeminino) {
        this.tituloFeminino = tituloFeminino;
    }

    public String getTituloAbreviado() {
        return tituloAbreviado;
    }

    public void setTituloAbreviado(String tituloAbreviado) {
        this.tituloAbreviado = tituloAbreviado;
    }

    public GrupoConfea getGrupoConfea() {
        return grupoConfea;
    }

    public void setGrupoConfea(GrupoConfea grupoConfea) {
        this.grupoConfea = grupoConfea;
    }

    public ModalidadeConfea getModalidadeConfea() {
        return modalidadeConfea;
    }

    public void setModalidadeConfea(ModalidadeConfea modalidadeConfea) {
        this.modalidadeConfea = modalidadeConfea;
    }

    public NivelConfea getNivelConfea() {
        return nivelConfea;
    }

    public void setNivelConfea(NivelConfea nivelConfea) {
        this.nivelConfea = nivelConfea;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return codigo + "/" + tituloAbreviado;
    }
}
