CREATE OR REPLACE VIEW VWHIERARQUIAORCAMENTARIA AS
with rec (id,superior_id, subordinada_id, entidade_id, codigo, descricao, exercicio_id, natureza, esferadopoder, iniciovigencia, fimvigencia, ano ) as (
        select 
               raiz.id,
               raiz.superior_id, 
               raiz.subordinada_id, 
               e.id,
               raiz.codigo,
               uo.descricao,
               raiz.exercicio_id,
               ent.natureza,
               uo.esferadopoder,
               coalesce(raiz.iniciovigencia, to_date('0101'||e.ano,'ddmmyyyy')) as iniciovigencia, 
               raiz.fimvigencia,
               e.ano
          from hierarquiaorganizacional raiz
    inner join exercicio e              on e.id = raiz.exercicio_id
    inner join unidadeorganizacional uo on uo.id = raiz.subordinada_id
     left join entidade ent             on ent.id = uo.entidade_id
         where raiz.tipohierarquiaorganizacional = 'ORCAMENTARIA'
           and raiz.superior_id is null
UNION ALL
        select 
               filho.id,
               filho.superior_id, 
               filho.subordinada_id, 
               e.id,
               filho.codigo,
               uo.descricao,
               filho.exercicio_id,
               case when (rec.natureza is null) then ent.natureza else rec.natureza end,
               case when (uo.esferadopoder is not null) then uo.esferadopoder else rec.esferadopoder end,            
               coalesce(filho.iniciovigencia, to_date('0101'||e.ano,'ddmmyyyy')) as iniciovigencia, 
               filho.fimvigencia,
               e.ano
          from hierarquiaorganizacional filho
    inner join rec                      on rec.subordinada_id = filho.superior_id 
                                       and filhoNaVigencia(filho.iniciovigencia, filho.fimvigencia, rec.iniciovigencia, rec.fimvigencia) = 1
    inner join exercicio e              on e.id = filho.exercicio_id
    inner join unidadeorganizacional uo on uo.id = filho.subordinada_id
     left join entidade ent             on ent.id = uo.entidade_id
         where filho.tipohierarquiaorganizacional = 'ORCAMENTARIA' 
) select * from rec
