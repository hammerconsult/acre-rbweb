package br.com.webpublico.entidades.rh.censo;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.rh.censo.SituacaoCenso;

import javax.persistence.*;
import java.util.Date;

@Entity
public class AnaliseAtualizacaoCenso extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuarioAvaliacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaAvaliacao;
    @Enumerated(EnumType.STRING)
    private SituacaoCenso situacaoCenso;
    private String motivoRejeicao;

    public AnaliseAtualizacaoCenso() {
        this.situacaoCenso = SituacaoCenso.AGUARDANDO_LIBERACAO;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }


    public UsuarioSistema getUsuarioAvaliacao() {
        return usuarioAvaliacao;
    }

    public void setUsuarioAvaliacao(UsuarioSistema usuarioAvaliacao) {
        this.usuarioAvaliacao = usuarioAvaliacao;
    }

    public Date getHoraAvaliacao() {
        return horaAvaliacao;
    }

    public void setHoraAvaliacao(Date horaAvaliacao) {
        this.horaAvaliacao = horaAvaliacao;
    }

    public SituacaoCenso getSituacaoCenso() {
        return situacaoCenso != null ? situacaoCenso : SituacaoCenso.AGUARDANDO_LIBERACAO;
    }

    public void setSituacaoCenso(SituacaoCenso situacaoCenso) {
        this.situacaoCenso = situacaoCenso;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }
}
