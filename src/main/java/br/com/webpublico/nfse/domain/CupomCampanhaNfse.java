package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by William on 20/01/2019.
 */
@Entity
@Audited
public class CupomCampanhaNfse extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CampanhaNfse campanha;
    @ManyToOne
    private UsuarioNotaPremiada usuario;
    @ManyToOne
    private DeclaracaoPrestacaoServico declaracao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEmissao;
    private String numero;
    @ManyToOne
    private PremioSorteioNfse premio;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CampanhaNfse getCampanha() {
        return campanha;
    }

    public void setCampanha(CampanhaNfse campanha) {
        this.campanha = campanha;
    }

    public UsuarioNotaPremiada getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioNotaPremiada usuario) {
        this.usuario = usuario;
    }

    public DeclaracaoPrestacaoServico getDeclaracao() {
        return declaracao;
    }

    public void setDeclaracao(DeclaracaoPrestacaoServico declaracao) {
        this.declaracao = declaracao;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date emitidoEm) {
        this.dataEmissao = emitidoEm;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public PremioSorteioNfse getPremio() {
        return premio;
    }

    public void setPremio(PremioSorteioNfse premio) {
        this.premio = premio;
    }
}
