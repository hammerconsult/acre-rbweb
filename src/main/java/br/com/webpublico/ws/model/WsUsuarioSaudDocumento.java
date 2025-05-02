package br.com.webpublico.ws.model;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.entidades.UsuarioSaudDocumento;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.util.ArquivoUtil;

import java.io.Serializable;

public class WsUsuarioSaudDocumento implements Serializable {

    private Long id;
    private WsParametroSaudDocumento parametroSaudDocumento;
    private ArquivoDTO arquivo;
    private Boolean rejeitado;
    private String observacao;

    public WsUsuarioSaudDocumento() {
        this.rejeitado = Boolean.FALSE;
    }

    public WsUsuarioSaudDocumento(UsuarioSaudDocumento usuarioSaudDocumento, ArquivoFacade arquivoFacade) {
        this.id = usuarioSaudDocumento.getId();
        this.parametroSaudDocumento = new WsParametroSaudDocumento(usuarioSaudDocumento.getParametroSaudDocumento());
        this.arquivo = ArquivoUtil.converterArquivoToArquivoDTO(usuarioSaudDocumento.getDocumento(), arquivoFacade);
        this.rejeitado = UsuarioSaudDocumento.Status.REJEITADO.equals(usuarioSaudDocumento.getStatus());
        this.observacao = usuarioSaudDocumento.getObservacao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WsParametroSaudDocumento getParametroSaudDocumento() {
        return parametroSaudDocumento;
    }

    public void setParametroSaudDocumento(WsParametroSaudDocumento parametroSaudDocumento) {
        this.parametroSaudDocumento = parametroSaudDocumento;
    }

    public ArquivoDTO getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoDTO arquivo) {
        this.arquivo = arquivo;
    }

    public Boolean getRejeitado() {
        return rejeitado;
    }

    public void setRejeitado(Boolean rejeitado) {
        this.rejeitado = rejeitado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
