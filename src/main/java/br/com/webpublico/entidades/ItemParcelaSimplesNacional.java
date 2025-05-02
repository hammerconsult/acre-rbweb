package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Desenvolvimento on 21/07/2016.
 */
@Audited
@Entity
public class ItemParcelaSimplesNacional extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ItemArquivoSimplesNacional itemArquivoSimplesNacional;

    private Long parcela_id;

    public ItemParcelaSimplesNacional() {
        super();
    }

    public ItemParcelaSimplesNacional(ItemArquivoSimplesNacional itemArquivoSimplesNacional, Long parcela_id) {
        super();
        this.itemArquivoSimplesNacional = itemArquivoSimplesNacional;
        this.parcela_id = parcela_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemArquivoSimplesNacional getItemArquivoSimplesNacional() {
        return itemArquivoSimplesNacional;
    }

    public void setItemArquivoSimplesNacional(ItemArquivoSimplesNacional itemArquivoSimplesNacional) {
        this.itemArquivoSimplesNacional = itemArquivoSimplesNacional;
    }

    public Long getParcela_id() {
        return parcela_id;
    }

    public void setParcela_id(Long parcela_id) {
        this.parcela_id = parcela_id;
    }
}
