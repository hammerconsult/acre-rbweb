package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoAtoLegal;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TermoColaboradorEventualFacade;
import br.com.webpublico.util.ConverterPessoa;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;


import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 24/08/14
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "termoColaboradorEventualControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-termo-colaborador-eventual", pattern = "/termo-colaborador-eventual/novo/", viewId = "/faces/financeiro/concessaodediarias/termo-colaborador-eventual/edita.xhtml"),
        @URLMapping(id = "editar-termo-colaborador-eventual", pattern = "/termo-colaborador-eventual/editar/#{termoColaboradorEventualControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/termo-colaborador-eventual/edita.xhtml"),
        @URLMapping(id = "ver-termo-colaborador-eventual", pattern = "/termo-colaborador-eventual/ver/#{termoColaboradorEventualControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/termo-colaborador-eventual/visualizar.xhtml"),
        @URLMapping(id = "listar-termo-colaborador-eventual", pattern = "/termo-colaborador-eventual/listar/", viewId = "/faces/financeiro/concessaodediarias/termo-colaborador-eventual/lista.xhtml")
})
public class TermoColaboradorEventualControlador extends PrettyControlador<TermoColaboradorEventual> implements Serializable, CRUD {

    @EJB
    private TermoColaboradorEventualFacade termoColaboradorEventualFacade;
    private ConverterPessoa converterPessoa;
    private String conteudo;

    public TermoColaboradorEventualControlador() {
        super(TermoColaboradorEventual.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return termoColaboradorEventualFacade;
    }

    @URLAction(mappingId = "novo-termo-colaborador-eventual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        SistemaControlador sistemaControlador = getSistemaControlador();
        selecionado.setUnidade(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setGeradoEm(sistemaControlador.getDataOperacao());
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "ver-termo-colaborador-eventual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-termo-colaborador-eventual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/termo-colaborador-eventual/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<Pessoa> completaPessoa(String parte) {
        return termoColaboradorEventualFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return termoColaboradorEventualFacade.getAtoLegalFacade().listaFiltrandoAtoLegalPorTipoExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente(), TipoAtoLegal.DECRETO);
    }

    public ConverterPessoa getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterPessoa(termoColaboradorEventualFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public void prepararConteudo() {
        try {
            conteudo = montaConteudo();
            //System.out.println(conteudo);
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    private String montaConteudo() {
        Pessoa pessoa = null;
        if (selecionado.getPessoa() instanceof PessoaFisica) {
            pessoa = termoColaboradorEventualFacade.getPessoaFacade().getPessoaFisicaFacade().recuperar(selecionado.getPessoa().getId());
        } else if (selecionado.getPessoa() instanceof PessoaJuridica) {
            pessoa = termoColaboradorEventualFacade.getPessoaFacade().getPessoaJuridicaFacade().recuperar(selecionado.getPessoa().getId());
        }
        EnderecoCorreio enderecoPrincipal = pessoa.getEnderecoPrincipal();
        if (enderecoPrincipal == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um endereço cadastrado.");
        }
        Telefone telefonePrincipal = pessoa.getTelefonePrincipal();
        if (telefonePrincipal == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um telefone cadastrado.");
        }

        StringBuilder string = new StringBuilder();
        string.append("<?xml version='1.0' encoding='iso-8859-1'?>");
        string.append("<!DOCTYPE HTML PUBLIC \\HTML 4.01 Transitional//PT\\ \\http://www.w3.org/TR/html4/loose.dtd\\>");
        string.append("<html> ");
        string.append("<div style='text-align:center'>  ");
        string.append("<img src=\"http://172.16.0.154:8080/webpublico/img/Brasao_de_Rio_Branco.gif\" alt=\\PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\\ /> </br>  ");
        string.append("<b> PREFEITURA DE RIO BRANCO </br>  ");
        string.append(" " + selecionado.getUnidade().getDescricao() + " </b> ");
        string.append("</div>  ");
        string.append("</br>");
        string.append("<div style='text-align:center'>  ");
        string.append("<b> DECRETO Nº ____ DE ______ DE ________ DE 2014 </b> ");
        string.append("</div> ");
        string.append("<br/> <br/>");
        string.append("<p align=\"justify\">");
        string.append("Pelo presente Termo de Compromisso, eu, " + negrito(pessoa.getNome()) + ", ");
        string.append("RG/CI nº " + negrito(pessoa.getRg_InscricaoEstadual()) + ", CPF nº " + negrito(pessoa.getCpf_Cnpj()) + ", ");
        string.append("residente e domiciliado " + negrito(enderecoPrincipal.getLogradouro()) + ", Bairro " + negrito(enderecoPrincipal.getBairro()) + ", ");
        string.append("CEP " + negrito(enderecoPrincipal.getCep()) + ",Cidade " + negrito(enderecoPrincipal.getLocalidade()) + ", ");
        string.append("Estado " + negrito(enderecoPrincipal.getUf()) + ", telefone " + negrito(telefonePrincipal.getTelefone()) + ", e-mail " + negrito(pessoa.getEmail()) + ", ");
        string.append("na qualidade de colaborador eventual (ou profissional contratado) para a ");
        string.append("execução do _____________________________________________________________________________________ ");
        string.append("___________________________________________________________________, firmo, ");
        string.append("perante o Município de Rio Branco, Capital do Estado do Acre, representado ");
        string.append("pela ____________________________________________________, o presente Termo, pelo qual:");
        string.append("</p>");
        string.append("<br/>");
        string.append("<p align=\"justify\">");
        string.append(" <b> I -</b> assumo o compromisso e a responsabilidade de apresentar ao ");
        string.append("(a)_________________________________, no prazo máximo de 5 (cinco) dias ");
        string.append("após a realização da viagem no trecho _____________/____________/__________, ");
        string.append("a prestação de contas das passagens e diárias recebidas, constando dos seguintes ");
        string.append("documentos:");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("I - Bilhete de passagem aérea (original); ou");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("II - Recibo ou cópia de bilhete de passagem rodoviária (original); ou");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("III - Comprovante de despesa de transporte efetuado por outro meio;");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("IV - Ata de presença em reunião (se for o caso);");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("V - Programação do evento ou atividade por ele conduzida (cópia);");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("VI - Relatório circunstanciado das atividades desenvolvidas e dos resultados ");
        string.append("obtidos/esperados pelo órgão ou entidade responsável pelo pagamento da despesa;");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("7) Relatório fotográfico do evento. ");
        string.append("</p>");
        string.append("<br/>");
        string.append("<p align=\"justify\">");
        string.append("<b> II - </b>  de devolver à ________________, no prazo de até 3 (três) dias úteis a ");
        string.append("contar da data prevista para início do deslocamento:");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("a) o valor das diárias recebidas em excesso, quando o retorno ocorrer antes da  ");
        string.append("data prevista, contando o prazo a partir da data do retorno à minha localidade de destino;");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("b) o valor total das diárias, juntamente com os bilhetes de passagens, se por  ");
        string.append("qualquer circunstância, meu deslocamento não for efetivado. ");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("<b> III - </b> de, em caso de extravio do bilhete de passagem, solicitar imediatamente a  ");
        string.append("2ª via à empresa emitente. ");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append("<b> IV - </b> de arcar com as despesas de eventuais multas cobradas por  ");
        string.append("descumprimento do horário de embarque. ");
        string.append("</p>");
        string.append("<br/> ");
        string.append("<p align=\"justify\">");
        string.append("Declaro que estou ciente de estar sujeito às penalidades previstas no Decreto  ");
        string.append(" nº____________________, de ______de _________de 2014, e na legislação");
        string.append(" aplicada à espécie, em caso de omissão no dever de prestar contas, de desvio de");
        string.append(" finalidade ou qualquer outra irregularidade no uso das diárias ou passagens");
        string.append(" recebidas.");
        string.append("</p>");
        string.append("<p align=\"justify\">");
        string.append(" <br/><br/>");
        string.append(" <center>");
        string.append(" ________________, ______de______________ de__________.");
        string.append(" <br/> <br/>");
        string.append(" Assinatura legível do colaborador eventual ou profissional contratado");
        string.append(" </center>");
        string.append(" ");
        string.append("</html> ");
        return string.toString();
    }

    private String negrito(String texto) {
        if (texto == null) {
            return " - ";
        }
        return "<b>" + texto + "</b>";
    }

    public void imprimirPDF() {
        prepararConteudo();
        Util.geraPDF("Termo de Diária Eventual Colaborador", conteudo, FacesContext.getCurrentInstance());
    }
}
