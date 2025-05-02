package br.com.webpublico.entidades;

import br.com.webpublico.enums.Perecibilidade;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity
public class Material extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Código")
    @Column(unique = true)
    private Long codigo;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Objeto de Compra")
    private ObjetoCompra objetoCompra;

    @Obrigatorio
    @Etiqueta("Descrição")
    @Length(maximo = 3000)
    private String descricao;

    @Etiqueta("Descrição Complementar")
    private String descricaoComplementar;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @Etiqueta("Grupo de Material")
    @Obrigatorio
    @ManyToOne
    private GrupoMaterial grupo;

    @Etiqueta("Marca")
    @ManyToOne
    private Marca marca;

    @Etiqueta("Modelo")
    @ManyToOne
    private Modelo modelo;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @Etiqueta("Controle de Lote")
    private Boolean controleDeLote;

    @Etiqueta("Material Médico-Hospitalar")
    private Boolean medicoHospitalar;

    @Invisivel
    @Transient
    private Boolean selecionadoNaLista;

    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private StatusMaterial statusMaterial;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Perecibilidade")
    private Perecibilidade perecibilidade;

    @ManyToOne
    @Etiqueta("Efetivação de Material")
    private EfetivacaoMaterial efetivacaoMaterial;

    @ManyToOne
    @Etiqueta("Derivação Componente")
    private DerivacaoObjetoCompraComponente derivacaoComponente;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EfetivacaoMaterial> efetivacoes;


    public Material() {
        this.medicoHospitalar = false;
        this.controleDeLote = false;
        this.statusMaterial = StatusMaterial.AGUARDANDO;
        this.perecibilidade = Perecibilidade.NAO_SE_APLICA;
        this.selecionadoNaLista = Boolean.FALSE;
        efetivacoes = new ArrayList<>();
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Boolean getControleDeLote() {
        return controleDeLote;
    }

    public void setControleDeLote(Boolean controleDeLote) {
        this.controleDeLote = controleDeLote;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public GrupoMaterial getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoMaterial grupo) {
        this.grupo = grupo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public Boolean getMedicoHospitalar() {
        return medicoHospitalar;
    }

    public void setMedicoHospitalar(Boolean medicoHospitalar) {
        this.medicoHospitalar = medicoHospitalar;
    }

    public Boolean getSelecionadoNaLista() {
        return selecionadoNaLista;
    }

    public void setSelecionadoNaLista(Boolean selecionadoNaLista) {
        this.selecionadoNaLista = selecionadoNaLista;
    }

    public StatusMaterial getStatusMaterial() {
        return statusMaterial;
    }

    public void setStatusMaterial(StatusMaterial statusMaterial) {
        this.statusMaterial = statusMaterial;
    }

    public DerivacaoObjetoCompraComponente getDerivacaoComponente() {
        return derivacaoComponente;
    }

    public void setDerivacaoComponente(DerivacaoObjetoCompraComponente derivacaoComponente) {
        this.derivacaoComponente = derivacaoComponente;
    }

    public List<EfetivacaoMaterial> getEfetivacoes() {
        return efetivacoes;
    }

    public void setEfetivacoes(List<EfetivacaoMaterial> efetivacoes) {
        this.efetivacoes = efetivacoes;
    }

    public Perecibilidade getPerecibilidade() {
        return perecibilidade;
    }

    public void setPerecibilidade(Perecibilidade perecibilidade) {
        this.perecibilidade = perecibilidade;
    }

    public String getCodigoDescricaoCurta() {
        descricao = descricao.length() > 70 ? descricao.substring(0, 70) : descricao;
        return codigo + " - " + descricao;
    }

    public String getCodigoDescricao() {
        return codigo + " - " + descricao;
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        try {
            return unidadeMedida.getMascaraQuantidade();
        } catch (Exception e) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        try {
            return unidadeMedida.getMascaraValorUnitario();
        } catch (Exception e) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    public EfetivacaoMaterial getUltimaEfetivacao() {
        if (!Util.isListNullOrEmpty(efetivacoes)) {
            List<EfetivacaoMaterial> list = efetivacoes.stream()
                .sorted(Comparator.comparing(EfetivacaoMaterial::getDataRegistro, Comparator.reverseOrder()))
                .collect(Collectors.toList());
            return list.get(0);
        }
        return null;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public String toStringAutoComplete() {
        String retorno = "";

        retorno += codigo == null ? "" : codigo;
        retorno += descricao == null ? "" : " - " + descricao;
        retorno += (descricaoComplementar == null || "".equals(descricaoComplementar.trim())) ? "" : " - " + descricaoComplementar;
        retorno += (unidadeMedida == null || unidadeMedida.getSigla() == null) ? "" : " - " + unidadeMedida.getSigla();

        return retorno.substring(0, retorno.length() > 80 ? 80 : retorno.length()).toUpperCase();
    }

    public String getRequerLote() {
        return controleDeLote ? "Sim" : "Não";
    }

    public String getDescricaoCurta() {
        if (descricao.length() > 40) {
            return descricao.substring(0, 40) + "...";
        } else {
            return descricao;
        }
    }

}
