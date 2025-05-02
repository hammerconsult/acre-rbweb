package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrestadorServicoNfseDTO implements NfseDTO {

    private Long id;
    private String inscricaoMunicipal;
    private PessoaNfseDTO pessoa;
    private List<br.com.webpublico.nfse.domain.dtos.SocioNfseDTO> socios = Lists.newArrayList();
    private List<br.com.webpublico.nfse.domain.dtos.ServicoNfseDTO> servicos = Lists.newArrayList();
    private List<br.com.webpublico.nfse.domain.dtos.CnaeNfseDTO> cnaes = Lists.newArrayList();
    private List<br.com.webpublico.nfse.domain.dtos.UserNfseDTO> usuarios = Lists.newArrayList();
    private EnquadramentoFiscalNfseDTO enquadramentoFiscal;
    private ArquivoDTO imagem;
    private Boolean emiteNfs;
    private Boolean instituicaoFinanceira;
    private Boolean enviaEmailNfseTomador;
    private Boolean enviaEmailNfseSocios;
    private Boolean enviaEmailNfseContador;
    private Boolean enviaEmailCancelaNfseTomador;
    private Boolean enviaEmailCancelaNfseSocios;
    private Boolean enviaEmailCancelaNfseContador;
    private String nomeParaContato;
    private String telefoneParaContato;
    private String resumoSobreEmpresa;
    private EscritorioContabilNfseDTO escritorioContabil;
    private NaturezaJuridicaNfseDTO naturezaJuridica;
    private List<ReceitaTributariaBrutaNfseDTO> receitasTributariaBruta;
    private List<ReceitaTributariaBrutaNfseDTO> ultimasReceitasTributariaBruta;
    private BigDecimal rbt12;
    private Date dataCredenciamentoISSOnline;
    private String chaveAcesso;
    private Boolean criarUsuario;
    private String password;
    private String situacao;

    public PrestadorServicoNfseDTO() {
        criarUsuario = Boolean.FALSE;
    }

    public PrestadorServicoNfseDTO(Long id, String inscricaoMunicipal, String nome, String nomeFantasianomeFantasia, String cpfCnpj) {
        this();
        this.id = id;
        this.inscricaoMunicipal = inscricaoMunicipal;
        this.pessoa = new PessoaNfseDTO(null, new br.com.webpublico.nfse.domain.dtos.DadosPessoaisNfseDTO());
        this.pessoa.getDadosPessoais().setCpfCnpj(cpfCnpj);
        this.pessoa.getDadosPessoais().setNomeRazaoSocial(nome);
        this.pessoa.getDadosPessoais().setNomeFantasia(nomeFantasianomeFantasia);
    }

    public PrestadorServicoNfseDTO(Long id, String inscricaoMunicipal, String nome,
                                   String nomeFantasianomeFantasia, String cpfCnpj, br.com.webpublico.nfse.domain.dtos.EnquadramentoFiscalNfseDTO enquadramentoFiscal) {
        this(id, inscricaoMunicipal, nome, nomeFantasianomeFantasia, cpfCnpj);
        this.enquadramentoFiscal = enquadramentoFiscal;
    }

    public PrestadorServicoNfseDTO(Long id, String inscricaoMunicipal, PessoaNfseDTO pessoa) {
        this.id = id;
        this.inscricaoMunicipal = inscricaoMunicipal;
        this.pessoa = pessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public PessoaNfseDTO getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaNfseDTO pessoa) {
        this.pessoa = pessoa;
    }

    public List<br.com.webpublico.nfse.domain.dtos.SocioNfseDTO> getSocios() {
        return socios;
    }

    public void setSocios(List<br.com.webpublico.nfse.domain.dtos.SocioNfseDTO> socios) {
        this.socios = socios;
    }

    public List<br.com.webpublico.nfse.domain.dtos.ServicoNfseDTO> getServicos() {
        return servicos;
    }

    public void setServicos(List<br.com.webpublico.nfse.domain.dtos.ServicoNfseDTO> servicos) {
        this.servicos = servicos;
    }

    public List<br.com.webpublico.nfse.domain.dtos.CnaeNfseDTO> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<br.com.webpublico.nfse.domain.dtos.CnaeNfseDTO> cnaes) {
        this.cnaes = cnaes;
    }

    public EnquadramentoFiscalNfseDTO getEnquadramentoFiscal() {
        return enquadramentoFiscal;
    }

    public void setEnquadramentoFiscal(EnquadramentoFiscalNfseDTO enquadramentoFiscal) {
        this.enquadramentoFiscal = enquadramentoFiscal;
    }

    public Boolean getEmiteNfs() {
        return emiteNfs;
    }

    public void setEmiteNfs(Boolean emiteNfs) {
        this.emiteNfs = emiteNfs;
    }

    public Boolean getInstituicaoFinanceira() {
        return instituicaoFinanceira;
    }

    public void setInstituicaoFinanceira(Boolean instituicaoFinanceira) {
        this.instituicaoFinanceira = instituicaoFinanceira;
    }

    public ArquivoDTO getImagem() {
        return imagem;
    }

    public void setImagem(ArquivoDTO imagem) {
        this.imagem = imagem;
    }

    public Boolean getEnviaEmailNfseTomador() {
        return enviaEmailNfseTomador;
    }

    public void setEnviaEmailNfseTomador(Boolean enviaEmailNfseTomador) {
        this.enviaEmailNfseTomador = enviaEmailNfseTomador;
    }

    public Boolean getEnviaEmailNfseContador() {
        return enviaEmailNfseContador;
    }

    public void setEnviaEmailNfseContador(Boolean enviaEmailNfseContador) {
        this.enviaEmailNfseContador = enviaEmailNfseContador;
    }

    public Boolean getEnviaEmailCancelaNfseTomador() {
        return enviaEmailCancelaNfseTomador;
    }

    public void setEnviaEmailCancelaNfseTomador(Boolean enviaEmailCancelaNfseTomador) {
        this.enviaEmailCancelaNfseTomador = enviaEmailCancelaNfseTomador;
    }

    public Boolean getEnviaEmailCancelaNfseContador() {
        return enviaEmailCancelaNfseContador;
    }

    public void setEnviaEmailCancelaNfseContador(Boolean enviaEmailCancelaNfseContador) {
        this.enviaEmailCancelaNfseContador = enviaEmailCancelaNfseContador;
    }

    public String getNomeParaContato() {
        return nomeParaContato;
    }

    public void setNomeParaContato(String nomeParaContato) {
        this.nomeParaContato = nomeParaContato;
    }

    public String getTelefoneParaContato() {
        return telefoneParaContato;
    }

    public void setTelefoneParaContato(String telefoneParaContato) {
        this.telefoneParaContato = telefoneParaContato;
    }

    public String getResumoSobreEmpresa() {
        return resumoSobreEmpresa;
    }

    public void setResumoSobreEmpresa(String resumoSobreEmpresa) {
        this.resumoSobreEmpresa = resumoSobreEmpresa;
    }

    public EscritorioContabilNfseDTO getEscritorioContabil() {
        return escritorioContabil;
    }

    public void setEscritorioContabil(EscritorioContabilNfseDTO escritorioContabil) {
        this.escritorioContabil = escritorioContabil;
    }

    public List<br.com.webpublico.nfse.domain.dtos.UserNfseDTO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<br.com.webpublico.nfse.domain.dtos.UserNfseDTO> usuarios) {
        this.usuarios = usuarios;
    }

    public Boolean getEnviaEmailNfseSocios() {
        return enviaEmailNfseSocios;
    }

    public void setEnviaEmailNfseSocios(Boolean enviaEmailNfseSocios) {
        this.enviaEmailNfseSocios = enviaEmailNfseSocios;
    }

    public Boolean getEnviaEmailCancelaNfseSocios() {
        return enviaEmailCancelaNfseSocios;
    }

    public void setEnviaEmailCancelaNfseSocios(Boolean enviaEmailCancelaNfseSocios) {
        this.enviaEmailCancelaNfseSocios = enviaEmailCancelaNfseSocios;
    }

    public NaturezaJuridicaNfseDTO getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridicaNfseDTO naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public List<ReceitaTributariaBrutaNfseDTO> getReceitasTributariaBruta() {
        return receitasTributariaBruta;
    }

    public void setReceitasTributariaBruta(List<ReceitaTributariaBrutaNfseDTO> receitasTributariaBruta) {
        this.receitasTributariaBruta = receitasTributariaBruta;
    }

    public List<ReceitaTributariaBrutaNfseDTO> getUltimasReceitasTributariaBruta() {
        return ultimasReceitasTributariaBruta;
    }

    public void setUltimasReceitasTributariaBruta(List<ReceitaTributariaBrutaNfseDTO> ultimasReceitasTributariaBruta) {
        this.ultimasReceitasTributariaBruta = ultimasReceitasTributariaBruta;
    }

    public BigDecimal getRbt12() {
        return rbt12;
    }

    public void setRbt12(BigDecimal rbt12) {
        this.rbt12 = rbt12;
    }

    public Date getDataCredenciamentoISSOnline() {
        return dataCredenciamentoISSOnline;
    }

    public void setDataCredenciamentoISSOnline(Date dataCredenciamentoISSOnline) {
        this.dataCredenciamentoISSOnline = dataCredenciamentoISSOnline;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public Boolean getCriarUsuario() {
        return criarUsuario;
    }

    public void setCriarUsuario(Boolean criarUsuario) {
        this.criarUsuario = criarUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        return "PrestadorServicoNfseDTO{" +
            "id=" + id +
            ", inscricaoMunicipal='" + inscricaoMunicipal + '\'' +
            ", pessoa=" + pessoa +
            '}';
    }

    @JsonIgnore
    public boolean hasServico(ServicoNfseDTO servico) {
        if (servico == null) {
            return false;
        }
        for (ServicoNfseDTO servicoCadastro : this.servicos) {
            if (servicoCadastro.getId().equals(servico.getId())) {
                return true;
            }
        }
        return false;
    }
}
