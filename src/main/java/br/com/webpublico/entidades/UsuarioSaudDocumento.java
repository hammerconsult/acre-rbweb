package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class UsuarioSaudDocumento extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UsuarioSaud usuarioSaud;
    @ManyToOne
    private ParametroSaudDocumento parametroSaudDocumento;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo documento;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String observacao;

    public UsuarioSaudDocumento() {
        super();
        this.status = Status.AGUARDANDO_AVALIACAO;
    }

    public UsuarioSaudDocumento(UsuarioSaud usuarioSaud, ParametroSaudDocumento parametroSaudDocumento) {
        this();
        this.usuarioSaud = usuarioSaud;
        this.parametroSaudDocumento = parametroSaudDocumento;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSaud getUsuarioSaud() {
        return usuarioSaud;
    }

    public void setUsuarioSaud(UsuarioSaud usuarioSaud) {
        this.usuarioSaud = usuarioSaud;
    }

    public ParametroSaudDocumento getParametroSaudDocumento() {
        return parametroSaudDocumento;
    }

    public void setParametroSaudDocumento(ParametroSaudDocumento parametroSaudDocumento) {
        this.parametroSaudDocumento = parametroSaudDocumento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Arquivo getDocumento() {
        return documento;
    }

    public void setDocumento(Arquivo arquivo) {
        this.documento = arquivo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public enum Status {
        AGUARDANDO_AVALIACAO("Aguardando Avaliação"), APROVADO("Aprovado"), REJEITADO("Rejeitado");

        private String descricao;

        Status(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
