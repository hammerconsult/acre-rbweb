package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 19/07/13
 * Time: 09:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Etiqueta("Provimento de Reversão")
@GrupoDiagrama(nome = "Recursos Humanos")
@Audited

public class ProvimentoReversao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Servidor")
    @Tabelavel
    @Obrigatorio
    private Aposentadoria aposentadoria;
    @ManyToOne
    @Etiqueta("Novo Contrato")
    @Tabelavel
    private ContratoFP novoContratoFP;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final de Vigência")
    @Tabelavel
    @Pesquisavel
    private Date finalVigencia;
    @ManyToOne
    @Etiqueta("Ato Legal")
    @Obrigatorio
    private AtoLegal atoLegal;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Laudo do Provimento de Reversão")
    private LaudoProvimentoReversao laudoProvimentoReversao;
    @Transient
    @Etiqueta("Tipo de Aposentadoria")
    private TipoAposentadoria tipoAposentadoria;
    @Transient
    private Long criadoEm;

    public ProvimentoReversao() {
        this.criadoEm = System.nanoTime();
        laudoProvimentoReversao = new LaudoProvimentoReversao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aposentadoria getAposentadoria() {
        return aposentadoria;
    }

    public void setAposentadoria(Aposentadoria aposentadoria) {
        this.aposentadoria = aposentadoria;
    }

    public ContratoFP getNovoContratoFP() {
        return novoContratoFP;
    }

    public void setNovoContratoFP(ContratoFP novoContratoFP) {
        this.novoContratoFP = novoContratoFP;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public LaudoProvimentoReversao getLaudoProvimentoReversao() {
        return laudoProvimentoReversao;
    }

    public void setLaudoProvimentoReversao(LaudoProvimentoReversao laudoProvimentoReversao) {
        this.laudoProvimentoReversao = laudoProvimentoReversao;
    }

    public TipoAposentadoria getTipoAposentadoria() {
        return tipoAposentadoria;
    }

    public void setTipoAposentadoria(TipoAposentadoria tipoAposentadoria) {
        this.tipoAposentadoria = tipoAposentadoria;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
