package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDicionarioDeDados;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@Etiqueta("Dicionário de Dados")
public class DicionarioDeDados extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Tipo de Dicionário de Dados")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoDicionarioDeDados tipoDicionarioDeDados;
    @OneToMany(mappedBy = "dicionarioDeDados", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColunaDicionarioDeDados> colunas;

    public DicionarioDeDados() {
        super();
        colunas = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoDicionarioDeDados getTipoDicionarioDeDados() {
        return tipoDicionarioDeDados;
    }

    public void setTipoDicionarioDeDados(TipoDicionarioDeDados tipoDicionarioDeDados) {
        this.tipoDicionarioDeDados = tipoDicionarioDeDados;
    }

    public List<ColunaDicionarioDeDados> getColunas() {
        return colunas;
    }

    public void setColunas(List<ColunaDicionarioDeDados> colunas) {
        this.colunas = colunas;
    }
}
