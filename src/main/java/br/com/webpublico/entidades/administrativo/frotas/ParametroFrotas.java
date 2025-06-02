package br.com.webpublico.entidades.administrativo.frotas;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.TipoObjetoFrotaGrupoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Wellington on 04/02/2016.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Frota")
public class ParametroFrotas extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Dias da Taxa à Vencer")
    @Tabelavel
    @Pesquisavel
    private Integer diasDaTaxaAVencer;

    @Obrigatorio
    @Etiqueta("Dias da Revisão à Vencer")
    @Tabelavel
    @Pesquisavel
    private Integer diasDaRevisaoAVencer;

    @Obrigatorio
    @Etiqueta("Quilômetros da Revisão à Vencer")
    @Tabelavel
    @Pesquisavel
    @Numerico
    private BigDecimal quilometrosDaRevisaoAVencer;

    @ManyToOne
    private GrupoMaterial grupoMaterial;

    @Etiqueta("Horas de Revisão à Vencer")
    @Tabelavel
    @Obrigatorio
    private BigDecimal segundosRevisaoAVencer;

    @Obrigatorio
    @Etiqueta("Dias para retirada de veículo/equipamento")
    @Tabelavel
    @Pesquisavel
    private Integer diasRetiradaVeiculoEquipamento;

    @Invisivel
    @OneToMany(mappedBy = "parametroFrotas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoObjetoFrotaGrupoBem> gruposPatrimoniais;

    @Invisivel
    @OneToMany(mappedBy = "parametroFrotas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroFrotasHierarquia> itemParametroFrotasHierarquia;

    public ParametroFrotas() {
        gruposPatrimoniais = Lists.newArrayList();
        itemParametroFrotasHierarquia = Lists.newArrayList();
        segundosRevisaoAVencer = BigDecimal.ZERO;
    }

    public ParametroFrotas(ParametroFrotas parametroFrotas) {
        this.id = parametroFrotas.getId();
        this.diasDaTaxaAVencer = parametroFrotas.getDiasDaTaxaAVencer();
        this.diasDaRevisaoAVencer = parametroFrotas.getDiasDaRevisaoAVencer();
        this.quilometrosDaRevisaoAVencer = parametroFrotas.getQuilometrosDaRevisaoAVencer();
        this.grupoMaterial = parametroFrotas.getGrupoMaterial();
        this.segundosRevisaoAVencer = parametroFrotas.getSegundosRevisaoAVencer();
        this.diasRetiradaVeiculoEquipamento = parametroFrotas.getDiasRetiradaVeiculoEquipamento();
    }

    public List<TipoObjetoFrotaGrupoBem> getGruposPatrimoniais() {
        return gruposPatrimoniais;
    }

    public void setGruposPatrimoniais(List<TipoObjetoFrotaGrupoBem> gruposPatrimoniais) {
        this.gruposPatrimoniais = gruposPatrimoniais;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDiasDaTaxaAVencer() {
        return diasDaTaxaAVencer;
    }

    public void setDiasDaTaxaAVencer(Integer diasDaTaxaAVencer) {
        this.diasDaTaxaAVencer = diasDaTaxaAVencer;
    }

    public Integer getDiasDaRevisaoAVencer() {
        return diasDaRevisaoAVencer != null ? diasDaRevisaoAVencer : 0;
    }

    public void setDiasDaRevisaoAVencer(Integer diasDaRevisaoAVencer) {
        this.diasDaRevisaoAVencer = diasDaRevisaoAVencer;
    }

    public List<ParametroFrotasHierarquia> getItemParametroFrotasHierarquia() {
        return itemParametroFrotasHierarquia;
    }

    public void setItemParametroFrotasHierarquia(List<ParametroFrotasHierarquia> itemParametroFrotasHierarquia) {
        this.itemParametroFrotasHierarquia = itemParametroFrotasHierarquia;
    }

    public BigDecimal getQuilometrosDaRevisaoAVencer() {
        return quilometrosDaRevisaoAVencer;
    }

    public void setQuilometrosDaRevisaoAVencer(BigDecimal quilometrosDaRevisaoAVencer) {
        this.quilometrosDaRevisaoAVencer = quilometrosDaRevisaoAVencer;
    }

    public BigDecimal getSegundosRevisaoAVencer() {
        return segundosRevisaoAVencer != null ? segundosRevisaoAVencer : BigDecimal.ZERO;
    }

    public void setSegundosRevisaoAVencer(BigDecimal segundosRevisaoAVencer) {
        this.segundosRevisaoAVencer = segundosRevisaoAVencer;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public Integer getDiasRetiradaVeiculoEquipamento() {
        return diasRetiradaVeiculoEquipamento;
    }

    public void setDiasRetiradaVeiculoEquipamento(Integer diasRetiradaVeiculoEquipamento) {
        this.diasRetiradaVeiculoEquipamento = diasRetiradaVeiculoEquipamento;
    }

    @Override
    public String toString() {
        try {
            return "";
        } catch (ValidacaoException ve) {
            return "";
        }
    }
}
