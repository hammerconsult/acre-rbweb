package br.com.webpublico.negocios.tributario.auxiliares;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping(value = "/ler-iptu")
public class LeitorMetodosCalculadorIPTU {


    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getItensParaContruirMenu() {
        return CalculadorIPTU.getMetodosDisponiveis();
    }

}
