package br.com.webpublico.nfse.rest;


import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.nfse.domain.dtos.NotificacaoNfseDTO;
import br.com.webpublico.seguranca.NotificacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

@RequestMapping("/nfse")
@Controller
public class FaleConoscoNfseResource implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(FaleConoscoNfseResource.class);

    @Autowired
    private NotificacaoService notificacaoService;


    @RequestMapping(value = "/fale-conosco/notificar", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<NotificacaoNfseDTO> create(@RequestBody NotificacaoNfseDTO dto) {
        try {
            Notificacao notificacao = new Notificacao();
            notificacao.setTitulo(dto.getTitulo());
            notificacao.setGravidade(Notificacao.Gravidade.valueOf(dto.getGravidade().name()));
            notificacao.setDescricao(dto.getDescricao());
            notificacao.setTipoNotificacao(TipoNotificacao.valueOf(dto.getTipoNotificacao().name()));
            notificacao.setLink(dto.getLink());
            notificacao.setCriadoEm(dto.getCriadoEm());
            notificacaoService.notificar(notificacao);
            return new ResponseEntity(dto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro {}", e);
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
