package br.com.webpublico.entidades.administrativo.frotas;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.administrativo.TipoInspecaoEquipamento;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by carlos on 24/05/17.
 */
@Entity
@Audited
@Etiqueta("Itens de Inspeção de Equipamento")
public class ItemInspecaoEquipamento extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Inspeção de Equipamento")
    private TipoInspecaoEquipamento tipoInspecaoEquipamento;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;

    public ItemInspecaoEquipamento() {
        super();
        this.ativo = Boolean.TRUE;
    }

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

    public TipoInspecaoEquipamento getTipoInspecaoEquipamento() {
        return tipoInspecaoEquipamento;
    }

    public void setTipoInspecaoEquipamento(TipoInspecaoEquipamento tipoInspecaoEquipamento) {
        this.tipoInspecaoEquipamento = tipoInspecaoEquipamento;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
