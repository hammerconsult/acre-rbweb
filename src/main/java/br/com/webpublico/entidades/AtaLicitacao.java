package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAtaLicitacao;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 17/03/14
 * Time: 10:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class AtaLicitacao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Tipo da Ata")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoAtaLicitacao tipoAtaLicitacao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private Licitacao licitacao;

    @Etiqueta("Número da Ata")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer numero;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Modelo Documento")
    @ManyToOne
    private ModeloDocto modeloDocto;

    @Etiqueta("Título da Ata")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String titulo;

    @Etiqueta("Descrição")
    private String descricao;

    public AtaLicitacao() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoAtaLicitacao getTipoAtaLicitacao() {
        return tipoAtaLicitacao;
    }

    public void setTipoAtaLicitacao(TipoAtaLicitacao tipoAtaLicitacao) {
        this.tipoAtaLicitacao = tipoAtaLicitacao;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public ModeloDocto getModeloDocto() {
        return modeloDocto;
    }

    public void setModeloDocto(ModeloDocto modeloDocto) {
        this.modeloDocto = modeloDocto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nº ").append(this.getNumero()).append(" Título: ").append(this.getTitulo());
        return sb.toString();
    }

    public String toStringAutoComplete() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nº ").append(this.getNumero()).append(" Tipo: ").append(this.getTipoAtaLicitacao()).append(" Título: ").append(this.getTitulo());
        if (sb.toString().length() > 100) {
            StringBuilder sbAux = new StringBuilder(sb.substring(0, 99));
            sb = new StringBuilder(sbAux);
        }
        return sb.toString();
    }

    public boolean isRetificacao() {
        return TipoAtaLicitacao.RETIFICACAO.equals(this.getTipoAtaLicitacao());
    }
}
