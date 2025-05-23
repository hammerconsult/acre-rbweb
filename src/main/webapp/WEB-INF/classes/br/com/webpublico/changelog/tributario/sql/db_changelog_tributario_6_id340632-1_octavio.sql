create or replace view vwconsultadividaativa as
select distinct
    c.cadastro_id id_cadastro,
    coalesce(ci.inscricaocadastral, coalesce(ce.inscricaocadastral, cr.numeroincra)) cadastro,
    e.ano exercicio,
    cp.pessoa_id id_pessoa,
    c.id id_calculo,
    c.subdivida sd,
    vd.id id_valordivida,
    c.datacalculo emissao,
    pvd.id id_parcela,
    pvd.opcaopagamento_id id_opcaopagamento,
    op.promocional cotaunica,
    pvd.vencimento,
    spvd.situacaoparcela situacaoparcela,
    spvd.referencia,
    pacote_consulta_de_debitos.getnumeroparcela(vd.id, op.id, op.promocional, pvd.sequenciaparcela) parcela,
    case
        when spvd.situacaoparcela <> 'EM_ABERTO'
            then pvd.valor
        else spvd.saldo
        end valororiginal,
    d.descricao divida,
    d.id id_divida,
    coalesce(pvd.dividaativa,0) dividaativa,
    coalesce(pvd.dividaativaajuizada,0) dividaativaajuizada,
    null pagamento,
    dva.acrescimo_id id_configuracaoacrescimo,
    case
        when ce.id is not null then 'ECONOMICO'
        when ci.id is not null then 'IMOBILIARIO'
        when cr.id is not null then 'RURAL'
        else 'PESSOA'
        end tipo_cadastro,
    coalesce(pf.cpf, pj.cnpj) cpfcnpj,
    coalesce(pf.nome, pj.razaosocial) nomerazaosocial,
    da.numero numero_inscricao,
    exinsc.ano ano_inscricao,
    (select max(cda.numero) from certidaodividaativa cda
                                     inner join itemcertidaodividaativa icda on icda.CERTIDAO_ID = cda.id
     where icda.iteminscricaodividaativa_id = ida.id
       and cda.SITUACAOCERTIDAODA <> 'CANCELADA') as numero_cda,
    (select max(pcj.numeroprocessoforum) from certidaodividaativa cda
                                                  inner join itemcertidaodividaativa icda on icda.CERTIDAO_ID = cda.id
                                                  inner join exercicio excda on excda.id = cda.exercicio_id
                                                  inner join processojudicialcda pjcda on pjcda.certidaodividaativa_id = cda.id
                                                  inner join processojudicial pcj on pcj.id = pjcda.processojudicial_id
     where icda.iteminscricaodividaativa_id = ida.id
       and cda.SITUACAOCERTIDAODA <> 'CANCELADA') as numero_ajuizamento,
    (select max(excda.ano) from certidaodividaativa cda
                                    inner join itemcertidaodividaativa icda on icda.CERTIDAO_ID = cda.id
                                    inner join exercicio excda on excda.id = cda.exercicio_id
     where icda.iteminscricaodividaativa_id = ida.id
       and cda.SITUACAOCERTIDAODA <> 'CANCELADA') as exercicio_cda,
    (select max(pcj.SITUACAO) from certidaodividaativa cda
                                       inner join itemcertidaodividaativa icda on icda.CERTIDAO_ID = cda.id
                                       inner join exercicio excda on excda.id = cda.exercicio_id
                                       inner join processojudicialcda pjcda on pjcda.certidaodividaativa_id = cda.id
                                       inner join processojudicial pcj on pcj.id = pjcda.processojudicial_id
     where icda.iteminscricaodividaativa_id = ida.id
       and cda.SITUACAOCERTIDAODA <> 'CANCELADA') as situacao_processo_judicial,
    c.tipocalculo, coalesce(pfda.cpf, pjda.cnpj) as cpf_original, coalesce(pfda.nome, pjda.razaosocial) as pessoa_original,
    endereco.logradouro, endereco.bairro, endereco.numero, endereco.complemento, endereco.tipologradouro, endereco.cep, endereco.tipoendereco,
    coalesce(pvd.debitoprotestado,0) debitoprotestado
from iteminscricaodividaativa ida
         inner join inscricaodividaativa da on ida.inscricaodividaativa_id = da.id
         left join pessoafisica pfda on ida.pessoa_id = pfda.id
         left join pessoajuridica pjda on ida.pessoa_id = pjda.id
         inner join exercicio exinsc on exinsc.id = da.exercicio_id
         inner join calculo c on c.id = ida.id
         inner join calculopessoa cp on cp.calculo_id = c.id
         left join pessoajuridica pj on pj.id = cp.pessoa_id
         left join pessoafisica pf on pf.id = cp.pessoa_id
         left join cadastroeconomico ce on c.cadastro_id = ce.id
         left join cadastroimobiliario ci on c.cadastro_id = ci.id
         left join cadastrorural cr on c.cadastro_id = cr.id
         inner join valordivida vd on vd.calculo_id = c.id
         inner join exercicio e on e.id = vd.exercicio_id
         inner join divida d on d.id = vd.divida_id
         inner join dividaacrescimo dva on dva.divida_id = d.id AND
                                           CURRENT_DATE BETWEEN DVA.INICIOVIGENCIA AND COALESCE(DVA.FINALVIGENCIA, CURRENT_DATE)
         inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
         inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
         inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
         left join vwenderecopessoa endereco on cp.pessoa_id = endereco.pessoa_id
where spvd.situacaoparcela <> 'ISOLAMENTO'
  and ida.situacao <> 'CANCELADO'
union
select distinct
    c.cadastro_id id_cadastro,
    coalesce(ci.inscricaocadastral, coalesce(ce.inscricaocadastral, cr.numeroincra)) cadastro,
    e.ano exercicio,
    cp.pessoa_id id_pessoa,
    c.id id_calculo,
    c.subdivida sd,
    vd.id id_valordivida,
    c.datacalculo emissao,
    pvd.id id_parcela,
    pvd.opcaopagamento_id id_opcaopagamento,
    op.promocional cotaunica,
    pvd.vencimento,
    spvd.situacaoparcela situacaoparcela,
    spvd.referencia,
    pacote_consulta_de_debitos.getnumeroparcela(vd.id, op.id, op.promocional, pvd.sequenciaparcela) parcela,
    case
        when spvd.situacaoparcela <> 'EM_ABERTO'
            then pvd.valor
        else spvd.saldo
        end valororiginal,
    d.descricao divida,
    d.id id_divida,
    coalesce(pvd.dividaativa,0) dividaativa,
    coalesce(pvd.dividaativaajuizada,0) dividaativaajuizada,
    null pagamento,
    dva.acrescimo_id id_configuracaoacrescimo,
    case
        when ce.id is not null then 'ECONOMICO'
        when ci.id is not null then 'IMOBILIARIO'
        when cr.id is not null then 'RURAL'
        else 'PESSOA'
        end tipo_cadastro,
    coalesce(pf.cpf, pj.cnpj) cpfcnpj,
    coalesce(pf.nome, pj.razaosocial) nomerazaosocial,
    0 numero_inscricao,
    0 ano_inscricao,
    0 numero_cda,
    '' numero_ajuizamento,
    0 exercicio_cda,
    '' situacao_processo_judicial,
    c.tipocalculo,
    '' as cpf_original,
    '' as pessoa_original,
    endereco.logradouro, endereco.bairro, endereco.numero, endereco.complemento, endereco.tipologradouro, endereco.cep, endereco.tipoendereco,
    coalesce(pvd.debitoprotestado,0) debitoprotestado
from CANCELAMENTOPARCELAMENTO cpp
         inner join calculo c on c.id = cpp.id
         inner join calculopessoa cp on cp.calculo_id = c.id
         left join pessoajuridica pj on pj.id = cp.pessoa_id
         left join pessoafisica pf on pf.id = cp.pessoa_id
         left join cadastroeconomico ce on c.cadastro_id = ce.id
         left join cadastroimobiliario ci on c.cadastro_id = ci.id
         left join cadastrorural cr on c.cadastro_id = cr.id
         inner join valordivida vd on vd.calculo_id = c.id
         inner join exercicio e on e.id = vd.exercicio_id
         inner join divida d on d.id = vd.divida_id
         inner join dividaacrescimo dva on dva.divida_id = d.id AND
                                           CURRENT_DATE BETWEEN DVA.INICIOVIGENCIA AND COALESCE(DVA.FINALVIGENCIA, CURRENT_DATE)
         inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
         inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
         inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
         left join vwenderecopessoa endereco on cp.pessoa_id = endereco.pessoa_id
where spvd.situacaoparcela <> 'ISOLAMENTO'
