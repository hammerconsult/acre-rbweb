package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Wellington on 22/12/2016.
 */
@Entity
@Audited
@Etiqueta("Mala Direta de RBtrans - Permissão")
public class MalaDiretaRBTransParcela extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private MalaDiretaRBTransPermissao malaDiretaRBTransPermissao;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    @ManyToOne
    private DAM dam;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MalaDiretaRBTransPermissao getMalaDiretaRBTransPermissao() {
        return malaDiretaRBTransPermissao;
    }

    public void setMalaDiretaRBTransPermissao(MalaDiretaRBTransPermissao malaDiretaRBTransPermissao) {
        this.malaDiretaRBTransPermissao = malaDiretaRBTransPermissao;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }
}
