package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.enums.SituacaoSolicitacaoRPS;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta(value = "Solicitação de RPS")
public class SolicitacaoRPS extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "solicitadaem")
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Solicitado em")
    private Date solicitadaEm;

    @Column(name = "analisadaem")
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Analizado em")
    private Date analisadaEm;

    @Column(name = "quantidadesolicitada")
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Quantidade Solicitada")
    private int quantidadeSolicitada;

    @Column(name = "observacaosolicitacao")
    private String observacaoSolicitacao;

    @Column(name = "observacaoanalise")
    private String observacaoAnalise;

    @ManyToOne
    @NotAudited
    private UsuarioWeb userEmpresa;

    @ManyToOne
    @Tabelavel
    @Etiqueta(value = "Prestador")
    private CadastroEconomico prestador;

    @ManyToOne
    @Tabelavel
    @Etiqueta(value = "Usuário da Análise")
    private UsuarioSistema userPrefeitura;

    @Column(name = "situacao")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoSolicitacaoRPS situacao;

    public SolicitacaoRPS() {
    }


    @Override
    public NfseDTO toNfseDto() {
        SolicitacaoRPSNfseDTO dto = new SolicitacaoRPSNfseDTO();
        dto.setId(this.getId());

        PrestadorServicoNfseDTO prestadorServicoNfseDTO = new PrestadorServicoNfseDTO(this.getPrestador().getId(),
            this.getPrestador().getInscricaoCadastral(), this.getPrestador().getPessoa().getNome(),
            this.getPrestador().getPessoa().getNomeFantasia(), this.getPrestador().getPessoa().getCpf_Cnpj());
        dto.setPrestador(prestadorServicoNfseDTO);
        dto.setUserEmpresa((UserNfseDTO) this.getUserEmpresa().toNfseDto());
        dto.setSolicitadaEm(getSolicitadaEm());
        dto.setQuantidadeSolicitada(getQuantidadeSolicitada());
        dto.setObservacaoAnalise(getObservacaoAnalise());
        dto.setObservacaoSolicitacao(getObservacaoSolicitacao());
        dto.setSituacao(getSituacao().toSituacaoDTO(getSituacao()));
        dto.setAnalisadaEm(getAnalisadaEm());
        return dto;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSolicitadaEm() {
        return solicitadaEm;
    }

    public void setSolicitadaEm(Date solicitadaEm) {
        this.solicitadaEm = solicitadaEm;
    }

    public Date getAnalisadaEm() {
        return analisadaEm;
    }

    public void setAnalisadaEm(Date analisadaEm) {
        this.analisadaEm = analisadaEm;
    }

    public int getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public void setQuantidadeSolicitada(int quantidadeSolicitada) {
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public String getObservacaoSolicitacao() {
        return observacaoSolicitacao;
    }

    public void setObservacaoSolicitacao(String observacaoSolicitacao) {
        this.observacaoSolicitacao = observacaoSolicitacao;
    }

    public String getObservacaoAnalise() {
        return observacaoAnalise;
    }

    public void setObservacaoAnalise(String observacaoAnalise) {
        this.observacaoAnalise = observacaoAnalise;
    }

    public UsuarioWeb getUserEmpresa() {
        return userEmpresa;
    }

    public void setUserEmpresa(UsuarioWeb userEmpresa) {
        this.userEmpresa = userEmpresa;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    public UsuarioSistema getUserPrefeitura() {
        return userPrefeitura;
    }

    public void setUserPrefeitura(UsuarioSistema userPrefeitura) {
        this.userPrefeitura = userPrefeitura;
    }

    public SituacaoSolicitacaoRPS getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoRPS situacao) {
        this.situacao = situacao;
    }

    public boolean isAguardando() {
        return SituacaoSolicitacaoRPS.AGUARDANDO.equals(this.getSituacao());
    }
}
