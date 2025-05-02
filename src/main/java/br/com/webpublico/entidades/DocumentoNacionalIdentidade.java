package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by William on 08/08/2018.
 */
@Entity
@Audited
@Table(name = "DOCNACIONALIDENTIDADE")
public class DocumentoNacionalIdentidade extends DocumentoPessoal {
    @Column
    @Etiqueta("DNI")
    @Pesquisavel
    private String dni;
    @Column
    @Etiqueta("Órgão Emissor")
    @Obrigatorio
    private String orgaoEmissao;
    @ManyToOne
    @Etiqueta("U.F.")
    private UF uf;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Expedição")
    private Date dataExpedicao;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getOrgaoEmissao() {
        return orgaoEmissao;
    }

    public void setOrgaoEmissao(String orgaoEmissao) {
        this.orgaoEmissao = orgaoEmissao;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public Date getDataExpedicao() {
        return dataExpedicao;
    }

    public void setDataExpedicao(Date dataExpedicao) {
        this.dataExpedicao = dataExpedicao;
    }
}
