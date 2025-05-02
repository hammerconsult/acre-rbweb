package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class VeiculoSaud extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Motorista")
    private MotoristaSaud motoristaSaud;
    private Boolean ativo;
    @Obrigatorio
    private String placa;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Veiculo")
    private TipoVeiculo tipoVeiculo;
    @ManyToOne
    @Obrigatorio
    private Marca marca;
    @ManyToOne
    @Obrigatorio
    private Cor cor;
    @Obrigatorio
    private String modelo;
    @Obrigatorio
    private Double km;
    @Obrigatorio
    @Etiqueta("Ano de Fabricação")
    private Integer anoFabricacao;
    @Obrigatorio
    @Etiqueta("Capacidade de Passageiros")
    private Integer capacidadePassageiros;
    @OneToMany(mappedBy = "veiculoSaud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VistoriaVeiculoSaud> vistorias;
    @OneToOne(cascade = CascadeType.ALL)
    @Obrigatorio
    @Etiqueta("CRLV")
    private Arquivo crlv;


    public VeiculoSaud() {
        super();
        this.ativo = Boolean.TRUE;
        this.dataCadastro = new Date();
        this.vistorias = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public MotoristaSaud getMotoristaSaud() {
        return motoristaSaud;
    }

    public void setMotoristaSaud(MotoristaSaud motoristaSaud) {
        this.motoristaSaud = motoristaSaud;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Double getKm() {
        return km;
    }

    public void setKm(Double km) {
        this.km = km;
    }

    public Integer getAnoFabricacao() {
        return anoFabricacao;
    }

    public void setAnoFabricacao(Integer anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public Integer getCapacidadePassageiros() {
        return capacidadePassageiros;
    }

    public void setCapacidadePassageiros(Integer capacidadePassageiros) {
        this.capacidadePassageiros = capacidadePassageiros;
    }

    public List<VistoriaVeiculoSaud> getVistorias() {
        return vistorias;
    }

    public void setVistorias(List<VistoriaVeiculoSaud> vistorias) {
        this.vistorias = vistorias;
    }

    public Arquivo getCrlv() {
        return crlv;
    }

    public void setCrlv(Arquivo crlv) {
        this.crlv = crlv;
    }

    public enum TipoVeiculo {
        VAN("Van"),
        MICRO_ONIBUS("Micro-Ônibus");

        private final String descricao;

        TipoVeiculo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
