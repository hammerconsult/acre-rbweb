package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desenvolvimento on 21/07/2016.
 */
@Entity
@Audited
public class ItemArquivoSimplesNacional extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ArquivoInconsistenciaSimplesNacional arquivo;
    private String cnpj;
    private Boolean enviouInconsistencia;
    private Boolean enviouRegularizacao;

    public ItemArquivoSimplesNacional() {
        super();
        this.enviouInconsistencia = Boolean.FALSE;
        this.enviouRegularizacao = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArquivoInconsistenciaSimplesNacional getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoInconsistenciaSimplesNacional arquivo) {
        this.arquivo = arquivo;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Boolean getEnviouInconsistencia() {
        return enviouInconsistencia;
    }

    public void setEnviouInconsistencia(Boolean enviouInconsistencia) {
        this.enviouInconsistencia = enviouInconsistencia;
    }

    public Boolean getEnviouRegularizacao() {
        return enviouRegularizacao;
    }

    public void setEnviouRegularizacao(Boolean enviouRegularizacao) {
        this.enviouRegularizacao = enviouRegularizacao;
    }
}
