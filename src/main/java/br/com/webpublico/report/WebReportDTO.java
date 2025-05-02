package br.com.webpublico.report;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class WebReportDTO {

    public static final String HEADER_MESSAGE_ERROR = "webreport-error";

    private String nome;
    private UsuarioSistema usuarioSistema;
    private Date solicitadoEm;
    private String uuid;
    private BigDecimal porcentagem;
    private byte[] conteudo;
    private Long inicio;
    private Long fim;
    private Boolean visualizado;
    private String erro;
    private String hash;
    private RelatorioDTO dto;

    public WebReportDTO(String nome, UsuarioSistema usuarioSistema, String uuid, String hash, RelatorioDTO dto) {
        this.nome = nome;
        this.usuarioSistema = usuarioSistema;
        this.uuid = uuid;
        this.solicitadoEm = new Date();
        this.porcentagem = BigDecimal.ZERO;
        this.inicio = System.currentTimeMillis();
        this.visualizado = Boolean.FALSE;
        this.hash = hash;
        this.dto = dto;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeReduzido() {
        return nome != null && nome.length() > 14 ? nome.substring(0, 14) : nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getSolicitadoEm() {
        return solicitadoEm;
    }

    public void setSolicitadoEm(Date solicitadoEm) {
        this.solicitadoEm = solicitadoEm;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getGerado() {
        return conteudo != null;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public BigDecimal getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(BigDecimal porcentagem) {
        this.porcentagem = porcentagem;
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

    public Boolean getVisualizado() {
        return visualizado;
    }

    public void setVisualizado(Boolean visualizado) {
        this.visualizado = visualizado;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public RelatorioDTO getDto() {
        return dto;
    }

    public void setDto(RelatorioDTO dto) {
        this.dto = dto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebReportDTO that = (WebReportDTO) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public Boolean isConcluido() {
        return porcentagem.compareTo(new BigDecimal(100)) == 0;
    }

    public Boolean isDeuErro() {
        return erro != null && !erro.trim().isEmpty();
    }

    public String getImagem() {
        if (isConcluido()) {
            return "pdf-100.png";
        }
        return "pdf-50.png";
    }

    public Long getTimeInSeconds() {
        if (fim != null && inicio != null)
            return (fim - inicio);
        return 0L;
    }

    public String getTempoAsString() {
        long HOUR = TimeUnit.HOURS.toMillis(1);

        Long tempo = getTimeInSeconds();

        if (tempo < HOUR) {
            return String.format("%1$TM:%1$TS%n", tempo.longValue());
        } else {
            return String.format("%d:%2$TM:%2$TS%n", tempo.longValue() / HOUR, tempo.longValue() % HOUR);
        }
    }
}
