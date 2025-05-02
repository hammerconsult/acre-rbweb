package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Aidfe extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cadastro")
    private CadastroEconomico cadastro;

    @Column(name = "numero")
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Column(name = "solicitadaem")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Solicitado em")
    private Date solicitadaEm;

    @Column(name = "analisadaem")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Analizado em")
    private Date analisadaEm;

    @Column(name = "quantidadesolicitada")
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Quantidade Solicitada")
    private int quantidadeSolicitada;

    @Column(name = "quantidadeDeferida")
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Quantidade Deferida")
    private int quantidadeDeferida;

    @Column(name = "numeroinicial")
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número Inicial")
    private Long numeroInicial;

    @Column(name = "numerofinal")
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número Final")
    private Long numeroFinal;

    @Column(name = "observacaosolicitacao")
    private String observacaoSolicitacao;

    @Column(name = "observacaoanalise")
    private String observacaoAnalise;

    @ManyToOne
    private UsuarioWeb userEmpresa;

    @ManyToOne
    private UsuarioSistema userPrefeitura;

    @Column(name = "situacao")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoAidfe situacao;

    public Aidfe() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
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

    public int getQuantidadeDeferida() {
        return quantidadeDeferida;
    }

    public void setQuantidadeDeferida(int quantidadeDeferida) {
        this.quantidadeDeferida = quantidadeDeferida;
    }

    public Long getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(Long numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public Long getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(Long numeroFinal) {
        this.numeroFinal = numeroFinal;
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

    public CadastroEconomico getCadastro() {
        return cadastro;
    }

    public void setCadastro(CadastroEconomico cadastro) {
        this.cadastro = cadastro;
    }

    public UsuarioWeb getUserEmpresa() {
        return userEmpresa;
    }

    public void setUserEmpresa(UsuarioWeb userEmpresa) {
        this.userEmpresa = userEmpresa;
    }

    public UsuarioSistema getUserPrefeitura() {
        return userPrefeitura;
    }

    public void setUserPrefeitura(UsuarioSistema userPrefeitura) {
        this.userPrefeitura = userPrefeitura;
    }


    public SituacaoAidfe getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAidfe situacao) {
        this.situacao = situacao;
    }

    @Override
    public NfseDTO toNfseDto() {
        AidfeNfseDTO dto = new AidfeNfseDTO();
        dto.setId(this.getId());
        dto.setNumero(getNumero().intValue());

        PrestadorServicoNfseDTO prestadorServicoNfseDTO = new PrestadorServicoNfseDTO(this.getCadastro().getId(),
            this.getCadastro().getInscricaoCadastral(), this.getCadastro().getPessoa().getNome(),
            this.getCadastro().getPessoa().getNomeFantasia(), this.getCadastro().getPessoa().getCpf_Cnpj());
        dto.setPrestadorServicos(prestadorServicoNfseDTO);
        dto.setUsuarioEmpresa((UserNfseDTO) this.getUserEmpresa().toNfseDto());
        dto.setSolicitadoEm(getSolicitadaEm());
        dto.setQuantidade(getQuantidadeSolicitada());
        dto.setQuantidadeDeferida(getQuantidadeDeferida());
        dto.setNumeroInicial(getNumeroInicial());
        dto.setNumeroFinal(getNumeroFinal());
        dto.setObservacao(getObservacaoSolicitacao());
        dto.setSituacaoAIDFE(getSituacao().toSituacaoDTO(getSituacao()));
        dto.setDeferidoEm(getAnalisadaEm());
        dto.setObservacaoDeferimento(getObservacaoAnalise());
        dto.setQuantidadeDeferida(getQuantidadeDeferida());
        return dto;
    }

    public boolean isAidfeAguardando(){
        return SituacaoAidfe.AGUARDANDO.equals(this.getSituacao());
    }
}
