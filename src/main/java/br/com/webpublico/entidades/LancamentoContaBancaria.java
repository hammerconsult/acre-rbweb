/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "Bancario")
@Entity

@Audited
public class LancamentoContaBancaria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ContaBancariaEntidade contaBancariaEntidade;
    @Temporal(TemporalType.DATE)
    private Date realizadoEm;
    @Monetario
    private BigDecimal valor;
    private String historico;
}
