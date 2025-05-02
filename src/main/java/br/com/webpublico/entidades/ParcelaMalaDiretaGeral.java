package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 07/06/2016.
 */
@Entity
@Audited
public class ParcelaMalaDiretaGeral extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParcelaValorDivida parcela;
    @ManyToOne
    private ItemMalaDiretaGeral itemMalaDiretaGeral;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public ItemMalaDiretaGeral getItemMalaDiretaGeral() {
        return itemMalaDiretaGeral;
    }

    public void setItemMalaDiretaGeral(ItemMalaDiretaGeral itemMalaDiretaGeral) {
        this.itemMalaDiretaGeral = itemMalaDiretaGeral;
    }
}
