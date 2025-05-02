package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class CDABloqueioJudicial extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private BloqueioJudicial bloqueioJudicial;
    private Long idCda;

    public CDABloqueioJudicial() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BloqueioJudicial getBloqueioJudicial() {
        return bloqueioJudicial;
    }

    public void setBloqueioJudicial(BloqueioJudicial bloqueioJudicial) {
        this.bloqueioJudicial = bloqueioJudicial;
    }

    public Long getIdCda() {
        return idCda;
    }

    public void setIdCda(Long idCda) {
        this.idCda = idCda;
    }
}
