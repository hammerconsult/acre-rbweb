/*
 * Codigo gerado automaticamente em Fri Jul 01 16:20:16 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.EntidadePCS;
import br.com.webpublico.entidades.MesesPromocao;
import br.com.webpublico.entidades.PlanoCargosSalarios;
import br.com.webpublico.entidades.rh.pccr.MesesProgressao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.PlanoCargosSalariosFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean(name = "planoCargosSalariosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPlanoCargosSalarios", pattern = "/plano-cargos-salarios/novo/", viewId = "/faces/rh/administracaodepagamento/planocargossalarios/edita.xhtml"),
    @URLMapping(id = "editarPlanoCargosSalarios", pattern = "/plano-cargos-salarios/editar/#{planoCargosSalariosControlador.id}/", viewId = "/faces/rh/administracaodepagamento/planocargossalarios/edita.xhtml"),
    @URLMapping(id = "listarPlanoCargosSalarios", pattern = "/plano-cargos-salarios/listar/", viewId = "/faces/rh/administracaodepagamento/planocargossalarios/lista.xhtml"),
    @URLMapping(id = "verPlanoCargosSalarios", pattern = "/plano-cargos-salarios/ver/#{planoCargosSalariosControlador.id}/", viewId = "/faces/rh/administracaodepagamento/planocargossalarios/visualizar.xhtml")
})
public class PlanoCargosSalariosControlador extends PrettyControlador<PlanoCargosSalarios> implements Serializable, CRUD {

    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    private EntidadePCS entidadePCSSelecionada;
    private ConverterAutoComplete converterEntidade;
    private MesesProgressao mesesProgressaoSelecionado;
    private MesesPromocao mesesPromocaoSelecionado;

    public Converter getConverterEntidade() {
        if (converterEntidade == null) {
            converterEntidade = new ConverterAutoComplete(Entidade.class, entidadeFacade);
        }
        return converterEntidade;
    }

    public EntidadePCS getEntidadePCSSelecionada() {
        return entidadePCSSelecionada;
    }

    public void setEntidadePCSSelecionada(EntidadePCS entidadePCSSelecionada) {
        this.entidadePCSSelecionada = entidadePCSSelecionada;
    }

    public boolean isPermitidoAdicionarEntidade() {
        if (entidadePCSSelecionada.getEntidade() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Entidade é obrigatório.");
            return false;
        }

        for (EntidadePCS entidadeDaVez : selecionado.getEntidadesPCS()) {
            if (entidadeDaVez.getEntidade().equals(entidadePCSSelecionada.getEntidade())) {
                FacesUtil.addOperacaoNaoPermitida("Esta entidade já foi adicionada na lista e não pode ser adicionada novamente.");
                return false;
            }
        }

        return true;
    }

    public void criarEntidade() {
        this.entidadePCSSelecionada = new EntidadePCS();
    }

    public void cancelarEntidade() {
        this.entidadePCSSelecionada = null;
    }

    public void confirmarEntidade() {
        if (!isPermitidoAdicionarEntidade()) {
            return;
        }

        entidadePCSSelecionada.setPlanoCargosSalarios(selecionado);
        selecionado.setEntidadesPCS(Util.adicionarObjetoEmLista(selecionado.getEntidadesPCS(), entidadePCSSelecionada));
        cancelarEntidade();
    }

    public void removerEntidade(EntidadePCS entidade) {
        selecionado.getEntidadesPCS().remove(entidade);
    }

    public List<Entidade> completarEntidade(String parte) {
        return entidadeFacade.listaEntidades(parte.trim());
    }

    public PlanoCargosSalariosControlador() {
        super(PlanoCargosSalarios.class);
    }

    public PlanoCargosSalariosFacade getFacade() {
        return planoCargosSalariosFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return planoCargosSalariosFacade;
    }

    public List<SelectItem> getTipoPcs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPCS o : TipoPCS.values()) {
            toReturn.add(new SelectItem(o, o.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        if (!Util.validaCampos(selecionado)) {
            return;
        }

        if (DataUtil.verificaDataFinalMaiorQueDataInicial(selecionado.getInicioVigencia(), selecionado.getFinalVigencia())) {
            FacesUtil.addOperacaoNaoPermitida("A data de inicio de vigência deve ser anterior a data de final de vigência.");
            return;
        }

        super.salvar();
    }

    public MesesProgressao getMesesProgressaoSelecionado() {
        return mesesProgressaoSelecionado;
    }

    public void setMesesProgressaoSelecionado(MesesProgressao mesesProgressaoSelecionado) {
        this.mesesProgressaoSelecionado = mesesProgressaoSelecionado;
    }

    public MesesPromocao getMesesPromocaoSelecionado() {
        return mesesPromocaoSelecionado;
    }

    public void setMesesPromocaoSelecionado(MesesPromocao mesesPromocaoSelecionado) {
        this.mesesPromocaoSelecionado = mesesPromocaoSelecionado;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/plano-cargos-salarios/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoPlanoCargosSalarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarPlanoCargosSalarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verPlanoCargosSalarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void criarMesesProgressao() {
        this.mesesProgressaoSelecionado = new MesesProgressao();
    }

    public void cancelarMesesProgressao() {
        this.mesesProgressaoSelecionado = null;
    }

    private boolean isVigenciaDoNovoMesValida() {
        if (selecionado.getMesesProgressao() == null || selecionado.getMesesProgressao().isEmpty()) {
            return true;
        }

        MesesProgressao ultimoMesAdicionado;
        selecionado.ordenarMesesProgressaoInicioVigenciaAsc();
        if (selecionado.getMesesProgressao().get(0).equals(mesesProgressaoSelecionado)) {
            return true;
        }

        try {
            ultimoMesAdicionado = selecionado.getMesesProgressao().get(selecionado.getMesesProgressao().indexOf(mesesProgressaoSelecionado) - 1);
        } catch (IndexOutOfBoundsException ie) {
            ultimoMesAdicionado = selecionado.getMesesProgressao().get(selecionado.getMesesProgressao().size() - 1);
        }

        if (ultimoMesAdicionado == null)
            return true;

        if (mesesProgressaoSelecionado.getInicioVigencia().compareTo(ultimoMesAdicionado.getInicioVigencia()) <= 0) {
            return false;
        }

        return true;
    }

    private void atribuirFinalDeVigenciaParaUltimoRegistroAdicionado() {
        if (selecionado.getMesesProgressao() == null || selecionado.getMesesProgressao().isEmpty())
            return;
        MesesProgressao ultimoMesAdicionado;
        selecionado.ordenarMesesProgressaoInicioVigenciaAsc();

        if (selecionado.getMesesProgressao().get(0).equals(mesesProgressaoSelecionado)) {
            return;
        }

        try {
            ultimoMesAdicionado = selecionado.getMesesProgressao().get(selecionado.getMesesProgressao().indexOf(mesesProgressaoSelecionado) - 1);
        } catch (IndexOutOfBoundsException ie) {
            ultimoMesAdicionado = selecionado.getMesesProgressao().get(selecionado.getMesesProgressao().size() - 1);
        }


        if (ultimoMesAdicionado == null)
            return;

        ultimoMesAdicionado.setFinalVigencia(DataUtil.adicionaDias(mesesProgressaoSelecionado.getInicioVigencia(), -1));
        selecionado.setMesesProgressao(Util.adicionarObjetoEmLista(selecionado.getMesesProgressao(), ultimoMesAdicionado));
    }

    public void confirmarMesesProgressao() {
        if (!Util.validaCampos(mesesProgressaoSelecionado)) {
            return;
        }

        if (DataUtil.verificaDataFinalMaiorQueDataInicial(mesesProgressaoSelecionado.getInicioVigencia(), mesesProgressaoSelecionado.getFinalVigencia())) {
            FacesUtil.addOperacaoNaoPermitida("A data de inicio de vigência deve ser anterior a data de final de vigência.");
            return;
        }

        if (!isVigenciaDoNovoMesValida()) {
            FacesUtil.addOperacaoNaoPermitida("A data de inicio de vigência deve ser posterior à do último registro adicionado");
            return;
        }

        atribuirFinalDeVigenciaParaUltimoRegistroAdicionado();

        mesesProgressaoSelecionado.setPlanoCargosSalarios(selecionado);
        selecionado.setMesesProgressao(Util.adicionarObjetoEmLista(selecionado.getMesesProgressao(), mesesProgressaoSelecionado));
        cancelarMesesProgressao();
    }

    public void removerMesesProgressao(MesesProgressao mesesProgressao) {
        selecionado.ordenarMesesProgressaoInicioVigenciaAsc();
        int indexMesRemovido = selecionado.getMesesProgressao().indexOf(mesesProgressao);
        selecionado.getMesesProgressao().remove(mesesProgressao);
        try {
            mesesProgressaoSelecionado = selecionado.getMesesProgressao().get(indexMesRemovido);
            atribuirFinalDeVigenciaParaUltimoRegistroAdicionado();
            return;
        } catch (IndexOutOfBoundsException ioe) {
        }

        try {
            MesesProgressao ultimoMes = selecionado.getMesesProgressao().get(indexMesRemovido - 1);
            ultimoMes.setFinalVigencia(null);
            selecionado.setMesesProgressao(Util.adicionarObjetoEmLista(selecionado.getMesesProgressao(), ultimoMes));
        } catch (IndexOutOfBoundsException ioe) {
        }

    }

    public void selecionarMesesProgressao(MesesProgressao mesesProgressao) {
        mesesProgressaoSelecionado = (MesesProgressao) Util.clonarObjeto(mesesProgressao);
    }

    public void novaConfiguracaoMesesPromocao() {
        mesesPromocaoSelecionado = new MesesPromocao(selecionado);
    }

    public void confirmarConfiguracaoMesesPromocao() {
        try {
            validarConfiguracaoMesesPromocao();
            selecionado.adicionarConfiguracaoMesesPromocao(mesesPromocaoSelecionado);
            cancelarConfiguracaoMesesPromocao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarConfiguracaoMesesPromocao() {
        ValidacaoException ve = new ValidacaoException();
        if (!Util.validaCampos(mesesPromocaoSelecionado)) throw ve;
        if (!DataUtil.isVigenciaValida(mesesPromocaoSelecionado, selecionado.getMesesPromocao())) throw ve;
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void cancelarConfiguracaoMesesPromocao() {
        if (mesesPromocaoSelecionado.isEditando()) {
            mesesPromocaoSelecionado.setOperacao(null);
            confirmarConfiguracaoMesesPromocao();
            return;
        }
        mesesPromocaoSelecionado = null;
    }

    public void selecionarConfiguracaoMesesPromocao(MesesPromocao mp) {
        mesesPromocaoSelecionado = (MesesPromocao) Util.clonarObjeto(mp);
        mesesPromocaoSelecionado.setOperacao(Operacoes.EDITAR);
        removerConfiguracaoMesesPromocao(mp);
    }

    public void removerConfiguracaoMesesPromocao(MesesPromocao mp) {
        selecionado.removerConfiguracaoMesesPromocao(mp);
    }
}
