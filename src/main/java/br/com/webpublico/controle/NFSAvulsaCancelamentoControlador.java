/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroCancelamentoNFSa;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 */
@ManagedBean(name = "nFSAvulsaCancelamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoNFSAvulsaCancelamento", pattern = "/nfs-avulsa-cancelamento/novo/",
        viewId = "/faces/tributario/notafiscalavulsa/cancelamento/edita.xhtml"),

    @URLMapping(id = "editarNFSAvulsaCancelamento", pattern = "/nfs-avulsa-cancelamento/editar/#{nFSAvulsaCancelamentoControlador.id}/",
        viewId = "/faces/tributario/notafiscalavulsa/cancelamento/edita.xhtml"),

    @URLMapping(id = "listarNFSAvulsaCancelamento", pattern = "/nfs-avulsa-cancelamento/listar/",
        viewId = "/faces/tributario/notafiscalavulsa/cancelamento/lista.xhtml"),

    @URLMapping(id = "verNFSAvulsaCancelamento", pattern = "/nfs-avulsa-cancelamento/ver/#{nFSAvulsaCancelamentoControlador.id}/",
        viewId = "/faces/tributario/notafiscalavulsa/cancelamento/visualizar.xhtml")
})
public class NFSAvulsaCancelamentoControlador extends PrettyControlador<NFSAvulsaCancelamento> implements Serializable, CRUD {


    @EJB
    private NFSAvulsaCancelamentoFacade nFSAvulsaCancelamentoFacade;
    @EJB
    EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private NFSAvulsaFacade nFSAvulsaFacade;
    private ConverterAutoComplete converterNFSAvulsa;
    private NFSAvulsa nota;
    private FiltroCancelamentoNFSa filtro;

    public NFSAvulsaCancelamentoControlador() {
        super(NFSAvulsaCancelamento.class);
    }


    public NFSAvulsa getNota() {
        return nota;
    }

    public void setNota(NFSAvulsa nota) {
        this.nota = nota;
    }

    public NFSAvulsa novaNotaZerada() {
        NFSAvulsa notaZerada = new NFSAvulsa();
        notaZerada.setSituacao(null);
        notaZerada.setValorIss(null);
        notaZerada.setDataNota(null);
        notaZerada.setEmissao(null);
        return notaZerada;
    }

    public boolean getExibeDadosDaNota() {
        return selecionado.getNfsAvulsa() != null;
    }

    public void atribuirNotaPesquisa(NFSAvulsa nfsAvulsa) {
        verificarDadosNFSa(nfsAvulsa);
        if (nota == null) {
            nota = novaNotaZerada();
        } else {
            nota = nFSAvulsaCancelamentoFacade.getNotaFacade().recuperar(nota.getId());
        }
    }

    private void verificarDadosNFSa(NFSAvulsa nfsAvulsa) {
        if (nfsAvulsa != null) {
            nota = nfsAvulsa;
            selecionado.setNfsAvulsa(nota);
        } else {
            nota = selecionado.getNfsAvulsa();
        }
    }

    public List<NFSAvulsa> completaNota(String parte) {
        return this.nFSAvulsaCancelamentoFacade.getNotaFacade().completaNFSAvulsaQueNaoEstejaCancelada(parte.trim());
    }

    public ConverterAutoComplete getConverterNota() {
        if (this.converterNFSAvulsa == null) {
            this.converterNFSAvulsa = new ConverterAutoComplete(NFSAvulsa.class, nFSAvulsaCancelamentoFacade.getNotaFacade());
        }
        return this.converterNFSAvulsa;
    }

    public String getEnderecoPrestador() {
        if (nota.getPrestador() != null) {
            return getEndereco(nota.getPrestador());
        }
        if (nota.getCmcPrestador() != null) {
            EnderecoCorreio end = nFSAvulsaFacade.getCadastroEconomicoFacade().recuperarEnderecoPessoaCMC(nota.getCmcPrestador().getId());
            return end != null ? (end.getTipoLogradouro() != null ? end.getTipoLogradouro().getDescricao() + " " : "") + end.getLogradouro() + " " + end.getBairro() + " Nº " + end.getNumero() : "";
        }
        return "";
    }

    public String getEnderecoTomador() {
        if (nota.getTomador() != null) {
            return getEndereco(nota.getTomador());
        }
        if (nota.getCmcTomador() != null) {
            EnderecoCorreio end = nFSAvulsaFacade.getCadastroEconomicoFacade().recuperarEnderecoPessoaCMC(nota.getCmcTomador().getId());
            return end != null ? (end.getTipoLogradouro() != null ? end.getTipoLogradouro().getDescricao() + " " : "") + end.getLogradouro() + " " + end.getBairro() + " Nº " + end.getNumero() : "";
        }
        return "";
    }

    private String getEndereco(Pessoa p) {
        List<EnderecoCorreio> enderecos = enderecoCorreioFacade.recuperaEnderecosPessoa(p);
        EnderecoCorreio enderecoCorreio = null;
        for (EnderecoCorreio end : enderecos) {
            if (end.getPrincipal()) {
                enderecoCorreio = end;
            }
            if (enderecoCorreio == null) {
                if (TipoEndereco.DOMICILIO_FISCAL.equals(end.getTipoEndereco())) {
                    enderecoCorreio = end;
                }
            }

            if (enderecoCorreio == null) {
                if (TipoEndereco.CORRESPONDENCIA.equals(end.getTipoEndereco())) {
                    return end.toString();
                }
            }
        }
        if (enderecoCorreio == null && enderecos.size() > 0) {
            enderecoCorreio = enderecos.get(0);
        }
        return enderecoCorreio != null ? enderecoCorreio.toString() : "";
    }


    @Override
    @URLAction(mappingId = "editarNFSAvulsaCancelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verNFSAvulsaCancelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        nota = nFSAvulsaCancelamentoFacade.getNotaFacade().recuperar(selecionado.getNfsAvulsa().getId());
    }

    @Override
    public AbstractFacade getFacede() {
        return nFSAvulsaCancelamentoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfs-avulsa-cancelamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoNFSAvulsaCancelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        this.selecionado = new NFSAvulsaCancelamento();
        this.nota = novaNotaZerada();
        this.selecionado.setData(new Date());
        this.selecionado.setUsuarioCancelamento(((SistemaControlador) Util.getControladorPeloNome("sistemaControlador")).getUsuarioCorrente());
        limparFiltros();
    }

    public void salvar() {
        if (validaCancelamento()) {
            try {
                nFSAvulsaCancelamentoFacade.getNotaFacade().cancelaParcelaValorDividaDaNFS(selecionado.getNfsAvulsa());
                selecionado.getNfsAvulsa().setSituacao(NFSAvulsa.Situacao.CANCELADA);
                selecionado.setNfsAvulsa(nFSAvulsaCancelamentoFacade.getNotaFacade().atualizaNFSAvulsa(selecionado.getNfsAvulsa()));
                super.salvar();
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addWarn("Não foi possível cancelar.", "A Nota Fiscal possui parcelas pagas.");
            } catch (Exception e) {
                FacesUtil.addWarn("Não foi possível cancelar.", "");
                logger.error(e.getMessage());
            }
        }
    }

    private boolean validaCancelamento() {
        boolean retorno = true;

        if (this.selecionado.getNfsAvulsa() == null) {
            FacesUtil.addWarn("Atenção!", "A Nota Fiscal Avulsa é um campo obrigatório");
            retorno = false;
        }

        if (this.selecionado.getMotivo() == null || this.selecionado.getMotivo().length() <= 0) {
            FacesUtil.addWarn("Atenção!", "Informe o motivo do cancelamento da nota fiscal avulsa.");
            retorno = false;
        }

        if (this.selecionado.getNfsAvulsa() == null && this.nFSAvulsaCancelamentoFacade.getNotaFacade().damPago(this.selecionado.getNfsAvulsa())) {
            FacesUtil.addWarn("Atenção!", "A nota fiscal avulsa não pode ser cancelada, pois o DAM ja foi pago.");
            retorno = false;
        }

        return retorno;
    }

    public BigDecimal getTotalIss() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : nota.getItens()) {
                total = total.add(it.getValorIss());
            }
        }
        return total;
    }

    public BigDecimal getTotalParcial() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : nota.getItens()) {
                total = total.add(it.getValorTotal());
            }
        }
        return total;
    }

    public BigDecimal getTotalUnitario() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : nota.getItens()) {
                total = total.add(it.getValorUnitario());
            }
        }
        return total;
    }

    public List<NFSAvulsaItem> getNovaLista() {
        return new ArrayList<>();
    }

    public FiltroCancelamentoNFSa getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroCancelamentoNFSa filtro) {
        this.filtro = filtro;
    }

    public void limparFiltros() {
        filtro = new FiltroCancelamentoNFSa();
        filtro.setNumero(null);
        filtro.setExercicio(null);
        filtro.setCpfCnpj(null);
        filtro.setNomeRazaoSocial(null);
        filtro.setNotas(Lists.<NFSAvulsa>newArrayList());
    }

    private void validarFiltrosCancelamento() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(filtro.getNumero())
            && filtro.getExercicio() == null
            && Strings.isNullOrEmpty(filtro.getCpfCnpj())
            && Strings.isNullOrEmpty(filtro.getNomeRazaoSocial())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um dos filtros.");
        }
        ve.lancarException();
    }

    public void hasNFSaNaoCanceladas(FiltroCancelamentoNFSa filtro) {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getNotas() == null || filtro.getNotas().isEmpty()) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Nenhuma Nota Fiscal de Serviços Avulsa encontrada com os filtros informados.");
        }
        ve.lancarException();
    }

    public void buscarNFSaCancelamento() {
        try {
            validarFiltrosCancelamento();
            filtro.setNotas(Lists.<NFSAvulsa>newArrayList());
            filtro.getNotas().addAll(nFSAvulsaFacade.buscarNFSaCancelamento(filtro));
            hasNFSaNaoCanceladas(filtro);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }
}
