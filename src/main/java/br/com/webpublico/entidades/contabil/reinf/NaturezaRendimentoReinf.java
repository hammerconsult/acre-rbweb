package br.com.webpublico.entidades.contabil.reinf;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.contabil.reinf.TipoGrupoNaturezaRendimentoReinf;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Audited
@Entity
public class NaturezaRendimentoReinf extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Grupo")
    private TipoGrupoNaturezaRendimentoReinf tipoGrupo;
    @Obrigatorio
    @Etiqueta("Código")
    private String codigo;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @ManyToOne
    @Etiqueta("Conta Padrão")
    private Conta conta;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    public NaturezaRendimentoReinf() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoGrupoNaturezaRendimentoReinf getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupoNaturezaRendimentoReinf tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
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

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public String toStringAutoComplete() {
        String retorno = codigo + " - " + descricao;
        if (retorno.length() > 70) {
            return retorno.substring(0, 67) + "...";
        }
        return retorno;
    }
}
