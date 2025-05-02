update operadoratransparquivo operaarq
set operaarq.exercicio_id =
        (
            select exec.id from operadoratransporte opera
            inner join exercicio exec on exec.ano = extract(year from opera.datacadastro)
            where opera.id = operaarq.operadoratectransporte_id
        )
where operaarq.exercicio_id is null
