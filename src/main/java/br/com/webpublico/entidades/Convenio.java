/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRecurso;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

public class Convenio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    private String descricao;
    @ManyToOne
    private TipoConvenio tipoConvenio;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    private String numeroDoTermo;
    private String numeroDoDiarioOficial;
    @Temporal(TemporalType.DATE)
    private Date dataDoDiarioOficial;
    @ManyToOne
    private OrgaoRepassador orgaoRepassador;
    @Enumerated(EnumType.STRING)
    private TipoRecurso tipoRecursoOrgaoRepassador;
    @Monetario
    private BigDecimal valorRepasse;
    @Monetario
    private BigDecimal valorContrapartida;
    @Enumerated(EnumType.STRING)
    private TipoRecurso tipoRecursoConvenio;
}
