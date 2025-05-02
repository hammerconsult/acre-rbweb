package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMalaDireta;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Configuração da Mala Direta")
public class ParametroMalaDireta extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo da Mala Direta")
    private TipoMalaDireta tipoMalaDireta;
    @Obrigatorio
    private String corpo;
    @Obrigatorio
    private String cabecalho;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Início da Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Final da Vigência")
    private Date fimVigencia;
    @Length(max = 255)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;

    public ParametroMalaDireta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoMalaDireta getTipoMalaDireta() {
        return tipoMalaDireta;
    }

    public void setTipoMalaDireta(TipoMalaDireta tipoMalaDireta) {
        this.tipoMalaDireta = tipoMalaDireta;
    }

    public String getCabecalho() {
        return cabecalho;
    }

    public void setCabecalho(String cabecalho) {
        this.cabecalho = cabecalho;
    }

    @Override
    public String toString() {
        return tipoMalaDireta.getDescricao() + " - " + descricao;
    }
}
