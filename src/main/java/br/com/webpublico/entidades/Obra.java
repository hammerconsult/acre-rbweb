package br.com.webpublico.entidades;


import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.enums.TipoResponsavelFiscal;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author venom
 */
@Entity
@Audited
@Etiqueta(value = "Obras")
public class Obra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta(value = "Nome")
    private String nome;

    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Início de Execução")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioExecucao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Prazo de Entrega")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date prazoEntrega;

    @Obrigatorio
    @ManyToOne
    @Etiqueta(value = "Contrato")
    private Contrato contrato;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Subtipo de Objeto de Compra")
    private SubTipoObjetoCompra subTipoObjetoCompra;

    @Pesquisavel
    @Etiqueta(value = "Cadastro Imobiliário")
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;

    @Invisivel
    @Etiqueta(value = "Latitude")
    private String latitude;

    @Invisivel
    @Etiqueta(value = "Longitude")
    private String longitude;

    @Invisivel
    @Etiqueta(value = "Efetivada")
    private Boolean efetivada;

    @Invisivel
    @OneToMany(mappedBy = "obra", cascade = CascadeType.MERGE)
    private List<ObraAnexo> anexos;

    @Invisivel
    @OneToMany(mappedBy = "obra", cascade = CascadeType.MERGE)
    private List<ObraServico> servicos;

    @Invisivel
    @OneToMany(mappedBy = "obra", cascade = CascadeType.MERGE)
    private List<ObraAnotacao> anotacoes;

    @OneToMany(mappedBy = "obra", cascade = CascadeType.MERGE)
    private List<ObraMedicao> medicoes;

    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObraTecnicoFiscal> tecnicoFiscais;
    /**
     * campo destinado a informar se foi Prestado Contas ou Não da Obra
     */
    @Invisivel
    private Boolean prestadoConta;
    /**
     * campo destinado para identificar se a obra faz parte da loa
     */
    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObraTermo> itemObraTermo;

    @Invisivel
    @Transient
    private List<ExecucaoContratoTipoFonte> dotacoes;

    @ManyToOne
    @Etiqueta(value = "LOA")
    private LOA loa;

    @ManyToOne
    @Etiqueta(value = "Projeto/Atividade")
    private AcaoPPA acaoPPA;


    @Obrigatorio
    @Length(maximo = 255)
    @Pesquisavel
    @Etiqueta("Alcance Social")
    private String alcanceSocial;

    @Obrigatorio
    @Length(maximo = 255)
    @Pesquisavel
    @Etiqueta("Finalidade da Obra")
    private String finalidadeObra;
    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObraSituacao> situacoes;

    public Obra() {
        super();
        this.efetivada = false;
        this.anexos = new ArrayList<>();
        this.servicos = new ArrayList<>();
        this.anotacoes = new ArrayList<>();
        this.medicoes = new ArrayList<>();
        this.tecnicoFiscais = new ArrayList<>();
        this.dotacoes = new ArrayList<>();
        this.itemObraTermo = Lists.newArrayList();
        this.situacoes = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ObraTermo> getItemObraTermo() {
        return itemObraTermo;
    }

    public void setItemObraTermo(List<ObraTermo> itemObraTermo) {
        this.itemObraTermo = itemObraTermo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getInicioExecucao() {
        return inicioExecucao;
    }

    public void setInicioExecucao(Date inicioExecucao) {
        this.inicioExecucao = inicioExecucao;
    }

    public Date getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(Date prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        return subTipoObjetoCompra;
    }

    public void setSubTipoObjetoCompra(SubTipoObjetoCompra subTipoObjetoCompra) {
        this.subTipoObjetoCompra = subTipoObjetoCompra;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }


    public List<ObraMedicao> getMedicoes() {
        return medicoes;
    }

    public void setMedicoes(List<ObraMedicao> medicoes) {
        this.medicoes = medicoes;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Boolean getEfetivada() {
        return efetivada;
    }

    public void setEfetivada(Boolean efetivada) {
        this.efetivada = efetivada;
    }

    public List<ObraAnexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<ObraAnexo> anexos) {
        this.anexos = anexos;
    }

    public List<ObraServico> getServicos() {
        Collections.sort(servicos);
        return servicos;
    }

    public void setServicos(List<ObraServico> servicos) {
        this.servicos = servicos;
    }

    public List<ObraAnotacao> getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(List<ObraAnotacao> anotacoes) {
        this.anotacoes = anotacoes;
    }

    public List<ObraMedicao> getMedicoesObra() {
        return medicoes;
    }

    public List<ObraTecnicoFiscal> getTecnicoFiscais() {
        return tecnicoFiscais;
    }

    public void setTecnicoFiscais(List<ObraTecnicoFiscal> tecnicoFiscais) {
        this.tecnicoFiscais = tecnicoFiscais;
    }

    public Boolean getPrestadoConta() {
        return prestadoConta;
    }

    public void setPrestadoConta(Boolean prestadoConta) {
        this.prestadoConta = prestadoConta;
    }

    public Boolean isPrestadoConta() {
        return prestadoConta == null ? false : true;
    }

    public List<ObraTecnicoFiscal> responsaveisPorTipo(TipoResponsavelFiscal tipo) {
        List<ObraTecnicoFiscal> retorno = new ArrayList<>();
        if (tecnicoFiscais != null && !tecnicoFiscais.isEmpty()) {
            for (ObraTecnicoFiscal obraTecnicoFiscal : tecnicoFiscais) {
                if (tipo.equals(obraTecnicoFiscal.getTecnicoFiscal().getTipoResponsavel())) {
                    retorno.add(obraTecnicoFiscal);
                }
            }
        }
        Collections.sort(retorno);
        return retorno;
    }

    public List<ObraTecnicoFiscal> getTecnicos() {
        return responsaveisPorTipo(TipoResponsavelFiscal.TECNICO);
    }

    public void setTecnicos(List<ObraTecnicoFiscal> tecnicoFiscais) {
//        this.tecnicoFiscais = responsaveisPorTipo(ResponsavelTecnicoFiscal.TipoResponsavel.TECNICO);

        for (ObraTecnicoFiscal obraTecnicoFiscal : tecnicoFiscais) {
            if (TipoResponsavelFiscal.TECNICO.equals(obraTecnicoFiscal.getTecnicoFiscal().getTipoResponsavel())) {
                this.tecnicoFiscais = tecnicoFiscais;
            }
        }
    }

    public List<ObraTecnicoFiscal> getFiscais() {
        return responsaveisPorTipo(TipoResponsavelFiscal.FISCAL);
    }


    public LOA getLoa() {
        return loa;
    }

    public void setLoa(LOA loa) {
        this.loa = loa;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public List<ExecucaoContratoTipoFonte> getDotacoes() {
        return dotacoes;
    }

    public void setDotacoes(List<ExecucaoContratoTipoFonte> dotacoes) {
        this.dotacoes = dotacoes;
    }

    public String getAlcanceSocial() {
        return alcanceSocial;
    }

    public void setAlcanceSocial(String alcanceSocial) {
        this.alcanceSocial = alcanceSocial;
    }

    public String getFinalidadeObra() {
        return finalidadeObra;
    }

    public void setFinalidadeObra(String finalidadeObra) {
        this.finalidadeObra = finalidadeObra;
    }

    public List<ObraSituacao> getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(List<ObraSituacao> situacoes) {
        this.situacoes = situacoes;
    }

    @Override
    public String toString() {
        String retorno = nome;
        if (contrato != null) {
            retorno += " Contrato: " + contrato.toString();
        }
        return retorno;
    }

    public BigDecimal getValorTotalDotacoes() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ObraMedicao medicao : medicoes) {
                total = total.add(medicao.getValorTotal());
            }
            return total;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalExecucaoContrato() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ExecucaoContratoTipoFonte execucao : getDotacoes()) {
                total = total.add(execucao.getValor());
            }
            return total;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalMedicoes() {
        BigDecimal total = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(getMedicoesObra())) {
            for (ObraMedicao medicao : getMedicoesObra()) {
                if (medicao.getValorTotal() != null && medicao.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
                    total = total.add(medicao.getValorTotal());
                }
            }
        }
        return total;
    }
}
