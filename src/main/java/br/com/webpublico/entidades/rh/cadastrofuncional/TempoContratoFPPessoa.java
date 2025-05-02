package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.pessoa.dto.TempoContratoFPDTO;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by William on 23/05/2019.
 */
@Entity
@Audited
public class TempoContratoFPPessoa extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisica pessoaFisica;
    private String localTrabalho;
    @Temporal(TemporalType.DATE)
    private Date inicio;
    @Temporal(TemporalType.DATE)
    private Date fim;

    public static TempoContratoFPDTO tempoToDto(TempoContratoFPPessoa tempo) {
        TempoContratoFPDTO dto = new TempoContratoFPDTO();
        dto.setId(tempo.getId());
        dto.setLocalTrabalhado(tempo.getLocalTrabalho());
        dto.setInicio(tempo.getInicio());
        dto.setFim(tempo.getFim());
        return dto;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalTrabalho() {
        return localTrabalho;
    }

    public void setLocalTrabalho(String localTrabalho) {
        this.localTrabalho = localTrabalho;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }
}
