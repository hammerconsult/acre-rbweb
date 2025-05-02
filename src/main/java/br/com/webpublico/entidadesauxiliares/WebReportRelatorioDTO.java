package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.util.Arrays;

public class WebReportRelatorioDTO {

    private String uuid;
    private String nome;
    private String url;
    private String usuario;
    private BigDecimal porcentagem;
    private String conteudo;
    private String mimeType;
    private Long inicio;
    private Long fim;
    private String containerName;
    private Long threadId;
    private String status;
    private String observacao;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(BigDecimal porcentagem) {
        this.porcentagem = porcentagem;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getInicio() {
        return inicio;
    }

    public void setInicio(Long inicio) {
        this.inicio = inicio;
    }

    public Long getFim() {
        return fim;
    }

    public void setFim(Long fim) {
        this.fim = fim;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getTimeInSeconds() {
        if (fim != null && inicio != null)
            return (fim - inicio);
        return 0L;
    }

    public String getExtensao() {
        return Arrays.stream(TipoRelatorioDTO.values())
            .filter(tr -> tr.getMimeType().equals(getMimeType()))
            .findFirst()
            .orElse(TipoRelatorioDTO.PDF).getExtension();
    }

    public String getNomeArquivo() {
        return getNome() + getExtensao();
    }
}
