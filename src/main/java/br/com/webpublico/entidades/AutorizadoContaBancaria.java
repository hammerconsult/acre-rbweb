package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Bancario")
@Entity

@Audited
public class AutorizadoContaBancaria implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    @ManyToOne
    private PessoaFisica pessoaFisica;

}
