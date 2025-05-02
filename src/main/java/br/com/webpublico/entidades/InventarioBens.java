package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Patrimonial")
@Entity
@Audited
@Etiqueta("Inventário de Bens Móveis")
public class InventarioBens extends SuperEntidade {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Operação")
    private Date programadoEm;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data de Abertura")
    private Date dataAbertura;

    @Etiqueta("Data de Fechamento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFechamento;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @ManyToOne
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;

    @OneToMany(mappedBy = "inventarioBens", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemInventarioBens> itensInventario;

    @OneToMany(mappedBy = "inventarioBens", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FechamentoInventarioBem> itensFechamentoInventario;

    public InventarioBens() {
        super();
        this.itensInventario = new ArrayList<>();
        this.itensFechamentoInventario = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getProgramadoEm() {
        return programadoEm;
    }

    public void setProgramadoEm(Date programadoEm) {
        this.programadoEm = programadoEm;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Date getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public List<ItemInventarioBens> getItensInventario() {
        return itensInventario;
    }

    public void setItensInventario(List<ItemInventarioBens> itensInventario) {
        this.itensInventario = itensInventario;
    }

    public List<FechamentoInventarioBem> getItensFechamentoInventario() {
        return itensFechamentoInventario;
    }

    public void setItensFechamentoInventario(List<FechamentoInventarioBem> itensFechamentoInventario) {
        this.itensFechamentoInventario = itensFechamentoInventario;
    }

    public Boolean isAbertura() {
        return dataAbertura == null;
    }

    public Boolean isSalvar() {
        return dataAbertura != null && dataFechamento == null;
    }

    public Boolean isFechamento() {
        return dataAbertura != null && dataFechamento == null;
    }

    public Boolean isImprimir() {
        return dataFechamento != null;
    }

    public String getCodigoOuAviso() {
        return codigo != null ? codigo.toString() : "Código gerado automático na abertura do inventário";
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + DataUtil.getDataFormatada(dataAbertura);
        } catch (NullPointerException e) {
            return "";
        }
    }
}
