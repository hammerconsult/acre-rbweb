package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 18/02/14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited

@Etiqueta("Configuração Despesa Bens")
public class ConfigDespesaBens implements Serializable, ValidadorVigencia {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupoBem;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Grupo Patrimonial")
    private TipoGrupo tipoGrupoBem;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    private Conta contaDespesa;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;


    public ConfigDespesaBens() {
        criadoEm = System.nanoTime();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigenci) {
        this.inicioVigencia = inicioVigenci;
    }

    @Override
    public Date getFimVigencia() {
        return fimVigencia;
    }

    @Override
    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoGrupo getTipoGrupoBem() {
        return tipoGrupoBem;
    }

    public void setTipoGrupoBem(TipoGrupo tipoGrupoBem) {
        this.tipoGrupoBem = tipoGrupoBem;
    }

    public Boolean getErroDuranteProcessamento() {
        return erroDuranteProcessamento != null ? erroDuranteProcessamento : Boolean.FALSE;
    }

    public void setErroDuranteProcessamento(Boolean erroDuranteProcessamento) {
        this.erroDuranteProcessamento = erroDuranteProcessamento;
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }

    public void setMensagemDeErro(String mensagemDeErro) {
        this.mensagemDeErro = mensagemDeErro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigDespesaBens)) return false;

        ConfigDespesaBens that = (ConfigDespesaBens) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String toString() {
        return DataUtil.getDataFormatada(inicioVigencia) + " - " + grupoBem + " - " + contaDespesa;
    }
}









