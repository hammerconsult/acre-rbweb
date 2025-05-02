package br.com.webpublico.controle;

import br.com.webpublico.entidades.ArquivoPermissionarioConcessionario;
import br.com.webpublico.entidades.ItemPermissionarioConcessionario;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoPermissionarioConcessionarioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 23/08/2016.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoArquivoPermissionarios", pattern = "/exportacao-permissionarios-concessionarios/novo/", viewId = "/faces/tributario/rbtrans/arquivos/exportacao/permissionarios-e-concessionarios/edita.xhtml"),
    @URLMapping(id = "editarArquivoPermissionarios", pattern = "/exportacao-permissionarios-concessionarios/editar/#{arquivoPermissionarioConcessionarioControlador.id}/", viewId = "/faces/tributario/rbtrans/arquivos/exportacao/permissionarios-e-concessionarios/edita.xhtml"),
    @URLMapping(id = "listarArquivoPermissionarios", pattern = "/exportacao-permissionarios-concessionarios/listar/", viewId = "/faces/tributario/rbtrans/arquivos/exportacao/permissionarios-e-concessionarios/lista.xhtml"),
    @URLMapping(id = "verArquivoPermissionarios", pattern = "/exportacao-permissionarios-concessionarios/ver/#{arquivoPermissionarioConcessionarioControlador.id}/", viewId = "/faces/tributario/rbtrans/arquivos/exportacao/permissionarios-e-concessionarios/visualizar.xhtml")
})
public class ArquivoPermissionarioConcessionarioControlador extends PrettyControlador<ArquivoPermissionarioConcessionario> implements Serializable, CRUD {

    @EJB
    private ArquivoPermissionarioConcessionarioFacade arquivoFacade;
    private final SimpleDateFormat formatterData = new SimpleDateFormat("dd/MM/yyyy");
    private FiltroResultado filtro;
    private List<ItemPermissionarioConcessionario> resultados;

    public ArquivoPermissionarioConcessionarioControlador() {
        super(ArquivoPermissionarioConcessionario.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return arquivoFacade;
    }

    @URLAction(mappingId = "novoArquivoPermissionarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataOperacao(new Date());
        selecionado.setUsuarioSistema(arquivoFacade.getSistemaFacade().getUsuarioCorrente());
        filtro = new FiltroResultado();
        resultados = Lists.newArrayList();
    }

    @URLAction(mappingId = "verArquivoPermissionarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        filtro = new FiltroResultado();
        resultados = Lists.newArrayList();
        resultados.addAll(selecionado.getItensArquivo());
        Collections.sort(resultados);
    }

    @URLAction(mappingId = "editarArquivoPermissionarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        redireciona();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exportacao-permissionarios-concessionarios/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public FiltroResultado getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroResultado filtro) {
        this.filtro = filtro;
    }

    public List<ItemPermissionarioConcessionario> getResultados() {
        return resultados;
    }

    public void setResultados(List<ItemPermissionarioConcessionario> resultados) {
        this.resultados = resultados;
    }

    public void pesquisarDetalhes() {
        arquivoFacade.gerarItensArquivo(selecionado);
        resultados = Lists.newArrayList();
        resultados.addAll(selecionado.getItensArquivo());
        Collections.sort(resultados);
        filtro = new FiltroResultado();
    }

    public void downloadArquivo() {
        try {
            arquivoFacade.downloadArquivo(selecionado);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void validarRegrasNegocio() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItensArquivo().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Pesquise primeiro antes de salvar.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public void salvar() {
        try {
            validarRegrasNegocio();
            selecionado = arquivoFacade.salvarMerge(selecionado);
            if (selecionado != null && selecionado.getId() != null) {
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar());
            } else {
                redireciona();
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public SelectItem[] getTiposDePessoa() {
        SelectItem[] opcoes = new SelectItem[3];
        opcoes[0] = new SelectItem("", "TODOS");
        opcoes[1] = new SelectItem("F", "F");
        opcoes[2] = new SelectItem("J", "J");
        return opcoes;
    }

    public SelectItem[] getTiposPermissionario() {
        SelectItem[] opcoes = new SelectItem[ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.PermissionarioConcessionario.values().length+1];
        opcoes[0] = new SelectItem("", "TODOS");
        int i = 1;
        for (ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.PermissionarioConcessionario tipo : ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.PermissionarioConcessionario.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }

    public String converterDataParaString(Date data) {
        if (data != null) {
            return formatterData.format(data);
        }
        return "";
    }

    public void filtrarResultados() {
        resultados = Lists.newArrayList();
        if (filtro != null) {
            for (ItemPermissionarioConcessionario item : selecionado.getItensArquivo()) {
                boolean adicionar = true;
                if (filtro.isAlgumFiltroInformado()) {
                    if (filtro.getTipoPessoa() != null) {
                        adicionar = adicionar ? filtro.getTipoPessoa().equals(item.getTipoPessoa()) : false;
                    }
                    if (filtro.getCpfCnpj() != null && !filtro.getCpfCnpj().trim().isEmpty()) {
                        adicionar = adicionar ? (item.getCpfCnpj().contains(filtro.getCpfCnpj())) : false;
                    }
                    if (filtro.getNomePermissionario() != null && !filtro.getNomePermissionario().trim().isEmpty()) {
                        adicionar = adicionar ? item.getNomePermissionario().toUpperCase().trim().contains(filtro.getNomePermissionario().toUpperCase().trim()) : false;
                    }
                    if (filtro.getDataVencimento() != null) {
                        adicionar = adicionar ? filtro.getDataVencimento().equals(item.getDataVencimento()) : false;
                    }
                    if (filtro.getTipo() != null && item.getTipoPermissionario() != null) {
                        adicionar = adicionar ? filtro.getTipo().equals(item.getTipoPermissionario().getPermissionarioConcessionario()) : false;
                    }
                }
                if (adicionar) {
                    resultados.add(item);
                }
            }
            if (!resultados.isEmpty()) {
                Collections.sort(resultados);
            }
        }
    }

    public class FiltroResultado {
        private String tipoPessoa;
        private String cpfCnpj;
        private String nomePermissionario;
        private Date dataVencimento;
        private ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.PermissionarioConcessionario tipo;

        public String getTipoPessoa() {
            return tipoPessoa;
        }

        public void setTipoPessoa(String tipoPessoa) {
            this.tipoPessoa = tipoPessoa;
        }

        public String getCpfCnpj() {
            return cpfCnpj;
        }

        public void setCpfCnpj(String cpfCnpj) {
            this.cpfCnpj = cpfCnpj;
        }

        public String getNomePermissionario() {
            return nomePermissionario;
        }

        public void setNomePermissionario(String nomePermissionario) {
            this.nomePermissionario = nomePermissionario;
        }

        public Date getDataVencimento() {
            return dataVencimento;
        }

        public void setDataVencimento(Date dataVencimento) {
            this.dataVencimento = dataVencimento;
        }

        public ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.PermissionarioConcessionario getTipo() {
            return tipo;
        }

        public void setTipo(ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.PermissionarioConcessionario tipo) {
            this.tipo = tipo;
        }

        public boolean isAlgumFiltroInformado() {
            return (tipoPessoa != null ||
                (cpfCnpj != null && !cpfCnpj.trim().isEmpty()) ||
                (nomePermissionario != null && !nomePermissionario.trim().isEmpty()) ||
                dataVencimento != null ||
                tipo != null);
        }
    }
}
