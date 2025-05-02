package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@Etiqueta("Agrupador do Relatório de Créditos")
public class AgrupadorRelatorioCredito extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @OneToMany(mappedBy = "agrupadorRelatorioCredito", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Dívidas")
    private List<AgrupadorRelatorioCreditoDivida> dividas;

    public AgrupadorRelatorioCredito() {
        super();
        dividas = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<AgrupadorRelatorioCreditoDivida> getDividas() {
        return dividas;
    }

    public void setDividas(List<AgrupadorRelatorioCreditoDivida> dividas) {
        this.dividas = dividas;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
