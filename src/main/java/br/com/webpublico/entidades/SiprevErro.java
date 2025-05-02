package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class SiprevErro extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Siprev siprev;
    private String mensagem;

    public SiprevErro() {

    }

    public SiprevErro(Siprev siprev, String mensagem) {
        this.siprev = siprev;
        this.mensagem = mensagem;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Siprev getSiprev() {
        return siprev;
    }

    public void setSiprev(Siprev siprev) {
        this.siprev = siprev;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
