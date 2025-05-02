/*
 * Codigo gerado automaticamente em Fri Feb 11 08:42:00 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TextoFixo;
import br.com.webpublico.util.ResultadoValidacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class TextoFacade extends AbstractFacade<TextoFixo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TextoFacade() {
        super(TextoFixo.class);
    }

    public Long recuperaProximoCodigo() {
        Query consulta = em.createQuery("select max(texto.codigo) from TextoFixo texto");
        if (consulta.getSingleResult() != null) {
            return (Long) consulta.getSingleResult() + 1;
        }
        return (long) 1;
    }

    public ResultadoValidacao salva(TextoFixo esteSelecionado) {
        ResultadoValidacao result = validaProcesso(esteSelecionado);
        if (result.isValidado()) {
            try {
                if (esteSelecionado.getId() == null) {
                    super.salvarNovo(esteSelecionado);
                } else {
                    super.salvar(esteSelecionado);
                }
                result.limpaMensagens();
                result.addInfo(null, "Salvo com sucesso!", "");
            } catch (Exception ex) {
                result.limpaMensagens();
                result.addErro(null, "Erro ao tentar salvar o Texto Fixo!", ex.getMessage());
            }
        }
        return result;
    }

    private ResultadoValidacao validaProcesso(TextoFixo selecionado) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        final String summary = "Não foi possível continuar!";
        if (selecionado.getCodigo() == null) {
            resultado.addErro("Formulario:codigo", summary, "O código deve ser informado.");
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().length() <= 0) {
            resultado.addErro("Formulario:descricao", summary, "A descrição deve ser informado.");
        }
        if (selecionado.getTextoFixo() == null || selecionado.getTextoFixo().trim().length() <= 0) {
            resultado.addErro("Formulario:texto", summary, "O texto deve ser informado.");
        }

        return resultado;
    }
}
