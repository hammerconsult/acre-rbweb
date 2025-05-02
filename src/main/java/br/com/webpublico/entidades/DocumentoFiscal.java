/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author arthur
 */
@GrupoDiagrama(nome = "Material")
@Audited
@Entity

@Inheritance(strategy = InheritanceType.JOINED)
public class DocumentoFiscal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date emissao;
    private Integer numero;
    @Monetario
    private BigDecimal valor;
    @ManyToOne
    private Pessoa emitente;
    @ManyToOne
    private Pessoa destinatario;
    @ManyToOne
    private Pessoa transportador;
}
