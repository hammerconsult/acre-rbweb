/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.AmbitoCnae;
import br.com.webpublico.enums.LicencaCnae;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.CnaeNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.pessoa.dto.CnaeDTO;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author terminal4
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity
@Audited
public class CNAE implements Serializable, NfseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Código CNAE")
    private String codigoCnae;
    @Tabelavel
    @Pesquisavel
    @Column(length = 255)
    @Size(max = 255)
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricaoDetalhada;
    private String migracaoChave;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Grau de Risco")
    private GrauDeRiscoDTO grauDeRisco;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Dispensado")
    private Boolean dispensado;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Sanitário")
    private Boolean fiscalizacaoSanitaria;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Infraestrutura")
    private Boolean seinfra;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ambiental")
    private Boolean meioAmbiente;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Permite Múltiplos Cadastros Mobiliários")
    private Boolean multiplosCMC;
    @Etiqueta("Interesse do Estado")
    private Boolean interesseDoEstado;

    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private Situacao situacao;
    @OneToMany(mappedBy = "cnae", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CNAEServico> servicos;
    @Transient
    private String codigoDescricao;

    public CNAE() {
        super();
        this.multiplosCMC = Boolean.FALSE;
        this.interesseDoEstado = Boolean.FALSE;
        this.servicos = Lists.newArrayList();
    }

    public CNAE(String codigoCnae, String descricaoDetalhada) {
        this.codigoCnae = codigoCnae;
        this.descricaoDetalhada = descricaoDetalhada;
        this.multiplosCMC = Boolean.FALSE;
        this.interesseDoEstado = Boolean.FALSE;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public String getCodigoCnae() {
        return codigoCnae;
    }

    public void setCodigoCnae(String codigoCnae) {
        this.codigoCnae = codigoCnae;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFiscalizacaoSanitaria() {
        if (fiscalizacaoSanitaria == null) {
            return false;
        }
        return fiscalizacaoSanitaria;
    }

    public void setFiscalizacaoSanitaria(Boolean fiscalizacaoSanitaria) {
        this.fiscalizacaoSanitaria = fiscalizacaoSanitaria;
    }

    public GrauDeRiscoDTO getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(GrauDeRiscoDTO grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Boolean getMultiplosCMC() {
        return multiplosCMC != null ? multiplosCMC : false;
    }

    public void setMultiplosCMC(Boolean multiplosCMC) {
        this.multiplosCMC = multiplosCMC;
    }

    public Boolean getDispensado() {
        return dispensado;
    }

    public void setDispensado(Boolean dispensado) {
        this.dispensado = dispensado;
    }

    public Boolean getSeinfra() {
        return seinfra;
    }

    public void setSeinfra(Boolean seinfra) {
        this.seinfra = seinfra;
    }

    public Boolean getMeioAmbiente() {
        return meioAmbiente != null ? meioAmbiente : Boolean.FALSE;
    }

    public void setMeioAmbiente(Boolean meioAmbiente) {
        this.meioAmbiente = meioAmbiente;
    }

    public Boolean getInteresseDoEstado() {
        return interesseDoEstado != null ? interesseDoEstado : Boolean.FALSE;
    }

    public void setInteresseDoEstado(Boolean interesseDoEstado) {
        this.interesseDoEstado = interesseDoEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CNAE)) {
            return false;
        }
        CNAE other = (CNAE) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigoCnae;
    }

    public String getToStringAutoComplete() {
        int tamanho = 100;
        if (codigoCnae != null && descricaoDetalhada != null) {
            return codigoCnae + " - " + (descricaoDetalhada.length() > tamanho ? descricaoDetalhada.substring(0, tamanho) + "..." : descricaoDetalhada);
        } else {
            return codigoCnae;
        }
    }

    public String getDescricaoReduzida() {
        if (descricaoDetalhada != null && descricaoDetalhada.length() > 100) {
            return descricaoDetalhada.substring(0, 100);
        } else {
            return descricaoDetalhada;
        }
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public List<CNAEServico> getServicos() {
        return servicos;
    }

    public void setServicos(List<CNAEServico> servicos) {
        this.servicos = servicos;
    }

    @Override
    public CnaeNfseDTO toNfseDto() {
        return new CnaeNfseDTO(this.id, this.codigoCnae, this.descricaoDetalhada, Situacao.ATIVO.equals(this.situacao), false);
    }

    public CnaeDTO toCnaeDTO() {
        CnaeDTO cnaeDTO = new CnaeDTO();
        cnaeDTO.setCodigo(codigoCnae);
        cnaeDTO.setId(id);
        cnaeDTO.setNome(descricaoDetalhada);
        return cnaeDTO;
    }

    public static void validarParaAdicaoEmLista(CNAE cnae, List<CNAE> cnaes) {
        ValidacaoException ve = new ValidacaoException();
        if (cnae == null || cnae.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um cnae para adicionar");
        } else if (cnaes.contains(cnae)) {
            ve.adicionarMensagemDeCampoObrigatorio("Esse cnae já foi selecionado.");
        }
        ve.lancarException();
    }

    public boolean temServico(Servico servico) {
        if (servicos != null && !servicos.isEmpty()) {
            for (CNAEServico cnaeServico : servicos) {
                if (cnaeServico.getServico().getId().equals(servico.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void adicionarServico(Servico servico) {
        CNAEServico cnaeServico = new CNAEServico();
        cnaeServico.setCnae(this);
        cnaeServico.setServico(servico);
        servicos.add(cnaeServico);
    }

    public String getNivelComplexibilidade() {
        return grauDeRisco != null ? grauDeRisco.getDescricao() : null;
    }

    public String getCodigoDescricao() {
        return (codigoCnae != null ? (codigoCnae + " - ") : "") + descricaoDetalhada + (grauDeRisco != null ? (" - " + grauDeRisco.getDescricao()) : "");
    }

    public void setCodigoDescricao(String codigoDescricao) {
        this.codigoDescricao = codigoDescricao;
    }

    public String getAmbito() {
        if (fiscalizacaoSanitaria != null && fiscalizacaoSanitaria) {
            return AmbitoCnae.SANITARIO.getDescricao();
        } else if (seinfra != null && seinfra) {
            return AmbitoCnae.INFRAESTRUTURA.getDescricao();
        } else if (meioAmbiente != null && meioAmbiente) {
            return AmbitoCnae.AMBIENTAL.getDescricao();
        } else if (interesseDoEstado != null && interesseDoEstado) {
            return "Estadual";
        }
        return "";
    }

    public String getLicenca() {
        if (dispensado != null && dispensado) {
            return LicencaCnae.DISPENSADO.getDescricao();
        }
        return LicencaCnae.DEFERIDO.getDescricao();
    }

    public enum Situacao {
        ATIVO("Ativo"),
        INATIVO("Inativo");

        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
