package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/09/14
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Equipamentos/Máquinas")
@GrupoDiagrama(nome = "Frota")
public class Equipamento extends ObjetoFrota {

    @Pesquisavel
    @Etiqueta("Ano")
    @Tabelavel(ordemApresentacao = 8)
    @Obrigatorio
    private Integer ano;

    @Etiqueta("Horas de Uso Aquisição")
    @Tabelavel(ordemApresentacao = 9)
    @Obrigatorio
    private BigDecimal horasUsoAquisicao;

    @Etiqueta("Horas de Uso")
    @Tabelavel(ordemApresentacao = 10)
    @Obrigatorio
    private BigDecimal horasUso;

    @Invisivel
    @OneToMany(mappedBy = "equipamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramaRevisaoEquipamento> revisoesEquipamento;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "equipamento")
    private List<HoraPercorridaEquipamento> itemHoraPercorrida;

    @ManyToOne
    @Etiqueta("Horas Percorrida")
    private HoraPercorridaEquipamento horaPercorrida;

    public Equipamento() {
        super();
        this.setTipoObjetoFrota(TipoObjetoFrota.EQUIPAMENTO);
        this.revisoesEquipamento = Lists.newArrayList();
    }

    public Equipamento(Equipamento equipamento, HierarquiaOrganizacional hierarquiaOrganizacional) {
        super();
        this.setId(equipamento.getId());
        this.setIdentificacao(equipamento.getIdentificacao());
        this.setDataAquisicao(equipamento.getDataAquisicao());
        this.setTipoAquisicaoObjetoFrota(equipamento.getTipoAquisicaoObjetoFrota());
        this.setBem(equipamento.getBem());
        this.setContrato(equipamento.getContrato());
        this.setDescricao(equipamento.getDescricao());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
        this.setAno(equipamento.getAno());
        this.setHorasUso(equipamento.getHorasUso());
        this.setHorasUsoAquisicao(equipamento.getHorasUsoAquisicao());
        this.setCedido(equipamento.getCedido());
        this.setCedidoPor(equipamento.getCedidoPor());
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public BigDecimal getHorasUso() {
        return horasUso;
    }

    public void setHorasUso(BigDecimal horasUso) {
        this.horasUso = horasUso;
    }

    public BigDecimal getHorasUsoAquisicao() {
        return horasUsoAquisicao;
    }

    public void setHorasUsoAquisicao(BigDecimal horasUsoAquisicao) {
        this.horasUsoAquisicao = horasUsoAquisicao;
    }

    public List<ProgramaRevisaoEquipamento> getRevisoesEquipamento() {
        return revisoesEquipamento;
    }

    public void setRevisoesEquipamento(List<ProgramaRevisaoEquipamento> revisoesEquipamento) {
        this.revisoesEquipamento = revisoesEquipamento;
    }

    public List<HoraPercorridaEquipamento> getItemHoraPercorrida() {
        return itemHoraPercorrida;
    }

    public void setItemHoraPercorrida(List<HoraPercorridaEquipamento> itemHoraPercorrida) {
        this.itemHoraPercorrida = itemHoraPercorrida;
    }

    public HoraPercorridaEquipamento getHoraPercorrida() {
        return horaPercorrida;
    }

    public void setHoraPercorrida(HoraPercorridaEquipamento horaPercorrida) {
        this.horaPercorrida = horaPercorrida;
    }

    @Override
    public String toString() {
        try {
            return getIdentificacao() + " - " + ano + " - " + (this.isCedido() ? " Cedido: " + this.getDescricao() : "") + super.toString();
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
