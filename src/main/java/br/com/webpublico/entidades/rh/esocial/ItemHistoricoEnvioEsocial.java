package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.esocial.enums.ClasseWP;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class ItemHistoricoEnvioEsocial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private HistoricoEnvioEsocial historicoEnvioEsocial;
    private String descricao;
    private String identificador;
    @Enumerated(EnumType.STRING)
    private ClasseWP classeWP;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public ClasseWP getClasseWP() {
        return classeWP;
    }

    public void setClasseWP(ClasseWP classeWP) {
        this.classeWP = classeWP;
    }

    public HistoricoEnvioEsocial getHistoricoEnvioEsocial() {
        return historicoEnvioEsocial;
    }

    public void setHistoricoEnvioEsocial(HistoricoEnvioEsocial historicoEnvioEsocial) {
        this.historicoEnvioEsocial = historicoEnvioEsocial;
    }
}

