package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.Propriedade;
import br.com.webpublico.entidades.Testada;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 09/02/14
 * Time: 15:44
 */
@ManagedBean
@ViewScoped
public class PesquisaCadastroImobiliarioControlador extends ComponentePesquisaGenerico implements Serializable {
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.codigo", "Código", String.class));
        itens.add(new ItemPesquisaGenerica("obj.inscricaoCadastral", "Inscrição Cadastral", String.class));
        itens.add(new ItemPesquisaGenerica("obj.lote.codigoLote", "Código Municipal do Lote", String.class));
        itens.add(new ItemPesquisaGenerica("obj.lote.descricaoLoteamento", "Código do Loteamento", String.class));
        itens.add(new ItemPesquisaGenerica("obj.lote.quadra.codigo", "Número da Quadra", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.lote.quadra.descricao", "Código Municipal da Quadra", String.class));
        itens.add(new ItemPesquisaGenerica("obj.lote.quadra.descricaoLoteamento", "Código de Loteamento da Quadra", String.class));
        itens.add(new ItemPesquisaGenerica("obj.lote.setor.codigo", "Código do Setor", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.lote.setor.nome", "Nome do Setor", String.class));
        itens.add(new ItemPesquisaGenerica("testada.face.logradouroBairro.bairro.codigo", "Código do Bairro", Long.class));
        itens.add(new ItemPesquisaGenerica("testada.face.logradouroBairro.bairro.descricao", "Nome do Bairro", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("testada.face.logradouroBairro.logradouro.tipoLogradouro.descricao", "Tipo Logradouro", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("testada.face.logradouroBairro.logradouro.nome", "Nome do Logradouro", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.lote.numeroCorreio", "Número", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.complementoEndereco", "Complemento (BCI)", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("propriedade.pessoa", "Proprietário", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("propriedade.pessoa", "CPF / CNPJ", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("construcao.pessoa", "Compromissário", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("construcao.codigo", "Código da Construção", Integer.class));
        itens.add(new ItemPesquisaGenerica("construcao.numeroAlvara", "Número do Alvará da Construção", String.class));
        itens.add(new ItemPesquisaGenerica("obj.ativo", "Situação do Cadastro", Boolean.class, false, false, "Inativo", "Ativo"));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.codigo", "Código", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.inscricaoCadastral", "Inscrição Cadastral", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.lote.codigoLote", "Código Municipal do Lote", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.lote.descricaoLoteamento", "Código do Loteamento", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.lote.quadra.codigo", "Número da Quadra", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.lote.quadra.descricao", "Código Municipal da Quadra", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.lote.quadra.descricaoLoteamento", "Código de Loteamento da Quadra", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.lote.setor.codigo", "Código do Setor", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.lote.setor.nome", "Nome do Setor", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.lote.numeroCorreio", "Número", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.complementoEndereco", "Complemento (BCI)", String.class, false, true));
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getHqlConsulta() {
        return "select distinct obj from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getComplementoQuery() {
        StringBuilder hql = new StringBuilder("");
        hql.append(" left join obj.lote.testadas testada ");
        hql.append(" left join obj.propriedade propriedade");
        hql.append(" left join obj.construcoes construcao");
        hql.append(super.getComplementoQuery());
        return hql.toString();
    }

    @Override
    public String montaCondicao() {
        StringBuilder condicao = new StringBuilder();
        condicao.append("(");
        condicao.append(super.montaCondicao());
        condicao.append(") and (testada.id is null or testada.principal = 1)");
        condicao.append(" and propriedade.finalVigencia is null  ");
        return condicao.toString();
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);

        lista = new ArrayList(new TreeSet(lista));

        if (!lista.isEmpty()) {
            metadata = new EntidadeMetaData(lista.get(0).getClass());
            for (CadastroImobiliario ci : (List<CadastroImobiliario>) lista) {
                ci.setPropriedades(new ArrayList<Propriedade>());
                ci.getConstrucoes().size();
                ci.getPropriedade().size();
                if (ci != null && ci.getLote() != null && ci.getLote().getId() != null) {
                    ci.setLote(cadastroImobiliarioFacade.getLoteFacade().recuperar(ci.getLote().getId()));
                    for (Testada obj : ci.getLote().getTestadas()) {
                        if (obj.getPrincipal()) {
                            if (obj.getFace() != null && obj.getFace().getLogradouroBairro() != null) {
                                ci.setBairro(obj.getFace().getLogradouroBairro().getBairro());
                                ci.setLogradouro(obj.getFace().getLogradouroBairro().getLogradouro());
                            }
                        }
                    }

                    if (ci.getPropriedade() != null)  {
                        for (Propriedade obj : ci.getPropriedade()) {
                            if (obj.getFinalVigencia() == null || obj.getFinalVigencia().after(new Date())) {
                                ci.getPropriedades().add(obj);
                            }
                        }
                    }
                }
            }
        }
    }
}
