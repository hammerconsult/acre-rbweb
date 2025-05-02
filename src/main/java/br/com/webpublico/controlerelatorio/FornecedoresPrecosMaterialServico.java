/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LicitacaoFacade;
import br.com.webpublico.negocios.ObjetoCompraFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@ViewScoped
@ManagedBean

@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-fornecedor-preco-material-servico", pattern = "/licitacao/relatorio-fornecedor-preco-material-servico/", viewId = "/faces/administrativo/relatorios/relatorio-fornecedores-e-precos-material-servico.xhtml")
})
public class FornecedoresPrecosMaterialServico implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(FornecedoresPrecosMaterialServico.class);
    @Limpavel(Limpavel.NULO)
    private Integer numero;
    @Limpavel(Limpavel.NULO)
    private Exercicio exercicio;
    @Limpavel(Limpavel.NULO)
    private ModalidadeLicitacao modalidadeLicitacao;
    @Limpavel(Limpavel.NULO)
    private Licitacao licitacao;
    @Limpavel(Limpavel.NULO)
    private Pessoa pessoa;
    @Limpavel(Limpavel.NULO)
    private ObjetoCompra objetoCompra;
    @Limpavel(Limpavel.NULO)
    private ServicoCompravel servicoCompravel;
    @Limpavel(Limpavel.NULO)
    private String filtros;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public String getCaminhoRelatorio() {
        return "/licitacao/relatorio-fornecedor-preco-material-servico/";
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ModalidadeLicitacao getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacao modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public ServicoCompravel getServicoCompravel() {
        return servicoCompravel;
    }

    public void setServicoCompravel(ServicoCompravel servicoCompravel) {
        this.servicoCompravel = servicoCompravel;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public List<Licitacao> completarLicitacoes(String parte) {
        return licitacaoFacade.buscarLicitacoes(parte);
    }

    public List<ObjetoCompra> buscarObjetoDeCompra(String parte) {
        return objetoCompraFacade.completaObjetoCompra(parte);
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ModalidadeLicitacao object : ModalidadeLicitacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novo-relatorio-fornecedor-preco-material-servico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        Util.limparCampos(this);
        limparFiltros();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            String nomeRelatorio = "Relatório de Fornecedores e Preços por Material/Serviço";
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE DESENVOLVIMENTO ECONÔMICO E FINANÇAS");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("complementoWhere", montarCondicao());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/fornecedor-preco-material-servico/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar o relatório: {}", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private String montarCondicao() {
        StringBuilder sb = new StringBuilder("");
        String complemento = " WHERE ";
        limparFiltros();

        if (numero != null) {
            sb.append(complemento).append(" licitacao.numerolicitacao = ").append(numero);
            filtros += "Número:" + numero + " ";
            complemento = " and ";
        }
        if (exercicio != null) {
            sb.append(complemento).append(" licitacao.exercicio_id = ").append(exercicio.getId());
            filtros += "Exercício:" + exercicio.getAno() + " ";
            complemento = " and ";
        }
        if (modalidadeLicitacao != null) {
            sb.append(complemento).append(" licitacao.modalidadelicitacao = '").append(modalidadeLicitacao.name()).append("'");
            filtros += "Modalidade:" + modalidadeLicitacao.getDescricao() + " ";
            complemento = " and ";
        }
        if (licitacao != null) {
            sb.append(complemento).append(" licitacao.id = ").append(licitacao.getId());
            filtros += "Objeto: " + licitacao.getResumoDoObjeto() + " ";
            complemento = " and ";
        }
        if (pessoa != null) {
            sb.append(complemento).append(" p.id = ").append(pessoa.getId());
            filtros += "Fornecedor: " + pessoa.getNome() + " ";
            complemento = " and ";
        }
        if (objetoCompra != null) {
            sb.append(complemento).append(" obj.id = ").append(objetoCompra.getId());
            filtros += "Item: " + objetoCompra.getDescricao() + " ";
            complemento = " and ";
        }
        if (servicoCompravel != null) {
            sb.append(complemento).append(" ser.id = ").append(servicoCompravel.getId());
            filtros += "Item: " + servicoCompravel.getDescricao() + " ";
        }

        return sb.toString();
    }

    private void limparFiltros() {
        this.filtros = "";
    }
}
