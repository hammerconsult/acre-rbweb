package br.com.webpublico.entidades.rh.processo;


import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Processo;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.rh.processo.TipoPad;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Processos Administrativo Disciplinar")
public class Pad extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoPad tipoPad;

    @ManyToOne
    private UsuarioSistema responsavel;

    @ManyToOne
    private Processo protocolo;

    private Boolean penalidade;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    @ManyToOne
    private ContratoFP contratoFP;


    @Override
    public Long getId() {
        return id;
    }

    public TipoPad getTipoPad() {
        return tipoPad;
    }

    public void setTipoPad(TipoPad tipoPad) {
        this.tipoPad = tipoPad;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public Processo getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Processo protocolo) {
        this.protocolo = protocolo;
    }

    public Boolean getPenalidade() {
        return penalidade;
    }

    public void setPenalidade(Boolean penalidade) {
        this.penalidade = penalidade;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }
}
