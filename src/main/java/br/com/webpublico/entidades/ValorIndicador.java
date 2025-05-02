package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Valor do Indicador")
public class ValorIndicador extends SuperEntidade{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Valor constatado na data de apuração.
     */
    @Obrigatorio
    @Tabelavel
    @Positivo
    @Etiqueta("Valor")
    private BigDecimal valor;
    /**
     * Data de apuração deste valor.
     */
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Apurado em")
    private Date apurado;

    @ManyToOne
    private Indicador indicador;

    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    public ValorIndicador() {
        dataRegistro = new Date();
    }

    public ValorIndicador(BigDecimal valor, Date apurado, Indicador indicador) {
        this.valor = valor;
        this.apurado = apurado;
        this.indicador = indicador;
    }

    public Date getApurado() {
        return apurado;
    }

    public void setApurado(Date apurado) {
        this.apurado = apurado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Indicador getIndicador() {
        return indicador;
    }

    public void setIndicador(Indicador indicador) {
        this.indicador = indicador;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @Override
    public String toString() {
        return "Valor: " + valor + ", Apurado: " + DataUtil.getDataFormatada(apurado) + ", Indicador: " + indicador;
    }
}
