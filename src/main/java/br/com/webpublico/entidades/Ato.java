/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Administrativo")
@Entity

@Audited
public class Ato implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;

    private Integer numero;

    @Tabelavel
    @Etiqueta("Data Promulgação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataPromulgacao;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data Publicação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataPublicacao;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioDeValidade;

    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @Obrigatorio
    @Tabelavel
    private String nome;

    @OneToOne
    private Arquivo arquivo;

    @ManyToOne
    private VeiculoDePublicacao veiculoDePublicacao;

    @ManyToOne
    private TipoAto tipoAto;

    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
}
