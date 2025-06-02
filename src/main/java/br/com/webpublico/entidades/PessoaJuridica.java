/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEmpresa;
import br.com.webpublico.enums.TipoInscricao;
import br.com.webpublico.enums.TipoPorte;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.PessoaNfseDTO;
import br.com.webpublico.pessoa.dto.PessoaJuridicaDTO;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.UtilEntidades;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Audited
@Entity
@Etiqueta("Pessoa Jurídica")
public class PessoaJuridica extends Pessoa implements Serializable {

    @Obrigatorio
    @Column(length = 150, name = "razaoSocial")
    @Etiqueta("Razão Social")
    @Tabelavel
    @Pesquisavel
    private String razaoSocial;
    @Column(length = 40, name = "nomereduzido")
    @Etiqueta("Nome Reduzido")
    @Obrigatorio
    @Pesquisavel
    private String nomeReduzido;
    @Column(length = 45, name = "nomefantasia")
    @Tabelavel
    @Etiqueta("Nome Fantasia")
    @Pesquisavel
    private String nomeFantasia;
    @Column(unique = true, name = "cnpj")
    @Tabelavel
    @Etiqueta("CNPJ")
    @Pesquisavel
    @Obrigatorio
    private String cnpj;
    @Column(length = 25, name = "inscricaoestadual")
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Inscrição Estadual")
    private String inscricaoEstadual;
    @Enumerated(EnumType.STRING)
    private TipoEmpresa tipoEmpresa;
    @Enumerated(EnumType.STRING)
    private TipoInscricao tipoInscricao;
    private String cei;
    @OneToMany(mappedBy = "pessoaJuridica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SociedadePessoaJuridica> sociedadePessoaJuridica;

    // REDESIM
    private String nire;
    private String descricaoObjeto;
    private BigDecimal capitalSocial;
    @Temporal(TemporalType.DATE)
    private Date inicioAtividade;
    private BigDecimal capitalIntegralizado;
    private BigDecimal valorCota;
    @Enumerated(EnumType.STRING)
    private TipoPorte porte;
    @OneToMany(mappedBy = "pessoaJuridica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JuntaComercialPessoaJuridica> juntaComercial;
    @OneToMany(mappedBy = "pessoaJuridica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilialPessoaJuridica> filiais;
    @OneToMany(mappedBy = "pessoaJuridica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoAlteracaoRedeSim> historicosIntegracaoRedeSim;
    @Temporal(TemporalType.DATE)
    private Date ultimaIntegracaoRedeSim;
    @ManyToOne
    private NaturezaJuridica naturezaJuridica;
    private String inscricaoMunicipal;
    @Etiqueta("Código de isenção/imunidade")
    private Integer codigoIsencaoImunidade;

    public PessoaJuridica() {
        super();
        this.sociedadePessoaJuridica = Lists.newArrayList();

        this.juntaComercial = Lists.newArrayList();
        this.filiais = Lists.newArrayList();
        this.historicosIntegracaoRedeSim = Lists.newArrayList();
    }

    public PessoaJuridica(Long id, String nome) {
        super(id);
        this.razaoSocial = nome;
    }

    public PessoaJuridica(String email, String homePage, Nacionalidade nacionalidade, List<EnderecoCorreio> enderecos, List<Telefone> telefones, EscritorioContabil escritorioContabil, String razaoSocial, String nomeReduzido, String nomeFantasia, String cnpj, String inscricaoEstadual, String observacao) {
        super(email, homePage, nacionalidade, enderecos, telefones, escritorioContabil, observacao);
        this.razaoSocial = razaoSocial;
        this.nomeReduzido = nomeReduzido;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public Date getUltimaIntegracaoRedeSim() {
        return ultimaIntegracaoRedeSim;
    }

    public void setUltimaIntegracaoRedeSim(Date ultimaIntegracaoRedeSim) {
        this.ultimaIntegracaoRedeSim = ultimaIntegracaoRedeSim;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return UtilEntidades.formatarCnpj(cnpj);
    }

    public String getCnpjSemFormatacao() {
        return StringUtil.removeCaracteresEspeciaisSemEspaco(cnpj);
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public List<SociedadePessoaJuridica> getSociedadePessoaJuridica() {
        return sociedadePessoaJuridica;
    }

    public void setSociedadePessoaJuridica(List<SociedadePessoaJuridica> sociedadePessoaJuridica) {
        this.sociedadePessoaJuridica = sociedadePessoaJuridica;
    }

    @Override
    public String getNome() {
        return razaoSocial != null ? razaoSocial : " ";
    }

    @Override
    public String getNomeTratamento() {
        return "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getCnpj() != null && !getCnpj().trim().equals("")) {
            sb.append(getCnpj());
            sb.append(" - ");
        }

        if (getNome() != null && !getNome().trim().equals("")) {
            sb.append(getNome());
        }

        return sb.toString();
    }

    @Override
    public String getCpf_Cnpj() {
        return getCnpj() != null ? getCnpj() : " ";
    }

    @Override
    public String getRg_InscricaoEstadual() {
        return getInscricaoEstadual();
    }

    @Override
    public String getOrgaoExpedidor() {
        return "";
    }

    @Override
    public TipoEmpresa getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(TipoEmpresa tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public TipoInscricao getTipoInscricao() {
        return tipoInscricao;
    }

    public void setTipoInscricao(TipoInscricao tipoInscricao) {
        this.tipoInscricao = tipoInscricao;
    }

    public String getCei() {
        return cei;
    }

    public void setCei(String cei) {
        this.cei = cei;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    @Override
    public int compareTo(Pessoa o) {
        try {
            return this.getNome().compareTo(o.getNome());
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isTipoEmpresaNormal() {
        return this.getTipoEmpresa().equals(TipoEmpresa.NORMAL);
    }

    @Override
    public PessoaNfseDTO toNfseDto() {
        PessoaNfseDTO pessoaNfseDTO = (PessoaNfseDTO) super.toNfseDto();
        pessoaNfseDTO.getDadosPessoais().setNomeFantasia(getNomeFantasia());
        try {
            if (Strings.isNullOrEmpty(pessoaNfseDTO.getDadosPessoais().getTelefone())) {
                Telefone fone = getTelefonePorTipo(TipoTelefone.COMERCIAL);
                if (fone != null) {
                    pessoaNfseDTO.getDadosPessoais().setTelefone(fone.getTelefone());
                }
            }
        } catch (Exception e) {
            log.debug("Não foi possível criar as dependencias LAzy da PessoaNfseDTO");
        }
        return pessoaNfseDTO;
    }


    public String getNire() {
        return nire;
    }

    public void setNire(String nire) {
        this.nire = nire;
    }

    public String getDescricaoObjeto() {
        return descricaoObjeto;
    }

    public void setDescricaoObjeto(String descricaoObjeto) {
        this.descricaoObjeto = descricaoObjeto;
    }

    public BigDecimal getCapitalSocial() {
        return capitalSocial;
    }

    public void setCapitalSocial(BigDecimal capitalSocial) {
        this.capitalSocial = capitalSocial;
    }

    public Date getInicioAtividade() {
        return inicioAtividade;
    }

    public void setInicioAtividade(Date inicioAtividade) {
        this.inicioAtividade = inicioAtividade;
    }

    public BigDecimal getCapitalIntegralizado() {
        return capitalIntegralizado;
    }

    public void setCapitalIntegralizado(BigDecimal capitalIntegralizado) {
        this.capitalIntegralizado = capitalIntegralizado;
    }

    public BigDecimal getValorCota() {
        return valorCota;
    }

    public void setValorCota(BigDecimal valorCota) {
        this.valorCota = valorCota;
    }

    public TipoPorte getPorte() {
        return porte;
    }

    public void setPorte(TipoPorte porte) {
        this.porte = porte;
    }

    public List<JuntaComercialPessoaJuridica> getJuntaComercial() {
        return juntaComercial;
    }

    public void setJuntaComercial(List<JuntaComercialPessoaJuridica> juntaComercial) {
        this.juntaComercial = juntaComercial;
    }

    public List<FilialPessoaJuridica> getFiliais() {
        return filiais;
    }

    public void setFiliais(List<FilialPessoaJuridica> filiais) {
        this.filiais = filiais;
    }

    public List<HistoricoAlteracaoRedeSim> getHistoricosIntegracaoRedeSim() {
        if (historicosIntegracaoRedeSim == null) {
            historicosIntegracaoRedeSim = Lists.newArrayList();
        }
        return historicosIntegracaoRedeSim;
    }

    public void setHistoricosIntegracaoRedeSim(List<HistoricoAlteracaoRedeSim> historicosIntegracaoRedeSim) {
        this.historicosIntegracaoRedeSim = historicosIntegracaoRedeSim;
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public void adicionarHistoricoRedeSimPessoa() {
        HistoricoAlteracaoRedeSim historico = new HistoricoAlteracaoRedeSim();
        historico.setPessoaJuridica(this);
        historico.setDescricao("Portal do Contribuinte");
        historico.setDataAlteracao(new Date());
        this.getHistoricosIntegracaoRedeSim().add(historico);
    }

    public static PessoaJuridica dtoToPessoaJuridica(PessoaJuridicaDTO pessoaJuridicaDTO) {
        PessoaJuridica pj = new PessoaJuridica();
        pj.setId(pessoaJuridicaDTO.getId());
        return pj;
    }

    public PessoaJuridicaDTO toPessoaJuridicaDTO() {
        PessoaJuridica pessoaJuridica = this;
        PessoaJuridicaDTO dto = new PessoaJuridicaDTO();
        dto.setId(pessoaJuridica.getId());
        dto.setCnpj(pessoaJuridica.getCnpj());
        dto.setNome(pessoaJuridica.getNome());
        dto.setNomeFantasia(pessoaJuridica.getNomeFantasia());
        dto.setInicioAtividade(pessoaJuridica.getInicioAtividade());
        dto.setPorte(pessoaJuridica.getPorte() != null ? br.com.webpublico.pessoa.enumeration.TipoPorte.valueOf(pessoaJuridica.getPorte().name()) : null);
        dto.setNumeroInscricaoEstadual(pessoaJuridica.getInscricaoEstadual());
        dto.setCapitalSocial(pessoaJuridica.getCapitalSocial());
        dto.setCapitalIntegralizado(pessoaJuridica.getCapitalIntegralizado());
        dto.setValorQuota(pessoaJuridica.getValorCota());
        dto.setEnderecoCorreio(EnderecoCorreio.toEnderecoCorreioDTO(pessoaJuridica.getEnderecoPrincipal()));
        dto.setEmail(pessoaJuridica.getEmail());
        dto.setTelefones(Telefone.toTelefones(pessoaJuridica.getTelefones()));
        dto.setContasBancarias(ContaCorrenteBancPessoa.toContaCorrenteBancariaDTOs(pessoaJuridica.getContaCorrenteBancPessoas()));
        dto.setCei(pessoaJuridica.getCei());
        return dto;
    }

    public br.com.webpublico.tributario.dto.PessoaJuridicaDTO toPessoaJuridicaDTOTributario(){
        PessoaJuridica pessoaJuridica = this;

        br.com.webpublico.tributario.dto.PessoaJuridicaDTO dto = new br.com.webpublico.tributario.dto.PessoaJuridicaDTO();

        dto.setId(pessoaJuridica.getId());
        dto.setCnpj(pessoaJuridica.getCnpj());
        dto.setNome(pessoaJuridica.getNome());
        dto.setNomeFantasia(pessoaJuridica.getNomeFantasia());
        dto.setInicioAtividade(pessoaJuridica.getInicioAtividade());
        dto.setPorte(pessoaJuridica.getPorte() != null ? br.com.webpublico.pessoa.enumeration.TipoPorte.valueOf(pessoaJuridica.getPorte().name()) : null);
        dto.setNumeroInscricaoEstadual(pessoaJuridica.getInscricaoEstadual());
        dto.setCapitalSocial(pessoaJuridica.getCapitalSocial());
        dto.setCapitalIntegralizado(pessoaJuridica.getCapitalIntegralizado());
        dto.setValorQuota(pessoaJuridica.getValorCota());
        dto.setEnderecoCorreio(EnderecoCorreio.toEnderecoCorreioDTOTributario(pessoaJuridica.getEnderecoPrincipal()));
        dto.setEmail(pessoaJuridica.getEmail());
        dto.setTelefones(Telefone.toTelefonesTributario(pessoaJuridica.getTelefones()));

        return dto;
    }

    public Integer getCodigoIsencaoImunidade() {
        return codigoIsencaoImunidade;
    }

    public void setCodigoIsencaoImunidade(Integer codigoIsencaoImunidade) {
        this.codigoIsencaoImunidade = codigoIsencaoImunidade;
    }
}
