package br.com.webpublico.negocios.tributario.auxiliares;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroImobiliarioDAO;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.ws.model.DebitoIntegracaoSIT;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;


@Controller
@RequestMapping(value = "/integracao/debitos")
public class ConsultaDebitosSit {

    @Autowired
    private JdbcCadastroImobiliarioDAO dao;


    @RequestMapping(value = "por-lote/", method = RequestMethod.GET)
    @ResponseBody
    public List<DebitoIntegracaoSIT> getDebitos(String setor, String quadra, String lote) {
        try {
            List<DebitoIntegracaoSIT> retorno = Lists.newArrayList();

            String inscricao = "1" + StringUtil.preencheString(setor, 3, '0') +
                    StringUtil.preencheString(quadra, 4, '0') +
                    StringUtil.preencheString(lote, 4, '0');

            List<CadastroImobiliario> lista = dao.lista(inscricao, true, null);
            for (CadastroImobiliario ci : lista) {
                List<ResultadoParcela> resultados  = new ConsultaParcela()
                        .addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, ci.getId())
                        .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO)
                        .addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.IGUAL, false)
                    .executaConsulta().getResultados();
                DebitoIntegracaoSIT debito = new DebitoIntegracaoSIT();
                debito.setLote(lote);
                debito.setSetor(setor);
                debito.setQuadra(quadra);
                debito.setAutonoma(ci.getAutonoma().toString());
                BigDecimal valor = BigDecimal.ZERO;
                if (!resultados.isEmpty()) {
                    debito.setTemRestricao(true);
                    for (ResultadoParcela parcela : resultados) {
                        valor = valor.add(parcela.getValorTotal());
                    }
                    DebitoIntegracaoSIT deb = new DebitoIntegracaoSIT();
                } else {
                    debito.setTemRestricao(false);
                }

                debito.setValorDevido(valor);
                retorno.add(debito);
            }


            return retorno;
        } catch (Exception e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }

}
