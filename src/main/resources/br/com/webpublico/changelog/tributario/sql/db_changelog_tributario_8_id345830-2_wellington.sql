create or replace view vwconsultadebitosexercicio as
select distinct
    c.cadastro_id as id_cadastro,
    coalesce(ci.inscricaocadastral, coalesce(ce.inscricaocadastral, cr.numeroincra)) as cadastro,
    e.ano as exercicio,
    cp.pessoa_id as id_pessoa,
    c.id as id_calculo,
    c.subdivida as sd,
    vd.id as id_valordivida,
    c.datacalculo as emissao,
    pvd.id as id_parcela,
    pvd.opcaopagamento_id as id_opcaopagamento,
    op.promocional as cotaunica,
    pvd.vencimento,
    spvd.situacaoparcela as situacaoparcela,
    spvd.referencia,
    pacote_consulta_de_debitos.getnumeroparcela(vd.id, op.id, op.promocional, pvd.sequenciaparcela) as parcela,
    case
        when spvd.situacaoparcela <> 'EM_ABERTO' then pvd.valor
        else spvd.saldo
        end as valororiginal,
    d.descricao as divida,
    d.id as id_divida,
    coalesce(pvd.dividaativa,0) as dividaativa,
    coalesce(pvd.dividaativaajuizada,0) as dividaativaajuizada,
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
    c.tipocalculo,
    endereco.logradouro,
    endereco.bairro,
    endereco.numero,
    endereco.complemento,
    endereco.tipologradouro,
    endereco.cep,
    endereco.tipoendereco
from calculo c
         inner join calculopessoa cp on cp.calculo_id = c.id
         left join pessoajuridica pj on pj.id = cp.pessoa_id
         left join pessoafisica pf on pf.id = cp.pessoa_id
         left join cadastroeconomico ce on c.cadastro_id = ce.id
         left join cadastroimobiliario ci on c.cadastro_id = ci.id
         left join cadastrorural cr on c.cadastro_id = cr.id
         inner join valordivida vd on vd.calculo_id = c.id
         inner join exercicio e on e.id = vd.exercicio_id
         inner join divida d on d.id = vd.divida_id
         inner join dividaacrescimo dva on dva.divida_id = d.id
    and current_date between dva.iniciovigencia and coalesce(dva.finalvigencia, current_date)
         inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
         inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
         inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
         left join vwenderecopessoa endereco on cp.pessoa_id = endereco.pessoa_id
where spvd.situacaoparcela <> 'ISOLAMENTO'
  and coalesce(pvd.dividaativa, 0) = 0
