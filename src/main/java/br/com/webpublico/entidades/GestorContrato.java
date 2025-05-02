package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Gestor do Contrato")
public class GestorContrato extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date finalVigencia;

    @ManyToOne
    @Etiqueta("Servidor")
    private ContratoFP servidor;

    @ManyToOne
    @Etiqueta("Pessoa Física")
    private PessoaFisica servidorPF;

    public GestorContrato() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public ContratoFP getServidor() {
        return servidor;
    }

    public void setServidor(ContratoFP servidor) {
        if (servidor != null) {
            setServidorPF(servidor.getMatriculaFP().getPessoa());
        }
        this.servidor = servidor;
    }

    public PessoaFisica getServidorPF() {
        return servidorPF;
    }

    public void setServidorPF(PessoaFisica servidorPF) {
        this.servidorPF = servidorPF;
    }

    @Override
    public String toString() {
        return servidor.toString();
    }

    public String getGestor() {
        if (servidor != null) {
            return servidor.toString();
        } else if (servidorPF != null) {
            return servidorPF.toString();
        }
        return null;
    }
}
