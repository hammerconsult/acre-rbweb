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

@GrupoDiagrama(nome = "Orcamentario")
@Entity

@Audited
public class Cota implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    private Integer mes;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private ContaBancariaEntidade contaBancaria;
    @Monetario
    private BigDecimal valorLiberado;
    @Monetario
    private BigDecimal valorUtilizado;
}
