package br.com.webpublico.negocios.rh.pccr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Felipe Marinzeck
 */
@Controller
@Scope("request")
public class VinculoFPLeitor implements Serializable {

    @Autowired
    private VinculoFPLeitorFacade vinculoFPLeitorFacade;


    @RequestMapping(value = "/ler-vinculofp/teste", method = RequestMethod.GET)
    @ResponseBody
    public String teste() {
        return "TEste";
    }


    @RequestMapping(value = "/ler-vinculofp/descricao", method = RequestMethod.GET)
    @ResponseBody
    public Object getVinculoFP(@RequestParam(value = "login") String login,
                               @RequestParam(value = "matricula") String matricula,
                               @RequestParam(value = "numero") String numero,
                               @RequestParam(value = "componente") String componente,
                               @RequestParam(value = "uo-id") Long idUnidadeOrganizacional,
                               @RequestParam(value = "data-referencia") Date dataReferencia,
                               @RequestParam(value = "classe") String classe,
                               @RequestParam(value = "validarPermissao") Boolean validarPermissao) {
        Object[] obj = (Object[]) vinculoFPLeitorFacade.recuperarVinculo(matricula, numero, classe);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("componente", componente);
        if (obj == null) {
            map.put("mensagem", "Não foram localizadas informações para os parâmetros digitados ("+matricula+"/"+numero+")");
            return map;
        }

        map.put("id", obj[0]);
        String complemento = obj[4].equals("Aposentadoria") ? " ("+obj[4]+")" : "";
        map.put("descricao", obj[1]+"/"+obj[2]+" - "+obj[3] + complemento);
        map.put("mensagem", null);

        if (validarPermissao == null || !validarPermissao){
            return map;
        }

        if (!vinculoFPLeitorFacade.isVinculoFPVigenteEmDataOperacao(Long.parseLong(""+obj[0]), dataReferencia)){
            map.put("id", null);
            map.put("mensagem", "Os parâmetros informados ("+matricula+"/"+numero+") correspondem a um vinculo não vigente.");
            return map;
        }

        if (!vinculoFPLeitorFacade.usuarioPossuiAcessoAoVinculo(login, Long.parseLong(""+obj[0]), idUnidadeOrganizacional, dataReferencia)){
            map.put("id", null);
            map.put("mensagem", "Os parâmetros informados ("+matricula+"/"+numero+") correspondem a um vinculo que pertence a outra unidade.");
            return map;
        }

        return map;
    }

    @RequestMapping(value = "/ler-vinculofp/vinculofp-ativo/", method = RequestMethod.GET)
    @ResponseBody
    public Object isVinculoFPAtivoEm(@RequestParam(value = "vinculofp-id") Long id,
                                     @RequestParam(value = "data-referencia") Date dataReferencia) {
        return vinculoFPLeitorFacade.isVinculoFPVigenteEmDataOperacao(id, dataReferencia);
    }

    @RequestMapping(value = "/ler-vinculofp/usuario-possui-acesso-vinculo/", method = RequestMethod.GET)
    @ResponseBody
    public Object isUsuarioComPermissaoParaVinculo(@RequestParam(value = "login") String login,
                                                   @RequestParam(value = "vinculofp-id") Long idVinculo,
                                                   @RequestParam(value = "uo-id") Long idUnidadeOrganizacional,
                                                   @RequestParam(value = "data-referencia") Date dataReferencia) {
        return vinculoFPLeitorFacade.usuarioPossuiAcessoAoVinculo(login, idVinculo, idUnidadeOrganizacional, dataReferencia);
    }
}
