package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by carlos on 18/06/15.
 */
@Entity
@Audited
@Etiqueta("Equipamento de Proteção Individual")
public class EquipamentoEPI extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Proteção")
    @ManyToOne
    private ProtecaoEPI protecaoEPI;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Categoria do Equipamento de Proteção Individual")
    private String categoria;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Equipamento de Proteção Individual")
    private String equipamento;

    public EquipamentoEPI() {
    }

    public EquipamentoEPI(String categoria, String equipamento, ProtecaoEPI protecaoEPI) {
        this.categoria = categoria;
        this.equipamento = equipamento;
        this.protecaoEPI = protecaoEPI;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProtecaoEPI getProtecaoEPI() {
        return protecaoEPI;
    }

    public void setProtecaoEPI(ProtecaoEPI protecaoEPI) {
        this.protecaoEPI = protecaoEPI;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

    @Override
    public String toString() {
        return equipamento;
    }
}
