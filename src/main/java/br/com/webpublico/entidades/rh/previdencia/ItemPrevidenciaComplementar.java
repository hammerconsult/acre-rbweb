package br.com.webpublico.entidades.rh.previdencia;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.previdencia.TipoRegimeTributacaoBBPrev;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity
@Table(name = "ITEMPREVCOMPLEMENTAR")
public class ItemPrevidenciaComplementar extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Previdência Complementar")
    private PrevidenciaComplementar previdenciaComplementar;
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    private Date inicioVigencia;
    @Etiqueta("Final da Vigência")
    private Date finalVigencia;
    @Etiqueta("Alíquota Servidor")
    private BigDecimal aliquotaServidor;
    @Etiqueta("Alíquota Patrocinador")
    private BigDecimal aliquotaPatrocinador;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação BBPrev")
    private TipoRegimeTributacaoBBPrev regimeTributacaoBBPrev;

    public ItemPrevidenciaComplementar() {
    }

    public ItemPrevidenciaComplementar(PrevidenciaComplementar previdenciaComplementar) {
        this.previdenciaComplementar = previdenciaComplementar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrevidenciaComplementar getPrevidenciaComplementar() {
        return previdenciaComplementar;
    }

    public void setPrevidenciaComplementar(PrevidenciaComplementar previdenciaComplementar) {
        this.previdenciaComplementar = previdenciaComplementar;
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

    public BigDecimal getAliquotaServidor() {
        return aliquotaServidor;
    }

    public void setAliquotaServidor(BigDecimal aliquotaServidor) {
        this.aliquotaServidor = aliquotaServidor;
    }

    public BigDecimal getAliquotaPatrocinador() {
        return aliquotaPatrocinador;
    }

    public void setAliquotaPatrocinador(BigDecimal aliquotaPatrocinador) {
        this.aliquotaPatrocinador = aliquotaPatrocinador;
    }

    public TipoRegimeTributacaoBBPrev getRegimeTributacaoBBPrev() {
        return regimeTributacaoBBPrev;
    }

    public void setRegimeTributacaoBBPrev(TipoRegimeTributacaoBBPrev regimeTributacaoBBPrev) {
        this.regimeTributacaoBBPrev = regimeTributacaoBBPrev;
    }
}
