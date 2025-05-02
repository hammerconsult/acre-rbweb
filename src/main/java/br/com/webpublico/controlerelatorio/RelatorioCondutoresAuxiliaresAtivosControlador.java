package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mateus on 21/01/2015.
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-condutores-auxiliares-ativos", pattern = "/relatorio/condutores-auxiliares-ativos", viewId = "/faces/tributario/relatorio/relatoriocondutoresauxiliaresativos.xhtml")
})
@ManagedBean
public class RelatorioCondutoresAuxiliaresAtivosControlador extends AbstractReport implements Serializable {

    @Enumerated(EnumType.STRING)
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Pessoa pessoa;
    private CadastroEconomico cadastroEconomico;
    private ConverterAutoComplete converterPessoaFisica;
    private ConverterAutoComplete converterCadastroEconomico;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;

    public RelatorioCondutoresAuxiliaresAtivosControlador() {
    }

    public ConverterAutoComplete getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoaFisica;
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCadastroEconomico;
    }

    public List<Pessoa> getListaPessoasFisicas(String parte) {
        return pessoaFacade.listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }

    public List<CadastroEconomico> getListaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.listaCadastroEconomicoPorCMCPessoaFisica(parte.trim());
    }

    public List<SelectItem> getListaTipoPermissao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, null));
        for (TipoPermissaoRBTrans tp : TipoPermissaoRBTrans.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        HashMap parametros = new HashMap();
        String nomeRelatorio = "RelatorioCondutoresAuxiliaresAtivos.jasper";
        try {
            parametros.put("SECRETARIA", sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente().getDescricao());
            parametros.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            parametros.put("ANO", sistemaControlador.getExercicioCorrente().getAno());
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("WHERE", gerarWhere());
            gerarRelatorio(nomeRelatorio, parametros);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String gerarWhere() {
        StringBuilder sql = new StringBuilder();
        if (pessoa != null) {
            sql.append(" and p.id = ").append(pessoa.getId());
        }
        if (cadastroEconomico != null) {
            sql.append(" and ce.inscricaocadastral = ").append(cadastroEconomico.getInscricaoCadastral());
        }
        if (tipoPermissaoRBTrans != null) {
            sql.append(" and pt.TIPOPERMISSAORBTRANS = '").append(tipoPermissaoRBTrans.name()).append("'");
        }
        return sql.toString();
    }

    @URLAction(mappingId = "relatorio-condutores-auxiliares-ativos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        tipoPermissaoRBTrans = null;
        pessoa = null;
        cadastroEconomico = null;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }
}
