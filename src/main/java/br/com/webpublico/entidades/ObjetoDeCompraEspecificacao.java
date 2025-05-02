package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Invisivel;
import com.beust.jcommander.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Desenvolvimento on 08/01/2016.
 */
@Entity
@Audited
@Table(name = "OBJETOCOMPRAESPECIFICACAO")
public class ObjetoDeCompraEspecificacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    private String texto;

    @ManyToOne
    private ObjetoCompra objetoCompra;

    private Boolean ativo;

    @Transient
    private Boolean selecionada;

    public ObjetoDeCompraEspecificacao() {
        super();
    }

    public ObjetoDeCompraEspecificacao(Boolean ativo, ObjetoCompra objetoCompra) {
        super();
        this.ativo = ativo;
        this.objetoCompra = objetoCompra;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        if (this.texto == null) {
            return "";
        }
        return this.texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTextoCurto() {
        if (Strings.isStringEmpty(texto)) {
            return "";
        }
        int endIndex = Math.min(texto.trim().length(), 600);
        return texto.trim().substring(0, endIndex) + (endIndex >= 600 ? "..." : "");
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getSelecionada() {
        return selecionada;
    }

    public void setSelecionada(Boolean selecionada) {
        this.selecionada = selecionada;
    }
}
