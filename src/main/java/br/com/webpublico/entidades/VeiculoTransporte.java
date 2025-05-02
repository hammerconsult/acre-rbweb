/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Veículo de Transporte")
public class VeiculoTransporte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Placa")
    private String placa;
    @Etiqueta("Ano de Fabricação")
    private Integer anoFabricacao;
    @Etiqueta("Ano do Modelo")
    private Integer anoModelo;
    @Etiqueta("Chassi")
    private String chassi;
    @Etiqueta("Nota Fiscal")
    private String notaFiscal;
    @Etiqueta("Espécie")
    private String especie;
    @Etiqueta("CAP/POT/CL")
    private String cappotcl;
    @Etiqueta("Tipo do Veículo")
    @ManyToOne
    private TipoVeiculo tipoVeiculo;
    @Etiqueta("Modelo")
    @ManyToOne
    private Modelo modelo;
    @Etiqueta("Cor")
    @ManyToOne
    private Cor cor;
    @Etiqueta("Combustível")
    @ManyToOne
    private Combustivel combustivel;
    @Etiqueta("Cidade")
    @ManyToOne
    private Cidade cidade;
    //    @Etiqueta("Veículo do Permissionário")
//    @OneToMany(mappedBy = "veiculoTransporte")
//    private List<VeiculoPermissionario> veiculosPermissionario;
    private String migracaoChave;
    @Invisivel
    @Transient
    private Long criadoEm;

    public VeiculoTransporte() {
        this.criadoEm = System.nanoTime();
        this.combustivel = new Combustivel();
        this.cor = new Cor();
        this.modelo = new Modelo();
        this.cidade = new Cidade();
        this.tipoVeiculo = new TipoVeiculo();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnoFabricacao() {
        return anoFabricacao;
    }

    public void setAnoFabricacao(Integer anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public Integer getAnoModelo() {
        return anoModelo;
    }

    public void setAnoModelo(Integer anoModelo) {
        this.anoModelo = anoModelo;
    }

    public String getCappotcl() {
        return cappotcl;
    }

    public void setCappotcl(String cappotcl) {
        this.cappotcl = cappotcl;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }


    public String getChassi() {
        return chassi;
    }

    public void setChassi(String chassi) {
        this.chassi = chassi;
    }

    public Combustivel getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(Combustivel combustivel) {
        this.combustivel = combustivel;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(String notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        if (placa != null) {
            placa = placa.replaceAll("-", "");
            placa = placa.toUpperCase();
        }
        this.placa = placa.toUpperCase();
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "VeiculoTransporte.id = " + this.id;
    }
}
