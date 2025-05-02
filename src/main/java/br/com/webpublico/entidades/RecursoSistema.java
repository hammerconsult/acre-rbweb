package br.com.webpublico.entidades;

import br.com.webpublico.enums.Direito;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Segurança")
@Etiqueta(value = "Recursos do Sistema")
public class RecursoSistema implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Nome")
    private String nome;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Caminho")
    @Column(unique = true)
    private String caminho;
    @Column(columnDefinition = "tinyint")
    private Boolean cadastro = Boolean.FALSE;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Módulo")
    @Enumerated(EnumType.STRING)
    private ModuloSistema modulo;
    @Transient
    private Long criadoEm;
    @Invisivel
    @Transient
    private Boolean selecionado;

    public RecursoSistema() {
        criadoEm = System.nanoTime();
        selecionado = false;
    }

    public static String extrairCaminhoAutorizacao(String uri) {
        if (uri.indexOf("/") > 0) {
            return uri.substring(0, uri.lastIndexOf('/'));
        } else {
            return uri;
        }
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Direito obterDireitoExigidoPelaUri(String uri) {
        if (cadastro) {
            if (uri.matches(".*/(lista|visualizar)\\.(xhtml|jsf)")) {
                return Direito.LEITURA;
            } else if (uri.matches(".*/edita\\.(xhtml|jsf)")) {
                return Direito.ESCRITA;
            }
        } else if (caminho.equals(uri)) {
            return Direito.LEITURA;
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public boolean isCadastro() {
        return cadastro;
    }

    public void setCadastro(boolean cadastro) {
        this.cadastro = cadastro;
    }

    public ModuloSistema getModulo() {
        return modulo;
    }

    public void setModulo(ModuloSistema modulo) {
        this.modulo = modulo;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public boolean comparaUri(String uri) {
        return caminho != null &&
                (caminho.equalsIgnoreCase(uri)
                        || caminho.equalsIgnoreCase(extrairCaminhoAutorizacao(uri)));
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RecursoSistema recursoSistema = (RecursoSistema) obj;

        if (this.getId() == null) {
            if (!this.getCriadoEm().equals(recursoSistema.getCriadoEm())) {
                return false;
            }
        } else {
            if (!this.getId().equals(recursoSistema.getId())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            int hash = 3;
            hash = 97 * hash + (criadoEm != null ? criadoEm.hashCode() : 0);
            return hash;
        } else {
            int hash = 7;
            hash = 71 * hash + (id != null ? id.hashCode() : 0);
            return hash;
        }
    }

    @Override
    public String toString() {
        String modulo = "Sem Módulo";
        if (this.getModulo() != null) {
            modulo = this.modulo.getDescricao();
        }
        return nome + " - " + caminho + " (" + modulo + ")";
    }

    public String toStringAutoComplete() {
        String retorno = toString();
        return retorno.length() > 68 ? retorno.substring(0, 65) + "..." : retorno;
    }
}
