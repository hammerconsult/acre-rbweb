package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.FavoritoDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 28/02/14
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity

public class Favorito implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    private String url;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Transient
    private Long criadoEm;

    public Favorito() {
        criadoEm = System.nanoTime();
    }

    public Favorito(String nomeFavorito, String urlFavorito, UsuarioSistema usuarioCorrente) {
        criadoEm = System.nanoTime();
        this.nome = nomeFavorito;
        this.url = urlFavorito;
        this.usuarioSistema = usuarioCorrente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return nome + " - " + url;
    }

    public FavoritoDTO toDTO() {
        FavoritoDTO dto = new FavoritoDTO();
        dto.setId(this.id);
        dto.setNome(this.nome);
        dto.setUrl(this.url);
        return dto;
    }
}
