package br.com.webpublico.entidades;

import br.com.webpublico.enums.Situacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mateus on 31/07/17.
 */
@Entity
@Audited
@Etiqueta("Identificador")
@GrupoDiagrama(nome = "Contábil")
public class Identificador extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Número")
    @Tabelavel
    private String numero;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    @Length(maximo = 255)
    private String descricao;
    @Obrigatorio
    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date data;
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private Situacao situacao;

    public Identificador() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        return numero + " - " + descricao + " - " + DataUtil.getDataFormatada(data);
    }
}
