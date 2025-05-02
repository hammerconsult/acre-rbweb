create or replace view vwconsultaprotesto as
select distinct ex.ano as exercicio, pp.codigo, pp.numeroprotocolo, pp.lancamento, pf_usuario.nome usuario,
                pp.situacao, al.numero as atolegal_numero, to_char(pp.motivo) as motivo, pp.tipocadastrotributario as tipo_cadastro,
                coalesce(pf_calculo.cpf, pj_calculo.cnpj) as cpfcnpj, coalesce(pf_calculo.nome, pj_calculo.razaosocial) as nomerazaosocial,
                coalesce(ci.inscricaocadastral, coalesce(cmc.inscricaocadastral, cr.numeroincra)) cadastro, endereco.logradouro,
                endereco.bairro, endereco.numero as endereco_numero, endereco.complemento, endereco.tipologradouro, endereco.cep, endereco.tipoendereco,
                (select max(case when cda2.numero is not null then cda2.numero || '/' end) from certidaodividaativa cda2
                 where cda2.id = icda.certidao_id
                   and cda2.situacaocertidaoda <> 'CANCELADA') ||
                (select max(excda.ano) from certidaodividaativa cda2
                                                inner join exercicio excda on excda.id = cda2.exercicio_id
                 where cda2.id = icda.certidao_id
                   and cda2.situacaocertidaoda <> 'CANCELADA') as numero_cda,
                item.parcela_id, cad.id as cadastro_id, pes_calculo.id as pessoa_id, ex_divida.ano as exercicio_divida,
                c.id as calculo_id, c.subdivida as sd, vd.id as valordivida_id, c.datacalculo, pvd.opcaopagamento_id,
                coalesce(op.promocional, 0) as cotaunica, pvd.vencimento, spvd.situacaoparcela, spvd.referencia,
                pacote_consulta_de_debitos.getnumeroparcela(vd.id, op.id, op.promocional, pvd.sequenciaparcela) parcela,
                (case when spvd.situacaoparcela <> 'EM_ABERTO' then pvd.valor else spvd.saldo end) as valororiginal,
                divida.descricao as divida, divida.id as divida_id, coalesce(pvd.dividaativa, 0) as dividaativa,
                coalesce(pvd.dividaativaajuizada, 0) as dividaativaajuizada, dva.acrescimo_id configuracaoacrescimo_id,
                c.tipocalculo
from processodeprotesto pp
         inner join exercicio ex on pp.exercicio_id = ex.id
         inner join itemprocessodeprotesto item on pp.id = item.processodeprotesto_id
         inner join parcelavalordivida pvd on item.parcela_id = pvd.id
         inner join opcaopagamento op on pvd.opcaopagamento_id = op.id
         inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id
         inner join valordivida vd on pvd.valordivida_id = vd.id
         inner join divida on vd.divida_id = divida.id
         inner join dividaacrescimo dva on dva.divida_id = divida.id and trunc(sysdate) between trunc(dva.iniciovigencia)
    and trunc(coalesce(dva.finalvigencia, sysdate))
         inner join exercicio ex_divida on vd.exercicio_id = ex_divida.id
         inner join calculo c on vd.calculo_id = c.id
         inner join calculopessoa cp on cp.calculo_id = c.id
         inner join pessoa pes_calculo on cp.pessoa_id = pes_calculo.id
         left join pessoafisica pf_calculo on pes_calculo.id = pf_calculo.id
         left join pessoajuridica pj_calculo on pes_calculo.id = pj_calculo.id
         inner join usuariosistema us on pp.usuarioincluiu_id = us.id
         inner join pessoafisica pf_usuario on us.pessoafisica_id = pf_usuario.id
         left join itemcertidaodividaativa icda on icda.iteminscricaodividaativa_id = c.id
         left join atolegal al on pp.atolegal_id = al.id
         left join pessoa pes on pp.pessoaprotesto_id = pes.id
         left join pessoafisica pf on pes.id = pf.id
         left join pessoajuridica pj on pes.id = pj.id
         left join cadastro cad on pp.cadastroprotesto_id = cad.id
         left join cadastroimobiliario ci on cad.id = ci.id
         left join cadastroeconomico cmc on cad.id = cmc.id
         left join cadastrorural cr on cad.id = cr.id
         left join vwenderecopessoa endereco on cp.pessoa_id = endereco.pessoa_id
