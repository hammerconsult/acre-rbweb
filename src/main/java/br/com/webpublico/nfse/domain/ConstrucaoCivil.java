package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.domain.dtos.ConstrucaoCivilNfseDTO;
import br.com.webpublico.nfse.domain.dtos.ConstrucaoCivilProfissionalNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * A ConstrucaoCivil.
 */
@Entity
@Audited
public class ConstrucaoCivil extends SuperEntidade implements Serializable, NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Prestador")
    @ManyToOne
    private CadastroEconomico cadastroEconomico;

    @Obrigatorio
    @Etiqueta("Responsavel")
    @ManyToOne
    private Pessoa responsavel;

    @Obrigatorio
    @Etiqueta("Localização")
    @OneToOne(cascade = CascadeType.ALL)
    private ConstrucaoCivilLocalizacao construcaoCivilLocalizacao;

    private Long codigoObra;

    @Obrigatorio
    @Etiqueta("Data da Aprovação")
    @Temporal(TemporalType.DATE)
    private Date dataAprovacaoProjeto;

    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    @Temporal(TemporalType.DATE)
    private Date dataConclusao;

    @Obrigatorio
    @Etiqueta("ART")
    private String art;

    @Obrigatorio
    @Etiqueta("N° Alvará")
    private String numeroAlvara;

    private Boolean incorporacao;

    private String matriculaImovel;

    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;

    private String numeroHabitese;

    @Obrigatorio
    @Etiqueta("Status")
    @Enumerated(EnumType.STRING)
    private ConstrucaoCivilStatus construcaoCivilStatus;

    @OneToMany(mappedBy = "construcaoCivil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConstrucaoCivilProfissional> profissionais;


    public ConstrucaoCivil() {
        super();
        incorporacao = Boolean.FALSE;
    }

    public ConstrucaoCivil(ConstrucaoCivilNfseDTO dto) {
        this.id = dto.getId();
        this.responsavel = null;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Pessoa getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public ConstrucaoCivilLocalizacao getConstrucaoCivilLocalizacao() {
        return construcaoCivilLocalizacao;
    }

    public void setConstrucaoCivilLocalizacao(ConstrucaoCivilLocalizacao construcaoCivilLocalizacao) {
        this.construcaoCivilLocalizacao = construcaoCivilLocalizacao;
    }

    public Long getCodigoObra() {
        return codigoObra;
    }

    public void setCodigoObra(Long codigoObra) {
        this.codigoObra = codigoObra;
    }

    public Date getDataAprovacaoProjeto() {
        return dataAprovacaoProjeto;
    }

    public void setDataAprovacaoProjeto(Date dataAprovacaoProjeto) {
        this.dataAprovacaoProjeto = dataAprovacaoProjeto;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getNumeroAlvara() {
        return numeroAlvara;
    }

    public void setNumeroAlvara(String numeroAlvara) {
        this.numeroAlvara = numeroAlvara;
    }

    public Boolean getIncorporacao() {
        return incorporacao;
    }

    public void setIncorporacao(Boolean incorporacao) {
        this.incorporacao = incorporacao;
    }

    public String getMatriculaImovel() {
        return matriculaImovel;
    }

    public void setMatriculaImovel(String matriculaImovel) {
        this.matriculaImovel = matriculaImovel;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public String getNumeroHabitese() {
        return numeroHabitese;
    }

    public void setNumeroHabitese(String numeroHabitese) {
        this.numeroHabitese = numeroHabitese;
    }

    public ConstrucaoCivilStatus getConstrucaoCivilStatus() {
        return construcaoCivilStatus;
    }

    public void setConstrucaoCivilStatus(ConstrucaoCivilStatus construcaoCivilStatus) {
        this.construcaoCivilStatus = construcaoCivilStatus;
    }

    public List<ConstrucaoCivilProfissional> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(List<ConstrucaoCivilProfissional> profissionais) {
        this.profissionais = profissionais;
    }

    @Override
    public ConstrucaoCivilNfseDTO toNfseDto() {
        return new ConstrucaoCivilNfseDTO(id,
            cadastroEconomico != null ? cadastroEconomico.toNfseDto() : null,
            responsavel != null ? responsavel.toNfseDto() : null,
            construcaoCivilLocalizacao != null ? construcaoCivilLocalizacao.toNfseDto() : null,
            codigoObra,
            dataAprovacaoProjeto,
            dataInicio,
            dataConclusao,
            art,
            numeroAlvara,
            incorporacao,
            matriculaImovel,
            numeroHabitese,
            cadastroImobiliario != null ? cadastroImobiliario.toNfseDto() : null,
            construcaoCivilStatus != null ? construcaoCivilStatus.toDTO() : null,
            criarProfissionaisToDTO());
    }

    public ConstrucaoCivilNfseDTO toSimpleNfseDto() {
        ConstrucaoCivilNfseDTO dto = new ConstrucaoCivilNfseDTO();
        dto.setId(this.id);
        dto.setIncorporacao(this.incorporacao);
        dto.setArt(this.art);
        dto.setCodigoObra(this.codigoObra);
        dto.setNumeroAlvara(this.numeroAlvara);
        return dto;
    }

    private List<ConstrucaoCivilProfissionalNfseDTO> criarProfissionaisToDTO() {
        List<ConstrucaoCivilProfissionalNfseDTO> toReturn = Lists.newArrayList();
        if (profissionais != null) {
            for (ConstrucaoCivilProfissional profissional : profissionais) {
                toReturn.add(profissional.toNfseDto());
            }
        }
        return toReturn;
    }

    @Override
    public void realizarValidacoes() throws ValidacaoException {
        super.realizarValidacoes();
        if (construcaoCivilLocalizacao != null) {
            construcaoCivilLocalizacao.validarCamposObrigatorios();
        }
    }
}
