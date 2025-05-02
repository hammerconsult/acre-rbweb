/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaVeiculoObjetoFrota;
import br.com.webpublico.enums.TipoCaminhao;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Veículo")
public class Veiculo extends ObjetoFrota {

    @Obrigatorio
    @Etiqueta("Categoria")
    @Tabelavel(ordemApresentacao = 5)
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private CategoriaVeiculoObjetoFrota categoria;

    @Etiqueta("Tipo ")
    @Pesquisavel
    @Invisivel
    @Enumerated(EnumType.STRING)
    private TipoCaminhao tipoCaminhao;

    @Obrigatorio
    @Etiqueta("Placa")
    @Tabelavel(ordemApresentacao = 6)
    @Pesquisavel
    private String placa;

    @Obrigatorio
    @Etiqueta("Ano/Fabricação")
    @Tabelavel(ordemApresentacao = 7)
    @Pesquisavel
    private Integer anoFabricacao;

    @Obrigatorio
    @Etiqueta("Cor")
    @Pesquisavel
    @ManyToOne
    private Cor cor;

    @Etiqueta("Km Aquisição")
    @Obrigatorio
    @Pesquisavel
    private BigDecimal kmAquisicao;

    //    Km utilizado somente para o cadastrar registro do veículo
    @Pesquisavel
    @Etiqueta("Km Atual")
    @Obrigatorio
    private BigDecimal kmAtual;

    @Pesquisavel
    @Etiqueta("Km Final Garantia")
    private BigDecimal kmFimGarantia;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "veiculo")
    private List<ProgramaRevisaoVeiculo> programaRevisao;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "veiculo")
    private List<KmPercorridoVeiculo> kmPercorridos;

    @ManyToOne
    @Etiqueta("Km Percorrido")
    private KmPercorridoVeiculo kmPercorrido;

    public Veiculo() {
        super();
        this.setTipoObjetoFrota(TipoObjetoFrota.VEICULO);
        this.kmPercorridos = Lists.newArrayList();
    }

    //    Utilizado na pesquisa genérica
    public Veiculo(Veiculo veiculo, HierarquiaOrganizacional hierarquiaOrganizacional) {
        super();
        this.setId(veiculo.getId());
        this.setIdentificacao(veiculo.getIdentificacao());
        this.setDataAquisicao(veiculo.getDataAquisicao());
        this.setCategoria(veiculo.getCategoria());
        this.setTipoAquisicaoObjetoFrota(veiculo.getTipoAquisicaoObjetoFrota());
        this.setBem(veiculo.getBem());
        this.setContrato(veiculo.getContrato());
        this.setPlaca(veiculo.getPlaca());
        this.setDescricao(veiculo.getDescricao());
        this.setAnoFabricacao(veiculo.getAnoFabricacao());
        this.setCor(veiculo.getCor());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
        this.setCedido(veiculo.getCedido());
        this.setCedidoPor(veiculo.getCedidoPor());
    }

    public List<KmPercorridoVeiculo> getKmPercorridos() {
        return kmPercorridos;
    }

    public void setKmPercorridos(List<KmPercorridoVeiculo> kmPercorridos) {
        this.kmPercorridos = kmPercorridos;
    }

    public KmPercorridoVeiculo getKmPercorrido() {
        return kmPercorrido;
    }

    public void setKmPercorrido(KmPercorridoVeiculo kmPercorrido) {
        this.kmPercorrido = kmPercorrido;
    }

    public BigDecimal getKmAquisicao() {
        return kmAquisicao;
    }

    public void setKmAquisicao(BigDecimal kmAquisicao) {
        this.kmAquisicao = kmAquisicao;
    }

    public BigDecimal getKmAtual() {
        return kmAtual;
    }

    public void setKmAtual(BigDecimal kmAtual) {
        this.kmAtual = kmAtual;
    }

    public BigDecimal getKmFimGarantia() {
        return kmFimGarantia;
    }

    public void setKmFimGarantia(BigDecimal kmFimGarantia) {
        this.kmFimGarantia = kmFimGarantia;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public List<ProgramaRevisaoVeiculo> getProgramaRevisao() {
        if (programaRevisao != null) {
            Collections.sort(programaRevisao, new Comparator<ProgramaRevisaoVeiculo>() {
                @Override
                public int compare(ProgramaRevisaoVeiculo o1, ProgramaRevisaoVeiculo o2) {
                    return o2.getDataLancamento().compareTo(o1.getDataLancamento());
                }
            });
        }
        return programaRevisao;
    }

    public void setProgramaRevisao(List<ProgramaRevisaoVeiculo> programaRevisao) {
        this.programaRevisao = programaRevisao;
    }

    public Integer getAnoFabricacao() {
        return anoFabricacao;
    }

    public void setAnoFabricacao(Integer anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    public CategoriaVeiculoObjetoFrota getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaVeiculoObjetoFrota categoria) {
        this.categoria = categoria;
    }

    public TipoCaminhao getTipoCaminhao() {
        return tipoCaminhao;
    }

    public void setTipoCaminhao(TipoCaminhao tipoCaminhao) {
        this.tipoCaminhao = tipoCaminhao;
    }

    @Override
    public String toString() {
        return getIdentificacao() + " - " + placa + " - " + (this.isCedido() ? "Cedido - " + this.getDescricao() : "") + super.toString();
    }
}
