package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Sub Página Menu Horizontal Página Prefeitura")
public class SubPaginaMenuHorizontal extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PaginaMenuHorizontalPaginaPrefeitura paginaMenuHorizontal;
    @Obrigatorio
    @Etiqueta("Título")
    private String titulo;
    @Obrigatorio
    @Etiqueta("Chave")
    private String chave;
    @Obrigatorio
    private Integer ordem;

    public SubPaginaMenuHorizontal() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaginaMenuHorizontalPaginaPrefeitura getPaginaMenuHorizontal() {
        return paginaMenuHorizontal;
    }

    public void setPaginaMenuHorizontal(PaginaMenuHorizontalPaginaPrefeitura paginaMenuHorizontal) {
        this.paginaMenuHorizontal = paginaMenuHorizontal;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
