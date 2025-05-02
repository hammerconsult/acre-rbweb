package br.com.webpublico.entidades;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Audited
public class ConstrucaoAlvara extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "construcaoAlvara", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CaracteristicasAlvaraConstrucao> caracteristicas;
    @ManyToOne
    private Construcao construcao;
    @Transient
    private Map<Atributo, ValorAtributo> atributos;
    private String descricao;
    private BigDecimal areaConstruida;
    private Integer quantidadePavimentos;

    public ConstrucaoAlvara() {
        atributos = Maps.newHashMap();
    }

    public ConstrucaoAlvara(Construcao construcao) {
        this.descricao = construcao.getDescricao();
        this.areaConstruida = BigDecimal.valueOf(construcao.getAreaConstruida());
        this.quantidadePavimentos = construcao.getQuantidadePavimentos();
        this.caracteristicas = Lists.newArrayList();
        this.construcao = construcao;
        for (CaracteristicasConstrucao caracteristicasConstrucao : construcao.getCaracteristicasConstrucao()) {
            CaracteristicasAlvaraConstrucao caracteristica = new CaracteristicasAlvaraConstrucao();
            caracteristica.setConstrucaoAlvara(this);
            caracteristica.setAtributo(caracteristicasConstrucao.getAtributo());
            ValorAtributo valorAtributo = new ValorAtributo();
            caracteristica.setValorAtributo(valorAtributo);
            valorAtributo.setAtributo(caracteristicasConstrucao.getValorAtributo().getAtributo());
            valorAtributo.setValorDiscreto(caracteristicasConstrucao.getValorAtributo().getValorDiscreto());
            valorAtributo.setValorDate(caracteristicasConstrucao.getValorAtributo().getValorDate());
            valorAtributo.setValorDecimal(caracteristicasConstrucao.getValorAtributo().getValorDecimal());
            valorAtributo.setValorInteiro(caracteristicasConstrucao.getValorAtributo().getValorInteiro());
            valorAtributo.setValorString(caracteristicasConstrucao.getValorAtributo().getValorString());
            this.caracteristicas.add(caracteristica);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida == null ? BigDecimal.ZERO : areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public List<CaracteristicasAlvaraConstrucao> getCaracteristicas() {
        if (caracteristicas == null) {
            caracteristicas = Lists.newArrayList();
        }
        return caracteristicas;
    }

    public void setCaracteristicas(List<CaracteristicasAlvaraConstrucao> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public void setAtributos(Map<Atributo, ValorAtributo> atributos) {
        this.atributos = atributos;
    }

    public Map<Atributo, ValorAtributo> getAtributos() {
        if (atributos == null || atributos.keySet().size() <= 0) {
            atributos = new HashMap<>();
            if (caracteristicas != null) {
                for (CaracteristicasAlvaraConstrucao carac : caracteristicas) {
                    atributos.put(carac.getAtributo(), carac.getValorAtributo());
                }
            }
        }

        return atributos;
    }

    public Integer getQuantidadePavimentos() {
        return quantidadePavimentos == null ? 0 : quantidadePavimentos;
    }

    public void setQuantidadePavimentos(Integer quantidadePavimentos) {
        this.quantidadePavimentos = quantidadePavimentos;
    }
}
