package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Configuração de Conta de Despesa e Tipo de Documento")
@Table(name = "CONFIGCONTADESPESATIPODOC")
public class ConfigContaDespesaTipoDocumento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Conta de Despesa")
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    private ContaDespesa contaDespesa;
    @Obrigatorio
    @Etiqueta("Tipo de Despesa")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private TipoContaDespesa tipoContaDespesa;
    @Pesquisavel
    @Etiqueta("Tipo do Documento")
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    private TipoDocumentoFiscal tipoDocumentoFiscal;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final de Vigência")
    @Pesquisavel
    @Tabelavel
    private Date finalVigencia;

    public ConfigContaDespesaTipoDocumento() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public TipoDocumentoFiscal getTipoDocumentoFiscal() {
        return tipoDocumentoFiscal;
    }

    public void setTipoDocumentoFiscal(TipoDocumentoFiscal tipoDocumentoFiscal) {
        this.tipoDocumentoFiscal = tipoDocumentoFiscal;
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
}
