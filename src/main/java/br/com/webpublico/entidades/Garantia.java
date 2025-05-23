package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Alex
 * @since 12/05/2017 08:25
 */
@Entity
@Audited
@Etiqueta(value = "Garantia do Bem", genero = "F")
public class Garantia extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Etiqueta(value = "Descrição da Garantia")
    @Obrigatorio
    private String descricao;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Vencimento da Garantia")
    @Obrigatorio
    private Date dataVencimento;

    public Garantia() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
