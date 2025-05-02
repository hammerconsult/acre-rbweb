package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 23/09/14
 * Time: 08:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Taxas para Veículo")
public class TaxaVeiculo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Obrigatória")
    private Boolean obrigatoria;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fim de Vigência")
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    public TaxaVeiculo() {
        this.obrigatoria = Boolean.TRUE;
        this.inicioVigencia = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getObrigatoria() {
        return obrigatoria;
    }

    public void setObrigatoria(Boolean obrigatoria) {
        this.obrigatoria = obrigatoria;
    }
}
