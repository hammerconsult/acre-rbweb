/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author major
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)

@Etiqueta("Divida Passiva")
public class DividaPassiva extends LancContabilManual implements Serializable {

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    private Contrato contrato;

    public DividaPassiva() {
    }

    public DividaPassiva(Contrato contrato, BigDecimal valorLancamento, Date dataLancamento, String numero, String historico, EventoContabil eventoContabil, UnidadeOrganizacional unidadeOrganizacional, ParametroEvento parametroEvento) {
        super(valorLancamento, dataLancamento, numero, historico, eventoContabil, unidadeOrganizacional, parametroEvento);
        this.contrato = contrato;
    }


    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    @Override
    public String toString() {
        return "";
    }
}
