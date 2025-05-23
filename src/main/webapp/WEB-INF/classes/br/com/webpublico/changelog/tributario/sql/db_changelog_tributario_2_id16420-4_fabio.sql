update calculoiptu u_iptu set ISENCAOCADASTROIMOBILIARIO_ID = (
select isencao.id
from
isencaocadastroimobiliario isencao
inner join processoisencaoiptu processoisencao on processoisencao.id = isencao.processoisencaoiptu_id
inner join categoriaisencaoiptu categoria on categoria.id = processoisencao.categoriaisencaoiptu_id
inner join cadastroimobiliario ci on ci.id = isencao.cadastroimobiliario_id
left join construcao on construcao.id = isencao.construcao_id
inner join calculoiptu iptu on iptu.CADASTROIMOBILIARIO_ID = ci.id
inner join calculo on calculo.id = iptu.id
                  and calculo.datacalculo between isencao.iniciovigencia and coalesce(isencao.finalvigencia, current_date)
inner join processocalculoiptu processoiptu on processoiptu.id = iptu.processocalculoiptu_id
inner join processocalculo on processocalculo.id = processoiptu.id and processocalculo.exercicio_id = processoisencao.exercicioprocesso_id
inner join exercicio on exercicio.id = processocalculo.exercicio_id
where iptu.ISENCAOCADASTROIMOBILIARIO_ID is null
 and iptu.id = u_iptu.id and rownum = 1)
 where u_iptu.id in (
 select iptu.id
from
isencaocadastroimobiliario isencao
inner join processoisencaoiptu processoisencao on processoisencao.id = isencao.processoisencaoiptu_id
inner join categoriaisencaoiptu categoria on categoria.id = processoisencao.categoriaisencaoiptu_id
inner join cadastroimobiliario ci on ci.id = isencao.cadastroimobiliario_id
left join construcao on construcao.id = isencao.construcao_id
inner join calculoiptu iptu on iptu.CADASTROIMOBILIARIO_ID = ci.id
inner join calculo on calculo.id = iptu.id
                  and calculo.datacalculo between isencao.iniciovigencia and coalesce(isencao.finalvigencia, current_date)
inner join processocalculoiptu processoiptu on processoiptu.id = iptu.processocalculoiptu_id
inner join processocalculo on processocalculo.id = processoiptu.id and processocalculo.exercicio_id = processoisencao.exercicioprocesso_id
inner join exercicio on exercicio.id = processocalculo.exercicio_id
where iptu.ISENCAOCADASTROIMOBILIARIO_ID is null
 and iptu.id = u_iptu.id
 and exists(select 1 from itemcalculoiptu item where item.CALCULOIPTU_ID = iptu.id and coalesce(item.ISENTO,0) = 1)
 )
