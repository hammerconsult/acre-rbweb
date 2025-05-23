package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.LogSistema;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoMensagemValidacao;
import br.com.webpublico.listeners.ControladorDeMensagens;
import br.com.webpublico.util.DataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Controller
@RequestMapping(value = "/mensagens")
@Scope("request")
public class MensagensControlador implements Serializable {

    @Autowired
    HttpServletRequest httpRequest;


    @RequestMapping(value = "/marcar-mensagens-lidas", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void marcarMensagensComoLidas() {
        String urlAtual = httpRequest.getHeader("url");

        // Quando as mensagens são emitidas para a própria URL
        httpRequest.getSession().setAttribute(urlAtual, Lists.<LogSistema>newArrayList());

        // Quando as mensagens são emitidas para OUTRA URL
        HashMap<String, String> caminhos = (HashMap) httpRequest.getSession().getAttribute(ControladorDeMensagens.CAMINHOS_NAVEGADOS);

        if (caminhos == null) {
            return;
        }

        String origem = caminhos.get(urlAtual);
        if (origem == null) {
            return;
        }

        httpRequest.getSession().setAttribute(origem, Lists.<LogSistema>newArrayList());
    }

    @RequestMapping(value = "/buscar-mensagens-nao-lidas", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<LogSistema>> buscarMensagensNaoLidas() throws IOException {
        String urlAtual = httpRequest.getHeader("url");

        // Quando as mensagens são emitidas para a própria URL
        List<LogSistema> mensagensNaoLidas = (List<LogSistema>) httpRequest.getSession().getAttribute(urlAtual);
        if (mensagensNaoLidas == null) {
            mensagensNaoLidas = Lists.newArrayList();
        }
        if (mensagensNaoLidas != null && !mensagensNaoLidas.isEmpty()) {
            return getStringJsonDasMensagens(urlAtual, mensagensNaoLidas);
        }

        // Quando as mensagens são emitidas para OUTRA URL
        HashMap<String, String> caminhos = (HashMap) httpRequest.getSession().getAttribute(ControladorDeMensagens.CAMINHOS_NAVEGADOS);
        if (caminhos == null) {
            return new ResponseEntity<>(mensagensNaoLidas, HttpStatus.OK);
        }

        String origem = caminhos.get(urlAtual);
        if (origem == null) {
            return new ResponseEntity<>(mensagensNaoLidas, HttpStatus.OK);
        }

        mensagensNaoLidas = (List<LogSistema>) httpRequest.getSession().getAttribute(origem);

        return getStringJsonDasMensagens(origem, mensagensNaoLidas);
    }

    private ResponseEntity<List<LogSistema>>  getStringJsonDasMensagens(String urlAtual, List<LogSistema> mensagensNaoLidas) throws IOException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(mensagensNaoLidas);

        httpRequest.getSession().setAttribute(urlAtual, Lists.<LogSistema>newArrayList());

        return new ResponseEntity<>(mensagensNaoLidas, HttpStatus.OK);
    }
}
