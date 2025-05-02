package br.com.webpublico.entidadesauxiliares;

public class UsuarioSistemaDTO {
    private String login;
    private boolean podeAlterarData;
    private String nome;
    private String nomeResumido;
    private Long id;
    private String unidadeAdmLogada;
    private Long idUnidadeAdmLogada;
    private String hierarquiaAdmLogada;
    private Long idHierarquiaAdmLogada;
    private String unidadeOrcLogada;
    private Long idUnidadeOrcLogada;
    private String hierarquiaOrcLogada;
    private Long idHieraraquiaOrcLogada;
    private String dataOperacao;

    public UsuarioSistemaDTO() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnidadeAdmLogada() {
        return unidadeAdmLogada;
    }

    public void setUnidadeAdmLogada(String unidadeAdmLogada) {
        this.unidadeAdmLogada = unidadeAdmLogada;
    }

    public Long getIdUnidadeAdmLogada() {
        return idUnidadeAdmLogada;
    }

    public void setIdUnidadeAdmLogada(Long idUnidadeAdmLogada) {
        this.idUnidadeAdmLogada = idUnidadeAdmLogada;
    }

    public String getUnidadeOrcLogada() {
        return unidadeOrcLogada;
    }

    public void setUnidadeOrcLogada(String unidadeOrcLogada) {
        this.unidadeOrcLogada = unidadeOrcLogada;
    }

    public Long getIdUnidadeOrcLogada() {
        return idUnidadeOrcLogada;
    }

    public void setIdUnidadeOrcLogada(Long idUnidadeOrcLogada) {
        this.idUnidadeOrcLogada = idUnidadeOrcLogada;
    }

    public String getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(String dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getNomeResumido() {
        return nomeResumido;
    }

    public void setNomeResumido(String nomeResumido) {
        this.nomeResumido = nomeResumido;
    }

    public String getHierarquiaAdmLogada() {
        return hierarquiaAdmLogada;
    }

    public void setHierarquiaAdmLogada(String hierarquiaAdmLogada) {
        this.hierarquiaAdmLogada = hierarquiaAdmLogada;
    }

    public Long getIdHierarquiaAdmLogada() {
        return idHierarquiaAdmLogada;
    }

    public void setIdHierarquiaAdmLogada(Long idHierarquiaAdmLogada) {
        this.idHierarquiaAdmLogada = idHierarquiaAdmLogada;
    }

    public String getHierarquiaOrcLogada() {
        return hierarquiaOrcLogada;
    }

    public void setHierarquiaOrcLogada(String hierarquiaOrcLogada) {
        this.hierarquiaOrcLogada = hierarquiaOrcLogada;
    }

    public Long getIdHieraraquiaOrcLogada() {
        return idHieraraquiaOrcLogada;
    }

    public void setIdHieraraquiaOrcLogada(Long idHieraraquiaOrcLogada) {
        this.idHieraraquiaOrcLogada = idHieraraquiaOrcLogada;
    }

    public boolean isPodeAlterarData() {
        return podeAlterarData;
    }

    public void setPodeAlterarData(boolean podeAlterarData) {
        this.podeAlterarData = podeAlterarData;
    }
}
