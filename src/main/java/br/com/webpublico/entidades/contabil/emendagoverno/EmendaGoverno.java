package br.com.webpublico.entidades.contabil.emendagoverno;

import br.com.webpublico.entidades.EsferaGoverno;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoEmenda;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Etiqueta("Emenda do Governo")
@Audited
public class EmendaGoverno extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Length(maximo = 20)
    @Etiqueta("Número")
    private String numero;
    @Obrigatorio
    @Length(maximo = 3000)
    @Etiqueta("Descrição")
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo")
    private TipoEmenda tipo;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Esfera do Governo")
    private EsferaGoverno esferaGoverno;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emendaGoverno", orphanRemoval = true)
    private List<EmendaGovernoParlamentar> parlamentares;

    public EmendaGoverno() {
        super();
        parlamentares = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoEmenda getTipo() {
        return tipo;
    }

    public void setTipo(TipoEmenda tipo) {
        this.tipo = tipo;
    }

    public EsferaGoverno getEsferaGoverno() {
        return esferaGoverno;
    }

    public void setEsferaGoverno(EsferaGoverno esferaGoverno) {
        this.esferaGoverno = esferaGoverno;
    }

    public List<EmendaGovernoParlamentar> getParlamentares() {
        return parlamentares;
    }

    public void setParlamentares(List<EmendaGovernoParlamentar> parlamentares) {
        this.parlamentares = parlamentares;
    }

    @Override
    public String toString() {
        return numero + " - " + descricao;
    }

    public String toStringAutoComplete() {
        int tamanho = 67;
        String retorno = numero != null ? numero + " - " : "";
        if (descricao != null) {
            String desc = descricao.replace("\n", " ").replace("\r", " ");
            retorno += desc;
            if (retorno.length() > tamanho) {
                retorno += retorno.substring(0, tamanho) + "...";
            }
        }
        return retorno;
    }
}
