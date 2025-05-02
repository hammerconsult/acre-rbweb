package br.com.webpublico.nfse;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Audited
@Etiqueta("Parecer Fiscal")
public class ParecerFiscalNfse extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataParecer;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    private Integer ano;
    @ManyToOne
    private UsuarioSistema fiscal;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Enumerated(EnumType.STRING)
    private TipoParecer tipo;
    private String parecer;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataParecer() {
        return dataParecer;
    }

    public void setDataParecer(Date dataParecer) {
        this.dataParecer = dataParecer;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public UsuarioSistema getFiscal() {
        return fiscal;
    }

    public void setFiscal(UsuarioSistema fiscal) {
        this.fiscal = fiscal;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public TipoParecer getTipo() {
        return tipo;
    }

    public void setTipo(TipoParecer tipo) {
        this.tipo = tipo;
    }

    public enum TipoParecer {
        LIVRO_FISCAL
    }


}
