package br.com.webpublico.entidades;

import javax.persistence.*;

@Entity
public class CalculoAlvaraEfetivo extends SuperEntidade{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoAlvara calculoAlvara;
    private Long idCalculoAgrupado;

    public CalculoAlvaraEfetivo() {
    }

    public CalculoAlvaraEfetivo(CalculoAlvara calculoAlvara, Long idCalculoAgrupado) {
        this.calculoAlvara = calculoAlvara;
        this.idCalculoAgrupado = idCalculoAgrupado;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoAlvara getCalculoAlvara() {
        return calculoAlvara;
    }

    public void setCalculoAlvara(CalculoAlvara calculoAlvara) {
        this.calculoAlvara = calculoAlvara;
    }

    public Long getIdCalculoAgrupado() {
        return idCalculoAgrupado;
    }

    public void setIdCalculoAgrupado(Long idCalculoAgrupado) {
        this.idCalculoAgrupado = idCalculoAgrupado;
    }
}
