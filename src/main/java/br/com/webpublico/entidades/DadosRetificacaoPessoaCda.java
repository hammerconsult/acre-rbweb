package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.softplan.enums.NaturezaCDASoftPlan;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "CDA")
@Entity
@Audited
public class DadosRetificacaoPessoaCda implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pessoa pessoa;
    private Long sequencia;
    private Boolean enviado;
    private String nome;
    private String natureza;
    private String cpfCnpj;
    private String ieRg;
    private String orgaoExpedidor;
    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String municipio;
    private String uf;
    private String cep;
    private String principal;
    @OneToMany(mappedBy = "dado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TelefoneDadosRetPessoaCda> telefones;
    @Transient
    private Long criadoEm;

    public DadosRetificacaoPessoaCda() {
        this.criadoEm = System.nanoTime();
        this.telefones = Lists.newArrayList();
        enviado = false;
    }

    public static Pessoa criarRetificacao(Pessoa pessoa) {
        if (pessoa.getId() != null && pessoa.getEnderecoCompletoOuCorrespondencia() != null) {
            DadosRetificacaoPessoaCda dados = new DadosRetificacaoPessoaCda();
            dados.setPessoa(pessoa);
            dados.setSequencia(Long.valueOf(pessoa.getDadosRetificacao().size() + 1l));
            dados.setNome(pessoa.getNome());
            dados.setNatureza(pessoa instanceof PessoaJuridica ? NaturezaCDASoftPlan.J.name() : NaturezaCDASoftPlan.F.name());
            dados.setCpfCnpj(pessoa.getCpf_Cnpj());
            dados.setIeRg(pessoa.getRg_InscricaoEstadual());
            if (pessoa.getOrgaoExpedidor() == null || pessoa.getOrgaoExpedidor().isEmpty()) {
                dados.setOrgaoExpedidor("NAO INFORMADO");
            } else {
                dados.setOrgaoExpedidor(pessoa.getOrgaoExpedidor());
            }
            EnderecoCorreio enderecoCorreio = pessoa.getEnderecoCompletoOuCorrespondencia();
            dados.setTipoLogradouro("RUA");//TODO
            dados.setLogradouro(enderecoCorreio.getLogradouro());
            dados.setNumero(enderecoCorreio.getNumero());
            dados.setComplemento(enderecoCorreio.getComplemento());
            dados.setBairro(enderecoCorreio.getBairro());
            dados.setMunicipio(enderecoCorreio.getLocalidade());
            dados.setUf(enderecoCorreio.getUf());
            dados.setCep(enderecoCorreio.getCep() != null ? enderecoCorreio.getCep().replace("-", "").replace(".", "") : "");
            dados.setPrincipal(enderecoCorreio.getPrincipal() ? "0" : "1");
            List<Telefone> telefones = Lists.newArrayList();
            telefones.add(pessoa.getTelefonePorTipo(TipoTelefone.COMERCIAL));
            telefones.add(pessoa.getTelefonePorTipo(TipoTelefone.RESIDENCIAL));
            int seq = 0;
            for (Telefone fone : telefones) {
                if (fone != null) {
                    TelefoneDadosRetPessoaCda telefone = new TelefoneDadosRetPessoaCda();
                    telefone.setDado(dados);
                    telefone.setSequencia(Integer.valueOf(++seq).longValue());
                    telefone.setNumero(fone.getSomenteTelefone());
                    telefone.setTipo(fone.getTipoFone());
                    dados.getTelefones().add(telefone);
                }
            }

            pessoa.getDadosRetificacao().add(dados);
        }
        return pessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getIeRg() {
        return ieRg != null ? ieRg.length() > 15 ? ieRg.substring(0, 14) : ieRg : " ";
    }

    public void setIeRg(String ieRg) {
        this.ieRg = ieRg;
    }

    public String getOrgaoExpedidor() {
        return orgaoExpedidor;
    }

    public void setOrgaoExpedidor(String orgaoExpedidor) {
        this.orgaoExpedidor = orgaoExpedidor;
    }

    public String getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getLogradouro() {
        return logradouro != null ? logradouro.trim() : "";
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero != null ? numero.length() > 12 ? numero.substring(0, 11) : numero : " ";
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento != null ? complemento.length() > 12 ? complemento.substring(0, 11) : complemento : " ";
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro != null ? bairro.trim() : "";
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        if (cep != null) {
            String cepLimpo = cep.replace("-", "").replace(".", "");
            return cepLimpo.length() > 8 ? cepLimpo.substring(0, 7) : cepLimpo;
        }
        return "";
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public List<TelefoneDadosRetPessoaCda> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<TelefoneDadosRetPessoaCda> telefones) {
        this.telefones = telefones;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

}
