package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Audited
@Entity
@Etiqueta("Dados do Bloqueio Outorga")
@GrupoDiagrama(nome = "RBTrans")
public class DadoBloqueioOutorga extends SuperEntidade {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private BloqueioOutorga bloqueioOutorga;
    @Obrigatorio
    @ManyToOne
    private Pessoa favorecido;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoCalculoRBTRans.TipoValor tipoValor;
    @Obrigatorio
    private BigDecimal valor;
    private BigDecimal montanteBloqueado;
    private BigDecimal montanteOriginal;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataBloqueio;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BloqueioOutorga getBloqueioOutorga() {
        return bloqueioOutorga;
    }

    public void setBloqueioOutorga(BloqueioOutorga bloqueioOutorga) {
        this.bloqueioOutorga = bloqueioOutorga;
    }

    public Pessoa getFavorecido() {
        return favorecido;
    }

    public void setFavorecido(Pessoa favorecido) {
        this.favorecido = favorecido;
    }

    public TipoCalculoRBTRans.TipoValor getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(TipoCalculoRBTRans.TipoValor tipoValor) {
        this.tipoValor = tipoValor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getMontanteBloqueado() {
        return montanteBloqueado;
    }

    public void setMontanteBloqueado(BigDecimal montanteBloqueado) {
        this.montanteBloqueado = montanteBloqueado;
    }

    public BigDecimal getMontanteOriginal() {
        return montanteOriginal;
    }

    public void setMontanteOriginal(BigDecimal montanteOriginal) {
        this.montanteOriginal = montanteOriginal;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }
}
