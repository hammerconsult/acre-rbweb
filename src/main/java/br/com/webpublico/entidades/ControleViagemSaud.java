package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class ControleViagemSaud extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Obrigatorio
    private Long codigo;
    @Obrigatorio
    @Etiqueta("Agendamento")
    @ManyToOne
    private AgendamentoViagemSaud agendamentoViagemSaud;
    @Obrigatorio
    @Etiqueta("Motorista do SAUD")
    @ManyToOne
    private MotoristaSaud motoristaSaud;
    @Obrigatorio
    @Etiqueta("Viagem Realizada")
    private Boolean viagemRealizada;

    public ControleViagemSaud() {
        super();
        this.viagemRealizada = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public AgendamentoViagemSaud getAgendamentoViagemSaud() {
        return agendamentoViagemSaud;
    }

    public void setAgendamentoViagemSaud(AgendamentoViagemSaud agendamentoViagemSaud) {
        this.agendamentoViagemSaud = agendamentoViagemSaud;
    }

    public MotoristaSaud getMotoristaSaud() {
        return motoristaSaud;
    }

    public void setMotoristaSaud(MotoristaSaud motoristaSaud) {
        this.motoristaSaud = motoristaSaud;
    }

    public Boolean getViagemRealizada() {
        return viagemRealizada;
    }

    public void setViagemRealizada(Boolean viagemRealizada) {
        this.viagemRealizada = viagemRealizada;
    }

    @Override
    public void realizarValidacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (agendamentoViagemSaud == null) ve.adicionarMensagemDeCampoObrigatorio("Informe um agendamento");
        if (motoristaSaud == null) ve.adicionarMensagemDeCampoObrigatorio("Informe o motorista");
        ve.lancarException();
    }
}
